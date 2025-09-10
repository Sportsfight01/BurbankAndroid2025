package com.dmss.burbankapp.ui.mydisplay.nearby

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.dmss.burbankapp.R
import com.dmss.burbankapp.data.api.ApiHelperImpl
import com.dmss.burbankapp.data.api.RetrofitBuilder
import com.dmss.burbankapp.data.apiUtils.Status
import com.dmss.burbankapp.data.apiUtils.ViewModelFactory
import com.dmss.burbankapp.data.local.CustomSharedPreferences
import com.dmss.burbankapp.data.local.entity.DatabaseBuilder
import com.dmss.burbankapp.data.local.entity.DatabaseHelperImpl
import com.dmss.burbankapp.data.model.DisplayRegionModel
import com.dmss.burbankapp.data.model.HouseDetailsByHouseType
import com.dmss.burbankapp.data.model.NearByDisplaysResponseModel
import com.dmss.burbankapp.data.model.NearByPlaceDataObject
import com.dmss.burbankapp.databinding.FragmentNearByMapBinding
import com.dmss.burbankapp.ui.mydisplay.DisplayHomesViewModel
import com.dmss.burbankapp.ui.mydisplay.DisplayToolbarViewModel
import com.dmss.burbankapp.ui.mydisplay.MyDisplayHomeFragment
import com.dmss.burbankapp.ui.view.CustomProgressDialog
import com.dmss.burbankapp.utils.AppConstants
import com.dmss.burbankapp.utils.AppUtils
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import timber.log.Timber
import java.util.*
import kotlin.collections.ArrayList


