package org.adam.kryptobot.di

import org.adam.kryptobot.feature.scanner.data.DexScannerApi
import org.adam.kryptobot.feature.scanner.data.KtorDexScannerApi
import org.adam.kryptobot.feature.swapper.data.JupiterSwapApi
import org.adam.kryptobot.feature.swapper.data.KtorJupiterSwapApi
import org.adam.kryptobot.feature.swapper.data.SolanaApi
import org.adam.kryptobot.feature.swapper.data.SolanaApiImpl
import org.koin.dsl.module

val dataModule = module {
    single<DexScannerApi> { KtorDexScannerApi(get()) }
    single<JupiterSwapApi> { KtorJupiterSwapApi(get()) }
    single<SolanaApi> { SolanaApiImpl(get())}
}