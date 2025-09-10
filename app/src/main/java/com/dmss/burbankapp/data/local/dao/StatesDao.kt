package com.dmss.burbankapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dmss.burbankapp.data.model.StateModel

@Dao
interface StatesDao {
    @Query("SELECT * FROM states")
    suspend fun getAllStates(): List<StateModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveStates(steps: List<StateModel>)

    @Query("DELETE FROM states")
    suspend fun deleteStatesRepositories()
}