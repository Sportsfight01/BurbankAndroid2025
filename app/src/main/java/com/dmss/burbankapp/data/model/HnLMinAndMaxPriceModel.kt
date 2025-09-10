package com.dmss.burbankapp.data.model

import com.google.gson.annotations.SerializedName

class HnLMinAndMaxPriceModel(
    @SerializedName("status")
    var status: Boolean,
    @SerializedName("message")
    var message: String,
    @SerializedName("Price")
    var priceModel:PriceRangeModel?=null


)

