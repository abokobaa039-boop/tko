package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.SubscriptionCardEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SubscriptionCardDao {
    @Query("SELECT * FROM subscription_cards ORDER BY createdTimestamp DESC")
    fun getAllCards(): Flow<List<SubscriptionCardEntity>>

    @Query("SELECT * FROM subscription_cards WHERE cardCode = :code LIMIT 1")
    suspend fun getCardByCode(code: String): SubscriptionCardEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCard(card: SubscriptionCardEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCards(cards: List<SubscriptionCardEntity>)

    @Update
    suspend fun updateCard(card: SubscriptionCardEntity)

    @Query("DELETE FROM subscription_cards WHERE id = :id")
    suspend fun deleteCard(id: Long)

    @Query("SELECT COUNT(*) FROM subscription_cards WHERE isUsed = 1")
    fun getUsedCardsCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM subscription_cards WHERE isUsed = 0")
    fun getAvailableCardsCount(): Flow<Int>
}
