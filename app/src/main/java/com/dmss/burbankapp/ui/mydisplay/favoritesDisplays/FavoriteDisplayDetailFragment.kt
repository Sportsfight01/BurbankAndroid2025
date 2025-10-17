package com.dmss.burbankapp.ui.mydisplay.favoritesDisplays

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.dmss.burbankapp.R
import com.dmss.burbankapp.data.api.ApiHelperImpl
import com.dmss.burbankapp.data.api.RetrofitBuilder
import com.dmss.burbankapp.data.apiUtils.Status
import com.dmss.burbankapp.data.apiUtils.ViewModelFactory
import com.dmss.burbankapp.data.local.CustomSharedPreferences
import com.dmss.burbankapp.data.local.entity.DatabaseBuilder
import com.dmss.burbankapp.data.local.entity.DatabaseHelperImpl
import com.dmss.burbankapp.data.model.DisplayDetailStaticModel
import com.dmss.burbankapp.data.model.FavoriteResponseModel
import com.dmss.burbankapp.data.model.HouseNameDetailByNameModel
import com.dmss.burbankapp.data.model.MyCollectionHnlModel
import com.dmss.burbankapp.databinding.FragmentFavoriteDisplayDetailBinding
import com.dmss.burbankapp.databinding.ListCollectionItemBinding
import com.dmss.burbankapp.ui.adapters.SlidingAdapter
import com.dmss.burbankapp.ui.designs.NewHomeQuizViewModel
import com.dmss.burbankapp.ui.enquireNow.EnquireNowActivity
import com.dmss.burbankapp.ui.main.MainActivity
import com.dmss.burbankapp.ui.view.CustomProgressDialog
import com.dmss.burbankapp.utils.AppConstants
import com.dmss.burbankapp.utils.AppUtils
import com.google.gson.JsonObject
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import common.AppController
import timber.log.Timber


class FavoriteDisplayDetailFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    lateinit var binding: FragmentFavoriteDisplayDetailBinding
    lateinit var listItemBinding: ListCollectionItemBinding
    lateinit var mainActivity: MainActivity
    var preferences: CustomSharedPreferences? = null
    lateinit var myHomeQuizViewModel: NewHomeQuizViewModel
    var isFromFavorite: Boolean? = null
    var isButtonEnabled: Boolean = false
    private var stateId: Int = -1
    private var userID: Int = -1
    lateinit var newHomeListModel: DisplayDetailStaticModel


    private lateinit var customSharedPreferences: CustomSharedPreferences
    private var customProgressDialog: CustomProgressDialog? = null
    var images: Array<Int> = arrayOf(
        R.drawable.rectangle_test,
        R.drawable.rectangle_test,
        R.drawable.rectangle_test,
        R.drawable.rectangle_test,
        R.drawable.rectangle_test,
        R.drawable.rectangle_test
    )
    lateinit var pageAdapter: PagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentFavoriteDisplayDetailBinding.inflate(layoutInflater, container, false)
        listItemBinding = ListCollectionItemBinding.bind(binding.root)
        return binding.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupViewModel()
        initViews()
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
        myHomeQuizViewModel.getFavorite().observe(viewLifecycleOwner, Observer { data ->
            when (data.status) {
                Status.SUCCESS -> {
                    customProgressDialog?.dismissProgress()
                    (activity as MainActivity).loadUserFavoritesDisplays()

                    var favoriteResponseModel: FavoriteResponseModel? = data.data

                    val toast = Toast.makeText(
                        requireContext(),
                        favoriteResponseModel?.message,
                        Toast.LENGTH_SHORT
                    )
                    toast.setGravity(Gravity.CENTER, 0, 0)
                    toast.show()


                    if (newHomeListModel.IsFav) {
                        listItemBinding.ivLike.setImageDrawable(
                            ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.fav_selected
                            )
                        )
                    } else
                        listItemBinding.ivLike.setImageDrawable(
                            ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.heart_bottom_svg
                            )
                        )

                    /*isFromFavorite?.let {
                        if (it) {
                            newHomeListModel.let {
                                val appEvent = AppEvent(
                                    AppEvent.UPDATE_HOMEDESIGNFAVORITE,
                                    Gson().toJson(newHomeListModel)
                                )
                                EventBus.getDefault().post(appEvent)
                            }

                        } else {
                            newHomeListModel.let {
                                val appEvent = AppEvent(
                                    AppEvent.UPDATE_FAVORITES_HOMEDESIGN,
                                    newHomeListModel,
                                    newHomeListModel.IsFav, true
                                )
                                EventBus.getDefault().post(appEvent)
                            }
                        }


                    }*/


                }
                Status.LOADING -> {
                    customProgressDialog?.showProgress()
                }
                Status.ERROR -> {
                    customProgressDialog?.dismissProgress()
                }
            }

        })
        myHomeQuizViewModel.getHouseNameByDetailLiveData()
            .observe(viewLifecycleOwner, Observer
            {
                when (it.status) {
                    Status.SUCCESS -> {
                        customProgressDialog?.dismissProgress()
                        val houseNameDetailByNameModel: HouseNameDetailByNameModel? = it.data
                        if (houseNameDetailByNameModel != null && houseNameDetailByNameModel.status) {
                            val floorPlanImageUrl =
                                houseNameDetailByNameModel.isHousesModel.homePlanModel.FloorPlanImageURL_Mobile

                            Timber.e("Image after Url--- $floorPlanImageUrl")
                            Timber.e("Image before Url--- ${houseNameDetailByNameModel.isHousesModel.homePlanModel.FloorPlanImageURL_Mobile}")
                            listItemBinding.tvPlace.text =
                                (houseNameDetailByNameModel.isHousesModel.HouseName + " " + houseNameDetailByNameModel.isHousesModel.HouseSize)
                            val price: String = AppUtils.getCommasForPriceValue(houseNameDetailByNameModel.isHousesModel.Price.toInt())
                            listItemBinding.tvPrice.text = ("$$price")
                            listItemBinding.tvBath.text = (houseNameDetailByNameModel.isHousesModel.BathRooms)
                            listItemBinding.tvBath.text = (houseNameDetailByNameModel.isHousesModel.BathRooms)
                            listItemBinding.tvBed.text = (houseNameDetailByNameModel.isHousesModel.BedRooms)
                            listItemBinding.tvcar.text = (houseNameDetailByNameModel.isHousesModel.CarSpace)

                            listItemBinding.tvWidth.text =
                                ("${houseNameDetailByNameModel.isHousesModel.MinLotWidth} m")
                            if(floorPlanImageUrl!=null && floorPlanImageUrl!="") {
                                Picasso.get()
                                    .load(floorPlanImageUrl)
                                    .into(binding.ivFloorpaln, object : Callback {
                                        override fun onSuccess() {
                                            binding.floorplanLabel.visibility = View.GONE

                                        }

                                        override fun onError(e: java.lang.Exception?) {
                                            //do smth when there is picture loading error
                                        }
                                    })
                            }

                            if (houseNameDetailByNameModel.isHousesModel.facadeLargeImageUrls.size > 0) {
                                pageAdapter = SlidingAdapter(
                                    requireContext(),
                                    houseNameDetailByNameModel.isHousesModel.facadeLargeImageUrls

                                )
                                binding.viewPager.adapter = pageAdapter
                                binding.dot.setViewPager(binding.viewPager)

                                binding.tvFacade.text =
                                    (houseNameDetailByNameModel.isHousesModel.facade + " Facade")
                            }

                            if (houseNameDetailByNameModel.isHousesModel.homePlanModel.Visualisation) {
                                binding.llMyplace.visibility = View.VISIBLE
                            } else {
                                binding.llMyplace.visibility = View.INVISIBLE
                            }
                        }
                        val facadeNames = arrayListOf<String>()
                        houseNameDetailByNameModel!!.isHousesModel.facadeLargeImageUrls.forEach {
                            val fileName = URLUtil.guessFileName(it, null, null)
                            if(fileName.contains("_")){
                                val fName = fileName.replace(".jpg","")
                                    .replace(".png","")
                                    .replace(".jpeg","").split("_")
                                if(fName.size>1){
                                    facadeNames.add("${fName[1]} Facade")
                                }
                            }
                        }
                        if (facadeNames.isNotEmpty()) {
                            binding.tvFacade.text = facadeNames[0]
                        }
                        binding.viewPager.addOnPageChangeListener(object :
                            ViewPager.OnPageChangeListener {
                            override fun onPageScrolled(
                                position: Int,
                                positionOffset: Float,
                                positionOffsetPixels: Int
                            ) {

                            }

                            override fun onPageSelected(position: Int) {
                                if (facadeNames != null) {
                                    if (facadeNames.isNotEmpty()) {
                                        if (facadeNames.size >= position) {
                                            binding.tvFacade.text = facadeNames[position]
                                        }
                                    }


                                }
                            }

                            override fun onPageScrollStateChanged(state: Int) {

                            }

                        })
                    }
                    Status.LOADING -> {
                        customProgressDialog?.showProgress()
                    }
                    Status.ERROR -> {
                        customProgressDialog?.dismissProgress()
                    }
                }
            })

        myHomeQuizViewModel.getMyCollectionHnlLiveData().observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    customProgressDialog?.dismissProgress()
                    var regionsData: MyCollectionHnlModel? = it.data
                    if (regionsData != null && regionsData.hnlQuizList.size > 0) {
                        val bundle = Bundle()
                        bundle.putParcelable(AppConstants.NEWHOMELISTMODEL, newHomeListModel)
                        loadFragment(FavoriteHomeAndLandFragment(), bundle)
                    } else {
                        AppUtils.showCustomCenterToast(requireContext(), "No Packages found")
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


        preferences = CustomSharedPreferences(requireContext())
        listItemBinding.rlPlace.visibility = View.GONE
        customProgressDialog = CustomProgressDialog(requireContext())
        arguments?.getParcelable<DisplayDetailStaticModel>(AppConstants.USER_FAVORITE_DISPLAY_MODEL)
            ?.let {
                newHomeListModel = it
            }
        arguments?.getBoolean("isFavorite", false)?.let {
            isFromFavorite = it
        }

        customSharedPreferences = CustomSharedPreferences(AppController.getInstance())
        stateId = customSharedPreferences.getStateID()
        userID = customSharedPreferences.getUserId()

        binding.llMyplace.setOnClickListener {

            var stateName = customSharedPreferences.getSelectedState().toString()
            if(stateName.contains("Wales")){
                stateName = "nsw"
            }else if(stateName.contains("South")){
                stateName = "South-Australia"
            }
            var url =
                AppConstants.MYPLACETHREED_BASE + "/${stateName.lowercase()}/myplace3dmobile/housename.${newHomeListModel.HouseName};housesize.${newHomeListModel.HouseSize}/"

            if (!url.startsWith("http://") && !url.startsWith("https://"))
                url = "http://$url";

            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(browserIntent)

            Timber.e("MyPlace3d URL ::----->${url}")


        }

        if (newHomeListModel.HouseName.isNotBlank()) {
            myHomeQuizViewModel.fetchHouseNameDetailApi(
                newHomeListModel.HouseSize,
                newHomeListModel.HouseName, stateId
            )
        }
        binding.enquiry.setOnClickListener {
            if (newHomeListModel.HouseName.isNotBlank()) {
                startActivity(
                    Intent(
                        requireContext(),
                        EnquireNowActivity::class.java
                    ).putExtra("HOSUENAME", newHomeListModel.HouseName)
                        .putExtra("HOUSESIZE", newHomeListModel.HouseSize!!)
                        .putExtra("ADDRESS", "")

                )
            }
        }
        binding.llBack.setOnClickListener {
            activity?.onBackPressed()
//            binding.tvtool.text = "See one of our display homes"


        }
        binding.llBack.setOnClickListener {
            activity?.onBackPressed()
//            binding.tvTool.text = "See one of our display homes"

        }
        binding.tvTool.text =
            (newHomeListModel.HouseName + " " + newHomeListModel.HouseSize)
        /*  binding.tvTool.text =getString(R.string.See_one_of_our_display_homes)*/
        listItemBinding.tvPlace.text =
            (newHomeListModel.HouseName + " " + newHomeListModel.HouseSize)

        val price: String =
            AppUtils.getCommasForPriceValue(newHomeListModel.HousePrice.toInt())

        listItemBinding.tvPrice.text = ("$$price")
        listItemBinding.tvPrice.visibility=View.VISIBLE
        listItemBinding.tvBath.text = (newHomeListModel.BathRooms.toString())
        listItemBinding.tvBed.text = (newHomeListModel.BedRooms.toString())
        listItemBinding.tvcar.text = (newHomeListModel.CarSpace.toString())
        listItemBinding.tvWidth.text = String.format("%.1f m", newHomeListModel.MinLotWidth)

        newHomeListModel.IsFav.let {
            if (it) {
                listItemBinding.ivLike.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.fav_selected
                    )
                )
                isButtonEnabled = false
                binding.savedesign.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.rectangle_disable_button)

            } else {
                listItemBinding.ivLike.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.heart_bottom_svg
                    )
                )
                binding.savedesign.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.rectangel_black)
                isButtonEnabled = true
            }

        }


        binding.savedesign.setOnClickListener {
            if (isButtonEnabled) {
                var isUserLoggedIn = customSharedPreferences.getUserLogin()
                if (isUserLoggedIn) {
                    listItemBinding.ivLike.performClick()
                } else {
                    AppUtils.showPleaseLoginDialog(
                        requireContext(),
                        requireActivity(),
                        "Please login to add favourites"
                    )
                }
            } else {
                AppUtils.showCustomCenterToast(requireContext(), "Design saved in favourites")
            }

        }
        listItemBinding.ivLike.setOnClickListener {
            var isUserLoggedIn = customSharedPreferences.getUserLogin()
            if (isUserLoggedIn) {
                newHomeListModel.IsFav.let {
                    if (it) {
                        listItemBinding.ivLike.setImageDrawable(
                            ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.heart_bottom_svg
                            )
                        )
                        binding.savedesign.background =
                            ContextCompat.getDrawable(requireContext(), R.drawable.rectangel_black)
                        isButtonEnabled = true


                    } else {
                        listItemBinding.ivLike.setImageDrawable(
                            ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.fav_selected
                            )
                        )
                        binding.savedesign.background = ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.rectangle_disable_button
                        )
                        isButtonEnabled = false


                    }
                    favoriteApiCall(newHomeListModel)

                }
            } else {
                AppUtils.showPleaseLoginDialog(
                    requireContext(),
                    requireActivity(),
                    "Please login to add favourites"
                )
            }


        }




        binding.llHomeland.setOnClickListener {
            newHomeListModel.HouseName.let {
                myHomeQuizViewModel.getMyCollectionHomeAndLand(stateId, it)
            }
        }


    }

    fun loadFragment(fragment: Fragment, bundle: Bundle) {
        val transaction: FragmentTransaction =
            activity?.supportFragmentManager!!.beginTransaction()
        if (fragment.isAdded) {
            return
        }

        transaction.add(R.id.fl_content, fragment)
        fragment.arguments = bundle
        transaction.addToBackStack(null)
        transaction.commit()
    }





    private fun favoriteApiCall(newHomeListModel: DisplayDetailStaticModel) {
        var jsonObject = JsonObject()
        jsonObject.addProperty("TypeId", 3)
        jsonObject.addProperty("UserId", preferences?.getUserId())
        jsonObject.addProperty("HouseId", newHomeListModel.Id)
        jsonObject.addProperty("StateId", preferences?.getStateID())
        jsonObject.addProperty("isfavourite", !newHomeListModel.IsFav)
        this.newHomeListModel.IsFav = !newHomeListModel.IsFav

        myHomeQuizViewModel.setFavorite(jsonObject)
    }


}