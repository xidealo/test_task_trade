package com.example.test_task

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.test_task.domain.SubscribeOnQuotesUseCase
import com.example.test_task.domain.UnsubscribeFromQuotesUseCase
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


data class Quote(
    val ticker: String,
    val percentChangesFromLastSession: String?,
    val lastStock: String?,
    val name: String?,
    val lastPriceDeal: Double?,
    val isPositiveDynamic: Boolean? = null,
    val pointChangesFromLastSession: String?,
    val isPositivePrice: Boolean
)

data class QuotesViewState(
    val quotes: List<Quote> = emptyList(),
    val isLoading: Boolean = false,
    val eventList: List<Event> = emptyList(),
    val error: Error? = null,

    ) {
    data class Error(val throwable: Throwable, val errorAction: () -> Unit)

    sealed interface Event {
        //show message, etc
    }
}

class QuotesViewModel(
    private val subscribeOnQuotesUseCase: SubscribeOnQuotesUseCase,
    private val unsubscribeFromQuotesUseCase: UnsubscribeFromQuotesUseCase
) : ViewModel() {

    private val mutableState: MutableStateFlow<QuotesViewState> =
        MutableStateFlow(QuotesViewState())
    val state: StateFlow<QuotesViewState> = mutableState.asStateFlow()

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        mutableState.update { state ->
            state.copy(
                error = QuotesViewState.Error(throwable) {
                    startCheckQuotes()
                },
                isLoading = false
            )
        }
    }

    fun startCheckQuotes() {
        mutableState.update { state ->
            state.copy(
                isLoading = true, error = null
            )
        }
        viewModelScope.launch(exceptionHandler) {
            subscribeOnQuotesUseCase().onEach { quotes ->
                mutableState.update { state ->
                    state.copy(
                        quotes = quotes.map { quote ->
                            quote.copy(
                                isPositiveDynamic = state.quotes.find { it.ticker == quote.ticker }.let { storedValue ->
                                    if (storedValue?.lastPriceDeal == null || quote.lastPriceDeal == null) {
                                        null
                                    } else {
                                        if (quote.lastPriceDeal == storedValue.lastPriceDeal) null
                                        else quote.lastPriceDeal > storedValue.lastPriceDeal
                                    }
                                },
                            )
                        },
                        isLoading = false,
                        error = null
                    )
                }
                Log.d("VM_TAG", "${state.value}")
            }.collect()
        }
    }

    fun stopCheckQuotes() {
        viewModelScope.launch(exceptionHandler) {
            unsubscribeFromQuotesUseCase()
        }
    }
}