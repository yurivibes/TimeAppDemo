package com.blackjackfruit.timeappdemo.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Orange start screen with one dark pill button, "Show the time →".
 * Geometry follows the time-check mockups (dp/pt, full screen, no insets).
 */
@Composable
fun LandingScreen(onShowTime: () -> Unit, modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize().clipToBounds().background(Palette.LandingBackground)) {
        // r=180 circle centred 40 in from the right edge, 60 down from the top.
        Circle(Palette.LandingCircleTop, 360, Modifier.align(Alignment.TopEnd).offset(x = 140.dp, y = (-120).dp))
        // r=140 circle centred 30 in from the left edge, 50 up from the bottom.
        Circle(Palette.LandingCircleBottom, 280, Modifier.align(Alignment.BottomStart).offset(x = (-110).dp, y = 90.dp))
        Row(
            Modifier
                .align(Alignment.Center)
                .height(64.dp)
                .clip(CircleShape)
                .background(Palette.Ink)
                .clickable(role = Role.Button, onClickLabel = "Show the time", onClick = onShowTime)
                .padding(start = 38.dp, end = 42.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                "Show the time",
                color = Palette.OnDark,
                fontFamily = spaceGrotesk(),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
            )
            Spacer(Modifier.width(13.dp))
            LineArrow(pointsRight = true, width = 15.dp, height = 12.dp, color = Palette.OnDark)
        }
    }
}

@Composable
internal fun Circle(color: androidx.compose.ui.graphics.Color, diameterDp: Int, modifier: Modifier) {
    Box(modifier.size(diameterDp.dp).clip(CircleShape).background(color))
}
