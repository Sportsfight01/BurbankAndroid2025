package com.dmss.burbankapp.ui.mydisplay

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.os.SystemClock
import android.provider.Settings
import android.text.Html
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.dmss.burbankapp.R
import com.dmss.burbankapp.data.api.ApiHelperImpl
import com.dmss.burbankapp.data.api.RetrofitBuilder
import com.dmss.burbankapp.data.apiUtils.ViewModelFactory
import com.dmss.burbankapp.data.local.CustomSharedPreferences
import com.dmss.burbankapp.data.local.entity.DatabaseBuilder
import com.dmss.burbankapp.data.local.entity.DatabaseHelperImpl
import com.dmss.burbankapp.databinding.FragmentMyDisplayHomeBinding
import com.dmss.burbankapp.databinding.HomelandToolBinding
import com.dmss.burbankapp.databinding.ProfileWithBadgeBinding
import com.dmss.burbankapp.ui.designs.NewHomeQuizViewModel
import com.dmss.burbankapp.ui.main.MainActivity
import com.dmss.burbankapp.ui.mydisplay.designs.PopularHomeDesignFragment
import com.dmss.burbankapp.ui.mydisplay.map.MapDisplayFragment
import com.dmss.burbankapp.ui.mydisplay.nearby.CalendarFragment
import com.dmss.burbankapp.ui.mydisplay.nearby.NearByMapFragment
import com.dmss.burbankapp.ui.mydisplay.nearby.NearbyBottomSheetFragment
import com.dmss.burbankapp.ui.mydisplay.regions.RegionsDisplayFragment
import com.dmss.burbankapp.ui.view.CustomProgressDialog
import com.dmss.burbankapp.utils.AppConstants
import com.dmss.burbankapp.utils.AppUtils
import com.dmss.burbankappold.utils.SharedViewModel
import timber.log.Timber


class MyDisplayHomeFragment : BaseContainerFragment<FragmentMyDisplayHomeBinding>() {
    lateinit var binding: FragmentMyDisplayHomeBinding
    lateinit var toolBinding: HomelandToolBinding
    lateinit var displayHomeViewModel: DisplayHomesViewModel
    private var customProgressDialog: CustomProgressDialog? = null
    private var subscreensOnTheStack: Int = 0
    private var stateId: Int = -1
    private var userID: Int = -1
    lateinit var myPreference: CustomSharedPreferences
    lateinit var myHomeQuizViewModel: NewHomeQuizViewModel
    lateinit var profileWithBadgeBinding: ProfileWithBadgeBinding

    // variable to track event time
    private var mLastClickTime: Long = 0
    var oldFragment: Fragment? = null
    private lateinit var displayToolbarViewModel: DisplayToolbarViewModel

