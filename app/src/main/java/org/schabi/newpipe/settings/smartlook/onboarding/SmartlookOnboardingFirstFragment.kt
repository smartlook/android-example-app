package org.schabi.newpipe.settings.smartlook.onboarding

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import org.schabi.newpipe.R
import org.schabi.newpipe.databinding.FragmentSmartlookOnboarding1Binding
import org.schabi.newpipe.settings.smartlook.extensions.viewBinding
import org.schabi.newpipe.settings.smartlook.prefs.SmartlookSettingsRepository
import org.schabi.newpipe.settings.smartlook.settings.SmartlookNavigator

class SmartlookOnboardingFirstFragment : Fragment(R.layout.fragment_smartlook_onboarding_1) {

    private val binding by viewBinding(FragmentSmartlookOnboarding1Binding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.bindViews()
    }

    private fun FragmentSmartlookOnboarding1Binding.bindViews() {
        saveButton.setOnClickListener {
            SmartlookSettingsRepository.apiKey = binding.textApiEditText.text.toString()
            (activity as? SmartlookNavigator)?.goTo(SmartlookOnboardingSecondFragment())
        }

        textApiEditText.setText(apiKey())
    }

    private fun apiKey() = arguments?.getString(API_KEY) ?: ""

    companion object {
        private const val API_KEY = "API_KEY"
        fun newInstance(apiKey: String?): SmartlookOnboardingFirstFragment {
            return SmartlookOnboardingFirstFragment().apply {
                arguments = Bundle().apply {
                    putString(API_KEY, apiKey)
                }
            }
        }
    }
}