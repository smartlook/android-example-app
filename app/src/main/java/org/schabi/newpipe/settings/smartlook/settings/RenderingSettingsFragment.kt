package org.schabi.newpipe.settings.smartlook.settings

import android.app.AlertDialog
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.CompoundButton
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.smartlook.sdk.smartlook.Smartlook
import com.smartlook.sdk.smartlook.analytics.video.annotations.RenderingMode
import com.smartlook.sdk.smartlook.analytics.video.annotations.RenderingModeOption
import org.schabi.newpipe.R
import org.schabi.newpipe.databinding.FragmentSmartlookRenderingSettingsBinding
import org.schabi.newpipe.settings.smartlook.extensions.viewBinding
import org.schabi.newpipe.settings.smartlook.prefs.SmartlookSettingsRepository

class RenderingSettingsFragment : Fragment(R.layout.fragment_smartlook_rendering_settings) {

    private val binding by viewBinding(FragmentSmartlookRenderingSettingsBinding::bind)

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

    private fun FragmentSmartlookRenderingSettingsBinding.bindViews() {
        val actionBar = (activity as AppCompatActivity).supportActionBar
        actionBar?.title = "Rendering Settings"

        loadRenderingMode()
        loadRenderingOptions()
        loadFrameRate()
        loadAdaptiveFrameRate()

        modeView.setOnClickListener {
            showRenderingModeDialog()
        }
        optionView.setOnClickListener {
            showRenderingOptionsDialog()
        }

        framerateView.onProgressChangeListener = object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                framerateView.value = "${progress + 2} FPS"
                SmartlookSettingsRepository.renderingFPS = progress + 2
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit
            override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit
        }
        adaptiveFramerateView.onCheckChangedListener = CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            SmartlookSettingsRepository.renderingAdaptive = isChecked
        }
    }

    private fun loadRenderingMode() {
        binding.modeView.value = SmartlookSettingsRepository.renderingMode.getString()
    }

    private fun loadRenderingOptions() {
        binding.optionView.value = SmartlookSettingsRepository.renderingModeOption?.getString()
                ?: "None"
    }

    private fun loadFrameRate() {
        binding.framerateView.value = "${SmartlookSettingsRepository.renderingFPS} FPS"
        binding.framerateView.progress = SmartlookSettingsRepository.renderingFPS
    }

    private fun loadAdaptiveFrameRate() {
        binding.adaptiveFramerateView.isChecked = SmartlookSettingsRepository.renderingAdaptive
    }

    private fun showRenderingModeDialog() {
        val modes = RenderingMode.values().map { it.getString() }.toTypedArray()

        AlertDialog.Builder(context)
                .setTitle("Rendering mode")
                .setSingleChoiceItems(
                        modes,
                        SmartlookSettingsRepository.renderingMode.ordinal
                ) { dialog, which ->
                    val selectedMode = RenderingMode.values()[which]
                    Smartlook.setRenderingMode(selectedMode)
                    SmartlookSettingsRepository.renderingMode = selectedMode
                    loadRenderingMode()
                    dialog.dismiss()
                }
                .create()
                .show()
    }

    private fun showRenderingOptionsDialog() {
        val modes = RenderingModeOption.values().map { it.getString() }.toTypedArray()

        AlertDialog.Builder(context)
                .setTitle("Rendering mode option")
                .setSingleChoiceItems(
                        modes,
                        SmartlookSettingsRepository.renderingModeOption?.ordinal ?: -1
                ) { dialog, which ->
                    val selectedMode = RenderingModeOption.values()[which]
                    Smartlook.setRenderingMode(Smartlook.currentRenderingMode(), selectedMode)
                    SmartlookSettingsRepository.renderingModeOption = selectedMode
                    loadRenderingOptions()
                    dialog.dismiss()
                }
                .create()
                .show()
    }

    private fun RenderingMode.getString(): String {
        return when (this) {
            RenderingMode.NO_RENDERING -> "No rendering"
            RenderingMode.NATIVE -> "Native"
            RenderingMode.WIREFRAME -> "Wireframe"
        }
    }

    private fun RenderingModeOption.getString(): String {
        return when (this) {
            RenderingModeOption.BLUEPRINT -> "Blueprint"
            RenderingModeOption.WIREFRAME -> "Wireframe"
            RenderingModeOption.ICON_BLUEPRINT -> "Icon blueprint"
        }
    }
}
