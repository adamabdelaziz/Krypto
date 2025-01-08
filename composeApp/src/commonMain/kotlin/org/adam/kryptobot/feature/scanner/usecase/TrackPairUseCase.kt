package org.adam.kryptobot.feature.scanner.usecase

import org.adam.kryptobot.feature.scanner.repository.ScannerRepository
import org.adam.kryptobot.feature.wallet.repository.WalletRepository


class TrackPairUseCase(
    private val scannerRepository: ScannerRepository,
    private val walletRepository: WalletRepository,
) {
    operator fun invoke(tokenAddress: String?) {
        tokenAddress?.let {
            val walletTokens = walletRepository.currentWallet.value.tokenList
            if (walletTokens.contains(tokenAddress)) {
                scannerRepository.trackPair(tokenAddress, false)
            } else {
                scannerRepository.trackPair(tokenAddress, true)
            }
        }
    }
}