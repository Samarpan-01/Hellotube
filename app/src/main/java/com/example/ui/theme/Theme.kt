package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val HelloDarkColorScheme = darkColorScheme(
  primary = WalkmanGoldPrimary,
  onPrimary = ObsidianBackground,
  primaryContainer = WalkmanGoldDark,
  onPrimaryContainer = WalkmanGoldLight,
  secondary = VUMeterCyan,
  onSecondary = ObsidianBackground,
  secondaryContainer = ObsidianSurfaceElevated,
  onSecondaryContainer = VUMeterCyan,
  tertiary = TubeAmber,
  onTertiary = ObsidianBackground,
  background = ObsidianBackground,
  onBackground = PureWhite,
  surface = ObsidianSurface,
  onSurface = PureWhite,
  surfaceVariant = ObsidianSurfaceElevated,
  onSurfaceVariant = TextMuted,
  outline = ObsidianSurfaceBorder
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true, // Audiophile theme defaults to premium dark
  dynamicColor: Boolean = false, // Keep signature Sony Walkman gold & obsidian aesthetic
  content: @Composable () -> Unit,
) {
  MaterialTheme(
    colorScheme = HelloDarkColorScheme,
    typography = Typography,
    content = content
  )
}
