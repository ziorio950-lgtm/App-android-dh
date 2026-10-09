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
  primary = Emerald400,
  onPrimary = Emerald900,
  primaryContainer = Emerald800,
  onPrimaryContainer = Emerald100,
  secondary = GoldPrimary,
  onSecondary = Color.Black,
  secondaryContainer = GoldDark,
  onSecondaryContainer = GoldLight,
  tertiary = Emerald300,
  background = DarkBg,
  onBackground = DarkTextPrimary,
  surface = DarkSurface,
  onSurface = DarkTextPrimary,
  surfaceVariant = DarkSurfaceVariant,
  onSurfaceVariant = DarkTextSecondary,
  outline = DarkDivider,
  error = CoralRed,
  onError = Color.White
)

private val LightColorScheme = lightColorScheme(
  primary = Emerald700,
  onPrimary = Color.White,
  primaryContainer = Emerald100,
  onPrimaryContainer = Emerald900,
  secondary = GoldPrimary,
  onSecondary = Color.White,
  secondaryContainer = GoldLight,
  onSecondaryContainer = GoldDark,
  tertiary = Emerald500,
  background = LightBg,
  onBackground = LightTextPrimary,
  surface = LightSurface,
  onSurface = LightTextPrimary,
  surfaceVariant = LightSurfaceVariant,
  onSurfaceVariant = LightTextSecondary,
  outline = LightDivider,
  error = CoralRed,
  onError = Color.White
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Use our brand colors for cohesive Arabic aesthetic
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

