package com.photosi.assignment.section.countries

import com.photosi.assignment.domain.entity.CountryEntity
import com.photosi.assignment.domain.entity.RepoApiResult
import kotlinx.collections.immutable.ImmutableList

@JvmInline
value class SelectCountriesScreenState(
    val countries: RepoApiResult<ImmutableList<CountryEntity>, Nothing>,
)