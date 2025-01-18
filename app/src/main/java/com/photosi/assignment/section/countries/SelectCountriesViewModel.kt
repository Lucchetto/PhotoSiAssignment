package com.photosi.assignment.section.countries

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.photosi.assignment.domain.CountriesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SelectCountriesViewModel(
    private val countriesRepository: CountriesRepository
) : ViewModel() {

    private val uiState = MutableStateFlow<SelectCountriesScreenState?>(null)
    val uiStateFlow: StateFlow<SelectCountriesScreenState?> get() = uiState

    init {
        reloadCountries()
    }

    fun reloadCountries() {
        viewModelScope.launch {
            uiState.update { null }

            uiState.value = SelectCountriesScreenState(countriesRepository.getCountries())
        }
    }
}