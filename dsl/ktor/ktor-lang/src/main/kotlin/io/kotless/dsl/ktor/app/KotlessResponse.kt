package io.kotless.dsl.ktor.app

import io.kotless.dsl.model.HttpResponse
import io.ktor.application.ApplicationCall
import io.ktor.http.HeadersBuilder
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.OutgoingContent
import io.ktor.response.ResponseHeaders
import io.ktor.server.engine.BaseApplicationResponse
import kotlinx.coroutines.*
import kotlinx.coroutines.io.*
import sun.reflect.generics.reflectiveObjects.NotImplementedException
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

class KotlessResponse(call: ApplicationCall) : BaseApplicationResponse(call), CoroutineScope {
    override val coroutineContext: CoroutineContext = EmptyCoroutineContext
    private val output = ByteChannel(true)
    private val bytes = ArrayList<Byte>()

    val reader = launch(Dispatchers.Unconfined) {
        val packet = output.readRemaining()
        var res = ByteArray(4096)
        var size = packet.readAvailable(res)
        while (size != 0) {
            bytes.addAll(res.toList().take(size))
            res = ByteArray(4096)
            size = packet.readAvailable(res)
        }
    }

    override val headers: ResponseHeaders = object : ResponseHeaders() {
        private val builder = HeadersBuilder()

        override fun engineAppendHeader(name: String, value: String) {
            builder.append(name, value)
        }

        override fun getEngineHeaderNames(): List<String> = builder.names().toList()
        override fun getEngineHeaderValues(name: String): List<String> = builder.getAll(name).orEmpty()
    }

    private var _status: HttpStatusCode? = null

    override suspend fun respondOutgoingContent(content: OutgoingContent) {
        try {
            super.respondOutgoingContent(content)
        } finally {
            output.close()
        }
    }

    override suspend fun respondUpgrade(upgrade: OutgoingContent.ProtocolUpgrade) = throw NotImplementedException()

    override suspend fun responseChannel() = output

    override fun setStatus(statusCode: HttpStatusCode) {
        _status = statusCode
    }

    suspend fun toHttp(): HttpResponse {
        reader.join()

        val status = status()?.value ?: 500
        val myHeaders = headers.allValues().entries().map { it.key to it.value.single() }.toMap().let { HashMap(it) }
        val text = bytes.toByteArray().toString(Charsets.UTF_8)
        return HttpResponse(status, myHeaders, text, false)
    }
}