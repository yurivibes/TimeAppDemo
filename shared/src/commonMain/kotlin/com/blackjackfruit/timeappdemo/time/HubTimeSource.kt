package com.blackjackfruit.timeappdemo.time

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Where the time screen gets the time it shows. */
fun interface TimeSource {
    /** The time text to show (e.g. "10:01 PM"), or null when it can't be read. */
    suspend fun currentTime(): String?
}

/**
 * Reads the time as plain text from `GET <hub>/time`. A non-2xx answer, an
 * empty body or a network error all mean "no time" (null).
 */
class HubTimeSource(
    private val url: String = Hub.timeUrl,
    private val client: HttpClient = defaultClient(),
    // Off the main thread: the first request loads and sets up Ktor, which
    // stalled the opening slide by 70-90 ms when it ran in the tap handler.
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default,
) : TimeSource {

    override suspend fun currentTime(): String? = withContext(dispatcher) {
        try {
            val response = client.get(url)
            if (response.status.isSuccess()) response.bodyAsText().trim().ifEmpty { null } else null
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            null
        }
    }

    private companion object {
        const val TIMEOUT_MS = 3_000L

        fun defaultClient() = HttpClient {
            install(HttpTimeout) {
                requestTimeoutMillis = TIMEOUT_MS
                connectTimeoutMillis = TIMEOUT_MS
            }
        }
    }
}
