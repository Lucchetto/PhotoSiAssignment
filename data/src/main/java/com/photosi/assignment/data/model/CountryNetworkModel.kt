package com.photosi.assignment.data.model

import kotlinx.serialization.Serializable

@Serializable
internal data class CountryNetworkModel(
    val iso: Int,
    val isoAlpha2: String,
    val isoAlpha3: String,
    val name: String,
    val phonePrefix: String,
    val phoneRegex: String
)

