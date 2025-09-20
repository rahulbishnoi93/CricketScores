/*
 * Copyright (C) 2023 The Android Open Source Project
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

package com.neox.cricketscores.model

import kotlinx.serialization.Serializable

@Serializable
data class MatchDetails(
    val batsmen: List<Batter> = emptyList(),
    val bowlers: List<Bowler> = emptyList(),
    val player_of_the_match: String? = null,
    val status: String? = null,
    val Livestatus: String? = null,
    val match_title: String? = null,
)

@Serializable
data class Batter(
    val balls: String? = null,
    val fours: String? = null,
    val name: String,
    val runs: String? = null,
    val sixes: String? = null,
    val strike_rate: String? = null,
)

@Serializable
data class Bowler(
    val econ: String? = null,
    val maidens: String? = null,
    val name: String,
    val overs: String? = null,
    val runs: String? = null,
    val wickets: String? = null,
)

