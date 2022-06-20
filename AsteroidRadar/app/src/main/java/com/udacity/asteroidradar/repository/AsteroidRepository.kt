package com.udacity.asteroidradar.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.database.asDomainModel
import com.udacity.asteroidradar.Asteroid
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

class AsteroidRepository(private val database: AsteroidDatabase) {

    val asteroids: LiveData<List<Asteroid>> = Transformations.map(database.asteroidDao.getAsteroids()) {
        it.asDomainModel()
    }

    suspend fun refreshAsteroids() {
        withContext(Dispatchers.IO) {
            Network.asteroid.getFeeds()
                .enqueue(object: Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                val result = parseAsteroidsJsonResult(JSONObject(response.body().orEmpty()))
                val resultDb = NetworkAsteroidContainer(result.map { asteroid ->
                    asteroid.asNetworkModel()
                })
                database.asteroidDao.insertAll(*resultDb.asDatabaseModel())
            }
            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.e("MainViewModel", "Failure: " + t.message)
            }
        })
        }
    }
}