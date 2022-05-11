package com.udacity.shoestore

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.udacity.shoestore.models.Shoe

class ShoeViewModel : ViewModel() {

    private var _shoes = mutableListOf<Shoe>()

    private val _shoeList = MutableLiveData<MutableList<Shoe>>()
    val shoeList: LiveData<MutableList<Shoe>>
        get() = _shoeList

    val name = MutableLiveData<String>()
    val size = MutableLiveData<String>()
    val company = MutableLiveData<String>()
    val description = MutableLiveData<String>()
    val image = MutableLiveData<String>()

    private val _isAddShoeSuccess = MutableLiveData<Boolean?>()
    val isAddShoeSuccess: LiveData<Boolean?>
        get() = _isAddShoeSuccess


    fun addShoe() {

        val shoe = Shoe(
            name = name.value.orEmpty(),
            size = size.value.orEmpty(),
            company = company.value.orEmpty(),
            description = description.value.orEmpty(),
            images = listOf(image.value.orEmpty())
        )

        val isShoeExist = _shoeList.value?.contains(shoe)

        if (isShoeExist != null && isShoeExist) {
            _isAddShoeSuccess.value = false
        } else {
            _shoes.add(shoe)
            _shoeList.value = _shoes
            _isAddShoeSuccess.value = true
        }

    }

    fun addShoeCompleted() {
        _isAddShoeSuccess.value = null
    }

    fun clearShoe() {
        name.value = ""
        size.value = ""
        company.value = ""
        description.value = ""
        image.value = ""
    }

    fun setShoe(shoe: Shoe?) {

        if (shoe != null) {
            shoe.let {
                name.value = it.name
                size.value = it.size
                company.value = it.company
                description.value = it.description
                image.value = it.images.firstOrNull().orEmpty()
            }
        } else {
            clearShoe()
        }
    }

}