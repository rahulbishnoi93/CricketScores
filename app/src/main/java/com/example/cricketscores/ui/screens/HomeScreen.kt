package com.example.cricketscores.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.FiberManualRecord
import androidx.compose.material.icons.rounded.PlayCircleFilled
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.SportsBaseball
import androidx.compose.material.icons.rounded.SportsCricket
import androidx.compose.material.icons.rounded.Title
import androidx.compose.material.icons.rounded.WifiTethering
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.wear.compose.foundation.lazy.TransformingLazyColumnState
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.material3.AppCard
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.ListHeader
import androidx.wear.compose.material3.ListSubHeader
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.SurfaceTransformation
import androidx.wear.compose.material3.Text
import androidx.wear.compose.material3.lazy.TransformationSpec
import androidx.wear.compose.material3.lazy.transformedHeight
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material3.ButtonDefaults
import androidx.wear.compose.material3.CircularProgressIndicator
import com.example.cricketscores.R
import com.example.cricketscores.model.Batter
import com.example.cricketscores.model.Bowler
import com.example.cricketscores.model.LiveMatch
import com.example.cricketscores.model.MatchDetails
import com.example.cricketscores.model.RecentMatch
import com.example.cricketscores.model.ScheduleMatch
import com.example.cricketscores.utils.TeamIconUtils
import com.google.android.horologist.compose.layout.ColumnItemType
import com.google.android.horologist.compose.layout.rememberResponsiveColumnPadding

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    cricketUiState: CricketUiState,
    retryAction: () -> Unit,
    listState: TransformingLazyColumnState,
    contentPadding: PaddingValues,
    transformationSpec : TransformationSpec,
    modifier: Modifier = Modifier,
    onOpenDetails: (String) -> Unit
) {
    when (cricketUiState) {
        is CricketUiState.Loading -> LoadingScreen(modifier = modifier.fillMaxSize())
        is CricketUiState.Success -> {
            val pullToRefreshState = rememberPullToRefreshState()
            var isRefreshing by remember { mutableStateOf(false) }

            LaunchedEffect(cricketUiState) {
                listState.scrollToItem(0) // Reset scroll to top
                if (isRefreshing) isRefreshing = false
            }

            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = {
                    isRefreshing = true
                    retryAction()
                },
                state = pullToRefreshState
            ) {
                MatchesGridScreen(
                    cricketUiState.liveMatches,
                    cricketUiState.recentMatches,
                    cricketUiState.schedule,
                    listState = listState,
                    contentPadding = contentPadding,
                    transformationSpec = transformationSpec,
                    onOpenDetails = onOpenDetails,
                    modifier = Modifier,
                    retryAction = retryAction,
                )
            }
        }
        is CricketUiState.Error -> ErrorScreen(retryAction, modifier = modifier.fillMaxSize(), errorText = cricketUiState.message)
    }
}

/**
 * The home screen displaying match grid.
 */
