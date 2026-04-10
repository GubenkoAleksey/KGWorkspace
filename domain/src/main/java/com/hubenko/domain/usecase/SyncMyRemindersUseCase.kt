package com.hubenko.domain.usecase

import com.hubenko.domain.repository.AuthDataSource
import com.hubenko.domain.repository.ReminderRepository
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class SyncMyRemindersUseCase @Inject constructor(
    private val authRepository: AuthDataSource,
    private val reminderRepository: ReminderRepository
) {
    suspend operator fun invoke() {
        val employeeId = authRepository.getCurrentUserId() ?: return
        // Get settings from Firestore and save to local DB (which also triggers AlarmScheduler in the RepositoryImpl)
        val settings = reminderRepository.getReminderSettings(employeeId).firstOrNull() ?: return
        reminderRepository.updateLocalCache(settings)
    }
}
