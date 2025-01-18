package com.photosi.assignment.data.mapper

/**
 * Implementation of the [CustomErrorMapper] when there are no custom errors to map
 */
internal object StubCustomErrorMapper: CustomErrorMapper<Nothing> {

    override fun mapTo(e: Exception) = null
}