package com.blackjackfruit.timeappdemo

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onRoot
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Golden frames of the screen transition at 0/25/50/75/100% of [TRANSITION_MS],
 * on the paused Compose clock so every frame is exact and repeatable.
 *
 * Record: ./gradlew :androidApp:recordRoborazziDebug
 * Verify: ./gradlew :androidApp:verifyRoborazziDebug   (the PR workflow runs this)
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
// Same logical size as the moto's screen class (443x980 dp), at 2x.
@Config(sdk = [35], qualifiers = "w443dp-h980dp-port-xhdpi")
class TransitionFramesTest {

    @get:Rule
    val compose = createComposeRule()

    private companion object {
        const val START_FRAMES = 2
        const val FRAME_MS = 16L
        const val MIDWAY_MS = 30L
    }

    private val timeState = AppState.ShowingTime(time = "10:22 PM", date = "Saturday, November 14")

    @Test
    fun showTimeSlidesInFromTheRight() = recordTransition(
        name = "show_time",
        from = AppState.Landing,
        // As on the phone: the screen opens before the hub answers, and the
        // answer arrives ~30 ms into the slide (a second state change mid-way).
        to = timeState.copy(time = null),
        midway = timeState,
    )

    @Test
    fun backSlidesOutToTheRight() = recordTransition(
        name = "back",
        from = timeState,
        to = AppState.Landing,
    )

    private fun recordTransition(name: String, from: AppState, to: AppState, midway: AppState? = null) {
        var state by mutableStateOf(from)
        compose.mainClock.autoAdvance = false
        compose.setContent { AppContent(state = state, onShowTime = {}, onBack = {}) }
        compose.mainClock.advanceTimeByFrame()
        compose.waitForIdle()

        state = to
        // The state change is applied at the next idle point. AnimatedContent
        // then recomposes on the next frame and plays its first frame (t=0)
        // on the one after (traced frame by frame).
        compose.waitForIdle()
        repeat(START_FRAMES) { compose.mainClock.advanceTimeByFrame() }
        var elapsed = 0L
        for (percent in listOf(0, 25, 50, 75, 100)) {
            if (percent == 25 && midway != null) {
                compose.mainClock.advanceTimeBy(MIDWAY_MS, ignoreFrameDuration = true)
                elapsed = MIDWAY_MS
                state = midway
                compose.waitForIdle()
            }
            // The last frame is taken one frame after the nominal end, so it
            // shows the settled screen (the first animation frame lands just
            // after t=0, which leaves the 300 ms frame 1 px short).
            val at = TRANSITION_MS * percent / 100L + if (percent == 100) FRAME_MS else 0L
            compose.mainClock.advanceTimeBy(at - elapsed, ignoreFrameDuration = true)
            elapsed = at
            compose.onRoot().captureRoboImage("src/test/screenshots/${name}_%03d.png".format(percent))
        }
    }
}
