package com.dmss.burbankapp.ui.designs

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.dmss.burbankapp.databinding.FragmentAlfrescoMyCollectionBinding
import com.dmss.burbankapp.databinding.InclueToolDesignBinding
import com.dmss.burbankapp.databinding.ProfileWithBadgeBinding
import com.dmss.burbankapp.ui.main.MainActivity
import com.dmss.burbankapp.ui.view.BreadCrumbAdapter
import com.dmss.burbankapp.ui.view.CustomProgressDialog
import com.dmss.burbankapp.utils.AppConstants
import com.dmss.burbankapp.utils.AppUtils
import common.AppController

class AllfrescoMyCollectionFragment : Fragment(), MainActivity.OnBackPressedListener,
    BreadCrumbAdapter.BreadcrumbItemClickListener {
    private val FEATURE = "Grand Alfresco"
    lateinit var binding: FragmentAlfrescoMyCollectionBinding
    private var stateId: Int = -1
    private var userID: Int = -1
    var isSelectedAnything: Boolean = false
    lateinit var nextFragment: Fragment
    private lateinit var newHomesNextFeaturesModel: NewHomesNextFeaturesModel
    private lateinit var mainActivity: MainActivity
    private lateinit var customSharedPreferences: CustomSharedPreferences
    lateinit var myHomeQuizViewModel: NewHomeQuizViewModel
    private var apiCallHappen = false
    private var customProgressDialog: CustomProgressDialog? = null
    var isNextClickable = true

    private lateinit var newHomeNextFeatureResponseModel: NewHomeNextFeatureResponseModel

    lateinit var bottomLayoutBinding: BottomLayoutMycollectionBinding
    lateinit var toolBinding: InclueToolDesignBinding
    private var NEWHOMESHOUSECOUNT = 0
    lateinit var profileWithBadgeBinding: ProfileWithBadgeBinding

    var isToolbarBack = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAlfrescoMyCollectionBinding.inflate(inflater, container, false)
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
                                    newHomeNextFeatureResponseModel.newHomesNextFeatures?.let { it1 ->
                                        it1.HouseCount.let { it2 ->
                                            AppUtils.getDesignsTextBasedOnCount(
                                                it2
                                            )
                                        }
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
                                                bottomLayoutBinding.next,
                                                bottomLayoutBinding.buttonNext
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
        toolBinding.ivBack.setOnClickListener {
            activity?.onBackPressed()
        }
        customSharedPreferences = CustomSharedPreferences(AppController.getInstance())
        toolBinding.customRecyclerview.initView(requireContext())
        setToolbarBreadCrumb()
        customProgressDialog = CustomProgressDialog(requireContext())
        var isUserLoggedIn = customSharedPreferences.getUserLogin()
        if (!isUserLoggedIn) {
            profileWithBadgeBinding.tvFavorites.background = resources.getDrawable(R.drawable.disable_rectangle_new)
            profileWithBadgeBinding.profileNotification.visibility=View.GONE
        }
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

        newHomesNextFeaturesModel =
            arguments?.getParcelable<NewHomesNextFeaturesModel>(AppConstants.NEWHOMENEXTQUESTIONMODEL)!!

        bottomLayoutBinding.tvDesigns.text =
            AppUtils.getDesignsTextBasedOnCount(AppConstants.HouseCount)
        stateId = customSharedPreferences.getStateID()
        userID = customSharedPreferences.getUserId()
        toolBinding.tvHowDoesIt.visibility = View.INVISIBLE
        toolBinding.tvTool.text = mainActivity.showToolHeadText()

        val isIDoNotWantThisButtonAvailable = newHomesNextFeaturesModel.
        nextFeatureAnswers?.any { it == AppConstants.I_DONT_WANT_THIS }
        if (isIDoNotWantThisButtonAvailable == true){
            binding.idontWantThis.visibility = View.VISIBLE
        }


        bottomLayoutBinding.tvDesigns.setOnClickListener {
            if (isNextClickable) {
                loadMyCollectionPackagesFragment()
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
                    "Please select one option"
                )
            }

        }

        binding.imusthave.setOnClickListener {
            isSelectedAnything = true
            showView(binding.imusthave)
            AppUtils.removeNextBreadCrumbItem(FEATURE)
            AppConstants.breadCrumbMyCollection[FEATURE] =
                BreadCrumbHashMapModel("Alfresco", FEATURE)

            loadNextQuestionFeature(newHomesNextFeaturesModel, AppConstants.I_MUST_HAVE_THIS)
            toolBinding.tvTool.visibility = View.GONE
        }

        binding.idontmind.setOnClickListener {
            isSelectedAnything = true
            showView(binding.idontmind)
            AppUtils.removeNextBreadCrumbItem(FEATURE)
            AppConstants.breadCrumbMyCollection[FEATURE] =
                BreadCrumbHashMapModel("", FEATURE)
            loadNextQuestionFeature(newHomesNextFeaturesModel, AppConstants.I_DONT_MIND)
            toolBinding.tvTool.visibility = View.GONE
        }

        binding.idontWantThis.setOnClickListener {
            isSelectedAnything = true
            showView(binding.idontWantThis)
            AppUtils.removeNextBreadCrumbItem(FEATURE)
            AppConstants.toolheaderHashMap[FEATURE] = ""
            AppConstants.breadCrumbMyCollection[FEATURE] =
                BreadCrumbHashMapModel("", FEATURE)
            loadNextQuestionFeature(newHomesNextFeaturesModel, AppConstants.I_DONT_WANT_THIS)
            toolBinding.tvTool.visibility = View.GONE
        }


        bottomLayoutBinding.buttonPrevious.setOnClickListener {
            isToolbarBack = false
            activity?.onBackPressed()
        }
    }

    private fun loadNextQuestionFeature(
        newHomeNextFeatureModel: NewHomesNextFeaturesModel,
        answer: String
    ) {
        var newHomeJsonArrayList: ArrayList<NewHomeJsonObject> = ArrayList()
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

                apiCallHappen = true
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
                /* minLotWidth = 0f*/
            )
        }
        if (mMyCollectionQuizQuestionRequest != null) {
            myHomeQuizViewModel.fetchNewHomesCollectionsNextFeature(mMyCollectionQuizQuestionRequest)
        }
        toolBinding.tvTool.text = mainActivity.showToolHeadText()
        /* toolBinding.ivBack.setOnClickListener{
             onBackPressed()
         }*/
        setToolbarBreadCrumb()
    }

    private fun showView(view: TextView) {
        binding.imusthave.background =
            ContextCompat.getDrawable(requireContext(), R.drawable.rectangle_grey_line)
        binding.idontmind.background =
            ContextCompat.getDrawable(requireContext(), R.drawable.rectangle_grey_line)
        binding.idontWantThis.background =
            ContextCompat.getDrawable(requireContext(), R.drawable.rectangle_grey_line)

        binding.imusthave.setTextColor(ContextCompat.getColor(requireContext(), R.color.grey_text_font_3_1))
        binding.idontmind.setTextColor(ContextCompat.getColor(requireContext(), R.color.grey_text_font_3_1))
        binding.idontWantThis.setTextColor(ContextCompat.getColor(requireContext(), R.color.grey_text_font_3_1))
        view.background = ContextCompat.getDrawable(requireContext(), R.drawable.rectangle_orange_bg)
        view.setTextColor(ContextCompat.getColor(requireContext(), R.color.white_3_1))

    }


    fun loadFragment(fragment: Fragment, bundle: Bundle) {
        // load fragment
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isToolbarBack = true
        isSelectedAnything = false
    }

    override fun onResume() {
        super.onResume()
        isToolbarBack = true
        if (AppConstants.newHomeJsonObjectsList.any { it.feature == FEATURE }) {
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
        val data = AppConstants.newHomeJsonObjectsList.find { it.feature == FEATURE }
        data?.let {
            when(it.answer){
                AppConstants.I_DONT_WANT_THIS -> showView(binding.idontWantThis)
                AppConstants.I_DONT_MIND -> showView(binding.idontmind)
                AppConstants.I_MUST_HAVE_THIS -> showView(binding.imusthave)
            }
        }
    }

    override fun onBackPressed(): Boolean {
        if (apiCallHappen && isToolbarBack){
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

        }
    }

}
