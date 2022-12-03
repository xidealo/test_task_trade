package com.example.test_task.data

import android.util.Log
import com.example.test_task.Quote
import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class QuoteRepository(
    val client: HttpClient,
    val json: Json
) {

    val tickers = listOf(
        "RSTI",
        "GAZP",
        "MRKZ",
        "RUAL",
        "HYDR",
        "MRKS",
        "SBER",
        "FEES",
        "TGKA",
        "VTBR",
        "ANH.US",
        "VICL.US",
        "BURG.US",
        "NBL.US",
        "YETI.US",
        "WSFS.US",
        "NIO.US",
        "DXC.US",
        "MIC.US",
        "HSBC.US",
        "EXPN.EU",
        "GSK.EU",
        "SHP.EU",
        "MAN.EU",
        "DB1.EU",
        "MUV2.EU",
        "TATE.EU",
        "KGF.EU",
        "MGGT.EU",
        "SGGD.EU"
    )

    private val quotasMap: MutableMap<String, QuoteServer> = mutableMapOf()

    private var webSocketSession: DefaultClientWebSocketSession? = null

    fun subscribeOnQuotes(): Flow<List<QuoteServer>> {

        return channelFlow {
            client.wss(
                host = "wss.tradernet.ru",
            ) {
                launch(Dispatchers.IO) {
                    while (true) {
                        send("[\"quotes\", [${tickers.joinToString { "\"$it\"" }}]]")
                        delay(1000)
                        channel.send((quotasMap.values.toList()))
                    }
                }

                webSocketSession = this

                while (true) {
                    val message = incoming.receive() as? Frame.Text ?: continue
                    Log.d("WEB_SOCKET_TAG", "Message: ${message.readText()}")
                    val serverModel = json.decodeFromString(
                        QuoteServer.serializer(),
                        message
                            .readText()
                            .substringAfter(",")
                            .substringBefore("]")
                    )
                    if (isParsed(serverModel)) {
                        quotasMap[serverModel.ticker] = serverModel
                    }
                }
            }
        }
    }

    private fun isParsed(serverModel: QuoteServer) =
        serverModel.ticker.isNotEmpty()


    suspend fun unsubscribeOnOrderUpdates() {
        if (webSocketSession != null) {
            webSocketSession?.close(CloseReason(CloseReason.Codes.NORMAL, "User logout"))
            webSocketSession = null
        }
    }
}