package com.example.android.politicalpreparedness.repository

import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.datasource.ElectionDataSource
import com.example.android.politicalpreparedness.datasource.Result
import com.example.android.politicalpreparedness.network.models.Election
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class ElectionLocalRepository(
    private val electionDao: ElectionDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ElectionDataSource {
    override suspend fun getUpComingElections(): Result<List<Election>> {
        TODO("Not yet implemented")
    }

    override suspend fun getSavedElections(): Result<List<Election>> {
        TODO("Not yet implemented")
    }

    override suspend fun saveElection(election: Election) {
        TODO("Not yet implemented")
    }

    override suspend fun getElection(id: String): Result<Election> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteElections() {
        TODO("Not yet implemented")
    }
}