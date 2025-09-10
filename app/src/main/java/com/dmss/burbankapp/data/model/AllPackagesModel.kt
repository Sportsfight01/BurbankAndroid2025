package com.dmss.burbankapp.data.model

import com.google.gson.annotations.SerializedName

class AllPackagesModel(
    @SerializedName("status")
    var statu: Boolean,
    @SerializedName("lstpackages")
    var packageList: ArrayList<PackageModel>
)