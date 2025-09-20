package com.neox.cricketscores.data

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.neox.cricketscores.model.*
import com.neox.cricketscores.network.CricketApiService
import kotlinx.serialization.json.Json
import java.io.IOException

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
            return false
        }
    }

    override suspend fun getAllMatches(): AllMatches {
        return try {
            if (hasNetwork()) {
                cricketApiService.getAllMatches()
            } else {
                val resp = phoneDataClient.requestFromPhone("/cric_scores/get_all_matches")
                if (resp != null) json.decodeFromString(resp) else throw IOException("Something went wrong while reading file")
            }
        } catch (t: Throwable) {
            val resp = phoneDataClient.requestFromPhone("/cric_scores/get_all_matches")
            if (resp != null) json.decodeFromString(resp) else throw IOException("Something went wrong while reading file")
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
                if (resp != null) json.decodeFromString(resp) else throw IOException("Something went wrong while reading file")
            }
        } catch (t: Throwable) {
            // as a last resort try phone
                val resp = phoneDataClient.requestFromPhone("/cric_scores/get_live")
                if (resp != null) json.decodeFromString(resp) else throw IOException("Something went wrong while reading file")
        }
    }

    override suspend fun getRecentMatches(): List<RecentMatch> {
        return try {
            if (hasNetwork()) {
                cricketApiService.getRecentMatches()
            } else {
                val resp = phoneDataClient.requestFromPhone("/cric_scores/get_recent")
                if (resp != null) json.decodeFromString(resp) else throw IOException("Something went wrong while reading file")
            }
        } catch (t: Throwable) {
                val resp = phoneDataClient.requestFromPhone("/cric_scores/get_recent")
                if (resp != null) json.decodeFromString(resp) else throw IOException("Something went wrong while reading file")
        }
    }

    override suspend fun getSchedule(): List<ScheduleMatch> {
        return try {
            if (hasNetwork()) {
                cricketApiService.getSchedule()
            } else {
                val resp = phoneDataClient.requestFromPhone("/cric_scores/get_schedule")
                if (resp != null) json.decodeFromString(resp) else throw IOException("Something went wrong while reading file")
            }
        } catch (t: Throwable) {
                val resp = phoneDataClient.requestFromPhone("/cric_scores/get_schedule")
                if (resp != null) json.decodeFromString(resp) else throw IOException("Something went wrong while reading file")
        }
    }

    override suspend fun getMatchDetails(id: String): MatchDetails {
        return try {
            if (hasNetwork()) {
                cricketApiService.getMatchDetails(id)
            } else {
                val resp = phoneDataClient.requestFromPhone("/cric_scores/get_match_details/$id")
                if (resp != null) json.decodeFromString(resp) else throw IOException("Something went wrong while reading file")
            }
        } catch (t: Throwable) {
                val resp = phoneDataClient.requestFromPhone("/cric_scores/get_match_details/$id")
                if (resp != null) json.decodeFromString(resp) else throw IOException("Something went wrong while reading file")
        }
    }
}
