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
    private val electionId: Long) : ViewModel() {

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

    private var _savedElection = MutableLiveData<Election?>()
    val savedElection: LiveData<Election?>
        get() = _savedElection

    val showLoading: SingleLiveEvent<Boolean> = SingleLiveEvent()
    val showErrorMessage: SingleLiveEvent<String> = SingleLiveEvent()


    init {
        getVoterInfo()
    }

    //TODO: Add var and methods to save and remove elections to local database
    //TODO: cont'd -- Populate initial state of save button to reflect proper action based on election saved status

    /**
     * Hint: The saved state can be accomplished in multiple ways. It is directly related to how elections are saved/removed from the database.
     */


    private fun getVoterInfo() {
        viewModelScope.launch {
            showLoading.value = true
            val result = dataSource.getVoterInfo(electionId = electionId)
            showLoading.postValue(false)
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

    fun getSavedElection(electionId: Long) {
        viewModelScope.launch {
            val result = dataSource.getElectionById(electionId = electionId)
            when (result) {
                is Result.Success<*> -> {
                    _savedElection.value = result.data as Election
                }
                else -> {
                    _savedElection.value = null
                }
            }
        }
    }

    fun updateFollowStatus() {
        if (_savedElection.value == null) {
            dataSource.saveElection(_savedElection.value)
        }
    }

}