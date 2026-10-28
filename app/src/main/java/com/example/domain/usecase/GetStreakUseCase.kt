package com.example.domain.usecase

import com.example.data.local.entity.StreakCounter
import com.example.domain.model.UserDailyStreak
import com.example.domain.repository.StreakRepository
import kotlinx.coroutines.flow.Flow

class GetStreakUseCase(
    private val streakRepository: StreakRepository
) {
    operator fun invoke(): Flow<UserDailyStreak> {
        return streakRepository.getUserDailyStreak()
    }

    fun getStreakCounters(): Flow<List<StreakCounter>> {
        return streakRepository.getStreakCounters()
    }

    fun getCompletionDates(): Flow<List<String>> {
        return streakRepository.getAllCompletionDates()
    }

    suspend fun getCompletionDatesSync(): List<String> {
        return streakRepository.getAllCompletionDatesSync()
    }
}
