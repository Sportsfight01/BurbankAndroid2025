package com.dmss.burbankapp.ui.mydisplay.regions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
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
import com.dmss.burbankapp.data.model.DisplayHomeRegionModel
import com.dmss.burbankapp.data.model.StateRegionModel
import com.dmss.burbankapp.databinding.FragmentRegionsDisplayBinding
import com.dmss.burbankapp.ui.mydisplay.DisplayHomesViewModel
import com.dmss.burbankapp.ui.mydisplay.MyDisplayHomeFragment
import com.dmss.burbankapp.ui.view.CustomProgressDialog
import com.dmss.burbankapp.utils.AppConstants
import common.AppController
import timber.log.Timber


class RegionsDisplayFragment : Fragment(), RegionAdapter.IUpdateState {
    lateinit var binding: FragmentRegionsDisplayBinding
    lateinit var displayHomeViewModel: DisplayHomesViewModel
    private var customProgressDialog: CustomProgressDialog? = null
    lateinit var mPreferences: CustomSharedPreferences
    var regionsList: ArrayList<StateRegionModel> = ArrayList()
    lateinit var homeAndLandRegionsAdapter: RegionAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegionsDisplayBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initViewModel()
    }

    private fun initView() {
        // todo change this line
        /*(parentFragment as MyDisplayHomeFragment).toolBinding.tvTool.text =
            "Choose the region you're interested in"*/

        mPreferences = CustomSharedPreferences(AppController.getInstance())
        customProgressDialog = CustomProgressDialog(requireContext())
        /* binding.regionsRecyclerview.layoutManager = LinearLayoutManager(requireContext())
         binding.regionsRecyclerview.adapter = adapter*/


        binding.regionsRecyclerview.apply {
            homeAndLandRegionsAdapter =
                RegionAdapter(
                    ArrayList(),
                    this@RegionsDisplayFragment,
                    AppController.getInstance()
                )
            adapter = homeAndLandRegionsAdapter
        }
        binding.regionsRecyclerview.adapter
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

        subscribers()
        getRegions()

    }

    private fun getRegions() {

        displayHomeViewModel.fetchRegions(mPreferences.getStateID())

    }

    private fun subscribers() {
        displayHomeViewModel.getRegionsLiveData().observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    customProgressDialog?.dismissProgress()
                    var regionsData: DisplayHomeRegionModel? = it.data
                    if (regionsData != null) {
                        for (model in regionsData.regionsByStateId) {
                            val dataModel = StateRegionModel(0, 0, 0f, 0f, model, false)
                            regionsList.add(dataModel)
                        }
                        //regionsList = regionsData
                        homeAndLandRegionsAdapter.setData(regionsList)
                        homeAndLandRegionsAdapter.notifyDataSetChanged()
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

    override fun selectState(position: Int) {
        if (regionsList.size > 0) {
            val stateModel: StateRegionModel = regionsList[position]
            for (state in regionsList) {
                state.isSelected = stateModel.RegionName == state.RegionName
                if (state.isSelected) {
                    AppConstants.SELECTED_REGION_DISPLAY = state.RegionName
                    loadFragment(RegionDetailFragment.newInstance(state.RegionName), Bundle())
                }
            }
            homeAndLandRegionsAdapter.notifyDataSetChanged()
            //hnlApiCall(stateModel.RegionName)

        }
    }

    fun loadFragment(fragment: Fragment, bundle: Bundle) {
        // load fragment
        val transaction: FragmentTransaction = parentFragmentManager.beginTransaction()
        fragment.arguments = bundle
        if (fragment.isAdded) {
            return
        }
        transaction.add(R.id.fragment_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

}