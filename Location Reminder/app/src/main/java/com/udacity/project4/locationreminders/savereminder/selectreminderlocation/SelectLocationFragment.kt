package com.udacity.project4.locationreminders.savereminder.selectreminderlocation


import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.snackbar.Snackbar
import com.udacity.project4.BuildConfig
import com.udacity.project4.R
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.databinding.FragmentSelectLocationBinding
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.utils.setDisplayHomeAsUpEnabled
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.*

private const val REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE = 34
private const val REQUEST_TURN_DEVICE_LOCATION_ON = 29
class SelectLocationFragment : BaseFragment(), OnMapReadyCallback, GoogleMap.OnMapClickListener {

    private val TAG = SelectLocationFragment::class.java.simpleName

    override val _viewModel: SaveReminderViewModel by sharedViewModel()
    private lateinit var binding: FragmentSelectLocationBinding

    private var selectedPoi: PointOfInterest? = null
    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_select_location, container, false)

        binding.viewModel = _viewModel
        binding.lifecycleOwner = this

        setHasOptionsMenu(true)
        setDisplayHomeAsUpEnabled(true)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        binding.btnSelect.setOnClickListener {

            if (selectedPoi == null) {
                _viewModel.showErrorMessage.value = getString(R.string.select_poi)
            } else {
                onLocationSelected()
            }

        }

        return binding.root
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        setPoiClick(map)
        setMapStyle(map)
        requestForegroundLocationPermissions()
        map.setOnMapClickListener(this)
    }


    @SuppressLint("MissingPermission")
    private fun getMyLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        map.moveCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                LatLng(location.latitude, location.longitude),
                                16f
                            )
                        )
                    } else {
                        Log.e(TAG, "Could not obtain location")
                    }
                }.addOnFailureListener {
                    Log.e(TAG, "Could not obtain location", it)
                }
        } else {
            requestForegroundLocationPermissions()
        }

    }

    @TargetApi(29)
    private fun requestForegroundLocationPermissions() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                map.isMyLocationEnabled = true
                checkDeviceLocationSettings()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                showSnackbarToEnablePermissions()
            }
            else -> {
                requestPermissions(
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE
                )
            }
        }
    }


    /*
     *  Uses the Location Client to check the current state of location settings, and gives the user
     *  the opportunity to turn on location services within our app.
     */
    private fun checkDeviceLocationSettings(resolve:Boolean = true) {
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_LOW_POWER
        }
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val settingsClient = LocationServices.getSettingsClient(requireActivity())
        val locationSettingsResponseTask = settingsClient.checkLocationSettings(builder.build())
        locationSettingsResponseTask.addOnFailureListener { exception ->
            if (exception is ResolvableApiException && resolve) {
                startIntentSenderForResult(
                    exception.resolution.intentSender,
                    REQUEST_TURN_DEVICE_LOCATION_ON, null, 0, 0, 0, null
                )
            } else {
                showSnackbarToEnableDeviceLocation()
            }
        }

        locationSettingsResponseTask.addOnCompleteListener {
            if (it.isSuccessful) {
                getMyLastKnownLocation()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (grantResults.isNotEmpty() && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                requestForegroundLocationPermissions()
        } else {
            showSnackbarToOpenSettings()
        }

    }


    private fun openSettings() {
        startActivityForResult(Intent().apply {
            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }, REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                requestForegroundLocationPermissions()
            } else {
                showSnackbarToEnablePermissions()
            }
        } else if (requestCode == REQUEST_TURN_DEVICE_LOCATION_ON) {
            if (resultCode == Activity.RESULT_OK) {
                getMyLastKnownLocation()
            } else {
                showSnackbarToEnableDeviceLocation()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun showSnackbarToEnableDeviceLocation() {
        Snackbar.make(
            requireActivity().findViewById(android.R.id.content),
            R.string.location_required_error, Snackbar.LENGTH_INDEFINITE
        ).setAction(R.string.enable) {
            checkDeviceLocationSettings()
        }.show()
    }

    private fun showSnackbarToOpenSettings() {
        Snackbar.make(
            requireActivity().findViewById(android.R.id.content),
            R.string.permission_denied_explanation,
            Snackbar.LENGTH_INDEFINITE
        )
            .setAction(R.string.enable) {
                openSettings()
            }.show()
    }

    private fun showSnackbarToEnablePermissions() {
        Snackbar.make(
            requireActivity().findViewById(android.R.id.content),
            R.string.permission_denied_explanation,
            Snackbar.LENGTH_INDEFINITE
        )
            .setAction(R.string.enable) {
                requestPermissions(
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE
                )
            }.show()
    }

    private fun setPoiClick(map: GoogleMap) {
        map.setOnPoiClickListener { poi ->
            selectedPoi = poi
            val poiMarker = map.addMarker(
                MarkerOptions()
                    .position(poi.latLng)
                    .title(poi.name)
            )
            poiMarker?.showInfoWindow()
        }
    }


    private fun setMapStyle(map: GoogleMap) {
        try {
            val success = map.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    requireActivity(),
                    R.raw.map_style
                ))

            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (e: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", e)
        }
    }


    private fun onLocationSelected() {
        with (_viewModel) {
            selectedPOI.value = selectedPoi
            latitude.value = selectedPoi?.latLng?.latitude
            longitude.value = selectedPoi?.latLng?.longitude
            reminderSelectedLocationStr.value = selectedPoi?.name
            navigationCommand.value = NavigationCommand.Back
        }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.map_options, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        // TODO: Change the map type based on the user's selection.
        R.id.normal_map -> {
            map.mapType = GoogleMap.MAP_TYPE_NORMAL
            true
        }
        R.id.hybrid_map -> {
            map.mapType = GoogleMap.MAP_TYPE_HYBRID
            true
        }
        R.id.satellite_map -> {
            map.mapType = GoogleMap.MAP_TYPE_SATELLITE
            true
        }
        R.id.terrain_map -> {
            map.mapType = GoogleMap.MAP_TYPE_TERRAIN
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onMapClick(p0: LatLng) {

        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        val addresses = p0.let { geocoder.getFromLocation(it.latitude, p0.longitude, 1) }

        // text for place id unless and address is retrieved
        var placeId = getString(R.string.init_location)
        if (addresses != null && addresses.isNotEmpty()) {
            placeId = addresses[0].getAddressLine(0)
        }

        selectedPoi = PointOfInterest(p0, placeId, "No Name")
        _viewModel.selectedPOI.value = selectedPoi
    }

}
