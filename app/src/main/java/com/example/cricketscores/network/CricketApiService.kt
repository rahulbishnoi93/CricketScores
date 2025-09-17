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

package com.example.cricketscores.network

import com.example.cricketscores.model.AllMatches
import com.example.cricketscores.model.LiveMatch
import com.example.cricketscores.model.MatchDetails
import com.example.cricketscores.model.RecentMatch
import com.example.cricketscores.model.ScheduleMatch
import retrofit2.http.GET
import retrofit2.http.Path


interface CricketApiService {

    @GET("live")
    suspend fun getLiveMatches(): List<LiveMatch>

    @GET("recent")
    suspend fun getRecentMatches(): List<RecentMatch>

    @GET("schedule")
    suspend fun getSchedule(): List<ScheduleMatch>

    @GET("match/{id}")
    suspend fun getMatchDetails(@Path("id") id: String): MatchDetails

    @GET("allMatches")
    suspend fun getAllMatches(): AllMatches
}
