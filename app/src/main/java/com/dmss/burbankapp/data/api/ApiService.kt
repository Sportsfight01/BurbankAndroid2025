package com.dmss.burbankapp.data.api


import com.dmss.burbankapp.data.model.*
import com.google.gson.JsonObject
import retrofit2.http.*

interface ApiService {
    @POST("Account/CheckEmailExists")
    suspend fun cheEmailExist(@Query("EmailId") email: String): CheckEmailModel

    @POST("more-users")
    suspend fun getMoreUsers(): List<ApiUser>
    @POST("notifications/SaveOrUpdateDeviceDetails")
    suspend fun SaveOrUpdateDeviceDetails(
        @Body body: JsonObject
    ): DeviceResponseModel
    @GET("error")
    suspend fun getUsersWithError(): List<ApiUser>

    @POST("Account/UserLogin")
    suspend fun getUserLogin(
        @Body jsonObject: JsonObject
    ): LoginModel

    @POST("Account/CreateUser")
    suspend fun signUpApi(
        @Body jsonObject: JsonObject
    ): SignUpModel

    @POST("Account/UpdateProfileImage")
    suspend fun updateProfileImage(
        @Body jsonObject: JsonObject
    ): UpdateProfilePicModel

    @POST("Account/ForgotPassword")
    suspend fun forgetPassword(@Query("EmailId") EmailId: String): ForgetPasswordModel


    @POST("HomeAndLand/GetAllPackages")
    suspend fun getAllPackages(@Query("StateId") StateId: String): AllPackagesModel


    @GET("Common/GetStates")
    suspend fun getStates(): StatesResponseModel

    @POST("Common/GetRegions")
    suspend fun getRegions(@Query("StateId") StateId: Int): RegionsResponseModel

    @POST("Common/GetRecentSearchData")
    suspend fun fetchRecentSearch(
        @Query("UserId") userId: String,
        @Query("TypeId") typeId: String,
        @Query("StateId") stateId: String
    ): RecentSearchModel

    @POST("Common/SetRecentSearch")
    suspend fun setRecentSearch(@Body jsonObject: JsonObject): SetRecentSearchModel

    @POST("HomeAndLand/HomeAndLandQuizDesigns")
    suspend fun getHomeAndLandQuizDesigns(
        @Query("page") page: String,
        @Body jsonObject: JsonObject
    ): HomeAndLandQuizDesignModel

    @POST("Common/Favourites")
    suspend fun setFavorite(@Body jsonObject: JsonObject): FavoriteResponseModel


    @POST("HomeAndLand/GetHnLPackages")
    suspend fun getHnLPackages(
        @Query("UserId") userId: Int,
        @Body body: JsonObject
    ): HnLPackagesModel

    @POST("HomeAndLand/GetHnLMinMaxPrice")
    suspend fun getMinAndMaxPrice(@Body body: JsonObject): HnLMinAndMaxPriceModel

    @POST("Common/GetUserFavouriteDesigns")
    suspend fun getAllFavorites(@Body body: JsonObject): AllFavoritesModel

    @POST("Common/EnquiryForm")
    suspend fun getEnquireForm(@Body body: JsonObject): EnquireModel

    @POST("Account/GetUserDetails")
    suspend fun getUserDetails(@Query("UserId") userId: Int): UserDetailResponseModel

    @POST("Account/ResetPassword")
    suspend fun resetPassword(
        @Body jsonObject: JsonObject
    ): ResetPasswordModel

    @Headers("Content-Type:application/json")
    @POST("Common/ShareAccount")
    suspend fun setShareAccount(@Query("UserId") userId: Int): ShareAccountModel

    @POST("Common/CheckEmailForSharing")
    suspend fun checkEmailForSharing(@Query("EmailId") emailId: String): CheckEmailForSharingModel

    @POST("Common/ShareAccount")
    suspend fun shareAccountWithEmail(
        @Query("UserId") userId: Int,
        @Body body: JsonObject
    ): ShareAccountModel

    @GET("Common/GetSearchType")
    suspend fun getSearchType(): SearchTypeRecentModel

    @POST("Common/GetRecentSearchData")
    suspend fun getRecentSearchData(
        @Query("UserId") userId: Int,
        @Query("TypeId") typeId: Int,
        @Query("StateId") stateId: Int
    ): RecentSearchDataResponseModel

    /*@GET("Common/GetSearchType")
    suspend fun getSearchType(): SearchTypeRecentModel*/

    @POST("Common/SetRecentSearch")
    suspend fun setRecentSearchData(
        @Body body: RecentSearchObjectRequest
    ): RecentSearchDataResponseModel

    @POST("Common/SetRecentSearch")
    suspend fun setRecentSearchMyCollectionData(
        @Body body: SetRecentMyCollectionRequest
    ): MyCollectionRecentSearchModel

    //http://172.17.0.40:7777/api/api/NewHomes/NewHomesQuiz?StateId=11

