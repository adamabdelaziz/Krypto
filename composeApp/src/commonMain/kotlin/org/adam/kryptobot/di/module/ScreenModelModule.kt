package org.adam.kryptobot.di.module

import org.adam.kryptobot.feature.scanner.ui.screens.ScannerScreenModel
import org.adam.kryptobot.feature.swapper.ui.screens.SwapperScreenModel
import org.adam.kryptobot.feature.wallet.ui.screens.WalletScreenModel
import org.koin.dsl.module

val screenModelsModule = module {
    factory {
        ScannerScreenModel(scannerRepository = get(), monitorTokenAddresses = get(), trackCoinsInWalletUseCase = get(), trackPairUseCase = get())
    }
    factory {
        SwapperScreenModel(
            swapperRepository = get(),
            scannerRepository = get(),
            walletRepository = get(),
            monitorTokenAddressesUseCase = get(),
        )
    }
    factory {
        WalletScreenModel(
            walletRepository = get(),
            trackCoinsInWalletUseCase = get(),
        )
    }
}