package com.example.cricketscores.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Info
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposableTargetMarker
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.wear.compose.foundation.lazy.TransformingLazyColumnState
import androidx.wear.compose.material3.AppCard
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.SurfaceTransformation
import androidx.wear.compose.material3.Text
import androidx.wear.compose.material3.lazy.TransformationSpec
import androidx.wear.compose.material3.lazy.transformedHeight
import com.example.cricketscores.R
import com.google.android.horologist.compose.layout.ColumnItemType
import com.google.android.horologist.compose.layout.rememberResponsiveColumnPadding

@Composable
fun InfoScreen(
    state : TransformingLazyColumnState,
    contentPadding : PaddingValues = rememberResponsiveColumnPadding(
        first = ColumnItemType.ListHeader,
        last = ColumnItemType.Card,
    ),
    transformationSpec : TransformationSpec,
    modifier: Modifier = Modifier,
    ) {
    TransformingLazyColumn(
    state = state,
    contentPadding = contentPadding,
    ) {
        item{
            TextAppHeader(
                modifier =
                    Modifier.fillMaxWidth().transformedHeight(this, transformationSpec),
                transformation = SurfaceTransformation(transformationSpec),
                heading = stringResource(R.string.app_name)
            )
        }
        item {
            AppCard(
                modifier = modifier.fillMaxWidth().transformedHeight(this, transformationSpec),
                transformation = SurfaceTransformation(transformationSpec),
                appImage = {
                    Icon(
                        imageVector = Icons.Rounded.Info,
                        contentDescription = "Info",
                        modifier = Modifier,
                        MaterialTheme.colorScheme.primaryDim
                    )
                },
                appName = { Text("Info") },
                title = {  },
                onClick = { /* ... */ },
            ) {
                Text("This app uses your watch’s internet (Wi-Fi/LTE) to fetch live scores.\nIf the watch is offline, the companion phone app will seamlessly provide the data.",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodySmall
                    )
            }
        }
    }
}