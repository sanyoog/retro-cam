package com.retrocam.app.presentation.camera.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.HdrStrong
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun QuickSettingsPill(
    visible: Boolean,
    selectedQuality: Int,
    selectedRatio: Int,
    onQualitySelected: (Int) -> Unit,
    onRatioSelected: (Int) -> Unit,
    onFullSettingsClick: () -> Unit,
    transparency: Float = 70f,
    modifier: Modifier = Modifier
) {
    val alpha = (100f - transparency) / 100f
    
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = spring()) + expandVertically(
            animationSpec = spring(),
            expandFrom = Alignment.Top
        ),
        exit = fadeOut(animationSpec = spring()) + shrinkVertically(
            animationSpec = spring(),
            shrinkTowards = Alignment.Top
        ),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Color.Black.copy(alpha = alpha))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Photo Quality Section
            QuickSettingItem(
                icon = {
                    Icon(
                        imageVector = Icons.Default.HdrStrong,
                        contentDescription = "Quality",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                },
                title = "Photo Quality",
                options = listOf("Low", "Medium", "High"),
                selectedIndex = selectedQuality,
                onSelected = onQualitySelected
            )

            // Aspect Ratio Section
            QuickSettingItem(
                icon = {
                    Icon(
                        imageVector = Icons.Default.AspectRatio,
                        contentDescription = "Ratio",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                },
                title = "Aspect Ratio",
                options = listOf("4:3", "16:9", "1:1", "Full"),
                selectedIndex = selectedRatio,
                onSelected = onRatioSelected
            )

            // Divider
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color.White.copy(alpha = 0.2f))
            )

            // Full Settings Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable(onClick = onFullSettingsClick)
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "All Settings",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "All Settings",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun QuickSettingItem(
    icon: @Composable () -> Unit,
    title: String,
    options: List<String>,
    selectedIndex: Int,
    onSelected: (Int) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            icon()
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 13.sp
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            options.forEachIndexed { index, option ->
                QuickSettingOption(
                    label = option,
                    selected = index == selectedIndex,
                    onClick = { onSelected(index) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun QuickSettingOption(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Text(
        text = label,
        style = MaterialTheme.typography.bodySmall,
        color = if (selected) Color.Black else Color.White,
        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
        fontSize = 12.sp,
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (selected) Color.White
                else Color.White.copy(alpha = 0.15f)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp, horizontal = 12.dp)
    )
}
