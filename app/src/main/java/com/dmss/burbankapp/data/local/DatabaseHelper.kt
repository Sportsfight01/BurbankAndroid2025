package com.dmss.burbankapp.data.local.entity

import com.dmss.burbankapp.data.model.StateModel

interface DatabaseHelper {
    suspend fun insertStates(stateList: List<StateModel>)
    suspend fun getAllStates(): List<StateModel>
    suspend fun deleteAllStates()


}