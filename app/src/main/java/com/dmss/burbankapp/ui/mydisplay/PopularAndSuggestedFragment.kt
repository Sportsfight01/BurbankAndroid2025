package com.dmss.burbankapp.ui.mydisplay

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
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
import com.dmss.burbankapp.databinding.FragmentPopularAndSuggestedBinding
import com.dmss.burbankapp.ui.dashboard.DashboardActivity
import com.dmss.burbankapp.ui.designs.NewHomeQuizViewModel
import com.dmss.burbankapp.ui.main.MainActivity
import com.dmss.burbankapp.ui.mydisplay.designs.PopularHomeDesignFragment
import com.dmss.burbankapp.ui.mydisplay.regions.SuggestedDisplayDetailsFragment
import com.dmss.burbankapp.ui.view.CustomProgressDialog
import com.dmss.burbankapp.utils.AppConstants
import com.dmss.burbankapp.utils.SpacesItemDecoration
import com.google.gson.JsonObject
import common.AppController
import timber.log.Timber


class PopularAndSuggestedFragment : BaseContainerFragment<FragmentPopularAndSuggestedBinding>(),
    SuggestedDisplayAdapter.ISuggestedDisplay, PopularGridAdapter.IPopularHomeDetailItemClick {
    private lateinit var binding: FragmentPopularAndSuggestedBinding
    lateinit var mPreferences: CustomSharedPreferences
    lateinit var displayHomeViewModel: DisplayHomesViewModel
    private var customProgressDialog: CustomProgressDialog? = null
    var gridAdapter: PopularGridAdapter? = null
    var suggestDisplayAdapter: SuggestedDisplayAdapter? = null
    lateinit var myHomeQuizViewModel: NewHomeQuizViewModel

    private var stateId: Int = -1
    private var userID: Int = -1

    companion object {
        @JvmStatic
        fun newInstance() =
            PopularAndSuggestedFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }

    override fun onResume() {
        super.onResume()
        Timber.e("onResume")
    }

    private fun initViewModel() {
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
        fetchDisplayByStateId()
    }

    private fun subscribers() {
        displayHomeViewModel.getDisplaysByStateId().observe(viewLifecycleOwner, Observer {
            if (viewLifecycleOwner.lifecycle.currentState == Lifecycle.State.RESUMED) {
                when (it.status) {
                    Status.SUCCESS -> {
                        customProgressDialog?.dismissProgress()

                        if (it.data != null) {
                            if (it.data.status) {
                                var totalResponse: DisplaysByStateIdResponseModel = it.data
                                totalResponse.getDisplaysById?.let { displaysByStateIdModel ->
                                    displaysByStateIdModel.let { model ->
                                        model.mostPopularHomesDTOs?.let { list ->
                                            if (list.isNotEmpty()) {
                                                gridAdapter?.submitList(list)
                                            } else {

                                            }


                                        }
                                    }

                                }

                                totalResponse.getDisplaysById?.let { displaysByStateIdModel ->
                                    displaysByStateIdModel.let { model ->
                                        model.suggestedHomesDTOs?.let { list ->
                                            if (list.isNotEmpty()) {
                                                binding.recyclerSuggested.visibility = View.VISIBLE
                                                binding.tvSuggested.visibility = View.VISIBLE
                                                suggestDisplayAdapter?.submitList(list)
                                            } else {
                                                binding.recyclerSuggested.visibility = View.GONE
                                                binding.tvSuggested.visibility = View.GONE
                                            }
                                        } ?: kotlin.run {
                                            binding.recyclerSuggested.visibility = View.GONE
                                            binding.tvSuggested.visibility = View.GONE
                                        }
                                    }

                                } ?: kotlin.run {
                                    binding.recyclerSuggested.visibility = View.GONE
                                    binding.tvSuggested.visibility = View.GONE
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
            }
        })
        myHomeQuizViewModel.getFavorite().observe(viewLifecycleOwner, Observer {
            if (viewLifecycleOwner.lifecycle.currentState == Lifecycle.State.RESUMED) {
                when (it.status) {
                    Status.SUCCESS -> {
                        customProgressDialog?.dismissProgress()

                        if (it.data != null) {
                            if (it.data.status) {
                                (activity as MainActivity).loadUserFavoritesDisplays()

                             /*   val toast = ToastHandler.getToastInstance(
                                    requireContext(),
                                    it.data.message,
                                    Toast.LENGTH_SHORT
                                )
                                toast.setGravity(Gravity.CENTER, 0, 0)
                                toast.show()*/
                                Toast.makeText(requireContext(),it.data.message,Toast.LENGTH_SHORT).show()

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
            }
        })
    }

    private fun fetchDisplayByStateId() {

        var jsonObject = DisplyByStateIdBodyModel(
            mPreferences.getStateID(),
            AppConstants.dummyLatitude,
            AppConstants.dummyLongitude,
            mPreferences.getUserId(), ""
        )
        displayHomeViewModel.fetchDisplaysByStateId(jsonObject)
    }

    override fun initUi() {

        customProgressDialog = CustomProgressDialog(requireContext())
        mPreferences = CustomSharedPreferences(requireContext())
        stateId = mPreferences.getStateID()
        userID = mPreferences.getUserId()

        customProgressDialog = CustomProgressDialog(requireContext())


        suggestDisplayAdapter = SuggestedDisplayAdapter(this,
            requireActivity())
        gridAdapter = PopularGridAdapter(this)

        binding.recyclerSuggested.adapter = suggestDisplayAdapter
        binding.recyclerSuggested.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        binding.recyclerPopular.adapter = gridAdapter
        val spacingInPixels = resources.getDimensionPixelSize(R.dimen._16sp)
        binding.recyclerPopular.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recyclerPopular.addItemDecoration(SpacesItemDecoration(2, spacingInPixels, true))
        initViewModel()
    }

    override fun getFragmentBinding(
        layoutInflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPopularAndSuggestedBinding {
        binding = FragmentPopularAndSuggestedBinding.inflate(layoutInflater)
        return binding
    }

    override fun selectSuggestedDisplay(model: SuggestedHomesDtoModel) {
        var bundle = Bundle()
        var latitude: String? = null
        var longitude: String? = null
        latitude = if (model.latitude.contains(",")) {
            model.latitude.replace(",", "").trim()
        } else {
            model.latitude
        }
        longitude = if (model.longitude.contains(",")) {
            model.longitude.replace(",", "").trim()
        } else {
            model.longitude
        }
        bundle.putString("LATITUDE", latitude)
        bundle.putString("LONGITUDE", longitude)
        bundle.putParcelable("SUGGESTED_MODEL", model)
        mPreferences.saveDisplayId(model.id)

        var viewModel = ViewModelProviders.of(requireActivity()).get(DisplayToolbarViewModel::class.java)
        val EstateName:String=model.EstateName.uppercase()
        val LotStreet1:String=model.LotStreet1?:""
        viewModel.updateToolbarValue("$EstateName, $LotStreet1")
//        viewModel.sendMessage(model.EstateName)
        loadFragment(SuggestedDisplayDetailsFragment(), bundle)
    }

    override fun handleFavoriteAndUnFavorite(model: SuggestedHomesDtoModel, isFavorite: Boolean) {
        val jsonObject = JsonObject()
        jsonObject.addProperty("TypeId", 3)
        jsonObject.addProperty("UserId", userID)
        jsonObject.addProperty("HouseId", model.id)
        jsonObject.addProperty("StateId", stateId)
        jsonObject.addProperty("isfavourite", isFavorite)
        myHomeQuizViewModel.setFavorite(jsonObject)
    }

    override fun popularDetailItemClick(model: MostPopularHomesDTOModel) {

        var bundle = Bundle()
        bundle.putString("HouseName", model.houseName)
        bundle.putString("HouseSize", model.HouseSize)

        if (model.locations.isNotEmpty()) {
            loadFragment(PopularHomeDesignFragment(), bundle)
        } else {
            var bundle = Bundle()
            mPreferences.saveDisplayId(model.Id)
            var dataModel = DisplayDetailStaticModel(
                model.houseName,
                model.HouseSize.toInt(),
                model.HousePrice,
                model.Storey,
                model.CarSpace,
                model.BathRooms,
                model.BedRooms,
                model.Id,
                model.IsFavourite,
                0.0
            )

            bundle.putParcelable(AppConstants.USER_FAVORITE_DISPLAY_MODEL, dataModel)
            bundle.putBoolean("isFavorite", false)
            bundle.putBoolean("isFavoriteDisplayId", true)
            mPreferences.saveDisplayId(model.Id)
            (parentFragment as MyDisplayHomeFragment).toolBinding.tvTool.text =model.houseName

            loadFragment(DisplayDetailFragment(), bundle)
        }

    }

    fun loadFragment(fragment: Fragment, bundle: Bundle) {
        // load fragment
        val transaction: FragmentTransaction = parentFragmentManager.beginTransaction()
        fragment.arguments = bundle
        if (fragment.isAdded) return
        transaction.add(R.id.fragment_container, fragment,PopularAndSuggestedFragment::class.simpleName)
        transaction.addToBackStack(null)
        transaction.commit()

    }
}