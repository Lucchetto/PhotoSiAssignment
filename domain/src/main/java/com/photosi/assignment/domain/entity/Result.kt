package com.photosi.assignment.domain.entity

sealed interface Result<T, E> {

    data class Success<T, E>(val value: T): Result<T, E>

    data class Failure<T, E>(val error: E): Result<T, E>
}
