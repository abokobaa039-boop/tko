package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val fullName: String,
    val phone: String,
    val password: String,
    val isPaid: Boolean = false,
    val isGuest: Boolean = false,
    val cardCodeUsed: String? = null,
    val subscriptionExpiryTimestamp: Long = System.currentTimeMillis() + (7L * 24 * 60 * 60 * 1000), // 7 days trial default
    val registeredTimestamp: Long = System.currentTimeMillis(),
    val gamesWonRounds: Int = 0,
    val gamesLostRounds: Int = 0,
    val gamesWonChawat: Int = 0,
    val gamesLostChawat: Int = 0,
    val isIncludedInDraw: Boolean = true
)
