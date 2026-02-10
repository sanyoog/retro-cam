package com.retrocam.app.ui.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.HdrStrong
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VideoSettings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.retrocam.app.presentation.settings.SettingsViewModel
import com.retrocam.app.ui.components.GlassButton
import com.retrocam.app.ui.components.GlassPanel
import com.retrocam.app.ui.theme.GlassBlack
import com.retrocam.app.ui.theme.GlassWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onAboutClick: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val soundEnabled by viewModel.soundEnabled.collectAsStateWithLifecycle()
    val hapticsEnabled by viewModel.hapticsEnabled.collectAsStateWithLifecycle()
    val photoQuality by viewModel.photoQuality.collectAsStateWithLifecycle()
    val gridEnabled by viewModel.gridEnabled.collectAsStateWithLifecycle()
    val aspectRatio by viewModel.aspectRatio.collectAsStateWithLifecycle()
    val timerDuration by viewModel.timerDuration.collectAsStateWithLifecycle()
    val videoQuality by viewModel.videoQuality.collectAsStateWithLifecycle()
    val uiTransparency by viewModel.uiTransparency.collectAsStateWithLifecycle()
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(GlassBlack)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                GlassButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = GlassWhite
                    )
                }
                
                Text(
                    text = "Settings",
                    style = MaterialTheme.typography.headlineMedium,
                    color = GlassWhite,
                    fontWeight = FontWeight.Bold
                )
                
                // Placeholder for symmetry
                Spacer(modifier = Modifier.size(48.dp))
            }
            
            // Settings content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Feedback Section
                SettingsSection(title = "Feedback") {
                    SettingsSwitchItem(
                        icon = Icons.Default.MusicNote,
                        title = "Camera Sounds",
                        description = "Play shutter sound when taking photos",
                        checked = soundEnabled,
                        onCheckedChange = { viewModel.toggleSound(it) }
                    )
                    
                    SettingsSwitchItem(
                        icon = Icons.Default.Vibration,
                        title = "Haptic Feedback",
                        description = "Vibrate on button presses and actions",
                        checked = hapticsEnabled,
                        onCheckedChange = { viewModel.toggleHaptics(it) }
                    )
                }
                
                // Photo Quality Section
                SettingsSection(title = "Photo Quality") {
                    SettingsQualityItem(
                        icon = Icons.Default.HdrStrong,
                        title = "Capture Quality",
                        description = "Higher quality = larger file size",
                        selectedQuality = photoQuality,
                        onQualitySelected = { viewModel.setPhotoQuality(it) }
                    )
                }
                
                // Camera Section
                SettingsSection(title = "Camera") {
                    SettingsSwitchItem(
                        icon = Icons.Default.GridOn,
                        title = "Grid Overlay",
                        description = "Show rule of thirds grid",
                        checked = gridEnabled,
                        onCheckedChange = { viewModel.toggleGrid(it) }
                    )
                    
                    SettingsPickerItem(
                        icon = Icons.Default.AspectRatio,
                        title = "Aspect Ratio",
                        description = getAspectRatioLabel(aspectRatio),
                        options = listOf("4:3", "16:9", "1:1", "Full"),
                        selectedIndex = aspectRatio,
                        onOptionSelected = { viewModel.setAspectRatio(it) }
                    )
                    
                    SettingsPickerItem(
                        icon = Icons.Default.Timer,
                        title = "Self Timer",
                        description = getTimerLabel(timerDuration),
                        options = listOf("Off", "3 sec", "5 sec", "10 sec"),
                        selectedIndex = when(timerDuration) {
                            0 -> 0
                            3 -> 1
                            5 -> 2
                            10 -> 3
                            else -> 0
                        },
                        onOptionSelected = { 
                            val duration = when(it) {
                                0 -> 0
                                1 -> 3
                                2 -> 5
                                3 -> 10
                                else -> 0
                            }
                            viewModel.setTimerDuration(duration)
                        }
                    )
                }
                
                // Video Section
                SettingsSection(title = "Video") {
                    SettingsPickerItem(
                        icon = Icons.Default.VideoSettings,
                        title = "Video Quality",
                        description = getVideoQualityLabel(videoQuality),
                        options = listOf("SD (480p)", "HD (720p)", "Full HD (1080p)", "4K (2160p)"),
                        selectedIndex = videoQuality,
                        onOptionSelected = { viewModel.setVideoQuality(it) }
                    )
                }
                
                // Appearance Section
                SettingsSection(title = "Appearance") {
                    SettingsSliderItem(
                        title = "UI Transparency",
                        description = "Adjust translucency of controls",
                        value = uiTransparency,
                        valueRange = 0f..100f,
                        onValueChange = { viewModel.setUITransparency(it.toInt()) },
                        valueLabel = { "${it.toInt()}%" }
                    )
                }
                
                // About Section
                SettingsSection(title = "About") {
                    SettingsActionItem(
                        icon = Icons.Default.Info,
                        title = "About RetroCam",
                        description = "Version 1.4.0 • Phase 5",
                        onClick = onAboutClick
                    )
                }
                
                // Developer Section
                SettingsSection(title = "Developer") {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Made by",
                            style = MaterialTheme.typography.bodySmall,
                            color = GlassWhite.copy(alpha = 0.6f)
                        )
                        Text(
                            text = "Sanyog",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = GlassWhite
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = GlassWhite.copy(alpha = 0.6f),
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        
        GlassPanel(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                content()
            }
        }
    }
}

