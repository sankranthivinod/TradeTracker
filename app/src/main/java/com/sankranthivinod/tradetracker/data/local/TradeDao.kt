package com.sankranthivinod.tradetracker.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TradeDao {
    @Query("SELECT * FROM trades ORDER BY timestamp DESC")
    fun getAllTrades(): Flow<List<TradeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTrade(trade: TradeEntity)

    @Delete
    fun deleteTrade(trade: TradeEntity)

    @Query("DELETE FROM trades")
    fun clearAllTrades()
}
