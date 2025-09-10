package com.dmss.burbankapp.ui.mydisplay.favoritesDisplays

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
import androidx.recyclerview.widget.RecyclerView
import com.dmss.burbankapp.R
import com.dmss.burbankapp.data.api.ApiHelperImpl
import com.dmss.burbankapp.data.api.RetrofitBuilder
import com.dmss.burbankapp.data.apiUtils.Status
import com.dmss.burbankapp.data.apiUtils.ViewModelFactory
import com.dmss.burbankapp.data.local.CustomSharedPreferences
import com.dmss.burbankapp.data.local.entity.DatabaseBuilder
import com.dmss.burbankapp.data.local.entity.DatabaseHelperImpl
import com.dmss.burbankapp.data.model.HouseDetailsByHouseType
import com.dmss.burbankapp.data.model.UserFavoriteDisplaysResponseModel
import com.dmss.burbankapp.data.model.UserFavoritesDisplayModel
import com.dmss.burbankapp.databinding.FragmentFavoriteDisplaysBinding
import com.dmss.burbankapp.databinding.HomelandToolBinding
import com.dmss.burbankapp.databinding.ProfileWithBadgeBinding
import com.dmss.burbankapp.ui.main.MainActivity
import com.dmss.burbankapp.ui.mydisplay.DisplayHomesViewModel
import com.dmss.burbankapp.ui.mydisplay.DisplayToolbarViewModel
import com.dmss.burbankapp.ui.view.CustomProgressDialog
import com.dmss.burbankapp.utils.AppConstants
import com.dmss.burbankapp.utils.AppUtils
import com.dmss.burbankapp.utils.DividerItemDecorator
import com.dmss.burbankapp.utils.WrapContentLinearLayoutManager
import com.google.gson.JsonObject
import java.util.*
import kotlin.collections.ArrayList


