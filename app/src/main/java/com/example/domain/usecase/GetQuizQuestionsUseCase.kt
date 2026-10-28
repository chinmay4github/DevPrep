package com.example.domain.usecase

import com.example.domain.model.Category
import com.example.domain.model.Difficulty
import com.example.domain.model.Question
import com.example.domain.repository.QuestionRepository

class GetQuizQuestionsUseCase(
    private val repository: QuestionRepository
) {
    suspend operator fun invoke(
        category: Category?,
        count: Int,
        difficulty: Difficulty? = null
    ): List<Question> {
        return repository.getQuestionsForQuiz(
            category = category,
            count = count,
            difficulty = difficulty
        )
    }
}
