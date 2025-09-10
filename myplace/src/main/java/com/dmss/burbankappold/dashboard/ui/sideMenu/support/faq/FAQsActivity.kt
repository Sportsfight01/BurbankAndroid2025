package com.dmss.burbankappold.dashboard.ui.sideMenu.support.faq

import adapters.ExpendableListAdapter
import android.content.Intent
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.view.View
import android.widget.ExpandableListView
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.dmss.burbankappold.R
import com.dmss.burbankappold.dashboard.ui.sideMenu.contactUs.ContactUsActivity
import com.dmss.burbankappold.databinding.ActivityFaqsBinding
import com.dmss.burbankappold.network.ApiRepository
import com.dmss.burbankappold.utils.getStateName
import com.dmss.burbankappold.utils.hide
import com.dmss.burbankappold.utils.show
import common.*
import interfaces.ExpendableListCallBack
import models.FAQModels
import models.faq.FAQDataItem
import models.faq.FilterFaqList
import org.json.JSONArray


class FAQsActivity : AppCompatActivity(),CellClickListener {
    private lateinit var mBinding : ActivityFaqsBinding
    var adapter: ExpendableListAdapter? = null
    var expandableListView: ExpandableListView? = null
    var listDataHeader: ArrayList<String>? = null
    var listDataChild: HashMap<String, String>? = null
    var faqModelsArrayList = ArrayList<FAQModels>()
    var position = 0
    var previousGroup = -1
    var controller: AppController? = null
    var dialog: TransparentProgressDialog? = null
    var mFaqList = arrayListOf<FilterFaqList>()
    private lateinit var faqsAdapter: FaqsAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityFaqsBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        initToolBar()
        setUpAdapter()
        controller = applicationContext as AppController
        callApi()

    }
    private fun callApi(){
        if(Utils.isNetworkAvailable(this)) {
            dialog = Utils.getProgress(this)
            ApiRepository.requestFaqDetails { isSuccess, faqData ->
                dialog?.dismiss()
                if (isSuccess) {
                    var faqListArr = faqData?.faqList!!.sortedBy { it.Category }
                    prepareFaqData(faqListArr)
                }
            }
        }
    }

    private fun prepareFaqData(faqList: List<FAQDataItem>?) {
        val filterList = faqList?.filter { it.State.contains(getStateName())}
        val groupList = filterList?.groupBy { it.Category }
        groupList?.forEach {
            mFaqList.add(FilterFaqList(it.key, it.value))
        }
        faqsAdapter.setFaqList(mFaqList)
    }

    private fun setUpAdapter() {
        faqsAdapter = FaqsAdapter(this)
        mBinding.lvExp.apply {
            this.layoutManager = LinearLayoutManager(this@FAQsActivity)
            this.adapter = faqsAdapter
        }
    }



    private fun initToolBar(){
        mBinding.toolbar.ivNavMenu.hide()
        mBinding.toolbar.ivToolBarBack.show()
        mBinding.toolbar.ivChat.show()
        mBinding.toolbar.ivChat.setOnClickListener {
            startActivity(Intent(this, ContactUsActivity::class.java))
        }
        mBinding.toolbar.ivToolBarBack.setOnClickListener {
            onBackPressed()
        }
      /*  mBinding.swiperefresh.setOnRefreshListener {
            if(mFaqList.isEmpty()) {
                Utils.hideSwipeRefresh(mBinding.swiperefresh)
                callApi()
            }else{
                Utils.hideSwipeRefresh(mBinding.swiperefresh)
            }
        }*/
    }

    override fun onCellClickListener(position: Int) {
        mBinding.lvExp.scrollToPosition(position)
        faqsAdapter.notifyDataSetChanged()

    }

}