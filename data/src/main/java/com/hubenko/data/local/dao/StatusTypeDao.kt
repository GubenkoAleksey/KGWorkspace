package com.hubenko.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hubenko.data.local.entity.StatusTypeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StatusTypeDao {
    @Query("SELECT * FROM status_types ORDER BY label ASC")
    fun getAll(): Flow<List<StatusTypeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(types: List<StatusTypeEntity>)
}
