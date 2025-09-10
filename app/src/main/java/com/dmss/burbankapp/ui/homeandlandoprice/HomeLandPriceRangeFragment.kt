package com.dmss.burbankapp.ui.homeandlandoprice

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.dmss.burbankapp.data.model.HnLMinAndMaxPriceModel
import com.dmss.burbankapp.data.model.HnLPackagesModel
import com.dmss.burbankapp.data.model.PriceRangeListModel
import com.dmss.burbankapp.databinding.FragmentHomeLandPriceRangeBinding
import com.dmss.burbankapp.databinding.HomelandToolBinding
import com.dmss.burbankapp.databinding.LayoutBottomNextPreviousBinding
import com.dmss.burbankapp.databinding.ProfileWithBadgeBinding
import com.dmss.burbankapp.ui.homeandland.HomeAndLandStoreysFragment
import com.dmss.burbankapp.ui.homeandland.HomeAndLandToolBarAdapter
import com.dmss.burbankapp.ui.homeandlandplaces.HomeLandPlaceFragment
import com.dmss.burbankapp.ui.homeandlandregions.HomeAndLandViewModel
import com.dmss.burbankapp.ui.main.MainActivity
import com.dmss.burbankapp.ui.mydisplay.DisplayToolbarViewModel
import com.dmss.burbankapp.ui.view.CustomProgressDialog
import com.dmss.burbankapp.utils.AppConstants
import com.dmss.burbankapp.utils.AppUtils
import com.dmss.burbankapp.viewmodel.HnLbackHandlingViewModel
import com.dmss.burbankapp.viewmodel.SingletonNameViewModelFactory
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import common.AppController
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber
import java.text.DecimalFormat


class HomeLandPriceRangeFragment : Fragment(), MainActivity.OnBackPressedListener {
    lateinit var homeAndLandViewModel: HomeAndLandViewModel
    lateinit var textList: ArrayList<TextView>
    lateinit var preferences: CustomSharedPreferences
    lateinit var hnLbackHandlingViewModel: HnLbackHandlingViewModel
    lateinit var singletonNameViewModelFactory: SingletonNameViewModelFactory
    lateinit var binding: FragmentHomeLandPriceRangeBinding
    lateinit var toolBinding: HomelandToolBinding
    lateinit var bottomBinding: LayoutBottomNextPreviousBinding
    lateinit var homeAndLandToolBarAdapter: HomeAndLandToolBarAdapter
    private val disposable: CompositeDisposable = CompositeDisposable()
    var isPriceChange = false

    var minimumPriceForPriceRange: Double? = null
    var maximumPriceForPriceRange: Double? = null



    var priceSetToTheSeekbar = false
    lateinit var profileWithBadgeBinding: ProfileWithBadgeBinding

    private lateinit var displayToolbarViewModel: DisplayToolbarViewModel

    var minimumSelectedPriceForPriceRange: Double? = null
    var maximumSelectedPriceForPriceRange: Double? = null


    var priceRangeList: ArrayList<PriceRangeListModel>? = null
    var isNextClickable: Boolean = true
    var showBarGraph = true
    var apiFirstTime = true
    var barGraphValue: Int? = null

    //Handling Back Stack Flow
    private var updateStoreysApi = true
    private var customProgressDialog: CustomProgressDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeLandPriceRangeBinding.inflate(inflater, container, false)
        toolBinding = HomelandToolBinding.bind(binding.root)
        bottomBinding = LayoutBottomNextPreviousBinding.bind(binding.root)
        profileWithBadgeBinding = ProfileWithBadgeBinding.bind(binding.root)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        preferences = CustomSharedPreferences(AppController.getInstance())

