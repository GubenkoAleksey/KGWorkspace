package com.hubenko.data.util

import com.google.firebase.firestore.FirebaseFirestoreException
import com.hubenko.domain.error.DataError
import com.hubenko.domain.util.Result
import kotlinx.coroutines.CancellationException

suspend fun <T> firestoreSafeCall(
    execute: suspend () -> T
): Result<T, DataError.Firestore> {
    return try {
        Result.Success(execute())
    } catch (e: FirebaseFirestoreException) {
        Result.Error(
            when (e.code) {
                FirebaseFirestoreException.Code.NOT_FOUND -> DataError.Firestore.NOT_FOUND
                FirebaseFirestoreException.Code.PERMISSION_DENIED -> DataError.Firestore.PERMISSION_DENIED
                FirebaseFirestoreException.Code.UNAVAILABLE -> DataError.Firestore.UNAVAILABLE
                FirebaseFirestoreException.Code.UNAUTHENTICATED -> DataError.Firestore.UNAUTHENTICATED
                FirebaseFirestoreException.Code.ALREADY_EXISTS -> DataError.Firestore.ALREADY_EXISTS
                else -> DataError.Firestore.UNKNOWN
            }
        )
    } catch (e: Exception) {
        if (e is CancellationException) throw e
        Result.Error(DataError.Firestore.UNKNOWN)
    }
}