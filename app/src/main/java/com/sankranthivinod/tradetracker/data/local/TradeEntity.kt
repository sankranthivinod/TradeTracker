package com.sankranthivinod.tradetracker.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trades")
data class TradeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val instrumentType: String, // "Stocks", "Options", "Futures", "Crypto"
    val optionType: String? = null, // "Call", "Put", or null
    val amount: Double, // positive for profit, negative for loss
    val ticker: String, // Note or symbol (e.g., "SPY", "TSLA")
    val timestamp: Long = System.currentTimeMillis()
)
