package com.dmss.burbankapp.ui.myfavourite

import android.os.Bundle
import android.os.Handler
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.dmss.burbankapp.R
import com.dmss.burbankapp.data.api.ApiHelperImpl
import com.dmss.burbankapp.data.api.RetrofitBuilder
import com.dmss.burbankapp.data.apiUtils.Status
import com.dmss.burbankapp.data.apiUtils.ViewModelFactory
import com.dmss.burbankapp.data.local.CustomSharedPreferences
import com.dmss.burbankapp.data.local.entity.DatabaseBuilder
import com.dmss.burbankapp.data.local.entity.DatabaseHelperImpl
import com.dmss.burbankapp.data.model.*
import com.dmss.burbankapp.databinding.*
import com.dmss.burbankapp.ui.dashboard.DashboardViewModel
import com.dmss.burbankapp.ui.designs.MyCollectionPlacesFragment
import com.dmss.burbankapp.ui.homeandlandplaces.HomeLandPlaceFragment
import com.dmss.burbankapp.ui.main.MainActivity
import com.dmss.burbankapp.ui.mydisplay.DisplayHomesViewModel
import com.dmss.burbankapp.ui.mydisplay.DisplayToolbarViewModel
import com.dmss.burbankapp.ui.mydisplay.favoritesDisplays.FavoriteDisplaysFragment
import com.dmss.burbankapp.ui.view.FavoritesNotAvailable
import com.dmss.burbankapp.utils.AppConstants
import com.dmss.burbankapp.utils.AppUtils
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import common.AppController
import timber.log.Timber

class MyFavouritesFragment: Fragment() {
    lateinit var binding: MyfavouritesBinding
    lateinit var toolBinding: HomelandToolBinding
    lateinit var profileWithBadgeBinding: ProfileWithBadgeBinding

    lateinit var myPreferences: CustomSharedPreferences
    lateinit var dashboardViewModel: DashboardViewModel
    lateinit var displayHomeViewModel: DisplayHomesViewModel
    var isHomeAndLandRecentSearchDataEmpty: Boolean = true
//    lateinit var profileLayoutBinding: ProfileLayoutBinding
    var favoritesNotAvailable: FavoritesNotAvailable? = null

