package com.example.test_task

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.test_task.domain.SubscribeOnQuotesUseCase
import com.example.test_task.domain.UnsubscribeFromQuotesUseCase
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.ClosedReceiveChannelException
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
    val priceDynamic: PriceDynamic,
    val pointChangesFromLastSession: String?,
    val isPositivePrice: Boolean?
) {
    enum class PriceDynamic {
        POSITIVE, NEGATIVE, STABLE
    }
}

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

    private var job: Job? = null

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        if (throwable is ClosedReceiveChannelException) {
            job?.cancel()
        } else {
            mutableState.update { state ->
                state.copy(
                    error = QuotesViewState.Error(throwable) {
                        startCheckQuotes()
                    },
                    isLoading = false
                )
            }
        }
    }

    init {
        mutableState.update { state ->
            state.copy(
                isLoading = true,
                error = null
            )
        }
    }

    fun startCheckQuotes() {
        job = viewModelScope.launch(exceptionHandler) {
            subscribeOnQuotesUseCase().onEach { quote ->

                val tickerOnView = mutableState.value.quotes.find { it.ticker == quote.ticker }

                val priceDynamic =
                    if (tickerOnView?.lastPriceDeal == null || quote.lastPriceDeal == null) {
                        Quote.PriceDynamic.STABLE
                    } else {
                        if (quote.lastPriceDeal == tickerOnView.lastPriceDeal) Quote.PriceDynamic.STABLE
                        else if (quote.lastPriceDeal > tickerOnView.lastPriceDeal) Quote.PriceDynamic.POSITIVE
                        else Quote.PriceDynamic.NEGATIVE
                    }

                    mutableState.update { state ->
                        state.copy(
                            quotes = state.quotes
                                .toMutableList()
                                .apply {
                                    if (find { it.ticker == quote.ticker } == null) {
                                        add(quote)
                                    }
                                }.map {
                                    if (it.ticker == quote.ticker) {
                                        quote.copy(
                                            ticker = quote.ticker,
                                            percentChangesFromLastSession = quote.percentChangesFromLastSession
                                                ?: tickerOnView?.percentChangesFromLastSession,
                                            lastStock = quote.lastStock ?: tickerOnView?.lastStock,
                                            name = quote.name ?: tickerOnView?.name,
                                            lastPriceDeal = quote.lastPriceDeal
                                                ?: tickerOnView?.lastPriceDeal,
                                            priceDynamic = priceDynamic,
                                            pointChangesFromLastSession = quote.pointChangesFromLastSession
                                                ?: tickerOnView?.pointChangesFromLastSession,
                                            isPositivePrice = quote.isPositivePrice ?: tickerOnView?.isPositivePrice,
                                        )
                                    } else it
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
        viewModelScope.launch() {
            job?.cancel()
            unsubscribeFromQuotesUseCase()
        }
    }
}