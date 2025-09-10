package com.dmss.burbankapp.ui.designs

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
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
import com.dmss.burbankapp.databinding.FragmentStoreysMycollectionBinding
import com.dmss.burbankapp.databinding.InclueToolDesignBinding
import com.dmss.burbankapp.databinding.ProfileWithBadgeBinding
import com.dmss.burbankapp.ui.dashboard.DashboardViewModel
import com.dmss.burbankapp.ui.main.MainActivity
import com.dmss.burbankapp.ui.main.MainActivity.OnBackPressedListener
import com.dmss.burbankapp.ui.video.PlayerActivity
import com.dmss.burbankapp.ui.view.BreadCrumbAdapter
import com.dmss.burbankapp.ui.view.CustomProgressDialog
import com.dmss.burbankapp.utils.AppConstants
import com.dmss.burbankapp.utils.AppUtils
import common.AppController
import timber.log.Timber

class StoreysMyCollectionFragment : Fragment(), OnBackPressedListener,
    BreadCrumbAdapter.BreadcrumbItemClickListener {
    private var apiCallHappen: Boolean = false
    private val FEATURE_STOREYS = "Storeys"
    private var customProgressDialog: CustomProgressDialog? = null
    private lateinit var myHomeQuizViewModel: NewHomeQuizViewModel
    lateinit var dashboardViewModel: DashboardViewModel
    private var stateId: Int = -1
    private var userID: Int = -1
    lateinit var binding: FragmentStoreysMycollectionBinding
    var isSelectedAnything: Boolean = false


    private lateinit var bottomLayoutBinding: BottomLayoutMycollectionBinding
    private lateinit var mainActivity: MainActivity
    private var newHomeJsonArrayList: ArrayList<NewHomeJsonObject> = ArrayList()


    var isNextClickable: Boolean = true

    private lateinit var nextFragment: Fragment
    private lateinit var newHomeNextFeatureResponseModel: NewHomeNextFeatureResponseModel


    private lateinit var customSharedPreferences: CustomSharedPreferences
    lateinit var toolBinding: InclueToolDesignBinding
    lateinit var profileWithBadgeBinding: ProfileWithBadgeBinding

    var newHomesNextFeatures: NewHomesNextFeaturesModel? = null

    //Validations
    var storeySelected = false
    var isToolbarBack = false
    private var NEWHOMESHOUSECOUNT = 0
    private var isButtonClicked = false
    override fun onAttach(context: Context) {
        mainActivity = activity as MainActivity
        super.onAttach(context)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStoreysMycollectionBinding.inflate(inflater, container, false)
        bottomLayoutBinding = BottomLayoutMycollectionBinding.bind(binding.root)
        toolBinding = InclueToolDesignBinding.bind(binding.root)
        profileWithBadgeBinding = ProfileWithBadgeBinding.bind(binding.root)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isToolbarBack = true
        isSelectedAnything = false
        initViews()
        setupViewModel()
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
        //bottomLayoutBinding.tvDesigns.visibility = View.INVISIBLE
    }

    private fun setupObserver() {
        myHomeQuizViewModel.getNewHomeNextQuestionLiveData().observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    val newHomeNextFeatureResponseModel: NewHomeNextFeatureResponseModel? = it.data
                    if (newHomeNextFeatureResponseModel != null && newHomeNextFeatureResponseModel.status) {
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
                            if (NEWHOMESHOUSECOUNT == 0 ){
                                AppUtils.showCustomCenterToast(
                                    bottomLayoutBinding.buttonNext.context,
                                    "No Houses Found"
                                )
                            }

                            nextFragment =
                                if (newHomeNextFeatureResponseModel.newHomesNextFeatures?.nextFeature != null) {
                                    mainActivity.fetchFirstMyCollectionQuizFragment(
                                        newHomeNextFeatureResponseModel.newHomesNextFeatures?.nextFeature!!
                                    )
                                } else {
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
    }

    private fun initViews() {
        AppUtils.profileCountStatus(profileWithBadgeBinding.profileNotification)
        if (AppConstants.breadCrumbMyCollection.isEmpty()) {
            toolBinding.tvTool.visibility = View.VISIBLE
            toolBinding.tvTool.text =
                getString(R.string.take_a_quick_survey_to_find_your_perfect_design)
        }
        newHomesNextFeatures = arguments?.getParcelable(AppConstants.NEWHOMENEXTQUESTIONMODEL)
        customSharedPreferences = CustomSharedPreferences(AppController.getInstance())
        stateId = customSharedPreferences.getStateID()
        userID = customSharedPreferences.getUserId()
        toolBinding.ivBack.setOnClickListener {
            activity?.onBackPressed()
        }
        toolBinding.customRecyclerview.initView(requireContext())
        setToolbarBreadCrumb(false)
        customProgressDialog = CustomProgressDialog(requireContext())
        var isUserLoggedIn = customSharedPreferences.getUserLogin()
        if (!isUserLoggedIn) {
            profileWithBadgeBinding.tvFavorites.background = resources.getDrawable(R.drawable.disable_rectangle_new)
            profileWithBadgeBinding.profileNotification.visibility=View.GONE
        }
        profileWithBadgeBinding.tvFavorites.setOnClickListener {
            val isUserLoggedIn = customSharedPreferences.getUserLogin()
            if (isUserLoggedIn) {
               /* val bundle = Bundle()
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
        bottomLayoutBinding.buttonPrevious.isEnabled = true

        bottomLayoutBinding.tvDesigns.text =
            AppUtils.getDesignsTextBasedOnCount(AppConstants.HouseCount)
        bottomLayoutBinding.tvDesigns.setOnClickListener {
            //CLEAR RECENT QUIZ
            /*if (isNextClickable) {
                if (this::newHomeNextFeatureResponseModel.isInitialized) {
                    loadMyCollectionPackagesFragment()
                }
            }*/

            if (isNextClickable) {
                if (this::newHomeNextFeatureResponseModel.isInitialized) {
                    val bundle = Bundle()
                    bundle.putBoolean(AppConstants.SELECT_FAVORITE, false)
                    bundle.putParcelable(
                        AppConstants.NEWHOMENEXTQUESTIONMODEL,
                        newHomeNextFeatureResponseModel.newHomesNextFeatures
                    )
                    loadFragment(MyCollectionPlacesFragment(), bundle)

                } else if (newHomesNextFeatures != null) {
                    newHomesNextFeatures?.let {
                        val bundle = Bundle()
                        bundle.putBoolean(AppConstants.SELECT_FAVORITE, false)
                        bundle.putParcelable(
                            AppConstants.NEWHOMENEXTQUESTIONMODEL,
                            it
                        )
                        loadFragment(MyCollectionPlacesFragment(), bundle)
                    }
                }
            }


        }

        binding.llSingle.setOnClickListener {
            isButtonClicked = true
            AppUtils.removeNextBreadCrumbItem(FEATURE_STOREYS)
            showHideViews(binding.llSingle, binding.tvSingle,binding.singleImg)
            toolBinding.tvTool.visibility = View.GONE
            loadNextQuestionFeature("1")

        }
        binding.llDouble.setOnClickListener {
            isButtonClicked = true
            AppUtils.removeNextBreadCrumbItem(FEATURE_STOREYS)
            showHideViews(binding.llDouble, binding.tvDouble,binding.doubleImg)
            toolBinding.tvTool.visibility = View.GONE
            newHomesNextFeatures?.let {
                loadNextQuestionFeature("2")
            }


        }
        binding.llNotSure.setOnClickListener {
            isButtonClicked = true
            showHideViews(binding.llNotSure, binding.tvNotsure,binding.groupQuestion)
            toolBinding.tvTool.visibility = View.GONE
            newHomesNextFeatures?.let {
                loadNextQuestionFeature("I don't mind")
            }

        }

        bottomLayoutBinding.buttonNext.setOnClickListener {
            val fragment = AppConstants.storedFragments[FEATURE_STOREYS]
            if (fragment != null){
                fragment.arguments?.let {
                    loadFragment(fragment, it)
                }
                return@setOnClickListener
            }
            if (isNextClickable) {
                if (storeySelected) {
                    if (NEWHOMESHOUSECOUNT <= 7) {
                        loadMyCollectionPackagesFragment()
                    } else {
                        if (this::newHomeNextFeatureResponseModel.isInitialized) {
                            newHomeNextFeatureResponseModel.newHomesNextFeatures.let {
                                val bundle = Bundle()
                                bundle.putParcelable(
                                    AppConstants.NEWHOMENEXTQUESTIONMODEL,
                                    newHomeNextFeatureResponseModel.newHomesNextFeatures
                                )
                                loadFragment(nextFragment, bundle)
                            }
                        }
                    }
                } else {
                    AppUtils.showCustomCenterToast(requireContext(), "Please select storeys")
                }
            }

        }

        toolBinding.tvHowDoesIt.setOnClickListener {
            startActivity(Intent(activity, PlayerActivity::class.java))
        }
        bottomLayoutBinding.buttonPrevious.setOnClickListener {
            isToolbarBack = false
            activity?.onBackPressed()
        }


    }


    private fun loadMyCollectionPackagesFragment() {
        if (this::newHomeNextFeatureResponseModel.isInitialized) {
            val bundle = Bundle()
            bundle.putBoolean(AppConstants.SELECT_FAVORITE, false)
            bundle.putParcelable(
                AppConstants.NEWHOMENEXTQUESTIONMODEL,
                newHomeNextFeatureResponseModel.newHomesNextFeatures
            )
            loadFragment(MyCollectionPlacesFragment(), bundle)

        }
    }

    private fun loadNextQuestionFeature(
        answer: String
    ) {
        val newHomeJsonObject = newHomesNextFeatures?.nextFeature?.let {
            newHomesNextFeatures!!.nextFeatureQuestion?.let { it1 ->
                NewHomeJsonObject(
                    feature = it,
                    question = it1,
                    answer = answer,
                )
            }
        }

        AppConstants.newHomeJsonObjectsList = newHomeJsonArrayList
        newHomesNextFeatures?.nextFeature?.let {
            if (newHomeJsonObject != null) {
                var arrayList: ArrayList<NewHomeJsonObject> = ArrayList()
                arrayList.add(newHomeJsonObject)

                AppConstants.newHomeHashMap[it] = arrayList

                when (answer) {
                    "1" -> {
                        AppConstants.toolheaderHashMap[FEATURE_STOREYS] = "Single"
                        AppConstants.breadCrumbMyCollection[FEATURE_STOREYS] =
                            BreadCrumbHashMapModel(
                                "Single",
                                FEATURE_STOREYS
                            )
                    }
                    "2" -> {
                        AppConstants.toolheaderHashMap[FEATURE_STOREYS] = "Double"
                        AppConstants.breadCrumbMyCollection[FEATURE_STOREYS] =
                            BreadCrumbHashMapModel(
                                "Double",
                                FEATURE_STOREYS
                            )
                    }
                    else -> {
                        AppConstants.toolheaderHashMap[FEATURE_STOREYS] = "All"
                        AppConstants.breadCrumbMyCollection[FEATURE_STOREYS] =
                            BreadCrumbHashMapModel(
                                "All",
                                FEATURE_STOREYS
                            )

                    }
                }
            }
            apiCallHappen = true
        }
        var feature = newHomesNextFeatures?.nextFeature
        newHomeJsonArrayList.clear()
        for ((_, value) in AppConstants.newHomeHashMap) {
            for (model in value) {
                if (model.feature == feature) {
                    newHomeJsonArrayList.add(model)
                    break
                } else {
                    newHomeJsonArrayList.add(model)
                }

            }
        }
        AppConstants.removeNextBredCrumpItem(FEATURE_STOREYS, newHomeJsonArrayList)
        AppConstants.newHomeJsonObjectsList = newHomeJsonArrayList
        val mMyCollectionQuizQuestionRequest = newHomesNextFeatures?.nextFeature?.let {
            MyCollectionQuizQuestionRequest(
                stateId,
                includePackages = 0,
                sortByPrice = 0,
                userId = userID,
                newHomeJsonList = newHomeJsonArrayList
            )
        }
        if (mMyCollectionQuizQuestionRequest != null) {
            myHomeQuizViewModel.fetchNewHomesCollectionsNextFeature(mMyCollectionQuizQuestionRequest)
        }
        setToolbarBreadCrumb(false)


    }

    fun loadFragment(fragment: Fragment, bundle: Bundle) {
        val transaction: FragmentTransaction = activity?.supportFragmentManager!!.beginTransaction()
        fragment.arguments = bundle
        if (fragment !is MyCollectionPlacesFragment) {
            AppConstants.storedFragments[FEATURE_STOREYS] = fragment
        }
        if (fragment.isAdded) {
            return
        }
        transaction.replace(R.id.fl_content, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun showHideViews(view: LinearLayout, textView: TextView,imageview: ImageView) {
        binding.groupQuestion.setColorFilter(ContextCompat.getColor(AppController.getInstance(), R.color.grey_text_font_3_1));
        binding.singleImg.setColorFilter(ContextCompat.getColor(AppController.getInstance(), R.color.grey_text_font_3_1));
        binding.doubleImg.setColorFilter(ContextCompat.getColor(AppController.getInstance(), R.color.grey_text_font_3_1));

        binding.llSingle.background =
            ContextCompat.getDrawable(AppController.getInstance(), R.drawable.rectangel_shape)
        binding.llDouble.background =
            ContextCompat.getDrawable(AppController.getInstance(), R.drawable.rectangel_shape)
        binding.llNotSure.background =
            ContextCompat.getDrawable(AppController.getInstance(), R.drawable.rectangel_shape)
        binding.tvSingle.setTextColor(
            ContextCompat.getColor(
                AppController.getInstance(),
                R.color.grey_text_font_3_1
            )
        )
        binding.tvDouble.setTextColor(
            ContextCompat.getColor(
                AppController.getInstance(),
                R.color.grey_text_font_3_1
            )
        )
        binding.tvNotsure.setTextColor(
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

    override fun onResume() {
        super.onResume()
        isToolbarBack = true
        if (AppConstants.newHomeJsonObjectsList.any { it.feature == FEATURE_STOREYS }) {
            isSelectedAnything = true
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
        myHomeQuizViewModel.fetchNewHomesCollectionsNextFeature(mMyCollectionQuizQuestionRequest)
        setButtonSelection()
    }

    private fun setButtonSelection(){
        val data = AppConstants.newHomeJsonObjectsList.find { it.feature == FEATURE_STOREYS }
        data?.let {
            when(it.answer){
                "1" -> showHideViews(binding.llSingle, binding.tvSingle,binding.singleImg)
                "2" -> showHideViews(binding.llDouble, binding.tvDouble,binding.doubleImg)
                else -> showHideViews(binding.llNotSure, binding.tvNotsure,binding.groupQuestion)
            }
        }
    }

    override fun onBackPressed(): Boolean {
        if (apiCallHappen && isToolbarBack) {
            mainActivity.removeLastElementOfHashMap()
            AppConstants.removeCurrentElement(FEATURE_STOREYS)
            AppConstants.storedFragments.remove(FEATURE_STOREYS)
        }
        return false
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
