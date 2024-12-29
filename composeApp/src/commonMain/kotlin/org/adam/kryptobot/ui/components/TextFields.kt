package org.adam.kryptobot.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextFieldColors
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import compose.icons.FeatherIcons
import compose.icons.feathericons.Eye
import compose.icons.feathericons.EyeOff
import org.adam.kryptobot.ui.theme.AppShapes
import org.adam.kryptobot.ui.theme.LocalAppColors
import org.adam.kryptobot.ui.theme.LocalAppShapes
import org.adam.kryptobot.ui.theme.LocalAppTypography

@Composable
fun ToggleableVisibilityField(
    modifier: Modifier = Modifier,
    text: String,
    onTextChanged: (String) -> Unit,
    isTextVisible: Boolean,
    onVisibilityChanged: (Boolean) -> Unit,
    label: String = "Enter Text"
) {
    OutlinedTextField(
        modifier = modifier,
        colors = TextFieldDefaults.outlinedTextFieldColors(
            backgroundColor = LocalAppColors.current.secondary,
            focusedBorderColor = LocalAppColors.current.primary,
            focusedLabelColor = LocalAppColors.current.onPrimary,
        ),
        shape = LocalAppShapes.current.pill,
        value = text,
        onValueChange = { onTextChanged(it) },
        label = { TextFieldLabel(text = label) },
        visualTransformation = if (isTextVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            val icon = if (isTextVisible) FeatherIcons.EyeOff else FeatherIcons.Eye

            IconButton(onClick = { onVisibilityChanged(!isTextVisible) }) {
                Icon(imageVector = icon, contentDescription = null)
            }
        },
    )
}

@Composable
fun InputTextField(
    modifier: Modifier = Modifier,
    text: String,
    onTextChanged: (String) -> Unit,
    label: String = "Enter Text"
) {
    OutlinedTextField(
        modifier = modifier,
        colors = TextFieldDefaults.outlinedTextFieldColors(
            backgroundColor = LocalAppColors.current.secondary,
            focusedBorderColor = LocalAppColors.current.primary,
            focusedLabelColor = LocalAppColors.current.onPrimary,
        ),
        shape = LocalAppShapes.current.pill,
        value = text,
        onValueChange = { onTextChanged(it) },
        label = { TextFieldLabel(text = label) },
    )
}

@Composable
fun TextFieldLabel(
    modifier: Modifier = Modifier,
    text: String,
) {
    Text(
        modifier = modifier.padding(8.dp).clip(LocalAppShapes.current.pill).background(
            LocalAppColors.current.primary
        ).padding(8.dp),
        text = text,
        style = LocalAppTypography.current.body1,
    )
}