package org.schabi.newpipe.fragments.list.playlist;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.viewbinding.ViewBinding;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.schabi.newpipe.NewPipeDatabase;
import org.schabi.newpipe.R;
import org.schabi.newpipe.database.playlist.model.PlaylistRemoteEntity;
import org.schabi.newpipe.databinding.PlaylistControlBinding;
import org.schabi.newpipe.databinding.PlaylistHeaderBinding;
import org.schabi.newpipe.extractor.InfoItem;
import org.schabi.newpipe.extractor.ListExtractor;
import org.schabi.newpipe.extractor.NewPipe;
import org.schabi.newpipe.extractor.ServiceList;
import org.schabi.newpipe.extractor.exceptions.ExtractionException;
import org.schabi.newpipe.extractor.playlist.PlaylistInfo;
import org.schabi.newpipe.extractor.services.youtube.YoutubeParsingHelper;
import org.schabi.newpipe.extractor.stream.StreamInfoItem;
import org.schabi.newpipe.extractor.stream.StreamType;
import org.schabi.newpipe.fragments.list.BaseListInfoFragment;
import org.schabi.newpipe.info_list.InfoItemDialog;
import org.schabi.newpipe.local.playlist.RemotePlaylistManager;
import org.schabi.newpipe.player.helper.PlayerHolder;
import org.schabi.newpipe.player.playqueue.PlayQueue;
import org.schabi.newpipe.player.playqueue.PlaylistPlayQueue;
import org.schabi.newpipe.report.ErrorActivity;
import org.schabi.newpipe.report.UserAction;
import org.schabi.newpipe.util.ExtractorHelper;
import org.schabi.newpipe.util.ImageDisplayConstants;
import org.schabi.newpipe.util.KoreUtil;
import org.schabi.newpipe.util.Localization;
import org.schabi.newpipe.util.NavigationHelper;
import org.schabi.newpipe.util.ShareUtils;
import org.schabi.newpipe.util.StreamDialogEntry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;

import static org.schabi.newpipe.ktx.ViewUtils.animate;
import static org.schabi.newpipe.util.ThemeHelper.resolveResourceIdFromAttr;

public class PlaylistFragment extends BaseListInfoFragment<PlaylistInfo> {
    private CompositeDisposable disposables;
    private Subscription bookmarkReactor;
    private AtomicBoolean isBookmarkButtonReady;

    private RemotePlaylistManager remotePlaylistManager;
    private PlaylistRemoteEntity playlistEntity;
    /*//////////////////////////////////////////////////////////////////////////
    // Views
    //////////////////////////////////////////////////////////////////////////*/

    private PlaylistHeaderBinding headerBinding;
    private PlaylistControlBinding playlistControlBinding;

    private MenuItem playlistBookmarkButton;

    public static PlaylistFragment getInstance(final int serviceId, final String url,
                                               final String name) {
        final PlaylistFragment instance = new PlaylistFragment();
        instance.setInitialData(serviceId, url, name);
        return instance;
    }

