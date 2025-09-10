package com.dmss.burbankapp.ui.mydisplay.regions

import android.os.Bundle
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
import com.dmss.burbankapp.data.model.DisplayRegionResponseModel
import com.dmss.burbankapp.data.model.HouseDetailsByHouseType
import com.dmss.burbankapp.databinding.FragmentRegionDetailBinding
import com.dmss.burbankapp.ui.mydisplay.DisplayHomesViewModel
import com.dmss.burbankapp.ui.mydisplay.DisplayToolbarViewModel
import com.dmss.burbankapp.ui.mydisplay.nearby.CalendarFragment
import com.dmss.burbankapp.ui.mydisplay.nearby.NearbyBottomSheetFragment
import com.dmss.burbankapp.ui.view.CustomProgressDialog
import com.dmss.burbankapp.utils.AppConstants
import com.dmss.burbankapp.utils.AppUtils
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.gson.JsonObject
import common.AppController
import timber.log.Timber
import java.util.*


class RegionDetailFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener,
    NearbyBottomSheetFragment.IDirection {
    lateinit var displayHomeViewModel: DisplayHomesViewModel
    private var customProgressDialog: CustomProgressDialog? = null
    lateinit var mPreferences: CustomSharedPreferences
    private var locationArrayList: ArrayList<LatLng> = ArrayList()
    lateinit var gMap: GoogleMap
    private var locationsList: ArrayList<DisplayRegionModel> = ArrayList()
    var mapFragment: SupportMapFragment? = null
    lateinit var model: DisplayRegionModel
    lateinit var viewModel: DisplayToolbarViewModel

    lateinit var selectedModel: DisplayRegionModel

    private var regionName: String? = null
    private lateinit var binding: FragmentRegionDetailBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            regionName = it.getString(REGION)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegionDetailBinding.inflate(layoutInflater)

        return binding.root
    }

    companion object {
        var REGION = "REGION"

        @JvmStatic
        fun newInstance(region: String) =
            RegionDetailFragment().apply {
                arguments = Bundle().apply {
                    putString(REGION, region)
                }
            }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initViewModel()
    }

    private fun initViewModel() {
        displayHomeViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(
                ApiHelperImpl(RetrofitBuilder.apiService),
                DatabaseHelperImpl(DatabaseBuilder.getInstance(requireContext()))
            )
        ).get(DisplayHomesViewModel::class.java)
        val headertext=getString(R.string.Choose_a_display_from_the)
        viewModel.updateToolbarValue(headertext+" "+ AppConstants.SELECTED_REGION_DISPLAY+".")
        subscribers()
        getRegions()
    }

    private fun getRegions() {

        var jsonObject = JsonObject()
        mPreferences.getStateID()
        jsonObject.addProperty("UserId", mPreferences.getUserId())
        jsonObject.addProperty("StateId", mPreferences.getStateID())
        jsonObject.addProperty("RegionName", AppConstants.SELECTED_REGION_DISPLAY)
        jsonObject.addProperty("PopularFlag", true)
        displayHomeViewModel.getDisplayForRegionAndMap(jsonObject)


    }

    private fun subscribers() {
        displayHomeViewModel.getDisplayForRegionAndMap().observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    customProgressDialog?.dismissProgress()
                    var regionsData: DisplayRegionResponseModel? = it?.data

                    if (regionsData != null) {
                        locationArrayList.clear()
                        locationsList.clear()
                        if (regionsData.displaysByRegionList.isNotEmpty() && regionsData.displaysByRegionList.size > 0) {
                            for (model in regionsData.displaysByRegionList) {
                                for (model in regionsData.displaysByRegionList) {
                                    var latitude: String? = null
                                    var longitude: String? = null
                                    latitude = if (model.latitude?.contains(",") == true) {
                                        model.latitude?.replace(",", "")?.trim()
                                    } else {
                                        model.latitude
                                    }
                                    longitude = if (model.longitude?.contains(",") == true) {
                                        model.longitude!!.replace(",", "").trim()
                                    } else {
                                        model.longitude
                                    }

                                    latitude?.let { lat ->
                                        longitude?.let { lon ->
                                            locationsList.add(model)
                                            locationArrayList.add(
                                                LatLng(
                                                    lat.toDouble(),
                                                    lon.toDouble()
                                                )
                                            )
                                        }
                                    }


                                }

                            }
                        }
                        mapFragment?.getMapAsync(this)
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

    private fun initView() {
        // todo change this line
        /*(parentFragment as MyDisplayHomeFragment).toolBinding.tvTool.text =f
            "Choose a display from the ${AppConstants.SELECTED_REGION_DISPLAY}"*/
        viewModel = ViewModelProviders.of(requireActivity()).get(
            DisplayToolbarViewModel::class.java)

        AppConstants.BACKSTACK_COUNT = 0
        mPreferences = CustomSharedPreferences(AppController.getInstance())
        customProgressDialog = CustomProgressDialog(requireContext())

        mapFragment =
            childFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment


        binding.tvRegionr.text = AppConstants.SELECTED_REGION_DISPLAY

        binding.llBack.setOnClickListener {
            activity?.onBackPressed()
        }

    }

    override fun onMapReady(googleMap: GoogleMap?) {
        googleMap ?: return
        gMap = googleMap
        gMap.setOnMarkerClickListener(this)
        gMap.uiSettings.isZoomControlsEnabled = true
        //gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(SYDNEY, 10f))

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
                val bounds = LatLngBounds.Builder().include(LatLng(maxLat, maxLon))
                    .include(LatLng(minLat, minLon)).build()
                gMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50))
                //TODO change ZOOM Value

            }
        }

    }

    override fun onMarkerClick(marker: Marker?): Boolean {
        var latLng: LatLng? = marker?.position
        Timber.e("Marker ----${latLng?.latitude}")
        var isValue: Boolean = false
        if (locationsList.isNotEmpty() && locationsList.size > 0) {
            for (displayRegionModel in locationsList) {
                selectedModel = displayRegionModel
                latLng?.latitude?.let { latitude ->
                    latLng.longitude.let { longitude ->
                        displayRegionModel.latitude?.let { apiLatitude ->
                            var newLatitude = apiLatitude
                            if (newLatitude.contains(",")) {
                                Timber.e("Comma found")
                                newLatitude = newLatitude.replace(",", "").trim()
                            }

                            displayRegionModel.longitude?.let { apiLongitude ->

                                var newLongitude = apiLongitude
                                if (newLongitude.contains(",")) {
                                    Timber.e("Comma found")
                                    newLongitude = newLongitude.replace(",", "").trim()
                                }
                                if (latitude == newLatitude.toDouble() && longitude == newLongitude.toDouble()) {
                                    isValue = true
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

    private fun showEditDialog(estateName: String, street: String, subrub: String) {
        viewModel.updateToolbarValue("${estateName.uppercase()}, $street")

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

    override fun showDirection(houseList: ArrayList<HouseDetailsByHouseType>) {
        selectedModel.latitude?.let { latitude ->
            selectedModel.longitude?.let { longitude ->
                AppUtils.navigateToGoogleMaps(
                    lat = latitude.toDouble(),
                    longitude.toDouble(),
                    requireContext()
                )
            }
        }
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

    private fun showBottomLayoutView(displayRegionModel: DisplayRegionModel) {
        // todo change this line
        /*(parentFragment as MyDisplayHomeFragment).toolBinding.tvTool.text =
            (displayRegionModel.estateName.uppercase(Locale.getDefault()) + ", " + displayRegionModel.LotStreet1)*/
        displayRegionModel.Id?.let { mPreferences.saveDisplayId(displayId = it) }
        binding.llBack.visibility = View.VISIBLE
        binding.bottomContainer.visibility = View.VISIBLE
        binding.tvEstateName.text = displayRegionModel.estateName
        binding.tvStreetSubrub.text =
            ("${displayRegionModel.LotStreet1},\n${displayRegionModel.lotSuburb}")
        model = displayRegionModel
        binding.bottomContainer.setOnClickListener {
            showEditDialog(
                displayRegionModel.estateName,
                displayRegionModel.LotStreet1,
                displayRegionModel.lotSuburb
            )
        }
    }

    private fun showBottomView(bottomView: Boolean) {
        if (bottomView) {
            Timber.e("bottom View $bottomView")
            if (this::model.isInitialized) {
                Timber.e("bottom View ${model.lotSuburb}")
                showBottomLayoutView(model)
            }
        }


    }


}