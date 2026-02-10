package com.retrocam.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.retrocam.app.ui.theme.GlassWhite
import kotlin.math.roundToInt

/**
 * Linear camera-style slider with tick marks
 * Modern iOS/Android camera app style slider
 */
@Composable
fun LinearCameraSlider(
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    steps: Int = 0
) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(28.dp)
            .pointerInput(valueRange) {
                detectTapGestures { offset ->
                    if (size.width > 0) {
                        val position = (offset.x / size.width).coerceIn(0f, 1f)
                        val newValue = valueRange.start + (valueRange.endInclusive - valueRange.start) * position
                        onValueChange(newValue.coerceIn(valueRange))
                    }
                }
            }
            .pointerInput(valueRange) {
                detectDragGestures { change, _ ->
                    change.consume()
                    if (size.width > 0) {
                        val position = (change.position.x / size.width).coerceIn(0f, 1f)
                        val newValue = valueRange.start + (valueRange.endInclusive - valueRange.start) * position
                        onValueChange(newValue.coerceIn(valueRange))
                    }
                }
            }
    ) {
        val width = size.width
        val height = size.height
        val centerY = height / 2
        
        // Calculate current position
        val normalizedValue = (value - valueRange.start) / (valueRange.endInclusive - valueRange.start)
        val thumbX = width * normalizedValue
        
        // Draw tick marks
        val tickCount = if (steps > 0) steps + 1 else 21
        val tickSpacing = width / (tickCount - 1)
        
        for (i in 0 until tickCount) {
            val tickX = i * tickSpacing
            val distanceFromThumb = kotlin.math.abs(tickX - thumbX)
            val maxDistance = width * 0.2f
            
            // Calculate tick properties based on distance from thumb
            val alpha = if (distanceFromThumb < maxDistance) {
                1f - (distanceFromThumb / maxDistance) * 0.7f
            } else {
                0.3f
            }
            
            val tickHeight = if (i % 5 == 0) {
                if (distanceFromThumb < maxDistance) 16.dp.toPx() else 12.dp.toPx()
            } else {
                if (distanceFromThumb < maxDistance) 10.dp.toPx() else 6.dp.toPx()
            }
            
            val strokeWidth = if (distanceFromThumb < maxDistance) 2.5.dp.toPx() else 1.5.dp.toPx()
            
            drawLine(
                color = GlassWhite.copy(alpha = alpha),
                start = Offset(tickX, centerY - tickHeight / 2),
                end = Offset(tickX, centerY + tickHeight / 2),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )
        }
        
        // Draw center indicator line (thumb position)
        drawLine(
            color = GlassWhite,
            start = Offset(thumbX, centerY - 20.dp.toPx()),
            end = Offset(thumbX, centerY + 20.dp.toPx()),
            strokeWidth = 3.dp.toPx(),
            cap = StrokeCap.Round
        )
        
        // Draw glowing circle at thumb
        drawCircle(
            color = GlassWhite.copy(alpha = 0.3f),
            radius = 12.dp.toPx(),
            center = Offset(thumbX, centerY)
        )
        
        drawCircle(
            color = GlassWhite,
            radius = 6.dp.toPx(),
            center = Offset(thumbX, centerY)
        )
    }
}
