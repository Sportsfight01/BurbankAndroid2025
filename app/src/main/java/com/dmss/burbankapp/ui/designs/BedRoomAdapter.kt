package com.dmss.burbankapp.ui.designs

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.dmss.burbankapp.R
import com.dmss.burbankapp.databinding.ItemBedroomsLayoutBinding
import common.AppController

class BedRoomAdapter(val callBack: (String) -> Unit) : RecyclerView.Adapter<BedRoomAdapter.ViewHolder>() {
    private var list = listOf<BedRooms>()
    fun setBedroomList(list: List<BedRooms>){
        this.list = list
        notifyDataSetChanged()
    }

    class ViewHolder(val binding: ItemBedroomsLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(bedroom: BedRooms) {
            val context = binding.root.context
            if(bedroom.name == NOT_SURE){
                binding.tvNumber.visibility = View.GONE
                binding.imgNotSure.visibility = View.VISIBLE
                binding.tvBedRoomText.text = bedroom.name
//                binding.tvBedRoomText.textSize = 13f
            }else{
                binding.tvNumber.text = bedroom.name
                binding.tvNumber.visibility = View.VISIBLE
                binding.imgNotSure.visibility = View.GONE
                binding.tvBedRoomText.text = context.getString(R.string.bedrooms)
//                binding.tvBedRoomText.textSize = 10f
            }
            if (bedroom.isChecked){
                binding.btnBedroom.background = ContextCompat.getDrawable(context, R.drawable.rectangle_orange_bg)
                binding.tvNumber.setTextColor(ContextCompat.getColor(context, R.color.white_3_1))
                binding.imgNotSure.setColorFilter(ContextCompat.getColor(AppController.getInstance(), R.color.white_3_1));

                binding.tvBedRoomText.setTextColor(ContextCompat.getColor(context, R.color.white_3_1))
            }else{
                binding.btnBedroom.background = ContextCompat.getDrawable(context, R.drawable.rectangel_shape)
                binding.tvNumber.setTextColor(ContextCompat.getColor(context, R.color.grey_text_font_3_1))
                binding.tvBedRoomText.setTextColor(ContextCompat.getColor(context, R.color.grey_text_font_3_1))
                binding.imgNotSure.setColorFilter(ContextCompat.getColor(AppController.getInstance(), R.color.grey_text_font_3_1));

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemBedroomsLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val bedroom = list[position]
        holder.onBind(bedroom)
        holder.binding.root.setOnClickListener {
            list.forEach { it.isChecked = false }
            bedroom.isChecked = true
            callBack.invoke(list[position].name)
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int = list.size
}