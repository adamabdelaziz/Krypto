package org.adam.kryptobot.feature.swapper.ui.model

import org.adam.kryptobot.feature.swapper.data.model.Transaction
import org.adam.kryptobot.feature.swapper.data.model.TransactionToken
import org.adam.kryptobot.feature.swapper.data.model.TransactionTokenUi
import org.adam.kryptobot.feature.swapper.enum.Status
import org.adam.kryptobot.feature.swapper.enum.SwapMode
import org.adam.kryptobot.feature.swapper.enum.TransactionStep

data class TransactionUiModel(
    val key:String, //QuoteRaw
    val amount: String,
    val initialPriceSol: String,
    val percentChange: String,
    val swapMode: SwapMode,
    val inToken: TransactionTokenUi,
    val outToken: TransactionTokenUi,
    val transactionStep: TransactionStep,
    val fees: String?,
    val slippageBps: Int,
    val beingTrackedForProfit: Boolean,
    val status: Status,
    val currentMessage: String,
)