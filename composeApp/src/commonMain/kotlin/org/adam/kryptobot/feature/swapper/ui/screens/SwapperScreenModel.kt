package org.adam.kryptobot.feature.swapper.ui.screens

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.zhuinden.flowcombinetuplekt.combineStates
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.adam.kryptobot.feature.scanner.repository.ScannerRepository
import org.adam.kryptobot.feature.scanner.ui.model.DexPairScanUiModel
import org.adam.kryptobot.feature.scanner.usecase.MonitorTokenAddressesUseCase
import org.adam.kryptobot.feature.swapper.repository.SwapperRepository
import org.adam.kryptobot.feature.swapper.ui.model.DexPairSwapUiModel
import org.adam.kryptobot.feature.wallet.repository.WalletRepository

class SwapperScreenModel(
    private val swapperRepository: SwapperRepository,
    private val scannerRepository: ScannerRepository,
    private val walletRepository: WalletRepository,
    private val monitorTokenAddressesUseCase: MonitorTokenAddressesUseCase,
) : ScreenModel, ScannerRepository by scannerRepository, SwapperRepository by swapperRepository,
    MonitorTokenAddressesUseCase by monitorTokenAddressesUseCase {

    private val _selectedDexPair = MutableStateFlow<DexPairSwapUiModel?>(null)
    val uiState: StateFlow<SwapperScreenUiState> = combineStates(
        screenModelScope,
        SharingStarted.WhileSubscribed(),
        latestDexPairs,
        trackedTokenAddresses,
        _selectedDexPair,
        quoteConfig,
        currentSwaps,
        ::mapSwapScreenUiState
    )

    init {
        monitorTokenAddressesUseCase.stop()
        monitorTokenAddressesUseCase.invoke(null)
    }

    fun onEvent(event: SwapperScreenEvent) {
        when (event) {
            is SwapperScreenEvent.OnDexPairClicked -> {
                _selectedDexPair.value = event.dexPair
            }
            is SwapperScreenEvent.UpdateSlippageBps ->
                updateQuoteConfig { copy(slippageBps = event.slippageBps) }
            is SwapperScreenEvent.UpdateSwapMode ->
                updateQuoteConfig { copy(swapMode = event.swapMode) }
            is SwapperScreenEvent.UpdateDexes ->
                updateQuoteConfig { copy(dexes = event.dexes) }
            is SwapperScreenEvent.UpdateExcludeDexes ->
                updateQuoteConfig { copy(excludeDexes = event.excludeDexes) }
            is SwapperScreenEvent.UpdateRestrictIntermediateTokens ->
                updateQuoteConfig { copy(restrictIntermediateTokens = event.restrict) }
            is SwapperScreenEvent.UpdateOnlyDirectRoutes ->
                updateQuoteConfig { copy(onlyDirectRoutes = event.onlyDirectRoutes) }
            is SwapperScreenEvent.UpdateAsLegacyTransaction ->
                updateQuoteConfig { copy(asLegacyTransaction = event.asLegacy) }
            is SwapperScreenEvent.UpdatePlatformFeeBps ->
                updateQuoteConfig { copy(platformFeeBps = event.platformFeeBps) }
            is SwapperScreenEvent.UpdateMaxAccounts ->
                updateQuoteConfig { copy(maxAccounts = event.maxAccounts) }
            is SwapperScreenEvent.UpdateAutoSlippage ->
                updateQuoteConfig { copy(autoSlippage = event.autoSlippage) }
            is SwapperScreenEvent.UpdateMaxAutoSlippageBps ->
                updateQuoteConfig { copy(maxAutoSlippageBps = event.maxAutoSlippageBps) }
            is SwapperScreenEvent.UpdateAutoSlippageCollisionUsdValue ->
                updateQuoteConfig { copy(autoSlippageCollisionUsdValue = event.value) }
        }
    }

    /*
        TODO: Actual function that takes parameters for getting quote.
          Store one stateflow exposing wrapper of params
     */
    private fun getQuote(dexPair: DexPairSwapUiModel) {
        screenModelScope.launch {
            swapperRepository.getQuote(
                inputAddress = dexPair.quoteToken?.address ?: "",
                outputAddress = dexPair.baseToken?.address ?: "",
                amount = 0.001,
            )
        }
    }
}