package com.dmss.burbankapp.ui.mydisplay.nearby

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.telephony.PhoneNumberUtils
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import com.dmss.burbankapp.R
import com.dmss.burbankapp.data.api.ApiHelperImpl
import com.dmss.burbankapp.data.api.RetrofitBuilder
import com.dmss.burbankapp.data.apiUtils.ViewModelFactory
import com.dmss.burbankapp.data.local.CustomSharedPreferences
import com.dmss.burbankapp.data.local.entity.DatabaseBuilder
import com.dmss.burbankapp.data.local.entity.DatabaseHelperImpl
import com.dmss.burbankapp.data.model.HouseAppointmentDataModelObject
import com.dmss.burbankapp.databinding.ActivityEnquireNowBinding
import com.dmss.burbankapp.databinding.FragmentChooseYourTimeCommentBinding
import com.dmss.burbankapp.ui.base.BaseActivity
import com.dmss.burbankapp.ui.dashboard.DashboardActivity
import com.dmss.burbankapp.ui.mydisplay.DisplayHomesViewModel
import com.dmss.burbankapp.ui.view.CustomProgressDialog
import com.dmss.burbankapp.utils.AppConstants
import com.dmss.burbankapp.utils.AppUtils
import com.google.gson.JsonObject
import java.util.Locale


class ChooseYourTimeCommentFragment : BaseActivity() {
    var selectedDate: String? = null

    lateinit var binding: FragmentChooseYourTimeCommentBinding
    lateinit var displayHomeViewModel: DisplayHomesViewModel
//    private var customProgressDialog: CustomProgressDialog? = null
    lateinit var mPreferences: CustomSharedPreferences
    var localPlaces: List<String> = arrayListOf()
    var localPlace: String? = null

    var estateName: String? = ""
    var street: String? = ""
    var suburb: String? = ""
    var houseName: String? = ""
    var houseSize: String? = ""
    var day: String? = ""
    var isChecked: Boolean = false

