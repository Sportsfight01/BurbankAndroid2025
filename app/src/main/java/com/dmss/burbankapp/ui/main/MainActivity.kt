package com.dmss.burbankapp.ui.main

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.text.Layout
import android.text.SpannableString
import android.text.style.AlignmentSpan
import android.util.Base64
import android.util.TypedValue
import android.view.*
import android.widget.*
import androidx.annotation.NonNull
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.dmss.burbankapp.R
import com.dmss.burbankapp.data.api.ApiHelperImpl
import com.dmss.burbankapp.data.api.RetrofitBuilder
import com.dmss.burbankapp.data.apiUtils.Status
import com.dmss.burbankapp.data.apiUtils.ViewModelFactory
import com.dmss.burbankapp.data.local.CustomSharedPreferences
import com.dmss.burbankapp.data.local.entity.DatabaseBuilder
import com.dmss.burbankapp.data.local.entity.DatabaseHelperImpl
import com.dmss.burbankapp.data.model.*
import com.dmss.burbankapp.databinding.ActivityMainBinding
import com.dmss.burbankapp.databinding.LayoutEmailSharingDialogBinding
import com.dmss.burbankapp.databinding.ProfileLayoutBinding
import com.dmss.burbankapp.databinding.ToolbarBinding
import com.dmss.burbankapp.ui.base.BaseActivity
import com.dmss.burbankapp.ui.dashboard.DashboardActivity
import com.dmss.burbankapp.ui.dashboard.DashboardViewModel
import com.dmss.burbankapp.ui.dashboard.ShareListAdapter
import com.dmss.burbankapp.ui.dashboard.StateListAdapter
import com.dmss.burbankapp.ui.designs.*
import com.dmss.burbankapp.ui.homeandland.HomeLandRegionFragment
import com.dmss.burbankapp.ui.homeandlandoprice.HomeLandPriceRangeFragment
import com.dmss.burbankapp.ui.homeandlandplaces.HomeLandPlaceFragment
import com.dmss.burbankapp.ui.loginhome.LoginHomeActivity
import com.dmss.burbankapp.ui.mydisplay.*
import com.dmss.burbankapp.ui.mydisplay.favoritesDisplays.FavoriteDisplaysFragment
import com.dmss.burbankapp.ui.myfavourite.MyFavouritesFragment
import com.dmss.burbankapp.utility.ExpandableLayout
import com.dmss.burbankapp.utility.ImagePickerActivity
import com.dmss.burbankapp.utils.AppConstants
import com.dmss.burbankapp.utils.AppUtils
import com.dmss.burbankapp.utils.IChangeHelpTextBasedOnSelectedView
import com.dmss.burbankapp.utils.IHideProfilePic
import com.dmss.burbankapp.utils.customviews.AppEvent
import com.dmss.burbankapp.utils.customviews.CircleTransform
import com.dmss.burbankapp.utils.customviews.SpinnerImage
import com.dmss.burbankapp.utils.customviews.Utility
import com.dmss.burbankapp.viewmodel.ProfilePicViewModel
import com.dmss.burbankapp.viewmodel.SingletonNameViewModelFactory
import com.facebook.login.LoginManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.gson.JsonObject
import com.squareup.picasso.Picasso
import common.AppController
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.util.*


