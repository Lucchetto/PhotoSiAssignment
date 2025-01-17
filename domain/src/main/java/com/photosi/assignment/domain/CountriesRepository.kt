package com.photosi.assignment.domain

import com.photosi.assignment.domain.entity.CountryEntity

interface CountriesRepository {

    suspend fun getCountries(): Result<List<CountryEntity>>
}