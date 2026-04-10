package com.hubenko.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.hubenko.domain.repository.StatusRepository
import com.hubenko.domain.util.Result
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted private val workerParams: WorkerParameters,
    private val repository: StatusRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val unsynced = repository.getUnsyncedStatuses()
            if (unsynced.isEmpty()) return Result.success()
            when (repository.syncStatuses(unsynced)) {
                is com.hubenko.domain.util.Result.Success -> Result.success()
                is com.hubenko.domain.util.Result.Error -> Result.retry()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }
}
