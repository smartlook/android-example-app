package org.schabi.newpipe.fragments.list.comments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.schabi.newpipe.R;
import org.schabi.newpipe.extractor.ListExtractor;
import org.schabi.newpipe.extractor.NewPipe;
import org.schabi.newpipe.extractor.comments.CommentsInfo;
import org.schabi.newpipe.fragments.list.BaseListInfoFragment;
import org.schabi.newpipe.ktx.ViewUtils;
import org.schabi.newpipe.report.UserAction;
import org.schabi.newpipe.util.ExtractorHelper;

import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

public class CommentsFragment extends BaseListInfoFragment<CommentsInfo> {
    private final CompositeDisposable disposables = new CompositeDisposable();

    public static CommentsFragment getInstance(final int serviceId, final  String url,
                                               final String name) {
        final CommentsFragment instance = new CommentsFragment();
        instance.setInitialData(serviceId, url, name);
        return instance;
    }

    /*//////////////////////////////////////////////////////////////////////////
    // LifeCycle
    //////////////////////////////////////////////////////////////////////////*/

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater,
                             @Nullable final ViewGroup container,
                             @Nullable final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_comments, container, false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disposables.clear();
    }

    /*//////////////////////////////////////////////////////////////////////////
    // Load and handle
    //////////////////////////////////////////////////////////////////////////*/

    @Override
    protected Single<ListExtractor.InfoItemsPage> loadMoreItemsLogic() {
        return ExtractorHelper.getMoreCommentItems(serviceId, currentInfo, currentNextPage);
    }

    @Override
    protected Single<CommentsInfo> loadResult(final boolean forceLoad) {
        return ExtractorHelper.getCommentsInfo(serviceId, url, forceLoad);
    }

    /*//////////////////////////////////////////////////////////////////////////
    // Contract
    //////////////////////////////////////////////////////////////////////////*/

    @Override
    public void showLoading() {
        super.showLoading();
    }

    @Override
    public void handleResult(@NonNull final CommentsInfo result) {
        super.handleResult(result);

        ViewUtils.slideUp(requireView(), 120, 150, 0.06f);

        if (!result.getErrors().isEmpty()) {
            showSnackBarError(result.getErrors(), UserAction.REQUESTED_COMMENTS,
                    NewPipe.getNameOfService(result.getServiceId()), result.getUrl(), 0);
        }

        disposables.clear();
    }

    @Override
    public void handleNextItems(final ListExtractor.InfoItemsPage result) {
        super.handleNextItems(result);

        if (!result.getErrors().isEmpty()) {
            showSnackBarError(result.getErrors(), UserAction.REQUESTED_COMMENTS,
                    NewPipe.getNameOfService(serviceId), "Get next page of: " + url,
                    R.string.general_error);
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

        hideLoading();
        showSnackBarError(exception, UserAction.REQUESTED_COMMENTS,
                NewPipe.getNameOfService(serviceId), url, R.string.error_unable_to_load_comments);
        return true;
    }

    /*//////////////////////////////////////////////////////////////////////////
    // Utils
    //////////////////////////////////////////////////////////////////////////*/

    @Override
    public void setTitle(final String title) { }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) { }

    @Override
    protected boolean isGridLayout() {
        return false;
    }
}
