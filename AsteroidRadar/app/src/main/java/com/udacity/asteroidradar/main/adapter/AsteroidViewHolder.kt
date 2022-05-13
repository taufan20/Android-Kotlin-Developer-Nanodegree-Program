package com.udacity.asteroidradar.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.databinding.ItemAsteroidBinding

class AsteroidViewHolder private constructor(val binding: ItemAsteroidBinding)
        : RecyclerView.ViewHolder(binding.root) {

    fun bind(clickListener: AsteroidListener, item: Asteroid) {
        /*binding.sleep = item
        binding.clickListener = clickListener
        binding.executePendingBindings()*/
    }

    companion object {
        fun from(parent: ViewGroup): AsteroidViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemAsteroidBinding.inflate(layoutInflater, parent, false)

            return AsteroidViewHolder(binding)
        }
    }

}