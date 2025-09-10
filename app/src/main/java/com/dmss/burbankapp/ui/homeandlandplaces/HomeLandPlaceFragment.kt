package com.dmss.burbankapp.ui.homeandlandplaces

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dmss.burbankapp.R
import com.dmss.burbankapp.data.api.ApiHelperImpl
import com.dmss.burbankapp.data.api.RetrofitBuilder
import com.dmss.burbankapp.data.apiUtils.Status
import com.dmss.burbankapp.data.apiUtils.ViewModelFactory
import com.dmss.burbankapp.data.local.CustomSharedPreferences
import com.dmss.burbankapp.data.local.entity.DatabaseBuilder
import com.dmss.burbankapp.data.local.entity.DatabaseHelperImpl
import com.dmss.burbankapp.data.model.*
import com.dmss.burbankapp.databinding.FragmentHomeLandPlaceBinding
import com.dmss.burbankapp.databinding.ProfileWithBadgeBinding
import com.dmss.burbankapp.ui.homeandland.HomeLandFilterFragment
import com.dmss.burbankapp.ui.homeandland.HomeLandFullScreenFragment
import com.dmss.burbankapp.ui.homeandland.HomeLandMapFragment
import com.dmss.burbankapp.ui.homeandlandregions.HomeAndLandViewModel
import com.dmss.burbankapp.ui.main.MainActivity
import com.dmss.burbankapp.ui.mydisplay.DisplayToolbarViewModel
import com.dmss.burbankapp.ui.view.CustomProgressDialog
import com.dmss.burbankapp.utils.AppConstants
import com.dmss.burbankapp.utils.AppUtils
import com.dmss.burbankapp.utils.customviews.AppEvent
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import common.AppController
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber

