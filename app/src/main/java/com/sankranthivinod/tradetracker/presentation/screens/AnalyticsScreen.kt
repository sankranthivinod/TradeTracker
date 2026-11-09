package com.sankranthivinod.tradetracker.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sankranthivinod.tradetracker.presentation.TradeUiState
import com.sankranthivinod.tradetracker.presentation.TradeViewModel
import com.sankranthivinod.tradetracker.presentation.components.TrendCanvasChart
import com.sankranthivinod.tradetracker.presentation.components.formatCurrency
import com.sankranthivinod.tradetracker.ui.theme.LossRed
import com.sankranthivinod.tradetracker.ui.theme.ProfitGreen
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    uiState: TradeUiState,
    viewModel: TradeViewModel,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val filterOptions = remember { listOf("All", "Options", "Stocks", "Futures", "Crypto") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Filter Chips Row
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Filter Trend by Instrument",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                filterOptions.forEach { filter ->
                    FilterChip(
                        selected = uiState.selectedFilter == filter,
                        onClick = { viewModel.onFilterSelected(filter) },
                        label = { Text(filter) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            }
        }

        // Cumulative P&L Canvas Chart
        TrendCanvasChart(points = uiState.cumulativePnLPoints)

        // Win vs Loss Ratio Breakdown
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
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Win / Loss Breakdown",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )

                val winRatioFloat = if (uiState.totalTradesCount > 0) {
                    (uiState.winCount.toFloat() / uiState.totalTradesCount)
                } else 0f

                LinearProgressIndicator(
                    progress = { winRatioFloat },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp),
                    color = ProfitGreen,
                    trackColor = LossRed,
                    strokeCap = StrokeCap.Round
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Winning Trades",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${uiState.winCount} (${String.format(Locale.US, "%.1f%%", uiState.winRate)})",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = ProfitGreen
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Losing Trades",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        val lossRate = if (uiState.totalTradesCount > 0) 100.0 - uiState.winRate else 0.0
                        Text(
                            text = "${uiState.lossCount} (${String.format(Locale.US, "%.1f%%", lossRate)})",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = LossRed
                        )
                    }
                }
            }
        }

        // Options Calls vs Puts Breakdown Card
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
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Options Performance (Calls vs Puts)",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Calls Performance
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "Calls (${uiState.callsCount} trades)",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = formatCurrency(uiState.callsPnL),
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = if (uiState.callsPnL >= 0) ProfitGreen else LossRed
                            )
                        }
                    }

                    // Puts Performance
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "Puts (${uiState.putsCount} trades)",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = formatCurrency(uiState.putsPnL),
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = if (uiState.putsPnL >= 0) ProfitGreen else LossRed
                            )
                        }
                    }
                }
            }
        }
    }
}