@Composable
fun MatchesGridScreen(
    matches: List<LiveMatch>,
    recentMatches: List<RecentMatch>,
    schedule: List<ScheduleMatch>,
    listState: TransformingLazyColumnState,
    contentPadding: PaddingValues,
    transformationSpec : TransformationSpec,
    onOpenDetails: (String) -> Unit,
    modifier: Modifier = Modifier,
    retryAction: () -> Unit,

) {
    TransformingLazyColumn(
        modifier = modifier,
        state = listState,
        contentPadding = contentPadding,
    ){
        item{
            TextAppHeader(
                modifier =
                    Modifier.fillMaxWidth().transformedHeight(this, transformationSpec),
                transformation = SurfaceTransformation(transformationSpec),
                heading = stringResource(R.string.app_name)
            )
        }
        item{
            TextSubHeader(
                modifier =
                    Modifier.fillMaxWidth().transformedHeight(this, transformationSpec),
                transformation = SurfaceTransformation(transformationSpec),
                icon = Icons.Rounded.Bolt,
                iconColor = Color.Red, // 🔴 red for live
                labelText = "Live Matches",
                subText = if (matches.isEmpty()) "No Live Matches" else null
            )
        }
        if (matches.isEmpty()){
            item{
                Spacer(modifier = Modifier.height(16.dp)) // space between
                Text(
                    modifier = modifier,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyExtraSmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    ),
                    text = "No Live Matches",
                )
            }
        }
        items(items=matches){ match ->
            MatchCard(
                match = match,
                modifier =
                    Modifier.fillMaxWidth().transformedHeight(this, transformationSpec),
                transformation = SurfaceTransformation(transformationSpec),
                onClick = { onOpenDetails(match.matchId) }
            )
        }
        item{
            TextSubHeader(
                modifier =
                    Modifier.fillMaxWidth().transformedHeight(this, transformationSpec),
                transformation = SurfaceTransformation(transformationSpec),
                icon = Icons.Rounded.CheckCircle,
                iconColor = Color.Gray,
                labelText = "Recent Matches",
            )
        }
        if (recentMatches.isEmpty()){
            item{
                Spacer(modifier = Modifier.height(16.dp)) // space between
                Text(
                    modifier = modifier,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyExtraSmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    ),
                    text = "No Recent Matches",
                )
            }
        }
        items(items=recentMatches){ match ->
            RecentMatchCard(
                match = match,
                modifier =
                    Modifier.fillMaxWidth().transformedHeight(this, transformationSpec),
                transformation = SurfaceTransformation(transformationSpec),
                onClick = { onOpenDetails(match.matchId) }
            )
        }
        item{
            TextSubHeader(
                modifier =
                    Modifier.fillMaxWidth().transformedHeight(this, transformationSpec),
                transformation = SurfaceTransformation(transformationSpec),
                icon = Icons.Rounded.Schedule,
                iconColor = Color.Blue,
                labelText = "Upcoming Matches",
                subText = if (schedule.isEmpty()) "No Scheduled Matches" else null
            )
        }
        if (schedule.isEmpty()){
            item{
                Spacer(modifier = Modifier.height(16.dp)) // space between
                Text(
                    modifier = modifier,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyExtraSmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    ),
                    text = "No Scheduled Matches",
                )
            }
        }
        items(items=schedule){ s ->
            ScheduleCard(
                match = s,
                modifier =
                    Modifier.fillMaxWidth().transformedHeight(this, transformationSpec),
                transformation = SurfaceTransformation(transformationSpec),
                onClick = { }
            )
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
@Composable
fun ScheduleCard(
    match: ScheduleMatch,
    modifier: Modifier = Modifier,
    transformation: SurfaceTransformation,
    onClick: () -> Unit,
) {
    AppCard(
        onClick = {},
        appName = {  }, // optional header
        time = {  },        // optional time (can remove if not needed)
        title = { },
        modifier = modifier,
        transformation = transformation
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(match.date, style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            ) )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Team 1
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    Image(
                        painter = painterResource(TeamIconUtils.getTeamFlagIcon(match.team1)),
                        contentDescription = "${match.team1} flag",
                        modifier = Modifier.size(18.dp)
                    )
                    Text(match.team1, style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primaryDim,)
                }

                // VS text styling
                Text(
                    "VS", style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primaryDim,
                )

                // Team 2
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(match.team2, style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primaryDim,)
                    Image(
                        painter = painterResource(TeamIconUtils.getTeamFlagIcon(match.team2)),
                        contentDescription = "${match.team2} flag",
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Match info styling
            Text(
                match.details,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontStyle = FontStyle.Italic
                ),
                color = MaterialTheme.colorScheme.secondaryDim,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }

    }
}
@Composable
fun RecentMatchCard(
    match: RecentMatch,
    modifier: Modifier = Modifier,
    transformation: SurfaceTransformation,
    onClick: () -> Unit,
) {
    AppCard(
        onClick = onClick,
        appName = { Text(match.matchSummary, style = MaterialTheme.typography.bodyExtraSmall,
            color = MaterialTheme.colorScheme.primary, ) },
        time = { Text("RECENT", style = MaterialTheme.typography.bodyExtraSmall,
            color = MaterialTheme.colorScheme.secondaryDim, ) },
        title = { },
        modifier = modifier,
        transformation = transformation
    ) {
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
                Column(horizontalAlignment = Alignment.Start,
                ) {
                    Text(match.team1.team, style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primaryDim,
                    )
                    Image(
                        painter = painterResource(TeamIconUtils.getTeamFlagIcon(match.team1.team)),
                        contentDescription = "${match.team1.team} flag",
                        modifier = Modifier.size(18.dp)
                    )
                }
                Text(match.team1.score, style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary,)
            }

            // Team 2 row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(horizontalAlignment = Alignment.Start,) {
                    Text(match.team2.team, style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primaryDim,
                    )
                    Image(
                        painter = painterResource(TeamIconUtils.getTeamFlagIcon(match.team2.team)),
                        contentDescription = "${match.team2.team} flag",
                        modifier = Modifier.size(18.dp)
                    )
                }
                Text(match.team2.score, style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary,)
            }

            // Result line
            Text(match.result, style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary,)
        }
    }
}

