package com.retrocam.presentation.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * Dark color scheme for the glassy, premium theme.
 */
private val DarkColorScheme = darkColorScheme(
    primary = GlassPrimary,
    secondary = GlassSecondary,
    tertiary = GlassTertiary,
    background = BackgroundDark,
    surface = Surface,
    surfaceVariant = SurfaceVariant,
    onPrimary = OnSurface,
    onSecondary = OnSurface,
    onTertiary = OnSurface,
    onBackground = OnSurface,
    onSurface = OnSurface,
    onSurfaceVariant = OnSurfaceVariant,
    error = ErrorColor
)

/**
 * RetroCam theme with glassy, premium aesthetic.
 * Always uses dark theme for the premium camera experience.
 */
@Composable
fun RetroCamTheme(
    darkTheme: Boolean = true, // Always use dark theme
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = android.graphics.Color.TRANSPARENT
            window.navigationBarColor = android.graphics.Color.TRANSPARENT
            WindowCompat.setDecorFitsSystemWindows(window, false)
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = false
                isAppearanceLightNavigationBars = false
            }
        }
    }
    
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
