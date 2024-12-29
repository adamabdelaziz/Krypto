package org.adam.kryptobot.feature.wallet.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinNavigatorScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow

class WalletScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = navigator.koinNavigatorScreenModel<WalletScreenModel>()
        val state: WalletScreenUiState by screenModel.uiState.collectAsState(WalletScreenUiState())

        WalletScreenContent(state = state, onEvent = screenModel::onEvent)
    }

    @Composable
    fun WalletScreenContent(
        state: WalletScreenUiState, onEvent: (WalletScreenEvent) -> Unit
    ) {

    }
}