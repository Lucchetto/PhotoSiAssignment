package com.photosi.assignment.domain.entity

sealed interface Result<T, E> {

    @JvmInline
    value class Success<T, E>(val value: T): Result<T, E>

    @JvmInline
    value class Failure<T, E>(val error: E): Result<T, E>
}
