package org.schabi.newpipe.settings.smartlook.payment

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import org.schabi.newpipe.R
import org.schabi.newpipe.databinding.ActivityOnboardingBinding
import org.schabi.newpipe.settings.smartlook.settings.SmartlookNavigator
import org.schabi.newpipe.util.Localization
import org.schabi.newpipe.util.ThemeHelper

class PaymentActivity : AppCompatActivity(), SmartlookNavigator {
    override fun onCreate(savedInstanceBundle: Bundle?) {
        setTheme(ThemeHelper.getSettingsThemeStyle(this))
        Localization.assureCorrectAppLanguage(this)
        super.onCreate(savedInstanceBundle)

        val binding: ActivityOnboardingBinding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceBundle == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_holder, PaymentLoginFragment.newInstance())
                    .commit()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun goTo(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
//                .setCustomAnimations(R.animator.custom_fade_in, R.animator.custom_fade_out,
//                        R.animator.custom_fade_in, R.animator.custom_fade_out)
                .replace(R.id.fragment_holder, fragment)
                .addToBackStack(null)
                .commit()
    }

    override fun goHome() {
        finish()
    }
}