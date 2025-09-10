package com.dmss.burbankappold.dashboard.ui.documents

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.dmss.burbankappold.BaseFragment
import com.dmss.burbankappold.R
import com.dmss.burbankappold.dashboard.PrefsHelper
import com.dmss.burbankappold.dashboard.ui.sideMenu.MyNotificationActivity
import com.dmss.burbankappold.dashboard.ui.sideMenu.contactUs.NotesData
import com.dmss.burbankappold.databinding.FragmentDocumentsBinding
import com.dmss.burbankappold.network.ApiRepository
import com.dmss.burbankappold.utils.*
import com.google.gson.Gson
import com.google.gson.JsonParser
import common.TransparentProgressDialog
import common.Utils
import models.photos.PhotosData
import models.photos.PhotosDataItem
import java.util.*

class DocumentsFragment : BaseFragment() {

    private var _binding: FragmentDocumentsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var documentsAdapter :  DocumentsAdapter? =  null
    private var documentsList = listOf<PhotosDataItem>()
    var pd : TransparentProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (documentsAdapter == null){
            documentsAdapter = DocumentsAdapter {clickType, document ->
                println("getDocUrl:: "+getDocUrl(document.url))
                when(clickType){
                    1 ->{
                        startActivity(Intent(requireContext(), WebViewActivity::class.java)
                            .putExtra(BundleKey.URL, getDocUrl(document.url)))
                    }
                }
            }
        }
    }

    /*private fun getDocUrl(tempUrl : String): String{
        return "https://drive.google.com/viewerng/viewer?embedded=true&url=https://nationalclickhome.burbankgroup.com.au/clickhome3webservice/$tempUrl"
    }*/
    private fun getDocUrl(tempUrl : String): String{
        return "https://drive.google.com/viewerng/viewer?embedded=true&url=$tempUrl"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDocumentsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()

        documentsList = AppConstants.documentsList
        if (documentsList.isEmpty()){
            requestApiCall()
        }else{
            documentsAdapter?.setDocumentsList(documentsList)
        }
        binding.etSearch.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                documentsAdapter?.filter?.filter(s.toString())
            }
        })
        binding.swiperefresh.setOnRefreshListener {
            if(documentsList.isEmpty()) {
                Utils.hideSwipeRefresh(binding.swiperefresh)
                requestApiCall()
            }else{
                Utils.hideSwipeRefresh(binding.swiperefresh)
            }
        }
        binding.icClose.setOnClickListener {
            if (binding.etSearch.text.isNullOrBlank()){
                binding.etSearch.clearFocus()
                hideKeyboardFrom(binding.root.context, binding.etSearch)
                showOrHideSearch(false)
            }else binding.etSearch.setText("")
        }
        binding.ivSearch.setOnClickListener {
            if (binding.clEditSearch.isVisible) {
                showOrHideSearch(false)
                binding.etSearch.setText("")
                binding.etSearch.clearFocus()
                hideKeyboardFrom(binding.root.context, binding.etSearch)
            }else{
                showOrHideSearch(true)
            }
        }
    }
    private fun showOrHideSearch(isShow: Boolean){
        if (isShow) binding.clEditSearch.show()
        else binding.clEditSearch.hide()
    }


    private fun sortBydateList(documentsList : List<PhotosDataItem>):List<PhotosDataItem>{
        val sortedList = documentsList.sortedByDescending {  it.metaData.createdOn }
        return sortedList
    }

   /* private fun requestApiCall() {
        if(Utils.isNetworkAvailable(activity)) {
            pd = Utils.getProgress(requireActivity())

//       skeletonScreen = Skeleton.bind(binding.rvDocuments)
//            .adapter(documentsAdapter)
//            .load(R.layout.layout_default_item_skeleton)
//            .show();
            ApiRepository.requestPhotosApi { isSuccess, photosData ->
                pd?.dismiss()
//            skeletonScreen.hide()
                if (isSuccess) {
                    photosData?.let { it ->
                        val docfilterList = it.filter {
                            it.type.trim().lowercase() != "jpg" && it.type.trim()
                                .lowercase() != "png" && it.type.trim().lowercase() != "jpeg"
                                    && it.type.trim().lowercase() != "eml"
                        }
                        println("docfilterList Size:: "+docfilterList.size)
                        var filterList = sortBydateList(docfilterList)
                        documentsList = filterList
                        if (documentsList.isNotEmpty()) {
                            documentsAdapter?.setDocumentsList(filterList)
                            binding.rvDocuments.visibility = View.VISIBLE
                            binding.nodocuments.visibility = View.GONE
                            binding.llSearch.visibility = View.VISIBLE

                        } else {
//                        binding.rvDocuments.visibility=View.GONE
                            binding.llSearch.visibility = View.GONE

                            binding.nodocuments.visibility = View.VISIBLE
                        }
                    }
                } else context?.showErrorToast()
            }
        }
    }*/
   @SuppressLint("SuspiciousIndentation")
   private fun requestApiCall() {
       if (Utils.isNetworkAvailable(activity)) {
           pd = Utils.getProgress(requireActivity())
           println("AppConstants.AppCookieContactUs:: "+AppConstants.AppCookieContactUs)
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
                  val notesList=resjsonObjectObj.getAsJsonObject("documents").getAsJsonArray(getString(R.string.list))
                  val photosData= Gson().fromJson(notesList,PhotosData::class.java)


                       photosData?.let { it ->
                          /* val docfilterList = it.filter {
                               it.extension?.trim()?.lowercase() != ".jpg" && it.extension?.trim()?.lowercase() != ".png" && it.extension?.trim()?.lowercase() != ".jpeg"
                                       && it.extension?.trim()?.lowercase() != ".eml"
                           }*/
                           /*val docfilterList = it.filter {
                                it.extension!=null && (it.extension?.trim()?.lowercase() == ".pdf" || it.extension?.trim()?.lowercase() == ".PDF")
                           }*/
                           val docfilterList = it.filter {
                               it.url!=null && it.url!=""
                           }
                           println("docfilterList Size:: " + docfilterList.size)
                           var filterList = sortBydateList(docfilterList)
                           documentsList = filterList
                           println("documentsList:: "+documentsList.size)
                           if (documentsList.isNotEmpty()) {
                               documentsAdapter?.setDocumentsList(filterList)
                               binding.rvDocuments.visibility = View.VISIBLE
                               binding.nodocuments.visibility = View.GONE
                               binding.llSearch.visibility = View.VISIBLE

                           } else {
//                        binding.rvDocuments.visibility=View.GONE
                               binding.llSearch.visibility = View.GONE

                               binding.nodocuments.visibility = View.VISIBLE
                           }
                       }
                   } else context?.showErrorToast()
               }
       }
   }

    private fun initViews() {
        binding.profileHeader.tvHeading.text = getString(R.string.my_documents)
        binding.profileHeader.tvHeading.changeTextColor(context)
        binding.profileHeader.tvSubHeading.text = getString(R.string.my_document_details)
        binding.profileHeader.tvSubHeading.changeTextColor(context)
        binding.profileHeader.ivPhoto.loadUrlUsingGlide(PrefsHelper.profileUrl)
        binding.rvDocuments.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = documentsAdapter
        }
        binding.profileHeader.tvNotificationsCount.visibility = if(PrefsHelper.notificationCount == "0")
            View.GONE else View.VISIBLE
//        binding.profileHeader.tvNotificationsCount.text = PrefsHelper.notificationCount
        if(PrefsHelper.notificationCount != "0" && PrefsHelper.notificationCount != "") {
            var notificationCount = PrefsHelper.notificationCount
            if (notificationCount!="" && notificationCount.toInt() > 100) {
                notificationCount = "99+"
            }
            binding.profileHeader.tvNotificationsCount.text =notificationCount
        }
        binding.profileHeader.ivPhoto.setOnClickListener {
            startActivity(Intent(requireContext(), MyNotificationActivity::class.java))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}