package org.adam.kryptobot.feature.telegram.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinNavigatorScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow

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
        state: TelegramTrackerScreenUiState, onEvent: (TelegramTrackerScreenEvent) -> Unit
    ) {
        // TODO: Implement Content
    }
}