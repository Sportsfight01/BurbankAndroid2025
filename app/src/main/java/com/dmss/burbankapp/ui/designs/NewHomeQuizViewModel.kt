package com.dmss.burbankapp.ui.designs

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dmss.burbankapp.data.api.ApiHelper
import com.dmss.burbankapp.data.api.NoConnectivityException
import com.dmss.burbankapp.data.apiUtils.Resource
import com.dmss.burbankapp.data.local.CustomSharedPreferences
import com.dmss.burbankapp.data.local.entity.DatabaseHelper
import com.dmss.burbankapp.data.model.*
import com.google.firebase.installations.remote.TokenResult.ResponseCode
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.launch

class NewHomeQuizViewModel(
    private val apiHelper: ApiHelper,
    private val dbHelper: DatabaseHelper
) : ViewModel() {
    var userLoginData = MutableLiveData<Resource<UserLoginModel>>()
    interface userLoginDataInter {
        fun Result()
    }

    fun getUserLoginData(): LiveData<Resource<UserLoginModel>> {
        return userLoginData

    }
    private val newHomeQuizLiveData = MutableLiveData<Resource<NewHomeQuizModel>>()
    var updateFavorite = MutableLiveData<Boolean>()
    fun getUpdateFavorite(): LiveData<Boolean> {
        return updateFavorite
    }

    private val newHomeQuizNextFeatureLiveData =
        MutableLiveData<Resource<NewHomeNextFeatureResponseModel>>()

    private val fetchHouseCountLiveData =
        MutableLiveData<Resource<NewHomeNextFeatureResponseModel>>()

    private val fetchHouseNameByDetailLiveData =
        MutableLiveData<Resource<HouseNameDetailByNameModel>>()

    private val recentSearchMyCollectionDataLive =
        MutableLiveData<Resource<MyCollectionRecentSearchModel>>()

    private val setRecentSearchMyCollectionLiveData =
        MutableLiveData<Resource<MyCollectionRecentSearchModel>>()
    private val myCollectionHomeLandLiveData = MutableLiveData<Resource<MyCollectionHnlModel>>()
    fun getMyCollectionHnlLiveData(): LiveData<Resource<MyCollectionHnlModel>> {
        return myCollectionHomeLandLiveData
    }

    fun setRecentSearchMyCollectionData(): LiveData<Resource<MyCollectionRecentSearchModel>> {
        return setRecentSearchMyCollectionLiveData
    }

    private val favoriteLiveData = MutableLiveData<Resource<FavoriteResponseModel>>()
    fun getFavorite(): LiveData<Resource<FavoriteResponseModel>> {
        return favoriteLiveData
    }

    fun getRecentSearchMyCollectionLiveData(): LiveData<Resource<MyCollectionRecentSearchModel>> {
        return recentSearchMyCollectionDataLive
    }

    private val favoriteListLiveData =
        MutableLiveData<Resource<ArrayList<HouseListCollectionModel>>>()

    fun getFavoritesListLiveData(): LiveData<Resource<ArrayList<HouseListCollectionModel>>> {
        return favoriteListLiveData
    }


    fun getHouseNameByDetailLiveData(): LiveData<Resource<HouseNameDetailByNameModel>> {
        return fetchHouseNameByDetailLiveData

    }

    fun getHouseCountLiveData(): LiveData<Resource<NewHomeNextFeatureResponseModel>> {
        return fetchHouseCountLiveData
    }

    fun getNewHomeQuizLiveData(): LiveData<Resource<NewHomeQuizModel>> {
        return newHomeQuizLiveData
    }

    fun getNewHomeNextQuestionLiveData(): LiveData<Resource<NewHomeNextFeatureResponseModel>> {
        return newHomeQuizNextFeatureLiveData
    }

    fun fetchMyCollectionQuiz(stateId: Int) {
        viewModelScope.launch {
            newHomeQuizLiveData.postValue(Resource.loading(null))
            try {
                val regionsData = apiHelper.fetchNewHomeQuiz(stateId)
                newHomeQuizLiveData.postValue(Resource.success(regionsData))
            } catch (e: TimeoutCancellationException) {
                newHomeQuizLiveData.postValue(Resource.error("TimeoutCancellationException", null))
            } catch (e: Exception) {
                newHomeQuizLiveData.postValue(
                    Resource.error(
                        "Something went wrong!! Please try again later",
                        null
                    )
                )
            }
        }
    }

    fun fetchNewHomesCollectionsNextFeature(jsonObject: MyCollectionQuizQuestionRequest) {
        viewModelScope.launch {
            val gson=Gson()
            newHomeQuizNextFeatureLiveData.postValue(Resource.loading(null))
//            var code=retrofit2.Response.success(apiHelper.fetchNewHomesNextFeature(jsonObject))
//            println("resonse out code:: "+code.code())

            try {newHomeQuizNextFeatureLiveData
                val regionsData = apiHelper.fetchNewHomesNextFeature(jsonObject)
                var code=retrofit2.Response.success(regionsData)
                println("response code from viewmodel:: "+code.code())
               /* var loginUser=CustomSharedPreferences.instance.getUserLogin()
                val text: String = regionsData.toString()*/
                newHomeQuizNextFeatureLiveData.postValue(Resource.success(regionsData))


                /*jsonObject.addProperty("Username", "mobileuser@gmail.com")
                jsonObject.addProperty("Password", "MobileUser@123")*/
            /*    jsonObject.addProperty("Username", "burbank_minad")
                jsonObject.addProperty("Password", "401b09eab3c013d4ca54922bb802bec8fd5318192b0a75f201d8b3727429090fb337591abd3e44453b954555b7a0812e1081c39b740293f765eae731f5a65ed1")
                val loginData = apiHelper.userLogin(jsonObject)*/
            } catch (e: TimeoutCancellationException) {
                newHomeQuizNextFeatureLiveData.postValue(
                    Resource.error(
                        "TimeoutCancellationException",
                        null
                    )
                )
            } catch (e: Exception) {
                newHomeQuizNextFeatureLiveData.postValue(
                    Resource.error(
                        "Something went wrong!! Please try again later",
                        null
                    )
                )
            }
        }
    }

    fun fetchNewHomesCollectionsNextFeatureLotWidth(
        jsonObject: MyCollectionQuizQuestionRequest,
        minLotWidth: String
    ) {
        viewModelScope.launch {
            newHomeQuizNextFeatureLiveData.postValue(Resource.loading(null))
            try {
                val regionsData =
                    apiHelper.fetchNewHomesNextFeatureMinLotWidth(jsonObject, minLotWidth)
                newHomeQuizNextFeatureLiveData.postValue(Resource.success(regionsData))
            } catch (e: TimeoutCancellationException) {
                newHomeQuizNextFeatureLiveData.postValue(
                    Resource.error(
                        "TimeoutCancellationException",
                        null
                    )
                )
            } catch (e: Exception) {
                newHomeQuizNextFeatureLiveData.postValue(
                    Resource.error(
                        "Something went wrong!! Please try again later",
                        null
                    )
                )
            }
        }
    }
    fun fetchLoginData() {
        viewModelScope.launch {
            userLoginData.postValue(Resource.loading(null))
            try {
                val jsonObject = JsonObject()
                /*jsonObject.addProperty("Username", "mobileuser@gmail.com")
                jsonObject.addProperty("Password", "MobileUser@123")*/
                jsonObject.addProperty("Username", "burbank_minad")
                jsonObject.addProperty("Password", "401b09eab3c013d4ca54922bb802bec8fd5318192b0a75f201d8b3727429090fb337591abd3e44453b954555b7a0812e1081c39b740293f765eae731f5a65ed1")
                val loginData = apiHelper.userLogin(jsonObject)
                userLoginData.postValue(Resource.success(loginData))
            } catch (e: TimeoutCancellationException) {
                userLoginData.postValue(Resource.error("TimeoutCancellationException", null))
            } catch (e: NoConnectivityException) {
                userLoginData.postValue(
                    Resource.error(e.message, null)
                )
            } catch (e: Exception) {
                userLoginData.postValue(
                    Resource.error(
                        "Something went wrong!! Please try again later",
                        null
                    )
                )
            }

        }
    }

    fun fetchNewHomesHouseCount(jsonObject: JsonObject) {
        viewModelScope.launch {
            fetchHouseCountLiveData.postValue(Resource.loading(null))
            try {
                val regionsData = apiHelper.fetchHouseCount(jsonObject)
                fetchHouseCountLiveData.postValue(Resource.success(regionsData))
            } catch (e: TimeoutCancellationException) {
                fetchHouseCountLiveData.postValue(
                    Resource.error(
                        "TimeoutCancellationException",
                        null
                    )
                )
            } catch (e: Exception) {
                fetchHouseCountLiveData.postValue(
                    Resource.error(
                        "Something went wrong!! Please try again later",
                        null
                    )
                )
            }
        }
    }

    fun fetchHouseNameDetailApi(houseSize: Int, houseName: String, stateId: Int) {
        viewModelScope.launch {
            fetchHouseNameByDetailLiveData.postValue(Resource.loading(null))
            try {
                val regionsData = apiHelper.getHouseNameDetailByName(houseSize, houseName, stateId)
                fetchHouseNameByDetailLiveData.postValue(Resource.success(regionsData))


            } catch (e: TimeoutCancellationException) {
                fetchHouseNameByDetailLiveData.postValue(
                    Resource.error(
                        "TimeoutCancellationException",
                        null
                    )
                )
            } catch (e: Exception) {
                fetchHouseNameByDetailLiveData.postValue(
                    Resource.error(
                        "Something went wrong!! Please try again later",
                        null
                    )
                )
            }
        }

    }

    fun setRecentSearchData(jsonObject: SetRecentMyCollectionRequest) {
        viewModelScope.launch {
            setRecentSearchMyCollectionLiveData.postValue(Resource.loading(null))
            try {
                val favoriteListData = apiHelper.setRecentSearchMyCollectionData(jsonObject)
                setRecentSearchMyCollectionLiveData.postValue(Resource.success(favoriteListData))
            } catch (e: TimeoutCancellationException) {
                setRecentSearchMyCollectionLiveData.postValue(
                    Resource.error(
                        "TimeoutCancellationException",
                        null
                    )
                )
            } catch (e: Exception) {
                setRecentSearchMyCollectionLiveData.postValue(
                    Resource.error(
                        "Something went wrong!! Please try again later",
                        null
                    )
                )
            }
        }

    }

    fun getRecentSearchDataMyCollection(userId: Int, typeId: Int, stateId: Int) {
        viewModelScope.launch {
            recentSearchMyCollectionDataLive.postValue(Resource.loading(null))
            try {
                val favoriteListData =
                    apiHelper.getRecentSearchDataMyCollection(userId, typeId, stateId)
                recentSearchMyCollectionDataLive.postValue(Resource.success(favoriteListData))
            } catch (e: TimeoutCancellationException) {
                recentSearchMyCollectionDataLive.postValue(
                    Resource.error(
                        "TimeoutCancellationException",
                        null
                    )
                )
            } catch (e: Exception) {
                recentSearchMyCollectionDataLive.postValue(
                    Resource.error(
                        "Something went wrong!! Please try again later",
                        null
                    )
                )
            }
        }

    }


    fun getAllFavorites(jsonObject: JsonObject) {
        viewModelScope.launch {
            favoriteListLiveData.postValue(Resource.loading(null))
            try {
                val favoriteListData = apiHelper.getAllFavoritesMyCollection(jsonObject)
                favoriteListLiveData.postValue(Resource.success(favoriteListData.houseListData))
            } catch (e: TimeoutCancellationException) {
                favoriteListLiveData.postValue(Resource.error("TimeoutCancellationException", null))
            } catch (e: Exception) {
                favoriteListLiveData.postValue(
                    Resource.error(
                        "Something went wrong!! Please try again later",
                        null
                    )
                )
            }
        }

    }

    fun setFavorite(jsonObject: JsonObject) {
        viewModelScope.launch {
            favoriteLiveData.postValue(Resource.loading(null))
            try {
                val favoriteData = apiHelper.setFavorite(jsonObject)
                favoriteLiveData.postValue(Resource.success(favoriteData))
            } catch (e: TimeoutCancellationException) {
                favoriteLiveData.postValue(Resource.error("TimeoutCancellationException", null))
            } catch (e: Exception) {
                favoriteLiveData.postValue(
                    Resource.error(
                        "Something went wrong!! Please try again later",
                        null
                    )
                )
            }
        }
    }

    fun getMyCollectionHomeAndLand(stateId: Int, houseName: String) {
        viewModelScope.launch {
            myCollectionHomeLandLiveData.postValue(Resource.loading(null))
            try {
                val favoriteData = apiHelper.getMyCollectionHnlDetailByName(stateId, houseName)
                myCollectionHomeLandLiveData.postValue(Resource.success(favoriteData))
            } catch (e: TimeoutCancellationException) {
                myCollectionHomeLandLiveData.postValue(
                    Resource.error(
                        "TimeoutCancellationException",
                        null
                    )
                )
            } catch (e: Exception) {
                myCollectionHomeLandLiveData.postValue(
                    Resource.error(
                        "Something went wrong!! Please try again later",
                        null
                    )
                )
            }
        }

    }

    fun fetchNewHomesCollectionsNextFeatureNext(jsonObject: MyCollectionQuizQuestionRequest) {
        viewModelScope.launch {
            val gson=Gson()
            newHomeQuizNextFeatureLiveData.postValue(Resource.loading(null))
            try {
                val regionsData = apiHelper.fetchNewHomesNextFeatureNext(jsonObject)
                newHomeQuizNextFeatureLiveData.postValue(Resource.success(regionsData))
            } catch (e: TimeoutCancellationException) {
                newHomeQuizNextFeatureLiveData.postValue(
                    Resource.error(
                        "TimeoutCancellationException",
                        null
                    )
                )
            } catch (e: Exception) {
                newHomeQuizNextFeatureLiveData.postValue(
                    Resource.error(
                        "Something went wrong!! Please try again later",
                        null
                    )
                )
            }
        }
    }

}