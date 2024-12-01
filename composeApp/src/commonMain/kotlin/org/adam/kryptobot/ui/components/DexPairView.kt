package org.adam.kryptobot.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.adam.kryptobot.feature.scanner.data.dto.DexPairDto
import org.adam.kryptobot.feature.scanner.data.dto.Pair

@Composable
fun DexPairView(
    headerText: String,
    dexPair: DexPairDto,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val transitionState = updateTransition(targetState = expanded, label = "Expand Transition")
    val columnHeight by transitionState.animateDp(
        transitionSpec = { spring(stiffness = Spring.StiffnessLow) },
        label = "Height Animation"
    ) {
        if (it) 200.dp else 0.dp
    }

    Column(
        modifier = modifier
            .padding(8.dp)
            .background(Color.LightGray, shape = RoundedCornerShape(8.dp))
            .clickable { expanded = !expanded }
    ) {
        Text(
            text = headerText,
            style = MaterialTheme.typography.h6,
            modifier = Modifier.padding(16.dp)
        )

        Box(
            modifier = Modifier
                .clipToBounds()
                .height(columnHeight)
        ) {
            LazyColumn {
                dexPair.pairs?.let {
                    items(it) { pair ->
                        Text(
                            text = pair.liquidity?.usd.toString(),
                            modifier = Modifier.padding(8.dp),
                            style = MaterialTheme.typography.body1
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PairInfoCard(pair: Pair?) {
    if (pair == null) {
        Text("No pair information available")
        return
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Text(
                text = pair.dexId ?: "Unknown DEX",
                style = MaterialTheme.typography.h6
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
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
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Price (USD): ${pair.priceUsd ?: "N/A"}",
                style = MaterialTheme.typography.body1
            )
            Text(
                text = "Liquidity (USD): ${pair.liquidity?.usd ?: "N/A"}",
                style = MaterialTheme.typography.body1
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Pair Address: ${pair.pairAddress ?: "N/A"}",
                style = MaterialTheme.typography.caption
            )
        }
    }
}