package com.dmss.burbankappold.dashboard.ui.finanace

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import com.dmss.burbankappold.dashboard.ui.OffsetItemDecoration
import com.dmss.burbankappold.dashboard.ui.sideMenu.MyNotificationActivity
import com.dmss.burbankappold.databinding.FragmentFinanceBinding
import com.dmss.burbankappold.network.ApiRepository
import com.dmss.burbankappold.utils.*
import com.google.gson.Gson
import com.google.gson.JsonObject
import common.AppController
import common.Common
import common.TransparentProgressDialog
import common.Utils
import models.LoginRequestBody
import models.finance.FinanceData
import models.finance.FinanceHomeData
import models.finance.ResultDataData
import org.json.JSONException
import org.json.JSONObject

class FinanceFragment : BaseFragment() {
    private var _binding : FragmentFinanceBinding? = null
    private val binding get() = _binding!!
    private var requestFinanceAPI:()-> Unit = {}
    private var dialog: TransparentProgressDialog? = null
    private val controller = AppController.controller
    private var financeAdapter: FinanceAdapter? = null
    private var financeData: FinanceData? =  null
    private var contractValue : Double = 0.0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFinanceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        if (financeAdapter?.list?.isNotEmpty() == true){
            updateUI(financeData)
            contractValue = financeData?.contractPrice?:0.0
        }else requestApiDetails()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        financeAdapter = FinanceAdapter{ type, financeData ->
            findNavController().navigate(R.id.nav_finance_detail_view,
            bundleOf(BundleKey.FINANCE_DATA to financeData,
            BundleKey.FINANCE_TYPE to type,
            BundleKey.CONTRACT_VALUE to contractValue))
        }
        //callAPi()
    }

    private fun initViews() {
        binding.profileHeader.tvHeading.text = getString(R.string.my_finance)
        binding.profileHeader.tvHeading.changeTextColor(context)
        binding.profileHeader.tvSubHeading.hide()
        binding.profileHeader.llFinance.show()
        binding.profileHeader.ivPhoto.loadUrlUsingGlide(PrefsHelper.profileUrl)
        binding.profileHeader.tvContractValue.text = "0".convertUsCurrency()
        binding.profileHeader.tvBalanceDue.text = "0".convertUsCurrency()
        binding.rvFinance.apply {
            this.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            this.adapter = financeAdapter
            this.addItemDecoration(OffsetItemDecoration(R.dimen._20sdp))
        }
       /* binding.profileHeader.tvNotificationsCount.visibility = if(PrefsHelper.notificationCount == "0" || PrefsHelper.notificationCount == "")
            View.GONE else View.VISIBLE
        binding.profileHeader.tvNotificationsCount.text = PrefsHelper.notificationCount*/
        if(PrefsHelper.notificationCount != "0" && PrefsHelper.notificationCount != "") {
            var notificationCount = PrefsHelper.notificationCount
            if (notificationCount != "" && notificationCount.toInt() > 100) {
                notificationCount = "99+"
            }
            binding.profileHeader.tvNotificationsCount.text = notificationCount
            binding.profileHeader.tvNotificationsCount.visibility = View.VISIBLE
        }else{
            binding.profileHeader.tvNotificationsCount.visibility = View.GONE
        }
        binding.profileHeader.ivPhoto.setOnClickListener {
            startActivity(Intent(requireContext(), MyNotificationActivity::class.java))
        }
        binding.swiperefresh.setOnRefreshListener {
            if(financeAdapter?.list?.isEmpty() == true) {
                Utils.hideSwipeRefresh(binding.swiperefresh)
                requestApiDetails()
            }else{
                Utils.hideSwipeRefresh(binding.swiperefresh)
            }
        }
    }

    private fun getMyPlaceLoginJson(): String {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("Region", controller.my_Place_Details.region)
            jsonObject.put("JobNumber", controller.my_Place_Details.jobNumber)
            jsonObject.put("UserName", controller.my_Place_Details.username)
            jsonObject.put("Password", controller.my_Place_Details.password)
        } catch (ex: JSONException) {
            ex.fillInStackTrace()
        }
        return jsonObject.toString()
    }

    fun setAdapter(financeData: FinanceData?) {
        val list = arrayListOf<FinanceHomeData>()
        list.add(
            FinanceHomeData("Overview", financeData, 0)
        )
        list.add(
            FinanceHomeData("Variations to Date", financeData, 1)
        )
        list.add(
            FinanceHomeData("Claims to Date", financeData, 2)
        )
        list.add(
            FinanceHomeData("Receipts to Date", financeData, 3)
        )
        financeAdapter?.setFinanceList(list)
    }

    private fun requestApiDetails(){
        if (controller.my_Place_Details != null) {
            if (Utils.isNetworkAvailable(activity)) {
                dialog = Utils.getProgress(activity)
                val t = Thread {
                    val jsonObject = JsonObject()
                    jsonObject.addProperty("Username", AppController.AuthUserName)
                    jsonObject.addProperty("Password", AppController.AuthPassword)

                    //jsonObject.put("Region", controller.my_Place_Details.region)
                   // jsonObject.put("JobNumber", controller.my_Place_Details.jobNumber)
                    //http://10.6.45.14:8085/myplace/api/finance/GetFinanceDetails?financialTicketId=182953&region=VIC
                    val financeResult = controller.webApiCall().getData_From_MyPlace(
                        Common.myPlaceBaseUrlQldOrSa + "" + Common.myPlaceGetFinance + "" + AppController.controller.my_Place_Details.jobNumber+"&region="+controller.my_Place_Details.region
                    )
                    Handler(Looper.getMainLooper()).post {
                        dialog?.cancel()
                        if (financeResult != null) {
                            println("financeResult::$financeResult")
                            val resultData =
                                Gson().fromJson(financeResult, ResultDataData::class.java)
//                        financeData = Gson().fromJson(financeResult, FinanceData::class.java)
                            if(resultData.status) {
                                financeData = resultData.financeDatails
                                contractValue = financeData?.contractPrice ?: 0.0
                                setAdapter(financeData)
                                updateUI(financeData)
                            }else{
                                Utils.showToast(
                                    activity,
                                    "My place details not valid for this job number",
                                    Common.errorCase
                                )
                            }
                        }else{
                            Utils.showToast(
                                activity,
                                "My place details not valid for this job number",
                                Common.errorCase
                            )
                        }
                    }
                  /*  val result: String = controller.webApiCall().postData_to_MyPlace(
                        Common.myPlaceBaseUrlQldOrSa + "" + Common.MyPlaceUserCheckUrl,
                        getMyPlaceLoginJson()
                    )
                    if (result.equals("true", ignoreCase = true)) {
                        val result2: String = controller.webApiCall()
                            .getData_From_MyPlace(Common.myPlaceBaseUrlQldOrSa + "" + Common.MyPlaceGetUserDetailsUrl)
                        if (!result2.equals("null", ignoreCase = true)) {
                            val financeResult = controller.webApiCall().getData_From_MyPlace(
                                Common.myPlaceBaseUrlVic + "" + Common.myPlaceGetFinance + "" + AppController.controller.my_Place_Details.jobNumber
                            )
                            Handler(Looper.getMainLooper()).post {
                                dialog?.cancel()
                                financeData = Gson().fromJson(financeResult, FinanceData::class.java)
                                contractValue = financeData?.ContractPrice?:0.0
                                setAdapter(financeData)
                                updateUI(financeData)
                            }
                        }
                    } else if (result.equals("false", ignoreCase = true)) {
                        Handler(Looper.getMainLooper()).post {
                            dialog?.cancel()
                            Utils.showToast(
                                activity,
                                "My place details not valid for this job number",
                                Common.errorCase
                            )
                        }
                    } else {
                        Handler(Looper.getMainLooper()).post { dialog?.cancel() }
                    }*/
                }
                t.start()
            } else {
                Handler(Looper.getMainLooper()).post { dialog?.cancel() }
            }
        } else {
            Handler(Looper.getMainLooper()).post { dialog?.cancel() }
        }
    }

    private fun updateUI(financeData: FinanceData?) {
        binding.profileHeader.tvContractValue.text = financeData?.contractPrice?.toString()?.convertUsCurrency()
    }

    companion object{
        fun getApprovedVariation(financeData: FinanceData?) : String{
            val sumValue: Double = financeData?.financeVariations?.sumOf { it.amount }?:0.0
            return  sumValue.toString().convertUsCurrency()
        }
        fun getContactValue(financeData: FinanceData?) : String{
            val totalAmount: Double = financeData?.contractPrice?:0.0
            val sumValue: Double = financeData?.financeVariations?.sumOf { it.amount }?:0.0
            return  totalAmount.plus(sumValue).toString().convertUsCurrency()
        }
        fun getTotalAmountClaimed(financeData: FinanceData?): String{
            val sumValue: Double = financeData?.financeClaims?.sumOf { it.amount }?:0.0
            return  sumValue.toString().convertUsCurrency()
        }
        fun getTotalAmountReceived(financeData: FinanceData?): String{
            val sumValue: Double = financeData?.financeReceipts?.sumOf { it.amount }?:0.0
            return sumValue.toString().convertUsCurrency()
        }
    }
}