    /*//////////////////////////////////////////////////////////////////////////
    // LifeCycle
    //////////////////////////////////////////////////////////////////////////*/

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        disposables = new CompositeDisposable();
        isBookmarkButtonReady = new AtomicBoolean(false);
        remotePlaylistManager = new RemotePlaylistManager(NewPipeDatabase
                .getInstance(requireContext()));
    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater,
                             @Nullable final ViewGroup container,
                             @Nullable final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_playlist, container, false);
    }

    /*//////////////////////////////////////////////////////////////////////////
    // Init
    //////////////////////////////////////////////////////////////////////////*/

    @Override
    protected ViewBinding getListHeader() {
        headerBinding = PlaylistHeaderBinding
                .inflate(activity.getLayoutInflater(), itemsList, false);
        playlistControlBinding = headerBinding.playlistControl;

        return headerBinding;
    }

    @Override
    protected void initViews(final View rootView, final Bundle savedInstanceState) {
        super.initViews(rootView, savedInstanceState);

        infoListAdapter.setUseMiniVariant(true);
    }

    private PlayQueue getPlayQueueStartingAt(final StreamInfoItem infoItem) {
        return getPlayQueue(Math.max(infoListAdapter.getItemsList().indexOf(infoItem), 0));
    }

    @Override
    protected void showStreamDialog(final StreamInfoItem item) {
        final Context context = getContext();
        final Activity activity = getActivity();
        if (context == null || context.getResources() == null || activity == null) {
            return;
        }

        final ArrayList<StreamDialogEntry> entries = new ArrayList<>();

        if (PlayerHolder.getType() != null) {
            entries.add(StreamDialogEntry.enqueue);
        }
        if (item.getStreamType() == StreamType.AUDIO_STREAM) {
            entries.addAll(Arrays.asList(
                    StreamDialogEntry.start_here_on_background,
                    StreamDialogEntry.append_playlist,
                    StreamDialogEntry.share
            ));
        } else  {
            entries.addAll(Arrays.asList(
                    StreamDialogEntry.start_here_on_background,
                    StreamDialogEntry.start_here_on_popup,
                    StreamDialogEntry.append_playlist,
                    StreamDialogEntry.share
            ));
        }
        if (KoreUtil.shouldShowPlayWithKodi(context, item.getServiceId())) {
            entries.add(StreamDialogEntry.play_with_kodi);
        }
        StreamDialogEntry.setEnabledEntries(entries);

        StreamDialogEntry.start_here_on_background.setCustomAction((fragment, infoItem) ->
                NavigationHelper.playOnBackgroundPlayer(context,
                        getPlayQueueStartingAt(infoItem), true));

        new InfoItemDialog(activity, item, StreamDialogEntry.getCommands(context),
                (dialog, which) -> StreamDialogEntry.clickOn(which, this, item)).show();
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        if (DEBUG) {
            Log.d(TAG, "onCreateOptionsMenu() called with: "
                    + "menu = [" + menu + "], inflater = [" + inflater + "]");
        }
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_playlist, menu);

        playlistBookmarkButton = menu.findItem(R.id.menu_item_bookmark);
        updateBookmarkButtons();
    }

    @Override
    public void onDestroyView() {
        headerBinding = null;
        playlistControlBinding = null;

        super.onDestroyView();
        if (isBookmarkButtonReady != null) {
            isBookmarkButtonReady.set(false);
        }

        if (disposables != null) {
            disposables.clear();
        }
        if (bookmarkReactor != null) {
            bookmarkReactor.cancel();
        }

        bookmarkReactor = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (disposables != null) {
            disposables.dispose();
        }

        disposables = null;
        remotePlaylistManager = null;
        playlistEntity = null;
        isBookmarkButtonReady = null;
    }

    /*//////////////////////////////////////////////////////////////////////////
    // Load and handle
    //////////////////////////////////////////////////////////////////////////*/

    @Override
    protected Single<ListExtractor.InfoItemsPage> loadMoreItemsLogic() {
        return ExtractorHelper.getMorePlaylistItems(serviceId, url, currentNextPage);
    }

    @Override
    protected Single<PlaylistInfo> loadResult(final boolean forceLoad) {
        return ExtractorHelper.getPlaylistInfo(serviceId, url, forceLoad);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                NavigationHelper.openSettings(requireContext());
                break;
            case R.id.menu_item_openInBrowser:
                ShareUtils.openUrlInBrowser(requireContext(), url);
                break;
            case R.id.menu_item_share:
                ShareUtils.shareText(requireContext(), name, url);
                break;
            case R.id.menu_item_bookmark:
                onBookmarkClicked();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }


    /*//////////////////////////////////////////////////////////////////////////
    // Contract
    //////////////////////////////////////////////////////////////////////////*/

    @Override
    public void showLoading() {
        super.showLoading();
        animate(headerBinding.getRoot(), false, 200);
        animate(itemsList, false, 100);

        IMAGE_LOADER.cancelDisplayTask(headerBinding.uploaderAvatarView);
        animate(headerBinding.uploaderLayout, false, 200);
    }

    @Override
    public void handleResult(@NonNull final PlaylistInfo result) {
        super.handleResult(result);

        animate(headerBinding.getRoot(), true, 100);
        animate(headerBinding.uploaderLayout, true, 300);
        headerBinding.uploaderLayout.setOnClickListener(null);
        // If we have an uploader put them into the UI
        if (!TextUtils.isEmpty(result.getUploaderName())) {
            headerBinding.uploaderName.setText(result.getUploaderName());
            if (!TextUtils.isEmpty(result.getUploaderUrl())) {
                headerBinding.uploaderLayout.setOnClickListener(v -> {
                    try {
                        NavigationHelper.openChannelFragment(getFM(), result.getServiceId(),
                                result.getUploaderUrl(), result.getUploaderName());
                    } catch (final Exception e) {
                        ErrorActivity.reportUiError((AppCompatActivity) getActivity(), e);
                    }
                });
            }
        } else { // Otherwise say we have no uploader
            headerBinding.uploaderName.setText(R.string.playlist_no_uploader);
        }

        playlistControlBinding.getRoot().setVisibility(View.VISIBLE);

        final String avatarUrl = result.getUploaderAvatarUrl();
        if (result.getServiceId() == ServiceList.YouTube.getServiceId()
                && (YoutubeParsingHelper.isYoutubeMixId(result.getId())
                || YoutubeParsingHelper.isYoutubeMusicMixId(result.getId()))) {
            // this is an auto-generated playlist (e.g. Youtube mix), so a radio is shown
            headerBinding.uploaderAvatarView.setDisableCircularTransformation(true);
            headerBinding.uploaderAvatarView.setBorderColor(
                    getResources().getColor(R.color.transparent_background_color));
            headerBinding.uploaderAvatarView.setImageDrawable(
                    AppCompatResources.getDrawable(requireContext(),
                    resolveResourceIdFromAttr(requireContext(), R.attr.ic_radio))
            );
        } else {
            IMAGE_LOADER.displayImage(avatarUrl, headerBinding.uploaderAvatarView,
                    ImageDisplayConstants.DISPLAY_AVATAR_OPTIONS);
        }

        headerBinding.playlistStreamCount.setText(Localization
                .localizeStreamCount(getContext(), result.getStreamCount()));

        if (!result.getErrors().isEmpty()) {
            showSnackBarError(result.getErrors(), UserAction.REQUESTED_PLAYLIST,
                    NewPipe.getNameOfService(result.getServiceId()), result.getUrl(), 0);
        }

        remotePlaylistManager.getPlaylist(result)
                .flatMap(lists -> getUpdateProcessor(lists, result), (lists, id) -> lists)
                .onBackpressureLatest()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getPlaylistBookmarkSubscriber());

        playlistControlBinding.playlistCtrlPlayAllButton.setOnClickListener(view ->
                NavigationHelper.playOnMainPlayer(activity, getPlayQueue()));
        playlistControlBinding.playlistCtrlPlayPopupButton.setOnClickListener(view ->
                NavigationHelper.playOnPopupPlayer(activity, getPlayQueue(), false));
        playlistControlBinding.playlistCtrlPlayBgButton.setOnClickListener(view ->
                NavigationHelper.playOnBackgroundPlayer(activity, getPlayQueue(), false));

        playlistControlBinding.playlistCtrlPlayPopupButton.setOnLongClickListener(view -> {
            NavigationHelper.enqueueOnPopupPlayer(activity, getPlayQueue(), true);
            return true;
        });

        playlistControlBinding.playlistCtrlPlayBgButton.setOnLongClickListener(view -> {
            NavigationHelper.enqueueOnBackgroundPlayer(activity, getPlayQueue(), true);
            return true;
        });
    }

    private PlayQueue getPlayQueue() {
        return getPlayQueue(0);
    }

    private PlayQueue getPlayQueue(final int index) {
        final List<StreamInfoItem> infoItems = new ArrayList<>();
        for (final InfoItem i : infoListAdapter.getItemsList()) {
            if (i instanceof StreamInfoItem) {
                infoItems.add((StreamInfoItem) i);
            }
        }
        return new PlaylistPlayQueue(
                currentInfo.getServiceId(),
                currentInfo.getUrl(),
                currentInfo.getNextPage(),
                infoItems,
                index
        );
    }

    @Override
    public void handleNextItems(final ListExtractor.InfoItemsPage result) {
        super.handleNextItems(result);

        if (!result.getErrors().isEmpty()) {
            showSnackBarError(result.getErrors(), UserAction.REQUESTED_PLAYLIST,
                    NewPipe.getNameOfService(serviceId), "Get next page of: " + url, 0);
        }
    }

    /*//////////////////////////////////////////////////////////////////////////
    // OnError
    //////////////////////////////////////////////////////////////////////////*/

    @Override
    protected boolean onError(final Throwable exception) {
        if (super.onError(exception)) {
            return true;
        }

        final int errorId = exception instanceof ExtractionException
                ? R.string.parsing_error : R.string.general_error;
        onUnrecoverableError(exception, UserAction.REQUESTED_PLAYLIST,
                NewPipe.getNameOfService(serviceId), url, errorId);
        return true;
    }

    /*//////////////////////////////////////////////////////////////////////////
    // Utils
    //////////////////////////////////////////////////////////////////////////*/

    private Flowable<Integer> getUpdateProcessor(
            @NonNull final List<PlaylistRemoteEntity> playlists,
            @NonNull final PlaylistInfo result) {
        final Flowable<Integer> noItemToUpdate = Flowable.just(/*noItemToUpdate=*/-1);
        if (playlists.isEmpty()) {
            return noItemToUpdate;
        }

        final PlaylistRemoteEntity playlistRemoteEntity = playlists.get(0);
        if (playlistRemoteEntity.isIdenticalTo(result)) {
            return noItemToUpdate;
        }

        return remotePlaylistManager.onUpdate(playlists.get(0).getUid(), result).toFlowable();
    }

    private Subscriber<List<PlaylistRemoteEntity>> getPlaylistBookmarkSubscriber() {
        return new Subscriber<List<PlaylistRemoteEntity>>() {
            @Override
            public void onSubscribe(final Subscription s) {
                if (bookmarkReactor != null) {
                    bookmarkReactor.cancel();
                }
                bookmarkReactor = s;
                bookmarkReactor.request(1);
            }

            @Override
            public void onNext(final List<PlaylistRemoteEntity> playlist) {
                playlistEntity = playlist.isEmpty() ? null : playlist.get(0);

                updateBookmarkButtons();
                isBookmarkButtonReady.set(true);

                if (bookmarkReactor != null) {
                    bookmarkReactor.request(1);
                }
            }

            @Override
            public void onError(final Throwable t) {
                PlaylistFragment.this.onError(t);
            }

            @Override
            public void onComplete() { }
        };
    }

    @Override
    public void setTitle(final String title) {
        super.setTitle(title);
        headerBinding.playlistTitleView.setText(title);
    }

    private void onBookmarkClicked() {
        if (isBookmarkButtonReady == null || !isBookmarkButtonReady.get()
                || remotePlaylistManager == null) {
            return;
        }

        final Disposable action;

        if (currentInfo != null && playlistEntity == null) {
            action = remotePlaylistManager.onBookmark(currentInfo)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(ignored -> { /* Do nothing */ }, this::onError);
        } else if (playlistEntity != null) {
            action = remotePlaylistManager.deletePlaylist(playlistEntity.getUid())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doFinally(() -> playlistEntity = null)
                    .subscribe(ignored -> { /* Do nothing */ }, this::onError);
        } else {
            action = Disposable.empty();
        }

        disposables.add(action);
    }

    private void updateBookmarkButtons() {
        if (playlistBookmarkButton == null || activity == null) {
            return;
        }

        final int iconAttr = playlistEntity == null
                ? R.attr.ic_playlist_add : R.attr.ic_playlist_check;

        final int titleRes = playlistEntity == null
                ? R.string.bookmark_playlist : R.string.unbookmark_playlist;

        playlistBookmarkButton.setIcon(resolveResourceIdFromAttr(activity, iconAttr));
        playlistBookmarkButton.setTitle(titleRes);
    }
}
