package com.photosi.assignment.data.util

import com.photosi.assignment.data.mapper.CustomErrorMapper
import com.photosi.assignment.domain.entity.RepoApiErrorEntity
import com.photosi.assignment.domain.entity.Result
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.IOException
import java.net.ConnectException
import java.net.NoRouteToHostException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class RepositoryHelperTest {

    private data class TestCustomError(val e: Exception)

    private val alwaysCustomErrorMapper = object : CustomErrorMapper<TestCustomError> {
        override fun mapTo(e: Exception) = TestCustomError(e)
    }

    private val neverCustomErrorMapper = object : CustomErrorMapper<TestCustomError> {
        override fun mapTo(e: Exception) = null
    }

    @Test
    fun `Test handleApiCall - success`() = runTest {
        // Given
        val apiCall = suspend { "Success" }

        // When
        val result = RepositoryHelper.handleApiCall(alwaysCustomErrorMapper, apiCall)

        // Then
        assertEquals(
            Result.Success<String, RepoApiErrorEntity<TestCustomError>>("Success"),
            result
        )
    }

    @Test
    fun `Test handleApiCall - custom error`() = runTest {
        // Given
        val dummyException = IllegalStateException()
        val apiCall = suspend { throw dummyException }

        // When
        val result = RepositoryHelper.handleApiCall(alwaysCustomErrorMapper, apiCall)

        // Then
        assertEquals(
            Result.Failure<Nothing, RepoApiErrorEntity<TestCustomError>>(
                RepoApiErrorEntity.Custom(TestCustomError(dummyException))
            ),
            result
        )
    }

    @Test
    fun `Test handleApiCall - other errors`() = runTest {
        suspend fun testOtherError(
            e: Exception,
            expectedError: RepoApiErrorEntity<TestCustomError>
        ) {
            // Given
            val apiCall = suspend { throw e }

            // When
            val result = RepositoryHelper.handleApiCall(neverCustomErrorMapper, apiCall)

            // Then
            assertEquals(
                Result.Failure<Nothing, RepoApiErrorEntity<TestCustomError>>(expectedError),
                result
            )
        }

        UnknownHostException().let {
            testOtherError(it, RepoApiErrorEntity.Network(it))
        }
        SocketTimeoutException().let {
            testOtherError(it, RepoApiErrorEntity.Network(it))
        }
        NoRouteToHostException().let {
            testOtherError(it, RepoApiErrorEntity.Network(it))
        }
        ConnectException().let {
            testOtherError(it, RepoApiErrorEntity.Network(it))
        }
        IOException().let {
            testOtherError(it, RepoApiErrorEntity.Unknown(it))
        }
    }
}