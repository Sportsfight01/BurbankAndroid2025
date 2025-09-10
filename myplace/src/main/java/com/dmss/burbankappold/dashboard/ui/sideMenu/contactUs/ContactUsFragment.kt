package com.dmss.burbankappold.dashboard.ui.sideMenu.contactUs

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.view.GestureDetector.SimpleOnGestureListener
import androidx.core.os.bundleOf
import androidx.core.view.doOnLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.dmss.burbankappold.R
import com.dmss.burbankappold.databinding.FragmentContactUsBinding
import com.dmss.burbankappold.databinding.LayoutContactUsReplyDialogBinding
import com.dmss.burbankappold.network.ApiRepository
import com.dmss.burbankappold.utils.*
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import common.AppController
import common.TransparentProgressDialog
import common.Utils

class ContactUsFragment : Fragment() {
    private lateinit var mBinding : FragmentContactUsBinding
    private lateinit var contactUsAdapter: ContactUsAdapter
    private var dialog: TransparentProgressDialog? = null
    private var mList = listOf<NotesData>()
    private var mMergedList = listOf<NotesData>()
    private var contractStatusContractId=""
    private var mReplyDataList = listOf<NotesData>()
    private var isSearchAvailable=false
    var rlLayoutpercent=0
    var createMessageAlertOpen=false
    var v3LoginCookie=""
    var tvNewMessageOriginalXval=0f
    private lateinit var alertDialog: AlertDialog
    lateinit var mSharedPreferences: SharedPreferences
    private fun sortBydateList(documentsList : List<NotesData>): List<NotesData> {
        val sortedList = documentsList.sortedByDescending {  it.activityDate }
        return sortedList
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        mBinding = FragmentContactUsBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        setOnClickListeners()
    }

