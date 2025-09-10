package com.dmss.burbankapp.ui.cluster

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
import com.dmss.burbankapp.databinding.FragmentHomeLandClusterBottomSheetBinding
import com.dmss.burbankapp.ui.homeandland.HomeLandFullScreenFragment
import com.dmss.burbankapp.ui.homeandlandregions.HomeAndLandViewModel
import com.dmss.burbankapp.ui.view.CustomProgressDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.JsonObject
import common.AppController

class HomeAndLandClusterBottomSheetDialog(var arrayList: ArrayList<HnlClusterItem>) :
    BottomSheetDialogFragment(),
    HomeLandClusterAdapter.PackageItemClick {
    val TAG = "HomeLandFilterBottomSheetFragment"

    private lateinit var linearLayoutManager: LinearLayoutManager

    lateinit var preferences: CustomSharedPreferences
    lateinit var homeLandViewModel: HomeAndLandViewModel
    lateinit var binding: FragmentHomeLandClusterBottomSheetBinding
    lateinit var homeAndlandPlaceAdapter: HomeLandClusterAdapter
    private var customProgressDialog: CustomProgressDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeLandClusterBottomSheetBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initView()
        setUpViewModel()


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
        homeLandViewModel.getFavorite().observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    customProgressDialog?.dismissProgress()

                    var favoriteResponseModel: FavoriteResponseModel? = it.data

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

    }

    private fun initView() {
        preferences = CustomSharedPreferences(AppController.getInstance())
        customProgressDialog = CustomProgressDialog(requireContext())

        binding.close.setOnClickListener {
            dialog!!.dismiss()
        }

        if (arrayList.size > 0) {

            if (arrayList.size == 1) {
                binding.tvHouseCount.text = ("${arrayList.size} House")
                binding.tvHouse.text="Selected house"
            } else {
                binding.tvHouseCount.text = ("${arrayList.size} Houses")
                binding.tvHouse.text="Houses in group"

            }

            linearLayoutManager = LinearLayoutManager(activity)
            binding.recyclerView.apply {
                homeAndlandPlaceAdapter = HomeLandClusterAdapter(
                    context,requireActivity(),
                    arrayList, this@HomeAndLandClusterBottomSheetDialog
                )
                binding.recyclerView.addItemDecoration(
                    DividerItemDecoration(
                        activity,
                        LinearLayoutManager.VERTICAL
                    )
                )
                layoutManager = linearLayoutManager
            }
            binding.recyclerView.adapter = homeAndlandPlaceAdapter
        }
    }

    override fun onDetach() {
        super.onDetach()

    }


    interface ItemClickListener {
        fun onItemClick(item: String?)
    }

    override fun selectedPackageItem(hnLQuizPackageModel: HnLQuizPackageModel) {
        dismiss()
        val bundle = Bundle()
        /*val fragment = HomeLandFullScreenFragment.newInstance(hnLQuizPackageModel,
            isFromFavorites = false,
            isCurrentUser = true
        )*/
        bundle.putParcelable("HNLQUIZMODEL",hnLQuizPackageModel)
        bundle.putBoolean("ISFROMFAVORITE",false)
        bundle.putBoolean("ISCURRENTUSER",true)
        loadFragment(HomeLandFullScreenFragment(), bundle, true)
    }

    fun loadFragment(fragment: Fragment, bundle: Bundle, isNotFullScreen: Boolean) {
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
        if (arrayList.size > 0) {
            for ((index, value) in arrayList.withIndex()) {
                if (value.hnLQuizPackageModel.id == hnLQuizPackageModel.id)
                    arrayList[index].hnLQuizPackageModel.isFav = isFavorite
            }
        }

    }

}