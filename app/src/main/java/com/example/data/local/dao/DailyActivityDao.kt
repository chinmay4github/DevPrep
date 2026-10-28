package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.DailyActivityEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyActivityDao {
    @Query("SELECT * FROM daily_activities ORDER BY dateString DESC")
    fun getAllActivities(): Flow<List<DailyActivityEntity>>

    @Query("SELECT * FROM daily_activities ORDER BY dateString DESC")
    suspend fun getAllActivitiesSync(): List<DailyActivityEntity>

    @Query("SELECT * FROM daily_activities WHERE dateString = :dateString LIMIT 1")
    suspend fun getActivityForDateSync(dateString: String): DailyActivityEntity?

    @Query("SELECT * FROM daily_activities WHERE dateString = :dateString LIMIT 1")
    fun getActivityForDate(dateString: String): Flow<DailyActivityEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateActivity(activity: DailyActivityEntity)

    @Query("SELECT COUNT(*) FROM daily_activities")
    fun getTotalActiveDaysCount(): Flow<Int>

    @Query("DELETE FROM daily_activities")
    suspend fun clearAll()
}
