package com.dmss.burbankappold.dashboard.ui.sideMenu.support.faq

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings.PluginState
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.RequestOptions
import com.dmss.burbankappold.R
import com.dmss.burbankappold.dashboard.ui.sideMenu.VideoWebViewActivity
import com.dmss.burbankappold.databinding.ItemFaqChildBinding
import com.dmss.burbankappold.utils.BundleKey
import com.dmss.burbankappold.utils.hide
import com.dmss.burbankappold.utils.show
import models.faq.FAQDataItem
import java.io.File


class FaqsChildAdapter(private val mFaqList: List<FAQDataItem>) : RecyclerView.Adapter<FaqsChildAdapter.ViewHolder>() {
    private var lastSelectedPosition = -1
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = ItemFaqChildBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        mFaqList[position].let { holder.bind(it) }

    }

    override fun getItemCount(): Int {
        return mFaqList.size
    }

    inner class ViewHolder(val binding: ItemFaqChildBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: FAQDataItem) {
            val mContext = binding.root.context
            binding.lblListHeader.text = item.Question
            binding.faqAnswer.text =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        Html.fromHtml(
                            item.Answer?.trim { it <= ' ' },
                            Html.FROM_HTML_MODE_COMPACT
                        )
                } else {
                    Html.fromHtml(item.Answer?.trim { it <= ' ' })
                }

            if (item.isExpanded){
                binding.faqAnswer.show()
                binding.headerDot.isChecked = true
                binding.lblListHeader.setTextColor(ContextCompat.getColor(mContext, R.color.appColor))
                if (item.VideoUrl?.isNotEmpty() == true) {
                    binding.videoView.show()
                    val requestOptions = RequestOptions()
                    requestOptions.isMemoryCacheable
                    binding.videoImage.settings.javaScriptEnabled = true
/*                    binding.videoImage.settings.setPluginState(PluginState.ON);
                    binding.videoImage.setWebChromeClient(WebChromeClient())*/
                    binding.videoImage.loadUrl(item.VideoUrl)
                    binding.imgPlay.setOnClickListener {

                        mContext.startActivity(Intent(mContext, VideoWebViewActivity::class.java).putExtra(BundleKey.URL, item.VideoUrl))
                    }
                }else binding.videoView.hide()
            }else{
                binding.faqAnswer.hide()
                binding.headerDot.isChecked = false
                binding.lblListHeader.setTextColor(ContextCompat.getColor(mContext, R.color.headerTextColor))
                binding.videoView.hide()
            }

            binding.root.setOnClickListener {
                if (lastSelectedPosition != -1 && lastSelectedPosition != adapterPosition) {
                    mFaqList[lastSelectedPosition].apply {
                        this.isExpanded = false
                    }
                    notifyItemChanged(lastSelectedPosition)
                }
                item.apply {
                    this.isExpanded = !item.isExpanded
                }
                notifyItemChanged(adapterPosition)
                lastSelectedPosition = adapterPosition

            }
        }
    }
}