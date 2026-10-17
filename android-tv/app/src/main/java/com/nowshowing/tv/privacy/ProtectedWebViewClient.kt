package com.nowshowing.tv.privacy

import android.content.Context
import android.net.Uri
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import java.io.ByteArrayInputStream

class ProtectedWebViewClient(
    context: Context,
    private val sourceName: String,
    private val onBlockedEvent: ((BlockType, String) -> Unit)? = null
) : WebViewClient() {

    private val preferences = ProtectionPreferences(context)
    private val statistics = BlockingStatistics(context)
    private val engine = FilterListManager.engine

    override fun shouldInterceptRequest(
        view: WebView?,
        request: WebResourceRequest?
    ): WebResourceResponse? {
        val url = request?.url?.toString() ?: return null

        // Check if this source has protection disabled by the user
        if (preferences.isSourceExempt(sourceName)) {
            return null
        }

        val pageUrl = view?.url
        val decision = engine.evaluate(
            url = url,
            pageUrl = pageUrl,
            isAdBlockEnabled = preferences.isContentBlockingEnabled,
            isTrackerBlockEnabled = preferences.isTrackerBlockingEnabled
        )

        if (decision.isBlocked) {
            when (decision.type) {
                BlockType.TRACKER -> statistics.recordTrackerBlocked()
                else -> statistics.recordAdBlocked()
            }
            onBlockedEvent?.invoke(decision.type ?: BlockType.AD, url)

            // Return empty response with 200 OK so the requesting page doesn't crash
            return WebResourceResponse(
                "text/plain",
                "UTF-8",
                200,
                "OK",
                mapOf("Access-Control-Allow-Origin" to "*"),
                ByteArrayInputStream(ByteArray(0))
            )
        }

        return super.shouldInterceptRequest(view, request)
    }

    override fun shouldOverrideUrlLoading(
        view: WebView?,
        request: WebResourceRequest?
    ): Boolean {
        val uri = request?.url ?: return false
        val scheme = (uri.scheme ?: "").lowercase()

        // 1. Strict Protocol Restriction: Disallow unauthorized app intents and scheme hijacking
        if (scheme != "http" && scheme != "https") {
            statistics.recordPopupBlocked()
            return true // Consume and block
        }

        // 2. Pop-up & Unsolicited Redirect Protection
        if (preferences.isPopupBlockingEnabled && !preferences.isSourceExempt(sourceName)) {
            val currentUri = view?.url?.let { Uri.parse(it) }
            val targetHost = uri.host ?: ""
            val currentHost = currentUri?.host ?: ""

            // If an unexpected external pop-up navigation is initiated away from player domain
            if (currentHost.isNotBlank() && targetHost.isNotBlank() && !targetHost.contains(currentHost) && !currentHost.contains(targetHost)) {
                // If it's a known ad or redirect tracker, block it
                val decision = engine.evaluate(uri.toString())
                if (decision.isBlocked) {
                    statistics.recordPopupBlocked()
                    return true
                }
            }
        }

        return false
    }
}
