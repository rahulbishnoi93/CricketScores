package com.example.cricketscores.data

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.example.cricketscores.model.*
import com.example.cricketscores.network.CricketApiService
import kotlinx.serialization.json.Json

/**
 * Repository interface (unchanged).
 */
interface CricketMatchesRepository {

    suspend fun getAllMatches(): AllMatches
    suspend fun getLiveMatches(): List<LiveMatch>
    suspend fun getRecentMatches(): List<RecentMatch>
    suspend fun getSchedule(): List<ScheduleMatch>
    suspend fun getMatchDetails(id: String): MatchDetails
}

/**
 * Network Implementation of Repository that usually fetches from the
 * cricketApi but will fallback to phone when the watch has no internet.
 */
class NetworkCricketMatchesRepository(
    private val cricketApiService: CricketApiService,
    private val appContext: Context,
    private val phoneDataClient: PhoneDataClient
) : CricketMatchesRepository {

    private val json = Json { ignoreUnknownKeys = true }
    private val TAG = "NetworkCricketRepo"

    private fun hasNetwork(): Boolean {
        try {
            val connectivityManager =
                appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val network = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
            return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
        } catch (t: Throwable) {
            Log.e(TAG, "hasNetwork check failed", t)
            return false
        }
    }

    override suspend fun getAllMatches(): AllMatches {
        return try {
            if (hasNetwork()) {
                cricketApiService.getAllMatches()
            } else {
                val resp = phoneDataClient.requestFromPhone("/cric_scores/get_all_matches")
                Log.d(TAG, "getAllMatches fallback: $resp")
                if (resp != null) json.decodeFromString(resp) else AllMatches(emptyList(), emptyList(), emptyList())
            }
        } catch (t: Throwable) {
            Log.e(TAG, "getAllMatches failed: ${t.message}", t)
            val resp = phoneDataClient.requestFromPhone("/cric_scores/get_all_matches")
            if (resp != null) json.decodeFromString(resp) else AllMatches(emptyList(), emptyList(), emptyList())
        }
    }

    override suspend fun getLiveMatches(): List<LiveMatch> {
        // Try watch network first
        return try {
            if (hasNetwork()) {
                cricketApiService.getLiveMatches()
            } else {
                // fallback to phone
                val resp = phoneDataClient.requestFromPhone("/cric_scores/get_live")
                Log.d(TAG, "getLiveMatches fallback: $resp")
                if (resp != null) json.decodeFromString(resp) else emptyList()
            }
        } catch (t: Throwable) {
            Log.e(TAG, "getLiveMatches failed: ${t.message}", t)
            // as a last resort try phone
                val resp = phoneDataClient.requestFromPhone("/cric_scores/get_live")
                Log.d(TAG, "getLiveMatches fallback: $resp")
                if (resp != null) json.decodeFromString(resp) else emptyList()
        }
    }

    override suspend fun getRecentMatches(): List<RecentMatch> {
        return try {
            if (hasNetwork()) {
                cricketApiService.getRecentMatches()
            } else {
                val resp = phoneDataClient.requestFromPhone("/cric_scores/get_recent")
                Log.d(TAG, "getRecentMatches fallback: $resp")
                if (resp != null) json.decodeFromString(resp) else emptyList()
            }
        } catch (t: Throwable) {
            Log.e(TAG, "getRecentMatches failed", t)
                val resp = phoneDataClient.requestFromPhone("/cric_scores/get_recent")
                if (resp != null) json.decodeFromString(resp) else emptyList()
        }
    }

    override suspend fun getSchedule(): List<ScheduleMatch> {
        return try {
            if (hasNetwork()) {
                cricketApiService.getSchedule()
            } else {
                val resp = phoneDataClient.requestFromPhone("/cric_scores/get_schedule")
                Log.d(TAG, "getSchedule fallback: $resp")
                if (resp != null) json.decodeFromString(resp) else emptyList()
            }
        } catch (t: Throwable) {
            Log.e(TAG, "getSchedule failed", t)
                val resp = phoneDataClient.requestFromPhone("/cric_scores/get_schedule")
                if (resp != null) json.decodeFromString(resp) else emptyList()
        }
    }

    override suspend fun getMatchDetails(id: String): MatchDetails {
        return try {
            if (hasNetwork()) {
                cricketApiService.getMatchDetails(id)
            } else {
                val resp = phoneDataClient.requestFromPhone("/cric_scores/get_match_details/$id")
                if (resp != null) json.decodeFromString(resp) else MatchDetails()
            }
        } catch (t: Throwable) {
            Log.e(TAG, "getMatchDetails failed", t)
                val resp = phoneDataClient.requestFromPhone("/cric_scores/get_match_details/$id")
                if (resp != null) json.decodeFromString(resp) else MatchDetails()
        }
    }
}
