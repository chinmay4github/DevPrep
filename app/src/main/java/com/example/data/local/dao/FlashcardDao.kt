package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.FlashcardEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FlashcardDao {
    @Query("SELECT * FROM flashcards ORDER BY id ASC")
    fun getAllFlashcards(): Flow<List<FlashcardEntity>>

    @Query("SELECT * FROM flashcards WHERE id = :id LIMIT 1")
    suspend fun getFlashcardById(id: Long): FlashcardEntity?

    @Query("SELECT * FROM flashcards WHERE category = :categoryId ORDER BY id ASC")
    fun getFlashcardsByCategory(categoryId: String): Flow<List<FlashcardEntity>>

    @Query("UPDATE flashcards SET mastery = mastery + 1, isMastered = CASE WHEN mastery + 1 >= 5 THEN 1 ELSE isMastered END WHERE id = :id")
    suspend fun incrementMastery(id: Long)

    @Query("UPDATE flashcards SET mastery = :mastery, isMastered = CASE WHEN :mastery >= 5 THEN 1 ELSE 0 END WHERE id = :id")
    suspend fun updateMastery(id: Long, mastery: Int)

    @Query("UPDATE flashcards SET mastery = CASE WHEN mastery > 0 THEN mastery - 1 ELSE 0 END WHERE id = :id")
    suspend fun decrementMastery(id: Long)

    @Query("UPDATE flashcards SET isMastered = NOT isMastered WHERE id = :id")
    suspend fun toggleMastered(id: Long)

    @Query("UPDATE flashcards SET isBookmarked = NOT isBookmarked WHERE id = :id")
    suspend fun toggleBookmark(id: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(flashcard: FlashcardEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(flashcards: List<FlashcardEntity>)

    @Query("SELECT COUNT(*) FROM flashcards")
    suspend fun getFlashcardCountSync(): Int

    @Query("SELECT COUNT(*) FROM flashcards")
    fun getFlashcardCount(): Flow<Int>
}
