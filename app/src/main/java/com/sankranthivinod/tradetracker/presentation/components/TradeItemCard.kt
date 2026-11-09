package com.sankranthivinod.tradetracker.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sankranthivinod.tradetracker.data.local.TradeEntity
import com.sankranthivinod.tradetracker.ui.theme.LossRed
import com.sankranthivinod.tradetracker.ui.theme.ProfitGreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TradeItemCard(
    trade: TradeEntity,
    onDeleteTrade: (TradeEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val isProfit = trade.amount >= 0
    val amountColor = if (isProfit) ProfitGreen else LossRed
    val dateFormat = SimpleDateFormat("MMM dd, yyyy • HH:mm", Locale.getDefault())
    val dateString = dateFormat.format(Date(trade.timestamp))

    val badgeLabel = if (trade.instrumentType == "Options" && trade.optionType != null) {
        "${trade.instrumentType} • ${trade.optionType}"
    } else {
        trade.instrumentType
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = trade.ticker,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    AssistChip(
                        onClick = { },
                        label = {
                            Text(
                                text = badgeLabel,
                                style = MaterialTheme.typography.labelSmall
                            )
                        },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                            labelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = dateString,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = formatCurrency(trade.amount),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = amountColor
                )

                Spacer(modifier = Modifier.width(4.dp))

                IconButton(onClick = { onDeleteTrade(trade) }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Trade",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}
