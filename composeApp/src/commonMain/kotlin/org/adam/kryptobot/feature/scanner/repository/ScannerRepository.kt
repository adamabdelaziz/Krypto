package org.adam.kryptobot.feature.scanner.repository

import co.touchlab.kermit.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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
    suspend fun getDexPairsByAddressList() // This is supposed to be multiple comma separated token addresses
    suspend fun getDexPairsByChainAndAddress(chainId: String, tokenAddress: String)

    val latestTokens: StateFlow<List<TokenDto>>
    val latestBoostedTokens: StateFlow<List<BoostedTokenDto>>
    val latestDexPairs: StateFlow<Map<String, DexPairDto>>
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

    private val _latestDexPairs: MutableStateFlow<Map<String, DexPairDto>> =
        MutableStateFlow(mapOf())
    override val latestDexPairs: StateFlow<Map<String, DexPairDto>> = _latestDexPairs.stateIn(
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

    override suspend fun getDexPairsByAddressList() {
        withContext(Dispatchers.IO) {
            try {
                val boostedAddress = _latestBoostedTokens.value.map { it.tokenAddress }
                val regularAddress = _latestTokens.value.map { it.tokenAddress }
                val tokenAddress = (boostedAddress + regularAddress)
                    .distinct()
                    .joinToString(",")
                val response = api.getPairsByTokenAddress(tokenAddress)
                response?.let {
                    val pairs = it.pairs
                    pairs?.let {
                        val pairAddresses = it.map { it.pairAddress }
                        val baseAddresses = it.map { it.baseToken?.address }
                        val quoteAddresses = it.map { it.quoteToken?.address }

                        Logger.d("Pair Addresses ${pairAddresses.size} Base Addresses ${baseAddresses.size} QuoteAddresses ${quoteAddresses.size}")
                        Logger.d("Pair Addresses Distinct ${pairAddresses.distinct().size} Base Addresses Distinct ${baseAddresses.distinct().size} QuoteAddresses Distinct ${quoteAddresses.distinct().size}")
                    }
                    /*
                        TODO:Store list of pairs based on their pair addresses as unique identifier
                     */
                    updateDexPairs(tokenAddress, response)
                }
            } catch (e: Exception) {
                Logger.d(e.message ?: " Null Error Message for getDexPairsByTokenAddress()")
            }
        }
    }

    override suspend fun getDexPairsByChainAndAddress(chainId: String, tokenAddress: String) {
        withContext(Dispatchers.IO) {
            try {
                val response = api.getPairsByAddress(chainId, tokenAddress)
                response?.let {
                    updateDexPairs(tokenAddress, response)
                }
            } catch (e: Exception) {
                Logger.d(e.message ?: " Null Error Message for getDexPairsByTokenAddress()")
            }
        }
    }

    private fun updateDexPairs(tokenAddress: String, pairs: DexPairDto) {
        _latestDexPairs.value = _latestDexPairs.value.toMutableMap().apply {
            this[tokenAddress] = pairs
        }
    }
}