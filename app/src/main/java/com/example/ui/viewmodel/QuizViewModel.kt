package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.Category
import com.example.domain.model.Difficulty
import com.example.domain.model.Question
import com.example.domain.model.QuizAttempt
import com.example.domain.model.StreakBadge
import com.example.domain.model.UserDailyStreak
import com.example.domain.usecase.GetQuizQuestionsUseCase
import com.example.domain.usecase.GetStreakUseCase
import com.example.domain.usecase.RecordQuizStreakUseCase
import com.example.domain.usecase.SubmitQuizAttemptUseCase
import com.example.domain.usecase.ToggleQuestionActionsUseCase
import com.example.domain.util.KidsStreakReward
import com.example.domain.util.StreakCalculator
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class QuizStage {
    SETUP,
    IN_PROGRESS,
    COMPLETED
}

data class QuestionAnswerResult(
    val question: Question,
    val selectedOptionIndex: Int, // -1 if timed out
    val isCorrect: Boolean,
    val isTimedOut: Boolean = false
)

data class QuizUiState(
    val stage: QuizStage = QuizStage.SETUP,
    val selectedCategory: Category? = null,
    val selectedQuestionCount: Int = 10,
    val selectedDifficulty: Difficulty? = null,
    val timePerQuestionSeconds: Int = 30, // 0 means Unlimited
    val remainingSecondsForQuestion: Int = 30,
    val isTimedOut: Boolean = false,
    val questions: List<Question> = emptyList(),
    val currentQuestionIndex: Int = 0,
    val selectedOptionIndex: Int? = null,
    val isAnswerSubmitted: Boolean = false,
    val score: Int = 0,
    val speedBonusTotal: Int = 0,
    val correctCount: Int = 0,
    val timedOutCount: Int = 0,
    val currentStreak: Int = 0,
    val maxStreak: Int = 0,
    val answerResults: List<QuestionAnswerResult> = emptyList(),
    val timeElapsedSeconds: Int = 0,
    val isSavingAttempt: Boolean = false,
    val savedAttemptId: Long? = null,
    val userDailyStreak: UserDailyStreak = UserDailyStreak(),
    val newlyUnlockedBadges: List<StreakBadge> = emptyList(),
    val showBadgeCelebrationDialog: Boolean = false,
    val completionDates: List<String> = emptyList(),
    val consecutiveDays: Int = 0,
    val kidsStreakReward: KidsStreakReward = StreakCalculator.getKidsStreakReward(0)
) {
    val currentQuestion: Question?
        get() = if (questions.isNotEmpty() && currentQuestionIndex in questions.indices) {
            questions[currentQuestionIndex]
        } else null

    val isLastQuestion: Boolean
        get() = currentQuestionIndex >= questions.size - 1

    val percentage: Int
        get() = if (questions.isNotEmpty()) (correctCount * 100) / questions.size else 0

    val gradeTitle: String
        get() = when {
            percentage >= 90 -> "Principal Mobile Architect 🌟"
            percentage >= 75 -> "Senior Mobile Engineer 🚀"
            percentage >= 60 -> "Mid-Level Mobile Engineer 🌱"
            else -> "Junior / Associate Engineer 📚"
        }

    val countdownFraction: Float
        get() = if (timePerQuestionSeconds > 0) {
            (remainingSecondsForQuestion.toFloat() / timePerQuestionSeconds).coerceIn(0f, 1f)
        } else 1f
}

