package com.photosi.assignment.data.util

import android.os.Bundle
import androidx.core.os.bundleOf

internal object IntentHelper {

    private const val WORKER_COMPLETED_MARKER_KEY = "worker_completed_marker"

    fun workerCompletedMarkerBundle() = bundleOf(WORKER_COMPLETED_MARKER_KEY to true)

    fun isWorkerCompletedMarker(
        bundle: Bundle
    ) = bundle.getBoolean(WORKER_COMPLETED_MARKER_KEY, false)
}