package org.adam.kryptobot.feature.swapper.data.mappers

import org.adam.kryptobot.feature.swapper.data.dto.DynamicSlippage
import org.adam.kryptobot.feature.swapper.data.dto.JupiterQuoteDto
import org.adam.kryptobot.feature.swapper.data.dto.JupiterSwapWrapDto
import org.adam.kryptobot.feature.swapper.data.dto.JupiterSwapWrapperDto
import org.sol4k.PublicKey

/*
    Doing this for now for default parameters based on whatever quote comes in
 */
fun JupiterQuoteDto.wrapped(userPublicKey: String): JupiterSwapWrapperDto {
    val feeAccount = if (this.swapMode == "ExactOut") {
        //Has to be inputMint
        this.inputMint
    } else {
        //Could be either or
        this.inputMint
    }

    val wrapper = JupiterSwapWrapperDto(
        userPublicKey = userPublicKey,
        quoteResponse = this,
        feeAccount = feeAccount,
        trackingAccount = feeAccount,
        destinationTokenAccount = userPublicKey,
        wrapAndUnwrapSol = true,
        useSharedAccounts = true,
        asLegacyTransaction = false,
        useTokenLedger = false,
        dynamicComputeUnitLimit = true,
        skipUserAccountsRpcCalls = false,
        computeUnitPriceMicroLamports = 0,
        prioritizationFeeLamports = 0,
        dynamicSlippage = DynamicSlippage(maxBps = 300, minBps = 100)
    )

    return wrapper
}

fun JupiterQuoteDto.wrappedTemp(userPublicKey: String): JupiterSwapWrapDto {
    val feeAccount = if (this.swapMode == "ExactOut") {
        //Has to be inputMint
        this.inputMint
    } else {
        //Could be either or
        this.inputMint
    }

    val wrapper = JupiterSwapWrapDto(
        userPublicKey = userPublicKey,
        quoteResponse = this,
    )

    return wrapper
}