class NearByMapFragment() : Fragment(), OnMapReadyCallback,
    NearbyBottomSheetFragment.IDirection, GoogleMap.OnMarkerClickListener {
    lateinit var myPreference: CustomSharedPreferences
    lateinit var selectedModel: DisplayRegionModel

    lateinit var displayHomeViewModel: DisplayHomesViewModel
    private var customProgressDialog: CustomProgressDialog? = null

    private var locationArrayList: ArrayList<LatLng> = ArrayList()
    private var locationsList: ArrayList<DisplayRegionModel> = ArrayList()

    lateinit var gMap: GoogleMap
    lateinit var binding: FragmentNearByMapBinding
    lateinit var viewModel:DisplayToolbarViewModel
    var estateLocationLatLng: LatLng? = null
    var mapFragment: SupportMapFragment? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        parent: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNearByMapBinding.inflate(layoutInflater, parent, false)
        return binding.root

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mapFragment =
            childFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment

        locationPermission()

        //showEditDialog()
        initViews()
        initViewModel()

    }

    private fun initViewModel() {
        Timber.e("DisplayHomes:---->")
        displayHomeViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(
                ApiHelperImpl(RetrofitBuilder.apiService),
                DatabaseHelperImpl(DatabaseBuilder.getInstance(requireContext()))
            )
        ).get(DisplayHomesViewModel::class.java)
        subscribers()


        fetchNearByPlaces()
    }
    private fun locationPermission() {
        Dexter.withActivity(activity)
            .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse) {
                    getCurrentLocation()
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse) {
                    // check for permanent denial of permission
                    if (response.isPermanentlyDenied) {
                        // navigate user to app settings
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest?,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            }).check()
    }

    fun getCurrentLocation() {
        var locationRequest = LocationRequest()
        locationRequest.interval = 10000
        locationRequest.fastestInterval = 3000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        var locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                if (locationResult.locations.isNotEmpty()) {
                    // get latest location
                    val location =
                        locationResult.lastLocation
                    AppConstants.currentLatitude = location.latitude.toString()
                    AppConstants.currentLongitude = location.longitude.toString()

                }


            }
        }
        LocationServices.getFusedLocationProviderClient(activity!!)
            .requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())


    }
    private fun fetchNearByPlaces() {
        var jsonObject = NearByPlaceDataObject(
            myPreference.getStateID(),
            AppConstants.currentLatitude,
            AppConstants.currentLatitude,
            myPreference.getUserId()
        )
        displayHomeViewModel.fetchNearByPlacesApi(jsonObject)
    }

    private fun subscribers() {
        displayHomeViewModel.getNearByPlacesLiveData().observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    customProgressDialog?.dismissProgress()

                    if (it.data != null) {
                        if (it.data.status) {

                            var totalResponse: NearByDisplaysResponseModel = it.data
                            locationArrayList = ArrayList()
                            locationsList = ArrayList()

                            if (totalResponse.regionsList.isNotEmpty() && totalResponse.regionsList.size > 0) {
                                for (displayRegionModel in totalResponse.regionsList) {
                                    var latitude: String? = null
                                    var longitude: String? = null
                                    latitude =
                                        if (displayRegionModel.latitude?.contains(",") == true) {
                                            displayRegionModel.latitude?.replace(",", "")?.trim()
                                        } else {
                                            displayRegionModel.latitude
                                        }
                                    longitude =
                                        if (displayRegionModel.longitude?.contains(",") == true) {
                                            displayRegionModel.longitude!!.replace(",", "").trim()
                                        } else {
                                            displayRegionModel.longitude
                                        }

                                    latitude?.let { lat ->
                                        longitude?.let { lon ->
                                            locationsList.add(displayRegionModel)
                                            locationArrayList.add(
                                                LatLng(
                                                    lat.toDouble(),
                                                    lon.toDouble()
                                                )
                                            )
                                        }
                                    }

                                    /* locationsList.add(displayRegionModel)
                                     locationArrayList.add(
                                         LatLng(
                                             displayRegionModel.latitude,
                                             displayRegionModel.longitude
                                         )
                                     )*/

                                }

                            }
                            mapFragment?.getMapAsync(this)

                            if (locationsList.size == 1) {
                                var model: DisplayRegionModel = locationsList[0]
                                showBottomLayoutView(model)
                            }


                        }
                    }
                }
                Status.LOADING -> {
                    customProgressDialog?.showProgress()
                }
                Status.ERROR -> {
                    customProgressDialog?.dismissProgress()
                }
            }

        })
    }

    private fun initViews() {
        // todo change this line
        /*(parentFragment as MyDisplayHomeFragment).toolBinding.tvTool.text =
            "Check out the displays that are\nnear you right now"*/
        customProgressDialog = CustomProgressDialog(requireContext())
        myPreference = CustomSharedPreferences(requireContext())
        viewModel = ViewModelProviders.of(requireActivity()).get(
            DisplayToolbarViewModel::class.java)
    }

    private fun showEditDialog(estateName: String, street: String, subrub: String) {
        Timber.e("Estate Name $street $subrub")
        val headerText = estateName.uppercase(Locale.getDefault()) + "," + street
        viewModel.updateToolbarValue("${estateName.uppercase()}, $street")

        // todo change this line
        //(parentFragment as MyDisplayHomeFragment).toolBinding.tvTool.text = headerText
        val fm: FragmentManager = requireActivity().supportFragmentManager
        var fragment = NearbyBottomSheetFragment(this, this::showBottomView, true).newInstance(
            estateName,
            street,
            subrub
        );
        if (fragment != null) {
            if (!fragment.isAdded) {
                fragment.show(fm, "fragment_edit_name")
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        googleMap ?: return
        gMap = googleMap
        gMap.setOnMarkerClickListener(this)
        gMap.uiSettings.isZoomControlsEnabled = true

        if (locationArrayList.size > 0) {
            for (LatLng in locationArrayList) {
                gMap.addMarker(
                    MarkerOptions().position(LatLng)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_display_marker))
                )
                var minLat = Int.MAX_VALUE.toDouble()
                var maxLat = Int.MIN_VALUE.toDouble()
                var minLon = Int.MAX_VALUE.toDouble()
                var maxLon = Int.MIN_VALUE.toDouble()
                for (point in locationArrayList) {
                    maxLat = Math.max(point.latitude, maxLat)
                    minLat = Math.min(point.latitude, minLat)
                    maxLon = Math.max(point.longitude, maxLon)
                    minLon = Math.min(point.longitude, minLon)
                }
//                gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng, 7f))
                val bounds = LatLngBounds.Builder().include(LatLng(maxLat, maxLon))
                    .include(LatLng(minLat, minLon)).build()
                gMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50))
            }
        }

    }


    private fun changeCurrentLocationMarker(currentLatLng: LatLng?) {
        gMap.clear()
        if (locationArrayList.size > 0) {
            for (latLng in locationArrayList) {

                Timber.e("LatLong ------:${latLng}")
                if (latLng == currentLatLng) {
                    gMap.addMarker(
                        MarkerOptions().position(currentLatLng)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.location))
                    )
                    gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 7f))
                } else {
                    gMap.addMarker(
                        MarkerOptions().position(latLng)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_display_marker))
                    )
                }


            }
        }

    }

    override fun onMarkerClick(marker: Marker?): Boolean {
        var latLng: LatLng? = marker?.position
        changeCurrentLocationMarker(latLng)
        var isValue: Boolean = false
        if (locationsList.isNotEmpty() && locationsList.size > 0) {
            for (displayRegionModel in locationsList) {
                selectedModel = displayRegionModel
                latLng?.latitude?.let { latitude ->
                    latLng.longitude.let { longitude ->
                        displayRegionModel.latitude?.let { apiLatitude ->
                            displayRegionModel.longitude?.let { apiLongitude ->
                                if (latitude == apiLatitude.toDouble() && longitude == apiLongitude.toDouble()) {
                                    isValue = true
                                    viewModel.updateToolbarValue(displayRegionModel.estateName)

                                    showEditDialog(
                                        displayRegionModel.estateName,
                                        displayRegionModel.LotStreet1,
                                        displayRegionModel.lotSuburb
                                    )
                                    showBottomLayoutView(displayRegionModel)

                                } else {
                                    isValue = false
                                }
                            }

                        }


                    }
                }
                if (isValue)
                    break
            }
        }


        return false
    }

    private fun showBottomLayoutView(displayRegionModel: DisplayRegionModel) {
        displayRegionModel.Id?.let {
            myPreference.saveDisplayId(it)
        }
        // todo change this line
        /*(parentFragment as MyDisplayHomeFragment).toolBinding.tvTool.text =
            (displayRegionModel.estateName.uppercase(Locale.getDefault()) + ", " + displayRegionModel.LotStreet1)
*/
        binding.bottomContainer.visibility = View.VISIBLE
        binding.tvEstateName.text = displayRegionModel.estateName
        binding.tvStreetSubrub.text =
            ("${displayRegionModel.LotStreet1},\n" +
                    "${displayRegionModel.lotSuburb}")
        binding.bottomContainer.setOnClickListener {

            showEditDialog(
                displayRegionModel.estateName,
                displayRegionModel.LotStreet1,
                displayRegionModel.lotSuburb
            )
        }
    }

    override fun showDirection(houseList: ArrayList<HouseDetailsByHouseType>) {
        if (houseList.size >0) {
            estateLocationLatLng =
                LatLng(houseList[0].Latitude.toDouble(), houseList[0].Longitude.toDouble())
            if (estateLocationLatLng != null) {
                AppUtils.navigateToGoogleMaps(
                    estateLocationLatLng!!.latitude,
                    estateLocationLatLng!!.longitude,
                    requireContext()
                )
            }
        }

    }

    private fun isGmsAvailable(context: Context?): Boolean {
        var isAvailable = false
        if (null != context) {
            val result: Int =
                GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context)
            isAvailable = com.google.android.gms.common.ConnectionResult.SUCCESS == result
        }
        return isAvailable
    }

    private fun navigateToGoogleMaps(lat: Double, lon: Double) {
        val gmmIntentUri = Uri.parse("geo:$lat,$lon?q=$lat,$lon")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        context?.startActivity(mapIntent)
        /*if (context?.let { mapIntent.resolveActivity(it.packageManager) } != null) {
            context?.startActivity(mapIntent)
        }*/
    }

    override fun bookAppointment(houseName: String, houseSize: String) {
        if (this::selectedModel.isInitialized) {
            var bundle = Bundle()
            bundle.putString("estateName", selectedModel.estateName)
            bundle.putString("street", selectedModel.LotStreet1)
            bundle.putString("suburb", selectedModel.lotSuburb)
            bundle.putString("houseName", houseName)
            bundle.putString("houseSize", houseSize)
            loadFragment(CalendarFragment(), bundle)
        }


    }

    private fun showBottomView(bottomView: Boolean) {


    }

    fun loadFragment(fragment: Fragment, bundle: Bundle) {
        // load fragment
        val transaction: FragmentTransaction = parentFragmentManager.beginTransaction()
        fragment.arguments = bundle
        if (fragment.isAdded) {
            return
        }
        transaction.add(R.id.fragment_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()


    }


}