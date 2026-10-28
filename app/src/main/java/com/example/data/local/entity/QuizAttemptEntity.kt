package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.domain.model.QuizAttempt

@Entity(tableName = "quiz_attempts")
data class QuizAttemptEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val categoryId: String,
    val categoryDisplayName: String,
    val totalQuestions: Int,
    val correctAnswers: Int,
    val score: Int,
    val timeTakenSeconds: Int,
    val difficulty: String
) {
    fun toDomain(): QuizAttempt {
        return QuizAttempt(
            id = id,
            timestamp = timestamp,
            categoryId = categoryId,
            categoryDisplayName = categoryDisplayName,
            totalQuestions = totalQuestions,
            correctAnswers = correctAnswers,
            score = score,
            timeTakenSeconds = timeTakenSeconds,
            difficulty = difficulty
        )
    }

    companion object {
        fun fromDomain(attempt: QuizAttempt): QuizAttemptEntity {
            return QuizAttemptEntity(
                id = attempt.id,
                timestamp = attempt.timestamp,
                categoryId = attempt.categoryId,
                categoryDisplayName = attempt.categoryDisplayName,
                totalQuestions = attempt.totalQuestions,
                correctAnswers = attempt.correctAnswers,
                score = attempt.score,
                timeTakenSeconds = attempt.timeTakenSeconds,
                difficulty = attempt.difficulty
            )
        }
    }
}
