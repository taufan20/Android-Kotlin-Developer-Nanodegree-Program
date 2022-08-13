package com.example.android.politicalpreparedness.datasource

import com.example.android.politicalpreparedness.network.models.Election

interface ElectionDataSource {
    suspend fun getUpComingElections(): Result<List<Election>>
    suspend fun getSavedElections(): Result<List<Election>>
    suspend fun saveElection(election: Election)
    suspend fun getVoterInfo(address: String, id: Long): Result<Election>
    suspend fun deleteElections()
    suspend fun deleteElectionById(electionId: Int)
}