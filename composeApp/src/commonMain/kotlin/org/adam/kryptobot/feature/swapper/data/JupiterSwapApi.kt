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
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.adam.kryptobot.feature.swapper.data.dto.JupiterQuoteDto
import org.adam.kryptobot.feature.swapper.data.dto.JupiterSwapInstructionsDto
import org.adam.kryptobot.feature.swapper.data.dto.JupiterSwapResponseDto

// download jupiter wallet

interface JupiterSwapApi {
    suspend fun getQuote(
        inputAddress: String,
        outputAddress: String,
        amount: String,
        slippageBps: Int,
        swapMode: String,
        dexes: List<String>,
        excludeDexes: List<String>,
        restrictIntermediateTokens: Boolean,
        onlyDirectRoutes: Boolean,
        asLegacyTransaction: Boolean,
        platformFeeBps: Int?,
        maxAccounts: Int?,
        autoSlippage: Boolean,
        maxAutoSlippageBps: Int?,
        autoSlippageCollisionUsdValue: Int?
    ): Pair<String?, JupiterQuoteDto?>

    suspend fun swapTokens(
        quoteResponse: String,
        userPublicKey: String,
        prioritizationFee: Long? = null,
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
        dexes: List<String>,
        excludeDexes: List<String>,
        restrictIntermediateTokens: Boolean,
        onlyDirectRoutes: Boolean,
        asLegacyTransaction: Boolean,
        platformFeeBps: Int?,
        maxAccounts: Int?,
        autoSlippage: Boolean,
        maxAutoSlippageBps: Int?,
        autoSlippageCollisionUsdValue: Int?
    ): Pair<String?, JupiterQuoteDto?> {

        val queryParams = Parameters.build {
            append("inputMint", inputAddress)
            append("outputMint", outputAddress)
            append("amount", amount)
            append("slippageBps", slippageBps.toString())
            append("swapMode", swapMode)

            if (dexes.isNotEmpty()) {
                append("dexes", dexes.joinToString(","))
            }

            if (excludeDexes.isNotEmpty()) {
                append("excludeDexes", excludeDexes.joinToString(","))
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

            Logger.d("Quote response status ${response.status}")

            val serialized = try {
                response.body<JupiterQuoteDto>()
            } catch (e: SerializationException) {
                null
            }

            text to serialized
        } catch (e: Exception) {
            Logger.d("API Quote exception ${e.message}")
            null to null
        }
    }

    override suspend fun swapTokens(
        quoteResponse: String,
        userPublicKey: String,
        prioritizationFee: Long?
    ): JupiterSwapResponseDto? {
        return try {
            val response: HttpResponse = client.post(POST_SWAP_URL) {
                contentType(ContentType.Application.Json)
                accept(ContentType.Application.Json)
                setBody(
                    buildJsonObject {
                        put("quoteResponse", Json.parseToJsonElement(quoteResponse))
                        put("userPublicKey", userPublicKey)
                        put("wrapAndUnwrapSol", true)
                        if (prioritizationFee != null) {
                            put("prioritizationFeeLamports", prioritizationFee)
                        } else {
                            put("prioritizationFeeLamports", "auto")
                        }
                        put("computeUnitLimit", 2_000_000)
                    }.toString()
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
            if (text.isNotEmpty()) {
                Logger.d("Successful non empty instruction response")
            }

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
    }
}