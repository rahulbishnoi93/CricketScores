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

package com.example.cricketscores.utils

import com.example.cricketscores.R

/**
 * Utility object for mapping team names to their corresponding flag icon resources.
 */
object TeamIconUtils {
    
    /**
     * Maps team names to their corresponding flag icon resource IDs.
     * Only includes flags that exist in the drawable resources.
     */
    private val teamIconMap = mapOf(
        "IND" to R.drawable.in_flag,
        "PAK" to R.drawable.pk,
        "AUS" to R.drawable.au,
        "ENG" to R.drawable.gb,
        "SA" to R.drawable.za,
        "NZ" to R.drawable.nz,
        "SL" to R.drawable.lk,
        "BAN" to R.drawable.bd,
        "AFG" to R.drawable.af,
        "IRE" to R.drawable.ie,
        "NED" to R.drawable.nl,
        "NEP" to R.drawable.np,
        "UAE" to R.drawable.ae,
        "NAM" to R.drawable.na,
        "HK" to R.drawable.hk
        // Note: ZIM, USA, CAN, PNG, OMAN flags are not available in drawable resources
        // They will fall back to the default icon
    )
    
    /**
     * Gets the flag icon resource ID for a given team name.
     * 
     * @param teamName The team name (e.g., "IND", "PAK", "AUS")
     * @return The drawable resource ID for the team's flag, or a default flag if not found
     */
    fun getTeamFlagIcon(teamName: String): Int {
        // Clean the team name by removing any extra characters and converting to uppercase
        val cleanTeamName = teamName.trim().uppercase()
        
        return teamIconMap[cleanTeamName] ?: R.drawable.ic_flag_placeholder // Default icon if team not found
    }
}
