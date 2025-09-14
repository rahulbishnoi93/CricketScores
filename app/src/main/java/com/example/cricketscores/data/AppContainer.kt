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

import android.content.Context
import com.example.cricketscores.network.CricketApiService
import retrofit2.Retrofit
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

/**
 * Dependency Injection container at the application level.
 */
interface AppContainer {
    val cricketMatchesRepository: CricketMatchesRepository
}

/**
 * Implementation for the Dependency Injection container at the application level.
 *
 * Variables are initialized lazily and the same instance is shared across the whole app.
 */
class DefaultAppContainer(private val appContext: Context) : AppContainer {
    private val baseUrl = "https://cricket-api-164069476032.asia-south1.run.app/"
    // Custom OkHttpClient with increased timeout
    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)  // default 10s → now 30s
        .readTimeout(60, TimeUnit.SECONDS)     // default 10s → now 60s
        .writeTimeout(60, TimeUnit.SECONDS)    // default 10s → now 60s
        .build()
    /**
     * Use the Retrofit builder to build a retrofit object using a kotlinx.serialization converter
     */
    private val retrofit: Retrofit = Retrofit.Builder()
        .client(okHttpClient) // inject custom client
        .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
        .baseUrl(baseUrl)
        .build()

    /**
     * Retrofit service object for creating api calls
     */
    private val retrofitService: CricketApiService by lazy {
        retrofit.create(CricketApiService::class.java)
    }

    /**
     * PhoneDataClient instance that will be used to request data from phone when watch is offline.
     */
    private val phoneDataClient: PhoneDataClient by lazy {
        PhoneDataClient(appContext)
    }

    override val cricketMatchesRepository: CricketMatchesRepository by lazy {
        NetworkCricketMatchesRepository(
            cricketApiService = retrofitService,
            appContext = appContext,
            phoneDataClient = phoneDataClient
        )
    }
}
