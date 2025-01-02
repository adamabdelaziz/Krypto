package org.adam.kryptobot.feature.scanner.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextButton
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
import org.adam.kryptobot.feature.scanner.enum.TokenCategory
import org.adam.kryptobot.ui.components.BasicButton
import org.adam.kryptobot.ui.components.PairInfoCard
import org.adam.kryptobot.ui.components.PaymentStatusCard
import org.adam.kryptobot.ui.theme.LocalAppColors

class ScannerScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = navigator.koinNavigatorScreenModel<ScannerScreenModel>()
        val state: ScannerScreenUiState by screenModel.uiState.collectAsState(ScannerScreenUiState())

        ScannerScreenContent(state = state, onEvent = screenModel::onEvent)
    }

    @Composable
    fun ScannerScreenContent(state: ScannerScreenUiState, onEvent: (ScannerScreenEvent) -> Unit) {
        Column(
            modifier = Modifier.fillMaxSize().background(LocalAppColors.current.background)
                .padding(bottom = 64.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
        ) {
            Row(modifier = Modifier.padding(bottom = 8.dp)) {
                BasicButton(onClick = { onEvent(ScannerScreenEvent.OnStopSelected) }, text = "Stop")
            }

            Row(modifier = Modifier.padding(bottom = 8.dp)) {
                for (category in TokenCategory.entries) {
                    BasicButton(
                        modifier = Modifier.padding(end = 8.dp),
                        onClick = { onEvent(ScannerScreenEvent.OnTokenCategorySelected(category)) },
                        text = category.toString()
                    )
                }
                BasicButton(
                    modifier = Modifier.padding(end = 8.dp),
                    onClick = { onEvent(ScannerScreenEvent.OnTokenCategorySelected(null)) },
                    text = "Tracked"
                )
            }

            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(state.latestDexPairs) { pair ->
                    PairInfoCard(pair = pair, onClick = {
                        onEvent(ScannerScreenEvent.OnTokenAddressSelected(pair))
                    })
                }
            }
        }


    }
}
