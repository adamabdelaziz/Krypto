package org.adam.kryptobot.feature.wallet.usecase

import co.touchlab.kermit.Logger
import org.adam.kryptobot.feature.scanner.repository.ScannerRepository
import org.adam.kryptobot.feature.wallet.repository.WalletRepository

interface TrackCoinsInWalletUseCase {
    suspend operator fun invoke()
}

class TrackCoinsInWalletUseCaseImpl(
    private val scannerRepository: ScannerRepository,
    private val walletRepository: WalletRepository,
) : TrackCoinsInWalletUseCase {

    override suspend operator fun invoke() {
        walletRepository.refreshBalance()
        val addressList = walletRepository.currentWallet.value.tokenBalance.map { it.first }
        addressList.forEach {
            Logger.d("Wallet address is $it")
            scannerRepository.trackPair(it, toggle = false)
        }
    }
}