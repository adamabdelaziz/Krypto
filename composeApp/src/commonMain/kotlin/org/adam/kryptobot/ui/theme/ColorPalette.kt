package org.adam.kryptobot.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class AppColors(
    val primary: Color = Color(0xFF6200EE),
    val secondary: Color = Color(0xFF03DAC6),
    val background: Color = Color(0xFFFFFFFF),
    val surface: Color = Color(0xFFFFFFFF),
    val error: Color = Color(0xFFB00020),
    val onPrimary: Color = Color(0xFFFFFFFF),
    val onSecondary: Color = Color(0xFF000000),
    val onBackground: Color = Color(0xFF000000),
    val onSurface: Color = Color(0xFF000000),
    val onError: Color = Color(0xFFFFFFFF),

    // Add your custom colors here
    val warning: Color = Color(0xFFFF9800),
    val success: Color = Color(0xFF4CAF50)
)


val LightColors = AppColors()

val DarkColors = AppColors(
    primary = Color(0xFFBB86FC),
    secondary = Color(0xFF03DAC6),
    background = Color(0xFF121212),
    surface = Color(0xFF121212),
    error = Color(0xFFCF6679),
    onPrimary = Color(0xFF000000),
    onSecondary = Color(0xFF000000),
    onBackground = Color(0xFFFFFFFF),
    onSurface = Color(0xFFFFFFFF),
    onError = Color(0xFF000000)
)

val DarkColorsCute = AppColors(
    primary = PinkBlush,
    secondary = SkyMist,
    background = SlateNight,
    surface = TwilightShadow,
    error = EmberGlow,
    onPrimary = MidnightContrast,
    onSecondary = MidnightContrast,
    onBackground = Moonlight,
    onSurface = Moonlight,
    onError = MidnightContrast,
    warning = CoralSunset,
    success = MintHaven
)
val DarkColorsPumpkin = AppColors(
    primary = PumpkinSpice,
    secondary = BurntSienna,
    background = CharcoalBlack,
    surface = DeepSlate,
    error = EmberGlow,
    onPrimary = Ivory,
    onSecondary = Ivory,
    onBackground = SoftWhite,
    onSurface = SoftWhite,
    onError = Ivory,
    warning = HarvestGold,
    success = ForestGreen
)
val DarkWinterColors = AppColors(
    primary = FrostbiteBlue,
    secondary = IcicleGray,
    background = MidnightBlue,
    surface = DeepIce,
    error = FrozenRed,
    onPrimary = SnowWhite,
    onSecondary = SnowWhite,
    onBackground = SnowWhite,
    onSurface = SnowWhite,
    warning = ArcticOrange,
    success = PineGreen
)
val LocalAppColors = staticCompositionLocalOf { DarkColorsPumpkin }
