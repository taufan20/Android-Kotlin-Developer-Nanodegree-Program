package com.example.android.politicalpreparedness.election

import android.app.Application
import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.datasource.ElectionDataSource
import com.example.android.politicalpreparedness.network.models.Election
import kotlinx.coroutines.launch
import com.example.android.politicalpreparedness.datasource.Result
import com.example.android.politicalpreparedness.utils.SingleLiveEvent
import com.google.gson.Gson

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

    val showLoading: SingleLiveEvent<Int> = SingleLiveEvent()
    val showErrorMessage: SingleLiveEvent<String> = SingleLiveEvent()


    //TODO: Create val and functions to populate live data for upcoming elections from the API and saved elections from local database
    fun getUpComingElections() {
        clearData()
        viewModelScope.launch {
            val result = dataSource.getUpComingElections()
            showLoading.value = View.GONE
            when (result) {
                is Result.Success<*> -> {
                    val dataList = ArrayList<Election>()
                    dataList.addAll((result.data as List<Election>))
                    _upComingElections.value = dataList
                }
                is Result.Error -> {
                    showErrorMessage.value = result.message.orEmpty()
                }
                else -> {
                    showErrorMessage.value = app.getString(R.string.unable_to_connect)
                }
            }
        }
    }

    private fun clearData() {
        _upComingElections.value = mutableListOf()
        showLoading.value = View.VISIBLE
        showErrorMessage.value = ""
    }

    //TODO: Create functions to navigate to saved or upcoming election voter info
    fun onElectionClicked(election: Election) {
        _navigateToVoterInfo.value = election
    }

    fun doneNavigating() {
        _navigateToVoterInfo.value = null
    }

}