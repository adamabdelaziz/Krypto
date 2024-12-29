package org.adam.kryptobot.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ButtonElevation
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.adam.kryptobot.ui.theme.LocalAppColors
import org.adam.kryptobot.ui.theme.LocalAppShapes
import org.adam.kryptobot.ui.theme.LocalAppTypography

@Composable
fun BasicButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit,
) {
    Button(
        onClick = { onClick() },
        modifier = modifier,
        shape = LocalAppShapes.current.pill,
        border = BorderStroke(4.dp, LocalAppColors.current.secondary),
        elevation = ButtonDefaults.elevation(16.dp, 24.dp,),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = LocalAppColors.current.primary,
            contentColor = LocalAppColors.current.onPrimary
        )
    ) {
        ButtonText(text = text)
    }
}

@Composable
fun ButtonText(
    modifier: Modifier = Modifier,
    text: String,
    color: Color = LocalAppColors.current.onPrimary,
) {
    Text(
        modifier = modifier.padding(8.dp),
        text = text,
        style = LocalAppTypography.current.button,
        color = color
    )
}