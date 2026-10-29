package com.example.di

import android.content.Context
import com.example.data.local.AppDatabase
import com.example.data.repository.QuestionRepositoryImpl
import com.example.data.repository.QuizRepositoryImpl
import com.example.data.repository.StreakRepositoryImpl
import com.example.domain.repository.QuestionRepository
import com.example.domain.repository.QuizRepository
import com.example.domain.repository.StreakRepository
import com.example.domain.usecase.GetCategoryFlashcardsUseCase
import com.example.domain.usecase.GetQuestionsUseCase
import com.example.domain.usecase.GetQuizQuestionsUseCase
import com.example.domain.usecase.GetStatsUseCase
import com.example.domain.usecase.GetStreakUseCase
import com.example.domain.usecase.RecordQuizStreakUseCase
import com.example.domain.usecase.SaveQuestionUseCase
import com.example.domain.usecase.SubmitQuizAttemptUseCase
import com.example.domain.usecase.ToggleQuestionActionsUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

interface AppContainer {
    val questionRepository: QuestionRepository
    val quizRepository: QuizRepository
    val streakRepository: StreakRepository

    val getQuestionsUseCase: GetQuestionsUseCase
    val getQuizQuestionsUseCase: GetQuizQuestionsUseCase
    val submitQuizAttemptUseCase: SubmitQuizAttemptUseCase
    val toggleQuestionActionsUseCase: ToggleQuestionActionsUseCase
    val saveQuestionUseCase: SaveQuestionUseCase
    val getStatsUseCase: GetStatsUseCase
    val getCategoryFlashcardsUseCase: GetCategoryFlashcardsUseCase
    val getStreakUseCase: GetStreakUseCase
    val recordQuizStreakUseCase: RecordQuizStreakUseCase
}

class DefaultAppContainer(private val context: Context) : AppContainer {
    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val database: AppDatabase by lazy {
        AppDatabase.getDatabase(context, appScope)
    }

    override val questionRepository: QuestionRepository by lazy {
        QuestionRepositoryImpl(database.questionDao(), database.categoryDao(), database.flashcardDao())
    }

    override val quizRepository: QuizRepository by lazy {
        QuizRepositoryImpl(database.quizAttemptDao(), database.questionDao())
    }

    override val streakRepository: StreakRepository by lazy {
        StreakRepositoryImpl(
            userStreakDao = database.userStreakDao(),
            dailyActivityDao = database.dailyActivityDao(),
            streakBadgeDao = database.streakBadgeDao(),
            streakCounterDao = database.streakCounterDao()
        )
    }

    override val getQuestionsUseCase: GetQuestionsUseCase by lazy {
        GetQuestionsUseCase(questionRepository)
    }

    override val getCategoryFlashcardsUseCase: GetCategoryFlashcardsUseCase by lazy {
        GetCategoryFlashcardsUseCase(questionRepository)
    }

    override val getQuizQuestionsUseCase: GetQuizQuestionsUseCase by lazy {
        GetQuizQuestionsUseCase(questionRepository)
    }

    override val submitQuizAttemptUseCase: SubmitQuizAttemptUseCase by lazy {
        SubmitQuizAttemptUseCase(quizRepository)
    }

    override val toggleQuestionActionsUseCase: ToggleQuestionActionsUseCase by lazy {
        ToggleQuestionActionsUseCase(questionRepository)
    }

    override val saveQuestionUseCase: SaveQuestionUseCase by lazy {
        SaveQuestionUseCase(questionRepository)
    }

    override val getStatsUseCase: GetStatsUseCase by lazy {
        GetStatsUseCase(quizRepository)
    }

    override val getStreakUseCase: GetStreakUseCase by lazy {
        GetStreakUseCase(streakRepository)
    }

    override val recordQuizStreakUseCase: RecordQuizStreakUseCase by lazy {
        RecordQuizStreakUseCase(streakRepository)
    }
}
