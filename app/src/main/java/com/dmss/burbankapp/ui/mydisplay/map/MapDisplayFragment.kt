package com.dmss.burbankapp.ui.mydisplay.map

import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
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
import com.dmss.burbankapp.data.model.HnLQuizPackageModel
import com.dmss.burbankapp.data.model.HouseDetailsByHouseType
import com.dmss.burbankapp.databinding.FragmentMapDisplayfragmentBinding
import com.dmss.burbankapp.ui.cluster.HnlClusterItem
import com.dmss.burbankapp.ui.cluster.MarkerClusterRenderer
import com.dmss.burbankapp.ui.cluster.MyClusterRenderer
import com.dmss.burbankapp.ui.mydisplay.DisplayHomesViewModel
import com.dmss.burbankapp.ui.mydisplay.DisplayToolbarViewModel
import com.dmss.burbankapp.ui.mydisplay.MyDisplayHomeFragment
import com.dmss.burbankapp.ui.mydisplay.nearby.CalendarFragment
import com.dmss.burbankapp.ui.mydisplay.nearby.NearbyBottomSheetFragment
import com.dmss.burbankapp.ui.view.CustomProgressDialog
import com.dmss.burbankapp.utils.AppUtils
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.gson.JsonObject
import com.google.maps.android.clustering.ClusterManager
import common.AppController
import timber.log.Timber
import java.util.*
import kotlin.collections.ArrayList

class MapDisplayFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener,
    NearbyBottomSheetFragment.IDirection,ClusterManager.OnClusterItemClickListener<HnlClusterItem>, MyClusterRenderer.ShowBottomSheetDialog {

    lateinit var binding: FragmentMapDisplayfragmentBinding

    lateinit var gMap: GoogleMap
    lateinit var selectedModel: DisplayRegionModel
    lateinit var viewModel: DisplayToolbarViewModel

    lateinit var displayHomeViewModel: DisplayHomesViewModel
    private var customProgressDialog: CustomProgressDialog? = null
    lateinit var mPreferences: CustomSharedPreferences
    private var locationArrayList: ArrayList<LatLng> = ArrayList()
    private var locationsList: ArrayList<DisplayRegionModel> = ArrayList()
    var mapFragment: SupportMapFragment? = null
    lateinit var model: DisplayRegionModel
    var clusterArrayList: ArrayList<HnlClusterItem> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMapDisplayfragmentBinding.inflate(layoutInflater, container, false)
        initView();
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    private fun initViewModel() {
        displayHomeViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(
                ApiHelperImpl(RetrofitBuilder.apiService),
                DatabaseHelperImpl(DatabaseBuilder.getInstance(requireContext()))
            )
        ).get(DisplayHomesViewModel::class.java)
        viewModel = ViewModelProviders.of(requireActivity()).get(
            DisplayToolbarViewModel::class.java)
        subscribers()
        getRegions()
    }

    private fun getRegions() {
        var jsonObject = JsonObject()
        mPreferences.getStateID()
        jsonObject.addProperty("UserId", mPreferences.getUserId())
        jsonObject.addProperty("StateId", mPreferences.getStateID())
        jsonObject.addProperty("RegionName", "")
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
                        clusterArrayList.clear()
                        clusterArrayList = ArrayList()
                        if (regionsData.displaysByRegionList.isNotEmpty() && regionsData.displaysByRegionList.size > 0) {
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
                                       var latLng= LatLng(
                                            lat.toDouble(),
                                            lon.toDouble()
                                        )
                                        locationArrayList.add(
                                            latLng
                                        )
                                       var hnLQuizPackageModel=HnLQuizPackageModel()
                                        var clusterItem =
                                            HnlClusterItem(hnLQuizPackageModel, "houseName", latLng)
                                        clusterItem?.let { itemCluster ->
                                            clusterArrayList.add(itemCluster)
                                        }
                                    }
                                }


                            }
                            getBoundaries()
