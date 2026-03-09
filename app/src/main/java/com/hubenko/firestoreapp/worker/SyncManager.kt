package com.hubenko.firestoreapp.worker

import android.content.Context
import androidx.work.*

object SyncManager {
    fun enqueueSyncWork(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncRequest = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "SyncStatusesWork",
            ExistingWorkPolicy.REPLACE,
            syncRequest
        )
    }
}
