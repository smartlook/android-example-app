package org.schabi.newpipe.settings.smartlook.settings

import android.app.AlertDialog
import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.smartlook.consentsdk.data.ConsentFormData
import com.smartlook.consentsdk.data.ConsentFormItem
import com.smartlook.consentsdk.listeners.ConsentResultsListener
import com.smartlook.sdk.smartlook.Smartlook
import com.smartlook.sdk.smartlook.analytics.event.annotations.EventTrackingMode
import org.schabi.newpipe.App
import org.schabi.newpipe.R
import org.schabi.newpipe.databinding.FragmentSmartlookMainSettingsBinding
import org.schabi.newpipe.settings.smartlook.extensions.viewBinding
import org.schabi.newpipe.settings.smartlook.prefs.PropertyType
import org.schabi.newpipe.settings.smartlook.prefs.SmartlookSettingsRepository

class SmartlookSettingsFragment : Fragment(R.layout.fragment_smartlook_main_settings) {

    private val binding by viewBinding(FragmentSmartlookMainSettingsBinding::bind)

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

    override fun onResume() {
        super.onResume()
        binding.sessionPropertiesView.value = (SmartlookSettingsRepository.sessionProperties?.size
                ?: 0).toString()
        binding.globalPropertiesView.value = (SmartlookSettingsRepository.globalProperties?.size
                ?: 0).toString()
        binding.userIdentificationView.value = SmartlookSettingsRepository.userId ?: "None"
        binding.eventTrackingView.value = SmartlookSettingsRepository.eventTrackingMode.getString()
    }

    private fun FragmentSmartlookMainSettingsBinding.bindViews() {
        val actionBar = (activity as AppCompatActivity).supportActionBar
        actionBar?.title = "Smartlook Settings"

        renderingView.setOnClickListener {
            (activity as SmartlookNavigator).goTo(RenderingSettingsFragment())
        }

        denyListView.setOnClickListener {
            //(activity as SettingsNavigator).goTo(PropertiesList.newInstance())
        }
        userIdentificationView.setOnClickListener {
            (activity as SmartlookNavigator).goTo(UserPropertyFragment.newInstance(SmartlookSettingsRepository.userId))
        }
        sessionPropertiesView.setOnClickListener {
            (activity as SmartlookNavigator).goTo(PropertiesListFragment.newInstance(PropertyType.Session))
        }

        eventTrackingView.setOnClickListener {
            showEvenTrackingDialog()
        }
        globalPropertiesView.setOnClickListener {
            (activity as SmartlookNavigator).goTo(PropertiesListFragment.newInstance(PropertyType.Global))
        }

        consentView.setOnClickListener {
            App.consentSDK.showConsentFormDialog(requireActivity(), prepareConsentFormData(), object : ConsentResultsListener {
                override fun onConsentResults(consentResults: HashMap<String, Boolean>) {
                }
            })
        }
    }

    private fun showEvenTrackingDialog() {
        val modes = EventTrackingMode.values().map { it.getString() }.toTypedArray()

        AlertDialog.Builder(context)
                .setTitle("Event tracking mode")
                .setSingleChoiceItems(
                        modes,
                        SmartlookSettingsRepository.eventTrackingMode.ordinal
                ) { dialog, which ->
                    val selectedMode = EventTrackingMode.values()[which]
                    Smartlook.setEventTrackingMode(selectedMode)
                    binding.eventTrackingView.value = SmartlookSettingsRepository.eventTrackingMode.getString()
                    dialog.dismiss()
                }
                .create()
                .show()
    }

    private fun EventTrackingMode.getString(): String {
        return when (this) {
            EventTrackingMode.FULL_TRACKING -> "Full tracking"
            EventTrackingMode.IGNORE_USER_INTERACTION -> "Ignore user interaction"
            EventTrackingMode.IGNORE_NAVIGATION_INTERACTION -> "Ignore navigation interaction"
            EventTrackingMode.IGNORE_RAGE_CLICKS -> "Ignore rage clicks"
            EventTrackingMode.NO_TRACKING -> "No tracking"
        }
    }

    private fun prepareConsentFormData(): ConsentFormData {
        return ConsentFormData(
                titleText = getString(R.string.consent_form_title),
                descriptionText = getString(R.string.consent_form_description),
                confirmButtonText = getString(R.string.consent_form_confirm_button_text),
                consentFormItems = prepareConsentFormItems()
        )
    }

    private fun prepareConsentFormItems(): Array<ConsentFormItem> {
        return arrayOf(
                ConsentFormItem(
                        consentKey = CONSENT_1_KEY,
                        required = true,
                        description = getString(R.string.consent_1_description),
                        link = null
                ),
                ConsentFormItem(
                        consentKey = CONSENT_2_KEY,
                        required = false,
                        description = getString(R.string.consent_2_description),
                        link = getString(R.string.consent_2_link)
                )
        )
    }

    companion object {
        const val CONSENT_1_KEY = "consent_1_key"
        const val CONSENT_2_KEY = "consent_2_key"
    }
}
