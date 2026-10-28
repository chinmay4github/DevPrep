package com.example.domain.usecase

import com.example.domain.model.QuizAttempt
import com.example.domain.model.QuizOverallStats
import com.example.domain.repository.QuizRepository
import kotlinx.coroutines.flow.Flow

class GetStatsUseCase(
    private val quizRepository: QuizRepository
) {
    fun getOverallStats(): Flow<QuizOverallStats> = quizRepository.getOverallStats()
    fun getRecentAttempts(): Flow<List<QuizAttempt>> = quizRepository.getAllAttempts()
    suspend fun clearHistory() = quizRepository.clearHistory()
}
