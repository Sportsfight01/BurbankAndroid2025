package com.dmss.burbankapp.data.model

import com.google.gson.annotations.SerializedName
 class LoginModel(

    @SerializedName("status")
    var Status: Boolean,
    @SerializedName("message")
    var Message: String = "",
    @SerializedName("Userid")
    var UserId: Int ,
    var LoginType:Boolean,

    var loginStatus: LoginStatus



)

enum class LoginStatus{
    FACE_BOOK_LOGIN,GMAIL_LOGIN,EMAIL_LOGIN
}