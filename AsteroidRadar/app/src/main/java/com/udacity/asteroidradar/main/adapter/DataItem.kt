package com.udacity.asteroidradar.main.adapter

import com.udacity.asteroidradar.models.Asteroid

sealed class DataItem {

    data class AsteroidItem(val asteroid: Asteroid): DataItem() {
        override val id = asteroid.id
    }

    abstract val id: Long

}
