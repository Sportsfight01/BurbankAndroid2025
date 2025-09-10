package com.dmss.burbankapp.ui.homeandland

import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.dmss.burbankapp.R
import com.dmss.burbankapp.data.local.CustomSharedPreferences
import com.dmss.burbankapp.data.model.HnLQuizPackageModel
import com.dmss.burbankapp.databinding.FragmentHomeLandMapBinding
import com.dmss.burbankapp.databinding.ProfileWithBadgeBinding
import com.dmss.burbankapp.ui.cluster.*
import com.dmss.burbankapp.ui.homeandlandplaces.HomeLandPlaceFragment
import com.dmss.burbankapp.ui.homeandlandplaces.MarkerInfoAdapter
import com.dmss.burbankapp.ui.main.MainActivity
import com.dmss.burbankapp.ui.mydisplay.DisplayToolbarViewModel
import com.dmss.burbankapp.utils.AppConstants
import com.dmss.burbankapp.utils.AppUtils
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.ClusterManager.OnClusterClickListener
import common.AppController


class HomeLandMapFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener,
    MyClusterRenderer.ShowBottomSheetDialog,
    ClusterManager.OnClusterItemClickListener<HnlClusterItem> {

    lateinit var markerInfoAdapter: MarkerInfoAdapter
    lateinit var itemView: View
    var clusterArrayList: ArrayList<HnlClusterItem> = ArrayList()

    lateinit var gMap: GoogleMap
    lateinit var packagesList: ArrayList<HnLQuizPackageModel>
    lateinit var preferences: CustomSharedPreferences
    lateinit var profileWithBadgeBinding: ProfileWithBadgeBinding
    private lateinit var displayToolbarViewModel: DisplayToolbarViewModel


    lateinit var binding: FragmentHomeLandMapBinding;
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeLandMapBinding.inflate(layoutInflater, container, false)
        profileWithBadgeBinding = ProfileWithBadgeBinding.bind(binding.root)

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initialVies()
    }

    private fun initialVies() {
        displayToolbarViewModel = ViewModelProviders.of(requireActivity())[DisplayToolbarViewModel::class.java]

        preferences = CustomSharedPreferences(AppController.getInstance())
        val headerText = if (AppConstants.PRICE_RANGE_HEADER.contains("|") || AppConstants.PRICE_RANGE_HEADER.isEmpty())
            (AppConstants.REGION_HEADER + AppConstants.PRICE_RANGE_HEADER + AppConstants.STOREYS_HEADER + AppConstants.BEDROOMS_HEADER)
        else (AppConstants.REGION_HEADER +" | "+ AppConstants.PRICE_RANGE_HEADER +" | "+ AppConstants.STOREYS_HEADER +" | "+ AppConstants.BEDROOMS_HEADER)
        binding.tvTool.text = headerText
        profileWithBadgeBinding.profileNotification.text=AppConstants.TotalMyFavs.toString()
        displayToolbarViewModel.favouritesCount.observe(viewLifecycleOwner, Observer<Int> { item ->
            profileWithBadgeBinding.profileNotification.text = item.toString()

        })
        packagesList =
            arguments?.getParcelableArrayList<HnLQuizPackageModel>(AppConstants.PACKAGESLIST) as ArrayList<HnLQuizPackageModel>
        var isUserLoggedIn = preferences.getUserLogin()
        if (!isUserLoggedIn) {
            profileWithBadgeBinding.tvFavorites.background = resources.getDrawable(R.drawable.disable_rectangle_new)
            profileWithBadgeBinding.profileNotification.visibility=View.GONE
        }
        profileWithBadgeBinding.tvFavorites.setOnClickListener {
            if (isUserLoggedIn) {

                (activity as MainActivity).showProfileDialog(activity!!)

            } else {
                AppUtils.showPleaseLoginDialog(
                    requireContext(),
                    requireActivity(),
                    getString(R.string.Please_login_to_view_edit_profile)
                )
            }
        }
        if (packagesList.size > 0) {
            clusterArrayList.clear()
            clusterArrayList = ArrayList()
            var latLong: LatLng? = null
            for (hnLQuizPackageModel in packagesList) {
                latLong = null
                var clusterItem: HnlClusterItem? = null
                hnLQuizPackageModel.houseName?.let { houseName ->
                    hnLQuizPackageModel.latitude?.let { latitude ->
                        if (latitude != "null") {
                            hnLQuizPackageModel.longitude?.let { longitude ->
                                if (longitude != "null") {
                                    if (latitude.isNotEmpty() && longitude.isNotEmpty()) {
                                        val lat = latitude.trim().toDouble()
                                        val long = longitude.trim().toDouble()
                                        latLong = LatLng(lat, long)
                                        latLong?.let {
                                            clusterItem =
                                                HnlClusterItem(hnLQuizPackageModel, houseName, it)
                                            clusterItem?.let { itemCluster ->
                                                clusterArrayList.add(itemCluster)
                                            }

                                        }

                                    }

                                }

                            }
                        }

                    }
                }
            }
        }


        binding.tvList.setOnClickListener {
            loadFragment(HomeLandPlaceFragment())
        }
        binding.ivBack.setOnClickListener {
            activity!!.onBackPressed()
        }

        val mapFragment: SupportMapFragment? =
            childFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
        mapFragment?.getMapAsync { googleMap: GoogleMap? ->
            GoogleMapHelper.defaultMapSettings(gMap)
            setUpClusterManager(googleMap!!)
        }

    }
    private fun getBoundaries(){
        if (clusterArrayList.size > 0) {
            for (LatLng in clusterArrayList) {
                gMap.addMarker(
                    MarkerOptions().position(LatLng.position)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_display_marker))
                )
                // Changed by durga 11/10/2022

//                gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng.position, 7f))
                gMap.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng.position,
                        10f
                    )
                )
            }
        }
    }
    override fun onMapReady(googleMap: GoogleMap?) {
        googleMap ?: return

        gMap = googleMap
        gMap.uiSettings.isZoomControlsEnabled = true
        gMap.setOnMarkerClickListener(this@HomeLandMapFragment)

        val markerInfoAdapter = MarkerInfoAdapter(activity?.applicationContext)
        gMap.setInfoWindowAdapter(markerInfoAdapter)

//        getBoundaries()
        if (googleMap != null)
            setUpClusterManager(googleMap)
      /*  if (packagesList.size > 0) {
            var latLong: LatLng? = null
            var hnLQuizPackageModel = packagesList[0]
            hnLQuizPackageModel.let { quizPackageModel ->
                quizPackageModel.latitude?.let { latitude ->
                    if (latitude != "null") {
                        quizPackageModel.longitude?.let { longitude ->
                            if (longitude != "null") {
                                if (latitude.isNotEmpty() && longitude.isNotEmpty()) {
                                    val lat = latitude.trim().toDouble()
                                    val long = longitude.trim().toDouble()
                                    latLong = LatLng(lat, long)
                                    latLong?.let { latitudeLongitude ->
                                        gMap.animateCamera(
                                            CameraUpdateFactory.newLatLngZoom(
                                                latitudeLongitude,
                                                10f
                                            )
                                        )
                                    }
                                }

                            }

                        }
                    }

                }


            }
        }*/


    }

    private fun setUpClusterManager(googleMap: GoogleMap) {
        val clusterManager: ClusterManager<HnlClusterItem> =
            ClusterManager(context, googleMap)

        googleMap.setOnMarkerClickListener(clusterManager);
        googleMap.setOnCameraIdleListener(clusterManager)

        clusterManager.setOnClusterItemClickListener(this);
        val markerClusterRenderer =
            context?.let { MarkerClusterRenderer(it, googleMap, clusterManager) }

        var mColoredCircleBackground = ShapeDrawable(OvalShape())
        clusterManager.renderer =
            MyClusterRenderer(activity, googleMap, clusterManager, mColoredCircleBackground, this)
        val items: List<HnlClusterItem> = clusterArrayList

        clusterManager.setAnimation(false)

        clusterManager.addItems(items)
        clusterManager.cluster()
        if(clusterArrayList!= null && clusterArrayList.size>0)
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(clusterArrayList[0].position,
            10f))
    }
    fun loadFragment(fragment: Fragment) {
        val transaction: FragmentTransaction = activity?.supportFragmentManager!!.beginTransaction()
        if (fragment.isAdded) {
            return
        }
        transaction.add(R.id.fl_content, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun onMarkerClick(p0: Marker?): Boolean {
        //loadFragment(HomeLandPlaceDetailFragment())
        return true
    }

    override fun showBottomSheetClusterDialog(arrayList: ArrayList<HnlClusterItem>) {
        showBottomSheetFilter(arrayList)
    }

    private fun showBottomSheetFilter(arrayList: ArrayList<HnlClusterItem>) {
        val homeAndLandClusterBottomSheetDialog = HomeAndLandClusterBottomSheetDialog(arrayList)
        homeAndLandClusterBottomSheetDialog.show(activity!!.supportFragmentManager, "")
    }


    override fun onClusterItemClick(item: HnlClusterItem?): Boolean {
        item?.let { hnlClusterItem ->

            var arrayList: ArrayList<HnlClusterItem> = ArrayList();
            arrayList.add(hnlClusterItem)
            showBottomSheetFilter(arrayList)
            /*hnlClusterItem.hnLQuizPackageModel.let {
                val fragment = HomeLandFullScreenFragment.newInstance(it, false)
                loadFragment(fragment)
            }*/
        }
        return true
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity?)?.getIHideProfilePic()?.hideProfilePic(true)
    }

    override fun onStop() {
        super.onStop()
        (activity as MainActivity?)?.getIHideProfilePic()?.hideProfilePic(false)

    }


}

