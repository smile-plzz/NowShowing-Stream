package com.nowshowing.tv.domain.source

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SourceRegistryTest {

    @Test
    fun testAllSourcesPresent() {
        assertEquals("Must contain 11 streaming sources from web app", 11, SourceRegistry.SOURCES.size)
    }

    @Test
    fun testMovieUrlConstruction() {
        val vidsrc = SourceRegistry.SOURCES.first { it.name == "VidSrc.to" }
        val url = SourceRegistry.buildVideoUrl(vidsrc, "tt1375666", isSeries = false)
        assertNotNull(url)
        assertEquals("https://vidsrc.to/embed/movie/tt1375666", url)
    }

    @Test
    fun testSeriesUrlConstruction() {
        val vidsrc = SourceRegistry.SOURCES.first { it.name == "VidSrc.to" }
        val url = SourceRegistry.buildVideoUrl(vidsrc, "tt0903747", season = 2, episode = 5, isSeries = true)
        assertNotNull(url)
        assertEquals("https://vidsrc.to/embed/tv/tt0903747/2/5", url)
    }

    @Test
    fun testVidsrcMeQuirk() {
        val vidsrcMe = SourceRegistry.SOURCES.first { it.name == "VidSrc.me" }
        val movieUrl = SourceRegistry.buildVideoUrl(vidsrcMe, "tt1375666", isSeries = false)
        assertEquals("https://v2.vidsrc.me/embed/tt1375666/", movieUrl)

        val seriesUrl = SourceRegistry.buildVideoUrl(vidsrcMe, "tt0903747", season = 3, episode = 4, isSeries = true)
        assertEquals("https://v2.vidsrc.me/embed/tt0903747/3-4/", seriesUrl)
    }

    @Test
    fun testAutoEmbedQuirk() {
        val autoEmbed = SourceRegistry.SOURCES.first { it.name == "AutoEmbed" }
        val seriesUrl = SourceRegistry.buildVideoUrl(autoEmbed, "tt0903747", season = 1, episode = 1, isSeries = true)
        assertEquals("https://autoembed.co/tv/imdb/tt0903747/1/1", seriesUrl)
    }

    @Test
    fun testNextSourceRotation() {
        val first = SourceRegistry.SOURCES.first()
        val next = SourceRegistry.getNextSource(first)
        assertEquals(SourceRegistry.SOURCES[1].name, next.name)

        val last = SourceRegistry.SOURCES.last()
        val wrapped = SourceRegistry.getNextSource(last)
        assertEquals(first.name, wrapped.name)
    }
}
