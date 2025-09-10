package com.dmss.burbankappold.dashboard.ui.documents

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.dmss.burbankappold.databinding.ItemDocumentsBinding
import com.dmss.burbankappold.utils.DOCUMENTS_FORMAT
import com.dmss.burbankappold.utils.convertDateFormat
import models.photos.PhotosDataItem

class DocumentsAdapter(val callBack: (Int, PhotosDataItem) -> Unit) :
    RecyclerView.Adapter<DocumentsAdapter.ViewHolder>(), Filterable {
    var mDocumentsList = listOf<PhotosDataItem>()
    var mDocumentsFilteredList = listOf<PhotosDataItem>()

    fun setDocumentsList(list: List<PhotosDataItem>) {
        this.mDocumentsList = list
        mDocumentsFilteredList = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = ItemDocumentsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mDocumentsFilteredList[position])
    }

    override fun getItemCount(): Int {
        return mDocumentsFilteredList.size
    }

    inner class ViewHolder(val binding: ItemDocumentsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(documents: PhotosDataItem) {
            binding.tvDocumentName.text = documents.title+"."
            binding.tvDate.text = String.format(
                "Uploaded on: %s",
                documents.metaData.createdOn.convertDateFormat(
                    DOCUMENTS_FORMAT
                )
            )
                .replace("am", " AM")
                .replace("pm", " PM")

            binding.ivFavourite.setOnClickListener {
                callBack.invoke(0, documents)
            }
            binding.root.setOnClickListener {
                callBack.invoke(1, documents)
            }
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charString = constraint?.toString() ?: ""
                mDocumentsFilteredList = if (charString.isEmpty()) mDocumentsList else {
                    val filteredList = ArrayList<PhotosDataItem>()
                    mDocumentsList
                        .filter {
                            var docdate= String.format(
                                "Uploaded on: %s",
                                it.metaData.createdOn.convertDateFormat(
                                    DOCUMENTS_FORMAT
                                )
                            )
                            println("TITLE:: "+it.title+"  charString:: "+charString)
                            (it.title.lowercase().contains(charString.lowercase())) or
                                    (it.title.contains(charString.lowercase())) or
                                    (docdate.lowercase().contains(charString.lowercase()))
                        }
                        .forEach { filteredList.add(it) }
                    filteredList
                }
                return FilterResults().apply { values = mDocumentsFilteredList }
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                mDocumentsFilteredList = if (results?.values == null)
                    ArrayList()
                else
                    results.values as List<PhotosDataItem>
                notifyDataSetChanged()
            }
        }
    }

}