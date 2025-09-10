package com.dmss.burbankapp.ui.designs.howmanybedrooms

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.dmss.burbankapp.R
import com.dmss.burbankapp.data.model.LotWidthItemModel
import com.dmss.burbankapp.utility.SquareView

class MyCollectioNumberOfBedroomsAdapter(
    var context: Context,
    var answerList: ArrayList<LotWidthItemModel>,
    var lotItemClick: LotItemClick
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    class LotViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var meters: TextView = itemView.findViewById(R.id.tv_meter_count)
        var tv_meter: TextView = itemView.findViewById(R.id.tv_meter)
        var squareView: SquareView = itemView.findViewById(R.id.ll_parent)
        fun bind(context: Context, lot: LotWidthItemModel, lotItemClick: LotItemClick) {


            meters.text = String.format("%.1f", lot.lotWidth.toFloat())
            squareView.setOnClickListener {
                lotItemClick.selectedLotItem(lot)
            }

            if (lot.isSelected) {
                tv_meter.setTextColor(ContextCompat.getColor(context, R.color.white))
                squareView.background =
                    ContextCompat.getDrawable(context, R.drawable.rectangel_skip)

            } else {
                tv_meter.setTextColor(ContextCompat.getColor(context, R.color.appColor))
                squareView.background =
                    ContextCompat.getDrawable(context, R.drawable.rectangel_shape)
            }
        }

    }

    interface LotItemClick {
        fun selectedLotItem(lotName: LotWidthItemModel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return LotViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_lot, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return answerList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is LotViewHolder -> {
                holder.bind(context, answerList[position], lotItemClick = lotItemClick)
            }

        }
    }

}