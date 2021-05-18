package org.schabi.newpipe.settings.smartlook.settings

import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import org.schabi.newpipe.R
import org.schabi.newpipe.databinding.FragmentSmartlookUserPropertyBinding
import org.schabi.newpipe.settings.smartlook.extensions.viewBinding
import org.schabi.newpipe.settings.smartlook.prefs.SmartlookRecordingSession
import org.schabi.newpipe.settings.smartlook.prefs.SmartlookSettingsRepository

class UserPropertyFragment : Fragment(R.layout.fragment_smartlook_user_property) {

    private val binding by viewBinding(FragmentSmartlookUserPropertyBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.action_delete).isVisible = false
        super.onPrepareOptionsMenu(menu)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.bindViews()
    }

    private fun FragmentSmartlookUserPropertyBinding.bindViews() {
        val actionBar = (activity as AppCompatActivity).supportActionBar
        actionBar?.title = "User Identification"
        textNameEditText.setText(property())

        saveButton.setOnClickListener {
            SmartlookSettingsRepository.userId = textNameEditText.text.toString()
            SmartlookRecordingSession.updateUserId()
            activity?.onBackPressed()
        }
    }

    private fun property() = arguments?.getString(PROPERTY)

    companion object {
        private const val PROPERTY = "PROPERTY"
        fun newInstance(item: String?): UserPropertyFragment {
            return UserPropertyFragment().apply {
                arguments = Bundle().apply {
                    putString(PROPERTY, item)
                }
            }
        }
    }
}