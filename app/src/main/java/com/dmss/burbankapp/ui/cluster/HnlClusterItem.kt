package com.dmss.burbankapp.ui.cluster

import com.dmss.burbankapp.data.model.HnLQuizPackageModel
import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.SerializedName
import com.google.maps.android.clustering.ClusterItem

class HnlClusterItem(
    val hnLQuizPackageModel: HnLQuizPackageModel,
    @SerializedName("HouseName")
    var houseName: String,
    private val latLng: LatLng

) : ClusterItem {
    override fun getSnippet(): String {
        return ""
    }

    override fun getTitle(): String {
        return houseName
    }

    override fun getPosition(): LatLng {
        return latLng
    }
}