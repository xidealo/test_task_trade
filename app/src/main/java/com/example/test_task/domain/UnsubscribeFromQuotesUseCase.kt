package com.example.test_task.domain

import com.example.test_task.data.QuoteRepository

class UnsubscribeFromQuotesUseCase(private val quoteRepository: QuoteRepository) {
    suspend operator fun invoke() {
            quoteRepository.unsubscribeOnOrderUpdates()
    }
}