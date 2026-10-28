package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.di.AppContainer

class ViewModelFactory(
    private val appContainer: AppContainer
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(FlashcardViewModel::class.java) -> {
                FlashcardViewModel(
                    getQuestionsUseCase = appContainer.getQuestionsUseCase,
                    getCategoryFlashcardsUseCase = appContainer.getCategoryFlashcardsUseCase,
                    toggleQuestionActionsUseCase = appContainer.toggleQuestionActionsUseCase,
                    getStreakUseCase = appContainer.getStreakUseCase
                ) as T
            }
            modelClass.isAssignableFrom(QuizViewModel::class.java) -> {
                QuizViewModel(
                    getQuizQuestionsUseCase = appContainer.getQuizQuestionsUseCase,
                    submitQuizAttemptUseCase = appContainer.submitQuizAttemptUseCase,
                    toggleQuestionActionsUseCase = appContainer.toggleQuestionActionsUseCase,
                    getStreakUseCase = appContainer.getStreakUseCase,
                    recordQuizStreakUseCase = appContainer.recordQuizStreakUseCase
                ) as T
            }
            modelClass.isAssignableFrom(StatsViewModel::class.java) -> {
                StatsViewModel(
                    getStatsUseCase = appContainer.getStatsUseCase,
                    getStreakUseCase = appContainer.getStreakUseCase,
                    recordQuizStreakUseCase = appContainer.recordQuizStreakUseCase
                ) as T
            }
            modelClass.isAssignableFrom(LibraryViewModel::class.java) -> {
                LibraryViewModel(
                    getQuestionsUseCase = appContainer.getQuestionsUseCase,
                    toggleQuestionActionsUseCase = appContainer.toggleQuestionActionsUseCase,
                    saveQuestionUseCase = appContainer.saveQuestionUseCase
                ) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
