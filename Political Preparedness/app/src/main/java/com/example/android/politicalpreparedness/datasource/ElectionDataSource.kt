package com.example.android.politicalpreparedness.datasource

import androidx.lifecycle.LiveData
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.State
import com.example.android.politicalpreparedness.network.models.VoterInfo

interface ElectionDataSource {

    val savedElections: LiveData<List<Election>>
    suspend fun getUpComingElections(): Result<List<Election>>
    suspend fun saveElection(election: Election)
    suspend fun getElectionById(electionId: Int): Result<Election>
    suspend fun getVoterInfo(address: String = "", electionId: Int): Result<List<State>>
    suspend fun deleteElections()
    suspend fun deleteElection(election: Election)
}