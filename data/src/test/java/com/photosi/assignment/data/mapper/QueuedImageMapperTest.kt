package com.photosi.assignment.data.mapper

import com.photosi.assignment.db.QueuedImage
import com.photosi.assignment.db.QueuedImageStatus
import com.photosi.assignment.domain.entity.QueuedImageEntity
import com.photosi.assignment.domain.entity.Result
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import kotlin.uuid.Uuid
import org.junit.Test
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
class QueuedImageMapperTest {
    
    @Test
    fun `Test mapFrom with Ready status`() {
        // Given
        val id = Uuid.random()
        val queuedImage = QueuedImage(
            id = id.toByteArray(),
            fileName = "test_image.jpg",
            status = QueuedImageStatus.Ready,
            resultUrl = null
        )

        // When
        val result = QueuedImageMapper.mapFrom(queuedImage)

        // Then
        val expected = QueuedImageEntity(
            id = id,
            fileName = "test_image.jpg",
            status = QueuedImageEntity.Status.Ready
        )
        assertEquals(expected, result)
    }

    @Test
    fun `Test mapFrom with Uploading status`() {
        // Given
        val id = Uuid.random()
        val queuedImage = QueuedImage(
            id = id.toByteArray(),
            fileName = "test_image.jpg",
            status = QueuedImageStatus.Uploading,
            resultUrl = null
        )

        // When
        val result = QueuedImageMapper.mapFrom(queuedImage)

        // Then
        val expected = QueuedImageEntity(
            id = id,
            fileName = "test_image.jpg",
            status = QueuedImageEntity.Status.Uploading
        )
        assertEquals(expected, result)
    }

    @Test
    fun `Test mapFrom with Success status`() {
        // Given
        val id = Uuid.random()
        val queuedImage = QueuedImage(
            id = id.toByteArray(),
            fileName = "test_image.jpg",
            status = QueuedImageStatus.Success,
            resultUrl = "http://example.com/result.jpg"
        )

        // When
        val result = QueuedImageMapper.mapFrom(queuedImage)

        // Then
        val expected = QueuedImageEntity(
            id = id,
            fileName = "test_image.jpg",
            status = QueuedImageEntity.Status.Completed(
                Result.Success("http://example.com/result.jpg")
            )
        )
        assertEquals(expected, result)
    }

    @Test
    fun `Test mapFrom with Success status but missing resultUrl`() {
        // Given
        val id = Uuid.random()
        val queuedImage = QueuedImage(
            id = id.toByteArray(),
            fileName = "test_image.jpg",
            status = QueuedImageStatus.Success,
            resultUrl = null
        )

        // When & Then
        assertThrows(
            IllegalArgumentException::class.java,
        ) {
            QueuedImageMapper.mapFrom(queuedImage)
        }
    }

    @Test
    fun `Test mapFrom with Failure status`() {
        // Given
        val id = Uuid.random()
        val queuedImage = QueuedImage(
            id = id.toByteArray(),
            fileName = "test_image.jpg",
            status = QueuedImageStatus.Failure,
            resultUrl = null
        )

        // When
        val result = QueuedImageMapper.mapFrom(queuedImage)

        // Then
        val expected = QueuedImageEntity(
            id = id,
            fileName = "test_image.jpg",
            status = QueuedImageEntity.Status.Completed(Result.Failure(Unit))
        )
        assertEquals(expected, result)
    }
}