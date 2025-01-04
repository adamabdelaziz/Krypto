package org.adam.kryptobot.feature.swapper.ui.screens

import org.adam.kryptobot.feature.scanner.ui.model.DexPairScanUiModel
import org.adam.kryptobot.feature.swapper.ui.model.DexPairSwapUiModel

sealed class SwapperScreenEvent {
    data class OnDexPairClicked(val dexPair: DexPairSwapUiModel) : SwapperScreenEvent()

    data class UpdateSlippageBps(val slippageBps: Int) : SwapperScreenEvent()
    data class UpdateSwapMode(val swapMode: String) : SwapperScreenEvent()
    data class UpdateDexes(val dexes: List<String>?) : SwapperScreenEvent()
    data class UpdateExcludeDexes(val excludeDexes: List<String>?) : SwapperScreenEvent()
    data class UpdateRestrictIntermediateTokens(val restrict: Boolean) : SwapperScreenEvent()
    data class UpdateOnlyDirectRoutes(val onlyDirectRoutes: Boolean) : SwapperScreenEvent()
    data class UpdateAsLegacyTransaction(val asLegacy: Boolean) : SwapperScreenEvent()
    data class UpdatePlatformFeeBps(val platformFeeBps: Int?) : SwapperScreenEvent()
    data class UpdateMaxAccounts(val maxAccounts: Int?) : SwapperScreenEvent()
    data class UpdateAutoSlippage(val autoSlippage: Boolean) : SwapperScreenEvent()
    data class UpdateMaxAutoSlippageBps(val maxAutoSlippageBps: Int?) : SwapperScreenEvent()
    data class UpdateAutoSlippageCollisionUsdValue(val value: Int?) : SwapperScreenEvent()
}