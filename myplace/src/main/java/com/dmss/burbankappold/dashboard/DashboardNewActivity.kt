package com.dmss.burbankappold.dashboard

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.text.Html
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import com.dmss.burbankappold.BaseActivity
import com.dmss.burbankappold.R
import com.dmss.burbankappold.dashboard.ui.details.DetailsFragment
import com.dmss.burbankappold.dashboard.ui.sideMenu.MyAppointmentsActivity
import com.dmss.burbankappold.dashboard.ui.sideMenu.MyHistoryActivity
import com.dmss.burbankappold.dashboard.ui.sideMenu.MyNotificationActivity
import com.dmss.burbankappold.dashboard.ui.sideMenu.MySettingsActivity
import com.dmss.burbankappold.dashboard.ui.sideMenu.contactUs.ContactUsActivity
import com.dmss.burbankappold.dashboard.ui.sideMenu.contactUs.NotesData
import com.dmss.burbankappold.dashboard.ui.sideMenu.support.SupportActivity
import com.dmss.burbankappold.databinding.ActivityDashboardNewBinding
import com.dmss.burbankappold.network.ApiRepository
import com.dmss.burbankappold.utils.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import common.AppController
import common.Common
import common.MyPlaceDataBase
import common.Utils
import models.photos.PhotosDataItem


