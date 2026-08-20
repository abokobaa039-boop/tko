package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subscription_cards")
data class SubscriptionCardEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val cardCode: String, // 13 digits format XXX-XXX-XXX-XXXX
    val category: String, // "1 Dinar (Month)", "5 Dinars (6 Months)", "10 Dinars (1 Year)"
    val priceDinars: Int, // 1, 5, 10
    val durationDays: Int, // 30, 180, 365
    val isUsed: Boolean = false,
    val usedByUserId: Long? = null,
    val usedByUserName: String? = null,
    val usedByUserPhone: String? = null,
    val usedTimestamp: Long? = null,
    val createdTimestamp: Long = System.currentTimeMillis()
)
