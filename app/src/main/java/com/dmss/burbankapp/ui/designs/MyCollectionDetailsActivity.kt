package com.dmss.burbankapp.ui.designs

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.webkit.URLUtil
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.dmss.burbankapp.R
import com.dmss.burbankapp.data.api.ApiHelperImpl
import com.dmss.burbankapp.data.api.RetrofitBuilder
import com.dmss.burbankapp.data.apiUtils.Status
import com.dmss.burbankapp.data.apiUtils.ViewModelFactory
import com.dmss.burbankapp.data.local.CustomSharedPreferences
import com.dmss.burbankapp.data.local.entity.DatabaseBuilder
import com.dmss.burbankapp.data.local.entity.DatabaseHelperImpl
import com.dmss.burbankapp.data.model.BreadcrumbModel
import com.dmss.burbankapp.data.model.FavoriteResponseModel
import com.dmss.burbankapp.data.model.HouseNameDetailByNameModel
import com.dmss.burbankapp.data.model.MyCollectionHnlModel
import com.dmss.burbankapp.data.model.NewHomeListModel
import com.dmss.burbankapp.databinding.DashboardProfileWidthDadgeBinding
import com.dmss.burbankapp.databinding.LayoutMycollectionDetailsBinding
import com.dmss.burbankapp.databinding.ListCollectionItemBinding
import com.dmss.burbankapp.ui.adapters.SlidingAdapter
import com.dmss.burbankapp.ui.dashboard.DashboardActivity
import com.dmss.burbankapp.ui.enquireNow.EnquireNowActivity
import com.dmss.burbankapp.ui.login.ChooseOptionsActivity
import com.dmss.burbankapp.ui.main.MainActivity
import com.dmss.burbankapp.ui.mydisplay.DisplayToolbarViewModel
import com.dmss.burbankapp.ui.view.BreadCrumbAdapter
import com.dmss.burbankapp.ui.view.CustomProgressDialog
import com.dmss.burbankapp.utility.ViewPageDotIndicator
import com.dmss.burbankapp.utils.AppConstants
import com.dmss.burbankapp.utils.AppUtils
import com.dmss.burbankapp.utils.customviews.AppEvent
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import common.AppController
import org.greenrobot.eventbus.EventBus
import timber.log.Timber
import kotlin.math.roundToInt

class MyCollectionDetailsActivity : AppCompatActivity() , BreadCrumbAdapter.BreadcrumbItemClickListener {
    lateinit var myHomeQuizViewModel: NewHomeQuizViewModel
    lateinit var binding: LayoutMycollectionDetailsBinding
    lateinit var listItemBinding: ListCollectionItemBinding
    lateinit var newHomeListModel: NewHomeListModel
    var newHomesList: ArrayList<NewHomeListModel> = ArrayList<NewHomeListModel>()
    lateinit var mainActivity: MainActivity
    var preferences: CustomSharedPreferences? = null
    var isFromFavorite: Boolean? = null
    var listIndex:Int=0
    var isButtonEnabled: Boolean = false
    //    lateinit var toolBinding: InclueToolDesignBinding
    lateinit var profileWithBadgeBinding: DashboardProfileWidthDadgeBinding
    var newHomeListModelLocal: NewHomeListModel? =null
    var isFromFavoriteLocal:Boolean?=false
    var listIndexLocal:Int?=0

    private var stateId: Int = -1
    private var userID: Int = -1
    private lateinit var customSharedPreferences: CustomSharedPreferences
    private var customProgressDialog: CustomProgressDialog? = null
    private lateinit var displayToolbarViewModel: DisplayToolbarViewModel

    var images: Array<Int> = arrayOf(
        R.drawable.rectangle_test,
        R.drawable.rectangle_test,
        R.drawable.rectangle_test,
        R.drawable.rectangle_test,
        R.drawable.rectangle_test,
        R.drawable.rectangle_test
    )
    lateinit var pageAdapter: PagerAdapter

    override fun breadCrumb(breadCrumb: BreadcrumbModel) {

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        val window = window
//        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

//        window.statusBarColor = Color.TRANSPARENT;
//        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN )
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN )
        window.statusBarColor = Color.WHITE
        binding = LayoutMycollectionDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        profileWithBadgeBinding = DashboardProfileWidthDadgeBinding.bind(binding.root)
        listItemBinding = ListCollectionItemBinding.bind(binding.root)
        preferences = CustomSharedPreferences(this)
        profileWithBadgeBinding.tvFavorites.visibility=View.GONE
        profileWithBadgeBinding.profileNotification.visibility=View.GONE

