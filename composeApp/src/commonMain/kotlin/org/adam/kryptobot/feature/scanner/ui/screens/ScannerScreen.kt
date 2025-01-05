package org.adam.kryptobot.feature.scanner.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import org.adam.kryptobot.feature.scanner.enum.Chain
import org.adam.kryptobot.feature.scanner.enum.Dex
import org.adam.kryptobot.feature.scanner.enum.TokenCategory
import org.adam.kryptobot.ui.components.BasicButton
import org.adam.kryptobot.ui.components.BasicCheckbox
import org.adam.kryptobot.ui.views.PairScanCard
import org.adam.kryptobot.ui.theme.LocalAppColors
import org.adam.kryptobot.util.titleCase

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
                BasicButton(
                    modifier = Modifier.padding(end = 8.dp),
                    onClick = { onEvent(ScannerScreenEvent.OnStopSelected) },
                    text = "Stop",
                    enabled = state.isScanRunning
                )
                TokenCategory.entries.forEach { category ->
                    BasicButton(
                        modifier = Modifier.padding(end = 8.dp),
                        onClick = { onEvent(ScannerScreenEvent.OnTokenCategorySelected(category)) },
                        text = category.toString(),
                        selected = category == state.selectedCategory,
                    )
                }
                BasicButton(
                    modifier = Modifier.padding(end = 8.dp),
                    onClick = { onEvent(ScannerScreenEvent.OnTokenCategorySelected(null)) },
                    text = "Tracked",
                    selected = state.selectedCategory == null,
                )
            }

            Row(
                modifier = Modifier.padding(bottom = 8.dp)
                    .background(LocalAppColors.current.primary)
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(modifier = Modifier.padding(bottom = 8.dp)) {
                        Chain.entries.forEach { chain ->
                            BasicCheckbox(
                                text = chain.name.titleCase(),
                                isChecked = state.selectedChainFilters.contains(chain),
                                onCheckedChange = {
                                    onEvent(
                                        ScannerScreenEvent.OnChainFilterToggled(
                                            chain
                                        )
                                    )
                                },
                            )
                        }
                    }
                    Row {
                        BasicButton(
                            modifier = Modifier.padding(end = 8.dp),
                            onClick = { onEvent(ScannerScreenEvent.OnChainFilterToggled(null)) },
                            text = "Clear All"
                        )
                    }
                }
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(modifier = Modifier.padding(bottom = 8.dp)) {
                        Dex.entries.forEach { dex ->
                            BasicCheckbox(
                                text = dex.name.titleCase(),
                                isChecked = state.selectedDexFilters.contains(dex),
                                onCheckedChange = {
                                    onEvent(
                                        ScannerScreenEvent.OnDexFilterToggled(
                                            dex
                                        )
                                    )
                                },
                            )
                        }
                    }
                    Row(modifier = Modifier.padding(bottom = 8.dp)) {
                        BasicButton(
                            modifier = Modifier.padding(end = 8.dp),
                            onClick = { onEvent(ScannerScreenEvent.OnDexFilterToggled(null)) },
                            text = "Clear All"
                        )
                    }
                }
            }

            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(state.latestDexPairs) { pair ->
                    PairScanCard(pair = pair, onClick = {
                        onEvent(ScannerScreenEvent.OnTokenAddressSelected(pair))
                    })
                }
            }
        }


    }
}
