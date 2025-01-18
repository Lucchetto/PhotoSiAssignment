package com.photosi.assignment.navigation

import kotlinx.serialization.Serializable

sealed interface AppRoute {

    @Serializable
    data object SelectCountries : AppRoute
}