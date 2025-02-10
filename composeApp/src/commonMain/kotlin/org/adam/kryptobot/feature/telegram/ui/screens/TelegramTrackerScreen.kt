package org.adam.kryptobot.feature.telegram.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinNavigatorScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.adam.kryptobot.feature.telegram.ui.FieldEntryLayout
import org.adam.kryptobot.feature.telegram.ui.LoggedInLayout
import org.adam.kryptobot.ui.theme.CurrentColors

class TelegramTrackerScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = navigator.koinNavigatorScreenModel<TelegramTrackerScreenModel>()
        val state: TelegramTrackerScreenUiState by screenModel.uiState.collectAsState(TelegramTrackerScreenUiState())

        TelegramTrackerScreenContent(state = state, onEvent = screenModel::onEvent)
    }

    @Composable
    fun TelegramTrackerScreenContent(
        state: TelegramTrackerScreenUiState,
        onEvent: (TelegramTrackerScreenEvent) -> Unit
    ) {
        Column(
            modifier = Modifier.fillMaxSize().background(CurrentColors.background)
                .padding(bottom = 64.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
        ) {
            state.authStep?.let { step ->
                when (step) {
                    TelegramAuthStep.NEEDS_PHONE_NUMBER -> {
                        FieldEntryLayout(
                            field = state.phoneNumber,
                            title = "Enter Phone Number",
                            onFieldChanged = { TelegramTrackerScreenEvent.OnPhoneNumberEntered(it) },
                            submit = { TelegramTrackerScreenEvent.SubmitPhoneNumber }
                        )
                    }

                    TelegramAuthStep.NEEDS_PASSWORD -> {
                        FieldEntryLayout(
                            field = state.password,
                            title = "Enter Password",
                            onFieldChanged = { TelegramTrackerScreenEvent.OnPasswordEntered(it) },
                            submit = { TelegramTrackerScreenEvent.SubmitPassword }
                        )
                    }

                    TelegramAuthStep.NEEDS_CODE -> {
                        FieldEntryLayout(
                            field = state.twoFactorCode,
                            title = "Enter 2FA Code",
                            onFieldChanged = { TelegramTrackerScreenEvent.OnCodeEntered(it) },
                            submit = { TelegramTrackerScreenEvent.SubmitCode }
                        )
                    }

                    TelegramAuthStep.LOGGED_IN -> {
                        LoggedInLayout(
                            state = state,
                            onEvent = onEvent
                        )
                    }
                }
            }
        }

    }
}