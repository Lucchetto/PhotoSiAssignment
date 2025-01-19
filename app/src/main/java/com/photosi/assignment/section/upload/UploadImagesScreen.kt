package com.photosi.assignment.section.upload

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.outlined.CloudUpload
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import com.photosi.assignment.R
import com.photosi.assignment.domain.entity.QueuedImageEntity
import com.photosi.assignment.ui.component.FullScreenLoading
import com.photosi.assignment.ui.theme.spacing
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
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

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = { Text(stringResource(R.string.select_images_to_upload_title)) },
                scrollBehavior = scrollBehavior
            )
        },
        bottomBar = {
            BottomAppBar {
                val photoPicker = rememberLauncherForActivityResult(
                    ActivityResultContracts.PickMultipleVisualMedia(),
                    viewModel::addImages
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
                if (uiState.queue?.isNotEmpty() == true) {
                    Spacer(modifier = Modifier.weight(1f))
                    ExtendedFloatingActionButton(
                        text = { Text(stringResource(R.string.upload_label)) },
                        icon = { Icon(Icons.Outlined.CloudUpload, stringResource(R.string.upload_label)) },
                        onClick = viewModel::startUpload
                    )
                }
            }
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
                modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                contentPadding = padding
            )
        } ?: FullScreenLoading(modifier = Modifier.padding(padding))
    }
}

@OptIn(ExperimentalUuidApi::class)
@Composable
private fun ImageQueueList(
    queue: ImmutableList<QueuedImageEntity>,
    imageRequestProvider: @Composable (QueuedImageEntity) -> ImageRequest,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues()
) = LazyColumn(
    modifier = modifier.fillMaxSize(),
    contentPadding = contentPadding
) {
    items(queue, key = { it.id.toByteArray() }) {
        QueuedImageItem(
            it,
            imageRequestProvider = imageRequestProvider,
        )
    }
}

@Composable
private fun QueuedImageItem(
    entity: QueuedImageEntity,
    imageRequestProvider: @Composable (QueuedImageEntity) -> ImageRequest,
    modifier: Modifier = Modifier,
) = Row(
    modifier = modifier.fillMaxWidth().padding(MaterialTheme.spacing.level5),
    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.level4),
) {
    AsyncImage(
        model = imageRequestProvider(entity),
        contentDescription = null,
        modifier = Modifier.size(80.dp).clip(MaterialTheme.shapes.medium),
        contentScale = ContentScale.Crop
    )
    Text(entity.fileName)
}

@OptIn(ExperimentalUuidApi::class)
@PreviewLightDark
@Composable
private fun ImageQueueListPreview() = MaterialTheme {
    Scaffold {
        ImageQueueList(
            List(50) {
                val uuid = Uuid.random()
                QueuedImageEntity(uuid, "${uuid}.png", QueuedImageEntity.Status.Ready)
            }.toImmutableList(),
            imageRequestProvider = {
                ImageRequest.Builder(LocalPlatformContext.current)
                    .data(R.drawable.ic_launcher_foreground)
                    .build()
            },
            modifier = Modifier.padding(it)
        )
    }
}
