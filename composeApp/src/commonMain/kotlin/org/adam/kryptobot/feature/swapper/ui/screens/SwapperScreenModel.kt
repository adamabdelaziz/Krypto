package org.adam.kryptobot.feature.swapper.ui.screens

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.lifecycle.NavigatorDisposable
import co.touchlab.kermit.Logger
import com.zhuinden.flowcombinetuplekt.combineStates
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.adam.kryptobot.feature.scanner.data.mappers.toDexPairSwapUiModel
import org.adam.kryptobot.feature.scanner.repository.ScannerRepository
import org.adam.kryptobot.feature.scanner.usecase.MonitorTokenAddressesUseCase
import org.adam.kryptobot.feature.swapper.enum.SwapMode
import org.adam.kryptobot.feature.swapper.repository.SwapperRepository
import org.adam.kryptobot.feature.swapper.ui.model.DexPairSwapUiModel
import org.adam.kryptobot.feature.wallet.repository.WalletRepository
import java.math.BigDecimal
import javax.swing.plaf.nimbus.State

class SwapperScreenModel(
    private val swapperRepository: SwapperRepository,
    private val scannerRepository: ScannerRepository,
    private val walletRepository: WalletRepository,
    private val monitorTokenAddressesUseCase: MonitorTokenAddressesUseCase,
) : ScreenModel, ScannerRepository by scannerRepository, SwapperRepository by swapperRepository {

    private val _selectedDexKey = MutableStateFlow<String?>(null)
    private val selectedDexPair: StateFlow<DexPairSwapUiModel?> = combine(
        _selectedDexKey,
        latestDexPairs
    ) { key, pairs ->
        val pair = pairs.firstOrNull { it.pairAddress == key }
        pair?.toDexPairSwapUiModel()
    }.stateIn(
        scope = screenModelScope,
        initialValue = null,
        started = SharingStarted.WhileSubscribed(5000),
    )

    val uiState: StateFlow<SwapperScreenUiState> = combineStates(
        screenModelScope,
        SharingStarted.WhileSubscribed(),
        latestDexPairs,
        trackedTokenAddresses,
        selectedDexPair,
        quoteConfig,
        currentSwaps,
        ::mapSwapScreenUiState
    )

    fun onScreenEnter() {
        monitorTokenAddressesUseCase(null, MonitorTokenAddressesUseCase.SWAP_SCAN_DELAY)
    }

    fun onEvent(event: SwapperScreenEvent) {
        when (event) {
            is SwapperScreenEvent.OnDexPairClicked -> {
                _selectedDexKey.value = event.dexPair.key
            }

            is SwapperScreenEvent.OnGetQuoteClicked -> {
                selectedDexPair.value?.let {
                    getQuote(it)
                }
            }

            is SwapperScreenEvent.UpdateAmount -> {
                event.amount?.let {
                    updateQuoteConfig { copy(amount = event.amount) }
                }
            }

            is SwapperScreenEvent.UpdateSlippageBps -> {
                event.slippageBps?.let {
                    updateQuoteConfig { copy(slippageBps = event.slippageBps) }
                }
            }

            is SwapperScreenEvent.UpdateSwapMode -> {
                val newSwapMode = when (quoteConfig.value.swapMode) {
                    SwapMode.ExactIn -> {
                        SwapMode.ExactOut
                    }

                    SwapMode.ExactOut -> {
                        SwapMode.ExactIn
                    }
                }
                updateQuoteConfig { copy(swapMode = newSwapMode) }
            }


            is SwapperScreenEvent.UpdateDexes -> {
                val newConfig = updateDexSelection(
                    dex = event.dex,
                    addToDexes = true,
                    currentConfig = quoteConfig.value
                )
                updateQuoteConfig { newConfig }
            }

            is SwapperScreenEvent.UpdateExcludeDexes -> {
                val newConfig = updateDexSelection(
                    dex = event.dex,
                    addToDexes = false,
                    currentConfig = quoteConfig.value
                )
                updateQuoteConfig { newConfig }
            }

            is SwapperScreenEvent.UpdateRestrictIntermediateTokens -> {
                updateQuoteConfig { copy(restrictIntermediateTokens = event.restrict) }
            }

            is SwapperScreenEvent.UpdateOnlyDirectRoutes -> {
                updateQuoteConfig { copy(onlyDirectRoutes = event.onlyDirectRoutes) }
            }

            is SwapperScreenEvent.UpdateAsLegacyTransaction -> {
                updateQuoteConfig { copy(asLegacyTransaction = event.asLegacy) }
            }

            is SwapperScreenEvent.UpdatePlatformFeeBps -> {
                updateQuoteConfig { copy(platformFeeBps = event.platformFeeBps) }
            }

            is SwapperScreenEvent.UpdateMaxAccounts -> {
                updateQuoteConfig { copy(maxAccounts = event.maxAccounts) }
            }

            is SwapperScreenEvent.UpdateAutoSlippage -> {
                updateQuoteConfig { copy(autoSlippage = event.autoSlippage) }
            }

            is SwapperScreenEvent.UpdateMaxAutoSlippageBps -> {
                updateQuoteConfig { copy(maxAutoSlippageBps = event.maxAutoSlippageBps) }
            }

            is SwapperScreenEvent.UpdateAutoSlippageCollisionUsdValue -> {
                updateQuoteConfig { copy(autoSlippageCollisionUsdValue = event.value) }
            }
        }
    }

    private fun getQuote(dexPair: DexPairSwapUiModel) {
        Logger.d("Price SOL is ${dexPair.priceSol}")
        screenModelScope.launch {
            getQuote(
                baseTokenAddress = dexPair.baseToken?.address,
                baseTokenSymbol = dexPair.baseToken?.symbol,
                quoteTokenAddress = dexPair.quoteToken?.address,
                quoteTokenSymbol = dexPair.quoteToken?.symbol,
                amount = quoteConfig.value.amount,
                initialPrice = BigDecimal(dexPair.priceSol),
                key = dexPair.key
            )
        }
    }

}