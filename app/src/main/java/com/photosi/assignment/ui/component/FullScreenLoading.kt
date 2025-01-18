package com.photosi.assignment.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark

@Composable
fun FullScreenLoading(
    modifier: Modifier = Modifier
) = Box(
    modifier = modifier.fillMaxSize(),
    contentAlignment = Alignment.Center
) {
    CircularProgressIndicator()
}

@PreviewLightDark
@Composable
private fun FullScreenLoadingPreview() = MaterialTheme {
    Scaffold {
        FullScreenLoading(Modifier.padding(it))
    }
}