class QuizViewModel(
    private val getQuizQuestionsUseCase: GetQuizQuestionsUseCase,
    private val submitQuizAttemptUseCase: SubmitQuizAttemptUseCase,
    private val toggleQuestionActionsUseCase: ToggleQuestionActionsUseCase,
    private val getStreakUseCase: GetStreakUseCase,
    private val recordQuizStreakUseCase: RecordQuizStreakUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    private var totalElapsedTimerJob: Job? = null
    private var countdownTimerJob: Job? = null

    init {
        viewModelScope.launch {
            getStreakUseCase().collect { streak ->
                _uiState.update { it.copy(userDailyStreak = streak) }
            }
        }
        viewModelScope.launch {
            getStreakUseCase.getCompletionDates().collect { dates ->
                val consecutive = calculateConsecutiveDays(dates)
                val reward = getKidsStreakReward(consecutive)
                _uiState.update {
                    it.copy(
                        completionDates = dates,
                        consecutiveDays = consecutive,
                        kidsStreakReward = reward
                    )
                }
            }
        }
    }

    fun selectCategory(category: Category?) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    fun selectQuestionCount(count: Int) {
        _uiState.update { it.copy(selectedQuestionCount = count) }
    }

    fun selectDifficulty(difficulty: Difficulty?) {
        _uiState.update { it.copy(selectedDifficulty = difficulty) }
    }

    fun selectTimePerQuestion(seconds: Int) {
        _uiState.update {
            it.copy(
                timePerQuestionSeconds = seconds,
                remainingSecondsForQuestion = seconds
            )
        }
    }

    fun startQuiz() {
        viewModelScope.launch {
            val questions = getQuizQuestionsUseCase(
                category = _uiState.value.selectedCategory,
                count = _uiState.value.selectedQuestionCount,
                difficulty = _uiState.value.selectedDifficulty
            ).shuffled()

            val initialTime = _uiState.value.timePerQuestionSeconds

            _uiState.update {
                it.copy(
                    stage = QuizStage.IN_PROGRESS,
                    questions = questions,
                    currentQuestionIndex = 0,
                    selectedOptionIndex = null,
                    isAnswerSubmitted = false,
                    isTimedOut = false,
                    remainingSecondsForQuestion = initialTime,
                    score = 0,
                    speedBonusTotal = 0,
                    correctCount = 0,
                    timedOutCount = 0,
                    currentStreak = 0,
                    maxStreak = 0,
                    answerResults = emptyList(),
                    timeElapsedSeconds = 0,
                    savedAttemptId = null,
                    newlyUnlockedBadges = emptyList(),
                    showBadgeCelebrationDialog = false
                )
            }

            startTotalElapsedTimer()
            startQuestionCountdown()
        }
    }

    private fun startTotalElapsedTimer() {
        totalElapsedTimerJob?.cancel()
        totalElapsedTimerJob = viewModelScope.launch {
            while (_uiState.value.stage == QuizStage.IN_PROGRESS) {
                delay(1000)
                _uiState.update { it.copy(timeElapsedSeconds = it.timeElapsedSeconds + 1) }
            }
        }
    }

    private fun startQuestionCountdown() {
        countdownTimerJob?.cancel()
        val timeLimit = _uiState.value.timePerQuestionSeconds
        if (timeLimit <= 0) return // Unlimited mode

        _uiState.update {
            it.copy(
                remainingSecondsForQuestion = timeLimit,
                isTimedOut = false
            )
        }

        countdownTimerJob = viewModelScope.launch {
            while (_uiState.value.stage == QuizStage.IN_PROGRESS && !_uiState.value.isAnswerSubmitted) {
                delay(1000)
                val currentRem = _uiState.value.remainingSecondsForQuestion
                if (currentRem > 1) {
                    _uiState.update { it.copy(remainingSecondsForQuestion = currentRem - 1) }
                } else {
                    // Time is up!
                    _uiState.update { it.copy(remainingSecondsForQuestion = 0) }
                    handleTimeout()
                    break
                }
            }
        }
    }

    private fun handleTimeout() {
        val currentQuestion = _uiState.value.currentQuestion ?: return
        val newResult = QuestionAnswerResult(
            question = currentQuestion,
            selectedOptionIndex = -1,
            isCorrect = false,
            isTimedOut = true
        )

        _uiState.update {
            it.copy(
                isAnswerSubmitted = true,
                isTimedOut = true,
                selectedOptionIndex = -1,
                currentStreak = 0,
                timedOutCount = it.timedOutCount + 1,
                answerResults = it.answerResults + newResult
            )
        }
    }

    fun selectOption(index: Int) {
        if (_uiState.value.isAnswerSubmitted) return
        countdownTimerJob?.cancel()

        val currentQuestion = _uiState.value.currentQuestion ?: return
        val isCorrect = index == currentQuestion.correctOptionIndex

        val newStreak = if (isCorrect) _uiState.value.currentStreak + 1 else 0
        val maxStreak = maxOf(_uiState.value.maxStreak, newStreak)
        val streakBonus = if (isCorrect) (newStreak - 1).coerceAtLeast(0) * 20 else 0

        // Speed bonus if answered fast under countdown
        val speedBonus = if (isCorrect && _uiState.value.timePerQuestionSeconds > 0) {
            _uiState.value.remainingSecondsForQuestion * 2
        } else 0

        val pointsGained = if (isCorrect) 100 + streakBonus + speedBonus else 0

        val newResult = QuestionAnswerResult(
            question = currentQuestion,
            selectedOptionIndex = index,
            isCorrect = isCorrect,
            isTimedOut = false
        )

        _uiState.update {
            it.copy(
                selectedOptionIndex = index,
                isAnswerSubmitted = true,
                isTimedOut = false,
                score = it.score + pointsGained,
                speedBonusTotal = it.speedBonusTotal + speedBonus,
                correctCount = if (isCorrect) it.correctCount + 1 else it.correctCount,
                currentStreak = newStreak,
                maxStreak = maxStreak,
                answerResults = it.answerResults + newResult
            )
        }
    }

    fun nextQuestion() {
        val state = _uiState.value
        if (state.isLastQuestion) {
            completeQuiz()
        } else {
            _uiState.update {
                it.copy(
                    currentQuestionIndex = it.currentQuestionIndex + 1,
                    selectedOptionIndex = null,
                    isAnswerSubmitted = false,
                    isTimedOut = false,
                    remainingSecondsForQuestion = it.timePerQuestionSeconds
                )
            }
            startQuestionCountdown()
        }
    }

    private fun completeQuiz() {
        totalElapsedTimerJob?.cancel()
        countdownTimerJob?.cancel()
        _uiState.update { it.copy(stage = QuizStage.COMPLETED, isSavingAttempt = true) }

        viewModelScope.launch {
            val state = _uiState.value
            val attempt = QuizAttempt(
                timestamp = System.currentTimeMillis(),
                categoryId = state.selectedCategory?.id ?: "all",
                categoryDisplayName = state.selectedCategory?.displayName ?: "All Mobile Domains",
                totalQuestions = state.questions.size,
                correctAnswers = state.correctCount,
                score = state.score,
                timeTakenSeconds = state.timeElapsedSeconds,
                difficulty = state.selectedDifficulty?.label ?: "Mixed"
            )
            val id = submitQuizAttemptUseCase(attempt)
            // Update daily streak and check for 3-day milestone badges!
            val newlyUnlockedBadges = recordQuizStreakUseCase(state.score)

            _uiState.update {
                it.copy(
                    isSavingAttempt = false,
                    savedAttemptId = id,
                    newlyUnlockedBadges = newlyUnlockedBadges,
                    showBadgeCelebrationDialog = newlyUnlockedBadges.isNotEmpty()
                )
            }
        }
    }

    fun dismissBadgeCelebration() {
        _uiState.update { it.copy(showBadgeCelebrationDialog = false) }
    }

    fun simulateDayAdvance() {
        viewModelScope.launch {
            val unlocked = recordQuizStreakUseCase.simulateDayAdvance()
            if (unlocked.isNotEmpty()) {
                _uiState.update {
                    it.copy(
                        newlyUnlockedBadges = unlocked,
                        showBadgeCelebrationDialog = true
                    )
                }
            }
        }
    }

    fun toggleBookmark(questionId: Long) {
        viewModelScope.launch {
            toggleQuestionActionsUseCase.toggleBookmark(questionId)
        }
    }

    fun returnToSetup() {
        totalElapsedTimerJob?.cancel()
        countdownTimerJob?.cancel()
        _uiState.update {
            it.copy(
                stage = QuizStage.SETUP,
                questions = emptyList(),
                currentQuestionIndex = 0,
                selectedOptionIndex = null,
                isAnswerSubmitted = false,
                isTimedOut = false,
                savedAttemptId = null,
                newlyUnlockedBadges = emptyList(),
                showBadgeCelebrationDialog = false
            )
        }
    }

    /**
     * Calculates the number of uninterrupted consecutive days from the stored
     * daily quiz completion dates for the kids-themed UI reward system.
     *
     * @param completionDates List of dates (e.g. from StreakCounter Room table).
     * @return Number of uninterrupted consecutive days up to today or yesterday.
     */
    fun calculateConsecutiveDays(completionDates: List<String>): Int {
        return StreakCalculator.calculateConsecutiveDays(completionDates)
    }

    /**
     * Calculates the kid-friendly reward milestone (e.g., Spark Bot, Rocket Cadet)
     * awarded every 3 consecutive days.
     */
    fun getKidsStreakReward(consecutiveDays: Int): KidsStreakReward {
        return StreakCalculator.getKidsStreakReward(consecutiveDays)
    }

    override fun onCleared() {
        super.onCleared()
        totalElapsedTimerJob?.cancel()
        countdownTimerJob?.cancel()
    }
}
