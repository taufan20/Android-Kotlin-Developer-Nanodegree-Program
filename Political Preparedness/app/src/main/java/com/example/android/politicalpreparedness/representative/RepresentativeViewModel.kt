package com.example.android.politicalpreparedness.representative

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.datasource.ElectionDataSource
import com.example.android.politicalpreparedness.datasource.Result
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.representative.model.Representative
import com.example.android.politicalpreparedness.utils.SingleLiveEvent
import kotlinx.coroutines.launch

class RepresentativeViewModel(
    private val dataSource: ElectionDataSource,
    val app: Application
    ): ViewModel() {

    //TODO: Establish live data for representatives and address
    private val _representativeList = MutableLiveData<MutableList<Representative>>()
    val representativeList: LiveData<MutableList<Representative>>
        get() = _representativeList

    private val _address = MutableLiveData<Address>()
    val address: LiveData<Address>
        get() = _address

    val showLoading: SingleLiveEvent<Boolean> = SingleLiveEvent()
    val showErrorMessage: SingleLiveEvent<String> = SingleLiveEvent()

    init {
        _address.value = Address( "", "", "", "", "")
    }

    //TODO: Create function to fetch representatives from API from a provided address
    /**
     *  The following code will prove helpful in constructing a representative from the API. This code combines the two nodes of the RepresentativeResponse into a single official :

    val (offices, officials) = getRepresentativesDeferred.await()
    _representatives.value = offices.flatMap { office -> office.getRepresentatives(officials) }

    Note: getRepresentatives in the above code represents the method used to fetch data from the API
    Note: _representatives in the above code represents the established mutable live data housing representatives

     */
    private fun getRepresentatives(address: String) {
        viewModelScope.launch {
            val result = dataSource.getRepresentatives(address)
            showLoading.value = false
            when (result) {
                is Result.Success<*> -> {
                    _representativeList.value = result as MutableList<Representative>
                }
                is Result.Error ->
                    showErrorMessage.value = result.message.orEmpty()
            }
        }
    }

    //TODO: Create function get address from geo location
    fun onUseMyLocationClicked(retrievedAddress: Address) {
        _address.value = retrievedAddress
        getRepresentatives(retrievedAddress.toFormattedString())
    }

    //TODO: Create function to get address from individual fields
    fun onSearchMyRepresentativesClicked() {
        _address.value?.let {
            getRepresentatives(it.toFormattedString())
        }
    }
}
