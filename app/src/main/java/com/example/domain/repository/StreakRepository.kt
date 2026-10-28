package com.example.domain.repository

import com.example.data.local.entity.StreakCounter
import com.example.domain.model.StreakBadge
import com.example.domain.model.UserDailyStreak
import kotlinx.coroutines.flow.Flow

interface StreakRepository {
    fun getUserDailyStreak(): Flow<UserDailyStreak>
    fun getStreakCounters(): Flow<List<StreakCounter>>
    fun getAllCompletionDates(): Flow<List<String>>
    suspend fun getAllCompletionDatesSync(): List<String>
    suspend fun recordQuizCompletion(score: Int): List<StreakBadge>
    suspend fun simulateDayAdvance(): List<StreakBadge>
    suspend fun resetStreak()
}
