package com.example.test_task

import com.example.test_task.domain.GetQuotesUseCase
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
    private lateinit var getQuotesUseCase: GetQuotesUseCase

    private val viewModel: QuotesViewModel by lazy {
        QuotesViewModel(
            getQuotesUseCase = getQuotesUseCase
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
        coEvery { getQuotesUseCase() } returns flow {
            emit(
                listOf(
                    Quote(
                        ticker = "1",
                        percentChangesFromLastSession = "",
                        lastStock = "",
                        name = "",
                        lastPriceDeal = "",
                        pointChangesFromLastSession = "",
                    )
                )
            )
        }
        assertTrue(viewModel.state.value.quotes.size == 1)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `return false when getQuotesUseCase has 3 quotes but state got only 1`() = runTest {
        coEvery { getQuotesUseCase() } returns flow {
            emit(
                listOf(
                    Quote(
                        ticker = "1",
                        percentChangesFromLastSession = "",
                        lastStock = "",
                        name = "",
                        lastPriceDeal = "",
                        pointChangesFromLastSession = "",
                    ),
                    Quote(
                        ticker = "1",
                        percentChangesFromLastSession = "",
                        lastStock = "",
                        name = "",
                        lastPriceDeal = "",
                        pointChangesFromLastSession = "",
                    ),
                    Quote(
                        ticker = "1",
                        percentChangesFromLastSession = "",
                        lastStock = "",
                        name = "",
                        lastPriceDeal = "",
                        pointChangesFromLastSession = "",
                    ),
                )
            )
        }
        assertFalse(viewModel.state.value.quotes.size == 1)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `return true when getQuotesUseCase throws error and state has not null error`() = runTest {
        coEvery { getQuotesUseCase() } throws  Exception("")
        assertTrue(viewModel.state.value.error != null)
    }

}