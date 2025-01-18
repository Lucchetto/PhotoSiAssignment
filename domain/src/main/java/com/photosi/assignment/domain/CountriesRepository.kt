package com.photosi.assignment.domain

import com.photosi.assignment.domain.entity.CountryEntity
import com.photosi.assignment.domain.entity.RepoApiResult
import kotlinx.collections.immutable.ImmutableList

interface CountriesRepository {

    suspend fun getCountries(): RepoApiResult<ImmutableList<CountryEntity>, Nothing>
}