package com.dmss.burbankappold.dashboard.ui.photos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dmss.burbankappold.R
import com.dmss.burbankappold.dashboard.DashboardNewActivity
import com.dmss.burbankappold.dashboard.PrefsHelper
import com.dmss.burbankappold.databinding.FragmentPhotoDetailsBinding
import com.dmss.burbankappold.utils.AppConstants
import com.dmss.burbankappold.utils.BundleKey
import com.dmss.burbankappold.utils.hide
import com.dmss.burbankappold.utils.show
import com.google.gson.Gson
import models.photos.PhotosDataItem


class PhotoDetailsFragment : Fragment() {
    private var _binding : FragmentPhotoDetailsBinding? = null
    private val binding get() = _binding!!
    private var photosList = listOf<List<PhotosDataItem>>()
    private var sortedPhotosList = ArrayList<List<PhotosDataItem>>()

    private var photosCount: Int = 0
    private var clickedPosition: Int = -1
    var previousPageClickedPosition=0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPhotoDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        photosCount = arguments?.getInt(BundleKey.PHOTOS_COUNT)?: 0
        clickedPosition = arguments?.getInt("Position", -1)?: -1
        previousPageClickedPosition=clickedPosition
//         previousPageClickedPosition=(AppConstants.photosList.size-1)-clickedPosition.toInt()
//        println("clickedPosition:: "+clickedPosition+"  AppConstants.photosList::"+AppConstants.photosList.size+" less:: "+previousPageClickedPosition)
       /* photosList = if (clickedPosition != -1){
            val dummyList = arrayListOf<List<PhotosDataItem>>()
            dummyList.add(AppConstants.photosList[clickedPosition])
            dummyList
        } else AppConstants.photosList*/
        println("clickedPosition:: $clickedPosition")
        var photosCountshared=PrefsHelper.photosCount
        photosList = AppConstants.photosList
        println("photosList:: "+Gson().toJson(photosList))
        var noOfPhotoscount=photosList.sumOf { it.size }
        photosCount=noOfPhotoscount-PrefsHelper.photosCount

        if(noOfPhotoscount>photosCountshared){

            PrefsHelper.photosCount = noOfPhotoscount
        }

    }
    private fun sortBydateList(documentsList : List<PhotosDataItem>):List<PhotosDataItem>{
        val sortedList = documentsList.sortedByDescending {

            it.docdate
        }
        return sortedList
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as DashboardNewActivity).initToolBarWithBackBackButton()
        binding.tvPhotoCount.text = getPhotosText(photosList.sumOf { it.size })
        if (photosCount>0) {
            binding.imgRed.show()
            binding.tvPhotoCountNew.show()
            binding.tvPhotoCountNew.text = "$photosCount New"
        }else{
            binding.imgRed.hide()
            binding.tvPhotoCountNew.hide()
        }
        binding.rvPhotosMain.apply {
            layoutManager = LinearLayoutManager(requireContext())
            (layoutManager as LinearLayoutManager).reverseLayout = true
            (layoutManager as LinearLayoutManager).stackFromEnd = true
            try {


                photosList.forEach {
                    var list = sortBydateList(it)
                    sortedPhotosList.add(list)
                }
                photosList = sortedPhotosList
                adapter = PhotosHeaderAdapter(photosList) {
                    findNavController().navigate(
                        R.id.nav_photo_view,
                        bundleOf(
                            BundleKey.IMAGE_URL to it.url,
                            BundleKey.IMAGE_FORMAT_TYPE to it.url,
                            BundleKey.DATE to it.docdate
                        )
                    )
                }
                layoutManager!!.scrollToPosition(previousPageClickedPosition)

            }catch (e:Exception){
            }
        }
    }

    private fun getPhotosText(count: Int): String{
        return if (count>1) "$count Photos" else "$count Photo"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        sortedPhotosList = ArrayList<List<PhotosDataItem>>()
    //        photosList = AppConstants.photosList

    }

}