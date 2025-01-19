package com.photosi.assignment.section.upload

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BrokenImage
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.CloudUpload
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.FileUpload
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import com.photosi.assignment.R
import com.photosi.assignment.domain.entity.QueuedImageEntity
import com.photosi.assignment.domain.entity.Result
import com.photosi.assignment.ui.component.FullScreenLoading
import com.photosi.assignment.ui.component.SwipeToDeleteContainer
import com.photosi.assignment.ui.component.SwipeToDeleteState
import com.photosi.assignment.ui.component.rememberSwipeToDeleteState
import com.photosi.assignment.ui.theme.PhotoSìAssignmentTheme
import com.photosi.assignment.ui.theme.spacing
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadImagesScreen(
    navController: NavController,
    viewModel: UploadImagesViewModel
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    NotificationPermissionRequester()

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = { Text(stringResource(R.string.upload_images_screen_title)) },
                scrollBehavior = scrollBehavior
            )
        },
        bottomBar = {
            BottomBar(
                fabAction = uiState.fabAction,
                onPhotoPicked = viewModel::addImages,
                onFabClick = viewModel::handleFabClick)

        }
    ) { padding ->
        uiState.queue?.let { queue ->
            ImageQueueList(
                queue = queue,
                imageRequestProvider = remember {
                    {
                        ImageRequest.Builder(LocalPlatformContext.current)
                            .data(viewModel.getFileForQueuedImage(it))
                            .build()
                    }
                },
                allowDelete = uiState.allowDeleteImages,
                onDelete = viewModel::deleteImage,
                modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                contentPadding = padding
            )
        } ?: FullScreenLoading(modifier = Modifier.padding(padding))
    }
}

@Composable
private fun BottomBar(
    fabAction: UploadImagesScreenState.FabAction?,
    onPhotoPicked: (List<Uri>) -> Unit,
    onFabClick: (UploadImagesScreenState.FabAction) -> Unit,
    modifier: Modifier = Modifier
) = BottomAppBar(modifier = modifier) {
    val photoPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickMultipleVisualMedia(),
        onPhotoPicked
    )

    IconButton(
        onClick = {
            photoPicker.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }
    ) {
        Icon(
            Icons.Outlined.PhotoLibrary,
            contentDescription = stringResource(R.string.add_images_gallery_label)
        )
    }

    fabAction?.let {
        @StringRes val labelRes: Int
        val icon: ImageVector
        val containerColor: Color

        when (it) {
            UploadImagesScreenState.FabAction.Upload -> {
                icon = Icons.Outlined.CloudUpload
                labelRes = R.string.upload_label
                containerColor = FloatingActionButtonDefaults.containerColor
            }
            UploadImagesScreenState.FabAction.CancelUpload -> {
                icon = Icons.Outlined.Close
                labelRes = R.string.cancel_label
                containerColor = MaterialTheme.colorScheme.errorContainer
            }
        }

        val animatedContainerColor by animateColorAsState(
            containerColor,
            label = "animatedContainerColor"
        )
        val animatedContentColor by animateColorAsState(
            contentColorFor(containerColor),
            label = "animatedContentColor"
        )

        Spacer(Modifier.weight(1f))
        ExtendedFloatingActionButton(
            text = { Text(stringResource(labelRes)) },
            icon = { Icon(icon, stringResource(labelRes)) },
            onClick = { onFabClick(it) },
            modifier = Modifier.animateContentSize(),
            containerColor = animatedContainerColor,
            contentColor = animatedContentColor
        )
    }
}

@OptIn(ExperimentalUuidApi::class, ExperimentalFoundationApi::class)
@Composable
private fun ImageQueueList(
    queue: ImmutableList<QueuedImageEntity>,
    imageRequestProvider: @Composable (QueuedImageEntity) -> ImageRequest,
    allowDelete: Boolean,
    onDelete: (QueuedImageEntity) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues()
) {
    var draggingToDeleteAnimatingKey by remember { mutableStateOf<Uuid?>(null) }
    val coroutineScope = rememberCoroutineScope()

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = contentPadding
    ) {
        items(queue, key = { it.id.toString() }) { image ->
            val swipeState = rememberSwipeToDeleteState()

            LaunchedEffect(draggingToDeleteAnimatingKey, allowDelete) {
                // Dismiss current swipe to delete state if another item is being dragged or when delete is not allowed
                if (draggingToDeleteAnimatingKey.let { it != null && it != image.id } || !allowDelete) {
                    coroutineScope.launch {
                        swipeState.animateTo(SwipeToDeleteState.Hidden)
                    }
                }
            }

            QueuedImageItem(
                image,
                allowDelete = draggingToDeleteAnimatingKey.let { it == null || it == image.id } && allowDelete,
                onDelete = { onDelete(image) },
                swipeState = swipeState,
                onDrag = remember { { draggingToDeleteAnimatingKey = image.id } },
                onRelease = remember { { draggingToDeleteAnimatingKey = null } },
                imageRequestProvider = imageRequestProvider,
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun QueuedImageItem(
    entity: QueuedImageEntity,
    imageRequestProvider: @Composable (QueuedImageEntity) -> ImageRequest,
    allowDelete: Boolean,
    onDelete: () -> Unit,
    swipeState: AnchoredDraggableState<SwipeToDeleteState> = rememberSwipeToDeleteState(),
    onDrag: () -> Unit,
    onRelease: () -> Unit,
    modifier: Modifier = Modifier,
) = SwipeToDeleteContainer(
    onDeleteConfirm = onDelete,
    state = swipeState,
    onDrag = onDrag,
    onRelease = onRelease,
    enabled = allowDelete,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(MaterialTheme.spacing.level5),
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.level4),
    ) {
        AsyncImage(
            model = imageRequestProvider(entity),
            contentDescription = null,
            modifier = Modifier.size(80.dp).clip(MaterialTheme.shapes.medium),
            contentScale = ContentScale.Crop
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.level3)
        ) {
            Text(entity.fileName)
            QueuedImageStatus(entity.status)
        }
    }
}

