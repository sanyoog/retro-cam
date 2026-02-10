package com.retrocam.app.presentation.camera.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun VideoRecordButton(
    isRecording: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scale = remember { Animatable(1f) }
    val pulseAlpha = remember { Animatable(0.5f) }

    LaunchedEffect(isRecording) {
        if (isRecording) {
            pulseAlpha.animateTo(
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(800, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                )
            )
        } else {
            pulseAlpha.snapTo(0.5f)
        }
    }

    Box(
        modifier = modifier
            .size(80.dp)
            .clip(CircleShape)
            .border(4.dp, Color.White, CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        // Pulsing outer ring when recording
        if (isRecording) {
            Canvas(modifier = Modifier.size(80.dp)) {
                drawCircle(
                    color = Color.Red.copy(alpha = pulseAlpha.value * 0.3f),
                    radius = size.minDimension / 2
                )
            }
        }

        // Inner button
        Box(
            modifier = Modifier
                .size(if (isRecording) 32.dp else 68.dp)
                .scale(scale.value)
                .clip(if (isRecording) RoundedCornerShape(8.dp) else CircleShape)
                .background(if (isRecording) Color.Red else Color.White)
        )
    }
}
