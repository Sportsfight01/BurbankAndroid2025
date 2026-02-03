package com.dmss.burbankapp.data.api

import com.dmss.burbankapp.data.model.*
import com.google.gson.JsonObject

interface ApiHelper {

    suspend fun getEmailExist(email: String): CheckEmailModel
    suspend fun getMoreUsers(): List<ApiUser>
    suspend fun getUsersWithError(): List<ApiUser>
    suspend fun getLoginApi(email: String, pass: String, body: JsonObject): LoginModel
    suspend fun signUpApi(body: JsonObject): SignUpModel
    suspend fun updateProfileImage(body: JsonObject): UpdateProfilePicModel
    suspend fun forgetPassword(body: String): ForgetPasswordModel
    suspend fun SaveOrUpdateDeviceDetails(jsonData: JsonObject): DeviceResponseModel
    suspend fun getPromotionsetails(stateId: String): PromotionsResponse

    suspend fun getAllPackages(StateId: String): AllPackagesModel
    suspend fun getAllStates(): StatesResponseModel
    suspend fun getAllRegions(StateId: Int): RegionsResponseModel
    suspend fun getRecentSearch(userId: String, typeId: String, stateId: String): RecentSearchModel
    suspend fun getHomeAndLandQuizDesign(page: String, body: JsonObject): HomeAndLandQuizDesignModel
    suspend fun setFavorite(body: JsonObject): FavoriteResponseModel
    suspend fun getHnLPackages(userId: Int, body: JsonObject): HnLPackagesModel
    suspend fun getMinAndMaxPrice(body: JsonObject): HnLMinAndMaxPriceModel
    suspend fun getAllFavorites(body: JsonObject): AllFavoritesModel
    suspend fun submitEnquireForm(body: JsonObject): EnquireModel
    suspend fun getUserDetails(userId: Int): UserDetailResponseModel
    suspend fun setResetPassword(jsonObject: JsonObject): ResetPasswordModel
    suspend fun setShareAccount(userId: Int): ShareAccountModel
    suspend fun checkEmailForSharing(emailId: String): CheckEmailForSharingModel
    suspend fun setShareAccountWithEmail(userId: Int, jsonObject: JsonObject): ShareAccountModel
    suspend fun getSearchType(): SearchTypeRecentModel
    suspend fun getRecentSearchData(
        userId: Int,
        typeId: Int,
        stateId: Int
    ): RecentSearchDataResponseModel

    suspend fun setRecentSearch(
        jsonObject: RecentSearchObjectRequest
    ): RecentSearchDataResponseModel

    suspend fun setRecentSearchMyCollectionData(
        jsonObject: SetRecentMyCollectionRequest
    ): MyCollectionRecentSearchModel

    suspend fun fetchNewHomeQuiz(stateId: Int): NewHomeQuizModel

    suspend fun fetchNewHomesNextFeature(jsonObject: MyCollectionQuizQuestionRequest): NewHomeNextFeatureResponseModel
    suspend fun fetchNewHomesNextFeatureMinLotWidth(
        jsonObject: MyCollectionQuizQuestionRequest,
        minLotWidth: String
    ): NewHomeNextFeatureResponseModel

    suspend fun fetchHouseCount(jsonObject: JsonObject): NewHomeNextFeatureResponseModel

    suspend fun updateUserInfo(jsonObject: JsonObject): UserInfo
    suspend fun userLogin(jsonObject: JsonObject): UserLoginModel
    suspend fun getHouseNameDetailByName(
        HouseSize: Int,
        houseName: String,
        stateId: Int
    ): HouseNameDetailByNameModel

    suspend fun getRecentSearchDataMyCollection(
        userId: Int, typeId: Int, stateId: Int
    ): MyCollectionRecentSearchModel

    suspend fun getAllFavoritesMyCollection(body: JsonObject): AllFavoritesDesignModel

    suspend fun getHomeAndLandPackageDetailPageApi(packageId: Int): HomeAndLandPackageDetailModel

    suspend fun getMyCollectionHnlDetailByName(
        stateId: Int,
        houseName: String
    ): MyCollectionHnlModel


    suspend fun fetchNewHomesNextFeatureNext(jsonObject: MyCollectionQuizQuestionRequest): NewHomeNextFeatureResponseModel

    suspend fun getDisplaysByStateId(jsonObject: DisplyByStateIdBodyModel): DisplaysByStateIdResponseModel

    suspend fun getNearByDisplaysByRegion(jsonObject: NearByPlaceDataObject): NearByDisplaysResponseModel

    suspend fun getHouseDetailsByEstate(jsonObject: HouseDetailByEstateObject): HouseDetailsResponseModel

    suspend fun fetchDisplayForRegionAndMap(jsonObject: JsonObject): DisplayRegionResponseModel

    suspend fun fetchRegionsByStateId(stateId: Int): RegionsByStateIdResponseModel

    suspend fun addHouseAppointment(obj: HouseAppointmentDataModelObject): HouseAppointmentResponseModel
    suspend fun getUserFavoriteDisplays(
        userId: Int,
        stateId: Int
    ): UserFavoriteDisplaysResponseModel

    suspend fun getDisplayHomesRegionByStateId(
        stateId: Int
    ): DisplayHomeRegionModel

}