package com.udacity.asteroidradar.main.adapter

import com.udacity.asteroidradar.Asteroid

class AsteroidListener(val clickListener: (asteroidId: Long) -> Unit) {
    fun onClick(asteroid: Asteroid) = clickListener(asteroid.id)
}
