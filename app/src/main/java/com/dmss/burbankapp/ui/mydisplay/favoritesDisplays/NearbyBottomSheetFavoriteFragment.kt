package com.dmss.burbankapp.ui.mydisplay.favoritesDisplays

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
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
import com.dmss.burbankapp.ui.mydisplay.DisplayHomesViewModel
import com.dmss.burbankapp.ui.mydisplay.DisplayToolbarViewModel
import com.dmss.burbankapp.ui.mydisplay.nearby.NearbyAdapter
import com.dmss.burbankapp.ui.view.CustomProgressDialog
import com.dmss.burbankapp.utils.AppConstants
import com.dmss.burbankapp.utils.AppUtils
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.JsonObject
import common.AppController
import timber.log.Timber


class NearbyBottomSheetFavoriteFragment(var direction: IDirection,var headerTextview:TextView) :
    BottomSheetDialogFragment(),
    NearbyAdapter.IOnItemClickListener {

    lateinit var binding: FragmentNearbyBottomSheetBinding
    lateinit var displayHomeViewModel: DisplayHomesViewModel
    private var customProgressDialog: CustomProgressDialog? = null
    lateinit var myPreference: CustomSharedPreferences
    var istradingnotExpand = true

    lateinit var adapter: NearbyAdapter
    var houseName = ""
    var houseSize = ""
    private var stateId: Int = -1
    private var userID: Int = -1
    var houseDetailsList: ArrayList<HouseDetailsByHouseType> = ArrayList()
    var pathList: ArrayList<LatLng> = ArrayList()
    lateinit var myHomeQuizViewModel: NewHomeQuizViewModel

    lateinit var totalResponse: HouseDetailsResponseModel
    private lateinit var displayToolbarViewModel: DisplayToolbarViewModel

    var estateName: String? = null
    var lotStreet: String? = null
    var lotSuburb: String? = null
    fun newInstance(
        estateName: String?,
        lotStreet: String?,
        lotSuburb: String?
    ): NearbyBottomSheetFavoriteFragment? {
        val frag = NearbyBottomSheetFavoriteFragment(direction,headerTextview)
        val args = Bundle()
        this.estateName = estateName
        args.putString("estateName", estateName)
        args.putString("lotStreet", lotStreet)
        args.putString("lotSuburb", lotSuburb)
        frag.arguments = args
        return frag
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(BottomSheetDialogFragment.STYLE_NO_FRAME, R.style.CustomBottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentNearbyBottomSheetBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initViewModel()
    }



    private fun initViewModel() {
        binding.llBack.setOnClickListener {
            activity?.onBackPressed()
        }

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
                    customProgressDialog?.dismissProgress()

                    if (it.data != null) {
                        if (it.data.status) {

                            totalResponse = it.data

                            var timeString = ""

                            if (totalResponse.houseDetailsList.isNotEmpty()
                                && totalResponse.houseDetailsList.size > 0
                            ) {
                                if (totalResponse.houseDetailsList[0].openTimes.isNotEmpty()) {
                                    var array =
                                        totalResponse.houseDetailsList[0].openTimes.split(",")
                                            .toTypedArray()

                                    for (str in array) {
                                        timeString += ("$str | ")

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
                                    houseSize = ("${model.HouseSize}")
                                }
                                houseDetailsList.clear()
                                houseDetailsList = totalResponse.houseDetailsList

                                adapter.submitList(totalResponse.houseDetailsList)
                            }

                            /*var totalResponse: NearByDisplaysResponseModel = it.data


                            if (totalResponse.regionsList.isNotEmpty() && totalResponse.regionsList.size > 0) {
                                adapter.submitList(totalResponse.regionsList)

                            }*/
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

        myHomeQuizViewModel.getFavorite().observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    customProgressDialog?.dismissProgress()
                    AppConstants.updateDisplayHomesFav=true

                    (activity as MainActivity).loadUserFavoritesDisplays()
                    displayToolbarViewModel.setFavouriteDisplayHomes(0)

                    if (it.data != null) {
                        if (it.data.status) {
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
        customProgressDialog = CustomProgressDialog(requireContext())
        myPreference = CustomSharedPreferences(requireContext())
        stateId = myPreference.getStateID()
        userID = myPreference.getUserId()
        displayToolbarViewModel = ViewModelProviders.of(requireActivity())[DisplayToolbarViewModel::class.java]
        AppConstants.updateDisplayHomesFav=false
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
        binding.ivBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        AppConstants.BACKSTACK_COUNT = 0
        estateName = arguments?.get("estateName") as String?
        lotStreet = arguments?.get("lotStreet") as String?
        lotSuburb = arguments?.get("lotSuburb") as String?

        if (estateName?.isNotEmpty() == true) {
            binding.tvEstateName.text = estateName
        }
        if (lotStreet?.isNotEmpty() == true
            && lotSuburb?.isNotEmpty() == true
        ) {
            binding.tvStreetSubrub.text = ("$lotStreet,$lotSuburb")

        }

        Timber.e("Estate name --->${estateName}")

        binding.tvEstateName2.visibility = View.VISIBLE
        if (estateName?.isNotEmpty() == true) {
            binding.tvEstateName2.text = estateName
//            binding.tvEstateName2.text  = "See one of our display homes"
        }
        if (lotStreet?.isNotEmpty() == true
            && lotSuburb?.isNotEmpty() == true
        ) {
            binding.tvStreetSubrub2.text = ("$lotStreet,\n$lotSuburb")

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
                if (totalResponse.houseDetailsList.size > 0) {
                    direction.showDirection(totalResponse.houseDetailsList)
                }

            }
        }
        binding.tvBookAppointment.setOnClickListener {
            dismiss()
            direction.bookAppointment(houseName,houseSize)
        }
    }

    interface IDirection {
        fun showDirection(houseList: ArrayList<HouseDetailsByHouseType>)
        fun bookAppointment(houseName: String,houseSize : String)
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
        bundle.putString("street", lotStreet)
        bundle.putString("estateName", estateName)

        headerTextview.text = "See one of our display homes"
        loadFragment(FavoriteDisplayDetailFragment(), bundle)
        dismiss()
    }

    override fun handleFavoriteAndUnFavorite(model: HouseDetailsByHouseType, isFavorite: Boolean) {
        val jsonObject = JsonObject()
        jsonObject.addProperty("TypeId", 3)
        jsonObject.addProperty("UserId", userID)
        jsonObject.addProperty("HouseId", model.displayId)
        jsonObject.addProperty("StateId", stateId)
        jsonObject.addProperty("isfavourite", isFavorite)
        myHomeQuizViewModel.setFavorite(jsonObject)
    }

    fun loadFragment(fragment: Fragment, bundle: Bundle) {
        // load fragment
        val transaction = activity?.supportFragmentManager!!.beginTransaction()
        fragment.arguments = bundle
        transaction.add(R.id.fl_content, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }


}