class MainActivity : BaseActivity(),
    ShareListAdapter.ShareAccountItemClick, IHideProfilePic, IChangeHelpTextBasedOnSelectedView {
    lateinit var myHomeQuizViewModel: NewHomeQuizViewModel
    lateinit var profileLayoutBinding: ProfileLayoutBinding
    lateinit var profilePicViewModel: ProfilePicViewModel;
    lateinit var singletonNameViewModelFactory: SingletonNameViewModelFactory
    private lateinit var profileProgress: ProgressBar
    lateinit var shareAdapter: ShareListAdapter
    lateinit var dashboardViewModel: DashboardViewModel
    lateinit var displayHomeViewModel: DisplayHomesViewModel

    private lateinit var iv_profile_dialog: ImageView
    lateinit var myPreferences: CustomSharedPreferences
    lateinit var relative_share_list: RelativeLayout
    var onBackPress: OnBackPressedListener? = null
    lateinit var firstFragment: Fragment
    private var stateId: Int = -1
    private lateinit var placePicture: String
    lateinit var binding: ActivityMainBinding
    lateinit var toolBinding: ToolbarBinding
    lateinit var myCollectionRecentSearchText: String
    private val CAMERA_REQUEST_CODE = 100
    lateinit var iHideProfilePic: IHideProfilePic
    private lateinit var displayToolbarViewModel: DisplayToolbarViewModel

    var minimumPriceForPriceRange: Double? = null
    var maximumPriceForPriceRange: Double? = null


    lateinit var sharingAccountBinding: LayoutEmailSharingDialogBinding
    lateinit var stateAdapter: StateListAdapter

    //profileDialog
    lateinit var dialogBuilder: AlertDialog.Builder
    var recentSearchHashMap: HashMap<String, ArrayList<NewHomeJsonObject>> = LinkedHashMap()
    var isMyCollectionRecentSearchDataEmpty: Boolean = true
    var isHomeAndLandRecentSearchDataEmpty: Boolean = true

    var recentSearchJsonModel: SearchJsonModel?=null
    var isFromNotification=false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        makeFullScreen()
        iHideProfilePic = this
        binding = ActivityMainBinding.inflate(layoutInflater)
        toolBinding = ToolbarBinding.bind(binding.root)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN )
        var myPreference = CustomSharedPreferences(this)

        var isUserLoggedIn = myPreference.getUserLogin()
        println("isUserLoggedIn MAIN ACTIVITY:: "+isUserLoggedIn)
        setContentView(binding.root)
        changeStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        AppConstants.HouseCount = 0
        AppConstants.newHomeJsonObjectsList.clear()
        AppConstants.isRecentDialogShowed = false
        AppConstants.storedFragments.clear()
        initViews()
        setupViewModel()
    }

    private fun getUserFavoritesDisplays() {
        displayHomeViewModel.getUserFavoritesDisplays()
    }

    @JvmName("getIHideProfilePic1")
    fun getIHideProfilePic(): IHideProfilePic {
        return iHideProfilePic
    }


    private fun initViews() {

        displayToolbarViewModel = ViewModelProviders.of(this)[DisplayToolbarViewModel::class.java]

        toolBinding.ivHome.setOnClickListener {

                finish()


        }
        displayToolbarViewModel.updateMainHeader.observe(this){
            val headertext =it.split(",").toTypedArray()
            toolBinding.tvMy.text=headertext[0]
            toolBinding.tvHeaderDesc.text=headertext[1]

        }
        displayToolbarViewModel.updateMainHeaderForDisplayHomes.observe(this){
            val headertext =it.split(",").toTypedArray()
            toolBinding.tvMy.text=headertext[0]
            toolBinding.tvHeaderDesc.text=headertext[1]

        }
        firstFragment = MyCollectionDashboardFragment()
        myPreferences = CustomSharedPreferences(this)
        /*Default Sort By Price */
        myPreferences.savePriceRange("Low")


        stateId = myPreferences.getStateID()
        println("stateId:: Before:: "+stateId)

        if(intent.hasExtra("StateId")){
            var stateIdInt= intent.getStringExtra("StateId")
            stateId= stateIdInt ?.toInt()!!
            println("stateId:: push:: "+stateId)
            myPreferences.setStateID(stateId)
            isFromNotification = true
        }
        println("stateId:: after:: "+stateId+" isFromNotification:: "+isFromNotification)
        val userInfoModel: UserInfoModel? = myPreferences.getUserInfoModel()
        /*if(userInfoModel?.ProfileImage?.isNotEmpty() == true) {
            userInfoModel.ProfileImage?.let {
                Picasso.get()
                    .load(it).error(R.mipmap.logo_myplace)
                    .into(toolBinding.ivProfile)
            }
        }*/

        toolBinding.ivProfile.setOnClickListener {
            /*  var isUserLoggedIn = myPreferences.getUserLogin()
              if (isUserLoggedIn) {

                  val userId: Int = myPreferences.getUserId()
                  dashboardViewModel.fetchShareAccountDetails(userId)
                  getRecentSearchForHomeLandAndMyCollection()

                  showProfileDialog(this)
              } else {
                  AppUtils.showPleaseLoginDialog(
                      this,
                      this,
                      "Please login to view/edit profile"
                  )
              }*/
            println("isFromNotification:: "+isFromNotification)
            if(isFromNotification){
                val signUpIntent = Intent(this, DashboardActivity::class.java)
                signUpIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(signUpIntent)
            }else {
                finish()
            }

        }
        loadFragmentBasedOnPreviousState()
        binding.navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }

    private fun showSharingAccountDialog(checkEmailForSharingModel: CheckEmailForSharingModel) {

        val checkEmailStatusModel: CheckEmailStatusModel? =
            checkEmailForSharingModel.checkEmailStatusModel
        sharingAccountBinding = LayoutEmailSharingDialogBinding.inflate(layoutInflater)
        val shareAlertBuilder: AlertDialog.Builder = AlertDialog.Builder(this)
        shareAlertBuilder.setView(sharingAccountBinding.root)
        val alertDialog: AlertDialog = shareAlertBuilder.create()
        sharingAccountBinding.tvCancel.setOnClickListener {
            alertDialog.dismiss()
        }
        var isCheck = false
        sharingAccountBinding.ivCheck.setOnClickListener {
            isCheck = !isCheck
            if (isCheck) {
                sharingAccountBinding.ivCheck.setImageDrawable(
                    ContextCompat.getDrawable(
                        this@MainActivity,
                        R.drawable.check_share
                    )
                )
            } else {
                sharingAccountBinding.ivCheck.setImageDrawable(
                    ContextCompat.getDrawable(
                        this@MainActivity,
                        R.drawable.uncheck_share
                    )
                )
            }
        }
        if (checkEmailStatusModel != null) {
            sharingAccountBinding.tvFullName.text = checkEmailStatusModel.fullName
            sharingAccountBinding.tvEmail.text = checkEmailStatusModel.email
            sharingAccountBinding.tvIAgree.text =
                "I agree for sharing my favourites\nwith ${checkEmailStatusModel.fullName}"

            var shareProfilePic: String? = null

            if (checkEmailStatusModel.profilePic.isNullOrEmpty().not() &&checkEmailStatusModel.profilePic!!.contains("~")) {
                shareProfilePic =
                    checkEmailStatusModel.profilePic!!.replace("~", "")
                shareProfilePic = "${AppConstants.PROFILEPIC_BASE}${shareProfilePic}"

            } else {
                shareProfilePic = checkEmailStatusModel.profilePic
            }
            shareProfilePic?.let {
                Picasso.get().load(shareProfilePic).transform(CircleTransform())
                    .error(R.mipmap.logo_myplace)
                    .into(sharingAccountBinding.cvProfile, object : com.squareup.picasso.Callback {
                        override fun onSuccess() {

                        }

                        override fun onError(e: Exception?) {

                        }


                    })

            }

        }
        sharingAccountBinding.tvConfirm.setOnClickListener {
            if (isCheck) {
                alertDialog.dismiss()
                val userID: Int = myPreferences.getUserId();
                val jsonObj = JsonObject()
                if (checkEmailStatusModel != null) {
                    jsonObj.addProperty("OthersEmail", checkEmailStatusModel.email)
                    jsonObj.addProperty("FavouriteAdded", true)
                    jsonObj.addProperty("ShareAccount", 1)
                    dashboardViewModel.fetchShareAccountWithEmail(userID, jsonObj)
                }
            } else {
                showSnackbar(sharingAccountBinding.tvConfirm, "Please agree the sharing")
            }
        }
        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        alertDialog.show()


    }

    private fun showSnackbar(view: View, message: String) {
        val snack = Snackbar.make(view, message, Snackbar.LENGTH_LONG)
        snack.show()
    }


    private fun loadFragmentBasedOnPreviousState() {
        val pushNotificationModuleType= intent.getStringExtra("ModuleType")
        when {
            intent.getIntExtra("SELECTED ITEM", 0) == 1 || pushNotificationModuleType == "HomeAndLand_Main" -> {
                binding.navigation.selectedItemId = R.id.navigation_homeandland
                toolBinding.tvHeaderDesc.visibility = View.VISIBLE
                toolBinding.tvMy.text = getString(R.string.house)
                toolBinding.tvMy.visibility=View.VISIBLE
                toolBinding.tvHeaderDesc.text = getString(R.string.and_symbol)+getString(R.string.land)
                AppConstants.MAINHEADER=getString(R.string.house)+","+getString(R.string.and_land)
                changeFragment(
                    HomeLandRegionFragment(),
                    HomeLandRegionFragment::class.java.simpleName, Bundle()
                )
            }
            intent.getIntExtra("SELECTED ITEM", 0) == 5 || pushNotificationModuleType == "NewHomes_Main"-> {
                binding.navigation.selectedItemId = R.id.navigation_designs
                toolBinding.tvHeaderDesc.visibility = View.VISIBLE
                toolBinding.tvMy.text = getString(R.string.home)
                toolBinding.tvHeaderDesc.text = getString(R.string.design)
                AppConstants.MAINHEADER=getString(R.string.home)+","+getString(R.string.design)

                val bundle = Bundle()
                bundle.putBoolean(AppConstants.SELECT_FAVORITE, false)
                changeFragment(
                    MyCollectionPlacesFragment(),
                    MyCollectionPlacesFragment::class.simpleName,
                    bundle
                )

            }
            intent.getIntExtra("SELECTED ITEM", 0) == 2 || pushNotificationModuleType == "DisplayHomes_Main"-> {
                binding.navigation.selectedItemId = R.id.navigation_display
                toolBinding.tvMy.visibility = View.VISIBLE
                toolBinding.tvHeaderDesc.visibility = View.VISIBLE
                toolBinding.tvMy.text = getString(R.string.display)
                toolBinding.tvHeaderDesc.text =getString(R.string.homes)
                AppConstants.MAINHEADER=getString(R.string.display)+","+getString(R.string.homes)

                val bundle = Bundle()
                bundle.putBoolean(AppConstants.SELECT_FAVORITE, false)
                changeFragment(
                    MyDisplayHomeFragment(),
                    MyDisplayHomeFragment::class.simpleName,
                    bundle
                )

            }
            intent.getIntExtra("SELECTED ITEM", 0) == 6 -> {
                binding.navigation.selectedItemId = R.id.navigation_designs
                toolBinding.tvHeaderDesc.visibility = View.VISIBLE
                toolBinding.tvMy.text =getString(R.string.home)
                toolBinding.tvHeaderDesc.text = getString(R.string.design)
                AppConstants.MAINHEADER=getString(R.string.home)+","+getString(R.string.design)

                val bundle = Bundle()
                bundle.putBoolean(AppConstants.SELECT_FAVORITE, true)
                changeFragment(
                    MyCollectionPlacesFragment(),
                    MyCollectionPlacesFragment::class.simpleName,
                    bundle
                )

            }
            intent.getIntExtra("SELECTED ITEM", 0) == 9 -> {
                binding.navigation.selectedItemId = R.id.navigation_myfav
                toolBinding.tvHeaderDesc.visibility = View.VISIBLE
                toolBinding.tvMy.text = getString(R.string.my)
                toolBinding.tvHeaderDesc.text = getString(R.string.favourites_small)
                AppConstants.MAINHEADER=getString(R.string.my)+","+getString(R.string.favourites_small)

                val bundle = Bundle()
                bundle.putBoolean(AppConstants.SELECT_FAVORITE, true)
                /* toolBinding.tvHeaderDesc.visibility = View.GONE
                      AppConstants.isPackagesFromProfile = false
                  toolBinding.tvMy.text = "My Favourites"*/
                changeFragment(
                    MyFavouritesFragment(),
                    MyFavouritesFragment::class.java.simpleName, Bundle()
                )

            }

            intent.getIntExtra("SELECTED ITEM", 0) == 7 -> {
                binding.navigation.selectedItemId = R.id.navigation_homeandland
                toolBinding.tvHeaderDesc.text =getString(R.string.and_symbol)+ getString(R.string.land)
                toolBinding.tvMy.text = getString(R.string.house)
                AppConstants.MAINHEADER=getString(R.string.house)+","+getString(R.string.and_land)

                val bundle = Bundle()
                bundle.putBoolean(AppConstants.SELECT_FAVORITE, false)
                AppConstants.minimumPriceForPriceRange?.let { it1 ->
                    bundle.putDouble(
                        "MIN_PRICE",
                        it1
                    )
                }
                AppConstants.maximumPriceForPriceRange?.let { it1 ->
                    bundle.putDouble(
                        "MAX_PRICE",
                        it1
                    )
                }
                changeFragment(
                    HomeLandPlaceFragment(),
                    HomeLandPlaceFragment::class.simpleName,
                    bundle
                )

            }
            intent.getIntExtra("SELECTED ITEM", 0) == 8  -> {
                binding.navigation.selectedItemId = R.id.navigation_homeandland
                toolBinding.tvHeaderDesc.visibility = View.VISIBLE
                toolBinding.tvMy.text = getString(R.string.house)
                toolBinding.tvHeaderDesc.text =getString(R.string.and_symbol)+ getString(R.string.land)
                AppConstants.MAINHEADER=getString(R.string.house)+","+getString(R.string.and_land)

                val bundle = Bundle()
                bundle.putBoolean(AppConstants.SELECT_FAVORITE, true)
                AppConstants.minimumPriceForPriceRange?.let { it1 ->
                    bundle.putDouble(
                        "MIN_PRICE",
                        it1
                    )
                }
                AppConstants.maximumPriceForPriceRange?.let { it1 ->
                    bundle.putDouble(
                        "MAX_PRICE",
                        it1
                    )
                }
                changeFragment(
                    HomeLandPlaceFragment(),
                    HomeLandPlaceFragment::class.simpleName,
                    bundle
                )

            }
            intent.getIntExtra("SELECTED ITEM", 0) == 9 -> {
                binding.navigation.selectedItemId = R.id.navigation_designs
                toolBinding.tvHeaderDesc.visibility = View.VISIBLE
                toolBinding.tvMy.text = getString(R.string.house)
                toolBinding.tvHeaderDesc.text = getString(R.string.and_symbol)+getString(R.string.land)
                AppConstants.MAINHEADER=getString(R.string.house)+","+getString(R.string.and_land)

                val bundle = Bundle()
                bundle.putBoolean(AppConstants.SELECT_FAVORITE, false)
                changeFragment(
                    MyCollectionPlacesFragment(),
                    MyCollectionPlacesFragment::class.simpleName,
                    bundle
                )

            }
            intent.getIntExtra("SELECTED ITEM", 0) == 11 -> {
                binding.navigation.selectedItemId = R.id.navigation_display
                toolBinding.tvMy.visibility = View.VISIBLE
                toolBinding.tvHeaderDesc.visibility = View.VISIBLE
                toolBinding.tvMy.text = getString(R.string.display)
                toolBinding.tvHeaderDesc.text = getString(R.string.homes)
                AppConstants.MAINHEADER=getString(R.string.display)+","+getString(R.string.homes)

                val bundle = Bundle()
                bundle.putBoolean(AppConstants.SELECT_FAVORITE, false)
                changeFragment(
                    FavoriteDisplaysFragment(),
                    FavoriteDisplaysFragment::class.simpleName,
                    bundle
                )

            }
            else -> {
                binding.navigation.selectedItemId = R.id.navigation_designs
                toolBinding.tvHeaderDesc.visibility = View.VISIBLE
                //                toolBinding.tvMy.text = "My"
                //                toolBinding.tvHeaderDesc.text = "Collection"
                toolBinding.tvMy.text = "Home"
                toolBinding.tvHeaderDesc.text = "Designs"
                AppConstants.MAINHEADER=getString(R.string.home)+","+getString(R.string.design)

                MyCollectionDashboardFragment().FEATURE=MyCollectionDashboardFragment().FEATURE_STOREYS
                changeFragment(
                    MyCollectionDashboardFragment(),
                    MyCollectionDashboardFragment::class.java.simpleName, Bundle()
                )
            }
        }
    }

    fun loadFragment(fragment: Fragment, bundle: Bundle) {
        // load fragment
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        fragment.arguments = bundle

        if (fragment.isAdded) {
            return
        }
        transaction.add(R.id.fl_content, fragment)

        transaction.addToBackStack(null)
        transaction.commit()
    }

    public fun showProfileDialog(activity: Activity) {


        profileLayoutBinding = ProfileLayoutBinding.inflate(layoutInflater)
        val userInfoModel = myPreferences.getUserInfoModel()
        if(userInfoModel.ProfileImage?.isNotEmpty() == true) {
            userInfoModel.ProfileImage?.let {
                Picasso.get()
                    .load(it).error(R.mipmap.logo_myplace)
                    .into(profileLayoutBinding.ivProfile)
            }
        }
        dialogBuilder = AlertDialog.Builder(activity)
        dialogBuilder.setView(profileLayoutBinding.root)
        val alertDialog: AlertDialog = dialogBuilder.create()
        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
//            alertDialog.setCancelable(false)
        alertDialog.show()

        val window: Window = alertDialog.window!!
        val currentDialogAttributes: WindowManager.LayoutParams = window.getAttributes()
        val newDialogAttributes = WindowManager.LayoutParams()
        newDialogAttributes.copyFrom(currentDialogAttributes)
        newDialogAttributes.width = WindowManager.LayoutParams.MATCH_PARENT
        newDialogAttributes.height = WindowManager.LayoutParams.WRAP_CONTENT
        newDialogAttributes.gravity = Gravity.TOP
        window.attributes = newDialogAttributes
        profileLayoutBinding.profileBack.setOnClickListener {
            alertDialog.dismiss()
        }
        profileLayoutBinding.llMyCollectionRecentSearch.setOnClickListener {
            if (AppConstants.newHomeHashMap.size > 0) {
                AppConstants.newHomeHashMap.clear()
            }

            AppConstants.newHomeHashMap.putAll(recentSearchHashMap)

            alertDialog.dismiss()
            binding.navigation.selectedItemId = R.id.navigation_designs
            toolBinding.tvHeaderDesc.visibility = View.VISIBLE
            toolBinding.tvMy.text =getString(R.string.home)
            toolBinding.tvHeaderDesc.text = getString(R.string.design)
            AppConstants.MAINHEADER=getString(R.string.home)+","+getString(R.string.design)

            val bundle = Bundle()
            bundle.putBoolean(AppConstants.SELECT_FAVORITE, false)
            changeFragment(
                MyCollectionPlacesFragment(),
                MyCollectionPlacesFragment::class.simpleName,
                bundle
            )

        }
        profileLayoutBinding.tvMyCollectionSaveDesign.setOnClickListener {
            if (AppConstants.newHomeHashMap.size > 0) {
                AppConstants.newHomeHashMap.clear()
            }

            AppConstants.newHomeHashMap.putAll(recentSearchHashMap)

            alertDialog.dismiss()
            binding.navigation.selectedItemId = R.id.navigation_designs
            toolBinding.tvHeaderDesc.visibility = View.VISIBLE
            toolBinding.tvMy.text = getString(R.string.home)
            toolBinding.tvHeaderDesc.text = getString(R.string.design)
            AppConstants.MAINHEADER=getString(R.string.home)+","+getString(R.string.design)

            val bundle = Bundle()
            bundle.putBoolean(AppConstants.SELECT_FAVORITE, false)
            changeFragment(
                MyCollectionPlacesFragment(),
                MyCollectionPlacesFragment::class.simpleName,
                bundle
            )
        }
        profileLayoutBinding.llHomandlandsearchview.setOnClickListener {
            binding.navigation.selectedItemId = R.id.navigation_homeandland
            toolBinding.tvHeaderDesc.visibility = View.VISIBLE
            toolBinding.tvMy.text = getString(R.string.house)
            toolBinding.tvHeaderDesc.text =getString(R.string.and_symbol)+ getString(R.string.land)
            AppConstants.MAINHEADER=getString(R.string.house)+","+getString(R.string.and_land)

            val bundle = Bundle()
            bundle.putBoolean(AppConstants.SELECT_FAVORITE, false)
            minimumPriceForPriceRange?.let { it1 -> bundle.putDouble("MIN_PRICE", it1) }
            maximumPriceForPriceRange?.let { it1 -> bundle.putDouble("MAX_PRICE", it1) }
            recentSearchJsonModel?.let {
                AppConstants.setRecentSearchData(it)
            }
            AppConstants.isPackagesFromProfile = true
            changeFragment(
                HomeLandPlaceFragment(),
                HomeLandPlaceFragment::class.simpleName,
                bundle
            )
            alertDialog.dismiss()
        }
        profileLayoutBinding.llMyHomeAndLand.setOnClickListener {
            alertDialog.dismiss()
            binding.navigation.selectedItemId = R.id.navigation_homeandland
            toolBinding.tvHeaderDesc.visibility = View.VISIBLE
            toolBinding.tvMy.text = getString(R.string.house)
            toolBinding.tvHeaderDesc.text =getString(R.string.and_symbol)+ getString(R.string.land)
            AppConstants.MAINHEADER=getString(R.string.house)+","+getString(R.string.and_land)

            val bundle = Bundle()
            bundle.putBoolean(AppConstants.SELECT_FAVORITE, true)
            minimumPriceForPriceRange?.let { it1 -> bundle.putDouble("MIN_PRICE", it1) }
            maximumPriceForPriceRange?.let { it1 -> bundle.putDouble("MAX_PRICE", it1) }
            changeFragment(
                HomeLandPlaceFragment(),
                HomeLandPlaceFragment::class.simpleName,
                bundle
            )
        }

        profileLayoutBinding.llMyHomeAndDesign.setOnClickListener {
            alertDialog.dismiss()
            binding.navigation.selectedItemId = R.id.navigation_designs
            toolBinding.tvHeaderDesc.visibility = View.VISIBLE
            toolBinding.tvMy.text = getString(R.string.home)
            toolBinding.tvHeaderDesc.text = getString(R.string.design)
            AppConstants.MAINHEADER=getString(R.string.home)+","+getString(R.string.design)
            val bundle = Bundle()
            bundle.putBoolean(AppConstants.SELECT_FAVORITE, true)
            changeFragment(
                MyCollectionPlacesFragment(),
                MyCollectionPlacesFragment::class.simpleName,
                bundle
            )
        }
        profileLayoutBinding.llMyfav.setOnClickListener {
            alertDialog.dismiss()
            binding.navigation.selectedItemId = R.id.navigation_myfav
            toolBinding.tvHeaderDesc.visibility = View.VISIBLE
            toolBinding.tvMy.text = getString(R.string.my)
            toolBinding.tvHeaderDesc.text = getString(R.string.favourites)
            AppConstants.MAINHEADER=getString(R.string.my)+","+getString(R.string.favourites)
            displayToolbarViewModel.setMainHeader(AppConstants.MAINHEADER)

            val bundle = Bundle()
            bundle.putBoolean(AppConstants.SELECT_FAVORITE, true)
            changeFragment(
                MyFavouritesFragment(),
                MyFavouritesFragment::class.java.simpleName, Bundle()
            )
        }


        iv_profile_dialog = alertDialog.findViewById<ImageView>(R.id.iv_profile)
        iv_profile_dialog.setOnClickListener {
            alertDialog.dismiss()
            AppConstants.TotalMyFavs=AppConstants.HOME_AND_LAND_NOTIFICATION.toInt()+AppConstants.HOME_DESIGN_NOTIFICATION.toInt()+AppConstants.MY_SAVED_DISPLAY.toInt()

            profileLayoutBinding.favNotification.text=AppConstants.TotalMyFavs.toString()

            if(AppConstants.TotalMyFavs < 0){
                AppConstants.TotalMyFavs=0
            }
            displayToolbarViewModel.setFavouritesCount(AppConstants.TotalMyFavs)
        }

        val shareRecyclerView = alertDialog.findViewById<RecyclerView>(R.id.share_recycler_view)
        val ll_upload_image = alertDialog.findViewById<LinearLayout>(R.id.ll_upload_image)

        if (myPreferences.getLoginType() == "email") {
            ll_upload_image.visibility = View.GONE
        } else {
            ll_upload_image.visibility = View.GONE
        }
        profileProgress = alertDialog.findViewById(R.id.profileProgress)
        relative_share_list = alertDialog.findViewById(R.id.relative_share_list)

        profileLayoutBinding.shareAccountNotification.text =
            myPreferences.getShareAccountNotification().toString()
        profileLayoutBinding.tvMyCollectionNotification.text =
            AppConstants.MY_COLLECTION_NOTIFICATION

        profileLayoutBinding.tvHomeandlandNotification.text =
            AppConstants.HOME_AND_LAND_NOTIFICATION
        profileLayoutBinding.ivHomedesignNotification.text = AppConstants.HOME_DESIGN_NOTIFICATION


        /*profileLayoutBinding.tvHomeLandSavePackages.text =
        ("${myPreferences.getHomeAndLandnNotification().toString()} SAVED PACKAGES")*/

        if (AppConstants.HOME_AND_LAND_NOTIFICATION.toInt() == 0) {
            profileLayoutBinding.tvHomeLandSavePackages.text =
                ("NO SAVED PACKAGES")
        } else {
            profileLayoutBinding.tvHomeLandSavePackages.text =
                ("${AppConstants.HOME_AND_LAND_NOTIFICATION} SAVED PACKAGES")
        }



        profileLayoutBinding.tvMyCollectionSaveDesign.text =
            ("${AppConstants.MY_COLLECTION_NOTIFICATION} DESIGN")
        profileLayoutBinding.tvHomeDesignSaved.text =
            ("${AppConstants.HOME_DESIGN_NOTIFICATION} SAVED DESIGNS")
        profileLayoutBinding.tvMySavedDisplays.text =
            ("${AppConstants.MY_SAVED_DISPLAY} SAVED DISPLAYS")
        profileLayoutBinding.tvMydisplaysNotification.text = AppConstants.MY_SAVED_DISPLAY
        AppConstants.TotalMyFavs=AppConstants.HOME_AND_LAND_NOTIFICATION.toInt()+AppConstants.HOME_DESIGN_NOTIFICATION.toInt()+AppConstants.MY_SAVED_DISPLAY.toInt()
        if(AppConstants.TotalMyFavs < 0){
            AppConstants.TotalMyFavs=0
        }
        profileLayoutBinding.favNotification.text=AppConstants.TotalMyFavs.toString()
        var displayToolbarViewModel = ViewModelProviders.of(this)[DisplayToolbarViewModel::class.java]

        displayToolbarViewModel.setFavouritesCount(AppConstants.TotalMyFavs)
        shareRecyclerView.apply {
            shareAdapter =
                ShareListAdapter(this@MainActivity, ArrayList(), this@MainActivity)
            adapter = shareAdapter
        }

        //MYCOLLECTION
        profileLayoutBinding.llMyCollectionRecentSearch.setOnClickListener {
            startActivity(
                Intent(
                    this@MainActivity,
                    MainActivity::class.java
                ).putExtra("SELECTED ITEM", 5)
            )

        }


        val etFirstName = alertDialog.findViewById<EditText>(R.id.et_first_name)
        val etLastName = alertDialog.findViewById<EditText>(R.id.et_last_name)
        val etEmail = alertDialog.findViewById<EditText>(R.id.et_email)
        val etPhone = alertDialog.findViewById<EditText>(R.id.et_phone)


        profileLayoutBinding.llUpdate.setOnClickListener {
            if (etFirstName.text.toString().trim().isNotEmpty()) {
                if (etLastName.text.toString().trim().isNotEmpty()) {
                    var firstName: String = profileLayoutBinding.etFirstName.text.toString()
                    var lastName: String = profileLayoutBinding.etLastName.text.toString()
                    var name: String = profileLayoutBinding.etFirstName.text.toString()
                    var phoneNumber: String = profileLayoutBinding.etPhone.text.toString()

                    var email: String = ""
                    userInfoModel.Email?.let {
                        email = it
                    }
                    var loginType = myPreferences.getLoginType()
                    var loginPassword = myPreferences.getLoginPassword()
                    var jsonObject = JsonObject()
                    jsonObject.addProperty("LoginType", loginType)
                    jsonObject.addProperty("FirstName", firstName)
                    jsonObject.addProperty("LastName", lastName)
                    jsonObject.addProperty("Email", email)
                    jsonObject.addProperty("PhoneNumber", phoneNumber)
                    jsonObject.addProperty("Password", loginPassword)
                    dashboardViewModel.updateUserDetailsApi(jsonObject)

                } else {
                    showSnackbar(etFirstName, "Please enter your Last Name")
                }
            } else {
                showSnackbar(etFirstName, "Please enter your First Name")
            }

        }
        userInfoModel.FirstName?.let {
            etFirstName.setText(it.capitalize())
        }
        userInfoModel.LastName?.let {
            etLastName.setText(it.capitalize())
        }
        etFirstName.setText(etFirstName.text.toString()+" "+etLastName.text.toString())

        var email: String = ""
        userInfoModel.Email?.let {
            email = it
        }
        etEmail.setText(email)
        userInfoModel.PhoneNumber?.let {
            etPhone.setText(it)
        }

        val rl_my_details = alertDialog.findViewById<RelativeLayout>(R.id.rl_my_details)
        val el_mydetails = alertDialog.findViewById<ExpandableLayout>(R.id.el_mydetails)
        val spin_arrow_mydetails = alertDialog.findViewById<SpinnerImage>(R.id.iv_arrow_mydetails)

        val rl_share = alertDialog.findViewById<RelativeLayout>(R.id.rl_share)
        val tv_share = alertDialog.findViewById<TextView>(R.id.tv_share)
        val et_share = alertDialog.findViewById<AppCompatEditText>(R.id.et_share)
        tv_share.setOnClickListener {
            val emailId: String = et_share.text.toString().trim()
            if (emailId.isNotEmpty()) {
                et_share.text?.clear()
                dashboardViewModel.checkEmailForSharing(emailId)
            }
        }

        val el_share = alertDialog.findViewById<ExpandableLayout>(R.id.el_share)
        val el_appsettings = alertDialog.findViewById<ExpandableLayout>(R.id.el_appsettings)


        val rl_my_collection = alertDialog.findViewById<RelativeLayout>(R.id.rl_my_collection)
        val el_mycollection = alertDialog.findViewById<ExpandableLayout>(R.id.el_mycollection)


        val rl_home_and_land = alertDialog.findViewById<RelativeLayout>(R.id.rl_home_and_land)
        val el_homeandland = alertDialog.findViewById<ExpandableLayout>(R.id.el_homeandland)

        val rl_home_and_design = alertDialog.findViewById<RelativeLayout>(R.id.rl_home_and_design)
        val el_homedesign = alertDialog.findViewById<ExpandableLayout>(R.id.el_homedesign)


        val rl_app_setting = alertDialog.findViewById<RelativeLayout>(R.id.rl_app_setting)

        val iv_arrow_share = alertDialog.findViewById<SpinnerImage>(R.id.iv_arrow_share)
        val iv_arrow_mycollection =
            alertDialog.findViewById<SpinnerImage>(R.id.iv_arrow_mycollection)
        val iv_arrow_homeandland = alertDialog.findViewById<SpinnerImage>(R.id.iv_arrow_homeandland)
        val iv_arrow_homeanddesign =
            alertDialog.findViewById<SpinnerImage>(R.id.iv_arrow_homeanddesign)
        val iv_arrow_appsettings = alertDialog.findViewById<SpinnerImage>(R.id.iv_arrow_appsettings)
        val mydeailsview_line = alertDialog.findViewById<View>(R.id.mydetails_line)


        val rl_logout = alertDialog.findViewById<RelativeLayout>(R.id.rl_logout)
        val tv_select = alertDialog.findViewById<TextView>(R.id.tv_select)
        tv_select.setOnClickListener {
            ImagePickerActivity.clearCache(activity);
            Utility.onProfileImageClick(activity)
        }

        fun showHideViews(position: Int) {
            when (position) {
                0 -> {
                    el_share.collapse()
                    el_mycollection.collapse()
                    el_homeandland.collapse()
                    el_homedesign.collapse()
                    el_appsettings.collapse()
                    profileLayoutBinding.elMydisplay.collapse()

                    iv_arrow_share.reverse()
                    iv_arrow_mycollection.reverse()
                    iv_arrow_homeandland.reverse()
                    iv_arrow_homeanddesign.reverse()
                    iv_arrow_appsettings.reverse()
                    profileLayoutBinding.ivArrowMydisplay.reverse()

                    if (el_mydetails.isExpanded) {
                        spin_arrow_mydetails.reverse()
                        el_mydetails.collapse()
                        mydeailsview_line.visibility=View.VISIBLE

                    } else {
                        spin_arrow_mydetails.rotate()
                        el_mydetails.expand()
                        mydeailsview_line.visibility=View.VISIBLE

                    }

                }
                1 -> {
                    el_mydetails.collapse()
                    el_mycollection.collapse()
                    el_homeandland.collapse()
                    el_homedesign.collapse()
                    el_appsettings.collapse()
                    profileLayoutBinding.elMydisplay.collapse()
                    profileLayoutBinding.elMyfav.collapse()

                    spin_arrow_mydetails.reverse()
                    iv_arrow_mycollection.reverse()
                    iv_arrow_homeandland.reverse()
                    iv_arrow_homeanddesign.reverse()
                    iv_arrow_appsettings.reverse()
                    profileLayoutBinding.ivArrowMydisplay.reverse()
                    profileLayoutBinding.ivArrowFav.reverse()

                    if (el_share.isExpanded) {
                        iv_arrow_share.reverse()
                        el_share.collapse()
                    } else {
                        iv_arrow_share.rotate()
                        el_share.expand()
                    }
                }
                2 -> {
                    el_mydetails.collapse()
                    el_share.collapse()
                    el_homeandland.collapse()
                    el_homedesign.collapse()
                    el_appsettings.collapse()
                    profileLayoutBinding.elMydisplay.collapse()
                    profileLayoutBinding.elMyfav.collapse()

                    spin_arrow_mydetails.reverse()
                    iv_arrow_share.reverse()
                    iv_arrow_homeandland.reverse()
                    iv_arrow_homeanddesign.reverse()
                    iv_arrow_appsettings.reverse()
                    profileLayoutBinding.ivArrowMydisplay.reverse()
                    profileLayoutBinding.ivArrowFav.reverse()

                    if (el_mycollection.isExpanded) {
                        iv_arrow_mycollection.reverse()
                        el_mycollection.collapse()
                    } else {
                        iv_arrow_mycollection.rotate()
                        el_mycollection.expand()
                    }
                }
                3 -> {
                    el_mydetails.collapse()
                    el_share.collapse()
                    el_mycollection.collapse()
                    el_homedesign.collapse()
                    el_appsettings.collapse()
                    profileLayoutBinding.elMydisplay.collapse()
                    profileLayoutBinding.elMyfav.collapse()

                    spin_arrow_mydetails.reverse()
                    iv_arrow_share.reverse()
                    iv_arrow_mycollection.reverse()
                    iv_arrow_homeanddesign.reverse()
                    iv_arrow_appsettings.reverse()
                    profileLayoutBinding.ivArrowMydisplay.reverse()
                    profileLayoutBinding.ivArrowFav.reverse()

                    if (el_homeandland.isExpanded) {
                        iv_arrow_homeandland.reverse()
                        el_homeandland.collapse()
                    } else {
                        iv_arrow_homeandland.rotate()
                        el_homeandland.expand()
                    }
                }
                4 -> {
                    el_mydetails.collapse()
                    el_share.collapse()
                    el_mycollection.collapse()
                    el_homeandland.collapse()
                    el_appsettings.collapse()
                    profileLayoutBinding.elMydisplay.collapse()
                    profileLayoutBinding.elMyfav.collapse()

                    spin_arrow_mydetails.reverse()
                    iv_arrow_share.reverse()
                    iv_arrow_mycollection.reverse()
                    iv_arrow_homeandland.reverse()
                    iv_arrow_appsettings.reverse()
                    profileLayoutBinding.ivArrowFav.reverse()

                    profileLayoutBinding.ivArrowMydisplay.reverse()

                    if (el_homedesign.isExpanded) {
                        iv_arrow_homeanddesign.reverse()
                        el_homedesign.collapse()
                    } else {
                        iv_arrow_homeanddesign.rotate()
                        el_homedesign.expand()
                    }
                }
                5 -> {
                    el_mydetails.collapse()
                    el_share.collapse()
                    el_mycollection.collapse()
                    el_homeandland.collapse()
                    el_homedesign.collapse()
                    profileLayoutBinding.elMydisplay.collapse()
                    profileLayoutBinding.elMyfav.collapse()

                    spin_arrow_mydetails.reverse()
                    iv_arrow_share.reverse()
                    iv_arrow_mycollection.reverse()
                    iv_arrow_homeandland.reverse()
                    iv_arrow_homeanddesign.reverse()
                    profileLayoutBinding.ivArrowMydisplay.reverse()
                    profileLayoutBinding.ivArrowFav.reverse()

                    if (el_appsettings.isExpanded) {
                        iv_arrow_appsettings.reverse()
                        el_appsettings.collapse()
                    } else {
                        iv_arrow_appsettings.rotate()
                        el_appsettings.expand()
                    }
                }
                6 -> {
                    el_mydetails.collapse()
                    el_share.collapse()
                    el_mycollection.collapse()
                    el_homeandland.collapse()
                    el_homedesign.collapse()


                    spin_arrow_mydetails.reverse()
                    iv_arrow_share.reverse()
                    iv_arrow_mycollection.reverse()
                    iv_arrow_homeandland.reverse()
                    iv_arrow_homeanddesign.reverse()
                    profileLayoutBinding.ivArrowMydisplay.reverse()
                    profileLayoutBinding.ivArrowFav.reverse()
                    profileLayoutBinding.elMyfav.collapse()

                    if (profileLayoutBinding.elMydisplay.isExpanded) {
                        profileLayoutBinding.ivArrowMydisplay.reverse()
                        profileLayoutBinding.elMydisplay.collapse()
                    } else {
                        profileLayoutBinding.ivArrowMydisplay.rotate()
                        profileLayoutBinding.elMydisplay.expand()
                    }
                }
                7 -> {
                    el_mydetails.collapse()
                    el_share.collapse()
                    el_mycollection.collapse()
                    el_homeandland.collapse()
                    el_homedesign.collapse()

                    spin_arrow_mydetails.reverse()
                    iv_arrow_share.reverse()
                    iv_arrow_mycollection.reverse()
                    iv_arrow_homeandland.reverse()
                    iv_arrow_homeanddesign.reverse()
                    profileLayoutBinding.ivArrowMydisplay.reverse()

                    if (profileLayoutBinding.elMyfav.isExpanded) {
                        profileLayoutBinding.ivArrowFav.reverse()
                        profileLayoutBinding.elMyfav.collapse()
                    } else {
                        profileLayoutBinding.ivArrowFav.rotate()
                        profileLayoutBinding.elMyfav.expand()
                    }
                }

            }
        }
        rl_share.setOnClickListener {
            showHideViews(1)
            val userId: Int = myPreferences.getUserId()
            dashboardViewModel.fetchShareAccountDetails(userId)
        }
        rl_logout.setOnClickListener {
            Utility.logoutDialog(this)
        }
        rl_my_details.setOnClickListener {
            showHideViews(0)


        }
        rl_my_collection.setOnClickListener {
            if (isMyCollectionRecentSearchDataEmpty) {
                showHideViews(2)
                var userId: Int = myPreferences.getUserId()
                // var typeId:Int =mPreferences.
                var stateId: Int = myPreferences.getStateID()
                dashboardViewModel.getRecentSearchDataMyCollection(userId, 2, stateId)
            } else {
                AppUtils.showCustomCenterToast(this, "Recent Searches not available")
            }

        }
        rl_home_and_land.setOnClickListener {
            if (isHomeAndLandRecentSearchDataEmpty) {
                showHideViews(3)
                var userId: Int = myPreferences.getUserId()
                // var typeId:Int =mPreferences.
                var stateId: Int = myPreferences.getStateID()
                dashboardViewModel.getRecentSearchData(userId, 1, stateId)
            } else {
                AppUtils.showCustomCenterToast(this, "Recent Searches not available")
            }
        }
        rl_home_and_design.setOnClickListener {

            var notification = AppConstants.HOME_DESIGN_NOTIFICATION.toInt()
            if (notification == 0) {
                showNoFavoriteDialog()
            } else {
                showHideViews(4)
            }

        }
        rl_app_setting.setOnClickListener {
            showHideViews(5)

        }

        profileLayoutBinding.rlMyDisplays.setOnClickListener {
            var notification = AppConstants.MY_SAVED_DISPLAY.toInt()
            if (notification == 0) {
                showNoFavoriteDialog()
            } else {
                showHideViews(6)
            }
        }
        profileLayoutBinding.rlFav.setOnClickListener {
            /*var notification = AppConstants.MY_SAVED_DISPLAY.toInt()
            if (notification == 0) {
                showNoFavoriteDialog()
            } else {*/
//            commented by durga laer will uncomment
//                showHideViews(7)

            /* startActivity(
                 Intent(
                     this@MainActivity,
                     MainActivity::class.java
                 ).putExtra("SELECTED ITEM", 9)
             )

             alertDialog.dismiss()*/
            val currentFragment = supportFragmentManager.primaryNavigationFragment

            toolBinding.tvHeaderDesc.visibility = View.VISIBLE
            AppConstants.isPackagesFromProfile = false
            toolBinding.tvMy.text = "My"
            toolBinding.tvHeaderDesc.text = "Favourites"
            binding.navigation.selectedItemId = R.id.navigation_myfav
            AppConstants.MAINHEADER=getString(R.string.my)+","+getString(R.string.favourites_small)

            if(currentFragment !is MyFavouritesFragment) {

                changeFragment(
                    MyFavouritesFragment(),
                    MyFavouritesFragment::class.java.simpleName, Bundle()
                )
            }
            alertDialog.dismiss()
        }
        profileLayoutBinding.llMySavedDisplays.setOnClickListener {
            binding.navigation.selectedItemId = R.id.navigation_display
            toolBinding.tvMy.visibility = View.VISIBLE
            toolBinding.tvHeaderDesc.visibility = View.VISIBLE
            toolBinding.tvMy.text =  getString(R.string.display)
            toolBinding.tvHeaderDesc.text =  getString(R.string.homes)
            AppConstants.MAINHEADER=getString(R.string.display)+","+getString(R.string.homes)

            val bundle = Bundle()
            bundle.putBoolean(AppConstants.SELECT_FAVORITE, false)
            changeFragment(
                FavoriteDisplaysFragment(),
                FavoriteDisplaysFragment::class.simpleName,
                bundle
            )
            alertDialog.dismiss()
        }

        //RecentSearchApi
        getRecentSearchForHomeLandAndMyCollection()
    }

    public fun getRecentSearchForHomeLandAndMyCollection() {
        val userId: Int = myPreferences.getUserId()
        val stateId: Int = myPreferences.getStateID()
        getUserFavoritesDisplays()
        dashboardViewModel.getRecentSearchData(userId, 1, stateId)
        dashboardViewModel.getRecentSearchDataMyCollection(userId, 2, stateId)
    }
    public fun loadUserFavoritesDisplays() {
        val userId: Int = myPreferences.getUserId()
        val stateId: Int = myPreferences.getStateID()
        getUserFavoritesDisplays()
    }
    public fun loadUserFavoritesHomeLand() {
        val userId: Int = myPreferences.getUserId()
        val stateId: Int = myPreferences.getStateID()
        dashboardViewModel.getRecentSearchData(userId, 1, stateId)
    }
    public fun loadUserFavoritesMyCollection() {
        val userId: Int = myPreferences.getUserId()
        val stateId: Int = myPreferences.getStateID()
        dashboardViewModel.getRecentSearchDataMyCollection(userId, 2, stateId)
    }
    private fun setupViewModel() {
        dashboardViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(
                ApiHelperImpl(RetrofitBuilder.apiService),
                DatabaseHelperImpl(DatabaseBuilder.getInstance(this))
            )
        ).get(DashboardViewModel::class.java)

        displayHomeViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(
                ApiHelperImpl(RetrofitBuilder.apiService),
                DatabaseHelperImpl(DatabaseBuilder.getInstance(this))
            )
        ).get(DisplayHomesViewModel::class.java)

        singletonNameViewModelFactory = SingletonNameViewModelFactory()
        profilePicViewModel = ViewModelProviders.of(this, singletonNameViewModelFactory).get(
            ProfilePicViewModel::class.java
        )
        myHomeQuizViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(
                ApiHelperImpl(RetrofitBuilder.apiService),
                DatabaseHelperImpl(DatabaseBuilder.getInstance(AppController.getInstance()))
            )
        ).get(NewHomeQuizViewModel::class.java)
        //  myHomeQuizViewModel.fetchMyCollectionQuiz(stateId)

        if (myPreferences.getUserId() != 0) {
            dashboardViewModel.fetchUserDetails(myPreferences.getUserId())
        }

        setUpObserver()

    }

    private fun setUpObserver() {
        var displayToolbarViewModel = run {
            ViewModelProviders.of(this)[DisplayToolbarViewModel::class.java]

        } ?: throw Exception("Invalid Activity")
        dashboardViewModel.getUserDetailsLiveData().observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    dismissProgressDialog()

                    val userDetailResponseModel: UserDetailResponseModel? = it.data
                    if (userDetailResponseModel != null) {
                        if (userDetailResponseModel.userInfo.ProfileImage != null && userDetailResponseModel.userInfo.ProfileImage.contains("~")) {
                            placePicture =
                                userDetailResponseModel.userInfo.ProfileImage.replace("~", "")
                            placePicture = "${AppConstants.PROFILEPIC_BASE}${placePicture}"
                        } else {
                            val userInfoModel = myPreferences.getUserInfoModel()
                            placePicture = userInfoModel.ProfileImage?:""
                        }
                        /* if(placePicture!=null && placePicture!="") {
                             Picasso.get()
                                 .load(placePicture).error(R.mipmap.logo_myplace)
                                 .into(toolBinding.ivProfile)
                         }*/
                        val userInfoModel = UserInfoModel(
                            "",
                            userDetailResponseModel.userInfo.FirstName,
                            userDetailResponseModel.userInfo.LastName,
                            userDetailResponseModel.userInfo.Email,
                            placePicture,
                            "", userDetailResponseModel.userInfo.PhoneNumber
                        )
                        myPreferences.saveUserInfoModel(userInfoModel)
                    }
                }
                Status.LOADING -> {
                    showProgressDialog()
                }
                Status.ERROR -> {
                    dismissProgressDialog()
                }
            }
        })

        dashboardViewModel.getShareAccountLiveData().observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