@Composable
private fun SettingsSwitchItem(
    icon: ImageVector,
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onCheckedChange(!checked) }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = GlassWhite.copy(alpha = 0.7f),
                modifier = Modifier.size(24.dp)
            )
            
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = GlassWhite
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = GlassWhite.copy(alpha = 0.5f)
                )
            }
        }
        
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = GlassWhite,
                checkedTrackColor = GlassWhite.copy(alpha = 0.3f),
                uncheckedThumbColor = GlassWhite.copy(alpha = 0.5f),
                uncheckedTrackColor = GlassWhite.copy(alpha = 0.1f)
            )
        )
    }
}

@Composable
private fun SettingsQualityItem(
    icon: ImageVector,
    title: String,
    description: String,
    selectedQuality: Int,
    onQualitySelected: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { expanded = !expanded }
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = GlassWhite.copy(alpha = 0.7f),
                modifier = Modifier.size(24.dp)
            )
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = GlassWhite
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = GlassWhite.copy(alpha = 0.5f)
                )
            }
            
            Text(
                text = when (selectedQuality) {
                    0 -> "Low"
                    1 -> "Medium"
                    else -> "High"
                },
                style = MaterialTheme.typography.bodyMedium,
                color = GlassWhite.copy(alpha = 0.7f)
            )
        }
        
        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Column(
                modifier = Modifier.padding(top = 12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                listOf(
                    Triple(0, "Low", "Fast capture, smaller files"),
                    Triple(1, "Medium", "Balanced quality and size"),
                    Triple(2, "High", "Best quality, larger files")
                ).forEach { (quality, label, desc) ->
                    QualityOption(
                        label = label,
                        description = desc,
                        selected = selectedQuality == quality,
                        onClick = { 
                            onQualitySelected(quality)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun QualityOption(
    label: String,
    description: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .background(
                if (selected) GlassWhite.copy(alpha = 0.1f)
                else Color.Transparent
            )
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = GlassWhite
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = GlassWhite.copy(alpha = 0.5f)
            )
        }
        
        if (selected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Selected",
                tint = GlassWhite,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun SettingsActionItem(
    icon: ImageVector,
    title: String,
    description: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = GlassWhite.copy(alpha = 0.7f),
            modifier = Modifier.size(24.dp)
        )
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = GlassWhite
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = GlassWhite.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
private fun SettingsPickerItem(
    icon: ImageVector,
    title: String,
    description: String,
    options: List<String>,
    selectedIndex: Int,
    onOptionSelected: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { expanded = !expanded }
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = GlassWhite.copy(alpha = 0.7f),
                modifier = Modifier.size(24.dp)
            )
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = GlassWhite
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = GlassWhite.copy(alpha = 0.5f)
                )
            }
        }
        
        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Column(
                modifier = Modifier.padding(top = 12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                options.forEachIndexed { index, option ->
                    PickerOption(
                        label = option,
                        selected = selectedIndex == index,
                        onClick = {
                            onOptionSelected(index)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun PickerOption(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .background(
                if (selected) GlassWhite.copy(alpha = 0.1f)
                else Color.Transparent
            )
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = GlassWhite
        )
        
        if (selected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Selected",
                tint = GlassWhite,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun SettingsSliderItem(
    title: String,
    description: String,
    value: Int,
    valueRange: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit,
    valueLabel: (Float) -> String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = GlassWhite
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = GlassWhite.copy(alpha = 0.5f)
                )
            }
            
            Text(
                text = valueLabel(value.toFloat()),
                style = MaterialTheme.typography.bodyMedium,
                color = GlassWhite.copy(alpha = 0.7f)
            )
        }
        
        Slider(
            value = value.toFloat(),
            onValueChange = onValueChange,
            valueRange = valueRange,
            colors = SliderDefaults.colors(
                thumbColor = GlassWhite,
                activeTrackColor = GlassWhite.copy(alpha = 0.7f),
                inactiveTrackColor = GlassWhite.copy(alpha = 0.2f)
            )
        )
    }
}

private fun getAspectRatioLabel(ratio: Int): String {
    return when (ratio) {
        0 -> "4:3 Standard"
        1 -> "16:9 Widescreen"
        2 -> "1:1 Square"
        3 -> "Full Screen"
        else -> "4:3 Standard"
    }
}

private fun getTimerLabel(duration: Int): String {
    return when (duration) {
        0 -> "Timer Off"
        3 -> "3 seconds"
        5 -> "5 seconds"
        10 -> "10 seconds"
        else -> "Timer Off"
    }
}

private fun getVideoQualityLabel(quality: Int): String {
    return when (quality) {
        0 -> "SD Quality (480p)"
        1 -> "HD Quality (720p)"
        2 -> "Full HD (1080p)"
        3 -> "4K (2160p)"
        else -> "HD Quality (720p)"
    }
}
