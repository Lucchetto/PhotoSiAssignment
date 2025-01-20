package com.photosi.assignment.section.countries

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.photosi.assignment.domain.CountriesRepository
import com.photosi.assignment.domain.entity.CountryEntity
import com.photosi.assignment.domain.entity.RepoApiErrorEntity
import com.photosi.assignment.domain.entity.RepoApiResult
import com.photosi.assignment.domain.entity.Result
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SelectCountriesViewModel(
    private val countriesRepository: CountriesRepository
) : ViewModel() {

    private val countriesFlow = MutableStateFlow<RepoApiResult<ImmutableList<CountryEntity>, Nothing>?>(null)
    val uiStateFlow: StateFlow<SelectCountriesScreenState?> get() = combine(
        countriesFlow,
        searchQueryFlow
    ) { countries, searchQuery ->
        countries?.let {
            SelectCountriesScreenState(
                searchQuery = searchQuery,
                countries = when (countries) {
                    is Result.Failure -> countries
                    is Result.Success -> {
                        if (searchQuery.isNotBlank()) {
                            // Filter the results
                            Result.Success(
                                countries.value.filter { country ->
                                    country.name.contains(searchQuery, true)
                                }.toImmutableList()
                            )
                        } else {
                            countries
                        }
                    }
                },
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)
    private val searchQueryFlow: MutableStateFlow<String> = MutableStateFlow("")

    init {
        reloadCountries()
    }

    fun updateSearchQuery(query: String) {
        searchQueryFlow.value = query
    }

    fun reloadCountries() {
        viewModelScope.launch {
            countriesFlow.value = null
            countriesFlow.value = countriesRepository.getCountries()
        }
    }
}