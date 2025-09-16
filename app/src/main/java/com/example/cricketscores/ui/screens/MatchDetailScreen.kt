package com.example.cricketscores.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.SportsBaseball
import androidx.compose.material.icons.rounded.SportsCricket
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.wear.compose.foundation.lazy.TransformingLazyColumnState
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.material3.AppCard
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.ButtonDefaults
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.SurfaceTransformation
import androidx.wear.compose.material3.Text
import androidx.wear.compose.material3.lazy.TransformationSpec
import androidx.wear.compose.material3.lazy.transformedHeight
import com.example.cricketscores.R
import com.example.cricketscores.model.Batter
import com.example.cricketscores.model.Bowler
import com.example.cricketscores.model.LiveMatch
import com.example.cricketscores.model.MatchDetails
import com.example.cricketscores.model.RecentMatch
import com.example.cricketscores.utils.TeamIconUtils
import com.google.android.horologist.compose.layout.ColumnItemType
import com.google.android.horologist.compose.layout.rememberResponsiveColumnPadding

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchDetailScreen(
    matchDetailUiState: MatchDetailUiState,
    liveMatch: LiveMatch?,
    recentMatch: RecentMatch?,
    retryAction: () -> Unit,
    modifier: Modifier = Modifier,
    listState: TransformingLazyColumnState,
    transformationSpec : TransformationSpec,
) {
    when (matchDetailUiState) {
        is MatchDetailUiState.Loading -> LoadingScreen(modifier = modifier.fillMaxSize())
        is MatchDetailUiState.Success -> {
            val pullToRefreshState = rememberPullToRefreshState()
            var isRefreshing by remember { mutableStateOf(false) }
            LaunchedEffect(matchDetailUiState) {
                if (isRefreshing) isRefreshing = false
                listState.scrollToItem(0) // Reset scroll to top
            }

            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = {
                    isRefreshing = true
                    retryAction()
                },
                state = pullToRefreshState
            ) {
                val match = matchDetailUiState.matchDetail
                TransformingLazyColumn(
                    modifier = modifier,
                    state = listState,
                    contentPadding = rememberResponsiveColumnPadding(
                        first = ColumnItemType.Card,
                        last = ColumnItemType.Button,
                    )
                ) {
                    // Match Status
                    if (!match.status.isNullOrEmpty()) {
                        item {
                            ResultsAppCard(
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .transformedHeight(this, transformationSpec),
                                transformation = SurfaceTransformation(transformationSpec),
                                onClick = {  },
                                statusText = "Recent",
                                liveMatch = liveMatch,
                                match = match,
                                recentMatch = recentMatch,
                            )
                        }
                    }

                    // Batsmen Section
                    if (match.batsmen.isNotEmpty()) {
                        item{
                            ResultsAppCard(
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .transformedHeight(this, transformationSpec),
                                transformation = SurfaceTransformation(transformationSpec),
                                onClick = {  },
                                statusText = "Live",
                                liveMatch = liveMatch,
                                match = match,
                                recentMatch = recentMatch,
                            )
                        }
                        item {
                            TextSubHeader(
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .transformedHeight(this, transformationSpec),
                                transformation = SurfaceTransformation(transformationSpec),
                                icon = Icons.Rounded.SportsCricket,
                                iconColor = Color.Red,
                                labelText = "Batsman",
                            )
                        }
                        items(match.batsmen) { batter ->
                            BatsmanScoreAppCard(
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .transformedHeight(this, transformationSpec),
                                transformation = SurfaceTransformation(transformationSpec),
                                onClick = {  },
                                batter = batter
                            )
                        }
                    }
                    // Bowlers Section
                    if (match.bowlers.isNotEmpty()) {
                        item {
                            TextSubHeader(
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .transformedHeight(this, transformationSpec),
                                transformation = SurfaceTransformation(transformationSpec),
                                icon = Icons.Rounded.SportsBaseball,
                                iconColor = Color.Red,
                                labelText = "Bowlers",
                            )
                        }
                        items(match.bowlers) { bowler ->
                            BowlerScoreAppCard(
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .transformedHeight(this, transformationSpec),
                                transformation = SurfaceTransformation(transformationSpec),
                                onClick = {  },
                                bowler = bowler
                            )
                        }
                    }
                    item {
                        Button(
                            onClick = { retryAction() },
                            shape = RoundedCornerShape(50),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Refresh,
                                contentDescription = stringResource(R.string.retry),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                            Text(stringResource(R.string.refresh))
                        }
                    }
                }
            }
        }
        is MatchDetailUiState.Error -> ErrorScreen(retryAction, modifier = modifier.fillMaxSize(), errorText = matchDetailUiState.message)
    }
}

