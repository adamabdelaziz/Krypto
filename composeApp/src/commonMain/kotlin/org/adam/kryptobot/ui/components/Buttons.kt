package org.adam.kryptobot.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Checkbox
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.adam.kryptobot.ui.theme.AppButtonColors
import org.adam.kryptobot.ui.theme.AppCheckboxColors
import org.adam.kryptobot.ui.theme.CurrentColors
import org.adam.kryptobot.ui.theme.CurrentShapes
import org.adam.kryptobot.ui.theme.CurrentTypography

@Composable
fun BasicButton(
    modifier: Modifier = Modifier,
    text: String,
    textColor: Color = CurrentColors.onPrimary,
    onClick: () -> Unit,
) {
    Button(
        onClick = { onClick() },
        modifier = modifier,
        shape = CurrentShapes.pill,
        border = BorderStroke(4.dp, CurrentColors.secondary),
        elevation = ButtonDefaults.elevation(16.dp, 24.dp,),
        colors = AppButtonColors
    ) {
        ButtonText(text = text, color = textColor)
    }
}

@Composable
fun ButtonText(
    modifier: Modifier = Modifier,
    text: String,
    color: Color = CurrentColors.onPrimary,
) {
    Text(
        modifier = modifier.padding(8.dp),
        text = text,
        style = CurrentTypography.button,
        color = color
    )
}

@Composable
fun BasicCheckbox(
    text: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .padding(8.dp)
    ) {
        Checkbox(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.padding(end = 8.dp),
            colors = AppCheckboxColors
        )
        Text(text = text)
    }
}