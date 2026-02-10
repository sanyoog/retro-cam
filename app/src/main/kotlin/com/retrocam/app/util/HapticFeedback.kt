package com.retrocam.app.util

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.HapticFeedbackConstants
import android.view.View

/**
 * Utility class for providing haptic feedback throughout the app
 */
object HapticFeedback {
    
    /**
     * Light tap for UI interactions (button press, toggle)
     */
    fun lightTap(view: View) {
        view.performHapticFeedback(
            HapticFeedbackConstants.CLOCK_TICK
        )
    }
    
    /**
     * Medium impact for significant actions (mode switch, filter applied)
     */
    fun mediumImpact(view: View) {
        view.performHapticFeedback(
            HapticFeedbackConstants.CONTEXT_CLICK
        )
    }
    
    /**
     * Heavy impact for major actions (photo capture)
     */
    fun heavyImpact(context: Context) {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val effect = VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE)
            vibrator.vibrate(effect)
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(50)
        }
    }
    
    /**
     * Success feedback (photo saved)
     */
    fun success(context: Context) {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Double tap pattern
            val timings = longArrayOf(0, 30, 50, 30)
            val amplitudes = intArrayOf(0, 100, 0, 100)
            val effect = VibrationEffect.createWaveform(timings, amplitudes, -1)
            vibrator.vibrate(effect)
        } else {
            // For older devices
            val pattern = longArrayOf(0, 30, 50, 30)
            @Suppress("DEPRECATION")
            vibrator.vibrate(pattern, -1)
        }
    }
    
    /**
     * Error feedback
     */
    fun error(context: Context) {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val effect = VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE)
            vibrator.vibrate(effect)
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(100)
        }
    }
}
