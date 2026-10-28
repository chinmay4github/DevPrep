package com.example.data.repository

import com.example.data.local.dao.CategoryDao
import com.example.data.local.dao.QuestionDao
import com.example.data.local.entity.CategoryEntity
import com.example.data.local.entity.QuestionEntity
import com.example.domain.model.Category
import com.example.domain.model.Difficulty
import com.example.domain.model.Question
import com.example.domain.model.QuestionType
import com.example.domain.repository.QuestionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class QuestionRepositoryImpl(
    private val questionDao: QuestionDao,
    private val categoryDao: CategoryDao
) : QuestionRepository {

    override fun getAllQuestions(): Flow<List<Question>> {
        return questionDao.getAllQuestions().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getFilteredQuestions(
        category: Category?,
        difficulty: Difficulty?,
        questionType: QuestionType?,
        isBookmarked: Boolean?,
        isMastered: Boolean?,
        searchQuery: String
    ): Flow<List<Question>> {
        return questionDao.getFilteredQuestions(
            category = category?.id,
            difficulty = difficulty?.name,
            questionType = questionType?.name,
            isBookmarked = isBookmarked,
            isMastered = isMastered,
            searchQuery = searchQuery.trim()
        ).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getFlashcardsByCategory(categoryId: String): Flow<List<Question>> {
        return questionDao.getFlashcardsByCategory(categoryId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getQuestionsByCategory(categoryId: String): Flow<List<Question>> {
        return questionDao.getQuestionsByCategory(categoryId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getAllCategoryEntities(): Flow<List<CategoryEntity>> {
        return categoryDao.getAllCategories()
    }

    override suspend fun insertCategory(category: CategoryEntity) {
        categoryDao.insertCategory(category)
    }

    override fun getQuestionById(id: Long): Flow<Question?> {
        return questionDao.getQuestionById(id).map { it?.toDomain() }
    }

    override suspend fun getQuestionsForQuiz(
        category: Category?,
        count: Int,
        difficulty: Difficulty?
    ): List<Question> {
        val entities = questionDao.getRandomQuestionsForQuiz(
            category = category?.id,
            difficulty = difficulty?.name,
            count = count
        )
        return entities.map { it.toDomain() }
    }

    override suspend fun toggleBookmark(id: Long) {
        questionDao.toggleBookmark(id)
    }

    override suspend fun toggleMastered(id: Long) {
        questionDao.toggleMastered(id)
    }

    override suspend fun updateNotes(id: Long, notes: String?) {
        questionDao.updateNotes(id, notes)
    }

    override suspend fun insertQuestion(question: Question): Long {
        return questionDao.insertQuestion(QuestionEntity.fromDomain(question))
    }

    override suspend fun deleteQuestion(id: Long) {
        questionDao.deleteQuestionById(id)
    }

    override fun getQuestionsCount(): Flow<Int> {
        return questionDao.getQuestionsCount()
    }
}
