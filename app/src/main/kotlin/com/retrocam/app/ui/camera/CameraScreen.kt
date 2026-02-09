package com.retrocam.app.ui.camera

import android.Manifest
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.FlipCameraAndroid
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rCapabilities
import com.retrocam.app.domain.model.CameraMode
import com.retrocam.app.domain.model.CaptureResult
import com.retrocam.app.domain.model.ManualSettings
import com.retrocam.app.presentation.camera.CameraViewModel
import com.retrocam.app.ui.components.CameraPreview
import com.retrocam.app.ui.components.GlassButton
import com.retrocam.app.ui.components.GlassPanel
import com.retrocam.app.ui.components.ProControlraPreview
import com.retrocam.app.ui.components.GlassButton
import com.retrocam.app.ui.components.GlassPanel
import com.retrocam.app.ui.components.ShutterButton
import com.retrocam.app.ui.theme.GlassBlack
import com.retrocam.app.ui.theme.GlassWhite
import com.retrocam.app.ui.theme.GlassSurfaceDark

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraScreen(
    viewModel: CameraViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    
    val cameraState by viewModel.cameraState.collectAsStateWithLifecycle()
    val captureResult by viewModel.captureResult.collectAsStateWithLifecycle()
    val manualSettings by viewModel.manualSettings.collectAsStateWithLifecycle()
    val cameraCapabilities by viewModel.cameraCapabilities.collectAsStateWithLifecycle()
    
    val cameraPermissionState = rememberPermissionState(
        permission = Manifest.permission.CAMERA
    )

    LaunchedEffect(captureResult) {
        when (captureResult) {
            is CaptureResult.Success -> {
                Toast.makeText(
                    context,
                    "Photo saved!",
                    Toast.LENGTH_SHORT
                ).show()
                viewModel.clearCaptureResult()
            }
            is CaptureResult.Error -> {
                Toast.makeText(
                    context,
                    "Failed to capture: ${(captureResult as CaptureResult.Error).message}",
                    Toast.LENGTH_LONG
                ).show()
                viewModel.clearCaptureResult()
            }
            null -> {}
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(GlassBlack)
    ) {
        if (cameraPermissionState.status.isGranted) {
            if (cameraState.isReady) {
                // Camera preview
                CameraPreview(
                    viewModel = viewModel,
                    lifecycleOwner = lifecycleOwner,
                    modifier = Modifier.fillMaxSize()
                )

                // UI Overlays
                CameraOverlay(
                    cameraMode = cameraState.mode,
                    manualSettings = manualSettings,
                    cameraCapabilities = cameraCapabilities,
                    onCaptureClick = { viewModel.capturePhoto() },
                    onModeToggle = { viewModel.toggleCameraMode() },
                    onManualSettingsChange = { viewModel.updateManualSettings(it) },
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                // Loading state
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = GlassWhite)
                }
            }

            cameraState.error?.let { error ->
                // Error state
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    GlassPanel(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = error,
                            color = GlassWhite,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        } else {
            // Permission not granted
            PermissionRequestScreen(
                onRequestPermission = { cameraPermissionState.launchPermissionRequest() },
                shouldShowRationale = cameraPermissionState.status.shouldShowRationale
            )
        }
    }
}

@Composable
private fun CameraOverlay(
    cameraMode: CameraMode,
    manualSettings: ManualSettings,
    cameraCapabilities: CameraCapabilities?,
    onCaptureClick: () -> Unit,
    onModeToggle: () -> Unit,
    onManualSettingsChange: (ManualSettings) -> Unit,
    modifier: Modifier = Modifier
) {
    var showProControls by remember { mutableStateOf(false) }
    
    // Show controls panel when in Pro mode
    LaunchedEffect(cameraMode) {
        showProControls = cameraMode == CameraMode.PRO
    }

    Box(modifier = modifier) {
        // Top bar
        TopBar(
            cameraMode = cameraMode,
            onModeToggle = onModeToggle,
            onProControlsToggle = { showProControls = !showProControls },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .statusBarsPadding()
        )

        // Bottom controls
        BottomControls(
            onCaptureClick = onCaptureClick,
            modifier = Modifier
    onProControlsToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Mode indicator with toggle
            GlassButton(
                onClick = onModeToggle,
                modifier = Modifier.height(40.dp),
                shape = RoundedCornerShape(20.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (cameraMode == CameraMode.PRO) 
                            Icons.Default.Settings else Icons.Default.Camera,
                        contentDescription = null,
                        tint = GlassWhite,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = if (cameraMode == CameraMode.NORMAL) "AUTO" else "PRO",
                        style = MaterialTheme.typography.labelMedium,
                        color = GlassWhite
                    )
                }
            }

            // Pro controls toggle (only visible in Pro mode)
            if (cameraMode == CameraMode.PRO) {
                GlassButton(
                    onClick = onProControlsToggle,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = "Controls",
                        tint = GlassWhite,
                        modifier = Modifier.size(20.dp)
                    )
                }ontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Mode indicator
            GlassPanel(
                modifier = Modifier.padding(8.dp)
            ) {
                Text(
                    text = if (cameraMode == CameraMode.NORMAL) "NORMAL" else "PRO",
                    style = MaterialTheme.typography.labelMedium,
                    color = GlassWhite
                )
            }

            // Settings button
            GlassButton(
                onClick = onModeToggle,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = GlassWhite
                )
            }
        }
    }
}

@Composable
private fun BottomControls(
    onCaptureClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .padding(bottom = 32.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Gallery button
            GlassButton(
                onClick = { /* TODO: Open gallery */ },
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PhotoLibrary,
                    contentDescription = "Gallery",
                    tint = GlassWhite,
                    modifier = Modifier.size(28.dp)
                )
            }

            // Shutter button
            ShutterButton(
                onClick = onCaptureClick,
                modifier = Modifier.size(80.dp)
            )

            // Flip camera button
            GlassButton(
                onClick = { /* TODO: Flip camera */ },
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.FlipCameraAndroid,
                    contentDescription = "Flip Camera",
                    tint = GlassWhite,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

@Composable
private fun PermissionRequestScreen(
    onRequestPermission: () -> Unit,
    shouldShowRationale: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(GlassBlack),
        contentAlignment = Alignment.Center
    ) {
        GlassPanel(
            modifier = Modifier
                .padding(32.dp)
                .fillMaxWidth()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = if (shouldShowRationale) {
                        "Camera permission is required to take photos"
                    } else {
                        "RetroCam needs camera access"
                    },
                    style = MaterialTheme.typography.titleMedium,
                    color = GlassWhite
                )
                
                Button(
                    onClick = onRequestPermission,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GlassWhite,
                        contentColor = GlassBlack
                    )
                ) {
                    Text("Grant Permission")
                }
            }
        }
    }
}
