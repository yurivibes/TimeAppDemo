package com.blackjackfruit.timeappdemo

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.blackjackfruit.timeappdemo.ui.LandingScreen
import com.blackjackfruit.timeappdemo.ui.NO_TIME
import com.blackjackfruit.timeappdemo.ui.TimeScreen

@Composable
@Preview
fun App(viewModel: TimeViewModel = viewModel { TimeViewModel() }) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    AppContent(state = state, onShowTime = viewModel::showTime, onBack = viewModel::back)
}

/** Length of the screen transition, in ms (both directions). */
const val TRANSITION_MS = 300

/**
 * The two screens and the transition between them: the time screen slides in
 * from the right over the landing screen, and Back slides it out to the right
 * again (300 ms, FastOutSlowIn). Stateless, so tests can drive [state].
 */
@Composable
fun AppContent(
    state: AppState,
    onShowTime: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // The last time-screen state, so the time screen keeps its texts while it
    // slides out after Back (state is already Landing then).
    val lastTimeState = remember { mutableStateOf<AppState.ShowingTime?>(null) }
    SideEffect { (state as? AppState.ShowingTime)?.let { lastTimeState.value = it } }

    MaterialTheme {
        // Target only WHICH screen shows. Targeting the whole state restarted
        // the transition when the hub's answer arrived mid-slide, and the
        // landing screen vanished (white background behind the sliding panel).
        AnimatedContent(
            targetState = state is AppState.ShowingTime,
            modifier = modifier,
            transitionSpec = { screenTransition() },
            label = "screen",
        ) { showingTime ->
            val timeState = state as? AppState.ShowingTime ?: lastTimeState.value
            if (showingTime && timeState != null) {
                TimeScreen(time = timeState.time ?: NO_TIME, date = timeState.date, onBack = onBack)
            } else {
                LandingScreen(onShowTime = onShowTime)
            }
        }
    }
}

private fun AnimatedContentTransitionScope<Boolean>.screenTransition(): ContentTransform {
    val spec = tween<androidx.compose.ui.unit.IntOffset>(TRANSITION_MS, easing = FastOutSlowInEasing)
    return if (targetState) {
        // The time screen covers the landing screen, which stays still underneath.
        slideInHorizontally(spec) { fullWidth -> fullWidth } togetherWith
            ExitTransition.KeepUntilTransitionsFinished
    } else {
        // The time screen leaves to the right, on top of the landing screen.
        ContentTransform(
            targetContentEnter = EnterTransition.None,
            initialContentExit = slideOutHorizontally(spec) { fullWidth -> fullWidth },
            targetContentZIndex = -1f,
        )
    }
}
