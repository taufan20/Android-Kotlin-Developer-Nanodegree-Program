package com.example.android.politicalpreparedness.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.datasource.ElectionDataSource
import com.example.android.politicalpreparedness.datasource.Result
import com.example.android.politicalpreparedness.network.CivicsApiService
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.ErrorResponse
import com.example.android.politicalpreparedness.network.models.dateToFormatedDate
import com.example.android.politicalpreparedness.network.models.ocdDivisionIdToDivision
import com.example.android.politicalpreparedness.representative.model.Representative
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.io.IOException
import retrofit2.HttpException

class ElectionRepository(
    private val civicsApiService: CivicsApiService,
    private val electionDao: ElectionDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ElectionDataSource {

    override val savedElections: LiveData<List<Election>> = electionDao.getSavedElections()

    override suspend fun getUpComingElections() = withContext(ioDispatcher) {
            return@withContext try {
                val result = civicsApiService.getElections()
                val elections = result.elections.map { election ->
                    Election(
                        id = election.id,
                        name = election.name,
                        electionDay = election.dateToFormatedDate(),
                        division = election.ocdDivisionIdToDivision()
                    )
                }
                Result.Success(elections)
            } catch (throwable: Throwable) {
                exceptionHandling(throwable)
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
            Log.d("ElectionRepository", "getVoterInfo: result ${Gson().toJson(result)}")
            Result.Success(state)
        } catch (throwable: Throwable) {
            exceptionHandling(throwable)
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

        } catch (throwable: Throwable) {
            exceptionHandling(throwable)
        }
    }

    private fun exceptionHandling(throwable: Throwable) = when (throwable) {
        is IOException -> Result.NetworkError
        is HttpException -> {
            val code = throwable.code()
            val errorResponse = convertErrorBody(throwable)
            Result.Error(errorResponse?.error?.message.orEmpty(), code)
        }
        else -> {
            Result.Error(null, null)
        }
    }

    private fun convertErrorBody(throwable: HttpException): ErrorResponse? {
        return try {
            throwable.response()?.errorBody()?.source()?.let {
                val errorResponse = Gson().fromJson(
                    throwable.response()?.errorBody()?.charStream(),
                    ErrorResponse::class.java
                )
                errorResponse
            }
        } catch (exception: Exception) {
            null
        }
    }
}