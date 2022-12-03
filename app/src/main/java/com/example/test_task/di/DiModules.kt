package com.example.test_task.di

import com.example.test_task.QuotesViewModel
import com.example.test_task.data.QuoteRepository
import com.example.test_task.domain.GetQuotesUseCase
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.websocket.*
import io.ktor.serialization.kotlinx.*
import kotlinx.serialization.json.Json
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

fun initKoin(appDeclaration: KoinAppDeclaration = {}) = startKoin {
    appDeclaration()
    modules(
        viewModelModule(),
        useCaseModule(),
        dataModule(),
        networkModule(),
    )
}

fun viewModelModule() = module {
    viewModel {
        QuotesViewModel(getQuotesUseCase = get())
    }
}

internal fun useCaseModule() = module {
    single {
        GetQuotesUseCase(
            quoteRepository = get(),
        )
    }
}

internal fun dataModule() = module {
    single {
        QuoteRepository(
            client = get(),
            json = get(),
        )
    }
}

fun networkModule() = module {
    single {
        Json {
            isLenient = false
            ignoreUnknownKeys = true
        }
    }
    single {
        HttpClient(OkHttp) {

            install(WebSockets){
                contentConverter = KotlinxWebsocketSerializationConverter(Json)
            }
        }
    }
}