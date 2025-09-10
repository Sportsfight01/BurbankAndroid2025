package com.dmss.burbankapp.ui.designs

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.dmss.burbankapp.databinding.FragmentHowWideIsLotBinding
import com.dmss.burbankapp.databinding.InclueToolDesignBinding
import com.dmss.burbankapp.databinding.ProfileWithBadgeBinding
import com.dmss.burbankapp.ui.designs.mycollectionlot.MyCollectionLotAdapter
import com.dmss.burbankapp.ui.main.MainActivity
import com.dmss.burbankapp.ui.view.BreadCrumbAdapter
import com.dmss.burbankapp.ui.view.CustomProgressDialog
import com.dmss.burbankapp.utils.AppConstants
import com.dmss.burbankapp.utils.AppUtils
import com.dmss.burbankapp.utils.customviews.SpacesItemDecoration
import common.AppController
import kotlin.math.roundToInt


class HowWideIsYourLotFragment : Fragment(), MainActivity.OnBackPressedListener,
    MyCollectionLotAdapter.LotItemClick, BreadCrumbAdapter.BreadcrumbItemClickListener {

    private val FEATURE = "Lot Width"

    lateinit var binding: FragmentHowWideIsLotBinding
    private var stateId: Int = -1
    private var userID: Int = -1
    lateinit var nextFragment: Fragment
    private lateinit var newHomesNextFeaturesModel: NewHomesNextFeaturesModel
    private lateinit var mainActivity: MainActivity
    private lateinit var customSharedPreferences: CustomSharedPreferences
    lateinit var myHomeQuizViewModel: NewHomeQuizViewModel
    var answersList: ArrayList<LotWidthItemModel> = ArrayList()
    lateinit var lotAdapter: MyCollectionLotAdapter
    private var apiCallHappen = false
    var spanCount: Int = 3
    private var customProgressDialog: CustomProgressDialog? = null
    var isNextClickable = true

    private lateinit var newHomeNextFeatureResponseModel: NewHomeNextFeatureResponseModel

    lateinit var bottomLayoutBinding: BottomLayoutMycollectionBinding
    lateinit var toolBinding: InclueToolDesignBinding
    lateinit var profileWithBadgeBinding: ProfileWithBadgeBinding

    private var NEWHOMESHOUSECOUNT = 0
    private var isToolbarBack = true
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHowWideIsLotBinding.inflate(inflater, container, false)
        bottomLayoutBinding = BottomLayoutMycollectionBinding.bind(binding.root)
        toolBinding = InclueToolDesignBinding.bind(binding.root)
        profileWithBadgeBinding = ProfileWithBadgeBinding.bind(binding.root)

        return binding.root

    }

    override fun onAttach(context: Context) {
        mainActivity = activity as MainActivity
        super.onAttach(context)


    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
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

        setupObserver()

    }

    private fun setupObserver() {
        myHomeQuizViewModel.getNewHomeNextQuestionLiveData()
            .observe(viewLifecycleOwner, Observer {
                when (it.status) {
                    Status.SUCCESS -> {
                        customProgressDialog?.dismissProgress()

                        var newHomeNextFeatureResponseModel: NewHomeNextFeatureResponseModel? =
                            it.data
                        if (newHomeNextFeatureResponseModel != null && newHomeNextFeatureResponseModel.status) {
                            this.newHomeNextFeatureResponseModel =
                                newHomeNextFeatureResponseModel
                            if (newHomeNextFeatureResponseModel.status) {
                                bottomLayoutBinding.tvDesigns.text =
                                    newHomeNextFeatureResponseModel.newHomesNextFeatures?.HouseCount?.let { it1 ->
                                        AppUtils.getDesignsTextBasedOnCount(
                                            it1
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
                                            newHomeNextFeatureResponseModel.newHomesNextFeatures?.nextFeature?:""
                                        )
                                    } else {
                                        MyCollectionPlacesFragment()
                                    }
                            }
                            if (this::bottomLayoutBinding.isInitialized) {
                                isNextClickable =
                                    newHomeNextFeatureResponseModel.newHomesNextFeatures?.HouseCount?.let { it1 ->
                                        AppUtils.disableAndEnableView(
                                            it1,
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
        profileWithBadgeBinding.profileNotification.text=AppConstants.TotalMyFavs.toString()
        toolBinding.ivBack.setOnClickListener {
            activity?.onBackPressed()
        }
        customSharedPreferences = CustomSharedPreferences(AppController.getInstance())
        toolBinding.customRecyclerview.initView(requireContext())
        setToolbarBreadCrumb()
        customProgressDialog = CustomProgressDialog(requireContext())
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

        answersList.clear()
        bottomLayoutBinding.tvDesigns.text =
            AppUtils.getDesignsTextBasedOnCount(AppConstants.HouseCount)

        newHomesNextFeaturesModel =
            arguments?.getParcelable<NewHomesNextFeaturesModel>(AppConstants.NEWHOMENEXTQUESTIONMODEL)!!

        newHomesNextFeaturesModel.nextFeatureAnswers.let {
            if (it != null) {
                for (lotWith in it) {
                    answersList.add(LotWidthItemModel(lotWith, false))
                }
            }
        }

        bottomLayoutBinding.buttonPrevious.setOnClickListener {
            isToolbarBack = false
            activity?.onBackPressed()
        }
        stateId = customSharedPreferences.getStateID()
        userID = customSharedPreferences.getUserId()
        toolBinding.tvHowDoesIt.visibility = View.INVISIBLE
        toolBinding.tvTool.text = mainActivity.showToolHeadText()

        val space = resources.getDimensionPixelSize(R.dimen._16dp)
        binding.lotRecyclerview.addItemDecoration(SpacesItemDecoration(spanCount, space, true))
        binding.lotRecyclerview.apply {

            //SORTING LIST BASED ON VALUE

            var sortedlist = answersList.sortedWith(compareBy { it.lotWidth.toFloat() })


            var sortedNewList: ArrayList<LotWidthItemModel> = ArrayList()
            for (lotWidthItemModel: LotWidthItemModel in sortedlist) {
                if (lotWidthItemModel.lotWidth.toDouble().roundToInt() != 0) {
                    sortedNewList.add(lotWidthItemModel)
                }
            }
            lotAdapter =
                MyCollectionLotAdapter(
                    requireContext(),
                    sortedNewList, this@HowWideIsYourLotFragment
                )
            adapter = lotAdapter

        }
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
            val fragment = AppConstants.storedFragments[FEATURE]
            if (fragment != null){
                fragment.arguments?.let {
                    loadFragment(fragment, it)
                }
                return@setOnClickListener
            }
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
            }
        }

    }

    private fun loadNextQuestionFeature(
        newHomeNextFeatureModel: NewHomesNextFeaturesModel,
        answer: String
    ) {
        val newHomeJsonArrayList: ArrayList<NewHomeJsonObject> = ArrayList()
        val newHomeJsonObject = newHomeNextFeatureModel.nextFeature?.let {
            newHomeNextFeatureModel.nextFeatureQuestion?.let { it1 ->
                NewHomeJsonObject(
                    feature = it,
                    question = it1,
                    answer = "",
                    minValue = answer,
                    maxValue = answer,
                    minLotWidth = answer


                )
            }
        }
        val lotPrice = AppUtils.roundRemainingString(answer)
        newHomeNextFeatureModel.nextFeature?.let {
            if (newHomeJsonObject != null) {
                var arrayList: ArrayList<NewHomeJsonObject> = ArrayList()
                arrayList.add(newHomeJsonObject)

                AppConstants.newHomeHashMap[it] = arrayList
                apiCallHappen = true
                AppUtils.removeNextBreadCrumbItem(FEATURE)
                AppConstants.toolheaderHashMap[FEATURE] = (" | $lotPrice M")
                AppConstants.breadCrumbMyCollection[FEATURE] =
                    BreadCrumbHashMapModel("$lotPrice M", FEATURE)

            }
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
        AppConstants.removeNextBredCrumpItem(FEATURE, newHomeJsonArrayList)
        AppConstants.newHomeJsonObjectsList = newHomeJsonArrayList
        val mMyCollectionQuizQuestionRequest = newHomeNextFeatureModel.nextFeature?.let {
            MyCollectionQuizQuestionRequest(
                stateId,
                includePackages = 0,
                sortByPrice = 0,
                userId = userID,
                newHomeJsonList = newHomeJsonArrayList
                /*minLotWidth = answer.toFloat()*/
            )
        }
        if (mMyCollectionQuizQuestionRequest != null) {
            myHomeQuizViewModel.fetchNewHomesCollectionsNextFeatureNext(
                mMyCollectionQuizQuestionRequest
            )
        }
        setToolbarBreadCrumb()
    }

    fun loadFragment(fragment: Fragment, bundle: Bundle) {
        val transaction: FragmentTransaction = activity?.supportFragmentManager!!.beginTransaction()
        fragment.arguments = bundle
        if (fragment !is MyCollectionPlacesFragment) {
            AppConstants.storedFragments[FEATURE] = fragment
        }
        if (fragment.isAdded) {
            return
        }
        transaction.replace(R.id.fl_content, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun selectedLotItem(lotWidth: LotWidthItemModel) {
        for (lotWidthModel in answersList) {
            lotWidthModel.isSelected = lotWidth.lotWidth == lotWidthModel.lotWidth
            lotAdapter.notifyDataSetChanged()
            loadNextQuestionFeature(newHomesNextFeaturesModel, lotWidth.lotWidth)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isToolbarBack = true
    }

    override fun onResume() {
        super.onResume()
        isToolbarBack = true
        if (AppConstants.newHomeJsonObjectsList.any { it.feature == FEATURE }) {
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
    }

    override fun onBackPressed(): Boolean {
        if (apiCallHappen && isToolbarBack) {
            mainActivity.removeLastElementOfHashMap()
            AppConstants.removeCurrentElement(FEATURE)
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
                        if (value.titleFragment == FEATURE) {
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
