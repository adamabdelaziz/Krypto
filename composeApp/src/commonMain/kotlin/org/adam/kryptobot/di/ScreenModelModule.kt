package org.adam.kryptobot.di

import org.adam.kryptobot.feature.scanner.screens.ScannerScreenModel
import org.adam.kryptobot.feature.swapper.screens.SwapperScreenModel
import org.adam.kryptobot.feature.wallet.screens.WalletScreenModel
import org.koin.dsl.module

val screenModelsModule = module {
    factory {
        ScannerScreenModel(get())
    }
    factory {
        SwapperScreenModel(
            swapperRepository = get(),
            scannerRepository = get(),
            walletRepository = get(),
        )
    }
    factory {
        WalletScreenModel(
            walletRepository = get(),
        )
    }
}