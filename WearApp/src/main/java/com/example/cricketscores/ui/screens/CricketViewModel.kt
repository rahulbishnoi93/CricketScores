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
package com.example.cricketscores.ui.screens

import android.Manifest
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.annotation.RequiresPermission
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.cricketscores.CricketApplication
import com.example.cricketscores.data.CricketMatchesRepository
import com.example.cricketscores.model.LiveMatch
import com.example.cricketscores.model.MatchDetails
import com.example.cricketscores.model.RecentMatch
import com.example.cricketscores.model.ScheduleMatch
import kotlinx.coroutines.launch
import kotlinx.coroutines.async
import retrofit2.HttpException
import java.io.IOException

/**
 * UI state for the Home screen
 */
sealed interface CricketUiState {
    data class Success(
        val liveMatches: List<LiveMatch>,
        val recentMatches: List<RecentMatch>,
        val schedule: List<ScheduleMatch>,
    ) : CricketUiState
    data class Error(val message: String) : CricketUiState
    object Loading : CricketUiState
}
sealed interface MatchDetailUiState {
    data class Success(val matchDetail: MatchDetails) : MatchDetailUiState
    data class Error(val message: String) : MatchDetailUiState
    object Loading : MatchDetailUiState
}

/**
 * Interface to check for network connectivity.
 */
interface NetworkStateChecker {
    fun isOnline(): Boolean
}

/**
 * Implementation of NetworkStateChecker that uses the Android ConnectivityManager.
 */
class AndroidNetworkStateChecker(private val context: Context) : NetworkStateChecker {
    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    override fun isOnline(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
    }
}

class CricketViewModel(
    private val cricketMatchesRepository: CricketMatchesRepository,
    private val networkStateChecker: NetworkStateChecker
) : ViewModel() {
    /** The mutable State that stores the status of the most recent request */
    var cricketUiState: CricketUiState by mutableStateOf(CricketUiState.Loading)
        private set

    var currentMatchDetails: MatchDetailUiState by mutableStateOf(MatchDetailUiState.Loading)
        private set


    init { loadHomeDataAll() }

    fun loadHomeDataAll() {
        viewModelScope.launch {
            cricketUiState = CricketUiState.Loading
            cricketUiState = try {
                val allMatches = cricketMatchesRepository.getAllMatches()
                CricketUiState.Success(
                    liveMatches = allMatches.live_matches,
                    recentMatches = allMatches.recent_matches,
                    schedule = allMatches.upcoming_matches
                )
            } catch (e: Exception) {
                CricketUiState.Error("Loading failed.\n Check your internet.")
            }
        }
    }
    fun loadHomeData() {
        viewModelScope.launch {
            // Check for network connectivity before making the call
//            if (!networkStateChecker.isOnline()) {
//                cricketUiState = CricketUiState.Error("No internet connection available")
//                return@launch
//            }
            cricketUiState = CricketUiState.Loading
            cricketUiState = try {
                val liveDeferred = async { runCatching { cricketMatchesRepository.getLiveMatches() }.getOrElse { e ->  } }
                val recentDeferred = async { runCatching { cricketMatchesRepository.getRecentMatches()}.getOrElse { e -> } }
                val scheduleDeferred = async { runCatching { cricketMatchesRepository.getSchedule()}.getOrElse { e -> } }
                val live = liveDeferred.await()
                val recent = recentDeferred.await()
                val schedule = scheduleDeferred.await()
                //val (live, recent, schedule) = awaitAll(liveDeferred, recentDeferred, scheduleDeferred)
                CricketUiState.Success(
                    liveMatches = live as List<LiveMatch>,
                    recentMatches = recent as List<RecentMatch>,
                    schedule = schedule as List<ScheduleMatch>,
                )
            } catch (e: IOException) {
                CricketUiState.Error("Network error occurred")
            } catch (e: HttpException) {
                CricketUiState.Error("HTTP error occurred")
            } catch (e: Exception){
                CricketUiState.Error("An unknown error occurred")
            }
        }
    }

    fun getMatchDetails(id: String) {
        viewModelScope.launch {
            // Check for network connectivity before making the call --- REMOVE THIS!!!!
//            if (!networkStateChecker.isOnline()) {
//                currentMatchDetails = MatchDetailUiState.Error("No internet connection available")
//                return@launch
//            }


            currentMatchDetails = MatchDetailUiState.Loading
            currentMatchDetails = try {
                val detail = cricketMatchesRepository.getMatchDetails(id)
                MatchDetailUiState.Success(detail)
            } catch (e: Exception) {
                e.printStackTrace()
                MatchDetailUiState.Error("Loading failed.\n Check your internet.")
            }
        }
    }
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as CricketApplication)
                val cricketMatchesRepository = application.container.cricketMatchesRepository
                val networkStateChecker = AndroidNetworkStateChecker(application.applicationContext)
                CricketViewModel(
                    cricketMatchesRepository = cricketMatchesRepository,
                    networkStateChecker = networkStateChecker
                )
            }
        }
    }
}