@Composable
private fun QueuedImageStatus(
    status: QueuedImageEntity.Status,
    modifier: Modifier = Modifier
) {
    when (status) {
        is QueuedImageEntity.Status.Completed -> when (val it = status.result) {
            is Result.Failure ->
                IconText(
                    icon = Icons.Outlined.BrokenImage,
                    textRes = R.string.failed_label,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = modifier
                )
            is Result.Success -> {
                val clipboardManager = LocalClipboardManager.current

                OutlinedTextField(
                    value = it.value,
                    onValueChange = {},
                    modifier = modifier.fillMaxWidth(),
                    readOnly = true,
                    label = { Text(stringResource(R.string.link_label)) },
                    trailingIcon = {
                        IconButton(
                            onClick = { clipboardManager.setText(AnnotatedString(it.value)) }
                        ) {
                            Icon(
                                Icons.Outlined.ContentCopy,
                                contentDescription = stringResource(R.string.copy)
                            )
                        }
                    },
                    singleLine = true,
                )
            }
        }
        QueuedImageEntity.Status.Ready -> {
            IconText(Icons.Outlined.Schedule, R.string.queued_label, modifier = modifier)
        }
        QueuedImageEntity.Status.Uploading -> {
            IconText(
                icon = Icons.Outlined.FileUpload,
                textRes = R.string.uploading_label,
                tint = MaterialTheme.colorScheme.primary,
                modifier = modifier
            )
        }
    }
}

@Composable
private fun IconText(
    icon: ImageVector,
    @StringRes textRes: Int,
    tint: Color = LocalContentColor.current,
    modifier: Modifier = Modifier,
) = CompositionLocalProvider(LocalContentColor provides tint) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.level2),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null)
        Text(stringResource(textRes))
    }
}

private class FabActionPreviewParamProvider : PreviewParameterProvider<UploadImagesScreenState.FabAction?> {

    override val values: Sequence<UploadImagesScreenState.FabAction?>
        get() = sequenceOf(*UploadImagesScreenState.FabAction.entries.toTypedArray(), null)
}

@PreviewLightDark
@Composable
private fun BottomBarPreview(
    @PreviewParameter(FabActionPreviewParamProvider::class) fabAction: UploadImagesScreenState.FabAction?
) = PhotoSìAssignmentTheme {
    BottomBar(fabAction, {}, {})
}

@OptIn(ExperimentalUuidApi::class)
@PreviewLightDark
@Composable
private fun ImageQueueListPreview() = PhotoSìAssignmentTheme {
    Scaffold {
        ImageQueueList(
            buildList {
                val uuid = Uuid.random()

                add(QueuedImageEntity(uuid, "${uuid}.jpg", QueuedImageEntity.Status.Ready))
                add(QueuedImageEntity(uuid, "${uuid}.jpg", QueuedImageEntity.Status.Uploading))
                add(
                    QueuedImageEntity(
                        id = uuid,
                        fileName = "${uuid}.jpg",
                        status = QueuedImageEntity.Status.Completed(Result.Failure(Unit))
                    )
                )
                add(
                    QueuedImageEntity(
                        id = uuid,
                        fileName = "${uuid}.jpg",
                        status = QueuedImageEntity.Status.Completed(Result.Success("https://example.com"))
                    )
                )
            }.toImmutableList(),
            imageRequestProvider = {
                ImageRequest.Builder(LocalPlatformContext.current)
                    .data(R.drawable.ic_launcher_foreground)
                    .build()
            },
            allowDelete = true,
            onDelete = {},
            modifier = Modifier.padding(it)
        )
    }
}
