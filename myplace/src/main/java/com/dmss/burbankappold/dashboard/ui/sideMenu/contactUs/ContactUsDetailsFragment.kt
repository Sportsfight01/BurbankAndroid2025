package com.dmss.burbankappold.dashboard.ui.sideMenu.contactUs

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.doOnLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.dmss.burbankappold.R
import com.dmss.burbankappold.databinding.FragmentContactUsDetailsBinding
import com.dmss.burbankappold.databinding.LayoutContactUsReplyDialogBinding
import com.dmss.burbankappold.network.ApiRepository
import com.dmss.burbankappold.utils.*
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import common.AppController
import common.TransparentProgressDialog
import common.Utils
import models.contactUs.ContactUsDataItem

class ContactUsDetailsFragment : Fragment() {
    private lateinit var mBinding: FragmentContactUsDetailsBinding
    private var contactsDataItem: NotesData? = null
    private lateinit var alertDialog: AlertDialog
    private var dialog: TransparentProgressDialog? = null
    private lateinit var contactUsRepliesAdapter: ContactUsRepliesAdapter
    private var mReliesList = ArrayList<NotesData>()
    private var mReliesToDataList = listOf<NotesData>()
    private var contractStatusContractId=""
    var v3LoginCookie=""

    var inititaildeltaX=0f
    var rlLayoutpercent=0
    var rlLayoutpercentRight=0
    var createMessageAlertOpen=false
    var tvInBoxOriginalXval=0f
    var tvReplyOriginalXVal=0f
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        mBinding = FragmentContactUsDetailsBinding.inflate(layoutInflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        contactsDataItem = arguments?.getParcelable(BundleKey.DATA_OBJECT)
        mReliesToDataList = AppConstants.terplysData

        if(contactsDataItem?.subject!=null) {
            mBinding.tvSubject.text = contactsDataItem?.subject+" ("+ contactsDataItem?.noteId+")"
        }
        mBinding.tvAuthor.text=AppConstants.selectedTitlte
        val mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity)
        var editor = mSharedPreferences.edit()
        editor.putBoolean(""+contactsDataItem?.noteId+"_"+contactsDataItem?.subject,true)
        editor.commit()
        if(contactsDataItem?.body!=null) {
            mBinding.tvbody.text = "${contactsDataItem?.body}"
        }

