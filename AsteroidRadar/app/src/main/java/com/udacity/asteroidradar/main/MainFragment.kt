package com.udacity.asteroidradar.main

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.udacity.asteroidradar.models.Asteroid
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.databinding.FragmentMainBinding
import com.udacity.asteroidradar.main.adapter.AsteroidAdapter
import com.udacity.asteroidradar.main.adapter.AsteroidListener

class MainFragment : Fragment() {

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this).get(MainViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding = FragmentMainBinding.inflate(inflater)
        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        val adapter = AsteroidAdapter(AsteroidListener { asteroid ->
            openDetailAsteroidPage(asteroid)
        })

        // Dummy data
        val asteroidList = mutableListOf<Asteroid>(
            Asteroid(
                id = 0L,
                codename = "test",
                closeApproachDate = "08-02-2022",
                absoluteMagnitude = 0.0,
                estimatedDiameter = 0.0,
                relativeVelocity = 0.0,
                distanceFromEarth = 0.0,
                isPotentiallyHazardous = false
            ),
            Asteroid(
                id = 0L,
                codename = "test",
                closeApproachDate = "08-02-2022",
                absoluteMagnitude = 0.0,
                estimatedDiameter = 0.0,
                relativeVelocity = 0.0,
                distanceFromEarth = 0.0,
                isPotentiallyHazardous = false
            ),
            Asteroid(
                id = 0L,
                codename = "test",
                closeApproachDate = "08-02-2022",
                absoluteMagnitude = 0.0,
                estimatedDiameter = 0.0,
                relativeVelocity = 0.0,
                distanceFromEarth = 0.0,
                isPotentiallyHazardous = false
            ),
            Asteroid(
                id = 0L,
                codename = "test",
                closeApproachDate = "08-02-2022",
                absoluteMagnitude = 0.0,
                estimatedDiameter = 0.0,
                relativeVelocity = 0.0,
                distanceFromEarth = 0.0,
                isPotentiallyHazardous = true
            )
        )

        adapter.submitList(asteroidList)
        
        binding.asteroidRecycler.adapter = adapter


        setHasOptionsMenu(true)

        return binding.root
    }

    private fun openDetailAsteroidPage(asteroid: Asteroid) {
        findNavController().navigate(MainFragmentDirections.actionShowDetail(asteroid))
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return true
    }
}
