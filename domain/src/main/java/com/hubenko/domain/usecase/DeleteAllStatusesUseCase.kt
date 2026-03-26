package com.hubenko.domain.usecase

import com.hubenko.domain.repository.StatusRepository
import javax.inject.Inject

/**
 * UseCase для видалення всіх статусів (локально та у Firestore).
 */
class DeleteAllStatusesUseCase @Inject constructor(
    private val repository: StatusRepository
) {
    suspend operator fun invoke(): Result<Unit> = repository.deleteAllStatuses()
}
