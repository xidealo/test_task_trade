package com.example.test_task.data

import android.util.Log
import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonArray


class QuoteRepository(
    val client: HttpClient,
    val json: Json
) {
    companion object {
        private val HOST = "wss.tradernet.ru"
        private val TICKERS = listOf(
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
        private const val COMMAND = "realtimeQuotes"
    }

    private var webSocketSession: DefaultClientWebSocketSession? = null

    fun subscribeOnQuotes(): Flow<QuoteServer> {
        return flow {
            client.wss(
                host = HOST,
            ) {
                webSocketSession = this

                send("[\"$COMMAND\", [${TICKERS.joinToString { "\"$it\"" }}]]")

                while (true) {
                    val message = incoming.receive() as? Frame.Text ?: continue
                    Log.d("WEB_SOCKET_TAG", "Message: ${message.readText()}")

                    val serverModel = json.decodeFromJsonElement<QuoteServer>(
                        json.decodeFromString(
                            JsonElement.serializer(),
                            message.readText()
                        ).jsonArray.last()
                    )

                    if (isParsed(serverModel)) {
                        emit(serverModel)
                    }
                }
            }
        }.flowOn(Dispatchers.IO)
    }

    private fun isParsed(serverModel: QuoteServer) =
        serverModel.ticker.isNotEmpty()

    //Есть вопрос
    suspend fun unsubscribeOnOrderUpdates() {
        webSocketSession?.let {
            webSocketSession?.close(CloseReason(CloseReason.Codes.NORMAL, "User logout"))
            webSocketSession = null
        }
    }
}