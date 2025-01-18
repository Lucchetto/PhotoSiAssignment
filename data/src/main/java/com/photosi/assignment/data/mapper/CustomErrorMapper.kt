package com.photosi.assignment.data.mapper

internal interface CustomErrorMapper<CustomError> {

    /**
     * Map an [Exception] to given [CustomError] type
     *
     * @return [CustomError] if the exception can be mapped, null otherwise
     */
    fun mapTo(e: Exception): CustomError?
}