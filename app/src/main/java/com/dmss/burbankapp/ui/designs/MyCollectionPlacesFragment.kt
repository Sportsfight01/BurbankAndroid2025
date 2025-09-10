package com.dmss.burbankapp.ui.designs

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
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
import com.dmss.burbankapp.data.model.*
import com.dmss.burbankapp.databinding.FragmentMyCollectionPlacesBinding
import com.dmss.burbankapp.databinding.InclueToolDesignBinding
import com.dmss.burbankapp.databinding.ProfileWithBadgeBinding
import com.dmss.burbankapp.ui.adapters.CollectionsPlaceAdapter
import com.dmss.burbankapp.ui.designs.mycollectionfavorites.MyCollectionFavoritesAdapter
import com.dmss.burbankapp.ui.main.MainActivity
import com.dmss.burbankapp.ui.mydisplay.DisplayToolbarViewModel
import com.dmss.burbankapp.ui.view.BreadCrumbAdapter
import com.dmss.burbankapp.ui.view.CustomProgressDialog
import com.dmss.burbankapp.utils.AppConstants
import com.dmss.burbankapp.utils.AppUtils
import com.dmss.burbankapp.utils.customviews.AppEvent
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import common.AppController
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber


class MyCollectionPlacesFragment : Fragment(), CollectionsPlaceAdapter.PackageItemClick,
    MyCollectionFavoritesAdapter.FavoritePackageItemClick,
    BreadCrumbAdapter.BreadcrumbItemClickListener {
    lateinit var myHomeQuizViewModel: NewHomeQuizViewModel
    lateinit var binding: FragmentMyCollectionPlacesBinding
    private var stateId: Int = -1
    private var userID: Int = -1
    private var newHomesList: ArrayList<NewHomeListModel> = ArrayList()
    private var favouritesHomesList: ArrayList<NewHomeListModel> = ArrayList()

    private lateinit var collectionsPlaceAdapter: CollectionsPlaceAdapter
    private lateinit var myCollectionFavoritesAdapter: MyCollectionFavoritesAdapter
    lateinit var mainActivity: MainActivity
    private var favoriteButtonTap = true
    lateinit var userInfoModel: UserInfoModel
    private var favoritesList: ArrayList<NewHomeListModel> = ArrayList()

    private var isMyCollectionFirstApi = false
    private var SELECT_FAVORITE: Boolean = false
    private var customProgressDialog: CustomProgressDialog? = null
    lateinit var toolBinding: InclueToolDesignBinding
    private var newHomesNextFeaturesModel: NewHomesNextFeaturesModel? = null
    private lateinit var displayToolbarViewModel: DisplayToolbarViewModel
    lateinit var profileWithBadgeBinding: ProfileWithBadgeBinding


    var currentUserList: ArrayList<NewHomeListModel> = ArrayList()
    var otherUserList: ArrayList<NewHomeListModel> = ArrayList()

    private lateinit var customSharedPreferences: CustomSharedPreferences
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyCollectionPlacesBinding.inflate(inflater, container, false)
        toolBinding = InclueToolDesignBinding.bind(binding.root)
        profileWithBadgeBinding = ProfileWithBadgeBinding.bind(binding.root)

        return binding.root
    }

    override fun onAttach(context: Context) {
        mainActivity = activity as MainActivity
        super.onAttach(context)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupViewModel()
        initViews()
    }

    private fun setupViewModel() {
        myHomeQuizViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(
                ApiHelperImpl(RetrofitBuilder.apiService),
                DatabaseHelperImpl(DatabaseBuilder.getInstance(AppController.getInstance()))
            )
        ).get(NewHomeQuizViewModel::class.java)

        setupObserver()

    }

    private fun setupObserver() {

        myHomeQuizViewModel.setRecentSearchMyCollectionData().observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    customProgressDialog?.dismissProgress()
                    var regionsData: MyCollectionRecentSearchModel? = it.data
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
                    (activity as MainActivity).loadUserFavoritesMyCollection()

                    var favoriteResponseModel: FavoriteResponseModel? = it.data
                    favoriteResponseModel?.message?.let { it1 ->
                        AppUtils.showCustomCenterToast(
                            requireContext(),
                            it1
                        )
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


        myHomeQuizViewModel.getFavoritesListLiveData().observe(viewLifecycleOwner, Observer
        {
            when (it.status) {
                Status.SUCCESS -> {
                    customProgressDialog?.dismissProgress()
                    var houselistData: ArrayList<HouseListCollectionModel>? = it.data
                    if (houselistData != null && houselistData.size > 0) {
                        binding.recyclerFavorites.visibility = View.VISIBLE
                        binding.tvNoFavorites.visibility = View.GONE
                        binding.recyclerPlaces.visibility = View.GONE
                        favoritesList.clear()
                        currentUserList.clear()
                        otherUserList.clear()

                        for (houselistModel in houselistData) {

                            if (houselistModel.fetchJsonRecentSearches.homeAndLandDTOsList != null) {
                                binding.tvNoFavorites.visibility = View.GONE
                                binding.recyclerFavorites.visibility = View.VISIBLE

                                if (houselistModel.fetchJsonRecentSearches.homeAndLandDTOsList!!.size > 0) {

                                    if (houselistModel.fetchJsonRecentSearches.emailId == userInfoModel.Email) {
                                        var currentUserName=
                                            "${getString(R.string.favourite_list) } (${houselistModel.fetchJsonRecentSearches.homeAndLandDTOsList?.size})"

                                        val hnLQuizPackageModel = NewHomeListModel()
                                        hnLQuizPackageModel.message = currentUserName
                                        currentUserList = ArrayList()
                                        currentUserList.add(hnLQuizPackageModel)
                                        for (model: NewHomeListModel in houselistModel.fetchJsonRecentSearches.homeAndLandDTOsList!!) {
                                            model.currentUserName =
                                                houselistModel.fetchJsonRecentSearches.FullName?.trim()
                                            currentUserList.add(model)
                                            favouritesHomesList.add(model)

                                        }

                                    }
                                    // commented other user favourites by durga
                                    /*else {
                                        var currentUserName =
                                            "${houselistModel.fetchJsonRecentSearches.FullName}'s Favourites List (${houselistModel.fetchJsonRecentSearches.homeAndLandDTOsList?.size})"
                                        val hnLQuizPackageModel = NewHomeListModel()
                                        hnLQuizPackageModel.message = currentUserName

                                        otherUserList = ArrayList()
                                        otherUserList.add(hnLQuizPackageModel)
                                        otherUserList.addAll(houselistModel.fetchJsonRecentSearches.homeAndLandDTOsList!!)

                                    }
*/
                                    if (currentUserList.size > 1) {
//                                        favoritesList = ArrayList()
                                        if(!favoritesList.containsAll(currentUserList)) {
                                            favoritesList.addAll(currentUserList)
                                        }
                                        if(!favoritesList.containsAll(otherUserList)) {
                                            favoritesList.addAll(otherUserList)
                                        }
                                    }
                                    else {
//                                        favoritesList = ArrayList()
//                                        favoritesList.addAll(otherUserList)
                                        if(!favoritesList.containsAll(otherUserList)) {
                                            favoritesList.addAll(otherUserList)
                                        }
                                    }


                                    /*houselistModel.fetchJsonRecentSearches.homeAndLandDTOsList?.let {
                                        favoritesList.addAll(houselistModel.fetchJsonRecentSearches.homeAndLandDTOsList!!)
                                        Timber.e("Favorites List is ---%s", favoritesList.size)
                                    }*/
                                    binding.tvNoFavorites.visibility = View.GONE
                                } else {
                                    binding.recyclerFavorites.visibility = View.GONE
                                    binding.recyclerPlaces.visibility = View.GONE
                                    binding.tvNoFavorites.visibility = View.VISIBLE
                                }
                            } else {
                                binding.recyclerFavorites.visibility = View.GONE
                                binding.recyclerPlaces.visibility = View.GONE
                                binding.tvNoFavorites.visibility = View.VISIBLE
                            }
                        }
                       /* binding.recyclerFavorites.addItemDecoration(
                            DividerItemDecoration(
                                activity,
                                LinearLayoutManager.VERTICAL
                            )
                        )*/
                        binding.recyclerFavorites.apply {
                            myCollectionFavoritesAdapter =
                                MyCollectionFavoritesAdapter(
                                    requireContext(),
                                    favoritesList, this@MyCollectionPlacesFragment
                                )
                            adapter = myCollectionFavoritesAdapter
                        }


                    } else {
                        binding.recyclerFavorites.visibility = View.GONE
                        binding.recyclerPlaces.visibility = View.GONE
                        binding.tvNoFavorites.visibility = View.VISIBLE
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

        myHomeQuizViewModel.getNewHomeNextQuestionLiveData()
            .observe(viewLifecycleOwner, Observer
            {
                when (it.status) {
                    Status.SUCCESS -> {
                        customProgressDialog?.dismissProgress()
                        binding.recyclerPlaces.visibility = View.VISIBLE
                        binding.recyclerFavorites.visibility = View.GONE
                        binding.tvNoFavorites.visibility = View.GONE
                        newHomesList = ArrayList()
                        var newHomeNextFeatureResponseModel: NewHomeNextFeatureResponseModel? =
                            it.data
                        if (newHomeNextFeatureResponseModel != null) {
                            if (newHomeNextFeatureResponseModel.status) {
                                if (newHomeNextFeatureResponseModel.newHomesNextFeatures?.newHomesList != null) {

                                    if (newHomeNextFeatureResponseModel.newHomesNextFeatures!!.newHomesList!!.size > 0) {
                                        newHomesList.addAll(
                                            newHomeNextFeatureResponseModel.newHomesNextFeatures!!.newHomesList!!
                                        )
                                        toolBinding.tvTotalDesigns.text="TOTAL "+newHomesList.size+" DESIGNS"
                                        binding.recyclerPlaces.apply {
                                            collectionsPlaceAdapter =
                                                CollectionsPlaceAdapter(
                                                    requireContext(),
                                                    requireActivity(),
                                                    newHomesList,
                                                    this@MyCollectionPlacesFragment
                                                )
                                            adapter = collectionsPlaceAdapter
                                        }

                                        collectionsPlaceAdapter.notifyDataSetChanged()
                                    }

                                }

                            }

                            //SET MY COLLECTION RECENT SEARCH
                            setRecentSearchMyCollection()
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

    private fun setRecentSearchMyCollection() {
        val newHomeJsonArrayList = ArrayList<NewHomeJsonObject>()
        for ((_, value) in AppConstants.newHomeHashMap) {
            newHomeJsonArrayList.addAll(value)
        }
        if (newHomesList.size > 0) {
            var resultCountObject =
                NewHomeJsonObject("resultsCount", "", newHomesList.size.toString())
            newHomeJsonArrayList.add(resultCountObject)
        }

        var obj: SetRecentMyCollectionRequest = SetRecentMyCollectionRequest(
            stateId, 2, userID, newHomeJsonArrayList
        )

        myHomeQuizViewModel.setRecentSearchData(obj)
    }

    private fun loadNextQuestionFeature(
    ) {
        binding.recyclerFavorites.visibility = View.GONE
        binding.recyclerPlaces.visibility = View.VISIBLE
        val newHomeJsonArrayList = ArrayList<NewHomeJsonObject>()
        for ((_, value) in AppConstants.newHomeHashMap) {
            newHomeJsonArrayList.addAll(value)
        }


        var mMyCollectionQuizQuestionRequest: MyCollectionQuizQuestionRequest?
        if (newHomeJsonArrayList.size > 0) {

            var minLotWidth: Float = 0f

            /*for (newHomeJsonObject in newHomeJsonArrayList) {
                if (newHomeJsonObject.feature == "Lot Width") {
                    minLotWidth = newHomeJsonObject.minLotWidth.toFloat()
                }

            }*/


            mMyCollectionQuizQuestionRequest = MyCollectionQuizQuestionRequest(
                stateId,
                includePackages = 1,
                sortByPrice = 0,
                userId = userID,
               /* minLotWidth = minLotWidth,*/
                newHomeJsonList = newHomeJsonArrayList

            )
        } else {
            mMyCollectionQuizQuestionRequest = MyCollectionQuizQuestionRequest(
                stateId,
                includePackages = 1,
                sortByPrice = 0,
                userId = userID,
                /*minLotWidth = 0f,*/
                newHomeJsonList = null
            )
        }

        myHomeQuizViewModel.fetchNewHomesCollectionsNextFeatureNext(
            mMyCollectionQuizQuestionRequest
        )
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
        displayToolbarViewModel.favouritesCount.observe(viewLifecycleOwner){
            profileWithBadgeBinding.profileNotification.text = it.toString()
            profileNotificationCountView(it)

        }
        toolBinding.ivBack.setOnClickListener {
            activity?.onBackPressed()

        }
        toolBinding.tvReset.setOnClickListener {

            val intent=  Intent(
                AppController.getInstance(),
                MainActivity::class.java
            ).putExtra("SELECTED ITEM", 0)
            startActivity(intent)
            activity!!.finish()

        }

        toolBinding.customRecyclerview.initView(requireContext())
        //GETTING  SAVED  LOCAL DATA
        customProgressDialog = CustomProgressDialog(requireContext())
        customSharedPreferences = CustomSharedPreferences(requireContext())
        stateId = customSharedPreferences.getStateID()
        userID = customSharedPreferences.getUserId()
        userInfoModel = customSharedPreferences.getUserInfoModel()

        arguments?.getBoolean(AppConstants.SELECT_FAVORITE, false)?.let {
            SELECT_FAVORITE = it
        }
        arguments?.getParcelable<NewHomesNextFeaturesModel>(AppConstants.NEWHOMENEXTQUESTIONMODEL)
            ?.let {
                newHomesNextFeaturesModel = it
            }
        var isUserLoggedIn = customSharedPreferences.getUserLogin()
        if (!isUserLoggedIn) {
            profileWithBadgeBinding.tvFavorites.background = resources.getDrawable(R.drawable.disable_rectangle_new)
            profileWithBadgeBinding.profileNotification.visibility=View.GONE
        }
        if (SELECT_FAVORITE) {
            profileWithBadgeBinding.tvFavorites.visibility = View.VISIBLE
            toolBinding.customRecyclerview.visibility = View.GONE
            toolBinding.tvTool.visibility = View.VISIBLE
            toolBinding.tvTool.text = ("Favourite Designs")
            isMyCollectionFirstApi = true
            toolBinding.tvReset.visibility=View.GONE
            toolBinding.tvTotalDesigns.visibility=View.GONE
            binding.favHeader.visibility=View.VISIBLE
          /*  toolBinding.tvFavorites.background =
                ContextCompat.getDrawable(
                    AppController.getInstance(),
                    R.drawable.rectangel_shape
                )
            toolBinding.tvFavorites.setTextColor(
                ContextCompat.getColor(
                    AppController.getInstance(),
                    R.color.appColor
                )
            )*/
            fetchAllFavoritesApiCall()
        } else {
            binding.favHeader.visibility=View.GONE
            toolBinding.tvReset.visibility=View.VISIBLE
            toolBinding.tvTotalDesigns.visibility=View.VISIBLE
            profileWithBadgeBinding.tvFavorites.visibility = View.VISIBLE
            binding.tvNoFavorites.visibility = View.GONE
            toolBinding.tvTool.visibility = View.GONE
            toolBinding.customRecyclerview.visibility = View.VISIBLE
            setToolbarBreadCrumb()
            loadNextQuestionFeature()
        }
        newHomesList = ArrayList()

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
        //showMainList()
        toolBinding.ivBack.setOnClickListener {
            activity?.onBackPressed()
        }
        profileWithBadgeBinding.tvFavorites.setOnClickListener {
            var isUserLoggedIn = customSharedPreferences.getUserLogin()
            if (isUserLoggedIn) {
               /* if (!isMyCollectionFirstApi) {

                    favoritesList.clear()
                    currentUserList.clear()
                    otherUserList.clear()

                    toolBinding.customRecyclerview.visibility = View.GONE
                    toolBinding.tvTool.visibility = View.VISIBLE
                    toolBinding.tvTool.text = ("Favourite Designs")
                    isMyCollectionFirstApi = true
                    toolBinding.tvFavorites.background =
                        ContextCompat.getDrawable(
                            AppController.getInstance(),
                            R.drawable.rectangel_shape
                        )
                    toolBinding.tvFavorites.setTextColor(
                        ContextCompat.getColor(
                            AppController.getInstance(),
                            R.color.appColor
                        )
                    )
                    fetchAllFavoritesApiCall()

                } else {
                    binding.tvNoFavorites.visibility = View.GONE
                    toolBinding.tvTool.visibility = View.GONE
                    toolBinding.customRecyclerview.visibility = View.VISIBLE
                    setToolbarBreadCrumb()
                    isMyCollectionFirstApi = false

                    toolBinding.tvFavorites.background =
                        ContextCompat.getDrawable(
                            AppController.getInstance(),
                            R.drawable.rectangel_line
                        )
                    toolBinding.tvFavorites.setTextColor(
                        ContextCompat.getColor(
                            AppController.getInstance(),
                            R.color.white
                        )
                    )
                    loadNextQuestionFeature()
                }*/
                (activity as MainActivity).showProfileDialog(activity!!)

            } else {
                AppUtils.showPleaseLoginDialog(
                    requireContext(),
                    requireActivity(),
                    getString(R.string.Please_login_to_view_edit_profile)
                )
            }
        }

    }


    fun loadFragment(fragment: Fragment, bundle: Bundle) {
        val transaction: FragmentTransaction =
            activity?.supportFragmentManager!!.beginTransaction()
        if (fragment.isAdded) {
            return
        }

        transaction.add(R.id.fl_content, fragment)
        fragment.arguments = bundle
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun showEditDialog() {
        val fm: FragmentManager = requireActivity().supportFragmentManager
        var fragment = MyCollectionsBottomSheetFragment().newInstance("Hello");
        fragment?.show(fm, "fragment_edit_name")
    }

    override fun selectedPackageItem(newHomeListModel: NewHomeListModel,index:Int) {
        var bundle = Bundle()
        bundle.putParcelable(AppConstants.NEWHOMELISTMODEL, newHomeListModel)
        bundle.putParcelableArrayList(AppConstants.NEWHOMELISTMODELLIST, newHomesList)
        bundle.putBoolean("isFavorite", false)
         bundle.putInt("index", index)
        loadFragment(MycollectionDetailsFragment(), bundle)
    }

    override fun selectedFavoriteDetailItem(newHomeListModel: NewHomeListModel,index:Int) {
       /* var favouriteList: ArrayList<NewHomeListModel> = ArrayList()
        favouriteList.addAll(currentUserList)*/
        var bundle = Bundle()
        bundle.putParcelable(AppConstants.NEWHOMELISTMODEL, newHomeListModel)
        bundle.putBoolean("isFavorite", true)
        bundle.putInt("index", index.minus(1))
        bundle.putParcelableArrayList(AppConstants.NEWHOMELISTMODELLIST, favouritesHomesList)
        loadFragment(MycollectionDetailsFragment(), bundle)

    }
    override fun selectFavoriteItemClick(
        newHomeListModel: NewHomeListModel,
        isFavorite: Boolean
    ) {
        val jsonObject = JsonObject()
        jsonObject.addProperty("TypeId", 2)
        jsonObject.addProperty("UserId", customSharedPreferences.getUserId())
        jsonObject.addProperty("HouseId", newHomeListModel.houseIdLandBank)
        jsonObject.addProperty("StateId", customSharedPreferences.getStateID())
        jsonObject.addProperty("isfavourite", isFavorite)
        myHomeQuizViewModel.setFavorite(jsonObject)


        updateFavoriteModelInList(newHomeListModel, isFavorite)

    }

    private fun updateFavoriteModelInList(
        newHomeListModel: NewHomeListModel,
        isFavorite: Boolean
    ) {
        if (newHomesList.size > 0) {
            for ((index, value) in newHomesList.withIndex()) {
                if (value.Id == newHomeListModel.Id)
                    newHomesList[index].IsFav = isFavorite
            }
        }

    }


    private fun fetchAllFavoritesApiCall() {
        binding.recyclerFavorites.visibility = View.VISIBLE
        binding.recyclerPlaces.visibility = View.GONE
        val jsonObject = JsonObject()
        jsonObject.addProperty("TypeId", 2)
        jsonObject.addProperty("UserId", customSharedPreferences.getUserId())
        jsonObject.addProperty("StateId", customSharedPreferences.getStateID())

        myHomeQuizViewModel.getAllFavorites(jsonObject)
    }



    override fun unFavoriteItemClick(
        newHomeListModel: NewHomeListModel
    ) {
        if (favoritesList.size > 0) {
            val jsonObject = JsonObject()
            jsonObject.addProperty("TypeId", 2)
            jsonObject.addProperty("UserId", customSharedPreferences.getUserId())
            jsonObject.addProperty("HouseId", newHomeListModel.houseIdLandBank)
            jsonObject.addProperty("StateId", customSharedPreferences.getStateID())
            jsonObject.addProperty("isfavourite", false)
            myHomeQuizViewModel.setFavorite(jsonObject)

            val iterator: MutableIterator<NewHomeListModel> = currentUserList.iterator()
            while (iterator.hasNext()) {
                if ((iterator.next() == newHomeListModel)) {
                    iterator.remove()
                }
            }
            Timber.e(" Favorites CurrentList %s", currentUserList.size)
            if (currentUserList.size > 1) {
                val headerCount: Int = currentUserList.size - 1
                val currentUserName =
                    "${getString(R.string.favourite_list) } (${headerCount})"
                val newHomeListModel = NewHomeListModel(
                    message = currentUserName
                )
                currentUserList[0] = newHomeListModel
                favoritesList = ArrayList()
                favoritesList.addAll(currentUserList)
                favoritesList.addAll(otherUserList)
            } else {
                favoritesList = ArrayList()
                favoritesList.addAll(otherUserList)
            }
            Timber.e("Favorites list %s", favoritesList.size)
            if (favoritesList.size == 0) {
                binding.recyclerPlaces.visibility = View.GONE
                binding.recyclerFavorites.visibility = View.GONE
                binding.tvNoFavorites.visibility = View.VISIBLE
            } else {
                binding.recyclerPlaces.visibility = View.GONE
                binding.tvNoFavorites.visibility = View.GONE
                binding.recyclerFavorites.visibility = View.VISIBLE
                myCollectionFavoritesAdapter.setAndUpdateData(favoritesList)

            }
        }


    }

    private fun setToolbarBreadCrumb() {
        var arrayList: java.util.ArrayList<BreadcrumbModel> = java.util.ArrayList()

        if (AppConstants.breadCrumbMyCollection.keys.size > 0) {
            val isLotWidthAvailable = AppConstants.breadCrumbMyCollection.any { it.key == "Lot Width" }
            if (isLotWidthAvailable) AppConstants.breadCrumbMyCollection.remove("MyCollectionLot")
            toolBinding.tvTool.visibility = View.GONE
            toolBinding.customRecyclerview.visibility = View.VISIBLE
            val myVeryOwnIterator: Iterator<*> =
                AppConstants.breadCrumbMyCollection.keys.iterator()

            while (myVeryOwnIterator.hasNext()) {
                val key = myVeryOwnIterator.next() as String
                var value = AppConstants.breadCrumbMyCollection[key]
                if (value != null) {
                    if (value.breadCrumbTitle != "") {
                        if (key == "Lot Width") {
                            arrayList.add(0,
                                BreadcrumbModel(
                                    value.breadCrumbTitle,
                                    value.titleFragment,
                                    false

                                )
                            )
                        }else{
                            arrayList.add(
                                BreadcrumbModel(
                                    value.breadCrumbTitle,
                                    value.titleFragment,
                                    false
                                )
                            )
                        }
                    }
                }

            }
        } else {
            toolBinding.customRecyclerview.visibility = View.GONE
            toolBinding.tvTool.visibility = View.VISIBLE
        }
        val list =
        toolBinding.customRecyclerview.setData(arrayList, this)
    }

    override fun breadCrumb(breadCrumb: BreadcrumbModel) {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onAppEvent(event: AppEvent) {
        if (event.type == AppEvent.UPDATE_FAVORITES_HOMEDESIGN) {
            event.newHomeListModel?.let {
                updateFavoriteModelInList(it, event.isFavorite)
                collectionsPlaceAdapter.notifyDataSetChanged()
            }

        } else if (event.type == AppEvent.UPDATE_HOMEDESIGNFAVORITE) {
            val hnLQuizPackageType = object : TypeToken<NewHomeListModel>() {}.type
            val hnLQuizPackageModel: NewHomeListModel =
                Gson().fromJson(event.data, hnLQuizPackageType)
            if (favoritesList.size > 0) {
                val iterator: MutableIterator<NewHomeListModel> = currentUserList.iterator()
                while (iterator.hasNext()) {
                    if ((iterator.next().Id == hnLQuizPackageModel.Id)) {
                        iterator.remove()
                    }
                }
                Timber.e(" Favorites Currentlist " + currentUserList.size)
                if (currentUserList.size > 1) {
                    val headerCount: Int = currentUserList.size - 1
                    val currentUserName =
                        "${getString(R.string.favourite_list) }(${headerCount})"
                    val newHomeListModel = NewHomeListModel(
                        message = currentUserName
                    )
                    currentUserList[0] = newHomeListModel
                    favoritesList = ArrayList()
                    favoritesList.addAll(currentUserList)
                    favoritesList.addAll(otherUserList)
                } else {
                    favoritesList = ArrayList()
                    favoritesList.addAll(otherUserList)
                }
                Timber.e("Favorites list " + favoritesList.size)
                if (favoritesList.size == 0) {
                    binding.recyclerPlaces.visibility = View.GONE
                    binding.recyclerFavorites.visibility = View.GONE
                    binding.tvNoFavorites.visibility = View.VISIBLE
                } else {
                    binding.recyclerPlaces.visibility = View.GONE
                    binding.tvNoFavorites.visibility = View.GONE
                    binding.recyclerFavorites.visibility = View.VISIBLE
                    myCollectionFavoritesAdapter.setAndUpdateData(favoritesList)

                }
            }
        }

    }

    override fun onStart() {
        super.onStart()
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
    }

    override fun onPause() {
        super.onPause()
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity?)?.getIHideProfilePic()?.hideProfilePic(true)
    }

    override fun onStop() {
        super.onStop()
        (activity as MainActivity?)?.getIHideProfilePic()?.hideProfilePic(false)

    }


}
