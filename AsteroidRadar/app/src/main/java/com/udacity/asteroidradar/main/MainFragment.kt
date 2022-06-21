package com.udacity.asteroidradar.main

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.databinding.FragmentMainBinding
import com.udacity.asteroidradar.main.adapter.AsteroidAdapter
import com.udacity.asteroidradar.main.adapter.AsteroidListener

class MainFragment : Fragment() {

    private val viewModel: MainViewModel by lazy {
        val activity = requireNotNull(this.activity)
        ViewModelProvider(this, MainViewModel.Factory(activity.application)).get(MainViewModel::class.java)
    }

    private var asteroidAdapter: AsteroidAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.asteroids.observe(viewLifecycleOwner, Observer<List<Asteroid>> { asteroids ->
            asteroids?.apply {
                asteroidAdapter?.submitList(this)
            }
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding = FragmentMainBinding.inflate(inflater)
        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        asteroidAdapter = AsteroidAdapter(AsteroidListener { asteroid ->
            openDetailAsteroidPage(asteroid)
        })
/*
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

        */


        binding.asteroidRecycler.adapter = asteroidAdapter


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
