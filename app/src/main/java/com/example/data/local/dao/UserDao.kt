package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users ORDER BY registeredTimestamp DESC")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: Long): UserEntity?

    @Query("SELECT * FROM users WHERE phone = :phone LIMIT 1")
    suspend fun getUserByPhone(phone: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity): Long

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("DELETE FROM users WHERE id = :id")
    suspend fun deleteUser(id: Long)

    @Query("UPDATE users SET isIncludedInDraw = :included WHERE id = :id")
    suspend fun updateDrawInclusion(id: Long, included: Boolean)

    @Query("UPDATE users SET isIncludedInDraw = :included")
    suspend fun updateAllDrawInclusion(included: Boolean)

    @Query("SELECT * FROM users WHERE isIncludedInDraw = 1 ORDER BY (gamesWonRounds + gamesWonChawat) DESC")
    fun getEligibleWinners(): Flow<List<UserEntity>>
}
