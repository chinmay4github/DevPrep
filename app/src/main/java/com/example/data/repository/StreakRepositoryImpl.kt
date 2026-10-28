package com.example.data.repository

import com.example.data.local.dao.DailyActivityDao
import com.example.data.local.dao.StreakBadgeDao
import com.example.data.local.dao.StreakCounterDao
import com.example.data.local.dao.UserStreakDao
import com.example.data.local.entity.DailyActivityEntity
import com.example.data.local.entity.StreakBadgeEntity
import com.example.data.local.entity.StreakCounter
import com.example.data.local.entity.UserStreakEntity
import com.example.domain.model.StreakBadge
import com.example.domain.model.UserDailyStreak
import com.example.domain.repository.StreakRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class StreakRepositoryImpl(
    private val userStreakDao: UserStreakDao,
    private val dailyActivityDao: DailyActivityDao,
    private val streakBadgeDao: StreakBadgeDao,
    private val streakCounterDao: StreakCounterDao
) : StreakRepository {

    private val dateFormat: SimpleDateFormat
        get() = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    private fun getTodayDateString(timestamp: Long = System.currentTimeMillis()): String {
        return dateFormat.format(Date(timestamp))
    }

    private fun getYesterdayDateString(todayString: String): String {
        return try {
            val date = dateFormat.parse(todayString) ?: Date()
            val calendar = Calendar.getInstance().apply {
                time = date
                add(Calendar.DAY_OF_YEAR, -1)
            }
            dateFormat.format(calendar.time)
        } catch (e: Exception) {
            ""
        }
    }

    private fun getNextDateString(fromDateString: String): String {
        return try {
            val date = dateFormat.parse(fromDateString) ?: Date()
            val calendar = Calendar.getInstance().apply {
                time = date
                add(Calendar.DAY_OF_YEAR, 1)
            }
            dateFormat.format(calendar.time)
        } catch (e: Exception) {
            getTodayDateString()
        }
    }

    override fun getUserDailyStreak(): Flow<UserDailyStreak> {
        return combine(
            userStreakDao.getUserStreak(),
            streakBadgeDao.getAllBadges()
        ) { streakEntity, badgeEntities ->
            val today = getTodayDateString()
            val yesterday = getYesterdayDateString(today)

            val current = streakEntity ?: UserStreakEntity()
            val hasCompletedToday = current.lastActiveDate.isNotEmpty() && current.lastActiveDate >= today

            // If last active was before yesterday, active streak is broken
            val effectiveStreak = when {
                current.lastActiveDate.isEmpty() -> 0
                current.lastActiveDate >= yesterday -> current.currentStreak
                else -> 0
            }

            val domainBadges = if (badgeEntities.isEmpty()) {
                StreakBadgeEntity.getDefaultBadges().map { it.toDomain() }
            } else {
                badgeEntities.map { it.toDomain() }
            }

            UserDailyStreak(
                currentStreak = effectiveStreak,
                longestStreak = current.longestStreak,
                lastActiveDate = current.lastActiveDate,
                totalActiveDays = current.totalActiveDays,
                hasCompletedQuizToday = hasCompletedToday,
                badges = domainBadges
            )
        }
    }

    override suspend fun recordQuizCompletion(score: Int): List<StreakBadge> {
        val now = System.currentTimeMillis()
        val today = getTodayDateString(now)
        val yesterday = getYesterdayDateString(today)

        // 1. Record daily activity
        val existingDaily = dailyActivityDao.getActivityForDateSync(today)
        val updatedDaily = if (existingDaily != null) {
            existingDaily.copy(
                quizCount = existingDaily.quizCount + 1,
                highestScore = maxOf(existingDaily.highestScore, score),
                timestamp = now
            )
        } else {
            DailyActivityEntity(
                dateString = today,
                timestamp = now,
                quizCount = 1,
                highestScore = score
            )
        }
        dailyActivityDao.insertOrUpdateActivity(updatedDaily)

        // 1b. Store daily quiz completion date in StreakCounter Room Entity
        val existingCounter = streakCounterDao.getStreakCounterByDate(today)
        val updatedCounter = if (existingCounter != null) {
            existingCounter.copy(
                quizzesCompleted = existingCounter.quizzesCompleted + 1,
                completionTimestamp = now,
                isCompleted = true
            )
        } else {
            StreakCounter(
                date = today,
                completionTimestamp = now,
                quizzesCompleted = 1,
                isCompleted = true
            )
        }
        streakCounterDao.insertOrUpdate(updatedCounter)

        // 2. Compute and update streak
        val currentStreakEntity = userStreakDao.getUserStreakSync() ?: UserStreakEntity()
        val (newCurrentStreak, newLongestStreak, newTotalDays) = when {
            currentStreakEntity.lastActiveDate == today -> {
                // Already did a quiz today; maintain streak
                Triple(
                    currentStreakEntity.currentStreak.coerceAtLeast(1),
                    maxOf(currentStreakEntity.longestStreak, currentStreakEntity.currentStreak.coerceAtLeast(1)),
                    currentStreakEntity.totalActiveDays.coerceAtLeast(1)
                )
            }
            currentStreakEntity.lastActiveDate == yesterday -> {
                // Consecutive day! Increment streak
                val nextStreak = currentStreakEntity.currentStreak + 1
                Triple(
                    nextStreak,
                    maxOf(currentStreakEntity.longestStreak, nextStreak),
                    currentStreakEntity.totalActiveDays + 1
                )
            }
            else -> {
                // First time or missed days: reset to 1
                Triple(
                    1,
                    maxOf(currentStreakEntity.longestStreak, 1),
                    currentStreakEntity.totalActiveDays + 1
                )
            }
        }

        userStreakDao.insertOrUpdate(
            UserStreakEntity(
                id = 1,
                currentStreak = newCurrentStreak,
                longestStreak = newLongestStreak,
                lastActiveDate = today,
                lastQuizTimestamp = now,
                totalActiveDays = newTotalDays
            )
        )

        // 3. Award badges for every 3 consecutive days!
        // Ensure default badges exist
        if (streakBadgeDao.getBadgeCountSync() == 0) {
            streakBadgeDao.insertAll(StreakBadgeEntity.getDefaultBadges())
        }

        val allBadges = streakBadgeDao.getAllBadgesSync()
        val newlyUnlockedBadges = mutableListOf<StreakBadge>()

        for (badge in allBadges) {
            if (badge.daysRequired <= newCurrentStreak && !badge.isUnlocked) {
                val unlocked = badge.copy(
                    isUnlocked = true,
                    unlockedAtTimestamp = now
                )
                streakBadgeDao.updateBadge(unlocked)
                newlyUnlockedBadges.add(unlocked.toDomain())
            }
        }

        return newlyUnlockedBadges
    }

    override suspend fun simulateDayAdvance(): List<StreakBadge> {
        val now = System.currentTimeMillis()
        val currentStreakEntity = userStreakDao.getUserStreakSync() ?: UserStreakEntity()

        val baseDate = if (currentStreakEntity.lastActiveDate.isNotEmpty()) {
            currentStreakEntity.lastActiveDate
        } else {
            getYesterdayDateString(getTodayDateString(now))
        }

        val nextDate = getNextDateString(baseDate)
        val nextStreak = currentStreakEntity.currentStreak + 1
        val newLongest = maxOf(currentStreakEntity.longestStreak, nextStreak)
        val newTotalDays = currentStreakEntity.totalActiveDays + 1

        dailyActivityDao.insertOrUpdateActivity(
            DailyActivityEntity(
                dateString = nextDate,
                timestamp = now,
                quizCount = 1,
                highestScore = 100
            )
        )

        // Save into StreakCounter Room Entity
        streakCounterDao.insertOrUpdate(
            StreakCounter(
                date = nextDate,
                completionTimestamp = now,
                quizzesCompleted = 1,
                isCompleted = true
            )
        )

        userStreakDao.insertOrUpdate(
            UserStreakEntity(
                id = 1,
                currentStreak = nextStreak,
                longestStreak = newLongest,
                lastActiveDate = nextDate,
                lastQuizTimestamp = now,
                totalActiveDays = newTotalDays
            )
        )

        if (streakBadgeDao.getBadgeCountSync() == 0) {
            streakBadgeDao.insertAll(StreakBadgeEntity.getDefaultBadges())
        }

        val allBadges = streakBadgeDao.getAllBadgesSync()
        val newlyUnlockedBadges = mutableListOf<StreakBadge>()

        for (badge in allBadges) {
            if (badge.daysRequired <= nextStreak && !badge.isUnlocked) {
                val unlocked = badge.copy(
                    isUnlocked = true,
                    unlockedAtTimestamp = now
                )
                streakBadgeDao.updateBadge(unlocked)
                newlyUnlockedBadges.add(unlocked.toDomain())
            }
        }

        return newlyUnlockedBadges
    }

    override suspend fun resetStreak() {
        userStreakDao.clearAll()
        dailyActivityDao.clearAll()
        streakBadgeDao.resetAllBadges()
        streakCounterDao.clearAll()
        userStreakDao.insertOrUpdate(UserStreakEntity())
    }

    override fun getStreakCounters(): Flow<List<StreakCounter>> =
        streakCounterDao.getAllStreakCounters()

    override fun getAllCompletionDates(): Flow<List<String>> =
        streakCounterDao.getAllCompletionDates()

    override suspend fun getAllCompletionDatesSync(): List<String> =
        streakCounterDao.getAllCompletionDatesSync()
}
