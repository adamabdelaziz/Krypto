package org.adam.kryptobot.feature.wallet.screens

import java.security.PrivateKey

sealed class WalletScreenEvent {
    data object OnRefreshBalanceClicked : WalletScreenEvent()
    data object OnToggleVisibilityClicked: WalletScreenEvent()
    data class OnUpdatePublicKeyClicked(val publicKey: String) : WalletScreenEvent()
    data class OnUpdatePrivateKeyClicked(val privateKey: String) : WalletScreenEvent()
}