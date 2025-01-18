package com.photosi.assignment.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.constrainWidth
import androidx.compose.ui.unit.dp
import com.photosi.assignment.ui.plus
import com.photosi.assignment.ui.theme.spacing

private val IllustrationSize = 135.dp

@Composable
fun ErrorStateScreen(
    illustration: @Composable () -> Unit,
    message: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    action: (@Composable () -> Unit)? = null,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) = LazyColumn(
    modifier = modifier.fillMaxSize(),
    contentPadding = contentPadding + PaddingValues(MaterialTheme.spacing.level5),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally
) {
    item {
        Layout(
            content = { illustration() }
        ) { measurables, constraints ->
            val illustrationSizePx = IllustrationSize.roundToPx()
            val width = constraints.constrainWidth(illustrationSizePx)
            val height = constraints.constrainHeight(illustrationSizePx)
            val adjustConstraints = constraints.copy(
                minWidth = width,
                maxWidth = width,
                minHeight = height,
                maxHeight = height
            )

            val placeable = measurables.single().measure(adjustConstraints)

            layout(placeable.width, placeable.height) {
                placeable.place(0, 0)
            }
        }
    }
    item {
        Spacer(Modifier.height(MaterialTheme.spacing.level5))
    }
    item {
        CompositionLocalProvider(
            LocalTextStyle provides LocalTextStyle.current.copy(textAlign = TextAlign.Center),
            content = message
        )
    }
    action?.let {
        item { Spacer(Modifier.height(MaterialTheme.spacing.level5)) }
        item{ it() }
    }
}