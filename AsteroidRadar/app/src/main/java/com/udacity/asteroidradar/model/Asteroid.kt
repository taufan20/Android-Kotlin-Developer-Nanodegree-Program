package com.udacity.asteroidradar.model

import android.os.Parcelable
import com.udacity.asteroidradar.database.DatabaseAsteroid
import com.udacity.asteroidradar.network.NetworkAsteroid
import com.udacity.asteroidradar.network.NetworkAsteroidContainer
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Asteroid(val id: Long, val codename: String, val closeApproachDate: String,
                    val absoluteMagnitude: Double, val estimatedDiameter: Double,
                    val relativeVelocity: Double, val distanceFromEarth: Double,
                    val isPotentiallyHazardous: Boolean) : Parcelable


fun Asteroid.asNetworkModel(): NetworkAsteroid {
    return NetworkAsteroid(
            id = id,
            codename = codename,
            closeApproachDate = closeApproachDate,
            absoluteMagnitude = absoluteMagnitude,
            estimatedDiameter = estimatedDiameter,
            relativeVelocity = relativeVelocity,
            distanceFromEarth = distanceFromEarth,
            isPotentiallyHazardous = isPotentiallyHazardous
        )
}
