package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter
import com.example.android.politicalpreparedness.election.adapter.ElectionListener
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.repository.ElectionRepository

class ElectionsFragment: Fragment() {

    private lateinit var binding: FragmentElectionBinding

    //TODO: Declare ViewModel
    private lateinit var viewModel: ElectionsViewModel

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_election, container, false)

        //TODO: Add ViewModel values and create ViewModel
        val application = requireNotNull(this.activity).application
        val dataSource = ElectionRepository(
            CivicsApi.retrofitService,
            ElectionDatabase.getInstance(application).electionDao)
        val viewModelFactory = ElectionsViewModelFactory(dataSource, application)

        viewModel = ViewModelProvider(this, viewModelFactory)[ElectionsViewModel::class.java]

        //TODO: Add binding values
        binding.electionViewModel = viewModel
        binding.lifecycleOwner = this

        //TODO: Link elections to voter info
        viewModel.navigateToVoterInfo.observe(viewLifecycleOwner, Observer { election ->
            election?.let {
                this.findNavController().navigate(
                    ElectionsFragmentDirections.actionElectionsFragmentToVoterInfoFragment(
                        election.id, election.division
                    )
                )
                viewModel.doneNavigating()
            }
        })


        //TODO: Initiate recycler adapters
        val upComingElectionAdapter = ElectionListAdapter(ElectionListener { election ->
            viewModel.onElectionClicked(election)
        })
        binding.upcomingElections.adapter = upComingElectionAdapter

        val savedElectionAdapter = ElectionListAdapter(ElectionListener { election ->
            viewModel.onElectionClicked(election)
        })
        binding.savedElections.adapter = savedElectionAdapter

        //TODO: Populate recycler adapters
        viewModel.upComingElections.observe(viewLifecycleOwner, Observer { elections ->
            elections?.let {
                upComingElectionAdapter.addHeaderAndSubmitList(
                    getString(R.string.upcoming_elections),
                    it
                )
            }
        })

        viewModel.savedElections.observe(viewLifecycleOwner, Observer { elections ->
            elections?.let {
                savedElectionAdapter.addHeaderAndSubmitList(
                    getString(R.string.saved_elections),
                    it
                )
            }
        })

        refreshElections()

        return binding.root
    }

    //TODO: Refresh adapters when fragment loads
    private fun refreshElections() {
        viewModel.getUpComingElections()
    }

}