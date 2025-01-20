package com.photosi.assignment.navigation

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val AppNavHostModule = module {
    viewModelOf(::AppNavHostViewModel)
}