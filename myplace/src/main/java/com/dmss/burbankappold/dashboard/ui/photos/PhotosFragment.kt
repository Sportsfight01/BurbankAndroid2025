package com.dmss.burbankappold.dashboard.ui.photos

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.dmss.burbankappold.BaseFragment
import com.dmss.burbankappold.R
import com.dmss.burbankappold.dashboard.DashboardNewActivity
import com.dmss.burbankappold.dashboard.PrefsHelper
import com.dmss.burbankappold.dashboard.ui.sideMenu.MyNotificationActivity
import com.dmss.burbankappold.databinding.FragmentPhotosBinding
import com.dmss.burbankappold.network.ApiRepository
import com.dmss.burbankappold.utils.*
import com.google.gson.Gson
import com.google.gson.JsonParser
import common.AppController
import common.MyPlaceDataBase
import common.TransparentProgressDialog
import common.Utils
import models.MyDocOrPhotosDataSetQldOrSa
import models.photos.PhotoItem
import models.photos.PhotosData
import models.photos.PhotosDataItem
import org.json.JSONException

class PhotosFragment : BaseFragment() {

    private var _binding: FragmentPhotosBinding? = null
    lateinit var filterList :List<PhotosDataItem>
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var photosAdapter :  PhotosAdapter? =  null
    private var groupedList = listOf<List<PhotosDataItem>>()
    var pd : TransparentProgressDialog? = null
    private var photosCount : Int = 0
    var myPlaceDataBase: MyPlaceDataBase? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPhotosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myPlaceDataBase = MyPlaceDataBase(requireContext())

