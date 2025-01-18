package com.photosi.assignment.domain.entity

typealias RepoApiResult<T, CustomError> = Result<T, RepoApiErrorEntity<CustomError>>