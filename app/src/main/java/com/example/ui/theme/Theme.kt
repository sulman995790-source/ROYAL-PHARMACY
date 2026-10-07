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
  primary = Color(0xFFF472B6), // Vibrant Pink 400
  onPrimary = Color(0xFF500724),
  primaryContainer = Color(0xFF831843), // Rich Magenta 900
  onPrimaryContainer = Color(0xFFFCE7F3),
  secondary = Color(0xFF38BDF8), // Vibrant Sky 400
  onSecondary = Color(0xFF082F49),
  secondaryContainer = Color(0xFF1E293B),
  onSecondaryContainer = Color(0xFFE2E8F0),
  tertiary = Color(0xFFFB923C), // Orange 400
  onTertiary = Color(0xFF431407),
  background = Color(0xFF0F172A), // Slate 900
  onBackground = Color(0xFFF8FAFC), // Slate 50
  surface = Color(0xFF1E293B), // Slate 800
  onSurface = Color(0xFFF8FAFC),
  surfaceVariant = Color(0xFF334155), // Slate 700
  onSurfaceVariant = Color(0xFFCBD5E1), // Slate 300
  outline = Color(0xFF475569), // Slate 600
  outlineVariant = Color(0xFF334155),
  error = Color(0xFFF87171),
  onError = Color(0xFF450A0A),
  errorContainer = Color(0xFF7F1D1D),
  onErrorContainer = Color(0xFFFEE2E2)
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
