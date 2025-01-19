package com.photosi.assignment.data.util

import kotlin.math.roundToLong

internal object TimeHelper {

    /**
     * @param fileSize in bytes
     * @param fileUploadTime in milliseconds
     * @param bytesLeft
     * @return estimated time in milliseconds
     */
    fun calculateRemainingTime(
        fileSize: Long,
        fileUploadTime: Long,
        bytesLeft: Long,
    ): Long {
        val bytesPerMillis = fileSize / fileUploadTime.toDouble()

        return (bytesLeft / bytesPerMillis).roundToLong()
    }
}