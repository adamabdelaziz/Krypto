package org.adam.kryptobot.feature.swapper.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinNavigatorScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.adam.kryptobot.ui.components.BasicButton
import org.adam.kryptobot.ui.components.PairSwapCard
import org.adam.kryptobot.ui.theme.CurrentColors

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
        Row(
            modifier = Modifier.fillMaxSize().background(CurrentColors.background)
                .padding(bottom = 64.dp),
        ) {
            LazyColumn (modifier = Modifier.weight(1f)) {
                items(state.pair) {
                    PairSwapCard(modifier = Modifier.padding(bottom = 8.dp), pair = it, onClick = {
                        onEvent(SwapperScreenEvent.OnDexPairClicked(it))
                    })
                }
            }
            LazyColumn(modifier = Modifier.weight(2f))  {
                /*
                    Separate composables for quote config, buy/sell params, etc.
                 */
            }

        }
    }
}