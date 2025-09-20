/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.neox.cricketscores.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.wear.compose.material3.ColorScheme

val Purple200 = Color(0xFFBB86FC)
val Purple700 = Color(0xFF3700B3)
val Teal200 = Color(0xFF03DAC5)
val Red400 = Color(0xFFCF6679)


// Backgrounds
val BackgroundPrimary = Color(0xFF121212)        // main app background
val BackgroundSecondary = Color(0xFF1E1E1E)      // slightly lighter for cards
val BackgroundCardDetail = Color(0xFF232323)     // detail card slightly brighter

// Text
val heading = Color(0xFFFFFFFF)       // Pure white for main text
val subheading = Color(0xFFCCCCCC)    // Light grey for secondary headings
val light = Color(0xFF888888)         // Muted grey for less important text
val accent = Color(0xFF4DD0E1)        // Amber/Gold for scores or LIVE tags
val iconBackground = Color(0xFF3A3A3A)   // Medium grey, looks premium
// Icons
val IconPrimary = Color(0xFFFFFFFF)
val IconSecondary = Color(0xFFB0B0B0)
val IconAccent = Color(0xFFFFC107)

// Dividers
val DividerColor = Color(0xFF333333)

// Live Match Tag
val LiveMatchRed = Color(0xFFD32F2F)

val WearAppColorPalette: ColorScheme = ColorScheme(
    primary = heading,
    primaryDim = subheading,
    secondary = accent,
    secondaryDim = light,
    error = Red400,
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onError = Color.Black,
    onBackground = iconBackground
)