package com.example.domain.usecase

import com.example.domain.model.Category
import com.example.domain.model.Difficulty
import com.example.domain.model.Question
import com.example.domain.model.QuestionType
import com.example.domain.repository.QuestionRepository
import kotlinx.coroutines.flow.Flow

class GetQuestionsUseCase(
    private val repository: QuestionRepository
) {
    operator fun invoke(
        category: Category? = null,
        difficulty: Difficulty? = null,
        questionType: QuestionType? = null,
        isBookmarked: Boolean? = null,
        isMastered: Boolean? = null,
        searchQuery: String = ""
    ): Flow<List<Question>> {
        return repository.getFilteredQuestions(
            category = category,
            difficulty = difficulty,
            questionType = questionType,
            isBookmarked = isBookmarked,
            isMastered = isMastered,
            searchQuery = searchQuery
        )
    }
}
