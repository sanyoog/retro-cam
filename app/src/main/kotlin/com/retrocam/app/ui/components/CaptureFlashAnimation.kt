package com.retrocam.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.delay

/**
 * Flash animation effect when capturing a photo
 */
@Composable
fun CaptureFlashAnimation(
    trigger: Boolean,
    onComplete: () -> Unit = {}
) {
    var showFlash by remember { mutableStateOf(false) }
    
    LaunchedEffect(trigger) {
        if (trigger) {
            showFlash = true
            delay(100)
            showFlash = false
            onComplete()
        }
    }
    
    AnimatedVisibility(
        visible = showFlash,
        enter = fadeIn(animationSpec = tween(50)),
        exit = fadeOut(animationSpec = tween(100))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        )
    }
}
