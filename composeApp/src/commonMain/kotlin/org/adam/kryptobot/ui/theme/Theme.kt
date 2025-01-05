package org.adam.kryptobot.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import org.adam.kryptobot.App

@Composable
fun AppTheme(
    colors: AppColors = DarkColorsPumpkin,
    shapes: AppShapes = AppShapes(),
    typography: AppTypography = AppTypography(),
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalAppColors provides colors,
        LocalAppShapes provides shapes,
        LocalAppTypography provides  typography,
    ) {
        content()
    }
}