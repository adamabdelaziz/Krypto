package org.adam.kryptobot.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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