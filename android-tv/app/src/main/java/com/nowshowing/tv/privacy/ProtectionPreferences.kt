package com.nowshowing.tv.privacy

import android.content.Context
import android.content.SharedPreferences

class ProtectionPreferences(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "nowshowing_protection_prefs"
        private const val KEY_CONTENT_BLOCKING = "pref_content_blocking"
        private const val KEY_TRACKER_BLOCKING = "pref_tracker_blocking"
        private const val KEY_POPUP_BLOCKING = "pref_popup_blocking"
        private const val KEY_COOKIE_RESTRICTIONS = "pref_cookie_restrictions"
        private const val KEY_EXEMPT_SOURCES = "pref_exempt_sources"
    }

    var isContentBlockingEnabled: Boolean
        get() = prefs.getBoolean(KEY_CONTENT_BLOCKING, true)
        set(value) = prefs.edit().putBoolean(KEY_CONTENT_BLOCKING, value).apply()

    var isTrackerBlockingEnabled: Boolean
        get() = prefs.getBoolean(KEY_TRACKER_BLOCKING, true)
        set(value) = prefs.edit().putBoolean(KEY_TRACKER_BLOCKING, value).apply()

    var isPopupBlockingEnabled: Boolean
        get() = prefs.getBoolean(KEY_POPUP_BLOCKING, true)
        set(value) = prefs.edit().putBoolean(KEY_POPUP_BLOCKING, value).apply()

    var isThirdPartyCookiesRestricted: Boolean
        get() = prefs.getBoolean(KEY_COOKIE_RESTRICTIONS, true)
        set(value) = prefs.edit().putBoolean(KEY_COOKIE_RESTRICTIONS, value).apply()

    fun isSourceExempt(sourceName: String): Boolean {
        val exempt = prefs.getStringSet(KEY_EXEMPT_SOURCES, emptySet()) ?: emptySet()
        return exempt.contains(sourceName)
    }

    fun setSourceExempt(sourceName: String, exempt: Boolean) {
        val current = (prefs.getStringSet(KEY_EXEMPT_SOURCES, emptySet()) ?: emptySet()).toMutableSet()
        if (exempt) {
            current.add(sourceName)
        } else {
            current.remove(sourceName)
        }
        prefs.edit().putStringSet(KEY_EXEMPT_SOURCES, current).apply()
    }

    fun getExemptSources(): Set<String> {
        return prefs.getStringSet(KEY_EXEMPT_SOURCES, emptySet()) ?: emptySet()
    }

    fun clearAllSourceExceptions() {
        prefs.edit().remove(KEY_EXEMPT_SOURCES).apply()
    }
}
