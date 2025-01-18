package com.photosi.assignment.domain

import com.photosi.assignment.domain.entity.CountryEntity
import com.photosi.assignment.domain.entity.RepoApiResult

interface CountriesRepository {

    suspend fun getCountries(): RepoApiResult<List<CountryEntity>, Nothing>
}