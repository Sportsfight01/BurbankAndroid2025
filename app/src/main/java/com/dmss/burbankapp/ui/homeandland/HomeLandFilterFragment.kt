package com.dmss.burbankapp.ui.homeandland

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
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
import com.dmss.burbankapp.data.model.PriceRangeListModel
import com.dmss.burbankapp.databinding.FragmentHomeLandFilterBinding
import com.dmss.burbankapp.ui.homeandlandregions.HomeAndLandViewModel
import com.dmss.burbankapp.ui.view.ISortingItem
import com.dmss.burbankapp.utils.AppConstants
import com.dmss.burbankapp.utils.AppUtils
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import common.AppController
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber
import java.text.DecimalFormat

class HomeLandFilterFragment(var filter: IUpdateFilterData) : DialogFragment(),
    ISortingItem {
    lateinit var textList: ArrayList<TextView>

    var packageFound = false
    var priceRangeList: ArrayList<PriceRangeListModel>? = null
    var updatePriceVal=""
    lateinit var homeAndLandViewModel: HomeAndLandViewModel
    private lateinit var hnlPackagesModel: HnLPackagesModel
    lateinit var mPreferences: CustomSharedPreferences
    private val disposable: CompositeDisposable = CompositeDisposable()
    private var mListener: ISortingItem? = null
    private var minValLeft=""
    private var maxValRight=""
    var isupdateRANGEValue:Boolean=false
    lateinit var binding: FragmentHomeLandFilterBinding

    var minimumSelectedPriceForPriceRange: Double? = null
    var maximumSelectedPriceForPriceRange: Double? = null


    var sortByPrice: Int = 0


    fun newInstance(title: String?, filter: IUpdateFilterData): HomeLandFilterFragment? {
        val frag = HomeLandFilterFragment(filter)
        val args = Bundle()
        args.putString("title", title)
        frag.arguments = args
        return frag
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeLandFilterBinding.inflate(inflater, container, false)
        if (dialog != null && dialog?.window != null) {
            dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
            dialog?.window?.setLayout(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            );
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViewModel()
        inItView()
        showDefaultViews()
    }

    private fun setUpViewModel() {
        homeAndLandViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(
                ApiHelperImpl(RetrofitBuilder.apiService),
                DatabaseHelperImpl(DatabaseBuilder.getInstance(AppController.getInstance()))
            )
        ).get(HomeAndLandViewModel::class.java)
        setupObserver()
    }

    private fun setupObserver() {
        homeAndLandViewModel.getHnLPackagesLiveData().observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    binding.progressFilter.visibility = View.GONE
                    var regionsData: HnLPackagesModel? = it.data
                    if (regionsData != null) {
                        if (regionsData.status) {
                            packageFound = true
                            priceRangeList = regionsData.priceRangeList
                            var minRangeValue: Int? = null
                            var maxRangeValue: Int? = null

                            regionsData.priceRangeModel?.let { priceRangeModel ->
                                priceRangeModel.MinPrice?.let { minPrice ->
                                    /*val reminderOfMinRangeValue = minPrice.toInt().rem(1000)
                                    minRangeValue = if (reminderOfMinRangeValue > 200) {
                                        (minPrice.toInt() / 1000).minus(1)
                                    } else {
                                        minPrice.toInt() / 1000
                                    }*/
                                    minRangeValue = minPrice.toInt() / 1000
                                    AppConstants.MIN_PRICE_SELECTED=""+minPrice.toInt()
                                    minimumSelectedPriceForPriceRange = minPrice
                                }
                                priceRangeModel.MaxPrice?.let { maxPrice ->
                                    /*val reminderOfMaxRangeValue = maxPrice.toInt().rem(1000)
                                    maxRangeValue = if (reminderOfMaxRangeValue > 200) {
                                        (maxPrice.toInt() / 1000).plus(1)
                                    } else {
                                        maxPrice.toInt() / 1000
                                    }*/
                                    AppConstants.MAX_PRICE_SELECTED=""+maxPrice.toInt()

                                    maxRangeValue = maxPrice.toInt() / 1000
                                    maximumSelectedPriceForPriceRange = maxPrice
                                }

                            }
                            updatePriceTextValue(minRangeValue, maxRangeValue)

                            /*   AppConstants.MIN_PRICE = minRangeValue.toString()
                               AppConstants.MAX_PRICE = maxPriceValue.toString()*/
                            val intArray = regionsData.countDistribution
                            if (intArray != null) {
                                if (intArray.size > 6)
                                    setBarHeightsDynamic(intArray)
                            }
                            if (intArray != null) {
                                for (value in intArray) {
                                }
                            }
                            hnlPackagesModel = regionsData

                        } else {
                            val numbers: Array<Int> = arrayOf<Int>(0, 0, 0, 0, 0, 0, 0)
                            if (numbers.size > 6)
                                setBarHeightsDynamic(numbers)
                            for (value in numbers) {
                                println("Integer Array Value ------ $value")
                            }
                            packageFound = false

                            regionsData.message?.let { it1 ->
                                AppUtils.showCustomCenterToast(
                                    requireContext(),
                                    it1
                                )
                            }
                        }
                    }
                }
                Status.LOADING -> {
                    binding.progressFilter.visibility = View.VISIBLE
                }
                Status.ERROR -> {
                    binding.progressFilter.visibility = View.GONE
                }
            }
        })
    }

    private fun showDefaultViews() {
        showStoreys(AppConstants.FILTER_SELECTED_STOREY)

        when (AppConstants.FILTER_BEDROOM_COUNT) {
            2 -> {
                showBedrooms(5)
            }
            3 -> {
                showBedrooms(3)
            }
            4 -> {
                showBedrooms(4)
            }
            5 -> {
                showBedrooms(5)
            }
            6 -> {
                showBedrooms(6)
            }
            else -> {
                showBedrooms(0)
            }
        }

        if (AppConstants.FILTER_Bathrooms?.isNotEmpty()!!) {
            when (AppConstants.FILTER_Bathrooms?.size) {
                4 -> {
                    showBathrooms(4)
                }
                1 -> {
                    showBathrooms(1)
                }
                else -> {
                    showBathrooms(0)
                }
            }

        } else {
            showBathrooms(0)
        }
        if (AppConstants.FILTER_CarSpaces?.isNotEmpty() == true) {
            if (AppConstants.FILTER_CarSpaces!!.size == 2) {
                showCarSpaces(0, false)
            } else {
                for (storey in AppConstants.FILTER_CarSpaces!!) {
                    showCarSpaces(storey, false)
                }
            }

        } else {
            showCarSpaces(0, false)
        }
    }

    private fun showSnakbar(view: View, message: String) {
        val snack = Snackbar.make(view, message, Snackbar.LENGTH_LONG)
        snack.show()
    }

    private fun storeyHideView(view: TextView, storey: Int) {
        binding.tvStoreyOne.background = null
        binding.tvStoreyTwo.background = null
        binding.tvStoreyAll.background = null
        binding.tvStoreyOne.setTextColor(
            ContextCompat.getColor(
                AppController.getInstance(),
                R.color.box_grey
            )
        )
        binding.tvStoreyTwo.setTextColor(
            ContextCompat.getColor(
                AppController.getInstance(),
                R.color.box_grey
            )
        )
        binding.tvStoreyAll.setTextColor(
            ContextCompat.getColor(
                AppController.getInstance(),
                R.color.box_grey
            )
        )
        view.setTextColor(
            ContextCompat.getColor(
                AppController.getInstance(),
                R.color.white
            )
        )

        when (storey) {
            1 -> {
                view.background =
                    ContextCompat.getDrawable(
                        AppController.getInstance(),
                        R.drawable.left_filled_drwable
                    )
            }
            2 -> {
                view.background =
                    ContextCompat.getDrawable(
                        AppController.getInstance(),
                        R.drawable.center_filled_drawable
                    )
            }
            else -> {
                view.background =
                    ContextCompat.getDrawable(
                        AppController.getInstance(),
                        R.drawable.right_filled_drwable
                    )
            }
        }


    }

    private fun bathroomHideView(view: TextView, bathroom: Int) {
        binding.tvBathroomsTwo.background = null
        binding.tvBathroomsThree.background = null
        binding.tvBathroomsAll.background = null
        binding.tvBathroomsTwo.setTextColor(
            ContextCompat.getColor(
                AppController.getInstance(),
                R.color.box_grey
            )
        )
        binding.tvBathroomsThree.setTextColor(
            ContextCompat.getColor(
                AppController.getInstance(),
                R.color.box_grey
            )
        )
        binding.tvBathroomsAll.setTextColor(
            ContextCompat.getColor(
                AppController.getInstance(),
                R.color.box_grey
            )
        )
        view.setTextColor(
            ContextCompat.getColor(
                AppController.getInstance(),
                R.color.white
            )
        )

        when (bathroom) {
            1 -> {
                view.background =
                    ContextCompat.getDrawable(
                        AppController.getInstance(),
                        R.drawable.left_filled_drwable
                    )
            }
            4 -> {
                view.background =
                    ContextCompat.getDrawable(
                        AppController.getInstance(),
                        R.drawable.center_filled_drawable
                    )
            }
            else -> {
                view.background =
                    ContextCompat.getDrawable(
                        AppController.getInstance(),
                        R.drawable.right_filled_drwable
                    )
            }
        }


    }

    private fun carSpaceHideView(view: TextView, carSpace: Int) {
        binding.tvCarSpacesOne.background = null
        binding.tvCarSpacesTwo.background = null
        binding.tvCarSpacesAll.background = null
        binding.tvCarSpacesOne.setTextColor(
            ContextCompat.getColor(
                AppController.getInstance(),
                R.color.box_grey
            )
        )
        binding.tvCarSpacesTwo.setTextColor(
            ContextCompat.getColor(
                AppController.getInstance(),
                R.color.box_grey
            )
        )
        binding.tvCarSpacesAll.setTextColor(
            ContextCompat.getColor(
                AppController.getInstance(),
                R.color.box_grey
            )
        )
        view.setTextColor(
            ContextCompat.getColor(
                AppController.getInstance(),
                R.color.white
            )
        )

        when (carSpace) {
            1 -> {
                view.background =
                    ContextCompat.getDrawable(
                        AppController.getInstance(),
                        R.drawable.left_filled_drwable
                    )
            }
            2 -> {
                view.background =
                    ContextCompat.getDrawable(
                        AppController.getInstance(),
                        R.drawable.center_filled_drawable
                    )
            }
            else -> {
                view.background =
                    ContextCompat.getDrawable(
                        AppController.getInstance(),
                        R.drawable.right_filled_drwable
                    )
            }
        }


    }

    private fun showBedrooms(bedroom: Int) {
        when (bedroom) {
            3 -> {
                bedRoomsHideView(binding.tvBedroomThree, bedroom)
            }
            4 -> {
                bedRoomsHideView(binding.tvBedroomFour, bedroom)
            }
            5 -> {
                bedRoomsHideView(binding.tvBedroomFive, bedroom)
            }
            6 -> {
                bedRoomsHideView(binding.tvBedroomFive, bedroom)
            }
            else -> {
                bedRoomsHideView(binding.tvBedroomAll, bedroom)
            }
        }

    }

    private fun bedRoomsHideView(view: TextView, bedroom: Int) {
        binding.tvBedroomThree.background = null
        binding.tvBedroomFour.background = null
        binding.tvBedroomFive.background = null
        binding.tvBedroomAll.background = null


        binding.tvBedroomThree.setTextColor(
            ContextCompat.getColor(
                AppController.getInstance(),
                R.color.box_grey
            )
        )
        binding.tvBedroomFour.setTextColor(
            ContextCompat.getColor(
                AppController.getInstance(),
                R.color.box_grey
            )
        )
        binding.tvBedroomFive.setTextColor(
            ContextCompat.getColor(
                AppController.getInstance(),
                R.color.box_grey
            )
        )
        binding.tvBedroomAll.setTextColor(
            ContextCompat.getColor(
                AppController.getInstance(),
                R.color.box_grey
            )
        )
        view.setTextColor(
            ContextCompat.getColor(
                AppController.getInstance(),
                R.color.white
            )
        )
        when (bedroom) {
            3 -> {
                view.background =
                    ContextCompat.getDrawable(
                        AppController.getInstance(),
                        R.drawable.left_filled_drwable
                    )
            }
            4 -> {
                view.background =
                    ContextCompat.getDrawable(
                        AppController.getInstance(),
                        R.drawable.center_filled_drawable
                    )
            }
            5 -> {
                view.background =
                    ContextCompat.getDrawable(
                        AppController.getInstance(),
                        R.drawable.center_filled_drawable
                    )
            }
            else -> {
                view.background =
                    ContextCompat.getDrawable(
                        AppController.getInstance(),
                        R.drawable.right_filled_drwable
                    )
            }
        }

    }

    private fun showStoreys(storey: Int) {
        when (storey) {
            1 -> {
                storeyHideView(binding.tvStoreyOne, storey)
            }
            2 -> {
                storeyHideView(binding.tvStoreyTwo, storey)
            }
            else -> {
                storeyHideView(binding.tvStoreyAll, storey)
            }
        }

    }

    private fun showCarSpaces(carSpace: Int, fromSelection: Boolean) {
        when (carSpace) {
            1 -> {
                carSpaceHideView(binding.tvCarSpacesOne, carSpace)
            }
            2 -> {
                if (fromSelection) {
                    carSpaceHideView(binding.tvCarSpacesTwo, carSpace)
                } else {
                    carSpaceHideView(binding.tvCarSpacesTwo, carSpace)
                }
            }
            else -> {
                carSpaceHideView(binding.tvCarSpacesAll, carSpace)
            }
        }

    }

    private fun showBathrooms(bathroom: Int) {
        when (bathroom) {
            1 -> {
                bathroomHideView(binding.tvBathroomsTwo, bathroom)
            }
            4 -> {
                bathroomHideView(binding.tvBathroomsThree, bathroom)
            }
            else -> {
                bathroomHideView(binding.tvBathroomsAll, bathroom)
            }
        }

    }


    private fun inItView() {
        if (AppConstants.FILTER_toolHeaderHashMapHomeLand.size == 0) {
            AppConstants.FILTER_toolHeaderHashMapHomeLand.putAll(AppConstants.toolheaderHashMapHomeLand)
        }

        if (AppConstants.FILTER_SELECTED_STOREY == -1) {
            AppConstants.FILTER_SELECTED_STOREY = AppConstants.SELECTED_STOREY
        }
        if (AppConstants.FILTER_BEDROOM_COUNT == 0) {
            AppConstants.FILTER_BEDROOM_COUNT = AppConstants.BEDROOM_COUNT
        }

        if (AppConstants.FILTER_Bathrooms == null) {
            AppConstants.FILTER_Bathrooms = AppConstants.Bathrooms
        }
        if (AppConstants.FILTER_BedRooms == null) {
            AppConstants.FILTER_BedRooms = AppConstants.BedRooms
        }
        if (AppConstants.FILTER_CarSpaces == null) {
            AppConstants.FILTER_CarSpaces = AppConstants.CarSpaces
        }


        mPreferences = CustomSharedPreferences(AppController.getInstance())

        if (mPreferences.getPriceRange() == "High") {
            binding.tvrange.text = "Price (High - Low)"
        } else {
            binding.tvrange.text = "Price (Low - High)"
        }

        mListener = this
        textList = ArrayList()
        textList.add(binding.first)
        textList.add(binding.second)
        textList.add(binding.third)
        textList.add(binding.fourth)
        textList.add(binding.fifth)
        textList.add(binding.sixth)
        textList.add(binding.seventh)


        binding.ivClose.setOnClickListener {
//            filter.updateFilterData(sortByPrice, false)
            dialog!!.dismiss()

        }
        binding.llSearch.setOnClickListener {
            if (packageFound) {
                /*if (sortByPrice == 0) {
                    binding.tvrange.text = "Price (Low - High)"
                } else {
                    binding.tvrange.text = "Price (High - Low)"

                }*/
                var sortValue=0

                if(binding.tvrange.text =="Price (High - Low)"){
                    sortValue=1
                }

                if(updatePriceVal!=null && updatePriceVal!="") {
                    AppConstants.FILTER_toolHeaderHashMapHomeLand[
                        AppConstants.PRICERANGE]= "|$updatePriceVal"
                }
                filter.updateFilterData(sortValue, packageFound)
                dialog!!.dismiss()

            } else {
                AppUtils.showCustomCenterToast(
                    binding.llSearch.context,
                    "No Packages found, Please refine your filter"
                )
            }
        }

        binding.rlSortBy.setOnClickListener {
            showBottomSheetFilter()
        }

        if (AppConstants.FILTER_MIN_PRICE.trim().isEmpty() && AppConstants.FILTER_MAX_PRICE.trim()
                .isEmpty()
        ) {
            Timber.e("Filter is Empty")
            val minValue = AppConstants.MIN_PRICE.toDouble().toInt()
            var maxValue = AppConstants.MAX_PRICE.toDouble().toInt()
            val reminderOfMinRangeValue = minValue.rem(1000)
            /* val minRangeValue = if (reminderOfMinRangeValue > 200) {
                 (minValue / 1000).minus(1)
             } else {
                 minValue / 1000
             }*/
            val minRangeValue =  minValue / 1000

            /* val reminderOfMaxRangeValue = maxValue.toInt().rem(1000)
             val maxRangeValue = if (reminderOfMaxRangeValue > 200) {
                 (maxValue / 1000).plus(1)
             } else {
                 maxValue / 1000
             }*/
            val  maxRangeValue =  maxValue / 1000
            AppConstants.FILTER_MIN_PRICE = AppConstants.MIN_PRICE
            AppConstants.FILTER_MAX_PRICE = AppConstants.MAX_PRICE
            binding.seekbar.setMinValue(minRangeValue.toFloat())
            binding.seekbar.setMaxValue(maxRangeValue.toFloat())
            binding.seekbar.setMinStartValue(minRangeValue.toFloat())
            binding.seekbar.setMaxStartValue(maxRangeValue.toFloat()).apply()
            hnlApiCall(AppConstants.MIN_PRICE, AppConstants.MAX_PRICE, 0)

        } else {
            Timber.e("Filter is Not Empty")
            if (AppConstants.FILTER_MIN_PRICE.isNotEmpty() && AppConstants.FILTER_MAX_PRICE.isNotEmpty()) {

                val minRangeValue = AppConstants.FILTER_MIN_PRICE.toDouble().toInt() / 1000
                var maxPriceValue = AppConstants.FILTER_MAX_PRICE.toDouble().toInt() / 1000

                val minValue = AppConstants.MIN_PRICE.toDouble().toInt() / 1000
                var maxValue = AppConstants.MAX_PRICE.toDouble().toInt() / 1000


                val reminderOfMaxRangeValue = AppConstants.MAX_PRICE.toDouble().toInt() % 1000

                /* if (reminderOfMaxRangeValue > 200) {
                     maxValue += 1
                 }*/

                Timber.e("SortFilter MinPrice $minRangeValue")
                Timber.e("SortFilter MaxPrice $maxPriceValue")

                binding.seekbar.setMinValue(minValue.toFloat())
                binding.seekbar.setMaxValue(maxValue.toFloat())


                binding.seekbar.setMinStartValue(minRangeValue.toFloat())
                binding.seekbar.setMaxStartValue(maxPriceValue.toFloat()).apply()
                hnlApiCall(AppConstants.MIN_PRICE_SELECTED, AppConstants.MAX_PRICE_SELECTED, 0)
            }
        }

        binding.seekbar.setOnRangeSeekbarChangeListener {
                minValue, maxValue ->
            updatePriceTextValue(minValue.toInt(),maxValue.toInt())
        }
        //Click Storeys

        binding.tvStoreyOne.setOnClickListener {
            showStoreys(1)
            AppConstants.FILTER_toolHeaderHashMapHomeLand[AppConstants.STOREYS] = "Single"
            AppConstants.FILTER_SELECTED_STOREY = 1
            AppConstants.SELECTED_STOREY = 1
            hnlApiCall(AppConstants.FILTER_MIN_PRICE, AppConstants.FILTER_MAX_PRICE, 0)
        }
        binding.tvStoreyTwo.setOnClickListener {
            showStoreys(2)
            AppConstants.FILTER_toolHeaderHashMapHomeLand[AppConstants.STOREYS] = "Double"
            AppConstants.FILTER_SELECTED_STOREY = 2
            AppConstants.SELECTED_STOREY = 2
            hnlApiCall(AppConstants.FILTER_MIN_PRICE, AppConstants.FILTER_MAX_PRICE, 0)
        }
        binding.tvStoreyAll.setOnClickListener {
            showStoreys(0)
            AppConstants.FILTER_toolHeaderHashMapHomeLand[AppConstants.STOREYS] = ""

            AppConstants.FILTER_SELECTED_STOREY = -1
            AppConstants.SELECTED_STOREY = -1
            hnlApiCall(AppConstants.FILTER_MIN_PRICE, AppConstants.FILTER_MAX_PRICE, 0)
        }

        //Clicks BedRooms
        binding.tvBedroomThree.setOnClickListener {
            showBedrooms(3)
            AppConstants.FILTER_BEDROOM_COUNT = 3
            AppConstants.FILTER_toolHeaderHashMapHomeLand[AppConstants.BEDROOMS] = "3 Bed"
            AppConstants.FILTER_BedRooms = arrayOf(3)
            AppConstants.BedRooms = arrayOf(3)
            hnlApiCall(AppConstants.FILTER_MIN_PRICE, AppConstants.FILTER_MAX_PRICE, 0)
        }
        binding.tvBedroomFour.setOnClickListener {
            AppConstants.FILTER_BedRooms = arrayOf(4)
            AppConstants.FILTER_toolHeaderHashMapHomeLand[AppConstants.BEDROOMS] = "4 Bed"
            AppConstants.FILTER_BEDROOM_COUNT = 4
            showBedrooms(4)
            AppConstants.BedRooms = arrayOf(4)
            hnlApiCall(AppConstants.FILTER_MIN_PRICE, AppConstants.FILTER_MAX_PRICE, 0)
        }
        binding.tvBedroomFive.setOnClickListener {
            AppConstants.FILTER_BedRooms = arrayOf(5, 6)
            AppConstants.FILTER_toolHeaderHashMapHomeLand[AppConstants.BEDROOMS] = "5+ Bed"
            AppConstants.FILTER_BEDROOM_COUNT = 5
            showBedrooms(5)
            AppConstants.BedRooms = arrayOf(5, 6)
            hnlApiCall(AppConstants.FILTER_MIN_PRICE, AppConstants.FILTER_MAX_PRICE, 0)
        }
        binding.tvBedroomAll.setOnClickListener {
            showBedrooms(0)
            AppConstants.FILTER_BedRooms = arrayOf(3, 4, 5, 6)
            AppConstants.FILTER_BEDROOM_COUNT = 0
            AppConstants.BedRooms = arrayOf(3, 4, 5, 6)
            AppConstants.FILTER_toolHeaderHashMapHomeLand[AppConstants.BEDROOMS] = ""
            hnlApiCall(AppConstants.FILTER_MIN_PRICE, AppConstants.FILTER_MAX_PRICE, 0)
        }

        //Click Car Spaces
        binding.tvCarSpacesOne.setOnClickListener {
            showCarSpaces(1, true)
            AppConstants.FILTER_CarSpaces = arrayOf(1)
            AppConstants.CarSpaces = arrayOf(1)
            hnlApiCall(AppConstants.FILTER_MIN_PRICE, AppConstants.FILTER_MAX_PRICE, 0)
        }
        binding.tvCarSpacesTwo.setOnClickListener {
            showCarSpaces(2, true)
            AppConstants.FILTER_CarSpaces = arrayOf(2)
            AppConstants.CarSpaces = arrayOf(2)
            hnlApiCall(AppConstants.FILTER_MIN_PRICE, AppConstants.FILTER_MAX_PRICE, 0)
        }
        binding.tvCarSpacesAll.setOnClickListener {
            showCarSpaces(0, true)
            AppConstants.FILTER_CarSpaces = arrayOf(1, 2)
            AppConstants.CarSpaces = arrayOf(1,2)
            hnlApiCall(AppConstants.FILTER_MIN_PRICE, AppConstants.FILTER_MAX_PRICE, 0)
        }
        //Click Bathrooms

        binding.tvBathroomsTwo.setOnClickListener {
            showBathrooms(1)
            AppConstants.FILTER_Bathrooms = arrayOf(2)
            AppConstants.Bathrooms = arrayOf(2)
            hnlApiCall(AppConstants.FILTER_MIN_PRICE, AppConstants.FILTER_MAX_PRICE, 0)
        }

        binding.tvBathroomsThree.setOnClickListener {
            showBathrooms(4)
            AppConstants.FILTER_Bathrooms = arrayOf(3, 4, 5, 6)
            AppConstants.Bathrooms = arrayOf(3, 4, 5, 6)
            hnlApiCall(AppConstants.FILTER_MIN_PRICE, AppConstants.FILTER_MAX_PRICE, 0)
        }


        binding.tvBathroomsAll.setOnClickListener {
            showBathrooms(0)
            AppConstants.FILTER_Bathrooms = arrayOf(2, 3, 4, 5, 6)
            AppConstants.Bathrooms = arrayOf(2, 3, 4, 5, 6)
            hnlApiCall(AppConstants.FILTER_MIN_PRICE, AppConstants.FILTER_MAX_PRICE, 0)
        }

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

        binding.seekbar.setOnRangeSeekbarFinalValueListener { minValue, maxValue ->
            Timber.e("Price Range Values  $minValue --- $maxValue")
            val minPriceValue = (minValue.toInt() * 1000).toString()
            val maxPriceValue = (maxValue.toInt() * 1000).toString()
            AppConstants.FILTER_MAX_PRICE=maxPriceValue
            isupdateRANGEValue=true
            hnlApiCall(minPriceValue, maxPriceValue, 0)
        }
    }

    private fun showMinAndMaxValue(model: PriceRangeListModel) {
        var minRangeValue: Int? = null
        var minRangeSelectedValue: Int? = null
        var maxPriceSelectedValue: Int? = null
        var maxPriceValue: Int? = null
        AppConstants.MIN_PRICE.let {
            minRangeValue = it.toDouble().toInt() / 1000
        }
        AppConstants.MAX_PRICE.let {
            maxPriceValue = it.toDouble().toInt() / 1000
        }
        isupdateRANGEValue=true
        val reminderOfMaxRangeValue = maxPriceValue?.rem(1000)
        /*if (reminderOfMaxRangeValue != null) {
            if (reminderOfMaxRangeValue > 200) {
                maxPriceValue = maxPriceValue?.plus(1)
            }
        }*/

        minRangeValue?.let { it1 -> binding.seekbar.setMinValue(it1.toFloat()) }
        maxPriceValue?.let { it1 -> binding.seekbar.setMaxValue(it1.toFloat()) }

        Timber.e("Show Min And Max Price Values ${AppConstants.MIN_PRICE} -- ${AppConstants.MAX_PRICE}")
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
            binding.seekbar.setMinStartValue(it1.toFloat()) }
        maxPriceSelectedValue?.let { it1 ->
            binding.seekbar.setMaxStartValue(it1.toFloat()).apply()
        }
        updatePriceTextValue(minRangeSelectedValue, maxPriceSelectedValue)
        minimumSelectedPriceForPriceRange = model.minPrice
        maximumSelectedPriceForPriceRange = model.maxPrice

    }

    private fun updatePriceTextValue(minValue: Int?, maxValue: Int?){
        val maxRangeVal=AppUtils.getFormattedNumber(maxValue?: 0);
        val measureVal:String=maxRangeVal.last().toString();
        val removedM=maxRangeVal?.replaceFirst(".$".toRegex(), "")
        val format = DecimalFormat("0.#")
        val df = DecimalFormat("#")
        val convertmaxRangeValue=format.format(removedM.toDouble())
        var kvalue = convertmaxRangeValue.toDouble()
        if (measureVal.contains("M")) {
            kvalue *= 1000
        }

        val maxRangeValue = convertmaxRangeValue.plus(measureVal)
        if(isupdateRANGEValue) {

            updatePriceVal =
                "$${AppUtils.getFormattedNumber(minValue ?: 0)}-$${df.format(kvalue)}" + "K"
        }
        if(minValLeft==""){
            minValLeft="$${AppUtils.getFormattedNumber(minValue?: 0)}"
            maxValRight="$${maxRangeValue}"
        }
        binding.tvSeek.text =  "$${AppUtils.getFormattedNumber(minValue?: 0)} to $${maxRangeValue}"
        binding.tvMinprice.text= minValLeft
        binding.tvMaxprice.text= maxValRight

    }

    private fun setBarHeightsDynamic(intArray: Array<Int>) {
        val maxNumber = intArray.maxOrNull()
        textList.forEachIndexed { index, element ->
            val minValue = intArray[index]
            if (minValue == 0 || minValue == 1) {
                element.height = 50

            } else {
                val height: Double = ((minValue.toDouble() / maxNumber!!) * 100) + 50
                element.height = height.toInt()
            }


            element.text = intArray[index].toString()
        }
    }


    private fun showBottomSheetFilter() {
        val fm: FragmentManager = activity!!.supportFragmentManager
        var fragment = HomeLandFilterBottomSheetFragment(this).newInstance(this);
        fragment?.show(fm, "fragment_filter_name")

    }

    private fun hnlApiCall(minPrice: String, maxPrice: String, sortByPrice: Int) {

        val stateId = mPreferences.getStateID()
        var userId = mPreferences.getUserId()

        val region = AppConstants.SELECTED_REGION

        val jsonObject = JsonObject()
        val hnlQuizObject = JsonObject()
        hnlQuizObject.addProperty("Region", region)


        val storeyArray = JsonArray()
        if (AppConstants.FILTER_SELECTED_STOREY != 3 && AppConstants.FILTER_SELECTED_STOREY != -1) {
            storeyArray.add(AppConstants.FILTER_SELECTED_STOREY)
        }

        if (storeyArray.size() == 0 || AppConstants.FILTER_SELECTED_STOREY == 0) {
            storeyArray.add("1")
            storeyArray.add("2")
        }
        val bedrooms = JsonArray()
        AppConstants.FILTER_BedRooms?.let {
            for (bed in it) {
                bedrooms.add(bed)
            }
        }

        val carspaces = JsonArray()
        AppConstants.FILTER_CarSpaces?.let { carSpalce ->
            carSpalce.forEach {
                carspaces.add(it)
            }
        }

        val bathrooms = JsonArray()
        AppConstants.FILTER_Bathrooms?.let { bathroom ->
            bathroom.forEach {
                bathrooms.add(it)
            }
        }

        Timber.e("Carspaces ---->:${carspaces.size()}")

        hnlQuizObject.add("Storey", storeyArray)
        hnlQuizObject.add("BedRooms", bedrooms)
        hnlQuizObject.add("CarSpaces", carspaces)
        hnlQuizObject.add("Bathrooms", bathrooms)
        hnlQuizObject.addProperty("StateId", stateId)
        hnlQuizObject.addProperty("SelectedMinValue", minPrice)
        hnlQuizObject.addProperty("SelectedMaxValue", maxPrice)
        hnlQuizObject.addProperty("IncludePackages", 0)
        hnlQuizObject.addProperty("PageNo", 0)
        hnlQuizObject.addProperty("SortByPrice", sortByPrice)

        jsonObject.add("HnLQuizItems", hnlQuizObject)
        if(updatePriceVal!=null && updatePriceVal!="") {
            AppConstants.FILTER_toolHeaderHashMapHomeLand[
                AppConstants.PRICERANGE]= "|$updatePriceVal"

        }
        homeAndLandViewModel.fetchHnLPackages(userId, jsonObject)
    }

    interface IUpdateFilterData {

        fun updateFilterData(sortByPrice: Int, packageFound: Boolean)

    }

    override fun sortItem(item: String?) {
        if (item != null) {
            sortByPrice = item.toInt()
            if (sortByPrice == 0) {
                binding.tvrange.text = "Price (Low - High)"
            } else {
                binding.tvrange.text = "Price (High - Low)"

            }

            if (AppConstants.FILTER_MIN_PRICE.isNotEmpty() && AppConstants.FILTER_MAX_PRICE.isNotEmpty()) {
                hnlApiCall(
                    AppConstants.FILTER_MIN_PRICE,
                    AppConstants.FILTER_MAX_PRICE,
                    item.toInt()
                )
            } else {
                hnlApiCall(AppConstants.MIN_PRICE, AppConstants.MAX_PRICE, item.toInt())
            }

        }
    }


}