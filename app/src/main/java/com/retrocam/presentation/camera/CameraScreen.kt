package com.retrocam.presentation.camera

import android.Manifest
import android.view.ViewGroup
import androidx.camera.view.PreviewView
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.retrocam.R
import com.retrocam.domain.model.Filter
import com.retrocam.presentation.components.GlassPanel
import com.retrocam.presentation.theme.AccentBlue
import com.retrocam.presentation.theme.OnSurface

/**
 * Main camera screen with glassy, premium UI.
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraScreen(
    viewModel: CameraViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        if (cameraPermissionState.status.isGranted) {
            CameraContent(
                uiState = uiState,
                onEvent = viewModel::onEvent
            )
        } else {
            PermissionScreen(
                onRequestPermission = { cameraPermissionState.launchPermissionRequest() }
            )
        }
    }
}

@Composable
private fun CameraContent(
    uiState: CameraUiState,
    onEvent: (CameraEvent) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraManager = remember { 
        com.retrocam.presentation.camera.CameraManager(context)
    }
    
    Box(modifier = Modifier.fillMaxSize()) {
        // Camera Preview
        AndroidView(
            factory = { ctx ->
                PreviewView(ctx).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    scaleType = PreviewView.ScaleType.FILL_CENTER
                }
            },
            update = { previewView ->
                kotlinx.coroutines.GlobalScope.launch {
                    cameraManager.startCamera(
                        lifecycleOwner = lifecycleOwner,
                        previewView = previewView,
                        cameraSettings = uiState.cameraSettings
                    )
                }
            },
            modifier = Modifier.fillMaxSize()
        )
        
        // Top Controls
        TopControls(
            mode = uiState.mode,
            onModeSwitch = { onEvent(CameraEvent.SwitchMode(it)) },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
        )
        
        // Bottom Controls
        BottomControls(
            currentFilter = uiState.currentFilter,
            isCapturing = uiState.isCapturing,
            onCaptureClick = { onEvent(CameraEvent.CapturePhoto) },
            onFilterClick = { onEvent(CameraEvent.ToggleFilterSheet) },
            onPresetClick = { onEvent(CameraEvent.TogglePresetSheet) },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
        )
        
        // Filter Sheet
        if (uiState.showFilterSheet) {
            FilterSheet(
                currentFilter = uiState.currentFilter,
                onFilterSelect = { onEvent(CameraEvent.SelectFilter(it)) },
                onDismiss = { onEvent(CameraEvent.ToggleFilterSheet) }
            )
        }
    }
}

@Composable
private fun TopControls(
    mode: com.retrocam.domain.model.CameraMode,
    onModeSwitch: (com.retrocam.domain.model.CameraMode) -> Unit,
    modifier: Modifier = Modifier
) {
    GlassPanel(
        modifier = modifier
            .padding(16.dp)
            .height(48.dp),
        cornerRadius = 24.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ModeButton(
                text = stringResource(R.string.normal_mode),
                isSelected = mode == com.retrocam.domain.model.CameraMode.NORMAL,
                onClick = { onModeSwitch(com.retrocam.domain.model.CameraMode.NORMAL) }
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            ModeButton(
                text = stringResource(R.string.pro_mode),
                isSelected = mode == com.retrocam.domain.model.CameraMode.PRO,
                onClick = { onModeSwitch(com.retrocam.domain.model.CameraMode.PRO) }
            )
        }
    }
}

@Composable
private fun ModeButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) AccentBlue else Color.Transparent,
        animationSpec = tween(300),
        label = "modeButtonBackground"
    )
    
    Box(
        modifier = modifier
            .height(40.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(backgroundColor)
            .clickable(
                onClick = onClick,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            )
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = OnSurface
        )
    }
}

@Composable
private fun BottomControls(
    currentFilter: Filter,
    isCapturing: Boolean,
    onCaptureClick: () -> Unit,
    onFilterClick: () -> Unit,
    onPresetClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Filter name display
        if (currentFilter.id != "none") {
            GlassPanel(
                modifier = Modifier.padding(bottom = 16.dp),
                cornerRadius = 12.dp
            ) {
                Text(
                    text = currentFilter.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnSurface,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Filter Button
            IconButton(
                onClick = onFilterClick,
                modifier = Modifier.size(48.dp)
            ) {
                GlassPanel(
                    modifier = Modifier.fillMaxSize(),
                    cornerRadius = 24.dp
                ) {
                    Icon(
                        imageVector = Icons.Default.FilterVintage,
                        contentDescription = stringResource(R.string.filters),
                        tint = OnSurface,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp)
                    )
                }
            }
            
            // Shutter Button
            ShutterButton(
                isCapturing = isCapturing,
                onClick = onCaptureClick
            )
            
            // Preset Button
            IconButton(
                onClick = onPresetClick,
                modifier = Modifier.size(48.dp)
            ) {
                GlassPanel(
                    modifier = Modifier.fillMaxSize(),
                    cornerRadius = 24.dp
                ) {
                    Icon(
                        imageVector = Icons.Default.Bookmark,
                        contentDescription = stringResource(R.string.presets),
                        tint = OnSurface,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ShutterButton(
    isCapturing: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (isCapturing) 0.9f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "shutterButtonScale"
    )
    
    Box(
        modifier = modifier
            .size(80.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0x40FFFFFF),
                        Color(0x10FFFFFF)
                    )
                )
            )
            .border(3.dp, Color(0x66FFFFFF), CircleShape)
            .clickable(
                onClick = onClick,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(Color.White)
        )
    }
}

@Composable
private fun FilterSheet(
    currentFilter: Filter,
    onFilterSelect: (Filter) -> Unit,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x80000000))
            .clickable(
                onClick = onDismiss,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            )
    ) {
        GlassPanel(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .heightIn(min = 200.dp, max = 400.dp)
                .padding(16.dp)
                .clickable(
                    onClick = {},
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ),
            cornerRadius = 24.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text(
                    text = stringResource(R.string.filters),
                    style = MaterialTheme.typography.titleLarge,
                    color = OnSurface,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                Filter.getAllFilters().forEach { filter ->
                    FilterItem(
                        filter = filter,
                        isSelected = filter.id == currentFilter.id,
                        onClick = { onFilterSelect(filter) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun FilterItem(
    filter: Filter,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) AccentBlue.copy(alpha = 0.3f) else Color.Transparent,
        label = "filterItemBackground"
    )
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = filter.name,
            style = MaterialTheme.typography.bodyLarge,
            color = OnSurface,
            modifier = Modifier.weight(1f)
        )
        
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = AccentBlue,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun PermissionScreen(
    onRequestPermission: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.CameraAlt,
                contentDescription = null,
                tint = OnSurface,
                modifier = Modifier
                    .size(64.dp)
                    .padding(bottom = 16.dp)
            )
            
            Text(
                text = stringResource(R.string.camera_permission_required),
                style = MaterialTheme.typography.bodyLarge,
                color = OnSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            
            Button(
                onClick = onRequestPermission,
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentBlue
                )
            ) {
                Text(
                    text = stringResource(R.string.grant_permission),
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}
