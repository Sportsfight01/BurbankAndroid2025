package com.dmss.burbankapp.ui.homeandland

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Priority
import com.bumptech.glide.request.RequestOptions
import com.dmss.burbankapp.R
import com.dmss.burbankapp.data.api.ApiHelperImpl
import com.dmss.burbankapp.data.api.RetrofitBuilder
import com.dmss.burbankapp.data.apiUtils.Status
import com.dmss.burbankapp.data.apiUtils.ViewModelFactory
import com.dmss.burbankapp.data.local.CustomSharedPreferences
import com.dmss.burbankapp.data.local.entity.DatabaseBuilder
import com.dmss.burbankapp.data.local.entity.DatabaseHelperImpl
import com.dmss.burbankapp.data.model.FavoriteResponseModel
import com.dmss.burbankapp.data.model.HnLQuizPackageModel
import com.dmss.burbankapp.databinding.FragmentHomeLandFullScreenBinding
import com.dmss.burbankapp.databinding.ProfileWithBadgeBinding
import com.dmss.burbankapp.ui.designs.MyCollectionHomeAndLandFragment
import com.dmss.burbankapp.ui.designs.NewHomeQuizViewModel
import com.dmss.burbankapp.ui.enquireNow.EnquireNowActivity
import com.dmss.burbankapp.ui.homeandlandregions.HomeAndLandViewModel
import com.dmss.burbankapp.ui.main.MainActivity
import com.dmss.burbankapp.ui.mydisplay.DisplayToolbarViewModel
import com.dmss.burbankapp.ui.mydisplay.MyDisplayHomeAndLandFragment
import com.dmss.burbankapp.ui.view.CustomProgressDialog
import com.dmss.burbankapp.ui.view.GlideImageLoader
import com.dmss.burbankapp.utils.AppConstants
import com.dmss.burbankapp.utils.AppUtils
import com.dmss.burbankapp.utils.customviews.AppEvent
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import common.AppController
import org.greenrobot.eventbus.EventBus
import timber.log.Timber