class FavoriteDisplaysFragment : Fragment(), FavoriteDisplayAdapter.onItemClickListener,
    NearbyBottomSheetFavoriteFragment.IDirection {
    lateinit var binding: FragmentFavoriteDisplaysBinding
    lateinit var toolBinding: HomelandToolBinding
    lateinit var profileWithBadgeBinding: ProfileWithBadgeBinding

    lateinit var displayHomeViewModel: DisplayHomesViewModel
    private var customProgressDialog: CustomProgressDialog? = null
    var favoriteAdapter: FavoriteDisplayAdapter? = null
    var userFavorites: ArrayList<UserFavoritesDisplayModel> = ArrayList()
    lateinit var mPreferences: CustomSharedPreferences
    var stateID = 0
    var userId = 0
    var userFavoriteDisplayModel: UserFavoritesDisplayModel? = null
    var deletePosition: Int = 0
    lateinit var dataModel: UserFavoritesDisplayModel
    private lateinit var displayToolbarViewModel: DisplayToolbarViewModel


    companion object {
        val TAG = FavoriteDisplaysFragment::javaClass.name
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentFavoriteDisplaysBinding.inflate(layoutInflater, container, false)
        toolBinding = HomelandToolBinding.bind(binding.root)
        profileWithBadgeBinding = ProfileWithBadgeBinding.bind(binding.root)

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initViews()
        initViewModel()

    }

    private fun profileNotificationCountView(notificationCount:Int){
        if(notificationCount==0){
            profileWithBadgeBinding.profileNotification.visibility=View.GONE
        }else{
            profileWithBadgeBinding.profileNotification.visibility=View.VISIBLE
        }
    }
    private fun initViews() {

        AppConstants.updateDisplayHomesFav=false
         displayToolbarViewModel = ViewModelProviders.of(requireActivity())[DisplayToolbarViewModel::class.java]

        mPreferences = CustomSharedPreferences(requireContext())
        stateID = mPreferences.getStateID()
        userId = mPreferences.getUserId()

        AppConstants.BACKSTACK_COUNT = 0
        toolBinding.ivBack.setOnClickListener {
            activity?.onBackPressed()
        }
        profileWithBadgeBinding.tvFavorites.visibility = View.VISIBLE
        profileWithBadgeBinding.profileNotification.text = AppConstants.TotalMyFavs.toString()
        profileNotificationCountView(AppConstants.TotalMyFavs)
        displayToolbarViewModel.favouritesCount.observe(viewLifecycleOwner, Observer<Int> { item ->
            profileWithBadgeBinding.profileNotification.text = item.toString()
            profileNotificationCountView(item)

        })
        displayToolbarViewModel.updateFavouriteDisplayHomes.observe(viewLifecycleOwner, Observer<Int> { item ->
            if(AppConstants.updateDisplayHomesFav)
            getUserFavoritesDisplays()
        })
        profileWithBadgeBinding.tvFavorites.setOnClickListener {
            var isUserLoggedIn = mPreferences.getUserLogin()
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
        toolBinding.tvTool.text = "Favourite displays"
        favoriteAdapter = FavoriteDisplayAdapter(this)
        customProgressDialog = CustomProgressDialog(requireContext())
        binding.recyclerFavorites.removeAllViews()
        val dividerItemDecoration: RecyclerView.ItemDecoration =
            DividerItemDecorator(
                ContextCompat.getDrawable(
                    binding.recyclerFavorites.context,
                    R.drawable.divider_recyclerview_favorite
                )
            )

       /* binding.recyclerFavorites.addItemDecoration(
            dividerItemDecoration
        )*/


        binding.recyclerFavorites.apply {
            adapter = favoriteAdapter
            layoutManager = WrapContentLinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }

    }

    private fun initViewModel() {
        displayHomeViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(
                ApiHelperImpl(RetrofitBuilder.apiService),
                DatabaseHelperImpl(DatabaseBuilder.getInstance(requireContext()))
            )
        )[DisplayHomesViewModel::class.java]
        subscribers()
        getUserFavoritesDisplays()

    }

    private fun getUserFavoritesDisplays() {
        displayHomeViewModel.getUserFavoritesDisplays()
    }

    private fun subscribers() {
        displayHomeViewModel.getUserFavoritesDisplaysLiveData.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    customProgressDialog?.dismissProgress()
                    if (it.data != null) {
                        if (it.data.status || !it.data.status) {
                            noData(false)
                            var totalResponse: UserFavoriteDisplaysResponseModel = it.data
                            userFavorites.clear()
                            userFavorites = totalResponse.userFavorites
                            favoriteAdapter?.submitList(totalResponse.userFavorites)
                            val currentUserHeader =
                                getString(R.string.favourite_displays)+" ("+userFavorites.size+")"
                            if(userFavorites.size==0){
                                noData(true)
                            }else{
                                noData(false)
                            }
                            binding.tvMydisplay.text=currentUserHeader

                        } else {
                            noData(true)
                        }
                    } else {
                        noData(true)
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
        displayHomeViewModel.getFavorite().observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    customProgressDialog?.dismissProgress()
                    if (it.data != null) {
                        if (it.data.status) {
                            (activity as MainActivity).loadUserFavoritesDisplays()
                            if (userFavorites.isNotEmpty() && userFavorites.size > 0) {

                                noData(false)
                                if (userFavoriteDisplayModel != null) {

                                    val iterator = userFavorites.iterator()
                                    while (iterator.hasNext()) {
                                        val item = iterator.next()
                                        if (item == userFavoriteDisplayModel) {
                                            iterator.remove()
                                        }
                                    }

                                    if (userFavorites.size > 0) {
                                        favoriteAdapter?.submitList(userFavorites)
                                        favoriteAdapter?.notifyItemRangeChanged(
                                            deletePosition,
                                            userFavorites.size
                                        )
                                    } else {
                                        noData(true)
                                    }
                                    val currentUserHeader =
                                        getString(R.string.favourite_displays)+" ("+userFavorites.size+")"
                                    binding.tvMydisplay.text=currentUserHeader
                                }


                            } else {
                                noData(true)
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

    fun noData(show: Boolean) {
        if (show) {
            binding.tvMydisplay.visibility = View.GONE
            binding.recyclerFavorites.visibility = View.GONE
            binding.tvNoFavorites.visibility = View.VISIBLE
        } else {
            binding.tvMydisplay.visibility = View.VISIBLE
            binding.recyclerFavorites.visibility = View.VISIBLE
            binding.tvNoFavorites.visibility = View.GONE
        }


    }

    override fun unFavoriteItemClickListener(model: UserFavoritesDisplayModel, position: Int) {
        userFavoriteDisplayModel = model
        deletePosition = position

        setFavoriteOrUnFavoriteApi(model)
    }

    private fun setFavoriteOrUnFavoriteApi(
        model: UserFavoritesDisplayModel
    ) {
        val jsonObject = JsonObject()
        jsonObject.addProperty("TypeId", 3)
        jsonObject.addProperty("UserId", userId)
        jsonObject.addProperty("HouseId", model.DisplayId)
        jsonObject.addProperty("StateId", stateID)
        jsonObject.addProperty("isfavourite", false)
        displayHomeViewModel.setFavorite(jsonObject)
    }

    override fun itemClickListener(model: UserFavoritesDisplayModel) {

        dataModel = model
        showEditDialog(
            model.displayEstateName,
            model.street,
            model.suburb
        )

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

    private fun showEditDialog(estateName: String, street: String, subrub: String) {


        if (estateName.isNotEmpty()) {
            val headerText = estateName.uppercase(Locale.getDefault()).trim() + ", " + street
            toolBinding.tvTool.text = headerText
        }


        val fm: FragmentManager = requireActivity().supportFragmentManager
        var fragment =
            NearbyBottomSheetFavoriteFragment(this,toolBinding.tvTool).newInstance(estateName, street, subrub);
        if (fragment != null) {
            if (!fragment.isAdded) {
                fragment.show(fm, TAG)
            }
        }
    }


    override fun showDirection(houseList: ArrayList<HouseDetailsByHouseType>) {
        var bundle = Bundle()
       /* bundle.putParcelableArrayList("DirectionList", houseList)
        loadFragment(FavoriteDirectionFragment(), bundle)*/

        if(houseList.size>0) {
            AppUtils.navigateDirections(
                houseList[0].Latitude.toDouble(),
                houseList[0].Longitude.toDouble(),
                activity!!
            )
        }
    }

    override fun bookAppointment(houseName: String, houseSize: String) {
//        toolBinding.tvTool.text = "Book an Appointment"
        if (this::dataModel.isInitialized) {
            var bundle = Bundle()
            bundle.putString("estateName", dataModel.displayEstateName)
            bundle.putString("street", dataModel.street)
            bundle.putString("suburb", dataModel.suburb)
            bundle.putString("houseName", houseName)
            bundle.putString("houseSize", houseSize)
            loadFragment(FavoriteDisplayBookAppintmentFragment(), bundle)
        }
    }

}