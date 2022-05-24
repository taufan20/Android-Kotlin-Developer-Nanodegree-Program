package com.udacity.asteroidradar.network

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.udacity.asteroidradar.models.Asteroid
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Deferred
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface AsteroidService {

    @GET("feed")
    fun getFeeds(
        @Query("start_date") startDate: String = "2015-09-07",
        @Query("end_date") endDate: String = "2015-09-08",
        @Query("api_key") apiKey: String = "C8zetbFUfqrviRosp5cmLCXkYmxr3xInx2DTMSYA"
    ): Deferred<List<Asteroid>>

}

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

object Network {

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.nasa.gov/neo/rest/v1/")
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()

    val asteroid = retrofit.create(AsteroidService::class.java)
}