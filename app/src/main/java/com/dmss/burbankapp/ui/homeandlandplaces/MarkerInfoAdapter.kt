package com.dmss.burbankapp.ui.homeandlandplaces

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.dmss.burbankapp.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker


class MarkerInfoAdapter(var context: Context?) : GoogleMap.InfoWindowAdapter {


    override fun getInfoContents(p0: Marker?): View {
        TODO("Not yet implemented")
    }

    override fun getInfoWindow(p0: Marker?): View {
        val inflater =
            context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val v: View = inflater.inflate(R.layout.map_marker_info_window, null)

       // val latLng: LatLng = p0?.position ?:
        /* val tvLat: TextView = v.findViewById<View>(R.id.tv_lat) as TextView
         val tvLng: TextView = v.findViewById<View>(R.id.tv_lng) as TextView
         tvLat.setText("Latitude:" + latLng.latitude)
         tvLng.setText("Longitude:" + latLng.longitude)*/
        return v


    }
}