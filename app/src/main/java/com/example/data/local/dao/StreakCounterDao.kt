package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.StreakCounter
import kotlinx.coroutines.flow.Flow

@Dao
interface StreakCounterDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(streakCounter: StreakCounter)

    @Query("SELECT * FROM streak_counters ORDER BY date DESC")
    fun getAllStreakCounters(): Flow<List<StreakCounter>>

    @Query("SELECT date FROM streak_counters WHERE isCompleted = 1 ORDER BY date DESC")
    fun getAllCompletionDates(): Flow<List<String>>

    @Query("SELECT date FROM streak_counters WHERE isCompleted = 1 ORDER BY date DESC")
    suspend fun getAllCompletionDatesSync(): List<String>

    @Query("SELECT * FROM streak_counters WHERE date = :date LIMIT 1")
    suspend fun getStreakCounterByDate(date: String): StreakCounter?

    @Query("SELECT COUNT(*) FROM streak_counters WHERE isCompleted = 1")
    suspend fun getCompletedCountSync(): Int

    @Query("DELETE FROM streak_counters")
    suspend fun clearAll()
}
