package com.dmss.burbankapp.ui.mydisplay.designs

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.bumptech.glide.Priority
import com.bumptech.glide.request.RequestOptions
import com.dmss.burbankapp.R
import com.dmss.burbankapp.data.model.DesignSubModel
import com.dmss.burbankapp.data.model.MostPopularHomesDTOModel
import com.dmss.burbankapp.databinding.PopularHomeDesignExpandableItemBinding
import com.dmss.burbankapp.ui.view.GlideImageLoader
import com.dmss.burbankapp.utils.AppConstants
import com.dmss.burbankapp.utils.DividerItemDecorator
import timber.log.Timber


class PopularHomeDesignExpandableAdapter(
    val listener: IDesignDetailItemClickListener
) :
    ListAdapter<MostPopularHomesDTOModel, PopularHomeDesignExpandableAdapter.ItemViewHolder>(
        PopularHomesComparator()
    ) {
    var housePrice: Double? = null

    init {
        housePrice = 0.0
    }

    private var options: RequestOptions? = null

    init {
        options = RequestOptions()
            .centerCrop()
            .priority(Priority.HIGH)

    }


    class PopularHomesComparator : DiffUtil.ItemCallback<MostPopularHomesDTOModel>() {
        override fun areItemsTheSame(
            oldItem: MostPopularHomesDTOModel,
            newItem: MostPopularHomesDTOModel
        ) =
            oldItem.Id == newItem.Id

        override fun areContentsTheSame(
            oldItem: MostPopularHomesDTOModel,
            newItem: MostPopularHomesDTOModel
        ): Boolean {
            return oldItem.houseName == newItem.houseName
        }

    }


    class ItemViewHolder(
        var binding: PopularHomeDesignExpandableItemBinding,
        val listener: IDesignDetailItemClickListener
    ) :
        RecyclerView.ViewHolder(binding.root), ExpandableSubList.IEstateClickListener {


        var adapter = ExpandableSubList(this)

        fun bind(model: MostPopularHomesDTOModel, options: RequestOptions?, expandView: Boolean) {
            var houseImageUrl: String? = model.imageUrl
            if (model.imageUrl.isNotEmpty()) {
                if (model.imageUrl.contains("~")) {
                    houseImageUrl = model.imageUrl.replace("~", "")
                }
            }
            houseImageUrl = "${AppConstants.PROFILEPIC_BASE}${houseImageUrl}".trim()
            GlideImageLoader(binding.ivPlace, binding.placeProgress).load(houseImageUrl, options)

            binding.tvPlace.text = ("${model.houseName} ${model.HouseSize}")
            binding.tvcar.text = model.CarSpace.toString()
            binding.tvBed.text = model.BedRooms.toString()
            binding.tvBath.text = model.BathRooms.toString()

            binding.recyclerSublist.layoutManager =
                LinearLayoutManager(binding.recyclerSublist.context)

            val dividerItemDecoration: ItemDecoration =
                DividerItemDecorator(
                    ContextCompat.getDrawable(
                        binding.recyclerSublist.context,
                        R.drawable.divider_recycler_view
                    )
                )

            Timber.e("Recyclerview adapter position : ---->$adapterPosition")
            Timber.e("Recyclerview adapter position new : ---->$expandView")
            if (adapterPosition == 0 && expandView) {
                binding.elParent.expand()
            } else {
                binding.elParent.collapse()
            }
            binding.recyclerSublist.addItemDecoration(dividerItemDecoration)
            binding.recyclerSublist.adapter = adapter
            binding.rlFirst.setOnClickListener {

                if (binding.elParent.isExpanded) {
                    binding.elParent.collapse()
                } else {
                    binding.elParent.expand()
                }

            }
            if (model.locations.isNotEmpty()) {
                binding.tvLocations.visibility = View.VISIBLE
                if (model.locations.size == 1) {
                    binding.tvLocations.text = ("${model.locations.size} LOCATION")
                } else {
                    binding.tvLocations.text = ("${model.locations.size} LOCATIONS")
                }
                var locations: ArrayList<DesignSubModel> = ArrayList()
                for (data in model.locations) {
                    data.price = model.HousePrice
                    data.CarSpace = model.CarSpace
                    data.BedRooms = model.BedRooms
                    data.BathRooms = model.BathRooms
                    locations.add(data)

                }
                adapter.updatedList(locations)
            } else {
                binding.tvLocations.visibility = View.INVISIBLE
            }
        }


        override fun estateItemClick(estate: DesignSubModel) {
            listener.onDesignItemClickListener(estate)
        }


    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemViewHolder {
        val binding =
            PopularHomeDesignExpandableItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return ItemViewHolder(binding, listener)

    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val currentItem = getItem(position)
        if (currentItem != null) {
            holder.bind(currentItem, options, holder.adapterPosition == 0)
        }
    }

    interface IDesignDetailItemClickListener {
        fun onDesignItemClickListener(model: DesignSubModel)
    }
}


