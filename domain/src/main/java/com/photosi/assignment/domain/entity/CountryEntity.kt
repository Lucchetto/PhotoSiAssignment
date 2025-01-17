package com.photosi.assignment.domain.entity

data class CountryEntity(
    val iso: Int,
    val isoAlpha2: String,
    val isoAlpha3: String,
    val name: String,
    val phonePrefix: String,
    val phoneRegex: String
)