@Composable
fun TextAppHeader(modifier: Modifier = Modifier, transformation: SurfaceTransformation, heading: String) {
    ListHeader(
        modifier = modifier,
        transformation = transformation,
    ) {
        Text(
            modifier = modifier,
            textAlign = TextAlign.Center,
            text = heading,
            color = MaterialTheme.colorScheme.primary,
        )
    }
}

@Composable
fun TextSubHeader(modifier: Modifier = Modifier,
                  transformation: SurfaceTransformation,
                  icon: ImageVector,       // pass the icon
                  iconColor: Color,        // pass the color
                  labelText: String,        // make text configurable too
                  subText: String? = null
) {
    ListSubHeader(
        modifier = modifier,
        transformation = transformation,
        icon = {
            Icon(
                imageVector = icon,
                contentDescription = "triggers open message action",
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background( MaterialTheme.colorScheme.onBackground,)
                    .padding(3.dp)  // gives breathing room inside the circle
            )
        },
        label = {
            Column {
                Text(
                    modifier = modifier,
                    textAlign = TextAlign.Center,
                    text = labelText,
                    color = MaterialTheme.colorScheme.primaryDim,
                )
            }
        },
    )
}
/**
 * The home screen displaying the loading message.
 */
@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    val messages = listOf(
        "preparing the numbers...",
        "Fetching the latest scores...",
        "Warming up the pitch...",
        "Setting the field positions...",
        "Getting things ready...",
        "Almost there, hold tight...",
        "Crunching the stats..."
    )
    // Pick one random message for this load
    val currentMessage = remember { messages.random() }
//
//    val infiniteTransition = rememberInfiniteTransition(label = "")
//    val rotation = infiniteTransition.animateFloat(
//        initialValue = 0f,
//        targetValue = 360f,
//        animationSpec = infiniteRepeatable(
//            animation = tween(1500, easing = LinearEasing),
//            repeatMode = RepeatMode.Restart
//        ), label = ""
//    )

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
//        Image(
//            painter = painterResource(id = R.drawable.loading_ball), // use ball-only PNG
//            contentDescription = "Rotating Cricket Ball",
//            modifier = Modifier
//                .size(60.dp)
//                .rotate(rotation.value)
//        )
        CircularProgressIndicator()

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = currentMessage,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
        )
    }
}

/**
 * The home screen displaying error message with re-attempt button.
 */
@Composable
fun ErrorScreen(retryAction: () -> Unit, modifier: Modifier = Modifier, errorText: String) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            modifier = Modifier.size(72.dp),
            imageVector = Icons.Default.Warning,
            contentDescription = errorText
        )
        Text(
            text = errorText,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        // A styled button for the retry action
        Button(
            onClick = retryAction,
            shape = RoundedCornerShape(24.dp), // More rounded button shape
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier
        ) {
            Text(
                text = stringResource(R.string.retry),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }
}

// TODO: Create a Chip Composable
@Composable
fun MatchCard(
    match: LiveMatch?,
    modifier: Modifier = Modifier,
    transformation: SurfaceTransformation,
    onClick: () -> Unit,
) {
    AppCard(
        onClick = onClick,
        appName = { Text(match?.liveMatchSummary!!, style = MaterialTheme.typography.bodyExtraSmall ,
            color = MaterialTheme.colorScheme.primary,) }, // optional header
        time = {
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
        },        // optional time (can remove if not needed)
        title = { },
        modifier = modifier,
        transformation = transformation
    ) {
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
                    Text(match?.team1!!.team, style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primaryDim,)
                    Image(
                        painter = painterResource(TeamIconUtils.getTeamFlagIcon(match?.team1!!.team)),
                        contentDescription = "${match.team1.team} flag",
                        modifier = Modifier.size(18.dp)
                    )
                }
                Text(match?.team1!!.score, style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary,)
            }

            // Team 2 row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(horizontalAlignment = Alignment.Start,){
                    Text(match?.team2!!.team, style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primaryDim,)
                    Image(
                        painter = painterResource(TeamIconUtils.getTeamFlagIcon(match?.team2!!.team)),
                        contentDescription = "${match?.team2!!.team} flag",
                        modifier = Modifier.size(18.dp)
                    )
                }
                Text(match?.team2!!.score, style = MaterialTheme.typography.bodyMedium,
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