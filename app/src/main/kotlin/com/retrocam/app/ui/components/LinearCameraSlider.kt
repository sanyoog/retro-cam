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
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    if (size.width > 0) {
                        // Calculate sensitivity - more drag needed for value change
                        val sensitivity = 0.003f * (valueRange.endInclusive - valueRange.start)
                        val delta = -dragAmount.x * sensitivity
                        val newValue = (value + delta).coerceIn(valueRange)
                        onValueChange(newValue)
                    }
                }
            }
            .pointerInput(valueRange) {
                detectTapGestures { offset ->
                    // Tap to adjust based on distance from center
                    val centerX = size.width / 2
                    val distanceFromCenter = offset.x - centerX
                    val sensitivity = 0.15f * (valueRange.endInclusive - valueRange.start)
                    val delta = distanceFromCenter / size.width * sensitivity
                    val newValue = (value + delta).coerceIn(valueRange)
                    onValueChange(newValue)
                }
            }
    ) {
        val width = size.width
        val height = size.height
        val centerY = height / 2
        val centerX = width / 2
        
        // Calculate normalized position (0 to 1)
        val normalizedValue = (value - valueRange.start) / (valueRange.endInclusive - valueRange.start)
        
        // Draw tick marks - they "slide" past the center indicator
        val tickCount = if (steps > 0) steps + 1 else 31
        val tickSpacing = width / 8f // Spacing between ticks
        
        // Calculate offset to make ticks appear to move
        val offset = -normalizedValue * tickSpacing * (tickCount - 1)
        
        for (i in -10..tickCount + 10) {
            val tickX = centerX + offset + (i * tickSpacing)
            
            // Only draw visible ticks
            if (tickX < -20 || tickX > width + 20) continue
            
            val distanceFromCenter = kotlin.math.abs(tickX - centerX)
            val maxDistance = width * 0.25f
            
            // Calculate tick properties based on distance from center
            val alpha = if (distanceFromCenter < maxDistance) {
                1f - (distanceFromCenter / maxDistance) * 0.7f
            } else {
                0.3f
            }
            
            val tickHeight = if (i % 5 == 0) {
                if (distanceFromCenter < tickSpacing * 2) 16.dp.toPx() else 12.dp.toPx()
            } else {
                if (distanceFromCenter < tickSpacing * 2) 10.dp.toPx() else 6.dp.toPx()
            }
            
            val strokeWidth = if (distanceFromCenter < tickSpacing * 2) 2.5.dp.toPx() else 1.5.dp.toPx()
            
            drawLine(
                color = GlassWhite.copy(alpha = alpha),
                start = Offset(tickX, centerY - tickHeight / 2),
                end = Offset(tickX, centerY + tickHeight / 2),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )
        }
        
        // Draw fixed center indicator (like camera lens marking)
        drawLine(
            color = GlassWhite,
            start = Offset(centerX, centerY - 22.dp.toPx()),
            end = Offset(centerX, centerY + 22.dp.toPx()),
            strokeWidth = 3.5.dp.toPx(),
            cap = StrokeCap.Round
        )
        
        // Draw glowing circle at center
        drawCircle(
            color = GlassWhite.copy(alpha = 0.4f),
            radius = 14.dp.toPx(),
            center = Offset(centerX, centerY)
        )
        
        drawCircle(
            color = GlassWhite,
            radius = 7.dp.toPx(),
            center = Offset(centerX, centerY)
        )
    }
}
