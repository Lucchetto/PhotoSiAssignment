package com.photosi.assignment.section.countries

import com.photosi.assignment.domain.entity.CountryEntity
import com.photosi.assignment.domain.entity.RepoApiResult

@JvmInline
value class SelectCountriesScreenState(
    val countries: RepoApiResult<List<CountryEntity>, Nothing>,
)