    @GET("NewHomes/NewHomesQuiz")
    suspend fun fetchNewHomesQuiz(
        @Query("StateId") stateId: Int
    ): NewHomeQuizModel

    @GET("Common/GetPromotions")
    suspend fun getPromotionsDetails(
        @Query("state") stateId: String
    ): PromotionsResponse

    @POST("NewHomes/NewHomesNextFeatures")
    suspend fun fetchNewHomesNextFeature(
        @Body jsonObject: MyCollectionQuizQuestionRequest
    ): NewHomeNextFeatureResponseModel

    @POST("NewHomes/NewHomesNextFeatures")
    suspend fun fetchNewHomesNextFeatureMinLotWidth(
        @Body jsonObject: MyCollectionQuizQuestionRequest, @Query("MinLotWidth") minLotWidth: String
    ): NewHomeNextFeatureResponseModel

    @POST("NewHomes/NewHomesNextFeatures")
    suspend fun fetchHouseCountApi(
        @Body jsonObject: JsonObject
    ): NewHomeNextFeatureResponseModel

    @POST("Account/UpdateUser")
    suspend fun updateUserApi(@Body jsonObject: JsonObject): UserInfo

    @POST("Account/Authenticate")
    suspend fun getUser(@Body jsonObject: JsonObject): UserLoginModel

    @GET("NewHomes/GetHouseDetailsByName")
    suspend fun getHouseDetailsByName(
        @Query("HouseSize") houseSize: Int,
        @Query("HouseName") houseName: String,
        @Query("StateId") stateId: Int
    ): HouseNameDetailByNameModel

    @POST("Common/GetRecentSearchData")
    suspend fun getRecentSearchDataMyCollection(
        @Query("UserId") userId: Int,
        @Query("TypeId") typeId: Int,
        @Query("StateId") stateId: Int
    ): MyCollectionRecentSearchModel


    @POST("Common/GetUserFavouriteDesigns")
    suspend fun getAllFavoritesCollections(@Body body: JsonObject): AllFavoritesDesignModel

    @GET("HomeAndLand/GetHnLPackageDetails")
    suspend fun getHomeAndLandPackageDetails(
        @Query("packageId_Landbank") landBank: Int
    ): HomeAndLandPackageDetailModel


    @GET("HomeAndLand/GetHnLPackageDetailsByName")
    suspend fun getMyCollectionHnlDetailByName(
        @Query("StateId") stateId: Int, @Query("HouseName") houseName: String
    ): MyCollectionHnlModel


    @POST("NewHomes/NewHomesNextFeatures")
    suspend fun fetchNewHomesNextFeatureNext(
        @Body jsonObject: MyCollectionQuizQuestionRequest
    ): NewHomeNextFeatureResponseModel


    @POST("DisplayHomes/DisplaysForRegionAndMap")
    suspend fun fetchDisplayForRegionAndMap(
        @Body jsonObject: JsonObject
    ): DisplayRegionResponseModel

    @GET("DisplayHomes/GetNearByDisplays")
    suspend fun getNearbyDisplays(
        @Query("stateId") stateId: Int,
        @Query("latitude") latitude: String,
        @Query("longitude") longitude: String,
        @Query("userId") userId: Int
    ): NearByDisplaysResponseModel

    @GET("DisplayHomes/GetDisplaysByStateId")
    suspend fun getDisplaysByStateId(
        @Query("stateId") stateId: Int,
        @Query("latitude") latitude: String,
        @Query("longitude") longitude: String,
        @Query("userId") userId: Int,
        @Query("storey") search: String,
        @Query("houseName") houseName: String,
        @Query("houseSize") houseSize: String

    ): DisplaysByStateIdResponseModel

    @GET("DisplayHomes/HouseDetailsByEstate")
    suspend fun getHouseDetailsByEstate(
        @Query("stateId") stateId: Int,
        @Query("estateName") estateName: String,
        @Query("userID") userId: Int
    ): HouseDetailsResponseModel
//POST /api/DisplayHomes/HouseAppointment

    @GET("DisplayHomes/GetRegionsByStateId")
    suspend fun getRegionsByStateId(
        @Query("stateId") stateId: Int
    ): RegionsByStateIdResponseModel


    @POST("DisplayHomes/HouseAppointment")
    suspend fun addHouseAppointment(
        @Body model: HouseAppointmentDataModelObject
    ): HouseAppointmentResponseModel


    // GET /api/DisplayHomes/GetUserFavouriteDisplays

    @GET("DisplayHomes/GetUserFavouriteDisplays")
    suspend fun getUserFavoriteDisplays(
        @Query("UserId") userId: Int,
        @Query("stateId") stateId: Int
    ): UserFavoriteDisplaysResponseModel


    //GET /api/DisplayHomes/GetRegionsByStateId
    @GET("DisplayHomes/GetRegionsByStateId")
    suspend fun getDisplayHomesRegionByStateId(
        @Query("stateId") stateId: Int
    ): DisplayHomeRegionModel
}
