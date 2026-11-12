package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
  primary = PrimaryGreen,
  onPrimary = Color.Black,
  primaryContainer = Color(0xFF003915),
  onPrimaryContainer = PrimaryGreenLight,
  secondary = CyanAccent,
  onSecondary = Color.Black,
  secondaryContainer = Color(0xFF004D5A),
  onSecondaryContainer = Color(0xFFA6EEFF),
  tertiary = PurpleBrand,
  onTertiary = Color.White,
  background = DarkBackground,
  onBackground = DarkTextPrimary,
  surface = DarkSurface,
  onSurface = DarkTextPrimary,
  surfaceVariant = DarkSurfaceVariant,
  onSurfaceVariant = DarkTextSecondary,
  error = CoralSale,
  onError = Color.White
)

private val LightColorScheme = lightColorScheme(
  primary = Color(0xFF00873B),
  onPrimary = Color.White,
  primaryContainer = Color(0xFFB9F6CA),
  onPrimaryContainer = Color(0xFF002108),
  secondary = Color(0xFF00677C),
  onSecondary = Color.White,
  secondaryContainer = Color(0xFFBAEBF8),
  onSecondaryContainer = Color(0xFF001F26),
  tertiary = PurpleBrand,
  onTertiary = Color.White,
  background = LightBackground,
  onBackground = LightTextPrimary,
  surface = LightSurface,
  onSurface = LightTextPrimary,
  surfaceVariant = LightSurfaceVariant,
  onSurfaceVariant = LightTextSecondary,
  error = CoralSale,
  onError = Color.White
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Prefer our custom fintech palette for branding coherence
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme = when {
    dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
      val context = LocalContext.current
      if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    }
    darkTheme -> DarkColorScheme
    else -> LightColorScheme
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
