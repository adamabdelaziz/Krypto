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
import org.adam.kryptobot.feature.scanner.data.dto.PairDto
import org.adam.kryptobot.feature.scanner.data.dto.PaymentStatusDto
import org.adam.kryptobot.feature.scanner.data.dto.LatestTokenDto
import org.adam.kryptobot.feature.scanner.data.dto.toDexPair
import org.adam.kryptobot.feature.scanner.data.dto.toToken
import org.adam.kryptobot.feature.scanner.data.model.DexPair
import org.adam.kryptobot.feature.scanner.data.model.Token
import org.adam.kryptobot.feature.scanner.enum.TokenCategory
import org.hipparchus.analysis.function.Log

interface ScannerRepository {
    suspend fun getTokens(tokenCategory: TokenCategory)

    suspend fun getDexPairsByAddressList(category: TokenCategory) // This is supposed to be multiple comma separated token addresses
    suspend fun getDexPairsByChainAndAddress(chainId: String, tokenAddress: String)
    suspend fun getOrdersPaidFor(chainId: String, tokenAddress: String)

    fun trackPair(dexPair: DexPair)

    //TODO make a wrapper for DTO and then map to TokenCategory
    val tokens: StateFlow<Map<TokenCategory, List<Token>>>

    val latestDexPairs: StateFlow<Map<TokenCategory, List<DexPair>>>
    val ordersPaidForByTokenAddress: StateFlow<List<PaymentStatusDto>>
}

class ScannerRepositoryImpl(
    private val api: DexScannerApi,
    private val stateFlowScope: CoroutineScope,
) : ScannerRepository {

    private val _tokens: MutableStateFlow<Map<TokenCategory, List<Token>>> =
        MutableStateFlow(mapOf())
    override val tokens: StateFlow<Map<TokenCategory, List<Token>>> =
        _tokens.stateIn(
            scope = stateFlowScope,
            initialValue = _tokens.value,
            started = SharingStarted.WhileSubscribed(5000),
        )

    private val _latestDexPairs: MutableStateFlow<Map<TokenCategory, List<DexPair>>> =
        MutableStateFlow(mapOf())
    override val latestDexPairs: StateFlow<Map<TokenCategory, List<DexPair>>> =
        _latestDexPairs.stateIn(
            scope = stateFlowScope,
            initialValue = _latestDexPairs.value,
            started = SharingStarted.WhileSubscribed(5000),
        )

    private val _ordersPaidFor: MutableStateFlow<List<PaymentStatusDto>> =
        MutableStateFlow(listOf())
    override val ordersPaidForByTokenAddress: StateFlow<List<PaymentStatusDto>> =
        _ordersPaidFor.stateIn(
            scope = stateFlowScope,
            initialValue = _ordersPaidFor.value,
            started = SharingStarted.WhileSubscribed(5000),
        )

    private val initialPairs: MutableMap<String, DexPair> = mutableMapOf()

    override suspend fun getTokens(tokenCategory: TokenCategory) {
        withContext(Dispatchers.IO) {
            try {
                val response = when (tokenCategory) {
                    TokenCategory.LATEST_BOOSTED -> api.getLatestBoostedTokens()
                        .map { it.toToken() }

                    TokenCategory.MOST_ACTIVE_BOOSTED -> api.getMostActiveBoostedTokens()
                        .map { it.toToken() }

                    TokenCategory.LATEST -> api.getLatestTokens().map { it.toToken() }
                    else -> listOf()
                }
                val map = _tokens.value.toMutableMap()
                map[tokenCategory] = response
                _tokens.value = map.toMap()
            } catch (e: Exception) {
                Logger.d(e.message ?: " Null Error Message for getLatestTokens()")
            }
        }
    }

    override suspend fun getDexPairsByAddressList(category: TokenCategory) {
        withContext(Dispatchers.IO) {
            try {
                val addresses =
                    _tokens.value[category]?.map { it.tokenAddress }?.distinct()?.joinToString(",")
                        ?: ""

                if (addresses.isNotEmpty()) {
                    val response = api.getPairsByTokenAddress(addresses)

                    response?.let {
                        val currentMap = _latestDexPairs.value.toMutableMap()
                        val oldList = currentMap[category] ?: listOf()

                        //Mapping and logic goes here.
                        val pairs =
                            it.pairs?.map { it.toDexPair(oldList, initialPairs.values.toList()) }

                        pairs?.let { fetchedPairs ->
                            pairs.forEach { pair ->
                                pair.pairAddress?.let {
                                    if (!initialPairs.containsKey(pair.pairAddress)) {
                                        initialPairs[pair.pairAddress] = pair
                                    }
                                }
                            }

                            currentMap[category] =
                                fetchedPairs.distinctBy { it.pairAddress }.sortedBy { pair ->
                                    pair.liquidityMarketRatio
                                }

                            _latestDexPairs.value = currentMap.toMap()
                        }
                    }
                }
            } catch (e: Exception) {
                Logger.d(e.message ?: " Null Error Message for getDexPairsByTokenAddress()")
            }
        }
    }

    //Uses pair address from other pair endpoint
    override suspend fun getDexPairsByChainAndAddress(chainId: String, tokenAddress: String) {
        withContext(Dispatchers.IO) {
            try {
                val response = api.getPairsByAddress(chainId, tokenAddress)
                //TODO
            } catch (e: Exception) {
                Logger.d(e.message ?: " Null Error Message for getDexPairsByTokenAddress()")
            }
        }
    }

    override suspend fun getOrdersPaidFor(chainId: String, tokenAddress: String) {
        withContext(Dispatchers.IO) {
            try {
                val response = api.checkOrdersPaidForOfToken(chainId, tokenAddress)
                val currentList = _ordersPaidFor.value
                val newList = (response + currentList).distinctBy { it.paymentTimestamp }
                    .sortedBy { it.paymentTimestamp }
                Logger.d("Orders Paid For Response Size ${response.size} $chainId $tokenAddress")
                _ordersPaidFor.value = newList
            } catch (e: Exception) {
                Logger.d(e.message ?: " Null Error Message for getDexPairsByTokenAddress()")
            }
        }
    }

    override fun trackPair(dexPair: DexPair) {
        val updatedMap = _latestDexPairs.value.mapValues { (_, pairs) ->
            pairs.map { pair ->
                if (pair.pairAddress == dexPair.pairAddress) {
                    pair.copy(beingTracked = !pair.beingTracked)
                } else {
                    pair
                }
            }
        }
        _latestDexPairs.value = updatedMap
    }
}