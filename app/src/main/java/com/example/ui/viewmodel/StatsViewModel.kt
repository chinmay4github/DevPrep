package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.QuizAttempt
import com.example.domain.model.QuizOverallStats
import com.example.domain.model.StreakBadge
import com.example.domain.model.UserDailyStreak
import com.example.domain.usecase.GetStatsUseCase
import com.example.domain.usecase.GetStreakUseCase
import com.example.domain.usecase.RecordQuizStreakUseCase
import com.example.domain.util.KidsStreakReward
import com.example.domain.util.StreakCalculator
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class StatsUiState(
    val stats: QuizOverallStats = QuizOverallStats(),
    val recentAttempts: List<QuizAttempt> = emptyList(),
    val streak: UserDailyStreak = UserDailyStreak(),
    val completionDates: List<String> = emptyList(),
    val consecutiveDays: Int = 0,
    val kidsStreakReward: KidsStreakReward = StreakCalculator.getKidsStreakReward(0),
    val selectedBadgeDetail: StreakBadge? = null,
    val isLoading: Boolean = true
)

class StatsViewModel(
    private val getStatsUseCase: GetStatsUseCase,
    private val getStreakUseCase: GetStreakUseCase,
    private val recordQuizStreakUseCase: RecordQuizStreakUseCase
) : ViewModel() {

    val uiState: StateFlow<StatsUiState> = combine(
        getStatsUseCase.getOverallStats(),
        getStatsUseCase.getRecentAttempts(),
        getStreakUseCase(),
        getStreakUseCase.getCompletionDates()
    ) { stats, attempts, streak, dates ->
        val consecutive = calculateConsecutiveDays(dates)
        val reward = getKidsStreakReward(consecutive)
        StatsUiState(
            stats = stats,
            recentAttempts = attempts,
            streak = streak,
            completionDates = dates,
            consecutiveDays = consecutive,
            kidsStreakReward = reward,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = StatsUiState()
    )

    /**
     * Calculates the number of uninterrupted consecutive days from the stored
     * daily quiz completion dates for the kids-themed UI reward system.
     *
     * @param completionDates List of dates (e.g. from StreakCounter Room table).
     * @return Number of uninterrupted consecutive days.
     */
    fun calculateConsecutiveDays(completionDates: List<String>): Int {
        return StreakCalculator.calculateConsecutiveDays(completionDates)
    }

    /**
     * Calculates the kid-friendly reward milestone information awarded every 3 days.
     */
    fun getKidsStreakReward(consecutiveDays: Int): KidsStreakReward {
        return StreakCalculator.getKidsStreakReward(consecutiveDays)
    }

    fun clearHistory() {
        viewModelScope.launch {
            getStatsUseCase.clearHistory()
        }
    }

    fun simulateDayAdvance() {
        viewModelScope.launch {
            recordQuizStreakUseCase.simulateDayAdvance()
        }
    }

    fun resetStreak() {
        viewModelScope.launch {
            recordQuizStreakUseCase.resetStreak()
        }
    }
}
