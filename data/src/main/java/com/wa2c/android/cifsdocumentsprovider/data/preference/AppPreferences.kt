package com.wa2c.android.cifsdocumentsprovider.data.preference

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.wa2c.android.cifsdocumentsprovider.common.values.HostSortType
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Preference repository
 */
@Singleton
internal class AppPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val preferences: SharedPreferences = getPreferences(context)

    /** Host sort type */
    var hostSortType: HostSortType
        get() {
            return HostSortType.findByValueOrDefault(preferences.getInt(PREFKEY_HOST_SORT_TYPE, -1))
        }
        set(value) {
            preferences.edit { putInt(PREFKEY_HOST_SORT_TYPE, value.intValue) }
        }


    /** UI Theme */
    var uiTheme: String?
        get() = preferences.getString(PREFKEY_UI_THEME, null)
        set(value) { preferences.edit { putString(PREFKEY_UI_THEME, value) } }

    /** Language */
    var language: String?
        get() = preferences.getString(PREFKEY_LANGUAGE, null)
        set(value) { preferences.edit { putString(PREFKEY_LANGUAGE, value) } }

    /** Use as local */
    var useAsLocal: Boolean
        get() = preferences.getBoolean(PREFKEY_USE_AS_LOCAL, false)
        set(value) { preferences.edit { putBoolean(PREFKEY_USE_AS_LOCAL, value) } }


    /**
     * Remove old keys
     */
    fun removeOldKeys() {
        // Delete obsoleted settings
        if (preferences.contains("prefkey_cifs_settings_temporal")) {
            preferences.edit { remove("prefkey_cifs_settings_temporal") }
        }
    }

    /**
     * Remove old settings
     */
    fun removeOldConnection(): List<Map<String, String?>> {
        if (preferences.getString("prefkey_cifs_settings", null) == null)
            return emptyList()

        return preferences.getString("prefkey_cifs_settings", null)?.let {
            preferences.edit { remove("prefkey_cifs_settings") }
            val format = Json { isLenient = true }
            format.decodeFromString<List<Map<String, String?>>>(it)
        } ?: emptyList()
    }

    companion object {
        private const val PREFKEY_HOST_SORT_TYPE = "prefkey_host_sort_type"
        private const val PREFKEY_UI_THEME = "prefkey_ui_theme"
        private const val PREFKEY_LANGUAGE = "prefkey_language"
        private const val PREFKEY_USE_AS_LOCAL = "prefkey_use_as_local"

        fun getPreferences(context: Context): SharedPreferences {
            return context.getSharedPreferences("App", Context.MODE_PRIVATE)
        }
    }

}
