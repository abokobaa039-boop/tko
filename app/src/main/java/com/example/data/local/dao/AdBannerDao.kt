package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.AdBannerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AdBannerDao {
    @Query("SELECT * FROM ad_banners ORDER BY id ASC")
    fun getAllBanners(): Flow<List<AdBannerEntity>>

    @Query("SELECT * FROM ad_banners WHERE id = :id LIMIT 1")
    fun getBannerById(id: Int): Flow<AdBannerEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateBanners(banners: List<AdBannerEntity>)

    @Update
    suspend fun updateBanner(banner: AdBannerEntity)
}
