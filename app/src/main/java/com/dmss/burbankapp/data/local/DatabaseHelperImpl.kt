package com.dmss.burbankapp.data.local.entity

import com.dmss.burbankapp.data.local.AppDatabase
import com.dmss.burbankapp.data.model.StateModel


class DatabaseHelperImpl(private val appDatabase: AppDatabase) : DatabaseHelper {
    override suspend fun insertStates(stateList: List<StateModel>) {
        appDatabase.statesDao().saveStates(stateList)
    }

    override suspend fun getAllStates(): List<StateModel> = appDatabase.statesDao().getAllStates()
    override suspend fun deleteAllStates() = appDatabase.statesDao().deleteStatesRepositories()


}