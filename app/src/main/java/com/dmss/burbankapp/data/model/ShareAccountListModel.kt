package com.dmss.burbankapp.data.model

import com.google.gson.annotations.SerializedName

class ShareAccountListModel(
    @SerializedName("status")
    var status: Boolean,
    @SerializedName("Count")
    var count: Int,
    @SerializedName("message")
    var message: String,
    @SerializedName("sharedUserLists")
    var shareList: ArrayList<ShareListModel>


)