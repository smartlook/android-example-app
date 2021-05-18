package org.schabi.newpipe.settings.smartlook.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import org.schabi.newpipe.MainActivity
import org.schabi.newpipe.R
import org.schabi.newpipe.databinding.ActivityOnboardingBinding
import org.schabi.newpipe.settings.smartlook.prefs.SmartlookSettingsRepository
import org.schabi.newpipe.settings.smartlook.settings.SmartlookNavigator
import org.schabi.newpipe.util.Localization
import org.schabi.newpipe.util.ThemeHelper

class SmartlookOnboardingActivity : AppCompatActivity(), SmartlookNavigator {
    override fun onCreate(savedInstanceBundle: Bundle?) {
        setTheme(ThemeHelper.getSettingsThemeStyle(this))
        Localization.assureCorrectAppLanguage(this)

        if (SmartlookSettingsRepository.apiKey != null && SmartlookSettingsRepository.isOnboardingFinished) {
            goHome()
        }

        super.onCreate(savedInstanceBundle)

        val binding: ActivityOnboardingBinding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val apiKey = intent.data?.getQueryParameter(DEEPLINK_API_KEY_QUERY)

        if (savedInstanceBundle == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_holder, SmartlookOnboardingFirstFragment.newInstance(apiKey))
                    .commit()
        }
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

    override fun goHome() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    companion object {
        private const val DEEPLINK_API_KEY_QUERY = "sdk"
    }
}