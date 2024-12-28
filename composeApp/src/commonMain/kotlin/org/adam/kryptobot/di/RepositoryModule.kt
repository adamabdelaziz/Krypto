package org.adam.kryptobot.di

import org.adam.kryptobot.feature.scanner.repository.ScannerRepository
import org.adam.kryptobot.feature.scanner.repository.ScannerRepositoryImpl
import org.adam.kryptobot.feature.swapper.repository.SwapperRepository
import org.adam.kryptobot.feature.swapper.repository.SwapperRepositoryImpl
import org.adam.kryptobot.feature.wallet.repository.WalletRepository
import org.adam.kryptobot.feature.wallet.repository.WalletRepositoryImpl
import org.koin.core.qualifier.named
import org.koin.core.scope.get
import org.koin.dsl.module

val repositoryModule = module {
    single<ScannerRepository> {
        ScannerRepositoryImpl(
            api = get(),
            stateFlowScope = get(named("IoScope"))
        )
    }
    single<SwapperRepository> {
        SwapperRepositoryImpl(
            json = get(),
            stateFlowScope = get(named("IoScope")),
            swapApi = get(),
            solanaApi = get(),
        )
    }
    single<WalletRepository> {
        WalletRepositoryImpl(
            json = get(),
            stateFlowScope = get(named("IoScope")),
            solanaApi = get(),
        )
    }
}