//             profileProgress.visibility = View.GONE

                    val shareAccountModel: ShareAccountModel? = it.data
                    if (shareAccountModel != null) {

                        val shareList: ArrayList<ShareListModel> =
                            shareAccountModel.shareListObj.shareList
                        myPreferences.saveShareAccountNotificationNumber(shareAccountModel.shareListObj.count)

                        for (shareListModel in shareList) {
                            if (!shareListModel.favouriteAdded) {
                                Timber.e("Favorites Not Added :  ${shareListModel.email}")
                                showPendingShareAccountDialog(shareListModel)
                            }

                        }


                        if (shareList.size > 0) {
                            relative_share_list.visibility = View.VISIBLE
                            shareAdapter.setData(shareList)
                        } else {
                            relative_share_list.visibility = View.GONE
                        }
                    }
                }
                Status.LOADING -> {
//             profileProgress.visibility = View.VISIBLE
                }
                Status.ERROR -> {
//             profileProgress.visibility = View.GONE
                }
            }
        })
        dashboardViewModel.getCheckEmailSharingLiveData().observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
//             profileProgress.visibility = View.GONE
                    var checkEmailForSharingModel: CheckEmailForSharingModel? = it.data
                    if (checkEmailForSharingModel != null) {
                        showSharingAccountDialog(checkEmailForSharingModel)
                    }
                }
                Status.LOADING -> {
//             profileProgress.visibility = View.VISIBLE
                }
                Status.ERROR -> {
//             profileProgress.visibility = View.GONE
                }
            }
        })

        dashboardViewModel.getShareAccWithEmailLiveData().observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
