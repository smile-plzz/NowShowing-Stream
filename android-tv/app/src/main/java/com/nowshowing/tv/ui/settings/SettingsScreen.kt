package com.nowshowing.tv.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nowshowing.tv.ui.common.TvTopBar
import com.nowshowing.tv.ui.theme.AccentCyan
import com.nowshowing.tv.ui.theme.AccentGold
import com.nowshowing.tv.ui.theme.BgDark
import com.nowshowing.tv.ui.theme.FocusBorder
import com.nowshowing.tv.ui.theme.SurfaceDark
import com.nowshowing.tv.ui.theme.SurfaceVariant
import com.nowshowing.tv.ui.theme.TextMuted
import com.nowshowing.tv.ui.theme.TextPrimary

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onNavigate: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
    ) {
        TvTopBar(currentRoute = "settings", onNavigate = onNavigate)

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 48.dp, vertical = 16.dp)
        ) {
            item(key = "header") {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = AccentCyan,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Privacy & Content Blocking",
                            color = TextPrimary,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Native Brave-inspired protection for TV streaming and embedded content",
                            color = TextMuted,
                            fontSize = 13.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }

            // Statistics Overview Card
            item(key = "stats_card") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(SurfaceDark)
                        .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StatBox(label = "Ads Blocked", count = uiState.totalAdsBlocked, color = AccentCyan)
                    StatBox(label = "Trackers Blocked", count = uiState.totalTrackersBlocked, color = AccentGold)
                    StatBox(label = "Pop-ups Suppressed", count = uiState.totalPopupsBlocked, color = Color(0xFFFF5252))
                }
                Spacer(modifier = Modifier.height(20.dp))
            }

            // Core Controls Section
            item(key = "controls_section") {
                Text(
                    text = "Protection Controls",
                    color = TextPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(10.dp))

                SettingToggleItem(
                    title = "Content & Advertising Blocking",
                    description = "Intercepts video ad injectors, banners, and network-level advertising requests",
                    isEnabled = uiState.isContentBlockingEnabled,
                    onToggle = { viewModel.toggleContentBlocking() }
                )

                SettingToggleItem(
                    title = "Tracker & Telemetry Protection",
                    description = "Blocks third-party analytics, fingerprinting, and behavioral tracking beacons",
                    isEnabled = uiState.isTrackerBlockingEnabled,
                    onToggle = { viewModel.toggleTrackerBlocking() }
                )

                SettingToggleItem(
                    title = "Pop-up & Malicious Redirect Blocking",
                    description = "Prevents unexpected window creation, tab hijacking, and rogue app intents",
                    isEnabled = uiState.isPopupBlockingEnabled,
                    onToggle = { viewModel.togglePopupBlocking() }
                )

                SettingToggleItem(
                    title = "Third-Party Cookie Restrictions",
                    description = "Restricts cross-site tracking cookies in embedded web player environments",
                    isEnabled = uiState.isCookieRestrictionEnabled,
                    onToggle = { viewModel.toggleCookieRestrictions() }
                )

                Spacer(modifier = Modifier.height(20.dp))
            }

            // Maintenance & Update Actions
            item(key = "maintenance_section") {
                Text(
                    text = "Filter Lists & Maintenance",
                    color = TextPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    TvSettingsButton(
                        label = if (uiState.isUpdatingFilters) "Updating…" else "Update Filter Lists Online",
                        icon = Icons.Default.Refresh,
                        isLoading = uiState.isUpdatingFilters,
                        onClick = { viewModel.updateFiltersOnline() }
                    )

                    TvSettingsButton(
                        label = "Reset Blocking Statistics",
                        icon = Icons.Default.Close,
                        onClick = { viewModel.resetStatistics() }
                    )
                }

                if (!uiState.updateStatusMessage.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = uiState.updateStatusMessage ?: "",
                        color = AccentCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
            }

            // Source Exceptions
            if (uiState.exemptSources.isNotEmpty()) {
                item(key = "exemptions_section") {
                    Text(
                        text = "Exempt Video Sources (Protection Disabled)",
                        color = AccentGold,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    uiState.exemptSources.forEach { sourceName ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(SurfaceDark)
                                .padding(horizontal = 16.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = sourceName, color = TextPrimary, fontSize = 14.sp)
                            TvSettingsButton(
                                label = "Restore Protection",
                                icon = Icons.Default.Check,
                                onClick = { viewModel.removeSourceExemption(sourceName) }
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun StatBox(label: String, count: Long, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = count.toString(),
            color = color,
            fontSize = 26.sp,
            fontWeight = FontWeight.Black
        )
        Text(
            text = label,
            color = TextMuted,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun SettingToggleItem(
    title: String,
    description: String,
    isEnabled: Boolean,
    onToggle: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(if (isFocused) FocusBorder else SurfaceDark)
            .border(
                width = if (isFocused) 2.dp else 1.dp,
                color = if (isFocused) Color.White else Color.White.copy(alpha = 0.05f),
                shape = RoundedCornerShape(10.dp)
            )
            .focusable(interactionSource = interactionSource)
            .clickable(interactionSource = interactionSource, indication = null, onClick = onToggle)
            .padding(horizontal = 20.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = if (isFocused) Color.Black else TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = description,
                color = if (isFocused) Color.Black.copy(alpha = 0.7f) else TextMuted,
                fontSize = 12.sp
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Switch(
            checked = isEnabled,
            onCheckedChange = { onToggle() },
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.Black,
                checkedTrackColor = if (isFocused) Color.Black else AccentCyan,
                uncheckedThumbColor = TextMuted,
                uncheckedTrackColor = SurfaceVariant
            )
        )
    }
    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
private fun TvSettingsButton(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isLoading: Boolean = false,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isFocused) FocusBorder else SurfaceVariant)
            .border(
                width = if (isFocused) 2.dp else 1.dp,
                color = if (isFocused) Color.White else Color.White.copy(alpha = 0.1f),
                shape = RoundedCornerShape(8.dp)
            )
            .focusable(interactionSource = interactionSource)
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color = if (isFocused) Color.Black else AccentCyan,
                modifier = Modifier.size(16.dp),
                strokeWidth = 2.dp
            )
            Spacer(modifier = Modifier.width(8.dp))
        } else {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isFocused) Color.Black else TextPrimary,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
            text = label,
            color = if (isFocused) Color.Black else TextPrimary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
