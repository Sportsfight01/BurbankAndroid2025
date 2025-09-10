package com.dmss.burbankapp.data.model

import com.google.gson.annotations.SerializedName

class PriceModel(
    @SerializedName("MinPrice")
    var minPrice: Double,
    @SerializedName("MaxPrice")
    var maxPrice: Double

)