package org.adam.kryptobot.di.module

import org.adam.kryptobot.di.IO_SCOPE
import org.adam.kryptobot.feature.scanner.usecase.MonitorTokenAddressesUseCase
import org.adam.kryptobot.feature.scanner.usecase.TrackPairUseCase
import org.adam.kryptobot.feature.wallet.usecase.TrackCoinsInWalletUseCase

import org.koin.core.qualifier.named
import org.koin.dsl.module

val useCaseModule = module {
    single {
        MonitorTokenAddressesUseCase(
            scannerRepository = get(),
            coroutineScope = get(named(IO_SCOPE)),
            snackbarManager = get(),
        )
    }
    single {
        TrackCoinsInWalletUseCase(
            scannerRepository = get(),
            walletRepository = get(),
        )
    }
    single {
        TrackPairUseCase(
            scannerRepository = get(),
            walletRepository = get(),
        )
    }
}