package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room Entity that stores daily quiz completion dates, timestamps,
 * and completion counts for the kids-themed UI streak and reward system.
 *
 * @param date Unique date string in "yyyy-MM-dd" format representing the completion day.
 * @param completionTimestamp Epoch millisecond timestamp of the latest quiz completion on this date.
 * @param quizzesCompleted Number of quizzes completed by the kid on this date.
 * @param isCompleted Flag indicating whether the daily quiz requirement was completed.
 */
@Entity(tableName = "streak_counters")
data class StreakCounter(
    @PrimaryKey
    val date: String,
    val completionTimestamp: Long = System.currentTimeMillis(),
    val quizzesCompleted: Int = 1,
    val isCompleted: Boolean = true
)
