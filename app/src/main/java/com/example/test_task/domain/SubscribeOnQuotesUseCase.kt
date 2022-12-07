package com.example.test_task.domain

import com.example.test_task.Quote
import com.example.test_task.data.QuoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.log10

class SubscribeOnQuotesUseCase(private val quoteRepository: QuoteRepository) {
    operator fun invoke(): Flow<Quote> {
        return quoteRepository.subscribeOnQuotes().let { serverQuote ->
            serverQuote.map { quoteServer ->
                Quote(
                    ticker = quoteServer.ticker,
                    name = quoteServer.name,
                    percentChangesFromLastSession = quoteServer.percentChangesFromLastSession?.toString(),
                    lastStock = quoteServer.lastStock,
                    lastPriceDeal = quoteServer.lastPriceDeal,
                    pointChangesFromLastSession = getPointChangesFromLastSession(
                        quoteServer.pointChangesFromLastSession?.toBigDecimal(),
                        quoteServer.minStep
                    ),
                    isPositivePrice = quoteServer.percentChangesFromLastSession.let {
                        if (it == null) null
                        else it > 0
                    },
                    priceDynamic = Quote.PriceDynamic.STABLE
                )
            }
        }.flowOn(Dispatchers.Default)
    }

    private fun getPointChangesFromLastSession(
        pointChanges: BigDecimal?,
        minStep: Double?
    ): String? {

        if (pointChanges == null) {
            return null
        }

        if (minStep == null) {
            return pointChanges.setScale(4, RoundingMode.UP)?.toPlainString()
        }
        val signs = (log10(minStep) * -1).toInt()

        if (signs == 0) return pointChanges.toPlainString()

        return pointChanges.setScale((log10(minStep) * -1).toInt(), RoundingMode.UP)
            ?.toPlainString()
    }
}