        if (photosAdapter == null){
            photosAdapter = PhotosAdapter {
                findNavController().navigate(
                    R.id.photoDetailsFragment,
                    bundleOf("Position" to it,
                    BundleKey.PHOTOS_COUNT to photosCount)

                )
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
//        println("AppConstants.photosList:: "+AppConstants.photosList.size)
        if (groupedList.isEmpty()){

            if (AppConstants.photosList.isEmpty()) {
                requestApiCall()}
            else {
                binding.tvSeeAllList.show()
                binding.tvSeeAll.visibility = View.GONE
                binding.tvSeeAllList.visibility = View.VISIBLE
                binding.tvSeeAllList.text=getString(R.string.see_all_photos)
                binding.profileHeader.tvSubHeading.text = getString(R.string.my_photos_details)
                 photosAdapter?.setPhotosList(AppConstants.photosList)
                binding.tvSeeAll.visibility = View.GONE
                binding.tvSeeAllList.visibility = View.VISIBLE
                binding.profileHeader.tvSubHeading.visibility = View.VISIBLE

                binding.tvSeeAllList.text=getString(R.string.see_all_photos)
            }
        }else{
            binding.tvSeeAllList.show()
        }
        binding.tvSeeAllList.setOnClickListener {
            findNavController().navigate(
                R.id.photoDetailsFragment,
                bundleOf(BundleKey.PHOTOS_COUNT to photosCount)
            )
        }
        binding.swiperefresh.setOnRefreshListener {
            if(groupedList.isEmpty()) {
                Utils.hideSwipeRefresh(binding.swiperefresh)
                requestApiCall()
            }else{
                Utils.hideSwipeRefresh(binding.swiperefresh)
            }
        }
    }

    private fun filterPhotosList(photosDataItem: List<PhotosDataItem>){
//        val oldCount = PrefsHelper.photosCount
//        println("filterPhotosList:: "+Gson().toJson(photosDataItem))

//        filterList = photosDataItem.filter { it.extension?.lowercase()?.trim() ==".jpg" || it.extension?.lowercase()?.trim() ==".png" }
        filterList = photosDataItem.filter { it.url!="" }

        println("filterList:: "+filterList.size)
        groupedList = filterList.groupBy { title -> title.title.split(" ")[0] }.map { it.value }
        photosAdapter?.setPhotosList(groupedList)
        AppConstants.photosList = groupedList as MutableList<List<PhotosDataItem>>
        AppConstants.savedphotosList = groupedList as MutableList<List<PhotosDataItem>>

        binding.tvSeeAll.show()
    }
/*
    private fun requestApiCall() {
        if(Utils.isNetworkAvailable(activity)) {
            pd = Utils.getProgress(requireActivity())
            ApiRepository.requestPhotosApi { isSuccess, photosData ->
                pd?.dismiss()
                if (isSuccess) {
                    photosData?.let { it ->
                        filterPhotosList(it)
                        val filterList = it.filter {
                            it.type.trim().lowercase() == "jpg" || it.type.trim()
                                .lowercase() == "png"
                        }
                        AppConstants.filterList = filterList as ArrayList<PhotosDataItem>
                        if (filterList.isEmpty()) {
                            binding.tvSeeAll.visibility = View.VISIBLE
                            binding.tvSeeAllList.visibility = View.GONE
                            binding.tvSeeAll.text = getString(R.string.No_recent_photos)
                            AppConstants.NoRecentPhotos = getString(R.string.No_recent_photos)
                            AppConstants.PhotosSubheader = getString(R.string.no_photos_details)
                            binding.profileHeader.tvSubHeading.text =
                                getString(R.string.no_photos_details)
                        } else {
                            binding.tvSeeAll.visibility = View.GONE
                            binding.tvSeeAllList.visibility = View.VISIBLE
                            binding.profileHeader.tvSubHeading.text =
                                getString(R.string.my_photos_details)
                        }
                        try {
                            val QldOrSaPhotosList = ArrayList<MyDocOrPhotosDataSetQldOrSa>()
                            filterList.forEach {
                                val json = Gson().toJson(it, PhotosDataItem::class.java)
                                val myDocumentsDataSetQldOrSa = MyDocOrPhotosDataSetQldOrSa(
                                    json,
                                    requireContext()
                                )
                                if (myDocumentsDataSetQldOrSa.type != null && myDocumentsDataSetQldOrSa.type.isNotEmpty() && myDocumentsDataSetQldOrSa.type.lowercase()
                                        .trim { it <= ' ' }
                                        .equals("jpg", ignoreCase = true)
                                ) {
                                    QldOrSaPhotosList.add(myDocumentsDataSetQldOrSa)
                                }
                            }
                            myPlaceDataBase!!.insertQldOrSaPhotos(
                                QldOrSaPhotosList,
                                AppController.controller.jobNumber,
                                AppController.controller.my_Place_Details.username,
                            )
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }
                } else {
                    binding.tvSeeAll.text = getString(R.string.see_all_photos)
                    binding.profileHeader.tvSubHeading.text = getString(R.string.my_photos_details)
                    context?.showErrorToast()
                }
            }
        }
    }
*/
private fun requestApiCall() {
    println("requestApiCall:: ")
    if(Utils.isNetworkAvailable(activity)) {
        pd = Utils.getProgress(requireActivity())
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

                val notesList=resjsonObjectObj.getAsJsonObject("photos").getAsJsonArray(getString(R.string.list))
                val photosData= Gson().fromJson(notesList, PhotosData::class.java)
                println("requestPhotosApi photosData122:: "+photosData)

                photosData?.let { it ->
                    filterPhotosList(it)

                    /*val filterList = it.filter {
                        it.extension?.trim()?.lowercase() == ".jpg" || it.extension?.trim()?.lowercase() == ".png" || it.extension?.trim()?.lowercase() == ".jpeg"
                    }*/
                    val filterList = it.filter {
                        it.url!=null && it.url!=""
                    }
                    AppConstants.filterList = filterList as ArrayList<PhotosDataItem>
                    if (filterList.isEmpty()) {
                        binding.tvSeeAll.visibility = View.VISIBLE
                        binding.tvSeeAllList.visibility = View.GONE
                        binding.tvSeeAll.text = getString(R.string.No_recent_photos)
                        AppConstants.NoRecentPhotos = getString(R.string.No_recent_photos)
                        AppConstants.PhotosSubheader = getString(R.string.no_photos_details)
                        binding.profileHeader.tvSubHeading.text =
                            getString(R.string.no_photos_details)
                    } else {
                        binding.tvSeeAll.visibility = View.GONE
                        binding.tvSeeAllList.visibility = View.VISIBLE
                        binding.profileHeader.tvSubHeading.text =
                            getString(R.string.my_photos_details)
                    }
                    try {
                        val QldOrSaPhotosList = ArrayList<MyDocOrPhotosDataSetQldOrSa>()
                        filterList.forEach {
                            val json = Gson().toJson(it, PhotosDataItem::class.java)
                            val myDocumentsDataSetQldOrSa = MyDocOrPhotosDataSetQldOrSa(
                                json,
                                requireContext()
                            )
                            if (myDocumentsDataSetQldOrSa.type != null && myDocumentsDataSetQldOrSa.type.isNotEmpty() && myDocumentsDataSetQldOrSa.type.lowercase()
                                    .trim { it <= ' ' }
                                    .equals("jpg", ignoreCase = true)
                            ) {
                                QldOrSaPhotosList.add(myDocumentsDataSetQldOrSa)
                            }
                        }
                        myPlaceDataBase!!.insertQldOrSaPhotos(
                            QldOrSaPhotosList,
                            AppController.controller.jobNumber,
                            AppController.controller.my_Place_Details.username,
                        )
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            } else {
                binding.tvSeeAll.text = getString(R.string.see_all_photos)
                binding.profileHeader.tvSubHeading.text = getString(R.string.my_photos_details)
                context?.showErrorToast()
            }
        }
    }
}


    private fun initViews() {
        binding.profileHeader.tvHeading.text = getString(R.string.my_photos)
        binding.profileHeader.tvHeading.changeTextColor(context)
        binding.profileHeader.tvSubHeading.changeTextColor(context)
        binding.profileHeader.ivPhoto.loadUrlUsingGlide(PrefsHelper.profileUrl)
        binding.rvPhotos.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            (layoutManager as LinearLayoutManager).reverseLayout = true
            (layoutManager as LinearLayoutManager).stackFromEnd = true
            adapter = photosAdapter
        }

        binding.profileHeader.tvNotificationsCount.visibility = if(PrefsHelper.notificationCount == "0" || PrefsHelper.notificationCount == "")
            View.GONE else View.VISIBLE
//        binding.profileHeader.tvNotificationsCount.text = PrefsHelper.notificationCount
        var notificationCount = PrefsHelper.notificationCount
        if (notificationCount!="" && notificationCount.toInt() > 100) {
            notificationCount = "99+"
        }
        binding.profileHeader.tvNotificationsCount.text =notificationCount

        binding.profileHeader.ivPhoto.setOnClickListener {
            startActivity(Intent(requireContext(), MyNotificationActivity::class.java))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}