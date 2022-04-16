package com.udacity.shoestore

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.udacity.shoestore.models.Shoe

class ShoeViewModel : BaseObservable() {

    var name = MutableLiveData<String>()
    var size = MutableLiveData<Double>()
    var company = MutableLiveData<String>()
    var description = MutableLiveData<String>()
    var images = MutableLiveData<List<String>>()

    private var _shoes = mutableListOf<Shoe>()
    private val _shoeList = MutableLiveData<MutableList<Shoe>>()
    val shoeList: LiveData<MutableList<Shoe>>
        get() = _shoeList

    private val _isAddShoeSuccess = MutableLiveData<Boolean>()
    val isAddShoeSuccess: LiveData<Boolean>
        get() = _isAddShoeSuccess

    @Bindable()
    fun setSizeValue(value: String) {
        size.value = value.toDouble()
    }//setDoubleInTextView

    fun addShoe() {
        /*Log.i("ShoeViewModel", "addShoe: ${shoe.value?.name}")

        val isShoeExist = _shoeList.value?.contains(shoe.value)

        if (isShoeExist != null && isShoeExist) {
            _isAddShoeSuccess.value = false
        } else {
            shoe.value?.let {
                _shoes.add(it)
                _shoeList.value = _shoes
                _isAddShoeSuccess.value = true
            }
        }*/

    }

    fun checkShoe(shoe: Shoe) {
        Log.i("ShoeViewModel", "checkShoe: ${shoe.name}")
    }

}