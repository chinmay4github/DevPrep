package com.example.domain.usecase

import com.example.domain.model.QuizAttempt
import com.example.domain.repository.QuizRepository

class SubmitQuizAttemptUseCase(
    private val quizRepository: QuizRepository
) {
    suspend operator fun invoke(attempt: QuizAttempt): Long {
        return quizRepository.recordAttempt(attempt)
    }
}
