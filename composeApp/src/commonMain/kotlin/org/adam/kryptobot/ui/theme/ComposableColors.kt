package org.adam.kryptobot.ui.theme

import androidx.compose.material.ButtonDefaults
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable

val AppOutlinedTextFieldColors
    @Composable
    get() = TextFieldDefaults.outlinedTextFieldColors(
        backgroundColor = LocalAppColors.current.secondary,
        focusedBorderColor = LocalAppColors.current.primary,
        focusedLabelColor = LocalAppColors.current.onPrimary,
    )

val AppButtonColors
    @Composable
    get() = ButtonDefaults.buttonColors(
        backgroundColor = LocalAppColors.current.primary,
        contentColor = LocalAppColors.current.onPrimary
    )