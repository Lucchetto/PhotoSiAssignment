package com.photosi.assignment.domain.entity

sealed interface RepoApiErrorEntity<out CustomError> {

    data class Custom<CustomError>(val e: CustomError): RepoApiErrorEntity<CustomError>

    data class Network(val e: Throwable): RepoApiErrorEntity<Nothing>

    data class Unknown(val e: Throwable): RepoApiErrorEntity<Nothing>
}