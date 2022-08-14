package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.databinding.FragmentVoterInfoBinding
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.repository.ElectionRepository

class VoterInfoFragment : Fragment() {

    private lateinit var binding: FragmentVoterInfoBinding

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_voter_info, container, false)

        //TODO: Add ViewModel values and create ViewModel
        val application = requireNotNull(this.activity).application
        val dataSource = ElectionRepository(
            CivicsApi.retrofitService,
            ElectionDatabase.getInstance(application).electionDao)
        val viewModelFactory = ElectionsViewModelFactory(dataSource, application)

        val viewModel = ViewModelProvider(this, viewModelFactory)
            .get(VoterInfoViewModel::class.java)

        //TODO: Add binding values
        binding.viewModel = viewModel

        //TODO: Populate voter info -- hide views without provided data.
        /**
        Hint: You will need to ensure proper data is provided from previous fragment.
        */


        //TODO: Handle loading of URLs

        //TODO: Handle save button UI state
        //TODO: cont'd Handle save button clicks

        return binding.root
    }

    //TODO: Create method to load URL intents

}