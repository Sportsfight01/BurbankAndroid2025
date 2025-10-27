package com.dmss.burbankapp.ui.homeandland

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
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
import com.dmss.burbankapp.data.model.HomeLandPackageModelObj
import com.dmss.burbankapp.data.model.ToolBarHeaderModel
import com.dmss.burbankapp.databinding.FragmentHomeLandBedRoomsBinding
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
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import common.AppController


class   HomeLandBedRoomsFragment : Fragment(), MainActivity.OnBackPressedListener {


    lateinit var preferences: CustomSharedPreferences
    lateinit var homeAndLandViewModel: HomeAndLandViewModel

    lateinit var hnLbackHandlingViewModel: HnLbackHandlingViewModel
    lateinit var singletonNameViewModelFactory: SingletonNameViewModelFactory

    //Handling Back Stack Flow
    private var updateStoreysApi = true
    private var initilizeApi = false
    lateinit var binding: FragmentHomeLandBedRoomsBinding
    lateinit var toolbarBinding: HomelandToolBinding
    lateinit var profileWithBadgeBinding: ProfileWithBadgeBinding

    lateinit var bottomBinding: LayoutBottomNextPreviousBinding
    lateinit var homeAndLandToolBarAdapter: HomeAndLandToolBarAdapter
    lateinit var toolHeaderList: ArrayList<ToolBarHeaderModel>
    private var customProgressDialog: CustomProgressDialog? = null
    var isNextClickable: Boolean = true
    var isBedRoomSelected = false
    private lateinit var displayToolbarViewModel: DisplayToolbarViewModel

    var minimumPriceForPriceRange: Double? = null
    var maximumPriceForPriceRange: Double? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeLandBedRoomsBinding.inflate(inflater, container, false)
        toolbarBinding = HomelandToolBinding.bind(binding.root)
        bottomBinding = LayoutBottomNextPreviousBinding.bind(binding.root)
        profileWithBadgeBinding = ProfileWithBadgeBinding.bind(binding.root)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        minimumPriceForPriceRange = arguments?.getDouble("MIN_PRICE")
        maximumPriceForPriceRange = arguments?.getDouble("MAX_PRICE")
        preferences = CustomSharedPreferences(requireContext())

