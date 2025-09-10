package com.dmss.burbankapp.ui.mydisplay.nearby

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.dmss.burbankapp.R
import com.dmss.burbankapp.data.api.ApiHelperImpl
import com.dmss.burbankapp.data.api.RetrofitBuilder
import com.dmss.burbankapp.data.apiUtils.Status
import com.dmss.burbankapp.data.apiUtils.ViewModelFactory
import com.dmss.burbankapp.data.local.CustomSharedPreferences
import com.dmss.burbankapp.data.local.entity.DatabaseBuilder
import com.dmss.burbankapp.data.local.entity.DatabaseHelperImpl
import com.dmss.burbankapp.data.model.DisplayDetailStaticModel
import com.dmss.burbankapp.data.model.HouseDetailByEstateObject
import com.dmss.burbankapp.data.model.HouseDetailsByHouseType
import com.dmss.burbankapp.data.model.HouseDetailsResponseModel
import com.dmss.burbankapp.databinding.FragmentNearbyBottomSheetBinding
import com.dmss.burbankapp.ui.designs.NewHomeQuizViewModel
import com.dmss.burbankapp.ui.main.MainActivity
import com.dmss.burbankapp.ui.mydisplay.DisplayDetailFragment
import com.dmss.burbankapp.ui.mydisplay.DisplayHomesViewModel
import com.dmss.burbankapp.ui.view.CustomProgressDialog
import com.dmss.burbankapp.utils.AppConstants
import com.dmss.burbankapp.utils.AppUtils
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.JsonObject
import common.AppController
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*


