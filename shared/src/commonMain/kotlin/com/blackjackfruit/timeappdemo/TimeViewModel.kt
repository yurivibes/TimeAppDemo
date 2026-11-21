package com.blackjackfruit.timeappdemo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blackjackfruit.timeappdemo.time.HubTimeSource
import com.blackjackfruit.timeappdemo.time.TimeSource
import com.blackjackfruit.timeappdemo.time.todayText
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/** What the app shows: the landing screen, or the time screen with its texts. */
sealed interface AppState {
    data object Landing : AppState

    /** [time] is null until the hub has answered. */
    data class ShowingTime(val time: String?, val date: String) : AppState
}

/**
 * Switches between the two screens and, while the time screen is open, polls
 * the hub every [pollIntervalMs] so the shown time follows the hub's answer.
 */
class TimeViewModel(
    private val timeSource: TimeSource = HubTimeSource(),
    private val dateText: () -> String = { todayText() },
    private val pollIntervalMs: Long = POLL_INTERVAL_MS,
) : ViewModel() {

    private val _state = MutableStateFlow<AppState>(AppState.Landing)
    val state: StateFlow<AppState> = _state.asStateFlow()

    private var polling: Job? = null

    fun showTime() {
        if (_state.value is AppState.ShowingTime) return
        _state.value = AppState.ShowingTime(time = null, date = dateText())
        polling = viewModelScope.launch {
            while (isActive) {
                val time = timeSource.currentTime()
                _state.update { current ->
                    if (current is AppState.ShowingTime) {
                        current.copy(time = time ?: current.time, date = dateText())
                    } else {
                        current
                    }
                }
                delay(pollIntervalMs)
            }
        }
    }

    fun back() {
        polling?.cancel()
        polling = null
        _state.value = AppState.Landing
    }

    companion object {
        const val POLL_INTERVAL_MS = 1_000L
    }
}
