package com.photosi.assignment.data

import androidx.datastore.preferences.core.byteArrayPreferencesKey

internal object InternalPreferenceKeys {

    inline val COMPLETED_WORKER_ID get() = byteArrayPreferencesKey("tracked_worker_id")
}