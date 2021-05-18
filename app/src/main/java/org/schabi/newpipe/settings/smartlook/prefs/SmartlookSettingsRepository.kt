package org.schabi.newpipe.settings.smartlook.prefs

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.smartlook.sdk.smartlook.analytics.event.annotations.EventTrackingMode
import com.smartlook.sdk.smartlook.analytics.video.annotations.RenderingMode
import com.smartlook.sdk.smartlook.analytics.video.annotations.RenderingModeOption

object SmartlookSettingsRepository {

    private const val ONBOARDING_FINISHED_KEY = "ONBOARDING_FINISHED_KEY"
    private const val EVENT_TRACKING_MODE_KEY = "EVENT_TRACKING_MODE_KEY"
    private const val RENDERING_MODE_KEY = "RENDERING_MODE_KEY"
    private const val RENDERING_MODE_OPTIONS_KEY = "RENDERING_MODE_OPTIONS_KEY"
    private const val RENDERING_FRAMERATE_KEY = "RENDERING_FRAMERATE_KEY"
    private const val RENDERING_ADAPTIVE_FRAMERATE_KEY = "RENDERING_ADAPTIVE_FRAMERATE_KEY"
    private const val SESSION_PROPERTIES_KEY = "SESSION_PROPERTIES_KEY"
    private const val GLOBAL_PROPERTIES_KEY = "GLOBAL_PROPERTIES_KEY"
    private const val API_KEY = "API_KEY"
    private const val USER_ID_KEY = "USER_ID_KEY"
    private const val USER_LOGIN_KEY = "USER_LOGIN_KEY"

    private val gson = Gson()
    private lateinit var sharedPreferences: SharedPreferences

    fun init(context: Context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    }

    var userLogin: String?
        get() = sharedPreferences.getString(USER_LOGIN_KEY, null)
        set(value) = sharedPreferences.edit().putString(USER_LOGIN_KEY, value).apply()

    var userId: String?
        get() = sharedPreferences.getString(USER_ID_KEY, null)
        set(value) = sharedPreferences.edit().putString(USER_ID_KEY, value).apply()

    var isOnboardingFinished: Boolean
        get() = sharedPreferences.getBoolean(ONBOARDING_FINISHED_KEY, false)
        set(value) = sharedPreferences.edit().putBoolean(ONBOARDING_FINISHED_KEY, value).apply()

    var apiKey: String?
        get() = sharedPreferences.getString(API_KEY, null)
        set(value) = sharedPreferences.edit().putString(API_KEY, value).apply()

    var eventTrackingMode: EventTrackingMode
        get() = gson.fromJson(sharedPreferences.getString(EVENT_TRACKING_MODE_KEY, null), EventTrackingMode::class.java)
                ?: EventTrackingMode.FULL_TRACKING
        set(value) = sharedPreferences.edit().putString(EVENT_TRACKING_MODE_KEY, gson.toJson(value)).apply()

    var renderingMode: RenderingMode
        get() = gson.fromJson(sharedPreferences.getString(RENDERING_MODE_KEY, null), RenderingMode::class.java)
                ?: RenderingMode.NATIVE
        set(value) = sharedPreferences.edit().putString(RENDERING_MODE_KEY, gson.toJson(value)).apply()

    var renderingModeOption: RenderingModeOption?
        get() = gson.fromJson(sharedPreferences.getString(RENDERING_MODE_OPTIONS_KEY, null), RenderingModeOption::class.java)
        set(value) = sharedPreferences.edit().putString(RENDERING_MODE_OPTIONS_KEY, gson.toJson(value)).apply()

    var renderingFPS: Int
        get() = sharedPreferences.getInt(RENDERING_FRAMERATE_KEY, 2)
        set(value) = sharedPreferences.edit().putInt(RENDERING_FRAMERATE_KEY, value).apply()

    var renderingAdaptive: Boolean
        get() = sharedPreferences.getBoolean(RENDERING_ADAPTIVE_FRAMERATE_KEY, true)
        set(value) = sharedPreferences.edit().putBoolean(RENDERING_ADAPTIVE_FRAMERATE_KEY, value).apply()

    var sessionProperties: List<Property>?
        get() {
            val properties = sharedPreferences.getString(SESSION_PROPERTIES_KEY, null)
            return if (properties == null) {
                properties
            } else {
                gson.fromJson(properties, object : TypeToken<List<Property>>() {}.type)
            }
        }
        set(value) = sharedPreferences.edit().putString(SESSION_PROPERTIES_KEY, if (value == null) null else gson.toJson(value)).apply()

    var globalProperties: List<Property>?
        get() {
            val properties = sharedPreferences.getString(GLOBAL_PROPERTIES_KEY, null)
            return if (properties == null) {
                properties
            } else {
                gson.fromJson(properties, object : TypeToken<List<Property>>() {}.type)
            }
        }
        set(value) = sharedPreferences.edit().putString(GLOBAL_PROPERTIES_KEY, if (value == null) null else gson.toJson(value)).apply()
}