        customSharedPreferences = CustomSharedPreferences(AppController.getInstance())
        binding.tool.tvMy.text = "Display"
        binding.tool.tvHeaderDesc.text = "Homes"
        println("MyCollections:: "+binding.tool.tvMy.text)

        setupViewModel()
        getCollectionsData()
        binding.tool.ivProfile.setOnClickListener {
            backToHome()
        }
        binding.ivBack.setOnClickListener {
            onBackPressed()
        }

    }


    private  fun  requireActivity():AppCompatActivity {
        val mContext=this
        return mContext
    }
    private fun profileNotificationCountView(notificationCount:Int){
        if(notificationCount==0){
            profileWithBadgeBinding.profileNotification.visibility= View.GONE
        }else{
            profileWithBadgeBinding.profileNotification.visibility= View.GONE
        }
    }
    private fun setupViewModel() {
        displayToolbarViewModel = ViewModelProviders.of(requireActivity())[DisplayToolbarViewModel::class.java]
        profileWithBadgeBinding.profileNotification.text= AppConstants.TotalMyFavs.toString()
        profileNotificationCountView(AppConstants.TotalMyFavs)
        displayToolbarViewModel.favouritesCount.observe(this){
            profileWithBadgeBinding.profileNotification.text = it.toString()
            profileNotificationCountView(it)

        }
        myHomeQuizViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(
                ApiHelperImpl(RetrofitBuilder.apiService),
                DatabaseHelperImpl(DatabaseBuilder.getInstance(AppController.getInstance()))
            )
        ).get(NewHomeQuizViewModel::class.java)

        profileWithBadgeBinding.tvFavorites.setOnClickListener {
            var isUserLoggedIn = customSharedPreferences.getUserLogin()
            if (isUserLoggedIn) {
                MainActivity().showProfileDialog(this)
            } else {
                AppUtils.showPleaseLoginDialog(
                    this,
                    requireActivity(),
                    getString(R.string.Please_login_to_view_edit_profile)
                )
            }
        }
        setupObserver()

    }

    private fun setupObserver() {
        myHomeQuizViewModel.getFavorite().observe(this, Observer { data ->
            when (data.status) {
                Status.SUCCESS -> {
                    customProgressDialog?.dismissProgress()
                    MainActivity().loadUserFavoritesMyCollection()

                    var favoriteResponseModel: FavoriteResponseModel? = data.data

                    val toast = Toast.makeText(
                        requireActivity(),
                        favoriteResponseModel?.message,
                        Toast.LENGTH_SHORT
                    )
                    toast.setGravity(Gravity.CENTER, 0, 0)
                    toast.show()


                    if (newHomeListModel.IsFav) {
                        listItemBinding.ivLike.setImageDrawable(
                            ContextCompat.getDrawable(
                                requireActivity(),
                                R.drawable.fav_selected
                            )
                        )
                    } else
                        listItemBinding.ivLike.setImageDrawable(
                            ContextCompat.getDrawable(
                                requireActivity(),
                                R.drawable.heart_bottom_svg
                            )
                        )

                    isFromFavorite?.let {
                        if (it) {
                            newHomeListModel.let {
                                val appEvent = AppEvent(
                                    AppEvent.UPDATE_HOMEDESIGNFAVORITE,
                                    Gson().toJson(newHomeListModel)
                                )
                                EventBus.getDefault().post(appEvent)
                            }

                        } else {
                            newHomeListModel.let {
                                val appEvent = AppEvent(
                                    AppEvent.UPDATE_FAVORITES_HOMEDESIGN,
                                    newHomeListModel,
                                    newHomeListModel.IsFav, true
                                )
                                EventBus.getDefault().post(appEvent)
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
        myHomeQuizViewModel.getHouseNameByDetailLiveData()
            .observe(this, Observer
            {
                when (it.status) {
                    Status.SUCCESS -> {
                        customProgressDialog?.dismissProgress()
                        val houseNameDetailByNameModel: HouseNameDetailByNameModel? = it.data
                        if (houseNameDetailByNameModel != null && houseNameDetailByNameModel.status) {
                            val floorPlanImageUrl =
                                houseNameDetailByNameModel.isHousesModel.homePlanModel.FloorPlanImageURL_Mobile

                            Timber.e("Image after Url--- $floorPlanImageUrl")
                            Timber.e("Image before Url--- ${houseNameDetailByNameModel.isHousesModel.homePlanModel.FloorPlanImageURL_Mobile}")
                            newHomeListModelLocal = NewHomeListModel().apply {
                                facadePermantUrl = houseNameDetailByNameModel.isHousesModel.FacadePermanentUrl
                                HouseName = houseNameDetailByNameModel.isHousesModel.HouseName
                                HouseSize = houseNameDetailByNameModel.isHousesModel.HouseSize.toInt()
                                Price = houseNameDetailByNameModel.isHousesModel.Price.toDouble()
                                BedRooms = houseNameDetailByNameModel.isHousesModel.BedRooms.toInt()
                                BathRooms = houseNameDetailByNameModel.isHousesModel.BathRooms.toInt()
                                CarSpace = houseNameDetailByNameModel.isHousesModel.CarSpace.toInt()
                                MinLotWidth = houseNameDetailByNameModel.isHousesModel.MinLotWidth.toFloat()
                                IsFav = houseNameDetailByNameModel.isHousesModel.IsFavourite.toBoolean()
//                                houseIdLandBank = houseNameDetailByNameModel.isHousesModel.houseIdLandBank
//                                currentUserName = houseNameDetailByNameModel.isHousesModel.currentUserName
                            }


                            if(floorPlanImageUrl!=null && floorPlanImageUrl!="") {
                                Picasso.get()
                                    .load(floorPlanImageUrl)
                                    .into(binding.ivFloorpaln, object : Callback {
                                        override fun onSuccess() {
                                            binding.floorplanLabel.visibility = View.GONE

                                        }

                                        override fun onError(e: java.lang.Exception?) {
                                            //do smth when there is picture loading error
                                        }
                                    })
                            }
                            if (houseNameDetailByNameModel.isHousesModel.facadeLargeImageUrls.size > 0) {
                                pageAdapter = SlidingAdapter(
                                    this,
                                    houseNameDetailByNameModel.isHousesModel.facadeLargeImageUrls
                                )
                                binding.viewPager.adapter = pageAdapter
//                                binding.dot.setViewPager(binding.viewPager)
                                ViewPageDotIndicator(this,binding.viewPager,binding.dotsLayout).setupDots(pageAdapter.count)

                                val facadeNames = arrayListOf<String>()
                                houseNameDetailByNameModel.isHousesModel.facadeLargeImageUrls.forEach {
                                    val fileName = URLUtil.guessFileName(it, null, null)
                                    if(fileName.contains("_")){
                                        val fName = fileName.replace(".jpg","")
                                            .replace(".png","")
                                            .replace(".jpeg","").split("_")
                                        if(fName.size>1){
                                            facadeNames.add("${fName[1]} Facade")
                                        }
                                    }
                                }
                                if (facadeNames.isNotEmpty()) {
                                    binding.tvFacade.text = facadeNames[0].firstCap()
                                }


                                binding.viewPager.addOnPageChangeListener(object :
                                    ViewPager.OnPageChangeListener {
                                    override fun onPageScrolled(
                                        position: Int,
                                        positionOffset: Float,
                                        positionOffsetPixels: Int
                                    ) {

                                    }

                                    override fun onPageSelected(position: Int) {
                                        if (facadeNames != null) {
                                            if (facadeNames.isNotEmpty()) {
                                                if (facadeNames.size > position) {
                                                    binding.tvFacade.text =
                                                        (facadeNames[position]).firstCap()
                                                }
                                            }


                                        }
                                    }

                                    override fun onPageScrollStateChanged(state: Int) {

                                    }

                                })

                                initViews(newHomeListModelLocal!!,isFromFavoriteLocal!!,listIndexLocal!!)

                            }

                            if (houseNameDetailByNameModel.isHousesModel.homePlanModel.Visualisation) {
                                binding.llMyplace.visibility = View.VISIBLE
                            } else {
                                binding.llMyplace.visibility = View.INVISIBLE
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

        myHomeQuizViewModel.getMyCollectionHnlLiveData().observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    customProgressDialog?.dismissProgress()
                    var regionsData: MyCollectionHnlModel? = it.data
                    if (regionsData != null && regionsData.hnlQuizList.size > 0) {
                        val bundle = Bundle()
                        bundle.putParcelable(AppConstants.NEWHOMELISTMODEL, newHomeListModel)
//                        loadFragment(MyCollectionHomeAndLandFragment(), bundle)
                    } else {
                        AppUtils.showCustomCenterToast(requireActivity(), "No Packages found")
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
    fun String.firstCap()=this.replaceFirstChar { it.uppercase() }

    private fun getCollectionsData(){


        customProgressDialog = CustomProgressDialog(requireActivity())

        intent?.getParcelableExtra<NewHomeListModel>(AppConstants.NEWHOMELISTMODEL)?.let {
            newHomeListModelLocal = it
        }
        intent?.getParcelableArrayListExtra<NewHomeListModel>(AppConstants.NEWHOMELISTMODELLIST)?.let {
            newHomesList = it
        }
        intent?.getBooleanExtra("isFavorite", false)?.let {
            isFromFavoriteLocal = it
        }
        intent?.getIntExtra("index", 0)?.let {
            listIndexLocal = it
        }

        intent?.getStringExtra("StateId")?.let {
            stateId = it.toInt()
        }

        intent?.getStringExtra("ModuleType")?.let {
            println("ModuleType:: "+it)
            if(it == "NewHomes"){
                binding.tool.tvMy.text = "Home"
                binding.tool.tvHeaderDesc.text = "Designs"

            }else{
                binding.tool.tvMy.text = "Display"
                binding.tool.tvHeaderDesc.text = "Homes"
                binding.bodyPrevious.visibility = View.GONE
                binding.bodyNext.visibility = View.GONE
            }
        }
        var HouseSize=0
        var HouseName="0"

        intent?.getStringExtra("HouseSize")?.let {
            HouseSize = it.toInt()
        }
        intent?.getStringExtra("HouseName")?.let {
            HouseName = it
        }
//        customProgressDialog?.showProgress()
        println("HouseName:: "+HouseName+" HouseSize:: "+HouseSize+" stateId:: "+stateId)
        myHomeQuizViewModel.fetchHouseNameDetailApi(
            HouseSize!!,
            HouseName, stateId
        )


    }

    private fun initViews(newHomeListModelLoc: NewHomeListModel, isFromFavoriteLoc:Boolean, currentIndex:Int) {
        newHomeListModel=newHomeListModelLoc
        isFromFavorite=isFromFavoriteLoc
        listIndex=currentIndex
        listItemBinding.rlPlace.visibility = View.GONE
        listItemBinding.previous.visibility = View.GONE
        listItemBinding.next.visibility = View.GONE
        binding.bodyPrevious.visibility = View.GONE
        binding.bodyNext.visibility = View.GONE
        listItemBinding.barLineTvcar.background=resources.getDrawable(R.color.white_3_1)
        listItemBinding.baLineBath.background=resources.getDrawable(R.color.white_3_1)

//        swipeListner()

        /* if(!isFromFavoriteLoc) {
             listItemBinding.previous.visibility = View.GONE
             listItemBinding.next.visibility = View.GONE

             binding.bodyPrevious.visibility = View.VISIBLE
             binding.bodyNext.visibility = View.VISIBLE
         }
 */
        if(listIndex==0){
            listItemBinding.previous.visibility= View.GONE
            binding.bodyPrevious.visibility = View.GONE


        }
        if(listIndex==newHomesList.size-1){
            listItemBinding.next.visibility= View.GONE
            binding.bodyNext.visibility = View.GONE


        }
        listItemBinding.next.setOnClickListener {
            initViews(newHomesList[listIndex+1],isFromFavoriteLoc,listIndex+1)

        }
        binding.bodyNext.setOnClickListener {
            initViews(newHomesList[listIndex+1],isFromFavoriteLoc,listIndex+1)

        }
        listItemBinding.previous.setOnClickListener {
            initViews(newHomesList[listIndex-1],isFromFavoriteLoc,listIndex-1)

        }
        binding.bodyPrevious.setOnClickListener {
            initViews(newHomesList[listIndex-1],isFromFavoriteLoc,listIndex-1)

        }
        setToolbarBreadCrumb()

        /*  toolBinding.tvTotalDesigns.text="TOTAL "+newHomesList.size+" DESIGNS"

          toolBinding.tvReset.visibility= View.VISIBLE
          toolBinding.tvTotalDesigns.visibility= View.VISIBLE
          toolBinding.customRecyclerview.initView(requireContext())
          toolBinding.customRecyclerview.visibility = View.VISIBLE
          if(isFromFavoriteLoc) {
              toolBinding.tvReset.visibility= View.GONE
              toolBinding.tvTotalDesigns.visibility= View.GONE
              profileWithBadgeBinding.tvFavorites.visibility= View.VISIBLE
              profileWithBadgeBinding.profileNotification.visibility= View.VISIBLE

          }*/
        customSharedPreferences = CustomSharedPreferences(AppController.getInstance())
        stateId = customSharedPreferences.getStateID()
        userID = customSharedPreferences.getUserId()
        var isUserLoggedIn = customSharedPreferences.getUserLogin()

        if (!isUserLoggedIn) {
            profileWithBadgeBinding.tvFavorites.background = resources.getDrawable(R.drawable.disable_rectangle_new)
            profileWithBadgeBinding.profileNotification.visibility= View.GONE

        }

        binding.llMyplace.setOnClickListener {
            var stateName: String= customSharedPreferences.getSelectedState().toString()
            //
            if(stateName.contains("Wales")){
                stateName = "nsw"
            }else if(stateName.contains("South")){
                stateName = "South-Australia"
            }else if(stateName.contains("NSW & ACT"))
                stateName = "nsw"

            var url =
                AppConstants.MYPLACETHREED_BASE + "/${stateName.lowercase()}/myplace3dmobile/housename.${newHomeListModel.HouseName};housesize.${newHomeListModel.HouseSize}/"

            if (!url.startsWith("http://") && !url.startsWith("https://"))
                url = "http://$url";

            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(browserIntent)

            Timber.e("MyPlace3d URL ::----->${url}")
            /*newHomeListModel.HouseName?.let {
                val intent = Intent(requireContext(), MyPlaceThreedActivity::class.java)
                intent.putExtra("HouseName", newHomeListModel.HouseName)
                intent.putExtra("HouseSize", newHomeListModel.HouseSize)
                startActivity(intent)
            }*/
        }


        binding.enquiry.setOnClickListener {
            if (!newHomeListModel.HouseName.isNullOrBlank() && newHomeListModel.HouseSize != null) {
                startActivity(
                    Intent(
                        requireActivity(),
                        EnquireNowActivity::class.java
                    ).putExtra("HOSUENAME", newHomeListModel.HouseName)
                        .putExtra("HOUSESIZE", newHomeListModel.HouseSize!!)
                        .putExtra("ADDRESS", "")

                        .putExtra("FROM", "collections")
                )
            }
        }
        binding.ivBack.setOnClickListener {
            backToHome()
        }
        binding.tvTool.visibility = View.VISIBLE
        binding.tvTool.text =
            (newHomeListModel.HouseName + " " + newHomeListModel.HouseSize)
        listItemBinding.tvPlace.text =
            (newHomeListModel.HouseName + " " + newHomeListModel.HouseSize)

        var price: String = ""
        newHomeListModel.Price?.roundToInt()?.let {
            price =
                AppUtils.getCommasForPriceValue(it)
        }


        listItemBinding.tvPrice.text = ("$$price")
        listItemBinding.tvPrice.visibility = View.GONE
        listItemBinding.tvBath.text = (newHomeListModel.BathRooms.toString())
        listItemBinding.tvBed.text = (newHomeListModel.BedRooms.toString())
        listItemBinding.tvcar.text = (newHomeListModel.CarSpace.toString())
        listItemBinding.tvWidth.text =
            (AppUtils.roundRemainingString(newHomeListModel.MinLotWidth.toString()) + "m")

        newHomeListModel.IsFav.let {
            if (it) {
                listItemBinding.ivLike.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireActivity(),
                        R.drawable.fav_selected
                    )
                )
                isButtonEnabled = false
                binding.savedesign.background =
                    ContextCompat.getDrawable(requireActivity(), R.drawable.rectangle_disable_button)

            } else {
                listItemBinding.ivLike.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireActivity(),
                        R.drawable.heart_bottom_svg
                    )
                )
                binding.savedesign.background =
                    ContextCompat.getDrawable(requireActivity(), R.drawable.rectangle_charcoal_bg)
                isButtonEnabled = true
            }

        }


        binding.savedesign.setOnClickListener {
            if (isButtonEnabled) {
                var isUserLoggedIn = customSharedPreferences.getUserLogin()
                if (isUserLoggedIn) {
                    listItemBinding.ivLike.performClick()
                } else {
                    AppUtils.showPleaseLoginDialog(
                        requireActivity(),
                        requireActivity(),
                        "Please login to add favourites"
                    )
                }
            } else {
                AppUtils.showCustomCenterToast(requireActivity(), "Design saved in favourites")
            }

        }
        listItemBinding.ivLike.visibility = View.GONE
        listItemBinding.ivLike.setOnClickListener {
            var isUserLoggedIn = customSharedPreferences.getUserLogin()
            if (isUserLoggedIn) {
                newHomeListModel.IsFav.let {
                    if (it) {
                        listItemBinding.ivLike.setImageDrawable(
                            ContextCompat.getDrawable(
                                requireActivity(),
                                R.drawable.heart_bottom_svg
                            )
                        )
                        binding.savedesign.background =
                            ContextCompat.getDrawable(requireActivity(), R.drawable.rectangle_charcoal_bg)
                        isButtonEnabled = true


                    } else {
                        listItemBinding.ivLike.setImageDrawable(
                            ContextCompat.getDrawable(
                                requireActivity(),
                                R.drawable.fav_selected
                            )
                        )
                        binding.savedesign.background = ContextCompat.getDrawable(
                            requireActivity(),
                            R.drawable.rectangle_disable_button
                        )
                        isButtonEnabled = false


                    }
                    favoriteApiCall(newHomeListModel)

                }
            } else {
                AppUtils.showPleaseLoginDialog(
                    requireActivity(),
                    requireActivity(),
                    "Please login to add favourites"
                )
            }


        }




        binding.llHomeland.setOnClickListener {
            newHomeListModel.HouseName?.let {
                myHomeQuizViewModel.getMyCollectionHomeAndLand(stateId, it)
            }

        }

        if(isFromFavoriteLoc){
//            toolBinding.tvTool.visibility= View.VISIBLE
//            toolBinding.customRecyclerview.visibility= View.GONE
        }
    }
    override fun onBackPressed() {
        super.onBackPressed()
        backToHome()
    }
    private fun backToHome(){
        val intent = Intent(this, DashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
    private fun setToolbarBreadCrumb() {
        var arrayList: java.util.ArrayList<BreadcrumbModel> = java.util.ArrayList()

        if (AppConstants.breadCrumbMyCollection.keys.size > 0) {
            val isLotWidthAvailable = AppConstants.breadCrumbMyCollection.any { it.key == "Lot Width" }
            if (isLotWidthAvailable) AppConstants.breadCrumbMyCollection.remove("MyCollectionLot")
//            toolBinding.tvTool.visibility = View.GONE
//            toolBinding.customRecyclerview.visibility = View.VISIBLE

            val myVeryOwnIterator: Iterator<*> =
                AppConstants.breadCrumbMyCollection.keys.iterator()

            while (myVeryOwnIterator.hasNext()) {
                val key = myVeryOwnIterator.next() as String
                var value = AppConstants.breadCrumbMyCollection[key]
                if (value != null) {
                    if (value.breadCrumbTitle != "") {
                        if (key == "Lot Width") {
                            arrayList.add(0,
                                BreadcrumbModel(
                                    value.breadCrumbTitle,
                                    value.titleFragment,
                                    false

                                )
                            )
                        }else{
                            arrayList.add(
                                BreadcrumbModel(
                                    value.breadCrumbTitle,
                                    value.titleFragment,
                                    false
                                )
                            )
                        }
                    }
                }

            }
        } else {
//            toolBinding.customRecyclerview.visibility = View.GONE
//            toolBinding.tvTool.visibility = View.VISIBLE
        }
//        val list =  toolBinding.customRecyclerview.setData(arrayList, this)
    }




    private fun favoriteApiCall(newHomeListModel: NewHomeListModel) {
        var jsonObject = JsonObject()
        jsonObject.addProperty("TypeId", 2)
        jsonObject.addProperty("UserId", preferences?.getUserId())
        jsonObject.addProperty("HouseId", newHomeListModel.houseIdLandBank)
        jsonObject.addProperty("StateId", preferences?.getStateID())
        jsonObject.addProperty("isfavourite", !newHomeListModel.IsFav)
        this.newHomeListModel.IsFav = !newHomeListModel.IsFav

        myHomeQuizViewModel.setFavorite(jsonObject)
    }
}