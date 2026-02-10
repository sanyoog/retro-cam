package com.retrocam.app.presentation.camera.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap

@Composable
fun GridOverlay(
    enabled: Boolean,
    modifier: Modifier = Modifier,
    color: Color = Color.White.copy(alpha = 0.3f),
    strokeWidth: Float = 1f
) {
    if (!enabled) return

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        // Vertical lines (rule of thirds)
        val verticalLine1X = width / 3f
        val verticalLine2X = width * 2f / 3f

        drawLine(
            color = color,
            start = Offset(verticalLine1X, 0f),
            end = Offset(verticalLine1X, height),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )

        drawLine(
            color = color,
            start = Offset(verticalLine2X, 0f),
            end = Offset(verticalLine2X, height),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )

        // Horizontal lines (rule of thirds)
        val horizontalLine1Y = height / 3f
        val horizontalLine2Y = height * 2f / 3f

        drawLine(
            color = color,
            start = Offset(0f, horizontalLine1Y),
            end = Offset(width, horizontalLine1Y),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )

        drawLine(
            color = color,
            start = Offset(0f, horizontalLine2Y),
            end = Offset(width, horizontalLine2Y),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
    }
}
