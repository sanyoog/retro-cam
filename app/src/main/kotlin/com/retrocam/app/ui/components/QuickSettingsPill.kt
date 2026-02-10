package com.retrocam.app.ui.components

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.retrocam.app.ui.theme.GlassWhite

@Composable
fun QuickSettingsPill(
    photoQuality: Int,
    aspectRatio: Int,
    onPhotoQualityChange: (Int) -> Unit,
    onAspectRatioChange: (Int) -> Unit,
    onFullSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color.Black.copy(alpha = 0.7f))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Title
        Text(
            text = "Quick Settings",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = GlassWhite,
            fontSize = 16.sp
        )

        // Divider
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(GlassWhite.copy(alpha = 0.2f))
        )

        // Photo Quality
        QuickSettingItem(
            icon = Icons.Default.HdrStrong,
            label = "Quality",
            currentValue = when (photoQuality) {
                0 -> "Low"
                1 -> "Medium"
                else -> "High"
            },
            onClick = {
                onPhotoQualityChange((photoQuality + 1) % 3)
            }
        )

        // Aspect Ratio
        QuickSettingItem(
            icon = Icons.Default.AspectRatio,
            label = "Ratio",
            currentValue = when (aspectRatio) {
                0 -> "4:3"
                1 -> "16:9"
                2 -> "1:1"
                else -> "Full"
            },
            onClick = {
                onAspectRatioChange((aspectRatio + 1) % 4)
            }
        )

        // Divider
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(GlassWhite.copy(alpha = 0.2f))
        )

        // Full Settings Button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .clickable(onClick = onFullSettingsClick)
                .background(Color.White.copy(alpha = 0.1f))
                .padding(12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = null,
                tint = GlassWhite,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "All Settings",
                style = MaterialTheme.typography.bodyMedium,
                color = GlassWhite,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun QuickSettingItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    currentValue: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .background(Color.White.copy(alpha = 0.05f))
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = GlassWhite.copy(alpha = 0.8f),
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = GlassWhite
            )
        }

        Text(
            text = currentValue,
            style = MaterialTheme.typography.bodyMedium,
            color = GlassWhite,
            fontWeight = FontWeight.Bold
        )
    }
}
