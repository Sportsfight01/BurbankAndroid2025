package com.dmss.burbankapp.ui.homeandland

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
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
import com.dmss.burbankapp.databinding.DashboardProfileWidthDadgeBinding
import com.dmss.burbankapp.databinding.FragmentHomeLandRegionBinding
import com.dmss.burbankapp.databinding.HomelandToolBinding
import com.dmss.burbankapp.databinding.LayoutBottomNextPreviousBinding
import com.dmss.burbankapp.databinding.LayoutRecentSearchBinding
import com.dmss.burbankapp.databinding.ProfileWithBadgeBinding
import com.dmss.burbankapp.ui.homeandlandoprice.HomeLandPriceRangeFragment

import com.dmss.burbankapp.ui.homeandlandplaces.HomeLandPlaceFragment
import com.dmss.burbankapp.ui.homeandlandregions.HomeAndLandRegionsAdapter
import com.dmss.burbankapp.ui.homeandlandregions.HomeAndLandViewModel
import com.dmss.burbankapp.ui.main.MainActivity
import com.dmss.burbankapp.ui.mydisplay.DisplayToolbarViewModel
import com.dmss.burbankapp.ui.view.BreadCrumbAdapter
import com.dmss.burbankapp.ui.view.CustomProgressDialog
import com.dmss.burbankapp.utils.AppConstants
import com.dmss.burbankapp.utils.AppUtils
import com.dmss.burbankapp.viewmodel.HnLbackHandlingViewModel
import com.dmss.burbankapp.viewmodel.ProfilePicViewModel
import com.dmss.burbankapp.viewmodel.SingletonNameViewModelFactory
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import common.AppController
import timber.log.Timber

