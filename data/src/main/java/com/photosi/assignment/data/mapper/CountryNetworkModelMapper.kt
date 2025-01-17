package com.photosi.assignment.data.mapper

import com.photosi.assignment.data.model.CountryNetworkModel
import com.photosi.assignment.domain.entity.CountryEntity

internal object CountryNetworkModelMapper {

    fun mapFrom(from: CountryNetworkModel) = CountryEntity(
        iso = from.iso,
        isoAlpha2 = from.isoAlpha2,
        isoAlpha3 = from.isoAlpha3,
        name = from.name,
        phonePrefix = from.phonePrefix,
        phoneRegex = from.phoneRegex
    )
}