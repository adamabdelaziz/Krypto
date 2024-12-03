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
import org.adam.kryptobot.feature.scanner.data.model.DexPair
import org.adam.kryptobot.feature.scanner.enum.TokenCategory
import org.hipparchus.analysis.function.Log

interface ScannerRepository {
    suspend fun getLatestTokens()
    suspend fun getLatestBoostedTokens()
    suspend fun getMostActiveBoostedTokens()

    suspend fun getDexPairsByAddressList(category: TokenCategory) // This is supposed to be multiple comma separated token addresses
    suspend fun getDexPairsByChainAndAddress(chainId: String, tokenAddress: String)
    suspend fun getOrdersPaidFor(chainId: String, tokenAddress: String)

    //TODO make a wrapper for DTO and then map to TokenCategory
    val latestTokens: StateFlow<List<LatestTokenDto>>
    val latestBoostedTokens: StateFlow<List<BoostedTokenDto>>
    val mostActiveBoostedTokens: StateFlow<List<BoostedTokenDto>>

    val latestDexPairs: StateFlow<Map<TokenCategory, List<DexPair>>>
    val ordersPaidForByTokenAddress: StateFlow<List<PaymentStatusDto>>
}

class ScannerRepositoryImpl(
    private val api: DexScannerApi,
    private val stateFlowScope: CoroutineScope,
) : ScannerRepository {

    private val _latestTokens: MutableStateFlow<List<LatestTokenDto>> =
        MutableStateFlow(listOf())
    override val latestTokens: StateFlow<List<LatestTokenDto>> = _latestTokens.stateIn(
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

    private val _mostActiveBoostedTokens: MutableStateFlow<List<BoostedTokenDto>> =
        MutableStateFlow(listOf())
    override val mostActiveBoostedTokens: StateFlow<List<BoostedTokenDto>> =
        _mostActiveBoostedTokens.stateIn(
            scope = stateFlowScope,
            initialValue = _mostActiveBoostedTokens.value,
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

    //TODO Consolidate with category
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

    override suspend fun getMostActiveBoostedTokens() {
        withContext(Dispatchers.IO) {
            try {
                val response = api.getMostActiveBoostedTokens()
                Logger.d("Boosted List Size is ${response.size}")
                _mostActiveBoostedTokens.value = response
            } catch (e: Exception) {
                Logger.d(e.message ?: " Null Error Message for getLatestBoostedTokens()")
            }
        }
    }

    override suspend fun getDexPairsByAddressList(category: TokenCategory) {
        withContext(Dispatchers.IO) {
            try {
                val addresses = when (category) {
                    TokenCategory.LATEST -> {
                        _latestTokens.value.map { it.tokenAddress }
                    }

                    TokenCategory.LATEST_BOOSTED -> {
                        _latestBoostedTokens.value.map { it.tokenAddress }
                    }

                    else -> {
                        _mostActiveBoostedTokens.value.map { it.tokenAddress }
                    }
                }.distinct().joinToString(",")

                if (addresses.isNotEmpty()) {
                    val response = api.getPairsByTokenAddress(addresses)

                    response?.let {
                        val currentMap = _latestDexPairs.value.toMutableMap()
                        val oldList = currentMap[category] ?: listOf()
                        val pairs = it.pairs?.map { it.toDexPair(oldList) }
                        pairs?.let { fetchedPairs ->
                            val pairAddresses = fetchedPairs.mapNotNull { it.pairAddress }
                            Logger.d("Fetched Pair Addresses: ${pairAddresses.size}, Distinct: ${pairAddresses.distinct().size}")

//                            val updatedPairs = fetchedPairs.map { pair ->
//                                val oldOne =
//                                    oldList.firstOrNull { it.pairAddress == pair.pairAddress }
//                                if (oldOne != null) {
//                                    val oldPriceNative = oldOne.priceNative?.toDoubleOrNull()
//                                    val newPriceNative = pair.priceNative?.toDoubleOrNull()
//                                    if (oldPriceNative != newPriceNative) {
//                                        Logger.d("Old $oldPriceNative new $newPriceNative")
//                                    }
//
//                                    val priceChangePercentage =
//                                        if (oldPriceNative != null && newPriceNative != null && oldPriceNative != 0.0) {
//                                            ((newPriceNative - oldPriceNative) / oldPriceNative) * 100
//                                        } else {
//                                            0.0
//                                        }
//                                    val debugString = String.format("%.12f", priceChangePercentage)
//                                    Logger.d("Percentage $debugString $category")
//                                    pair.copy(priceChangeSinceScanned = 420.0)
//                                } else {
//                                    pair
//                                }
//                            }
                            val oldPairsMap = oldList.associateBy { it.pairAddress }

                            //This sucks replace this block
//                            val mergedList = fetchedPairs.map { newPair ->
//                                oldPairsMap[newPair.pairAddress] ?: newPair
//                            } + oldList.filterNot { oldPair ->
//                                fetchedPairs.any { it.pairAddress == oldPair.pairAddress }
//                            }.sortedBy { pair ->
//                                pair.priceUsd?.toDouble()
//                            }

                            Logger.d(
                                "Old List Size: ${oldList.size}, " +
                                        "Fetched Pairs Size: ${fetchedPairs.size}, "
                                      //  "Merged List Size: ${mergedList.size}"
                            )

                            currentMap[category] =
                                fetchedPairs.distinctBy { it.pairAddress }.sortedBy { pair ->
                                    pair.priceChangeSinceScanned
                                }.reversed()
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
}