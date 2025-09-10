package com.dmss.burbankapp.ui.mydisplay

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
import com.dmss.burbankapp.data.model.FavoriteResponseModel
import com.dmss.burbankapp.data.model.HnLQuizPackageModel
import com.dmss.burbankapp.data.model.MyCollectionHnlModel
import com.dmss.burbankapp.databinding.FragmentMyDisplayHomeAndLandBinding
import com.dmss.burbankapp.databinding.HomelandToolBinding
import com.dmss.burbankapp.databinding.ProfileWithBadgeBinding
import com.dmss.burbankapp.ui.designs.NewHomeQuizViewModel
import com.dmss.burbankapp.ui.homeandland.HomeLandFullScreenFragment
import com.dmss.burbankapp.ui.homeandlandplaces.HomeLandPlaceAdapter
import com.dmss.burbankapp.ui.homeandlandregions.HomeAndLandViewModel
import com.dmss.burbankapp.ui.main.MainActivity
import com.dmss.burbankapp.ui.view.CustomProgressDialog
import com.dmss.burbankapp.utils.AppConstants
import com.google.gson.JsonObject
import common.AppController


class MyDisplayHomeAndLandFragment : Fragment(), HomeLandPlaceAdapter.PackageItemClick  {
    lateinit var binding: FragmentMyDisplayHomeAndLandBinding

    lateinit var homeAndlandPlaceAdapter: HomeLandPlaceAdapter
    lateinit var myHomeQuizViewModel: NewHomeQuizViewModel
    private var customProgressDialog: CustomProgressDialog? = null
    lateinit var packagesList: ArrayList<HnLQuizPackageModel>
    private var stateId: Int = -1
    private lateinit var linearLayoutManager: LinearLayoutManager

    lateinit var newHomeListModel: DisplayDetailStaticModel

    var pagination = 1
    var pastVisibleItems: Int = 0
    var visibleItemCount: Int = 0
    var totalItemCount: Int = 0
    var loading = false
    private lateinit var displayToolbarViewModel: DisplayToolbarViewModel

    private lateinit var customSharedPreferences: CustomSharedPreferences
    lateinit var homeLandViewModel: HomeAndLandViewModel
//    lateinit var profileWithBadgeBinding: ProfileWithBadgeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyDisplayHomeAndLandBinding.inflate(layoutInflater, container, false)
//        profileWithBadgeBinding = ProfileWithBadgeBinding.bind(binding.root)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModel()
        initViews()
    }
    private fun profileNotificationCountView(notificationCount:Int){
        if(notificationCount==0){
//            profileWithBadgeBinding.profileNotification.visibility=View.GONE
        }else{
//            profileWithBadgeBinding.profileNotification.visibility=View.VISIBLE
        }
    }
    private fun initViews() {
        displayToolbarViewModel = ViewModelProviders.of(requireActivity())[DisplayToolbarViewModel::class.java]
        displayToolbarViewModel.favouritesCount.observe(viewLifecycleOwner){
//            profileNotificationCountView(it)

        }
        displayToolbarViewModel.setMainHeaderForDisplayHomes(getString(R.string.house)+","+getString(R.string.and_land))
        AppConstants.MAINHEADER=getString(R.string.house)+","+getString(R.string.and_land)

        binding.ivBack.setOnClickListener {
            displayToolbarViewModel.setMainHeaderForDisplayHomes(getString(R.string.display)+","+getString(R.string.homes))
            requireActivity().onBackPressed()

        }

        linearLayoutManager = LinearLayoutManager(activity)
        customSharedPreferences = CustomSharedPreferences(requireContext())

        stateId = customSharedPreferences.getStateID()
        packagesList = ArrayList()
        customProgressDialog = CustomProgressDialog(requireContext())

        arguments?.getParcelable<DisplayDetailStaticModel>(AppConstants.NEWHOMELISTMODEL)?.let {
            newHomeListModel = it
            AppConstants.displayHomeSubHeader=it.HouseName
        }


        newHomeListModel.HouseName.let {
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
                packagesList, this@MyDisplayHomeAndLandFragment
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
        bundle.putString("classfrom", MyDisplayHomeAndLandFragment::class.simpleName)
        loadFragment(HomeLandFullScreenFragment(), bundle, false)
    }


    fun loadFragment(fragment: Fragment, bundle: Bundle, isNotFullScreen: Boolean) {
        // load fragment
        val transaction: FragmentTransaction = parentFragmentManager.beginTransaction()
        fragment.arguments = bundle
        if (fragment.isAdded) return
        transaction.add(R.id.fragment_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()

    }


   /* override fun selectFavoriteItemClick(
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