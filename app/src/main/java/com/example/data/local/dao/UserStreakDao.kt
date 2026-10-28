package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.UserStreakEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserStreakDao {
    @Query("SELECT * FROM user_streak WHERE id = 1 LIMIT 1")
    fun getUserStreak(): Flow<UserStreakEntity?>

    @Query("SELECT * FROM user_streak WHERE id = 1 LIMIT 1")
    suspend fun getUserStreakSync(): UserStreakEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(streak: UserStreakEntity)

    @Query("DELETE FROM user_streak")
    suspend fun clearAll()
}
