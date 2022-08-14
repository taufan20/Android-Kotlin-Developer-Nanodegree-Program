package com.example.android.politicalpreparedness.election

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.datasource.ElectionDataSource
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.VoterInfo

class VoterInfoViewModel(
    private val dataSource: ElectionDataSource,
    private val app: Application) : ViewModel() {

    //TODO: Add live data to hold voter info
    private val _electionInfoUrl = MutableLiveData<String>()
    var electionInfoUrl: LiveData<String> = _electionInfoUrl

    //TODO: Add var and methods to populate voter info
    private var _votingLocationFinderUrl = MutableLiveData<String>()
    val votingLocationFinderUrl: LiveData<String>
        get() = _votingLocationFinderUrl

    //TODO: Add var and methods to support loading URLs
    private var _ballotInfoUrl = MutableLiveData<String>()
    val ballotInfoUrl: LiveData<String>
        get() = _ballotInfoUrl

    //TODO: Add var and methods to save and remove elections to local database
    //TODO: cont'd -- Populate initial state of save button to reflect proper action based on election saved status

    /**
     * Hint: The saved state can be accomplished in multiple ways. It is directly related to how elections are saved/removed from the database.
     */

}