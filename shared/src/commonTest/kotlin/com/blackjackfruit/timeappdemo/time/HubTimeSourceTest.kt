package com.blackjackfruit.timeappdemo.time

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class HubTimeSourceTest {
    private fun source(status: HttpStatusCode, body: String, seen: MutableList<String> = mutableListOf()) =
        HubTimeSource(
            client = HttpClient(MockEngine { request ->
                seen += request.url.toString()
                respond(body, status)
            }),
        )

    @Test
    fun returnsTrimmedBodyOn200() = runTest {
        val seen = mutableListOf<String>()
        assertEquals("10:01 PM", source(HttpStatusCode.OK, "10:01 PM\n", seen).currentTime())
        assertEquals(listOf("http://192.168.125.6/time"), seen)
    }

    @Test
    fun nullOnErrorStatus() = runTest {
        assertNull(source(HttpStatusCode.NotFound, "{\"error\":\"not found\"}").currentTime())
    }

    @Test
    fun nullOnEmptyBody() = runTest {
        assertNull(source(HttpStatusCode.OK, "  ").currentTime())
    }

    @Test
    fun nullOnNetworkError() = runTest {
        val failing = HubTimeSource(client = HttpClient(MockEngine { error("unreachable") }))
        assertNull(failing.currentTime())
    }
}
