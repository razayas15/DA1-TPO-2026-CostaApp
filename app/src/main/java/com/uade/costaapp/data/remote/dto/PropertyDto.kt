package com.uade.costaapp.data.remote.dto

data class PropertyDto(
    val id: String,
    val title: String,
    val price: Double,
    val currency: String,
    val operationType: String,
    val thumbnail: String?,
    val location: LocationDto,
    val attributes: AttributesDto
)

data class LocationDto(
    val city: String,
    val zone: String,
    val latitude: Double,
    val longitude: Double
)

data class AttributesDto(
    val rooms: Int,
    val bathrooms: Int,
    val surface: Double
)
