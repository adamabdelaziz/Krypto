import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import cafe.adriel.voyager.transitions.SlideTransition
import org.adam.kryptobot.feature.swapper.screens.SwapperScreen
import org.adam.kryptobot.feature.wallet.screens.WalletScreen

object WalletTab : Tab {

    override val options: TabOptions
        @Composable
        get() {
            val title = "Wallet"

            return remember {
                TabOptions(
                    index = 0u,
                    title = title,
                    icon = null
                )
            }
        }

    @Composable
    override fun Content() {
        Navigator(screen = WalletScreen()) { navigator ->
            SlideTransition(navigator = navigator)
        }
    }
}