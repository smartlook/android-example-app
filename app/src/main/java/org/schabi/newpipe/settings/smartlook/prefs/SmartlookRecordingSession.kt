package org.schabi.newpipe.settings.smartlook.prefs

import android.app.Activity
import android.os.Bundle
import com.smartlook.sdk.smartlook.Smartlook
import com.smartlook.sdk.smartlook.util.annotations.LogAspect

object SmartlookRecordingSession {

    fun start(activity: Activity?): Boolean {
        return SmartlookSettingsRepository.apiKey?.let {
            val builder = Smartlook.SetupOptionsBuilder(it)
                    .setActivity(activity)
                    .useAdaptiveFramerate(SmartlookSettingsRepository.renderingAdaptive)
                    .setFps(SmartlookSettingsRepository.renderingFPS)
                    .setRenderingMode(SmartlookSettingsRepository.renderingMode)
                    .setRenderingModeOption(SmartlookSettingsRepository.renderingModeOption)
                    .startNewSession()

            Smartlook.setupAndStartRecording(builder.build())
            Smartlook.debugLoggingAspects(listOf(LogAspect.REST))

            updateUserId()
            updateGlobalProperties()
            updateSessionProperties()

            true
        } ?: false
    }

    fun updateUserId() {
        SmartlookSettingsRepository.userId?.let { Smartlook.setUserIdentifier(it) }
    }

    fun updateRenderingMode() {
        Smartlook.setRenderingMode(SmartlookSettingsRepository.renderingMode)
    }

    fun updateGlobalProperties() {
        Smartlook.removeAllGlobalEventProperties()
        SmartlookSettingsRepository.globalProperties?.let { properties ->
            Smartlook.setUserProperties(Bundle().apply {
                properties.forEach { putString(it.key, it.value) }
            }, false)
        }
    }

    fun updateSessionProperties() {
        SmartlookSettingsRepository.sessionProperties?.let { properties ->
            Smartlook.setUserProperties(Bundle().apply {
                properties.forEach { putString(it.key, it.value) }
            }, false)
        }
    }
}