    var recentSearchJsonModel: SearchJsonModel?=null
    var minimumPriceForPriceRange: Double? = null
    var maximumPriceForPriceRange: Double? = null
    var isMyCollectionRecentSearchDataEmpty: Boolean = true
    lateinit var myCollectionRecentSearchText: String
    var recentSearchHashMap: HashMap<String, ArrayList<NewHomeJsonObject>> = LinkedHashMap()
    private lateinit var displayToolbarViewModel: DisplayToolbarViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {

        super.onActivityCreated(savedInstanceState)
        initViews()
        setupViewModel()
        profileCount()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = MyfavouritesBinding.inflate(inflater, container, false)
        toolBinding = HomelandToolBinding.bind(binding.root)
        profileWithBadgeBinding = ProfileWithBadgeBinding.bind(binding.root)

        return binding.root
    }
    private fun profileCount(){
         displayToolbarViewModel = ViewModelProviders.of(requireActivity())[DisplayToolbarViewModel::class.java]

        displayToolbarViewModel.favouritesCount.observe(viewLifecycleOwner, Observer<Int> { item ->
            setupViewModel()
        })
    }
    private fun initViews() {

        myPreferences = CustomSharedPreferences(AppController.getInstance())
        toolBinding.tvTool.text="View your saved favourites"
        favoritesNotAvailable = FavoritesNotAvailable(activity!!)
        AppConstants.newHomeHashMap.putAll(recentSearchHashMap)
//        profileWithBadgeBinding.profileNotification.text=AppConstants.TotalMyFavs.toString()
        var isUserLoggedIn = myPreferences.getUserLogin()
        if (!isUserLoggedIn) {
            profileWithBadgeBinding.tvFavorites.background = resources.getDrawable(R.drawable.disable_rectangle_new)
            profileWithBadgeBinding.profileNotification.visibility=View.GONE
        }
        toolBinding.ivBack.setOnClickListener {
            activity?.onBackPressed()
        }
        profileWithBadgeBinding.tvFavorites.visibility=View.GONE
        profileWithBadgeBinding.profileNotification.visibility=View.GONE

        profileWithBadgeBinding.tvFavorites.setOnClickListener {

            var isUserLoggedIn = myPreferences.getUserLogin()
            if (isUserLoggedIn) {
                /*  val bundle = Bundle()
                  bundle.putBoolean(AppConstants.SELECT_FAVORITE, true)
                  loadFragment(MyCollectionPlacesFragment(), bundle)*/
                (activity as MainActivity).showProfileDialog(activity!!)

            } else {
                AppUtils.showPleaseLoginDialog(
                    requireContext(),
                    requireActivity(),
                    getString(R.string.Please_login_to_view_edit_profile)
                )
            }

        }
        recentSearchJsonModel?.let {
            AppConstants.setRecentSearchData(it)
        }
        binding.rlHomeAndLand.setOnClickListener {
            if (isHomeAndLandRecentSearchDataEmpty) {
                showHideViews(3)
                var userId: Int = myPreferences.getUserId()
                // var typeId:Int =mPreferences.
                var stateId: Int = myPreferences.getStateID()
                dashboardViewModel.getRecentSearchData(userId, 1, stateId)

            } else {
                AppUtils.showCustomCenterToast(AppController.getInstance(), "Recent Searches not available")
            }
        }
        binding.rlMyDisplays.setOnClickListener {
//            var notification = AppConstants.MY_SAVED_DISPLAY.toInt()
//            if (notification == 0) {
//                showNoFavoriteDialog()
//            } else {
                showHideViews(6)
//            }
        }
        binding.rlHomeAndDesign.setOnClickListener {

//            var notification = AppConstants.HOME_DESIGN_NOTIFICATION.toInt()
//            if (notification == 0) {
//                showNoFavoriteDialog()
//            } else {
                showHideViews(4)
//            }

        }
        binding.llMyHomeAndLand.setOnClickListener {

            val bundle = Bundle()
            bundle.putBoolean(AppConstants.SELECT_FAVORITE, true)
            minimumPriceForPriceRange?.let { it1 -> bundle.putDouble("MIN_PRICE", it1) }
            maximumPriceForPriceRange?.let { it1 -> bundle.putDouble("MAX_PRICE", it1) }
            val homeLandCount=binding.tvHomeandlandNotification.text.toString().toInt()
            if(homeLandCount==0){
                showNoFavoriteDialog()

            }else {
                loadFragment(
                    HomeLandPlaceFragment(),
                    bundle
                )
            }
        }
        binding.llHomandlandsearchview.setOnClickListener {
          /*  binding.navigation.selectedItemId = R.id.navigation_homeandland
            toolBinding.tvHeaderDesc.visibility = View.VISIBLE
            toolBinding.tvMy.text = "My"
            toolBinding.tvHeaderDesc.text = getString(R.string.homeandLand)*/
            val bundle = Bundle()
            bundle.putBoolean(AppConstants.SELECT_FAVORITE, false)
            minimumPriceForPriceRange?.let { it1 -> bundle.putDouble("MIN_PRICE", it1) }
            maximumPriceForPriceRange?.let { it1 -> bundle.putDouble("MAX_PRICE", it1) }
            recentSearchJsonModel?.let {
                AppConstants.setRecentSearchData(it)
            }
            displayToolbarViewModel.setMainHeaderForDisplayHomes(getString(R.string.house) + "," + getString(R.string.and_land))

            loadFragment(
                HomeLandPlaceFragment(),
                bundle
            )
        }
        binding.llHomedesignsearchview1.setOnClickListener {
            /*  binding.navigation.selectedItemId = R.id.navigation_homeandland
              toolBinding.tvHeaderDesc.visibility = View.VISIBLE
              toolBinding.tvMy.text = "My"
              toolBinding.tvHeaderDesc.text = getString(R.string.homeandLand)*/
            if (AppConstants.newHomeHashMap.size > 0) {
                AppConstants.newHomeHashMap.clear()
            }
            AppConstants.newHomeHashMap.putAll(recentSearchHashMap)
          val bundle = Bundle()
            bundle.putBoolean(AppConstants.SELECT_FAVORITE, false)
            displayToolbarViewModel.setMainHeaderForDisplayHomes(getString(R.string.home) + "," + getString(R.string.design))
            loadFragment(
                MyCollectionPlacesFragment(),
                bundle
            )


        }
        binding.rlMyCollection.setOnClickListener {
            if (isMyCollectionRecentSearchDataEmpty) {
                showHideViews(2)
                var userId: Int = myPreferences.getUserId()
                // var typeId:Int =mPreferences.
                var stateId: Int = myPreferences.getStateID()

                dashboardViewModel.getRecentSearchDataMyCollection(userId, 2, stateId)
            } else {
                AppUtils.showCustomCenterToast(AppController.getInstance(), "Recent Searches not available")
            }

        }
        binding.llMyHomeAndDesign.setOnClickListener {
            var homedesignCount = binding.ivHomedesignNotification.text.toString()
            if (homedesignCount.toInt() == 0) {
                showNoFavoriteDialog()
            } else {
            val bundle = Bundle()
            bundle.putBoolean(AppConstants.SELECT_FAVORITE, true)
            loadFragment(
                MyCollectionPlacesFragment(),
                bundle
            )
        }
        }
        binding.llMySavedDisplays.setOnClickListener {
//            binding.navigation.selectedItemId = R.id.navigation_display
//            toolBinding.tvMy.visibility = View.VISIBLE
//            toolBinding.tvHeaderDesc.visibility = View.GONE
//            toolBinding.tvMy.text = "Displays"
           val displaysCount= binding.tvMydisplaysNotification.text.toString().toInt()
            if(displaysCount==0){
                showNoFavoriteDialog()
            }else {
                val bundle = Bundle()
                bundle.putBoolean(AppConstants.SELECT_FAVORITE, false)
                loadFragment(
                    FavoriteDisplaysFragment(),
                    bundle
                )
            }
        }
    }
    private fun setupViewModel() {
        dashboardViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(
                ApiHelperImpl(RetrofitBuilder.apiService),
                DatabaseHelperImpl(DatabaseBuilder.getInstance(AppController.getInstance()))
            )
        ).get(DashboardViewModel::class.java)

        displayHomeViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(
                ApiHelperImpl(RetrofitBuilder.apiService),
                DatabaseHelperImpl(DatabaseBuilder.getInstance(AppController.getInstance()))
            )
        ).get(DisplayHomesViewModel::class.java)
        getRecentSearchForHomeLandAndMyCollection()
    }
        private fun getRecentSearchForHomeLandAndMyCollection() {
        val userId: Int = myPreferences.getUserId()
        val stateId: Int = myPreferences.getStateID()
        dashboardViewModel.getRecentSearchData(userId, 1, stateId)
        dashboardViewModel.getRecentSearchDataMyCollection(userId, 2, stateId)
            getUserFavoritesDisplays()

            setUpObserver()

        }

    private fun getUserFavoritesDisplays() {
        displayHomeViewModel.getUserFavoritesDisplays()
    }
    private fun setUpObserver() {
            dashboardViewModel.getRecentSearchLiveData().observe(viewLifecycleOwner, Observer {
                when (it.status) {
                    Status.SUCCESS -> {
                        binding.profileProgress.visibility = View.GONE
                        var regionsData: RecentSearchDataResponseModel? = it.data
                        if (regionsData != null && regionsData.status) {
                            recentSearchJsonModel = regionsData.searchJsonModel

                            regionsData.searchJsonModel?.let { model ->
                                minimumPriceForPriceRange = model.minPrice
                                maximumPriceForPriceRange = model.maxPrice

                            }

                            if (regionsData.status) {
                                isHomeAndLandRecentSearchDataEmpty = true
                                AppConstants.HOME_AND_LAND_NOTIFICATION =
                                    regionsData.userfavourites.toString()
                                var minPrice: Double =
                                    (regionsData.searchJsonModel?.minPrice ?: 0.0)
                                var maxPrice: Double =
                                    (regionsData.searchJsonModel?.maxPrice ?: 0.0)

                                var minValue = minPrice.toInt()
                                var maxValue = maxPrice.toInt()
                                var totalPrice: String =
                                    "$${minValue / 1000}K to $${maxValue / 1000}K"

                                AppConstants.MIN_PRICE =
                                    regionsData.searchJsonModel?.minPrice.toString()
                                AppConstants.MAX_PRICE =
                                    regionsData.searchJsonModel?.maxPrice.toString()

                                if (this::binding.isInitialized) {

                                    binding.tvHomeLandPrice.text = totalPrice
                                    if (regionsData.userfavourites == 0) {
                                        binding.tvHomeLandSavePackages.text =
                                            ("NO SAVED PACKAGES")
                                        binding.tvHomeandlandNotification.text = "0"
                                        binding.tvHomeLandSavePackages.background=resources.getDrawable(R.drawable.disable_gery)
                                    } else {
                                        if(regionsData.userfavourites==1) {
                                            binding.tvHomeLandSavePackages.text =
                                                ("${regionsData.userfavourites} SAVED PACKAGE")
                                        }else{
                                            binding.tvHomeLandSavePackages.text =
                                                ("${regionsData.userfavourites} SAVED PACKAGES")
                                        }
                                        if (regionsData.userfavourites != null) {
                                            binding.tvHomeandlandNotification.text =
                                                regionsData.userfavourites.toString()
                                        }
                                        binding.tvHomeLandSavePackages.background=resources.getDrawable(R.drawable.enable_orange_bg)


                                    }
                                    if (regionsData.searchJsonModel?.regionsList?.size ?: 0 > 0) {

                                        if (regionsData.searchJsonModel?.regionsList?.get(0)?.regionName?.isNotEmpty() == true) {
                                            var regionData: String =
                                                regionsData.searchJsonModel?.regionsList!![0].regionName

                                            AppConstants.SELECTED_REGION = regionData
//                                            val regionText = "Region &nbsp; <font color=#5c5e5e>$regionData</font>"
//                                            binding.tvHomeLandRegion.text = Html.fromHtml(regionText, HtmlCompat.FROM_HTML_MODE_LEGACY)
                                            binding.tvHomeLandRegionValue.text = AppConstants.SELECTED_REGION.trim()

                                        }

                                        val bedRooms = regionsData.searchJsonModel?.BedRoomFilters?.filter { it.IsChecked }
                                        when(bedRooms?.size){
                                            1 ->{
                                                bedRooms.forEach {
                                                    binding.tvBedrooms.text = it.displayName
                                                }
                                            }
                                            2 ->{
                                                binding.tvBedrooms.text = "5+"
                                            }
                                            else ->{
                                                binding.tvBedrooms.text = "All"
                                            }
                                        }
                                    }
                                    if (regionsData.searchJsonModel?.BathRoomFilters?.size ?: 0 > 0) {
                                        var arr=ArrayList<Int>()
                                        for (bathroom in regionsData.searchJsonModel?.BathRoomFilters!!) {
                                            if (bathroom.IsChecked) {
                                                arr.add(bathroom.value)
                                            }

                                        }
                                        AppConstants.FILTER_Bathrooms= arr.toTypedArray()
                                    }
                                    if (regionsData.searchJsonModel?.CarSpaces?.size ?: 0 > 0) {
                                        var arr=ArrayList<Int>()
                                        for (carspace in regionsData.searchJsonModel?.CarSpaces!!) {
                                            if (carspace.IsChecked) {
                                                arr.add(carspace.value)
                                            }

                                        }
                                        AppConstants.FILTER_CarSpaces= arr.toTypedArray()
                                    }
                                    if (regionsData.searchJsonModel?.StoreyFilters?.size ?: 0 > 0) {
                                        var storeyString = ""
                                        val storeyFilter = regionsData.searchJsonModel?.StoreyFilters?.filter { it.IsChecked }
                                        if (storeyFilter!!.size>1) storeyString = "All"
                                        else{
                                            for (searchSubModel in regionsData.searchJsonModel?.StoreyFilters!!) {
                                                if (searchSubModel.IsChecked) {
                                                    storeyString = searchSubModel.displayName
                                                }
                                            }
                                        }
                                        binding.tvStorey.text = storeyString
                                    }
                                }


                            } else {
                                showSnackbar(
                                    binding.llMyCollection,
                                    regionsData.FetchData
                                )
                            }

                        } else {
                            AppConstants.HOME_AND_LAND_NOTIFICATION = "0"
                            isHomeAndLandRecentSearchDataEmpty = false
                        }

                    }
                    Status.LOADING -> {
                        binding.profileProgress.visibility = View.VISIBLE
                    }
                    Status.ERROR -> {
                        isHomeAndLandRecentSearchDataEmpty = false
                        binding.profileProgress.visibility = View.GONE
                    }
                }

            })

        dashboardViewModel.getRecentSearchMyCollectionLiveData().observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                   binding.profileProgress.visibility = View.GONE
                    var regionsData: MyCollectionRecentSearchModel? = it.data
                    if (regionsData != null && regionsData.status) {
                        isMyCollectionRecentSearchDataEmpty = true
                        val newHomeJsonArrayList = ArrayList<NewHomeJsonObject>()

                        if (regionsData.searchJsonList.size > 0) {
                            for (myCollectionRecentSearchAnswerModel: MyCollectionRecentSearchAnswerModel in regionsData.searchJsonList) {

                                var newHomeJsonObject: NewHomeJsonObject? =
                                    if (myCollectionRecentSearchAnswerModel.minValue == null && myCollectionRecentSearchAnswerModel.maxValue == null) {
                                        NewHomeJsonObject(
                                            myCollectionRecentSearchAnswerModel.feature,
                                            myCollectionRecentSearchAnswerModel.question,
                                            myCollectionRecentSearchAnswerModel.answer
                                        )
                                    } else {
                                        NewHomeJsonObject(
                                            myCollectionRecentSearchAnswerModel.feature,
                                            myCollectionRecentSearchAnswerModel.question,
                                            myCollectionRecentSearchAnswerModel.answer,
                                            myCollectionRecentSearchAnswerModel.minValue,
                                            myCollectionRecentSearchAnswerModel.maxValue
                                        )
                                    }



                                if (newHomeJsonObject != null) {
                                    if (newHomeJsonObject.feature != "resultsCount") {
                                        newHomeJsonArrayList.add(newHomeJsonObject)
                                    } else {
                                        if (this::binding.isInitialized) {
                                            binding.tvMyCollectionNotification.text =
                                                newHomeJsonObject.answer.toString()
                                        }
                                    }

                                }

                            }
                        }
                        recentSearchHashMap[AppConstants.MYCOLLECTION_RECENT_SEARCH_KEY] =
                            newHomeJsonArrayList


                        AppConstants.HOME_DESIGN_NOTIFICATION =
                            regionsData.userfavourites.toString()
                        myCollectionRecentSearchText = ""

                        if (this::binding.isInitialized) {
                            binding.ivHomedesignNotification.text =
                                regionsData.userfavourites.toString()


                            if (regionsData.userfavourites == 0) {
                                binding.tvHomeDesignSaved.text =
                                    ("NO SAVED DESIGNS")
                                binding.tvHomeDesignSaved.background=resources.getDrawable(R.drawable.disable_gery)
                            } else {
                                binding.tvHomeDesignSaved.background=resources.getDrawable(R.drawable.enable_orange_bg)

                                if(regionsData.userfavourites==1){
                                    binding.tvHomeDesignSaved.text =
                                        ("${regionsData.userfavourites} SAVED DESIGN")
                                }else {
                                    binding.tvHomeDesignSaved.text =
                                        ("${regionsData.userfavourites} SAVED DESIGNS")
                                }
                            }


                        }
                        if (regionsData.searchJsonList.size > 0) {

                            if (regionsData.searchJsonList.size > 0) {
                                for (resultCountModel in regionsData.searchJsonList) {
                                    if (resultCountModel.feature == "resultsCount") {
                                        if (resultCountModel.answer.isNotEmpty()) {
                                            // myCollectionRecentSearchText = resultCountModel.feature
                                            AppConstants.MY_COLLECTION_NOTIFICATION =
                                                resultCountModel.answer
                                            if (this::binding.isInitialized) {
                                                binding.ivMyDetailsNotification.text =
                                                    resultCountModel.answer
                                                binding.tvMyCollectionSaveDesign.text =
                                                    ("${resultCountModel.answer} DESIGNS")
                                            }
                                        } else {
                                            AppConstants.MY_COLLECTION_NOTIFICATION = "0"
                                        }
                                    }

                                }


                            }

                            val isLotWidthAvailable = regionsData.searchJsonList.any { it.feature == "Lot Width"}
//                            for (myCollectionRecentSearchAnswerModel in regionsData.searchJsonList.filter { it.answer != "I don't mind" }) {
                            for (myCollectionRecentSearchAnswerModel in regionsData.searchJsonList.filter { it.answer != "I don't mind"  && it.answer != "I do not want this"}) {
                                when (myCollectionRecentSearchAnswerModel.feature) {

                                    "Lot Width" -> {
                                        if (myCollectionRecentSearchAnswerModel.maxValue.isNotEmpty()) {
                                            var answer = AppUtils.roundRemainingString(
                                                myCollectionRecentSearchAnswerModel.maxValue
                                            )
                                            myCollectionRecentSearchText += "$answer" + "M"
                                        }
                                    }

                                    "Storeys" -> {
                                        var storey = ""
                                        if (myCollectionRecentSearchAnswerModel.answer == "1") {
                                            storey = if (isLotWidthAvailable) " | Single" else "Single"
                                        } else if (myCollectionRecentSearchAnswerModel.answer == "2") {
                                            storey = if (isLotWidthAvailable) " | Double" else "Double"
                                        }
                                        myCollectionRecentSearchText += storey

                                    }
                                    "No Of Bedrooms" -> {
                                        var bedRooms: String = ""

                                        if (myCollectionRecentSearchAnswerModel.answer == "3") {
                                            bedRooms = "3 Bed"
                                        } else if (myCollectionRecentSearchAnswerModel.answer == "4") {
                                            bedRooms = "4 Bed"

                                        } else if (myCollectionRecentSearchAnswerModel.answer == "5") {
                                            bedRooms = "5 Bed"
                                        }
                                        myCollectionRecentSearchText += " | $bedRooms"

                                    }
                                    "Grand Alfresco" -> {
                                        myCollectionRecentSearchText += " | Alfresco"

                                    }
                                    "Storage (more than 1 per room)" -> {
                                        myCollectionRecentSearchText += "| Storage (more than 1 per room)"

                                    }
                                    "European Laundry" -> {
                                        myCollectionRecentSearchText += " | European Laundry"
                                    }
                                    "Separate Kids Living Area" -> {
                                        myCollectionRecentSearchText += " | Separate Kids Living Area"
                                    }
                                    "Separate Living Area" -> {
                                        myCollectionRecentSearchText += " | Separate Living Area"
                                    }
                                    "Straight Corridor" -> {
                                        myCollectionRecentSearchText += " | Straight Corridor"

                                    }
                                    "Living/Meals Entire Rear" -> {
                                        myCollectionRecentSearchText += " | Living/Meals Entire Rear"

                                    }
                                    "Study" -> {
                                        myCollectionRecentSearchText += " | Study"

                                    }
                                    "Minor Bedrooms Wing" -> {
                                        myCollectionRecentSearchText += " | Minor Bedrooms Wing"

                                    }
                                    "Bedroom At Front" -> {
                                        myCollectionRecentSearchText += " | Bedroom At Front"

                                    }

                                    "Price" -> {
                                        myCollectionRecentSearchText += " | Price"

                                    }
                                    /* "resultsCount" -> {
                                         myCollectionRecentSearchText =
                                             "Take a Quick Survey to Find Your\nPerfect Design"
                                     }*/
                                }


                            }

                            if (regionsData.searchJsonList.isNotEmpty()) {
                                if (regionsData.searchJsonList.size == 2) {
                                    var model = regionsData.searchJsonList[0]
                                    if (model.feature == "resultsCount") {
                                        myCollectionRecentSearchText =
                                            "Take a Quick Survey to Find Your\nPerfect Design"
                                    }
                                } else if (regionsData.searchJsonList.size == 1) {
                                    var model = regionsData.searchJsonList[0]
                                    if (model.feature == "resultsCount") {
                                        myCollectionRecentSearchText =
                                            "Take a Quick Survey to Find Your Perfect Design"
                                    }
                                }
                            }


                        }
                        isMyCollectionRecentSearchDataEmpty =
                            myCollectionRecentSearchText.isNotEmpty()
                        if (this::binding.isInitialized) {
                           /* binding.tvMyCollectionQuiz.text =
                                myCollectionRecentSearchText*/
                            if(myCollectionRecentSearchText.isNotEmpty()&&myCollectionRecentSearchText.first().toString()== " "){
                                binding.tvMyCollectionRecentText.text=myCollectionRecentSearchText.drop(2)
                            }else {
                                if(myCollectionRecentSearchText.isNotEmpty()){
                                    binding.tvMyCollectionRecentText.text=myCollectionRecentSearchText
                                }else{
                                    binding.tvMyCollectionRecentText.text = "Selected All Designs"
                                }
                            }
                        }

                    } else {
                        isMyCollectionRecentSearchDataEmpty = false
                    }
                }
                Status.LOADING -> {
                    binding.profileProgress.visibility = View.VISIBLE
                }
                Status.ERROR -> {
                    binding.profileProgress.visibility = View.GONE
                }
            }
        })

        displayHomeViewModel.getUserFavoritesDisplaysLiveData.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
