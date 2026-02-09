package com.retrocam.app.util

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaActionSound

/**
 * Utility class for playing camera sound effects
 */
class SoundEffects(private val context: Context) {
    
    private var soundEnabled = true
    private var mediaActionSound: MediaActionSound? = null
    
    init {
        initializeSound()
    }
    
    private fun initializeSound() {
        try {
            mediaActionSound = MediaActionSound().apply {
                load(MediaActionSound.SHUTTER_CLICK)
                load(MediaActionSound.START_VIDEO_RECORDING)
                load(MediaActionSound.STOP_VIDEO_RECORDING)
                load(MediaActionSound.FOCUS_COMPLETE)
            }
        } catch (e: Exception) {
            // Sound initialization failed, continue without sound
            mediaActionSound = null
        }
    }
    
    fun playShutter() {
        if (soundEnabled) {
            mediaActionSound?.play(MediaActionSound.SHUTTER_CLICK)
        }
    }
    
    fun playFocus() {
        if (soundEnabled) {
            mediaActionSound?.play(MediaActionSound.FOCUS_COMPLETE)
        }
    }
    
    fun playStartRecording() {
        if (soundEnabled) {
            mediaActionSound?.play(MediaActionSound.START_VIDEO_RECORDING)
        }
    }
    
    fun playStopRecording() {
        if (soundEnabled) {
            mediaActionSound?.play(MediaActionSound.STOP_VIDEO_RECORDING)
        }
    }
    
    fun setSoundEnabled(enabled: Boolean) {
        soundEnabled = enabled
    }
    
    fun isSoundEnabled(): Boolean = soundEnabled
    
    fun release() {
        mediaActionSound?.release()
        mediaActionSound = null
    }
}
