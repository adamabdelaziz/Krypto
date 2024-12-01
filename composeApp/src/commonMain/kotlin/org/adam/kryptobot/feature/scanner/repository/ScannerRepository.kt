package org.adam.kryptobot.feature.scanner.repository

import co.touchlab.kermit.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext
import org.adam.kryptobot.feature.scanner.data.DexScannerApi
import org.adam.kryptobot.feature.scanner.data.dto.BoostedTokenDto
import org.adam.kryptobot.feature.scanner.data.dto.DexPairDto
import org.adam.kryptobot.feature.scanner.data.dto.TokenDto

interface ScannerRepository {
    suspend fun getLatestTokens()
    suspend fun getLatestBoostedTokens()
    suspend fun getDexPairsByTokenAddress(tokenAddress: String) // This is supposed to be multiple comma separated token addresses
    suspend fun getDexPairsByTokenAddress(chainId: String, tokenAddress: String)

    val latestTokens: StateFlow<List<TokenDto>>
    val latestBoostedTokens: StateFlow<List<BoostedTokenDto>>
    val latestDexPairs: StateFlow<Map<String, List<DexPairDto>>>
}

class ScannerRepositoryImpl(
    private val api: DexScannerApi,
    private val stateFlowScope: CoroutineScope,
) : ScannerRepository {

    private val _latestTokens: MutableStateFlow<List<TokenDto>> =
        MutableStateFlow(listOf())
    override val latestTokens: StateFlow<List<TokenDto>> = _latestTokens.stateIn(
        scope = stateFlowScope,
        initialValue = _latestTokens.value,
        started = SharingStarted.WhileSubscribed(5000),
    )

    private val _latestBoostedTokens: MutableStateFlow<List<BoostedTokenDto>> =
        MutableStateFlow(listOf())
    override val latestBoostedTokens: StateFlow<List<BoostedTokenDto>> =
        _latestBoostedTokens.stateIn(
            scope = stateFlowScope,
            initialValue = _latestBoostedTokens.value,
            started = SharingStarted.WhileSubscribed(5000),
        )

    private val _latestDexPairs: MutableStateFlow<Map<String, List<DexPairDto>>> =
        MutableStateFlow(mapOf())
    override val latestDexPairs: StateFlow<Map<String, List<DexPairDto>>> = _latestDexPairs.stateIn(
        scope = stateFlowScope,
        initialValue = _latestDexPairs.value,
        started = SharingStarted.WhileSubscribed(5000),
    )

    override suspend fun getLatestTokens() {
        withContext(Dispatchers.IO) {
            try {
                val response = api.getLatestTokens()
                Logger.d("List Size is ${response.size}")
                _latestTokens.value = response
            } catch (e: Exception) {
                Logger.d(e.message ?: " Null Error Message for getLatestTokens()")
            }
        }
    }

    override suspend fun getLatestBoostedTokens() {
        withContext(Dispatchers.IO) {
            try {
                val response = api.getLatestBoostedTokens()
                Logger.d("Boosted List Size is ${response.size}")
                _latestBoostedTokens.value = response
            } catch (e: Exception) {
                Logger.d(e.message ?: " Null Error Message for getLatestBoostedTokens()")
            }
        }
    }

    override suspend fun getDexPairsByTokenAddress(tokenAddress: String) {
        withContext(Dispatchers.IO) {
            try {
                val response = api.getPairsByTokenAddress(tokenAddress)
                Logger.d("Token Response Size is ${response.size}")
                updateDexPairs(tokenAddress, response)
            } catch (e: Exception) {
                Logger.d(e.message ?: " Null Error Message for getDexPairsByTokenAddress()")
            }
        }
    }

    override suspend fun getDexPairsByTokenAddress(chainId: String, tokenAddress: String) {
        withContext(Dispatchers.IO) {
            try {
                val response = api.getPairsByAddress(chainId, tokenAddress)
                Logger.d("Token Response Size is ${response.size} $chainId $tokenAddress")
                updateDexPairs(tokenAddress, response)
            } catch (e: Exception) {
                Logger.d(e.message ?: " Null Error Message for getDexPairsByTokenAddress()")
            }
        }
    }

    private fun updateDexPairs(tokenAddress: String, pairs: List<DexPairDto>) {
        _latestDexPairs.value = _latestDexPairs.value.toMutableMap().apply {
            this[tokenAddress] = pairs
        }
    }
}