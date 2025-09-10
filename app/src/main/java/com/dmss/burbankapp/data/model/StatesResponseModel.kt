package com.dmss.burbankapp.data.model

import com.google.gson.annotations.SerializedName

class StatesResponseModel(
    @SerializedName("status")
    var status: Boolean,
    @SerializedName("States")
    var statesList: ArrayList<StateModel>
)

