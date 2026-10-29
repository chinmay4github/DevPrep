package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.domain.model.Category
import com.example.domain.model.Difficulty
import com.example.domain.model.Question
import com.example.domain.model.QuestionType

/**
 * Flashcard Entity in Room DB representing an interview flashcard.
 * Includes the `mastery` status field to track how many times a user
 * has correctly guessed the card during study sessions.
 */
@Entity(
    tableName = "flashcards",
    indices = [
        Index(value = ["category"]),
        Index(value = ["mastery"]),
        Index(value = ["isMastered"])
    ]
)
data class FlashcardEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val questionId: Long = 0,
    val category: String = "",
    val title: String = "",
    val questionText: String = "",
    val answerSummary: String = "",
    val deepDiveExplanation: String = "",
    val codeSnippet: String? = null,
    val scenarioContext: String? = null,
    val mastery: Int = 0, // Number of times user has correctly guessed this card
    val isMastered: Boolean = false,
    val isBookmarked: Boolean = false,
    val difficulty: String = "MEDIUM",
    val updatedAt: Long = System.currentTimeMillis()
) {
    fun toDomain(): Question {
        return Question(
            id = if (questionId > 0) questionId else id,
            category = Category.fromId(category),
            title = title,
            questionText = questionText,
            codeSnippet = codeSnippet,
            scenarioContext = scenarioContext,
            options = emptyList(),
            correctOptionIndex = 0,
            answerSummary = answerSummary,
            deepDiveExplanation = deepDiveExplanation,
            followUpInterviewerQuestion = "",
            difficulty = Difficulty.fromString(difficulty),
            isBookmarked = isBookmarked,
            isMastered = isMastered || mastery >= 5,
            mastery = mastery,
            questionType = QuestionType.FLASHCARD,
            createdAt = updatedAt
        )
    }

    companion object {
        fun fromDomain(question: Question): FlashcardEntity {
            return FlashcardEntity(
                id = question.id,
                questionId = question.id,
                category = question.category.id,
                title = question.title,
                questionText = question.questionText,
                answerSummary = question.answerSummary,
                deepDiveExplanation = question.deepDiveExplanation,
                codeSnippet = question.codeSnippet,
                scenarioContext = question.scenarioContext,
                mastery = question.mastery,
                isMastered = question.isMastered || question.mastery >= 5,
                isBookmarked = question.isBookmarked,
                difficulty = question.difficulty.name,
                updatedAt = System.currentTimeMillis()
            )
        }
    }
}
