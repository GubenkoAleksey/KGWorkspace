package com.hubenko.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.hubenko.data.local.dao.BaseRateDao
import com.hubenko.data.local.dao.EmployeeDao
import com.hubenko.data.local.dao.EmployeeHourlyRateDao
import com.hubenko.data.local.dao.EmployeeStatusDao
import com.hubenko.data.local.dao.HourlyRateDao
import com.hubenko.data.local.dao.ReminderSettingsDao
import com.hubenko.data.local.dao.StatusTypeDao
import com.hubenko.data.local.entity.BaseRateEntity
import com.hubenko.data.local.entity.EmployeeEntity
import com.hubenko.data.local.entity.EmployeeHourlyRateEntity
import com.hubenko.data.local.entity.EmployeeStatusEntity
import com.hubenko.data.local.entity.HourlyRateEntity
import com.hubenko.data.local.entity.ReminderSettingsEntity
import com.hubenko.data.local.entity.StatusTypeEntity

@Database(
    entities = [
        EmployeeStatusEntity::class,
        EmployeeEntity::class,
        EmployeeHourlyRateEntity::class,
        ReminderSettingsEntity::class,
        StatusTypeEntity::class,
        BaseRateEntity::class,
        HourlyRateEntity::class
    ],
    version = 16,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun employeeStatusDao(): EmployeeStatusDao
    abstract fun employeeDao(): EmployeeDao
    abstract fun employeeHourlyRateDao(): EmployeeHourlyRateDao
    abstract fun reminderSettingsDao(): ReminderSettingsDao
    abstract fun statusTypeDao(): StatusTypeDao
    abstract fun baseRateDao(): BaseRateDao
    abstract fun hourlyRateDao(): HourlyRateDao
}
