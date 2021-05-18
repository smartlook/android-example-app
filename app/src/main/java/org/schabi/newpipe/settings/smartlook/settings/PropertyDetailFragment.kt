package org.schabi.newpipe.settings.smartlook.settings

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import org.schabi.newpipe.R
import org.schabi.newpipe.databinding.FragmentSmartlookPropertyDetailBinding
import org.schabi.newpipe.settings.smartlook.extensions.viewBinding
import org.schabi.newpipe.settings.smartlook.prefs.Property
import org.schabi.newpipe.settings.smartlook.prefs.PropertyType
import org.schabi.newpipe.settings.smartlook.prefs.SmartlookRecordingSession
import org.schabi.newpipe.settings.smartlook.prefs.SmartlookSettingsRepository

class PropertyDetailFragment : Fragment(R.layout.fragment_smartlook_property_detail) {

    private val binding by viewBinding(FragmentSmartlookPropertyDetailBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.action_delete).isVisible = property() != null
        super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_delete -> deleteProperty()
        }
        return false
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.bindViews()
    }

    private fun FragmentSmartlookPropertyDetailBinding.bindViews() {
        val actionBar = (activity as AppCompatActivity).supportActionBar
        actionBar?.title = "Property Definition"

        val item = property()
        textNameEditText.setText(item?.key)
        textValueEditText.setText(item?.value)

        saveButton.setOnClickListener {
            val type = type()
            val items = when (type) {
                PropertyType.Session -> SmartlookSettingsRepository.sessionProperties?.toMutableList()
                PropertyType.Global -> SmartlookSettingsRepository.globalProperties?.toMutableList()
            } ?: mutableListOf()

            item?.let { newItem ->
                items.remove(newItem)
                items.add(Property(textNameEditText.text.toString(), textValueEditText.text.toString()))
            } ?: run {
                items.add(Property(textNameEditText.text.toString(), textValueEditText.text.toString()))
            }

            when (type) {
                PropertyType.Session -> SmartlookSettingsRepository.sessionProperties = items
                PropertyType.Global -> {
                    SmartlookSettingsRepository.globalProperties = items
                    SmartlookRecordingSession.updateGlobalProperties()
                }
            }

            activity?.onBackPressed()
        }
    }

    private fun deleteProperty() {
        property()?.let {
            val type = type()
            val items = when (type) {
                PropertyType.Session -> SmartlookSettingsRepository.sessionProperties?.toMutableList()
                PropertyType.Global -> SmartlookSettingsRepository.globalProperties?.toMutableList()
            } ?: mutableListOf()

            items.remove(it)

            when (type) {
                PropertyType.Session -> {
                    SmartlookSettingsRepository.sessionProperties = items
                }
                PropertyType.Global -> {
                    SmartlookSettingsRepository.globalProperties = items
                    SmartlookRecordingSession.updateGlobalProperties()
                }
            }



            activity?.onBackPressed()
        }
    }

    private fun property() = arguments?.getSerializable(PROPERTY) as? Property
    private fun type() = arguments?.getSerializable(TYPE) as PropertyType

    companion object {
        private const val PROPERTY = "PROPERTY"
        private const val TYPE = "type"

        fun newInstance(item: Property?, type: PropertyType): PropertyDetailFragment {
            return PropertyDetailFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(PROPERTY, item)
                    putSerializable(TYPE, type)
                }
            }
        }
    }
}