    override fun onResume() {
        super.onResume()

    }
    private fun updateHeader(headerText:String){
        toolBinding.tvTool.text =headerText

    }
    private fun profileNotificationCountView(notificationCount:Int){
        if(notificationCount==0){
            profileWithBadgeBinding.profileNotification.visibility=View.GONE
        }else{
            profileWithBadgeBinding.profileNotification.visibility=View.VISIBLE
        }
    }
    private fun initViews() {

        toolBinding.ivBack.setOnClickListener {
            activity?.onBackPressed()
         /*   changeColor(binding.tvNearby, binding.ivNearby,false)
            changeColor(binding.tvDesigns, binding.ivDesigns,false)
            changeColor(binding.tvRegions, binding.ivRegions,false)
            changeColor(binding.tvMap, binding.ivMap,false)*/
            val currentcount=getFragmentManager()!!.getBackStackEntryCount()
            val currentFragment = activity!!.supportFragmentManager.fragments.last()
            if(currentcount==0){
                updateHeader(AppConstants.headerTextStr)
            }
         /*   if(currentFragment!=null && currentFragment.tag!=PopularAndSuggestedFragment::class.simpleName &&
                    currentFragment.tag!=NearbyBottomSheetFragment::class.simpleName){
                toolBinding.tvTool.text = "See one of our display homes"

        }*/


        }

        initViewModel()
        customProgressDialog = CustomProgressDialog(requireContext())
        myPreference = CustomSharedPreferences(requireContext())
        stateId = myPreference.getStateID()
        userID = myPreference.getUserId()
        subscreensOnTheStack = AppConstants.BACKSTACK_COUNT

        customProgressDialog = CustomProgressDialog(requireContext())
        profileWithBadgeBinding.tvFavorites.visibility = View.VISIBLE
        profileWithBadgeBinding.profileNotification.visibility = View.VISIBLE
        binding.header.visibility=View.VISIBLE
        toolBinding.tvTool.text = "See one of our display homes"
        toolBinding.tvTool.visibility=View.VISIBLE
        toolBinding.tvSubTool.visibility=View.GONE
        profileWithBadgeBinding.profileNotification.text=AppConstants.TotalMyFavs.toString()
        profileNotificationCountView(AppConstants.TotalMyFavs)
        displayToolbarViewModel.favouritesCount.observe(this, Observer<Int> {
            profileWithBadgeBinding.profileNotification.text = it.toString()
            profileNotificationCountView(it)

        })

       displayToolbarViewModel.updateMainHeaderForDisplayHomes.observe(viewLifecycleOwner){
           val displayHomesfragmentContainer = activity!!.supportFragmentManager.findFragmentById(R.id.fragment_container)
           val flcontentsfragmentContainer = activity!!.supportFragmentManager.findFragmentById(R.id.fl_content)
            if(it.contains(getString(R.string.display))){
                binding.header.visibility=View.VISIBLE
                binding.view.visibility=View.VISIBLE
                toolBinding.tvTool.visibility=View.VISIBLE
                toolBinding.tvSubTool.visibility=View.GONE

            }else{
                binding.header.visibility=View.GONE
                binding.view.visibility=View.GONE
                toolBinding.tvTool.visibility=View.GONE
                toolBinding.tvSubTool.visibility=View.VISIBLE
                toolBinding.tvSubTool.text=AppConstants.displayHomeSubHeader
            }
           if(displayHomesfragmentContainer!!.tag==PopularAndSuggestedFragment::class.simpleName){
               toolBinding.tvTool.text=getString(R.string.See_one_of_our_display_homes)
               toolBinding.tvTool.visibility=View.VISIBLE
               toolBinding.tvSubTool.visibility=View.GONE
           }
           if(displayHomesfragmentContainer!!.tag==CalendarFragment::class.simpleName){
               toolBinding.tvTool.text=getString(R.string.book_an_appointment_small)
           }
        }
        var isUserLoggedIn = myPreference.getUserLogin()
        if (!isUserLoggedIn) {
            profileWithBadgeBinding.tvFavorites.background = resources.getDrawable(R.drawable.disable_rectangle_new)
            profileWithBadgeBinding.profileNotification.visibility=View.GONE
        }
        profileWithBadgeBinding.tvFavorites.setOnClickListener {

            var isUserLoggedIn = myPreference.getUserLogin()
            if (isUserLoggedIn) {
                /*  val bundle = Bundle()
                  bundle.putBoolean(AppConstants.SELECT_FAVORITE, true)
                  loadFragment(MyCollectionPlacesFragment(), bundle)*/
                (activity as MainActivity).showProfileDialog(activity!!)

            } else {
                AppUtils.showPleaseLoginDialog(
                    requireContext(),
                    requireActivity(),
                    getString(R.string.Please_login_to_view_edit_profile)
                )
            }

        }
        binding.llNearby.setOnClickListener {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return@setOnClickListener
            }
            navigateToNearby()
        }
        binding.llDesigns.setOnClickListener {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return@setOnClickListener
            }
            updateHeader(getString(R.string.Are_you_looking_for_specific_design))
            AppConstants.headerTextStr=getString(R.string.Are_you_looking_for_specific_design)

            mLastClickTime = SystemClock.elapsedRealtime()
            Timber.e("Designs")