//                    customProgressDialog?.dismissProgress()
                    if (it.data != null) {
                        if (it.data.status || !it.data.status) {
                            var totalResponse: UserFavoriteDisplaysResponseModel = it.data
                            if (totalResponse.userFavorites != null && totalResponse.userFavorites.size > 0) {

                                AppConstants.MY_SAVED_DISPLAY =
                                    totalResponse.userFavorites.size.toString()
//                                Timber.e("My Saved Displays API ${AppConstants.MY_SAVED_DISPLAY}")
                                if (this::binding.isInitialized) {
                                    if(AppConstants.MY_SAVED_DISPLAY.toInt()==1){
                                        binding.tvMySavedDisplays.text =
                                            ("${AppConstants.MY_SAVED_DISPLAY} SAVED DISPLAY")
                                    }else {
                                        binding.tvMySavedDisplays.text =
                                            ("${AppConstants.MY_SAVED_DISPLAY} SAVED DISPLAYS")
                                    }
                                    binding.tvMydisplaysNotification.text =
                                        AppConstants.MY_SAVED_DISPLAY

                                    Timber.e("My Saved Displays1 ${AppConstants.MY_SAVED_DISPLAY}")
                                    binding.tvMySavedDisplays.background=resources.getDrawable(R.drawable.enable_orange_bg)

                                }
                            } else {
                                AppConstants.MY_SAVED_DISPLAY = "0"
                                if (this::binding.isInitialized) {
                                    binding.tvMydisplaysNotification.text = "0"
                                    binding.tvMySavedDisplays.text = "NO SAVED DISPLAYS"
                                    binding.tvMySavedDisplays.background=resources.getDrawable(R.drawable.disable_gery)
                                }
                            }


                        }
                    }
                }
                Status.LOADING -> {
//                    customProgressDialog?.showProgress()
                }
                Status.ERROR -> {
//                    customProgressDialog?.dismissProgress()
                }
            }

        })
    }
    fun showHideViews(position: Int) {
        when (position) {
            0 -> {
                binding.elShare.collapse()
                binding.elMycollection.collapse()
                binding.elHomeandland.collapse()
                binding.elHomedesign.collapse()
                binding.elAppsettings.collapse()
                binding.elMydisplay.collapse()

                binding.ivArrowMycollection.reverse()
                binding.ivArrowHomeandland.reverse()
                binding.ivArrowHomeanddesign.reverse()
                binding.ivArrowMydisplay.reverse()


            }
           /* 1 -> {
                el_mydetails.collapse()
                el_mycollection.collapse()
                el_homeandland.collapse()
                el_homedesign.collapse()
                el_appsettings.collapse()
                profileLayoutBinding.elMydisplay.collapse()

                spin_arrow_mydetails.reverse()
                iv_arrow_mycollection.reverse()
                iv_arrow_homeandland.reverse()
                iv_arrow_homeanddesign.reverse()
                iv_arrow_appsettings.reverse()
                profileLayoutBinding.ivArrowMydisplay.reverse()

                if (el_share.isExpanded) {
                    iv_arrow_share.reverse()
                    el_share.collapse()
                } else {
                    iv_arrow_share.rotate()
                    el_share.expand()
                }
            }
            2 -> {
                el_mydetails.collapse()
                el_share.collapse()
                el_homeandland.collapse()
                el_homedesign.collapse()
                el_appsettings.collapse()
                profileLayoutBinding.elMydisplay.collapse()

                spin_arrow_mydetails.reverse()
                iv_arrow_share.reverse()
                iv_arrow_homeandland.reverse()
                iv_arrow_homeanddesign.reverse()
                iv_arrow_appsettings.reverse()
                profileLayoutBinding.ivArrowMydisplay.reverse()

                if (el_mycollection.isExpanded) {
                    iv_arrow_mycollection.reverse()
                    el_mycollection.collapse()
                } else {
                    iv_arrow_mycollection.rotate()
                    el_mycollection.expand()
                }
            }*/
            3 -> {
                binding.elMydetails.collapse()
                binding.elShare.collapse()
                binding.elMycollection.collapse()
                binding.elHomedesign.collapse()
                binding.elAppsettings.collapse()
                binding.elMydisplay.collapse()

//                binding.spinArrowMydetails.reverse()
//                binding.ivArrowShare.reverse()
                binding.ivArrowMycollection.reverse()
                binding.ivArrowHomeanddesign.reverse()
//                binding.ivArrow_aAppsettings.reverse()
                binding.ivArrowMydisplay.reverse()

                if (binding.elHomeandland.isExpanded) {
                    binding.ivArrowHomeandland.reverse()
                    binding.elHomeandland.collapse()
                } else {
                    binding.ivArrowHomeandland.rotate()
                    binding.elHomeandland.expand()
                }
            }
          4 -> {
                  binding.elMydetails.collapse()
                  binding.elShare.collapse()
                  binding.elMycollection.collapse()
                  binding.elHomeandland.collapse()
                  binding.elAppsettings.collapse()
              binding.elMydisplay.collapse()

//              spin_arrow_mydetails.reverse()
              binding.ivArrowShare.reverse()
              binding. ivArrowMycollection.reverse()
              binding.ivArrowHomeandland.reverse()
              binding.ivArrowAppsettings.reverse()
              binding.ivArrowMydisplay.reverse()

                if (binding.elHomedesign.isExpanded) {
                    binding.ivArrowHomeanddesign.reverse()
                    binding.elHomedesign.collapse()
                } else {
                    binding.ivArrowHomeanddesign.rotate()
                    binding.elHomedesign.expand()
                }
            }
           /* 5 -> {
                el_mydetails.collapse()
                el_share.collapse()
                el_mycollection.collapse()
                el_homeandland.collapse()
                el_homedesign.collapse()
                profileLayoutBinding.elMydisplay.collapse()

                spin_arrow_mydetails.reverse()
                iv_arrow_share.reverse()
                iv_arrow_mycollection.reverse()
                iv_arrow_homeandland.reverse()
                iv_arrow_homeanddesign.reverse()
                profileLayoutBinding.ivArrowMydisplay.reverse()

                if (el_appsettings.isExpanded) {
                    iv_arrow_appsettings.reverse()
                    el_appsettings.collapse()
                } else {
                    iv_arrow_appsettings.rotate()
                    el_appsettings.expand()
                }
            }*/
            6 -> {
                binding.elMydetails.collapse()
                binding.elShare.collapse()
                binding.elMycollection.collapse()
                binding.elHomeandland.collapse()
                binding.elHomedesign.collapse()


//                binding.spinArrowMydetails.reverse()
//                iv_arrow_share.reverse()
                binding.ivArrowMycollection.reverse()
                binding.ivArrowHomeandland.reverse()
                binding.ivArrowHomeanddesign.reverse()
                binding.ivArrowMydisplay.reverse()

                if (binding.elMydisplay.isExpanded) {
                    binding.ivArrowMydisplay.reverse()
                    binding.elMydisplay.collapse()
                } else {
                    binding.ivArrowMydisplay.rotate()
                    binding.elMydisplay.expand()
                }
            }


        }
    }
    fun showNoFavoriteDialog() {
        Timber.e("CustomDialog")
       /* favoritesNotAvailable?.let {
            if (!it.isShowing) {
                 it.show()
            }
        }*/
        Toast.makeText(AppController.getInstance(),getString(R.string.favourites_not_available),Toast.LENGTH_SHORT).show()

    }
    private fun showSnackbar(view: View, message: String) {
        val snack = Snackbar.make(view, message, Snackbar.LENGTH_LONG)
        snack.show()
    }
    fun loadFragment(fragment: Fragment, bundle: Bundle) {
        val transaction: FragmentTransaction = activity?.supportFragmentManager!!.beginTransaction()
        fragment.arguments = bundle
        if (fragment.isAdded) {
            return
        }
        transaction.add(R.id.fl_content, fragment,AppConstants.MyFavouriteFlow)
        transaction.addToBackStack(null)
        transaction.commit()
    }

}