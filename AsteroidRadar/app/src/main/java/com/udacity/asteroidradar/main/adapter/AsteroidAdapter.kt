package com.udacity.asteroidradar.main.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.udacity.asteroidradar.Asteroid

class AsteroidAdapter(val clickListener: AsteroidListener) : ListAdapter<Asteroid,
        AsteroidViewHolder>(AsteroidDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AsteroidViewHolder {
        return AsteroidViewHolder.from(parent)

    }

    override fun onBindViewHolder(holder: AsteroidViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(clickListener, item)

    }
}
