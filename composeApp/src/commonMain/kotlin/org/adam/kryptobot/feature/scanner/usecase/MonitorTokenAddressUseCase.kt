package org.adam.kryptobot.feature.scanner.usecase

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.adam.kryptobot.feature.scanner.enum.TokenCategory
import org.adam.kryptobot.feature.scanner.repository.ScannerRepository
import org.adam.kryptobot.feature.scanner.usecase.MonitorTokenAddressesUseCase.Companion.SCAN_DELAY
import org.adam.kryptobot.ui.components.snackbar.SnackbarManager
import org.adam.kryptobot.util.cancelAndNull

interface MonitorTokenAddressesUseCase {
    operator fun invoke(tokenCategory: TokenCategory?, delay: Long = SCAN_DELAY)
    fun stop()

    companion object {
        const val SCAN_DELAY = 5000L
        const val SWAP_SCAN_DELAY = 3000L
    }
}

class MonitorTokenAddressesUseCaseImpl(
    private val scannerRepository: ScannerRepository,
    private val coroutineScope: CoroutineScope,
    private val snackbarManager: SnackbarManager,
): MonitorTokenAddressesUseCase {
    private var monitorJob: Job? = null

    override operator fun invoke(tokenCategory: TokenCategory?, delay: Long) {
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