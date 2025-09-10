package com.dmss.burbankapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity(tableName = "states")
data class StateModel(
    @PrimaryKey
    @SerializedName("Id")
    var id: Int? = null,
    @SerializedName("Name")
    var name: String? = null,
    @SerializedName("StateOrder")
    var stateOrder: Int? = null,
    @SerializedName("isSelected")
    @Expose
    var isSelected: Boolean? = null
)