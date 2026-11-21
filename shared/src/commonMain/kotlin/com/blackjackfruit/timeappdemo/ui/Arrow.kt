package com.blackjackfruit.timeappdemo.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** A 2-dp line arrow (shaft + chevron) filling [width] x [height], pointing left or right. */
@Composable
internal fun LineArrow(
    pointsRight: Boolean,
    width: Dp,
    height: Dp,
    color: Color,
    modifier: Modifier = Modifier,
    strokeWidth: Dp = 2.dp,
) {
    Canvas(modifier.size(width, height)) {
        val sw = strokeWidth.toPx()
        val half = sw / 2
        val w = size.width
        val h = size.height
        val midY = h / 2
        val tipX = if (pointsRight) w - half else half
        val tailX = if (pointsRight) half else w - half
        val arm = h / 2 - half
        val armX = if (pointsRight) tipX - arm else tipX + arm
        drawLine(color, Offset(tailX, midY), Offset(tipX, midY), sw, StrokeCap.Butt)
        val chevron = Path().apply {
            moveTo(armX, midY - arm)
            lineTo(tipX, midY)
            lineTo(armX, midY + arm)
        }
        drawPath(chevron, color, style = Stroke(width = sw, cap = StrokeCap.Square, join = StrokeJoin.Miter))
    }
}
