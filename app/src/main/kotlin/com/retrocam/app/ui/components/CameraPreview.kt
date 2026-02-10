package com.retrocam.app.ui.components

import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.retrocam.app.presentation.camera.CameraViewModel

@Composable
fun CameraPreview(
    viewModel: CameraViewModel,
    lifecycleOwner: LifecycleOwner,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val cameraState by viewModel.cameraState.collectAsStateWithLifecycle()
    
    val previewView = remember {
        PreviewView(context).apply {
            implementationMode = PreviewView.ImplementationMode.COMPATIBLE
            scaleType = PreviewView.ScaleType.FILL_CENTER
        }
    }

    DisposableEffect(key1 = lifecycleOwner, key2 = cameraState.lensFacing) {
        val cameraProvider = viewModel.getCameraProvider()
        val preview = viewModel.getPreview()
        val imageCapture = viewModel.getImageCapture()
        val cameraSelector = viewModel.getCameraSelector(cameraState.lensFacing)

        if (cameraProvider != null && preview != null && imageCapture != null) {
            try {
                // Unbind all use cases before rebinding
                cameraProvider.unbindAll()

                // Set surface provider
                preview.setSurfaceProvider(previewView.surfaceProvider)

                // Bind use cases to camera (MUST include imageCapture!)
                val camera = cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageCapture  // This was missing!
                )
                
                // Set camera reference in ViewModel for manual controls
                viewModel.setCamera(camera)
            } catch (e: Exception) {
                // Handle binding errors
                e.printStackTrace()
            }
        }

        onDispose {
            cameraProvider?.unbindAll()
        }
    }

    AndroidView(
        factory = { previewView },
        modifier = modifier
    )
}
