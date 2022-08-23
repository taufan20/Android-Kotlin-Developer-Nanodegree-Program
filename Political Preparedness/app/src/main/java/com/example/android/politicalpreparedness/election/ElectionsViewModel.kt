package com.example.android.politicalpreparedness.election

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.datasource.ElectionDataSource
import com.example.android.politicalpreparedness.network.models.Election
import kotlinx.coroutines.launch
import com.example.android.politicalpreparedness.datasource.Result
import com.example.android.politicalpreparedness.utils.SingleLiveEvent

//TODO: Construct ViewModel and provide election datasource
class ElectionsViewModel(
    private val dataSource: ElectionDataSource,
    val app: Application): ViewModel() {

    //TODO: Create live data val for upcoming elections
    private val _upComingElections = MutableLiveData<List<Election>>()
    var upComingElections: LiveData<List<Election>> = _upComingElections

    //TODO: Create live data val for saved elections
    var savedElections: LiveData<List<Election>> = dataSource.savedElections

    private val _navigateToVoterInfo = MutableLiveData<Election>()
    val navigateToVoterInfo: LiveData<Election>
        get() = _navigateToVoterInfo

    val showLoading: SingleLiveEvent<Boolean> = SingleLiveEvent()
    val showErrorMessage: SingleLiveEvent<String> = SingleLiveEvent()


    //TODO: Create val and functions to populate live data for upcoming elections from the API and saved elections from local database
    fun getUpComingElections() {
        showLoading.value = true
        viewModelScope.launch {
            val result = dataSource.getUpComingElections()
            showLoading.value = false
            when (result) {
                is Result.Success<*> -> {
                    val dataList = ArrayList<Election>()
                    dataList.addAll((result.data as List<Election>))
                    _upComingElections.value = dataList
                }
                is Result.Error ->
                    showErrorMessage.value = result.message.orEmpty()
            }
        }
    }

    //TODO: Create functions to navigate to saved or upcoming election voter info
    fun onElectionClicked(election: Election) {
        _navigateToVoterInfo.value = election
    }

    fun doneNavigating() {
        _navigateToVoterInfo.value = null
    }

}