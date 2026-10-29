package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val TrendWaveColorScheme = darkColorScheme(
  primary = NeonViolet,
  onPrimary = TextPrimary,
  primaryContainer = SurfaceCardElevated,
  onPrimaryContainer = ElectricCyan,
  secondary = ElectricCyan,
  onSecondary = MidnightBlack,
  secondaryContainer = SurfaceCardLight,
  onSecondaryContainer = TextPrimary,
  tertiary = HotMagenta,
  onTertiary = TextPrimary,
  background = MidnightBlack,
  onBackground = TextPrimary,
  surface = DarkNavyBg,
  onSurface = TextPrimary,
  surfaceVariant = SurfaceCard,
  onSurfaceVariant = TextSecondary,
  outline = BorderSubtle,
  outlineVariant = SurfaceCardElevated,
)

@Composable
fun TrendWaveTheme(
  darkTheme: Boolean = true,
  content: @Composable () -> Unit,
) {
  val view = LocalView.current
  if (!view.isInEditMode) {
    SideEffect {
      val window = (view.context as? Activity)?.window
      if (window != null) {
        window.statusBarColor = MidnightBlack.toArgb()
        window.navigationBarColor = MidnightBlack.toArgb()
        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
      }
    }
  }

  MaterialTheme(
    colorScheme = TrendWaveColorScheme,
    typography = Typography,
    content = content
  )
}
