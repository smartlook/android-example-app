package org.schabi.newpipe.settings.smartlook.onboarding

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import org.schabi.newpipe.R
import org.schabi.newpipe.databinding.FragmentSmartlookOnboarding2Binding
import org.schabi.newpipe.settings.smartlook.extensions.viewBinding
import org.schabi.newpipe.settings.smartlook.prefs.SmartlookRecordingSession
import org.schabi.newpipe.settings.smartlook.prefs.SmartlookSettingsRepository
import org.schabi.newpipe.settings.smartlook.settings.SmartlookNavigator

class SmartlookOnboardingSecondFragment : Fragment(R.layout.fragment_smartlook_onboarding_2) {

    private val binding by viewBinding(FragmentSmartlookOnboarding2Binding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.bindViews()
    }

    private fun FragmentSmartlookOnboarding2Binding.bindViews() {
        saveButton.setOnClickListener {
            SmartlookSettingsRepository.isOnboardingFinished = true
            SmartlookRecordingSession.start(requireActivity())
            SmartlookSettingsRepository.userId = textUserIdEditText.text.toString()
            SmartlookRecordingSession.updateUserId()
            (activity as? SmartlookNavigator)?.goHome()
        }
    }
}