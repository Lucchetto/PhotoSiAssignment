package com.photosi.assignment.ui.component

import androidx.compose.animation.core.exponentialDecay
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.photosi.assignment.R
import com.photosi.assignment.ui.theme.PhotoSìAssignmentTheme
import com.photosi.assignment.ui.theme.spacing
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

enum class SwipeToDeleteState { Hidden, Peek }

private inline val SwipeToDeleteWidth get() = 100.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun rememberSwipeToDeleteState(): AnchoredDraggableState<SwipeToDeleteState> {
    val density = LocalDensity.current
    return remember(density) {
        AnchoredDraggableState(
            SwipeToDeleteState.Hidden,
            DraggableAnchors {
                SwipeToDeleteState.Hidden at 0f
                SwipeToDeleteState.Peek at with(density) { SwipeToDeleteWidth.toPx() * -1 }
            },
            positionalThreshold = { distance -> distance * 0.5f },
            velocityThreshold = { with(density) { 125.dp.toPx() } },
            tween(),
            exponentialDecay()
        )
    }
}

@Composable
fun SwipeToDeleteIcon(
    icon: ImageVector = Icons.Default.Delete,
    contentDescription: String? = stringResource(R.string.delete)
) = Icon(icon, contentDescription, Modifier.size(28.dp))

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SwipeToDeleteContainer(
    onDeleteConfirm: () -> Unit,
    modifier: Modifier = Modifier,
    state: AnchoredDraggableState<SwipeToDeleteState> = rememberSwipeToDeleteState(),
    icon: @Composable BoxScope.() -> Unit = { SwipeToDeleteIcon() },
    onDrag: () -> Unit = {},
    onRelease: () -> Unit = {},
    enabled: () -> Boolean = { true },
    content: @Composable BoxScope.() -> Unit,
) = Layout(
    content = {
        val layoutOrientation = LocalLayoutDirection.current
        val coroutineScope = rememberCoroutineScope()
        val onDragLambda: (change: PointerInputChange, dragAmount: Float) -> Unit = remember(state, layoutOrientation, enabled, onDrag) {
            val dragEventMultiplier = when (layoutOrientation) {
                LayoutDirection.Ltr -> 1
                LayoutDirection.Rtl -> -1
            }
            { change, dragAmount ->
                change.consume()
                // Dispatch drag event only if enabled and notify onDrag only if event has actually
                // moved
                if (enabled() && state.dispatchRawDelta(dragAmount * dragEventMultiplier) != 0f) {
                    onDrag()
                }
            }
        }
        val onDragCancel: () -> Unit = remember(coroutineScope, state, onRelease) {
            {
                with(state) {
                    anchors.closestAnchor(offset)?.let {
                        coroutineScope.launch {
                            animateTo(it)
                            onRelease()
                        }
                    }
                }
            }
        }

        CompositionLocalProvider(
            LocalContentColor provides contentColorFor(MaterialTheme.colorScheme.error)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(onClick = onDeleteConfirm, role = Role.Button),
                contentAlignment = Alignment.Center,
                content = icon
            )
        }

        Box(
            modifier = Modifier
                .offset {
                    IntOffset(
                        x = state
                            .requireOffset()
                            .roundToInt(),
                        y = 0
                    )
                }
                .pointerInput(onDragLambda, onDragCancel) {
                    try {
                        detectHorizontalDragGestures(
                            onDragEnd = onDragCancel,
                            onDragCancel = onDragCancel,
                            onHorizontalDrag = onDragLambda
                        )
                    } catch (e: CancellationException) {
                        onDragCancel()
                    }
                }

        ) { content() }
    },
    modifier = modifier.background(MaterialTheme.colorScheme.error),
) { measurables, constraints ->
    require(measurables.size == 2)

    val swipeToDeleteWidth = SwipeToDeleteWidth
        .roundToPx()
        .coerceAtMost(constraints.maxWidth)
    val contentPlaceable = measurables[1].measure(
        constraints.copy(minWidth = swipeToDeleteWidth)
    )
    val deleteActionPlaceable = measurables[0].measure(
        Constraints.fixed(swipeToDeleteWidth, contentPlaceable.height)
    )

    layout(contentPlaceable.width, contentPlaceable.height) {
        deleteActionPlaceable.placeRelative(
            x = contentPlaceable.width - deleteActionPlaceable.width,
            y = 0
        )
        contentPlaceable.place(IntOffset.Zero)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
@PreviewLightDark
private fun SwipeToDeleteContainerPreview() = PhotoSìAssignmentTheme {
    Column(
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.level5)
    ) {
        SwipeToDeleteContainer(
            onDeleteConfirm = {},
            content = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(70.dp)
                        .background(MaterialTheme.colorScheme.background),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text("Swipe to delete", color = MaterialTheme.colorScheme.onBackground)
                }
            }
        )

        SwipeToDeleteContainer(
            onDeleteConfirm = {},
            content = {
                Box(
                    modifier = Modifier
                        .height(70.dp)
                        .background(MaterialTheme.colorScheme.background),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text("Swipe to delete", color = MaterialTheme.colorScheme.onBackground)
                }
            }
        )
    }
}