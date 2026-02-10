package com.retrocam.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Glass-style button with translucent background
 * Used for UI controls throughout the app
 * @param transparency 0-100, where higher values mean more transparent
 */
@Composable
fun GlassButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    transparency: Float = 70f,
    shape: androidx.compose.ui.graphics.Shape = CircleShape,
    content: @Composable () -> Unit
) {
    val alpha = (100f - transparency) / 100f // Convert to alpha (inverse)
    
    Box(
        modifier = modifier
            .clip(shape)
            .background(Color.Black.copy(alpha = alpha))
            .border(
                width = 1.5.dp,
                color = Color.White.copy(alpha = alpha * 0.3f),
                shape = shape
            )
            .clickable(
                onClick = onClick,
                indication = rememberRipple(color = Color.White),
                interactionSource = remember { MutableInteractionSource() }
            ),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}
