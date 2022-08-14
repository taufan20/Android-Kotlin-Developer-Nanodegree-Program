package com.example.android.politicalpreparedness.datasource

import androidx.lifecycle.LiveData
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.VoterInfo

interface ElectionDataSource {

    val savedElections: LiveData<List<Election>>
    suspend fun getUpComingElections(): Result<List<Election>>
    suspend fun saveElection(election: Election)
    suspend fun getVoterInfo(address: String, id: Long): Result<VoterInfo>
    suspend fun deleteElections()
    suspend fun deleteElectionById(electionId: Int)
}