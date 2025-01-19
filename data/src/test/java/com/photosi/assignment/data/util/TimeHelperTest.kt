package com.photosi.assignment.data.util

import org.junit.Assert.assertEquals
import org.junit.Test

class TimeHelperTest {

    @Test
    fun `Test calculateRemainingTime - no rounding`() {
        // Given
        val fileSize = 640L
        val fileUploadTime = 10L
        val bytesLeft = 5760L

        // When
        val result = TimeHelper.calculateRemainingTime(fileSize, fileUploadTime, bytesLeft)

        // Then
        assertEquals(90, result)
    }

    @Test
    fun `Test calculateRemainingTime - rounding down`() {
        // Given
        val fileSize = 1024L
        val fileUploadTime = 5L
        val bytesLeft = 5000L

        // When
        val result = TimeHelper.calculateRemainingTime(fileSize, fileUploadTime, bytesLeft)

        // Then
        assertEquals(24, result)
    }

    @Test
    fun `Test calculateRemainingTime - rounding up`() {
        // Given
        val fileSize = 512L
        val fileUploadTime = 4L
        val bytesLeft = 350L

        // When
        val result = TimeHelper.calculateRemainingTime(fileSize, fileUploadTime, bytesLeft)

        // Then
        assertEquals(3, result)
    }
}