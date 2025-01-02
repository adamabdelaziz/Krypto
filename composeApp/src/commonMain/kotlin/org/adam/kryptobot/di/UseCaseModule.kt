package org.adam.kryptobot.di

import org.adam.kryptobot.feature.scanner.usecase.MonitorTokenAddressesUseCase
import org.adam.kryptobot.feature.scanner.usecase.MonitorTokenAddressesUseCaseImpl
import org.koin.core.qualifier.named
import org.koin.dsl.module

val useCaseModule = module {
    single<MonitorTokenAddressesUseCase> {
        MonitorTokenAddressesUseCaseImpl(
            scannerRepository = get(),
            coroutineScope = get(named("IoScope")),
        )
    }
}