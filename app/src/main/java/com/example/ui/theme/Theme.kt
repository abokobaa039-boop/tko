package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Professional Polish Theme Color Palette
val CasinoFeltDark = Color(0xFF0E1B16)
val CasinoFeltDeep = Color(0xFF081C15)
val CasinoFeltCenter = Color(0xFF1B4332)
val CasinoHeaderBg = Color(0xFF1A2E26)
val CasinoBorder = Color(0xFF2D453B)
val CasinoDockBg = Color(0xFF0A1410)
val CasinoGreenAccent = Color(0xFF4CAF50)
val CasinoGreenLight = Color(0xFF81C784)
val CasinoGreenMint = Color(0xFFA5D6A7)
val CasinoGreenBadge = Color(0xFF2E7D32)
val CasinoGold = Color(0xFFFACC15)
val CasinoGoldMuted = Color(0xFFEAB308)
val CasinoRed = Color(0xFFDC2626)
val CasinoRedDark = Color(0xFF991B1B)
val CasinoCardBg = Color(0xFFFFFFFF)
val CasinoCardBorder = Color(0xFFD1D5DB)

private val CardGameColorScheme = darkColorScheme(
    primary = CasinoGold,
    onPrimary = Color(0xFF0A1410),
    primaryContainer = CasinoHeaderBg,
    onPrimaryContainer = CasinoGreenMint,
    secondary = CasinoGreenAccent,
    onSecondary = Color.Black,
    secondaryContainer = CasinoGreenBadge,
    onSecondaryContainer = Color.White,
    background = CasinoFeltDark,
    onBackground = Color.White,
    surface = CasinoHeaderBg,
    onSurface = Color.White,
    surfaceVariant = CasinoDockBg,
    onSurfaceVariant = CasinoGreenMint,
    error = CasinoRed,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = CardGameColorScheme,
        typography = Typography,
        content = content
    )
}


