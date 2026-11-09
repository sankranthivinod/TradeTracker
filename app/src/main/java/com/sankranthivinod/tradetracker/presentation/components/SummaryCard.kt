package com.sankranthivinod.tradetracker.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sankranthivinod.tradetracker.presentation.TradeUiState
import com.sankranthivinod.tradetracker.ui.theme.LossRed
import com.sankranthivinod.tradetracker.ui.theme.ProfitGreen
import java.util.Locale

fun formatCurrency(amount: Double): String {
    val absAmount = kotlin.math.abs(amount)
    val formatted = String.format(Locale.US, "%.2f", absAmount)
    return if (amount > 0) "+$$formatted" else if (amount < 0) "-$$formatted" else "$$formatted"
}

@Composable
fun SummaryHeader(
    uiState: TradeUiState,
    modifier: Modifier = Modifier
) {
    val totalPnL = uiState.totalNetPnL
    val pnlColor = if (totalPnL > 0) ProfitGreen else if (totalPnL < 0) LossRed else MaterialTheme.colorScheme.onSurface
    val pnlIcon = if (totalPnL >= 0) Icons.AutoMirrored.Filled.TrendingUp else Icons.AutoMirrored.Filled.TrendingDown

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Main Net P&L Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Total Net Profit / Loss",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Icon(
                        imageVector = pnlIcon,
                        contentDescription = null,
                        tint = pnlColor
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = formatCurrency(totalPnL),
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 32.sp
                    ),
                    color = pnlColor
                )
            }
        }

        // Quick Metrics Cards (Grid of 4)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MetricCard(
                label = "Win Rate",
                value = String.format(Locale.US, "%.1f%%", uiState.winRate),
                valueColor = if (uiState.winRate >= 50) ProfitGreen else LossRed,
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                label = "Total Trades",
                value = uiState.totalTradesCount.toString(),
                valueColor = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MetricCard(
                label = "Largest Win",
                value = formatCurrency(uiState.largestWin),
                valueColor = ProfitGreen,
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                label = "Largest Loss",
                value = formatCurrency(uiState.largestLoss),
                valueColor = LossRed,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun MetricCard(
    label: String,
    value: String,
    valueColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = valueColor
            )
        }
    }
}
