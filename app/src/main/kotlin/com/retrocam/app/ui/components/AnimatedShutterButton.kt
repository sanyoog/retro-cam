package com.retrocam.app.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.retrocam.app.ui.theme.GlassWhite
import kotlinx.coroutines.delay

/**
 * Enhanced shutter button with press animation and ripple effect
 */
@Composable
fun AnimatedShutterButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isPressed by remember { mutableStateOf(false) }
    var showRipple by remember { mutableStateOf(false) }
    
    // Scale animation for press
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.85f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "shutter_scale"
    )
    
    // Rotation animation for feedback
    val rotation by animateFloatAsState(
        targetValue = if (isPressed) 15f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "shutter_rotation"
    )
    
    LaunchedEffect(isPressed) {
        if (isPressed) {
            delay(50)
            showRipple = true
            delay(100)
            isPressed = false
            showRipple = false
        }
    }
    
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // Ripple effect\n        AnimatedVisibility(
            visible = showRipple,
            enter = fadeIn(tween(100)) + scaleIn(
                initialScale = 0.8f,
                animationSpec = tween(300, easing = FastOutSlowInEasing)
            ),
            exit = fadeOut(tween(200)) + scaleOut(
                targetScale = 1.5f,
                animationSpec = tween(300)
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                GlassWhite.copy(alpha = 0.3f),
                                Color.Transparent
                            )
                        ),
                        shape = CircleShape
                    )
            )
        }
        
        // Main shutter button
        ShutterButton(
            onClick = {
                isPressed = true
                onClick()
            },
            modifier = Modifier
                .fillMaxSize()
                .scale(scale)
        )
    }
}