        setUpViewModel()
        initViews()

    }


    private fun setUpViewModel() {
        singletonNameViewModelFactory = SingletonNameViewModelFactory()
        hnLbackHandlingViewModel = ViewModelProviders.of(this, singletonNameViewModelFactory).get(
            HnLbackHandlingViewModel::class.java
        )

        homeAndLandViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(
                ApiHelperImpl(RetrofitBuilder.apiService),
                DatabaseHelperImpl(DatabaseBuilder.getInstance(AppController.getInstance()))
            )
        ).get(HomeAndLandViewModel::class.java)
        setupObserver()

    }
    private fun saveServiceData(regionsData: HnLPackagesModel,isServiceUpdate:Boolean){
        bottomBinding.tvPackages.setTextColor(
            ContextCompat.getColor(
                AppController.getInstance(),
                R.color.orange_bg_3_1
            )
        )
        var minRangeValue: Int? = null
        var maxRangeValue: Int? = null
        var minPriceForApi: Double? = null
        var maxPriceForApi: Double? = null
        regionsData.priceRangeModel?.let { priceRangeModel ->
            priceRangeModel.MinPrice?.let { minPrice ->
                /*val reminderOfMinRangeValue = minPrice.toInt().rem(1000)
                minRangeValue = if (reminderOfMinRangeValue > 200) {
                    (minPrice.toInt() / 1000).minus(1)
                } else {
                    minPrice.toInt() / 1000
                }*/
                minRangeValue = minPrice.toInt() / 1000
//                minimumPriceForPriceRange = minPrice
                minPriceForApi = minPrice
            }
            priceRangeModel.MaxPrice?.let { maxPrice ->
                /*val reminderOfMaxRangeValue = maxPrice.toInt().rem(1000)
                maxRangeValue = if (reminderOfMaxRangeValue > 200) {
                    (maxPrice.toInt() / 1000).plus(1)
                } else {
                    maxPrice.toInt() / 1000
                }*/

                maxRangeValue = maxPrice.toInt() / 1000
//                maximumPriceForPriceRange = maxPrice
                maxPriceForApi = maxPrice
            }

        }
        if(AppConstants.totalmaximumPrice==0) {
            AppConstants.totalminimumPrice = minRangeValue!!
            AppConstants.totalmaximumPrice = maxRangeValue!!
        }


        AppConstants.MIN_PRICE = minRangeValue.toString()
        AppConstants.MAX_PRICE = maxRangeValue.toString()
        /* binding.tvSeek.text =
             ("$" + minRangeValue + "K to " + "$" + maxPriceValue + "K")*/
        if (AppConstants.REGION.isEmpty() || AppConstants.REGION_HEADER.isEmpty()) {
            AppConstants.PRICE_RANGE_HEADER =
                " $" + minRangeValue + "K-" + "$" + maxRangeValue + "K"
        } else {
            AppConstants.PRICE_RANGE_HEADER =
                " | $" + minRangeValue + "K-" + "$" + maxRangeValue + "K"
        }

        minimumPriceForPriceRange = minRangeValue!!*1000.toDouble()
        maximumPriceForPriceRange=maxRangeValue!!*1000.toDouble()
        AppConstants.toolheaderHashMapHomeLand[AppConstants.PRICERANGE] =
            AppConstants.PRICE_RANGE_HEADER
        //UPDTATING TOOLBAR ADAPTER
        setToolbarHeaderTitle()

        // binding.tvSeek.text = ("$${minRangeValue}K to $${maxRangeValue}K")
        updatePriceText(minRangeValue, maxRangeValue)

        val intArray = regionsData.countDistribution
        if (intArray != null) {
            if (intArray.size > 6)
                intArray.let { it1 -> setBarHeightsDynamic(it1) }
        }
        bottomBinding.tvPackages.setTextColor(
            ContextCompat.getColor(
                AppController.getInstance(),
                R.color.orange_bg_3_1
            )
        )
        priceRangeList = regionsData.priceRangeList
        if(isServiceUpdate){
            minRangeValue?.let { it1 -> binding.seekbar.setMinValue(it1.toFloat()) }
            maxRangeValue?.let { it1 -> binding.seekbar.setMaxValue(it1.toFloat()) }
            minRangeValue?.let { it1 -> binding.seekbar.setMinStartValue(it1.toFloat()) }
            maxRangeValue?.let { it1 -> binding.seekbar.setMaxStartValue(it1.toFloat()).apply() }
      /*      binding.seekbar.setMinStartValue(AppConstants.priceRangeMinimum!!.toFloat())
            binding.seekbar.setMaxStartValue(AppConstants.priceRangeMaxmimu!!.toFloat()).apply()*/
            if (regionsData.HnLQuizResults != 0) {
                regionsData.HnLQuizResults?.let {
                    AppConstants.PACKAGES_COUNT = it
                }
            }
            AppConstants.priceRangeMinimum=minRangeValue!!.toFloat()
            AppConstants.priceRangeMaxmimu=maxRangeValue!!.toFloat()
        }
        regionsData.HnLQuizResults?.let { it1 -> preferences.setPackagesCount(it1) }
        bottomBinding.tvPackages.text =
            regionsData.HnLQuizResults?.let { it1 ->
                AppUtils.getPackageTextBasedOnCount(
                    it1
                )
            }

        if (this::bottomBinding.isInitialized) {
            isNextClickable = regionsData.HnLQuizResults?.let { it1 ->
                AppUtils.disableAndEnableView(
                    it1,
                    bottomBinding.tvPackages,
                    bottomBinding.next, bottomBinding.buttonNext
                )
            } == true
            if(isServiceUpdate){
                AppUtils.disableAndEnableView(
                    0,
                    bottomBinding.tvPackages,
                    bottomBinding.next, bottomBinding.buttonNext
                )
            }

            bottomBinding.tvPackages.isEnabled=true
            bottomBinding.tvPackages.setTextColor(
                ContextCompat.getColor(
                    AppController.getInstance(),
                    R.color.grey_text_font_3_1
                )
            )

        }
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
            bottomBinding.tvPackages.text =
                AppUtils.getPackageTextBasedOnCount(AppConstants.PACKAGES_COUNT)
            setToolbarHeaderTitle()
//            }
        })
        homeAndLandViewModel.getHnLPackagesLiveDataFirst().observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {

                    customProgressDialog?.dismissProgress()
                    val regionsData: HnLPackagesModel? = it.data
                    if (regionsData != null && regionsData.status) {
                        AppConstants.homeLandregionsData=regionsData
                        saveServiceData(regionsData,true)


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

        homeAndLandViewModel.getHnLPackagesLiveData().observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {

                    customProgressDialog?.dismissProgress()
                    val regionsData: HnLPackagesModel? = it.data
                    if (regionsData != null && regionsData.status) {
                        AppConstants.homeLandregionsData=regionsData
                        AppConstants.STOREYS_HEADER=""
                        AppConstants.BEDROOMS_HEADER=""
                        bottomBinding.tvPackages.setTextColor(
                            ContextCompat.getColor(
                                AppController.getInstance(),
                                R.color.orange_bg_3_1
                            )
                        )
                        var minRangeValue: Int? = null
                        var maxRangeValue: Int? = null
                        var minPriceForApi: Double? = null
                        var maxPriceForApi: Double? = null
                        regionsData.priceRangeModel?.let { priceRangeModel ->
                            priceRangeModel.MinPrice?.let { minPrice ->
                                val reminderOfMinRangeValue = minPrice.toInt().rem(1000)
                                minRangeValue = if (reminderOfMinRangeValue > 200) {
                                    (minPrice.toInt() / 1000).minus(1)
                                } else {
                                    minPrice.toInt() / 1000
                                }
                                minimumPriceForPriceRange = minPrice
                                minPriceForApi = minPrice
                            }
                            priceRangeModel.MaxPrice?.let { maxPrice ->
                                val reminderOfMaxRangeValue = maxPrice.toInt().rem(1000)
                                maxRangeValue = if (reminderOfMaxRangeValue > 200) {
                                    (maxPrice.toInt() / 1000).plus(1)
                                } else {
                                    maxPrice.toInt() / 1000
                                }
                                maximumPriceForPriceRange = maxPrice
                                maxPriceForApi = maxPrice
                            }
                            AppConstants.priceRangeMinimum=minimumPriceForPriceRange!!.toFloat()/1000
                            AppConstants.priceRangeMaxmimu=maximumPriceForPriceRange!!.toFloat()/1000
                            if(AppConstants.totalmaximumPrice==0) {
                                AppConstants.totalminimumPrice = minRangeValue!!
                                AppConstants.totalmaximumPrice = maxRangeValue!!
                            }
                        }
                        AppConstants.MIN_PRICE = minRangeValue.toString()
                        AppConstants.MAX_PRICE = maxRangeValue.toString()
                        AppConstants.SELECTED_MIN_PRICE =""+minPriceForApi
                        AppConstants.SELECTED_MAX_PRICE =""+maxPriceForApi

                        binding.seekbar.setMinStartValue(AppConstants.MIN_PRICE!!.toFloat())
                        binding.seekbar.setMaxStartValue(AppConstants.MAX_PRICE !!.toFloat())
                        /* binding.tvSeek.text =
                             ("$" + minRangeValue + "K to " + "$" + maxPriceValue + "K")*/
                        if (AppConstants.REGION.isEmpty() || AppConstants.REGION_HEADER.isEmpty()) {
                            AppConstants.PRICE_RANGE_HEADER =
                                " $" + minRangeValue + "K-" + "$" + maxRangeValue + "K"
                        } else {
                            AppConstants.PRICE_RANGE_HEADER =
                                " | $" + minRangeValue + "K-" + "$" + maxRangeValue + "K"
                        }


                        AppConstants.toolheaderHashMapHomeLand[AppConstants.PRICERANGE] =
                            AppConstants.PRICE_RANGE_HEADER
                        //UPDTATING TOOLBAR ADAPTER
                        setToolbarHeaderTitle()

                        // binding.tvSeek.text = ("$${minRangeValue}K to $${maxRangeValue}K")
                        updatePriceText(minRangeValue, maxRangeValue)

                        val intArray = regionsData.countDistribution
                        if (intArray != null) {
                            if (intArray.size > 6)
                                intArray.let { it1 -> setBarHeightsDynamic(it1) }
                        }
                        bottomBinding.tvPackages.setTextColor(
                            ContextCompat.getColor(
                                AppController.getInstance(),
                                R.color.appColor
                            )
                        )
                        priceRangeList = regionsData.priceRangeList
                        if (regionsData.HnLQuizResults != 0) {
                            regionsData.HnLQuizResults?.let {
                                AppConstants.PACKAGES_COUNT = it
                            }

                        }

                        regionsData.HnLQuizResults?.let { it1 -> preferences.setPackagesCount(it1) }
                        bottomBinding.tvPackages.text =
                            regionsData.HnLQuizResults?.let { it1 ->
                                AppUtils.getPackageTextBasedOnCount(
                                    it1
                                )
                            }

                        if (this::bottomBinding.isInitialized) {
                            isNextClickable = regionsData.HnLQuizResults?.let { it1 ->
                                AppUtils.disableAndEnableView(
                                    it1,
                                    bottomBinding.tvPackages,
                                    bottomBinding.next, bottomBinding.buttonNext
                                )
                            } == true
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
        homeAndLandViewModel.getHnLMinAndMaxPriceLiveData().observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    customProgressDialog?.dismissProgress()
                    var regionsData: HnLMinAndMaxPriceModel? = it.data
                    if (regionsData != null) {
                        Timber.e("regionsData $regionsData")
                        if (regionsData.status) {
                            Timber.e("Statue ${regionsData.status}")
                            bottomBinding.tvPackages.setTextColor(
                                ContextCompat.getColor(
                                    AppController.getInstance(),
                                    R.color.appColor
                                )
                            )
                            var minRangeValue: Int? = null
                            var maxPriceValue: Int? = null
                            var minPriceForApi: Double? = null
                            var maxPriceForApi: Double? = null
                            regionsData.priceModel?.let { priceRangeModel ->
                                priceRangeModel.MinPrice?.let { minPrice ->
                                    minRangeValue = minPrice.toInt() / 1000
                                    minPriceForApi = minPrice
                                }
                                priceRangeModel.MaxPrice?.let { maxPrice ->
                                    maxPriceValue = maxPrice.toInt() / 1000
                                    maxPriceForApi = maxPrice
                                }

                            }

                            if (minPriceForApi != null && maxPriceForApi != null) {
                                hnlApiCall(
                                    minPriceValue = minPriceForApi.toString(),
                                    maxPriceValue = maxPriceForApi.toString()
                                )
                            }
                            val reminderOfMinRangeValue = minRangeValue?.rem(1000)
                            val reminderOfMaxRangeValue = maxPriceValue?.rem(1000)
                            if (reminderOfMaxRangeValue != null) {
                                if (reminderOfMaxRangeValue > 200) {
                                    maxPriceValue = maxPriceValue?.plus(1)
                                }
                            }
                            AppConstants.MIN_PRICE = minRangeValue.toString()
                            AppConstants.MAX_PRICE = maxPriceValue.toString()
                            /* binding.tvSeek.text =
                                 ("$" + minRangeValue + "K to " + "$" + maxPriceValue + "K")*/
                            if (AppConstants.REGION.isEmpty() || AppConstants.REGION_HEADER.isEmpty()) {
                                AppConstants.PRICE_RANGE_HEADER =
                                    " $" + minRangeValue + "K-" + "$" + maxPriceValue + "K"
                            } else {
                                AppConstants.PRICE_RANGE_HEADER =
                                    " | $" + minRangeValue + "K-" + "$" + maxPriceValue + "K"
                            }


                            AppConstants.toolheaderHashMapHomeLand[AppConstants.PRICERANGE] =
                                AppConstants.PRICE_RANGE_HEADER
                            //UPDTATING TOOLBAR ADAPTER
                            setToolbarHeaderTitle()
                            minRangeValue?.let { it1 -> binding.seekbar.setMinValue(it1.toFloat()) }
                            maxPriceValue?.let { it1 -> binding.seekbar.setMaxValue(it1.toFloat()) }
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
        // commented by durga 23/02/2023
        /* hnLbackHandlingViewModel.bacPressLiveData.observe(viewLifecycleOwner, Observer {

             if (it) {
                 AppConstants.previouslySelectedMinPrice?.let { minimum ->

                     AppConstants.previouslySelectedMaxPrice?.let { maximum ->
                         hnlApiCall(
                             minimum.toString(),
                             maximum.toString()
                         )

                     }

                 }

             }
         })*/

    }


    private fun setBarHeightsDynamic(intArray: Array<Int>) {
        val maxNumber = intArray.maxOrNull()
        textList.forEachIndexed { index, element ->
            val minValue = intArray[index]
            if (minValue == 0 || minValue == 1) {
                element.height = 70
            } else {
                val height: Double = ((minValue.toDouble() / maxNumber!!) * 200) + 70
                element.height = height.toInt()
            }
            element.text = intArray[index].toString()
        }
    }

    fun loadFragment(fragment: Fragment, bundle: Bundle) {
        clearFilterData()
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
    private fun profileNotificationCountView(notificationCount:Int){
        if(notificationCount==0){
            profileWithBadgeBinding.profileNotification.visibility=View.GONE
        }else{
            profileWithBadgeBinding.profileNotification.visibility=View.VISIBLE
        }
    }
    private fun initViews() {
        displayToolbarViewModel = ViewModelProviders.of(requireActivity())[DisplayToolbarViewModel::class.java]


        profileWithBadgeBinding.profileNotification.text=AppConstants.TotalMyFavs.toString()
        profileNotificationCountView(AppConstants.TotalMyFavs)
        displayToolbarViewModel.favouritesCount.observe(viewLifecycleOwner, Observer<Int> { item ->
            profileWithBadgeBinding.profileNotification.text = item.toString()
            profileNotificationCountView(item)
        })
        if (AppConstants.REGION_HEADER.isEmpty()) {
            toolBinding.tvTool.text = ("Take a quick survey to find your perfect design")
        } else {
            toolBinding.tvTool.text = AppConstants.REGION_HEADER
            setToolbarHeaderTitle()
        }
        minimumPriceForPriceRange = arguments?.getDouble("MIN_PRICE")
        maximumPriceForPriceRange = arguments?.getDouble("MAX_PRICE")
        customProgressDialog = CustomProgressDialog(requireContext())

        toolBinding.ivBack.setOnClickListener {
            AppConstants.previouslySelectedMinPrice = 0.0
            AppConstants.previouslySelectedMaxPrice = 0.0
        }
        textList = ArrayList()
        textList.add(binding.first)
        textList.add(binding.second)
        textList.add(binding.third)
        textList.add(binding.fourth)
        textList.add(binding.fifth)
        textList.add(binding.sixth)
        textList.add(binding.seventh)

        bottomBinding.tvPackages.setOnClickListener {
            /* AppConstants.STOREYS_HEADER =""
             AppConstants.BEDROOMS_HEADER =""*/
            AppConstants.FILTER_CarSpaces = arrayOf(1, 2)
            AppConstants.FILTER_BedRooms = arrayOf(1, 2, 3, 4, 5, 6)
            AppConstants.BedRooms= arrayOf(1,2,3,4,5,6)

            if (isNextClickable) {
                var fragment =
                    HomeLandPlaceFragment();

                val bundle = Bundle()
                bundle.putBoolean(AppConstants.SELECT_FAVORITE, false)
                minimumSelectedPriceForPriceRange?.let { it1 ->
                    bundle.putDouble("MIN_PRICE", it1)
                    AppConstants.previouslySelectedMinPrice = it1
                } ?: kotlin.run {
                    minimumPriceForPriceRange?.let { it1 ->
                        bundle.putDouble("MIN_PRICE", it1)
                        AppConstants.previouslySelectedMinPrice = it1
                    }
                }
                maximumSelectedPriceForPriceRange?.let { it1 ->
                    bundle.putDouble("MAX_PRICE", it1)
                    AppConstants.previouslySelectedMaxPrice = it1
                } ?: let {
                    maximumPriceForPriceRange?.let { it1 ->
                        bundle.putDouble("MAX_PRICE", it1)
                        AppConstants.previouslySelectedMaxPrice = it1
                    }
                }
                AppConstants.ISFROM_SKIP = false
                AppConstants.clearCarspaceBathroomsdata()
                loadFragment(fragment, bundle)
            }
        }

        toolBinding.ivBack.setOnClickListener {
            AppConstants.selectedmaximumPriceForPriceRange=-1.0
            AppConstants.totalmaximumPrice=0
            AppConstants.totalminimumPrice=0
            AppConstants.homeLandregionsData=null
            AppConstants.SELECTED_BEDROOM_COUNT=0
            AppConstants.SELECTED_STOREY=0
            AppConstants.BEDROOM_COUNT=-2
            AppConstants.SELECTED_MIN_PRICE="0.0"
            AppConstants.SELECTED_MAX_PRICE="0.0"
            AppConstants.SELECTED_STOREY_PREVIOUS=0
            AppConstants.toolheaderHashMapHomeLand.remove(AppConstants.STOREYS)
            AppConstants.toolheaderHashMapHomeLand.remove(AppConstants.BEDROOMS)
            AppConstants.toolheaderHashMapHomeLand.remove(AppConstants.PRICERANGE)
            if(!AppConstants.ISFROM_SKIP) {
                hnLbackHandlingViewModel.setUpdateRegionLiveData(true)
            }

            (activity as MainActivity).onBackPressed()
        }
        var isUserLoggedIn = preferences.getUserLogin()
        if (!isUserLoggedIn) {
            profileWithBadgeBinding.tvFavorites.background = resources.getDrawable(R.drawable.disable_rectangle_new)
            profileWithBadgeBinding.profileNotification.visibility=View.GONE
        }
        bottomBinding.ivNext.setOnClickListener {
            bottomBinding.buttonNext.performClick()
        }
        bottomBinding.buttonNext.setOnClickListener {
            if (isPriceChange) {
                if (isNextClickable) {
                    val bundle = Bundle()
                    bundle.putBoolean(AppConstants.SELECT_FAVORITE, true)
                    minimumSelectedPriceForPriceRange?.let { it1 ->
                        bundle.putDouble("MIN_PRICE", it1)
                    } ?: kotlin.run {
                        minimumPriceForPriceRange?.let { it1 -> bundle.putDouble("MIN_PRICE", it1) }
                    }
                    maximumSelectedPriceForPriceRange?.let { it1 ->
                        bundle.putDouble("MAX_PRICE", it1)
                    } ?: let {
                        maximumPriceForPriceRange?.let { it1 -> bundle.putDouble("MAX_PRICE", it1) }
                    }
                    AppConstants.selectedminimumPriceForPriceRange=minimumPriceForPriceRange
                    AppConstants.selectedmaximumPriceForPriceRange=maximumPriceForPriceRange
                    loadFragment(HomeAndLandStoreysFragment(), bundle)

                }
            } else {
                AppUtils.showCustomCenterToast(
                    bottomBinding.buttonNext.context,
                    "Please select price"
                )
            }
        }

        bottomBinding.buttonPrevious.setOnClickListener {
            updateStoreysApi = false
            updateStoreysApi = if(AppConstants.ISFROM_SKIP) {
                hnLbackHandlingViewModel.setUpdateRegionLiveData(true)
                true
            }else{
                hnLbackHandlingViewModel.setUpdateRegionLiveData(false)
                false

            }
            activity!!.onBackPressed()
        }


        AppConstants.toolheaderHashMapHomeLand[AppConstants.PRICERANGE] = ""

        setToolbarHeaderTitle()
        // getMinAndMaxPrice(minimumPriceForPriceRange, maximumPriceForPriceRange)
        binding.first.setOnClickListener {
            priceRangeList?.let { listData ->
                var model: PriceRangeListModel = listData[0]
                showMinAndMaxValue(model)
            }
        }
        binding.second.setOnClickListener {
            priceRangeList?.let { listData ->
                var model: PriceRangeListModel = listData[1]
                showMinAndMaxValue(model)
            }
        }
        binding.third.setOnClickListener {
            priceRangeList?.let { listData ->
                var model: PriceRangeListModel = listData[2]
                showMinAndMaxValue(model)
            }
        }
        binding.fourth.setOnClickListener {
            priceRangeList?.let { listData ->
                var model: PriceRangeListModel = listData[3]
                showMinAndMaxValue(model)
            }
        }
        binding.fifth.setOnClickListener {
            priceRangeList?.let { listData ->
                var model: PriceRangeListModel = listData[4]
                showMinAndMaxValue(model)
            }
        }
        binding.sixth.setOnClickListener {
            priceRangeList?.let { listData ->
                var model: PriceRangeListModel = listData[5]
                showMinAndMaxValue(model)
            }
        }
        binding.seventh.setOnClickListener {
            priceRangeList?.let { listData ->
                var model: PriceRangeListModel = listData[6]
                showMinAndMaxValue(model)
            }
        }
        binding.seekbar.setOnRangeSeekbarChangeListener {
                minValue, maxValue ->
            /* if (AppConstants.REGION.isEmpty() || AppConstants.REGION_HEADER.isEmpty()) {
                 AppConstants.PRICE_RANGE_HEADER =
                     " $" + minValue + "K-" + "$" + maxValue + "K"
             } else {*/
            AppConstants.PRICE_RANGE_HEADER =
                " | $" + minValue + "K-" + "$" + maxValue + "K"
//            }


            AppConstants.toolheaderHashMapHomeLand[AppConstants.PRICERANGE] =
                AppConstants.PRICE_RANGE_HEADER
            //UPDTATING TOOLBAR ADAPTER
            setToolbarHeaderTitle()
            updatePriceText(minValue.toInt(),maxValue.toInt())
        }
        binding.seekbar.setOnRangeSeekbarFinalValueListener { minValue, maxValue ->
            Timber.e("Price Range Values  $minValue --- $maxValue")
            val minPriceValue = (minValue.toInt() * 1000).toString()
            val maxPriceValue = (maxValue.toInt() * 1000).toString()
            isPriceChange = true
            hnlApiCall(minPriceValue, maxPriceValue)

        }
        getLiveData()
    }
    private fun getLiveData(){
        if(AppConstants.homeLandregionsData==null) {
            AppConstants.totalminimumPrice=0
            AppConstants.totalmaximumPrice=0

            hnlApiCallFirst(
                AppConstants.totalminimumPrice.toString(),
                AppConstants.totalmaximumPrice.toString()
            )
        }else{
            saveServiceData(AppConstants.homeLandregionsData!!,false)
//        println("priceRangeMinimum:: "+AppConstants.priceRangeMinimum+" priceRangeMaxmimu:: "+AppConstants.priceRangeMaxmimu+" totalminimumPrice::  "+AppConstants.totalminimumPrice.toFloat()+"  totalmaximumPrice:: "+AppConstants.totalmaximumPrice.toFloat()+" PACKAGES_COUNT:: "+AppConstants.PACKAGES_COUNT)
            binding.seekbar.setMinValue(AppConstants.totalminimumPrice.toFloat())
            binding.seekbar.setMaxValue(AppConstants.totalmaximumPrice.toFloat())
            binding.seekbar.setMinStartValue(AppConstants.priceRangeMinimum!!.toFloat())
            binding.seekbar.setMaxStartValue(AppConstants.priceRangeMaxmimu!!.toFloat()).apply()
            bottomBinding.tvPackages.text =AppUtils.getPackageTextBasedOnCount(AppConstants.PACKAGES_COUNT)
            println("binding.seekbar back:: "+ binding.seekbar.selectedMinValue.toString()+"  "+binding.seekbar.selectedMaxValue.toString()+" totalminimumPrice:: "+AppConstants.totalminimumPrice.toFloat()+" totalmaximumPrice:: "+AppConstants.totalmaximumPrice.toFloat())

            isNextClickable=true
            isPriceChange=true

//            AppConstants.priceRangeMinimum?.let { it1 -> binding.seekbar.setMinStartValue(it1) }
//            AppConstants.priceRangeMaxmimu?.let { it1 ->
//                binding.seekbar.setMaxStartValue(it1).apply()
//            }

        }
    }
    private fun hnlApiCallFirst(minPriceValue: String, maxPriceValue: String) {
        var stateId = preferences.getStateID()
        var region = AppConstants.SELECTED_REGION
        var userId = preferences.getUserId()

        var jsonObject = JsonObject()
        var hnlQuizObject = JsonObject()

        val storeyArray = JsonArray()
        storeyArray.add(1)
        storeyArray.add(2)


        AppConstants.Bathrooms = arrayOf(1, 2, 3, 4, 5, 6)
        val bathrooms = JsonArray()
        for (bathRoom in AppConstants.Bathrooms!!) {
            bathrooms.add(bathRoom)
        }
        val bedrooms = JsonArray()
        var bedRooms = arrayOf(3, 4, 5, 6)
        for (bed in bedRooms) {
            bedrooms.add(bed)
        }
        val carSpaces = JsonArray()
        carSpaces.add(1)
        carSpaces.add(2)

        hnlQuizObject.addProperty("Region", region)
        hnlQuizObject.add("Storey", storeyArray)
        hnlQuizObject.add("BedRooms", bedrooms)
        hnlQuizObject.add("CarSpaces", carSpaces)
        hnlQuizObject.add("Bathrooms", bathrooms)
        hnlQuizObject.addProperty("StateId", stateId)
        hnlQuizObject.addProperty("SelectedMinValue", minPriceValue)
        hnlQuizObject.addProperty("SelectedMaxValue", maxPriceValue)
        hnlQuizObject.addProperty("PageNo", 0)
        hnlQuizObject.addProperty("SortByPrice", 0)
        hnlQuizObject.addProperty("IncludePackages", 0)

        jsonObject.add("HnLQuizItems", hnlQuizObject)
        homeAndLandViewModel.fetchHnLPackagesFirst(userId, jsonObject)
    }

    private fun showMinAndMaxValue(model: PriceRangeListModel) {
        isPriceChange = true
        showBarGraph = false
        apiFirstTime = false
        var minRangeValue: Int? = null
        var minRangeSelectedValue: Int? = null
        var maxPriceSelectedValue: Int? = null
        var maxPriceValue: Int? = null
        minimumPriceForPriceRange?.let {
            minRangeValue = it.toInt() / 1000
        }
        maximumPriceForPriceRange?.let {
            maxPriceValue = it.toInt() / 1000
        }

        val reminderOfMaxRangeValue = maxPriceValue?.rem(1000)
        if (reminderOfMaxRangeValue != null) {
            if (reminderOfMaxRangeValue > 200) {
                maxPriceValue = maxPriceValue?.plus(1)
            }
        }

        /*  minRangeValue?.let { it1 -> binding.seekbar.setMinValue(it1.toFloat()) }
          maxPriceValue?.let { it1 -> binding.seekbar.setMaxValue(it1.toFloat()) }
  */
        Timber.e("Show Min And Max Price Values $minimumPriceForPriceRange -- $maximumPriceForPriceRange")
        model.minPrice.let {
            minRangeSelectedValue = model.minPrice.toInt() / 1000
        }
        model.maxPrice.let {
            maxPriceSelectedValue = model.maxPrice.toInt() / 1000
        }

        val reminderOfMaxRangeSelectedValue = maxPriceSelectedValue?.rem(1000)
        /*if (reminderOfMaxRangeSelectedValue != null) {
            if (reminderOfMaxRangeSelectedValue > 200) {
                maxPriceSelectedValue = maxPriceSelectedValue?.plus(1)
            }
        }*/
        minRangeSelectedValue?.let { it1 ->
            AppConstants.priceRangeMinimum=it1.toFloat()
            binding.seekbar.setMinStartValue(it1.toFloat()) }
        maxPriceSelectedValue?.let { it1 ->
            AppConstants.priceRangeMaxmimu=it1.toFloat()

            binding.seekbar.setMaxStartValue(it1.toFloat()).apply()
        }

        // binding.tvSeek.text = ("$${minRangeSelectedValue}K to $${maxPriceSelectedValue}K")
        updatePriceText(minRangeSelectedValue, maxPriceSelectedValue)

        /* if (AppConstants.REGION_HEADER.isEmpty()) {
             AppConstants.PRICE_RANGE_HEADER =
                 "$" + minRangeSelectedValue + "K-" + "$" + maxPriceSelectedValue + "K"

         } else {*/
        AppConstants.PRICE_RANGE_HEADER =
            " | $" + minRangeSelectedValue + "K-" + "$" + maxPriceSelectedValue + "K"
//        }
        AppConstants.toolheaderHashMapHomeLand[AppConstants.PRICERANGE] =
            AppConstants.PRICE_RANGE_HEADER
        setToolbarHeaderTitle()
        AppConstants.PACKAGES_COUNT = model.price

        minimumSelectedPriceForPriceRange = model.minPrice
        maximumSelectedPriceForPriceRange = model.maxPrice

        bottomBinding.tvPackages.text =
            AppUtils.getPackageTextBasedOnCount(model.price)

        isNextClickable = AppUtils.disableAndEnableView(
            model.price,
            bottomBinding.tvPackages,
            bottomBinding.next, bottomBinding.buttonNext
        )
        /*AppConstants.priceRangeMinimum=minRangeSelectedValue!!.toFloat()
        AppConstants.priceRangeMaxmimu=maxPriceSelectedValue!!.toFloat()*/
        binding.seekbar.setMinValue(AppConstants.totalminimumPrice.toFloat())
        binding.seekbar.setMaxValue(AppConstants.totalmaximumPrice.toFloat())
    }

    @SuppressLint("SetTextI18n")
    private fun updatePriceText(minRangeValue : Int?, maxRangeValue: Int?){
        println("minRangeValue:$minRangeValue maxRangeValue:: $maxRangeValue")
        var maxval=AppUtils.getFormattedNumber(AppConstants.totalmaximumPrice?: 0)
        var lastchar=maxval.last()
        var maxValround=removeTrailingZeros(maxval.dropLast(1))+lastchar

        var maxRangeValue=AppUtils.getFormattedNumber(maxRangeValue?: 0)
        var maxRangeValueLastchar=maxRangeValue.last()
        var maxRangeValueround=removeTrailingZeros(maxRangeValue.dropLast(1))+maxRangeValueLastchar

        var minRangeValue=AppUtils.getFormattedNumber(minRangeValue?: 0)
        var minRangeValueLastchar=minRangeValue.last()
        var minRangeValueround=removeTrailingZeros(minRangeValue.dropLast(1))+minRangeValueLastchar

        binding.tvMinprice.text="$" + AppUtils.getFormattedNumber(AppConstants.totalminimumPrice?: 0)
        binding.tvMaxprice.text= "$$maxValround"
        binding.tvSeek.text = "$$minRangeValueround to $$maxRangeValueround"
    }
    fun removeTrailingZeros(num: String): String {
        if(!num.contains('.')) // Return the original number if it doesn't contain decimal
            return num
        return num
            .dropLastWhile { it == '0' } // Remove trailing zero
            .dropLastWhile { it == '.' } // Remove decimal in case it's the last character in the resultant string
    }
    private fun numberAdjustment(value:Double):String{
        val formatter = DecimalFormat("#,###,##0.00");
        return formatter.format(value).replace(".00","");
    }

    fun loadFragment(fragment: Fragment) {
        clearFilterData()
        val transaction: FragmentTransaction = activity?.supportFragmentManager!!.beginTransaction()
        if (fragment.isAdded) {
            return
        }
        transaction.add(R.id.fl_content, fragment)
        transaction.addToBackStack("HomeLandPlaceFragment")
        transaction.commit()
    }


    private fun getMinAndMaxPrice(
        minimumPriceForPriceRange: Double?,
        maximumPriceForPriceRange: Double?
    ) {
        var stateId = preferences.getStateID()
        var region = AppConstants.SELECTED_REGION
        var jsonObject = JsonObject()
        var hnlQuizObject = JsonObject()
        hnlQuizObject.addProperty("Region", region)

        AppConstants.Bathrooms = arrayOf(1, 2, 3, 4, 5, 6)
        val bathrooms = JsonArray()
        for (bathRoom in AppConstants.Bathrooms!!) {
            bathrooms.add(bathRoom)
        }

        val storeyArray = JsonArray()
        storeyArray.add(1)
        storeyArray.add(2)
        val bedrooms = JsonArray()
        AppConstants.BedRooms?.let {
            for (bed in it) {
                bedrooms.add(bed)
            }
        }
        val carspaces = JsonArray()
        carspaces.add(1)
        carspaces.add(2)

        hnlQuizObject.add("Storey", storeyArray)
        hnlQuizObject.add("BedRooms", bedrooms)
        hnlQuizObject.add("CarSpaces", carspaces)
        hnlQuizObject.add("Bathrooms", bathrooms)
        hnlQuizObject.addProperty("StateId", stateId)
        AppConstants.previouslySelectedMinPrice?.let {
            hnlQuizObject.addProperty("SelectedMinValue", it)
        } ?: kotlin.run {
            hnlQuizObject.addProperty("SelectedMinValue", minimumPriceForPriceRange)
        }
        AppConstants.previouslySelectedMaxPrice?.let {
            hnlQuizObject.addProperty("SelectedMaxValue", it)
        } ?: kotlin.run {
            hnlQuizObject.addProperty("SelectedMaxValue", maximumPriceForPriceRange)
        }


        hnlQuizObject.addProperty("PageNo", 0)
        hnlQuizObject.addProperty("SortByPrice", 0)

        jsonObject.add("HnLQuizItems", hnlQuizObject)
        homeAndLandViewModel.fetchMinAndMaxPrice(jsonObject)

    }

    private fun hnlApiCall(minPriceValue: String, maxPriceValue: String) {
        var stateId = preferences.getStateID()
        var region = AppConstants.SELECTED_REGION
        var userId = preferences.getUserId()

        var jsonObject = JsonObject()
        var hnlQuizObject = JsonObject()
        val storeyArray = JsonArray()
        if(AppConstants.SELECTED_STOREY_PREVIOUS!=0){
            storeyArray.add(AppConstants.SELECTED_STOREY_PREVIOUS)
        }
        else {
            storeyArray.add(1)
            storeyArray.add(2)
        }


        AppConstants.Bathrooms = arrayOf(1, 2, 3, 4, 5, 6)
        val bathrooms = JsonArray()
        for (bathRoom in AppConstants.Bathrooms!!) {
            bathrooms.add(bathRoom)
        }
        val bedrooms = JsonArray()
        println("AppConstants.BEDROOM_COUNT:: "+AppConstants.SELECTED_BEDROOM_COUNT)
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

        hnlQuizObject.addProperty("Region", region)
        hnlQuizObject.add("Storey", storeyArray)
        hnlQuizObject.add("BedRooms", bedrooms)
        hnlQuizObject.add("CarSpaces", carSpaces)
        hnlQuizObject.add("Bathrooms", bathrooms)
        hnlQuizObject.addProperty("StateId", stateId)
        hnlQuizObject.addProperty("SelectedMinValue", minPriceValue)
        hnlQuizObject.addProperty("SelectedMaxValue", maxPriceValue)
        hnlQuizObject.addProperty("PageNo", 0)
        hnlQuizObject.addProperty("SortByPrice", 0)
        hnlQuizObject.addProperty("IncludePackages", 0)
        jsonObject.add("HnLQuizItems", hnlQuizObject)
        println("fetchHnLPackages Pricerange::$hnlQuizObject")
        homeAndLandViewModel.fetchHnLPackages(userId, jsonObject)
    }

    override fun onBackPressed(): Boolean {
        hnLbackHandlingViewModel.setUpdateBedroomsLiveData(updateStoreysApi)
        return false
    }

    private fun setToolbarHeaderTitle() {
        val toolHeaderStringBuilder = StringBuilder()
        for (key in AppConstants.toolheaderHashMapHomeLand.keys) {
            /*if (key == AppConstants.PRICERANGE) {
                var value = AppConstants.toolheaderHashMapHomeLand[key]
                toolHeaderStringBuilder.append(value)
                break
            } else {*/
            var value = AppConstants.toolheaderHashMapHomeLand[key]
            toolHeaderStringBuilder.append(value)
//            }
        }
        /*if (AppConstants.REGION_HEADER.isEmpty()) {
            AppConstants.REGION_HEADER="SKIP"
            toolBinding.tvTool.text = ("Take a quick survey to find your perfect design")
        } else {*/
        toolBinding.tvTool.visibility = View.VISIBLE
        toolBinding.tvTool.text = toolHeaderStringBuilder.toString()

//        }
        println("toolHeaderStringBuilder:: "+toolHeaderStringBuilder.toString())

    }
}