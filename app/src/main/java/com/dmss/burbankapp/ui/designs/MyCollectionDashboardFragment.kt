package com.dmss.burbankapp.ui.designs

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
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
import com.dmss.burbankapp.data.model.*
import com.dmss.burbankapp.databinding.BottomLayoutMycollectionBinding
import com.dmss.burbankapp.databinding.FragmentMyCollectionDashboardBinding
import com.dmss.burbankapp.databinding.InclueToolDesignBinding
import com.dmss.burbankapp.databinding.LayoutRecentSearchMycollectionBinding
import com.dmss.burbankapp.databinding.ProfileWithBadgeBinding
import com.dmss.burbankapp.ui.dashboard.DashboardViewModel
import com.dmss.burbankapp.ui.main.MainActivity
import com.dmss.burbankapp.ui.mydisplay.DisplayToolbarViewModel
import com.dmss.burbankapp.ui.video.PlayerActivity
import com.dmss.burbankapp.ui.view.BreadCrumbAdapter
import com.dmss.burbankapp.ui.view.CustomProgressDialog
import com.dmss.burbankapp.utils.AppConstants
import com.dmss.burbankapp.utils.AppUtils
import com.google.gson.Gson
import com.google.gson.JsonObject
import common.AppController
import timber.log.Timber


