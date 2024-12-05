package org.adam.kryptobot.feature.swapper.data

import io.ktor.client.HttpClient

// download jupiter wallet

interface JupiterSwapApi {
    suspend fun getQuote(inputAddress: String, outputAddress: String, amount: Int)

}

class KtorJupiterSwapApi(private val client: HttpClient) : JupiterSwapApi {

    override suspend fun getQuote(inputAddress: String, outputAddress: String, amount: Int) {

    }

    companion object {
        private const val BASE_URL = "https://quote-api.jup.ag/v6/"
    }
}