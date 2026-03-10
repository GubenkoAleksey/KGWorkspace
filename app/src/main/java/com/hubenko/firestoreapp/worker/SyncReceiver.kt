package com.hubenko.firestoreapp.worker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.hubenko.data.worker.SyncManager

class SyncReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "com.hubenko.firestoreapp.SYNC_STATUSES") {
            SyncManager.enqueueSyncWork(context)
        }
    }
}
