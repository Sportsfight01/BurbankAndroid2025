package com.dmss.burbankapp.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dmss.burbankapp.data.api.RetrofitBuilder
import com.dmss.burbankapp.data.apiUtils.Resource
import com.dmss.burbankapp.data.model.UpdateProfilePicModel
import com.google.gson.JsonObject
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.launch

class ProfilePicViewModel: ViewModel() {
    lateinit var bitmap: Bitmap;


    lateinit var profilePicViewModel: ProfilePicViewModel
    private val profilePicBitMap = MutableLiveData<Bitmap>()
    private val profilePictureLiveData = MutableLiveData<Resource<UpdateProfilePicModel>>()

    @Synchronized
    fun getInstance(): ProfilePicViewModel? {
        return profilePicViewModel
    }

    fun setUser(user: Bitmap) {
        profilePicBitMap.postValue(user)
    }


    fun getProfilePic(): LiveData<Bitmap> {
        return profilePicBitMap
    }

    fun updateImageApi(userId: Int, photoString: String) {
        viewModelScope.launch {
            profilePictureLiveData.postValue(Resource.loading(null))
            try {
                val user = JsonObject()
                user.addProperty("UserId", userId)
                user.addProperty("ImageContent", photoString)

                val updateProfilePicApi = RetrofitBuilder.apiService.updateProfileImage(user)
                profilePictureLiveData.postValue(Resource.success(updateProfilePicApi))
            } catch (e: TimeoutCancellationException) {
                profilePictureLiveData.postValue(
                    Resource.error(
                        "TimeoutCancellationException",
                        null
                    )
                )
            } catch (e: Exception) {
                profilePictureLiveData.postValue(Resource.error("Something Went Wrong", null))
            }
        }
    }

    fun getProfilePicApiLiveData(): MutableLiveData<Resource<UpdateProfilePicModel>> {
        return profilePictureLiveData

    }


}