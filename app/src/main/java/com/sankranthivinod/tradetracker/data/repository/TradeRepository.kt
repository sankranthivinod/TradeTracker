package com.sankranthivinod.tradetracker.data.repository

import com.sankranthivinod.tradetracker.data.local.TradeDao
import com.sankranthivinod.tradetracker.data.local.TradeEntity
import kotlinx.coroutines.flow.Flow

interface TradeRepository {
    val allTrades: Flow<List<TradeEntity>>
    suspend fun insertTrade(trade: TradeEntity)
    suspend fun deleteTrade(trade: TradeEntity)
    suspend fun clearAllTrades()
}

class TradeRepositoryImpl(private val tradeDao: TradeDao) : TradeRepository {
    override val allTrades: Flow<List<TradeEntity>> = tradeDao.getAllTrades()

    override suspend fun insertTrade(trade: TradeEntity) {
        tradeDao.insertTrade(trade)
    }

    override suspend fun deleteTrade(trade: TradeEntity) {
        tradeDao.deleteTrade(trade)
    }

    override suspend fun clearAllTrades() {
        tradeDao.clearAllTrades()
    }
}
