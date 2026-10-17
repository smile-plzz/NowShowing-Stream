package com.nowshowing.tv.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.nowshowing.tv.privacy.BlockingStatistics
import com.nowshowing.tv.privacy.FilterListManager
import com.nowshowing.tv.privacy.ProtectionPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SettingsUiState(
    val isContentBlockingEnabled: Boolean = true,
    val isTrackerBlockingEnabled: Boolean = true,
    val isPopupBlockingEnabled: Boolean = true,
    val isCookieRestrictionEnabled: Boolean = true,
    val totalAdsBlocked: Long = 0L,
    val totalTrackersBlocked: Long = 0L,
    val totalPopupsBlocked: Long = 0L,
    val exemptSources: Set<String> = emptySet(),
    val updateStatusMessage: String? = null,
    val isUpdatingFilters: Boolean = false
)

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val preferences = ProtectionPreferences(application)
    private val statistics = BlockingStatistics(application)

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        refreshState()
    }

    fun refreshState() {
        _uiState.value = SettingsUiState(
            isContentBlockingEnabled = preferences.isContentBlockingEnabled,
            isTrackerBlockingEnabled = preferences.isTrackerBlockingEnabled,
            isPopupBlockingEnabled = preferences.isPopupBlockingEnabled,
            isCookieRestrictionEnabled = preferences.isThirdPartyCookiesRestricted,
            totalAdsBlocked = statistics.totalAdsBlocked,
            totalTrackersBlocked = statistics.totalTrackersBlocked,
            totalPopupsBlocked = statistics.totalPopupsBlocked,
            exemptSources = preferences.getExemptSources()
        )
    }

    fun toggleContentBlocking() {
        val newVal = !preferences.isContentBlockingEnabled
        preferences.isContentBlockingEnabled = newVal
        refreshState()
    }

    fun toggleTrackerBlocking() {
        val newVal = !preferences.isTrackerBlockingEnabled
        preferences.isTrackerBlockingEnabled = newVal
        refreshState()
    }

    fun togglePopupBlocking() {
        val newVal = !preferences.isPopupBlockingEnabled
        preferences.isPopupBlockingEnabled = newVal
        refreshState()
    }

    fun toggleCookieRestrictions() {
        val newVal = !preferences.isThirdPartyCookiesRestricted
        preferences.isThirdPartyCookiesRestricted = newVal
        refreshState()
    }

    fun resetStatistics() {
        statistics.clearStats()
        refreshState()
    }

    fun clearSourceExceptions() {
        preferences.clearAllSourceExceptions()
        refreshState()
    }

    fun removeSourceExemption(sourceName: String) {
        preferences.setSourceExempt(sourceName, false)
        refreshState()
    }

    fun updateFiltersOnline() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isUpdatingFilters = true,
                updateStatusMessage = "Checking online for latest EasyList and EasyPrivacy updates…"
            )
            val success = FilterListManager.updateRulesOnline(getApplication())
            _uiState.value = _uiState.value.copy(
                isUpdatingFilters = false,
                updateStatusMessage = if (success) "Filter lists successfully updated to latest version." else "Filter lists are already up to date (using bundled ruleset)."
            )
        }
    }
}
