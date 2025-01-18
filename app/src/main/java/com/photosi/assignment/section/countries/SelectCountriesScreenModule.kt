package com.photosi.assignment.section.countries

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val SelectCountriesScreenModule = module {
    viewModelOf(::SelectCountriesViewModel)
}