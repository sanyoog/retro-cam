package com.retrocam.app.ui.camera

import android.Manifest
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.retrocam.app.domain.model.CameraCapabilities
import com.retrocam.app.domain.model.CameraMode
import com.retrocam.app.domain.model.CaptureResult
import com.retrocam.app.domain.model.FilterConfig
import com.retrocam.app.domain.model.ManualSettings
import com.retrocam.app.presentation.camera.CameraViewModel
import com.retrocam.app.ui.components.AnimatedShutterButton
import com.retrocam.app.ui.components.CameraPreview
import com.retrocam.app.ui.components.CaptureFlashAnimation
import com.retrocam.app.ui.components.FilterPill
import com.retrocam.app.ui.components.GalleryThumbnail
import com.retrocam.app.ui.components.GlassButton
import com.retrocam.app.ui.components.GlassPanel
import com.retrocam.app.ui.components.ProModePill
import com.retrocam.app.ui.components.QuickSettingsPill
import com.retrocam.app.ui.components.ShutterButton
import com.retrocam.app.ui.theme.GlassBlack
import com.retrocam.app.ui.theme.GlassWhite
import com.retrocam.app.ui.theme.GlassSurfaceDark
import com.retrocam.app.util.GalleryOpener
import com.retrocam.app.util.HapticFeedback
import com.retrocam.app.util.ImageLoader
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraScreen(
    onNavigateToSettings: () -> Unit = {},
    viewModel: CameraViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    
    val cameraState by viewModel.cameraState.collectAsStateWithLifecycle()
    val captureResult by viewModel.captureResult.collectAsStateWithLifecycle()
    val manualSettings by viewModel.manualSettings.collectAsStateWithLifecycle()
    val cameraCapabilities by viewModel.cameraCapabilities.collectAsStateWithLifecycle()
    val currentFilter by viewModel.currentFilter.collectAsStateWithLifecycle()
    val lastPhotoUri by viewModel.lastPhotoUri.collectAsStateWithLifecycle()
    val captureFlashTrigger by viewModel.captureFlashTrigger.collectAsStateWithLifecycle()
    val hapticsEnabled by viewModel.hapticsEnabled.collectAsStateWithLifecycle()
    
    var showFilterPanel by remember { mutableStateOf(false) }
    var showQuickSettings by remember { mutableStateOf(false) }
    var galleryThumbnail by remember { mutableStateOf<androidx.compose.ui.graphics.ImageBitmap?>(null) }
    var loadingThumbnail by remember { mutableStateOf(false) }
    
    val scope = rememberCoroutineScope()
    
    val photoQuality by viewModel.photoQuality.collectAsStateWithLifecycle()
    val aspectRatio by viewModel.aspectRatio.collectAsStateWithLifecycle()
    
    // Load thumbnail when last photo changes
    LaunchedEffect(lastPhotoUri) {
        lastPhotoUri?.let { uri ->
            loadingThumbnail = true
            scope.launch {
                val bitmap = ImageLoader.loadThumbnail(context, uri, 128)
                galleryThumbnail = bitmap?.asImageBitmap()
                loadingThumbnail = false
            }
        }
    }
    
    // Load latest photo on screen start
    LaunchedEffect(Unit) {
        scope.launch {
            val latestUri = ImageLoader.getLatestPhoto(context)
            if (latestUri != null) {
                loadingThumbnail = true
                val bitmap = ImageLoader.loadThumbnail(context, latestUri, 128)
                galleryThumbnail = bitmap?.asImageBitmap()
                loadingThumbnail = false
            }
        }
    }
    
    val cameraPermissionState = rememberPermissionState(
        permission = Manifest.permission.CAMERA
    )

    LaunchedEffect(captureResult) {
        when (captureResult) {
            is CaptureResult.Success -> {
                // Success haptic feedback
                if (hapticsEnabled) {
                    HapticFeedback.success(context)
                }
                Toast.makeText(
                    context,
                    "Photo saved!",
                    Toast.LENGTH_SHORT
                ).show()
                viewModel.clearCaptureResult()
            }
            is CaptureResult.Error -> {
                // Error haptic feedback
                if (hapticsEnabled) {
                    HapticFeedback.error(context)
                }
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
                
                // Capture flash animation
                CaptureFlashAnimation(trigger = captureFlashTrigger)

                // UI Overlays
                CameraOverlay(
                    cameraMode = cameraState.mode,
                    manualSettings = manualSettings,
                    cameraCapabilities = cameraCapabilities,
                    currentFilter = currentFilter,
                    showFilterPanel = showFilterPanel,
                    showQuickSettings = showQuickSettings,
                    photoQuality = photoQuality,
                    aspectRatio = aspectRatio,
                    galleryThumbnail = galleryThumbnail,
                    loadingThumbnail = loadingThumbnail,
                    hapticsEnabled = hapticsEnabled,
                    onCaptureClick = { 
                        if (hapticsEnabled) {
                            HapticFeedback.heavyImpact(context)
                        }
                        viewModel.capturePhoto() 
                    },
                    onModeToggle = { 
                        viewModel.toggleCameraMode() 
                    },
                    onManualSettingsChange = { viewModel.updateManualSettings(it) },
                    onFilterToggle = { showFilterPanel = !showFilterPanel },
                    onFilterChange = { viewModel.updateFilter(it) },
                    onGalleryClick = { 
                        GalleryOpener.openGallery(context)
                    },
                    onSettingsClick = { showQuickSettings = !showQuickSettings },
                    onPhotoQualityChange = { viewModel.setPhotoQuality(it) },
                    onAspectRatioChange = { viewModel.setAspectRatio(it) },
                    onFullSettingsClick = { onNavigateToSettings() },
                    onFlipCamera = { viewModel.flipCamera() },
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
                shouldShowRationale = false
            )
        }
    }
}

@Composable
private fun CameraOverlay(
    cameraMode: CameraMode,
    manualSettings: ManualSettings,
    cameraCapabilities: CameraCapabilities?,
    currentFilter: FilterConfig,
    showFilterPanel: Boolean,
    showQuickSettings: Boolean,
    photoQuality: Int,
    aspectRatio: Int,
    galleryThumbnail: androidx.compose.ui.graphics.ImageBitmap?,
    loadingThumbnail: Boolean,
    hapticsEnabled: Boolean,
    onCaptureClick: () -> Unit,
    onModeToggle: () -> Unit,
    onManualSettingsChange: (ManualSettings) -> Unit,
    onFilterToggle: () -> Unit,
    onFilterChange: (FilterConfig) -> Unit,
    onGalleryClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onPhotoQualityChange: (Int) -> Unit,
    onAspectRatioChange: (Int) -> Unit,
    onFullSettingsClick: () -> Unit,
    onFlipCamera: () -> Unit,
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
            hapticsEnabled = hapticsEnabled,
            onModeToggle = onModeToggle,
            onProControlsToggle = { showProControls = !showProControls },
            onFilterToggle = onFilterToggle,
            onSettingsClick = onSettingsClick,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .statusBarsPadding()
        )

        // Quick Settings Pill (below top bar)
        com.retrocam.app.presentation.camera.components.QuickSettingsPill(
            visible = showQuickSettings,
            selectedQuality = photoQuality,
            selectedRatio = aspectRatio,
            onQualitySelected = onPhotoQualityChange,
            onRatioSelected = onAspectRatioChange,
            onFullSettingsClick = onFullSettingsClick,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(top = 60.dp, start = 16.dp, end = 16.dp)
        )

        // Bottom controls
        BottomControls(
            galleryThumbnail = galleryThumbnail,
            loadingThumbnail = loadingThumbnail,
            onCaptureClick = onCaptureClick,
            onGalleryClick = onGalleryClick,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
        )

        // Filter pill (below top buttons)
        AnimatedVisibility(
            visible = showFilterPanel,
            enter = fadeIn(spring()) + scaleIn(
                initialScale = 0.8f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy
                )
            ),
            exit = fadeOut(spring()) + scaleOut(targetScale = 0.8f)
        ) {
            FilterPill(
                visible = true,
                currentFilter = currentFilter,
                onFilterChange = onFilterChange,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 120.dp) // Below the top bar
            )
        }

        // Quick Settings Pill (top right)
        AnimatedVisibility(
            visible = showQuickSettings,
            enter = fadeIn(tween(200)) + expandVertically(),
            exit = fadeOut(tween(200)) + shrinkVertically()
        ) {
            QuickSettingsPill(
                photoQuality = photoQuality,
                aspectRatio = aspectRatio,
                onPhotoQualityChange = onPhotoQualityChange,
                onAspectRatioChange = onAspectRatioChange,
                onFullSettingsClick = onFullSettingsClick,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .statusBarsPadding()
                    .width(220.dp)
            )
        }

        // Pro mode pill (above shutter button)
        AnimatedVisibility(
            visible = showProControls && cameraCapabilities != null,
            enter = fadeIn(spring()) + scaleIn(
                initialScale = 0.8f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy
                )
            ),
            exit = fadeOut(spring()) + scaleOut(targetScale = 0.8f)
        ) {
            cameraCapabilities?.let { caps ->
                ProModePill(
                    visible = true,
                    capabilities = caps,
                    currentSettings = manualSettings,
                    onSettingsChange = onManualSettingsChange,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 140.dp) // Above the shutter button
                )
            }
        }
    }
}

