package org.adam.kryptobot.feature.telegram.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.adam.kryptobot.feature.telegram.ui.screens.TelegramTrackerScreenEvent
import org.adam.kryptobot.feature.telegram.ui.screens.TelegramTrackerScreenUiState
import org.adam.kryptobot.ui.components.BasicButton
import org.adam.kryptobot.ui.components.InputTextFieldImproved
import org.adam.kryptobot.ui.theme.CurrentColors
import org.adam.kryptobot.ui.theme.CurrentTypography

@Composable
fun FieldEntryLayout(
    modifier: Modifier = Modifier,
    title: String,
    field: String,
    onFieldChanged: (String) -> Unit,
    submit: () -> Unit,
) {
    Text(title, modifier = Modifier.padding(bottom = 8.dp), style = CurrentTypography.h1, color = CurrentColors.onPrimary)
    InputTextFieldImproved(
        modifier = Modifier.padding(bottom = 8.dp),
        text = field,
        onTextChanged = onFieldChanged,
        label = "Enter value",
        isError = false
    )
    BasicButton(
        text = "Enter",
        onClick = submit,
    )
}

@Composable
fun LoggedInLayout(
    state: TelegramTrackerScreenUiState,
    onEvent: (TelegramTrackerScreenEvent) -> Unit
) {

}