    /*  override fun onCreateView(
          inflater: LayoutInflater, container: ViewGroup?,
          savedInstanceState: Bundle?
      ): View {
          binding = FragmentChooseYourTimeCommentBinding.inflate(inflater, container, false)
          return binding.root
      }


      override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
          super.onViewCreated(view, savedInstanceState)
          initUI()
          initViewModel()
      }*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentChooseYourTimeCommentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        changeStatusBarColor(ContextCompat.getColor(this, R.color.app_bg))
        initUI()
    }


    private fun initViewModel() {
        displayHomeViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(
                ApiHelperImpl(RetrofitBuilder.apiService),
                DatabaseHelperImpl(DatabaseBuilder.getInstance(this))
            )
        ).get(DisplayHomesViewModel::class.java)

//        subscribers()
    }

    private fun getWebView(){
        var userInfoModel = mPreferences.getUserInfoModel()
        var mFirstName = binding.firstName.text.toString()
        var mLastName = binding.lastName.text.toString()
        var mEmail = binding.email.text.toString()
        var mPhoneNumber = binding.phone.text.toString()
        selectedDate = intent.extras?.getString("selectedDate")
        day = intent.extras?.getString("day")?.toUpperCase()
        var mDate = selectedDate?.split(",")?.get(1)?.trim()
        val mTime = binding.tvTime.text.toString()
        val mAddress =binding.tvOndisplay.text.toString()
        if (intent != null) {
            estateName = intent.extras?.getString("estateName")
            street = intent.extras?.getString("street")
            suburb = intent.extras?.getString("suburb")
            houseName = intent.extras?.getString("houseName")
            houseSize = intent.extras?.getString("houseName")
        }
//            ng.tvOndisplay.text = ("ON DISPLAY: $houseName")
//        binding.tvStreetSuburb.text = ("$street,\n$suburb")
        var stateName: String = mPreferences.getSelectedState().toString()
        var stateId: String = mPreferences.getSelectedStateID().toString()
        var webUrl= ""

        if (stateName.contentEquals("victoria", true)) {
            webUrl = "https://share.hsforms.com/1uh3e6AmJQjm22lHMnhWv4Qqcx0d?"
        } else if (stateName.contentEquals("queensland", true)) {
            webUrl = "https://share.hsforms.com/11nt-jFEDRySaT1feMsmz_Aqcx0d?"
        } else if (stateName.contentEquals("south-australia", true)) {
            webUrl = "https://share.hsforms.com/1jd9AXNTaRbOgzd1LljYw7gqcx0d?"
        } else if (stateName.contentEquals("NSW & ACT", true)) {
            webUrl = "https://share.hsforms.com/1tqvI8gtuSUCSz5rpHnbzXAqcx0d?"
        }

       val mWebUrl=
           webUrl+"firstname=$mFirstName&lastname=$mLastName&email=$mEmail&phone=$mPhoneNumber&where_would_you_like_to_live_=SANorth&preferred_day_week=&preferred_date=$mDate&preferred_time=$mTime&description=&i_accept_burbank_s_privacy_policy_and_collection_statement_=&original_marketing_activity=MyPlace App&build_address=$mAddress&housename=$houseName"

        println("mWebUrl:: "+mWebUrl)
        binding.webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                return false
            }
        }

        binding.webView.settings.javaScriptEnabled = true
           clearWebViewData(binding.webView)
        // Handle URL loading inside the WebView
        binding.webView.webViewClient = WebViewClient()
        binding.webView.loadUrl(mWebUrl)

    }
    private fun addHouseAppointment() {
        var firstName: String = ""
        if (binding.firstName.text.isNotEmpty()) {
            firstName = binding.firstName.text.toString().capitalize().trim()
        }

        var lastName: String = ""
        if (binding.lastName.text.isNotEmpty()) {
            lastName = binding.lastName.text.toString().capitalize().trim()
        }
        var phoneNumber: String = ""
        if (binding.phone.text.isNotEmpty()) {
            phoneNumber = binding.phone.text.toString().trim()
        }

        var etEmail = ""
        if (binding.email.text.toString().isNotEmpty()) {
            etEmail = binding.email.text.toString().trim()
        }

        var comment = binding.etComment.text.toString()

        if (firstName.isNotEmpty()) {
            if (lastName.isNotEmpty()) {
                if (!TextUtils.isEmpty(phoneNumber)) {
                    if (PhoneNumberUtils.isGlobalPhoneNumber(phoneNumber) && phoneNumber.length >= 8) {
                        if (!TextUtils.isEmpty(etEmail)) {
                            if (validEmailName()) {

                                if (!TextUtils.isEmpty(localPlace)) {
                                    if (isChecked) {
                                        var date = ""
                                        var time = ""
                                        var obj = day?.let {
                                            selectedDate?.let { it1 ->
                                                date = it1
                                                time = it.toUpperCase(Locale.ROOT)
                                                HouseAppointmentDataModelObject(
                                                    mPreferences.getDisplayId(),
                                                    mPreferences.getStateID(),
                                                    mPreferences.getUserId(),
                                                    it1,
                                                    comment,
                                                    it.toUpperCase(Locale.ROOT),
                                                    firstName,
                                                    lastName,
                                                    phoneNumber,
                                                    email = etEmail,
                                                    localPlace ?: "",
                                                    isChecked

                                                )
                                            }
                                        }
                                        if (obj != null) {
                                            fetchEnquiry(
                                                comment,
                                                houseName,
                                                houseSize,
                                                firstName,
                                                lastName,
                                                phoneNumber,
                                                etEmail,
                                                localPlace,
                                                time,
                                                date
                                            )
                                            displayHomeViewModel.fetchHouseAppointment(obj)
                                        }
                                    } else {
                                        AppUtils.showValidationAlert(
                                            this,
                                            "Please accept Burbank's privacy policy"
                                        )
                                    }
                                } else {
                                    AppUtils.showValidationAlert(
                                        this,
                                        "Please select where would you like to live"
                                    )
                                }
                            }else{
                                AppUtils.showValidationAlert(this, "Please enter valid email id")

                            }
                        }
                        else {
                            AppUtils.showValidationAlert(
                                this,
                                "Please enter email"
                            )
                        }


                    } else {
                        AppUtils.showValidationAlert(
                            this,
                            "Please enter valid Phone number"
                        )
                    }


                } else {
                    AppUtils.showValidationAlert(this, "Please enter phone number")
                }

            } else {
                AppUtils.showValidationAlert(this, "Please enter last name")
            }

        } else {
            AppUtils.showValidationAlert(this, "Please enter first name")
        }
    }
    private fun validEmailName(): Boolean {
        val emailString = binding.email.text.toString().trim()
        return Patterns.EMAIL_ADDRESS.matcher(emailString).matches()
    }
    private fun fetchEnquiry(
        message: String?,
        houseName: String?,
        houseSize: String?,
        firstName: String?,
        lastName: String?,
        phoneNumber: String?,
        email: String?,
        localPlace: String?,
        time: String?,
        date: String?
    ) {
        val enquiryObj = JsonObject()
        val bodyObj = JsonObject()
        var stateName: String = mPreferences.getSelectedState().toString()
        var stateId: String = mPreferences.getSelectedStateID().toString()
        //
        if(stateName.contains("Wales")){
            stateName = "nsw"
        }else if(stateName.contains("South")){
            stateName = "south-australia"
        }else if(stateName.contains("queens")){
            stateName = "queensland"
        }else{
            stateName = "victoria"
        }

        var estate = ""
        var notes = "DisplayLocation-${estateName!!.trim()}~AppointmentTime-${time}~Date-${date}"
        if(estateName != null){
            estate = estateName!!.trim()
            estate = estate.replace(" ", "-")
        }else{
            estate =""
        }
        /*var subject =
            "MyPlace app Enquiry - https://www.burbank.com.au/${stateName}homedetails/housename.${houseName};housesize.${houseSize}"*/
        var formName = "DisplayHomesAppointment"