class NearbyBottomSheetFragment(
    var direction: IDirection,
    val bottomView: (Boolean) -> Unit,
    var showTopbar: Boolean
) :
    BottomSheetDialogFragment(),
    NearbyAdapter.IOnItemClickListener {

    lateinit var binding: FragmentNearbyBottomSheetBinding
    lateinit var displayHomeViewModel: DisplayHomesViewModel
    private var customProgressDialog: CustomProgressDialog? = null
    lateinit var myPreference: CustomSharedPreferences
    lateinit var totalResponse: HouseDetailsResponseModel
    lateinit var adapter: NearbyAdapter
    var houseName = ""
    var houseSize = ""
    private var stateId: Int = -1
    private var userID: Int = -1
    var houseDetailsList: ArrayList<HouseDetailsByHouseType> = ArrayList()
    var pathList: ArrayList<LatLng> = ArrayList()
    lateinit var myHomeQuizViewModel: NewHomeQuizViewModel
    var istradingnotExpand = true

    var estateName: String? = null
    var lotStreet: String? = null
    var lotSuburb: String? = null
    fun newInstance(
        estateName: String?,
        lotStreet: String?,
        lotSuburb: String?
    ): NearbyBottomSheetFragment? {
        val frag = NearbyBottomSheetFragment(direction, bottomView, showTopbar)
        val args = Bundle()
        this.estateName = estateName
        args.putString("estateName", estateName)
        args.putString("lotStreet", lotStreet)
        args.putString("lotSuburb", lotSuburb)
        frag.arguments = args
        return frag
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentNearbyBottomSheetBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initView()
        initViewModel()
    }

    private fun initViewModel() {
        Timber.e("DisplayHomes:---->")
        displayHomeViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(
                ApiHelperImpl(RetrofitBuilder.apiService),
                DatabaseHelperImpl(DatabaseBuilder.getInstance(requireContext()))
            )
        ).get(DisplayHomesViewModel::class.java)
        myHomeQuizViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(
                ApiHelperImpl(RetrofitBuilder.apiService),
                DatabaseHelperImpl(DatabaseBuilder.getInstance(AppController.getInstance()))
            )
        ).get(NewHomeQuizViewModel::class.java)

        subscribers()


        fetchNearByPlacesDetails()
    }

    private fun fetchNearByPlacesDetails() {

        estateName?.let {
            var jsonObject = HouseDetailByEstateObject(
                myPreference.getStateID(), it, myPreference.getUserId()
            )
            displayHomeViewModel.fetchHouseDetailsByEstate(jsonObject)
        }

    }

    private fun subscribers() {
        displayHomeViewModel.getHouseDetailsEstateData().observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    //customProgressDialog?.dismissProgress()

                    if (it.data != null) {
                        if (it.data.status) {

                            totalResponse = it.data

                            var timeString = ""

                            if (totalResponse.houseDetailsList.isNotEmpty()
                                && totalResponse.houseDetailsList.size > 0
                            ) {
                                val cal: Calendar = Calendar.getInstance()
                                val dayOfMonth: Int = cal.get(Calendar.DAY_OF_MONTH)
                                val dayOfMonthStr = dayOfMonth.toString()

                                val date = Date()
                                val monthDateFormat = "MM"
                                val monthDateFormat1 = "dd"

                                val mdf = SimpleDateFormat(monthDateFormat)
                                val mdf1 = SimpleDateFormat(monthDateFormat1)

                                val month: String = mdf.format(date)
                                val day: String = mdf1.format(date)
                                var todayDate= "$day/$month"

                                if (totalResponse.houseDetailsList[0].openTimes.isNotEmpty()) {
                                    var array =
                                        totalResponse.houseDetailsList[0].openTimes.split(",")
                                            .toTypedArray()
                                    var timeStringarray =
                                        array[0].split(" ").toList()
                                    if(timeStringarray[1].contains(todayDate)) {
                                        Collections.replaceAll(
                                            timeStringarray,
                                            timeStringarray[0],
                                            "Today"
                                        )
                                        array[0] = timeStringarray.joinToString().replace(",", "")
                                    }
                                    for (str in array) {

                                        timeString += ("$str \n")

                                    }
                                    if (timeString.length > 1) {
                                        timeString =
                                            AppUtils.removeLastIndexOfString(timeString.trim())
                                    }

                                    binding.tvTrading.text = timeString
                                    // timeString=  timeString.replace(/,\s*$/, "",i):String
                                }

                                for (model in totalResponse.houseDetailsList) {
                                    houseName += ("${model.HouseName} ${model.HouseSize}, ")
                                    houseSize = "${model.HouseSize}"

                                }
                                houseDetailsList.clear()
                                houseDetailsList = totalResponse.houseDetailsList

                                adapter.submitList(totalResponse.houseDetailsList)
                                bottomView(true)
                            }

                            /*var totalResponse: NearByDisplaysResponseModel = it.data


                            if (totalResponse.regionsList.isNotEmpty() && totalResponse.regionsList.size > 0) {
                                adapter.submitList(totalResponse.regionsList)

                            }*/
                        }
                    }
                }
                Status.LOADING -> {
                    bottomView(false)
                    // customProgressDialog?.showProgress()
                }
                Status.ERROR -> {
                    bottomView(false)
                    // customProgressDialog?.dismissProgress()
                }
            }

        })

        myHomeQuizViewModel.getFavorite().observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    customProgressDialog?.dismissProgress()
                    (activity as MainActivity).loadUserFavoritesDisplays()

                    if (it.data != null) {
                        if (it.data.status) {
                            val toast = Toast.makeText(
                                requireContext(),
                                it.data.message,
                                Toast.LENGTH_SHORT
                            )
                            toast.setGravity(Gravity.CENTER, 0, 0)
                            toast.show()
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
    }

    private fun initView() {

        binding.llBack.setOnClickListener {
            dismiss()
        }
        customProgressDialog = CustomProgressDialog(requireActivity())
        myPreference = CustomSharedPreferences(requireContext())
        stateId = myPreference.getStateID()
        userID = myPreference.getUserId()

        binding.ivBack.setOnClickListener {
            dismiss()
            requireActivity().onBackPressed()
        }

        binding.llTrading.setOnClickListener {
            if(istradingnotExpand) {
                istradingnotExpand=false
                binding.tvTrading.isSingleLine = false
                binding.downArrow.rotation=90f
                binding.downArrow.visibility=View.VISIBLE

            }else{
                istradingnotExpand=true
                binding.tvTrading.isSingleLine = true
                binding.downArrow.rotation=270f
                binding.downArrow.visibility=View.VISIBLE
            }
        }
        AppConstants.BACKSTACK_COUNT = 0
        estateName = arguments?.get("estateName") as String?
        lotStreet = arguments?.get("lotStreet") as String?
        lotSuburb = arguments?.get("lotSuburb") as String?


        if (showTopbar) {
            binding.tvEstateName.visibility = View.GONE
            binding.tvStreetSubrub.visibility = View.GONE
            binding.llBack.visibility = View.GONE
            binding.rlTopBar.visibility = View.VISIBLE
            binding.tvEstateName2.visibility = View.VISIBLE
            if (estateName?.isNotEmpty() == true) {
                binding.tvEstateName2.text = estateName
            }
            if (lotStreet?.isNotEmpty() == true
                && lotSuburb?.isNotEmpty() == true
            ) {
                binding.tvStreetSubrub2.text = ("$lotStreet,\n$lotSuburb")

            }
        } else {
            binding.rlTopBar.visibility = View.GONE
            binding.tvEstateName.visibility = View.VISIBLE
            binding.tvStreetSubrub.visibility = View.VISIBLE
            binding.llBack.visibility = View.VISIBLE


            binding.tvEstateName.visibility = View.VISIBLE
            if (estateName?.isNotEmpty() == true) {
                binding.tvEstateName.text = estateName
            }
            if (lotStreet?.isNotEmpty() == true
                && lotSuburb?.isNotEmpty() == true
            ) {
                binding.tvStreetSubrub.text = ("$lotStreet, $lotSuburb")

            }
        }

        adapter = NearbyAdapter(this,requireActivity())
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.addItemDecoration(
            DividerItemDecoration(
                activity,
                LinearLayoutManager.VERTICAL
            )
        )
        binding.recyclerView.adapter = adapter

        binding.tvDirection.setOnClickListener {
            dismiss()
            if (this::totalResponse.isInitialized) {
                if (totalResponse.houseDetailsList != null) {
                    if (totalResponse.houseDetailsList.size > 0) {
                        direction.showDirection(totalResponse.houseDetailsList)
                    }
                }
            }

        }
        binding.tvBookAppointment.setOnClickListener {
            dismiss()
            direction.bookAppointment(houseName,houseSize)
        }
        binding.llContainer.setOnClickListener {
            dismiss()
        }
    }

    interface IDirection {
        fun showDirection(houseList: ArrayList<HouseDetailsByHouseType>)
        fun bookAppointment(houseName: String, houseSize: String)
    }

    override fun locationItemSelectedListener(model: HouseDetailsByHouseType) {
        var dataModel = DisplayDetailStaticModel(
            model.HouseName,
            model.HouseSize,
            model.price,
            model.Storey,
            model.CarSpace,
            model.BathRooms,
            model.BedRooms,
            model.displayId,
            model.isFavorite,
            model.lotWidth
        )

        var bundle = Bundle()
        bundle.putParcelable(AppConstants.USER_FAVORITE_DISPLAY_MODEL, dataModel)
        bundle.putBoolean("isFavorite", false)
        bundle.putString("ESTATE_NAME", estateName)
        loadFragment(DisplayDetailFragment(), bundle)
        dismiss()
    }

    override fun handleFavoriteAndUnFavorite(model: HouseDetailsByHouseType, isFavorite: Boolean) {

        Timber.e("Favorite API boolean $isFavorite")
        val jsonObject = JsonObject()
        jsonObject.addProperty("TypeId", 3)
        jsonObject.addProperty("UserId", userID)
        jsonObject.addProperty("HouseId", model.displayId)
        jsonObject.addProperty("StateId", stateId)
        jsonObject.addProperty("isfavourite", isFavorite)
        myHomeQuizViewModel.setFavorite(jsonObject)

    }

    fun loadFragment(fragment: Fragment, bundle: Bundle) {
        val transaction: FragmentTransaction = parentFragmentManager.beginTransaction()
        fragment.arguments = bundle
        if (fragment.isAdded) return
        transaction.add(R.id.fragment_container, fragment,NearbyBottomSheetFragment::class.simpleName)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)

        Timber.e("Bottom Sheet is Hidden $hidden")
    }

    companion object {
        var TAG = "NearbyBottomSheetFragment"
    }

    override fun dismiss() {
        Timber.e("Bottom Sheet is Hidden")
        super.dismiss()

    }

    override fun onStop() {
        super.onStop()
        Timber.e(TAG, "onStop: ")
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.e(TAG, "onDestroy: ")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Timber.e(TAG, "onDestroyView: ")
    }

}