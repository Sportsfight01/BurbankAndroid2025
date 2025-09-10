package com.dmss.burbankappold.dashboard.ui.home

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.dmss.burbankappold.BaseFragment
import com.dmss.burbankappold.R
import com.dmss.burbankappold.dashboard.DashboardNewActivity
import com.dmss.burbankappold.dashboard.PrefsHelper
import com.dmss.burbankappold.dashboard.ui.OffsetItemDecoration
import com.dmss.burbankappold.dashboard.ui.home.adapters.JobNumbersAdapter
import com.dmss.burbankappold.dashboard.ui.home.adapters.ProgressHomeAdapter
import com.dmss.burbankappold.dashboard.ui.home.adapters.ProgressHomeNewAdapter
import com.dmss.burbankappold.dashboard.ui.sideMenu.MyNotificationActivity
import com.dmss.burbankappold.dashboard.ui.sideMenu.contactUs.list
import com.dmss.burbankappold.databinding.FragmentHomeBinding
import com.dmss.burbankappold.databinding.LayoutJobNumbersBinding
import com.dmss.burbankappold.network.ApiRepository
import com.dmss.burbankappold.utils.*
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import common.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import models.LoginUserData
import models.MyDocOrPhotosDataSetQldOrSa
import models.MyPlaceCredentials
import models.photos.DocumnetsData
import models.photos.PhotosData
import models.photos.PhotosDataItem
import models.profile.MyPlaceDetail
import models.profile.UserJobProfile
import models.progress.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import java.math.RoundingMode

class HomeFragment : BaseFragment() {

    private lateinit var binding : FragmentHomeBinding
//    private lateinit var binding : FragmentHomeBinding

    private lateinit var linearLayoutManager: LinearLayoutManager
    var controller: AppController? = null
    var pd: TransparentProgressDialog? = null
    private var progressAdapter: ProgressHomeAdapter? = null
    private var progressTasksNewAdapter: ProgressHomeNewAdapter? = null
    private lateinit var constructionContractData:ConstructionContract
    private lateinit var preconstructionContractData:PreconstructionContract
    var adminCompletedTask = 0
    var baseStageCompletedTask: Int = 0
    var frameStageCompletedTask: Int = 0
    var lockUpCompletedTask: Int = 0
    var miscellaneousCompletedTask = 0
    var finishingTask: Int = 0
    var fixedOutCompletedTask = 0
    var handOverCompletedTask = 0
    var lockpercent = 0
    var adminpercent: Int = 0
    var basepercent: Int = 0
    var framepercent: Int = 0
    var fixedOutpercent = 0
    var completionpercent = 0
    var miscellanouspercent = 0
    var avgPercent: Int = 0
    private var mProgressList = arrayListOf<ProgressData>()
    private var adminData = listOf<UserJobProgressItem>()
    private var frameStageData = listOf<UserJobProgressItem>()
    private var lockUpStageData = listOf<UserJobProgressItem>()
    private var fixingStageData = listOf<UserJobProgressItem>()
    private var finishingStageData = listOf<UserJobProgressItem>()
    private var baseStageData = listOf<UserJobProgressItem>()
    var myPlaceDataBase: MyPlaceDataBase? = null
    private lateinit var viewModel: SharedViewModel
    var listConstructionStageCondition =ArrayList<TaskCompletedInfoData>()