@Composable
private fun TopBar(
    cameraMode: CameraMode,
    hapticsEnabled: Boolean,
    onModeToggle: () -> Unit,
    onProControlsToggle: () -> Unit,
    onFilterToggle: () -> Unit,
    onSettingsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    
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
                onClick = { 
                    if (hapticsEnabled) {
                        HapticFeedback.mediumImpact(view)
                    }
                    onModeToggle() 
                },
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

            // Filter and controls buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Filter button
                GlassButton(
                    onClick = { 
                        if (hapticsEnabled) {
                            HapticFeedback.lightTap(view)
                        }
                        onFilterToggle() 
                    },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Filters",
                        tint = GlassWhite,
                        modifier = Modifier.size(20.dp)
                    )
                }
                
                // Pro controls toggle (only visible in Pro mode)
                if (cameraMode == CameraMode.PRO) {
                    GlassButton(
                        onClick = { 
                            if (hapticsEnabled) {
                                HapticFeedback.lightTap(view)
                            }
                            onProControlsToggle() 
                        },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Controls",
                            tint = GlassWhite,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                
                // Quick Settings button
                GlassButton(
                    onClick = { 
                        if (hapticsEnabled) {
                            HapticFeedback.lightTap(view)
                        }
                        onSettingsClick() 
                    },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Quick Settings",
                        tint = GlassWhite,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun BottomControls(
    galleryThumbnail: androidx.compose.ui.graphics.ImageBitmap?,
    loadingThumbnail: Boolean,
    onCaptureClick: () -> Unit,
    onGalleryClick: () -> Unit,
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
            // Gallery thumbnail
            GalleryThumbnail(
                thumbnail = galleryThumbnail,
                isLoading = loadingThumbnail,
                onClick = onGalleryClick,
                modifier = Modifier
            )

            // Shutter button
            AnimatedShutterButton(
                onClick = onCaptureClick,
                modifier = Modifier.size(80.dp)
            )

            // Flip camera button
            GlassButton(
                onClick = onFlipCamera,
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
