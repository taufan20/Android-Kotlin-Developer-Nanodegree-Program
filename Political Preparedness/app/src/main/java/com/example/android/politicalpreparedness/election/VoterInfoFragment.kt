package com.example.android.politicalpreparedness.election

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat.startActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.databinding.FragmentVoterInfoBinding
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.repository.ElectionRepository

class VoterInfoFragment : Fragment() {

    private lateinit var binding: FragmentVoterInfoBinding

    private lateinit var viewModel: VoterInfoViewModel

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_voter_info, container, false)

        val election = navArgs<VoterInfoFragmentArgs>().value.argElection

        //TODO: Add ViewModel values and create ViewModel
        val application = requireNotNull(this.activity).application
        val dataSource = ElectionRepository(
            CivicsApi.retrofitService,
            ElectionDatabase.getInstance(application).electionDao)
        val viewModelFactory = VoterInfoViewModelFactory(dataSource, application, election)

        viewModel = ViewModelProvider(this, viewModelFactory) [VoterInfoViewModel::class.java]

        //TODO: Add binding values
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        //TODO: Populate voter info -- hide views without provided data.
        /**
        Hint: You will need to ensure proper data is provided from previous fragment.
        */

        viewModel.navigateToWebBrowser.observe(viewLifecycleOwner, Observer { url ->

            url?.let {
                openWebBrowser(url)
                viewModel.onNavigatingToBrowserDone()
            }

        })

        //TODO: Handle loading of URLs

        //TODO: Handle save button UI state
        //TODO: cont'd Handle save button clicks

        viewModel.savedElection.observe(viewLifecycleOwner, Observer {
            binding.followButton.text = if (it == null) {
                getString(R.string.follow_election)
            } else {
                getString(R.string.unfollow_election)
            }
        })

        return binding.root
    }


    //TODO: Create method to load URL intents

    private fun openWebBrowser(url: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        startActivity(intent)
    }
}