class MyCollectionDashboardFragment : Fragment(), MainActivity.OnBackPressedListener,
    BreadCrumbAdapter.BreadcrumbItemClickListener {

    private var apiCallHappen: Boolean = false
    public val FEATURE_STOREYS = "MyCollectionLot"
    public var FEATURE = "Lot Width"
    private var customProgressDialog: CustomProgressDialog? = null
    private lateinit var myHomeQuizViewModel: NewHomeQuizViewModel
    lateinit var dashboardViewModel: DashboardViewModel
    private var stateId: Int = -1
    private var userID: Int = -1
    lateinit var binding: FragmentMyCollectionDashboardBinding
    private lateinit var quizListModel: NewHomeQuizListModel
    lateinit var newHomeJsonArrayList: ArrayList<NewHomeJsonObject>

    private lateinit var bottomLayoutBinding: BottomLayoutMycollectionBinding
    private lateinit var mainActivity: MainActivity
    private var isLotEntered: Boolean = false

    var isNextClickable: Boolean = true
    var iHaveLandSelected = false
    var isValidMeters = false
    var isOptionSelected = false
    var isSelected = false

    private lateinit var nextFragment: Fragment
    private lateinit var newHomeNextFeatureResponseModel: NewHomeNextFeatureResponseModel
    var newHomesNextFeaturesModel: NewHomesNextFeaturesModel? = null
    lateinit var myCollectionRecentSearchText: String
    private lateinit var displayToolbarViewModel: DisplayToolbarViewModel


    private lateinit var customSharedPreferences: CustomSharedPreferences
    lateinit var toolBinding: InclueToolDesignBinding
    lateinit var profileWithBadgeBinding: ProfileWithBadgeBinding

    //Validations
    var storeySelected = false
    var recentSearchHashMap: java.util.HashMap<String, ArrayList<NewHomeJsonObject>> =
        java.util.LinkedHashMap()

    ///
    var questionModel: NewHomeJsonObject? = null
    private var NEWHOMESHOUSECOUNT = 0
    private var isToolbarBack = true

    override fun onAttach(context: Context) {
        mainActivity = activity as MainActivity
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyCollectionDashboardBinding.inflate(inflater, container, false)
        bottomLayoutBinding = BottomLayoutMycollectionBinding.bind(binding.root)
        toolBinding = InclueToolDesignBinding.bind(binding.root)
        profileWithBadgeBinding = ProfileWithBadgeBinding.bind(binding.root)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isToolbarBack = true
        isSelected = false
        initViews()
        setupViewModel()
        //Get House Count API
        getHouseCount()
    }

    override fun onResume() {
        super.onResume()
        isToolbarBack = true
        if (AppConstants.newHomeJsonObjectsList.any { it.feature == FEATURE}) {
            isSelected = true
            requestApiCall()
        }
    }

    private fun requestApiCall(){
        val mMyCollectionQuizQuestionRequest =  MyCollectionQuizQuestionRequest(
            stateId,
            includePackages = 0,
            sortByPrice = 0,
            userId = userID,
            newHomeJsonList = AppConstants.newHomeJsonObjectsList
        )
//        myHomeQuizViewModel.fetchNewHomesCollectionsNextFeature(mMyCollectionQuizQuestionRequest)
        setButtonSelection()
    }

    private fun setButtonSelection(){
        val data = AppConstants.newHomeJsonObjectsList.find { it.feature == FEATURE }
        data?.let {
            when(it.answer){
                AppConstants.I_DONT_MIND -> {
                    showHideViews(binding.iDontHaveLand, binding.tvIDontHave,binding.into)
                    binding.groupView.visibility = View.INVISIBLE
                    binding.proceedButton.visibility = View.INVISIBLE
                    toolBinding.tvTool.visibility = View.GONE
                    toolBinding.customRecyclerview.visibility = View.VISIBLE
                    setToolbarBreadCrumb(false)
                }
                ""-> {
                    isLotEntered = true
                    showHideViews(binding.IHaveLand, binding.tvIHave,binding.right)
                    binding.groupView.visibility = View.VISIBLE
                    binding.etMeters.setText(data.maxValue)
                    toolBinding.tvTool.visibility = View.GONE
                    toolBinding.customRecyclerview.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun setupViewModel() {
        myHomeQuizViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(
                ApiHelperImpl(RetrofitBuilder.apiService),
                DatabaseHelperImpl(DatabaseBuilder.getInstance(AppController.getInstance()))
            )
        ).get(NewHomeQuizViewModel::class.java)
        dashboardViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(
                ApiHelperImpl(RetrofitBuilder.apiService),
                DatabaseHelperImpl(DatabaseBuilder.getInstance(AppController.getInstance()))
            )
        ).get(DashboardViewModel::class.java)

        setupObserver()
        bottomLayoutBinding.tvDesigns.visibility = View.INVISIBLE
    }

    private fun setupObserver() {
        myHomeQuizViewModel.getHouseCountLiveData().observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    val newHomeNextFeatureResponseModel: NewHomeNextFeatureResponseModel? = it.data
                    if (newHomeNextFeatureResponseModel != null && (newHomeNextFeatureResponseModel.status)) {

                        this.newHomeNextFeatureResponseModel = newHomeNextFeatureResponseModel
                        bottomLayoutBinding.tvDesigns.visibility = View.VISIBLE
                        bottomLayoutBinding.tvDesigns.text =
                            newHomeNextFeatureResponseModel.newHomesNextFeatures?.let { it1 ->
                                AppUtils.getDesignsTextBasedOnCount(
                                    it1.HouseCount
                                )
                            }
                        NEWHOMESHOUSECOUNT = newHomeNextFeatureResponseModel.newHomesNextFeatures?.HouseCount
                            ?: 0
                        AppConstants.HouseCount = NEWHOMESHOUSECOUNT
                        myHomeQuizViewModel.fetchMyCollectionQuiz(stateId)
                        var isUserLoggedIn = customSharedPreferences.getUserLogin()
                        if (isUserLoggedIn && !AppConstants.isRecentDialogShowed) {
                            myHomeQuizViewModel.getRecentSearchDataMyCollection(userID, 2, stateId)
                        }
                    } else {
                        // AppUtils.disableAndEnableView()
                    }
                    customProgressDialog?.dismissProgress()
                }
                Status.LOADING -> {
                    customProgressDialog?.showProgress()
                }
                Status.ERROR -> {
                    customProgressDialog?.dismissProgress()
                }
            }
        })
        myHomeQuizViewModel.getNewHomeNextQuestionLiveData().observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {

                    val newHomeNextFeatureResponseModel: NewHomeNextFeatureResponseModel? = it.data
                    var gson = Gson()
                    if (newHomeNextFeatureResponseModel != null) {
                        AppConstants.newHomeNextFeatureResponseModel=newHomeNextFeatureResponseModel
                    }
                    if (newHomeNextFeatureResponseModel != null && newHomeNextFeatureResponseModel.status) {
                        if (iHaveLandSelected) {
                            var value = binding.etMeters.text.toString()
                            if (value.isNotEmpty()) {
                                AppConstants.breadCrumbMyCollection[FEATURE_STOREYS] =
                                    BreadCrumbHashMapModel("$value"+"M", FEATURE_STOREYS)
                                setToolbarBreadCrumb(false)
                            }
                        }

                        newHomeNextFeatureResponseModel.message?.let {
                            isValidMeters = false
                            isOptionSelected = false
                            AppUtils.showCustomCenterToast(requireContext(), it)
                        } ?: kotlin.run {
                            isValidMeters = true
                            isOptionSelected = true
                        }
                        this.newHomeNextFeatureResponseModel = newHomeNextFeatureResponseModel
                        if (newHomeNextFeatureResponseModel.status) {
                            storeySelected = true
                            bottomLayoutBinding.tvDesigns.text =
                                newHomeNextFeatureResponseModel.newHomesNextFeatures?.let { it1 ->
                                    AppUtils.getDesignsTextBasedOnCount(
                                        it1.HouseCount
                                    )
                                }
                            NEWHOMESHOUSECOUNT =
                                newHomeNextFeatureResponseModel.newHomesNextFeatures?.HouseCount
                                    ?: 0
                            AppConstants.HouseCount = NEWHOMESHOUSECOUNT

                            nextFragment =
                                if (newHomeNextFeatureResponseModel.newHomesNextFeatures?.nextFeature != null) {
                                    newHomeNextFeatureResponseModel.newHomesNextFeatures?.nextFeature!!
                                    mainActivity.fetchFirstMyCollectionQuizFragment(
                                        newHomeNextFeatureResponseModel.newHomesNextFeatures?.nextFeature!!
                                    )
                                }
                                /*if (newHomeNextFeatureResponseModel.newHomesNextFeatures?.nextFeature != null) {
                                    newHomeNextFeatureResponseModel.newHomesNextFeatures!!.nextFeature?.let { it1 ->
                                        mainActivity.fetchFirstMyCollectionQuizFragment(
                                            it1
                                        )
                                    }!!
                                }*/
                                else {
                                    MyCollectionPlacesFragment()
                                }
                        }

                        if (this::bottomLayoutBinding.isInitialized) {
                            isNextClickable =
                                newHomeNextFeatureResponseModel.newHomesNextFeatures?.let { it1 ->
                                    AppUtils.disableAndEnableView(
                                        it1.HouseCount,
                                        bottomLayoutBinding.tvDesigns,
                                        bottomLayoutBinding.next, bottomLayoutBinding.buttonNext
                                    )
                                } == true
                        }
                    } else {
                        if (this::bottomLayoutBinding.isInitialized) {
                            isNextClickable = AppUtils.noPackagesAndDisableClick(
                                bottomLayoutBinding.tvDesigns,
                                bottomLayoutBinding.next, bottomLayoutBinding.buttonNext, false
                            )
                        }
                    }
                    customProgressDialog?.dismissProgress()

                }
                Status.LOADING -> {
                    customProgressDialog?.showProgress()
                }
                Status.ERROR -> {
                    customProgressDialog?.dismissProgress()
                }
            }
        })
        myHomeQuizViewModel.getNewHomeQuizLiveData().observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    var newHomeQuizModel: NewHomeQuizModel? = it.data
                    if (newHomeQuizModel != null && newHomeQuizModel.status) {
                        AppConstants.newHomeQuizList = newHomeQuizModel.newhomesQuiz
                        AppConstants.QUESTION_ORDER = 1
                        if (newHomeQuizModel.newhomesQuiz.isNotEmpty()) {
                            quizListModel =
                                newHomeQuizModel.newhomesQuiz[0]
                            binding.tvHeaderQuestion.text = quizListModel.question
                        }
                    }

                    customProgressDialog?.dismissProgress()

                }
                Status.LOADING -> {
                    customProgressDialog?.showProgress()
                }
                Status.ERROR -> {
                    customProgressDialog?.dismissProgress()
                }
            }
        })

        if (!AppConstants.isRecentDialogShowed){
            myHomeQuizViewModel.getRecentSearchMyCollectionLiveData()
                .observe(viewLifecycleOwner, Observer { it ->
                    when (it.status) {
                        Status.SUCCESS -> {
                            customProgressDialog?.dismissProgress()
                            var regionsData: MyCollectionRecentSearchModel? = it.data
                            if (regionsData != null && regionsData.status) {
                                myCollectionRecentSearchText = ""

                                if (regionsData.searchJsonList.size > 0) {

                                    val newHomeJsonArrayList = ArrayList<NewHomeJsonObject>()


                                    if (regionsData.searchJsonList.size > 0) {
                                        println("regionsData.searchJsonList:: "+Gson().toJson(regionsData.searchJsonList))

                                        for (myCollectionRecentSearchAnswerModel: MyCollectionRecentSearchAnswerModel in regionsData.searchJsonList.filter { it.feature !=  "resultsCount"}) {

                                            var newHomeJsonObject: NewHomeJsonObject =
                                                if (myCollectionRecentSearchAnswerModel.minValue == null
                                                    && myCollectionRecentSearchAnswerModel.maxValue == null) {
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
                                            newHomeJsonArrayList.add(newHomeJsonObject)
                                        }
                                    }
                                    recentSearchHashMap[AppConstants.MYCOLLECTION_RECENT_SEARCH_KEY] =
                                        newHomeJsonArrayList

                                    val isLotWidthAvailable = regionsData.searchJsonList.any { it.feature == "Lot Width" && it.answer != AppConstants.I_DONT_MIND}

                                    for (myCollectionRecentSearchAnswerModel in regionsData.searchJsonList.filter { it.answer != "I don't mind" && it.answer != "I do not want this"}) {
//                                    for (myCollectionRecentSearchAnswerModel in regionsData.searchJsonList) {
                                        println("myCollectionRecentSearchAnswerModel.feature:: "+myCollectionRecentSearchAnswerModel.feature+"  Maxval::"+myCollectionRecentSearchAnswerModel.maxValue)
                                        when (myCollectionRecentSearchAnswerModel.feature) {

                                            "Lot Width" -> {
                                                if (myCollectionRecentSearchAnswerModel.maxValue.isNotEmpty()) {
                                                    var answer = AppUtils.roundRemainingString(
                                                        myCollectionRecentSearchAnswerModel.maxValue
                                                    )
                                                    myCollectionRecentSearchText += "$answer" + "M"
                                                    AppConstants.breadCrumbMyCollection[myCollectionRecentSearchAnswerModel.feature] = BreadCrumbHashMapModel(
                                                        "$answer" + "M" , myCollectionRecentSearchAnswerModel.feature
                                                    )
                                                }
                                                println("myCollectionRecentSearchText:: "+myCollectionRecentSearchText)
                                            }

                                            "Storeys" -> {
                                                var storey = ""
                                                if (myCollectionRecentSearchAnswerModel.answer == "1") {
                                                    storey = if (isLotWidthAvailable) " | Single" else "Single"
                                                } else if (myCollectionRecentSearchAnswerModel.answer == "2") {
                                                    storey = if (isLotWidthAvailable) " | Double" else "Double"
                                                }
                                                else if (myCollectionRecentSearchAnswerModel.answer == "I don't mind") {
                                                    storey = if (isLotWidthAvailable) " | All" else "All"
                                                }
                                                myCollectionRecentSearchText += storey

                                                AppConstants.breadCrumbMyCollection[myCollectionRecentSearchAnswerModel.feature] = BreadCrumbHashMapModel(
                                                    storey, myCollectionRecentSearchAnswerModel.feature
                                                )

                                            }
                                            "No Of Bedrooms" -> {
                                                var bedRooms = ""
                                                when (myCollectionRecentSearchAnswerModel.answer) {
                                                    "3" -> {
                                                        bedRooms = "3 Bed"
                                                    }
                                                    "4" -> {
                                                        bedRooms = "4 Bed"

                                                    }
                                                    "5" -> {
                                                        bedRooms = "5 Bed"
                                                    }
                                                    "I don't mind" -> {
                                                        if(!myCollectionRecentSearchText.contains("All")) {
                                                            bedRooms = "All"
                                                        }
                                                    }
                                                }
                                                if(bedRooms!="") {
                                                    myCollectionRecentSearchText += " | $bedRooms"
                                                }
                                                AppConstants.breadCrumbMyCollection[myCollectionRecentSearchAnswerModel.feature] = BreadCrumbHashMapModel(
                                                    bedRooms, myCollectionRecentSearchAnswerModel.feature
                                                )

                                            }
                                            "Grand Alfresco" -> {
                                                myCollectionRecentSearchText += " | Alfresco"

                                                AppConstants.breadCrumbMyCollection[myCollectionRecentSearchAnswerModel.feature] = BreadCrumbHashMapModel(
                                                    "Alfresco", myCollectionRecentSearchAnswerModel.feature
                                                )

                                            }
                                            "Storage (more than 1 per room)" -> {
                                                myCollectionRecentSearchText += " | More than 1 per room"

                                                AppConstants.breadCrumbMyCollection[myCollectionRecentSearchAnswerModel.feature] = BreadCrumbHashMapModel(
                                                    myCollectionRecentSearchAnswerModel.feature , myCollectionRecentSearchAnswerModel.feature
                                                )


                                            }
                                            "European Laundry" -> {
                                                myCollectionRecentSearchText += " | European Laundry"

                                                AppConstants.breadCrumbMyCollection[myCollectionRecentSearchAnswerModel.feature] = BreadCrumbHashMapModel(
                                                    myCollectionRecentSearchAnswerModel.feature , myCollectionRecentSearchAnswerModel.feature
                                                )
                                            }
                                            "Separate Kids Living Area" -> {
                                                myCollectionRecentSearchText += " | Separate kids living area"

                                                AppConstants.breadCrumbMyCollection[myCollectionRecentSearchAnswerModel.feature] = BreadCrumbHashMapModel(
                                                    myCollectionRecentSearchAnswerModel.feature , myCollectionRecentSearchAnswerModel.feature
                                                )
                                            }
                                            "Separate Living Area" -> {
                                                myCollectionRecentSearchText += " | Separate Living Area"

                                                AppConstants.breadCrumbMyCollection[myCollectionRecentSearchAnswerModel.feature] = BreadCrumbHashMapModel(
                                                    myCollectionRecentSearchAnswerModel.feature , myCollectionRecentSearchAnswerModel.feature
                                                )
                                            }
                                            "Straight Corridor" -> {
                                                myCollectionRecentSearchText += " | Straight Corridor"

                                                AppConstants.breadCrumbMyCollection[myCollectionRecentSearchAnswerModel.feature] = BreadCrumbHashMapModel(
                                                    myCollectionRecentSearchAnswerModel.feature , myCollectionRecentSearchAnswerModel.feature
                                                )

                                            }
                                            "Living/Meals Entire Rear" -> {
                                                myCollectionRecentSearchText += " | Living/Meals Entire Rear"

                                                AppConstants.breadCrumbMyCollection[myCollectionRecentSearchAnswerModel.feature] = BreadCrumbHashMapModel(
                                                    myCollectionRecentSearchAnswerModel.feature , myCollectionRecentSearchAnswerModel.feature
                                                )

                                            }
                                            "Study" -> {
                                                myCollectionRecentSearchText += " | Study"
                                                AppConstants.breadCrumbMyCollection[myCollectionRecentSearchAnswerModel.feature] = BreadCrumbHashMapModel(
                                                    myCollectionRecentSearchAnswerModel.feature , myCollectionRecentSearchAnswerModel.feature
                                                )

                                            }
                                            "Minor Bedrooms Wing" -> {
                                                myCollectionRecentSearchText += " | Minor Bedrooms Wing"

                                                AppConstants.breadCrumbMyCollection[myCollectionRecentSearchAnswerModel.feature] = BreadCrumbHashMapModel(
                                                    myCollectionRecentSearchAnswerModel.feature , myCollectionRecentSearchAnswerModel.feature
                                                )

                                            }
                                            "Bedroom At Front" -> {
                                                myCollectionRecentSearchText += " | Bedroom At Front"

                                                AppConstants.breadCrumbMyCollection[myCollectionRecentSearchAnswerModel.feature] = BreadCrumbHashMapModel(
                                                    myCollectionRecentSearchAnswerModel.feature , myCollectionRecentSearchAnswerModel.feature
                                                )

                                            }

                                            "Price" -> {
                                                myCollectionRecentSearchText += " | Price"

                                                AppConstants.breadCrumbMyCollection[myCollectionRecentSearchAnswerModel.feature] = BreadCrumbHashMapModel(
                                                    myCollectionRecentSearchAnswerModel.feature , myCollectionRecentSearchAnswerModel.feature
                                                )

                                            }
                                            /*"resultsCount" -> {
                                                myCollectionRecentSearchText =
                                                    getString(R.string.take_a_quick_survey_to_find_your_perfect_design)
                                            }*/
                                        }


                                    }

                                    if (regionsData.searchJsonList.size == 1) {
                                        val model = regionsData.searchJsonList[0]
                                        if (model.feature == "resultsCount") {
                                            myCollectionRecentSearchText =
                                                getString(R.string.take_a_quick_survey_to_find_your_perfect_design)
                                        }
                                    }

                                }
                                if (isVisible && isAdded && !AppConstants.isRecentDialogShowed) {
                                    if (regionsData.searchJsonList.isNotEmpty()) {
                                        println("regionsData.searchJsonList:: "+regionsData.searchJsonList.size+" data "+Gson().toJson(regionsData.searchJsonList))
                                        if (regionsData.searchJsonList.size == 2) {

                                            var model = regionsData.searchJsonList[0]
                                            if (model.feature != "resultsCount") {
                                                showRecentSearchDialog(myCollectionRecentSearchText)
                                            } else {
                                                showRecentSearchDialog(getString(R.string.take_a_quick_survey_to_find_your_perfect_design))
                                            }
                                        } else {
                                            showRecentSearchDialog(myCollectionRecentSearchText)
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
    }
    private fun profileNotificationCountView(notificationCount:Int){
        if(notificationCount==0){
            profileWithBadgeBinding.profileNotification.visibility=View.GONE
        }else{
            profileWithBadgeBinding.profileNotification.visibility=View.VISIBLE
        }
    }
    private fun initViews() {
        displayToolbarViewModel = ViewModelProviders.of(requireActivity())[DisplayToolbarViewModel::class.java]
        profileWithBadgeBinding.profileNotification.text=AppConstants.TotalMyFavs.toString()
        displayToolbarViewModel.favouritesCount.observe(viewLifecycleOwner){
            profileWithBadgeBinding.profileNotification.text = it.toString()
            profileNotificationCountView(it)
        }
        profileNotificationCountView(AppConstants.TotalMyFavs)
        toolBinding.ivBack.setOnClickListener {
            activity?.onBackPressed()
        }
        customSharedPreferences = CustomSharedPreferences(AppController.getInstance())
        toolBinding.customRecyclerview.initView(requireContext())
        toolBinding.tvTool.visibility = View.VISIBLE
        customProgressDialog = CustomProgressDialog(requireContext())

        var isUserLoggedIn = customSharedPreferences.getUserLogin()
        if (!isUserLoggedIn) {
            profileWithBadgeBinding.tvFavorites.background = resources.getDrawable(R.drawable.disable_rectangle_new)
            profileWithBadgeBinding.profileNotification.visibility=View.GONE
        }
        profileWithBadgeBinding.tvFavorites.setOnClickListener {

            var isUserLoggedIn = customSharedPreferences.getUserLogin()
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
        bottomLayoutBinding.previous.background =
            ContextCompat.getDrawable(requireContext(), R.drawable.rectangle_disable_button)
        bottomLayoutBinding.buttonPrevious.isEnabled = true


        stateId = customSharedPreferences.getStateID()
        userID = customSharedPreferences.getUserId()

        bottomLayoutBinding.tvDesigns.text =
            AppUtils.getDesignsTextBasedOnCount(AppConstants.HouseCount)
        bottomLayoutBinding.tvDesigns.setOnClickListener {
            if (isLotEntered){
                removeNextBreadCrumbItem(FEATURE_STOREYS)
            }else {
                //CLEAR RECENT QUIZ
                AppConstants.breadCrumbMyCollection.clear()
                AppConstants.newHomeHashMap.clear()
            }

            if (isNextClickable) {
                if (this::newHomeNextFeatureResponseModel.isInitialized) {
                    val bundle = Bundle()
                    bundle.putBoolean(AppConstants.SELECT_FAVORITE, false)
                    bundle.putParcelable(
                        AppConstants.NEWHOMENEXTQUESTIONMODEL,
                        newHomeNextFeatureResponseModel.newHomesNextFeatures
                    )
                    loadFragment(MyCollectionPlacesFragment(), bundle)

                }
            }else{
                AppUtils.showCustomCenterToast(
                    bottomLayoutBinding.buttonNext.context,
                    "No Houses Found"
                )
            }
        }
        binding.iDontHaveLand.setOnClickListener {
            iHaveLandSelected = false
            isOptionSelected = false
            isSelected = true
            isLotEntered = false
            AppUtils.removeNextBreadCrumbItem(FEATURE_STOREYS)
            AppConstants.breadCrumbMyCollection.clear()
            if (this::quizListModel.isInitialized) {
                toolBinding.customRecyclerview.visibility = View.GONE
                toolBinding.tvTool.visibility = View.VISIBLE
                toolBinding.tvTool.text =
                    getString(R.string.take_a_quick_survey_to_find_your_perfect_design)
                showHideViews(binding.iDontHaveLand, binding.tvIDontHave,binding.into)
                binding.groupView.visibility = View.INVISIBLE
                binding.proceedButton.visibility = View.GONE
                loadNextQuestionFeature(quizListModel, "I don't mind", "")
            }

        }
        binding.IHaveLand.setOnClickListener {
            iHaveLandSelected = true
            isOptionSelected = false
            isSelected = true
            AppUtils.removeNextBreadCrumbItem(FEATURE_STOREYS)
            toolBinding.tvTool.visibility = View.VISIBLE
            toolBinding.tvTool.text = getString(R.string.take_a_quick_survey_to_find_your_perfect_design)
            bottomLayoutBinding.next.background = ContextCompat.getDrawable(
                activity!!,
                R.drawable.disable_next_background
            )

            if (this::quizListModel.isInitialized) {
                toolBinding.tvTool.visibility = View.VISIBLE
                toolBinding.customRecyclerview.visibility = View.GONE
                AppConstants.breadCrumbMyCollection.clear()
                showHideViews(binding.IHaveLand, binding.tvIHave,binding.right)
                binding.groupView.visibility = View.VISIBLE
                binding.etMeters.text?.clear()

            }

        }

        binding.etMeters.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (s != null) {
                    if (s.isNotEmpty()) {
                        binding.proceedButton.visibility = View.VISIBLE
                    } else {
                        binding.proceedButton.visibility = View.GONE
                        bottomLayoutBinding.next.background = ContextCompat.getDrawable(
                            activity!!,
                            R.drawable.disable_next_background
                        )
                        toolBinding.tvTool.visibility=View.VISIBLE
                        toolBinding.customRecyclerview.visibility = View.GONE

                        toolBinding.tvTool.text =
                            getString(R.string.take_a_quick_survey_to_find_your_perfect_design)
                    }
                }
            }
        })
        binding.proceedButton.setOnClickListener {

            if (this::quizListModel.isInitialized) {
                var value = binding.etMeters.text.toString()
                if(value.toDouble()>99.99 || value.toDouble()<1.0){
                    Toast.makeText(AppController.getInstance(),"Please enter value between 1 to 100m",Toast.LENGTH_SHORT).show()
                }
                else {
                    toolBinding.tvTool.visibility=View.GONE
                    toolBinding.customRecyclerview.visibility = View.VISIBLE
                    isLotEntered = true
                    loadNextQuestionFeature(quizListModel, "", value)
                }
            }
        }
        binding.etMeters.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                Timber.e("Meter Value is : ${binding.etMeters.text.toString()}")
                if (this::quizListModel.isInitialized) {
                    var value = binding.etMeters.text.toString()
                    loadNextQuestionFeature(quizListModel, "", value)
                }
                true
            }
            false
        }


        bottomLayoutBinding.buttonNext.setOnClickListener {
            val fragment = AppConstants.storedFragments[FEATURE]

            /* if (fragment != null){
                 fragment.arguments?.let {
                     loadFragment(fragment, it)
                 }
                 return@setOnClickListener
             }*/
            if (isSelected) {
                if (isNextClickable) {
                    if (!iHaveLandSelected) {
                        if (this::newHomeNextFeatureResponseModel.isInitialized) {
                            newHomeNextFeatureResponseModel.newHomesNextFeatures.let {
                                val bundle = Bundle()
                                bundle.putParcelable(
                                    AppConstants.NEWHOMENEXTQUESTIONMODEL,
                                    AppConstants.newHomeNextFeatureResponseModel!!.newHomesNextFeatures
                                )
                                loadFragment(nextFragment, bundle)
                            }
                        }
                    } else {
                        val meters = binding.etMeters.text.toString()
                        if (meters.isNotEmpty()) {
                            if (isOptionSelected) {
                                AppConstants.breadCrumbMyCollection[FEATURE_STOREYS] =
                                    BreadCrumbHashMapModel(meters+"M", FEATURE_STOREYS)
                                setToolbarBreadCrumb(false)
                                if (this::newHomeNextFeatureResponseModel.isInitialized) {
                                    newHomeNextFeatureResponseModel.newHomesNextFeatures.let {
                                        val bundle = Bundle()
                                        bundle.putParcelable(
                                            AppConstants.NEWHOMENEXTQUESTIONMODEL,
                                            AppConstants.newHomeNextFeatureResponseModel!!.newHomesNextFeatures
                                        )
                                        loadFragment(nextFragment, bundle)
                                    }
                                }
                            }
                        } else {
                            AppUtils.showCustomCenterToast(
                                requireContext(),
                                "Please enter lot width"
                            )
                        }

                    }

                }
            } else {
                AppUtils.showCustomCenterToast(requireContext(), "Please select one option")
            }

        }

        toolBinding.tvHowDoesIt.setOnClickListener {
            startActivity(Intent(activity, PlayerActivity::class.java))
        }
        bottomLayoutBinding.buttonPrevious.setOnClickListener {
            /* isToolbarBack = false
             activity?.onBackPressed()*/
        }
    }

    private fun getHouseCount() {

        val jsonObject = JsonObject()
        jsonObject.addProperty("StateId", stateId)
        jsonObject.addProperty("IncludePackages", 0)
        jsonObject.addProperty("SortByPrice", 0)
        jsonObject.addProperty("UserId", userID)
        jsonObject.addProperty("FeatureAllFilter", "8")

        myHomeQuizViewModel.fetchNewHomesHouseCount(jsonObject)
    }

    private fun loadNextQuestionFeature(
        newHomeQuizListModel: NewHomeQuizListModel,
        answer: String, maxValue: String
    ) {
        AppConstants.newHomeHashMap = LinkedHashMap()
        val newHomeJsonArrayList = ArrayList<NewHomeJsonObject>()
        val newHomeJsonObject = newHomeQuizListModel.feature?.let {
            newHomeQuizListModel.question?.let { it1 ->
                NewHomeJsonObject(
                    feature = it,
                    question = it1,
                    answer = answer,
                    minValue = "0.00",
                    maxValue = maxValue
                )
            }
        }
        if (newHomeJsonObject != null) {
            newHomeJsonArrayList.add(newHomeJsonObject)
        }
        newHomeQuizListModel.feature?.let {
            if (newHomeJsonObject != null) {
                AppConstants.newHomeHashMapNextFeature[it] = newHomeJsonObject

                var arrayList: ArrayList<NewHomeJsonObject> = ArrayList()
                arrayList.add(newHomeJsonObject)
                AppConstants.newHomeHashMap[it] = arrayList
            }
            apiCallHappen = true
        }
        AppConstants.removeNextBredCrumpItem(FEATURE, newHomeJsonArrayList)
        AppConstants.newHomeJsonObjectsList = newHomeJsonArrayList
        questionModel = newHomeJsonObject

        val mMyCollectionQuizQuestionRequest = newHomeQuizListModel.feature?.let {
            MyCollectionQuizQuestionRequest(
                stateId,
                includePackages = 0,
                sortByPrice = 0,
                userId = userID,
                newHomeJsonList = newHomeJsonArrayList
            )
        }
        if (mMyCollectionQuizQuestionRequest != null ) {
            myHomeQuizViewModel.fetchNewHomesCollectionsNextFeature(mMyCollectionQuizQuestionRequest)
        }
        /* else{
             loadNextQuestionFeature(quizListModel, "", binding.etMeters.text.toString())

         }*/
        setToolbarBreadCrumb(AppConstants.isFirstMyCollection)


    }


    fun loadFragment(fragment: Fragment, bundle: Bundle) {
        val transaction: FragmentTransaction = activity?.supportFragmentManager!!.beginTransaction()
        bundle.putParcelable(AppConstants.PREVIOUS_QUESTION, questionModel)
        fragment.arguments = bundle
        if (fragment !is MyCollectionPlacesFragment) {
            AppConstants.storedFragments[FEATURE] = fragment
        }
        if (fragment.isAdded) {
            return
        }
        transaction.replace(R.id.fl_content, fragment)
        transaction.addToBackStack(MyCollectionDashboardFragment::class.simpleName)
        transaction.commit()
    }

    private fun showHideViews(view: LinearLayout, textView: TextView,imageview:ImageView) {
        binding.into.setColorFilter(ContextCompat.getColor(AppController.getInstance(), R.color.grey_text_font_3_1));
        binding.right.setColorFilter(ContextCompat.getColor(AppController.getInstance(), R.color.grey_text_font_3_1));

        binding.iDontHaveLand.background =
            ContextCompat.getDrawable(AppController.getInstance(), R.drawable.rectangel_shape)
        binding.IHaveLand.background =
            ContextCompat.getDrawable(AppController.getInstance(), R.drawable.rectangel_shape)
        binding.tvIDontHave.setTextColor(
            ContextCompat.getColor(
                AppController.getInstance(),
                R.color.grey_text_font_3_1
            )
        )
        binding.tvIHave.setTextColor(
            ContextCompat.getColor(
                AppController.getInstance(),
                R.color.grey_text_font_3_1
            )
        )
        view.background =
            ContextCompat.getDrawable(AppController.getInstance(), R.drawable.rectangle_orange_bg)
        textView.setTextColor(ContextCompat.getColor(AppController.getInstance(), R.color.white_3_1))
        imageview.setColorFilter(ContextCompat.getColor(AppController.getInstance(), R.color.white_3_1));


    }

    override fun onBackPressed(): Boolean {
        AppConstants.HouseCount = 0
        AppConstants.newHomeJsonObjectsList.clear()
        AppConstants.isRecentDialogShowed = false
        AppConstants.storedFragments.clear()
        return false
    }

    private fun showRecentSearchDialog(contentString: String) {
        AppConstants.isRecentDialogShowed = true
        println("contentString:: $contentString")
        val layoutRecentSearchBinding: LayoutRecentSearchMycollectionBinding =
            LayoutRecentSearchMycollectionBinding.inflate(layoutInflater)
        val shareAlertBuilder: AlertDialog.Builder =
            AlertDialog.Builder(context)
        shareAlertBuilder.setView(layoutRecentSearchBinding.root)
        val alertDialog: AlertDialog = shareAlertBuilder.create()
        layoutRecentSearchBinding.tvStart.setOnClickListener {
            AppConstants.breadCrumbMyCollection.clear()
            AppConstants.newHomeHashMap.clear()
            alertDialog.dismiss()
        }
        if (contentString.isNotEmpty()) {
            layoutRecentSearchBinding.tvRegionPrice.text = contentString
        }else{
            layoutRecentSearchBinding.tvRegionPrice.text ="Selected All Designs"

        }
        layoutRecentSearchBinding.ivClose.setOnClickListener {
            alertDialog.dismiss()
        }
        layoutRecentSearchBinding.tvShowpackages.setOnClickListener { view ->
            if (this::newHomeNextFeatureResponseModel.isInitialized) {
                if (AppConstants.newHomeHashMap.size > 0) {
                    AppConstants.newHomeHashMap.clear()
                }
                AppConstants.newHomeHashMap.putAll(recentSearchHashMap)
                newHomeNextFeatureResponseModel.newHomesNextFeatures.let {
                    val bundle = Bundle()
                    bundle.putBoolean(AppConstants.SELECT_FAVORITE, false)
                    bundle.putParcelable(
                        AppConstants.NEWHOMENEXTQUESTIONMODEL,
                        it
                    )
                    loadFragment(MyCollectionPlacesFragment(), bundle)
                }
            }
            alertDialog.dismiss()
        }

        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        alertDialog.show()

    }


    override fun breadCrumb(breadCrumb: BreadcrumbModel) {

        Timber.e("Selected --${breadCrumb.breadCrumbTitle} ---${breadCrumb.isSelected}")

    }

    private fun setToolbarBreadCrumb(isFistTime: Boolean) {

        var arrayList: ArrayList<BreadcrumbModel> = ArrayList()
        if (AppConstants.breadCrumbMyCollection.keys.size > 0) {
            val myVeryOwnIterator: Iterator<*> = AppConstants.breadCrumbMyCollection.keys.iterator()
            while (myVeryOwnIterator.hasNext()) {
                val key = myVeryOwnIterator.next() as String
                var value = AppConstants.breadCrumbMyCollection[key]
                if (value != null) {
                    if (value.breadCrumbTitle != "") {
                        if (value.titleFragment == FEATURE_STOREYS) {
                            arrayList.add(
                                BreadcrumbModel(
                                    value.breadCrumbTitle,
                                    value.titleFragment,
                                    true
                                )
                            )
                        } else {
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
        }
        if (isFistTime) {
            arrayList.clear()
            AppConstants.isFirstMyCollection = false
        }
        toolBinding.customRecyclerview.setData(arrayList, this)
    }

    fun removeNextBreadCrumbItem(breadCrumbKey: String) {
        val newHashMap: HashMap<String, BreadCrumbHashMapModel> = LinkedHashMap()

        if (AppConstants.breadCrumbMyCollection.keys.size > 0) {
            val breadCrumbIterator: Iterator<*> =
                AppConstants.breadCrumbMyCollection.keys.iterator()
            while (breadCrumbIterator.hasNext()) {
                val key = breadCrumbIterator.next() as String
                var value = AppConstants.breadCrumbMyCollection[key]
                if (key == breadCrumbKey) {
                    if (value != null) {
                        newHashMap[key] = value
                    }

                }

            }

            if (newHashMap.size > 0) {
                AppConstants.breadCrumbMyCollection.clear()
                val newBreadCrumbIterator: Iterator<*> = newHashMap.keys.iterator()
                while (newBreadCrumbIterator.hasNext()) {
                    val key = newBreadCrumbIterator.next() as String
                    val value = newHashMap[key]
                    if (value != null) {
                        AppConstants.breadCrumbMyCollection[key] = value
                    }
                }

            }
        }
    }


}