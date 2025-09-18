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

package com.example.cricketscores.model

import kotlinx.serialization.Serializable

/**
 * This data class defines a live cricket match which includes match summary, ID, and team scores.
 */
@Serializable
data class LiveMatch(
    val liveMatchSummary: String,
    val matchId: String,
    val team1: Team,
    val team2: Team
)

/**
 * This data class defines a team with its score and name.
 */
@Serializable
data class Team(
    val score: String,
    val team: String
)
