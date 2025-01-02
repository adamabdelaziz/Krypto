package org.adam.kryptobot.feature.scanner.usecase

import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import org.adam.kryptobot.feature.scanner.enum.TokenCategory
import org.adam.kryptobot.feature.scanner.repository.ScannerRepository
import org.adam.kryptobot.ui.components.snackbar.SnackbarManager
import org.adam.kryptobot.util.cancelAndNull

interface MonitorTokenAddressesUseCase {
    operator fun invoke(tokenCategory: TokenCategory?)
    fun stop()
}

class MonitorTokenAddressesUseCaseImpl(
    private val scannerRepository: ScannerRepository,
    private val coroutineScope: CoroutineScope,
    private val snackbarManager: SnackbarManager,
): MonitorTokenAddressesUseCase {
    companion object {
        private const val SCAN_DELAY = 5000L
    }

    private var monitorJob: Job? = null

    override operator fun invoke(tokenCategory: TokenCategory?) {
        stop()

        scannerRepository.changeCategory(tokenCategory)
        coroutineScope.launch {
            tokenCategory?.let {
                scannerRepository.getTokens(it)
            }
        }

        monitorJob = coroutineScope.launch {
            while (true) {
                scannerRepository.getDexPairsByAddressList(tokenCategory)
                delay(SCAN_DELAY)
            }
        }
    }

    override fun stop() {
        monitorJob?.cancelAndNull()
    }
}