package com.dmss.burbankappold.homecare

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.interfaces.ItemClickListener
import com.denzcoskun.imageslider.models.SlideModel
import com.dmss.burbankappold.R
import com.dmss.burbankappold.dashboard.HomeActivity
import com.dmss.burbankappold.databinding.ActivityHomecaredashboardBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import common.Common


class HomeCareDashboardActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityHomecaredashboardBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomecaredashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

       /* val navHost =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment?
        val navController = navHost!!.navController
        val navInflater = navController.navInflater
        val graph: NavGraph = navInflater.inflate(R.navigation.nav_graph)
        graph.setStartDestination(R.id.ManualsFragment);*/
        initView()
        setupNavController()
        setupMenuBottomNavigation()
//        val navController = findNavController(R.id.nav_host_fragment_content_main)
//        appBarConfiguration = AppBarConfiguration(navController.graph)
//        setupActionBarWithNavController(navController, appBarConfiguration)


    }
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
     fun changeProfileBg( colourCode:Int){
         binding.contentMain.tabsBodyContent.topLine.visibility=View.GONE
         if(colourCode==R.color.white){
             binding.contentMain.tabsBodyContent.topLine.visibility=View.VISIBLE
         }
        binding.contentMain.tabsBodyContent.rrWelcomeProfileFragment.background=getDrawable(colourCode)
    }

    private fun setupNavController() {
        navController = findNavController(R.id.nav_host_fragment_content_dashboard)
    }
    private fun setNavGraph( navStartPosition:Int){
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_dashboard) as NavHostFragment
        val inflater = navHostFragment.navController.navInflater
        val graph = inflater.inflate(R.navigation.nav_graph)
        graph.setStartDestination( navStartPosition)
        binding.contentMain.slider.welcomeImage.visibility=View.GONE
        var headerText=""
        var subHeaderText=""
        println("navStartPosition:: $navStartPosition")
        when(navStartPosition){

            R.id. ManualsFragment-> {
                headerText=getString(R.string.my_manuals)
                subHeaderText=getString(R.string.manual_sub_header_text)
            }
            R.id. DocumentsFragment-> { headerText=getString(R.string.my_documents)
                subHeaderText=getString(R.string.document_sub_header_text)}
            R.id. WarrantiesFragment-> { headerText=getString(R.string.my_warranties)
                subHeaderText=getString(R.string.warranty_sub_header_text)}
            R.id. ReportsFragment-> { headerText=getString(R.string.report_issue)
                subHeaderText=getString(R.string.warranty_sub_header_text)}
        }

        binding.contentMain.tabsBodyContent.welcomeProfile.profileHeader.text=headerText
        binding.contentMain.tabsBodyContent.welcomeProfile.profileSubHeader.text=subHeaderText

        val navController = navHostFragment.navController
        navController.setGraph(graph, intent.extras)
    }
    private fun initView(){

        binding.contentMain.bottomNavigation.uncheckAllItems()
        binding.contentMain.slider.welcomeProfile.tvNotificationsCount.text="12"
        binding.leftDrawerMenu.tvUsername.text="Mitchell Symonds"
        binding.leftDrawerMenu.tvPageName.text=getString(R.string.home_care)

        binding.contentMain.toolbar.ivNavMenu.setOnClickListener {
            toggleLeftDrawer()
        }
        binding.leftDrawerMenu.tvCompletedPercentage.setOnClickListener {

           Common.showChangePhaseMessageAlert(this@HomeCareDashboardActivity)
        }

        binding.contentMain.toolbar.tvToolbarTitle.setOnClickListener {
            val i = Intent(this@HomeCareDashboardActivity, HomeCareDashboardActivity::class.java)
            finish()
            overridePendingTransition(0, 0)
            startActivity(i)
            overridePendingTransition(0, 0)
        }
        val imageList = ArrayList<SlideModel>()
/*        imageList.add(SlideModel("https://images.immediate.co.uk/production/volatile/sites/3/2019/04/Avengers-Endgame-Banner-2-de7cf60.jpg?quality=90&resize=620,413",""))
        imageList.add(SlideModel("https://img.cinemablend.com/filter:scale/quill/3/7/0/0/8/e/37008e36e98cd75101cf1347396eac8534871a19.jpg?mw=600",""))
        imageList.add(SlideModel("https://www.adgully.com/img/800/201711/spider-man-homecoming-banner.jpg"," "))
        imageList.add(SlideModel(R.drawable.welcome_slider_5,""))
        imageList.add(SlideModel("https://live.staticflickr.com/1980/29996141587_7886795726_b.jpg",""))*/


        imageList.add(SlideModel(R.drawable.welcome_slider_3,""))
        imageList.add(SlideModel(R.drawable.welcome_slider_3,""))
        imageList.add(SlideModel(R.drawable.welcome_slider_3,""))
        imageList.add(SlideModel(R.drawable.welcome_slider_3,""))
        imageList.add(SlideModel(R.drawable.welcome_slider_3,""))
        binding.contentMain.slider.imageSlider.setImageList(imageList,ScaleTypes.CENTER_CROP)

        binding.contentMain.slider.imageSlider.setItemClickListener(object : ItemClickListener {
            override fun onItemSelected(position: Int) {
                // item has been clicked
                // you got the ID - so use that info to do what you want ^
                println("onItemSelected:: $position")
               sliderSelectedPosition(position)
            }
        })

    }
    private fun checkCurrentFragment(id : Int): Boolean{
        val navController =
            Navigation.findNavController(this, R.id.nav_host_fragment_content_dashboard)
        return navController.currentDestination?.id == id
    }
    private fun navigateTo(resId: Int) {

        if (checkCurrentFragment(resId).not())
            navController.setGraph(resId)
    }
    private fun setupMenuBottomNavigation() {
        val navigationItemSelectedListener =
            BottomNavigationView.OnNavigationItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.manuals -> {
//                        setNavGraph(R.id.ManualsFragment)
                        sliderSelectedPosition(0)

                        return@OnNavigationItemSelectedListener true
                    }
                    R.id.documents -> {
                        sliderSelectedPosition(1)

//                        setNavGraph(R.id.DocumentsFragment)
                        return@OnNavigationItemSelectedListener true
                    }
                    R.id.warranties -> {
                        sliderSelectedPosition(2)

//                        setNavGraph(R.id.WarrantiesFragment)
                        return@OnNavigationItemSelectedListener true
                    }
                    R.id.report -> {
                        sliderSelectedPosition(3)

//                        setNavGraph(R.id.WarrantiesFragment)
                        return@OnNavigationItemSelectedListener true
                    }
                    R.id.help_section -> {
                        sliderSelectedPosition(4)

//                        setNavGraph(R.id.DocumentsFragment)
                        return@OnNavigationItemSelectedListener true
                    }
                }
                false
            }
        binding.contentMain.bottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener)
    }
   private fun sliderSelectedPosition(position:Int){
       binding.contentMain.llTabsContent.visibility=View.VISIBLE
       binding.contentMain.llSlider.visibility=View.GONE
       when (position) {
           0 -> {
//               binding.contentMain. bottomNavigation.selectedItemId = R.id.manuals
               setNavGraph(R.id.ManualsFragment)

           }
           1 -> {
//               binding.contentMain. bottomNavigation.selectedItemId = R.id.documents
               setNavGraph(R.id.DocumentsFragment)
           }
           2 -> {
//               binding.contentMain. bottomNavigation.selectedItemId = R.id.warranties
               setNavGraph(R.id.WarrantiesFragment)

           }
           3 -> {
               setNavGraph(R.id.ReportsFragment)
//               binding.contentMain. bottomNavigation.selectedItemId = R.id.report
           }
           4 -> {
               setNavGraph(R.id.ManualsFragment)
//               binding.contentMain. bottomNavigation.selectedItemId = R.id.help_section
           }
       }
   }
    fun BottomNavigationView.uncheckAllItems() {
        menu.setGroupCheckable(0, true, false)
        for (i in 0 until menu.size()) {
            menu.getItem(i).isChecked = false
        }
        menu.setGroupCheckable(0, true, true)
    }
    private fun toggleLeftDrawer() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        var menu=menuInflater.inflate(R.menu.menu_main, menu)

        return true
    }

}