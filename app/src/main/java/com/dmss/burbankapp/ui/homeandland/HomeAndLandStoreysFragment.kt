package com.dmss.burbankapp.ui.homeandland

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
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
import com.dmss.burbankapp.data.model.HnLPackagesModel
import com.dmss.burbankapp.data.model.HomeAndLandDtoForPackages
import com.dmss.burbankapp.data.model.HomeLandPackageModelObj
import com.dmss.burbankapp.databinding.FragmentHomeLandStoresBinding
import com.dmss.burbankapp.databinding.HomelandToolBinding
import com.dmss.burbankapp.databinding.LayoutBottomNextPreviousBinding
import com.dmss.burbankapp.databinding.ProfileWithBadgeBinding
import com.dmss.burbankapp.ui.homeandlandplaces.HomeLandPlaceFragment
import com.dmss.burbankapp.ui.homeandlandregions.HomeAndLandViewModel
import com.dmss.burbankapp.ui.main.MainActivity
import com.dmss.burbankapp.ui.mydisplay.DisplayToolbarViewModel
import com.dmss.burbankapp.ui.view.CustomProgressDialog
import com.dmss.burbankapp.utils.AppConstants
import com.dmss.burbankapp.utils.AppUtils
import com.dmss.burbankapp.viewmodel.HnLbackHandlingViewModel
import com.dmss.burbankapp.viewmodel.SingletonNameViewModelFactory
import com.google.android.material.snackbar.Snackbar
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import common.AppController
import timber.log.Timber


class HomeAndLandStoreysFragment : Fragment(), MainActivity.OnBackPressedListener {
    private var customProgressDialog: CustomProgressDialog? = null
    lateinit var preferences: CustomSharedPreferences
    lateinit var homeAndLandViewModel: HomeAndLandViewModel
    lateinit var headerText: String
    lateinit var storeyString: String
    lateinit var hnLPackagesModel: HnLPackagesModel
    private var storeyCount: Int = 0
    private var isStoreySelected = false

    var minimumPriceForPriceRange: Double? = null
    var maximumPriceForPriceRange: Double? = null

    var homeAndLandDtoForPackages: HomeAndLandDtoForPackages? = null

    //Handling Back Stack Flow
    private var updateRegionApi = true
    private var initilizeApi = false
    var isNextClickable: Boolean = true


    lateinit var hnLbackHandlingViewModel: HnLbackHandlingViewModel
    lateinit var singletonNameViewModelFactory: SingletonNameViewModelFactory
    lateinit var binding: FragmentHomeLandStoresBinding
    lateinit var bottomBinding: LayoutBottomNextPreviousBinding
    lateinit var toolBinding: HomelandToolBinding
    lateinit var profileWithBadgeBinding: ProfileWithBadgeBinding
    private lateinit var displayToolbarViewModel: DisplayToolbarViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeLandStoresBinding.inflate(inflater, container, false)
        bottomBinding = LayoutBottomNextPreviousBinding.bind(binding.root)
        toolBinding = HomelandToolBinding.bind(binding.root)
        profileWithBadgeBinding = ProfileWithBadgeBinding.bind(binding.root)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        minimumPriceForPriceRange = arguments?.getDouble("MIN_PRICE")
        maximumPriceForPriceRange = arguments?.getDouble("MAX_PRICE")

