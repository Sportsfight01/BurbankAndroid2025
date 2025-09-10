package com.dmss.burbankapp.ui.homeandlandregions

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dmss.burbankapp.data.api.ApiHelper
import com.dmss.burbankapp.data.apiUtils.Resource
import com.dmss.burbankapp.data.local.entity.DatabaseHelper
import com.dmss.burbankapp.data.model.*
import com.google.gson.JsonObject
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.launch

class HomeAndLandViewModel(
    private val apiHelper: ApiHelper,
    private val dbHelper: DatabaseHelper
) : ViewModel() {
    private val regionsLiveData = MutableLiveData<Resource<ArrayList<StateRegionModel>>>()
    private val hnLiveData = MutableLiveData<Resource<HnLPackagesModel>>()
    private val hnLiveDataFirst = MutableLiveData<Resource<HnLPackagesModel>>()
    private val hnLMinAndMaxLiveData = MutableLiveData<Resource<HnLMinAndMaxPriceModel>>()
    private val favoriteLiveData = MutableLiveData<Resource<FavoriteResponseModel>>()
    private val favoriteListLiveData = MutableLiveData<Resource<ArrayList<HouseListModel>>>()
    private val homeLandPackageDetailLiveData =
        MutableLiveData<Resource<HomeAndLandPackageDetailModel>>()

    fun getHomeAndLandPackageDetailLiveData(): LiveData<Resource<HomeAndLandPackageDetailModel>> {
        return homeLandPackageDetailLiveData
    }


    fun getFavoritesListLiveData(): LiveData<Resource<ArrayList<HouseListModel>>> {
        return favoriteListLiveData
    }

    private val recentSearchDataLive =
        MutableLiveData<Resource<RecentSearchDataResponseModel>>()

    private val setRecentSearchDataLive =
        MutableLiveData<Resource<RecentSearchDataResponseModel>>()

    fun setRecentSearchLiveData(): LiveData<Resource<RecentSearchDataResponseModel>> {
        return setRecentSearchDataLive
    }

    fun getRegionsLiveData(): LiveData<Resource<ArrayList<StateRegionModel>>> {
        return regionsLiveData
    }

    fun getRecentSearchLiveData(): LiveData<Resource<RecentSearchDataResponseModel>> {
        return recentSearchDataLive
    }

    fun getFavorite(): LiveData<Resource<FavoriteResponseModel>> {
        return favoriteLiveData
    }

    fun getHnLPackagesLiveData(): LiveData<Resource<HnLPackagesModel>> {
        return hnLiveData
    }

    fun getHnLPackagesLiveDataFirst(): LiveData<Resource<HnLPackagesModel>> {
        return hnLiveDataFirst
    }

    fun getHnLMinAndMaxPriceLiveData(): LiveData<Resource<HnLMinAndMaxPriceModel>> {
        return hnLMinAndMaxLiveData
    }


    fun fetchRegions(stateId: Int) {
        viewModelScope.launch {
            regionsLiveData.postValue(Resource.loading(null))
            try {
                val regionsData = apiHelper.getAllRegions(stateId)
                regionsLiveData.postValue(Resource.success(regionsData.statesList))
            } catch (e: TimeoutCancellationException) {
                regionsLiveData.postValue(Resource.error("TimeoutCancellationException", null))
            } catch (e: Exception) {
                regionsLiveData.postValue(
                    Resource.error(
                        "Something went wrong!! Please try again later",
                        null
                    )
                )
            }
        }
    }

    fun fetchHnLPackages(userId: Int, jsonObject: JsonObject) {
        viewModelScope.launch {
            hnLiveData.postValue(Resource.loading(null))
            try {
                val hnlpackagesData = apiHelper.getHnLPackages(userId, jsonObject)
                hnLiveData.postValue(Resource.success(hnlpackagesData))
            } catch (e: TimeoutCancellationException) {
                hnLiveData.postValue(Resource.error("TimeoutCancellationException", null))
            } catch (e: Exception) {
                hnLiveData.postValue(
                    Resource.error(
                        "Something went wrong!! Please try again later",
                        null
                    )
                )
            }
        }
    }

    fun fetchHnLPackagesFirst(userId: Int, jsonObject: JsonObject) {
        viewModelScope.launch {
            hnLiveDataFirst.postValue(Resource.loading(null))
            try {
                val hnlpackagesData = apiHelper.getHnLPackages(userId, jsonObject)
                hnLiveDataFirst.postValue(Resource.success(hnlpackagesData))
            } catch (e: TimeoutCancellationException) {
                hnLiveDataFirst.postValue(Resource.error("TimeoutCancellationException", null))
            } catch (e: Exception) {
                hnLiveDataFirst.postValue(
                    Resource.error(
                        "Something went wrong!! Please try again later",
                        null
                    )
                )
            }
        }
    }

    fun fetchMinAndMaxPrice(jsonObject: JsonObject) {
        viewModelScope.launch {
            hnLMinAndMaxLiveData.postValue(Resource.loading(null))
            try {
                val hnlMinAndMaxPrice = apiHelper.getMinAndMaxPrice(jsonObject)
                hnLMinAndMaxLiveData.postValue(Resource.success(hnlMinAndMaxPrice))
            } catch (e: TimeoutCancellationException) {
                hnLMinAndMaxLiveData.postValue(Resource.error("TimeoutCancellationException", null))
            } catch (e: Exception) {
                hnLMinAndMaxLiveData.postValue(
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

    fun getAllFavorites(jsonObject: JsonObject) {
        viewModelScope.launch {
            favoriteListLiveData.postValue(Resource.loading(null))
            try {
                val favoriteListData = apiHelper.getAllFavorites(jsonObject)
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

    fun getRecentSearchData(userId: Int, typeId: Int, stateId: Int) {
        viewModelScope.launch {
            recentSearchDataLive.postValue(Resource.loading(null))
            try {
                val favoriteListData = apiHelper.getRecentSearchData(userId, typeId, stateId)
                recentSearchDataLive.postValue(Resource.success(favoriteListData))
            } catch (e: TimeoutCancellationException) {
                recentSearchDataLive.postValue(Resource.error("TimeoutCancellationException", null))
            } catch (e: Exception) {
                recentSearchDataLive.postValue(
                    Resource.error(
                        "Something went wrong!! Please try again later",
                        null
                    )
                )
            }
        }

    }

    fun setRecentSearchData(jsonObject: RecentSearchObjectRequest) {
        viewModelScope.launch {
            setRecentSearchDataLive.postValue(Resource.loading(null))
            try {
                val favoriteListData = apiHelper.setRecentSearch(jsonObject)
                setRecentSearchDataLive.postValue(Resource.success(favoriteListData))
            } catch (e: TimeoutCancellationException) {
                setRecentSearchDataLive.postValue(
                    Resource.error(
                        "TimeoutCancellationException",
                        null
                    )
                )
            } catch (e: Exception) {
                setRecentSearchDataLive.postValue(
                    Resource.error(
                        "Something went wrong!! Please try again later",
                        null
                    )
                )
            }
        }

    }

    fun getHomeAndLandPackageDetailApi(packageId: Int) {
        viewModelScope.launch {
            homeLandPackageDetailLiveData.postValue(Resource.loading(null))
            try {
                val homeLandPackageDetailModel =
                    apiHelper.getHomeAndLandPackageDetailPageApi(packageId)
                homeLandPackageDetailLiveData.postValue(Resource.success(homeLandPackageDetailModel))
            } catch (e: TimeoutCancellationException) {
                setRecentSearchDataLive.postValue(
                    Resource.error(
                        "TimeoutCancellationException",
                        null
                    )
                )
            } catch (e: Exception) {
                homeLandPackageDetailLiveData.postValue(
                    Resource.error(
                        "Something went wrong!! Please try again later",
                        null
                    )
                )
            }
        }
    }


}