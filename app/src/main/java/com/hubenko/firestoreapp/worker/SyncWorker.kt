package com.hubenko.firestoreapp.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.firestore.FirebaseFirestore
import com.hubenko.firestoreapp.data.local.AppDatabase
import com.hubenko.firestoreapp.data.repository.StatusRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            // Instantiate dependencies manually as we don't use Hilt
            val dao = AppDatabase.getDatabase(applicationContext).employeeStatusDao()
            val firestore = FirebaseFirestore.getInstance()
            val repository = StatusRepository(applicationContext, dao, firestore)

            val unsyncedStatuses = repository.getUnsyncedStatuses()

            if (unsyncedStatuses.isEmpty()) {
                return@withContext Result.success()
            }

            val result = repository.syncStatuses(unsyncedStatuses)
            
            if (result.isSuccess) {
                Result.success()
            } else {
                Result.retry()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }
}
