package com.retrocam.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brightness6
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.Exposure
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.retrocam.app.domain.model.CameraCapabilities
import com.retrocam.app.domain.model.ManualSettings
import com.retrocam.app.ui.theme.GlassWhite
import kotlin.math.roundToInt

enum class ProSetting {
    ISO, SHUTTER, WHITE_BALANCE, FOCUS, EXPOSURE
}

/**
 * Modern Pro mode controls with circular icon selector and linear slider
 * Appears as a pill above the shutter button
 */
@Composable
fun ProModePill(
    visible: Boolean,
    capabilities: CameraCapabilities?,
    currentSettings: ManualSettings,
    onSettingsChange: (ManualSettings) -> Unit,
    transparency: Float = 70f,
    modifier: Modifier = Modifier
) {
    var selectedSetting by remember { mutableStateOf<ProSetting?>(null) }
    
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = spring(stiffness = Spring.StiffnessMedium)) +
                scaleIn(
                    initialScale = 0.8f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                ),
        exit = fadeOut(animationSpec = spring()) + scaleOut(targetScale = 0.8f),
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Linear slider (appears above pill when setting is selected)
            AnimatedVisibility(
                visible = selectedSetting != null,
                enter = fadeIn(spring()) + expandVertically(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                ),
                exit = fadeOut(spring()) + shrinkVertically(spring())
            ) {
                selectedSetting?.let { setting ->
                    LinearSliderControl(
                        setting = setting,
                        capabilities = capabilities,
                        currentSettings = currentSettings,
                        onValueChange = onSettingsChange,
                        onDismiss = { selectedSetting = null }
                    )
                }
            }
            
            // Main pill with circular icons
            Box(
                modifier = Modifier
                    .height(68.dp)
                    .clip(RoundedCornerShape(34.dp))
                    .background(Color.Black.copy(alpha = (100f - transparency) / 100f))
                    .border(
                        width = 1.5.dp,
                        color = Color.White.copy(alpha = ((100f - transparency) / 100f) * 0.3f),
                        shape = RoundedCornerShape(34.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 10.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // ISO
                    capabilities?.isoRange?.let {
                        CircularProControl(
                            icon = Icons.Default.Brightness6,
                            label = "ISO",
                            value = (currentSettings.iso ?: it.first).toString(),
                            isSelected = selectedSetting == ProSetting.ISO,
                            onClick = { selectedSetting = if (selectedSetting == ProSetting.ISO) null else ProSetting.ISO }
                        )
                    }
                    
                    // Shutter Speed
                    capabilities?.exposureTimeRange?.let { range ->
                        val shutterValue = currentSettings.shutterSpeed?.let { speed ->
                            val speedInNs = speed.toDouble()
                            "1/${(1000000000.0 / speedInNs).toInt()}"
                        } ?: "AUTO"
                        
                        CircularProControl(
                            icon = Icons.Default.CameraAlt,
                            label = "S",
                            value = shutterValue,
                            isSelected = selectedSetting == ProSetting.SHUTTER,
                            onClick = { selectedSetting = if (selectedSetting == ProSetting.SHUTTER) null else ProSetting.SHUTTER }
                        )
                    }
                    
                    // White Balance
                    if (capabilities?.isManualWhiteBalanceSupported == true) {
                        val wbValue = currentSettings.whiteBalance?.let { "${it}K" } ?: "AUTO"
                        
                        CircularProControl(
                            icon = Icons.Default.WbSunny,
                            label = "WB",
                            value = wbValue,
                            isSelected = selectedSetting == ProSetting.WHITE_BALANCE,
                            onClick = { selectedSetting = if (selectedSetting == ProSetting.WHITE_BALANCE) null else ProSetting.WHITE_BALANCE }
                        )
                    }
                    
                    // Focus
                    if (capabilities?.isManualFocusSupported == true) {
                        val focusValue = currentSettings.focusDistance?.let {
                            when {
                                it < 0.1f -> "∞"
                                it < 1f -> "${(1f / it).roundToInt()}m"
                                else -> "${(1f / it * 100).roundToInt()}cm"
                            }
                        } ?: "AUTO"
                        
                        CircularProControl(
                            icon = Icons.Default.CenterFocusStrong,
                            label = "F",
                            value = focusValue,
                            isSelected = selectedSetting == ProSetting.FOCUS,
                            onClick = { selectedSetting = if (selectedSetting == ProSetting.FOCUS) null else ProSetting.FOCUS }
                        )
                    }
                    
                    // Exposure Compensation
                    capabilities?.exposureCompensationRange?.let {
                        val evValue = currentSettings.exposureCompensation?.let { ev ->
                            if (ev > 0) "+$ev" else ev.toString()
                        } ?: "0"
                        
                        CircularProControl(
                            icon = Icons.Default.Exposure,
                            label = "EV",
                            value = evValue,
                            isSelected = selectedSetting == ProSetting.EXPOSURE,
                            onClick = { selectedSetting = if (selectedSetting == ProSetting.EXPOSURE) null else ProSetting.EXPOSURE }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CircularProControl(
    icon: ImageVector,
    label: String,
    value: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp),
        modifier = modifier
            .width(48.dp)
            .clickable(onClick = onClick)
    ) {
        // Circular icon button
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(
                    if (isSelected) Color(0x4DFFFFFF) // 30% white when selected
                    else Color(0x1AFFFFFF) // 10% white when not selected
                )
                .border(
                    width = if (isSelected) 1.5.dp else 0.5.dp,
                    color = if (isSelected) GlassWhite else Color(0x33FFFFFF),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isSelected) GlassWhite else GlassWhite.copy(alpha = 0.7f),
                modifier = Modifier.size(20.dp)
            )
        }
        
        // Value text
        Text(
            text = value,
            style = MaterialTheme.typography.labelSmall,
            fontSize = 9.sp,
            color = if (isSelected) GlassWhite else GlassWhite.copy(alpha = 0.7f),
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            maxLines = 1
        )
    }
}

@Composable
private fun LinearSliderControl(
    setting: ProSetting,
    capabilities: CameraCapabilities?,
    currentSettings: ManualSettings,
    onValueChange: (ManualSettings) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val label: String
    val value: Float
    val range: ClosedFloatingPointRange<Float>
    val formatter: (Float) -> String
    val onChange: (Float) -> Unit
    
    when (setting) {
        ProSetting.ISO -> {
            val isoRange = capabilities?.isoRange ?: (100..3200)
            val currentIso = (currentSettings.iso ?: isoRange.first).toFloat()
            
            label = "ISO"
            value = currentIso
            range = isoRange.first.toFloat()..isoRange.last.toFloat()
            formatter = { v -> v.roundToInt().toString() }
            onChange = { v -> onValueChange(currentSettings.copy(iso = v.roundToInt())) }
        }
        ProSetting.SHUTTER -> {
            val expRange = capabilities?.exposureTimeRange ?: (1_000_000L..1_000_000_000L)
            val minMs = expRange.first / 1_000_000
            val maxMs = expRange.last / 1_000_000
            val currentShutter = (currentSettings.shutterSpeed?.div(1_000_000)?.toFloat() 
                ?: minMs.toFloat())
            
            label = "SHUTTER SPEED"
            value = currentShutter
            range = minMs.toFloat()..maxMs.toFloat().coerceAtMost(1000f)
            formatter = { v -> "1/${(1000.0 / v).toInt()}s" }
            onChange = { v -> onValueChange(currentSettings.copy(shutterSpeed = (v * 1_000_000).toLong())) }
        }
        ProSetting.WHITE_BALANCE -> {
            val currentWB = currentSettings.whiteBalance?.toFloat() ?: 5500f
            
            label = "WHITE BALANCE"
            value = currentWB
            range = 2000f..10000f
            formatter = { v -> "${v.roundToInt()}K" }
            onChange = { v -> onValueChange(currentSettings.copy(whiteBalance = v.roundToInt())) }
        }
        ProSetting.FOCUS -> {
            val focusRange = capabilities?.focusDistanceRange?.let { r -> r.start..r.endInclusive } ?: (0f..1f)
            val currentFocus = currentSettings.focusDistance ?: 0f
            
            label = "FOCUS"
            value = currentFocus
            range = focusRange
            formatter = { v -> 
                when {
                    v < 0.1f -> "∞ Infinity"
                    v < 1f -> "${(1f / v).roundToInt()}m"
                    else -> "${(1f / v * 100f).roundToInt()}cm"
                }
            }
            onChange = { v -> onValueChange(currentSettings.copy(focusDistance = v)) }
        }
        ProSetting.EXPOSURE -> {
            val evRange = capabilities?.exposureCompensationRange ?: (-6..6)
            val currentEV = currentSettings.exposureCompensation?.toFloat() ?: 0f
            
            label = "EXPOSURE COMPENSATION"
            value = currentEV
            range = evRange.first.toFloat()..evRange.last.toFloat()
            formatter = { v -> 
                val ev = v.roundToInt()
                if (ev > 0) "+$ev EV" else "$ev EV"
            }
            onChange = { v -> onValueChange(currentSettings.copy(exposureCompensation = v.roundToInt())) }
        }
    }
    
    Box(
        modifier = modifier
            .fillMaxWidth(0.85f)
            .height(60.dp)
            .clip(RoundedCornerShape(30.dp))
            .background(Color.Black.copy(alpha = 0.3f))
            .border(
                width = 1.5.dp,
                color = Color.White.copy(alpha = 0.2f),
                shape = RoundedCornerShape(30.dp)
            )
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 10.sp,
                    color = GlassWhite.copy(alpha = 0.7f),
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = formatter(value),
                    style = MaterialTheme.typography.labelMedium,
                    fontSize = 12.sp,
                    color = GlassWhite,
                    fontWeight = FontWeight.Bold
                )
            }
            
            // Linear slider (camera-style)
            LinearCameraSlider(
                value = value,
                valueRange = range,
                onValueChange = onChange
            )
        }
    }
}