@Composable
fun ResultsAppCard(
    modifier: Modifier = Modifier,
    transformation: SurfaceTransformation,
    onClick: () -> Unit,
    statusText: String,
    liveMatch: LiveMatch?,
    match: MatchDetails,
    recentMatch: RecentMatch?,
) {
    AppCard(
        onClick = onClick,
        appName = { Text(match?.match_title!!, color = MaterialTheme.colorScheme.primary)  }, // optional header
        time = { if (statusText.equals("Live", ignoreCase = true)) {
            Box(
                modifier = Modifier
                    .background(Color.Red, RoundedCornerShape(4.dp))
                    .padding(horizontal = 2.dp, vertical = 1.dp)
            ) {
                Text(
                    text = "LIVE",
                    style = MaterialTheme.typography.bodyExtraSmall,
                    color = Color.White
                )
            }
        } else {
            Text(
                statusText,
                style = MaterialTheme.typography.bodyExtraSmall,
                color = MaterialTheme.colorScheme.secondaryDim
            )
        }},        // optional time (can remove if not needed)
        title = { },
        modifier = modifier,
        transformation = transformation
    ) {
        Column {
            if (liveMatch != null) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Team 1 row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(horizontalAlignment = Alignment.Start,){
                            Text(
                                liveMatch?.team1!!.team,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primaryDim
                            )
                            Image(
                                painter = painterResource(TeamIconUtils.getTeamFlagIcon(liveMatch?.team1!!.team)),
                                contentDescription = "${liveMatch?.team1!!.team} flag",
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Text(liveMatch?.team1!!.score, style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.secondary )
                    }

                    // Team 2 row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(horizontalAlignment = Alignment.Start,){
                            Text(
                                liveMatch?.team2!!.team,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primaryDim
                            )
                            Image(
                                painter = painterResource(TeamIconUtils.getTeamFlagIcon(liveMatch?.team2!!.team)),
                                contentDescription = "${liveMatch?.team2!!.team} flag",
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Text(liveMatch?.team2!!.score, style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.secondary)
                    }
                }
            } else if (recentMatch != null) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Team 1 row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(TeamIconUtils.getTeamFlagIcon(recentMatch.team1.team)),
                                contentDescription = "${recentMatch.team1.team} flag",
                                modifier = Modifier.size(18.dp)
                            )
                            Text(recentMatch.team1.team,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primaryDim)
                        }
                        Text(recentMatch.team1.score,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.secondary)
                    }

                    // Team 2 row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(TeamIconUtils.getTeamFlagIcon(recentMatch.team2.team)),
                                contentDescription = "${recentMatch.team2.team} flag",
                                modifier = Modifier.size(18.dp)
                            )
                            Text(recentMatch.team2.team,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primaryDim)
                        }
                        Text(recentMatch.team2.score,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.secondary)
                    }
                }
            }
            if (match?.status != null){
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    thickness = 1.dp, // line thickness
                    color = MaterialTheme.colorScheme.secondaryDim
                )
                Text(match?.status?: "", style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary)
            }

            if(match?.player_of_the_match != null){
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp), // line thickness
                    color = MaterialTheme.colorScheme.secondaryDim
                )
                Column {
                    Text(
                        text = "Player of the Match:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondaryDim,
                    )
                    Text(
                        text = match?.player_of_the_match!!,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            if (match?.Livestatus != null){
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp), // line thickness
                    color = MaterialTheme.colorScheme.secondaryDim
                )
                Text(match?.Livestatus?: "", style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary,)
            }

        }
    }
}

@Composable
fun BatsmanScoreAppCard(
    modifier: Modifier = Modifier,
    transformation: SurfaceTransformation,
    onClick: () -> Unit,
    batter: Batter,
) {
    val formattedSr = batter.strike_rate?.toDoubleOrNull()?.let {
        String.format("%.2f", it)
    } ?: "-"

    AppCard(
        onClick = onClick,
        appName = { Text(batter.name, color = MaterialTheme.colorScheme.primaryDim) },
        time = {
            Text("SR: $formattedSr",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.6f))
        },
        title = { },
        modifier = modifier,
        transformation = transformation
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Runs: ${batter.runs ?: "-"}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary)
                Text("Balls: ${batter.balls ?: "-"}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary)
            }

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Fours: ${batter.fours ?: "-"}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary)
                Text("Sixes: ${batter.sixes ?: "-"}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary)
            }
        }
    }
}
@Composable
fun BowlerScoreAppCard(
    modifier: Modifier = Modifier,
    transformation: SurfaceTransformation,
    onClick: () -> Unit,
    bowler: Bowler,
) {
    val formattedEr = bowler.econ?.toDoubleOrNull()?.let {
        String.format("%.2f", it)
    } ?: "-"
    AppCard(
        onClick = onClick,
        appName = { Text(bowler.name, color = MaterialTheme.colorScheme.primaryDim) },
        time = {
            Text("Eco: $formattedEr",
                style = MaterialTheme.typography.bodyExtraSmall,
                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.6f))
        },
        title = { },
        modifier = modifier,
        transformation = transformation
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Overs: ${bowler.overs ?: "-"}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary)
                Text("Maidens: ${bowler.maidens ?: "-"}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary)
            }

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Runs: ${bowler.runs ?: "-"}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary)
                Text("Wickets: ${bowler.wickets ?: "-"}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary)
            }
        }
    }
}