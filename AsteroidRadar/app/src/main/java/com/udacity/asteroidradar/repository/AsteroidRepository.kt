package com.udacity.asteroidradar.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.database.asDomainModel
import com.udacity.asteroidradar.model.Asteroid
import com.udacity.asteroidradar.utils.Constants
import com.udacity.asteroidradar.model.PictureOfDay
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.main.Filter
import com.udacity.asteroidradar.model.asNetworkModel
import com.udacity.asteroidradar.network.Network
import com.udacity.asteroidradar.network.NetworkAsteroidContainer
import com.udacity.asteroidradar.network.asDatabaseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class AsteroidRepository(private val database: AsteroidDatabase) {

    fun getAsteroids(filter: Filter) : LiveData<List<Asteroid>> {
        return when (filter) {
            Filter.TODAY -> {
                Transformations.map(database.asteroidDao.getAsteroidsByToday(getCurrentFormattedDate())) {
                    it.asDomainModel()
                }
            }
            else -> {
                Transformations.map(database.asteroidDao.getAsteroids()) {
                    it.asDomainModel()
                }
            }
        }
    }

    suspend fun refreshAsteroids() {
        withContext(Dispatchers.IO) {
            try {
                val result = Network.asteroid.getFeeds(startDate = getCurrentFormattedDate())
                val jsonObject = parseAsteroidsJsonResult(JSONObject(result))
                val resultDb = NetworkAsteroidContainer(jsonObject.map { asteroid ->
                    asteroid.asNetworkModel()
                })
                database.asteroidDao.insertAll(*resultDb.asDatabaseModel())
            } catch (e: Exception) {
                Log.e("AsteroidRepository", "Failure: " + e.message)
                ""
            }
        }
    }

    suspend fun getPictureOfTheDay() : PictureOfDay? {
        return try {
            val result = Network.asteroid.getImageOfTheDay()
            result
        } catch (e: Exception) {
            Log.e("AsteroidRepository", "Failure: " + e.message)
            null
        }
    }

    fun deleteAll() {
        try {
            database.asteroidDao.deleteAll()
        } catch (e: Exception) {
            Log.e("AsteroidRepository", "Failure: " + e.message)
        }
    }


    private fun getCurrentFormattedDate(): String {
        val currentTime = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT, Locale.getDefault())
        return dateFormat.format(currentTime)
    }

}