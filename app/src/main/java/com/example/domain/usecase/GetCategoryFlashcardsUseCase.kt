package com.example.domain.usecase

import com.example.data.local.entity.CategoryEntity
import com.example.domain.model.Question
import com.example.domain.repository.QuestionRepository
import kotlinx.coroutines.flow.Flow

class GetCategoryFlashcardsUseCase(
    private val repository: QuestionRepository
) {
    fun getFlashcardsByCategory(categoryId: String): Flow<List<Question>> {
        return repository.getFlashcardsByCategory(categoryId)
    }

    fun getAllCategories(): Flow<List<CategoryEntity>> {
        return repository.getAllCategoryEntities()
    }
}