class HomeLandRegionFragment : Fragment(), HomeAndLandRegionsAdapter.IUpdateState,
    MainActivity.OnBackPressedListener, BreadCrumbAdapter.BreadcrumbItemClickListener {

    private var _selectedRegion: String? = null
    var homeAndLandDtoForPackages: HomeAndLandDtoForPackages? = null
    private var isNotFromFavourites:Boolean = true
    lateinit var binding: FragmentHomeLandRegionBinding
    lateinit var profilePicViewModel: ProfilePicViewModel
    lateinit var singletonNameViewModelFactory: SingletonNameViewModelFactory
    lateinit var homeAndLandViewModel: HomeAndLandViewModel
    lateinit var homeAndLandRegionsAdapter: HomeAndLandRegionsAdapter
    var regionsList: ArrayList<StateRegionModel> = ArrayList()
    lateinit var mPreferences: CustomSharedPreferences
    var selectedRegionId: Int = 0
    lateinit var userInfoModel: UserInfoModel
    var headerText: String = ""
    var bedroomString: String = ""
    var storeyString: String = ""
    var regionHeaderString: String = ""
    var isNextClickable: Boolean = true
    var isRecentSearchClicked:Boolean = false
    var minimumPriceForPriceRange: Double? = null
    var maximumPriceForPriceRange: Double? = null

    private var hnlPackagesModel: HnLPackagesModel? = null
    lateinit var hnLbackHandlingViewModel: HnLbackHandlingViewModel

    lateinit var bottomBinding: LayoutBottomNextPreviousBinding
    var isFirstTimeBackPressed = true

    private var customProgressDialog: CustomProgressDialog? = null
    lateinit var toolBinding: HomelandToolBinding
    lateinit var profileWithBadgeBinding: ProfileWithBadgeBinding

    private lateinit var displayToolbarViewModel: DisplayToolbarViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        clearLocalData()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeLandRegionBinding.inflate(inflater, container, false)
        toolBinding = HomelandToolBinding.bind(binding.root)
       profileWithBadgeBinding = ProfileWithBadgeBinding.bind(binding.root)

        bottomBinding = LayoutBottomNextPreviousBinding.bind(binding.root)
        AppConstants.isHomeLandProfile=true
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {

        super.onActivityCreated(savedInstanceState)
        initViews()
        setUpViewModel()
    }

    private fun setUpViewModel() {
        singletonNameViewModelFactory = SingletonNameViewModelFactory()
        profilePicViewModel = ViewModelProviders.of(this, singletonNameViewModelFactory).get(
            ProfilePicViewModel::class.java
        )
        homeAndLandViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(
                ApiHelperImpl(RetrofitBuilder.apiService),
                DatabaseHelperImpl(DatabaseBuilder.getInstance(AppController.getInstance()))
            )
        ).get(HomeAndLandViewModel::class.java)

        var userId: Int = mPreferences.getUserId()
        // var typeId:Int =mPreferences.
        var stateId: Int = mPreferences.getStateID()


        var isUserLoggedIn = mPreferences.getUserLogin()
        if (isUserLoggedIn) {
            homeAndLandViewModel.getRecentSearchData(userId, 1, stateId)
        }else{
            profileWithBadgeBinding.tvFavorites.background = resources.getDrawable(R.drawable.disable_rectangle_new)
            profileWithBadgeBinding.profileNotification.visibility=View.GONE

        }


        /*FETCH REGIONS*/
        homeAndLandViewModel.fetchRegions(mPreferences.getStateID())

        hnLbackHandlingViewModel = ViewModelProviders.of(this, singletonNameViewModelFactory).get(
            HnLbackHandlingViewModel::class.java
        )
        hnLbackHandlingViewModel.setUpdateRegionLiveData(false)


        /*SETTING OBSERVERS*/
        setupObserver()
    }

    @SuppressLint("SetTextI18n")
    private fun setupObserver() {
        homeAndLandViewModel.getRegionsLiveData().observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    customProgressDialog?.dismissProgress()
                    var regionsData: ArrayList<StateRegionModel>? = it.data
                    if (regionsData != null) {
                        regionsList = regionsData
                        /*for (state in regionsList) {
                            state.isSelected = selectedRegionId == state.regionId
                        }*/
                        homeAndLandRegionsAdapter.setData(regionsList)
                        homeAndLandRegionsAdapter.notifyDataSetChanged()
                    }

                }
                Status.LOADING -> {
                    customProgressDialog?.showProgress()
                }
                Status.ERROR -> {
                    customProgressDialog?.dismissProgress()
                }
            }
        })
        homeAndLandViewModel.getHnLPackagesLiveData().observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    customProgressDialog?.dismissProgress()
                    var regionsData: HnLPackagesModel? = it.data
                    setToolbarHeaderTitle()

                    if (regionsData != null && regionsData.status) {
                        hnlPackagesModel = regionsData
                        bottomBinding.tvPackages.setTextColor(
                            ContextCompat.getColor(
                                AppController.getInstance(),
                                R.color.grey_text_font_3_1
                            )
                        )
                        regionsData.priceRangeModel?.let { priceRangeModel ->

                            priceRangeModel.MinPrice.let { price ->
                                minimumPriceForPriceRange = price
                            }

                            priceRangeModel.MaxPrice.let { price ->
                                maximumPriceForPriceRange = price
                            }


                        }

                        homeAndLandDtoForPackages = HomeAndLandDtoForPackages(
                            _selectedRegion,
                            ArrayList(),
                            ArrayList(),
                            minimumPriceForPriceRange.toString(),
                            maximumPriceForPriceRange.toString()
                        )
                        AppConstants.MIN_PRICE = minimumPriceForPriceRange.toString()
                        AppConstants.MAX_PRICE = maximumPriceForPriceRange.toString()
                        if (AppConstants.HOMEANDLAND_TAP == 1) {
                            bottomBinding.tvPackages.visibility = View.INVISIBLE
                            bottomBinding.tvSkip.visibility = View.VISIBLE
                        } else {
                            bottomBinding.tvPackages.visibility = View.VISIBLE
                            bottomBinding.tvSkip.visibility = View.INVISIBLE
                        }

                        regionsData.HnLQuizResults?.let {
                            AppConstants.PACKAGES_COUNT = it
                            hnLbackHandlingViewModel.setUpdateRegionLiveData(false)
                            bottomBinding.tvPackages.text =
                                AppUtils.getPackageTextBasedOnCount(it)
                        }
                        if (this::bottomBinding.isInitialized) {
                            isNextClickable = regionsData.HnLQuizResults?.let { it1 ->
                                AppUtils.disableAndEnableView(
                                    it1,
                                    bottomBinding.tvPackages,
                                    bottomBinding.next,
                                    bottomBinding.buttonNext
                                )
                            } == true
                            if (headerText.isEmpty() || hnlPackagesModel == null) {
                                bottomBinding.next.setBackgroundResource(R.drawable.disable_next_background)
                                toolBinding.tvTool.text=getString(R.string.take_a_quick_survey_to_find_your_perfect_design)
                            }
                        }
                    } else {
                        if (this::bottomBinding.isInitialized) {
                            isNextClickable = AppUtils.noPackagesAndDisableClick(
                                bottomBinding.tvPackages,
                                bottomBinding.next, bottomBinding.buttonNext, true
                            )

                        }
                    }

                }
                Status.LOADING -> {
                    customProgressDialog?.showProgress()
                }
                Status.ERROR -> {
                    customProgressDialog?.dismissProgress()
                }
            }
        })
        homeAndLandViewModel.getRecentSearchLiveData().observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    customProgressDialog?.dismissProgress()
                    var regionsData: RecentSearchDataResponseModel? = it.data
                    if (regionsData != null && regionsData.status) {
                        if (regionsData.status) {

                            regionsData.searchJsonModel?.let { model ->
                                minimumPriceForPriceRange = model.minPrice
                                maximumPriceForPriceRange = model.maxPrice

                            }

                            Timber.e("Min Price $minimumPriceForPriceRange")
                            Timber.e("MaX Price $maximumPriceForPriceRange")

                            AppConstants.MIN_PRICE =
                                regionsData.searchJsonModel?.minPrice.toString()
                            AppConstants.MAX_PRICE =
                                regionsData.searchJsonModel?.maxPrice.toString()

                            AppConstants.Bathrooms = arrayOf(1, 2, 3, 4, 5, 6)

                            if (regionsData.searchJsonModel?.BedRoomFilters?.size ?: 0 > 0) {
                                for (bedroom in regionsData.searchJsonModel?.BedRoomFilters!!) {

                                    if (bedroom.value == 3 && bedroom.IsChecked) {
                                        AppConstants.BedRooms = arrayOf(3, 4, 5, 6)
                                    } else {
                                        AppConstants.BedRooms = arrayOf(4, 5, 6)
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
                            var isChecked = 0
                            if (regionsData.searchJsonModel?.StoreyFilters?.size ?: 0 > 0) {
                                for (storey in regionsData.searchJsonModel?.StoreyFilters!!) {
                                    if (storey.IsChecked) {
                                        isChecked++
                                    } else {
                                        if (storey.value == 1) {
                                            AppConstants.SELECTED_STOREY = 1
                                        } else {
                                            AppConstants.SELECTED_STOREY = 2
                                        }
                                    }
                                }
                            }
                            if (isChecked == 2) {
                                AppConstants.SELECTED_STOREY = 3
                            }
                            //Data checking
                            if (AppConstants.isPackagesFromProfile.not() || isNotFromFavourites.not()) {
                               if(!AppConstants.isHomeLandProfile.not()) {

                                   regionsData.searchJsonModel?.let { it1 ->
                                       showRecentSearchDialog(
                                           it1
                                       )

                                   }
                               }
                            }
                        }
                    }

                }
                Status.LOADING -> {
                    customProgressDialog?.showProgress()
                }
                Status.ERROR -> {
                    customProgressDialog?.dismissProgress()
                }
            }
        })
        hnLbackHandlingViewModel.getUpdateRegionLiveData().observe(viewLifecycleOwner, Observer {
            val isUpdate: Boolean = it
            if (isUpdate) {
                /*FETCH REGIONS*/
                hnlApiCall(AppConstants.SELECTED_REGION)
                //REMOVING ALREADY SELECTED STOREY
                AppConstants.SELECTED_STOREY = -1
            } else {
                bottomBinding.tvPackages.text =
                    AppUtils.getPackageTextBasedOnCount(AppConstants.PACKAGES_COUNT)
                setToolbarHeaderTitle()
            }
        })

    }
    private fun profileNotificationCountView(notificationCount:Int){
        if(notificationCount==0){
            profileWithBadgeBinding.profileNotification.visibility=View.GONE
        }else{
            profileWithBadgeBinding.profileNotification.visibility=View.VISIBLE
        }
    }
    private fun initViews() {
        displayToolbarViewModel = ViewModelProviders.of(requireActivity())[DisplayToolbarViewModel::class.java]


        isNotFromFavourites = true
        profileWithBadgeBinding.profileNotification.text=AppConstants.TotalMyFavs.toString()
        profileNotificationCountView(AppConstants.TotalMyFavs)
        displayToolbarViewModel.favouritesCount.observe(viewLifecycleOwner, Observer<Int> { item ->
            profileWithBadgeBinding.profileNotification.text = item.toString()
            profileNotificationCountView(item)
        })
        profileWithBadgeBinding.tvFavorites.setOnClickListener {
            var isUserLoggedIn = mPreferences.getUserLogin()
            if (isUserLoggedIn) {
               /* isNotFromFavourites = false
                val bundle = Bundle()
                bundle.putBoolean(AppConstants.SELECT_FAVORITE, true)
                minimumPriceForPriceRange?.let { it1 -> bundle.putDouble("MIN_PRICE", it1) }
                maximumPriceForPriceRange?.let { it1 -> bundle.putDouble("MAX_PRICE", it1) }
                loadFragment(HomeLandPlaceFragment(), bundle)*/
                (activity as MainActivity).showProfileDialog(activity!!)

            } else {
                AppUtils.showPleaseLoginDialog(
                    requireContext(),
                    requireActivity(),
                    getString(R.string.Please_login_to_view_edit_profile)
                )
            }
        }

        customProgressDialog = CustomProgressDialog(requireContext())
        bottomBinding.previous.background =
            ContextCompat.getDrawable(requireContext(), R.drawable.rectangle_disable_button)

        bottomBinding.buttonPrevious.isEnabled = false

        bottomBinding.tvSkip.setOnClickListener {
            clearLocalData()
            refreshHomeLangPriceRange()
            AppConstants.ISFROM_SKIP = true
            AppConstants.homeAndLandMap["REGION"] = ""
            AppConstants.SELECTED_REGION = ""
            AppConstants.toolheaderHashMapHomeLand[AppConstants.REGION] = ""
            val bundle = Bundle()
            minimumPriceForPriceRange = 0.0
            maximumPriceForPriceRange = 0.0
            minimumPriceForPriceRange?.let { it1 -> bundle.putDouble("MIN_PRICE", it1) }
            maximumPriceForPriceRange?.let { it1 -> bundle.putDouble("MAX_PRICE", it1) }
            loadFragment(HomeLandPriceRangeFragment(), bundle)
            AppConstants.HOMEANDLAND_TAP = 0
            isFirstTimeBackPressed = false
        }

        bottomBinding.previous.isEnabled = false
        bottomBinding.tvPackages.visibility = View.GONE
        bottomBinding.tvSkip.visibility = View.VISIBLE
        AppConstants.CarSpaces = arrayOf()
        toolBinding.tvTool.text = ("Take a quick survey to find your perfect design")
        mPreferences = CustomSharedPreferences(AppController.getInstance())

        userInfoModel = mPreferences.getUserInfoModel();
        // selectedRegionId = mPreferences.getRegionID()
        binding.regionsRecyclerview.apply {
            homeAndLandRegionsAdapter =
                HomeAndLandRegionsAdapter(
                    ArrayList(),
                    this@HomeLandRegionFragment,
                    AppController.getInstance()
                )
            adapter = homeAndLandRegionsAdapter
        }
        binding.regionsRecyclerview.adapter
        toolBinding.tvTool.visibility = View.VISIBLE
        toolBinding.ivBack.visibility = View.VISIBLE
        toolBinding.ivBack.setOnClickListener {
            requireActivity().onBackPressed()
            AppUtils.disableAndEnableView(
                0,
                bottomBinding.tvPackages,
                bottomBinding.next,
                bottomBinding.buttonNext
            )
        }
        bottomBinding.ivNext.setOnClickListener {
            bottomBinding.buttonNext.performClick()
        }

        bottomBinding.buttonNext.setOnClickListener {
            if (isNextClickable) {
                AppConstants.HOMEANDLAND_TAP = 0
                if (headerText.isNotEmpty() && hnlPackagesModel != null) {
                    val bundle = Bundle()
                    minimumPriceForPriceRange?.let { it1 -> bundle.putDouble("MIN_PRICE", it1) }
                    maximumPriceForPriceRange?.let { it1 -> bundle.putDouble("MAX_PRICE", it1) }
                    bundle.putParcelable(AppConstants.HNLPACKAGESMODEL, homeAndLandDtoForPackages)
                    AppConstants.ISFROM_SKIP = false
                    loadFragment(HomeLandPriceRangeFragment(), bundle)
                } else {
                    AppUtils.showCustomCenterToast(requireContext(), "Please select region")
                }
            }
        }

        bottomBinding.tvPackages.setOnClickListener {

            AppConstants.FILTER_CarSpaces = arrayOf(1, 2)
            AppConstants.FILTER_BedRooms = arrayOf(1, 2, 3, 4, 5, 6)

            if (isNextClickable) {

                minimumPriceForPriceRange?.let { minPrice ->
                    maximumPriceForPriceRange?.let { maxPrice ->

                        var firstValue = minPrice.toInt() / 1000
                        var secondValue = maxPrice.toInt() / 1000


                        AppConstants.PRICE_RANGE_HEADER =
                            " | $" + firstValue + "K-" + "$" + secondValue + "K"
                        AppConstants.toolheaderHashMapHomeLand[AppConstants.PRICERANGE] =
                            AppConstants.PRICE_RANGE_HEADER
                    }

                }

                val bundle = Bundle()
                var homeAndLandPackageModelObj = HomeLandPackageModelObj(
                    headerText,
                    0,
                    0,
                    0,
                    0,
                    hnlPackagesModel?.priceRangeModel?.MinPrice.toString(),
                    hnlPackagesModel?.priceRangeModel?.MaxPrice.toString(),
                    1,
                    0
                )
                bundle.putSerializable(
                    AppConstants.HOME_LAND_PACKAGE_OBJECT,
                    homeAndLandPackageModelObj
                )
               AppConstants.clearCarspaceBathroomsdata()
                AppConstants.ISFROM_SKIP = false
                minimumPriceForPriceRange?.let { it1 -> bundle.putDouble("MIN_PRICE", it1) }
                maximumPriceForPriceRange?.let { it1 -> bundle.putDouble("MAX_PRICE", it1) }
                loadFragment(HomeLandPlaceFragment(), bundle)
            }
        }

    }

    fun loadFragment(fragment: Fragment, bundle: Bundle) {
        val transaction: FragmentTransaction = activity?.supportFragmentManager!!.beginTransaction()
        fragment.arguments = bundle
        if (fragment.isAdded) {
            return
        }
        transaction.add(R.id.fl_content, fragment,fragment::class.simpleName)
        transaction.addToBackStack(null)
        transaction.commit()
    }
    fun refreshHomeLangPriceRange(){
        AppConstants.selectedminimumPriceForPriceRange=-1.0
        AppConstants.selectedmaximumPriceForPriceRange=-1.0
        AppConstants.homeLandregionsData=null
        AppConstants.HOMEANDLAND_TAP = 0
        AppConstants.SELECTED_STOREY = -1
    }
    override fun selectState(position: Int) {
        if (regionsList.size > 0) {
           /* AppConstants.selectedminimumPriceForPriceRange=-1.0
            AppConstants.selectedmaximumPriceForPriceRange=-1.0
            AppConstants.homeLandregionsData=null
            AppConstants.SELECTED_STOREY = -1*/
            AppConstants.HOMEANDLAND_TAP = 0
            if(isRecentSearchClicked){
                isRecentSearchClicked=false
                AppConstants.toolheaderHashMapHomeLand[AppConstants.REGION]=""
                AppConstants.toolheaderHashMapHomeLand[AppConstants.PRICERANGE]=""
                AppConstants.toolheaderHashMapHomeLand[AppConstants.STOREYS]=""
                AppConstants.toolheaderHashMapHomeLand[AppConstants.BEDROOMS]=""

            }
            val selectedList = regionsList.filter { it.isSelected}
            val multipleRegion = selectedList.joinToString(separator = ","){it.RegionName}
            val multipleRegionrequest = selectedList.joinToString(separator = ", "){it.RegionName}
            //mPreferences.setRegionId(multipleRegionIds.toString())
            mPreferences.selectedRegion(multipleRegion)
            headerText = multipleRegion
            val toolBarText = if (regionsList.size == selectedList.size) "All Regions" else multipleRegionrequest
//            toolBinding.tvTool.text = toolBarText
            _selectedRegion = multipleRegion
            AppConstants.homeAndLandMap["REGION"] = toolBarText
            AppConstants.SELECTED_REGION = multipleRegion
            AppConstants.REGION_HEADER = toolBarText
            AppConstants.toolheaderHashMapHomeLand[AppConstants.REGION] = toolBarText
            AppConstants.STOREYS_HEADER=""
            AppConstants.BEDROOMS_HEADER=""
//            val stateModel: StateRegionModel = regionsList.get(position)
            /*for (state in regionsList) {
                state.isSelected = stateModel.regionId == state.regionId
                if (state.isSelected) {
                    mPreferences.setRegionId(state.regionId)
                    mPreferences.selectedRegion(state.RegionName)
                    toolBinding.tvTool.text = state.RegionName
                    //SELECTED REGION
                    _selectedRegion = state.RegionName

                    headerText = state.RegionName
                    AppConstants.homeAndLandMap["REGION"] = state.RegionName
                    AppConstants.SELECTED_REGION = state.RegionName
                    AppConstants.REGION_HEADER = state.RegionName
                    AppConstants.toolheaderHashMapHomeLand[AppConstants.REGION] = state.RegionName
                }

            }*/
           // homeAndLandRegionsAdapter.notifyDataSetChanged()

            hnlApiCall(multipleRegion)
            isFirstTimeBackPressed = false

        }
    }

    private fun hnlApiCall(region: String) {
        val stateId = mPreferences.getStateID();
        val userId = mPreferences.getUserId()
        val storey = AppConstants.SELECTED_STOREY_PREVIOUS
        val jsonObject = JsonObject()
        val hnlQuizObject = JsonObject()
        hnlQuizObject.addProperty("Region", region)
        //Removing stored Values
        AppConstants.BedRooms = arrayOf(3, 4, 5, 6)
        AppConstants.Bathrooms = arrayOf(1, 2, 3, 4, 5, 6)

        val storeyArray = JsonArray()

        val bedrooms = JsonArray()

        if (storey!= 0) {
            storeyArray.add(storey)
        } else {
            storeyArray.add(1)
            storeyArray.add(2)
        }
        if(AppConstants.SELECTED_BEDROOM_COUNT>0){
            bedrooms.add(AppConstants.SELECTED_BEDROOM_COUNT)
        }
        else{
            var bedRooms = arrayOf(3, 4, 5, 6)
            for (bed in bedRooms) {
                bedrooms.add(bed)
            }
        }
        val carspaces = JsonArray()
        carspaces.add(1)
        carspaces.add(2)

        val bathrooms = JsonArray()
        for (bathroom in AppConstants.Bathrooms!!) {
            bathrooms.add(bathroom)
        }

        hnlQuizObject.add("Storey", storeyArray)
        hnlQuizObject.add("BedRooms", bedrooms)
        hnlQuizObject.add("CarSpaces", carspaces)
        hnlQuizObject.add("Bathrooms", bathrooms)
        hnlQuizObject.addProperty("StateId", stateId)
        hnlQuizObject.addProperty("SelectedMinValue", AppConstants.SELECTED_MIN_PRICE)
        hnlQuizObject.addProperty("SelectedMaxValue", AppConstants.SELECTED_MAX_PRICE)
        hnlQuizObject.addProperty("PageNo", 0)
        hnlQuizObject.addProperty("SortByPrice", 0)
        hnlQuizObject.addProperty("IncludePackages", 0)
        jsonObject.add("HnLQuizItems", hnlQuizObject)
        homeAndLandViewModel.fetchHnLPackages(userId, jsonObject)
    }

    private fun showRecentSearchDialog(searchJsonModel: SearchJsonModel) {
        var regionName: String = ""
        AppConstants.SELECTED_REGION = searchJsonModel.regionsList.joinToString(","){it.regionName}
        val layoutRecentSearchBinding: LayoutRecentSearchBinding =
            LayoutRecentSearchBinding.inflate(layoutInflater)
        val shareAlertBuilder: AlertDialog.Builder =
            AlertDialog.Builder(context)
        shareAlertBuilder.setView(layoutRecentSearchBinding.root)
        val alertDialog: AlertDialog = shareAlertBuilder.create()
        layoutRecentSearchBinding.tvStart.setOnClickListener {
            alertDialog.dismiss()
        }
        layoutRecentSearchBinding.ivClose.setOnClickListener {
            alertDialog.dismiss()
        }
        val bundle = Bundle()
        layoutRecentSearchBinding.tvShowpackages.setOnClickListener {
            var minRangeValue: Int? = null
            var maxRangeValue: Int? = null
            AppConstants.ISFROM_SKIP = false
            searchJsonModel.minPrice.let { it1 ->
                bundle.putDouble("MIN_PRICE", it1)
                minRangeValue = it1.toInt() / 1000
            }
            searchJsonModel.maxPrice.let { it1 ->
                bundle.putDouble("MAX_PRICE", it1)
                maxRangeValue = it1.toInt() / 1000

            }

            AppConstants.PRICE_RANGE_HEADER =
                "$" + minRangeValue + "K-" + "$" + maxRangeValue + "K"
            AppConstants.toolheaderHashMapHomeLand[AppConstants.REGION] =
                regionName
            AppConstants.toolheaderHashMapHomeLand[AppConstants.PRICERANGE] =
                AppConstants.PRICE_RANGE_HEADER
            AppConstants.toolheaderHashMapHomeLand[AppConstants.STOREYS] =
                storeyString
            AppConstants.toolheaderHashMapHomeLand[AppConstants.BEDROOMS] =
                bedroomString
            AppConstants.REGION_HEADER = regionName
            AppConstants.STOREYS_HEADER = storeyString
            AppConstants.BEDROOMS_HEADER = bedroomString
            AppConstants.MIN_PRICE = minRangeValue.toString()
            AppConstants.MAX_PRICE = maxRangeValue.toString()
            /*AppConstants.FILTER_BedRooms = when(searchJsonModel.BedRoomFilters.size){
                1 -> arrayOf(searchJsonModel.BedRoomFilters[0].value)
                2 -> arrayOf(5,6)
                else -> arrayOf(3,4,5,6)
            }*/
            val storeyFilter = searchJsonModel.StoreyFilters.filter { it.IsChecked }
            /*AppConstants.FILTER_SELECTED_STOREY = when(storeyFilter.size){
                1 -> storeyFilter[0].value
                else -> 2
            }*/
            var count = -1
            if (storeyFilter.size>1) count = -1
            else {
                storeyFilter.forEach {
                    if (it.IsChecked) {
                        count = it.value
                        return@forEach
                    }
                }
            }
            AppConstants.SELECTED_STOREY = count
            val bedRooms = searchJsonModel.BedRoomFilters.filter { it.IsChecked }
            when(bedRooms.size){
                1 ->{
                    bedRooms.forEach {
                        AppConstants.BedRooms = arrayOf(it.value)
                        AppConstants.BEDROOM_COUNT = it.value
                    }
                }
                2 ->{
                    AppConstants.BedRooms = arrayOf(5,6)
                    AppConstants.BEDROOM_COUNT = 5
                }
                else ->{
                    AppConstants.BedRooms = arrayOf(3,4,5,6)
                    AppConstants.BEDROOM_COUNT = -1
                }
            }
            val bathroomFilter = searchJsonModel.BathRoomFilters.filter { it.IsChecked }
            if(bathroomFilter.size>1)
                AppConstants.Bathrooms = arrayOf(2,3)
            else {
                bathroomFilter.forEach{
                    when (it.value) {
                        2 -> if (it.IsChecked) AppConstants.Bathrooms = arrayOf(2)
                        3 -> if (it.IsChecked) AppConstants.Bathrooms = arrayOf(3)
                    }
                }
            }

            searchJsonModel.CarSpaces.forEach {
                when(it.value){
                    1 -> if (it.IsChecked) AppConstants.CarSpaces = arrayOf(1)
                    2 -> if (it.IsChecked) AppConstants.CarSpaces = arrayOf(2)
                    else -> AppConstants.CarSpaces = arrayOf(1,2)
                }
            }
            /*AppConstants.FILTER_MIN_PRICE = searchJsonModel.minPrice.toString()
            AppConstants.FILTER_MAX_PRICE = searchJsonModel.maxPrice.toString()*/
            alertDialog.dismiss()
            loadFragment(HomeLandPlaceFragment(), bundle)

            isRecentSearchClicked=true
        }
        val minPrice: Double = searchJsonModel.minPrice / 1000
        val maxPrice: Double = searchJsonModel.maxPrice / 1000

        if (searchJsonModel.StoreyFilters != null && searchJsonModel.StoreyFilters.size > 0) {
            storeyString = ""
            val storeyFilter = searchJsonModel.StoreyFilters.filter { it.IsChecked }
            if (storeyFilter.size>1) {storeyString = ""}
            else{
                for (searchSubModel in searchJsonModel.StoreyFilters) {
                    if (searchSubModel.IsChecked) {
                        storeyString = searchSubModel.displayName
                    }
                }
            }
        }

        if (searchJsonModel.BedRoomFilters.size > 0) {
            bedroomString = ""
            val selectedBedRooms = searchJsonModel.BedRoomFilters.filter { it.IsChecked }
            if (selectedBedRooms.size>2){
                bedroomString = ("")
            }
            else if (selectedBedRooms.size == 2) {
                bedroomString = ("5+ Bed")
            } else {
                for (searchSubModel in selectedBedRooms) {
                    if (searchSubModel.IsChecked)
                        bedroomString = ("${searchSubModel.value} Bed")
                }
            }
        }

        val regionsArrList = arrayListOf<String>()
        if (searchJsonModel.regionsList.size > 0) {
            val regionList = searchJsonModel.regionsList[0].regionName.split(",")
            regionList.forEach{
                regionsArrList.add(it)
            }
            regionName = if (regionsList.size == regionsArrList.size) "All Regions" else searchJsonModel.regionsList.joinToString(","){it.regionName}
        }

        var header: String = ""

           if(storeyString!=""){
               storeyString= " $storeyString"
           }
        if(bedroomString!=""){
            bedroomString= " | $bedroomString"
        }
        header = if (regionName.isNotEmpty()) {
            (regionName + " | " + "$${minPrice.toInt()}K-$${maxPrice.toInt()}K |") + storeyString+bedroomString
        } else {
            ("$${minPrice.toInt()}K-$${maxPrice.toInt()}K |"  + storeyString + bedroomString)
        }
        if(storeyString=="" && bedroomString=="" && regionName=="" ){
            header = ("| $${minPrice.toInt()}K-$${maxPrice.toInt()}K |")
        }

        layoutRecentSearchBinding.tvRegionPrice.text = header
        val homeAndLandPackageModelObj = HomeLandPackageModelObj(
            regionName,
            0,
            0,
            0,
            0,
            searchJsonModel.minPrice.toString(),
            searchJsonModel.maxPrice.toString(),
            1,
            0
        )
        bundle.putSerializable(
            AppConstants.HOME_LAND_PACKAGE_OBJECT,
            homeAndLandPackageModelObj
        )
        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        alertDialog.show()

    }

    private fun showSnakbar(view: View, message: String) {
    /*    val snack = Snackbar.make(view, message, Snackbar.LENGTH_LONG)
        snack.show()*/
        Toast.makeText(activity,message, Toast.LENGTH_SHORT).show()

    }
    private fun setToolbarHeaderTitle() {
        val toolHeaderStringBuilder = StringBuilder()
        if(AppConstants.toolheaderHashMapHomeLand.size>0) {
            for (key in AppConstants.toolheaderHashMapHomeLand.keys) {
                if (key == AppConstants.PRICERANGE) {
                var value = AppConstants.toolheaderHashMapHomeLand[key]
                    if(value!="" && !value!!.contains("|")) {
                        toolHeaderStringBuilder.append(" |$value")
                    }else{
                        toolHeaderStringBuilder.append(value)

                    }
            } else {
                var value = AppConstants.toolheaderHashMapHomeLand[key]
                toolHeaderStringBuilder.append(value)
            }
            }
            toolBinding.tvTool.visibility = View.VISIBLE

            toolBinding.tvTool.text = toolHeaderStringBuilder.toString()
        }else{
            toolBinding.tvTool.text = getString(R.string.take_a_quick_survey_to_find_your_perfect_design)

        }

    }
    override fun onBackPressed(): Boolean {
        if (isFirstTimeBackPressed) {
            isFirstTimeBackPressed = false
        } else {
            isFirstTimeBackPressed = true
            homeAndLandViewModel.fetchRegions(mPreferences.getStateID())
            toolBinding.tvTool.text = "Take a quick survey to find your perfect design"
            bottomBinding.tvPackages.visibility = View.INVISIBLE
            bottomBinding.tvSkip.visibility = View.VISIBLE
            headerText = ""
            AppConstants.toolheaderHashMapHomeLand.remove(AppConstants.STOREYS)
            AppConstants.toolheaderHashMapHomeLand.remove(AppConstants.BEDROOMS)
            AppConstants.toolheaderHashMapHomeLand.remove(AppConstants.PRICERANGE)
            AppConstants.toolheaderHashMapHomeLand.remove(AppConstants.REGION)

            AppConstants.SELECTED_STOREY=0
            AppConstants.BEDROOM_COUNT=-2
            AppConstants.SELECTED_BEDROOM_COUNT=0
            AppConstants.SELECTED_MIN_PRICE="0.0"
            AppConstants.SELECTED_MAX_PRICE="0.0"
            refreshHomeLangPriceRange()
            AppConstants.SELECTED_STOREY_PREVIOUS=0

        }
        return isFirstTimeBackPressed

    }

    private fun clearLocalData() {
        AppConstants.REGION_HEADER = ""
        AppConstants.STOREYS_HEADER = ""
        AppConstants.BEDROOMS_HEADER = ""
        AppConstants.PRICE_RANGE_HEADER = ""
        AppConstants.PACKAGES_COUNT = 0
        AppConstants.SELECTED_STOREY = -1

        AppConstants.FILTER_SELECTED_STOREY = -1
        AppConstants.FILTER_MIN_PRICE = ""
        AppConstants.FILTER_MAX_PRICE = ""
        AppConstants.FILTER_Bathrooms = null
        AppConstants.FILTER_BedRooms = null
        AppConstants.FILTER_CarSpaces = null
        AppConstants.FILTER_BEDROOM_COUNT = 0
        AppConstants.FILTER_toolHeaderHashMapHomeLand.clear()

    }


    override fun breadCrumb(breadCrumb: BreadcrumbModel) {

    }


}