        initViews()
        setUpViewModel()
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
        var bedRoomCount = AppConstants.SELECTED_BEDROOM_COUNT
        if(bedRoomCount>-2) {
            previousSelectedItem(bedRoomCount)
            when (bedRoomCount) {
                3 -> {
                    AppConstants.toolheaderHashMapHomeLand[AppConstants.BEDROOMS] = " | 3 Bed"
                    AppConstants.BEDROOMS_HEADER = " | 3 Bed"
                }
                4 -> {
                    AppConstants.toolheaderHashMapHomeLand[AppConstants.BEDROOMS] = " | 4 Bed"
                    AppConstants.BEDROOMS_HEADER = " | 4 Bed"
                }

                5 -> {
                    AppConstants.toolheaderHashMapHomeLand[AppConstants.BEDROOMS] = " | 5+ Bed"
                    AppConstants.BEDROOMS_HEADER = " | 5+ Bed"
                }
                -1 -> {
                    AppConstants.toolheaderHashMapHomeLand[AppConstants.BEDROOMS] = ""
                    AppConstants.BEDROOMS_HEADER = ""
                }

            }
            setToolbarHeaderTitle()
        }

    }

    private fun setupObserver() {
        homeAndLandViewModel.getHnLPackagesLiveData().observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    isBedRoomSelected = true
                    customProgressDialog?.dismissProgress()
                    var regionsData: HnLPackagesModel? = it.data
                    if (regionsData != null && regionsData.status) {
                        bottomBinding.tvPackages.setTextColor(
                            ContextCompat.getColor(
                                AppController.getInstance(),
                                R.color.orange_bg_3_1
                            )
                        )

                        if (this::bottomBinding.isInitialized) {
                            bottomBinding.tvPackages.setTextColor(
                                ContextCompat.getColor(
                                    AppController.getInstance(),
                                    R.color.orange_bg_3_1
                                )
                            )
                            bottomBinding.tvPackages.text =
                                regionsData.HnLQuizResults?.let { it1 ->
                                    AppUtils.getPackageTextBasedOnCount(
                                        it1
                                    )
                                }

                            regionsData.HnLQuizResults?.let {
                                isNextClickable = AppUtils.disableAndEnableView(
                                    it,
                                    bottomBinding.tvPackages,
                                    bottomBinding.next, bottomBinding.buttonNext
                                )

                                preferences.setPackagesCount(it)
                                AppConstants.PACKAGES_COUNT = it
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

        hnLbackHandlingViewModel.getUpdateBedroomsLiveData().observe(viewLifecycleOwner, Observer {
            val isUpdate: Boolean = it
            if (initilizeApi) {
                if (isUpdate) {
                    var bedRoomCount = AppConstants.BEDROOM_COUNT
                    previousSelectedItem(bedRoomCount)

                } else {
                    bottomBinding.tvPackages.text =
                        AppUtils.getPackageTextBasedOnCount(AppConstants.PACKAGES_COUNT)
                }
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

        AppConstants.toolheaderHashMapHomeLand[AppConstants.BEDROOMS] = ""
        AppUtils.profileCountStatus(profileWithBadgeBinding.profileNotification)
        displayToolbarViewModel.favouritesCount.observe(viewLifecycleOwner, Observer<Int> { item ->
            profileWithBadgeBinding.profileNotification.text = item.toString()
            profileNotificationCountView(item)

        })
        customProgressDialog = CustomProgressDialog(requireContext())
        toolHeaderList = ArrayList()
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



        bottomBinding.tvPackages.text =
            AppUtils.getPackageTextBasedOnCount(AppConstants.PACKAGES_COUNT)
        binding.llThree.setOnClickListener {
            changeBackground(binding.llThree, binding.tvThree,binding.tvThreeNumber,binding.imgQuestion,true)
            AppConstants.toolheaderHashMapHomeLand[AppConstants.BEDROOMS] = " | 3 Bed"
            AppConstants.BEDROOMS_HEADER = " | 3 Bed"
            AppConstants.clearCarspaceBathroomsdata()
            hnlApiCall(3)
        }
        binding.llFour.setOnClickListener {
            changeBackground(binding.llFour, binding.tvFour,binding.tvFourNumber,binding.imgQuestion,true)
            AppConstants.toolheaderHashMapHomeLand[AppConstants.BEDROOMS] = " | 4 Bed"
            AppConstants.BEDROOMS_HEADER = " | 4 Bed"
            AppConstants.clearCarspaceBathroomsdata()
            hnlApiCall(4)
        }
        binding.llFive.setOnClickListener {
            changeBackground(binding.llFive, binding.tvFive,binding.tvFiveNumber,binding.imgQuestion,true)
            AppConstants.toolheaderHashMapHomeLand[AppConstants.BEDROOMS] = " | 5+ Bed"
            AppConstants.BEDROOMS_HEADER = " | 5+ Bed"
            AppConstants.clearCarspaceBathroomsdata()
            hnlApiCall(5)
        }
        binding.llNotsure.setOnClickListener {
            changeBackground(binding.llNotsure, binding.tvNotsure,binding.tvFiveNumber,binding.imgQuestion,false)
            AppConstants.toolheaderHashMapHomeLand[AppConstants.BEDROOMS] = ""
            AppConstants.BEDROOMS_HEADER = ""
            AppConstants.clearCarspaceBathroomsdata()
            hnlApiCall(-1)
        }

        bottomBinding.ivNext.setOnClickListener {
            bottomBinding.buttonNext.performClick()
        }
        bottomBinding.buttonNext.setOnClickListener {
            if (isBedRoomSelected) {
                if (isNextClickable && AppConstants.PACKAGES_COUNT != 0) {
                    val fragment =
                        HomeLandPlaceFragment()
                    var bundle = Bundle()
                    AppConstants.clearCarspaceBathroomsdata()
                    minimumPriceForPriceRange?.let { it1 -> bundle.putDouble("MIN_PRICE", it1) }
                    maximumPriceForPriceRange?.let { it1 -> bundle.putDouble("MAX_PRICE", it1) }
                    loadFragment(fragment, bundle)
                }
            } else {
                AppUtils.showCustomCenterToast(
                    bottomBinding.buttonNext.context,
                    "Please select bedrooms"
                )
            }
        }
        bottomBinding.ivPrevios.setOnClickListener {
            updateStoreysApi = false
            hnLbackHandlingViewModel._backPressLiveData.postValue(false)
            requireActivity().onBackPressed()
        }
        bottomBinding.buttonPrevious.setOnClickListener {
            updateStoreysApi = false
            hnLbackHandlingViewModel._backPressLiveData.postValue(false)
            hnLbackHandlingViewModel.setUpdateRegionLiveData(false)

            requireActivity().onBackPressed()
        }
        toolbarBinding.ivBack.setOnClickListener {
            AppConstants.toolheaderHashMapHomeLand.remove(AppConstants.BEDROOMS)
            AppConstants.BEDROOM_COUNT=-2
            AppConstants.SELECTED_BEDROOM_COUNT=0

            hnLbackHandlingViewModel._backPressLiveData.postValue(true)
            hnLbackHandlingViewModel.setUpdateRegionLiveData(true)
            requireActivity().onBackPressed()
        }
        bottomBinding.tvPackages.setOnClickListener {
            AppConstants.FILTER_CarSpaces = arrayOf(1, 2)
            AppConstants.FILTER_BedRooms = arrayOf(1, 2, 3, 4, 5, 6)

            if (isNextClickable && AppConstants.PACKAGES_COUNT != 0) {
                val bundle = Bundle()
                minimumPriceForPriceRange?.let { it1 -> bundle.putDouble("MIN_PRICE", it1) }
                maximumPriceForPriceRange?.let { it1 -> bundle.putDouble("MAX_PRICE", it1) }
                val homeAndLandPackageModelObj = HomeLandPackageModelObj(
                    AppConstants.REGION_HEADER,
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
                AppConstants.clearCarspaceBathroomsdata()
                loadFragment(HomeLandPlaceFragment(), bundle)
            }

        }
        setToolbarHeaderTitle()
    }


    private fun changeBackground(view: View, textView: TextView,numberTextview:TextView,imageview:ImageView,isnumberSelected:Boolean) {
        binding.llThree.background =
            ContextCompat.getDrawable(AppController.getInstance(), R.drawable.rectangel_shape)
        binding.llFour.background =
            ContextCompat.getDrawable(AppController.getInstance(), R.drawable.rectangel_shape)
        binding.llFive.background =
            ContextCompat.getDrawable(AppController.getInstance(), R.drawable.rectangel_shape)
        binding.llNotsure.background =
            ContextCompat.getDrawable(AppController.getInstance(), R.drawable.rectangel_shape)

        binding.tvFiveNumber.setTextColor(
            ContextCompat.getColor(
                AppController.getInstance(),
                R.color.grey_text_font_3_1
            )
        )
        binding.tvFourNumber.setTextColor(
            ContextCompat.getColor(
                AppController.getInstance(),
                R.color.grey_text_font_3_1
            )
        )
        binding.tvThreeNumber.setTextColor(
            ContextCompat.getColor(
                AppController.getInstance(),
                R.color.grey_text_font_3_1
            )
        )
        binding.imgQuestion.setColorFilter(ContextCompat.getColor(AppController.getInstance(), R.color.grey_text_font_3_1));


        binding.tvThree.setTextColor(
            ContextCompat.getColor(
                AppController.getInstance(),
                R.color.grey_text_font_3_1
            )
        )
        binding.tvFour.setTextColor(
            ContextCompat.getColor(
                AppController.getInstance(),
                R.color.grey_text_font_3_1
            )
        )
        binding.tvFive.setTextColor(
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
       if(isnumberSelected){
           numberTextview.setTextColor(
               ContextCompat.getColor(
                   AppController.getInstance(),
                   R.color.white_3_1
               )
           )
       }else{
           imageview.setColorFilter(ContextCompat.getColor(AppController.getInstance(), R.color.white_3_1));

       }

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
        AppConstants.FILTER_MIN_PRICE = ""
        AppConstants.FILTER_MAX_PRICE = ""
        AppConstants.FILTER_Bathrooms = null
        AppConstants.FILTER_BedRooms = null
        AppConstants.FILTER_CarSpaces = null
        AppConstants.FILTER_BEDROOM_COUNT = 0
        AppConstants.FILTER_toolHeaderHashMapHomeLand.clear()
    }


    private fun hnlApiCall(numberOfBedRoom: Int) {
        AppConstants.BEDROOM_COUNT = numberOfBedRoom
        AppConstants.SELECTED_BEDROOM_COUNT=numberOfBedRoom

        val stateId = preferences.getStateID()
        val region = AppConstants.SELECTED_REGION
        val storey = AppConstants.SELECTED_STOREY
        val userId = preferences.getUserId()


        val jsonObject = JsonObject()
        val hnlQuizObject = JsonObject()
        hnlQuizObject.addProperty("Region", region)

        val storeyArray = JsonArray()
        if (storey != -1 && storey != 3) {
            storeyArray.add(storey)
        } else {
            storeyArray.add(1)
            storeyArray.add(2)
        }

        when (numberOfBedRoom) {
            3 -> {
                AppConstants.BedRooms = arrayOf(3)
            }
            4 -> {
                AppConstants.BedRooms = arrayOf(4)
            }
            5 -> {
                AppConstants.BedRooms = arrayOf(5, 6)
            }
            else -> {
                AppConstants.BedRooms = arrayOf(3, 4, 5, 6)
            }
        }
        AppConstants.Bathrooms = arrayOf(1, 2, 3, 4, 5, 6)

        setToolbarHeaderTitle()
        val bedrooms = JsonArray()
        AppConstants.BedRooms?.let {
            for (bedroomCount in it)
                bedrooms.add(bedroomCount)
        }


        val carSpaces = JsonArray()
        carSpaces.add(1)
        carSpaces.add(2)
        val bathrooms = JsonArray()

        AppConstants.Bathrooms?.let {
            for (bathRoom in it) {
                bathrooms.add(bathRoom)
            }
        }


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
    }

    override fun onBackPressed(): Boolean {
        hnLbackHandlingViewModel.setUpdateStoreysLiveData(updateStoreysApi)
        return false
    }

    private fun previousSelectedItem(selectedItem: Int) {
        when (selectedItem) {
            5 -> {
                changeBackground(binding.llFive, binding.tvFive,binding.tvFiveNumber,binding.imgQuestion,true)
                hnlApiCall(selectedItem)
            }
            4 -> {
                changeBackground(binding.llFour, binding.tvFour,binding.tvFourNumber,binding.imgQuestion,true)
                hnlApiCall(selectedItem)
            }
            3 -> {
                changeBackground(binding.llThree, binding.tvThree,binding.tvThreeNumber,binding.imgQuestion,true)
                hnlApiCall(selectedItem)
            }
            -1 -> {
                changeBackground(binding.llNotsure, binding.tvNotsure,binding.tvNotsure,binding.imgQuestion,false)
                hnlApiCall(selectedItem)
            }

        }

    }

    private fun setToolbarHeaderTitle() {
        val toolHeaderStringBuilder = StringBuilder()
        for (key in AppConstants.toolheaderHashMapHomeLand.keys) {
            if (key == AppConstants.BEDROOMS) {
                var value = AppConstants.toolheaderHashMapHomeLand[key]
                toolHeaderStringBuilder.append(value)
                break
            } else {
                var value = AppConstants.toolheaderHashMapHomeLand[key]
                toolHeaderStringBuilder.append(value)
            }
        }
        toolbarBinding.tvTool.visibility = View.VISIBLE
        toolbarBinding.tvTool.text = toolHeaderStringBuilder.toString()

    }


}
