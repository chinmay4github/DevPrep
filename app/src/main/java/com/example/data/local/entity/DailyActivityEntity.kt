package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_activities")
data class DailyActivityEntity(
    @PrimaryKey
    val dateString: String, // Format: YYYY-MM-DD
    val timestamp: Long = System.currentTimeMillis(),
    val quizCount: Int = 1,
    val highestScore: Int = 0
)
