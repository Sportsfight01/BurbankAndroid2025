package com.dmss.burbankapp.ui.mydisplay.regions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.dmss.burbankapp.R
import com.dmss.burbankapp.data.local.CustomSharedPreferences
import com.dmss.burbankapp.data.model.HouseDetailsByHouseType
import com.dmss.burbankapp.data.model.SuggestedHomesDtoModel
import com.dmss.burbankapp.databinding.FragmentSuggestedDisplayDetailsBinding
import com.dmss.burbankapp.ui.mydisplay.MyDisplayHomeFragment
import com.dmss.burbankapp.ui.mydisplay.nearby.CalendarFragment
import com.dmss.burbankapp.ui.mydisplay.nearby.NearbyBottomSheetFragment
import com.dmss.burbankapp.utils.AppConstants
import com.dmss.burbankapp.utils.AppUtils
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import java.util.*

private const val LATITUDE = "LATITUDE"
private const val LONGITUDE = "LONGITUDE"

class SuggestedDisplayDetailsFragment : Fragment(), OnMapReadyCallback,
    GoogleMap.OnMarkerClickListener, NearbyBottomSheetFragment.IDirection {

    private lateinit var binding: FragmentSuggestedDisplayDetailsBinding
    lateinit var myPreference: CustomSharedPreferences
    lateinit var gMap: GoogleMap
    var mapFragment: SupportMapFragment? = null
    lateinit var locationLatLng: LatLng

    var latitude: Double? = null
    var longitude: Double? = null
    var suggestedModel: SuggestedHomesDtoModel? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSuggestedDisplayDetailsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    companion object {
        var TAG = "SuggestedDisplayDetailsFragment"
        const val SUGGESTED_MODEL = "SUGGESTED_MODEL"

        @JvmStatic
        fun newInstance(latitude: Double, longitude: Double) =
            SuggestedDisplayDetailsFragment().apply {
                arguments = Bundle().apply {
                    putDouble(LATITUDE, latitude)
                    putDouble(LONGITUDE, longitude)

                }
            }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initViews()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mapFragment = null
    }

    private fun initViews() {
        AppConstants.BACKSTACK_COUNT = 0
        mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        myPreference = CustomSharedPreferences(requireContext())

        suggestedModel = arguments?.getParcelable(SUGGESTED_MODEL)
        suggestedModel?.let {
            showEditDialog(
                it.EstateName,
                it.LotStreet1,
                it.lotSuburb,
                it
            )

        }
        latitude = arguments?.getString(LATITUDE)?.toDouble()
        longitude = arguments?.getString(LONGITUDE)?.toDouble()
        if (latitude != null && longitude != null) {
            locationLatLng = LatLng(latitude!!, longitude!!)
            mapFragment?.getMapAsync(this)
        }

    }


    override fun onMapReady(googleMap: GoogleMap?) {
        googleMap ?: return
        gMap = googleMap
        gMap.setOnMarkerClickListener(this)
        gMap.uiSettings.isZoomControlsEnabled = true

        if (this::locationLatLng.isInitialized) {
            gMap.addMarker(
                MarkerOptions().position(locationLatLng)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.location))
            )
            gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(locationLatLng, 7f))
        }
    }


    override fun onMarkerClick(marker: Marker?): Boolean {
        suggestedModel?.let {
            showEditDialog(
                it.EstateName,
                it.LotStreet1,
                it.lotSuburb,
                it
            )
            showBottomLayoutView(it)

        }
        return false
    }

    private fun showEditDialog(
        estateName: String,
        street: String,
        subrub: String,
        model: SuggestedHomesDtoModel
    ) {
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

        showBottomLayoutView(model)
    }

    private fun showBottomView(bottomView: Boolean) {
        if (bottomView) {
            suggestedModel?.let { showBottomLayoutView(it) }

        }
    }

    override fun showDirection(houseList: ArrayList<HouseDetailsByHouseType>) {
        suggestedModel?.let { model ->
            if (model.latitude.isNotEmpty() && model.longitude.isNotEmpty()) {
                AppUtils.navigateToGoogleMaps(
                    model.latitude.toDouble(),
                    model.longitude.toDouble(),
                    requireContext()
                )
            }

        }

    }

    override fun bookAppointment(houseName: String, houseSize: String) {
        var bundle = Bundle()
        bundle.putString("estateName", suggestedModel?.EstateName)
        bundle.putString("street", suggestedModel?.LotStreet1)
        bundle.putString("suburb", suggestedModel?.lotSuburb)
        bundle.putString("houseName", houseName)
        bundle.putString("houseSize", houseSize)
        loadFragment(CalendarFragment(), bundle)


    }

    fun loadFragment(fragment: Fragment, bundle: Bundle) {
        // load fragment
        val transaction: FragmentTransaction = parentFragmentManager.beginTransaction()
        fragment.arguments = bundle
        if (fragment.isAdded) {
            return
        }
        transaction.add(R.id.fragment_container, fragment, SuggestedDisplayDetailsFragment::class.simpleName)
        transaction.addToBackStack(null)
        transaction.commit()


    }

    private fun showBottomLayoutView(displayRegionModel: SuggestedHomesDtoModel) {
        //todo remove
       // (parentFragment as MyDisplayHomeFragment).toolBinding.tvTool.text =
       //     (displayRegionModel.EstateName.uppercase(Locale.getDefault()) + ", " + displayRegionModel.LotStreet1)
        binding.bottomContainer.visibility = View.VISIBLE
        binding.tvEstateName.text = displayRegionModel.EstateName
        binding.tvStreetSubrub.text =
            ("${displayRegionModel.LotStreet1},\n${displayRegionModel.lotSuburb}")
        binding.bottomContainer.setOnClickListener {
            showEditDialog(
                displayRegionModel.EstateName,
                displayRegionModel.LotStreet1,
                displayRegionModel.lotSuburb, displayRegionModel
            )
        }
    }


}