package com.dmss.burbankappold.homecare.manuals
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.digitalminds.homecare.model.ManualDataIteam
import com.dmss.burbankappold.databinding.ManualItemsBinding
import com.dmss.burbankappold.utils.DOCUMENTS_FORMAT
import com.dmss.burbankappold.utils.convertDateFormat


class ManualsAdapter(val callBack: (Int, ManualDataIteam) -> Unit) :
    RecyclerView.Adapter<ManualsAdapter.ViewHolder>(), Filterable {
    var mDocumentsList = listOf<ManualDataIteam>()
    var mDocumentsFilteredList = listOf<ManualDataIteam>()

    fun setDocumentsList(list: List<ManualDataIteam>) {
        this.mDocumentsList = list
        mDocumentsFilteredList = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = ManualItemsBinding.inflate(
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

    inner class ViewHolder(val binding: ManualItemsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(documents: ManualDataIteam) {
            binding.tvDocumentName.text = "${documents.title}.${documents.type}"
            binding.tvDate.text =documents.docdate
              /*  binding.tvDate.text = String.format(
                "Uploaded on: %s",
                documents.docdate.convertDateFormat(
                    DOCUMENTS_FORMAT
                )
            )*/
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
                    val filteredList = ArrayList<ManualDataIteam>()
                    mDocumentsList
                        .filter {
                            var docdate= String.format(
                                "Uploaded on: %s",
                                it.docdate.convertDateFormat(
                                    DOCUMENTS_FORMAT
                                )
                            )
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
                    results.values as List<ManualDataIteam>
                notifyDataSetChanged()
            }
        }
    }
}