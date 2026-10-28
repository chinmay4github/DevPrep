package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.QuizAttemptEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface QuizAttemptDao {
    @Query("SELECT * FROM quiz_attempts ORDER BY timestamp DESC")
    fun getAllAttempts(): Flow<List<QuizAttemptEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttempt(attempt: QuizAttemptEntity): Long

    @Query("DELETE FROM quiz_attempts")
    suspend fun clearAllAttempts()

    @Query("SELECT COUNT(*) FROM quiz_attempts")
    fun getAttemptsCount(): Flow<Int>

    @Query("SELECT MAX(score) FROM quiz_attempts")
    fun getHighestScore(): Flow<Int?>

    @Query("SELECT AVG((correctAnswers * 100) / totalQuestions) FROM quiz_attempts WHERE totalQuestions > 0")
    fun getAveragePercentage(): Flow<Double?>
}
