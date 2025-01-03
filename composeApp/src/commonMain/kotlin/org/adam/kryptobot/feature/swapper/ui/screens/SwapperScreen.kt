package org.adam.kryptobot.feature.swapper.ui.screens

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
import org.adam.kryptobot.ui.components.BasicButton
import org.adam.kryptobot.ui.components.PairInfoCard
import org.adam.kryptobot.ui.theme.LocalAppColors

class SwapperScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = navigator.koinNavigatorScreenModel<SwapperScreenModel>()
        val state: SwapperScreenUiState by screenModel.uiState.collectAsState(SwapperScreenUiState())

        SwapperScreenContent(state = state, onEvent = screenModel::onEvent)
    }

    @Composable
    fun SwapperScreenContent(state: SwapperScreenUiState, onEvent: (SwapperScreenEvent) -> Unit) {
        Column(
            modifier = Modifier.fillMaxSize().background(LocalAppColors.current.background)
                .padding(bottom = 64.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
        ) {
            BasicButton(
                modifier = Modifier.padding(bottom = 8.dp),
                onClick = { onEvent(SwapperScreenEvent.OnGenerateDebugWalletClicked) },
                text = "Generate Debug Wallet"
            )
            state.pair?.let {
                PairInfoCard(modifier = Modifier.padding(bottom = 8.dp), pair = it, onClick = {
                    onEvent(SwapperScreenEvent.OnDexPairClicked(it))
                })
            }
            state.quote?.let {
                BasicButton(
                    onClick = { onEvent(SwapperScreenEvent.OnGenerateSwapInstructionsClicked) },
                    text = "Generate Swap Instructions "
                )
            }
            if (state.swapResponse != null || state.swapInstructions != null) {
                BasicButton(
                    onClick = { onEvent(SwapperScreenEvent.OnPerformSwapTransactionClicked) },
                    text = "Perform Swap Transaction"
                )
            }
        }
    }
}