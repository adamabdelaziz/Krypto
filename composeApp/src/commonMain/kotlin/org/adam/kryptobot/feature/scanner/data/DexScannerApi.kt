package org.adam.kryptobot.feature.scanner.data

import co.touchlab.kermit.Logger
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlinx.serialization.json.Json
import org.adam.kryptobot.feature.scanner.data.dto.BoostedTokenDto
import org.adam.kryptobot.feature.scanner.data.dto.DexPairDto
import org.adam.kryptobot.feature.scanner.data.dto.PaymentStatusDto
import org.adam.kryptobot.feature.scanner.data.dto.TokenDto

interface DexScannerApi {
    suspend fun getLatestTokens(): List<TokenDto>
    suspend fun getLatestBoostedTokens(): List<BoostedTokenDto>
    suspend fun getMostActiveBoostedTokens(): List<BoostedTokenDto>

    suspend fun checkOrdersPaidForOfToken(
        chainId: String,
        tokenAddress: String
    ): List<PaymentStatusDto>

    suspend fun getPairsByAddress(chainId: String, tokenAddress: String): List<DexPairDto>
    suspend fun getPairsByTokenAddress(tokenAddress: String): List<DexPairDto>
}

class KtorDexScannerApi(private val client: HttpClient) : DexScannerApi {
    override suspend fun getLatestTokens(): List<TokenDto> {
        return try {
            val response: HttpResponse = client.get("${BASE_API_URL}token-profiles/latest/v1")
            val rawResponse = response.bodyAsText()
            Logger.d("Raw Response: $rawResponse")
            if (response.status.isSuccess()) {
                val tokens = Json.decodeFromString<List<TokenDto>>(rawResponse)
                tokens.takeIf { it.isNotEmpty() } ?: listOf()
            } else {
                Logger.d("Error response: ${response.status}, $rawResponse")
                listOf()
            }
        } catch (e: Exception) {
            Logger.d("API Exception: ${e.message}")
            listOf()
        }
    }

    override suspend fun getLatestBoostedTokens(): List<BoostedTokenDto> {
        return try {
            client.get("${BASE_API_URL}token-boosts/latest/v1").body<List<BoostedTokenDto>>()
        } catch (e: Exception) {
            Logger.d("API Exception ${e.message}")
            listOf()
        }
    }

    override suspend fun getMostActiveBoostedTokens(): List<BoostedTokenDto> {
        return try {
            client.get("${BASE_API_URL}token-boosts/top/v1").body<List<BoostedTokenDto>>()
        } catch (e: Exception) {
            Logger.d("API Exception ${e.message}")
            listOf()
        }
    }

    override suspend fun checkOrdersPaidForOfToken(
        chainId: String,
        tokenAddress: String
    ): List<PaymentStatusDto> {
        return try {
            client.get("${BASE_API_URL}orders/v1/${chainId}/${tokenAddress}")
                .body<List<PaymentStatusDto>>()
        } catch (e: Exception) {
            Logger.d("API Exception ${e.message}")
            listOf()
        }
    }

    override suspend fun getPairsByAddress(chainId: String, tokenAddress: String): List<DexPairDto> {
        return try {
            client.get("${BASE_API_URL}latest/dex/pairs/${chainId}/${tokenAddress}")
                .body<List<DexPairDto>>()
        } catch (e: Exception) {
            Logger.d("API Exception ${e.message}")
            listOf()
        }
    }

    override suspend fun getPairsByTokenAddress(tokenAddress: String): List<DexPairDto> {
        return try {
            val response: HttpResponse = client.get("${BASE_API_URL}latest/dex/tokens/$tokenAddress")
            val rawJson = response.bodyAsText()
            Logger.d("RawJson is $rawJson")
            client.get("${BASE_API_URL}latest/dex/tokens/${tokenAddress}")
                .body<List<DexPairDto>>()
        } catch (e: Exception) {
            Logger.d("API Exception ${e.message}")
            listOf()
        }
    }

    companion object {
        private const val BASE_API_URL = "https://api.dexscreener.com/"
    }
}