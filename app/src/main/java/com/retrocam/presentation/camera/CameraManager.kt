package com.retrocam.presentation.camera

import android.content.Context
import android.net.Uri
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.retrocam.domain.model.CameraSettings
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Manager for CameraX operations.
 * Handles camera initialization, preview, and photo capture.
 */
@Singleton
class CameraManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var camera: Camera? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private var imageCapture: ImageCapture? = null
    private var preview: Preview? = null
    
    /**
     * Initialize camera with preview.
     */
    suspend fun startCamera(
        lifecycleOwner: LifecycleOwner,
        previewView: PreviewView,
        cameraSettings: CameraSettings? = null
    ): Result<Unit> = withContext(Dispatchers.Main) {
        try {
            val provider = getCameraProvider()
            cameraProvider = provider
            
            // Unbind all use cases before rebinding
            provider.unbindAll()
            
            // Setup preview
            preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }
            
            // Setup image capture
            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                .build()
            
            // Select back camera
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            
            // Bind use cases to camera
            camera = provider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageCapture
            )
            
            // Apply camera settings if provided
            cameraSettings?.let { applySettings(it) }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Capture photo and save to file.
     */
    suspend fun capturePhoto(): Result<Uri> = withContext(Dispatchers.IO) {
        try {
            val capture = imageCapture ?: return@withContext Result.failure(
                IllegalStateException("Camera not initialized")
            )
            
            // Create output file
            val photoFile = createPhotoFile()
            val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
            
            // Capture photo
            val result = suspendCoroutine<ImageCapture.OutputFileResults> { continuation ->
                capture.takePicture(
                    outputOptions,
                    ContextCompat.getMainExecutor(context),
                    object : ImageCapture.OnImageSavedCallback {
                        override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                            continuation.resume(output)
                        }
                        
                        override fun onError(exception: ImageCaptureException) {
                            continuation.resume(
                                throw exception
                            )
                        }
                    }
                )
            }
            
            val savedUri = result.savedUri ?: Uri.fromFile(photoFile)
            Result.success(savedUri)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Apply camera settings (for Pro Mode).
     */
    fun applySettings(settings: CameraSettings) {
        camera?.cameraControl?.let { control ->
            settings.exposureCompensation?.let { exposure ->
                control.setExposureCompensationIndex(exposure)
            }
            
            settings.focusDistance?.let { distance ->
                // Manual focus would be set via Camera2 interop
                // For now, using CameraX defaults
            }
            
            // ISO, shutter speed, and white balance require Camera2 interop
            // These would be implemented with Camera2Interop.Extender
        }
    }
    
    /**
     * Release camera resources.
     */
    fun release() {
        cameraProvider?.unbindAll()
        camera = null
        preview = null
        imageCapture = null
    }
    
    /**
     * Get CameraProvider instance.
     */
    private suspend fun getCameraProvider(): ProcessCameraProvider {
        return suspendCoroutine { continuation ->
            val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
            cameraProviderFuture.addListener({
                continuation.resume(cameraProviderFuture.get())
            }, ContextCompat.getMainExecutor(context))
        }
    }
    
    /**
     * Create file for saving captured photo.
     */
    private fun createPhotoFile(): File {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir = File(context.getExternalFilesDir(null), "Pictures")
        storageDir.mkdirs()
        return File(storageDir, "RETROCAM_$timestamp.jpg")
    }
}
