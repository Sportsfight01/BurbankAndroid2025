package com.dmss.burbankapp.ui.homeandland

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.dmss.burbankapp.R
import com.dmss.burbankapp.databinding.FragmentMapDialogBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import timber.log.Timber


class MapDialogFragment(latLong: LatLng?) : DialogFragment(), OnMapReadyCallback {
    val SYDNEY = LatLng(-33.862, 151.21)
    private val ZOOM_LEVEL = 11f
    var mMap: GoogleMap? = null
    var latLng: LatLng? = latLong
    private lateinit var mapFragment: SupportMapFragment

    lateinit var binding: FragmentMapDialogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setStyle(STYLE_NORMAL,R.style.FullScreenDialogStyle)

        Timber.e("LatLong Latitude--:" + latLng?.latitude)
        Timber.e("LatLong Longitude--:" + latLng?.longitude)
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.WRAP_CONTENT
            dialog.window!!.setLayout(width, height)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE
        )

        requireDialog().window?.setWindowAnimations(
            R.style.DialogAnimation
        )

        var mapFragment = childFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        if (mapFragment == null) {
            mapFragment = SupportMapFragment()
        }
        mapFragment.getMapAsync(this)


        binding.closeImage.setOnClickListener {
            dismiss()

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMapDialogBinding.inflate(layoutInflater, container, false)
        return binding.root


    }

    /* override fun onActivityCreated(savedInstanceState: Bundle?) {
         super.onActivityCreated(savedInstanceState)
         val mapFragment:SupportMapFragment? = childFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
         mapFragment?.getMapAsync(this)




     }*/

    override fun onDestroy() {
        super.onDestroy()
        Timber.e("onDestroy")
    }


    override fun onMapReady(googleMap: GoogleMap?) {

        mMap = googleMap
        mMap?.mapType = GoogleMap.MAP_TYPE_HYBRID

        val latLng = LatLng(37.7688472, -122.4130859)
        mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 11f))

        val markerOptions = MarkerOptions()
        markerOptions.position(latLng)
        markerOptions.title("Current Position")
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))
        mMap?.addMarker(markerOptions)
        Timber.e("onMapReady Called")
        /*if (googleMap != null) {
            with(googleMap) {
                Timber.e("onMapReady")
                moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, ZOOM_LEVEL))
                addMarker(latLng.let { MarkerOptions().position(it) })
            }
        } else {
            return
        }*/


    }

}