class HomeLandFullScreenFragment : Fragment(), OnMapReadyCallback,
    GoogleMap.OnMapClickListener, GoogleMap.OnMarkerClickListener {
    private val ZOOM_LEVEL = 15f
    var latLong: LatLng? = null

    lateinit var homeLandViewModel: HomeAndLandViewModel
    lateinit var myHomeQuizViewModel: NewHomeQuizViewModel
    lateinit var binding: FragmentHomeLandFullScreenBinding
    var hnLQuizPackageModel: HnLQuizPackageModel? = null
    private var options: RequestOptions? = null
    var customProgressDialog: CustomProgressDialog? = null
    private lateinit var customSharedPreferences: CustomSharedPreferences
    private var isFavoriteDetails: Boolean? = null
    private var currenUser: Boolean? = null
    var classfrom: String?=null

    var isButtonEnabled: Boolean = false

    private var map: GoogleMap? = null
    var mapFragment: SupportMapFragment? = null
    private lateinit var displayToolbarViewModel: DisplayToolbarViewModel
    lateinit var profileWithBadgeBinding: ProfileWithBadgeBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeLandFullScreenBinding.inflate(inflater, container, false)
        profileWithBadgeBinding = ProfileWithBadgeBinding.bind(binding.root)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isFavoriteDetails = arguments?.getBoolean("ISFROMFAVORITE")
        currenUser = arguments?.getBoolean("ISCURRENTUSER")

        classfrom = arguments?.getString("classfrom")

        hnLQuizPackageModel = arguments?.getParcelable("HNLQUIZMODEL")

        mapFragment =
            childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?

        binding.ivBack.setOnClickListener {
            requireActivity().onBackPressed()
        }


        setUpViewModel()
        initView()

    }

    private fun convertGoogleMapIntoImageView() {
        map?.setOnMapLoadedCallback {
            map?.snapshot { bitmap ->
                binding.transparentImage.setImageBitmap(bitmap)
            }
        }
    }

    private fun setUpViewModel() {
        homeLandViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(
                ApiHelperImpl(RetrofitBuilder.apiService),
                DatabaseHelperImpl(DatabaseBuilder.getInstance(AppController.getInstance()))
            )
        ).get(HomeAndLandViewModel::class.java)

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
        homeLandViewModel.getFavorite().observe(viewLifecycleOwner, Observer { data ->
            when (data.status) {
                Status.SUCCESS -> {
                    customProgressDialog?.dismissProgress()
                    (activity as MainActivity).loadUserFavoritesHomeLand()

                    var favoriteResponseModel: FavoriteResponseModel? = data.data

                  /*  val toast = favoriteResponseModel?.message?.let {
                        ToastHandler.getToastInstance(
                            requireContext(),
                            it,
                            Toast.LENGTH_SHORT
                        )
                    }
                    toast?.setGravity(Gravity.CENTER, 0, 0)
                    toast?.show()*/
                    Toast.makeText(requireContext(),favoriteResponseModel?.message,Toast.LENGTH_SHORT).show()
                    hnLQuizPackageModel.let {
                        if (isFavoriteDetails == true) {
                            if (it != null) {
                                if (it.isFav) {
                                    binding.ivHeart.setImageDrawable(
                                        ContextCompat.getDrawable(
                                            requireContext(),
                                            R.drawable.fav_selected
                                        )
                                    )
                                } else {
                                    binding.ivHeart.setImageDrawable(
                                        ContextCompat.getDrawable(
                                            requireContext(),
                                            R.drawable.heart_bottom_svg
                                        )
                                    )


                                }
                            }
                            val favoriteApp = AppEvent(
                                AppEvent.UPDATE_HOMELANDFAVORITE,
                                Gson().toJson(it)
                            )
                            EventBus.getDefault().post(favoriteApp)

                        } else {
                            if (it != null) {
                                Timber.e("HomeLandDetails Favorite ${it.isFav}")
                            }
                            if (it != null) {
                                if (it.isFav) {
                                    binding.ivHeart.setImageDrawable(
                                        ContextCompat.getDrawable(
                                            requireContext(),
                                            R.drawable.fav_selected
                                        )
                                    )
                                } else {
                                    binding.ivHeart.setImageDrawable(
                                        ContextCompat.getDrawable(
                                            requireContext(),
                                            R.drawable.heart_bottom_svg
                                        )
                                    )
                                }

                                val appEvent = AppEvent(
                                    AppEvent.UPDATE_FAVORITES,
                                    hnLQuizPackageModel,
                                    hnLQuizPackageModel?.isFav
                                )
                                EventBus.getDefault().post(appEvent)
                            }

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

        homeLandViewModel.getHomeAndLandPackageDetailLiveData()
            .observe(viewLifecycleOwner, Observer {
                when (it.status) {
                    Status.SUCCESS -> {
                        customProgressDialog?.dismissProgress()
                        val homeAndLandPackageDetailModel = it.data
                        val floorPlanImage =
                            homeAndLandPackageDetailModel?.getpackagebyId?.FloorPlanImageURL_Mobile

                        Timber.e("floor plan image : -----$floorPlanImage")
                        if (floorPlanImage?.isNotEmpty() == true) {
                            Picasso.get()
                                .load(floorPlanImage?.trim())
                                .into(binding.ivFloorpaln, object : Callback {
                                    override fun onSuccess() {
                                        binding.floorplanLabel.visibility = View.GONE

                                    }

                                    override fun onError(e: java.lang.Exception?) {
                                        binding.floorplanLabel.visibility = View.GONE
                                        //do smth when there is picture loading error
                                    }
                                })
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

    private fun profileNotificationCountView(notificationCount:Int){
        if(notificationCount==0){
            profileWithBadgeBinding.profileNotification.visibility=View.GONE
        }else{
            profileWithBadgeBinding.profileNotification.visibility=View.VISIBLE
        }
    }
    private fun initView() {
        displayToolbarViewModel = ViewModelProviders.of(requireActivity())[DisplayToolbarViewModel::class.java]
        profileWithBadgeBinding.profileNotification.text=AppConstants.TotalMyFavs.toString()
        profileNotificationCountView(AppConstants.TotalMyFavs)
        displayToolbarViewModel.favouritesCount.observe(viewLifecycleOwner){
            profileWithBadgeBinding.profileNotification.text = it.toString()
            profileNotificationCountView(it)

        }
        binding.ivBack.setOnClickListener {
            activity?.onBackPressed()
        }
        if(classfrom==MyDisplayHomeAndLandFragment::class.simpleName){
            binding.rlHeader.visibility=View.GONE
        }

        customSharedPreferences = CustomSharedPreferences(requireContext())
        customProgressDialog = CustomProgressDialog(requireContext())
        options = RequestOptions()
            .centerCrop()
            .priority(Priority.HIGH)

        var isUserLoggedIn = customSharedPreferences.getUserLogin()
        if (!isUserLoggedIn) {
            profileWithBadgeBinding.tvFavorites.background = resources.getDrawable(R.drawable.disable_rectangle_new)
            profileWithBadgeBinding.profileNotification.visibility=View.GONE
        }
        hnLQuizPackageModel?.let {

            it.price?.let { price ->

                val priceValue: String =
                    AppUtils.getCommasForPriceValue(price.toInt())
                binding.tvPrice.text = ("$$priceValue")
            }


        }



        if (currenUser == false) {
            binding.ivHeart.visibility = View.INVISIBLE
        }

        if (isFavoriteDetails == true) {
            binding.ivHeart.setImageDrawable(
                ContextCompat.getDrawable(
                    AppController.getInstance(),
                    R.drawable.fav_selected
                )
            )
        }
        AppConstants.displayHomeSubHeader= (hnLQuizPackageModel?.houseName + " " + hnLQuizPackageModel?.houseSize)

        binding.tvTool.text =
            (hnLQuizPackageModel?.houseName + " " + hnLQuizPackageModel?.houseSize)
        binding.tvHouseName.text =
            (hnLQuizPackageModel?.houseName + " " + hnLQuizPackageModel?.houseSize)
        binding.tvAddress.text = hnLQuizPackageModel?.address

        binding.tvbedrooms.text = hnLQuizPackageModel?.bedrooms.toString()
        binding.tvbathrooms.text = hnLQuizPackageModel?.bathrooms.toString()
        binding.tvcars.text = hnLQuizPackageModel?.carspace.toString()

        if (hnLQuizPackageModel?.FacadePermanentUrl != null) {
            hnLQuizPackageModel?.FacadePermanentUrl?.let { floorImageApiUrl ->
                var placePicture: String? = floorImageApiUrl
                if (hnLQuizPackageModel?.FacadePermanentUrl?.contains("~")!!) {
                    placePicture = floorImageApiUrl.replace("~", "")
                }
                placePicture = "${AppConstants.PROFILEPIC_BASE}${placePicture}"

                GlideImageLoader(binding.ivPlace, binding.progressPlace).load(placePicture, options)
            }
        }else binding.progressPlace.visibility = View.GONE




        binding.tvHouseName.text =
            (hnLQuizPackageModel?.houseName + " " + hnLQuizPackageModel?.houseSize)
        binding.tvFacade.text = (hnLQuizPackageModel?.Facade + " Facade")
        binding.tvAddress.text = hnLQuizPackageModel?.address

        binding.tvbedrooms.text = hnLQuizPackageModel?.bedrooms.toString()
        binding.tvbathrooms.text = hnLQuizPackageModel?.bathrooms.toString()
        binding.tvcars.text = hnLQuizPackageModel?.carspace.toString()

        hnLQuizPackageModel?.landSize?.let {
            val ms2 = String.format(
                resources.getString(R.string.ms),
                it.toInt()
            )
            binding.tvLandunit.text = Html.fromHtml(ms2)
        }






        hnLQuizPackageModel?.isFav.let {

            if (isFavoriteDetails == true) {

                if (it == true) {
                    binding.ivHeart.setImageDrawable(
                        ContextCompat.getDrawable(
                            AppController.getInstance(),
                            R.drawable.heart_bottom_svg
                        )
                    )

                    binding.savedesign.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.rectangel_skip)
                    isButtonEnabled = true


                } else {
                    binding.ivHeart.setImageDrawable(
                        ContextCompat.getDrawable(
                            AppController.getInstance(),
                            R.drawable.fav_selected
                        )
                    )

                    binding.savedesign.background = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.rectangle_disable_button
                    )
                    isButtonEnabled = false
                }

            } else {
                if (it == true) {
                    binding.ivHeart.setImageDrawable(
                        ContextCompat.getDrawable(
                            AppController.getInstance(),
                            R.drawable.fav_selected
                        )
                    )

                    binding.savedesign.background = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.rectangle_disable_button
                    )
                    isButtonEnabled = false
                } else {
                    binding.ivHeart.setImageDrawable(
                        ContextCompat.getDrawable(
                            AppController.getInstance(),
                            R.drawable.heart_bottom_svg
                        )
                    )

                    binding.savedesign.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.rectangel_skip)
                    isButtonEnabled = true
                }
            }


        }
        binding.savedesign.setOnClickListener {
            myHomeQuizViewModel.updateFavorite.postValue(true)
            Timber.e("Saved design is Clickble----" + binding.savedesign.isClickable)
            if (isButtonEnabled) {
                var isUserLoggedIn = customSharedPreferences.getUserLogin()
                if (isUserLoggedIn) {
                    binding.ivHeart.performClick()
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
        binding.tvEnquiry.setOnClickListener {

            var houseName = (hnLQuizPackageModel?.houseName + " " + hnLQuizPackageModel?.houseSize)
            var packageId = hnLQuizPackageModel
            /*if (hnLQuizPackageModel != null) {*/
                startActivity(
                    Intent(
                        requireContext(),
                        EnquireNowActivity::class.java
                    ).putExtra("HOSUENAME", hnLQuizPackageModel?.houseName)
                        .putExtra("HOUSESIZE", hnLQuizPackageModel?.houseSize)
                        .putExtra("ADDRESS", hnLQuizPackageModel?.address)
                        .putExtra("FROM", "homeland")
                        .putExtra("PACKAGEID", (hnLQuizPackageModel?.packageId).toString())
                        .putExtra("PACKAGE_LANDBANK_ID", (hnLQuizPackageModel?.packageIdLandBank).toString())
                )
            /*}*/
        }
        hnLQuizPackageModel?.packageIdLandBank.let {
            if (it != null) {
                getHomeLandPackage(it)
            }
        }
        binding.ivHeart.setOnClickListener {
            myHomeQuizViewModel.updateFavorite.postValue(true)
            var isUserLoggedIn = customSharedPreferences.getUserLogin()
            if (isUserLoggedIn) {
                hnLQuizPackageModel?.isFav.let {
                    if (isFavoriteDetails == true) {
                        if (it == true) {
                            binding.ivHeart.setImageDrawable(
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

                        } else {
                            binding.ivHeart.setImageDrawable(
                                ContextCompat.getDrawable(
                                    requireContext(),
                                    R.drawable.heart_bottom_svg
                                )
                            )
                            binding.savedesign.background =
                                ContextCompat.getDrawable(
                                    requireContext(),
                                    R.drawable.rectangel_skip
                                )
                            isButtonEnabled = true


                        }
                    } else {
                        if (it == true) {
                            binding.ivHeart.setImageDrawable(
                                ContextCompat.getDrawable(
                                    requireContext(),
                                    R.drawable.heart_bottom_svg
                                )
                            )
                            binding.savedesign.background =
                                ContextCompat.getDrawable(
                                    requireContext(),
                                    R.drawable.rectangel_skip
                                )
                            isButtonEnabled = true


                        } else {
                            binding.ivHeart.setImageDrawable(
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
                    }

                    hnLQuizPackageModel?.let { model ->
                        setFavoriteOrUnFavoriteApi(model)
                    }


                }

            } else {
                AppUtils.showPleaseLoginDialog(
                    requireContext(),
                    requireActivity(),
                    "Please login to add favourites"
                )
            }


        }

        hnLQuizPackageModel?.let { quizPackageModel ->
            quizPackageModel.latitude?.let { latitude ->
                if (latitude != "null") {
                    quizPackageModel.longitude?.let { longitude ->
                        if (longitude != "null") {
                            if (latitude.isNotEmpty() && longitude.isNotEmpty()) {
                                val lat = latitude.trim().toDouble()
                                val long = longitude.trim().toDouble()
                                latLong = LatLng(lat, long)
                                mapFragment?.getMapAsync(this)
                            }

                        }

                    }
                }

            }


        }


    }

    private fun getHomeLandPackage(packageId: Int) {
        homeLandViewModel.getHomeAndLandPackageDetailApi(packageId)
    }

    fun loadFragment(fragment: Fragment) {
        val transaction: FragmentTransaction = activity?.supportFragmentManager!!.beginTransaction()
        if (fragment.isAdded) {
            return
        }
        transaction.add(R.id.fl_content, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        googleMap ?: return

        googleMap.setOnMapClickListener(this)
        googleMap.setOnMarkerClickListener(this)
        map = googleMap


        latLong?.let {
            googleMap.addMarker(
                MarkerOptions().position(it)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.location))
            )
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(it, ZOOM_LEVEL))

        }

        convertGoogleMapIntoImageView()
    }

    private fun setFavoriteOrUnFavoriteApi(
        hnLQuizPackageModel: HnLQuizPackageModel
    ) {
        val jsonObject = JsonObject()
        jsonObject.addProperty("TypeId", 1)
        jsonObject.addProperty("UserId", customSharedPreferences.getUserId())
        jsonObject.addProperty("HouseId", hnLQuizPackageModel.packageId)
        jsonObject.addProperty("StateId", customSharedPreferences.getStateID())

        Timber.e("Favorite User ${hnLQuizPackageModel.isFav}")

        if (isFavoriteDetails == true) {
            this.hnLQuizPackageModel?.isFav = this.hnLQuizPackageModel?.isFav != false
            jsonObject.addProperty("isfavourite", this.hnLQuizPackageModel?.isFav)
        } else {
            this.hnLQuizPackageModel?.isFav = this.hnLQuizPackageModel?.isFav != true
            jsonObject.addProperty("isfavourite", this.hnLQuizPackageModel?.isFav)
        }



        homeLandViewModel.setFavorite(jsonObject)
    }


    override fun onMapClick(p0: LatLng?) {
        loadFullScreenMapDialog()
    }

    override fun onMarkerClick(p0: Marker?): Boolean {
        loadFullScreenMapDialog()
        return false
    }

    private fun loadFullScreenMapDialog(){
        val fm = childFragmentManager
        latLong?.let {
            val pop = FullScreenMapDialog(it)
            pop.show(fm, FullScreenMapDialog::class.java.canonicalName)
        }
    }

}
