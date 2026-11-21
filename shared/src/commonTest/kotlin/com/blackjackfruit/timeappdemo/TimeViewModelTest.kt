package com.blackjackfruit.timeappdemo

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class TimeViewModelTest {
    private val dispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setUp() = Dispatchers.setMain(dispatcher)

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun pollsUntilTheHubAnswersAndKeepsTheLastTime() = runTest(dispatcher) {
        // The hub has no /time rule at first (null), then answers, then fails again.
        val answers = ArrayDeque(listOf(null, "10:01 PM", null))
        var calls = 0
        val vm = TimeViewModel(
            timeSource = { calls++; answers.removeFirstOrNull() },
            dateText = { "Saturday, November 14" },
            pollIntervalMs = 1_000,
        )

        vm.showTime()
        runCurrent()
        assertEquals(AppState.ShowingTime(null, "Saturday, November 14"), vm.state.value)

        advanceTimeBy(1_001)
        assertEquals(AppState.ShowingTime("10:01 PM", "Saturday, November 14"), vm.state.value)

        advanceTimeBy(1_000)
        assertEquals(AppState.ShowingTime("10:01 PM", "Saturday, November 14"), vm.state.value)
        assertEquals(3, calls)

        vm.back()
        advanceTimeBy(5_000)
        assertEquals(AppState.Landing, vm.state.value)
        assertEquals(3, calls)
    }
}
