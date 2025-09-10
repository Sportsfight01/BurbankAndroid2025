package com.dmss.burbankapp.ui.homeandlandoprice

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dmss.burbankapp.R

class BarGraphAdapter(var intArray: Array<Int>, var context: Context,var maxNumber: Int) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return BarGraphViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_bar_graph_item, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return intArray.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is BarGraphViewHolder -> {
                holder.bind(intArray[position], position, context, maxNumber)
            }

        }
    }

    class BarGraphViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var stateText: TextView = itemView.findViewById(R.id.tv_number)
        var parentView: RelativeLayout = itemView.findViewById(R.id.rl_parent)
        private val Int.dp: Int
            get() = (this * Resources.getSystem().displayMetrics.density + 0.5f).toInt()

        fun bind(i: Int, position: Int, context: Context, maxNumber: Int) {
            stateText.text = i.toString()
            val height: Double = (i.toDouble()/ maxNumber) * 200
            when (position) {
                0 -> {

                    parentView.layoutParams = RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, height.toInt().dp
                    )
                }
                1 -> {
                    parentView.layoutParams = RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, height.toInt().dp
                    )
                }
                2 -> {
                    parentView.layoutParams = RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, height.toInt().dp
                    )
                }
                3 -> {
                    parentView.layoutParams = RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, height.toInt().dp
                    )
                }
                else -> println("Number is not between 1 and 3")
            }


        }


    }
}