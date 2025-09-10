package com.dmss.burbankapp.ui.mydisplay.favoritesDisplays

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.dmss.burbankapp.data.model.HnLQuizPackageModel
import com.dmss.burbankapp.data.model.MyCollectionHnlModel
import com.dmss.burbankapp.databinding.FragmentFavoriteHomeAndLandBinding
import com.dmss.burbankapp.ui.designs.NewHomeQuizViewModel
import com.dmss.burbankapp.ui.homeandland.HomeLandFullScreenFragment
import com.dmss.burbankapp.ui.homeandlandplaces.HomeLandPlaceAdapter
import com.dmss.burbankapp.ui.view.CustomProgressDialog
import com.dmss.burbankapp.utils.AppConstants
import common.AppController

class FavoriteHomeAndLandFragment : Fragment(), HomeLandPlaceAdapter.PackageItemClick {
    lateinit var binding: FragmentFavoriteHomeAndLandBinding

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

    private lateinit var customSharedPreferences: CustomSharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFavoriteHomeAndLandBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModel()
        initViews()
    }


    private fun initViews() {
        binding.ivBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        linearLayoutManager = LinearLayoutManager(activity)
        customSharedPreferences = CustomSharedPreferences(requireContext())

        stateId = customSharedPreferences.getStateID()
        packagesList = ArrayList()
        customProgressDialog = CustomProgressDialog(requireContext())

        arguments?.getParcelable<DisplayDetailStaticModel>(AppConstants.NEWHOMELISTMODEL)?.let {
            newHomeListModel = it
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
                packagesList, this@FavoriteHomeAndLandFragment
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
        setupObserver()

    }

    private fun setupObserver() {

        myHomeQuizViewModel.updateFavorite.observe(viewLifecycleOwner, Observer {
            if (it) {

                newHomeListModel.let { model ->
                    model.HouseName.let {
                        binding.tvHouse.text = (model.HouseName + " " + model.HouseSize)

                        myHomeQuizViewModel.getMyCollectionHomeAndLand(stateId, model.HouseName)
                    }
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
        loadFragment(HomeLandFullScreenFragment(), bundle)
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


    override fun selectFavoriteItemClick(
        hnLQuizPackageModel: HnLQuizPackageModel,
        isFavorite: Boolean
    ) {

    }


}