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
package com.example.cricketscores.data

import com.example.cricketscores.model.LiveMatch
import com.example.cricketscores.model.MatchDetails
import com.example.cricketscores.model.RecentMatch
import com.example.cricketscores.model.ScheduleMatch
import com.example.cricketscores.network.CricketApiService

/**
 * Repository that fetch mars photos list from marsApi.
 */
interface CricketMatchesRepository {
    /** Fetches list of MarsPhoto from marsApi */
    suspend fun getLiveMatches(): List<LiveMatch>
    suspend fun getRecentMatches(): List<RecentMatch>
    suspend fun getSchedule(): List<ScheduleMatch>
    suspend fun getMatchDetails(id: String): MatchDetails
}

/**
 * Network Implementation of Repository that fetch mars photos list from marsApi.
 */
class NetworkCricketMatchesRepository(
    private val cricketApiService: CricketApiService
) : CricketMatchesRepository {
    /** Fetches list of MarsPhoto from marsApi*/
    override suspend fun getLiveMatches(): List<LiveMatch> = cricketApiService.getLiveMatches()
    override suspend fun getRecentMatches(): List<RecentMatch> = cricketApiService.getRecentMatches()
    override suspend fun getSchedule(): List<ScheduleMatch> = cricketApiService.getSchedule()
    override suspend fun getMatchDetails(id: String): MatchDetails = cricketApiService.getMatchDetails(id)
}
