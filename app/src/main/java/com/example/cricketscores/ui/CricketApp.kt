package com.example.cricketscores.ui

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
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.example.cricketscores.ui.screens.CricketUiState
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
                        HomeScreen(
                            cricketUiState = cricketViewModel.cricketUiState,
                            retryAction = cricketViewModel::loadHomeData,
                            onOpenDetails = { id -> cricketViewModel.getMatchDetails(id)
                                                navController.navigate("details/$id")},
                            listState = listState,
                            contentPadding = contentPadding,
                            transformationSpec = transformationSpec
                        )
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
