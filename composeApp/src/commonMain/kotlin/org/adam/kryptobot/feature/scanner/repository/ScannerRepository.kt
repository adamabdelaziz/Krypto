package org.adam.kryptobot.feature.scanner.repository

import co.touchlab.kermit.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext
import org.adam.kryptobot.feature.scanner.data.DexScannerApi
import org.adam.kryptobot.feature.scanner.data.dto.PaymentStatusDto
import org.adam.kryptobot.feature.scanner.data.dto.toToken
import org.adam.kryptobot.feature.scanner.data.mappers.toDexPair
import org.adam.kryptobot.feature.scanner.data.model.DexPair
import org.adam.kryptobot.feature.scanner.data.model.Token
import org.adam.kryptobot.feature.scanner.enum.TokenCategory
import org.adam.kryptobot.ui.components.snackbar.SnackbarManager
import org.adam.kryptobot.util.SOLANA_MINT_ADDRESS

interface ScannerRepository {
    suspend fun getTokens(tokenCategory: TokenCategory)

    suspend fun getDexPairsByAddressList(category: TokenCategory?) // This is supposed to be multiple comma separated token addresses
    suspend fun getDexPairsByChainAndAddress(chainId: String, tokenAddress: String)
    suspend fun getOrdersPaidFor(chainId: String, tokenAddress: String)

    fun trackPair(baseTokenAddress: String?, toggle: Boolean = true)
    fun changeCategory(tokenCategory: TokenCategory?)

    val trackedTokenAddresses: StateFlow<Set<String>>
    val tokens: StateFlow<List<Token>>
    val latestDexPairs: StateFlow<List<DexPair>>
    val ordersPaidForByTokenAddress: StateFlow<List<PaymentStatusDto>>
    val selectedTokenCategory: StateFlow<TokenCategory?>
}

