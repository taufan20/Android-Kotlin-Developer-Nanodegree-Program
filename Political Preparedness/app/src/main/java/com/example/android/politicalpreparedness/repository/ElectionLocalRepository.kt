package com.example.android.politicalpreparedness.repository

import android.util.Log
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.datasource.ElectionDataSource
import com.example.android.politicalpreparedness.datasource.Result
import com.example.android.politicalpreparedness.network.CivicsApiService
import com.example.android.politicalpreparedness.network.models.Election
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ElectionLocalRepository(
    private val civicsApiService: CivicsApiService,
    private val electionDao: ElectionDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ElectionDataSource {

    override suspend fun getUpComingElections() = withContext(ioDispatcher) {
            return@withContext try {
                val result = civicsApiService.getElections()
                Log.d("ElectionDataSource", "getUpComingElections: ${result.elections.size}")
                Result.Success(result.elections)
            } catch (e: Exception) {
                Result.Error(e.localizedMessage)

            }
        }

    override suspend fun getSavedElections() = withContext(ioDispatcher) {
            Result.Success(electionDao.getSavedElections())
    }

    override suspend fun saveElection(election: Election) = withContext(ioDispatcher) {
        electionDao.saveElection(election)
    }

    override suspend fun getVoterInfo(address: String, id: Long) = withContext(ioDispatcher) {
        return@withContext try {
            val result = civicsApiService.getVoterInfo(address, id)
            Result.Success(result.election)
        } catch (e: Exception) {
            Result.Error(e.localizedMessage)

        }
    }

    override suspend fun deleteElections() = withContext(ioDispatcher) {
        electionDao.removeAll()
    }

    override suspend fun deleteElectionById(electionId: Int) = withContext(ioDispatcher) {
        electionDao.removeElection(electionId)
    }
}