package com.uade.costaapp.data.remote

import com.uade.costaapp.data.remote.dto.PropertyDto
import retrofit2.Response
import retrofit2.http.GET

interface CostaAppApiService {
    @GET("api/v1/properties.json")
    suspend fun getProperties(): Response<List<PropertyDto>>
}
