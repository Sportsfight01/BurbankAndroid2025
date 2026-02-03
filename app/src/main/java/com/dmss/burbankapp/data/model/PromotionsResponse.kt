package com.dmss.burbankapp.data.model

import com.google.gson.annotations.SerializedName

data class PromotionsResponse(
    @SerializedName("Code")
    val code: Int,
    @SerializedName("Data")
    val data: List<PromotionItem>,
    @SerializedName("Count")
    val count: Int,
    @SerializedName("Message")
    val message: String
)

data class PromotionItem(
    @SerializedName("Description")
    val description: String?,     // can be empty in sample, so nullable
    @SerializedName("PageUrl")
    val pageUrl: String?,
    @SerializedName("Image")
    val image: String,           // can be null
    @SerializedName("State")
    val state: String,
    @SerializedName("SortOrder")
    val sortOrder: Int
)
