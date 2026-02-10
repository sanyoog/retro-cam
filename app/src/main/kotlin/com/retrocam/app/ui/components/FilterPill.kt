package com.retrocam.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.retrocam.app.domain.model.FilterConfig
import com.retrocam.app.domain.model.FilterType
import com.retrocam.app.ui.theme.GlassWhite

/**
 * Modern filter selection pill with circular filter previews
 * Appears below the top action buttons
 */
@Composable
fun FilterPill(
    visible: Boolean,
    currentFilter: FilterConfig,
    onFilterChange: (FilterConfig) -> Unit,
    transparency: Float = 70f,
    modifier: Modifier = Modifier
) {
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
        Box(
            modifier = Modifier
                .height(80.dp)
                .clip(RoundedCornerShape(40.dp))
                .background(Color.Black.copy(alpha = (100f - transparency) / 100f))
                .border(
                    width = 1.5.dp,
                    color = Color.White.copy(alpha = ((100f - transparency) / 100f) * 0.3f),
                    shape = RoundedCornerShape(40.dp)
                )
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilterType.values().forEach { filterType ->
                    CircularFilterOption(
                        filterType = filterType,
                        isSelected = currentFilter.type == filterType,
                        onClick = {
                            onFilterChange(
                                FilterConfig(
                                    type = filterType,
                                    intensity = if (filterType == FilterType.NONE) 0f else 0.8f
                                )
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun CircularFilterOption(
    filterType: FilterType,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
            .width(56.dp)
            .clickable(onClick = onClick)
    ) {
        // Circular filter preview
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(
                    brush = getFilterGradient(filterType)
                )
                .border(
                    width = if (isSelected) 2.5.dp else 1.dp,
                    color = if (isSelected) GlassWhite else Color(0x4DFFFFFF), // 30% white
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            // Show check icon if selected
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(GlassWhite),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
        
        // Filter name
        Text(
            text = filterType.displayName.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            fontSize = 9.sp,
            color = if (isSelected) GlassWhite else GlassWhite.copy(alpha = 0.7f),
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            maxLines = 1
        )
    }
}

/**
 * Get gradient representing each filter type
 */
private fun getFilterGradient(filterType: FilterType): Brush {
    return when (filterType) {
        FilterType.NONE -> Brush.linearGradient(
            colors = listOf(
                Color(0xFFFFFFFF),
                Color(0xFFE0E0E0)
            )
        )
        FilterType.VINTAGE -> Brush.linearGradient(
            colors = listOf(
                Color(0xFFE8C39E),
                Color(0xFFD4A574),
                Color(0xFFB8865F)
            )
        )
        FilterType.BLACK_AND_WHITE -> Brush.linearGradient(
            colors = listOf(
                Color(0xFFFFFFFF),
                Color(0xFF808080),
                Color(0xFF000000)
            )
        )
        FilterType.SEPIA -> Brush.linearGradient(
            colors = listOf(
                Color(0xFFFFF8DC),
                Color(0xFFD2B48C),
                Color(0xFF8B7355)
            )
        )
        FilterType.FILM_GRAIN -> Brush.linearGradient(
            colors = listOf(
                Color(0xFFD0D0D0),
                Color(0xFFA0A0A0),
                Color(0xFF707070)
            )
        )
        FilterType.WARM_VINTAGE -> Brush.linearGradient(
            colors = listOf(
                Color(0xFFFFE4B5),
                Color(0xFFFFB347),
                Color(0xFFFF8C00)
            )
        )
        FilterType.COOL_VINTAGE -> Brush.linearGradient(
            colors = listOf(
                Color(0xFFB0E0E6),
                Color(0xFF87CEEB),
                Color(0xFF4682B4)
            )
        )
        FilterType.FADED -> Brush.linearGradient(
            colors = listOf(
                Color(0xFFF5F5F5),
                Color(0xFFD0D0D0),
                Color(0xFFB0B0B0)
            )
        )
        FilterType.DRAMATIC -> Brush.linearGradient(
            colors = listOf(
                Color(0xFFFFFFFF),
                Color(0xFF404040),
                Color(0xFF000000)
            )
        )
        FilterType.SOFT_GLOW -> Brush.radialGradient(
            colors = listOf(
                Color(0xFFFFFAF0),
                Color(0xFFFFE4B5),
                Color(0xFFFFD700)
            )
        )
        FilterType.VIGNETTE -> Brush.radialGradient(
            colors = listOf(
                Color(0xFFFFFFFF),
                Color(0xFFD0D0D0),
                Color(0xFF404040)
            )
        )
        FilterType.CROSS_PROCESS -> Brush.linearGradient(
            colors = listOf(
                Color(0xFF00CED1),
                Color(0xFFFF1493),
                Color(0xFFFFD700)
            )
        )
        FilterType.POLAROID -> Brush.linearGradient(
            colors = listOf(
                Color(0xFFFFFAF0),
                Color(0xFFE8D5C4),
                Color(0xFFD2B48C)
            )
        )
        FilterType.RETRO_70S -> Brush.linearGradient(
            colors = listOf(
                Color(0xFFFFB347),
                Color(0xFFFF8C00),
                Color(0xFFB8860B)
            )
        )
        FilterType.RETRO_80S -> Brush.linearGradient(
            colors = listOf(
                Color(0xFFFF1493),
                Color(0xFF00CED1),
                Color(0xFFFFD700)
            )
        )
    }
}