        var subject = "MyPlace app Enquiry - ${AppConstants.WEB_BASE_URL}/${stateName}/display-location/${estate}/"

        bodyObj.addProperty("Message", message)
        bodyObj.addProperty("I'm interested in", "$houseName $houseSize")
        bodyObj.addProperty("Where would you like to live", localPlace)
        bodyObj.addProperty(
            "I accept Burbank’s privacy policy and wish to receive Burbank’s latest news and offers on my email.",
            isChecked
        )

        bodyObj.addProperty("Name", firstName + " " + lastName)
        bodyObj.addProperty("PhoneNumber", phoneNumber)
        bodyObj.addProperty("Email", email)
        bodyObj.addProperty("Where would you like to live", localPlace)

        enquiryObj.addProperty("Subject", subject)
        enquiryObj.addProperty("Email", email)
        enquiryObj.addProperty("Message", message)
        enquiryObj.addProperty("Build", localPlace)
        enquiryObj.addProperty("Where would you like to live", localPlace)
        enquiryObj.addProperty("State", stateName)
        enquiryObj.addProperty("Header", "")
        enquiryObj.addProperty("IsConsent", true)
        enquiryObj.addProperty("ReferralSource", "")
        enquiryObj.addProperty("EnquiryType", "Android - App")
        enquiryObj.addProperty("FirstName", firstName)
        enquiryObj.addProperty("HouseName", houseName)
        enquiryObj.addProperty("Hearaboutus", "")

        enquiryObj.addProperty("FormName", formName)
        enquiryObj.addProperty("PhoneNumber", phoneNumber)
        enquiryObj.addProperty("LastName", lastName)
        enquiryObj.addProperty("Notes", notes)
        enquiryObj.add("Body", bodyObj)

