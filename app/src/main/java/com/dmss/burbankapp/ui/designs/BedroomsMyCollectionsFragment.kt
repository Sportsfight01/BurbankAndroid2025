package com.dmss.burbankapp.ui.designs

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
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
import com.dmss.burbankapp.databinding.FragmentBedroomsMyCollectionsBinding
import com.dmss.burbankapp.databinding.InclueToolDesignBinding
import com.dmss.burbankapp.databinding.ProfileWithBadgeBinding
import com.dmss.burbankapp.ui.main.MainActivity
import com.dmss.burbankapp.ui.view.BreadCrumbAdapter
import com.dmss.burbankapp.ui.view.CustomProgressDialog
import com.dmss.burbankapp.utils.AppConstants
import com.dmss.burbankapp.utils.AppUtils
import common.AppController
import timber.log.Timber

const val NOT_SURE = "Not Sure"
class BedroomsMyCollectionsFragment : Fragment(), MainActivity.OnBackPressedListener,
    BreadCrumbAdapter.BreadcrumbItemClickListener {
    private val FEATURE_NO_OF_BEDROOM = "No Of Bedrooms"

    lateinit var myHomeQuizViewModel: NewHomeQuizViewModel
    private lateinit var newHomesNextFeaturesModel: NewHomesNextFeaturesModel
    private var stateId: Int = -1
    private var userID: Int = -1
    private var nextFragment: Fragment = Fragment()
    var isSelectedAnything: Boolean = false

    lateinit var bottomLayoutBinding: BottomLayoutMycollectionBinding
    lateinit var binding: FragmentBedroomsMyCollectionsBinding
    lateinit var toolBinding: InclueToolDesignBinding
    private lateinit var customSharedPreferences: CustomSharedPreferences
    private lateinit var mainActivity: MainActivity
    private var newHomeJsonArrayList: ArrayList<NewHomeJsonObject> = ArrayList()
    lateinit var profileWithBadgeBinding: ProfileWithBadgeBinding

    private lateinit var newHomeNextFeatureResponseModel: NewHomeNextFeatureResponseModel
    private var customProgressDialog: CustomProgressDialog? = null
    var isNextClickable = true
    private var apiCallHappen = false
    var isToolbarBack = true
    private var NEWHOMESHOUSECOUNT = 0
    private lateinit var bedRoomAdapter: BedRoomAdapter

    override fun onAttach(context: Context) {
        mainActivity = activity as MainActivity
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bedRoomAdapter = BedRoomAdapter{
            isSelectedAnything = true
            AppUtils.removeNextBreadCrumbItem(FEATURE_NO_OF_BEDROOM)
            if (it != NOT_SURE)
                loadNextQuestionFeature(newHomesNextFeaturesModel, it)
            else loadNextQuestionFeature(newHomesNextFeaturesModel, AppConstants.I_DONT_MIND)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentBedroomsMyCollectionsBinding.inflate(inflater, container, false)
        bottomLayoutBinding = BottomLayoutMycollectionBinding.bind(binding.root)
        toolBinding = InclueToolDesignBinding.bind(binding.root)
        profileWithBadgeBinding = ProfileWithBadgeBinding.bind(binding.root)

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initViews()
        setupViewModel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isToolbarBack = true
        isSelectedAnything = false
    }

    override fun onResume() {
        super.onResume()
        isToolbarBack = true
        if (AppConstants.newHomeJsonObjectsList.any { it.feature == FEATURE_NO_OF_BEDROOM }) {
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
            newHomeJsonList = AppConstants.newHomeJsonObjectsList,
            featureAllFilter="8")
        myHomeQuizViewModel.fetchNewHomesCollectionsNextFeature(mMyCollectionQuizQuestionRequest)
    }

    private fun getBedRoomList(): ArrayList<BedRooms>{
        val list = arrayListOf<BedRooms>()
        val data = AppConstants.newHomeJsonObjectsList.find { it.feature == FEATURE_NO_OF_BEDROOM }
        if (newHomesNextFeaturesModel.nextFeatureAnswers?.isNotEmpty() == true){
            newHomesNextFeaturesModel.nextFeatureAnswers?.forEach {
                list.add(BedRooms(it, data?.answer == it))
            }
        }
        list.add(BedRooms(NOT_SURE, data?.answer == AppConstants.I_DONT_MIND))
        return list
    }

    private fun setupViewModel() {
        myHomeQuizViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(
                ApiHelperImpl(RetrofitBuilder.apiService),
                DatabaseHelperImpl(DatabaseBuilder.getInstance(AppController.getInstance()))
            )
        ).get(NewHomeQuizViewModel::class.java)

        setupObserver()

    }

    private fun setupObserver() {
        myHomeQuizViewModel.getNewHomeNextQuestionLiveData().observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    customProgressDialog?.dismissProgress()

                    var newHomeNextFeatureResponseModel: NewHomeNextFeatureResponseModel? = it.data
                    if (newHomeNextFeatureResponseModel != null && newHomeNextFeatureResponseModel.status) {
                        this.newHomeNextFeatureResponseModel = newHomeNextFeatureResponseModel
                        if (newHomeNextFeatureResponseModel.status) {
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
                                        newHomeNextFeatureResponseModel.newHomesNextFeatures!!.nextFeature!!
                                    )
                                } else {
                                    MyCollectionPlacesFragment()
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
                        }
                    } else {
                        if (this::bottomLayoutBinding.isInitialized) {
                            isNextClickable = AppUtils.noPackagesAndDisableClick(
                                bottomLayoutBinding.tvDesigns,
                                bottomLayoutBinding.next, bottomLayoutBinding.buttonNext, false
                            )
                        }
                    }
                    setToolbarBreadCrumb()

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

    private fun loadNextQuestionFeature(
        newHomeNextFeatureModel: NewHomesNextFeaturesModel,
        answer: String
    ) {
        isSelectedAnything = true
        newHomeJsonArrayList = ArrayList()
        val newHomeJsonObject = newHomeNextFeatureModel.nextFeature?.let {
            newHomeNextFeatureModel.nextFeatureQuestion?.let { it1 ->
                NewHomeJsonObject(
                    feature = it,
                    question = it1,
                    answer = answer

                )
            }
        }
        newHomeNextFeatureModel.nextFeature?.let {
            if (newHomeJsonObject != null) {
                var arrayList: ArrayList<NewHomeJsonObject> = ArrayList()
                arrayList.add(newHomeJsonObject)

                AppConstants.newHomeHashMap[it] = arrayList
                if (answer != AppConstants.I_DONT_MIND) {
                    AppConstants.toolheaderHashMap[FEATURE_NO_OF_BEDROOM] = (" | $answer Bed")
                    AppConstants.breadCrumbMyCollection[FEATURE_NO_OF_BEDROOM] =
                        BreadCrumbHashMapModel("$answer Bed", FEATURE_NO_OF_BEDROOM)
                } else {
                    AppConstants.toolheaderHashMap[FEATURE_NO_OF_BEDROOM] = ("")
                    AppConstants.breadCrumbMyCollection[FEATURE_NO_OF_BEDROOM] =
                        BreadCrumbHashMapModel("", FEATURE_NO_OF_BEDROOM)
                }
                setToolbarBreadCrumb()
            }
            apiCallHappen = true
        }


        var feature = newHomeNextFeatureModel.nextFeature
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
        AppConstants.removeNextBredCrumpItem(FEATURE_NO_OF_BEDROOM, newHomeJsonArrayList)
        AppConstants.newHomeJsonObjectsList = newHomeJsonArrayList
        val mMyCollectionQuizQuestionRequest = newHomeNextFeatureModel.nextFeature?.let {
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
        toolBinding.tvTool.text = mainActivity.showToolHeadText()
    }

    private fun initViews() {
        AppUtils.profileCountStatus(profileWithBadgeBinding.profileNotification)
        customSharedPreferences = CustomSharedPreferences(AppController.getInstance())
        newHomesNextFeaturesModel =
            arguments?.getParcelable<NewHomesNextFeaturesModel>(AppConstants.NEWHOMENEXTQUESTIONMODEL)!!
        var isUserLoggedIn = customSharedPreferences.getUserLogin()
        if (!isUserLoggedIn) {
            profileWithBadgeBinding.tvFavorites.background = resources.getDrawable(R.drawable.disable_rectangle_new)
            profileWithBadgeBinding.profileNotification.visibility=View.GONE
        }
        toolBinding.ivBack.setOnClickListener {
            activity?.onBackPressed()
        }
        toolBinding.customRecyclerview.initView(requireContext())
        setToolbarBreadCrumb()
        customProgressDialog = CustomProgressDialog(requireContext())
        binding.rvBedrooms.apply {
            layoutManager = GridLayoutManager(requireContext(), 3)
            adapter = bedRoomAdapter
        }
        bedRoomAdapter.setBedroomList(getBedRoomList())
        profileWithBadgeBinding.tvFavorites.setOnClickListener {
            var isUserLoggedIn = customSharedPreferences.getUserLogin()
            if (isUserLoggedIn) {
             /*   val bundle = Bundle()
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

        stateId = customSharedPreferences.getStateID()
        userID = customSharedPreferences.getUserId()
        toolBinding.tvHowDoesIt.visibility = View.INVISIBLE

        toolBinding.tvTool.text = mainActivity.showToolHeadText()

        bottomLayoutBinding.tvDesigns.text =
            AppUtils.getDesignsTextBasedOnCount(AppConstants.HouseCount)

        bottomLayoutBinding.tvDesigns.setOnClickListener {
            if (isNextClickable) {
                if (this::newHomeNextFeatureResponseModel.isInitialized) {
                    val bundle = Bundle()
                    bundle.putBoolean(AppConstants.SELECT_FAVORITE, false)
                    bundle.putParcelable(
                        AppConstants.NEWHOMENEXTQUESTIONMODEL,
                        newHomeNextFeatureResponseModel.newHomesNextFeatures
                    )
                    loadFragment(MyCollectionPlacesFragment(), bundle)

                } else if (this::newHomesNextFeaturesModel.isInitialized) {
                    val bundle = Bundle()
                    bundle.putBoolean(AppConstants.SELECT_FAVORITE, false)
                    bundle.putParcelable(
                        AppConstants.NEWHOMENEXTQUESTIONMODEL,
                        newHomesNextFeaturesModel
                    )
                    loadFragment(MyCollectionPlacesFragment(), bundle)
                }
            }
        }

        bottomLayoutBinding.buttonNext.setOnClickListener {
            val fragment = AppConstants.storedFragments[FEATURE_NO_OF_BEDROOM]
            if (fragment != null){
                fragment.arguments?.let {
                    if(NEWHOMESHOUSECOUNT!=0) {
                        loadFragment(fragment, it)
                    }else{
                        AppUtils.showCustomCenterToast(
                            bottomLayoutBinding.buttonNext.context,
                            "No Houses Found"
                        )
                    }
                }
                return@setOnClickListener
            }
            if (isSelectedAnything) {
                if (isNextClickable) {
                    if (NEWHOMESHOUSECOUNT <= 7) {
                        loadMyCollectionPackagesFragment()
                    } else {

                        if (this::newHomeNextFeatureResponseModel.isInitialized) {
                            val bundle = Bundle()
                            bundle.putParcelable(
                                AppConstants.NEWHOMENEXTQUESTIONMODEL,
                                newHomeNextFeatureResponseModel.newHomesNextFeatures
                            )
                            loadFragment(nextFragment, bundle)
                        }
                    }
                }else{
                    AppUtils.showCustomCenterToast(
                        bottomLayoutBinding.buttonNext.context,
                        "No Houses Found"
                    )
                }
            } else {
                AppUtils.showCustomCenterToast(
                    bottomLayoutBinding.buttonNext.context,
                    "Please select bedrooms"
                )
            }
        }

        bottomLayoutBinding.buttonPrevious.setOnClickListener {
            isToolbarBack = false
            activity?.onBackPressed()
        }

    }

    fun loadFragment(fragment: Fragment, bundle: Bundle?) {
        // load fragment
        val transaction: FragmentTransaction = activity?.supportFragmentManager!!.beginTransaction()
        fragment.arguments = bundle
        if (fragment !is MyCollectionPlacesFragment) {
            AppConstants.storedFragments[FEATURE_NO_OF_BEDROOM] = fragment
        }
        if (fragment.isAdded) {
            return
        }
        transaction.replace(R.id.fl_content, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun onBackPressed(): Boolean {
        if (apiCallHappen && isToolbarBack){
            mainActivity.removeLastElementOfHashMap()
            AppConstants.removeCurrentElement(FEATURE_NO_OF_BEDROOM)
            AppConstants.storedFragments.remove(FEATURE_NO_OF_BEDROOM)
        }
        return false
    }

    private fun setToolbarBreadCrumb() {

        var arrayList: java.util.ArrayList<BreadcrumbModel> = java.util.ArrayList()

        if (AppConstants.breadCrumbMyCollection.keys.size > 0) {
            val myVeryOwnIterator: Iterator<*> = AppConstants.breadCrumbMyCollection.keys.iterator()
            while (myVeryOwnIterator.hasNext()) {
                val key = myVeryOwnIterator.next() as String
                var value = AppConstants.breadCrumbMyCollection[key]
                if (value != null) {
                    if (value.breadCrumbTitle != "") {
                        if (value.titleFragment == FEATURE_NO_OF_BEDROOM) {
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
        toolBinding.customRecyclerview.setData(arrayList, this)

    }


    override fun breadCrumb(breadCrumb: BreadcrumbModel) {
        Timber.e("Selected --${breadCrumb.breadCrumbTitle} ---${breadCrumb.isSelected}")
    }

    /*fun showNextItemInTheBreadCrumb(){
        var count = 0
        var found = false
        val iter: MutableIterator<Map.Entry<Int, String>> = AppConstants.toolheaderHashMap.keys.iterator()
        while (iter.hasNext()) {
            val entry = iter.next()
            if ("Sample".equals(entry.value, ignoreCase = true)) {
                iter.remove()
                found = true
            }
            if (found) {
                // set the new key using count...
            }
            count++
        }
    }*/

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


}
