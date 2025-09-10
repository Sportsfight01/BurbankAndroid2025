package com.dmss.burbankapp.data.model

import com.google.gson.annotations.SerializedName

class CheckEmailForSharingModel (
    @SerializedName("status")
    var status:Boolean,
    @SerializedName("CheckEmailstatus")
    var checkEmailStatusModel: CheckEmailStatusModel
)