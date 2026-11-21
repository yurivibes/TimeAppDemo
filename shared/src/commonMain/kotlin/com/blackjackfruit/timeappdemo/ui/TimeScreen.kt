package com.blackjackfruit.timeappdemo.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp

/** Placeholder shown until the first time reading arrives. */
const val NO_TIME = "--:--"

/**
 * Indigo clock screen: a round white back button top-left, the [time] large in
 * the middle and the [date] under it. Geometry follows the time-check mockups
 * (dp/pt, full screen, no insets).
 */
@Composable
fun TimeScreen(time: String, date: String, onBack: () -> Unit, modifier: Modifier = Modifier) {
    val font = spaceGrotesk()
    Box(modifier.fillMaxSize().clipToBounds().background(Palette.TimeBackground)) {
        // r=100 circle centred on the left edge, 220 down from the top.
        Circle(Palette.TimeCircleLeft, 200, Modifier.align(Alignment.TopStart).offset(x = (-100).dp, y = 120.dp))
        // r=190 circle centred 40 in from the right and bottom edges.
        Circle(Palette.TimeCircleBottom, 380, Modifier.align(Alignment.BottomEnd).offset(x = 150.dp, y = 150.dp))
        Box(
            Modifier
                .offset(x = 24.dp, y = 24.dp)
                .size(52.dp)
                .clip(CircleShape)
                .background(Palette.OnDark)
                .clickable(role = Role.Button, onClick = onBack)
                .semantics { contentDescription = "Back" },
            contentAlignment = Alignment.Center,
        ) {
            LineArrow(pointsRight = false, width = 18.dp, height = 16.dp, color = Palette.Ink)
        }
        // The time/date block sits slightly above the screen centre, and the
        // date line overlaps the time's descender space (measured on both mockups).
        Column(
            Modifier.align(Alignment.Center).offset(y = (-16.5).dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                time,
                color = Palette.OnDark,
                fontFamily = font,
                fontWeight = FontWeight.Bold,
                fontSize = timeTextSize,
                letterSpacing = (-0.02).em,
                softWrap = false,
                maxLines = 1,
            )
            Text(
                date,
                modifier = Modifier.offset(y = (-2.8).dp),
                color = Palette.DateText,
                fontFamily = font,
                fontWeight = FontWeight.Medium,
                fontSize = 19.5.sp,
                letterSpacing = 0.01.em,
                maxLines = 1,
            )
        }
    }
}