//             profileProgress.visibility = View.GONE
                    val shareAccountModel: ShareAccountModel? = it.data
                    if (shareAccountModel != null) {
                        val shareList: ArrayList<ShareListModel> =
                            shareAccountModel.shareListObj.shareList

                        if (shareList.size > 0) {
                            relative_share_list.visibility = View.VISIBLE
                            shareAdapter.setData(shareList)
                        } else {
                            relative_share_list.visibility = View.GONE
                        }
                    }
                }
                Status.LOADING -> {
//             profileProgress.visibility = View.VISIBLE
                }
                Status.ERROR -> {
//             profileProgress.visibility = View.GONE
                }
            }
        })

        if (!AppConstants.isRecentDialogShowed){
            dashboardViewModel.getRecentSearchLiveData().observe(this, Observer {
                when (it.status) {
                    Status.SUCCESS -> {
//             profileProgress.visibility = View.GONE
                        var PREVIOUS_HOME_AND_LAND_NOTIFICATION=AppConstants.HOME_AND_LAND_NOTIFICATION
                        var regionsData: RecentSearchDataResponseModel? = it.data
                        if (regionsData != null && regionsData.status) {

                            recentSearchJsonModel = regionsData.searchJsonModel

                            regionsData.searchJsonModel?.let { model ->
                                minimumPriceForPriceRange = model.minPrice
                                maximumPriceForPriceRange = model.maxPrice

                            }

                            if (regionsData.status) {
                                isHomeAndLandRecentSearchDataEmpty = true
                                AppConstants.HOME_AND_LAND_NOTIFICATION =
                                    regionsData.userfavourites.toString()
                                var minPrice: Double =
                                    (regionsData.searchJsonModel?.minPrice ?: 0.0)
                                var maxPrice: Double =
                                    (regionsData.searchJsonModel?.maxPrice ?: 0.0)

                                var minValue = minPrice.toInt()
                                var maxValue = maxPrice.toInt()
                                var totalPrice: String =
                                    "$${minValue / 1000}K to $${maxValue / 1000}K"

                                AppConstants.MIN_PRICE =
                                    regionsData.searchJsonModel?.minPrice.toString()
                                AppConstants.MAX_PRICE =
                                    regionsData.searchJsonModel?.maxPrice.toString()

                                if (this::profileLayoutBinding.isInitialized) {

                                    profileLayoutBinding.tvHomeLandPrice.text = totalPrice
                                    if (regionsData.userfavourites == 0) {
                                        profileLayoutBinding.tvHomeLandSavePackages.text =
                                            ("NO SAVED PACKAGES")
                                        profileLayoutBinding.tvHomeandlandNotification.text = "0"
                                    } else {
                                        profileLayoutBinding.tvHomeLandSavePackages.text =
                                            ("${regionsData.userfavourites} SAVED PACKAGES")
                                        if (regionsData.userfavourites != null) {
                                            profileLayoutBinding.tvHomeandlandNotification.text =
                                                regionsData.userfavourites.toString()
                                        }


                                    }
//                         AppConstants.TotalMyFavs= regionsData.userfavourites

                                    if (regionsData.searchJsonModel?.regionsList?.size ?: 0 > 0) {

                                        if (regionsData.searchJsonModel?.regionsList?.get(0)?.regionName?.isNotEmpty() == true) {
                                            var regionData: String =
                                                regionsData.searchJsonModel?.regionsList!![0].regionName

                                            AppConstants.SELECTED_REGION = regionData
                                            val regionText = "Region &nbsp; <font color=#5c5e5e>$regionData</font>"
                                            profileLayoutBinding.tvHomeLandRegion.text = Html.fromHtml(regionText, HtmlCompat.FROM_HTML_MODE_LEGACY)

                                        }

                                        val bedRooms = regionsData.searchJsonModel?.BedRoomFilters?.filter { it.IsChecked }
                                        when(bedRooms?.size){
                                            1 ->{
                                                bedRooms.forEach {
                                                    profileLayoutBinding.tvBedrooms.text = it.displayName
                                                }
                                            }
                                            2 ->{
                                                profileLayoutBinding.tvBedrooms.text = "5+"
                                            }
                                            else ->{
                                                profileLayoutBinding.tvBedrooms.text = "All"
                                            }
                                        }
                                    }

                                    if (regionsData.searchJsonModel?.StoreyFilters?.size ?: 0 > 0) {
                                        var storeyString = ""
                                        val storeyFilter = regionsData.searchJsonModel?.StoreyFilters?.filter { it.IsChecked }
                                        if (storeyFilter!!.size>1) storeyString = "All"
                                        else{
                                            for (searchSubModel in regionsData.searchJsonModel?.StoreyFilters!!) {
                                                if (searchSubModel.IsChecked) {
                                                    storeyString = searchSubModel.displayName
                                                }
                                            }
                                        }
                                        profileLayoutBinding.tvStorey.text = storeyString
                                    }
                                }


                            } else {
                                showSnackbar(
                                    profileLayoutBinding.llMyCollection,
                                    regionsData.FetchData
                                )
                            }

                        } else {
                            AppConstants.HOME_AND_LAND_NOTIFICATION = "0"
                            isHomeAndLandRecentSearchDataEmpty = false
                        }

                        AppConstants.TotalMyFavs=AppConstants.TotalMyFavs-PREVIOUS_HOME_AND_LAND_NOTIFICATION.toInt()+AppConstants.HOME_AND_LAND_NOTIFICATION.toInt()

                        if(AppConstants.TotalMyFavs < 0){
                            AppConstants.TotalMyFavs=0
                        }

                        displayToolbarViewModel.setFavouritesCount(AppConstants.TotalMyFavs)

                    }
                    Status.LOADING -> {
//             profileProgress.visibility = View.VISIBLE
                    }
                    Status.ERROR -> {
                        isHomeAndLandRecentSearchDataEmpty = false
//             profileProgress.visibility = View.GONE
                    }
                }

            })
        }
        dashboardViewModel.getRecentSearchMyCollectionLiveData().observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
