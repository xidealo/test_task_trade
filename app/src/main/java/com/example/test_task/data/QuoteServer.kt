package com.example.test_task.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class QuoteServer(

    @SerialName("c")
    val ticker: String = "",

    @SerialName("pcp")
    val percentChangesFromLastSession: Double? = null,

    @SerialName("ltr")
    val lastStock: String? = null,

    @SerialName("name")
    val name: String? = null,

    @SerialName("ltp")
    val lastPriceDeal: Double? = null,

    @SerialName("chg")
    val pointChangesFromLastSession: Double? = null,

    @SerialName("min_step")
    val minStep: Double? = null,
)