    private fun clearData(){
        controller!!.analytics.settingsLogoutButtonTouchEvent()
//        controller!!.isUserLoggedIn = false
        controller!!.loggedInFromSocial = false
        controller!!.my_Place_Details = null
//        controller!!.profilePicUrl = ""
//        controller!!.setProfileInfo("")
        controller!!.selectedJobPosition = 0
        AppConstants.filterList= emptyList()
        AppConstants.NoRecentPhotos=""
        AppConstants.PhotosSubheader=""
        AppConstants.AppCookieContactUs = ""
        AppConstants.savedphotosList.clear()
        AppConstants.financeItemStatus=false
        AppConstants.savedphotosList=mutableListOf<List<PhotosDataItem>>()
        AppConstants.listConstructionStageCondition=ArrayList<TaskCompletedInfoData>()
        AppConstants.photosList = mutableListOf<List<PhotosDataItem>>()
        AppConstants.documentsList =  listOf<PhotosDataItem>()
        PrefsHelper.clearPrefs()

    }
    private fun callAPI(){
        if (PrefsHelper.isProfileApiCalled) {
            if (mProgressList.isEmpty()) {
                pd = Utils.getProgress(activity)
                callForNewApi()
            } else {
                updateProfileProgress(mProgressList[0].progress)
                progressAdapter?.setProgressList(mProgressList)
            }
        }
        else{
            requestProfileDetailsApi()
        }
    }
    private fun showJobNumbersDialog(contentString: String) {
        val layoutRecentSearchBinding: LayoutJobNumbersBinding =
            LayoutJobNumbersBinding.inflate(layoutInflater)
        val shareAlertBuilder: AlertDialog.Builder =
            AlertDialog.Builder(context)
        shareAlertBuilder.setCancelable(false)
        shareAlertBuilder.setView(layoutRecentSearchBinding.root)
        val alertDialog: AlertDialog = shareAlertBuilder.create()
        var jobList:ArrayList<String> = ArrayList<String>()
        val linearLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        AppController.controller.userProfile.userDetails[0].myPlaceJobDetailses.forEach {
            jobList.add(it.jobNo)
        }
        viewModel.financeItem(false)
        var SELECTEDJOBNUMBER= AppController.getPreference(context, AppController.SELECTEDJOBNUMBER)
        var loginJobnumber= AppController.getPreference(context, AppController.JOBNUMBER)
        var PREVIOUSSELECTEDJOBNUMBER= AppController.getPreference(context, AppController.PREVIOUSSELECTEDJOBNUMBER)

        if( SELECTEDJOBNUMBER.trim()=="defaultValue"){
            if(loginJobnumber!=null && loginJobnumber!="null"&& PREVIOUSSELECTEDJOBNUMBER==AppController.defaultValue ){
                AppController.setPreference(context,AppController.SELECTEDJOBNUMBER,loginJobnumber)
                if(loginJobnumber!="") {
                    AppController.setPreference(
                        context,
                        AppController.PREVIOUSSELECTEDJOBNUMBER,
                        loginJobnumber
                    )
                }

            }
            /*else{
                layoutRecentSearchBinding.dialogTitle.text="ASSOCIATED JOB NUMBERS"
            }*/
        }
        layoutRecentSearchBinding.rvjobnubers.layoutManager=linearLayoutManager
        layoutRecentSearchBinding.rvjobnubers.adapter=JobNumbersAdapter(jobList){
            clearData()
            AppController.setPreference(context,AppController.SELECTEDJOBNUMBER,it)
            AppController.setPreference(context,AppController.PREVIOUSSELECTEDJOBNUMBER,it)
            AppController.controller.jobNumber=it
            AppConstants.validateVersionCode(requireActivity()) {
            if (!it) {
                callAPI()
            }
                }
            alertDialog.dismiss()

        }
        layoutRecentSearchBinding.ivClose.setOnClickListener {
            var PREVIOUSSELECTEDJOBNUMBER= AppController.getPreference(context, AppController.PREVIOUSSELECTEDJOBNUMBER)
            if(PREVIOUSSELECTEDJOBNUMBER!=AppController.defaultValue){
                AppController.setPreference(context,AppController.SELECTEDJOBNUMBER,PREVIOUSSELECTEDJOBNUMBER)
                AppController.controller.jobNumber=PREVIOUSSELECTEDJOBNUMBER
                callAPI()
                alertDialog.dismiss()
            }else{
                Toast.makeText(context,"Please select job number",Toast.LENGTH_SHORT).show()
            }

        }

        /* layoutRecentSearchBinding.apply {
             this.linearLayoutManager = linearLayoutManager
             this.adapter = progressAdapter
             this.addItemDecoration(OffsetItemDecoration(R.dimen._20sdp))
         }*/
        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        alertDialog.show()

    }

