package com.sankranthivinod.tradetracker.presentation

import com.sankranthivinod.tradetracker.data.local.TradeEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class CumulativePoint(
    val timestamp: Long,
    val tradeAmount: Double,
    val cumulativePnL: Double,
    val ticker: String,
    val instrumentType: String
)

data class TradeUiState(
    val trades: List<TradeEntity> = emptyList(),
    val selectedFilter: String = "All",
    val instrumentTypeInput: String = "Options",
    val optionTypeInput: String = "Call",
    val amountInput: String = "",
    val tickerInput: String = "",
    val amountError: String? = null,
    val showAboutDialog: Boolean = false,
    val showClearConfirmDialog: Boolean = false,
    val exportCsvData: String? = null,

    // Admin Rules State
    val rulesEnabled: Boolean = false,
    val maxLossPerTrade: Double = 500.0,
    val maxLossPerDay: Double = 1000.0,
    val showAdminPasswordDialog: Boolean = false,
    val adminPasswordInput: String = "",
    val adminPasswordError: String? = null,
    val isAdminAuthenticated: Boolean = false
) {
    val filteredTrades: List<TradeEntity>
        get() = if (selectedFilter == "All") {
            trades
        } else {
            trades.filter { it.instrumentType.equals(selectedFilter, ignoreCase = true) }
        }

    val totalNetPnL: Double
        get() = trades.sumOf { it.amount }

    val totalTradesCount: Int
        get() = trades.size

    val winCount: Int
        get() = trades.count { it.amount > 0 }

    val lossCount: Int
        get() = trades.count { it.amount < 0 }

    val winRate: Double
        get() = if (totalTradesCount > 0) (winCount.toDouble() / totalTradesCount) * 100 else 0.0

    val largestWin: Double
        get() = trades.filter { it.amount > 0 }.maxOfOrNull { it.amount } ?: 0.0

    val largestLoss: Double
        get() = trades.filter { it.amount < 0 }.minOfOrNull { it.amount } ?: 0.0

    val callsCount: Int
        get() = trades.count { it.optionType.equals("Call", ignoreCase = true) }

    val putsCount: Int
        get() = trades.count { it.optionType.equals("Put", ignoreCase = true) }

    val callsPnL: Double
        get() = trades.filter { it.optionType.equals("Call", ignoreCase = true) }.sumOf { it.amount }

    val putsPnL: Double
        get() = trades.filter { it.optionType.equals("Put", ignoreCase = true) }.sumOf { it.amount }

    // Cumulative P&L over time (chronological order)
    val cumulativePnLPoints: List<CumulativePoint>
        get() {
            val sortedList = filteredTrades.sortedBy { it.timestamp }
            var runningTotal = 0.0
            return sortedList.map { trade ->
                runningTotal += trade.amount
                CumulativePoint(
                    timestamp = trade.timestamp,
                    tradeAmount = trade.amount,
                    cumulativePnL = runningTotal,
                    ticker = trade.ticker,
                    instrumentType = trade.instrumentType
                )
            }
        }

    // Rule Violation Calculations
    fun isTradeLossViolated(trade: TradeEntity): Boolean {
        if (!rulesEnabled) return false
        if (trade.amount >= 0) return false
        val lossAmount = kotlin.math.abs(trade.amount)
        return lossAmount > maxLossPerTrade
    }

    fun isDayLossViolated(timestamp: Long): Boolean {
        if (!rulesEnabled) return false
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val targetDateStr = dateFormat.format(Date(timestamp))
        val dailyLoss = trades
            .filter { dateFormat.format(Date(it.timestamp)) == targetDateStr && it.amount < 0 }
            .sumOf { kotlin.math.abs(it.amount) }
        return dailyLoss > maxLossPerDay
    }

    fun getDailyLossForTimestamp(timestamp: Long): Double {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val targetDateStr = dateFormat.format(Date(timestamp))
        return trades
            .filter { dateFormat.format(Date(it.timestamp)) == targetDateStr && it.amount < 0 }
            .sumOf { kotlin.math.abs(it.amount) }
    }
}
