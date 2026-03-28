package com.hubenko.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hubenko.data.local.entity.ReminderSettingsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderSettingsDao {
    @Query("SELECT * FROM reminder_settings WHERE employeeId = :employeeId")
    fun getSettings(employeeId: String): Flow<ReminderSettingsEntity?>

    @Query("SELECT * FROM reminder_settings WHERE employeeId = :employeeId")
    suspend fun getSettingsSync(employeeId: String): ReminderSettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSettings(settings: ReminderSettingsEntity)
}
