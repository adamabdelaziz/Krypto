package org.adam.kryptobot.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.adam.kryptobot.feature.scanner.data.dto.PaymentStatusDto
import org.adam.kryptobot.feature.scanner.data.dto.TxCount
import org.adam.kryptobot.feature.scanner.ui.model.DexPairUiModel
import org.adam.kryptobot.util.formatToDollarString
import org.adam.kryptobot.util.formatUnixTimestamp

@Composable
fun PairInfoCard(modifier: Modifier = Modifier, pair: DexPairUiModel?, onClick: () -> Unit) {
    if (pair == null) {
        Text("No pair information available")
        return
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            }
            .padding(8.dp),
        elevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = pair.dexId ?: "Unknown DEX",
                    style = MaterialTheme.typography.h6
                )

                Text(
                    text = "${String.format("%.4f", pair.priceChangeSinceScanned)}%",
                    style = MaterialTheme.typography.h6
                )

                Text(
                    text = "${String.format("%.4f", pair.recentPriceChangeSinceScanned)}%",
                    style = MaterialTheme.typography.h6
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Base Token: ${pair.baseToken?.name ?: "N/A"}",
                        style = MaterialTheme.typography.body1
                    )
                    Text(
                        text = "Symbol: ${pair.baseToken?.symbol ?: "N/A"}",
                        style = MaterialTheme.typography.body2
                    )
                }

                Column {
                    Text(
                        text = "Quote Token: ${pair.quoteToken?.name ?: "N/A"}",
                        style = MaterialTheme.typography.body1
                    )
                    Text(
                        text = "Symbol: ${pair.quoteToken?.symbol ?: "N/A"}",
                        style = MaterialTheme.typography.body2
                    )
                }

                Column {
                    Text(
                        text = "Price (USD): ${pair.priceUsd ?: "N/A"}",
                        style = MaterialTheme.typography.body1
                    )

                    Text(
                        text = "Liquidity (USD): ${pair.liquidity?.usd?.formatToDollarString() ?: "N/A"}",
                        style = MaterialTheme.typography.body1
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Market Cap: ${pair.marketCap?.formatToDollarString() ?: "N/A"}",
                    style = MaterialTheme.typography.body1
                )
                Text(
                    text = "Ratio: ${String.format("%.4f", pair.liquidityMarketRatio)}%",
                    style = MaterialTheme.typography.body1
                )
                Text(
                    text = "FDV: ${pair.fdv?.formatToDollarString() ?: "N/A"}",
                    style = MaterialTheme.typography.body1
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Metric",
                            style = MaterialTheme.typography.subtitle1,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "5min",
                            style = MaterialTheme.typography.subtitle1,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "1hr",
                            style = MaterialTheme.typography.subtitle1,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "6hr",
                            style = MaterialTheme.typography.subtitle1,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "24hr",
                            style = MaterialTheme.typography.subtitle1,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Volume:",
                            style = MaterialTheme.typography.body1,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "${pair.volume?.m5 ?: "N/A"}",
                            style = MaterialTheme.typography.body1,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "${pair.volume?.h1 ?: "N/A"}",
                            style = MaterialTheme.typography.body1,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "${pair.volume?.h6 ?: "N/A"}",
                            style = MaterialTheme.typography.body1,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "${pair.volume?.h24 ?: "N/A"}",
                            style = MaterialTheme.typography.body1,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Price Change:",
                            style = MaterialTheme.typography.body1,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "${pair.priceChange?.m5 ?: "N/A"}",
                            style = MaterialTheme.typography.body1,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "${pair.priceChange?.h1 ?: "N/A"}",
                            style = MaterialTheme.typography.body1,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "${pair.priceChange?.h6 ?: "N/A"}",
                            style = MaterialTheme.typography.body1,
                            modifier = Modifier.weight(1f)
                        )

                        Text(
                            text = "${pair.priceChange?.h24 ?: "N/A"}",
                            style = MaterialTheme.typography.body1,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Transactions:",
                            style = MaterialTheme.typography.body1,
                            modifier = Modifier.weight(1f)
                        )
                        TransactionCountText(
                            transactionCount = pair.transactions?.m5,
                            modifier = Modifier.weight(0.5f)
                        )
                        TransactionCountText(
                            transactionCount = pair.transactions?.h1,
                            modifier = Modifier.weight(0.5f)
                        )
                        TransactionCountText(
                            transactionCount = pair.transactions?.h6,
                            modifier = Modifier.weight(0.5f)
                        )
                        TransactionCountText(
                            transactionCount = pair.transactions?.h24,
                            modifier = Modifier.weight(0.5f)
                        )
                    }
                }
            }


            Spacer(modifier = Modifier.height(8.dp))

        }
    }
}

@Composable
fun TransactionCountText(modifier: Modifier = Modifier, transactionCount: TxCount?) {
    Text(
        text = "${transactionCount?.buys ?: "N/A"}",
        style = MaterialTheme.typography.body1,
        modifier = modifier.padding(end = 8.dp),
        color = Color.Green,
    )
    Text(
        text = "${transactionCount?.sells ?: "N/A"}",
        style = MaterialTheme.typography.body1,
        modifier = modifier,
        color = Color.Red,
    )
}

@Composable
fun PaymentStatusCard(modifier: Modifier = Modifier, paymentStatus: PaymentStatusDto) {
    Card(modifier = Modifier.fillMaxWidth().padding(8.dp), elevation = 8.dp) {
        Column(modifier = Modifier.fillMaxWidth().padding(4.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(paymentStatus.status)
                Text(paymentStatus.type)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Text(formatUnixTimestamp(paymentStatus.paymentTimestamp))
            }
        }
    }
}