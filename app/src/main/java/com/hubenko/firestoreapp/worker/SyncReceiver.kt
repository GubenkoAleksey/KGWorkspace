package com.hubenko.firestoreapp.worker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class SyncReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "com.hubenko.firestoreapp.SYNC_STATUSES") {
            SyncManager.enqueueSyncWork(context)
        }
    }
}