        Timber.e("MinPrice Story $minimumPriceForPriceRange")
        Timber.e("MaxPrice Story$maximumPriceForPriceRange")
        initViews()
        setUpViewModel()

    }

    override fun onResume() {
        super.onResume()
        Timber.e("onResume --->")
    }

    private fun setUpViewModel() {
        singletonNameViewModelFactory = SingletonNameViewModelFactory()
        homeAndLandViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(
                ApiHelperImpl(RetrofitBuilder.apiService),
                DatabaseHelperImpl(DatabaseBuilder.getInstance(AppController.getInstance()))
            )
        ).get(HomeAndLandViewModel::class.java)
        hnLbackHandlingViewModel = ViewModelProviders.of(this, singletonNameViewModelFactory).get(
            HnLbackHandlingViewModel::class.java
        )
        setupObserver()
    }

    private fun setupObserver() {
        hnLbackHandlingViewModel.getUpdateRegionLiveData().observe(viewLifecycleOwner, Observer {
//            val isUpdate: Boolean = it
//            println("regionsData getUpdateRegionLiveData:: "+it)
//            if (isUpdate) {
//                /*FETCH REGIONS*/
//                hnlApiCall(AppConstants.SELECTED_REGION)
//                //REMOVING ALREADY SELECTED STOREY
//                AppConstants.SELECTED_STOREY = -1
//            } else {
//            println("PACKAGES_COUNT stories:: "+AppConstants.PACKAGES_COUNT)
            bottomBinding.tvPackages.text =
                AppUtils.getPackageTextBasedOnCount(AppConstants.PACKAGES_COUNT)
//            }
            setToolbarHeaderTitle()
        })
        hnLbackHandlingViewModel.bacPressLiveData.observe(viewLifecycleOwner, Observer {
        })
        homeAndLandViewModel.getHnLPackagesLiveData().observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    customProgressDialog?.dismissProgress()
                    var regionsData: HnLPackagesModel? = it.data
                    if (regionsData != null && regionsData.status) {
                        hnLPackagesModel = regionsData
                        AppConstants.BEDROOMS_HEADER=""
                        regionsData.HnLQuizResults?.let { it1 ->
                            preferences.setPackagesCount(it1)
                            AppConstants.PACKAGES_COUNT = it1
                        }


                        bottomBinding.tvPackages.visibility = View.VISIBLE
                        bottomBinding.tvPackages.setTextColor(
                            ContextCompat.getColor(
                                AppController.getInstance(),
                                R.color.grey_text_font_3_1
                            )
                        )
                        bottomBinding.tvPackages.text =
                            regionsData.HnLQuizResults?.let { it1 ->
                                AppUtils.getPackageTextBasedOnCount(
                                    it1
                                )
                            }
                        if(AppConstants.PACKAGES_COUNT>0){
                            isNextClickable=true
                        }
                        if (this::bottomBinding.isInitialized) {
                            // changed by durga 01/12/2022

                            /*  isNextClickable = regionsData.HnLQuizResults?.let { it1 ->
                                  AppUtils.disableAndEnableView(
                                      it1,
                                      bottomBinding.tvPackages,
                                      bottomBinding.next, bottomBinding.buttonNext
                                  )
                              } == true*/
                        }
                        //toolBinding.tvTool.text = storeyString
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

        /*hnLbackHandlingViewModel.bacPressLiveData.observe(viewLifecycleOwner, Observer {
            val isUpdate: Boolean = it
            if (!AppConstants.ISFROM_SKIP) {
                if (initilizeApi) {
                    if (isUpdate) {
                        var storey = AppConstants.SELECTED_STOREY
                        previousSelectedItem(storey)
                        hnlApiCall(storey)
                    } else {
                        bottomBinding.tvPackages.visibility = View.VISIBLE
                        bottomBinding.tvPackages.text =
                            AppUtils.getPackageTextBasedOnCount(AppConstants.PACKAGES_COUNT)
                    }
                }
            }
        })*/

//        hnlApiIntilizeCall();
    }

    private fun changeBackground(view: View, textView: TextView,imageview:ImageView) {
        isStoreySelected = true
        binding.llSingle.background =
            ContextCompat.getDrawable(AppController.getInstance(), R.drawable.rectangel_shape)
        binding.llDouble.background =
            ContextCompat.getDrawable(AppController.getInstance(), R.drawable.rectangel_shape)
        binding.llNotsure.background =
            ContextCompat.getDrawable(AppController.getInstance(), R.drawable.rectangel_shape)
        binding.imgNotsure.setColorFilter(ContextCompat.getColor(AppController.getInstance(), R.color.grey_text_font_3_1));
        binding.imgDouble.setColorFilter(ContextCompat.getColor(AppController.getInstance(), R.color.grey_text_font_3_1));
        binding.imgSingle.setColorFilter(ContextCompat.getColor(AppController.getInstance(), R.color.grey_text_font_3_1));

            AppUtils.disableAndEnableView(
                1,
                bottomBinding.tvPackages,
                bottomBinding.next, bottomBinding.buttonNext
            )

        binding.tvSingle.setTextColor(
            ContextCompat.getColor(
                AppController.getInstance(),
                R.color.grey_text_font_3_1
            )
        )
        binding.tvDouble.setTextColor(
            ContextCompat.getColor(
                AppController.getInstance(),
                R.color.grey_text_font_3_1
            )
        )
        binding.tvNotsure.setTextColor(
            ContextCompat.getColor(
                AppController.getInstance(),
                R.color.grey_text_font_3_1
            )
        )

        textView.setTextColor(ContextCompat.getColor(AppController.getInstance(), R.color.white_3_1))
        view.background =
            ContextCompat.getDrawable(AppController.getInstance(), R.drawable.rectangle_orange_bg)
        imageview.setColorFilter(ContextCompat.getColor(AppController.getInstance(), R.color.white_3_1));


    }
    /*private fun notifyToolAdapter() {
        toolHeaderList =
            (activity as MainActivity).getToolListInHomeAndLand(
                AppConstants.toolheaderHashMapHomeLand,
                AppConstants.PRICERANGE
            )
        if (this::homeAndLandToolBarAdapter.isInitialized) {
            homeAndLandToolBarAdapter.updateData(toolHeaderList)
        }
    }*/
    private fun profileNotificationCountView(notificationCount:Int){
        if(notificationCount==0){
            profileWithBadgeBinding.profileNotification.visibility=View.GONE
        }else{
            profileWithBadgeBinding.profileNotification.visibility=View.VISIBLE
        }
    }
    private fun initViews() {
        displayToolbarViewModel = ViewModelProviders.of(requireActivity())[DisplayToolbarViewModel::class.java]

        AppConstants.toolheaderHashMapHomeLand[AppConstants.STOREYS] = ""
        AppUtils.profileCountStatus(profileWithBadgeBinding.profileNotification)
        displayToolbarViewModel.favouritesCount.observe(viewLifecycleOwner, Observer<Int> { item ->
            profileWithBadgeBinding.profileNotification.text = item.toString()
            profileNotificationCountView(item)

        })
        setToolbarHeaderTitle()
        if (AppConstants.REGION_HEADER.isEmpty()) {
            AppConstants.PRICE_RANGE_HEADER =
                "$" + (minimumPriceForPriceRange?.toInt()
                    ?: 0) / 1000 + "K-" + "$" + (maximumPriceForPriceRange?.toInt()
                    ?: 0) / 1000 + "K"
        } else {
            AppConstants.PRICE_RANGE_HEADER =
                " | $" + (minimumPriceForPriceRange?.toInt()
                    ?: 0) / 1000 + "K-" + "$" + (maximumPriceForPriceRange?.toInt()
                    ?: 0) / 1000 + "K"
        }
        AppConstants.toolheaderHashMapHomeLand[AppConstants.PRICERANGE] =
            AppConstants.PRICE_RANGE_HEADER

        bottomBinding.tvPackages.visibility = View.GONE
        preferences = CustomSharedPreferences(AppController.getInstance())
        homeAndLandDtoForPackages =
            arguments?.getParcelable<HomeAndLandDtoForPackages>(AppConstants.HNLPACKAGESMODEL)

        var isUserLoggedIn = preferences.getUserLogin()
        if (!isUserLoggedIn) {
            profileWithBadgeBinding.tvFavorites.background = resources.getDrawable(R.drawable.disable_rectangle_new)
            profileWithBadgeBinding.profileNotification.visibility=View.GONE
        }
        profileWithBadgeBinding.tvFavorites.setOnClickListener {

            var isUserLoggedIn = preferences.getUserLogin()
            if (isUserLoggedIn) {
               /* val bundle = Bundle()
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
        headerText = AppConstants.REGION_HEADER


        if (AppConstants.PACKAGES_COUNT == 0) {
            bottomBinding.tvPackages.visibility = View.GONE
        } else {
            bottomBinding.tvPackages.visibility = View.VISIBLE
            bottomBinding.tvPackages.setTextColor(
                ContextCompat.getColor(
                    AppController.getInstance(),
                    R.color.grey_text_font_3_1
                )
            )
            bottomBinding.tvPackages.text =
                AppUtils.getPackageTextBasedOnCount(AppConstants.PACKAGES_COUNT)
        }


        binding.llSingle.setOnClickListener {
            AppConstants.clearCarspaceBathroomsdata()
            changeBackground(binding.llSingle, binding.tvSingle,binding.imgSingle)
            hnlApiCall(1)
        }
        binding.llDouble.setOnClickListener {
            AppConstants.clearCarspaceBathroomsdata()
            changeBackground(binding.llDouble, binding.tvDouble,binding.imgDouble)
            hnlApiCall(2)
        }
        binding.llNotsure.setOnClickListener {
            AppConstants.clearCarspaceBathroomsdata()
            changeBackground(binding.llNotsure, binding.tvNotsure,binding.imgNotsure)
            AppConstants.toolheaderHashMapHomeLand[AppConstants.STOREYS] = ""
            hnlApiCall(3)

        }
        bottomBinding.buttonPrevious.setOnClickListener {
            updateRegionApi = false
            hnLbackHandlingViewModel._backPressLiveData.postValue(true)
            activity!!.onBackPressed()
        }
        bottomBinding.ivPrevios.setOnClickListener {
            updateRegionApi = false
            hnLbackHandlingViewModel._backPressLiveData.postValue(true)
            activity?.onBackPressed()
        }
        toolBinding.ivBack.setOnClickListener {

            hnLbackHandlingViewModel._backPressLiveData.postValue(false)
        }

        toolBinding.tvTool.visibility = View.VISIBLE
        bottomBinding.tvPackages.setOnClickListener {
            AppConstants.FILTER_CarSpaces = arrayOf(1, 2)
            AppConstants.FILTER_BedRooms = arrayOf(1, 2, 3, 4, 5, 6)

            if (isNextClickable) {
                val bundle = Bundle()
                val homeAndLandPackageModelObj = HomeLandPackageModelObj(
                    headerText,
                    AppConstants.SELECTED_STOREY,
                    0,
                    0,
                    0,
                    AppConstants.MIN_PRICE,
                    AppConstants.MAX_PRICE,
                    1,
                    0
                )
                bundle.putSerializable(
                    AppConstants.HOME_LAND_PACKAGE_OBJECT,
                    homeAndLandPackageModelObj
                )
                minimumPriceForPriceRange?.let { it1 -> bundle.putDouble("MIN_PRICE", it1) }
                maximumPriceForPriceRange?.let { it1 -> bundle.putDouble("MAX_PRICE", it1) }
                AppConstants.clearCarspaceBathroomsdata()
                loadFragment(HomeLandPlaceFragment(), bundle)
            }


        }

        toolBinding.ivBack.setOnClickListener {
            AppConstants.toolheaderHashMapHomeLand.remove(AppConstants.STOREYS)
            AppConstants.toolheaderHashMapHomeLand.remove(AppConstants.BEDROOMS)
            AppConstants.SELECTED_STOREY=0
            AppConstants.BEDROOM_COUNT=-2
            AppConstants.SELECTED_BEDROOM_COUNT=0
            AppConstants.SELECTED_STOREY_PREVIOUS=0
            hnLbackHandlingViewModel.setBackStackString("Madhu")
            hnLbackHandlingViewModel.setUpdateRegionLiveData(true)

            activity!!.onBackPressed()
        }

        bottomBinding.ivNext.setOnClickListener {
            bottomBinding.buttonNext.performClick()
        }
        bottomBinding.buttonNext.setOnClickListener {
            if (isNextClickable) {
                if (isStoreySelected) {
                    var fragment = HomeLandBedRoomsFragment();
                    val bundle = Bundle()
                    minimumPriceForPriceRange?.let { it1 -> bundle.putDouble("MIN_PRICE", it1) }
                    maximumPriceForPriceRange?.let { it1 -> bundle.putDouble("MAX_PRICE", it1) }
                    loadFragment(fragment, bundle)
                } else {
                    AppUtils.showCustomCenterToast(requireContext(), "Please select storeys")
                }
            }
        }
        if(AppConstants.SELECTED_STOREY_PREVIOUS!=0){
            setHeader(AppConstants.SELECTED_STOREY_PREVIOUS)
        }
        previousSelectedItem(AppConstants.SELECTED_STOREY_PREVIOUS)
    }

    fun loadFragment(fragment: Fragment, bundle: Bundle) {
        clearFilterData()
        initilizeApi = true
        val transaction: FragmentTransaction = activity?.supportFragmentManager!!.beginTransaction()
        fragment.arguments = bundle
        if (fragment.isAdded) {
            return
        }
        transaction.add(R.id.fl_content, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun clearFilterData() {
        AppConstants.FILTER_SELECTED_STOREY = -1
        AppConstants.FILTER_SELECTED_STOREY = 0

        AppConstants.FILTER_MIN_PRICE = ""
        AppConstants.FILTER_MAX_PRICE = ""
        AppConstants.FILTER_Bathrooms = null
        AppConstants.FILTER_BedRooms = null
        AppConstants.FILTER_CarSpaces = null
        AppConstants.FILTER_BEDROOM_COUNT = 0
        //  AppConstants.FILTER_toolHeaderHashMapHomeLand.clear()
    }


    private fun hnlApiCall(storey: Int) {
        AppConstants.SELECTED_STOREY = storey
        AppConstants.SELECTED_STOREY_PREVIOUS = storey

        storeyCount = storey
        var userId = preferences.getUserId()
        val stateId = preferences.getStateID()
        var region = AppConstants.SELECTED_REGION

        val jsonObject = JsonObject()
        val hnlQuizObject = JsonObject()

        if (region.isEmpty())
            hnlQuizObject.addProperty("Region", "")
        else
            hnlQuizObject.addProperty("Region", region)

        val storeyArray = JsonArray()
        if (AppConstants.SELECTED_STOREY != 3) {
            storeyArray.add(storey)
            when (storey) {
                1 -> {
                    AppConstants.STOREYS_HEADER = " | Single"
                    AppConstants.toolheaderHashMapHomeLand[AppConstants.STOREYS] = " | Single"
                    storeyString = " Single"

                }
                2 -> {
                    AppConstants.STOREYS_HEADER = " | Double"
                    AppConstants.toolheaderHashMapHomeLand[AppConstants.STOREYS] = " | Single"
                    storeyString = " Double"
                }

            }
        } else {
            storeyString = ""
            AppConstants.STOREYS_HEADER = ""
        }
        AppConstants.toolheaderHashMapHomeLand[AppConstants.STOREYS] = AppConstants.STOREYS_HEADER
        storeyString = headerText + AppConstants.STOREYS_HEADER

        AppConstants.BedRooms = arrayOf(3, 4, 5, 6)
        AppConstants.Bathrooms = arrayOf(1, 2, 3, 4, 5, 6)

        val bathrooms = JsonArray()
        for (bathroom in AppConstants.Bathrooms!!) {
            bathrooms.add(bathroom)
        }
        val bedrooms = JsonArray()
        /*AppConstants.BedRooms?.let {
            for (bed in it) {
                bedrooms.add(bed)
            }
        }*/
        if(AppConstants.SELECTED_BEDROOM_COUNT>0){
            bedrooms.add(AppConstants.SELECTED_BEDROOM_COUNT)
        }
        else{
            var bedRooms = arrayOf(3, 4, 5, 6)
            for (bed in bedRooms) {
                bedrooms.add(bed)
            }
        }
        val carSpaces = JsonArray()
        carSpaces.add(1)
        carSpaces.add(2)

        hnlQuizObject.add("Storey", storeyArray)
        hnlQuizObject.add("BedRooms", bedrooms)
        hnlQuizObject.add("CarSpaces", carSpaces)
        hnlQuizObject.add("Bathrooms", bathrooms)
        hnlQuizObject.addProperty("StateId", stateId)

        minimumPriceForPriceRange?.let { minimumPrice ->
            hnlQuizObject.addProperty("SelectedMinValue", minimumPrice)
        } ?: kotlin.run {
            hnlQuizObject.addProperty("SelectedMinValue", 0)
        }
        maximumPriceForPriceRange?.let { maximumPrice ->
            hnlQuizObject.addProperty("SelectedMaxValue", maximumPrice)
        } ?: kotlin.run {
            hnlQuizObject.addProperty("SelectedMaxValue", 0)
        }
        hnlQuizObject.addProperty("PageNo", 0)
        hnlQuizObject.addProperty("SortByPrice", 0)
        hnlQuizObject.addProperty("IncludePackages", 0)

        jsonObject.add("HnLQuizItems", hnlQuizObject)
        homeAndLandViewModel.fetchHnLPackages(userId, jsonObject)
        setToolbarHeaderTitle()
    }
   private fun setHeader(storey:Int){
       AppConstants.SELECTED_STOREY = storey

       storeyCount = storey
       if (AppConstants.SELECTED_STOREY != 3) {
           when (storey) {
               1 -> {
                   AppConstants.STOREYS_HEADER = " | Single"
                   AppConstants.toolheaderHashMapHomeLand[AppConstants.STOREYS] = " | Single"
                   storeyString = " Single"

               }
               2 -> {
                   AppConstants.STOREYS_HEADER = " | Double"
                   AppConstants.toolheaderHashMapHomeLand[AppConstants.STOREYS] = " | Single"
                   storeyString = " Double"
               }

           }
       } else {
           storeyString = ""
           AppConstants.STOREYS_HEADER = ""
       }
       AppConstants.toolheaderHashMapHomeLand[AppConstants.STOREYS] = AppConstants.STOREYS_HEADER
       setToolbarHeaderTitle()
    }
    private fun hnlApiIntilizeCall() {
        var userId = preferences.getUserId()
        val stateId = preferences.getStateID()
        val region = AppConstants.SELECTED_REGION

        val jsonObject = JsonObject()
        val hnlQuizObject = JsonObject()
        hnlQuizObject.addProperty("Region", region)

        val storeyArray = JsonArray()
        storeyArray.add(1)
        storeyArray.add(2)

        AppConstants.BedRooms = arrayOf(3, 4, 5, 6)
        AppConstants.Bathrooms = arrayOf(1, 2, 3, 4, 5, 6)

        val bathrooms = JsonArray()
        for (bathroom in AppConstants.Bathrooms!!) {
            bathrooms.add(bathroom)
        }
        val bedrooms = JsonArray()
        AppConstants.BedRooms?.let {
            for (bed in it) {
                bedrooms.add(bed)
            }
        }
        val carSpaces = JsonArray()
        carSpaces.add(1)
        carSpaces.add(2)

        hnlQuizObject.add("Storey", storeyArray)
        hnlQuizObject.add("BedRooms", bedrooms)
        hnlQuizObject.add("CarSpaces", carSpaces)
        hnlQuizObject.add("Bathrooms", bathrooms)
        hnlQuizObject.addProperty("StateId", stateId)

        minimumPriceForPriceRange?.let { minimumPrice ->
            hnlQuizObject.addProperty("SelectedMinValue", minimumPrice)
        } ?: kotlin.run {
            hnlQuizObject.addProperty("SelectedMinValue", 0)
        }
        maximumPriceForPriceRange?.let { maximumPrice ->
            hnlQuizObject.addProperty("SelectedMaxValue", maximumPrice)
        } ?: kotlin.run {
            hnlQuizObject.addProperty("SelectedMaxValue", 0)
        }
        hnlQuizObject.addProperty("PageNo", 0)
        hnlQuizObject.addProperty("SortByPrice", 0)

        jsonObject.add("HnLQuizItems", hnlQuizObject)
        homeAndLandViewModel.fetchHnLPackages(userId, jsonObject)
    }

    private fun showSnakbar(view: View, message: String) {
     /*   val snack = Snackbar.make(view, message, Snackbar.LENGTH_LONG)
        snack.show()*/
        Toast.makeText(activity,message, Toast.LENGTH_SHORT).show()

    }

    override fun onBackPressed(): Boolean {
        hnLbackHandlingViewModel.setUpdateRegionLiveData(updateRegionApi)
        return false
    }

    private fun previousSelectedItem(selectedItem: Int) {
        when (selectedItem) {
            1 -> {
                changeBackground(binding.llSingle, binding.tvSingle,binding.imgSingle)
            }
            2 -> {
                changeBackground(binding.llDouble, binding.tvDouble,binding.imgDouble)
            }
            3 -> {
                changeBackground(binding.llNotsure, binding.tvNotsure,binding.imgNotsure)
            }

        }

    }

    private fun setToolbarHeaderTitle() {
        val toolHeaderStringBuilder = StringBuilder()
        for (key in AppConstants.toolheaderHashMapHomeLand.keys) {
            /*if (key == AppConstants.STOREYS) {
                var value = AppConstants.toolheaderHashMapHomeLand[key]
                toolHeaderStringBuilder.append(value)
                break
            } else {*/
                var value = AppConstants.toolheaderHashMapHomeLand[key]
                toolHeaderStringBuilder.append(value)
//            }
        }
        toolBinding.tvTool.visibility = View.VISIBLE

        toolBinding.tvTool.text = toolHeaderStringBuilder.toString()

    }

}
