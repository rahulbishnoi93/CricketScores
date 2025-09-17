package com.example.cricketscores.ui

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
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
            val navController = rememberSwipeDismissableNavController()
            var hasStartedOnce by remember { mutableStateOf(false) }

            val lifecycleOwner = LocalLifecycleOwner.current
            DisposableEffect(lifecycleOwner,navController) {
                val observer = LifecycleEventObserver { _, event ->
                    if (event == Lifecycle.Event.ON_START) {
                        val currentRoute = navController.currentBackStackEntry?.destination?.route
                        val currentBackStackEntry = navController.currentBackStackEntry
                        when {
                            currentRoute == "home" -> {
                                if (hasStartedOnce) {
                                    Log.d("LifecyclyEvent", "hasStartedOnce: ${hasStartedOnce}")
                                    cricketViewModel.loadHomeDataAll()
                                } else {
                                    hasStartedOnce = true
                                }
                            }
                            currentRoute?.startsWith("details/") == true -> {
                                val matchId = currentBackStackEntry!!.arguments?.getString("matchId")
                                Log.d("LifecyclyEvent", "matchId: ${matchId}")
                                if (hasStartedOnce) {
                                    Log.d("LifecyclyEvent", "hasStartedOnce: ${hasStartedOnce}")
                                    cricketViewModel.loadHomeDataAll()
                                    cricketViewModel.getMatchDetails(matchId!!)
                                } else {
                                    hasStartedOnce = true
                                }
                            }
                        }
                    }
                }
                lifecycleOwner.lifecycle.addObserver(observer)
                onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
            }

            ScreenScaffold(
                scrollState = listState,
                contentPadding = rememberResponsiveColumnPadding(
                    first = ColumnItemType.ListHeader,
                    last = ColumnItemType.Button,
                ),
                /* *************************** Part 11: EdgeButton *************************** */

            ) { contentPadding ->
                SwipeDismissableNavHost(navController = navController, startDestination = "home") {
                    composable("home") {
                        val pagerState = rememberPagerState(initialPage = 0, pageCount = { 2 })
                        val pages: List<@Composable () -> Unit> = listOf(
                            {
                                HomeScreen(
                                    cricketUiState = cricketViewModel.cricketUiState,
                                    retryAction = cricketViewModel::loadHomeDataAll,
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
                                cricketViewModel.loadHomeDataAll()
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
