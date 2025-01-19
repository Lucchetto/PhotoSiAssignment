package com.photosi.assignment.data.mapper

import androidx.work.Data
import androidx.work.workDataOf
import com.photosi.assignment.domain.UploadImagesWorkerStatusEntity

internal object UploadImagesWorkerStatusEntityRunningMapper {

    private const val CURRENT_ITEM_KEY = "current_item"
    private const val TOTAL_COUNT_KEY = "total_count"
    private const val ESTIMATED_REMAINING_TIME_KEY = "estimated_remaining_time"

    fun mapFrom(value: UploadImagesWorkerStatusEntity.Running): Data = workDataOf(
        CURRENT_ITEM_KEY to value.currentItem,
        TOTAL_COUNT_KEY to value.totalCount,
        ESTIMATED_REMAINING_TIME_KEY to value.estimatedRemainingTime
    )

    fun mapTo(data: Data): UploadImagesWorkerStatusEntity.Running? {
        val currentItem = data.keyValueMap[CURRENT_ITEM_KEY] as Int?
        val totalCount = data.keyValueMap[TOTAL_COUNT_KEY] as Int?
        val estimatedRemainingTime = data.keyValueMap[ESTIMATED_REMAINING_TIME_KEY] as Long?

        return if (currentItem == null || totalCount == null) {
            null
        } else {
            UploadImagesWorkerStatusEntity.Running(
                currentItem = currentItem,
                totalCount = totalCount,
                estimatedRemainingTime = estimatedRemainingTime
            )
        }
    }
}