package com.dmss.burbankappold.dashboard.ui.sideMenu.support.infoCentre

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import com.dmss.burbankappold.R
import com.dmss.burbankappold.dashboard.ui.sideMenu.contactUs.ContactUsActivity
import com.dmss.burbankappold.databinding.ActivityInfoCentreBinding
import com.dmss.burbankappold.network.ApiRepository
import com.dmss.burbankappold.utils.BundleKey
import com.dmss.burbankappold.utils.getStateName
import com.dmss.burbankappold.utils.hide
import com.dmss.burbankappold.utils.show
import common.TransparentProgressDialog
import common.Utils
import models.faq.FAQDataItem
import models.faq.FilterFaqList
import models.infoCentre.InfoSearchData

class InfoCentreActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityInfoCentreBinding
    var dialog: TransparentProgressDialog? = null
    var icList = arrayListOf<FAQDataItem>()
    var catList = arrayListOf<InfoSearchData>()
    lateinit var infoCenterAdapter: InfoCenterAdapter
    lateinit var searchAdapter: InfoCenterSearchAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityInfoCentreBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        initToolBar()
        setUpAdapter()
        requestApiCall()
        mBinding.ivSearch.setOnClickListener {
            if (mBinding.rvSearch.isVisible){
                mBinding.rvSearch.hide()
                mBinding.ivSearch.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_info_center_search))
                val filterList = icList?.filter { it.State.contains(getStateName())}
                prepareFaqData(icList.filter { it.Category == "" },false)
                infoCenterAdapter.setInfoList(filterList)

                mBinding.rvSearch.apply {
                    layoutManager = GridLayoutManager(this@InfoCentreActivity, 3)
                    adapter = searchAdapter
                }
            }
            else{
                mBinding.rvSearch.show()
                mBinding.ivSearch.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_search_orange_fill))
            }
        }
    }

    private fun setUpAdapter() {
        infoCenterAdapter = InfoCenterAdapter{
            startActivity(
                Intent(
                    this@InfoCentreActivity,
                    InfoCentreDetailsActivity::class.java
                ).putExtra(BundleKey.DATA_OBJECT, it)
            )
        }
        mBinding.rvInfoCenter.apply {
            adapter = infoCenterAdapter
            layoutManager = GridLayoutManager(context, 2)
        }

        searchAdapter = InfoCenterSearchAdapter{ cat ->
            prepareFaqData(icList.filter { it.Category == cat },true)
        }

        mBinding.rvSearch.apply {
            layoutManager = GridLayoutManager(this@InfoCentreActivity, 3)
            adapter = searchAdapter
        }
    }

    private fun requestApiCall() {
        if(Utils.isNetworkAvailable(this)) {
            dialog = Utils.getProgress(this)
            ApiRepository.requestInfoCenterDetails { isSuccess, faqData ->
                dialog?.dismiss()
                if (isSuccess) {
                    if (faqData?.faqList?.isNotEmpty() == true) {
                        icList = faqData.faqList
                        prepareFaqData(icList)
                    }
                }
            }
        }
    }

    private fun prepareFaqData(infoList: List<FAQDataItem>?, isFromSearch: Boolean = false) {
        val filterList = infoList?.filter { it.State.contains(getStateName())}
        if (!isFromSearch) {
            val groupList = filterList?.groupBy { it.Category }
            groupList?.forEach {
                catList.add(InfoSearchData(it.key))
            }
        }
        if (filterList?.isNotEmpty() == true) {
            infoCenterAdapter.setInfoList(filterList)
        }
        searchAdapter.setInfoSearchData(catList)
    }

    private fun initToolBar() {
        mBinding.toolbar.ivNavMenu.hide()
        mBinding.toolbar.ivToolBarBack.show()
        mBinding.toolbar.ivChat.show()
        mBinding.toolbar.ivChat.setOnClickListener {
            startActivity(Intent(this, ContactUsActivity::class.java))
        }
        mBinding.toolbar.ivToolBarBack.setOnClickListener {
            onBackPressed()
        }
        mBinding.swiperefresh.setOnRefreshListener {
            if(icList.isEmpty()) {
                Utils.hideSwipeRefresh(mBinding.swiperefresh)
                requestApiCall()
            }else{
                Utils.hideSwipeRefresh(mBinding.swiperefresh)
            }
        }
    }

}