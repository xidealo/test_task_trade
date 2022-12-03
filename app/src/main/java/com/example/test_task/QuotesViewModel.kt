package com.example.test_task

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.test_task.domain.GetQuotesUseCase
import io.ktor.websocket.*
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


data class Quote(
    val ticker: String,
    val percentChanges: String?,
    val lastStock: String?,
    val name: String?,
    val lastPriceDeal: String?,
)

data class QuotesViewState(
    val quotes: List<Quote> = emptyList(),
    val eventList: List<Event> = emptyList()
) {
    sealed interface Event {

    }
}

class QuotesViewModel(
    private val getQuotesUseCase: GetQuotesUseCase
) : ViewModel() {

    private val mutableState: MutableStateFlow<QuotesViewState> =
        MutableStateFlow(QuotesViewState())
    val state: StateFlow<QuotesViewState> = mutableState.asStateFlow()

    val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        println(throwable)
    }

    init {
        viewModelScope.launch(exceptionHandler) {
            getQuotesUseCase().onEach { quotes ->
                mutableState.update { state ->
                    state.copy(
                        quotes = quotes
                    )
                }
                Log.d("VM_TAG", "${state.value}")
            }.collect()
        }
    }
}