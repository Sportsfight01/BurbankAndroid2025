package com.dmss.burbankapp.data.model

import com.google.gson.annotations.SerializedName

class PackageModel(

    @SerializedName("PackageId")
    var PackageId: Int,
    @SerializedName("PackageId_LandBank")
    var PackageId_LandBank: Int,
    @SerializedName("ConsultantFirstName")
    var ConsultantFirstName: String,
    @SerializedName("ConsultantSurName")
    var ConsultantSurName: String,
    @SerializedName("ContactNo")
    var ContactNo: String,
    @SerializedName("HousePrice")
    var HousePrice: String

)
