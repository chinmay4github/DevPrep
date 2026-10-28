package com.example.domain.model

data class QuizAttempt(
    val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val categoryId: String, // "all" or specific Category.id
    val categoryDisplayName: String,
    val totalQuestions: Int,
    val correctAnswers: Int,
    val score: Int, // Calculated with streak bonus
    val timeTakenSeconds: Int,
    val difficulty: String
) {
    val percentage: Int
        get() = if (totalQuestions > 0) (correctAnswers * 100) / totalQuestions else 0

    val gradeTitle: String
        get() = when {
            percentage >= 90 -> "Principal / Staff Mobile Architect"
            percentage >= 75 -> "Senior Mobile Engineer"
            percentage >= 60 -> "Mid-Level Mobile Engineer"
            else -> "Junior / Associate Engineer"
        }
}

data class CategoryStats(
    val category: Category,
    val totalQuestions: Int,
    val masteredQuestions: Int,
    val bookmarkedQuestions: Int
) {
    val masteryPercentage: Int
        get() = if (totalQuestions > 0) (masteredQuestions * 100) / totalQuestions else 0
}

data class QuizOverallStats(
    val totalQuizzesTaken: Int = 0,
    val averageScorePercentage: Int = 0,
    val highestScore: Int = 0,
    val totalQuestionsMastered: Int = 0,
    val totalQuestionsCount: Int = 0,
    val totalBookmarkedCount: Int = 0,
    val categoryStatsList: List<CategoryStats> = emptyList()
)
