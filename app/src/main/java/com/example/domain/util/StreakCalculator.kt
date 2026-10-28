package com.example.domain.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Kid-friendly streak reward milestone information.
 *
 * Designed for the playful kids-themed UI where completing quizzes consecutive days
 * earns cheerful robot rewards and milestone badges every 3 days.
 */
data class KidsStreakReward(
    val consecutiveDays: Int,
    val rewardTier: Int, // 1 for 3-day, 2 for 6-day, etc.
    val badgeName: String,
    val badgeEmoji: String,
    val cheerTitle: String,
    val cheerMessage: String,
    val nextMilestoneDays: Int,
    val daysUntilNextMilestone: Int,
    val isMilestoneAchieved: Boolean
)

object StreakCalculator {

    private const val DATE_PATTERN = "yyyy-MM-dd"

    /**
     * Calculates the count of uninterrupted consecutive days of completed quizzes
     * from a list of date strings (formatted as "yyyy-MM-dd").
     *
     * Rules:
     * 1. Filters invalid/empty entries and extracts unique dates.
     * 2. Sorts dates in descending order (most recent first).
     * 3. An active streak requires the latest completion to be today OR yesterday.
     *    If the latest date is older than yesterday, the streak is broken and returns 0.
     * 4. Counts backwards from the latest date day by day. Any gap terminates the streak.
     *
     * @param completionDates List of date strings where a quiz was completed.
     * @param referenceDate Optional reference date string (default is today) for testing.
     * @return Number of uninterrupted consecutive days.
     */
    fun calculateConsecutiveDays(
        completionDates: List<String>,
        referenceDate: String? = null
    ): Int {
        if (completionDates.isEmpty()) return 0

        val dateFormat = SimpleDateFormat(DATE_PATTERN, Locale.US).apply {
            isLenient = false
        }

        // Parse and deduplicate valid dates
        val parsedDates = completionDates.mapNotNull { str ->
            try {
                if (str.isNotBlank()) dateFormat.parse(str.trim()) else null
            } catch (e: Exception) {
                null
            }
        }.distinct().sortedDescending()

        if (parsedDates.isEmpty()) return 0

        val todayDate = if (referenceDate != null) {
            try {
                dateFormat.parse(referenceDate) ?: Date()
            } catch (e: Exception) {
                Date()
            }
        } else {
            Date()
        }

        val todayCalendar = Calendar.getInstance().apply {
            time = todayDate
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val yesterdayCalendar = (todayCalendar.clone() as Calendar).apply {
            add(Calendar.DAY_OF_YEAR, -1)
        }

        val mostRecentCal = Calendar.getInstance().apply {
            time = parsedDates.first()
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // If the latest completion is before yesterday, the streak was broken!
        if (mostRecentCal.before(yesterdayCalendar)) {
            return 0
        }

        // Count consecutive backward days
        var count = 0
        val expectedCal = mostRecentCal.clone() as Calendar

        for (date in parsedDates) {
            val dateCal = Calendar.getInstance().apply {
                time = date
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            if (isSameDay(dateCal, expectedCal)) {
                count++
                expectedCal.add(Calendar.DAY_OF_YEAR, -1)
            } else {
                // Gap encountered in the completion sequence
                break
            }
        }

        return count
    }

    private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    /**
     * Translates consecutive days into kids-themed UI rewards (every 3 consecutive days).
     */
    fun getKidsStreakReward(consecutiveDays: Int): KidsStreakReward {
        val tier = consecutiveDays / 3
        val nextMilestone = if (consecutiveDays % 3 == 0 && consecutiveDays > 0) {
            consecutiveDays + 3
        } else {
            ((consecutiveDays / 3) + 1) * 3
        }
        val daysRemaining = (nextMilestone - consecutiveDays).coerceAtLeast(1)

        return when {
            consecutiveDays >= 12 -> KidsStreakReward(
                consecutiveDays = consecutiveDays,
                rewardTier = 4,
                badgeName = "Cosmic Master",
                badgeEmoji = "👑",
                cheerTitle = "Cosmic Champion! 👑",
                cheerMessage = "Incredible! $consecutiveDays days in a row! Robby crowned you Master of the Galaxy! 🚀🌟",
                nextMilestoneDays = nextMilestone,
                daysUntilNextMilestone = daysRemaining,
                isMilestoneAchieved = true
            )
            consecutiveDays >= 9 -> KidsStreakReward(
                consecutiveDays = consecutiveDays,
                rewardTier = 3,
                badgeName = "Brain Dynamo",
                badgeEmoji = "🧠",
                cheerTitle = "Brain Dynamo Zap! ⚡",
                cheerMessage = "Super brain power! $consecutiveDays days in a row! Your circuits are supercharged! 💡",
                nextMilestoneDays = nextMilestone,
                daysUntilNextMilestone = daysRemaining,
                isMilestoneAchieved = true
            )
            consecutiveDays >= 6 -> KidsStreakReward(
                consecutiveDays = consecutiveDays,
                rewardTier = 2,
                badgeName = "Rocket Cadet",
                badgeEmoji = "🚀",
                cheerTitle = "Blast Off, Rocket Cadet! 🚀",
                cheerMessage = "High in orbit! $consecutiveDays days in a row! You're flying past the stars! ⭐",
                nextMilestoneDays = nextMilestone,
                daysUntilNextMilestone = daysRemaining,
                isMilestoneAchieved = true
            )
            consecutiveDays >= 3 -> KidsStreakReward(
                consecutiveDays = consecutiveDays,
                rewardTier = 1,
                badgeName = "Spark Bot",
                badgeEmoji = "⚡",
                cheerTitle = "Power Zap Unlocked! ⚡",
                cheerMessage = "Awesome job! You reached 3 consecutive days! Robby the Robot is dancing! 🎉",
                nextMilestoneDays = nextMilestone,
                daysUntilNextMilestone = daysRemaining,
                isMilestoneAchieved = true
            )
            consecutiveDays > 0 -> KidsStreakReward(
                consecutiveDays = consecutiveDays,
                rewardTier = 0,
                badgeName = "Junior Scout",
                badgeEmoji = "🌱",
                cheerTitle = "Streak Started! 🌱",
                cheerMessage = "Great start! $daysRemaining more day(s) until your shiny Spark Bot ⚡ badge!",
                nextMilestoneDays = 3,
                daysUntilNextMilestone = daysRemaining,
                isMilestoneAchieved = false
            )
            else -> KidsStreakReward(
                consecutiveDays = 0,
                rewardTier = 0,
                badgeName = "Explorer",
                badgeEmoji = "🤖",
                cheerTitle = "Start Your Streak Today! 🤖",
                cheerMessage = "Complete today's quiz to start your daily streak and earn badges every 3 days!",
                nextMilestoneDays = 3,
                daysUntilNextMilestone = 3,
                isMilestoneAchieved = false
            )
        }
    }
}
