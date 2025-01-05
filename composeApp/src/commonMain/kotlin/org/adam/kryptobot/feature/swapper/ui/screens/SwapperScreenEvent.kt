package org.adam.kryptobot.feature.swapper.ui.screens

import org.adam.kryptobot.feature.scanner.enum.Dex
import org.adam.kryptobot.feature.swapper.ui.model.DexPairSwapUiModel

sealed class SwapperScreenEvent {
    data class OnDexPairClicked(val dexPair: DexPairSwapUiModel) : SwapperScreenEvent()
    data object OnGetQuoteClicked : SwapperScreenEvent()

    data class UpdateAmount(val amount: Double) : SwapperScreenEvent()
    data class UpdateSlippageBps(val slippageBps: Int) : SwapperScreenEvent()
    data object UpdateSwapMode : SwapperScreenEvent()
    data class UpdateDexes(val dex: Dex) : SwapperScreenEvent()
    data class UpdateExcludeDexes(val dex: Dex) : SwapperScreenEvent()
    data class UpdateRestrictIntermediateTokens(val restrict: Boolean) : SwapperScreenEvent()
    data class UpdateOnlyDirectRoutes(val onlyDirectRoutes: Boolean) : SwapperScreenEvent()
    data class UpdateAsLegacyTransaction(val asLegacy: Boolean) : SwapperScreenEvent()
    data class UpdatePlatformFeeBps(val platformFeeBps: Int?) : SwapperScreenEvent()
    data class UpdateMaxAccounts(val maxAccounts: Int?) : SwapperScreenEvent()
    data class UpdateAutoSlippage(val autoSlippage: Boolean) : SwapperScreenEvent()
    data class UpdateMaxAutoSlippageBps(val maxAutoSlippageBps: Int?) : SwapperScreenEvent()
    data class UpdateAutoSlippageCollisionUsdValue(val value: Int?) : SwapperScreenEvent()
}