package com.photosi.assignment.domain.entity

import java.io.IOException

sealed interface RepoApiErrorEntity<out CustomError> {

    @JvmInline
    value class Custom<CustomError>(val e: CustomError): RepoApiErrorEntity<CustomError>

    @JvmInline
    value class Network(val e: Throwable): RepoApiErrorEntity<Nothing>

    @JvmInline
    value class Unknown(val e: Throwable): RepoApiErrorEntity<Nothing>
}