package org.adam.kryptobot.feature.checker.data

import io.ktor.client.HttpClient

interface RugCheckApi {
}

class KtorRugCheckApi(private val client: HttpClient) : RugCheckApi {

}