package com.example.data.repository

import com.example.data.local.dao.QuestionDao
import com.example.data.local.dao.QuizAttemptDao
import com.example.data.local.entity.QuizAttemptEntity
import com.example.domain.model.Category
import com.example.domain.model.CategoryStats
import com.example.domain.model.QuizAttempt
import com.example.domain.model.QuizOverallStats
import com.example.domain.repository.QuizRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class QuizRepositoryImpl(
    private val quizAttemptDao: QuizAttemptDao,
    private val questionDao: QuestionDao
) : QuizRepository {

    override fun getAllAttempts(): Flow<List<QuizAttempt>> {
        return quizAttemptDao.getAllAttempts().map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun recordAttempt(attempt: QuizAttempt): Long {
        return quizAttemptDao.insertAttempt(QuizAttemptEntity.fromDomain(attempt))
    }

    override suspend fun clearHistory() {
        quizAttemptDao.clearAllAttempts()
    }

    override fun getOverallStats(): Flow<QuizOverallStats> {
        return combine(
            quizAttemptDao.getAllAttempts(),
            questionDao.getAllQuestions()
        ) { attempts, questions ->
            val totalQuizzes = attempts.size
            val avgPercentage = if (attempts.isNotEmpty()) {
                attempts.map { it.toDomain().percentage }.average().toInt()
            } else {
                0
            }
            val highestScore = attempts.maxOfOrNull { it.score } ?: 0
            val totalQuestionsCount = questions.size
            val totalMastered = questions.count { it.isMastered }
            val totalBookmarked = questions.count { it.isBookmarked }

            val categoryStatsList = Category.entries.map { cat ->
                val catQuestions = questions.filter { it.category == cat.id }
                CategoryStats(
                    category = cat,
                    totalQuestions = catQuestions.size,
                    masteredQuestions = catQuestions.count { it.isMastered },
                    bookmarkedQuestions = catQuestions.count { it.isBookmarked }
                )
            }

            QuizOverallStats(
                totalQuizzesTaken = totalQuizzes,
                averageScorePercentage = avgPercentage,
                highestScore = highestScore,
                totalQuestionsMastered = totalMastered,
                totalQuestionsCount = totalQuestionsCount,
                totalBookmarkedCount = totalBookmarked,
                categoryStatsList = categoryStatsList
            )
        }
    }
}