class HomeLandPlaceFragment : Fragment(),
    HomeLandPlaceAdapter.PackageItemClick, HomeLandFilterFragment.IUpdateFilterData,
    HomeAndLandFavoritesAdapter.FavoritePackageItemClick {
    private lateinit var linearLayoutManager: LinearLayoutManager
    lateinit var homeLandViewModel: HomeAndLandViewModel
    lateinit var preferences: CustomSharedPreferences
    lateinit var homeAndlandPlaceAdapter: HomeLandPlaceAdapter
    lateinit var homeLandFavoritesAdapter: HomeAndLandFavoritesAdapter
    lateinit var packagesList: ArrayList<HnLQuizPackageModel>
    var pagination = 1
    var pastVisibleItems: Int = 0
    var visibleItemCount: Int = 0
    var totalItemCount: Int = 0
    var loading = false
    private var favoriteButtonTap = false
    private var loadMapFragment = false
    private var customProgressDialog: CustomProgressDialog? = null
    var minimumPriceForPriceRange: Double? = null
    var maximumPriceForPriceRange: Double? = null

    var homeLandFilterPackageFound = false

    lateinit var binding: FragmentHomeLandPlaceBinding
    var favoritesHnlPackages: ArrayList<HnLQuizPackageModel> = ArrayList()
    var favoritesHnlPackagestwo: ArrayList<HnLQuizPackageModel> = ArrayList()

    lateinit var userInfoModel: UserInfoModel
    private var SELECT_FAVORITE: Boolean = false

    private var isFromSortByPrice: Boolean = true

    var currentUserList: ArrayList<HnLQuizPackageModel> = ArrayList()
    var otherUserList: ArrayList<HnLQuizPackageModel> = ArrayList()
    private lateinit var displayToolbarViewModel: DisplayToolbarViewModel

    lateinit var profileWithBadgeBinding: ProfileWithBadgeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeLandPlaceBinding.inflate(inflater)
        profileWithBadgeBinding = ProfileWithBadgeBinding.bind(binding.root)

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        minimumPriceForPriceRange = arguments?.getDouble("MIN_PRICE")
        maximumPriceForPriceRange = arguments?.getDouble("MAX_PRICE")
        minimumPriceForPriceRange?.let {
            AppConstants.MIN_PRICE = it.toString()
        }
        maximumPriceForPriceRange?.let {
            AppConstants.MAX_PRICE = it.toString()
        }
        setUpViewModel()
        initViews()
    }


    private fun setUpViewModel() {
        homeLandViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(
                ApiHelperImpl(RetrofitBuilder.apiService),
                DatabaseHelperImpl(DatabaseBuilder.getInstance(AppController.getInstance()))
            )
        ).get(HomeAndLandViewModel::class.java)
        setupObserver()
    }

    private fun setupObserver() {
        homeLandViewModel.getHnLPackagesLiveData().observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    binding.tvList.isClickable = true
                    customProgressDialog?.dismissProgress()

                    binding.recyclerPlaces.visibility = View.VISIBLE
                    binding.recyclerFavorites.visibility = View.GONE

                    var regionsData: HnLPackagesModel? = it.data
                    if (regionsData != null && regionsData.status) {
                        regionsData.HnLQuizResults?.let { it1 -> preferences.setPackagesCount(it1) }
                        if (pagination == 1) {
                            var userId: Int = preferences.getUserId()
                            var stateId: Int = preferences.getStateID()
                            val bathRoomArray = ArrayList<SearchSubModel>()
                            val storeyFilterArray = ArrayList<SearchSubModel>()
                            val carSpacesArray = ArrayList<SearchSubModel>()
                            val bedRoomFilterArray = ArrayList<SearchSubModel>()

                            var jObjectRequest = JsonObject()
                            val region = AppConstants.SELECTED_REGION
                            jObjectRequest.addProperty("RegionName", region)
                            var regionAddressObj = JsonObject()
                            regionAddressObj.addProperty("Longitudefield", "")
                            regionAddressObj.addProperty("Latitudefield", "")
                            jObjectRequest.add("RegionAddress", regionAddressObj)

                            var regionsList = ArrayList<RegionsModel>()

                            var regionsModel = RegionsModel(region)
                            regionsList.add(regionsModel)


                            if (AppConstants.SELECTED_STOREY != -1 && AppConstants.SELECTED_STOREY != 3) {
                                if (AppConstants.SELECTED_STOREY == 1) {
                                    storeyFilterArray.add(
                                        SearchSubModel(
                                            1,
                                            true,
                                            "Single"
                                        )
                                    )
                                    storeyFilterArray.add(
                                        SearchSubModel(
                                            2,
                                            false,
                                            "Double"
                                        )
                                    )
                                } else {
                                    storeyFilterArray.add(
                                        SearchSubModel(
                                            1,
                                            false,
                                            "Single"
                                        )
                                    )
                                    storeyFilterArray.add(
                                        SearchSubModel(
                                            2,
                                            true,
                                            "Double"
                                        )
                                    )
                                }
                            } else {
                                storeyFilterArray.add(
                                    SearchSubModel(
                                        1,
                                        true,
                                        "Single"
                                    )
                                )
                                storeyFilterArray.add(
                                    SearchSubModel(
                                        2,
                                        true,
                                        "Double"
                                    )
                                )
                            }

                            /*AppConstants.BedRooms?.let { bedroomList ->
                                {
                                    if (bedroomList.size < 3) {
                                        bedRoomFilterArray.add(SearchSubModel(3, false, "3"))
                                    }
                                }
                            }*/

                            for (bedRoomCount in 3..6){
                                bedRoomFilterArray.add(
                                    SearchSubModel(
                                        bedRoomCount,
                                        AppConstants.BedRooms?.contains(bedRoomCount) == true,
                                        "$bedRoomCount"
                                    )
                                )
                            }

                            for (bathRoom in 2..3) {
                                bathRoomArray.add(
                                    SearchSubModel(
                                        bathRoom,
                                        AppConstants.Bathrooms?.contains(bathRoom) == true,
                                        "$bathRoom"
                                    )
                                )
                            }
                            for (carSpace in 1..2) {
                                carSpacesArray.add(
                                    SearchSubModel(
                                        carSpace,
                                        AppConstants.CarSpaces?.contains(carSpace) == true,
                                        "$carSpace"
                                    )
                                )
                            }


                            var searchTextJsonObjRequest = SearchTextJsonObjRequest(
                                "price",
                                AppConstants.MAX_PRICE,
                                AppConstants.MIN_PRICE,
                                "140",
                                "458",
                                bathRoomArray,
                                bedRoomFilterArray,
                                carSpacesArray,
                                regionsList,
                                storeyFilterArray

                            )
                            var recentSearchObjectRequest = RecentSearchObjectRequest(
                                userId.toString(), stateId.toString(), 1, searchTextJsonObjRequest

                            )
                            homeLandViewModel.setRecentSearchData(recentSearchObjectRequest)
                        }
                    }
                    if (regionsData != null) {
                        if (regionsData.hnlQuizList != null) {
                            if (regionsData.hnlQuizList!!.size > 0) {
                                loading = true
                                pagination++
                                if (pagination == 1) {
                                    packagesList.clear()
                                }
                                regionsData.hnlQuizList?.let { it1 -> packagesList.addAll(it1) }
                                loadMapFragment = true
                                if (packagesList.size > 0) {
                                    homeAndlandPlaceAdapter.notifyDataSetChanged()
                                } else {
                                    binding.recyclerPlaces.visibility = View.GONE
                                    binding.recyclerFavorites.visibility = View.GONE
                                    binding.tvNoFavorites.visibility = View.VISIBLE
                                    binding.tvNoFavorites.text = getString(R.string.no_saved_packages)
                                }
                            } else {
                                loading = false
                                noMorePackagesLoad()
                            }


                        } else {
                            loading = false
                            noMorePackagesLoad()
                        }


                    } else {
                        loading = false
                        noMorePackagesLoad()
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
        homeLandViewModel.getFavorite().observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    customProgressDialog?.dismissProgress()
                    (activity as MainActivity).loadUserFavoritesHomeLand()
                    var favoriteResponseModel: FavoriteResponseModel? = it.data
                    if (favoritesHnlPackages.size == 0 && packagesList.size ==0){
                        binding.recyclerPlaces.visibility = View.GONE
                        binding.recyclerFavorites.visibility = View.GONE
                        binding.tvNoFavorites.visibility = View.VISIBLE
                        binding.tvNoFavorites.text = getString(R.string.no_saved_packages)
                    }
                    val toast = Toast.makeText(
                        requireContext(),
                        favoriteResponseModel?.message,
                        Toast.LENGTH_SHORT
                    )
                    toast.setGravity(Gravity.CENTER, 0, 0)
                    toast.show()


                }
                Status.LOADING -> {
                    customProgressDialog?.showProgress()
                }
                Status.ERROR -> {
                    customProgressDialog?.dismissProgress()
                }
            }

        })
        homeLandViewModel.getFavoritesListLiveData().observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    customProgressDialog?.dismissProgress()
                    var houseListData: ArrayList<HouseListModel>? = it.data
                    if (houseListData != null && houseListData.size > 0) {
                        favoritesHnlPackages.clear()
                        for (houseListModel in houseListData) {
                            if (houseListModel.fetchJsonRecentSearches.homeAndLandDTOsList.size > 0) {

                                Timber.e("Api user Name ::${houseListModel.fetchJsonRecentSearches.FullName.trim()}")
                                Timber.e("Local user Name ::${userInfoModel.FirstName}")

                                var userFullName =
                                    userInfoModel.FirstName + " " + userInfoModel.LastName
                                Timber.e("Api user FullName   $userFullName")
                               /* if (userInfoModel.FirstName?.let { it1 ->
                                        houseListModel.fetchJsonRecentSearches.FullName.trim()
                                            .contains(
                                                it1
                                            )
                                    } == true) {*/

                                // Changed by durga 11/10/2022
                                if(userInfoModel.FirstName+" "+userInfoModel.LastName == houseListModel.fetchJsonRecentSearches.FullName.trim()){

                                    val currentUserHeader =
                                    getString(R.string.favourite_packages)+" (${houseListModel.fetchJsonRecentSearches.homeAndLandDTOsList.size})"
                                val hnLQuizPackageModel = HnLQuizPackageModel(
                                    "Address",
                                    0,
                                    0,
                                    0,
                                    12345.23,
                                    "FacadePermanentUrl",
                                    "Facade",
                                    "HouseName",
                                    false, "12.222",
                                    "12.12222",
                                    12.12222,
                                    1,
                                    1,
                                    currentUserHeader,
                                    0, 0, 0f
                                )
                                currentUserList = ArrayList()
                                currentUserList.add(hnLQuizPackageModel)
                                for (model: HnLQuizPackageModel in houseListModel.fetchJsonRecentSearches.homeAndLandDTOsList) {
                                    model.currentUserName =
                                        houseListModel.fetchJsonRecentSearches.FullName.trim()
                                    currentUserList.add(model)
                                }

                            }
                                // commented other user favouries by durga
                             /*   else {
                                val otherUserHeader =
                                    "${houseListModel.fetchJsonRecentSearches.FullName} ${getString(R.string.favourite_packages)} (${houseListModel.fetchJsonRecentSearches.homeAndLandDTOsList.size})"
                                val hnLQuizPackageModel = HnLQuizPackageModel(
                                    "Address",
                                    0,
                                    0,
                                    0,
                                    12345.88,
                                    "FacadePermanentUrl",
                                    "Facade",
                                    "HouseName",
                                    false,
                                    "12.222",
                                    "12.12222",
                                    12.12222,
                                    1,
                                    1,
                                    otherUserHeader,
                                    0, 0, 0f
                                )
                                otherUserList = ArrayList()
                                otherUserList.add(hnLQuizPackageModel)
                                otherUserList.addAll(houseListModel.fetchJsonRecentSearches.homeAndLandDTOsList)
                            }*/
                            }
                            binding.recyclerPlaces.removeAllViews()
                            binding.recyclerFavorites.removeAllViews()

                            binding.recyclerPlaces.visibility = View.GONE
                            binding.tvNoFavorites.visibility = View.GONE
                            binding.recyclerFavorites.visibility = View.VISIBLE
                          /*  binding.recyclerFavorites.addItemDecoration(
                                DividerItemDecoration(
                                    activity,
                                    LinearLayoutManager.VERTICAL
                                )
                            )*/

                           /* if (currentUserList.size > 1) {
                                favoritesHnlPackages = ArrayList()
                                favoritesHnlPackages.addAll(currentUserList)
                                favoritesHnlPackages.addAll(otherUserList)
                            } else {
                                favoritesHnlPackages = ArrayList()
                                favoritesHnlPackages.addAll(otherUserList)

                            }*/

                            // changed by durga

                            if (currentUserList.size > 1) {
//                                        favoritesHnlPackages = ArrayList()
                                if(!favoritesHnlPackages.containsAll(currentUserList)) {
                                    favoritesHnlPackages.addAll(currentUserList)
                                }
                                if(!favoritesHnlPackages.containsAll(otherUserList)) {
                                    favoritesHnlPackages.addAll(otherUserList)
                                }
                            }
                            else {
//                                        favoritesHnlPackages = ArrayList()
//                                        favoritesHnlPackages.addAll(otherUserList)
                                if(!favoritesHnlPackages.containsAll(otherUserList)) {
                                    favoritesHnlPackages.addAll(otherUserList)
                                }
                            }

                            Timber.e("Hnl Favorites" + favoritesHnlPackages.size)

                            if (favoritesHnlPackages.size != 0) {
                                binding.recyclerFavorites.apply {
                                    homeLandFavoritesAdapter = HomeAndLandFavoritesAdapter(
                                        context,
                                        favoritesHnlPackages, this@HomeLandPlaceFragment
                                    )
                                }
                                binding.recyclerFavorites.adapter = homeLandFavoritesAdapter

                            } else {
                                binding.recyclerPlaces.visibility = View.GONE
                                binding.recyclerFavorites.visibility = View.GONE
                                binding.tvNoFavorites.visibility = View.VISIBLE
                                binding.tvNoFavorites.text = "No Favourite Packages found"
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
    fun noMorePackagesLoad(){
        val toast = Toast.makeText(
            requireContext(),
            "No more packages to load.",
            Toast.LENGTH_SHORT
        )
        toast.setGravity(Gravity.CENTER, 0, 0)
        toast.show()
    }

    private fun setToolbarHeaderTitle() {
        val toolHeaderStringBuilder = StringBuilder()
        for (key in AppConstants.toolheaderHashMapHomeLand.keys) {
            val value = AppConstants.toolheaderHashMapHomeLand[key]
            if(value!="All") {
                toolHeaderStringBuilder.append(value)
            }
        }
        binding.tvTool.visibility = View.VISIBLE
        binding.tvTool.text = toolHeaderStringBuilder.toString()

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

        linearLayoutManager = LinearLayoutManager(activity)
        preferences = CustomSharedPreferences(AppController.getInstance())
        userInfoModel = preferences.getUserInfoModel()
        customProgressDialog = CustomProgressDialog(requireContext())
        profileWithBadgeBinding.profileNotification.text=AppConstants.TotalMyFavs.toString()
        profileNotificationCountView(AppConstants.TotalMyFavs)
        displayToolbarViewModel.favouritesCount.observe(viewLifecycleOwner, Observer<Int> { item ->
            profileWithBadgeBinding.profileNotification.text = item.toString()
            profileNotificationCountView(item)

        })
        setToolbarHeaderTitle()
        packagesList = ArrayList()
        var isUserLoggedIn = preferences.getUserLogin()
        AppConstants.isHomeLandProfile=false
        if (!isUserLoggedIn) {
            profileWithBadgeBinding.tvFavorites.background = resources.getDrawable(R.drawable.disable_rectangle_new)
            profileWithBadgeBinding.profileNotification.visibility=View.GONE
        }
        profileWithBadgeBinding.tvFavorites.setOnClickListener {
            var isUserLoggedIn = preferences.getUserLogin()

            if (isUserLoggedIn) {

                (activity as MainActivity).showProfileDialog(activity!!)

            } else {
                AppUtils.showPleaseLoginDialog(
                    requireContext(),
                    requireActivity(),
                    getString(R.string.Please_login_to_view_edit_profile)
                )
            }


        }

        binding.tvList.setOnClickListener {
            if (loadMapFragment) {
                val bundle = Bundle()
                bundle.putParcelableArrayList(AppConstants.PACKAGESLIST, packagesList)
                loadFragment(HomeLandMapFragment(), bundle, true)
            }
        }
        binding.ivBack.setOnClickListener {
            activity!!.onBackPressed()
        }


        binding.tvSortFilter.setOnClickListener {
            showEditDialog()
        }
        val horizontalDecoration = DividerItemDecoration(
            binding.recyclerPlaces.context,
            DividerItemDecoration.VERTICAL
        )
        val horizontalDivider = ContextCompat.getDrawable(
            activity!!, R.drawable.divider
        )
        horizontalDecoration.setDrawable(horizontalDivider!!)
        binding.recyclerPlaces.addItemDecoration(horizontalDecoration)
        binding.recyclerFavorites.addItemDecoration(horizontalDecoration)
     /*   binding.recyclerPlaces.addItemDecoration(
            DividerItemDecoration(
                activity,
                LinearLayoutManager.VERTICAL
            )
        )*/

        binding.recyclerPlaces.apply {
            homeAndlandPlaceAdapter = HomeLandPlaceAdapter(
                false,
                context,
                requireActivity(),
                packagesList, this@HomeLandPlaceFragment
            )
            layoutManager = linearLayoutManager
        }
        binding.recyclerPlaces.adapter = homeAndlandPlaceAdapter

        binding.recyclerPlaces.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) //check for scroll down
                {
                    visibleItemCount = linearLayoutManager.childCount
                    totalItemCount = linearLayoutManager.itemCount
                    pastVisibleItems = linearLayoutManager.findFirstVisibleItemPosition()
                    if (loading) {
                        if (visibleItemCount + pastVisibleItems >= totalItemCount) {
                            loading = false
                            if (favoriteButtonTap) {
                                fetchAllFavoritesApiCall()
                            } else {
                                hnlApiCall(pagination)
                            }

                        }
                    }
                }
            }
        })
        arguments?.getBoolean(AppConstants.SELECT_FAVORITE, false)?.let {
            SELECT_FAVORITE = it
        }

        if (SELECT_FAVORITE) {

//            binding.tvFavorites.visibility = View.INVISIBLE
            binding.tvTool.text = getString(R.string.favourite_packages)
            binding.tvSortFilter.visibility = View.INVISIBLE
            binding.tvList.visibility = View.INVISIBLE

            favoriteButtonTap = true
            pagination = 1
            packagesList.clear()
            homeAndlandPlaceAdapter.setFavorite(true)
            binding.recyclerPlaces.visibility = View.GONE
            binding.recyclerFavorites.visibility = View.VISIBLE
            binding.favHeader.visibility=View.VISIBLE
            fetchAllFavoritesApiCall()
        }
        else {
//            binding.tvFavorites.visibility = View.VISIBLE
            binding.tvSortFilter.visibility = View.VISIBLE
            binding.tvList.visibility = View.VISIBLE
            binding.favHeader.visibility=View.GONE
            val list = arrayListOf<String>()
            list.add(AppConstants.REGION_HEADER.replace("|","").trim())
            list.add(AppConstants.PRICE_RANGE_HEADER.replace("|","").trim())
            if(AppConstants.STOREYS_HEADER!="All") {
                list.add(AppConstants.STOREYS_HEADER.replace("|", "").trim())
            }
            if(AppConstants.BEDROOMS_HEADER!="All") {
                list.add(AppConstants.BEDROOMS_HEADER.replace("|", "").trim())
            }
            val headerString = list.filter { it.trim().isNotEmpty() }.joinToString(prefix = "", separator = " | ", postfix = "")
            binding.tvTool.text = headerString
            favoriteButtonTap = false
            homeAndlandPlaceAdapter.setFavorite(false)
            packagesList.clear()
            binding.recyclerPlaces.visibility = View.VISIBLE
            binding.recyclerFavorites.visibility = View.GONE

            binding.recyclerPlaces.apply {
                homeAndlandPlaceAdapter = HomeLandPlaceAdapter(
                    false,
                    context,
                    requireActivity(),
                    packagesList, this@HomeLandPlaceFragment
                )
                layoutManager = linearLayoutManager
            }
            binding.recyclerPlaces.adapter = homeAndlandPlaceAdapter
            hnlApiCall(pagination)

        }

        showFavoriteButtonBackground()


    }

    private fun showFavoriteButtonBackground() {
        if (!favoriteButtonTap) {
          /*  binding.tvFavorites.background =
                ContextCompat.getDrawable(AppController.getInstance(), R.drawable.rectangel_line)
            binding.tvFavorites.setTextColor(
                ContextCompat.getColor(
                    AppController.getInstance(),
                    R.color.white
                )
            )
*/

        } else {
          /*  binding.tvFavorites.background =
                ContextCompat.getDrawable(AppController.getInstance(), R.drawable.rectangel_shape)
            binding.tvFavorites.setTextColor(
                ContextCompat.getColor(
                    AppController.getInstance(),
                    R.color.appColor
                )
            )*/
        }
    }

    private fun fetchAllFavoritesApiCall() {
        var jsonObject = JsonObject()
        jsonObject.addProperty("TypeId", 1)
        jsonObject.addProperty("UserId", preferences.getUserId())
        jsonObject.addProperty("StateId", preferences.getStateID())

        homeLandViewModel.getAllFavorites(jsonObject)
    }

    fun loadFragment(fragment: Fragment, bundle: Bundle, isNotFullScreen: Boolean) {
        if (preferences != null) {
            preferences.savePriceRange("Low")
        }

        // load fragment
        val transaction: FragmentTransaction = activity?.supportFragmentManager!!.beginTransaction()
        if (isNotFullScreen) {
            fragment.arguments = bundle
        }
        if (fragment.isAdded) {
            return
        }
        transaction.add(R.id.fl_content, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun showEditDialog() {
        homeLandFilterPackageFound = false

        minimumPriceForPriceRange?.let {
            AppConstants.MIN_PRICE = it.toString()
        }
        maximumPriceForPriceRange?.let {
            AppConstants.MAX_PRICE = it.toString()
        }
        val fm: FragmentManager = activity!!.supportFragmentManager
        var fragment = HomeLandFilterFragment(this)
            .newInstance("Hello", this)
        fragment?.show(fm, "fragment_edit_name")
    }

    fun hnlApiCall(page: Int) {

        Timber.e("Api Call Pagination Page Count------>$page")
        val stateId = preferences.getStateID()
        var userId = preferences.getUserId()

        val region = AppConstants.SELECTED_REGION


        val jsonObject = JsonObject()
        val hnlQuizObject = JsonObject()
        hnlQuizObject.addProperty("Region", region)


        val storeyArray = JsonArray()

        if (AppConstants.FILTER_SELECTED_STOREY == -1 || !homeLandFilterPackageFound) {
            if (AppConstants.SELECTED_STOREY != 3 && AppConstants.SELECTED_STOREY != -1) {
                storeyArray.add(AppConstants.SELECTED_STOREY)
            } else {
                storeyArray.add(1)
                storeyArray.add(2)
            }
        } else {
            if (AppConstants.FILTER_SELECTED_STOREY != 3 && AppConstants.FILTER_SELECTED_STOREY != -1) {
                storeyArray.add(AppConstants.FILTER_SELECTED_STOREY)
            } else {
                storeyArray.add(1)
                storeyArray.add(2)
            }
        }

        val bedrooms = JsonArray()
        if (AppConstants.FILTER_BedRooms == null || !homeLandFilterPackageFound) {
//            AppConstants.BedRooms= arrayOf(1,2,3,4,5,6)
            AppConstants.BedRooms?.let {
                for (bed in it) {
                    bedrooms.add(bed)
                }
            } ?: kotlin.run {
                AppConstants.FILTER_BedRooms?.let {
                    for (bed in it) {
                        bedrooms.add(bed)
                    }
                }
            }
        } else {
            AppConstants.FILTER_BedRooms?.let {
                for (bed in it) {
                    bedrooms.add(bed)
                }
            }
        }

        val carspaces = JsonArray()
        if (AppConstants.FILTER_CarSpaces != null) {
            AppConstants.FILTER_CarSpaces?.let {
                for (car in it) {
                    carspaces.add(car)
                }
            } ?: kotlin.run {
                carspaces.add(1)
                carspaces.add(2)
            }
        } else {
            carspaces.add(1)
            carspaces.add(2)
        }
        val bathrooms = JsonArray()
        if (AppConstants.FILTER_Bathrooms != null || !homeLandFilterPackageFound) {
            AppConstants.FILTER_Bathrooms?.let {
                for (bathroom in it) {
                    bathrooms.add(bathroom)
                }
            } ?: run {
                for (bathroom in arrayOf(1, 2, 3, 4, 5, 6)) {
                    bathrooms.add(bathroom)
                }
            }
        } else {
            for (bathroom in arrayOf(1, 2, 3, 4, 5, 6)) {
                bathrooms.add(bathroom)
            }
        }
        hnlQuizObject.add("Storey", storeyArray)
        hnlQuizObject.add("BedRooms", bedrooms)
        hnlQuizObject.add("CarSpaces", carspaces)
        hnlQuizObject.add("Bathrooms", bathrooms)
        hnlQuizObject.addProperty("StateId", stateId)

        if (AppConstants.FILTER_MIN_PRICE.trim().isEmpty() && AppConstants.FILTER_MAX_PRICE.trim()
                .isEmpty()
        ) {
            minimumPriceForPriceRange?.let { minimumPrice ->
                hnlQuizObject.addProperty("SelectedMinValue", minimumPrice)
            } ?: kotlin.run {
                hnlQuizObject.addProperty("SelectedMinValue", AppConstants.MIN_PRICE)
            }
            maximumPriceForPriceRange?.let { maximumPrice ->
                hnlQuizObject.addProperty("SelectedMaxValue", maximumPrice)
            } ?: kotlin.run {
                hnlQuizObject.addProperty("SelectedMaxValue", AppConstants.MAX_PRICE)
            }
        } else {
            if (homeLandFilterPackageFound) {
                hnlQuizObject.addProperty("SelectedMinValue", AppConstants.FILTER_MIN_PRICE)
                hnlQuizObject.addProperty("SelectedMaxValue", AppConstants.FILTER_MAX_PRICE)
            } else {
                minimumPriceForPriceRange?.let { minimumPrice ->
                    hnlQuizObject.addProperty("SelectedMinValue", minimumPrice)
                } ?: kotlin.run {
                    hnlQuizObject.addProperty("SelectedMinValue", AppConstants.MIN_PRICE)
                }
                maximumPriceForPriceRange?.let { maximumPrice ->
                    hnlQuizObject.addProperty("SelectedMaxValue", maximumPrice)
                } ?: kotlin.run {
                    hnlQuizObject.addProperty("SelectedMaxValue", AppConstants.MAX_PRICE)
                }
            }

        }
        hnlQuizObject.addProperty("IncludePackages", 1)
        hnlQuizObject.addProperty("PageNo", page)

        if (isFromSortByPrice) {
            hnlQuizObject.addProperty("SortByPrice", 0)
        } else {
            hnlQuizObject.addProperty("SortByPrice", 1)
        }

        jsonObject.add("HnLQuizItems", hnlQuizObject)
        homeLandViewModel.fetchHnLPackages(userId, jsonObject)
    }

    override fun selectedPackageItem(hnLQuizPackageModel: HnLQuizPackageModel) {
        val bundle = Bundle()
        bundle.putParcelable("HNLQUIZMODEL", hnLQuizPackageModel)
        bundle.putBoolean("ISFROMFAVORITE", false)
        bundle.putBoolean("ISCURRENTUSER", true)
        loadFragment(HomeLandFullScreenFragment(), bundle, true)
    }

    override fun selectFavoriteItemClick(
        hnLQuizPackageModel: HnLQuizPackageModel,
        isFavorite: Boolean
    ) {
        val jsonObject = JsonObject()
        jsonObject.addProperty("TypeId", 1)
        jsonObject.addProperty("UserId", preferences.getUserId())
        jsonObject.addProperty("HouseId", hnLQuizPackageModel.packageId)
        jsonObject.addProperty("StateId", preferences.getStateID())
        jsonObject.addProperty("isfavourite", isFavorite)
        homeLandViewModel.setFavorite(jsonObject)

        updateFavoriteModelInList(hnLQuizPackageModel, isFavorite)

    }

    private fun updateFavoriteModelInList(
        hnLQuizPackageModel: HnLQuizPackageModel,
        isFavorite: Boolean
    ) {
        if (packagesList.size > 0) {
            for ((index, value) in packagesList.withIndex()) {
                if (value.id == hnLQuizPackageModel.id)
                    packagesList[index].isFav = isFavorite
            }
        }

    }

    override fun updateFilterData(sortByPrice: Int, packageFound: Boolean) {
        if (AppConstants.FILTER_toolHeaderHashMapHomeLand.size > 0) {
            val list = arrayListOf<String>()
            AppConstants.FILTER_toolHeaderHashMapHomeLand.values.forEachIndexed { index, s ->
                list.add(s.replace("|", "").trim())
            }
            val toolHeaderStringBuilder = list.filter{ it.trim().isNotEmpty() }
                .joinToString(prefix = "", postfix = "", separator = " | ")
            binding.tvTool.text = toolHeaderStringBuilder

        }
        homeLandFilterPackageFound = packageFound
        Timber.e("HomeLand --Filter ---- $packageFound")
        isFromSortByPrice = sortByPrice == 0
        packagesList.clear()
        pagination = 1
        hnlApiCall(1)
    }

    override fun selectedFavoriteDetailItem(hnLQuizPackageModel: HnLQuizPackageModel) {
        val bundle = Bundle()
        var isCurrentUser: Boolean = false
        hnLQuizPackageModel.currentUserName?.let {
            isCurrentUser = userInfoModel.FirstName?.let { it1 -> it.contains(it1) } == true
        }
        bundle.putParcelable("HNLQUIZMODEL", hnLQuizPackageModel)
        bundle.putBoolean("ISFROMFAVORITE", true)
        bundle.putBoolean("ISCURRENTUSER", isCurrentUser)
        loadFragment(HomeLandFullScreenFragment(), bundle, true)
    }

    override fun unFavoriteItemClick(hnLQuizPackageModel: HnLQuizPackageModel) {
        if (favoritesHnlPackages.size > 0) {
            val jsonObject = JsonObject()
            jsonObject.addProperty("TypeId", 1)
            jsonObject.addProperty("UserId", preferences.getUserId())
            jsonObject.addProperty("HouseId", hnLQuizPackageModel.packageId)
            jsonObject.addProperty("StateId", preferences.getStateID())
            jsonObject.addProperty("isfavourite", false)
            homeLandViewModel.setFavorite(jsonObject)


            val iterater: MutableIterator<HnLQuizPackageModel> = currentUserList.iterator()
            while (iterater.hasNext()) {
                if ((iterater.next() == hnLQuizPackageModel)) {
                    iterater.remove()
                }
            }
            if (currentUserList.size > 1) {
                val headerCount: Int = currentUserList.size - 1
                val currentUserHeader =
                    getString(R.string.favourite_packages)+" ("+headerCount+")"
                val hnLQuizPackageModel = HnLQuizPackageModel(
                    "Address",
                    0,
                    0,
                    0,
                    12345.88,
                    "FacadePermanentUrl",
                    "Facade",
                    "HouseName",
                    false,
                    "12.222",
                    "2.12222",
                    12.12222,
                    1,
                    1,
                    currentUserHeader,
                    0, 0, 0f
                )
                currentUserList[0] = hnLQuizPackageModel
                favoritesHnlPackages = ArrayList()
                favoritesHnlPackages.addAll(currentUserList)
                favoritesHnlPackages.addAll(otherUserList)
            } else {
                favoritesHnlPackages = ArrayList()
                favoritesHnlPackages.addAll(otherUserList)
            }
            homeLandFavoritesAdapter.setAndUpdateData(favoritesHnlPackages)
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onAppEvent(event: AppEvent) {
        if (event.type == AppEvent.UPDATE_FAVORITES) {
            Timber.e("Favorites Updated ---${event.isFavorite}")
            event.hnLQuizPackageModel?.let {
                updateFavoriteModelInList(it, event.isFavorite)
                homeAndlandPlaceAdapter.notifyDataSetChanged()
            }
        } else if (event.type == AppEvent.UPDATE_HOMELANDFAVORITE) {
            val hnLQuizPackageType = object : TypeToken<HnLQuizPackageModel>() {}.type
            val hnLQuizPackageModel: HnLQuizPackageModel =
                Gson().fromJson(event.data, hnLQuizPackageType)
            favoritesHnlPackagestwo.clear()
//            favoritesHnlPackages.remove(hnLQuizPackageModel)
            /*if (favoritesHnlPackages.size > 0) {
                for ((index, value) in favoritesHnlPackages.withIndex()) {
                    if (value.id == hnLQuizPackageModel.id)
//                        favoritesHnlPackages[index] = hnLQuizPackageModel
                        favoritesHnlPackages.remove(hnLQuizPackageModel)

                }*/
                Timber.e("Adapter Updated ---${event.isFavorite}")
            /*favoritesHnlPackages.forEachIndexed{index,e->
                if(e.id==hnLQuizPackageModel.id){
                }
            }*/
           /* favoritesHnlPackages.forEach {
                if(it.id!=hnLQuizPackageModel.id){
                    favoritesHnlPackagestwo.add(it)
                }
            }
            favoritesHnlPackages=favoritesHnlPackagestwo
            homeLandFavoritesAdapter.setAndUpdateData(favoritesHnlPackages)
            homeLandFavoritesAdapter.notifyDataSetChanged()*/
            if (favoritesHnlPackages.size > 0) {
                val iterator: MutableIterator<HnLQuizPackageModel> = currentUserList.iterator()
                while (iterator.hasNext()) {
                    if ((iterator.next().id == hnLQuizPackageModel.id)) {
                        iterator.remove()
                    }
                }
                Timber.e(" Favorites Currentlist " + currentUserList.size)
                if (currentUserList.size > 1) {
                    val headerCount: Int = currentUserList.size - 1
                    val currentUserName =
                        getString(R.string.favourite_packages)+" ("+headerCount+")"
                    val newHomeListModel = HnLQuizPackageModel(
                        message = currentUserName
                    )
                    currentUserList[0] = newHomeListModel
                    favoritesHnlPackages = ArrayList()
                    favoritesHnlPackages.addAll(currentUserList)
                    favoritesHnlPackages.addAll(otherUserList)
                } else {
                    favoritesHnlPackages = ArrayList()
                    favoritesHnlPackages.addAll(otherUserList)
                }
                homeLandFavoritesAdapter.setAndUpdateData(favoritesHnlPackages)

            }
//            }
        }

    }

    override fun onResume() {
        super.onResume()

        (activity as MainActivity?)?.getIHideProfilePic()?.hideProfilePic(true)
    }

    override fun onStart() {
        super.onStart()
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
    }

    override fun onPause() {
        super.onPause()
        if (preferences != null) {
            preferences.savePriceRange("Low")
        }
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
    }


    override fun onStop() {
        super.onStop()
        (activity as MainActivity?)?.getIHideProfilePic()?.hideProfilePic(false)
        clearFilterData()
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

    override fun onDestroy() {
        super.onDestroy()
        Timber.e("onDestroy() --->")
    }


}





