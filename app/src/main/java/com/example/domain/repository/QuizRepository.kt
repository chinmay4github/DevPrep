package com.example.domain.repository

import com.example.domain.model.QuizAttempt
import com.example.domain.model.QuizOverallStats
import kotlinx.coroutines.flow.Flow

interface QuizRepository {
    fun getAllAttempts(): Flow<List<QuizAttempt>>
    suspend fun recordAttempt(attempt: QuizAttempt): Long
    suspend fun clearHistory()
    fun getOverallStats(): Flow<QuizOverallStats>
}
