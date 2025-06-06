package org.adam.kryptobot.feature.wallet.ui.screens

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.adam.kryptobot.feature.wallet.repository.WalletRepository
import org.adam.kryptobot.feature.wallet.screens.WalletScreenEvent
import org.adam.kryptobot.feature.wallet.screens.WalletScreenUiState
import org.adam.kryptobot.feature.wallet.usecase.TrackCoinsInWalletUseCase

class WalletScreenModel(
    private val walletRepository: WalletRepository,
    private val trackCoinsInWalletUseCase: TrackCoinsInWalletUseCase,
) : ScreenModel {

    private val _privateKeyVisibility = MutableStateFlow(false)

    val uiState: StateFlow<WalletScreenUiState> = combine(
        walletRepository.currentWallet,
        _privateKeyVisibility
    ) { wallet, isPrivateKeyVisible ->
        WalletScreenUiState(
            wallet = wallet,
            isPrivateKeyVisible = isPrivateKeyVisible
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
                    trackCoinsInWalletUseCase()
                }
            }

            is WalletScreenEvent.OnUpdatePrivateKeyClicked -> {
                walletRepository.updateWallet { it.copy(privateAddress = event.privateKey) }
            }

            is WalletScreenEvent.OnUpdatePublicKeyClicked -> {
                walletRepository.updateWallet { it.copy(publicKey = event.publicKey) }
            }

            WalletScreenEvent.OnToggleVisibilityClicked -> {
                _privateKeyVisibility.value = !_privateKeyVisibility.value
            }
        }
    }

}