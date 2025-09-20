package com.neox.cricketscores.model

import kotlinx.serialization.Serializable

@Serializable
data class AllMatches(
    val live_matches: List<LiveMatch>,
    val recent_matches: List<RecentMatch>,
    val upcoming_matches: List<ScheduleMatch>
)
