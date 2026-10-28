package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.Category
import com.example.domain.model.Difficulty
import com.example.domain.model.Question
import com.example.domain.model.QuestionType
import com.example.domain.usecase.GetQuestionsUseCase
import com.example.domain.usecase.SaveQuestionUseCase
import com.example.domain.usecase.ToggleQuestionActionsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class LibraryFilter {
    ALL,
    BOOKMARKED,
    MASTERED,
    NEEDS_PRACTICE
}

data class LibraryFilters(
    val query: String = "",
    val category: Category? = null,
    val filter: LibraryFilter = LibraryFilter.ALL,
    val difficulty: Difficulty? = null
)

data class LibraryUiState(
    val questions: List<Question> = emptyList(),
    val searchQuery: String = "",
    val selectedCategory: Category? = null,
    val selectedFilter: LibraryFilter = LibraryFilter.ALL,
    val selectedDifficulty: Difficulty? = null,
    val isAddQuestionSheetOpen: Boolean = false,
    val activeQuestionDetail: Question? = null,
    val isLoading: Boolean = true
)

class LibraryViewModel(
    private val getQuestionsUseCase: GetQuestionsUseCase,
    private val toggleQuestionActionsUseCase: ToggleQuestionActionsUseCase,
    private val saveQuestionUseCase: SaveQuestionUseCase
) : ViewModel() {

    private val _filters = MutableStateFlow(LibraryFilters())
    private val _isAddSheetOpen = MutableStateFlow(false)
    private val _activeDetail = MutableStateFlow<Question?>(null)

    val uiState: StateFlow<LibraryUiState> = combine(
        getQuestionsUseCase(),
        _filters,
        _isAddSheetOpen,
        _activeDetail
    ) { allQuestions, filters, addOpen, detail ->
        val filtered = allQuestions.filter { q ->
            val matchesCategory = filters.category == null || q.category == filters.category
            val matchesDiff = filters.difficulty == null || q.difficulty == filters.difficulty
            val matchesFilter = when (filters.filter) {
                LibraryFilter.ALL -> true
                LibraryFilter.BOOKMARKED -> q.isBookmarked
                LibraryFilter.MASTERED -> q.isMastered
                LibraryFilter.NEEDS_PRACTICE -> !q.isMastered
            }
            val matchesQuery = filters.query.isBlank() ||
                    q.title.contains(filters.query, ignoreCase = true) ||
                    q.questionText.contains(filters.query, ignoreCase = true) ||
                    q.answerSummary.contains(filters.query, ignoreCase = true)

            matchesCategory && matchesDiff && matchesFilter && matchesQuery
        }

        LibraryUiState(
            questions = filtered,
            searchQuery = filters.query,
            selectedCategory = filters.category,
            selectedFilter = filters.filter,
            selectedDifficulty = filters.difficulty,
            isAddQuestionSheetOpen = addOpen,
            activeQuestionDetail = detail,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = LibraryUiState()
    )

    fun onSearchQueryChanged(query: String) {
        _filters.update { it.copy(query = query) }
    }

    fun onCategorySelected(category: Category?) {
        _filters.update { it.copy(category = category) }
    }

    fun onFilterSelected(filter: LibraryFilter) {
        _filters.update { it.copy(filter = filter) }
    }

    fun onDifficultySelected(difficulty: Difficulty?) {
        _filters.update { it.copy(difficulty = difficulty) }
    }

    fun openAddQuestionSheet() {
        _isAddSheetOpen.value = true
    }

    fun closeAddQuestionSheet() {
        _isAddSheetOpen.value = false
    }

    fun openDetail(question: Question) {
        _activeDetail.value = question
    }

    fun closeDetail() {
        _activeDetail.value = null
    }

    fun toggleBookmark(id: Long) {
        viewModelScope.launch {
            toggleQuestionActionsUseCase.toggleBookmark(id)
        }
    }

    fun toggleMastered(id: Long) {
        viewModelScope.launch {
            toggleQuestionActionsUseCase.toggleMastered(id)
        }
    }

    fun deleteQuestion(id: Long) {
        viewModelScope.launch {
            toggleQuestionActionsUseCase.delete(id)
            if (_activeDetail.value?.id == id) {
                _activeDetail.value = null
            }
        }
    }

    fun addCustomQuestion(
        category: Category,
        title: String,
        questionText: String,
        answerSummary: String,
        deepDive: String,
        codeSnippet: String?,
        followUp: String,
        difficulty: Difficulty,
        questionType: QuestionType,
        options: List<String>,
        correctOptionIndex: Int
    ) {
        viewModelScope.launch {
            val newQuestion = Question(
                category = category,
                title = title.trim(),
                questionText = questionText.trim(),
                codeSnippet = if (codeSnippet.isNullOrBlank()) null else codeSnippet.trim(),
                options = options,
                correctOptionIndex = correctOptionIndex,
                answerSummary = answerSummary.trim(),
                deepDiveExplanation = if (deepDive.isBlank()) answerSummary.trim() else deepDive.trim(),
                followUpInterviewerQuestion = if (followUp.isBlank()) "What are the runtime performance implications?" else followUp.trim(),
                difficulty = difficulty,
                questionType = questionType
            )
            saveQuestionUseCase(newQuestion)
            _isAddSheetOpen.value = false
        }
    }
}
