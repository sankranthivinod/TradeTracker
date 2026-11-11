package com.sankranthivinod.tradetracker.data.repository

import com.sankranthivinod.tradetracker.data.local.TradeDao
import com.sankranthivinod.tradetracker.data.local.TradeEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

interface TradeRepository {
    val allTrades: Flow<List<TradeEntity>>
    suspend fun insertTrade(trade: TradeEntity)
    suspend fun deleteTrade(trade: TradeEntity)
    suspend fun clearAllTrades()
}

class TradeRepositoryImpl(private val tradeDao: TradeDao) : TradeRepository {
    override val allTrades: Flow<List<TradeEntity>> = tradeDao.getAllTrades()

    override suspend fun insertTrade(trade: TradeEntity) {
        withContext(Dispatchers.IO) {
            tradeDao.insertTrade(trade)
        }
    }

    override suspend fun deleteTrade(trade: TradeEntity) {
        withContext(Dispatchers.IO) {
            tradeDao.deleteTrade(trade)
        }
    }

    override suspend fun clearAllTrades() {
        withContext(Dispatchers.IO) {
            tradeDao.clearAllTrades()
        }
    }
}