//             profileProgress.visibility = View.GONE
                    var regionsData: MyCollectionRecentSearchModel? = it.data
                    if (regionsData != null && regionsData.status) {
                        isMyCollectionRecentSearchDataEmpty = true
                        val newHomeJsonArrayList = ArrayList<NewHomeJsonObject>()
                        var  PREVIOUS_HOME_DESIGN_NOTIFICATION=AppConstants.HOME_DESIGN_NOTIFICATION
                        if (regionsData.searchJsonList.size > 0) {
                            for (myCollectionRecentSearchAnswerModel: MyCollectionRecentSearchAnswerModel in regionsData.searchJsonList) {

                                var newHomeJsonObject: NewHomeJsonObject? =
                                    if (myCollectionRecentSearchAnswerModel.minValue == null && myCollectionRecentSearchAnswerModel.maxValue == null) {
                                        NewHomeJsonObject(
                                            myCollectionRecentSearchAnswerModel.feature,
                                            myCollectionRecentSearchAnswerModel.question,
                                            myCollectionRecentSearchAnswerModel.answer
                                        )
                                    } else {
                                        NewHomeJsonObject(
                                            myCollectionRecentSearchAnswerModel.feature,
                                            myCollectionRecentSearchAnswerModel.question,
                                            myCollectionRecentSearchAnswerModel.answer,
                                            myCollectionRecentSearchAnswerModel.minValue,
                                            myCollectionRecentSearchAnswerModel.maxValue
                                        )
                                    }



                                if (newHomeJsonObject != null) {
                                    if (newHomeJsonObject.feature != "resultsCount") {
                                        newHomeJsonArrayList.add(newHomeJsonObject)
                                    } else {
                                        if (this::profileLayoutBinding.isInitialized) {
                                            profileLayoutBinding.tvMyCollectionNotification.text =
                                                newHomeJsonObject.answer.toString()
                                        }
                                    }

                                }

                            }
                        }
                        recentSearchHashMap[AppConstants.MYCOLLECTION_RECENT_SEARCH_KEY] =
                            newHomeJsonArrayList


                        AppConstants.HOME_DESIGN_NOTIFICATION =
                            regionsData.userfavourites.toString()
                        myCollectionRecentSearchText = ""

                        if (this::profileLayoutBinding.isInitialized) {
                            profileLayoutBinding.ivHomedesignNotification.text =
                                regionsData.userfavourites.toString()


                            if (regionsData.userfavourites == 0) {
                                profileLayoutBinding.tvHomeDesignSaved.text =
                                    ("NO SAVED DESIGNS")
                            } else {
                                profileLayoutBinding.tvHomeDesignSaved.text =
                                    ("${regionsData.userfavourites} SAVED DESIGNS")
                            }


                        }

                        AppConstants.TotalMyFavs=AppConstants.TotalMyFavs-PREVIOUS_HOME_DESIGN_NOTIFICATION.toInt()+AppConstants.HOME_DESIGN_NOTIFICATION.toInt()

                        if(AppConstants.TotalMyFavs < 0){
                            AppConstants.TotalMyFavs=0
                        }
                        displayToolbarViewModel.setFavouritesCount(AppConstants.TotalMyFavs)

                        if (regionsData.searchJsonList.size > 0) {

                            if (regionsData.searchJsonList.size > 0) {
                                for (resultCountModel in regionsData.searchJsonList) {
                                    if (resultCountModel.feature == "resultsCount") {
                                        if (resultCountModel.answer.isNotEmpty()) {
                                            // myCollectionRecentSearchText = resultCountModel.feature
                                            AppConstants.MY_COLLECTION_NOTIFICATION =
                                                resultCountModel.answer
                                            if (this::profileLayoutBinding.isInitialized) {
//                                         profileLayoutBinding.ivMyDetailsNotification.text =
//                                             resultCountModel.answer
                                                profileLayoutBinding.tvMyCollectionSaveDesign.text =
                                                    ("${resultCountModel.answer} DESIGNS")
                                            }
                                        } else {
                                            AppConstants.MY_COLLECTION_NOTIFICATION = "0"
                                        }
                                    }

                                }


                            }

                            val isLotWidthAvailable = regionsData.searchJsonList.any { it.feature == "Lot Width"}
                            for (myCollectionRecentSearchAnswerModel in regionsData.searchJsonList.filter { it.answer != "I don't mind" }) {
                                when (myCollectionRecentSearchAnswerModel.feature) {

                                    "Lot Width" -> {
                                        if (myCollectionRecentSearchAnswerModel.maxValue.isNotEmpty()) {
                                            var answer = AppUtils.roundRemainingString(
                                                myCollectionRecentSearchAnswerModel.maxValue
                                            )
                                            myCollectionRecentSearchText += "$answer" + "M"
                                        }
                                    }

                                    "Storeys" -> {
                                        var storey = ""
                                        if (myCollectionRecentSearchAnswerModel.answer == "1") {
                                            storey = if (isLotWidthAvailable) " | Single" else "Single"
                                        } else if (myCollectionRecentSearchAnswerModel.answer == "2") {
                                            storey = if (isLotWidthAvailable) " | Double" else "Double"
                                        }
                                        myCollectionRecentSearchText += storey

                                    }
                                    "No Of Bedrooms" -> {
                                        var bedRooms: String = ""

                                        if (myCollectionRecentSearchAnswerModel.answer == "3") {
                                            bedRooms = "3 Bed"
                                        } else if (myCollectionRecentSearchAnswerModel.answer == "4") {
                                            bedRooms = "4 Bed"

                                        } else if (myCollectionRecentSearchAnswerModel.answer == "5") {
                                            bedRooms = "5 Bed"
                                        }
                                        myCollectionRecentSearchText += " | $bedRooms"

                                    }
                                    "Grand Alfresco" -> {
                                        myCollectionRecentSearchText += " | Alfresco"

                                    }
                                    "Storage (more than 1 per room)" -> {
                                        myCollectionRecentSearchText += "| Storage (more than 1 per room)"

                                    }
                                    "European Laundry" -> {
                                        myCollectionRecentSearchText += " | European Laundry"
                                    }
                                    "Separate Kids Living Area" -> {
                                        myCollectionRecentSearchText += " | Separate Kids Living Area"
                                    }
                                    "Separate Living Area" -> {
                                        myCollectionRecentSearchText += " | Separate Living Area"
                                    }
                                    "Straight Corridor" -> {
                                        myCollectionRecentSearchText += " | Straight Corridor"

                                    }
                                    "Living/Meals Entire Rear" -> {
                                        myCollectionRecentSearchText += " | Living/Meals Entire Rear"

                                    }
                                    "Study" -> {
                                        myCollectionRecentSearchText += " | Study"

                                    }
                                    "Minor Bedrooms Wing" -> {
                                        myCollectionRecentSearchText += " | Minor Bedrooms Wing"

                                    }
                                    "Bedroom At Front" -> {
                                        myCollectionRecentSearchText += " | Bedroom At Front"

                                    }

                                    "Price" -> {
                                        myCollectionRecentSearchText += " | Price"

                                    }
                                    /* "resultsCount" -> {
                                         myCollectionRecentSearchText =
                                             "Take a Quick Survey to Find Your\nPerfect Design"
                                     }*/
                                }


                            }

                            if (regionsData.searchJsonList.isNotEmpty()) {
                                if (regionsData.searchJsonList.size == 2) {
                                    var model = regionsData.searchJsonList[1]
                                    if (model.feature == "resultsCount") {
                                        myCollectionRecentSearchText =
                                            "Take a Quick Survey to Find Your\nPerfect Design"
                                    }
                                } else if (regionsData.searchJsonList.size == 1) {
                                    var model = regionsData.searchJsonList[0]
                                    if (model.feature == "resultsCount") {
                                        myCollectionRecentSearchText =
                                            "Take a Quick Survey to Find Your\nPerfect Design"
                                    }
                                }
                            }


                        }
                        isMyCollectionRecentSearchDataEmpty =
                            myCollectionRecentSearchText.isNotEmpty()
                        if (this::profileLayoutBinding.isInitialized) {
                            profileLayoutBinding.tvMyCollectionQuiz.text =
                                myCollectionRecentSearchText
                        }

                    } else {
                        isMyCollectionRecentSearchDataEmpty = false
                    }
                }
                Status.LOADING -> {
//             profileProgress.visibility = View.VISIBLE
                }
                Status.ERROR -> {
//             profileProgress.visibility = View.GONE
                }
            }
        })
        dashboardViewModel.getProfilePicLiveData().observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
