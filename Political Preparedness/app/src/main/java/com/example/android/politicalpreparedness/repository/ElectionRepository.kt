package com.example.android.politicalpreparedness.repository

import androidx.lifecycle.LiveData
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.datasource.ElectionDataSource
import com.example.android.politicalpreparedness.datasource.Result
import com.example.android.politicalpreparedness.network.CivicsApiService
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.representative.model.Representative
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ElectionRepository(
    private val civicsApiService: CivicsApiService,
    private val electionDao: ElectionDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ElectionDataSource {

    override val savedElections: LiveData<List<Election>> = electionDao.getSavedElections()

    override suspend fun getUpComingElections() = withContext(ioDispatcher) {
            return@withContext try {
                val result = civicsApiService.getElections()
                Result.Success(result.elections)
            } catch (e: Exception) {
                Result.Error(e.localizedMessage)

            }
        }

    override suspend fun saveElection(election: Election) {
        withContext(ioDispatcher) {
            electionDao.saveElection(election)
        }
    }

    override suspend fun getElectionById(electionId: Int) = withContext(ioDispatcher) {
        return@withContext try {
            val result = electionDao.getElectionById(electionId)
            Result.Success(result)
        }catch (e: Exception) {
            Result.Error(e.localizedMessage)
        }
    }

    override suspend fun getVoterInfo(address: String, electionId: Int) = withContext(ioDispatcher) {
        return@withContext try {
            val result = civicsApiService.getVoterInfo(address, electionId)
            val state = result.state.orEmpty()
            Result.Success(state)
        } catch (e: Exception) {
            Result.Error(e.localizedMessage)

        }
    }

    override suspend fun deleteElections() = withContext(ioDispatcher) {
        electionDao.removeAll()
    }

    override suspend fun deleteElection(election: Election) {
        withContext(ioDispatcher) {
            electionDao.removeElection(election)
        }
    }

    override suspend fun getRepresentatives(address: String) = withContext(ioDispatcher) {
        return@withContext try {
            val result = civicsApiService.getRepresentatives(address)
            val offices = result.offices
            val officials = result.officials

            val representatives = mutableListOf<Representative>()
            offices.forEach { office ->
                representatives.addAll(office.getRepresentatives(officials))
            }

            Result.Success(representatives)

        } catch (e: Exception) {
            Result.Error(e.localizedMessage)
        }
    }
}