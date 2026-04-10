package com.hubenko.data.util

import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteFullException
import com.hubenko.domain.error.DataError
import com.hubenko.domain.util.Result
import kotlinx.coroutines.CancellationException

suspend fun <T> localSafeCall(
    execute: suspend () -> T
): Result<T, DataError.Local> {
    return try {
        Result.Success(execute())
    } catch (e: SQLiteFullException) {
        Result.Error(DataError.Local.DISK_FULL)
    } catch (e: SQLiteException) {
        Result.Error(DataError.Local.UNKNOWN)
    } catch (e: Exception) {
        if (e is CancellationException) throw e
        Result.Error(DataError.Local.UNKNOWN)
    }
}