package com.uade.costaapp.data.mapper

import com.uade.costaapp.data.local.entity.PropertyEntity
import com.uade.costaapp.data.remote.dto.PropertyDto
import javax.inject.Inject

class PropertyMapper @Inject constructor() {
    fun dtoToEntity(dto: PropertyDto): PropertyEntity = PropertyEntity(
        id            = dto.id,
        title         = dto.title,
        description   = dto.description ?: "Hermosa propiedad ubicada en la costa. Ideal para disfrutar en familia o como inversión.",
        images        = dto.images,
        price         = dto.price,
        currency      = dto.currency,
        zone          = dto.location.zone,
        rooms         = dto.attributes.rooms,
        surface       = dto.attributes.surface,
        bathrooms     = dto.attributes.bathrooms,
        latitude      = dto.location.latitude,
        longitude     = dto.location.longitude,
        // Adaptamos "Venta" / "Alquiler" a "sale" / "rent" para el Design System del Mapa
        operationType = if (dto.operationType.equals("Venta", ignoreCase = true)) "sale" else "rent",
        imageUrl      = dto.thumbnail ?: "",
        contactPhone  = "", // No provisto por la API actual
        lastUpdated   = System.currentTimeMillis()
    )
}
