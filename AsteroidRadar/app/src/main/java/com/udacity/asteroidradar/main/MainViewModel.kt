package com.udacity.asteroidradar.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.model.Asteroid
import com.udacity.asteroidradar.repository.AsteroidRepository
import kotlinx.coroutines.launch

enum class AsteroidApiStatus { LOADING, DONE }

enum class Filter { WEEK, TODAY, SAVED }

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val database = getDatabase(application)
    private val asteroidsRepository = AsteroidRepository(database)

    private val _pictureUrl = MutableLiveData<String>()
    val pictureUrl: LiveData<String>
        get() = _pictureUrl

    private val _title = MutableLiveData<String>()
    val title: LiveData<String>
        get() = _title

    private val _status = MutableLiveData<AsteroidApiStatus>()
    val status: LiveData<AsteroidApiStatus>
        get() = _status

    private val _asteroids = MutableLiveData<List<Asteroid>>()
    val asteroids: LiveData<List<Asteroid>>
        get() = _asteroids

    private val asteroidsObserver = Observer<List<Asteroid>> {
        _asteroids.value = it
    }

    private var asteroidsLiveData: LiveData<List<Asteroid>>

    init {
        asteroidsLiveData = asteroidsRepository.getAsteroids(Filter.SAVED)
        asteroidsLiveData.observeForever(asteroidsObserver)

        refreshAsteroids()
        getPictureOfTheDay()
    }

    fun getFilteredAsteroids(filter: Filter) {
        asteroidsLiveData = asteroidsRepository.getAsteroids(filter)
        asteroidsLiveData.observeForever(asteroidsObserver)
    }

    private fun refreshAsteroids() {
        _status.value = AsteroidApiStatus.LOADING
        viewModelScope.launch {
            _status.value = AsteroidApiStatus.DONE
            asteroidsRepository.refreshAsteroids()
        }
    }

    private fun getPictureOfTheDay() {
        viewModelScope.launch {
            val result = asteroidsRepository.getPictureOfTheDay()
            _pictureUrl.value = result?.url.orEmpty()
            _title.value = result?.title.orEmpty()
        }
    }

    override fun onCleared() {
        super.onCleared()
        asteroidsLiveData.removeObserver(asteroidsObserver)
    }

    /**
     * Factory for constructing DevByteViewModel with parameter
     */
    class Factory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}