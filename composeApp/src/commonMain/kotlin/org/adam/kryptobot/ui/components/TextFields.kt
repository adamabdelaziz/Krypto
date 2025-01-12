package org.adam.kryptobot.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import compose.icons.FeatherIcons
import compose.icons.feathericons.Eye
import compose.icons.feathericons.EyeOff
import org.adam.kryptobot.ui.theme.AppOutlinedTextFieldColors
import org.adam.kryptobot.ui.theme.CurrentColors
import org.adam.kryptobot.ui.theme.CurrentShapes
import org.adam.kryptobot.ui.theme.CurrentTypography

@Composable
fun PasswordTextField(
    modifier: Modifier = Modifier,
    text: String,
    onTextChanged: (String) -> Unit,
    isTextVisible: Boolean,
    onVisibilityChanged: (Boolean) -> Unit,
    label: String = "Enter Text"
) {
    OutlinedTextField(
        modifier = modifier,
        colors = AppOutlinedTextFieldColors,
        shape = CurrentShapes.pill,
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
    label: String = "Enter Text",
    isError: Boolean = false,
) {
    val textFieldState = remember { mutableStateOf(TextFieldValue(text)) }

    OutlinedTextField(
        modifier = modifier,
        colors = AppOutlinedTextFieldColors,
        shape = CurrentShapes.pill,
        value = textFieldState.value,
        onValueChange = {
            textFieldState.value = it
            onTextChanged(it.text)
        },
        label = { TextFieldLabel(text = label) },
        isError = isError,
    )
}

@Composable
fun InputTextFieldImproved(
    modifier: Modifier = Modifier,
    text: String,
    onTextChanged: (String) -> Unit,
    label: String = "Enter Text",
    isError: Boolean = false,
) {
    OutlinedTextField(
        modifier = modifier,
        colors = AppOutlinedTextFieldColors,
        shape = CurrentShapes.pill,
        value = text,
        onValueChange = {
            onTextChanged(it)
        },
        label = { TextFieldLabel(text = label) },
        isError = isError,
    )
}

@Composable
fun ValidatedTextField(
    modifier: Modifier = Modifier,
    text: String,
    onTextChanged: (String) -> Unit,
    label: String = "Enter Text",
    isError: Boolean = false,
    validate: (String) -> Boolean = { it.toIntOrNull() != null || it.isEmpty() }
) {
    OutlinedTextField(
        modifier = modifier,
        colors = AppOutlinedTextFieldColors,
        shape = CurrentShapes.pill,
        value = text,
        onValueChange = { updatedText ->
            if (validate(updatedText)) {
                onTextChanged(updatedText)
            }
        },
        label = { TextFieldLabel(text = label) },
        isError = isError,
    )
}

@Composable
fun TextFieldLabel(
    modifier: Modifier = Modifier,
    text: String,
) {
    Text(
        modifier = modifier.padding(8.dp).clip(CurrentShapes.pill).background(
            CurrentColors.primary
        ).padding(8.dp),
        text = text,
        style = CurrentTypography.body1,
    )
}

@Composable
fun BasicText(
    modifier: Modifier = Modifier,
    text: String,
    color: Color = CurrentColors.onPrimary,
    style: TextStyle = CurrentTypography.body1,
) {
    Text(
        modifier = modifier,
        text = text,
        style = style,
        color = color,
    )
}