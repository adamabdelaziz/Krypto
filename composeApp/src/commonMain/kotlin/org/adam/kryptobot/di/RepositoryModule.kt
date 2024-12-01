package org.adam.kryptobot.di

import org.adam.kryptobot.feature.scanner.repository.ScannerRepository
import org.adam.kryptobot.feature.scanner.repository.ScannerRepositoryImpl
import org.koin.core.qualifier.named
import org.koin.dsl.module

val repositoryModule = module {
    single<ScannerRepository> {
        ScannerRepositoryImpl(
            api = get(),
            stateFlowScope = get(named("IoScope"))
        )
    }
}