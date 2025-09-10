package com.dmss.burbankapp.ui.mydisplay.regions

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.dmss.burbankapp.R
import com.dmss.burbankapp.data.model.StateRegionModel

class RegionAdapter(
    var dataList: ArrayList<StateRegionModel>,
    private val updateCallback: IUpdateState,
    private val context: Context
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    class StateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var stateText: TextView = itemView.findViewById(R.id.tv_state)
        fun bind(
            stateModel: StateRegionModel,
            updateCallback: IUpdateState, context: Context
        ) {
            itemView.setOnClickListener {
                updateCallback.selectState(adapterPosition)
            }
            stateText.text = stateModel.RegionName
            if (stateModel.isSelected) {
                stateText.background = ContextCompat.getDrawable(context, R.drawable.rectangle_orange_bg)
                stateText.setTextColor(ContextCompat.getColor(context, R.color.white))
            } else {
                stateText.background =
                    ContextCompat.getDrawable(context, R.drawable.rectangle_grey_line)
                stateText.setTextColor(ContextCompat.getColor(context, R.color.grey_text_font_3_1))
            }

        }

    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    fun setData(statesData: ArrayList<StateRegionModel>) {
        dataList.clear()
        dataList = statesData
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is StateViewHolder -> {
                holder.bind(dataList[position], updateCallback, context)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return StateViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_choose_state, parent, false)
        )

    }

    interface IUpdateState {
        fun selectState(position: Int)
    }


}