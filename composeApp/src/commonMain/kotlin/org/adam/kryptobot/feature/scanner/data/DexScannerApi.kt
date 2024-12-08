package org.adam.kryptobot.feature.scanner.data

import co.touchlab.kermit.Logger
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import org.adam.kryptobot.feature.scanner.data.dto.BoostedTokenDto
import org.adam.kryptobot.feature.scanner.data.dto.DexPairDto
import org.adam.kryptobot.feature.scanner.data.dto.PaymentStatusDto
import org.adam.kryptobot.feature.scanner.data.dto.LatestTokenDto

interface DexScannerApi {
    suspend fun getLatestTokens(): List<LatestTokenDto>
    suspend fun getLatestBoostedTokens(): List<BoostedTokenDto>
    suspend fun getMostActiveBoostedTokens(): List<BoostedTokenDto>

    suspend fun checkOrdersPaidForOfToken(
        chainId: String,
        tokenAddress: String
    ): List<PaymentStatusDto>

    suspend fun getPairsByAddress(chainId: String, tokenAddress: String): DexPairDto?
    suspend fun getPairsByTokenAddress(tokenAddress: String): DexPairDto?
    suspend fun searchForPairs(query: String): DexPairDto?
}

/*
    Check these with various endpoints(doesnt seem to do anything for latest tokens) and search endpoint
    ?rankBy=trendingScoreH6&order=desc&minLiq=100000
 */
class KtorDexScannerApi(private val client: HttpClient) : DexScannerApi {
    override suspend fun getLatestTokens(): List<LatestTokenDto> {
        return try {
            val response: HttpResponse =
                client.get("${BASE_API_URL}token-profiles/latest/v1") {
                    url {
                        parameters.append("rankBy", "trendingScoreH6")
                        parameters.append("order", "desc")
                        parameters.append("minLiq", "100,000")
                    }
                }
            val rawResponse = response.bodyAsText()
            //Logger.d("Raw Response: $rawResponse")
            if (response.status.isSuccess()) {
                val tokens = response.body<List<LatestTokenDto>>()
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

    //TODO consolidate these two functions
    override suspend fun getLatestBoostedTokens(): List<BoostedTokenDto> {
        return try {
            val response: HttpResponse =
                client.get("${BASE_API_URL}token-boosts/latest/v1?limit=50")
            val rawResponse = response.bodyAsText()
            //Logger.d("Raw Response: $rawResponse")
            if (response.status.isSuccess()) {
                val tokens = response.body<List<BoostedTokenDto>>()
                tokens.takeIf { it.isNotEmpty() } ?: listOf()
            } else {
                Logger.d("Error response: ${response.status}, $rawResponse")
                listOf()
            }
        } catch (e: Exception) {
            Logger.d("API Exception ${e.message}")
            listOf()
        }
    }

    override suspend fun getMostActiveBoostedTokens(): List<BoostedTokenDto> {
        return try {
            val response: HttpResponse = client.get("${BASE_API_URL}token-boosts/top/v1")
            val rawResponse = response.bodyAsText()
            //Logger.d("Raw Response: $rawResponse")
            if (response.status.isSuccess()) {
                val tokens = response.body<List<BoostedTokenDto>>()
                tokens.takeIf { it.isNotEmpty() } ?: listOf()
            } else {
                Logger.d("Error response: ${response.status}, $rawResponse")
                listOf()
            }
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
            val response: HttpResponse =
                client.get("${BASE_API_URL}orders/v1/${chainId}/${tokenAddress}")
            val rawResponse = response.bodyAsText()
            Logger.d("Raw Response: $rawResponse")
            if (response.status.isSuccess()) {
                val tokens = response.body<List<PaymentStatusDto>>()
                tokens.takeIf { it.isNotEmpty() } ?: listOf()
            } else {
                Logger.d("Error response: ${response.status}, $rawResponse")
                listOf()
            }
        } catch (e: Exception) {
            Logger.d("API Exception ${e.message}")
            listOf()
        }
    }

    override suspend fun getPairsByAddress(
        chainId: String,
        tokenAddress: String
    ): DexPairDto? {
        Logger.d("API called with $chainId and $tokenAddress")
        return try {
            val response: HttpResponse =
                client.get("${BASE_API_URL}latest/dex/pairs/${chainId}/${tokenAddress}")
            val rawResponse = response.bodyAsText()
            Logger.d("Raw Response: $rawResponse")
            if (response.status.isSuccess()) {
                val pairs = response.body<DexPairDto>()
                pairs
            } else {
                Logger.d("Error ${response.status}")
                null
            }
        } catch (e: Exception) {
            Logger.d("API Exception ${e.message}")
            null
        }
    }

    override suspend fun getPairsByTokenAddress(tokenAddress: String): DexPairDto? {
        return try {
            val response: HttpResponse =
                client.get("${BASE_API_URL}latest/dex/tokens/$tokenAddress")
//            val rawJson = response.bodyAsText()
//            val formattedJson = Json { prettyPrint = true }.parseToJsonElement(rawJson).toString()
//            Logger.d("Formatted JSON:\n" +
//                   "$formattedJson")
            response.body<DexPairDto>()
        } catch (e: Exception) {
            Logger.d("API Exception ${e.message}")
            null
        }
    }

    override suspend fun searchForPairs(query: String): DexPairDto? {
        return try {
            val response: HttpResponse =
                client.get("https://api.dexscreener.com/latest/dex/search") {
                    url {
                        parameters.append("q", query)
                    }
                }.body()
//            val rawJson = response.bodyAsText()
//            Logger.d("RawJson is $rawJson")
            response.body<DexPairDto>()
        } catch (e: Exception) {
            Logger.d("API Exception ${e.message}")
            null
        }
    }

    companion object {
        private const val BASE_API_URL = "https://api.dexscreener.com/"
    }
}