package com.photosi.assignment.section.upload

import android.content.ContentValues
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.CloudUpload
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import com.photosi.assignment.R
import com.photosi.assignment.domain.UploadImagesWorkerStatusEntity
import com.photosi.assignment.time.TimeFormatters
import com.photosi.assignment.ui.component.FullScreenLoading
import com.photosi.assignment.ui.theme.PhotoSìAssignmentTheme
import com.photosi.assignment.ui.theme.spacing

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
                workerStatus = uiState.workerStatus,
                fabAction = uiState.fabAction,
                onPhotoPicked = viewModel::addImages,
                onFabClick = viewModel::handleFabClick)

        }
    ) { padding ->
        uiState.queue?.let { queue ->
            ImageQueueList(
                queue = queue,
                imageRequestProvider = {
                    ImageRequest.Builder(LocalPlatformContext.current)
                        .data(viewModel.getFileForQueuedImage(it))
                        .build()
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
    workerStatus: UploadImagesWorkerStatusEntity?,
    fabAction: UploadImagesScreenState.FabAction?,
    onPhotoPicked: (List<Uri>) -> Unit,
    onFabClick: (UploadImagesScreenState.FabAction) -> Unit,
    modifier: Modifier = Modifier
) = Column(modifier = modifier.animateContentSize()) {
    (workerStatus as? UploadImagesWorkerStatusEntity.Running)?.let {
        val description = buildString {
            append(stringResource(R.string.upload_running_desc_image_count, it.currentItem + 1, it.totalCount))
            it.estimatedRemainingTime?.let {
                append("\n")
                append(
                    stringResource(
                        R.string.upload_running_desc_estimated_time,
                        TimeFormatters.formatCountdown(LocalContext.current, it)
                    )
                )
            }
        }

        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = BottomAppBarDefaults.containerColor,
            tonalElevation = BottomAppBarDefaults.ContainerElevation
        ) {
            Text(description, modifier = Modifier.padding(MaterialTheme.spacing.level4))
        }
    }

    BottomAppBar {
        val photoPicker = rememberLauncherForActivityResult(
            ActivityResultContracts.PickMultipleVisualMedia(),
            onPhotoPicked
        )
        var pendingPhotoUri by remember { mutableStateOf<Uri?>(null) }
        val cameraLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.TakePicture()
        ) { success ->
            pendingPhotoUri.takeIf { success }?.let { onPhotoPicked(listOf(it)) }
            pendingPhotoUri = null
        }
        val context = LocalContext.current

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
        IconButton(
            onClick = {
                context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, ContentValues())?.let {
                    pendingPhotoUri = it
                    cameraLauncher.launch(it)
                }
            }
        ) {
            Icon(
                Icons.Outlined.CameraAlt,
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
}

private class BottomBarPreviewParamProvider
    : PreviewParameterProvider<Pair<UploadImagesWorkerStatusEntity?, UploadImagesScreenState.FabAction?>> {

    override val values: Sequence<Pair<UploadImagesWorkerStatusEntity?, UploadImagesScreenState.FabAction?>>
        get() = sequenceOf(
            null to null,
            null to UploadImagesScreenState.FabAction.Upload,
            Pair(
                UploadImagesWorkerStatusEntity.Running(1, 25, null),
                UploadImagesScreenState.FabAction.CancelUpload
            ),
            Pair(
                UploadImagesWorkerStatusEntity.Running(1, 25, 95000),
                UploadImagesScreenState.FabAction.CancelUpload
            )
        )
}

@PreviewLightDark
@Composable
private fun BottomBarPreview(
    @PreviewParameter(BottomBarPreviewParamProvider::class) params: Pair<UploadImagesWorkerStatusEntity?, UploadImagesScreenState.FabAction?>
) = PhotoSìAssignmentTheme {
    BottomBar(params.first, params.second, {}, {})
}
