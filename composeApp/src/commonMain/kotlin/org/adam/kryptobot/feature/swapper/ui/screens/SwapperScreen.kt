package org.adam.kryptobot.feature.swapper.ui.screens

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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinNavigatorScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.adam.kryptobot.feature.scanner.enum.Dex
import org.adam.kryptobot.feature.swapper.enum.SwapMode
import org.adam.kryptobot.ui.components.BasicButton
import org.adam.kryptobot.ui.components.BasicCheckbox
import org.adam.kryptobot.ui.components.BasicText
import org.adam.kryptobot.ui.components.BasicCard
import org.adam.kryptobot.ui.components.CenteredRow
import org.adam.kryptobot.ui.components.InputTextField
import org.adam.kryptobot.ui.components.ValidatedTextField
import org.adam.kryptobot.ui.views.PairSwapCard
import org.adam.kryptobot.ui.theme.CurrentColors
import org.adam.kryptobot.ui.views.TransactionView
import org.adam.kryptobot.util.toStringOrEmpty

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
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(state.pair) {
                    PairSwapCard(
                        modifier = Modifier.padding(bottom = 8.dp),
                        selected = it == state.selectedPair,
                        pair = it,
                        onClick = {
                            onEvent(SwapperScreenEvent.OnDexPairClicked(it))
                        }
                    )
                }
            }
            Column(modifier = Modifier.weight(2f)) {
                /*
                   Separate composables for quote config, buy/sell params, etc.
                 */
                BasicCard {
                    CenteredRow {
                        ValidatedTextField(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            text = state.quoteParams.amount.toStringOrEmpty(),
                            onTextChanged = { onEvent(SwapperScreenEvent.UpdateAmount(it.toDoubleOrNull() ?: 0.0)) },
                            label = "Enter Amount",
                            isError = state.quoteParams.amount == 0.0,
                            validate = { it.toDoubleOrNull() != null }
                        )
                        ValidatedTextField(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            text = state.quoteParams.slippageBps.toStringOrEmpty(),
                            onTextChanged = { onEvent(SwapperScreenEvent.UpdateSlippageBps(it.toIntOrNull() ?: 0)) },
                            label = "Enter Slippage",
                            isError = state.quoteParams.slippageBps == 0,
                            validate = { it.toIntOrNull() != null }
                        )
                        BasicText(
                            modifier = Modifier.padding(horizontal = 8.dp),
                            text = "Swap Mode:",
                        )
                        BasicCheckbox(
                            text = SwapMode.ExactOut.name,
                            isChecked = state.quoteParams.swapMode == SwapMode.ExactOut,
                            onCheckedChange = { onEvent(SwapperScreenEvent.UpdateSwapMode) },
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        BasicCheckbox(
                            text = SwapMode.ExactIn.name,
                            isChecked = state.quoteParams.swapMode == SwapMode.ExactIn,
                            onCheckedChange = { onEvent(SwapperScreenEvent.UpdateSwapMode) },
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                }

                BasicCard {
                    Row {
                        Column {
                            CenteredRow {
                                BasicText(
                                    modifier = Modifier.padding(end = 8.dp),
                                    text = "Include Dexes:",
                                )

                                Dex.entries.forEach { dex ->
                                    BasicCheckbox(
                                        text = dex.name,
                                        isChecked = dex in state.quoteParams.dexes,
                                        onCheckedChange = { onEvent(SwapperScreenEvent.UpdateDexes(dex)) },
                                        modifier = Modifier.padding(end = 8.dp)
                                    )
                                }
                            }

                            CenteredRow {
                                BasicText(
                                    modifier = Modifier.padding(end = 8.dp),
                                    text = "Exclude Dexes:",
                                )
                                Dex.entries.forEach { dex ->
                                    BasicCheckbox(
                                        text = dex.name,
                                        isChecked = dex in state.quoteParams.excludeDexes,
                                        onCheckedChange = { onEvent(SwapperScreenEvent.UpdateExcludeDexes(dex)) },
                                        modifier = Modifier.padding(end = 8.dp)
                                    )
                                }
                            }

                            CenteredRow {
                                BasicCheckbox(
                                    text = "As Legacy Transaction",
                                    isChecked = state.quoteParams.asLegacyTransaction,
                                    onCheckedChange = { onEvent(SwapperScreenEvent.UpdateAsLegacyTransaction(it)) },
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                                BasicCheckbox(
                                    text = "Auto Slippage",
                                    isChecked = state.quoteParams.autoSlippage,
                                    onCheckedChange = { onEvent(SwapperScreenEvent.UpdateAutoSlippage(it)) },
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                                BasicCheckbox(
                                    text = "Restrict Intermediate Tokens",
                                    isChecked = state.quoteParams.restrictIntermediateTokens,
                                    onCheckedChange = { onEvent(SwapperScreenEvent.UpdateRestrictIntermediateTokens(it)) },
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                                BasicCheckbox(
                                    text = "Only Direct Routes",
                                    isChecked = state.quoteParams.onlyDirectRoutes,
                                    onCheckedChange = { onEvent(SwapperScreenEvent.UpdateOnlyDirectRoutes(it)) },
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                            }
                        }
                    }


                }

                BasicCard {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        CenteredRow(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                            ValidatedTextField(
                                modifier = Modifier.weight(1f).padding(horizontal = 16.dp),
                                text = state.quoteParams.platformFeeBps.toStringOrEmpty(),
                                onTextChanged = { onEvent(SwapperScreenEvent.UpdatePlatformFeeBps(it.toIntOrNull())) },
                                label = "Platform Fee Bps",

                                )
                            ValidatedTextField(
                                modifier = Modifier.weight(1f).padding(horizontal = 16.dp),
                                text = state.quoteParams.maxAccounts.toStringOrEmpty(),
                                onTextChanged = { onEvent(SwapperScreenEvent.UpdateMaxAccounts(it.toIntOrNull())) },
                                label = "Max Accounts",

                                )
                        }

                        CenteredRow(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                            ValidatedTextField(
                                modifier = Modifier.weight(1f).padding(horizontal = 16.dp),
                                text = state.quoteParams.maxAutoSlippageBps.toStringOrEmpty(),
                                onTextChanged = { onEvent(SwapperScreenEvent.UpdateMaxAutoSlippageBps(it.toIntOrNull())) },
                                label = "Max Auto Slippage Bps",

                                )
                            ValidatedTextField(
                                modifier = Modifier.weight(1f).padding(horizontal = 16.dp),
                                text = state.quoteParams.autoSlippageCollisionUsdValue.toStringOrEmpty(),
                                onTextChanged = { onEvent(SwapperScreenEvent.UpdateAutoSlippageCollisionUsdValue(it.toIntOrNull())) },
                                label = "Auto Slippage Collision USD Value",

                                )
                        }
                    }
                }
                BasicCard {
                    CenteredRow(modifier = Modifier.padding(8.dp), horizontalArrangement = Arrangement.Center) {
                        BasicButton(
                            enabled = state.quoteParams.amount > 0.0 && state.selectedPair != null,
                            text = "Get Quote",
                            onClick = { onEvent(SwapperScreenEvent.OnGetQuoteClicked) },
                        )
                    }
                }
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    /*
                    TransactionStep UI here
                     */
                    items(state.selectedTransactionSteps) { item ->
                        TransactionView(modifier = Modifier.padding(vertical = 2.dp), transaction = item, onClick = {})
                    }
                }

            }

        }
    }

}