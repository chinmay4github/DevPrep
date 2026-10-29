package com.example.domain.model

data class Question(
    val id: Long = 0,
    val category: Category,
    val title: String,
    val questionText: String,
    val codeSnippet: String? = null,
    val scenarioContext: String? = null,
    val options: List<String> = emptyList(), // For MCQ, 4 options
    val correctOptionIndex: Int = 0,
    val answerSummary: String, // Concise 3-6 line senior answer
    val deepDiveExplanation: String, // Architecture rationale, trade-offs
    val followUpInterviewerQuestion: String, // Follow-up interviewer question
    val difficulty: Difficulty = Difficulty.MEDIUM,
    val isBookmarked: Boolean = false,
    val isMastered: Boolean = false,
    val mastery: Int = 0, // Number of times user has correctly guessed this card
    val questionType: QuestionType = QuestionType.FLASHCARD,
    val userNotes: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

typealias Flashcard = Question
