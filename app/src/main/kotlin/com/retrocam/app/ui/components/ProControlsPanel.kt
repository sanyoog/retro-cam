package com.retrocam.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brightness6
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.Exposure
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.retrocam.app.domain.model.CameraCapabilities
import com.retrocam.app.domain.model.ManualSettings
import com.retrocam.app.ui.theme.GlassWhite
import kotlin.math.roundToInt

/**
 * Pro mode manual controls panel
 * Slide-up panel with ISO, shutter, WB, focus controls
 */
@Composable
fun ProControlsPanel(
    visible: Boolean,
    capabilities: CameraCapabilities?,
    currentSettings: ManualSettings,
    onSettingsChange: (ManualSettings) -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
        exit = fadeOut() + slideOutVertically(targetOffsetY = { it }),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .background(Color(0xCC000000)) // 80% black
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Handle bar
            Box(
                modifier = Modifier
                    .width(48.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color(0x40FFFFFF))
                    .align(Alignment.CenterHorizontally)
            )

            Text(
                text = "PRO CONTROLS",
                style = MaterialTheme.typography.titleMedium,
                color = GlassWhite,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // ISO Control
            capabilities?.isoRange?.let { range ->
                ManualControlSlider(
                    label = "ISO",
                    value = currentSettings.iso ?: range.first,
                    valueRange = range.first.toFloat()..range.last.toFloat(),
                    onValueChange = { onSettingsChange(currentSettings.copy(iso = it.roundToInt())) },
                    valueFormatter = { it.roundToInt().toString() },
                    icon = Icons.Default.Brightness6
                )
            }

            // Shutter Speed Control
            capabilities?.exposureTimeRange?.let { range ->
                val minMs = range.first / 1_000_000
                val maxMs = range.last / 1_000_000
                
                ManualControlSlider(
                    label = "SHUTTER",
                    value = (currentSettings.shutterSpeed?.div(1_000_000)?.toFloat() 
                        ?: minMs.toFloat()),
                    valueRange = minMs.toFloat()..maxMs.toFloat().coerceAtMost(1000f),
                    onValueChange = { 
                        onSettingsChange(currentSettings.copy(shutterSpeed = (it * 1_000_000).toLong()))
                    },
                    valueFormatter = { "1/${(1000 / it).roundToInt()}s" },
                    icon = Icons.Default.CameraAlt
                )
            }

            // White Balance Control
            if (capabilities?.isManualWhiteBalanceSupported == true) {
                ManualControlSlider(
                    label = "WHITE BALANCE",
                    value = currentSettings.whiteBalance?.toFloat() ?: 5500f,
                    valueRange = 2000f..10000f,
                    onValueChange = { 
                        onSettingsChange(currentSettings.copy(whiteBalance = it.roundToInt()))
                    },
                    valueFormatter = { "${it.roundToInt()}K" },
                    icon = Icons.Default.WbSunny
                )
            }

            // Focus Distance Control
            if (capabilities?.isManualFocusSupported == true) {
                ManualControlSlider(
                    label = "FOCUS",
                    value = currentSettings.focusDistance ?: 0f,
                    valueRange = 0f..(capabilities.focusDistanceRange?.endInclusive ?: 1f),
                    onValueChange = { 
                        onSettingsChange(currentSettings.copy(focusDistance = it))
                    },
                    valueFormatter = { 
                        when {
                            it < 0.1f -> "∞"
                            it < 1f -> "${(1f / it).roundToInt()}m"
                            else -> "${(1f / it * 100).roundToInt()}cm"
                        }
                    },
                    icon = Icons.Default.CenterFocusStrong
                )
            }

            // Exposure Compensation
            capabilities?.exposureCompensationRange?.let { range ->
                ManualControlSlider(
                    label = "EXPOSURE",
                    value = currentSettings.exposureCompensation?.toFloat() ?: 0f,
                    valueRange = range.first.toFloat()..range.last.toFloat(),
                    onValueChange = { 
                        onSettingsChange(currentSettings.copy(exposureCompensation = it.roundToInt()))
                    },
                    valueFormatter = { 
                        val ev = it.roundToInt()
                        if (ev > 0) "+$ev EV" else "$ev EV"
                    },
                    icon = Icons.Default.Exposure
                )
            }

            // Reset button
            Button(
                onClick = { onSettingsChange(ManualSettings()) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0x26FFFFFF),
                    contentColor = GlassWhite
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text("Reset to Auto")
            }
        }
    }
}

@Composable
private fun ManualControlSlider(
    label: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit,
    valueFormatter: (Float) -> String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = GlassWhite.copy(alpha = 0.7f),
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = GlassWhite.copy(alpha = 0.7f)
                )
            }
            Text(
                text = valueFormatter(value),
                style = MaterialTheme.typography.labelLarge,
                color = GlassWhite
            )
        }

        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            colors = SliderDefaults.colors(
                thumbColor = GlassWhite,
                activeTrackColor = GlassWhite,
                inactiveTrackColor = Color(0x40FFFFFF)
            )
        )
    }
}
