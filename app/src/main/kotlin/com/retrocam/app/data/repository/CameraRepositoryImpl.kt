package com.retrocam.app.data.repository

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.retrocam.app.domain.model.CaptureResult
import com.retrocam.app.domain.model.ManualSettings
import com.retrocam.app.domain.repository.CameraRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.guava.await
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

private const val TAG = "CameraRepository"

/**
 * Implementation of camera operations using CameraX
 */
class CameraRepositoryImpl(
    private val context: Context,
    private val ioDispatcher: CoroutineDispatcher
) : CameraRepository {

    override suspend fun getCameraProvider(): ProcessCameraProvider {
        return withContext(ioDispatcher) {
            ProcessCameraProvider.getInstance(context).await()
        }
    }

    override fun createPreview(): Preview {
        return Preview.Builder()
            .build()
    }

    override fun createImageCapture(): ImageCapture {
        return ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
            .build()
    }

    override fun getCameraSelector(): CameraSelector {
        return CameraSelector.DEFAULT_BACK_CAMERA
    }

    override suspend fun capturePhoto(
        imageCapture: ImageCapture,
        manualSettings: ManualSettings?
    ): CaptureResult = withContext(ioDispatcher) {
        val name = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.US)
            .format(System.currentTimeMillis())
        
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/RetroCam")
            }
        }

        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(
                context.contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )
            .build()

        try {
            val result = suspendCoroutine<CaptureResult> { continuation ->
                imageCapture.takePicture(
                    outputOptions,
                    ContextCompat.getMainExecutor(context),
                    object : ImageCapture.OnImageSavedCallback {
                        override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                            val savedUri = output.savedUri ?: Uri.EMPTY
                            Log.d(TAG, "Photo saved: $savedUri")
                            continuation.resume(
                                CaptureResult.Success(
                                    uri = savedUri,
                                    filePath = savedUri.toString()
                                )
                            )
                        }

                        override fun onError(exception: ImageCaptureException) {
                            Log.e(TAG, "Photo capture failed: ${exception.message}", exception)
                            continuation.resume(
                                CaptureResult.Error(
                                    message = exception.message ?: "Unknown error",
                                    exception = exception
                                )
                            )
                        }
                    }
                )
            }
            result
        } catch (e: Exception) {
            Log.e(TAG, "Photo capture exception: ${e.message}", e)
            CaptureResult.Error(
                message = e.message ?: "Unknown error",
                exception = e
            )
        }
    }

    override fun applyManualSettings(settings: ManualSettings) {
        // Will be implemented with Camera2 interop in Phase 2
        Log.d(TAG, "Manual settings: $settings")
    }
}