        displayHomeViewModel.fetchEnquire(enquiryObj)
    }
    fun clearWebViewData(webView: WebView) {
        webView.clearCache(true)
        webView.clearHistory()
        webView.clearFormData()
        webView.clearSslPreferences()
        webView.loadUrl("about:blank")

        // Clear cookies
        val cookieManager = CookieManager.getInstance()
        cookieManager.removeAllCookies(null)
        cookieManager.flush()
    }
    /*private fun subscribers() {
        displayHomeViewModel.getHouseAppointmentLiveData().observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    customProgressDialog?.dismissProgress()
                    var regionsData: HouseAppointmentResponseModel? = it?.data

                    var bundle = Bundle()
                    bundle.putString("estateName", estateName)
                    bundle.putString("street", street)
                    bundle.putString("suburb", suburb)
                    bundle.putString("houseName", houseName)
                    bundle.putString("day", day)
                    bundle.putString("selectedDate", selectedDate)

                    loadFragment(RequestSuccessfulFragment(), bundle)
                    if (regionsData != null) {

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
*/
    private fun initUI() {
        binding.ivBack.setOnClickListener {
            onBackPressed()
        }
        binding.ivProfile.setOnClickListener {

            val signUpIntent = Intent(this, DashboardActivity::class.java)
            signUpIntent.flags =Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(signUpIntent)
        }
        mPreferences = CustomSharedPreferences(this)

        var stateName = mPreferences.getSelectedState()
        if (!TextUtils.isEmpty(stateName)) {
            if (stateName.contentEquals("victoria", true)) {
                localPlaces = resources.getStringArray(R.array.victoria).toList()
            } else if (stateName.contentEquals("queensland", true)) {
                localPlaces = resources.getStringArray(R.array.queensland).toList()
            } else if (stateName.contentEquals("south-australia", true)) {
                localPlaces = resources.getStringArray(R.array.south_australia).toList()
            } else if (stateName.contentEquals("NSW & ACT", true)) {
                localPlaces = resources.getStringArray(R.array.nsw).toList()
            }
        }
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item, localPlaces
        )
        binding.localPlaceSpinner.adapter = adapter
        binding.localPlaceSpinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long
            ) {
                localPlace = if (position == 0) {
                    ""
                } else {
                    localPlaces[position]
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }

        AppConstants.BACKSTACK_COUNT = 0

        customProgressDialog = CustomProgressDialog(this)
        var userInfoModel = mPreferences.getUserInfoModel()


        userInfoModel.FirstName?.let {
            if (it.isNotEmpty()) {
                binding.firstName.setText(it)
                binding.firstName.background =
                    ContextCompat.getDrawable(
                        binding.firstName.context,
                        R.drawable.disable_gery
                    )
                binding.firstName.isEnabled = false
                binding.firstName.isFocusable = false
            } else {
                binding.firstName.background = ContextCompat.getDrawable(
                    binding.firstName.context,
                    R.drawable.rectangel_shape
                )
                binding.firstName.isEnabled = true
                binding.firstName.isFocusableInTouchMode = true


            }


        }

        userInfoModel.Email?.let {
            if (it.isNotEmpty()) {
                binding.email.setText(it)
                binding.email.background =
                    ContextCompat.getDrawable(binding.email.context, R.drawable.disable_gery)
                binding.email.isEnabled = false
                binding.email.isFocusable = false

            } else {
                binding.email.background = ContextCompat.getDrawable(
                    binding.email.context,
                    R.drawable.rectangel_shape
                )
                binding.email.isEnabled = true
                binding.email.isFocusableInTouchMode = true
            }
        }

        userInfoModel.LastName?.let {
            if (it.isNotEmpty()) {
                binding.lastName.setText(it)
                binding.lastName.background =
                    ContextCompat.getDrawable(binding.lastName.context, R.drawable.disable_gery)
                binding.lastName.isFocusable = false
                binding.lastName.isFocusableInTouchMode = false
            } else {
                binding.lastName.background = ContextCompat.getDrawable(
                    binding.lastName.context,
                    R.drawable.rectangel_shape
                )
                binding.lastName.isFocusable = true
                binding.lastName.isFocusableInTouchMode = true
            }
        }

        userInfoModel.PhoneNumber?.let {
            binding.phone.setText(it)
        }

        if (intent != null) {
            estateName = intent.extras?.getString("estateName")
            street = intent.extras?.getString("street")
            suburb = intent.extras?.getString("suburb")
            houseName = intent.extras?.getString("houseName")
            houseSize = intent.extras?.getString("houseName")
        }
        if (estateName != null && street != null && suburb != null && houseName != null) {
            binding.tvHeader.text = estateName
            binding.tvOndisplay.text = ("ON DISPLAY: $houseName")
            binding.tvStreetSuburb.text = ("$street,\n$suburb")
        }


        selectedDate = intent.extras?.getString("selectedDate")
        day = intent.extras?.getString("day")?.toUpperCase()

        if (selectedDate != null && day != null) {
            binding.tvDay.text = selectedDate
            binding.tvTime.text = day
        }

        binding.ivCheck.setOnClickListener {
            if (isChecked) {
                isChecked = false
                binding.ivCheck.setImageResource(
                    R.drawable.uncheck_checkbox

                )

            } else {
                isChecked = true
                binding.ivCheck.setImageResource(

                    R.drawable.enq_checj

                )
            }


        }
        binding.btnEnq.setOnClickListener {
            addHouseAppointment()
            /* if (binding.etComment.text.toString().trim().isNotEmpty()) {

             } else {
                 AppUtils.showCustomCenterToast(requireContext(), "Please enter comment")
             }*/
        }
        getWebView()

    }
 /*fun loadFragment(fragment: Fragment, bundle: Bundle) {
        // load fragment
        val transaction: FragmentTransaction = parentFragmentManager.beginTransaction()
        fragment.arguments = bundle
        if (fragment.isAdded) {
            return
        }
        transaction.add(R.id.fragment_container, fragment)
//        transaction.addToBackStack(null)
        transaction.commit()
    }*/

}