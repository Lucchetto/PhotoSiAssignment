package com.photosi.assignment.data

import com.photosi.assignment.data.api.PhotoforseApi
import com.photosi.assignment.data.mapper.CountryNetworkModelMapper
import com.photosi.assignment.data.mapper.StubCustomErrorMapper
import com.photosi.assignment.data.util.RepositoryHelper.handleApiCall
import com.photosi.assignment.domain.CountriesRepository

internal class CountriesRepositoryImpl(
    private val photoforseApi: PhotoforseApi
): CountriesRepository {

    override suspend fun getCountries() = handleApiCall(StubCustomErrorMapper) {
        photoforseApi.getCountries().map(CountryNetworkModelMapper::mapFrom)
    }
}