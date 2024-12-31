package org.adam.kryptobot.feature.scanner.usecase

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import org.adam.kryptobot.feature.scanner.enum.TokenCategory
import org.adam.kryptobot.feature.scanner.repository.ScannerRepository
import org.adam.kryptobot.util.cancelAndNull

class MonitorTokenAddressesUseCase(
    private val scannerRepository: ScannerRepository,
    private val coroutineScope: CoroutineScope
) {
    companion object {
        private const val SCAN_DELAY = 5000L
    }

    private var monitorJob: Job? = null

    operator fun invoke(tokenCategory: TokenCategory) {
        stop()
        monitorJob = coroutineScope.launch {
            while (true) {
                scannerRepository.getDexPairsByAddressList(tokenCategory)
                delay(SCAN_DELAY)
            }
        }
    }

    fun stop() {
        monitorJob?.cancelAndNull()
    }
}