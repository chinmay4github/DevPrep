package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.domain.model.Category
import com.example.domain.model.Difficulty
import com.example.domain.model.Question
import com.example.domain.model.QuestionType

@Entity(
    tableName = "questions",
    indices = [
        Index(value = ["category"]),
        Index(value = ["difficulty"]),
        Index(value = ["isMastered"]),
        Index(value = ["isBookmarked"])
    ]
)
data class QuestionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val category: String, // Category.id
    val title: String,
    val questionText: String,
    val codeSnippet: String? = null,
    val scenarioContext: String? = null,
    val optionsJson: String, // Comma or delimiter separated options
    val correctOptionIndex: Int = 0,
    val answerSummary: String,
    val deepDiveExplanation: String,
    val followUpInterviewerQuestion: String,
    val difficulty: String, // Difficulty name
    val isBookmarked: Boolean = false,
    val isMastered: Boolean = false,
    val questionType: String, // QuestionType name
    val userNotes: String? = null,
    val createdAt: Long = System.currentTimeMillis()
) {
    fun toDomain(): Question {
        val cat = Category.fromId(category)
        val diff = Difficulty.fromString(difficulty)
        val type = try {
            QuestionType.valueOf(questionType)
        } catch (e: Exception) {
            QuestionType.FLASHCARD
        }
        val optionsList = if (optionsJson.isBlank()) {
            emptyList()
        } else {
            optionsJson.split("|||")
        }

        return Question(
            id = id,
            category = cat,
            title = title,
            questionText = questionText,
            codeSnippet = codeSnippet,
            scenarioContext = scenarioContext,
            options = optionsList,
            correctOptionIndex = correctOptionIndex,
            answerSummary = answerSummary,
            deepDiveExplanation = deepDiveExplanation,
            followUpInterviewerQuestion = followUpInterviewerQuestion,
            difficulty = diff,
            isBookmarked = isBookmarked,
            isMastered = isMastered,
            questionType = type,
            userNotes = userNotes,
            createdAt = createdAt
        )
    }

    companion object {
        fun fromDomain(question: Question): QuestionEntity {
            return QuestionEntity(
                id = question.id,
                category = question.category.id,
                title = question.title,
                questionText = question.questionText,
                codeSnippet = question.codeSnippet,
                scenarioContext = question.scenarioContext,
                optionsJson = question.options.joinToString("|||"),
                correctOptionIndex = question.correctOptionIndex,
                answerSummary = question.answerSummary,
                deepDiveExplanation = question.deepDiveExplanation,
                followUpInterviewerQuestion = question.followUpInterviewerQuestion,
                difficulty = question.difficulty.name,
                isBookmarked = question.isBookmarked,
                isMastered = question.isMastered,
                questionType = question.questionType.name,
                userNotes = question.userNotes,
                createdAt = question.createdAt
            )
        }
    }
}