            subscreensOnTheStack = 0
            changeColor(binding.tvDesigns, binding.ivDesigns,true)
            loadFragment(PopularHomeDesignFragment(), Bundle(),PopularHomeDesignFragment::class.simpleName!!)

        }
        binding.llRegions.setOnClickListener {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return@setOnClickListener
            }
            mLastClickTime = SystemClock.elapsedRealtime()
            Timber.e("Regions")
            updateHeader(getString(R.string.Choose_the_region_youre_interested_in))
            AppConstants.headerTextStr=getString(R.string.Choose_the_region_youre_interested_in)

            subscreensOnTheStack = 0
            changeColor(binding.tvRegions, binding.ivRegions,true)
            loadFragment(RegionsDisplayFragment(), Bundle(),RegionsDisplayFragment::class.simpleName!!)
        }
        binding.llMap.setOnClickListener {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return@setOnClickListener
            }
            mLastClickTime = SystemClock.elapsedRealtime()
            Timber.e("MapView")
            AppConstants.headerTextStr=getString(R.string.See_one_of_our_display_homes)
            updateHeader(getString(R.string.See_one_of_our_display_homes))

            subscreensOnTheStack = 0
            changeColor(binding.tvMap, binding.ivMap,true)
            loadFragment(MapDisplayFragment(), Bundle(),MapDisplayFragment::class.simpleName!!)
        }

        //REPLACE FRAGMENT
        loadFragment(PopularAndSuggestedFragment(), Bundle(),PopularAndSuggestedFragment::class.simpleName!!)
        displayToolbarViewModel.setMainHeaderForDisplayHomes(AppConstants.MAINHEADER)

    }
    fun navigateToNearby(){
        mLastClickTime = SystemClock.elapsedRealtime()
        updateHeader(getString(R.string.Check_out_the_displays_that_are_near_you_right_now))
        AppConstants.headerTextStr=getString(R.string.Check_out_the_displays_that_are_near_you_right_now)

        Timber.e("NearBy")
        subscreensOnTheStack = 0
        changeColor(binding.tvNearby, binding.ivNearby,true)
        loadFragment(NearByMapFragment(), Bundle(),NearByMapFragment::class.simpleName!!)
    }

    private fun initViewModel() {
        displayHomeViewModel = ViewModelProviders.of(
            requireActivity(),
            ViewModelFactory(
                ApiHelperImpl(RetrofitBuilder.apiService),
                DatabaseHelperImpl(DatabaseBuilder.getInstance(requireContext()))
            )
        ).get(DisplayHomesViewModel::class.java)
        displayToolbarViewModel = ViewModelProviders.of(requireActivity())[DisplayToolbarViewModel::class.java]
        displayToolbarViewModel.updateToolbarValue(getString(R.string.See_one_of_our_display_homes))
        var displayHomesfragmentContainer = activity!!.supportFragmentManager.findFragmentById(R.id.fragment_container)
        displayToolbarViewModel.updateToolBar.observe(viewLifecycleOwner){
            toolBinding.tvTool.text = it
        }
    }

    private fun changeColor(
        selectedText: TextView,
        selectedImage: ImageView,
        isSelected:Boolean
    ) {
        binding.tvNearby.setTextColor(ContextCompat.getColor(requireContext(), R.color.grey_text_font_3_1))
        binding.tvDesigns.setTextColor(ContextCompat.getColor(requireContext(), R.color.grey_text_font_3_1))
        binding.tvRegions.setTextColor(ContextCompat.getColor(requireContext(), R.color.grey_text_font_3_1))
        binding.tvMap.setTextColor(ContextCompat.getColor(requireContext(), R.color.grey_text_font_3_1))


        binding.ivNearby.setColorFilter(

            ContextCompat.getColor(
                binding.ivNearby.context,
                R.color.grey_text_font_3_1
            ), PorterDuff.Mode.SRC_ATOP
        );
        binding.ivDesigns.setColorFilter(
            ContextCompat.getColor(
                binding.ivNearby.context,
                R.color.grey_text_font_3_1
            ), PorterDuff.Mode.SRC_ATOP
        );
        binding.ivRegions.setColorFilter(
            ContextCompat.getColor(
                binding.ivNearby.context,
                R.color.grey_text_font_3_1
            ), PorterDuff.Mode.SRC_ATOP
        );
        binding.ivMap.setColorFilter(
            ContextCompat.getColor(
                binding.ivNearby.context,
                R.color.grey_text_font_3_1
            ), PorterDuff.Mode.SRC_ATOP
        );
        if(isSelected) {
            selectedText.setTextColor(ContextCompat.getColor(requireContext(), R.color.orange_bg_3_1))
            selectedImage.setColorFilter(
                ContextCompat.getColor(
                    binding.ivNearby.context,
                    R.color.orange_bg_3_1
                ), PorterDuff.Mode.SRC_ATOP
            )
        }


    }

    fun loadFragment(fragment: Fragment, bundle: Bundle,fragmentTag:String) {
        // load fragment
        if(fragmentTag==PopularAndSuggestedFragment::class.simpleName){
            AppConstants.headerTextStr=getString(R.string.See_one_of_our_display_homes)
        }
        val transaction: FragmentTransaction = parentFragmentManager.beginTransaction()
        fragment.arguments = bundle
        transaction.replace(R.id.fragment_container, fragment,fragmentTag)
        transaction.commit()
    }

    override fun initUi() {
        updateToolText("See one of our display homes")
        initViews()
    }

    override fun getFragmentBinding(
        layoutInflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentMyDisplayHomeBinding {
        binding = FragmentMyDisplayHomeBinding.inflate(layoutInflater)
        toolBinding = HomelandToolBinding.bind(binding.root)
        profileWithBadgeBinding = ProfileWithBadgeBinding.bind(binding.root)

        return binding
    }


}
