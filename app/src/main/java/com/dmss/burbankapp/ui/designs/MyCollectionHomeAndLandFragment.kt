package com.dmss.burbankapp.ui.designs

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
import com.dmss.burbankapp.data.model.FavoriteResponseModel
import com.dmss.burbankapp.data.model.HnLQuizPackageModel
import com.dmss.burbankapp.data.model.MyCollectionHnlModel
import com.dmss.burbankapp.data.model.NewHomeListModel
import com.dmss.burbankapp.databinding.FragmentMyCollectionHomeAndLandBinding
import com.dmss.burbankapp.databinding.ProfileWithBadgeBinding
import com.dmss.burbankapp.ui.homeandland.HomeLandFullScreenFragment
import com.dmss.burbankapp.ui.homeandlandplaces.HomeLandPlaceAdapter
import com.dmss.burbankapp.ui.homeandlandregions.HomeAndLandViewModel
import com.dmss.burbankapp.ui.main.MainActivity
import com.dmss.burbankapp.ui.mydisplay.DisplayToolbarViewModel
import com.dmss.burbankapp.ui.view.CustomProgressDialog
import com.dmss.burbankapp.utils.AppConstants
import com.dmss.burbankapp.utils.AppUtils
import com.google.gson.JsonObject
import common.AppController


class MyCollectionHomeAndLandFragment : Fragment(), HomeLandPlaceAdapter.PackageItemClick {

    lateinit var homeAndlandPlaceAdapter: HomeLandPlaceAdapter
    lateinit var myHomeQuizViewModel: NewHomeQuizViewModel
    private var customProgressDialog: CustomProgressDialog? = null
    lateinit var packagesList: ArrayList<HnLQuizPackageModel>
    private var stateId: Int = -1
    private lateinit var linearLayoutManager: LinearLayoutManager

    lateinit var newHomeListModel: NewHomeListModel
    private lateinit var displayToolbarViewModel: DisplayToolbarViewModel
    lateinit var profileWithBadgeBinding: ProfileWithBadgeBinding
    var pagination = 1
    var pastVisibleItems: Int = 0
    var visibleItemCount: Int = 0
    var totalItemCount: Int = 0
    var loading = false
    lateinit var homeLandViewModel: HomeAndLandViewModel

    private lateinit var customSharedPreferences: CustomSharedPreferences

    lateinit var binding: FragmentMyCollectionHomeAndLandBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyCollectionHomeAndLandBinding.inflate(layoutInflater, container, false)
        profileWithBadgeBinding = ProfileWithBadgeBinding.bind(binding.root)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModel()
        initViews()
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
        displayToolbarViewModel.setMainHeader(getString(R.string.house)+","+getString(R.string.and_land))
        binding.ivBack.setOnClickListener {
            activity?.onBackPressed()
        }

        linearLayoutManager = LinearLayoutManager(activity)
        customSharedPreferences = CustomSharedPreferences(requireContext())

        stateId = customSharedPreferences.getStateID()
        packagesList = ArrayList()
        customProgressDialog = CustomProgressDialog(requireContext())

        arguments?.getParcelable<NewHomeListModel>(AppConstants.NEWHOMELISTMODEL)?.let {
            newHomeListModel = it
        }
        var isUserLoggedIn = customSharedPreferences.getUserLogin()
        AppConstants.isHomeLandProfile=false
        if (!isUserLoggedIn) {
            profileWithBadgeBinding.tvFavorites.background = resources.getDrawable(R.drawable.disable_rectangle_new)
            profileWithBadgeBinding.profileNotification.visibility=View.GONE
        }

        newHomeListModel.HouseName?.let {
            binding.tvHouse.text = (newHomeListModel.HouseName + " " + newHomeListModel.HouseSize)

            myHomeQuizViewModel.getMyCollectionHomeAndLand(stateId, it)
        }
        binding.recyclerPlaces.addItemDecoration(
            DividerItemDecoration(
                activity,
                LinearLayoutManager.VERTICAL
            )
        )
        binding.recyclerPlaces.apply {
            homeAndlandPlaceAdapter = HomeLandPlaceAdapter(
                false,
                context,
                requireActivity(),
                packagesList, this@MyCollectionHomeAndLandFragment
            )
            layoutManager = linearLayoutManager
        }
        binding.recyclerPlaces.adapter = homeAndlandPlaceAdapter

    }

    private fun setupViewModel() {
        myHomeQuizViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(
                ApiHelperImpl(RetrofitBuilder.apiService),
                DatabaseHelperImpl(DatabaseBuilder.getInstance(AppController.getInstance()))
            )
        ).get(NewHomeQuizViewModel::class.java)
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
        homeLandViewModel.getFavorite().observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    customProgressDialog?.dismissProgress()
                    (activity as MainActivity).loadUserFavoritesHomeLand()
                    var favoriteResponseModel: FavoriteResponseModel? = it.data
                   /* if (favoritesHnlPackages.size == 0 && packagesList.size ==0){
                        binding.recyclerPlaces.visibility = View.GONE
                        binding.recyclerPlaces.visibility = View.GONE
                        binding.tvNoFavorites.visibility = View.VISIBLE
                        binding.tvNoFavorites.text = getString(R.string.no_saved_packages)
                    }*/
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
        myHomeQuizViewModel.getMyCollectionHnlLiveData().observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    customProgressDialog?.dismissProgress()
                    var regionsData: MyCollectionHnlModel? = it.data

                    if (regionsData != null && regionsData.hnlQuizList.size > 0) {
                        loading = true
                        pagination++
                        if (pagination == 1) {
                            packagesList.clear()
                        }
                        packagesList.addAll(regionsData.hnlQuizList)
                        if (packagesList.size > 0) {
                            homeAndlandPlaceAdapter.notifyDataSetChanged()
                        } else {
                            /*NO DATA*/
                        }

                    } else {
                        loading = false
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

    override fun selectedPackageItem(hnLQuizPackageModel: HnLQuizPackageModel) {
        val bundle = Bundle()
        bundle.putParcelable("HNLQUIZMODEL", hnLQuizPackageModel)
        bundle.putBoolean("ISFROMFAVORITE", false)
        bundle.putBoolean("ISCURRENTUSER", true)
        bundle.putString("classfrom", MyCollectionHomeAndLandFragment::class.simpleName)
        loadFragment(HomeLandFullScreenFragment(), bundle, true)
    }

    fun loadFragment(fragment: Fragment, bundle: Bundle, isNotFullScreen: Boolean) {
        val transaction: FragmentTransaction = parentFragmentManager.beginTransaction()
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

    /*override fun selectFavoriteItemClick(
        hnLQuizPackageModel: HnLQuizPackageModel,
        isFavorite: Boolean
    ) {

    }*/
    override fun selectFavoriteItemClick(
        hnLQuizPackageModel: HnLQuizPackageModel,
        isFavorite: Boolean
    ) {
        val jsonObject = JsonObject()
        jsonObject.addProperty("TypeId", 1)
        jsonObject.addProperty("UserId", customSharedPreferences.getUserId())
        jsonObject.addProperty("HouseId", hnLQuizPackageModel.packageId)
        jsonObject.addProperty("StateId", customSharedPreferences.getStateID())
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

}