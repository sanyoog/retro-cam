package com.retrocam.app.presentation.camera.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TimerCountdown(
    secondsRemaining: Int,
    modifier: Modifier = Modifier
) {
    val scale = remember { Animatable(0f) }
    val alpha = remember { Animatable(1f) }

    LaunchedEffect(secondsRemaining) {
        scale.snapTo(0f)
        alpha.snapTo(1f)
        
        scale.animateTo(
            targetValue = 1.5f,
            animationSpec = tween(durationMillis = 1000, easing = LinearEasing)
        )
        
        alpha.animateTo(
            targetValue = 0f,
            animationSpec = tween(durationMillis = 800, delayMillis = 200, easing = LinearEasing)
        )
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Expanding circle
        Canvas(
            modifier = Modifier.size(200.dp * scale.value)
        ) {
            drawCircle(
                color = Color.White.copy(alpha = alpha.value * 0.3f),
                radius = size.minDimension / 2,
                style = Stroke(width = 4f)
            )
        }

        // Countdown number
        Text(
            text = secondsRemaining.toString(),
            style = MaterialTheme.typography.displayLarge,
            fontSize = (80.sp.value * (1f + scale.value * 0.2f)).sp,
            fontWeight = FontWeight.Bold,
            color = Color.White.copy(alpha = alpha.value)
        )
    }
}
