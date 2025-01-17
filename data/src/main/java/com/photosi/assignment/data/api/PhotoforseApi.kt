package com.photosi.assignment.data.api

import com.photosi.assignment.data.model.CountryNetworkModel
import retrofit2.http.GET

internal interface PhotoforseApi {

    @GET("geographics/countries/")
    suspend fun getCountries(): List<CountryNetworkModel>
}