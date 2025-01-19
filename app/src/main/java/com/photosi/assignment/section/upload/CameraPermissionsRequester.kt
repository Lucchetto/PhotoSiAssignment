package com.photosi.assignment.section.upload

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.photosi.assignment.R

interface CameraPermissionsRequester {

    fun requestIfNecessary()
}

private class CameraPermissionsRequesterImpl(
    private val activity: Activity,
    private val launcher: ManagedActivityResultLauncher<String, Boolean>,
    private val onGranted: () -> Unit,
): CameraPermissionsRequester {

    override fun requestIfNecessary() {
        when {
            // We use scoped storage on Android 10 and above
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> onGranted()
            ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> onGranted()
            ActivityCompat.shouldShowRequestPermissionRationale(
                activity, Manifest.permission.WRITE_EXTERNAL_STORAGE

            ) -> Toast.makeText(
                activity,
                activity.getString(R.string.write_storage_rationale_desc),
                Toast.LENGTH_LONG
            ).show()
            else -> launcher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }
}

@Composable
fun rememberStoragePermissionRequester(onGranted: () -> Unit): CameraPermissionsRequester {
    val activity = requireNotNull(LocalActivity.current)
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { success ->
        if (success) {
            onGranted()
        } else {
            Toast.makeText(
                activity,
                activity.getString(R.string.write_storage_denied_desc),
                Toast.LENGTH_LONG
            ).show()
        }
    }
    return remember(activity, launcher) {
        CameraPermissionsRequesterImpl(activity, launcher, onGranted)
    }
}