package com.nowshowing.tv.ui.player

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.nowshowing.tv.data.model.OmdbTitleDetail
import com.nowshowing.tv.data.repository.MediaRepository
import com.nowshowing.tv.domain.source.SourceRegistry
import com.nowshowing.tv.domain.source.VideoSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class PlayerUiState(
    val imdbId: String = "",
    val title: String = "",
    val poster: String = "",
    val year: String = "",
    val type: String = "movie",
    val season: Int = 1,
    val episode: Int = 1,
    val currentSource: VideoSource = SourceRegistry.SOURCES.first(),
    val streamUrl: String = "",
    val isDirectMedia: Boolean = false,
    val showControls: Boolean = true,
    val showBlankRecoveryHint: Boolean = false,
    val isPlaying: Boolean = true,
    val currentPositionMs: Long = 0L,
    val durationMs: Long = 0L
)

class PlayerViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = MediaRepository(application)
    private val _uiState = MutableStateFlow(PlayerUiState())
    val uiState: StateFlow<PlayerUiState> = _uiState.asStateFlow()

    fun initialize(imdbId: String, season: Int, episode: Int, type: String) {
        viewModelScope.launch {
            val prefSource = SourceRegistry.getPreferredSource(getApplication())
            val detail = repository.getDetails(imdbId)
            val titleName = detail?.title ?: "NowShowing"
            val posterUrl = detail?.poster ?: ""
            val yearVal = detail?.year ?: ""

            val url = SourceRegistry.buildVideoUrl(
                source = prefSource,
                imdbId = imdbId,
                season = season,
                episode = episode,
                isSeries = type == "series"
            ) ?: ""

            val isDirect = url.endsWith(".mp4") || url.endsWith(".m3u8") || url.endsWith(".mpd")

            _uiState.value = PlayerUiState(
                imdbId = imdbId,
                title = titleName,
                poster = posterUrl,
                year = yearVal,
                type = type,
                season = season,
                episode = episode,
                currentSource = prefSource,
                streamUrl = url,
                isDirectMedia = isDirect
            )

            // Register playback initiation in Continue Watching
            repository.updateContinueWatching(
                imdbId = imdbId,
                title = titleName,
                poster = posterUrl,
                year = yearVal,
                type = type,
                season = season,
                episode = episode,
                positionMs = 0L,
                durationMs = 0L
            )
        }
    }

    fun switchNextSource() {
        val next = SourceRegistry.getNextSource(_uiState.value.currentSource)
        setSource(next)
    }

    fun setSource(source: VideoSource) {
        SourceRegistry.setPreferredSource(getApplication(), source.name)
        val url = SourceRegistry.buildVideoUrl(
            source = source,
            imdbId = _uiState.value.imdbId,
            season = _uiState.value.season,
            episode = _uiState.value.episode,
            isSeries = _uiState.value.type == "series"
        ) ?: ""

        val isDirect = url.endsWith(".mp4") || url.endsWith(".m3u8") || url.endsWith(".mpd")

        _uiState.value = _uiState.value.copy(
            currentSource = source,
            streamUrl = url,
            isDirectMedia = isDirect,
            showBlankRecoveryHint = false
        )
    }

    fun updateProgress(positionMs: Long, durationMs: Long) {
        _uiState.value = _uiState.value.copy(
            currentPositionMs = positionMs,
            durationMs = durationMs
        )
        viewModelScope.launch {
            val s = _uiState.value
            repository.updateContinueWatching(
                imdbId = s.imdbId,
                title = s.title,
                poster = s.poster,
                year = s.year,
                type = s.type,
                season = s.season,
                episode = s.episode,
                positionMs = positionMs,
                durationMs = durationMs
            )
        }
    }

    fun triggerBlankRecovery() {
        _uiState.value = _uiState.value.copy(showBlankRecoveryHint = true)
    }

    fun dismissRecoveryHint() {
        _uiState.value = _uiState.value.copy(showBlankRecoveryHint = false)
    }
}
