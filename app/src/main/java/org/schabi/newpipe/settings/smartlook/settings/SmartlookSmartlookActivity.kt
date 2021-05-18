package org.schabi.newpipe.settings.smartlook.settings

import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import org.schabi.newpipe.R
import org.schabi.newpipe.databinding.SettingsLayoutBinding
import org.schabi.newpipe.util.Localization
import org.schabi.newpipe.util.ThemeHelper


class SmartlookSmartlookActivity : AppCompatActivity(), SmartlookNavigator {
    override fun onCreate(savedInstanceBundle: Bundle?) {
        setTheme(ThemeHelper.getSettingsThemeStyle(this))
        Localization.assureCorrectAppLanguage(this)

        super.onCreate(savedInstanceBundle)

        val settingsLayoutBinding: SettingsLayoutBinding = SettingsLayoutBinding.inflate(layoutInflater)
        setContentView(settingsLayoutBinding.root)
        setSupportActionBar(settingsLayoutBinding.toolbarLayout.toolbar)

        if (savedInstanceBundle == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_holder, SmartlookSettingsFragment())
                    .commit()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val actionBar = supportActionBar
        menuInflater.inflate(R.menu.smartlook_settings_menu, menu)
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setDisplayShowHomeEnabled(true)
            actionBar.setDisplayShowTitleEnabled(true)
        }
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun goTo(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
                .setCustomAnimations(R.animator.custom_fade_in, R.animator.custom_fade_out,
                        R.animator.custom_fade_in, R.animator.custom_fade_out)
                .replace(R.id.fragment_holder, fragment)
                .addToBackStack(null)
                .commit()
    }
}