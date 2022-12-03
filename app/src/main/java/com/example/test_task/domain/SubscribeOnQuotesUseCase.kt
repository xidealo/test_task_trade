package com.example.test_task.domain

import com.example.test_task.Quote
import com.example.test_task.data.QuoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class SubscribeOnQuotesUseCase(private val quoteRepository: QuoteRepository) {
    operator fun invoke(): Flow<List<Quote>> {
        return quoteRepository.subscribeOnQuotes().map {
            it.map { quoteServer ->
                Quote(
                    ticker = quoteServer.ticker,
                    name = quoteServer.name,
                    percentChangesFromLastSession = quoteServer.percentChangesFromLastSession,
                    lastStock = quoteServer.lastStock,
                    lastPriceDeal = quoteServer.lastPriceDeal,
                    pointChangesFromLastSession = quoteServer.pointChangesFromLastSession
                )
            }
        }.flowOn(Dispatchers.Default)
    }
}