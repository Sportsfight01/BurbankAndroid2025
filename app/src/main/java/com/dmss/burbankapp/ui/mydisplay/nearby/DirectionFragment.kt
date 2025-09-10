package com.dmss.burbankapp.ui.mydisplay.nearby

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.dmss.burbankapp.R
import com.dmss.burbankapp.data.model.HouseDetailsByHouseType
import com.dmss.burbankapp.databinding.FragmentDirectionBinding
import com.dmss.burbankapp.utils.AnimationUtils
import com.dmss.burbankapp.utils.AppConstants
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*


class DirectionFragment : Fragment(), OnMapReadyCallback {

    lateinit var gMap: GoogleMap
    val SYDNEY = LatLng(-33.862, 151.21)

    private var originMarker: Marker? = null
    private var greyPolyLine: Polyline? = null
    private var blackPolyline: Polyline? = null
    private var destinationMarker: Marker? = null
    var pathList: ArrayList<LatLng> = ArrayList()
    lateinit var binding: FragmentDirectionBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDirectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        AppConstants.BACKSTACK_COUNT = 0
        val mapFragment: SupportMapFragment? =
            childFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment


        binding.llBack.setOnClickListener {
            activity?.onBackPressed()
        }


        var directionList =
            arguments?.getParcelableArrayList<HouseDetailsByHouseType>("DirectionList")

        pathList.add(
            LatLng(
                AppConstants.dummyLatitude.toDouble(),
                AppConstants.dummyLongitude.toDouble()
            )
        )

        if (directionList?.isNotEmpty() == true) {
            for (houseModel in directionList) {

                if (houseModel.Latitude.isNotEmpty() && houseModel.Longitude.isNotEmpty()) {

                    if (houseModel != null) {
                        if (houseModel.displayEstateName.isNotEmpty()) {
                            binding.tvHeader.text = houseModel.displayEstateName
                        }
                        if (houseModel.street.isNotEmpty() && houseModel.suburb.isNotEmpty()) {
                            binding.tvSubHeader.text = ("${houseModel.street}, ${houseModel.suburb}")
                        }

                        pathList.add(
                            LatLng(
                                houseModel.Latitude.toDouble(),
                                houseModel.Longitude.toDouble()
                            )
                        )
                    }

                }


            }

        }

        binding.ivBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        /*if (directionList != null && directionList.size > 0) {
            for (model in directionList) {
                binding.tvHeader.text = model.displayEstateName
                binding.tvSubHeader.text = ("${model.street} ${model.suburb}")

                if (model.Latitude.isNotEmpty() && model.Longitude.isNotEmpty()) {
                    pathList.add(LatLng(model.Latitude.toDouble(), model.Longitude.toDouble()))
                }
            }
        }*/
        mapFragment?.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        googleMap ?: return
        gMap = googleMap
        gMap.uiSettings.isZoomControlsEnabled = true

        if (pathList.isNotEmpty())
            showPath(pathList)

    }

    private fun addOriginDestinationMarkerAndGet(latLng: LatLng): Marker {
        val bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(getDestinationBitmap())
        // return gMap.addMarker(MarkerOptions().position(latLng).icon(R.drawable.location))

        return gMap.addMarker(
            MarkerOptions().position(latLng)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.location))
        )
    }

    private fun getDestinationBitmap(): Bitmap {
        val height = 20
        val width = 20
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        val canvas = Canvas(bitmap)
        val paint = Paint()
        paint.color = Color.BLACK
        paint.style = Paint.Style.FILL
        paint.isAntiAlias = true
        canvas.drawRect(0F, 0F, width.toFloat(), height.toFloat(), paint)
        return bitmap
    }


    private fun showPath(latLngList: List<LatLng>) {


        val builder = LatLngBounds.Builder()
        for (latLng in latLngList) {
            builder.include(latLng)
        }
        val bounds = builder.build()
        gMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 2))
        val polylineOptions = PolylineOptions()
        polylineOptions.color(ContextCompat.getColor(requireContext(), R.color.polyline))
        polylineOptions.width(15f)
        polylineOptions.addAll(latLngList)
        greyPolyLine = gMap.addPolyline(polylineOptions)

        val blackPolylineOptions = PolylineOptions()
        blackPolylineOptions.width(15f)
        blackPolylineOptions.color(ContextCompat.getColor(requireContext(), R.color.polyline))
        blackPolyline = gMap.addPolyline(blackPolylineOptions)

        originMarker = addOriginDestinationMarkerAndGet(latLngList[0])
        //originMarker?.setAnchor(0.5f, 0.5f)
        destinationMarker = addOriginDestinationMarkerAndGet(latLngList[latLngList.size - 1])
        //destinationMarker?.setAnchor(0.5f, 0.5f)

        val polylineAnimator = AnimationUtils.polyLineAnimator()
        polylineAnimator.addUpdateListener { valueAnimator ->
            val percentValue = (valueAnimator.animatedValue as Int)
            val index = (greyPolyLine?.points!!.size * (percentValue / 100.0f)).toInt()
            blackPolyline?.points = greyPolyLine?.points!!.subList(0, index)
        }
        polylineAnimator.start()
    }

}