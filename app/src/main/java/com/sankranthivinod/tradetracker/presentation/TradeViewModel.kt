package com.sankranthivinod.tradetracker.presentation

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.sankranthivinod.tradetracker.data.local.TradeDatabase
import com.sankranthivinod.tradetracker.data.local.TradeEntity
import com.sankranthivinod.tradetracker.data.repository.TradeRepository
import com.sankranthivinod.tradetracker.data.repository.TradeRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TradeViewModel(private val repository: TradeRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(TradeUiState())
    val uiState: StateFlow<TradeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.allTrades.collect { tradesList ->
                _uiState.update { it.copy(trades = tradesList) }
            }
        }
    }

    fun onInstrumentTypeChanged(type: String) {
        _uiState.update {
            it.copy(
                instrumentTypeInput = type,
                // reset option type if not options
                optionTypeInput = if (type == "Options") "Call" else "Call"
            )
        }
    }

    fun onOptionTypeChanged(type: String) {
        _uiState.update { it.copy(optionTypeInput = type) }
    }

    fun onAmountInputChanged(amount: String) {
        _uiState.update { it.copy(amountInput = amount, amountError = null) }
    }

    fun onTickerInputChanged(ticker: String) {
        _uiState.update { it.copy(tickerInput = ticker) }
    }

    fun onFilterSelected(filter: String) {
        _uiState.update { it.copy(selectedFilter = filter) }
    }

    fun saveTrade() {
        val amountStr = _uiState.value.amountInput.trim()
        val amountVal = amountStr.toDoubleOrNull()

        if (amountVal == null) {
            _uiState.update { it.copy(amountError = "Please enter a valid amount (e.g., 150.00 or -50.25)") }
            return
        }

        val instrumentType = _uiState.value.instrumentTypeInput
        val optionType = if (instrumentType == "Options") _uiState.value.optionTypeInput else null
        val ticker = _uiState.value.tickerInput.trim().ifEmpty { instrumentType.uppercase(Locale.ROOT) }

        val newTrade = TradeEntity(
            instrumentType = instrumentType,
            optionType = optionType,
            amount = amountVal,
            ticker = ticker,
            timestamp = System.currentTimeMillis()
        )

        viewModelScope.launch {
            repository.insertTrade(newTrade)
            // Clear inputs on success
            _uiState.update {
                it.copy(
                    amountInput = "",
                    tickerInput = "",
                    amountError = null
                )
            }
        }
    }

    fun deleteTrade(trade: TradeEntity) {
        viewModelScope.launch {
            repository.deleteTrade(trade)
        }
    }

    fun clearAllTrades() {
        viewModelScope.launch {
            repository.clearAllTrades()
            _uiState.update { it.copy(showClearConfirmDialog = false) }
        }
    }

    fun setShowAboutDialog(show: Boolean) {
        _uiState.update { it.copy(showAboutDialog = show) }
    }

    fun setShowClearConfirmDialog(show: Boolean) {
        _uiState.update { it.copy(showClearConfirmDialog = show) }
    }

    fun exportCsv(context: Context) {
        val tradesList = _uiState.value.trades
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val csvHeader = "ID,Timestamp,Date,Instrument,OptionType,Ticker,Amount\n"
        val csvRows = tradesList.joinToString("\n") { trade ->
            val formattedDate = dateFormat.format(Date(trade.timestamp))
            "${trade.id},${trade.timestamp},\"$formattedDate\",\"${trade.instrumentType}\",\"${trade.optionType ?: ""}\",\"${trade.ticker}\",${trade.amount}"
        }
        val csvContent = csvHeader + csvRows

        _uiState.update { it.copy(exportCsvData = csvContent) }

        // Trigger Android Share Sheet with CSV content
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, csvContent)
            putExtra(Intent.EXTRA_SUBJECT, "TradeTracker_Export_${System.currentTimeMillis()}.csv")
            type = "text/csv"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Export Trade Data")
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(shareIntent)
    }

    fun clearExportCsvData() {
        _uiState.update { it.copy(exportCsvData = null) }
    }

    companion object {
        fun provideFactory(context: Context): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    val db = TradeDatabase.getDatabase(context.applicationContext)
                    val repo = TradeRepositoryImpl(db.tradeDao())
                    return TradeViewModel(repo) as T
                }
            }
    }
}
