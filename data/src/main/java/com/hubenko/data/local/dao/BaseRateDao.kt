package com.hubenko.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hubenko.data.local.entity.BaseRateEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BaseRateDao {
    @Query("SELECT * FROM base_rates ORDER BY label ASC")
    fun getAll(): Flow<List<BaseRateEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(rates: List<BaseRateEntity>)
}