    private val jsonBody: JsonObject
        get() {
            val jsonObject = JsonObject()
            try {
                jsonObject.addProperty("Email", controller!!.userProfile.userDetails[0].email)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            return jsonObject
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        binding.profileHeader.ivPhoto.loadUrlUsingGlide(PrefsHelper.profileUrl)
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myPlaceDataBase = MyPlaceDataBase(requireContext())
        controller = requireActivity().applicationContext as AppController
        controller!!.analytics.setScreen(activity, "MyProgress_Screen")


        listConstructionStageCondition.clear()
        progressTasksNewAdapter = ProgressHomeNewAdapter { data ->
            try {
//                var userJobProgressItemlist = ArrayList<UserJobProgressItem>()

                var prgressData = ProgressData(
                    data.name,
                    data.date,
                    data.progress,
                    "",
                    data.tasksCompleted,
                    data.totalTasks,
                    ProgressStage.AdminStage,
                    data.list
                )

                /* it.list.forEach {
                     if(it.dateactual==null || it.dateactual=="null" || it.dateactual=="NULL")
                     it.dateactual=""
                 }*/
                findNavController().navigate(
                    R.id.action_nav_home_to_progressDetailsFragment,
                    bundleOf(BundleKey.PROGRESS_DATA to prgressData)
                )
            } catch (e: Exception) {
            }
        }
    }

    private fun initView() {


        binding.profileHeader.tvHeading.changeTextColor(context, R.color.new_gray_color)
        binding.profileHeader.tvSubHeading.changeTextColor(context, R.color.new_gray_color)
        binding.profileHeader.ivPhoto.setImageDrawable(ContextCompat.getDrawable( activity!!,R.drawable.burbank_black))
//        binding.profileHeader.ivPhoto.borderColor=  ContextCompat.getColor(
//            activity!!,
//            R.color.grey_text_font_3_1
//        )
        viewModel = activity?.run {
            ViewModelProviders.of(this)[SharedViewModel::class.java]
        } ?: throw Exception("Invalid Activity")
        AppController.controller.jobNumber=AppController.getPreference(context,AppController.JOBNUMBER)
        binding.swiperefresh.setOnRefreshListener {
            Utils.hideSwipeRefresh(binding.swiperefresh)
            if(mProgressList.isEmpty() )
                pd = Utils.getProgress(activity)
            callForNewApi()


        }
        viewModel.financeItem(AppConstants.financeItemStatus)

    }

    private fun requestProfileDetailsApi(){
        pd = Utils.getProgress(requireActivity())
        PrefsHelper.isProfileApiCalled = true
        callForNewApi()
        /* ApiRepository.requestUserLoginDetails(jsonBody) { isSuccess, response ->
             if (isSuccess) {
                 parseAPIResponse(response) {
                     PrefsHelper.isProfileApiCalled = true
                     callForNewApi()
                 }
             }
         }*/
    }

    private fun parseAPIResponse(result: String?, callback: () -> Unit) {
        try {
            val jsonObject = JSONObject(result)
            if (jsonObject.getBoolean(Common.Status_Key)) {
                val job = jsonObject.getJSONObject(Common.Result_Key)
                if (job.getBoolean(Common.Sucess_Key)) {
                    controller?.setProfileInfo(job.toString())
                    val gson = GsonBuilder().create()
                    val profile: UserJobProfile =
                        gson.fromJson(job.toString(), UserJobProfile::class.java)
                    AppController.controller.setUserJobProfile(profile)
                    AppController.controller.setUserJobDetail(profile.UserDetails)
                    AppController.controller.myPlaceDetail = profile.UserDetails[0].MyPlaceDetails
//                    val myPlaceDetails = profile.UserDetails[0].MyPlaceDetails[0]
                    var myPlaceDetails: MyPlaceDetail? = null

                    if(AppController.controller.jobNumber==null || AppController.controller.jobNumber=="null" || AppController.controller.jobNumber==""){
                        AppController.controller.jobNumber=profile.UserDetails[0].MyPlaceDetails[0].JobNo
                    }
                    for (item in profile.UserDetails[0].MyPlaceDetails){
                        if(item.JobNo==AppController.controller.jobNumber)
                            myPlaceDetails=item
                    }
                    val myPlaceCredentials = MyPlaceCredentials(
                        myPlaceDetails!!.Region,
                        myPlaceDetails!!.JobNo,
                        myPlaceDetails!!.UserName,
                        myPlaceDetails!!.Password
                    )
                    AppController.controller.my_Place_Details = myPlaceCredentials
                    callback.invoke()
                } else {
                    Utils.showToast(
                        requireActivity(),
                        jsonObject.getString(Common.Message),
                        Common.errorCase
                    )
                }
            } else {
                Utils.showToast(
                    requireActivity(),
                    jsonObject.getString(Common.Message),
                    Common.errorCase
                )
            }
        } catch (jsonException: JSONException) {
            jsonException.printStackTrace()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        AppConstants.validateVersionCode(requireActivity()) {
//            if (!it) {
                initView()
                linearLayoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                var jobNubersList =
                    AppController.controller.userProfile.userDetails[0].myPlaceJobDetailses
                var SELECTEDJOBNUMBER =
                    AppController.getPreference(activity, AppController.SELECTEDJOBNUMBER)


                binding.rvProgress.apply {
                    this.layoutManager = linearLayoutManager
                    this.adapter = progressTasksNewAdapter
                    this.addItemDecoration(OffsetItemDecoration(R.dimen._20sdp))
                }
                val snapHelper: LinearSnapHelper = SnapHelperOneByOne()
                snapHelper.attachToRecyclerView(binding.rvProgress)
                binding.rvProgress.addOnScrollListener(
                    MiddleItemFinder(
                        requireContext(),
                        linearLayoutManager,
                        object : MiddleItemFinder.MiddleItemCallback {
                            override fun scrollFinished(middleElement: Int) {
                                if (middleElement != 0) {
                                    if (listConstructionStageCondition[middleElement] != null && listConstructionStageCondition[middleElement].percentage != null) {
                                        updateStage(
                                            listConstructionStageCondition[middleElement].percentage!!,
                                            middleElement
                                        )
                                    }
                                } else {
                                    binding.tvStage.text = getString(R.string.your_overall_progress)
                                }
                            }
                        },
                        RecyclerView.SCROLL_STATE_IDLE
                    )
                )
                if (jobNubersList.size > 1 && SELECTEDJOBNUMBER.equals("defaultValue")) {
                    showJobNumbersDialog("")

                } else {

                    if (PrefsHelper.isProfileApiCalled) {
                        if (AppConstants.listConstructionStageCondition != null) {
//
//
//                generatedAdapter()
                            listConstructionStageCondition =
                                AppConstants.listConstructionStageCondition!!
                            if (listConstructionStageCondition.size > 0) {
                                progressTasksNewAdapter?.setProgressList(
                                    listConstructionStageCondition
                                )
                            }
                            updateSubHeaders()

                        } else {
                            AppConstants.validateVersionCode(requireActivity()) {
                                if (!it) {
                                    pd = Utils.getProgress(activity)
                                    callForNewApi()
                                }
                            }
                        }
                    } else {
                        AppConstants.validateVersionCode(requireActivity()) {
                        if (!it) {
                            requestProfileDetailsApi()
                        }
                    }
                    }
                }
//            }
//        }
    }

    private fun updateStage(progress: Int, position: Int) {
        binding.tvStage.text = when (progress) {
            100 -> {
                "Completed Stage"
            }
            in 2..99 -> {
                "Your Current Stage"
            }
            else -> {
                if (listConstructionStageCondition[position - 1].percentage == 100) {
                    "Your Current Stage"
                } else "Pending Previous Stage"
            }
        }
    }
    private fun filterPhotosList(photosDataItem: List<PhotosDataItem>){
//        var filterList = photosDataItem.filter { it.extension?.lowercase()?.trim() ==".jpg" || it.extension?.lowercase()?.trim() ==".png" }
        var filterList = photosDataItem.filter { it.url !="" }

        var groupedList = filterList.groupBy { title -> title.title.split(" ")[0] }.map { it.value }
        AppConstants.photosList = groupedList as MutableList<List<PhotosDataItem>>
        AppConstants.savedphotosList = groupedList as MutableList<List<PhotosDataItem>>
    }
    private fun callForNewApi() {
        updateSubHeaders()
        if(Utils.isNetworkAvailable(activity) ) {


            var JOBNUMBER = AppController.getPreference(context, AppController.SELECTEDJOBNUMBER)
            if (JOBNUMBER == AppController.defaultValue) {
                JOBNUMBER =
                    AppController.controller.userProfile.userDetails[0].myPlaceJobDetailses[0].jobNo
            }
            var myPlaceJobDetailses =
                AppController.controller.userProfile.userDetails[0].myPlaceJobDetailses
            myPlaceJobDetailses.forEach {
                if (it.jobNo == JOBNUMBER) {
                    val myPlaceCredentials = MyPlaceCredentials(
                        it.region,
                        it.jobNo,
                        it.userName,
                        it.password
                    )
                    AppController.controller.my_Place_Details = myPlaceCredentials
                }
            }
            val jsonParser = JsonParser()
            val userLoginData = Gson().toJson(UserV2LoginData("santhosh.rechintala","Sa9030343907@#",false))
            val loginJsonObject = JSONObject(userLoginData)
            val request = loginJsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull());

            var Password= AppController.controller?.my_Place_Details?.password
            var JobNo= AppController.controller?.my_Place_Details?.jobNumber
            var UserName= AppController.controller?.my_Place_Details?.username
            /*val jsonString="{contractNumber:$JobNo,userName:$UserName,password:$Password}"
            val jsonObjectObj = jsonParser.parse(jsonString).asJsonObject*/
            val jsonObjectObj = Utils.getLoginRequest(JobNo,UserName,Password)



            if (Utils.isNetworkAvailable(activity)) {
                ApiRepository.requestContactUsLogin(jsonObjectObj) { isSuccess, contactData, loginUserData ->
                    if (isSuccess) {
                        var loginUserDataModel= Gson().fromJson(loginUserData,LoginUserData::class.java)
                        ApiRepository.requestClickhomeViewLogin(request) { isSuccess, cookie, loginUserData ->
//                    pd?.dismiss()
                            if (isSuccess && loginUserDataModel.masterContractId!=null) {
                                if (Utils.isNetworkAvailable(context)) {
                                    val jsonString =
                                        AppConstants.jsonPreConstructionContract + AppConstants.jsonConstructionContract
                                    val jsonPreConstructionContract =
                                        jsonParser.parse(jsonString).asJsonObject
                                    ApiRepository.requestconstructionContractsJobSteps(
                                        loginUserDataModel.masterContractId!!,
                                        cookie!!,
                                        jsonPreConstructionContract
                                    ) { isSuccess, progressData ->
                                        var contractJson = JSONObject(progressData)

                                        if(contractJson.has("constructionContract")) {
                                            var constructionContractJson =
                                                contractJson.getJSONObject("constructionContract")

                                            constructionContractData = Gson().fromJson(
                                                constructionContractJson.toString(),
                                                ConstructionContract::class.java
                                            )
                                            AppConstants.constructionContractData =
                                                constructionContractData
                                        }
                                        else{
//                                             Toast.makeText(activity,"No data available",Toast.LENGTH_SHORT).show()
                                        }
                                        if(contractJson.has("preconstructionContract")) {
                                            var preconstructionContractJson =
                                                contractJson.getJSONObject("preconstructionContract")

                                            preconstructionContractData = Gson().fromJson(
                                                preconstructionContractJson.toString(),
                                                PreconstructionContract::class.java
                                            )

                                            AppConstants.preconstructionContractData =
                                                preconstructionContractData
                                        }
                                        else{
//                                             Toast.makeText(activity,"No data available",Toast.LENGTH_SHORT).show()
                                        }
//                               GlobalScope.launch { // launch new coroutine in background and continue
//                                   delay(1000L) // non-blocking delay for 1 second (default time unit is ms)
                                        generatedAdapter()
//                               }


                                    }
                                }
                            }else{
                                pd?.dismiss()
                                Toast.makeText(
                                    context,
                                    "Something went wrong.Please try again.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        /* ApiRepository.requestProgressSteps { isSuccess, progressData ->
                pd?.dismiss()
                if (isSuccess) {
                    processProgressData(progressData)
                }
            }

            requestPhotosApiCall()*/
                    }else{
                        pd?.dismiss()
                        Toast.makeText(
                            context,
                            "Something went wrong.Please try again.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }else{

//                Log.d("Network", "No internet :(");
                Toast.makeText(
                    context,
                    "Internet not available, Please connect to internet.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }else{
            pd?.dismiss()

        }
    }
    @SuppressLint("SuspiciousIndentation")
    private fun generatedAdapter(){

        var listPreOfCompletedTasks=ArrayList<TaskCompletedData>()
        var listPreOfPreConstructionTasks =ArrayList<TaskCompletedData>()
        var listConstructionTasks =ArrayList<TaskCompletedData>()


        var youruserJobProgressItem = ArrayList<UserJobProgressItem>()
        var userJobProgressNewItemList= ArrayList<UserJobProgressNewItem>()
        var region= AppController.controller.userProfile.userDetails[0].myPlaceJobDetailses[0].region
        var preconstructioncompletedTasks=0.0
        var listOfTaskData = ArrayList<TasksData>()
        listConstructionStageCondition.add(TaskCompletedInfoData( "Your New Home" ,0,0.0,0,listOfTaskData,youruserJobProgressItem))
        var preuserJobProgressItem = ArrayList<UserJobProgressItem>()
        var financeItemStatus=false
        var completedDate = ""
        var completedDateHiddenFalse = ""

        if(::preconstructionContractData.isInitialized) {
            var preConstructTototalSize =0
            var percetageVal =0
            var precostructionHiddenFalse=preconstructionContractData.tasks.list.filter { !it.hidden && !it.taskName.startsWith("ETS",false)}
            println("precostructionHiddenFalse:: "+precostructionHiddenFalse.size)
            preconstructionContractData.tasks.list.forEachIndexed { index, it ->


                var status = "Pending"

                if (it.completedDate != null && it.completedDate != "") {
//                    preconstructioncompletedTasks += 1
                    completedDate = it.completedDate
                    status = "Completed"


                }

                if (region == "NSW" || region == "ACT") {
                    if (it.taskName == "Selection appointments complete") {
                        if (it.completedDate != null && it.completedDate != "") {
                            var userJobProgressNewItem = UserJobProgressNewItem(
                                dateactual = it.completedDate,
                                name = "Edge Appointment",
                                status = status,
                                stageName = it.stage.stageName,
                                taskid = it.taskId!!
                            )
                            userJobProgressNewItemList.add(userJobProgressNewItem)
                        }
                    }
                    if (it.taskName == "Contract Presented") {
                        if (it.completedDate != null && it.completedDate != "") {
                            var userJobProgressNewItem = UserJobProgressNewItem(
                                dateactual = it.completedDate,
                                name = "Sign Building Contract",
                                status = status,
                                stageName = it.stage.stageName,
                                taskid = it.taskId!!
                            )
                            userJobProgressNewItemList.add(userJobProgressNewItem)
                        }
                    }
                }
                if (region == "VIC" || region == "QLD" || region == "SA") {
                    println("region taskName:: "+region+"  taskName:: "+it.taskName )
                    if (it.taskName == "Colour Selection") {
                        if (it.completedDate != null && it.completedDate != "") {
                            var userJobProgressNewItem = UserJobProgressNewItem(
                                dateactual = it.completedDate,
                                name = "Edge Appointment",
                                status = status,
                                stageName = it.stage.stageName,
                                taskid = it.taskId!!
                            )
                            userJobProgressNewItemList.add(userJobProgressNewItem)
                        }
                    }
                    if (it.taskName == "Sign Building Contract") {
                        if (it.completedDate != null && it.completedDate != "") {
                            var userJobProgressNewItem = UserJobProgressNewItem(
                                dateactual = it.completedDate,
                                name = it.taskName,
                                status = status,
                                stageName = it.stage.stageName,
                                taskid = it.taskId!!
                            )

                            userJobProgressNewItemList.add(userJobProgressNewItem)


                        }
                    }
                }

                if (it.taskName == "Sign Building Contract" || it.taskName == "Contract Signed") {
                    financeItemStatus = true
                    var isFinananceVisible = false
                    if (it.completedDate != null && it.completedDate != "") {
//                            viewModel.financeItem(true)
                        isFinananceVisible = true
                    } else {
//                            viewModel.financeItem(false)
                        isFinananceVisible = false

                    }
                    viewModel.financeItem(isFinananceVisible)

                    AppConstants.financeItemStatus = isFinananceVisible

                }
             // Tasks Starting with ETS are burbank local tasks stop it and hidden tag false data is considering
                if (!it.hidden && !it.taskName.startsWith("ETS",false)) {
                    if (it.completedDate != null && it.completedDate != "") {
                        preconstructioncompletedTasks += 1
                        completedDate = it.completedDate
                        status = "Completed"


                    }
                    println("preconstructionHidden:: " + " TASK:: " + it.taskName + "  :: " + it.completedDate)
                    listPreOfPreConstructionTasks.add(
                        TaskCompletedData(
                            it.taskId,
                            it.taskName,
                            it.completedDate,
                            it.stage.stageName,
                            it.hidden
                        )
                    )
                    listOfTaskData.add(TasksData(completedDate, it.taskId, it.taskName))

                    preuserJobProgressItem.add(
                        UserJobProgressItem(
                            comment = "",
                            dateactual = it.completedDate,
                            datedescription = "",
                            forclient = true,
                            name = it.taskName,
                            phasecode = "",
                            resourcename = "",
                            sequence = 0,
                            stageId = 0,
                            stageName = it.stage.stageName,
                            status = status,
                            taskid = it.taskId!!.toInt()
                        )
                    )



                    processProgressDataNew(preuserJobProgressItem, 1)

//                    var preConstructTototalSize = preconstructionContractData.tasks.list.size
                    preConstructTototalSize = precostructionHiddenFalse.size

                    var actpercentage =
                        ((preconstructioncompletedTasks * 100) / preConstructTototalSize).toDouble()
//        val percentage = actpercentage.toBigDecimal().setScale(1, RoundingMode.UP).toInt()
                    percetageVal = roundValCal(actpercentage)


                }
            }
            listConstructionStageCondition.add(
                TaskCompletedInfoData(
                    AppConstants.Admin_Stage,
                    preConstructTototalSize,
                    preconstructioncompletedTasks,
                    percetageVal,
                    listOfTaskData,
                    preuserJobProgressItem
                )
            )
        }
        if( ::constructionContractData.isInitialized){


            constructionContractData.tasks.list.forEach {

//            if(it.completedDate!=null && it.completedDate!=""){
//                status ="Completed"
//            }
                if (it.taskName == "PC INSPECTION") {
                    if (it.completedDate != null && it.completedDate != "") {
                        var userJobProgressNewItem = UserJobProgressNewItem(
                            dateactual = it.completedDate,
                            name = it.taskName,
                            status = "Completed",
                            stageName = it.stage.stageName!!,
                            taskid = it.taskId!!
                        )

                        userJobProgressNewItemList.add(userJobProgressNewItem)


                    }
                }
                // Tasks Starting with ETS are burbank local tasks stop it and hidden tag false data is considering
                if(!it.hidden && !it.taskName.startsWith("ETS",false)) {
                    var stageName = ""
                    stageName =
                        if (it.stage.stageName == AppConstants.Miscellaneous || it.stage.stageName == AppConstants.Fixout_Stage) {
                            AppConstants.Fixing_Stage
                        } else if (it.stage.stageName == AppConstants.Completion || it.stage.stageName == AppConstants.Handover) {
                            AppConstants.Finishing_Stage
                        } else {
                            it.stage.stageName
                        }
                    if (stageName == AppConstants.Finishing_Stage || stageName == AppConstants.Fixing_Stage || stageName == AppConstants.Base_Stage || stageName == AppConstants.Lockup_Stage || stageName == AppConstants.Frame_Stage) {
                        listConstructionTasks.add(
                            TaskCompletedData(
                                it.taskId,
                                it.taskName,
                                it.completedDate,
                                stageName,
                                it.hidden
                            )
                        )
                    }
                }
            }

            val constructiongroupedList: List<List<TaskCompletedData>> =
                listConstructionTasks.groupBy { title -> title.stageName }.map { it.value }

            constructiongroupedList.forEachIndexed { index, it ->
                var userJobProgressItem = ArrayList<UserJobProgressItem>()

                var completedTasks = 0.0
                var stageName = ""
                var hidden =true

                var completedDate = ""
                var listOfTaskData = ArrayList<TasksData>()
                it.forEach {
                    var status = "Pending"
                    var completedDateVal = ""
                    if (it.completedDate != null) {
                        completedTasks += 1
                        completedDate = it.completedDate!!
                        status = "Completed"
                        completedDateVal = it.completedDate!!
                    }
                    stageName = it.stageName!!
                    hidden = it.hidden!!
                    listOfTaskData.add(TasksData(completedDate, it.taskId, it.taskName))

                    userJobProgressItem.add(
                        UserJobProgressItem(
                            comment = "",
                            dateactual = completedDateVal,
                            datedescription = "",
                            forclient = true,
                            name = it.taskName,
                            phasecode = "",
                            resourcename = "",
                            sequence = 0,
                            stageId = 0,
                            stageName = stageName,
                            status = status,
                            taskid = it.taskId!!.toInt()
                        )
                    )

                }

                var actpercentage = (completedTasks * 100) / it.size
                var percetageVal = roundValCal(actpercentage)
//            val solution:Double = Math.round(actpercentage * 10.0) / 10.0
//            val percentage = actpercentage.toBigDecimal().setScale(1, RoundingMode.CEILING).toInt()
//                     if (stageName == "Administration") {
//                         stageName = "$stageName C"
//                     }
                if(stageName!=null && stageName!="" ) {
                    listConstructionStageCondition.add(
                        TaskCompletedInfoData(
                            stageName,
                            it.size,
                            completedTasks,
                            percetageVal,
                            listOfTaskData,
                            userJobProgressItem
                        )
                    )



                    processProgressDataNew(userJobProgressItem, index + 2)
                }
            }

        }
        AppConstants.UserJobProgressNewItem = userJobProgressNewItemList
        updateSubHeaders()
        setNotificationCount()
        var listConstructionStageConditionReArrange = Array<TaskCompletedInfoData?>(7){null}

        for(it in listConstructionStageCondition){
            /*  if(it.stageName=="Your New Home"){
                listConstructionStageConditionReArrange[0]=it

            }*/
            when(it.stageName){
                AppConstants.Your_New_Home   ->  listConstructionStageConditionReArrange[0]=it
                AppConstants.Admin_Stage     ->  listConstructionStageConditionReArrange[1]=it
                AppConstants.Base_Stage      ->  listConstructionStageConditionReArrange[2]=it
                AppConstants.Frame_Stage     ->  listConstructionStageConditionReArrange[3]=it
                AppConstants.Lockup_Stage    ->  listConstructionStageConditionReArrange[4]=it
                AppConstants.Fixing_Stage    ->  listConstructionStageConditionReArrange[5]=it
                AppConstants.Finishing_Stage ->  listConstructionStageConditionReArrange[6]=it
            }


        }

        listConstructionStageCondition.clear()
        listConstructionStageCondition = listConstructionStageConditionReArrange.toList() as ArrayList<TaskCompletedInfoData>

        AppConstants.listConstructionStageCondition=listConstructionStageCondition
        GlobalScope.launch(Dispatchers.Main) {
            delay(1000L) // non-blocking delay for 1 second (default time unit is ms)
            try {
                if(listConstructionStageCondition.size>0) {
                    progressTasksNewAdapter?.setProgressList(listConstructionStageCondition)
                }
                binding.tvStage.text = getString(R.string.your_overall_progress)
            } catch (t: Throwable) {
                println(t.message)
            }
        }
        requestApiClickHomeLoginBaseUrl()
        if(PrefsHelper.notificationCount != "0" && PrefsHelper.notificationCount != "") {
            var notificationCount = PrefsHelper.notificationCount
            if (notificationCount!="" && notificationCount.toInt() > 100) {
                notificationCount = "99+"
            }
            binding.profileHeader.tvNotificationsCount.text =notificationCount
        }
        binding.profileHeader.tvNotificationsCount.visibility = if(PrefsHelper.notificationCount == "0" || PrefsHelper.notificationCount == "")
            View.GONE else View.VISIBLE
        pd?.dismiss()
    }

    fun requestApiClickHomeLoginBaseUrl(){

        var Password = AppController.controller?.my_Place_Details?.password
        var JobNo = AppController.controller?.my_Place_Details?.jobNumber
        var UserName = AppController.controller?.my_Place_Details?.username
        /*   val jsonParser = JsonParser()
           val jsonString = "{contractNumber:$JobNo,userName:$UserName,password:$Password}"
           val jsonObjectObj = jsonParser.parse(jsonString).asJsonObject*/
        val jsonObjectObj = Utils.getLoginRequest(JobNo,UserName,Password)

        if (Utils.isNetworkAvailable(activity)) {
            var dialog = Utils.getProgress(activity)
            ApiRepository.requestContactUsLogin(jsonObjectObj) { isSuccess, contactData, loginUserData ->
                dialog?.dismiss()
                if (isSuccess) {
                    if (Utils.isNetworkAvailable(activity)) {
                        activity?.let { SharedPrefHelper.setSharedOBJECT(activity!!,activity!!.getString(R.string.login_user_data),loginUserData)
                            AppConstants.AppCookieContactUs=contactData!!
                            requestPhotoAndDocumentsApiCall()

                        }

                    } else {
                        Toast.makeText(activity,"Please check internet and try again..",Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
    fun updateSubHeaders(){
        var all_items_percentage=0.0
        if(listConstructionStageCondition!=null && listConstructionStageCondition.isNotEmpty()) {
            listConstructionStageCondition.forEach {
                if(it!=null && it.stageName!=null) {
                    if (it.percentage != null) {
                        all_items_percentage += it.percentage!!
                    }
                }
            }
        }
        var totalPercentage =0

//        var total_actpercentage = ((all_items_percentage)/(listConstructionStageCondition.size-1)).toDouble()
        var total_actpercentage = (all_items_percentage/6).toDouble()

//        totalPercentage = total_actpercentage.toBigDecimal().setScale(1, RoundingMode.UP).toInt()
//          totalPercentage = roundValCal(total_actpercentage)
        totalPercentage=roundValCal(total_actpercentage)
        binding.progress.progress=totalPercentage
        updateProfileProgress(totalPercentage)
    }
    fun roundValCal(actpercentage:Double):Int{
        var arr=actpercentage.toString().split(".")
        var percetageVal=0
        var afterDecimalVal:Char= arr[1][0]
        if(arr.isNotEmpty()){
            if(afterDecimalVal.toString().toInt() > 4){
                percetageVal=actpercentage.toInt()+1
            }else{
                percetageVal=arr[0].toInt()
            }
        }
        return percetageVal
    }
    private fun requestPhotoAndDocumentsApiCall() {
        if (Utils.isNetworkAvailable(activity)) {
            pd = Utils.getProgress(requireActivity())
            // Hide for 3.7 release need to uncomment for chart functionality
//            DashboardNewActivity().requestApiClickHomeMasterContracts(AppConstants.AppCookieContactUs,requireActivity() as AppCompatActivity)


            val jsonParser = JsonParser()
            val jsonString = AppConstants.jsonDocumentsString
            val jsonObjectObj = jsonParser.parse(jsonString).asJsonObject
            ApiRepository.requestPhotosApi(
                AppConstants.AppCookieContactUs,
                jsonObjectObj
            ) { isSuccess, res ->
                pd?.dismiss()
                if (isSuccess) {

                    val resjsonObjectObj = jsonParser.parse(res).asJsonObject
                    val documentsNotesList = resjsonObjectObj.getAsJsonObject("documents")
                        .getAsJsonArray(getString(R.string.list))
                    val photosNotesList = resjsonObjectObj.getAsJsonObject("photos")
                        .getAsJsonArray(getString(R.string.list))
                    val documentsData =
                        Gson().fromJson(documentsNotesList, DocumnetsData::class.java)
                    val photosData = Gson().fromJson(photosNotesList, PhotosData::class.java)
                    filterPhotosList(photosData)

                    val QldOrSaPhotosList = ArrayList<MyDocOrPhotosDataSetQldOrSa>()
                    photosData?.let { it ->
                        val filterList = it.filter {
                            it.url!=""
                        }

                        /* val filterList = it.filter {
                             it.extension?.trim()?.lowercase() == ".jpg" || it.extension?.trim()?.lowercase() == ".png"
                         }*/
//                        AppConstants.filterList = filterList as ArrayList<PhotosDataItem>
                        try {
                            filterList.forEach {
                                val json = Gson().toJson(it, PhotosDataItem::class.java)
                                val myDocumentsDataSetQldOrSa = MyDocOrPhotosDataSetQldOrSa(
                                    json,
                                    requireContext()
                                )

//                                if (myDocumentsDataSetQldOrSa.type != null && myDocumentsDataSetQldOrSa.type.isNotEmpty() && myDocumentsDataSetQldOrSa.type.lowercase()
//                                        .trim { it <= ' ' }
//                                        .equals("jpg", ignoreCase = true)
//                                ) {
                                QldOrSaPhotosList.add(myDocumentsDataSetQldOrSa)
//                                }
                            }
//                            myPlaceDataBase!!.insertQldOrSaPhotos(
//                                QldOrSaPhotosList,
//                                AppController.controller.jobNumber,
//                                AppController.controller.my_Place_Details.username,
//                            )

                            myPlaceDataBase!!.insertQldOrSaPhotos(
                                QldOrSaPhotosList,
                                AppController.controller.my_Place_Details.jobNumber,
                                AppController.controller.my_Place_Details.username,
                            )

                            val sortedList = QldOrSaPhotosList.sortedByDescending { it.docDate }

                            sortedList.forEach {
                                myPlaceDataBase!!.insertPhotoNotifications(
                                    AppController.controller.my_Place_Details.jobNumber,
                                    AppController.controller.my_Place_Details.username,
                                    it.docDate,
                                    it.docDate
                                )
                            }
                        }catch (e:Exception){
                            e.printStackTrace()
                        }
                    }
                    documentsData?.let { it ->

                        val docfilterList = it.filter {
                            it.url!=""
//                            it.extension?.trim()?.lowercase() != ".jpg" && it.extension?.trim()?.lowercase() != ".png" && it.extension?.trim()?.lowercase() != ".jpeg"
//                                    && it.extension?.trim()?.lowercase() != ".eml" && it.extension?.trim()?.lowercase() != ".txt"
                        }
//                        val docfilterList = it.filter {
//                            it.extension?.trim()?.lowercase() == ".pdf" || it.extension?.trim()?.lowercase() == ".PDF"
//                        }


//                                if (myDocumentsDataSetQldOrSa.type != null && myDocumentsDataSetQldOrSa.type.isNotEmpty() && myDocumentsDataSetQldOrSa.type.lowercase()
//                                        .trim { it <= ' ' }
//                                        .equals("jpg", ignoreCase = true)
//                                ) {
                        docfilterList.forEach {
                            val json = Gson().toJson(it, PhotosDataItem::class.java)
                            val myDocumentsDataSetQldOrSa = MyDocOrPhotosDataSetQldOrSa(
                                json,
                                requireContext()
                            )
                            QldOrSaPhotosList.add(myDocumentsDataSetQldOrSa)
                        }
//                        var groupedList = docfilterList.groupBy { title -> title.title.split(" ")[0] }.map { it.value }
                        AppConstants.documentsList = sortBydateList(docfilterList)
                        myPlaceDataBase!!.insertQldOrSaPhotos(
                            QldOrSaPhotosList,
                            AppController.controller.my_Place_Details.jobNumber,
                            AppController.controller.my_Place_Details.username,
                        )
                    }
                    val sortedList = QldOrSaPhotosList.sortedByDescending { it.docDate }

                    sortedList.forEach {
                        myPlaceDataBase!!.insertPhotoNotifications(
                            AppController.controller.my_Place_Details.jobNumber,
                            AppController.controller.my_Place_Details.username,
                            it.docDate,
                            it.docDate
                        )
                    }
                    setNotificationCount()
                    if(PrefsHelper.notificationCount != "0" && PrefsHelper.notificationCount != "") {
                        var notificationCount = PrefsHelper.notificationCount
                        if (notificationCount!="" && notificationCount.toInt() > 100) {
                            notificationCount = "99+"
                        }
                        binding.profileHeader.tvNotificationsCount.text =notificationCount
                    }
                    binding.profileHeader.tvNotificationsCount.visibility = if(PrefsHelper.notificationCount == "0"|| PrefsHelper.notificationCount == "")
                        View.GONE else View.VISIBLE

                    binding.profileHeader.ivPhoto.setOnClickListener {
                        startActivity(Intent(requireContext(), MyNotificationActivity::class.java))
                    }

                }
            }
        }
    }
    private fun sortBydateList(documentsList : List<PhotosDataItem>):List<PhotosDataItem>{
        val sortedList = documentsList.sortedByDescending {  it.metaData.createdOn }
        return sortedList
    }
    override fun onDestroyView() {
        super.onDestroyView()
    }
    private fun processProgressDataNew(progressData: List<UserJobProgressItem>?,postion:Int) {
        val adCompletedTask = progressData?.filter {
            it.status.equals("Completed", false)
        }
//        if(it.completedDate!=null && it.completedDate!=""){
        addProgressDataToLocalDB(adCompletedTask, postion)

    }

    private fun addProgressDataToLocalDB(list: List<UserJobProgressItem>?, position: Int){
        list?.forEach {
            myPlaceDataBase?.insertProgress(
                controller?.my_Place_Details?.jobNumber,
                controller?.my_Place_Details?.username,
                it.taskid,
                position,
                it.name,
                it.status,
                it.dateactual!!.convertDateFormat(LOCAL_DB_DATE_FORMAT),
                it.dateactual
            )
        }
        setNotificationCount()
    }

    private fun setNotificationCount(){
        PrefsHelper.notificationCount = myPlaceDataBase?.getNotificationCount(
            controller?.my_Place_Details?.jobNumber,
            controller?.my_Place_Details?.username).toString()

    }

    private fun updateProfileProgress(progress: Int) {
        var JOBNUMBER=""
        if( AppController.getPreference(activity,AppController.PREVIOUSSELECTEDJOBNUMBER)!=AppController.defaultValue){
            JOBNUMBER=AppController.getPreference(context,AppController.PREVIOUSSELECTEDJOBNUMBER)
        }
        if(JOBNUMBER==""){
            JOBNUMBER=AppController.controller.userProfile.userDetails[0].myPlaceJobDetailses[0].jobNo
        }
        binding.progress.show()
        AppController.progres=progress
        var initString= Html.fromHtml("Your home " +"<strong><font color='#FF6224'>"+JOBNUMBER+"</font></strong>"+" "+getString(R.string.your_home_is_currently_completed)+"<strong>$progress%</strong> completed. Swipe to see your stages.")
        binding.profileHeader.tvSubHeading.text =initString
//        (requireActivity() as DashboardNewActivity).binding.leftDrawerMenu.tvCompletedPercentage.text=initString
        var initMenuString= Html.fromHtml("Your home " +"<strong><font color='#FF6224'>"+JOBNUMBER+"</font></strong>"+" "+getString(R.string.your_home_is_currently_completed)+"<strong>$progress%</strong> completed.")

        viewModel.setProfileSubHeader(initMenuString)


        binding.profileHeader.tvHeading.text = getString(R.string.myprogress)
        binding.profileHeader.ivPhoto.loadUrlUsingGlide(PrefsHelper.profileUrl)
        if(PrefsHelper.notificationCount != "0" && PrefsHelper.notificationCount != "") {
            var notificationCount = PrefsHelper.notificationCount
            if (notificationCount!="" && notificationCount.toInt() > 100) {
                notificationCount = "99+"
            }
            binding.profileHeader.tvNotificationsCount.text =notificationCount
        }
        binding.profileHeader.tvNotificationsCount.visibility = if(PrefsHelper.notificationCount == "0" || PrefsHelper.notificationCount == "")
            View.GONE else View.VISIBLE
        binding.profileHeader.ivPhoto.setOnClickListener {
            startActivity(Intent(requireContext(), MyNotificationActivity::class.java))
        }
    }



}