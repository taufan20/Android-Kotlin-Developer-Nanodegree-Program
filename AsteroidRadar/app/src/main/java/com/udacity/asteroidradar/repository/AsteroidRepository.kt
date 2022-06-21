package com.udacity.asteroidradar.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.database.asDomainModel
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.asNetworkModel
import com.udacity.asteroidradar.network.Network
import com.udacity.asteroidradar.network.NetworkAsteroidContainer
import com.udacity.asteroidradar.network.asDatabaseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class AsteroidRepository(private val database: AsteroidDatabase) {

    val asteroids: LiveData<List<Asteroid>> = Transformations.map(database.asteroidDao.getAsteroids()) {
        it.asDomainModel()
    }

    suspend fun refreshAsteroids() {
        withContext(Dispatchers.IO) {
            try {
                val result = Network.asteroid.getFeeds(startDate = getCurrentFormattedDate())
                val jsonObject = parseAsteroidsJsonResult(JSONObject(result))
                Log.e("MainViewModel", "Success: $jsonObject")
                val resultDb = NetworkAsteroidContainer(jsonObject.map { asteroid ->
                    asteroid.asNetworkModel()
                })
                database.asteroidDao.insertAll(*resultDb.asDatabaseModel())
            } catch (e: Exception) {
                Log.e("MainViewModel", "Failure: " + e.message)
            }
        }
    }
    


    fun getCurrentFormattedDate(): String {
        val currentTime = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT, Locale.getDefault())
        return dateFormat.format(currentTime)
    }
}