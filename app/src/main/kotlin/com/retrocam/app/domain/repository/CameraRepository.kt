package com.retrocam.app.domain.repository

import android.net.Uri
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import com.retrocam.app.domain.model.CaptureResult
import com.retrocam.app.domain.model.ManualSettings
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for camera operations
 * Abstracts CameraX implementation details from the domain layer
 */
interface CameraRepository {
    
    /**
     * Get the camera provider
     */
    suspend fun getCameraProvider(): ProcessCameraProvider
    
    /**
     * Create a preview use case
     */
    fun createPreview(): Preview
    
    /**
     * Create an image capture use case
     */
    fun createImageCapture(): ImageCapture
    
    /**
     * Get the default camera selector (back camera)
     */
    fun getCameraSelector(): CameraSelector
    
    /**
     * Capture a photo and save it to storage
     */
    suspend fun capturePhoto(
        imageCapture: ImageCapture,
        manualSettings: ManualSettings? = null
    ): CaptureResult
    
    /**
     * Apply manual settings to the camera (Pro mode)
     */
    fun applyManualSettings(settings: ManualSettings)
}
