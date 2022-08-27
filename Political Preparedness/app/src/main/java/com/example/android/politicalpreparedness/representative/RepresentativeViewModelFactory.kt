package com.example.android.politicalpreparedness.representative

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.datasource.ElectionDataSource
import com.example.android.politicalpreparedness.election.ElectionsViewModel
import com.example.android.politicalpreparedness.network.CivicsApiService

//TODO: Create Factory to generate ElectionViewModel with provided election datasource
class RepresentativeViewModelFactory(
    private val electionDataSource: ElectionDataSource,
    private val app: Application): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RepresentativeViewModel::class.java)) {
            return RepresentativeViewModel(electionDataSource, app) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }


}