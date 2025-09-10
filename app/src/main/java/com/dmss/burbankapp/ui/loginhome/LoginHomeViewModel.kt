package com.dmss.burbankapp.ui.loginhome

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dmss.burbankapp.data.api.ApiHelper
import com.dmss.burbankapp.data.apiUtils.Resource
import com.dmss.burbankapp.data.local.entity.DatabaseHelper
import com.dmss.burbankapp.data.model.SignUpModel
import com.dmss.burbankapp.data.model.UserInfoModel
import com.google.gson.JsonObject
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.launch
import org.json.JSONObject
import timber.log.Timber

class LoginHomeViewModel(
    private val apiHelper: ApiHelper,
    private val dbHelper: DatabaseHelper
) : ViewModel() {

    private val signUpLiveData = MutableLiveData<Resource<SignUpModel>>()
    lateinit var userInfoModel: UserInfoModel

    fun createUserSignUpData(): LiveData<Resource<SignUpModel>> {
        return signUpLiveData
    }


    fun createUser(jsonObject: JSONObject) {
        viewModelScope.launch {
            signUpLiveData.postValue(Resource.loading(null))
            try {
                var signUpJsonObject = JsonObject()
                if (jsonObject.has("LoginType")) {
                    //userInfoModel.LoginType = jsonObject.optString("LoginType")
                    signUpJsonObject.addProperty("LoginType", jsonObject.optString("LoginType"))
                }
                if (jsonObject.has("FirstName")) {
                    // userInfoModel.FirstName = jsonObject.optString("FirstName")
                    signUpJsonObject.addProperty("FirstName", jsonObject.optString("FirstName"))
                }
                Timber.e("Api user Name (Create User)::${signUpJsonObject.get("FirstName")}")

                if (jsonObject.has("LastName")) {
                    // userInfoModel.LastName = jsonObject.optString("LastName")
                    signUpJsonObject.addProperty("LastName", jsonObject.optString("LastName"))
                }
                if (jsonObject.has("Email")) {
                    // userInfoModel.Email = jsonObject.optString("Email")
                    signUpJsonObject.addProperty("Email", jsonObject.optString("Email"))
                }
                if (jsonObject.has("ImageContent")) {
                    signUpJsonObject.addProperty(
                        "ImageContent",
                        jsonObject.optString("ImageContent")
                    )
                }
                val signUp = apiHelper.signUpApi(signUpJsonObject)
                signUpLiveData.postValue(Resource.success(signUp))
            } catch (e: TimeoutCancellationException) {
                signUpLiveData.postValue(Resource.error("TimeoutCancellationException", null))
            } catch (e: Exception) {
                signUpLiveData.postValue(
                    Resource.error(
                        "Something went wrong!! Please try again later",
                        null
                    )
                )
            }
        }
    }


}