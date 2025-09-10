package com.dmss.burbankapp.ui.mydisplay.designs

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dmss.burbankapp.data.model.DesignSubModel
import com.dmss.burbankapp.databinding.LayoutExpandSubListBinding

class ExpandableSubList(var iEstateClickListener: IEstateClickListener) :
    RecyclerView.Adapter<ExpandableSubList.ItemViewHolder>() {
    var locationList: ArrayList<DesignSubModel> = ArrayList()


    class ItemViewHolder(var binding: LayoutExpandSubListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(location: DesignSubModel) {
            binding.tvLocation.text = ("${location.LotSuburb?:""} - ${location.EstateName?:""}")
        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemViewHolder {
        return ItemViewHolder(
            LayoutExpandSubListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )

        )
    }

    override fun onBindViewHolder(
        holder: ItemViewHolder,
        position: Int
    ) {
        var location = locationList[position]
        holder.bind(location)
        holder.itemView.setOnClickListener {
            iEstateClickListener.estateItemClick(location)
        }
    }

    override fun getItemCount(): Int {
        return locationList.size
    }

    fun updatedList(locations: ArrayList<DesignSubModel>) {
        locationList = locations
        notifyDataSetChanged()

    }

    interface IEstateClickListener {
        fun estateItemClick(estate: DesignSubModel)
    }

}