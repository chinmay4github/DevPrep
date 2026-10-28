package com.example.domain.usecase

import com.example.domain.model.StreakBadge
import com.example.domain.repository.StreakRepository

class RecordQuizStreakUseCase(
    private val streakRepository: StreakRepository
) {
    suspend operator fun invoke(score: Int): List<StreakBadge> {
        return streakRepository.recordQuizCompletion(score)
    }

    suspend fun simulateDayAdvance(): List<StreakBadge> {
        return streakRepository.simulateDayAdvance()
    }

    suspend fun resetStreak() {
        streakRepository.resetStreak()
    }
}
