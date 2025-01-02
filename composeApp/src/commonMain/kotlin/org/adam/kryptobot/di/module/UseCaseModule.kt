package org.adam.kryptobot.di.module

import org.adam.kryptobot.di.IO_SCOPE
import org.adam.kryptobot.feature.scanner.usecase.MonitorTokenAddressesUseCase
import org.adam.kryptobot.feature.scanner.usecase.MonitorTokenAddressesUseCaseImpl
import org.koin.core.qualifier.named
import org.koin.dsl.module

val useCaseModule = module {
    single<MonitorTokenAddressesUseCase> {
        MonitorTokenAddressesUseCaseImpl(
            scannerRepository = get(),
            coroutineScope = get(named(IO_SCOPE)),
            snackbarManager = get(),
        )
    }
}