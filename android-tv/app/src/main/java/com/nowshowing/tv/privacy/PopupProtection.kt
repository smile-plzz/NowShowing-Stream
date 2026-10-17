package com.nowshowing.tv.privacy

import android.content.Context
import android.os.Message
import android.view.View
import android.webkit.JsResult
import android.webkit.WebChromeClient
import android.webkit.WebView

class PopupProtection(
    context: Context,
    private val sourceName: String,
    private val onFullscreenChanged: ((View?, WebChromeClient.CustomViewCallback?) -> Unit)? = null
) : WebChromeClient() {

    private val preferences = ProtectionPreferences(context)
    private val statistics = BlockingStatistics(context)

    override fun onCreateWindow(
        view: WebView?,
        isDialog: Boolean,
        isUserGesture: Boolean,
        resultMsg: Message?
    ): Boolean {
        if (preferences.isPopupBlockingEnabled && !preferences.isSourceExempt(sourceName)) {
            // Unsolicited pop-up window or pop-under suppressed
            statistics.recordPopupBlocked()
            return false
        }
        return super.onCreateWindow(view, isDialog, isUserGesture, resultMsg)
    }

    override fun onJsAlert(
        view: WebView?,
        url: String?,
        message: String?,
        result: JsResult?
    ): Boolean {
        // Prevent rogue ad scripts from locking TV UI with JavaScript dialogs
        result?.cancel()
        return true
    }

    override fun onJsConfirm(
        view: WebView?,
        url: String?,
        message: String?,
        result: JsResult?
    ): Boolean {
        result?.cancel()
        return true
    }

    override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
        onFullscreenChanged?.invoke(view, callback)
    }

    override fun onHideCustomView() {
        onFullscreenChanged?.invoke(null, null)
    }
}
