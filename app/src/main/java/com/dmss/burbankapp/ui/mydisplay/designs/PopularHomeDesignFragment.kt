package com.dmss.burbankapp.ui.mydisplay.designs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
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
import com.dmss.burbankapp.data.model.DesignSubModel
import com.dmss.burbankapp.data.model.DisplaysByStateIdResponseModel
import com.dmss.burbankapp.data.model.DisplyByStateIdBodyModel
import com.dmss.burbankapp.databinding.FragmentPopularHomeDesignBinding
import com.dmss.burbankapp.ui.mydisplay.DisplayHomesViewModel
import com.dmss.burbankapp.ui.mydisplay.DisplayToolbarViewModel
import com.dmss.burbankapp.ui.mydisplay.MyDisplayHomeFragment
import com.dmss.burbankapp.ui.view.CustomProgressDialog
import com.dmss.burbankapp.utils.AppConstants
import com.dmss.burbankapp.utils.DividerItemDecorator
import timber.log.Timber


class PopularHomeDesignFragment : Fragment(),
    PopularRecyclerviewAdapter.IDesignDetailItemClickListener {
    lateinit var binding: FragmentPopularHomeDesignBinding


    lateinit var displayHomeViewModel: DisplayHomesViewModel
    private var customProgressDialog: CustomProgressDialog? = null
    lateinit var myPreference: CustomSharedPreferences
    lateinit var adapter: PopularRecyclerviewAdapter
    private lateinit var viewModel: DisplayToolbarViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPopularHomeDesignBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewModel()
        initView();
    }

    private fun fetchPopularHomes(houseName: String, houseSize: String, search: String) {

        var jsonObject = DisplyByStateIdBodyModel(
            myPreference.getStateID(),
            AppConstants.dummyLatitude,
            AppConstants.dummyLongitude,
            myPreference.getUserId(), search, houseName, houseSize
        )
        displayHomeViewModel.fetchDisplaysByStateId(jsonObject)

    }

    private fun initViewModel() {
        displayHomeViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(
                ApiHelperImpl(RetrofitBuilder.apiService),
                DatabaseHelperImpl(DatabaseBuilder.getInstance(requireContext()))
            )
        ).get(DisplayHomesViewModel::class.java)
       // viewModel = ViewModelProviders.of(requireActivity()).get(DisplayToolbarViewModel::class.java)
        subscribers()

    }

    private fun subscribers() {
        displayHomeViewModel.getDisplaysByStateId().observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    customProgressDialog?.dismissProgress()

                    if (it.data != null) {
                        if (it.data.status) {

                            var totalResponse: DisplaysByStateIdResponseModel = it.data

                            totalResponse.getDisplaysById?.let { displayByStateIdModel ->
                                displayByStateIdModel.mostPopularHomesDTOs?.let { list ->
                                    if (list.isNotEmpty()) {
                                        binding.netstedView.visibility = View.VISIBLE
                                        binding.errorMessage.visibility = View.GONE
                                        Timber.e("List Size ${list.size}")
                                        adapter.updatedList(list)
                                    }else{
                                        binding.netstedView.visibility = View.GONE
                                        binding.errorMessage.visibility = View.VISIBLE
                                    }
                                }
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
        // todo change this line
        /*(parentFragment as MyDisplayHomeFragment).toolBinding.tvTool.text =
            "Are you looking for specific design?"*/

       // viewModel.updateToolbarValue("Are you looking for specific design?")

        myPreference = CustomSharedPreferences(requireContext())
        customProgressDialog = CustomProgressDialog(requireContext())

        adapter = PopularRecyclerviewAdapter(this)
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())


        val dividerItemDecoration: RecyclerView.ItemDecoration =
            DividerItemDecorator(
                ContextCompat.getDrawable(
                    binding.recyclerView.context,
                    R.drawable.divider_recyclerview
                )
            )

        binding.recyclerView.addItemDecoration(
            dividerItemDecoration
        )
        binding.recyclerView.adapter = adapter

        var houseName: String? = arguments?.getString("HouseName")
        var houseSize: String? = arguments?.getString("HouseSize")

        if (houseName != null && houseSize != null) {
            binding.clSelection.visibility = View.GONE
            fetchPopularHomes(houseName, houseSize.toString(), "")
        } else {
            binding.clSelection.visibility = View.VISIBLE
            fetchPopularHomes("", "", "all")
        }

        binding.tvALL.setOnClickListener {
            changeView(binding.tvALL)
            binding.recyclerView.removeAllViews()
            fetchPopularHomes("", "", "all")
        }
        binding.tvSingle.setOnClickListener {
            changeView(binding.tvSingle)
            binding.recyclerView.removeAllViews()

            fetchPopularHomes("", "", "1")
        }
        binding.tvDouble.setOnClickListener {
            changeView(binding.tvDouble)
            binding.recyclerView.removeAllViews()
            fetchPopularHomes("", "", "2")
        }
    }

    private fun changeView(selectedView: TextView) {
        binding.tvALL.setTextColor(ContextCompat.getColor(requireContext(), R.color.grey_text_font_3_1))
        binding.tvSingle.setTextColor(ContextCompat.getColor(requireContext(), R.color.grey_text_font_3_1))
        binding.tvDouble.setTextColor(ContextCompat.getColor(requireContext(), R.color.grey_text_font_3_1))

        binding.tvALL.background =
            ContextCompat.getDrawable(requireContext(), R.drawable.rectangel_orange_line)
        binding.tvSingle.background =
            ContextCompat.getDrawable(requireContext(), R.drawable.rectangel_orange_line)
        binding.tvDouble.background =
            ContextCompat.getDrawable(requireContext(), R.drawable.rectangel_orange_line)
        selectedView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white_3_1))
        selectedView.background =
            ContextCompat.getDrawable(requireContext(), R.drawable.rectangle_orange_bg)
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

    override fun onDesignItemClickListener(model: DesignSubModel) {
        var bundle = Bundle()
        bundle.putString("LATITUDE", model.latitude)
        bundle.putString("LONGITUDE", model.longitude)
        bundle.putParcelable("DESIGNSUBMODEL", model)
        myPreference.saveDisplayId(model.Id)
        loadFragment(PopularHomeDesignDetailFragment(), bundle)

    }


}