package com.nowshowing.tv.domain.source

import android.content.Context
import android.content.SharedPreferences

object SourceRegistry {

    private const val PREFS_NAME = "nowshowing_source_prefs"
    private const val KEY_PREFERRED_SOURCE = "preferred_source_name"

    val SOURCES = listOf(
        VideoSource(name = "VidSrc.to", url = "https://vidsrc.to/embed/movie/", tvUrl = "https://vidsrc.to/embed/tv/"),
        VideoSource(name = "AutoEmbed", url = "https://autoembed.co/movie/imdb/", tvUrl = "https://autoembed.co/tv/imdb/"),
        VideoSource(name = "2Embed", url = "https://www.2embed.cc/embed/", tvUrl = "https://www.2embed.cc/embedtv/"),
        VideoSource(name = "2Embed.stream", url = "https://www.2embed.stream/embed/movie/", tvUrl = "https://www.2embed.stream/embed/tv/"),
        VideoSource(name = "VidSrc.icu", url = "https://vidsrc.icu/embed/movie/", tvUrl = "https://vidsrc.icu/embed/tv/"),
        VideoSource(name = "VidSrc.xyz", url = "https://vidsrc.xyz/embed/movie/", tvUrl = "https://vidsrc.xyz/embed/tv/"),
        VideoSource(name = "VidSrc.me", url = "https://v2.vidsrc.me/embed/", tvUrl = "https://v2.vidsrc.me/embed/"),
        VideoSource(name = "MoviesAPI", url = "https://moviesapi.club/movie/", tvUrl = "https://moviesapi.club/tv/"),
        VideoSource(name = "MultiEmbed", url = "https://multiembed.mov/?video_id=", tvUrl = "https://multiembed.mov/?video_id="),
        VideoSource(name = "EmbedSu", url = "https://embed.su/embed/movie/", tvUrl = "https://embed.su/embed/tv/"),
        VideoSource(name = "VidSrc.mov", url = "https://vidsrc.mov/embed/movie/", tvUrl = "https://vidsrc.mov/embed/tv/")
    )

    fun buildVideoUrl(
        source: VideoSource,
        imdbId: String,
        season: Int = 1,
        episode: Int = 1,
        isSeries: Boolean = false
    ): String? {
        if (isSeries) {
            val base = source.tvUrl ?: return null
            return when {
                base.contains("vidsrc.me") -> "$base$imdbId/$season-$episode/"
                base.contains("multiembed.mov") -> "$base$imdbId&s=$season&e=$episode"
                base.contains("2embed.cc") -> "$base$imdbId&s=$season&e=$episode"
                base.contains("autoembed") -> "$base$imdbId/$season/$episode"
                base.contains("moviesapi") -> "$base$imdbId/$season/$episode"
                else -> "$base$imdbId/$season/$episode"
            }
        } else {
            val base = source.url ?: return null
            return when {
                base.contains("vidsrc.me") -> "$base$imdbId/"
                base.contains("multiembed.mov") -> "$base$imdbId"
                else -> "$base$imdbId"
            }
        }
    }

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun getPreferredSource(context: Context): VideoSource {
        val name = getPrefs(context).getString(KEY_PREFERRED_SOURCE, "VidSrc.to")
        return SOURCES.firstOrNull { it.name == name } ?: SOURCES.first()
    }

    fun setPreferredSource(context: Context, sourceName: String) {
        getPrefs(context).edit().putString(KEY_PREFERRED_SOURCE, sourceName).apply()
    }

    fun getNextSource(currentSource: VideoSource): VideoSource {
        val idx = SOURCES.indexOfFirst { it.name == currentSource.name }
        if (idx == -1 || idx >= SOURCES.lastIndex) {
            return SOURCES.first()
        }
        return SOURCES[idx + 1]
    }
}
