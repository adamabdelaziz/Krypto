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
import org.adam.kryptobot.ui.components.PairInfoCard
import org.adam.kryptobot.ui.components.PaymentStatusCard

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
            modifier = Modifier.fillMaxSize().background(Color.Gray),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
        ) {
            Row(modifier = Modifier.padding(bottom = 8.dp)) {
//                Button(modifier = Modifier.padding(end = 8.dp), onClick = {
//                    onEvent(ScannerScreenEvent.OnTokenAddressSelected("", ""))
//                }, content = {
//                    Text("Start")
//                })
                Button(onClick = {
                    onEvent(ScannerScreenEvent.OnStopSelected)
                }, content = {
                    Text("Stop")
                })
            }

            Row(modifier = Modifier.padding(bottom = 8.dp)) {
                for (category in TokenCategory.entries) {
                    Button(modifier = Modifier.padding(end = 8.dp), onClick = {
                        onEvent(ScannerScreenEvent.OnTokenCategorySelected(category))
                    }, content = {
                        Text(category.toString())
                    })
                }
            }

            Row(modifier = Modifier.fillMaxWidth()) {
                LazyColumn(modifier = Modifier.fillMaxWidth(0.80f)) {
                    items(state.latestDexPairs) { pair ->
                        PairInfoCard(pair = pair, onClick = {
                            onEvent(ScannerScreenEvent.OnTokenAddressSelected(pair))
                        })
                    }
                }
                if (state.currentPaymentStatus.isNotEmpty()) {
                    LazyColumn(modifier = Modifier.fillMaxWidth(0.80f)) {
                        items(state.currentPaymentStatus) { status ->
                            PaymentStatusCard(paymentStatus = status)
                        }
                    }
                }
            }

        }
    }
}