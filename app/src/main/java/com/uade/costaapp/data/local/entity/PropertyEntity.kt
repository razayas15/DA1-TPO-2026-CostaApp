package com.uade.costaapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "properties")
data class PropertyEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String? = null,
    val images: List<String>? = null,
    val price: Double,
    val currency: String,
    val zone: String,
    val rooms: Int,
    val surface: Double,
    val bathrooms: Int,
    val latitude: Double,
    val longitude: Double,
    val operationType: String,
    val imageUrl: String,
    val contactPhone: String,
    val isFavorite: Boolean = false,
    val lastViewedAt: Long = 0L,
    val lastVisit: Long? = null,
    val lastUpdated: Long
)
