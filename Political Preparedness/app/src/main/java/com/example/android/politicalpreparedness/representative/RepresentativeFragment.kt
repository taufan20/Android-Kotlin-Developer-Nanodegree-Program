package com.example.android.politicalpreparedness.representative

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.databinding.FragmentRepresentativeBinding
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.repository.ElectionRepository
import com.example.android.politicalpreparedness.representative.adapter.RepresentativeListAdapter
import com.example.android.politicalpreparedness.representative.model.Representative
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.util.Locale


private const val KEY_MOTION_LAYOUT_STATE = "KEY_MOTION_LAYOUT_STATE"
private const val KEY_ADDRESS_1 = "KEY_ADDRESS_1"
private const val KEY_ADDRESS_2 = "KEY_ADDRESS_2"
private const val KEY_CITY = "KEY_CITY"
private const val KEY_STATE = "KEY_STATE"
private const val KEY_ZIP = "KEY_ZIP"
private const val KEY_REPRESENTATIVES = "KEY_REPRESENTATIVES"

class DetailFragment : Fragment() {

    private var representativeList: ArrayList<Representative> = arrayListOf()
    private lateinit var binding: FragmentRepresentativeBinding

    private lateinit var address: Address

    companion object {
        //TODO: Add Constant for Location request
        const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 0
    }

    private lateinit var fusedLocationProviderCLient: FusedLocationProviderClient

    //TODO: Declare ViewModel
    private lateinit var viewModel: RepresentativeViewModel

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        //TODO: Establish bindings
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_representative, container, false)

        val application = requireNotNull(this.activity).application
        val dataSource = ElectionRepository(
            CivicsApi.retrofitService,
            ElectionDatabase.getInstance(application).electionDao)
        val viewModelFactory = RepresentativeViewModelFactory(dataSource, application)

        viewModel = ViewModelProvider(this, viewModelFactory)[RepresentativeViewModel::class.java]

        fusedLocationProviderCLient = LocationServices.getFusedLocationProviderClient(requireActivity())

        //TODO: Add binding values
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.executePendingBindings()


        //TODO: Define and assign Representative adapter
        val representativeAdapter = RepresentativeListAdapter()
        binding.representatives.adapter = representativeAdapter

        //TODO: Populate Representative adapter
        viewModel.representativeList.observe(viewLifecycleOwner, Observer { representatives ->
            representativeList = representatives
            representativeAdapter.submitList(representatives)

        })

        viewModel.showErrorMessage.observe(viewLifecycleOwner, Observer { message ->
            binding.errorText.apply {
                visibility = if (message.isEmpty()) {
                    View.GONE
                } else {
                    View.VISIBLE
                }
                text = message
            }
        })

        //TODO: Establish button listeners for field and location search
        binding.buttonSearch.setOnClickListener {
            hideKeyboard()
            viewModel.onSearchMyRepresentativesClicked()
        }

        binding.buttonLocation.setOnClickListener {
            hideKeyboard()
            checkLocationPermissions()
        }

        binding.stateSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
                viewModel.address.value?.state = binding.stateSpinner.selectedItem as String
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                viewModel.address.value?.state = binding.stateSpinner.selectedItem as String
            }
        }

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {

        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            val motionLayoutState = savedInstanceState?.getInt(KEY_MOTION_LAYOUT_STATE)
            motionLayoutState?.let { binding.representativeMotionLayout.transitionToState(it) }
        }

        address = Address(
            line1 = savedInstanceState?.getString(KEY_ADDRESS_1).orEmpty(),
            line2 = savedInstanceState?.getString(KEY_ADDRESS_2).orEmpty(),
            city = savedInstanceState?.getString(KEY_CITY).orEmpty(),
            state = savedInstanceState?.getString(KEY_STATE).orEmpty(),
            zip = savedInstanceState?.getString(KEY_ZIP).orEmpty()
        )

        representativeList = savedInstanceState?.getParcelableArrayList(KEY_REPRESENTATIVES) ?: ArrayList()

        viewModel.setRepresentatives(representativeList)
        viewModel.setAddress(address)

        super.onActivityCreated(savedInstanceState)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        //TODO: Handle location permission result to get location on permission granted

        if (requestCode != PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            return
        }

        if (isPermissionGranted()) {
            getLocation()
        } else {
            // Show error
        }
    }

    private fun checkLocationPermissions() {
        return if (isPermissionGranted()) {
            getLocation()
        } else {
            //TODO: Request Location permissions
            requestLocationPermission()
        }
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
        )
    }

    private fun isPermissionGranted() : Boolean {
        //TODO: Check if permission is already granted and return (true = granted, false = denied/other)
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        //TODO: Get location from LocationServices
        //TODO: The geoCodeLocation method is a helper function to change the lat/long location to a human readable street address
        fusedLocationProviderCLient.lastLocation.addOnSuccessListener { location ->
            location?.let {
                val address = geoCodeLocation(location)
                viewModel.onUseMyLocationClicked(address)
            }
        }
    }

    private fun geoCodeLocation(location: Location): Address {
        val geocoder = Geocoder(context, Locale.getDefault())
        return geocoder.getFromLocation(location.latitude, location.longitude, 1)
                .map { address ->
                    Address(address.thoroughfare.orEmpty(),
                        address.subThoroughfare.orEmpty(),
                        address.locality.orEmpty(),
                        address.adminArea.orEmpty(),
                        address.postalCode.orEmpty()
                    )
                }
                .first()
    }

    private fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
    }

    override fun onSaveInstanceState(outState: Bundle) {

        with (binding) {
            outState.apply {
                putString(KEY_ADDRESS_1, addressLine1.text.toString())
                putString(KEY_ADDRESS_2, addressLine2.text.toString())
                putString(KEY_CITY, city.text.toString())
                putString(KEY_STATE, stateSpinner.selectedItem.toString())
                putString(KEY_ZIP, zip.text.toString())
                if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                    putInt(KEY_MOTION_LAYOUT_STATE, representativeMotionLayout.currentState)
                }

                putParcelableArrayList(KEY_REPRESENTATIVES, representativeList)
            }
        }

        super.onSaveInstanceState(outState)

    }



}