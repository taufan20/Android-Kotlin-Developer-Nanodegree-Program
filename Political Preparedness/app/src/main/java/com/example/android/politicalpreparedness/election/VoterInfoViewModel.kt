package com.example.android.politicalpreparedness.election

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.datasource.ElectionDataSource
import com.example.android.politicalpreparedness.utils.SingleLiveEvent
import kotlinx.coroutines.launch
import com.example.android.politicalpreparedness.datasource.Result
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.State
import com.example.android.politicalpreparedness.network.models.VoterInfo

class VoterInfoViewModel(
    private val dataSource: ElectionDataSource,
    private val app: Application,
    val election: Election) : ViewModel() {

    //TODO: Add live data to hold voter info
    private val _electionInfoUrl = MutableLiveData<String>()

    //TODO: Add var and methods to populate voter info
    private var _votingLocationFinderUrl = MutableLiveData<String>()

    //TODO: Add var and methods to support loading URLs
    private var _ballotInfoUrl = MutableLiveData<String>()

    private var _savedElection = MutableLiveData<Election?>()
    val savedElection: LiveData<Election?>
        get() = _savedElection

    val showLoading: SingleLiveEvent<Boolean> = SingleLiveEvent()
    val showErrorMessage: SingleLiveEvent<String> = SingleLiveEvent()

    private val _navigateToWebBrowser = MutableLiveData<String?>()
    val navigateToWebBrowser: LiveData<String?>
        get() = _navigateToWebBrowser


    init {
        getVoterInfo()
        getSavedElection()
    }

    //TODO: Add var and methods to save and remove elections to local database
    //TODO: cont'd -- Populate initial state of save button to reflect proper action based on election saved status

    /**
     * Hint: The saved state can be accomplished in multiple ways. It is directly related to how elections are saved/removed from the database.
     */


    private fun getVoterInfo() {
        viewModelScope.launch {
            showLoading.value = true
            val dummyAddress = "Modesto"
            val result = dataSource.getVoterInfo(address = dummyAddress, electionId = election.id)
            showLoading.value = false
            when (result) {
                is Result.Success<*> -> {
                    val states = result.data as List<State>
                    if (states.isNotEmpty()) {
                        val electionInfo = states[0].electionAdministrationBody
                        _electionInfoUrl.value = electionInfo.electionInfoUrl.orEmpty()
                        _ballotInfoUrl.value = electionInfo.ballotInfoUrl.orEmpty()
                        _votingLocationFinderUrl.value = electionInfo.votingLocationFinderUrl.orEmpty()
                    }
                }
                is Result.Error -> {
                    showErrorMessage.value = result.message.orEmpty()
                }
            }
        }
    }

    private fun getSavedElection() {
        viewModelScope.launch {
            when (val result = dataSource.getElectionById(electionId = election.id)) {
                is Result.Success<*> -> {
                    _savedElection.value = result.data as Election
                }
                else -> {
                    _savedElection.value = null
                }
            }
        }
    }

    fun onFollowButtonClicked() {
        viewModelScope.launch {
            if (_savedElection.value == null) {
                dataSource.saveElection(election)
            } else {
                dataSource.deleteElection(election)
            }
        }
    }

    fun onElectionInfoClicked() {
        _navigateToWebBrowser.value = _electionInfoUrl.value
    }

    fun onBallotInfoClicked() {
        _navigateToWebBrowser.value = _ballotInfoUrl.value
    }

    fun onVotingLocationClicked() {
        _navigateToWebBrowser.value = _votingLocationFinderUrl.value
    }

    fun onNavigatingToBrowserDone() {
        _navigateToWebBrowser.value = null
    }




}