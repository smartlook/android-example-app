package org.schabi.newpipe.settings;

import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.smartlook.sdk.smartlook.Smartlook;
import com.smartlook.sdk.smartlook.analytics.video.annotations.RenderingMode;

import org.schabi.newpipe.R;
import org.schabi.newpipe.databinding.SettingsLayoutBinding;
import org.schabi.newpipe.settings.smartlook.prefs.SmartlookRecordingSession;
import org.schabi.newpipe.util.DeviceUtils;
import org.schabi.newpipe.util.ThemeHelper;
import org.schabi.newpipe.views.FocusOverlayView;

import static org.schabi.newpipe.util.Localization.assureCorrectAppLanguage;

/*
 * Created by Christian Schabesberger on 31.08.15.
 *
 * Copyright (C) Christian Schabesberger 2015 <chris.schabesberger@mailbox.org>
 * SettingsActivity.java is part of NewPipe.
 *
 * NewPipe is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * NewPipe is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with NewPipe.  If not, see <http://www.gnu.org/licenses/>.
 */

public class SettingsActivity extends AppCompatActivity
        implements BasePreferenceFragment.OnPreferenceStartFragmentCallback {

    public static void initSettings(final Context context) {
        NewPipeSettings.initSettings(context);
    }

    @Override
    protected void onCreate(final Bundle savedInstanceBundle) {
        setTheme(ThemeHelper.getSettingsThemeStyle(this));
        assureCorrectAppLanguage(this);
        super.onCreate(savedInstanceBundle);
        Smartlook.setRenderingMode(RenderingMode.WIREFRAME);

        final SettingsLayoutBinding settingsLayoutBinding =
                SettingsLayoutBinding.inflate(getLayoutInflater());
        setContentView(settingsLayoutBinding.getRoot());

        setSupportActionBar(settingsLayoutBinding.toolbarLayout.toolbar);

        if (savedInstanceBundle == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_holder, new MainSettingsFragment())
                    .commit();
        }

        if (DeviceUtils.isTv(this)) {
            FocusOverlayView.setupFocusObserver(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SmartlookRecordingSession.INSTANCE.updateRenderingMode();
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(true);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        final int id = item.getItemId();
        if (id == android.R.id.home) {
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                finish();
            } else {
                getSupportFragmentManager().popBackStack();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPreferenceStartFragment(final PreferenceFragmentCompat caller,
                                             final Preference preference) {
        final Fragment fragment = Fragment
                .instantiate(this, preference.getFragment(), preference.getExtras());
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.animator.custom_fade_in, R.animator.custom_fade_out,
                        R.animator.custom_fade_in, R.animator.custom_fade_out)
                .replace(R.id.fragment_holder, fragment)
                .addToBackStack(null)
                .commit();
        return true;
    }
}
