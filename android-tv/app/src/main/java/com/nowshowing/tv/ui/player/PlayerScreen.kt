package com.nowshowing.tv.ui.player

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.nowshowing.tv.domain.source.SourceRegistry
import com.nowshowing.tv.privacy.PopupProtection
import com.nowshowing.tv.privacy.ProtectedWebViewClient
import com.nowshowing.tv.ui.theme.AccentCyan
import com.nowshowing.tv.ui.theme.AccentGold
import com.nowshowing.tv.ui.theme.FocusBorder
import com.nowshowing.tv.ui.theme.FocusGlow
import com.nowshowing.tv.ui.theme.SurfaceDark
import com.nowshowing.tv.ui.theme.TextMuted
import com.nowshowing.tv.ui.theme.TextPrimary
import kotlinx.coroutines.delay

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun PlayerScreen(
    imdbId: String,
    season: Int,
    episode: Int,
    type: String,
    viewModel: PlayerViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    var showHud by remember { mutableStateOf(true) }

    BackHandler {
        onBack()
    }

    LaunchedEffect(imdbId, season, episode, type) {
        viewModel.initialize(imdbId, season, episode, type)
    }

    // Auto-hide HUD after 6 seconds
    LaunchedEffect(showHud) {
        if (showHud) {
            delay(6000)
            showHud = false
        }
    }

    // 10-second blank player detection timer for embed sources
    LaunchedEffect(uiState.streamUrl) {
        if (!uiState.isDirectMedia && uiState.streamUrl.isNotBlank()) {
            delay(10000)
            viewModel.triggerBlankRecovery()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .clickable { showHud = !showHud }
    ) {
        // Player Surface: Tier 1 (Media3) vs Tier 2 (TV-Optimized Protected WebView)
        if (uiState.isDirectMedia && uiState.streamUrl.isNotBlank()) {
            // Media3 ExoPlayer Engine (direct streams)
            val exoPlayer = remember(uiState.streamUrl) {
                ExoPlayer.Builder(context).build().apply {
                    setMediaItem(MediaItem.fromUri(uiState.streamUrl))
                    prepare()
                    playWhenReady = true
                }
            }

            DisposableEffect(exoPlayer) {
                onDispose {
                    viewModel.updateProgress(exoPlayer.currentPosition, exoPlayer.duration)
                    exoPlayer.release()
                }
            }

            AndroidView(
                factory = { ctx ->
                    PlayerView(ctx).apply {
                        player = exoPlayer
                        useController = true
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        } else if (uiState.streamUrl.isNotBlank()) {
            // TV-Optimized Protected WebView Engine with Built-in Privacy Shields
            AndroidView(
                factory = { ctx ->
                    WebView(ctx).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        settings.apply {
                            javaScriptEnabled = true
                            domStorageEnabled = true
                            mediaPlaybackRequiresUserGesture = false
                            loadsImagesAutomatically = true
                            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                            userAgentString = "Mozilla/5.0 (Linux; Android 14; BRAVIA 4K Build/URB1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36"
                        }

                        // Block third-party tracking cookies
                        CookieManager.getInstance().setAcceptThirdPartyCookies(this, false)

                        // Native Content, Tracker & Pop-up Protection
                        webViewClient = ProtectedWebViewClient(
                            context = ctx,
                            sourceName = uiState.currentSource.name,
                            onBlockedEvent = { type, url ->
                                viewModel.onItemBlocked(type, url)
                            }
                        )
                        webChromeClient = PopupProtection(
                            context = ctx,
                            sourceName = uiState.currentSource.name
                        )

                        loadUrl(uiState.streamUrl)
                    }
                },
                update = { webView ->
                    if (webView.url != uiState.streamUrl && uiState.streamUrl.isNotBlank()) {
                        webView.loadUrl(uiState.streamUrl)
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        // TV Overlay HUD
        AnimatedVisibility(
            visible = showHud,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.85f),
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.9f)
                            )
                        )
                    )
                    .padding(32.dp)
            ) {
                // Top Row: Title, Back button, Source info, Shields status
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        PlayerIconButton(
                            icon = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDesc = "Back",
                            onClick = onBack
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = uiState.title,
                                color = TextPrimary,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            val sub = if (uiState.type == "series") "Season ${uiState.season} Episode ${uiState.episode}" else uiState.year
                            Text(
                                text = sub,
                                color = TextMuted,
                                fontSize = 13.sp
                            )
                        }
                    }

                    // Action buttons: Shields Toggle, Switch Source, Open External
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        // Privacy Shields Toggle Pill
                        val shieldLabel = if (uiState.isProtectionExempt) {
                            "Shields: OFF"
                        } else if (uiState.sessionBlockedCount > 0) {
                            "Shields: ON (${uiState.sessionBlockedCount} blocked)"
                        } else {
                            "Shields: ON"
                        }

                        PlayerActionButton(
                            label = shieldLabel,
                            icon = Icons.Default.Security,
                            isAccent = !uiState.isProtectionExempt,
                            onClick = { viewModel.toggleSourceProtection() }
                        )

                        PlayerActionButton(
                            label = "Source: ${uiState.currentSource.name}",
                            icon = Icons.Default.SwapHoriz,
                            onClick = { viewModel.switchNextSource() }
                        )

                        PlayerActionButton(
                            label = "External Player",
                            icon = Icons.AutoMirrored.Filled.OpenInNew,
                            onClick = {
                                if (uiState.streamUrl.isNotBlank()) {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uiState.streamUrl)).apply {
                                        setDataAndType(Uri.parse(uiState.streamUrl), "video/*")
                                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    }
                                    try {
                                        context.startActivity(intent)
                                    } catch (_: Exception) {
                                        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(uiState.streamUrl)).apply {
                                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        }
                                        context.startActivity(browserIntent)
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }

        // 10-Second Blank Player Recovery Banner Overlay
        if (uiState.showBlankRecoveryHint) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 36.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceDark.copy(alpha = 0.96f))
                    .border(1.5.dp, AccentGold, RoundedCornerShape(12.dp))
                    .padding(horizontal = 24.dp, vertical = 14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "⚠️ Screen blank or video not loading?",
                        color = AccentGold,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )

                    if (!uiState.isProtectionExempt) {
                        PlayerActionButton(
                            label = "Disable Shields for this source",
                            icon = Icons.Default.Security,
                            onClick = { viewModel.toggleSourceProtection() }
                        )
                    }

                    PlayerActionButton(
                        label = "Switch to ${SourceRegistry.getNextSource(uiState.currentSource).name}",
                        icon = Icons.Default.SwapHoriz,
                        onClick = { viewModel.switchNextSource() }
                    )

                    PlayerActionButton(
                        label = "Dismiss",
                        icon = null,
                        onClick = { viewModel.dismissRecoveryHint() }
                    )
                }
            }
        }
    }
}

