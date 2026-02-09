package com.retrocam.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Glassy panel with frosted blur effect.
 * Core component for the premium UI aesthetic.
 */
@Composable
fun GlassPanel(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 16.dp,
    backgroundColor: Color = Color(0x1AFFFFFF),
    borderColor: Color = Color(0x26FFFFFF),
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(cornerRadius)
            )
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0x0DFFFFFF),
                        Color(0x00FFFFFF)
                    )
                ),
                shape = RoundedCornerShape(cornerRadius)
            )
    ) {
        // Border
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    color = Color.Transparent,
                    shape = RoundedCornerShape(cornerRadius)
                )
        )
        
        content()
    }
}

/**
 * Glassy surface with elevation and subtle glow.
 */
@Composable
fun GlassSurface(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    shape: RoundedCornerShape = RoundedCornerShape(16.dp),
    backgroundColor: Color = Color(0x14FFFFFF),
    contentColor: Color = Color.White,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier,
        onClick = onClick ?: {},
        shape = shape,
        color = backgroundColor,
        contentColor = contentColor
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0x0AFFFFFF),
                            Color(0x00FFFFFF)
                        )
                    )
                )
        ) {
            content()
        }
    }
}
