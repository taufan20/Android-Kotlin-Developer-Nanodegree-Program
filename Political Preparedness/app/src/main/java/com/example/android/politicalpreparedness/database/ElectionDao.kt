package com.example.android.politicalpreparedness.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.android.politicalpreparedness.network.models.Election
import retrofit2.http.DELETE

@Dao
interface ElectionDao {

    //TODO: Add insert query
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveElection(election: Election)

    //TODO: Add select all election query
    @Query("SELECT * FROM election_table")
    fun getSavedElections(): LiveData<List<Election>>

    //TODO: Add select single election query
    @Query("SELECT * FROM election_table WHERE id == :electionId")
    suspend fun getElectionById(electionId: Long) : Election

    //TODO: Add delete query
    @Query("DELETE FROM election_table WHERE id == :electionId")
    suspend fun removeElection(electionId: Long)

    //TODO: Add clear query
    @Query("DELETE FROM election_table")
    suspend fun removeAll()

}