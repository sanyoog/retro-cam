package com.retrocam.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Glassy translucent panel with subtle border
 * Core component for modern glass UI design
 * @param transparency 0-100, where higher values mean more transparent
 */
@Composable
fun GlassPanel(
    modifier: Modifier = Modifier,
    transparency: Float = 70f,
    content: @Composable () -> Unit
) {
    val alpha = (100f - transparency) / 100f // Convert to alpha (inverse)
    
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color.Black.copy(alpha = alpha))
            .border(
                width = 1.5.dp,
                color = Color.White.copy(alpha = alpha * 0.3f),
                shape = RoundedCornerShape(20.dp)
            )
            .padding(20.dp)
    ) {
        content()
    }
}
