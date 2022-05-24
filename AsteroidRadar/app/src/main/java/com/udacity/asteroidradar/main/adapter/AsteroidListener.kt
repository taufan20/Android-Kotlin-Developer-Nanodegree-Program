package com.udacity.asteroidradar.main.adapter

import com.udacity.asteroidradar.models.Asteroid

class AsteroidListener(val clickListener: (asteroid: Asteroid) -> Unit) {
    fun onClick(asteroid: Asteroid) = clickListener(asteroid)
}
