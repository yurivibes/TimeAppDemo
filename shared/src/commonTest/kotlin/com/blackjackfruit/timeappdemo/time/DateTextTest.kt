package com.blackjackfruit.timeappdemo.time

import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals

class DateTextTest {
    @Test
    fun formatsWeekdayMonthAndDay() {
        assertEquals("Saturday, November 14", formatDate(LocalDate(2026, 11, 14)))
        assertEquals("Monday, January 5", formatDate(LocalDate(2026, 1, 5)))
    }
}