//             profileProgress.visibility = View.GONE
                    var regionsData: UpdateProfilePicModel? = it.data
                    if (regionsData != null && regionsData.Status == true) {
                        // Changed by durga 11/10/2022
                        var imageData=regionsData.file
                        if (imageData!!.contains("~")) {
                            placePicture = imageData.replace("~", "")
                            placePicture = "${AppConstants.PROFILEPIC_BASE}${placePicture}"
                        } else {
                            placePicture = imageData
                        }
                        /*  regionsData.file?.let { imageData ->
                              {

                                  if (imageData.contains("~")) {
                                      println("getProfilePicLiveData contains:: "+imageData)
                                      placePicture = imageData.replace("~", "")
                                      placePicture = "${AppConstants.PROFILEPIC_BASE}${placePicture}"
                                  } else {
                                      placePicture = imageData
                                  }

                              }

                          }*/
                        val appEvent = AppEvent(
                            AppEvent.UPDATE_PROFILE_PIC,
                            placePicture

                        )
                        EventBus.getDefault().post(appEvent)


                        var userInfoModel = myPreferences.getUserInfoModel()
                        myPreferences.saveUserInfoModel(
                            UserInfoModel(
                                "",
                                userInfoModel.FirstName,
                                userInfoModel.LastName,
                                userInfoModel.Email,
                                placePicture, "", ""
                            )
                        )

                        regionsData.Message?.let { it1 ->
                            showSnackbar(
                                profileLayoutBinding.tvSelect,
                                it1
                            )
                        }

                    }

                }
                Status.LOADING -> {
//             profileProgress.visibility = View.VISIBLE
                }
                Status.ERROR -> {
//             profileProgress.visibility = View.GONE
                }
            }
        })

        dashboardViewModel.getUpdateUser().observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
