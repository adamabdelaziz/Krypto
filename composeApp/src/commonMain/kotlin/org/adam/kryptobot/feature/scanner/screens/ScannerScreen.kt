package org.adam.kryptobot.feature.scanner.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
        Row(
            modifier = Modifier.fillMaxSize().background(Color.Gray),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.fillMaxWidth(1f)) {
                state.latestTokens.forEach {
                    TextButton(onClick = {
                        onEvent(ScannerScreenEvent.OnTokenAddressSelected(it.chainId, it.tokenAddress))
                    }, content = {
                        Text(it.chainId)
                        Spacer(Modifier.width(8.dp))
                        Text(it.tokenAddress)
                    })
                }
            }
            LazyColumn(modifier = Modifier.fillMaxWidth(2f)) {
                state.latestDexPairs.forEach { entry->
                    item {
                        Text("Token Address: ${entry.key}")
                    }
                    items(entry.value) { value->
                        Text("${value.pairs?.size} Size")
                        Text("${value.pairs?.firstOrNull()?.priceNative}")
                    }
                }
            }

//            Column(modifier = Modifier.fillMaxWidth(1f)) {
//                state.latestBoostedTokens.forEach {
//                    TextButton(onClick = {}, content = {
//                        Text(it.chainId)
//                        Spacer(Modifier.width(8.dp))
//                        Text(it.tokenAddress)
//                    })
//                }
//            }
        }

    }
}