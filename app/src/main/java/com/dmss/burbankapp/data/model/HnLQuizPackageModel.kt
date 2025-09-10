package com.dmss.burbankapp.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class HnLQuizPackageModel(
    @SerializedName("Address")
    var address: String? = null,
    @SerializedName("BedRooms")
    var bedrooms: Int? = null,//Check some times string
    @SerializedName("BathRooms")
    var bathrooms: Int? = null,//Check some times string
    @SerializedName("CarSpace")
    var carspace: Int? = null,//Check some times string
    @SerializedName("HousePrice")
    var housePrice: Double? = null,
    @SerializedName("FacadePermanentUrl")
    var FacadePermanentUrl: String? = null,
    @SerializedName("Facade")
    var Facade: String? = null,
    @SerializedName("HouseName")
    var houseName: String? = null,
    @SerializedName("IsFav")
    var isFav: Boolean = false,
    @SerializedName("Latitude")
    var latitude: String? = null,
    @SerializedName("Longitude")
    var longitude: String? = null,
    @SerializedName("Price")
    var price: Double? = null,
    @SerializedName("HouseSize")
    var houseSize: Int? = null,
    @SerializedName("PackageId")
    var packageId: Int? = null,
    var message: String? = null,
    @SerializedName("PackageId_LandBank")
    var packageIdLandBank: Int? = null,
    @SerializedName("Id")
    var id: Int? = null,
    @SerializedName("LandSize")
    var landSize: Float? = null,
    var currentUserName: String? = ""


) : Parcelable
