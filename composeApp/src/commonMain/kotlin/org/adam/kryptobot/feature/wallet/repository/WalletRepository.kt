package org.adam.kryptobot.feature.wallet.repository

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.serialization.json.Json
import org.adam.kryptobot.feature.swapper.data.SolanaApi
import org.adam.kryptobot.feature.wallet.data.model.Wallet
import org.adam.kryptobot.util.SECOND_WALLET_PRIVATE_KEY
import org.adam.kryptobot.util.SECOND_WALLET_PUBLIC_KEY

interface WalletRepository {
    val currentWallet: StateFlow<Wallet>

    suspend fun refreshBalance()
    fun updateWallet(update: (Wallet) -> Wallet)
}

class WalletRepositoryImpl(
    private val json: Json,
    private val stateFlowScope: CoroutineScope,
    private val solanaApi: SolanaApi,
) : WalletRepository {

    private val _currentWallet: MutableStateFlow<Wallet> = MutableStateFlow(
        Wallet(
            publicKey = SECOND_WALLET_PUBLIC_KEY,
            privateAddress = SECOND_WALLET_PRIVATE_KEY,
        )
    )
    override val currentWallet: StateFlow<Wallet> = _currentWallet.stateIn(
        scope = stateFlowScope,
        initialValue = _currentWallet.value,
        started = SharingStarted.WhileSubscribed(5000),
    )

    override fun updateWallet(update: (Wallet) -> Wallet) {
        _currentWallet.value = update(_currentWallet.value)
    }

    override suspend fun refreshBalance() {
        val balance = solanaApi.getWalletBalance(SECOND_WALLET_PUBLIC_KEY)
        solanaApi.createATAForWSOL(ownerWalletAddress = SECOND_WALLET_PUBLIC_KEY)
        val tokenBalances = solanaApi.getTokenBalances(SECOND_WALLET_PUBLIC_KEY)
        updateWallet {
            it.copy(balance = balance.toString(), tokenBalance = tokenBalances)
        }
    }
}