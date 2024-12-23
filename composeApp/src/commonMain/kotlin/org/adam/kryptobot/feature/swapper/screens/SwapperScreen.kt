package org.adam.kryptobot.feature.swapper.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinNavigatorScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.adam.kryptobot.ui.components.PairInfoCard

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
            modifier = Modifier.fillMaxSize().background(Color.Gray).padding(bottom = 64.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
        ) {
            Button(
                modifier = Modifier.padding(bottom = 8.dp),
                onClick = { onEvent(SwapperScreenEvent.OnGenerateDebugWalletClicked) },
                content = {
                    Text("Generate Debug Wallet")
                }
            )
            state.pair?.let {
                PairInfoCard(modifier = Modifier.padding(bottom = 8.dp), pair = it, onClick = {
                    onEvent(SwapperScreenEvent.OnDexPairClicked(it))
                })
            }
            state.quote?.let {
                Button(
                    onClick = { onEvent(SwapperScreenEvent.OnGenerateSwapInstructionsClicked) },
                    content = {
                        Text("Generate Swap Instructions ")
                    }
                )
            }
            if (state.swapResponse != null || state.swapInstructions != null) {
                Button(
                    onClick = { onEvent(SwapperScreenEvent.OnPerformSwapTransactionClicked) },
                    content = {
                        Text("Perform Swap Transaction")
                    }
                )
            }
        }
    }
}