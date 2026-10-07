package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
  primary = RoyalMagenta,
  onPrimary = Color.White,
  primaryContainer = RoyalMagentaLight,
  onPrimaryContainer = RoyalMagentaDark,
  secondary = RoyalNavy,
  onSecondary = Color.White,
  secondaryContainer = Color(0xFFE2E8F0),
  onSecondaryContainer = RoyalNavy,
  tertiary = AccentOrange,
  background = GrayBackground,
  onBackground = TextDark,
  surface = SurfaceWhite,
  onSurface = TextDark,
  surfaceVariant = Color(0xFFF1F5F9),
  onSurfaceVariant = TextMuted,
  outline = CardBorder,
  outlineVariant = Color(0xFFE2E8F0),
  error = StatusRed,
  errorContainer = StatusRedLight
)

private val DarkColorScheme = darkColorScheme(
  primary = Color(0xFFEC4899), // Bright Magenta for dark theme
  onPrimary = Color.White,
  primaryContainer = Color(0xFF831843),
  onPrimaryContainer = Color(0xFFFCE7F3),
  secondary = Color(0xFF93C5FD), // Soft Blue
  onSecondary = Color(0xFF0F172A),
  secondaryContainer = Color(0xFF1E293B),
  onSecondaryContainer = Color(0xFFE2E8F0),
  tertiary = Color(0xFFFB923C),
  background = Color(0xFF0F172A), // Slate 900
  onBackground = Color(0xFFF8FAFC),
  surface = Color(0xFF1E293B), // Slate 800
  onSurface = Color(0xFFF8FAFC),
  surfaceVariant = Color(0xFF334155),
  onSurfaceVariant = Color(0xFF94A3B8),
  outline = Color(0xFF475569),
  outlineVariant = Color(0xFF334155),
  error = Color(0xFFF87171),
  errorContainer = Color(0xFF7F1D1D)
)

@Composable
fun RoyalPharmacyTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
