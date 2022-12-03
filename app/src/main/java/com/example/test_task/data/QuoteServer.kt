package com.example.test_task.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class QuoteServer(

    @SerialName("c")
    val ticker: String = "",

    @SerialName("pcp")
    val percentChangesFromLastSession: String? = null,

    @SerialName("ltr")
    val lastStock: String? = null,

    @SerialName("name")
    val name: String? = null,

    @SerialName("ltp")
    val lastPriceDeal: String? = null,

    @SerialName("chg")
    val pointChangesFromLastSession: String? = null
)
