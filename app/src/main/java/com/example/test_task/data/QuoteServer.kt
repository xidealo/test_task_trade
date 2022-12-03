package com.example.test_task.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class QuoteServer(

    @SerialName("c")
    val ticker: String = "",

    @SerialName("name")
    val name: String? = null
)