class DashboardNewActivity : BaseActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController
    public lateinit var binding: ActivityDashboardNewBinding
    private lateinit var myPlaceDataBase: MyPlaceDataBase
    private lateinit var viewModel: SharedViewModel
    var bottomNavigation: BottomNavigationView? = null

    var controller: AppController? = null
    private val isFromPhoto: Boolean by lazy {
        intent.getBooleanExtra(BundleKey.PHOTO, false)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardNewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        myPlaceDataBase = MyPlaceDataBase(this)
        initData()
        initToolBar()
        setupNavController()
        setupDrawerNavigation()
        setupBottomNavigation()
        setupMenuBottomNavigation()
        setDrawerMenuClickListener()
        AppConstants.validateVersionCode(this) {
            if (!it) {
                requestProfileDetailsApi()
            }
        }
        if (isFromPhoto){
            Handler().postDelayed({
                navigateTo(R.id.action_nav_home_to_photo_fragmnet)
            }, 500)
        }

    }


    fun initToolBar() {
        binding.contentMain.toolbar.constraint.setBackgroundColor(Color.TRANSPARENT)
        binding.contentMain.toolbar.ivNavMenu.show()
        binding.contentMain.toolbar.ivToolBarBack.hide()
        binding.contentMain.toolbar.tvToolbarTitle.changeIconColor(this, R.color.new_gray_color)
        binding.contentMain.toolbar.ivNavMenu.changeIconColor(this, R.color.new_gray_color)
        binding.contentMain.toolbar.ivChat.show()
//        binding.contentMain.toolbar.ivChat.hide()

        binding.contentMain.toolbar.ivChat.changeIconColor(this, R.color.new_gray_color)
        setBottomSelection()
    }

    private fun requestProfileDetailsApi() {
        try {
            val id = AppController.controller.userProfile.userDetailses[0].id
            val jsonObject = JsonObject()
            jsonObject.addProperty("Id", id)
            ApiRepository.requestProfileDetails(jsonObject){ isSuccess, data ->
                if (isSuccess){
//                    println("data?.ProfilePicPath:: "+data?.ProfilePicPath?:"")
                    PrefsHelper.profileUrl = data?.ProfilePicPath?:""
                    AppController.lastUpdatedDate = data?.UpdatedDate
                    updateNavProfileStatus()
                    data?.NotificationTypes?.forEach{
                        when(it.Name) {
                            "Photo Added" ->{
                                AppController.controller.isPhotoNotifications = true
                            }
                            "Stage Completion" ->{
                                AppController.controller.isStageNotifications = true
                            }
                            "Stage Change" ->{
                                AppController.controller.isProgressNotifications = true
                            }
                        }
                    }
                }
            }
        }catch (e : Exception){
            e.printStackTrace()
        }
    }

    private fun setDrawerMenuClickListener() {
        binding.leftDrawerMenu.clChangeJobNumber.setOnClickListener {
            val intent = Intent(this, DashboardNewActivity::class.java)
            startActivity(intent)
            AppController.setPreference(this,AppController.SELECTEDJOBNUMBER,AppController.defaultValue)
            finish()
            toggleLeftDrawer()
        }
        binding.leftDrawerMenu.clMySettings.setOnClickListener {
            startActivity(Intent(this, MySettingsActivity::class.java))
            toggleLeftDrawer()
        }
        binding.leftDrawerMenu.clMySupport.setOnClickListener {
            startActivity(Intent(this, SupportActivity::class.java))
            toggleLeftDrawer()
        }
        binding.leftDrawerMenu.clMyAppointments.setOnClickListener {
            startActivity(Intent(this, MyAppointmentsActivity::class.java))
            toggleLeftDrawer()
        }
        binding.leftDrawerMenu.clHistory.setOnClickListener {
            startActivity(Intent(this, MyHistoryActivity::class.java))
            toggleLeftDrawer()
        }
        binding.leftDrawerMenu.clMyContacts.setOnClickListener {
            startActivity(Intent(this, DetailsFragment::class.java))
            toggleLeftDrawer()
        }
        binding.leftDrawerMenu.clMyNotifications.setOnClickListener {
            startActivity(Intent(this, MyNotificationActivity::class.java))
            toggleLeftDrawer()
        }
        binding.contentMain.toolbar.ivChat.setOnClickListener {
            startActivity(Intent(this, ContactUsActivity::class.java))
        }
        binding.drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener{
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {}
            override fun onDrawerOpened(drawerView: View) {
                setNotificationCountToDrawerLayout()
            }
            override fun onDrawerClosed(drawerView: View) {}
            override fun onDrawerStateChanged(newState: Int) {}

        })
    }

    private fun setNotificationCountToDrawerLayout(){
//        binding.leftDrawerMenu.tvNotificationsCount.text = PrefsHelper.notificationCount
        if(PrefsHelper.notificationCount != "0" && PrefsHelper.notificationCount != "") {
            var notificationCount = PrefsHelper.notificationCount
            if (notificationCount!="" && notificationCount.toInt() > 100) {
                notificationCount = "99+"
            }
            binding.leftDrawerMenu.tvNotificationsCount.text =notificationCount
        }
        binding.leftDrawerMenu.tvNotificationsCount.visibility = if (PrefsHelper.notificationCount == "" || PrefsHelper.notificationCount == "0") View.GONE else View.VISIBLE
        binding.leftDrawerMenu.ivPhoto.loadUrlUsingGlide(PrefsHelper.profileUrl)
    }

    fun updateToolBarTitle(title: String? = getString(R.string.dashboard_app_name)) {
        binding.contentMain.toolbar.constraint.changeBackgroundColor(this)
        binding.contentMain.toolbar.ivNavMenu.show()
        binding.contentMain.toolbar.ivNavMenu.changeIconColor(this)
        binding.contentMain.toolbar.tvToolbarTitle.changeIconColor(this)
        binding.contentMain.toolbar.ivToolBarBack.hide()
        binding.contentMain.toolbar.ivChat.show()
//        binding.contentMain.toolbar.ivChat.hide()

        binding.contentMain.toolbar.ivChat.changeIconColor(this)
        setBottomSelection()
    }

    fun initToolBarWithBackBackButton(title: String? = getString(R.string.dashboard_app_name), isTrasToolBar : Boolean = false) {
        if (isTrasToolBar) {
            binding.contentMain.toolbar.constraint.changeBackgroundColor(
                this,
                android.R.color.transparent
            )
            binding.contentMain.toolbar.ivToolBarBack.changeIconColor(this, R.color.new_gray_color)
            binding.contentMain.toolbar.tvToolbarTitle.changeIconColor(this, R.color.new_gray_color)
        }else{
            binding.contentMain.toolbar.constraint.changeBackgroundColor(this)
            binding.contentMain.toolbar.ivToolBarBack.changeIconColor(this)
            binding.contentMain.toolbar.tvToolbarTitle.changeIconColor(this)
        }
        binding.contentMain.toolbar.ivNavMenu.hide()
        binding.contentMain.toolbar.ivToolBarBack.show()
        binding.contentMain.toolbar.ivChat.show()
//        binding.contentMain.toolbar.ivChat.hide()

        binding.contentMain.toolbar.ivToolBarBack.setOnClickListener {
            onBackPressed()
        }
        viewModel = ViewModelProviders.of(this)[SharedViewModel::class.java]
        viewModel.selected.observe(this, Observer<String> { item ->
            // Update the UI using new item data
            binding.contentMain.toolbar.constraint.changeBackgroundColor(this,R.color.new_gray_color)
            binding.contentMain.toolbar.ivChat.show()
            binding.contentMain.toolbar.ivChat.changeIconColor(this, R.color.white)

            binding.contentMain.toolbar.ivToolBarBack.changeIconColor(this, R.color.white)
            binding.contentMain.toolbar.tvToolbarTitle.changeIconColor(this, R.color.white)


        })

    }

    fun updateNavProfileStatus(progress: Int = 0) {
//        println("progress:: $progress")
        binding.leftDrawerMenu.tvUsername.text =
            AppController.controller.userProfile.userDetailses[0].fullName
        var JOBNUMBER=""
        if( AppController.getPreference(this,AppController.PREVIOUSSELECTEDJOBNUMBER)!=AppController.defaultValue){
            JOBNUMBER=AppController.getPreference(this,AppController.PREVIOUSSELECTEDJOBNUMBER)
        }
        if(JOBNUMBER==""){
            JOBNUMBER=AppController.controller.userProfile.userDetails[0].myPlaceJobDetailses[0].jobNo
        }
        var initString= Html.fromHtml("Your home " +"<strong><font color='#FF6224'>"+JOBNUMBER+"</font></strong>"+" "+getString(R.string.your_home_is_currently_completed)+"<strong>${AppController.progres}%</strong> completed")
        binding.leftDrawerMenu.tvCompletedPercentage.text =initString
         binding.leftDrawerMenu.tvPageName.text =getString(R.string.build_process)
        binding.leftDrawerMenu.tvCompletedPercentage.setOnClickListener {
//            Common.showChangePhaseMessageAlert(this@DashboardNewActivity)

        }

        binding.leftDrawerMenu.ivPhoto.loadUrlUsingGlide(PrefsHelper.profileUrl)
    }
    private fun initData() {
        controller = applicationContext as AppController
        controller!!.analytics.dashboardLoadingEvent()
        var jobNubersList= AppController.controller.userProfile.userDetails[0].myPlaceJobDetailses
        if(jobNubersList.size==1){
            binding.leftDrawerMenu.clChangeJobNumber.visibility=View.GONE
            binding.leftDrawerMenu.jobNumberLine.visibility=View.GONE

        }

    /*    viewModel = this.run {
            ViewModelProviders.of(this)[SharedViewModel::class.java]
        } ?: throw Exception("Invalid Activity")
        viewModel.selected.observe(this, Observer<String> { item ->
            // Update the UI using new item data
            println("selected:: $item")
            binding.contentMain.toolbar.constraint.changeBackgroundColor(this,R.color.new_gray_color)
            binding.contentMain.toolbar.ivChat.hide()
            binding.contentMain.toolbar.ivToolBarBack.changeIconColor(this, R.color.white)
            binding.contentMain.toolbar.tvToolbarTitle.changeIconColor(this, R.color.white)

        })*/
        viewModel = ViewModelProviders.of(this)[SharedViewModel::class.java]


        viewModel.profileSubHeader.observe(this, Observer {
//            println("profileSubHeader Main:: $it     ${PrefsHelper.notificationCount}")
//            println("profileSubHeader Main Notofication count:: ${PrefsHelper.notificationCount}   ${AppController.progres}")

           /* binding.leftDrawerMenu.tvNotificationsCount.visibility = if(AppController.progres == 0)
                View.GONE else View.VISIBLE*/
            if(AppController.progres==0){
                binding.leftDrawerMenu.tvNotificationsCount.visibility=View.GONE
            }
            binding.leftDrawerMenu.tvCompletedPercentage.text =it

        })
        viewModel.financetab.observe(this,Observer<Boolean> {
            if(it){
                /*    binding.contentMain.bottomNavView.navFinanceButton.visibility=View.VISIBLE
                    binding.contentMain.bottomNavView.radioGroup.weightSum=5F*/
                binding.contentMain.llBottomNavViewFive.visibility=View.VISIBLE
                binding.contentMain.llBottomNavView.visibility=View.GONE

//                setFooterWidthHeightForFive()
            }else{
                /*  binding.contentMain.bottomNavView.navFinanceButton.visibility=View.GONE
                  binding.contentMain.bottomNavView.radioGroup.weightSum=4F*/
                binding.contentMain.llBottomNavViewFive.visibility=View.GONE
                binding.contentMain.llBottomNavView.visibility=View.VISIBLE
//                setFooterWidthHeightForFour()
            }
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun setupNavController() {
        navController = findNavController(R.id.nav_host_fragment_content_dashboard_new)
    }

    private fun setupDrawerNavigation() {
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_details, R.id.nav_photos,
                R.id.nav_documents, R.id.nav_finance
            ), binding.drawerLayout
        )
        binding.contentMain.toolbar.ivNavMenu.setOnClickListener {
            toggleLeftDrawer()
        }
        binding.leftDrawerMenu.ivNavClose.setOnClickListener {
            toggleLeftDrawer()
        }
        binding.leftDrawerMenu.ivPhoto.setOnClickListener {
            toggleLeftDrawer()
            startActivity(Intent(this, MyNotificationActivity::class.java))
        }
    }

    private fun toggleLeftDrawer() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }
    }
    private fun setupMenuBottomNavigation() {
        val navigationItemSelectedListener =
            BottomNavigationView.OnNavigationItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.navigation_home -> {
                        navigateTo(R.id.nav_home)
                        return@OnNavigationItemSelectedListener true
                    }
                    R.id.navigation_dashboard -> {
                        navigateTo(R.id.nav_details)
                        return@OnNavigationItemSelectedListener true
                    }
                    R.id.navigation_notifications -> {
                        navigateTo(R.id.nav_photos)
                        return@OnNavigationItemSelectedListener true
                    }
                    R.id.navigation_documents -> {
                        navigateTo(R.id.nav_documents)
                        return@OnNavigationItemSelectedListener true
                    }
                    R.id.navigation_finance -> {
                        navigateTo(R.id.nav_finance)
                        return@OnNavigationItemSelectedListener true
                    }
                }
                false
            }
        binding.contentMain.bottomNavMenuView.bottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener)
        binding.contentMain.bottomNavMenuViewFive.bottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener)

    }
    private fun setupBottomNavigation() {
        binding.contentMain.bottomNavView.navHomeButton.setOnClickListener {
            navigateTo(R.id.nav_home)

        }
        binding.contentMain.bottomNavView.navDetailsButton.setOnClickListener {
            navigateTo(R.id.nav_details)
        }
        binding.contentMain.bottomNavView.navPhotosButton.setOnClickListener {
            navigateTo(R.id.nav_photos)
        }
        binding.contentMain.bottomNavView.navDocumentsButton.setOnClickListener {
            navigateTo(R.id.nav_documents)
        }
        binding.contentMain.bottomNavView.navFinanceButton.setOnClickListener {
            navigateTo(R.id.nav_finance)
        }

        binding.contentMain.bottomNavViewFive.navHomeButton.setOnClickListener {
            navigateTo(R.id.nav_home)
        }
        binding.contentMain.bottomNavViewFive.navDetailsButton.setOnClickListener {
            navigateTo(R.id.nav_details)
        }
        binding.contentMain.bottomNavViewFive.navPhotosButton.setOnClickListener {
            navigateTo(R.id.nav_photos)
        }
        binding.contentMain.bottomNavViewFive.navDocumentsButton.setOnClickListener {
            navigateTo(R.id.nav_documents)
        }
        binding.contentMain.bottomNavViewFive.navFinanceButton.setOnClickListener {
            navigateTo(R.id.nav_finance)
        }

//        binding.contentMain.bottomNavView.navFinanceButton.visibility=View.GONE
//        binding.contentMain.bottomNavView.radioGroup.weightSum=4.5F


    }

    private fun navigateTo(resId: Int) {
        if (checkCurrentFragment(resId).not())
            navController.navigate(resId)
    }

    private fun checkCurrentFragment(id : Int): Boolean{
        val navController =
            Navigation.findNavController(this, R.id.nav_host_fragment_content_dashboard_new)
        return navController.currentDestination?.id == id
    }

    override fun onBackPressed() {
        onNavigateBackPressed()
    }

    private fun setBottomSelection(){
        val navController =
            Navigation.findNavController(this, R.id.nav_host_fragment_content_dashboard_new)
        when (navController.currentDestination?.id) {
            R.id.nav_home, R.id.progressDetailsFragment ->{
                binding.contentMain.bottomNavView.navHomeButton.isChecked = true
                binding.contentMain.bottomNavViewFive.navHomeButton.isChecked = true
                binding.contentMain.bottomNavViewFive.navHomeButton.isChecked = true


            }
            R.id.nav_details ->{
                binding.contentMain.bottomNavView.navDetailsButton.isChecked = true
                binding.contentMain.bottomNavViewFive.navDetailsButton.isChecked = true

            }
            R.id.nav_photos, R.id.nav_photo_view ->{
                binding.contentMain.bottomNavView.navPhotosButton.isChecked = true
                binding.contentMain.bottomNavViewFive.navPhotosButton.isChecked = true

            }
            R.id.nav_documents ->{
                binding.contentMain.bottomNavView.navDocumentsButton.isChecked = true
                binding.contentMain.bottomNavViewFive.navDocumentsButton.isChecked = true

            }
            R.id.nav_finance, R.id.financeDetailsFragment ->{
                binding.contentMain.bottomNavView.navFinanceButton.isChecked = true
                binding.contentMain.bottomNavViewFive.navFinanceButton.isChecked = true

            }
        }
    }

    override fun onResume() {
        super.onResume()

        // Hide for 3.7 release need to uncomment for chart functionality
        if(AppConstants.backFromContcatcus) {
            AppConstants.backFromContcatcus=false
//            requestApiClickHomeMasterContracts(AppConstants.AppCookieContactUs,this)
        }
        AppConstants.validateVersionCode(this) {
            if(it) {
                logoutIamBuilding()
            }
        }
    }
    private fun logoutIamBuilding(){
        controller?.analytics?.settingsLogoutButtonTouchEvent()
        controller?.isUserLoggedIn = false
        controller?.loggedInFromSocial = false
        controller?.my_Place_Details = null
        controller?.profilePicUrl = ""
        controller?.setProfileInfo("")
        AppConstants.financeItemStatus=false
        controller?.selectedJobPosition = 0
        AppConstants.filterList= emptyList()
        AppConstants.NoRecentPhotos=""
        AppConstants.PhotosSubheader=""
        AppConstants.AppCookieContactUs=""
        AppConstants.savedphotosList.clear()
        AppConstants.savedphotosList=mutableListOf<List<PhotosDataItem>>()
        AppController.setPreference(this,AppController.SELECTEDJOBNUMBER,AppController.defaultValue)
        AppController.setPreference(this,AppController.PREVIOUSSELECTEDJOBNUMBER,AppController.defaultValue)

        PrefsHelper.clearPrefs()
    }
    private fun onNavigateBackPressed() {
        val navController =
            Navigation.findNavController(this, R.id.nav_host_fragment_content_dashboard_new)
        when (navController.currentDestination?.id) {
            R.id.nav_home -> {
                finish()
//                finishAffinity()
            }
            else -> {
                super.onBackPressed()
            }
        }
    }

   /* fun requestApiClickHomeLoginBaseUrl(context: AppCompatActivity){
        if(AppConstants.AppCookieContactUs!=""){
            requestApiClickHomeMasterContracts(AppConstants.AppCookieContactUs, context)

        }else {
            var Password = AppController.controller?.my_Place_Details?.password
            var JobNo = AppController.controller?.my_Place_Details?.jobNumber
            var UserName = AppController.controller?.my_Place_Details?.username
            val jsonParser = JsonParser()
            val jsonString = "{contractNumber:$JobNo,userName:$UserName,password:$Password}"
            val jsonObjectObj = jsonParser.parse(jsonString).asJsonObject
            if (Utils.isNetworkAvailable(context)) {
                var dialog = Utils.getProgress(context)
                ApiRepository.requestContactUsLogin(jsonObjectObj) { isSuccess, contactData, loginUserData ->
                    dialog?.dismiss()
                    if (isSuccess) {
                        if (Utils.isNetworkAvailable(context)) {
                            SharedPrefHelper.setSharedOBJECT(context,context.getString(R.string.login_user_data),loginUserData)

                            requestApiClickHomeMasterContracts(contactData!!, context)
                        } else {

                        }
                    }
                }
            }
        }
    }*/
   fun requestApiClickHomeMasterContracts(cookie:String,context: Activity){
        val jsonParser = JsonParser()
        AppConstants.AppCookieContactUs=cookie
//        println("DashboardNew AppConstants.AppCookieContactUs:: "+AppConstants.AppCookieContactUs)
        val jsonString =AppConstants.jsonNotesString
        val jsonObjectObj = jsonParser.parse(jsonString).asJsonObject
        var dialog = Utils.getProgress(context)
        ApiRepository.requestContactMasterContracts(cookie,jsonObjectObj){ isSuccess, contactData ->
            dialog?.dismiss()
            if (isSuccess){
                val resjsonObjectObj = jsonParser.parse(contactData).asJsonObject
                val notesList=resjsonObjectObj.getAsJsonObject(context.getString(R.string.notes)).getAsJsonArray(context.getString(R.string.list))
                val mNotesData= Gson().fromJson(notesList,Array<NotesData>::class.java)
                val NoReplayToData= mNotesData.filter { it.replyTo==null }
                val filterReplayToData= mNotesData.filter { it.replyTo!=null }
                var mReplyDataList=filterReplayToData
                val NoReplayToDataSort =AppConstants.filterAndSortNotesData(NoReplayToData,filterReplayToData)
                var mList=NoReplayToDataSort.reversed()
//                AppConstants.liastOfNotesData=mList
//                println("mList:: Size"+mList.size)
                var allreadItems=ArrayList<Boolean>()
                mList.forEach {
                    val mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
                    val isitemViewed=mSharedPreferences.getBoolean(""+it?.noteId+"_"+it?.subject,false)
//                    println("isitemViewed:: $isitemViewed")
                    if(isitemViewed){
                        allreadItems.add(isitemViewed)
                    }
                }
                AppConstants.contacts_total_items = mList.size
                AppConstants.contacts_allreadItems = allreadItems.size
                if(allreadItems.size==mList.size){

                    context.findViewById<TextView>(R.id.tv_notifications).hide()
                }else{
//                    context.findViewById<TextView>(R.id.tv_notifications).show()

                }

            }
        }
    }
}