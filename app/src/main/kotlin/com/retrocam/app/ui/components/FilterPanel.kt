package com.retrocam.app.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx. compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.retrocam.app.domain.model.FilterConfig
import com.retrocam.app.domain.model.FilterType
import com.retrocam.app.ui.theme.GlassWhite
import kotlin.math.roundToInt

/**
 * Filter selection panel with intensity slider
 */
@Composable
fun FilterPanel(
    currentFilter: FilterConfig,
    onFilterChange: (FilterConfig) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            .background(Color(0xCC000000))
            .padding(vertical = 16.dp)
            .animateContentSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Filter type selector
        Text(
            text = "FILTERS",
            style = MaterialTheme.typography.labelMedium,
            color = GlassWhite.copy(alpha = 0.7f),
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterType.values().forEach { filterType ->
                FilterChip(
                    filterType = filterType,
                    isSelected = currentFilter.type == filterType,
                    onClick = {
                        onFilterChange(currentFilter.copy(type = filterType))
                    }
                )
            }
        }
        
        // Intensity slider (only show if filter is not NONE)
        if (currentFilter.type != FilterType.NONE) {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "INTENSITY",
                        style = MaterialTheme.typography.labelSmall,
                        color = GlassWhite.copy(alpha = 0.7f)
                    )
                    Text(
                        text = "${(currentFilter.intensity * 100).roundToInt()}%",
                        style = MaterialTheme.typography.labelSmall,
                        color = GlassWhite
                    )
                }
                
                Slider(
                    value = currentFilter.intensity,
                    onValueChange = { intensity ->
                        onFilterChange(currentFilter.copy(intensity = intensity))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = SliderDefaults.colors(
                        thumbColor = GlassWhite,
                        activeTrackColor = GlassWhite.copy(alpha = 0.8f),
                        inactiveTrackColor = GlassWhite.copy(alpha = 0.2f)
                    )
                )
            }
        }
    }
}

@Composable
private fun FilterChip(
    filterType: FilterType,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlassButton(
        onClick = onClick,
        modifier = modifier.height(40.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = GlassWhite,
                    modifier = Modifier.size(16.dp)
                )
            }
            Text(
                text = filterType.displayName,
                style = MaterialTheme.typography.labelMedium,
                color = if (isSelected) GlassWhite else GlassWhite.copy(alpha = 0.7f)
            )
        }
    }
}
