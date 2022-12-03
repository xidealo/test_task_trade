package com.example.test_task.domain

import com.example.test_task.Quote
import com.example.test_task.data.QuoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class GetQuotesUseCase(private val quoteRepository: QuoteRepository) {
    operator fun invoke(): Flow<List<Quote>> {
        return quoteRepository.subscribeOnQuotes().map {
            it.map { quoteServer ->
                Quote(
                    ticker = quoteServer.ticker,
                    name = quoteServer.name,
                    percentChanges = "",
                    lastStock = "",
                    lastPriceDeal = "",
                )
            }
        }.flowOn(Dispatchers.Default)
    }
}