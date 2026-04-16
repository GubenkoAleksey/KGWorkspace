package com.hubenko.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hubenko.data.local.entity.HourlyRateEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HourlyRateDao {
    @Query("SELECT * FROM hourly_rates ORDER BY label ASC")
    fun getAll(): Flow<List<HourlyRateEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(rates: List<HourlyRateEntity>)
}