class ScannerRepositoryImpl(
    private val api: DexScannerApi,
    private val stateFlowScope: CoroutineScope,
    private val snackbarManager: SnackbarManager,
) : ScannerRepository {

    private val _tokens: MutableStateFlow<List<Token>> =
        MutableStateFlow(listOf())
    override val tokens: StateFlow<List<Token>> =
        _tokens.stateIn(
            scope = stateFlowScope,
            initialValue = _tokens.value,
            started = SharingStarted.WhileSubscribed(5000),
        )
    private val _selectedTokenCategory: MutableStateFlow<TokenCategory?> =
        MutableStateFlow(TokenCategory.MostActiveBoosted)
    override val selectedTokenCategory: StateFlow<TokenCategory?> =
        _selectedTokenCategory.stateIn(
            scope = stateFlowScope,
            initialValue = _selectedTokenCategory.value,
            started = SharingStarted.WhileSubscribed(5000),
        )

    private val _latestDexPairs: MutableStateFlow<List<DexPair>> =
        MutableStateFlow(listOf())
    override val latestDexPairs: StateFlow<List<DexPair>> =
        combine(
            _latestDexPairs,
            _selectedTokenCategory
        ) { dexPairs, selectedCategory ->
            dexPairs.filter {
                when (selectedCategory) {
                    TokenCategory.LatestBoosted -> {
                        latestBoostedTokenAddresses.contains(it.baseToken?.address)
                    }

                    TokenCategory.MostActiveBoosted -> {
                        mostActiveBoostedTokenAddresses.contains(it.baseToken?.address)
                    }

                    TokenCategory.Latest -> {
                        latestTokenAddresses.contains(it.baseToken?.address)
                    }

                    else -> {
                        trackedTokenAddresses.value.contains(it.baseToken?.address)
                    }
                }
            }
        }.stateIn(
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

    private val initialPairs = mutableMapOf<String, DexPair>()

    private val _trackedTokenAddresses: MutableStateFlow<Set<String>> = MutableStateFlow(setOf())
    override val trackedTokenAddresses = _trackedTokenAddresses.stateIn(
        scope = stateFlowScope,
        initialValue = _trackedTokenAddresses.value,
        started = SharingStarted.WhileSubscribed(5000),
    )

    private val latestBoostedTokenAddresses = mutableSetOf<String>()
    private val mostActiveBoostedTokenAddresses = mutableSetOf<String>()
    private val latestTokenAddresses = mutableSetOf<String>()

    override suspend fun getTokens(tokenCategory: TokenCategory) {
        withContext(Dispatchers.IO) {
            try {
                val response: List<Token>

                when (tokenCategory) {
                    TokenCategory.LatestBoosted -> {
                        response = api.getLatestBoostedTokens().map { it.toToken() }
                        latestBoostedTokenAddresses.addAll(response.map { it.tokenAddress })
                    }

                    TokenCategory.MostActiveBoosted -> {
                        response = api.getMostActiveBoostedTokens().map { it.toToken() }
                        mostActiveBoostedTokenAddresses.addAll(response.map { it.tokenAddress })
                    }

                    TokenCategory.Latest -> {
                        response = api.getLatestTokens().map { it.toToken() }
                        latestTokenAddresses.addAll(response.map { it.tokenAddress })
                    }
                }

                val updatedTokens = _tokens.value.toMutableList()

                response.forEach { newToken ->
                    updatedTokens.removeIf { it.tokenAddress == newToken.tokenAddress }
                    updatedTokens.add(newToken)
                }
                Logger.d("Token size  is ${updatedTokens.size}")
                _tokens.value = updatedTokens

            } catch (e: Exception) {
                Logger.d(e.message ?: " Null Error Message for getLatestTokens()")
            }
        }
    }

    override suspend fun getDexPairsByAddressList(category: TokenCategory?) {
        withContext(Dispatchers.IO) {
            try {
                val addresses = when (category) {
                    TokenCategory.LatestBoosted -> {
                        latestBoostedTokenAddresses.take(29)
                    }

                    TokenCategory.MostActiveBoosted -> {
                        mostActiveBoostedTokenAddresses.take(29)
                    }

                    TokenCategory.Latest -> {
                        latestTokenAddresses.take(29)
                    }

                    else -> {
                        _trackedTokenAddresses.value.take(29)
                    }
                }.distinct().joinToString(",")

                if (addresses.isNotEmpty()) {
                    Logger.d("Tracked size is ${_trackedTokenAddresses.value.size}")
                    val response = api.getPairsByTokenAddress(addresses)

                    response?.let {
                        val oldList = _latestDexPairs.value.toMutableList()

                        //Mapping and logic goes here.
                        val pairs =
                            it.pairs?.map { it.toDexPair(oldList, initialPairs.values.toList()) }

                        pairs?.let { fetchedPairs ->
                            fetchedPairs.forEach { pair ->
                                pair.baseToken?.address.let { key ->
                                    if (!initialPairs.containsKey(key) && key != null) {
                                        initialPairs[key] = pair
                                    }
                                }

                                oldList.removeIf { it.pairAddress == pair.pairAddress }
                                oldList.add(pair)
                                /*
                                Quote token is SOL/USDC/WETH etc
                                Base token is shit coin
                                Pair address is likely real unique identifier but likely should filter all quote tokens that arent SOL i.e address == So11111111111111111111111111111111111111112
                                Multiple different pair addresses can exist for same base token/quote token pair since there are multiple liquditiy pools so tracking all of them and doing it by coin for now
                                 */
                                // Logger.d("${pair.chainId} ${pair.dexId} ${pair.pairAddress} | ${pair.baseToken?.address} ${pair.baseToken?.symbol} | ${pair.quoteToken?.address} ${pair.quoteToken?.symbol}")
                            }
                        }
                        Logger.d("DEx Size is ${oldList.size}")

                        _latestDexPairs.value =
                            oldList.toList().distinctBy { it.pairAddress }
                                .sortedBy { it.priceChangeSinceScanned }.reversed()
                                .filter { it.quoteToken?.address == SOLANA_MINT_ADDRESS }
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

    override fun trackPair(baseTokenAddress: String?, toggle: Boolean) {
        baseTokenAddress?.let {
            val trackedSet = _trackedTokenAddresses.value.toMutableSet()
            val alreadyAdded = trackedSet.add(it)

            if (toggle && !alreadyAdded) {
                trackedSet.remove(it)
            }

            _trackedTokenAddresses.value = trackedSet
        }
    }

    override fun changeCategory(tokenCategory: TokenCategory?) {
        _selectedTokenCategory.value = tokenCategory
    }
}