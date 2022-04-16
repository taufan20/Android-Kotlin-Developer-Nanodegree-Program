package com.udacity.shoestore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.udacity.shoestore.models.Shoe
import java.lang.IllegalArgumentException

/*

class ShoeViewModelFactory(private val shoe: Shoe? = null) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ShoeViewModel::class.java)) {
            return ShoeViewModel(shoe) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}*/
