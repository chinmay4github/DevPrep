package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.StreakBadgeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StreakBadgeDao {
    @Query("SELECT * FROM streak_badges ORDER BY daysRequired ASC")
    fun getAllBadges(): Flow<List<StreakBadgeEntity>>

    @Query("SELECT * FROM streak_badges ORDER BY daysRequired ASC")
    suspend fun getAllBadgesSync(): List<StreakBadgeEntity>

    @Query("SELECT * FROM streak_badges WHERE isUnlocked = 1 ORDER BY daysRequired ASC")
    fun getUnlockedBadges(): Flow<List<StreakBadgeEntity>>

    @Query("SELECT * FROM streak_badges WHERE id = :id LIMIT 1")
    suspend fun getBadgeById(id: String): StreakBadgeEntity?

    @Query("SELECT COUNT(*) FROM streak_badges")
    suspend fun getBadgeCountSync(): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(badges: List<StreakBadgeEntity>)

    @Update
    suspend fun updateBadge(badge: StreakBadgeEntity)

    @Query("UPDATE streak_badges SET isUnlocked = 0, unlockedAtTimestamp = NULL")
    suspend fun resetAllBadges()
}
