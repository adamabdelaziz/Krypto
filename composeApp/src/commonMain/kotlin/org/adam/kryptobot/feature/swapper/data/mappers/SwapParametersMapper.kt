package org.adam.kryptobot.feature.swapper.data.mappers

import org.adam.kryptobot.feature.swapper.data.dto.JupiterQuoteDto
import org.adam.kryptobot.feature.swapper.data.dto.JupiterSwapParametersDto
import org.sol4k.PublicKey

/*
    Doing this for now for default parameters based on whatever quote comes in
 */
fun JupiterQuoteDto.toJupiterSwapParameters(userPublicKey: String): JupiterSwapParametersDto {

    val feeAccount = if (this.swapMode == "ExactOut") {
        //Has to be inputMint
        this.inputMint
    } else {
        //Could be either or
        this.inputMint
    }

    val swapParams = JupiterSwapParametersDto(
        useSharedAccounts = false,
        feeAccount = feeAccount,
        trackingAccount = null,
        computeUnitPriceMicroLamports = null,
        prioritizationFeeLamports = this.platformFee?.feeBps ?: 0,
        destinationTokenAccount = userPublicKey,
    )

    return swapParams
}