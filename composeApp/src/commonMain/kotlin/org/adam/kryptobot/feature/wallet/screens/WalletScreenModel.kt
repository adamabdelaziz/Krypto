package org.adam.kryptobot.feature.wallet.screens

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.adam.kryptobot.feature.swapper.screens.SwapperScreenUiState
import org.adam.kryptobot.feature.wallet.repository.WalletRepository

class WalletScreenModel(
    private val walletRepository: WalletRepository
) : ScreenModel {

    val uiState: StateFlow<WalletScreenUiState> = walletRepository.currentWallet
        .map { wallet ->
            WalletScreenUiState(
                wallet = wallet
            )
        }.stateIn(
            scope = screenModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = WalletScreenUiState()
        )

    fun onEvent(event: WalletScreenEvent) {
        when (event) {
            WalletScreenEvent.OnRefreshBalanceClicked -> {
                screenModelScope.launch {
                    walletRepository.refreshBalance()
                }

            }

            is WalletScreenEvent.OnUpdatePrivateKeyClicked -> {
                walletRepository.updateWallet { it.copy(privateAddress = event.privateKey) }
            }

            is WalletScreenEvent.OnUpdatePublicKeyClicked -> {
                walletRepository.updateWallet { it.copy(publicKey = event.publicKey) }
            }
        }
    }
}