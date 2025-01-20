package com.photosi.assignment.section.countries

import androidx.compose.runtime.Immutable
import com.photosi.assignment.domain.entity.CountryEntity
import com.photosi.assignment.domain.entity.RepoApiResult
import kotlinx.collections.immutable.ImmutableList

@Immutable
data class SelectCountriesScreenState(
    val searchQuery: String,
    val countries: RepoApiResult<ImmutableList<CountryEntity>, Nothing>,
)