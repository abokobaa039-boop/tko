package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "admin_settings")
data class AdminSettingsEntity(
    @PrimaryKey
    val id: Int = 1,
    val adminUsername: String = "uas",
    val adminPassword: String = "6090081",
    val defaultTurnTimerSeconds: Int = 20, // 0 = unlimited / مفتوح
    val cardSkin: String = "classic", // classic, gold, vintage, royal
    val guestTrialDays: Int = 7 // مدة مكوث الزائر / الفترة التجريبية الافتراضية بالأيام
)
