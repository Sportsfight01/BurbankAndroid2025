package com.dmss.burbankapp.ui.view

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dmss.burbankapp.data.model.BreadcrumbModel
import com.dmss.burbankapp.databinding.ListItemBreadcrumBinding
import java.util.*

class BreadCrumbAdapter(
    var context: Context,
    var breadCrumbList: ArrayList<BreadcrumbModel?>?,
    var listener: BreadcrumbItemClickListener
) :
    RecyclerView.Adapter<BreadCrumbAdapter.BreadCrumbHolder>() {

    override fun getItemCount(): Int {
        breadCrumbList?.let {
            return it.size
        } ?: kotlin.run {
            return 0
        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BreadCrumbHolder {

        val itemBinding = ListItemBreadcrumBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BreadCrumbHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: BreadCrumbHolder, position: Int) {
        val breadCrumb = breadCrumbList?.get(position)
        if (breadCrumb != null) {
            holder.bindBreadCrumb(context, breadCrumb, listener, position)
        }
    }

    class BreadCrumbHolder(var itemBinding: ListItemBreadcrumBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bindBreadCrumb(
            context: Context,
            breadCrumb: BreadcrumbModel,
            itemClickListener: BreadcrumbItemClickListener, position: Int
        ) {
            itemBinding.root.setOnClickListener {
                itemClickListener.breadCrumb(breadCrumb)
            }
            if (position == 0) {
                itemBinding.view.visibility = View.INVISIBLE
            } else {
                itemBinding.view.visibility = View.VISIBLE
            }
            val extraBold: Typeface = Typeface.createFromAsset(
                context.assets,
                "fonts/montserrat_bold.ttf"
            )
            val medium: Typeface = Typeface.createFromAsset(
                context.assets,
                "fonts/montserrat_medium.ttf"
            )
            breadCrumb.isSelected?.let {
                if (it) {
                    itemBinding.tvLabel.typeface = extraBold
                } else {
                    itemBinding.tvLabel.typeface = medium
                }
            }

            itemBinding.tvLabel.text = breadCrumb.breadCrumbTitle


        }

    }

    interface BreadcrumbItemClickListener {
        fun breadCrumb(breadCrumb: BreadcrumbModel)
    }

}