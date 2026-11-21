package com.blackjackfruit.timeappdemo.time

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.time.Clock

/** "Saturday, November 14": weekday, month and day, no year. */
fun formatDate(date: LocalDate): String =
    "${date.dayOfWeek.displayName()}, ${date.month.displayName()} ${date.day}"

/** Today's date on this device, formatted by [formatDate]. */
fun todayText(clock: Clock = Clock.System, zone: TimeZone = TimeZone.currentSystemDefault()): String =
    formatDate(clock.todayIn(zone))

private fun DayOfWeek.displayName(): String = name.lowercase().replaceFirstChar { it.uppercase() }

private fun Month.displayName(): String = name.lowercase().replaceFirstChar { it.uppercase() }
