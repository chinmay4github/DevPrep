package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.local.dao.CategoryDao
import com.example.data.local.dao.DailyActivityDao
import com.example.data.local.dao.FlashcardDao
import com.example.data.local.dao.QuestionDao
import com.example.data.local.dao.QuizAttemptDao
import com.example.data.local.dao.StreakBadgeDao
import com.example.data.local.dao.StreakCounterDao
import com.example.data.local.dao.UserStreakDao
import com.example.data.local.entity.CategoryEntity
import com.example.data.local.entity.DailyActivityEntity
import com.example.data.local.entity.FlashcardEntity
import com.example.data.local.entity.QuestionEntity
import com.example.data.local.entity.QuizAttemptEntity
import com.example.data.local.entity.StreakBadgeEntity
import com.example.data.local.entity.StreakCounter
import com.example.data.local.entity.UserStreakEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        CategoryEntity::class,
        QuestionEntity::class,
        FlashcardEntity::class,
        QuizAttemptEntity::class,
        DailyActivityEntity::class,
        UserStreakEntity::class,
        StreakBadgeEntity::class,
        StreakCounter::class
    ],
    version = 5,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun questionDao(): QuestionDao
    abstract fun flashcardDao(): FlashcardDao
    abstract fun quizAttemptDao(): QuizAttemptDao
    abstract fun dailyActivityDao(): DailyActivityDao
    abstract fun userStreakDao(): UserStreakDao
    abstract fun streakBadgeDao(): StreakBadgeDao
    abstract fun streakCounterDao(): StreakCounterDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `categories` (
                        `id` TEXT NOT NULL,
                        `name` TEXT NOT NULL,
                        `shortName` TEXT NOT NULL,
                        `description` TEXT NOT NULL,
                        `iconName` TEXT NOT NULL,
                        `displayOrder` INTEGER NOT NULL DEFAULT 0,
                        PRIMARY KEY(`id`)
                    )
                    """.trimIndent()
                )
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `daily_activities` (
                        `dateString` TEXT NOT NULL,
                        `timestamp` INTEGER NOT NULL,
                        `quizCount` INTEGER NOT NULL DEFAULT 1,
                        `highestScore` INTEGER NOT NULL DEFAULT 0,
                        PRIMARY KEY(`dateString`)
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `user_streak` (
                        `id` INTEGER NOT NULL,
                        `currentStreak` INTEGER NOT NULL DEFAULT 0,
                        `longestStreak` INTEGER NOT NULL DEFAULT 0,
                        `lastActiveDate` TEXT NOT NULL DEFAULT '',
                        `lastQuizTimestamp` INTEGER NOT NULL DEFAULT 0,
                        `totalActiveDays` INTEGER NOT NULL DEFAULT 0,
                        PRIMARY KEY(`id`)
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `streak_badges` (
                        `id` TEXT NOT NULL,
                        `daysRequired` INTEGER NOT NULL,
                        `name` TEXT NOT NULL,
                        `kidDescription` TEXT NOT NULL,
                        `emoji` TEXT NOT NULL,
                        `colorHex` TEXT NOT NULL,
                        `isUnlocked` INTEGER NOT NULL DEFAULT 0,
                        `unlockedAtTimestamp` INTEGER,
                        PRIMARY KEY(`id`)
                    )
                    """.trimIndent()
                )
            }
        }

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `streak_counters` (
                        `date` TEXT NOT NULL,
                        `completionTimestamp` INTEGER NOT NULL,
                        `quizzesCompleted` INTEGER NOT NULL DEFAULT 1,
                        `isCompleted` INTEGER NOT NULL DEFAULT 1,
                        PRIMARY KEY(`date`)
                    )
                    """.trimIndent()
                )
            }
        }

        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE `questions` ADD COLUMN `mastery` INTEGER NOT NULL DEFAULT 0")
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `flashcards` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `questionId` INTEGER NOT NULL DEFAULT 0,
                        `category` TEXT NOT NULL DEFAULT '',
                        `title` TEXT NOT NULL DEFAULT '',
                        `questionText` TEXT NOT NULL DEFAULT '',
                        `answerSummary` TEXT NOT NULL DEFAULT '',
                        `deepDiveExplanation` TEXT NOT NULL DEFAULT '',
                        `codeSnippet` TEXT,
                        `scenarioContext` TEXT,
                        `mastery` INTEGER NOT NULL DEFAULT 0,
                        `isMastered` INTEGER NOT NULL DEFAULT 0,
                        `isBookmarked` INTEGER NOT NULL DEFAULT 0,
                        `difficulty` TEXT NOT NULL DEFAULT 'MEDIUM',
                        `updatedAt` INTEGER NOT NULL DEFAULT 0
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_flashcards_category` ON `flashcards` (`category`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_flashcards_mastery` ON `flashcards` (`mastery`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_flashcards_isMastered` ON `flashcards` (`isMastered`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_questions_mastery` ON `questions` (`mastery`)")
            }
        }

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "devprep_database.db"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5)
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database)
                    }
                }
            }

            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database)
                    }
                }
            }

            private suspend fun populateInitialData(database: AppDatabase) {
                if (database.categoryDao().getCategoryCountSync() == 0) {
                    database.categoryDao().insertAll(InitialQuestionsData.getInitialCategories())
                }
                if (database.questionDao().getQuestionsCountSync() == 0) {
                    database.questionDao().insertAll(InitialQuestionsData.getInitialQuestions())
                }
                if (database.flashcardDao().getFlashcardCountSync() == 0) {
                    val initialCards = InitialQuestionsData.getInitialQuestions()
                        .filter { it.questionType == com.example.domain.model.QuestionType.FLASHCARD.name }
                        .map { q ->
                            FlashcardEntity(
                                id = q.id,
                                questionId = q.id,
                                category = q.category,
                                title = q.title,
                                questionText = q.questionText,
                                answerSummary = q.answerSummary,
                                deepDiveExplanation = q.deepDiveExplanation,
                                codeSnippet = q.codeSnippet,
                                scenarioContext = q.scenarioContext,
                                mastery = q.mastery,
                                isMastered = q.isMastered,
                                isBookmarked = q.isBookmarked,
                                difficulty = q.difficulty,
                                updatedAt = q.createdAt
                            )
                        }
                    database.flashcardDao().insertAll(initialCards)
                }
                if (database.streakBadgeDao().getBadgeCountSync() == 0) {
                    database.streakBadgeDao().insertAll(StreakBadgeEntity.getDefaultBadges())
                }
                if (database.userStreakDao().getUserStreakSync() == null) {
                    database.userStreakDao().insertOrUpdate(UserStreakEntity())
                }
            }
        }
    }
}
