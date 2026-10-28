package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.entity.CategoryEntity
import com.example.domain.model.Category
import com.example.domain.model.Difficulty
import com.example.domain.model.Question
import com.example.domain.model.UserDailyStreak
import com.example.domain.usecase.GetCategoryFlashcardsUseCase
import com.example.domain.usecase.GetQuestionsUseCase
import com.example.domain.usecase.GetStreakUseCase
import com.example.domain.usecase.ToggleQuestionActionsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FlashcardFilters(
    val categoryId: String? = null,
    val difficulty: Difficulty? = null,
    val onlyBookmarked: Boolean = false
)

data class FlashcardUiState(
    val questions: List<Question> = emptyList(),
    val categories: List<CategoryEntity> = emptyList(),
    val currentIndex: Int = 0,
    val isFlipped: Boolean = false,
    val selectedCategoryId: String? = null,
    val selectedDifficulty: Difficulty? = null,
    val onlyBookmarked: Boolean = false,
    val streak: UserDailyStreak = UserDailyStreak(),
    val isLoading: Boolean = true
) {
    val currentQuestion: Question?
        get() = if (questions.isNotEmpty() && currentIndex in questions.indices) questions[currentIndex] else null

    val totalCount: Int
        get() = questions.size

    val progress: Float
        get() = if (totalCount > 0) (currentIndex + 1).toFloat() / totalCount else 0f
}

class FlashcardViewModel(
    private val getQuestionsUseCase: GetQuestionsUseCase,
    private val getCategoryFlashcardsUseCase: GetCategoryFlashcardsUseCase,
    private val toggleQuestionActionsUseCase: ToggleQuestionActionsUseCase,
    private val getStreakUseCase: GetStreakUseCase
) : ViewModel() {

    private val _filters = MutableStateFlow(FlashcardFilters())
    private val _currentIndex = MutableStateFlow(0)
    private val _isFlipped = MutableStateFlow(false)

    val uiState: StateFlow<FlashcardUiState> = combine(
        getQuestionsUseCase(),
        getCategoryFlashcardsUseCase.getAllCategories(),
        _filters,
        combine(_currentIndex, _isFlipped, getStreakUseCase()) { idx, flipped, streak ->
            Triple(idx, flipped, streak)
        }
    ) { allQuestions, categories, filters, indexFlippedStreak ->
        val (index, flipped, streak) = indexFlippedStreak
        val filtered = allQuestions.filter { q ->
            (filters.categoryId == null || q.category.id == filters.categoryId) &&
            (filters.difficulty == null || q.difficulty == filters.difficulty) &&
            (!filters.onlyBookmarked || q.isBookmarked)
        }
        val safeIndex = if (filtered.isEmpty()) 0 else index.coerceIn(0, filtered.lastIndex)
        FlashcardUiState(
            questions = filtered,
            categories = categories,
            currentIndex = safeIndex,
            isFlipped = flipped,
            selectedCategoryId = filters.categoryId,
            selectedDifficulty = filters.difficulty,
            onlyBookmarked = filters.onlyBookmarked,
            streak = streak,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = FlashcardUiState()
    )

    fun flipCard() {
        _isFlipped.update { !it }
    }

    fun nextCard() {
        _isFlipped.value = false
        _currentIndex.update { it + 1 }
    }

    fun prevCard() {
        _isFlipped.value = false
        _currentIndex.update { (it - 1).coerceAtLeast(0) }
    }

    fun jumpToIndex(index: Int) {
        _isFlipped.value = false
        _currentIndex.value = index
    }

    fun shuffle() {
        _isFlipped.value = false
        _currentIndex.value = 0
    }

    fun toggleBookmark() {
        val current = uiState.value.currentQuestion ?: return
        viewModelScope.launch {
            toggleQuestionActionsUseCase.toggleBookmark(current.id)
        }
    }

    fun toggleMastered() {
        val current = uiState.value.currentQuestion ?: return
        viewModelScope.launch {
            toggleQuestionActionsUseCase.toggleMastered(current.id)
        }
    }

    fun setCategoryFilter(categoryId: String?) {
        _isFlipped.value = false
        _currentIndex.value = 0
        _filters.update { it.copy(categoryId = categoryId) }
    }

    fun setCategory(categoryId: String?) = setCategoryFilter(categoryId)

    fun setDifficultyFilter(difficulty: Difficulty?) {
        _isFlipped.value = false
        _currentIndex.value = 0
        _filters.update { it.copy(difficulty = difficulty) }
    }

    fun setDifficulty(difficulty: Difficulty?) = setDifficultyFilter(difficulty)

    fun markMastered() {
        val current = uiState.value.currentQuestion ?: return
        if (!current.isMastered) {
            toggleMastered()
        }
        nextCard()
    }

    fun markNeedsPractice() {
        val current = uiState.value.currentQuestion ?: return
        if (current.isMastered) {
            toggleMastered()
        }
        nextCard()
    }

    fun toggleBookmarkedFilter() {
        _isFlipped.value = false
        _currentIndex.value = 0
        _filters.update { it.copy(onlyBookmarked = !it.onlyBookmarked) }
    }

    fun toggleOnlyBookmarked() = toggleBookmarkedFilter()

    fun clearFilters() {
        _isFlipped.value = false
        _currentIndex.value = 0
        _filters.value = FlashcardFilters()
    }
}
