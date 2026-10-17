package com.nowshowing.tv.privacy

import android.content.Context
import android.content.SharedPreferences
import java.util.concurrent.atomic.AtomicLong

class BlockingStatistics(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "nowshowing_blocking_stats"
        private const val KEY_ADS_BLOCKED = "stat_ads_blocked"
        private const val KEY_TRACKERS_BLOCKED = "stat_trackers_blocked"
        private const val KEY_POPUPS_BLOCKED = "stat_popups_blocked"
    }

    private val adsCount = AtomicLong(prefs.getLong(KEY_ADS_BLOCKED, 0L))
    private val trackersCount = AtomicLong(prefs.getLong(KEY_TRACKERS_BLOCKED, 0L))
    private val popupsCount = AtomicLong(prefs.getLong(KEY_POPUPS_BLOCKED, 0L))

    val totalAdsBlocked: Long
        get() = adsCount.get()

    val totalTrackersBlocked: Long
        get() = trackersCount.get()

    val totalPopupsBlocked: Long
        get() = popupsCount.get()

    val totalBlocked: Long
        get() = totalAdsBlocked + totalTrackersBlocked + totalPopupsBlocked

    fun recordAdBlocked() {
        val newVal = adsCount.incrementAndGet()
        prefs.edit().putLong(KEY_ADS_BLOCKED, newVal).apply()
    }

    fun recordTrackerBlocked() {
        val newVal = trackersCount.incrementAndGet()
        prefs.edit().putLong(KEY_TRACKERS_BLOCKED, newVal).apply()
    }

    fun recordPopupBlocked() {
        val newVal = popupsCount.incrementAndGet()
        prefs.edit().putLong(KEY_POPUPS_BLOCKED, newVal).apply()
    }

    fun clearStats() {
        adsCount.set(0L)
        trackersCount.set(0L)
        popupsCount.set(0L)
        prefs.edit().clear().apply()
    }
}
