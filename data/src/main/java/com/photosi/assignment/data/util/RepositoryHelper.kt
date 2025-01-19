package com.photosi.assignment.data.util

import com.photosi.assignment.data.mapper.CustomErrorMapper
import com.photosi.assignment.domain.entity.RepoApiErrorEntity
import com.photosi.assignment.domain.entity.RepoApiResult
import com.photosi.assignment.domain.entity.Result
import kotlinx.coroutines.CancellationException
import java.net.ConnectException
import java.net.NoRouteToHostException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

internal object RepositoryHelper {

    /**
     * Safely run the API call and map exceptions to [RepoApiErrorEntity]s
     */
    suspend fun <T, CustomError> handleApiCall(
        errorMapper: CustomErrorMapper<CustomError>,
        apiCall: suspend () -> T
    ): RepoApiResult<T, CustomError> = try {
        Result.Success(apiCall())
    } catch (e: Exception) {
        // Coroutines cancellation must not be suppressed
        if (e is CancellationException) throw e

        val error = errorMapper.mapTo(e)?.let {
            RepoApiErrorEntity.Custom(it)
        } ?: when (e) {
            is UnknownHostException,
            is SocketTimeoutException,
            is NoRouteToHostException,
            is ConnectException -> RepoApiErrorEntity.Network(e)
            else -> RepoApiErrorEntity.Unknown(e)
        }

        Result.Failure(error)
    }
}