package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.QuestionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestionDao {
    @Query("SELECT * FROM questions ORDER BY id ASC")
    fun getAllQuestions(): Flow<List<QuestionEntity>>

    @Query("SELECT * FROM questions WHERE id = :id LIMIT 1")
    fun getQuestionById(id: Long): Flow<QuestionEntity?>

    /**
     * Query specifically for filtering flashcards by Category ID (e.g. 'android_arch', 'ios_swift')
     */
    @Query(
        """
        SELECT * FROM questions 
        WHERE category = :categoryId 
        AND questionType = 'FLASHCARD'
        ORDER BY id ASC
        """
    )
    fun getFlashcardsByCategory(categoryId: String): Flow<List<QuestionEntity>>

    /**
     * Query for all questions (both Flashcards & MCQs) within a given category
     */
    @Query(
        """
        SELECT * FROM questions 
        WHERE category = :categoryId 
        ORDER BY id ASC
        """
    )
    fun getQuestionsByCategory(categoryId: String): Flow<List<QuestionEntity>>

    @Query(
        """
        SELECT * FROM questions 
        WHERE (:category IS NULL OR category = :category)
        AND (:difficulty IS NULL OR difficulty = :difficulty)
        AND (:questionType IS NULL OR questionType = :questionType)
        AND (:isBookmarked IS NULL OR isBookmarked = :isBookmarked)
        AND (:isMastered IS NULL OR isMastered = :isMastered)
        AND (
            :searchQuery = '' 
            OR title LIKE '%' || :searchQuery || '%'
            OR questionText LIKE '%' || :searchQuery || '%'
            OR answerSummary LIKE '%' || :searchQuery || '%'
        )
        ORDER BY id ASC
        """
    )
    fun getFilteredQuestions(
        category: String?,
        difficulty: String?,
        questionType: String?,
        isBookmarked: Boolean?,
        isMastered: Boolean?,
        searchQuery: String
    ): Flow<List<QuestionEntity>>

    @Query(
        """
        SELECT * FROM questions 
        WHERE (:category IS NULL OR category = :category)
        AND (:difficulty IS NULL OR difficulty = :difficulty)
        ORDER BY RANDOM() 
        LIMIT :count
        """
    )
    suspend fun getRandomQuestionsForQuiz(
        category: String?,
        difficulty: String?,
        count: Int
    ): List<QuestionEntity>

    @Query("UPDATE questions SET isBookmarked = NOT isBookmarked WHERE id = :id")
    suspend fun toggleBookmark(id: Long)

    @Query("UPDATE questions SET isMastered = NOT isMastered WHERE id = :id")
    suspend fun toggleMastered(id: Long)

    @Query("UPDATE questions SET mastery = mastery + 1, isMastered = CASE WHEN mastery + 1 >= 5 THEN 1 ELSE isMastered END WHERE id = :id")
    suspend fun incrementMastery(id: Long)

    @Query("UPDATE questions SET mastery = :mastery, isMastered = CASE WHEN :mastery >= 5 THEN 1 ELSE 0 END WHERE id = :id")
    suspend fun updateMastery(id: Long, mastery: Int)

    @Query("UPDATE questions SET mastery = CASE WHEN mastery > 0 THEN mastery - 1 ELSE 0 END WHERE id = :id")
    suspend fun decrementMastery(id: Long)

    @Query("UPDATE questions SET userNotes = :notes WHERE id = :id")
    suspend fun updateNotes(id: Long, notes: String?)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestion(question: QuestionEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(questions: List<QuestionEntity>)

    @Query("DELETE FROM questions WHERE id = :id")
    suspend fun deleteQuestionById(id: Long)

    @Query("SELECT COUNT(*) FROM questions")
    fun getQuestionsCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM questions")
    suspend fun getQuestionsCountSync(): Int

    @Query("SELECT COUNT(*) FROM questions WHERE category = :categoryId AND questionType = 'FLASHCARD'")
    fun getFlashcardCountByCategory(categoryId: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM questions WHERE isMastered = 1")
    fun getMasteredCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM questions WHERE isBookmarked = 1")
    fun getBookmarkedCount(): Flow<Int>
}