        mBinding.tvReply.setOnClickListener {
            createReplyDialog()
        }
        mBinding.tvInBox.setOnClickListener {
            requireActivity().onBackPressed()
        }
        initAdapter()
//        createSwipe()
    }
    @SuppressLint("ClickableViewAccessibility")
    private fun createSwipe(){
        mBinding.rlLayout.doOnLayout {
            rlLayoutpercent= (it.measuredWidth * 0.55).toInt()
            rlLayoutpercentRight= (it.measuredWidth * 0.20).toInt()

            println("measuredWidth:: "+it.measuredWidth+" percent:: "+rlLayoutpercent )
            tvInBoxOriginalXval=mBinding.tvInBox.x
            tvReplyOriginalXVal=mBinding.tvReply.x
        }
        mBinding.tvInBox.setOnTouchListener { v, motionEvent ->
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
                MotionEvent.ACTION_MOVE -> {
                    val deltaX=motionEvent.rawX-lastX
                    val deltY=motionEvent.rawY-lastY
                    var position=originalX+deltaX-300f
                    if(rlLayoutpercentRight<position.toInt()){
                        requireActivity().onBackPressed()

                    }
                    if(position>0)
                        v.x=position
//                    v.y=originalY+deltY

                }
                MotionEvent.ACTION_UP ->{
                    v.x=tvInBoxOriginalXval
                }
            }
            true
        }

        mBinding.tvReply.setOnTouchListener { v, motionEvent ->
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
                MotionEvent.ACTION_MOVE -> {

                    val deltaX=motionEvent.rawX-lastX
                    val deltY=motionEvent.rawY-lastY
                    var position=originalX+deltaX-230f
                    if(inititaildeltaX==0f){
                        inititaildeltaX=position
                        println("inititaildeltaX:: $inititaildeltaX")
                    }
                    if(rlLayoutpercent>position.toInt()){
                        println(" ACTION_MOVE "+rlLayoutpercent+" position::  "+position.toInt())
                        if(!createMessageAlertOpen) {
                            createMessageAlertOpen = true
                            createReplyDialog()
                        }
                    }
//                    if(position<0) {
                    v.x = position
//                    }
//                    v.y=originalY+deltY

                }
                MotionEvent.ACTION_UP ->{
//                    println("lastX:: $lastX inititaildeltaX:: $inititaildeltaX")

                    v.x= tvReplyOriginalXVal
                }
            }
            true
        }
    }

    private fun initAdapter() {
        contactUsRepliesAdapter = ContactUsRepliesAdapter()
        mBinding.rvReplies.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = contactUsRepliesAdapter
        }
        setAdapter()

    }
    private fun setAdapter(){
        if(contactsDataItem!=null){
            mReliesList= mReliesToDataList.filter { it.replyTo?.noteId == contactsDataItem?.noteId} as ArrayList<NotesData>
            if (mReliesList != null && mReliesList.size>0){
                contactUsRepliesAdapter.setContactsList(mReliesList)
                mBinding.tvReplyText.visibility=View.VISIBLE

            }else{
                mBinding.tvReplyText.visibility=View.GONE
            }
        }
    }

    private fun createReplyDialog(){
        val layoutReplyBinding: LayoutContactUsReplyDialogBinding =
            LayoutContactUsReplyDialogBinding.inflate(layoutInflater)
        val shareAlertBuilder: AlertDialog.Builder =
            AlertDialog.Builder(context)
        shareAlertBuilder.setCancelable(false)
        shareAlertBuilder.setView(layoutReplyBinding.root)
        layoutReplyBinding.etSubject.setText("Re: ${contactsDataItem?.subject} ${contactsDataItem?.noteId}")
        layoutReplyBinding.etTo.setText(contactsDataItem?.author?.fullName)
        layoutReplyBinding.etSubject.background = ContextCompat.getDrawable(requireContext(), R.drawable.reply_note_edit_bg)
        layoutReplyBinding.etTo.background = ContextCompat.getDrawable(requireContext(), R.drawable.reply_note_edit_bg)
        layoutReplyBinding.etTo.setDisable()
        layoutReplyBinding.etSubject.setDisable()
        var username = AppController.controller?.userProfile!!.userDetails[0].fullName

        alertDialog = shareAlertBuilder.create()
        layoutReplyBinding.btnCancel.setOnClickListener {
            alertDialog.dismiss()
            createMessageAlertOpen = false

        }
        layoutReplyBinding.btnSubmit.setOnClickListener {
            val message = layoutReplyBinding.etMessage.text.toString().trim()
            val subject = layoutReplyBinding.etSubject.text.toString().trim()
            createMessageAlertOpen = false
            if (isAllFieldsValidated(subject, message)) {
                dialog = Utils.getProgress(requireActivity())
                val jsonBody = JsonObject()
                val replyjsonBody = JsonObject()

                if(username!=null && username!="") {
                    jsonBody.addProperty("body", "$message - Message from $username")

                }else {
                    jsonBody.addProperty("body", message)
                }
                jsonBody.addProperty("noteType", "N")

//                jsonBody.addProperty("stepid", 0)
                jsonBody.addProperty("subject", "$subject (MyPlace App Message)")
//                jsonBody.addProperty("subject", subject+" (MyPlace App Message)")
//                jsonBody.addProperty("replytoid", contactsDataItem?.noteId)
                replyjsonBody.addProperty("noteId", contactsDataItem?.noteId)
                replyjsonBody.addProperty("noteType", "N")
                jsonBody.add("replyTo",replyjsonBody)

                ApiRepository.requestNewMessage(AppConstants.AppCookieContactUs,contactsDataItem?.contract?.contractId!!,jsonBody) { isSuccess, response ->
                    dialog?.dismiss()
                    if (isSuccess) {
                        response?.also {
                            if(Utils.isNetworkAvailable(activity))
                                requestApiClickHomeLoginBaseUrl()
                        }
                        alertDialog.dismiss()
                        contactUsRepliesAdapter.notifyDataSetChanged()
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
    fun requestApiClickHomeLoginBaseUrl(){
        var Password= AppController.controller?.my_Place_Details?.password
        var JobNo= AppController.controller?.my_Place_Details?.jobNumber
        var UserName= AppController.controller?.my_Place_Details?.username
        /*val jsonParser = JsonParser()
        val jsonString="{contractNumber:$JobNo,userName:$UserName,password:$Password}"
        val jsonObjectObj = jsonParser.parse(jsonString).asJsonObject*/
        val jsonObjectObj = Utils.getLoginRequest(JobNo,UserName,Password)

        dialog = Utils.getProgress(activity)
        ApiRepository.requestContactUsLogin(jsonObjectObj){ isSuccess, contactData,loginUserData ->
            dialog?.dismiss()
            if (isSuccess){
                v3LoginCookie=contactData!!
                AppConstants.AppCookieContactUs=contactData!!
                requestApiClickHomeMasterContracts(contactData!!)
            }
        }
    }
    private fun requestApiClickHomeMasterContracts(cookie:String){
        val jsonParser = JsonParser()
        val jsonString =AppConstants.jsonNotesString
        val jsonObjectObj = jsonParser.parse(jsonString).asJsonObject
        dialog = Utils.getProgress(activity)
        ApiRepository.requestContactMasterContracts(cookie,jsonObjectObj){ isSuccess, contactData ->
            dialog?.dismiss()
            if (isSuccess){
                val resjsonObjectObj = jsonParser.parse(contactData).asJsonObject




                contractStatusContractId=AppConstants.getContractId(resjsonObjectObj)
                println("contractStatusContractId"+contractStatusContractId)



                var notesList=resjsonObjectObj.getAsJsonObject(getString(R.string.notes)).getAsJsonArray(getString(R.string.list))
                var mNotesData=Gson().fromJson(notesList,Array<NotesData>::class.java)

                var NoReplayToData= mNotesData.filter { it.replyTo==null }
                mReliesToDataList= mNotesData.filter { it.replyTo!=null } as ArrayList<NotesData>
                val NoReplayToDataSort = AppConstants.filterAndSortNotesData(NoReplayToData,mReliesToDataList)
                AppConstants.liastOfNotesData=NoReplayToDataSort.reversed()
                AppConstants.contactUsGivenReply=true
                setAdapter()

            }
        }
    }
}