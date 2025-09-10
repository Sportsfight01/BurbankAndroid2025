package com.dmss.burbankapp.ui.mydisplay.designs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProviders
import com.dmss.burbankapp.R
import com.dmss.burbankapp.data.local.CustomSharedPreferences
import com.dmss.burbankapp.data.model.DesignSubModel
import com.dmss.burbankapp.data.model.HouseDetailsByHouseType
import com.dmss.burbankapp.databinding.FragmentPopularHomeDesignDetailBinding
import com.dmss.burbankapp.ui.mydisplay.DisplayToolbarViewModel
import com.dmss.burbankapp.ui.mydisplay.MyDisplayHomeFragment
import com.dmss.burbankapp.ui.mydisplay.nearby.CalendarFragment
import com.dmss.burbankapp.ui.mydisplay.nearby.NearbyBottomSheetFragment
import com.dmss.burbankapp.ui.mydisplay.regions.SuggestedDisplayDetailsFragment
import com.dmss.burbankapp.ui.view.CustomProgressDialog
import com.dmss.burbankapp.utils.AppUtils
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import timber.log.Timber

private const val LATITUDE = "LATITUDE"
private const val LONGITUDE = "LONGITUDE"

class PopularHomeDesignDetailFragment : Fragment(), OnMapReadyCallback,
    GoogleMap.OnMarkerClickListener, NearbyBottomSheetFragment.IDirection {

    private lateinit var binding: FragmentPopularHomeDesignDetailBinding
    private var customProgressDialog: CustomProgressDialog? = null
    lateinit var adapter: PopularHomeDesignExpandableAdapter

    lateinit var myPreference: CustomSharedPreferences
    lateinit var gMap: GoogleMap
    var mapFragment: SupportMapFragment? = null

    lateinit var locationLatLng: LatLng

    var latitude: Double? = null
    var longitude: Double? = null
    lateinit var suggestedModel: DesignSubModel
    lateinit var viewModel: DisplayToolbarViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPopularHomeDesignDetailBinding.inflate(layoutInflater, container, false)

        return binding.root
    }

    companion object {

        const val DESIGNSUBMODEL = "DESIGNSUBMODEL"

        @JvmStatic
        fun newInstance(latitude: Double, longitude: Double) =
            SuggestedDisplayDetailsFragment().apply {
                arguments = Bundle().apply {
                    putDouble(LATITUDE, latitude)
                    putDouble(LONGITUDE, longitude)

                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    private fun initViews() {
        binding.llBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
        viewModel = ViewModelProviders.of(requireActivity()).get(
            DisplayToolbarViewModel::class.java)
        mapFragment =
            childFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        myPreference = CustomSharedPreferences(requireContext())

        suggestedModel = arguments?.getParcelable(PopularHomeDesignDetailFragment.DESIGNSUBMODEL)!!
        suggestedModel.let {
            showEditDialog(
                it.EstateName?:"",
                it.LotStreet1?:"",
                it.LotSuburb?:""
            )

        }
        var lat=arguments?.getString(LATITUDE)
        var lng=arguments?.getString(LONGITUDE)
        if(lat!!.contains(",")) {
           lat= lat.replace(",", "")
        }
        if(lng!!.contains(",")) {
            lng= lng.replace(",", "")
        }
        latitude = lat?.toDouble()
        longitude = lng?.toDouble()
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
            gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(locationLatLng, 10f))
        }
    }

    override fun onMarkerClick(marker: Marker?): Boolean {
        suggestedModel.let {
            it.LotStreet1?.let { it1 ->
                showEditDialog(
                    it.EstateName?:"",
                    it1,
                    it.LotSuburb?:""
                )
            }

        }
        showBottomLayoutViewNew(suggestedModel)


        return false
    }

    private fun showBottomLayoutViewNew(displayRegionModel: DesignSubModel) {
        // todo change this line
        /*(parentFragment as MyDisplayHomeFragment).toolBinding.tvTool.text =
            displayRegionModel.EstateName*/

        binding.llBack.visibility = View.VISIBLE
        binding.bottomContainer.visibility = View.VISIBLE
        binding.tvEstateName.text = displayRegionModel.EstateName
        binding.tvStreetSubrub.text =
            ("${displayRegionModel.LotStreet1?:""},\n${displayRegionModel.LotSuburb?:""}")
        suggestedModel = displayRegionModel
        binding.bottomContainer.setOnClickListener {
            showEditDialog(
                displayRegionModel.EstateName?:"",
                displayRegionModel.LotStreet1?:"",
                displayRegionModel.LotSuburb?:""
            )
        }
    }


    private fun showEditDialog(estateName: String, street: String, subrub: String) {
        val fm: FragmentManager = requireActivity().supportFragmentManager
        viewModel.updateToolbarValue("${estateName.uppercase()}")

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


    private fun showEditDialog(
        estateName: String,
        street: String,
        subrub: String,
        model: DesignSubModel
    ) {
        val fm: FragmentManager = requireActivity().supportFragmentManager
        var fragment = NearbyBottomSheetFragment(this, this::showBottomView, false).newInstance(
            estateName,
            street,
            subrub
        );
        if (fragment != null) {
            if (!fragment.isAdded) {
                fragment.show(fm, "fragment_edit_name")
            }
        }

        showBottomLayoutViewNew(model)
    }


    fun showBottomView(bottomView: Boolean) {
        if (bottomView) {
            Timber.e("bottom View $bottomView")

            if (this::suggestedModel.isInitialized) {
                Timber.e("bottom View ${suggestedModel.LotSuburb}")
                showBottomLayoutViewNew(suggestedModel)
            }
        }
    }


    override fun showDirection(houseList: ArrayList<HouseDetailsByHouseType>) {
        if (suggestedModel.latitude.isNotEmpty() && suggestedModel.longitude.isNotEmpty()) {
            AppUtils.navigateToGoogleMaps(
                suggestedModel.latitude.toDouble(),
                suggestedModel.longitude.toDouble(),
                requireContext()
            )
        }
    }

    override fun bookAppointment(houseName: String, houseSize: String) {
        var bundle = Bundle()
        bundle.putString("estateName", suggestedModel.EstateName)
        bundle.putString("street", suggestedModel.LotStreet1?:"")
        bundle.putString("suburb", suggestedModel.LotSuburb?:"")
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
        transaction.add(R.id.fragment_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()


    }


}