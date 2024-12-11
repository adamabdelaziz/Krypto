package org.adam.kryptobot.feature.swapper.data

import co.touchlab.kermit.Logger
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.accept
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.Parameters
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import org.adam.kryptobot.feature.swapper.data.dto.JupiterQuoteDto
import org.adam.kryptobot.feature.swapper.data.dto.JupiterSwapInstructionsDto
import org.adam.kryptobot.feature.swapper.data.dto.JupiterSwapResponseDto
import org.adam.kryptobot.feature.swapper.data.dto.JupiterSwapWrapDto
import org.adam.kryptobot.feature.swapper.data.dto.JupiterSwapWrapperDto
import kotlin.math.pow

// download jupiter wallet

interface JupiterSwapApi {
    suspend fun getQuote(
        inputAddress: String,
        outputAddress: String,
        amount: String,
        slippageBps: Int = 50,
        swapMode: String = "ExactIn",  // Default to ExactIn
        dexes: List<String>? = null,
        excludeDexes: List<String>? = null,
        restrictIntermediateTokens: Boolean = false,
        onlyDirectRoutes: Boolean = false,
        asLegacyTransaction: Boolean = false,
        platformFeeBps: Int? = null,
        maxAccounts: Int? = null,
        autoSlippage: Boolean = false,
        maxAutoSlippageBps: Int? = null,
        autoSlippageCollisionUsdValue: Int? = null
    ): String?

    suspend fun swapTokens(
        quoteResponse: String,
        userPublicKey: String,
    ): JupiterSwapResponseDto?

    suspend fun swapInstructions(
        quoteResponse: String,
        userPublicKey: String,
    ): JupiterSwapInstructionsDto?
}

class KtorJupiterSwapApi(private val client: HttpClient) : JupiterSwapApi {

    override suspend fun getQuote(
        inputAddress: String,
        outputAddress: String,
        amount: String,
        slippageBps: Int,
        swapMode: String,
        dexes: List<String>?,
        excludeDexes: List<String>?,
        restrictIntermediateTokens: Boolean,
        onlyDirectRoutes: Boolean,
        asLegacyTransaction: Boolean,
        platformFeeBps: Int?,
        maxAccounts: Int?,
        autoSlippage: Boolean,
        maxAutoSlippageBps: Int?,
        autoSlippageCollisionUsdValue: Int?
    ): String? {

        val queryParams = Parameters.build {
            append("inputMint", inputAddress)
            append("outputMint", outputAddress)
            append("amount", amount)
            append("slippageBps", slippageBps.toString())
            append("swapMode", swapMode)

            dexes?.let {
                append("dexes", it.joinToString(","))
            }

            excludeDexes?.let {
                append("excludeDexes", it.joinToString(","))
            }

            append("restrictIntermediateTokens", restrictIntermediateTokens.toString())
            append("onlyDirectRoutes", onlyDirectRoutes.toString())
            append("asLegacyTransaction", asLegacyTransaction.toString())

            platformFeeBps?.let {
                append("platformFeeBps", it.toString())
            }

            maxAccounts?.let {
                append("maxAccounts", it.toString())
            }

            append("autoSlippage", autoSlippage.toString())

            maxAutoSlippageBps?.let {
                append("maxAutoSlippageBps", it.toString())
            }

            autoSlippageCollisionUsdValue?.let {
                append("autoSlippageCollisionUsdValue", it.toString())
            }
        }

        return try {
            val response: HttpResponse = client.get(GET_QUOTE_URL) {
                method = HttpMethod.Get
                url.parameters.appendAll(queryParams)
            }
            val text = response.bodyAsText()

            Logger.d(text)
            Logger.d("Quote response status ${response.status}")

            text
        } catch (e: Exception) {
            Logger.d("API Quote exception ${e.message}")
            null

        }
    }

    override suspend fun swapTokens(
        quoteResponse: String,
        userPublicKey: String,
    ): JupiterSwapResponseDto? {
        return try {
            val response: HttpResponse = client.post(POST_SWAP_URL) {
                contentType(ContentType.Application.Json)
                accept(ContentType.Application.Json)
                setBody(
                    """
                {
                    "quoteResponse": $quoteResponse,
                    "userPublicKey": "$userPublicKey"
                }
                """.trimIndent()
                )
            }

            val text = response.bodyAsText()
            Logger.d(text)

            if (response.status.isSuccess()) {
                response.body<JupiterSwapResponseDto>()
            } else {
                null
            }
        } catch (e: Exception) {
            Logger.d("API Swap exception ${e.message}")
            null

        }
    }

    override suspend fun swapInstructions(
        quoteResponse: String,
        userPublicKey: String,
    ): JupiterSwapInstructionsDto? {
        return try {
            val response: HttpResponse = client.post(POST_SWAP_INSTRUCTIONS_URL) {
                contentType(ContentType.Application.Json)
                accept(ContentType.Application.Json)
                setBody(
                    """
                {
                    "quoteResponse": $quoteResponse,
                    "userPublicKey": "$userPublicKey"
                }
                """.trimIndent()
                )
            }

            val text = response.bodyAsText()
            Logger.d(text)

            if (response.status.isSuccess()) {
                response.body<JupiterSwapInstructionsDto>()
            } else {
                null
            }
        } catch (e: Exception) {
            Logger.d("API Swap Instructions exception ${e.message}")
            null

        }
    }

    companion object {
        private const val BASE_API_URL = "https://quote-api.jup.ag/v6/"
        private const val GET_QUOTE_URL = "${BASE_API_URL}quote"
        private const val POST_SWAP_URL = "${BASE_API_URL}swap"
        private const val POST_SWAP_INSTRUCTIONS_URL = "${BASE_API_URL}swap-instructions"

        const val EXACT_IN = "ExactIn"
        const val EXACT_OUT = "ExactOut"
    }
}