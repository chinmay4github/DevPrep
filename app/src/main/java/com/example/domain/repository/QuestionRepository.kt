package com.example.domain.repository

import com.example.data.local.entity.CategoryEntity
import com.example.domain.model.Category
import com.example.domain.model.Difficulty
import com.example.domain.model.Question
import com.example.domain.model.QuestionType
import kotlinx.coroutines.flow.Flow

interface QuestionRepository {
    fun getAllQuestions(): Flow<List<Question>>
    fun getFilteredQuestions(
        category: Category? = null,
        difficulty: Difficulty? = null,
        questionType: QuestionType? = null,
        isBookmarked: Boolean? = null,
        isMastered: Boolean? = null,
        searchQuery: String = ""
    ): Flow<List<Question>>

    fun getFlashcardsByCategory(categoryId: String): Flow<List<Question>>
    fun getQuestionsByCategory(categoryId: String): Flow<List<Question>>
    fun getAllCategoryEntities(): Flow<List<CategoryEntity>>
    suspend fun insertCategory(category: CategoryEntity)

    fun getQuestionById(id: Long): Flow<Question?>
    suspend fun getQuestionsForQuiz(category: Category?, count: Int, difficulty: Difficulty?): List<Question>
    suspend fun toggleBookmark(id: Long)
    suspend fun toggleMastered(id: Long)
    suspend fun updateNotes(id: Long, notes: String?)
    suspend fun insertQuestion(question: Question): Long
    suspend fun deleteQuestion(id: Long)
    fun getQuestionsCount(): Flow<Int>
}
