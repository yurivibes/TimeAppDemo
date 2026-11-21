package com.blackjackfruit.timeappdemo.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import org.jetbrains.compose.resources.Font
import timeappdemo.shared.generated.resources.Res
import timeappdemo.shared.generated.resources.space_grotesk_bold
import timeappdemo.shared.generated.resources.space_grotesk_medium

/** Space Grotesk (SIL OFL 1.1, licenses/SpaceGrotesk-OFL.txt), the mockups' typeface. */
@Composable
internal fun spaceGrotesk(): FontFamily = FontFamily(
    Font(Res.font.space_grotesk_medium, FontWeight.Medium),
    Font(Res.font.space_grotesk_bold, FontWeight.Bold),
)

/**
 * Size of the big time text. The mockups use a different size per platform
 * (fitted: 80.5 sp on the Android one, 64.5 pt on the iPhone one).
 */
internal expect val timeTextSize: TextUnit
