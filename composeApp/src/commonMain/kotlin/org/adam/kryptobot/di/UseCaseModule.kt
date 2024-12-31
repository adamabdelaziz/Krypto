package org.adam.kryptobot.di

import org.adam.kryptobot.feature.scanner.usecase.MonitorTokenAddressesUseCase
import org.koin.core.qualifier.named
import org.koin.dsl.module

val useCaseModule = module {
    single {
        MonitorTokenAddressesUseCase(
            scannerRepository = get(),
            coroutineScope = get(named("IoScope")),
        )
    }
}