package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ad_banners")
data class AdBannerEntity(
    @PrimaryKey
    val id: Int, // 1 to 4
    val placementName: String,
    val title: String,
    val description: String,
    val imageUrl: String = "",
    val displayMode: String = "HORIZONTAL", // "HORIZONTAL", "VERTICAL", "POPUP"
    val isActive: Boolean = true
)