    private fun initAdapter() {
        contactUsAdapter = ContactUsAdapter{
            AppConstants.terplysData= it.replies?.let { it1 -> sortBydateList(it1) }!!
            findNavController().navigate(R.id.action_fragment_contact_us_details,
                bundleOf(BundleKey.DATA_OBJECT to it))
        }
        mBinding.rvContactUs.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = contactUsAdapter
        }
        if(AppConstants.liastOfNotesData.isEmpty()) {
            AppConstants.validateVersionCode(requireActivity()) {
                if (!it) {
                    requestApiClickHomeLoginBaseUrl()
                    AppConstants.contactusSearchEnable = false
                    requestApiClickHomeMasterContracts(AppConstants.AppCookieContactUs)
                }
            }
        }else{

            mList=AppConstants.liastOfNotesData
//            println("mList Not empty ::"+ mList[0].noteId)
            if(AppConstants.contactUsGivenReply)
                mBinding.rvContactUs.layoutManager?.smoothScrollToPosition(mBinding.rvContactUs, null ,0)
            AppConstants.contactUsGivenReply=false
        }
        contactUsAdapter.setContactsList(mList)
        contactUsAdapter.notifyDataSetChanged()
        if (AppConstants.contactusSearchEnable){
            mBinding.rlSearch.show()

        }else {
            mBinding.rlSearch.show()
        }

//        requestApiCall()
    }
    private fun requestApiCall(){
        dialog = Utils.getProgress(requireActivity())
        ApiRepository.requestContactUsDetails{ isSuccess, contactData ->
            dialog?.dismiss()
            if (isSuccess){
                contactData?.let {
                    /*        contactUsAdapter.setContactsList(it.reversed())
                            mList = it*/
                }
            }
        }
    }

    fun requestApiClickHomeLoginBaseUrl(){
        var Password= AppController.controller?.my_Place_Details?.password
        var JobNo= AppController.controller?.my_Place_Details?.jobNumber
        var UserName= AppController.controller?.my_Place_Details?.username
        val jsonParser = JsonParser()
     /*   val jsonString="{contractNumber:$JobNo,userName:$UserName,password:$Password}"
        println("jsonString:: "+jsonString)
        val jsonObjectObj = jsonParser.parse(jsonString).asJsonObject*/
        val jsonObjectObj = Utils.getLoginRequest(JobNo,UserName,Password)

        if (Utils.isNetworkAvailable(activity)) {
//            dialog = Utils.getProgress(activity)
            ApiRepository.requestContactUsLogin(jsonObjectObj) { isSuccess, contactData,loginUserData->
                dialog?.dismiss()
                if (isSuccess) {
                    if (Utils.isNetworkAvailable(activity)) {
                        SharedPrefHelper.setSharedOBJECT(activity!!,getString(R.string.login_user_data),loginUserData)
                         v3LoginCookie=contactData!!
                        AppConstants.AppCookieContactUs=contactData!!
                        dialog?.show()
                        requestApiClickHomeMasterContracts(contactData!!)
                    }else{

                    }
                }
            }
        }
    }
    private fun requestApiClickHomeMasterContracts(cookie:String){

        val jsonParser = JsonParser()
        val jsonString =AppConstants.jsonNotesString
        val jsonObjectObj = jsonParser.parse(jsonString).asJsonObject
        var dialog = Utils.getProgress(activity)
        ApiRepository.requestContactMasterContracts(cookie,jsonObjectObj){ isSuccess, contactData ->
            dialog?.dismiss()
            if (isSuccess){
                val resjsonObjectObj = jsonParser.parse(contactData).asJsonObject
                println("resjsonObjectObj:: "+resjsonObjectObj)
                contractStatusContractId=AppConstants.getContractId(resjsonObjectObj)


                println(" contractStatusContractId:: "+contractStatusContractId)

                val notesList=resjsonObjectObj.getAsJsonObject(getString(R.string.notes)).getAsJsonArray(getString(R.string.list))
                val mNotesData=Gson().fromJson(notesList,Array<NotesData>::class.java)
                val NoReplayToData= mNotesData.filter { it.replyTo==null }
                val filterReplayToData= mNotesData.filter { it.replyTo!=null }
                mReplyDataList=filterReplayToData
                val NoReplayToDataSort =AppConstants.filterAndSortNotesData(NoReplayToData,filterReplayToData)
                mList=NoReplayToDataSort.reversed()
//                var distinctByList = sortBydateList(mReplyDataList.distinctBy { it.replyTo!!.noteId })


                contactUsAdapter.setContactsList(mList)
                AppConstants.liastOfNotesData=mList
                var viewModel = activity?.run {
                    ViewModelProviders.of(this)[SharedViewModel::class.java]
                } ?: throw Exception("Invalid Activity")
                if(mList.isNotEmpty()) {
                    viewModel.setNotesdata(mReplyDataList as ArrayList<NotesData>)
                    mBinding.noRecordFound.visibility=View.GONE
                    mBinding.rvContactUs.visibility=View.VISIBLE
                    isSearchAvailable=true
                }else{
                    mBinding.noRecordFound.visibility=View.VISIBLE
                    mBinding.rvContactUs.visibility=View.GONE
                    isSearchAvailable=false
                }

                dialog?.dismiss()

            }
        }
    }
    @SuppressLint("ClickableViewAccessibility")
    private fun setOnClickListeners() {


        var  gestureDetector = GestureDetector(activity!!, SingleTapConfirm())

        mBinding.swiperefresh.setOnRefreshListener {
            if(mList.isEmpty() && Utils.isNetworkAvailable(activity)) {
                Utils.hideSwipeRefresh(mBinding.swiperefresh)

                requestApiClickHomeLoginBaseUrl()
            }else{
                Utils.hideSwipeRefresh(mBinding.swiperefresh)
            }

        }
        mBinding.ivSearch.setOnClickListener {
            if (mBinding.rlSearch.isVisible){
                AppConstants.contactusSearchEnable=false
                mBinding.rlSearch.hide()

            }else {
                AppConstants.contactusSearchEnable=true
                mBinding.rlSearch.show()
            }
        }

        mBinding.rlLayout.doOnLayout {
            rlLayoutpercent=it.measuredWidth/ 100 * 40
//            println("measuredWidth:: "+it.measuredWidth+" percent:: "+rlLayoutpercent )
            tvNewMessageOriginalXval=mBinding.tvNewMessage.x

        }
        mBinding.tvNewMessage.setOnClickListener {
            createReplyDialog()

        }
        /*  mBinding.tvNewMessage.setOnTouchListener(object : OnTouchListener {
              override fun onTouch(arg0: View?, arg1: MotionEvent?): Boolean {
                  if (gestureDetector.onTouchEvent(arg1)) {
                      // single tap
                      createReplyDialog()
                      return true
                  } else {
                      // your code for move and drag
                  }
                  return false
              }
          })*/
        /* mBinding.tvNewMessage.setOnTouchListener { v, motionEvent ->

             var lastX=0f
               var lastY=0f
               var originalX=0
               var originalY=0
               when (motionEvent.action) {
                   MotionEvent.ACTION_DOWN -> {
                       lastX=motionEvent.rawX
                       lastY=motionEvent.rawY
                       originalX=v.x.toInt()
                       originalY=v.y.toInt()
   //                     println(" RAWW ::ACTION_DOWN  "+originalX)

                   }
                   MotionEvent.ACTION_DOWN -> {
                       lastX=motionEvent.rawX
                       lastY=motionEvent.rawY
                       originalX=v.x.toInt()
                       originalY=v.y.toInt()
   //                     println(" RAWW ::ACTION_DOWN  "+originalX)

                   }
                   MotionEvent.ACTION_MOVE -> {
                       val deltaX=motionEvent.rawX-lastX
                       val deltY=motionEvent.rawY-lastY
                       var position=originalX+deltaX-300f
                     if(rlLayoutpercent<position.toInt()){
                         println(" ACTION_MOVE "+rlLayoutpercent+" position::  "+position.toInt())
                         if(!createMessageAlertOpen) {
                             createMessageAlertOpen = true
                             createReplyDialog()
                         }
                     }
                       if(position>0)
                       v.x=position
   //                    v.y=originalY+deltY

                   }
                   MotionEvent.ACTION_UP ->{
                       v.x=tvNewMessageOriginalXval
                   }

               }

               true
           }*/

        mBinding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (s != null && s.isNotEmpty()){
//                  contactUsAdapter.filter.filter(s.toString())
                    filter(s.toString())
                }else{
                    mBinding.noRecordFound.visibility=View.GONE
                    mBinding.rvContactUs.visibility=View.VISIBLE
                    contactUsAdapter.filterList(mList)

                }
            }
        })

        mBinding.icClose.setOnClickListener {
            if (mBinding.etSearch.text.isNullOrBlank()){
                mBinding.etSearch.clearFocus()
                hideKeyboardFrom(mBinding.root.context, mBinding.etSearch)
                showOrHideSearch(false)
            }else {
                contactUsAdapter.filterList(mList)

                mBinding.etSearch.setText("")}
        }
        mBinding.ivSearch.setOnClickListener {
            if(isSearchAvailable)
                if (mBinding.rlSearch.isVisible ) {
                    showOrHideSearch(false)
                    mBinding.etSearch.setText("")
                    mBinding.etSearch.clearFocus()
                    hideKeyboardFrom(mBinding.root.context, mBinding.etSearch)
                }else{
                    showOrHideSearch(true)
                }
        }

    }
    private fun showOrHideSearch(isShow: Boolean){
        if (isShow) {
            mBinding.rlSearch.show()
            AppConstants.contactusSearchEnable=true

        }else {
            mBinding.rlSearch.hide()
            AppConstants.contactusSearchEnable=false

        }
    }
    private fun filter(text: String) {
        // creating a new array list to filter our data.
        val filteredlist: ArrayList<NotesData> = ArrayList()

        // running a for loop to compare elements.
        for (item in mList) {
            // checking if the entered string matched with any item of our recycler view.
            if (item.unknownAuthor!=null && item.unknownAuthor!!.toLowerCase().contains(text.toLowerCase())
            ) {
                // if the item is matched we are
                // adding it to our filtered list.
                filteredlist.add(item)
            }else if(item.subject!=null && item.subject!!.toLowerCase().contains(text.toLowerCase())){
                filteredlist.add(item)

            }else if(item.activityDate!=null &&item.activityDate.convertDateFormat().contains(text)){
                filteredlist.add(item)

            }
        }
        if (filteredlist.isEmpty()) {
            // if no item is added in filtered list we are
            // displaying a toast message as no data found.
//            Toast.makeText(activity, "No Data Found..", Toast.LENGTH_SHORT).show()
            mBinding.noRecordFound.visibility=View.VISIBLE
            mBinding.rvContactUs.visibility=View.INVISIBLE

            contactUsAdapter.filterList(filteredlist)

        } else {
            // at last we are passing that filtered
            // list to our adapter class.
            mBinding.noRecordFound.visibility=View.GONE

            mBinding.rvContactUs.visibility=View.VISIBLE
            contactUsAdapter.filterList(filteredlist)
        }
    }
    private fun createReplyDialog(){
        val layoutReplyBinding: LayoutContactUsReplyDialogBinding =
            LayoutContactUsReplyDialogBinding.inflate(layoutInflater)
        val shareAlertBuilder: AlertDialog.Builder =
            AlertDialog.Builder(context)
        shareAlertBuilder.setCancelable(false)
        shareAlertBuilder.setView(layoutReplyBinding.root)
        alertDialog = shareAlertBuilder.create()
        layoutReplyBinding.btnCancel.setOnClickListener {
            alertDialog.dismiss()
            createMessageAlertOpen = false

        }
        layoutReplyBinding.messageHeader.text="New Message"
        layoutReplyBinding.btnSubmit.setOnClickListener {
            val message = layoutReplyBinding.etMessage.text.toString().trim()
            val subject = layoutReplyBinding.etSubject.text.toString().trim()

            var username=AppController.controller?.userProfile!!.userDetails[0].fullName

            createMessageAlertOpen = false
            if (isAllFieldsValidated(subject, message)) {
                dialog?.show()
                val jsonBody = JsonObject()
                if(username!=null && username!="") {
                    jsonBody.addProperty("body", "$message - Message from $username")
                }else{
                    jsonBody.addProperty("body", message)
                }
//                jsonBody.addProperty("replytoid", "null")
//                jsonBody.addProperty("stepid", 0)
                jsonBody.addProperty("noteType", "N")

                jsonBody.addProperty("subject", "MyPlace App Message: $subject")
                ApiRepository.requestNewMessage(v3LoginCookie,contractStatusContractId,jsonBody) { isSuccess, _ ->
                    dialog?.dismiss()
                    if (isSuccess) {
                        alertDialog.dismiss()
                        requestApiClickHomeLoginBaseUrl()
                    }
                }
            }
        }
        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        alertDialog.show()
    }

    private fun isAllFieldsValidated(subject: String, message: String): Boolean{
        if (subject.trim().isEmpty()){
            context?.showToast("Please enter subject")
            return false
        }
        else if (message.trim().isEmpty()){
            context?.showToast("Please enter message")
            return false
        }
        else if (message.length<9){
            context?.showToast("Please enter minimum of 10 characters")
            return false
        }
        return true
    }
    private class SingleTapConfirm : SimpleOnGestureListener() {
        override fun onSingleTapUp(event: MotionEvent?): Boolean {

//            println("onSingleTapUp")
            return true
        }
    }

}