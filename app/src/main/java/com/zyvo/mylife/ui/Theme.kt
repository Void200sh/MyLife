package com.zyvo.mylife.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.zyvo.mylife.data.AppThemeId

data class Palette(
    val bg: Color,
    val card: Color,
    val ink: Color,
    val ink2: Color,
    val line: Color,
    val accent: Color,
    val accentSoft: Color,
    val amber: Color,
    val blue: Color,
    val purple: Color,
    val dark: Boolean,
    val display: FontFamily
)

val AppThemeId.palette: Palette
    get() = when (this) {
        AppThemeId.SAGE -> Palette(
            bg = Color(0xFFFDF7EF),
            card = Color(0xFFFFFFFF),
            ink = Color(0xFF22201C),
            ink2 = Color(0xFF6E6A62),
            line = Color(0xFFE9E1D2),
            accent = Color(0xFF5E8B6A),
            accentSoft = Color(0xFFE4EFE3),
            amber = Color(0xFFE8A13C),
            blue = Color(0xFF5B7FA6),
            purple = Color(0xFF9B7BB8),
            dark = false,
            display = FontFamily.Serif
        )
        AppThemeId.PAPER -> Palette(
            bg = Color(0xFFF2EDE3),
            card = Color(0xFFFBF7EE),
            ink = Color(0xFF2B2117),
            ink2 = Color(0xFF7A6A58),
            line = Color(0xFFE0D5C2),
            accent = Color(0xFFA64B2A),
            accentSoft = Color(0xFFF0DFD3),
            amber = Color(0xFFB8862F),
            blue = Color(0xFF5E7285),
            purple = Color(0xFF8A6A88),
            dark = false,
            display = FontFamily.Serif
        )
        AppThemeId.PORCELAIN -> Palette(
            bg = Color(0xFFECEDE8),
            card = Color(0xFFF8F9F5),
            ink = Color(0xFF232722),
            ink2 = Color(0xFF6E746D),
            line = Color(0xFFD8DBD3),
            accent = Color(0xFF5E7285),
            accentSoft = Color(0xFFDFE6EA),
            amber = Color(0xFFC08A3E),
            blue = Color(0xFF5E7285),
            purple = Color(0xFF7C6F8F),
            dark = false,
            display = FontFamily.SansSerif
        )
        AppThemeId.CHROME -> Palette(
            bg = Color(0xFF08090C),
            card = Color(0xFF101318),
            ink = Color(0xFFF2F4F8),
            ink2 = Color(0xFF9AA1AD),
            line = Color(0xFF1E232B),
            accent = Color(0xFF4D8DFF),
            accentSoft = Color(0xFF12213A),
            amber = Color(0xFFFFB74D),
            blue = Color(0xFF4D8DFF),
            purple = Color(0xFF9F7AEA),
            dark = true,
            display = FontFamily.SansSerif
        )
        AppThemeId.TACTICAL -> Palette(
            bg = Color(0xFF0C0E0A),
            card = Color(0xFF13160F),
            ink = Color(0xFFE8EDDA),
            ink2 = Color(0xFF8E9A7C),
            line = Color(0xFF23291C),
            accent = Color(0xFFFFB000),
            accentSoft = Color(0xFF2A2205),
            amber = Color(0xFFFFB000),
            blue = Color(0xFF7FA650),
            purple = Color(0xFFA3B57A),
            dark = true,
            display = FontFamily.Monospace
        )
    }

val LocalPalette = staticCompositionLocalOf { AppThemeId.SAGE.palette }

@Composable
fun MyLifeTheme(theme: AppThemeId, content: @Composable () -> Unit) {
    val palette = theme.palette
    val scheme = if (palette.dark) {
        darkColorScheme(
            primary = palette.accent,
            onPrimary = palette.bg,
            secondary = palette.purple,
            tertiary = palette.amber,
            background = palette.bg,
            onBackground = palette.ink,
            surface = palette.card,
            onSurface = palette.ink,
            surfaceVariant = palette.card,
            onSurfaceVariant = palette.ink2,
            outline = palette.line,
            error = Color(0xFFFF6B6B)
        )
    } else {
        lightColorScheme(
            primary = palette.accent,
            onPrimary = Color.White,
            secondary = palette.purple,
            tertiary = palette.amber,
            background = palette.bg,
            onBackground = palette.ink,
            surface = palette.card,
            onSurface = palette.ink,
            surfaceVariant = palette.card,
            onSurfaceVariant = palette.ink2,
            outline = palette.line,
            error = Color(0xFFB3261E)
        )
    }
    val typography = Typography(
        displayLarge = TextStyle(fontFamily = palette.display, fontWeight = FontWeight.Bold, fontSize = 40.sp, lineHeight = 44.sp, letterSpacing = (-1).sp),
        headlineLarge = TextStyle(fontFamily = palette.display, fontWeight = FontWeight.Bold, fontSize = 30.sp, lineHeight = 34.sp, letterSpacing = (-0.5).sp),
        headlineMedium = TextStyle(fontFamily = palette.display, fontWeight = FontWeight.Bold, fontSize = 24.sp, lineHeight = 28.sp),
        titleLarge = TextStyle(fontFamily = palette.display, fontWeight = FontWeight.Bold, fontSize = 20.sp, lineHeight = 24.sp),
        titleMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold, fontSize = 14.sp, lineHeight = 18.sp),
        bodyLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 14.sp, lineHeight = 21.sp),
        bodyMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 12.5.sp, lineHeight = 18.sp),
        bodySmall = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 11.sp, lineHeight = 16.sp),
        labelSmall = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold, fontSize = 10.sp, letterSpacing = 1.2.sp)
    )
    CompositionLocalProvider(LocalPalette provides palette) {
        MaterialTheme(colorScheme = scheme, typography = typography, content = content)
    }
}
