package com.example.android.politicalpreparedness.election

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.datasource.ElectionDataSource

//TODO: Create Factory to generate ElectionViewModel with provided election datasource
class ElectionsViewModelFactory(
    private val electionDataSource: ElectionDataSource,
    private val app: Application): ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ElectionsViewModel::class.java)) {
            return ElectionsViewModel(electionDataSource, app) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}