//                            setUpClusterManager(gMap)
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
    override fun onClusterItemClick(item: HnlClusterItem?): Boolean {
        item?.let { hnlClusterItem ->
            /* var arrayList: ArrayList<HnlClusterItem> = ArrayList();
            arrayList.add(hnlClusterItem)*/
            onMarkerClickSheet(hnlClusterItem.position)
//            showBottomSheetFilter(arrayList)
            /*hnlClusterItem.hnLQuizPackageModel.let {
                val fragment = HomeLandFullScreenFragment.newInstance(it, false)
                loadFragment(fragment)
            }*/
        }
        return true
    }
    private fun setUpClusterManager(googleMap: GoogleMap) {
        val clusterManager: ClusterManager<HnlClusterItem> =
            ClusterManager(context, googleMap)

        googleMap.setOnMarkerClickListener(clusterManager);

        clusterManager.setOnClusterItemClickListener(this);
        val markerClusterRenderer =
            context?.let { MarkerClusterRenderer(it, googleMap, clusterManager) }

        var mColoredCircleBackground = ShapeDrawable(OvalShape())
        clusterManager.renderer =
            MyClusterRenderer(activity, googleMap, clusterManager, mColoredCircleBackground, this)
        val items: List<HnlClusterItem> = clusterArrayList


        clusterManager.addItems(items)
        clusterManager.cluster()
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(clusterArrayList[0].position,
            10f))
        if (locationArrayList.size > 0) {
            for (LatLng in locationArrayList) {
                gMap.addMarker(
                    MarkerOptions().position(LatLng)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_display_marker))
                )
            }
        }
    }
    private fun initView() {
        // todo change this line
        /*(parentFragment as MyDisplayHomeFragment).toolBinding.tvTool.text =
            "See one of our display homes"*/

        mPreferences = CustomSharedPreferences(AppController.getInstance())
        customProgressDialog = CustomProgressDialog(requireContext())
        mapFragment =
            childFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)


        binding.llBack.setOnClickListener {
            activity?.onBackPressed()
        }


    }
   private fun getBoundaries(){
       if (locationArrayList.size > 0) {
           for (LatLng in locationArrayList) {
               gMap.addMarker(
                   MarkerOptions().position(LatLng)
                       .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_display_marker))
               )
//               gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng, 8f))
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
           }
       }
   }
    override fun onMapReady(googleMap: GoogleMap?) {
        googleMap ?: return
        gMap = googleMap
        gMap.setOnMarkerClickListener(this)
        gMap.uiSettings.isZoomControlsEnabled = true
        //gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(SYDNEY, 10f))
        initViewModel()


    }

    private fun changeCurrentLocationMarker(currentLatLng: LatLng?) {
        gMap.clear()
        if (locationArrayList.size > 0) {
            for (latLng in locationArrayList) {
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
    fun onMarkerClickSheet(latLng1:LatLng): Boolean {
//        var latLng: LatLng? = marker?.position
                var latLng: LatLng? = latLng1
        changeCurrentLocationMarker(latLng)
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

    private fun showBottomLayoutView(displayRegionModel: DisplayRegionModel) {
        displayRegionModel.Id?.let { mPreferences.saveDisplayId(displayId = it) }
        // todo change this line
       /* (parentFragment as MyDisplayHomeFragment).toolBinding.tvTool.text =
            (displayRegionModel.estateName.uppercase(Locale.getDefault()) + ", " + displayRegionModel.LotStreet1)
*/


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

    private fun showEditDialog(estateName: String, street: String, subrub: String) {
        val fm: FragmentManager = requireActivity().supportFragmentManager
        viewModel.updateToolbarValue("${estateName.uppercase()}, $street")

        var fragment = NearbyBottomSheetFragment(this, this::showBottomView, true).newInstance(
            estateName,
            street,
            subrub
        );
        if (fragment != null) {
            if (!fragment.isAdded) {
                fragment.show(fm, NearbyBottomSheetFragment.TAG)
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
    override fun showBottomSheetClusterDialog(arrayList: ArrayList<HnlClusterItem>) {
//        showBottomSheetFilter(arrayList)

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