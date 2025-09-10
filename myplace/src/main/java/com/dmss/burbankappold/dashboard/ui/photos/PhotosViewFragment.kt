package com.dmss.burbankappold.dashboard.ui.photos

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.dmss.burbankappold.dashboard.DashboardNewActivity
import com.dmss.burbankappold.databinding.FragmentPhotosViewBinding
import com.dmss.burbankappold.utils.*
import com.google.gson.Gson
import common.AppController
import models.photos.PhotosDataItem

class PhotosViewFragment : Fragment() {
    private var _binding: FragmentPhotosViewBinding? = null
    private val binding get() = _binding!!
    lateinit var imageUrl:List<String>
    lateinit var dates:List<String>
    lateinit var titles:List<String>
    var selectedimagePostion=0

    private val photoUrl: String? by lazy {
        arguments?.getString(BundleKey.IMAGE_URL)
    }
    private val imageFormatType: String? by lazy {
        arguments?.getString(BundleKey.IMAGE_FORMAT_TYPE)
    }
    private val date: String? by lazy {
        arguments?.getString(BundleKey.DATE)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentPhotosViewBinding.inflate(layoutInflater,container, false)
        return binding.root
    }
    fun findIndex(arr: List<String>, item: String): Int {
        for (i in arr.indices)
        {
            if (arr[i] == item) {
                return i
            }
        }
        return -1
    }
    fun swipeListner() {
        binding.ivPhotos.setOnTouchListener(object :
            OnSwipeTouchListener(AppController.getInstance()) {


            @SuppressLint("SuspiciousIndentation")
            override fun onSwipeRight() {
//                Toast.makeText(AppController.getInstance(),"onSwipeRight", Toast.LENGTH_SHORT).show()
                println("selectedimagePostion Right:: "+selectedimagePostion+" imageUrl:: "+imageUrl.size)

                if(selectedimagePostion>-1)
                showimagePhot(selectedimagePostion-1)

            }

            override fun onSwipeLeft() {
//                Toast.makeText(AppController.getInstance(),"onSwipeLeft", Toast.LENGTH_SHORT).show()
                println("selectedimagePostion Left:: "+selectedimagePostion+" imageUrl:: "+imageUrl.size)
                if(selectedimagePostion<imageUrl.size)

                showimagePhot(selectedimagePostion+1)
            }

//            override fun onSwipeBottom() {
////                Toast.makeText(AppController.getInstance(),"onSwipeBottom", Toast.LENGTH_SHORT).show()
//
//            }
//            override fun onSwipeTop() {
//            }
        })
    }
    fun showimagePhot(postion:Int){
        selectedimagePostion=postion
        if(selectedimagePostion==0){
            binding.leftArrow.visibility=View.GONE
            binding.rightArrow.visibility=View.VISIBLE
        }
        else if(selectedimagePostion==imageUrl.size-1){
            binding.leftArrow.visibility=View.VISIBLE
            binding.rightArrow.visibility=View.GONE
        }
        else if(selectedimagePostion>0 && selectedimagePostion<imageUrl.size-1){

            binding.leftArrow.visibility=View.VISIBLE
            binding.rightArrow.visibility=View.VISIBLE
        }
        if(imageUrl.size==1){
            binding.leftArrow.visibility=View.GONE
            binding.rightArrow.visibility=View.GONE
        }
        binding.date.text=dates[postion].convertDateFormat(PHOTOS_DATE_FORMAT)
        var title=titles[postion].lowercase()
        binding.title.text=title.sentenceFirstLetterCaps(title).toString()
        binding.progressbar.show()

        Glide.with(binding.root.context)
            .load(imageUrl[postion]?.replace("=0", ""))
            .listener(object : RequestListener<Drawable?> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable?>?,
                    isFirstResource: Boolean
                ): Boolean {
                    binding.progressbar.hide()
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable?>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    try {
                        if (binding.progressbar != null) {
                            binding.progressbar.hide()
                        }
                    }catch (e:Exception){
                        e.printStackTrace()
/*                        if (binding.progressbar != null) {
                            binding.progressbar.hide()
                        }*/
                    }
                    return false
                }

            })
            .into(binding.ivPhotos)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var photosList = sortBydateList(AppConstants.photosList.flatten())
        println("photosList:: "+AppConstants.photosList.size)
//        if(AppConstants.photosList.size==1) {
//            imageUrl = photosList.map { it.url }.toList()
//            dates= photosList.map { it.docdate }.toList()
//            titles= photosList.map { it.title }.toList()
//        }else{
            imageUrl = photosList.map { it.url }.toList()
            dates= photosList.map { it.metaData.createdOn }.toList()
            titles= photosList.map { it.title }.toList()
//        }

//        var selectedimagePostion=imageUrl.g
        imageUrl.forEachIndexed{index, item ->
            if(item==photoUrl) {
                println("index = $index, item = $item ")
                selectedimagePostion=index
            }
        }
        showimagePhot(selectedimagePostion)
//        var selectedimagePostion=findIndex(imageUrl, photoUrl!!)
       var sliderAdapter = SliderAdapter( imageUrl,dates,titles,imageFormatType!!)
        // on below line we are setting auto cycle direction
        // for our slider view from left to right.
    /*    binding.sliderView.autoCycleDirection = SliderView.LAYOUT_DIRECTION_LTR
        // on below line we are setting adapter for our slider.
        binding.sliderView.setSliderAdapter(sliderAdapter)
        binding.sliderView.onPageSelected(selectedimagePostion)
        // on below line we are setting scroll time
        // in seconds for our slider view.
//        binding.sliderView.scrollTimeInSec = 3
        // on below line we are setting auto cycle
        // to true to auto slide our items.
//        binding.sliderView.isAutoCycle = false
//        binding.sliderView.indicatorSelectedColor
        binding.sliderView.indicatorSelectedColor = Color.WHITE;
        binding.sliderView.indicatorUnselectedColor = Color.WHITE;
        binding.sliderView.currentPagePosition=selectedimagePostion
        binding.sliderView.setIndicatorEnabled(false)
        println("currentPagePosition:: "+binding.sliderView.currentPagePosition+"  selectedimagePostion:: "+selectedimagePostion)
*/

        binding.leftArrow.setOnClickListener {
            showimagePhot(selectedimagePostion-1)
        }
        binding.rightArrow.setOnClickListener {
            showimagePhot(selectedimagePostion+1)
        }
        // on below line we are calling start
        // auto cycle to start our cycle.
//        binding.sliderView.startAutoCycle()
        swipeListner()
        (activity as DashboardNewActivity).initToolBarWithBackBackButton(date?.convertDateFormat(PHOTOS_DATE_FORMAT))
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
    private fun sortBydateList(documentsList : List<PhotosDataItem>):List<PhotosDataItem>{
        val sortedList = documentsList.sortedByDescending {  it.metaData.createdOn }
        return sortedList
    }
}