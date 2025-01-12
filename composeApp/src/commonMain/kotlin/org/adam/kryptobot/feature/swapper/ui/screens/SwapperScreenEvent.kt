package org.adam.kryptobot.feature.swapper.ui.screens

import org.adam.kryptobot.feature.scanner.enum.Dex
import org.adam.kryptobot.feature.swapper.ui.model.DexPairSwapUiModel
import org.adam.kryptobot.feature.swapper.ui.model.TransactionUiModel
import java.math.BigDecimal

sealed class SwapperScreenEvent {
    data class OnDexPairClicked(val dexPair: DexPairSwapUiModel) : SwapperScreenEvent()
    data class OnTransactionClicked(val transaction: TransactionUiModel): SwapperScreenEvent()
    data object OnGetQuoteClicked : SwapperScreenEvent()

    data class UpdateAmount(val amount: Double?) : SwapperScreenEvent()
    data class UpdateSlippageBps(val slippageBps: Int?) : SwapperScreenEvent()
    data object UpdateSwapMode : SwapperScreenEvent()
    data object UpdateSafeMode : SwapperScreenEvent()
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

    data class UpdateProfitTargetPercent(val profitTarget: BigDecimal?) : SwapperScreenEvent()
    data class UpdateStopLossPercent(val stopLoss: BigDecimal?) : SwapperScreenEvent()
    data class UpdateTrailingStopPercent(val trailingStop: BigDecimal?) : SwapperScreenEvent()
    data class UpdateExitPercent(val exitPercentage: BigDecimal?) : SwapperScreenEvent()

}