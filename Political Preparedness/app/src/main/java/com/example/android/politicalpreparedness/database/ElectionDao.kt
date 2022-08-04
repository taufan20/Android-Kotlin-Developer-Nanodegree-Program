package com.example.android.politicalpreparedness.database

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
    suspend fun getElections(): List<Election>

    //TODO: Add select single election query
    @Query("SELECT * FROM election_table WHERE id == :electionId")
    suspend fun getElectionById(electionId: Int)

    //TODO: Add delete query
    @DELETE
    suspend fun delete(election: Election)

    //TODO: Add clear query
    @Query("DELETE FROM election_table")
    suspend fun deleteAll()

}