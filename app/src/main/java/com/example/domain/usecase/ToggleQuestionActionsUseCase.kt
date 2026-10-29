package com.example.domain.usecase

import com.example.domain.repository.QuestionRepository

class ToggleQuestionActionsUseCase(
    private val repository: QuestionRepository
) {
    suspend fun toggleBookmark(id: Long) = repository.toggleBookmark(id)
    suspend fun toggleMastered(id: Long) = repository.toggleMastered(id)
    suspend fun recordCorrectGuess(id: Long) = repository.incrementMastery(id)
    suspend fun incrementMastery(id: Long) = repository.incrementMastery(id)
    suspend fun updateMastery(id: Long, mastery: Int) = repository.updateMastery(id, mastery)
    suspend fun decrementMastery(id: Long) = repository.decrementMastery(id)
    suspend fun updateNotes(id: Long, notes: String?) = repository.updateNotes(id, notes)
    suspend fun delete(id: Long) = repository.deleteQuestion(id)
}
