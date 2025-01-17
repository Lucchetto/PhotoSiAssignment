package com.photosi.assignment.data.mapper

import com.photosi.assignment.data.model.CountryNetworkModel
import com.photosi.assignment.domain.entity.CountryEntity
import org.junit.Assert.assertEquals
import org.junit.Test

class CountryEntityNetworkModelMapperTest {

    @Test
    fun `Test mapFrom`() {
        // Given
        val countryNetworkModel = CountryNetworkModel(
            iso = 840,
            isoAlpha2 = "US",
            isoAlpha3 = "USA",
            name = "United States",
            phonePrefix = "+1",
            phoneRegex = "\\+1\\d{10}"
        )

        // When
        val result = CountryNetworkModelMapper.mapFrom(countryNetworkModel)

        // Then
        val expectedCountryEntity = CountryEntity(
            iso = 840,
            isoAlpha2 = "US",
            isoAlpha3 = "USA",
            name = "United States",
            phonePrefix = "+1",
            phoneRegex = "\\+1\\d{10}"
        )
        assertEquals(expectedCountryEntity, result)
    }
}