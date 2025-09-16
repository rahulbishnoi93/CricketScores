package com.example.cricketscores.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.wear.compose.material3.AppScaffold

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.lazy.rememberTransformationSpec
import com.example.cricketscores.ui.screens.HomeScreen
import com.example.cricketscores.ui.theme.WearAppTheme
import com.example.cricketscores.ui.screens.CricketViewModel
import com.google.android.horologist.compose.layout.ColumnItemType
import com.google.android.horologist.compose.layout.rememberResponsiveColumnPadding
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.pager.HorizontalPager
import androidx.wear.compose.foundation.pager.rememberPagerState
import androidx.wear.compose.material3.HorizontalPageIndicator
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.PageIndicatorDefaults.selectedColor
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.example.cricketscores.ui.screens.CricketUiState
import com.example.cricketscores.ui.screens.InfoScreen
import com.example.cricketscores.ui.screens.MatchDetailScreen

@Composable
fun CricketApp() {
    val cricketViewModel: CricketViewModel =
        viewModel(factory = CricketViewModel.Factory)
    WearAppTheme {
        AppScaffold {
            val listState = rememberTransformingLazyColumnState()
            val transformationSpec = rememberTransformationSpec()

            ScreenScaffold(
                scrollState = listState,
                contentPadding = rememberResponsiveColumnPadding(
                    first = ColumnItemType.ListHeader,
                    last = ColumnItemType.Button,
                ),
                /* *************************** Part 11: EdgeButton *************************** */

            ) { contentPadding ->
                val navController = rememberSwipeDismissableNavController()
                SwipeDismissableNavHost(navController = navController, startDestination = "home") {
                    composable("home") {
                        val pagerState = rememberPagerState(initialPage = 0, pageCount = { 2 })
                        val pages: List<@Composable () -> Unit> = listOf(
                            {
                                HomeScreen(
                                    cricketUiState = cricketViewModel.cricketUiState,
                                    retryAction = cricketViewModel::loadHomeData,
                                    onOpenDetails = { id -> cricketViewModel.getMatchDetails(id)
                                        navController.navigate("details/$id")},
                                    listState = listState,
                                    contentPadding = contentPadding,
                                    transformationSpec = transformationSpec
                                )
                            },
                            {
                                InfoScreen(
                                    state = listState,
                                    transformationSpec = transformationSpec
                                )
                            }
                        )
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            HorizontalPager(
                                state = pagerState,
                                modifier = Modifier.weight(1f).fillMaxSize()

                            ) { page ->
                                pages[page]()
                            }
                            HorizontalPageIndicator(
                                pagerState = pagerState,
                                selectedColor = MaterialTheme.colorScheme.primary,
                                unselectedColor = MaterialTheme.colorScheme.primaryDim,
                                backgroundColor = MaterialTheme.colorScheme.primaryDim.copy(alpha = 0.1f),
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .padding(bottom = 8.dp) // ensure it's visible
                            )
                        }
                    }
                    composable("details/{matchId}") { backStackEntry ->
                        val matchId = backStackEntry.arguments?.getString("matchId") ?: "sample"
                        // Derive the liveMatch and recentMatch from the current state
                        val liveMatch = remember(cricketViewModel.cricketUiState) {
                            if (cricketViewModel.cricketUiState is CricketUiState.Success) {
                                (cricketViewModel.cricketUiState as CricketUiState.Success).liveMatches.find { it.matchId == matchId }
                            } else {
                                null
                            }
                        }
                        val recentMatch = remember(cricketViewModel.cricketUiState) {
                            if (cricketViewModel.cricketUiState is CricketUiState.Success) {
                                (cricketViewModel.cricketUiState as CricketUiState.Success).recentMatches.find { it.matchId == matchId }
                            } else {
                                null
                            }
                        }
                        MatchDetailScreen(
                            matchDetailUiState = cricketViewModel.currentMatchDetails,
                            // Pass the new state properties from the ViewModel
                            liveMatch = liveMatch,
                            recentMatch = recentMatch,
                            retryAction= {
                                cricketViewModel.loadHomeData()
                                cricketViewModel.getMatchDetails(matchId)
                                         },
                            listState = listState,
                            transformationSpec = transformationSpec,
                        )
                    }
                }
            }
        }
    }
}
