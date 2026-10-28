package com.example.domain.usecase

import com.example.domain.model.Question
import com.example.domain.repository.QuestionRepository

class SaveQuestionUseCase(
    private val repository: QuestionRepository
) {
    suspend operator fun invoke(question: Question): Long {
        return repository.insertQuestion(question)
    }
}
