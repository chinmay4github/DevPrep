package com.example.domain.model

data class StreakBadge(
    val id: String,
    val daysRequired: Int,
    val name: String,
    val kidDescription: String,
    val emoji: String,
    val colorHex: String,
    val isUnlocked: Boolean = false,
    val unlockedAtTimestamp: Long? = null
)

data class UserDailyStreak(
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val lastActiveDate: String = "",
    val totalActiveDays: Int = 0,
    val hasCompletedQuizToday: Boolean = false,
    val badges: List<StreakBadge> = emptyList()
) {
    val unlockedBadges: List<StreakBadge>
        get() = badges.filter { it.isUnlocked }

    val nextBadge: StreakBadge?
        get() = badges.filter { !it.isUnlocked }.minByOrNull { it.daysRequired }

    val daysUntilNextBadge: Int
        get() {
            val target = nextBadge?.daysRequired ?: return 0
            return (target - currentStreak).coerceAtLeast(0)
        }

    val progressToNextBadge: Float
        get() {
            val next = nextBadge ?: return 1.0f
            val previousRequirement = badges
                .filter { it.daysRequired < next.daysRequired }
                .maxOfOrNull { it.daysRequired } ?: 0
            val interval = (next.daysRequired - previousRequirement).coerceAtLeast(1)
            val currentInInterval = (currentStreak - previousRequirement).coerceAtLeast(0)
            return (currentInInterval.toFloat() / interval.toFloat()).coerceIn(0f, 1f)
        }
}
