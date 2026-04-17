package com.hubenko.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hubenko.data.local.entity.EmployeeHourlyRateEntity

@Dao
interface EmployeeHourlyRateDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(rates: List<EmployeeHourlyRateEntity>)

    @Query("DELETE FROM employee_hourly_rates WHERE employeeId = :employeeId")
    suspend fun deleteByEmployeeId(employeeId: String)
}
