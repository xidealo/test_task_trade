package com.example.test_task

import com.example.test_task.domain.SubscribeOnQuotesUseCase
import com.example.test_task.domain.UnsubscribeFromQuotesUseCase
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before

class QuotesViewModelTest {

    @MockK
    private lateinit var subscribeOnQuotesUseCase: SubscribeOnQuotesUseCase

    @MockK
    private lateinit var unsubscribeFromQuotesUseCase: UnsubscribeFromQuotesUseCase

    private val viewModel: QuotesViewModel by lazy {
        QuotesViewModel(
            subscribeOnQuotesUseCase = subscribeOnQuotesUseCase,
            unsubscribeFromQuotesUseCase = unsubscribeFromQuotesUseCase
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(Dispatchers.Unconfined)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `return true when getQuotesUseCase has 1 quote`() = runTest {
        coEvery { subscribeOnQuotesUseCase() } returns flow {
            emit(
                Quote(
                    ticker = "1",
                    percentChangesFromLastSession = "",
                    lastStock = "",
                    name = "",
                    lastPriceDeal = 1.0,
                    pointChangesFromLastSession = "",
                    isPositivePrice = true,
                    priceDynamic = Quote.PriceDynamic.POSITIVE,

                    )
            )
        }
        viewModel.startCheckQuotes()
        assertTrue(viewModel.state.value.quotes.size == 1)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `return false when getQuotesUseCase has 3 quotes but state got only 1`() = runTest {
        coEvery { subscribeOnQuotesUseCase() } returns flow {
            emit(

                Quote(
                    ticker = "1",
                    percentChangesFromLastSession = "",
                    lastStock = "",
                    name = "",
                    lastPriceDeal = 1.0,
                    pointChangesFromLastSession = "",
                    isPositivePrice = true,
                    priceDynamic = Quote.PriceDynamic.POSITIVE,

                    )
            )
            emit(
                Quote(
                    ticker = "1",
                    percentChangesFromLastSession = "",
                    lastStock = "",
                    name = "",
                    lastPriceDeal = 1.0,
                    pointChangesFromLastSession = "",
                    isPositivePrice = true,
                    priceDynamic = Quote.PriceDynamic.POSITIVE,

                    )
            )
            emit(
                Quote(
                    ticker = "1",
                    percentChangesFromLastSession = "",
                    lastStock = "",
                    name = "",
                    lastPriceDeal = 1.0,
                    pointChangesFromLastSession = "",
                    isPositivePrice = true,
                    priceDynamic = Quote.PriceDynamic.POSITIVE,

                    )
            )
        }
        viewModel.startCheckQuotes()
        assertFalse(viewModel.state.value.quotes.size == 1)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `return true when getQuotesUseCase throws error and state has not null error`() = runTest {
        coEvery { subscribeOnQuotesUseCase() } throws Exception("")
        viewModel.startCheckQuotes()
        assertTrue(viewModel.state.value.error != null)
    }

}