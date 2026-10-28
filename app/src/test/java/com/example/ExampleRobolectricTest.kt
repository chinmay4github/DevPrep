package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AppDatabase
import com.example.data.local.entity.StreakBadgeEntity
import com.example.data.repository.StreakRepositoryImpl
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  private lateinit var db: AppDatabase
  private lateinit var streakRepository: StreakRepositoryImpl

  @Before
  fun createDb() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
      .allowMainThreadQueries()
      .build()
    runBlocking {
      db.streakBadgeDao().insertAll(StreakBadgeEntity.getDefaultBadges())
    }
    streakRepository = StreakRepositoryImpl(
      userStreakDao = db.userStreakDao(),
      dailyActivityDao = db.dailyActivityDao(),
      streakBadgeDao = db.streakBadgeDao(),
      streakCounterDao = db.streakCounterDao()
    )
  }

  @After
  fun closeDb() {
    db.close()
  }

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("DevPrep", appName)
  }

  @Test
  fun `completing quiz updates streak and rewards badge every 3 consecutive days`() = runBlocking {
    // Initial state
    val initialStreak = streakRepository.getUserDailyStreak().first()
    assertEquals(0, initialStreak.currentStreak)

    // Day 1: complete quiz
    val badgesDay1 = streakRepository.recordQuizCompletion(100)
    assertEquals(0, badgesDay1.size) // No badge yet for 1 day
    val streakAfterDay1 = streakRepository.getUserDailyStreak().first()
    assertEquals(1, streakAfterDay1.currentStreak)
    assertTrue(streakAfterDay1.hasCompletedQuizToday)

    // Completing multiple quizzes on the same day does not falsely increment streak
    streakRepository.recordQuizCompletion(150)
    val streakSameDay = streakRepository.getUserDailyStreak().first()
    assertEquals(1, streakSameDay.currentStreak)

    // Advance to Day 2 and complete quiz
    val badgesDay2 = streakRepository.simulateDayAdvance()
    assertEquals(0, badgesDay2.size)
    val streakAfterDay2 = streakRepository.getUserDailyStreak().first()
    assertEquals(2, streakAfterDay2.currentStreak)

    // Advance to Day 3 and complete quiz -> 3 CONSECUTIVE DAYS!
    val badgesDay3 = streakRepository.simulateDayAdvance()
    assertEquals(1, badgesDay3.size) // 3-Day badge unlocked!
    assertEquals("Spark Bot ⚡", badgesDay3.first().name)
    assertEquals(3, badgesDay3.first().daysRequired)

    val streakAfterDay3 = streakRepository.getUserDailyStreak().first()
    assertEquals(3, streakAfterDay3.currentStreak)
    assertEquals(1, streakAfterDay3.unlockedBadges.size)
    assertEquals("Spark Bot ⚡", streakAfterDay3.unlockedBadges.first().name)

    // Continue to Day 4, 5, 6
    streakRepository.simulateDayAdvance() // Day 4
    streakRepository.simulateDayAdvance() // Day 5
    val badgesDay6 = streakRepository.simulateDayAdvance() // Day 6 -> 6 CONSECUTIVE DAYS!
    assertEquals(1, badgesDay6.size) // 6-Day badge unlocked!
    assertEquals("Rocket Cadet 🚀", badgesDay6.first().name)

    val streakAfterDay6 = streakRepository.getUserDailyStreak().first()
    assertEquals(6, streakAfterDay6.currentStreak)
    assertEquals(2, streakAfterDay6.unlockedBadges.size)

    // Verify StreakCounter Room Entity recorded daily completion dates
    val streakCounterDates = streakRepository.getAllCompletionDates().first()
    assertEquals(6, streakCounterDates.size)
    val streakCounters = db.streakCounterDao().getAllCompletionDatesSync()
    assertEquals(6, streakCounters.size)
  }
}
