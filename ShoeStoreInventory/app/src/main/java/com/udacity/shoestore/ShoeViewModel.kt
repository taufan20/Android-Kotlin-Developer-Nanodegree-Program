package com.udacity.shoestore

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.udacity.shoestore.models.Shoe

class ShoeViewModel : ViewModel() {

    private var _shoes = mutableListOf<Shoe>()
    private val _shoeList = MutableLiveData<MutableList<Shoe>>()
    val shoeList: LiveData<MutableList<Shoe>>
        get() = _shoeList

    private val _eventAddShoeFinish = MutableLiveData<Boolean>()
    val eventAddShoeFinish: LiveData<Boolean>
        get() = _eventAddShoeFinish

    init {
        Log.i("ShoeViewModel", "init: ${shoeList.value?.size}")
    }

    fun addShoe(shoe: Shoe) {
        val isShoeExist = _shoeList.value?.contains(shoe)

        isShoeExist?.let {
            _eventAddShoeFinish.value = false
        }.run {
            _shoes.add(shoe)
            _shoeList.value = _shoes
            _eventAddShoeFinish.value = true
        }

        Log.i("ShoeViewModel", "addShoe: ${_shoeList.value?.size}")
    }

    fun onAddShoeComplete() {
        _eventAddShoeFinish.value = false
    }
}