@Composable
private fun PlayerIconButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDesc: String,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    Box(
        modifier = Modifier
            .size(42.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isFocused) FocusBorder else SurfaceDark)
            .border(
                width = if (isFocused) 2.dp else 0.dp,
                color = if (isFocused) Color.White else Color.Transparent,
                shape = RoundedCornerShape(8.dp)
            )
            .focusable(interactionSource = interactionSource)
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDesc,
            tint = if (isFocused) Color.Black else TextPrimary,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun PlayerActionButton(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector?,
    isAccent: Boolean = false,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val bg = when {
        isFocused -> FocusBorder
        isAccent -> AccentCyan.copy(alpha = 0.2f)
        else -> SurfaceDark
    }

    val textColor = when {
        isFocused -> Color.Black
        isAccent -> AccentCyan
        else -> TextPrimary
    }

    Row(
        modifier = Modifier
            .shadow(
                elevation = if (isFocused) 12.dp else 2.dp,
                shape = RoundedCornerShape(8.dp),
                spotColor = if (isFocused) FocusGlow else Color.Transparent
            )
            .clip(RoundedCornerShape(8.dp))
            .background(bg)
            .border(
                width = if (isFocused) 2.dp else 1.dp,
                color = if (isFocused) Color.White else if (isAccent) AccentCyan.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.12f),
                shape = RoundedCornerShape(8.dp)
            )
            .focusable(interactionSource = interactionSource)
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = textColor,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
        }
        Text(
            text = label,
            color = textColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
