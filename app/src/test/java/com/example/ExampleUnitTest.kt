package com.example

import com.example.data.local.InitialQuestionsData
import com.example.data.local.entity.CategoryEntity
import com.example.data.local.entity.QuestionEntity
import com.example.data.local.entity.StreakBadgeEntity
import com.example.domain.model.Category
import com.example.domain.model.Difficulty
import com.example.domain.model.QuestionType
import com.example.domain.model.QuizAttempt
import com.example.domain.model.StreakBadge
import com.example.domain.model.UserDailyStreak
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun testQuizAttempt_gradeAndPercentage() {
    val attemptHigh = QuizAttempt(
      id = 1,
      categoryId = "all",
      categoryDisplayName = "All Domains",
      totalQuestions = 10,
      correctAnswers = 9,
      score = 980,
      timeTakenSeconds = 45,
      difficulty = "Mixed"
    )
    assertEquals(90, attemptHigh.percentage)
    assertEquals("Principal / Staff Mobile Architect", attemptHigh.gradeTitle)

    val attemptSenior = QuizAttempt(
      id = 2,
      categoryId = "android_arch",
      categoryDisplayName = "Android Arch",
      totalQuestions = 10,
      correctAnswers = 8,
      score = 800,
      timeTakenSeconds = 60,
      difficulty = "Hard"
    )
    assertEquals(80, attemptSenior.percentage)
    assertEquals("Senior Mobile Engineer", attemptSenior.gradeTitle)
  }

  @Test
  fun testQuestionEntity_mapping() {
    val entity = QuestionEntity(
      id = 42,
      category = Category.ROOM_PRODUCTION.id,
      title = "Test Migration",
      questionText = "What causes schema mismatch?",
      codeSnippet = "val MIGRATION_1_2 = ...",
      scenarioContext = "Production crash",
      optionsJson = "A|||B|||C|||D",
      correctOptionIndex = 0,
      answerSummary = "Identity hash mismatch.",
      deepDiveExplanation = "Room validates schema JSON.",
      followUpInterviewerQuestion = "How do you test it?",
      difficulty = Difficulty.HARD.name,
      isBookmarked = true,
      isMastered = false,
      questionType = QuestionType.MCQ.name
    )

    val domain = entity.toDomain()
    assertEquals(42L, domain.id)
    assertEquals(Category.ROOM_PRODUCTION, domain.category)
    assertEquals(Difficulty.HARD, domain.difficulty)
    assertEquals(4, domain.options.size)
    assertEquals("A", domain.options[0])
    assertEquals(true, domain.isBookmarked)
    assertEquals(false, domain.isMastered)
  }

  @Test
  fun testInitialCategories_containsAndroidAndIos() {
    val initialCategories = InitialQuestionsData.getInitialCategories()
    assertTrue(initialCategories.any { it.shortName == "Android" && it.id == "android_arch" })
    assertTrue(initialCategories.any { it.shortName == "iOS" && it.id == "ios_swift" })
    assertEquals(4, initialCategories.size)
  }

  @Test
  fun testUserDailyStreak_progressAndNextBadgeCalculation() {
    val defaultBadges = StreakBadgeEntity.getDefaultBadges().map { it.toDomain() }
    
    // User with 1-day streak
    val streakDay1 = UserDailyStreak(
      currentStreak = 1,
      longestStreak = 1,
      lastActiveDate = "2026-10-28",
      totalActiveDays = 1,
      hasCompletedQuizToday = true,
      badges = defaultBadges
    )
    assertEquals(2, streakDay1.daysUntilNextBadge)
    assertEquals("streak_badge_3", streakDay1.nextBadge?.id)
    assertEquals(0, streakDay1.unlockedBadges.size)

    // User reaches 3-day streak
    val badgesAfterDay3 = defaultBadges.map {
      if (it.daysRequired <= 3) it.copy(isUnlocked = true) else it
    }
    val streakDay3 = UserDailyStreak(
      currentStreak = 3,
      longestStreak = 3,
      lastActiveDate = "2026-10-28",
      totalActiveDays = 3,
      hasCompletedQuizToday = true,
      badges = badgesAfterDay3
    )
    assertEquals(1, streakDay3.unlockedBadges.size)
    assertEquals("Spark Bot ⚡", streakDay3.unlockedBadges.first().name)
    assertEquals("Rocket Cadet 🚀", streakDay3.nextBadge?.name)
    assertEquals(3, streakDay3.daysUntilNextBadge) // 6 - 3 = 3 days left
  }

  @Test
  fun testStreakBadgeDefaults_milestonesEvery3Days() {
    val defaultBadges = StreakBadgeEntity.getDefaultBadges()
    assertTrue(defaultBadges.any { it.daysRequired == 3 })
    assertTrue(defaultBadges.any { it.daysRequired == 6 })
    assertTrue(defaultBadges.any { it.daysRequired == 9 })
    assertTrue(defaultBadges.any { it.daysRequired == 12 })
  }

  @Test
  fun testCalculateConsecutiveDays_continuousSequence() {
    val completionDates = listOf("2026-10-28", "2026-10-27", "2026-10-26")
    val days = com.example.domain.util.StreakCalculator.calculateConsecutiveDays(
      completionDates = completionDates,
      referenceDate = "2026-10-28"
    )
    assertEquals(3, days)
  }

  @Test
  fun testCalculateConsecutiveDays_activeYesterdayNotDoneToday() {
    // Kid completed yesterday and the day before, hasn't done today's quiz yet
    val completionDates = listOf("2026-10-27", "2026-10-26")
    val days = com.example.domain.util.StreakCalculator.calculateConsecutiveDays(
      completionDates = completionDates,
      referenceDate = "2026-10-28"
    )
    assertEquals(2, days)
  }

  @Test
  fun testCalculateConsecutiveDays_deduplicatesSameDayCompletions() {
    // Kid took 3 quizzes on the same day and 1 quiz yesterday
    val completionDates = listOf("2026-10-28", "2026-10-28", "2026-10-28", "2026-10-27")
    val days = com.example.domain.util.StreakCalculator.calculateConsecutiveDays(
      completionDates = completionDates,
      referenceDate = "2026-10-28"
    )
    assertEquals(2, days)
  }

  @Test
  fun testCalculateConsecutiveDays_brokenStreakMissedYesterday() {
    // Last completion was 3 days ago (gap)
    val completionDates = listOf("2026-10-25", "2026-10-24", "2026-10-23")
    val days = com.example.domain.util.StreakCalculator.calculateConsecutiveDays(
      completionDates = completionDates,
      referenceDate = "2026-10-28"
    )
    assertEquals(0, days)
  }

  @Test
  fun testCalculateConsecutiveDays_gapInHistoryStopsStreak() {
    // Gap on 2026-10-27
    val completionDates = listOf("2026-10-28", "2026-10-26", "2026-10-25")
    val days = com.example.domain.util.StreakCalculator.calculateConsecutiveDays(
      completionDates = completionDates,
      referenceDate = "2026-10-28"
    )
    assertEquals(1, days)
  }

  @Test
  fun testCalculateConsecutiveDays_unsortedInputAndEmpty() {
    assertEquals(0, com.example.domain.util.StreakCalculator.calculateConsecutiveDays(emptyList()))
    
    val unsorted = listOf("2026-10-26", "2026-10-28", "2026-10-27")
    val days = com.example.domain.util.StreakCalculator.calculateConsecutiveDays(
      completionDates = unsorted,
      referenceDate = "2026-10-28"
    )
    assertEquals(3, days)
  }

  @Test
  fun testKidsStreakReward_milestoneRewardsEvery3Days() {
    // 0 days
    val reward0 = com.example.domain.util.StreakCalculator.getKidsStreakReward(0)
    assertFalse(reward0.isMilestoneAchieved)
    assertEquals(3, reward0.daysUntilNextMilestone)

    // 1 day
    val reward1 = com.example.domain.util.StreakCalculator.getKidsStreakReward(1)
    assertFalse(reward1.isMilestoneAchieved)
    assertEquals(2, reward1.daysUntilNextMilestone)

    // 3 consecutive days -> Spark Bot unlocked!
    val reward3 = com.example.domain.util.StreakCalculator.getKidsStreakReward(3)
    assertTrue(reward3.isMilestoneAchieved)
    assertEquals("Spark Bot", reward3.badgeName)
    assertEquals("⚡", reward3.badgeEmoji)
    assertEquals(3, reward3.daysUntilNextMilestone) // Next is 6

    // 6 consecutive days -> Rocket Cadet unlocked!
    val reward6 = com.example.domain.util.StreakCalculator.getKidsStreakReward(6)
    assertTrue(reward6.isMilestoneAchieved)
    assertEquals("Rocket Cadet", reward6.badgeName)
    assertEquals("🚀", reward6.badgeEmoji)
  }
}
