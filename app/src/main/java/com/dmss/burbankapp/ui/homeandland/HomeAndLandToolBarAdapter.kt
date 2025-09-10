package com.dmss.burbankapp.ui.homeandland

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.dmss.burbankapp.R
import com.dmss.burbankapp.data.model.ToolBarHeaderModel

class HomeAndLandToolBarAdapter(
    var context: Context,
    var toolHeaderList: ArrayList<ToolBarHeaderModel>
) :
    RecyclerView.Adapter<HomeAndLandToolBarAdapter.ToolBarViewHolder>() {


    class ToolBarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var toolBarText: TextView = itemView.findViewById(R.id.tv_tool_header)
        fun bind(toolBarHeaderModel: ToolBarHeaderModel, context: Context) {
            toolBarText.text = toolBarHeaderModel.headerText

            if (toolBarHeaderModel.isHighlight) {
                toolBarText.setTextColor(ContextCompat.getColor(context, R.color.white))
            } else {
                toolBarText.setTextColor(ContextCompat.getColor(context, R.color.box_grey))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToolBarViewHolder {
        return ToolBarViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_toolbar_list, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return toolHeaderList.size
    }

    override fun onBindViewHolder(holder: ToolBarViewHolder, position: Int) {

        var toolBarHeaderModel = toolHeaderList[position]
        holder.bind(toolHeaderList[position], context)

    }

    fun updateData(data: ArrayList<ToolBarHeaderModel>) {
        if (data != null && data.size > 0) {
            toolHeaderList.clear()
            toolHeaderList = data
            notifyDataSetChanged()

        }

    }

    interface interfaceToolItemClick {
        fun toolBarItemClick()
    }

}