package com.example.domain.usecase

import com.example.domain.repository.QuestionRepository

class ToggleQuestionActionsUseCase(
    private val repository: QuestionRepository
) {
    suspend fun toggleBookmark(id: Long) = repository.toggleBookmark(id)
    suspend fun toggleMastered(id: Long) = repository.toggleMastered(id)
    suspend fun updateNotes(id: Long, notes: String?) = repository.updateNotes(id, notes)
    suspend fun delete(id: Long) = repository.deleteQuestion(id)
}