//             profileProgress.visibility = View.GONE
                    var regionsData: UserInfo? = it.data
                    if (regionsData != null) {

                        if (myPreferences.getUserId() != 0) {
                            dashboardViewModel.fetchUserDetails(myPreferences.getUserId())
                        }
                        if (regionsData.Message.isNotEmpty()) {
                            var customMessage = "Profile Updated Successfully"
                            AppUtils.showCustomCenterToast(
                                profileLayoutBinding.llUpdate.context,
                                customMessage
                            )

                            /* AppUtils.showCustomCenterToast(
                                 profileLayoutBinding.llUpdate.context,
                                 regionsData.Message
                             )*/
                        }

                    }

                }
                Status.LOADING -> {
//             profileProgress.visibility = View.VISIBLE
                }
                Status.ERROR -> {
//             profileProgress.visibility = View.GONE
                }
            }
        })


        displayHomeViewModel.getUserFavoritesDisplaysLiveData.observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    customProgressDialog?.dismissProgress()
                    if (it.data != null) {
                        var PreviousMY_SAVED_DISPLAY= AppConstants.MY_SAVED_DISPLAY.toInt()
                        if (it.data.status || !it.data.status) {
                            var totalResponse: UserFavoriteDisplaysResponseModel = it.data
                            if (totalResponse.userFavorites != null && totalResponse.userFavorites.size > 0) {
                                AppConstants.MY_SAVED_DISPLAY =
                                    totalResponse.userFavorites.size.toString()
                                Timber.e("My Saved Displays API ${AppConstants.MY_SAVED_DISPLAY}")
                                if (this::profileLayoutBinding.isInitialized) {
                                    profileLayoutBinding.tvMySavedDisplays.text =
                                        ("${AppConstants.MY_SAVED_DISPLAY} SAVED DISPLAYS")
                                    profileLayoutBinding.tvMydisplaysNotification.text =
                                        AppConstants.MY_SAVED_DISPLAY

                                    Timber.e("My Saved Displays1 ${AppConstants.MY_SAVED_DISPLAY}")
                                }
                            } else {
                                AppConstants.MY_SAVED_DISPLAY = "0"
                                if (this::profileLayoutBinding.isInitialized) {
                                    profileLayoutBinding.tvMydisplaysNotification.text = "0"
                                }
                            }

                            AppConstants.TotalMyFavs= AppConstants.TotalMyFavs-PreviousMY_SAVED_DISPLAY+AppConstants.MY_SAVED_DISPLAY.toInt()

                            if(AppConstants.TotalMyFavs < 0){
                                AppConstants.TotalMyFavs=0
                            }
                            displayToolbarViewModel.setFavouritesCount(AppConstants.TotalMyFavs)

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


    private val mOnNavigationItemSelectedListener: BottomNavigationView.OnNavigationItemSelectedListener =
        object : BottomNavigationView.OnNavigationItemSelectedListener {
            override fun onNavigationItemSelected(@NonNull item: MenuItem): Boolean {
                val currentFragment = supportFragmentManager.primaryNavigationFragment
                when (item.itemId) {
                    R.id.navigation_designs -> {
                        if(currentFragment !is MyCollectionDashboardFragment) {
                            AppConstants.isPackagesFromProfile = false
                            AppConstants.isFirstMyCollection = true
                            toolBinding.tvHeaderDesc.visibility = View.VISIBLE
                            toolBinding.tvMy.text = getString(R.string.home)
                            toolBinding.tvHeaderDesc.text = getString(R.string.design)
                            AppConstants.newHomeJsonObjectsList.clear()
                            AppConstants.MAINHEADER=getString(R.string.home)+","+getString(R.string.design)
                            changeFragment(
                                MyCollectionDashboardFragment(),
                                null, Bundle()
                            )
                            return true
                        }
                    }
                    R.id.navigation_homeandland -> {
                        if(currentFragment !is HomeLandRegionFragment) {
                            AppConstants.isPackagesFromProfile = false
                            AppConstants.HOMEANDLAND_TAP = 1
                            toolBinding.tvHeaderDesc.visibility = View.VISIBLE
                            toolBinding.tvMy.text = getString(R.string.house)
                            toolBinding.tvHeaderDesc.text = getString(R.string.and_symbol)+getString(R.string.land)
                            AppConstants.MAINHEADER=getString(R.string.house)+","+getString(R.string.land)
                            changeFragment(
                                HomeLandRegionFragment(),
                                HomeLandRegionFragment::class.java.simpleName, Bundle()
                            )

                            return true
                        }
                    }
                    R.id.navigation_display -> {
                        if(currentFragment !is MyDisplayHomeFragment) {
                            toolBinding.tvHeaderDesc.visibility = View.VISIBLE
                            AppConstants.isPackagesFromProfile = false
                            toolBinding.tvMy.text =  getString(R.string.display)
                            toolBinding.tvHeaderDesc.text = getString(R.string.homes)
                            AppConstants.MAINHEADER=getString(R.string.display)+","+getString(R.string.homes)
                            changeFragment(
                                MyDisplayHomeFragment(),
                                MyDisplayHomeFragment::class.java.simpleName, Bundle()
                            )
                            return true
                        }
                    }
                    R.id.navigation_myfav ->{

                        var isUserLoggedIn = myPreferences.getUserLogin()

                        if(currentFragment !is MyFavouritesFragment) {
                            if(isUserLoggedIn) {
                                toolBinding.tvHeaderDesc.visibility = View.VISIBLE
                                AppConstants.isPackagesFromProfile = false
                                toolBinding.tvMy.text = getString(R.string.my)
                                toolBinding.tvHeaderDesc.text =getString(R.string.favourites_small)
                                AppConstants.MAINHEADER=getString(R.string.my)+","+getString(R.string.favourites_small)
                                displayToolbarViewModel.setMainHeader(AppConstants.MAINHEADER)
                                changeFragment(
                                    MyFavouritesFragment(),
                                    MyFavouritesFragment::class.java.simpleName, Bundle()
                                )
                                return true
                            }else{
                                showPleaseLoginDialogFromMain("Please login to view favourites")
                            }

                            return false
                        }
                    }
                }
                return false
            }
        }

    fun showPleaseLoginDialogFromMain(message: String) {
        val dialogBuilder = AlertDialog.Builder(this)
        val title = SpannableString("MyPlace")
        title.setSpan(
            AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER),
            0,
            title.length,
            0
        )
        dialogBuilder.setTitle(title)//Should be in center
        dialogBuilder.setMessage(message)
            // if the dialog is cancelable
            .setCancelable(false)
            // positive button text and action
            .setPositiveButton(Html.fromHtml("<font color='${resources.getColor(R.color.orange_bg_3_1)}'>LOGIN</font>")) { dialog, _ ->
                dialog.cancel()

                LoginManager.getInstance().logOut()
                //Removing All Saved Local Data

                myPreferences.clearSession()
                val intent = Intent(this, LoginHomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                dialog.dismiss()
                finish()
            }
            .setNegativeButton(Html.fromHtml("<font color='#000000'>OK</font>")) { dialog, _ ->
                dialog.cancel()
            }

        // create dialog box
        val alert = dialogBuilder.create()
        // set title for alert dialog box
        //alert.setTitle("Select ${state.name}?")
        // show alert dialog
        alert.show()
        val negativeButton = alert.getButton(DialogInterface.BUTTON_NEGATIVE)
        val positiveButton = alert.getButton(DialogInterface.BUTTON_POSITIVE)

        negativeButton.isAllCaps = false
        positiveButton.isAllCaps = false

        negativeButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18F);
        positiveButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18F);
        val layoutParams = positiveButton.layoutParams as LinearLayout.LayoutParams
        layoutParams.weight = 10F
        positiveButton.layoutParams = layoutParams
        negativeButton.layoutParams = layoutParams
    }
    fun loadFragment(fragment: Fragment) {
        // load fragment
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        if (fragment.isAdded) {
            return
        }
        transaction.add(R.id.fl_content, fragment)
        //transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun setProfileImage(fileUri: Uri) {
        /*  Picasso.get()
          .load(fileUri.toString()).error(R.mipmap.logo_myplace)
          .into(toolBinding.ivProfile)*/

        if (this::profileLayoutBinding.isInitialized) {
            Picasso.get()
                .load(fileUri.toString()).error(R.mipmap.logo_myplace)
                .into(profileLayoutBinding.ivProfile);
        }
        Glide.with(this).asBitmap()
            .load(fileUri)
            .apply(
                RequestOptions().override(
                    800 // 800 pixels
                )
            )
            .listener(object : RequestListener<Bitmap> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any,
                    target: Target<Bitmap>,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }

                override fun onResourceReady(
                    resource: Bitmap,
                    model: Any,
                    target: Target<Bitmap>,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    val baos = ByteArrayOutputStream()
                    resource.compress(Bitmap.CompressFormat.JPEG, 80, baos)
                    val data = baos.toByteArray()
                    val base64 = Base64.encodeToString(
                        data,
                        Base64.DEFAULT
                    )
                    dashboardViewModel.updateImageApi(myPreferences.getUserId(), base64)
                    return false
                }
            }).submit()

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            CAMERA_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    val uri = data.getParcelableExtra<Uri>("path")
                    if (uri != null) {
                        setProfileImage(uri)
                    }
                }
            }
            else -> {
                Toast.makeText(this, "Unrecognized request code ", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun selectedFavoriteItem(shareListModel: ShareListModel) {
        Toast.makeText(this, "ItemFavorite Click", Toast.LENGTH_SHORT).show()
    }

    override fun selectedDeleteItem(shareListModel: ShareListModel) {
        shareDeleteConfirmationDialog(shareListModel)
    }

    private fun shareDeleteConfirmationDialog(shareListModel: ShareListModel) {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setMessage("Are you sure want to delete?")
            // if the dialog is cancelable
            .setCancelable(false)
            // positive button text and action
            .setPositiveButton(Html.fromHtml("<font color='${resources.getColor(R.color.orange_bg_3_1)}'>YES</font>")) { dialog, id ->
                dialog.dismiss()
                val userID: Int = myPreferences.getUserId();
                val jsonObj = JsonObject()
                if (shareListModel != null) {
                    jsonObj.addProperty("OthersEmail", shareListModel.email)
                    jsonObj.addProperty("FavouriteAdded", shareListModel.favouriteAdded)
                    jsonObj.addProperty("ShareAccount", 0)
                    dashboardViewModel.fetchShareAccountWithEmail(userID, jsonObj)
                }
            }
            // negative button text and action
            .setNegativeButton(Html.fromHtml("<font color='${resources.getColor(R.color.orange_bg_3_1)}'>NO</font>"), DialogInterface.OnClickListener { dialog, id ->
                dialog.cancel()
            })

        // create dialog box
        val alert = dialogBuilder.create()
        // set title for alert dialog box
        alert.setTitle("Delete")
        // show alert dialog
        alert.show()
    }

    override fun onBackPressed() {
        if(isFromNotification){
            val signUpIntent = Intent(this, DashboardActivity::class.java)
            signUpIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(signUpIntent)
        }else {
            val fragment = supportFragmentManager.findFragmentById(R.id.fl_content)
//            displayToolbarViewModel.setMainHeader(AppConstants.MAINHEADER)
            val displayFragmentContainer =
                supportFragmentManager.findFragmentById(R.id.fragment_container)
            val displayHomesfragmentContainer =
                supportFragmentManager.findFragmentById(R.id.fl_content)

            if (displayFragmentContainer != null) {
                val tag = displayFragmentContainer!!.tag
                if (tag == DisplayDetailFragment()::class.simpleName || tag == PopularAndSuggestedFragment()::class.simpleName) {
                    displayToolbarViewModel.setMainHeaderForDisplayHomes(
                        getString(R.string.display) + "," + getString(
                            R.string.homes
                        )
                    )
                }
            }
            if (displayHomesfragmentContainer != null) {
                val tag = displayHomesfragmentContainer!!.tag
                if (tag == MycollectionDetailsFragment::class.simpleName) {
                    displayToolbarViewModel.setMainHeader(
                        getString(R.string.home) + "," + getString(
                            R.string.design
                        )
                    )
                } else if (tag == MyFavouritesFragment::class.simpleName) {
                    finish()
                } else if (tag == AppConstants.MyFavouriteFlow) {
                    displayToolbarViewModel.setMainHeader(
                        getString(R.string.my) + "," + getString(
                            R.string.favourites_small
                        )
                    )
                } else if (tag == HomeLandPlaceFragment::class.simpleName || tag == HomeLandPriceRangeFragment()::class.simpleName) {

                    displayToolbarViewModel.setMainHeader(
                        getString(R.string.house) + "," + getString(
                            R.string.and_land
                        )

                    )
                }
            }
            if (fragment !is OnBackPressedListener || !(fragment as OnBackPressedListener).onBackPressed()) {
                super.onBackPressed()
            }
        }
    }


    interface IUpdateData {
        fun updateData()
    }

    interface OnBackPressedListener {
        fun onBackPressed(): Boolean
    }

    interface RegionUpdateApi {
        fun updateRegionApi()
    }


    fun fetchFirstMyCollectionQuizFragment(feature: String): Fragment {
        when (feature) {
            "Storeys" -> {
                firstFragment = StoreysMyCollectionFragment()
            }
            "No Of Bedrooms" -> {
                firstFragment = BedroomsMyCollectionsFragment()
            }
            "Grand Alfresco" -> {
                firstFragment = AllfrescoMyCollectionFragment()
            }
            "Storage (more than 1 per room)" -> {
                firstFragment = LotsOfStorageFragment()
            }
            "European Laundry" -> {
                firstFragment = EuropeanCupboardFragment()
            }
            "Separate Kids Living Area" -> {
                firstFragment = SeparateKidsFragment()
            }
            "Separate Living Area" -> {
                firstFragment = SeparateLivingFragment()
            }
            "Straight Corridor" -> {
                firstFragment = StarightCorridorFragment()
            }
            "Living/Meals Entire Rear" -> {
                firstFragment = LivingMealsCollectionFragment()
            }
            "Study" -> {
                firstFragment = MyCollectionStudyFragment()
            }
            "Minor Bedrooms Wing" -> {
                firstFragment = BedroomsGroupedFragment()
            }
            "Bedroom At Front" -> {
                firstFragment = MasterBedroomMyCollectionFragment()
            }
            "Lot Width" -> {
                firstFragment = HowWideIsYourLotFragment()
            }
            "Price" -> {
                firstFragment = BedroomsMyCollectionsFragment()
            }
            else ->{
                firstFragment = MyCollectionPlacesFragment()
            }


        }
        return firstFragment
    }

    fun removeLastElementOfHashMap() {
        val listKeys: LinkedList<String> = LinkedList()
        val listKeysToolHeader: LinkedList<String> = LinkedList()
        listKeys.addAll(AppConstants.newHomeHashMap.keys)
        listKeysToolHeader.addAll(AppConstants.toolheaderHashMap.keys)
        if (listKeys.size > 0) {
            AppConstants.newHomeHashMap.remove(listKeys.last)
        }
        if (listKeysToolHeader.size > 0) {
            AppConstants.toolheaderHashMap.remove(listKeysToolHeader.last)
        }
    }

    fun showToolHeadText(): String {
        var toolHeader = ""

        if (AppConstants.toolheaderHashMap.keys.size > 0) {
            val myVeryOwnIterator: Iterator<*> = AppConstants.toolheaderHashMap.keys.iterator()
            while (myVeryOwnIterator.hasNext()) {
                val key = myVeryOwnIterator.next() as String
                var value = AppConstants.toolheaderHashMap[key]
                toolHeader += value

            }
        }
        return toolHeader


    }

    fun showToolHeadBreadCrumbt(): String {
        var toolHeader = ""

        if (AppConstants.toolheaderHashMap.keys.size > 0) {
            val myVeryOwnIterator: Iterator<*> = AppConstants.toolheaderHashMap.keys.iterator()
            while (myVeryOwnIterator.hasNext()) {
                val key = myVeryOwnIterator.next() as String
                var value = AppConstants.toolheaderHashMap[key]
                toolHeader += value

            }
        }
        return toolHeader


    }

    fun getToolListInHomeAndLand(
        toolHeaderHashMap: HashMap<String, String>,
        currentToolbarString: String
    ): ArrayList<ToolBarHeaderModel> {
        var toolHeaderList: ArrayList<ToolBarHeaderModel> = ArrayList()
        if (toolHeaderHashMap.keys.size > 0) {
            val myVeryOwnIterator: Iterator<*> =
                toolHeaderHashMap.keys.iterator()
            while (myVeryOwnIterator.hasNext()) {
                val key = myVeryOwnIterator.next() as String
                val value = toolHeaderHashMap[key].toString()
                if (key == currentToolbarString) {
                    toolHeaderList.add(ToolBarHeaderModel(value, true))
                } else {
                    toolHeaderList.add(ToolBarHeaderModel(value, false))
                }

            }
        }
        return toolHeaderList
    }

    fun showSnakbar(view: View, message: String) {
        /* val snack = Snackbar.make(view, message, Snackbar.LENGTH_LONG)
         snack.show()*/
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show()

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onAppEvent(event: AppEvent) {

        if (event.type == AppEvent.UNAUTHARIZED) {
            showCustomDialog()
        } else if (event.type == AppEvent.UPDATE_PROFILE_PIC) {
            /* Picasso.get()
              .load(event.data).error(R.mipmap.logo_myplace)
              .into(toolBinding.ivProfile)*/

            if (this::profileLayoutBinding.isInitialized) {
                Picasso.get()
                    .load(event.data).error(R.mipmap.logo_myplace)
                    .into(profileLayoutBinding.ivProfile);
            }


        } else if (event.type == AppEvent.DISPLAYHOMES_EVENT) {
            binding.navigation.selectedItemId = R.id.navigation_display
            toolBinding.tvMy.visibility = View.VISIBLE
            toolBinding.tvHeaderDesc.visibility = View.VISIBLE
            toolBinding.tvMy.text =  getString(R.string.display)
            toolBinding.tvHeaderDesc.text = getString(R.string.homes)
            AppConstants.MAINHEADER=getString(R.string.display)+","+getString(R.string.homes)

            val bundle = Bundle()
            bundle.putBoolean(AppConstants.SELECT_FAVORITE, false)

            changeFragment(
                MyDisplayHomeFragment(),
                MyDisplayHomeFragment::class.simpleName,
                bundle
            )
        }
    }

    override fun onStart() {
        super.onStart()
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
    }

    fun changeFragment(fragment: Fragment?, tagFragmentName: String?, bundle: Bundle) {
        val mFragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = mFragmentManager.beginTransaction()

        val currentFragment: Fragment? = mFragmentManager.primaryNavigationFragment
        if (currentFragment != null) {
            fragmentTransaction.hide(currentFragment)
        }
        var fragmentTemp: Fragment? = mFragmentManager.findFragmentByTag(tagFragmentName)
        if (fragmentTemp == null) {
            fragmentTemp = fragment
            if (fragmentTemp != null) {
                fragmentTemp.arguments = bundle
                fragmentTransaction.replace(R.id.fl_content, fragmentTemp,tagFragmentName)

            }
        } else {
            fragmentTransaction.show(fragmentTemp)
        }
        fragmentTransaction.setPrimaryNavigationFragment(fragmentTemp)
        fragmentTransaction.setReorderingAllowed(true)
        fragmentTransaction.commitNowAllowingStateLoss()
    }

    override fun hideProfilePic(hideProfilePic: Boolean) {
        if (hideProfilePic) {
            if (this::toolBinding.isInitialized) {
                toolBinding.ivProfile.visibility = View.VISIBLE
            }

        } else {
            if (this::toolBinding.isInitialized) {
                toolBinding.ivProfile.visibility = View.VISIBLE
            }
        }

    }

    public fun clearFilterData() {
        AppConstants.FILTER_SELECTED_STOREY = -1
        AppConstants.FILTER_MIN_PRICE = ""
        AppConstants.FILTER_MAX_PRICE = ""
        AppConstants.FILTER_Bathrooms = null
        AppConstants.FILTER_BedRooms = null
        AppConstants.FILTER_CarSpaces = null
        AppConstants.FILTER_BEDROOM_COUNT = 0
    }

    open fun changeHelpText(helpTextContent: String) {

    }

    override fun changeHelpText(position: Int) {

    }

    private fun showPendingShareAccountDialog(shareListModel: ShareListModel) {
        sharingAccountBinding = LayoutEmailSharingDialogBinding.inflate(layoutInflater)
        val shareAlertBuilder: AlertDialog.Builder = AlertDialog.Builder(this)
        shareAlertBuilder.setView(sharingAccountBinding.root)
        val alertDialog: AlertDialog = shareAlertBuilder.create()
        sharingAccountBinding.tvCancel.setOnClickListener {
            alertDialog.dismiss()
        }
        var isCheck = false
        sharingAccountBinding.ivCheck.setOnClickListener {
            isCheck = !isCheck
            if (isCheck) {
                sharingAccountBinding.ivCheck.setImageDrawable(
                    ContextCompat.getDrawable(
                        this@MainActivity,
                        R.drawable.check_share
                    )
                )
            } else {
                sharingAccountBinding.ivCheck.setImageDrawable(
                    ContextCompat.getDrawable(
                        this@MainActivity,
                        R.drawable.uncheck_share
                    )
                )
            }
        }
        sharingAccountBinding.tvFullName.text = shareListModel.fullName
        sharingAccountBinding.tvEmail.text = shareListModel.email
        sharingAccountBinding.tvIAgree.text =
            "I agree for sharing my favourites\nwith ${shareListModel.fullName}"

        Picasso.get().load(R.drawable.profile_user).error(R.mipmap.logo_myplace)
            .into(sharingAccountBinding.cvProfile, object : com.squareup.picasso.Callback {
                override fun onSuccess() {
                }

                override fun onError(e: Exception?) {
                }


            })

        sharingAccountBinding.tvConfirm.setOnClickListener {
            if (isCheck) {
                alertDialog.dismiss()
                val userID: Int = myPreferences.getUserId();
                val jsonObj = JsonObject()
                if (shareListModel != null) {
                    jsonObj.addProperty("OthersEmail", shareListModel.email)
                    jsonObj.addProperty("FavouriteAdded", true)
                    jsonObj.addProperty("ShareAccount", 2)
                    dashboardViewModel.fetchShareAccountWithEmail(userID, jsonObj)
                }
            } else {
                showSnackbar(sharingAccountBinding.tvConfirm, "Please agree the sharing")
            }
        }
        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        alertDialog.show()

    }


}