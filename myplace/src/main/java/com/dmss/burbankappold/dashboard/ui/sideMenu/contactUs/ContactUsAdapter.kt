package com.dmss.burbankappold.dashboard.ui.sideMenu.contactUs

import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.dmss.burbankappold.R
import com.dmss.burbankappold.databinding.*
import com.dmss.burbankappold.utils.*
import com.google.gson.Gson
import common.AppController
import kotlinx.coroutines.flow.combine
import models.contactUs.ContactUsData
import models.contactUs.ContactUsDataItem
import models.history.MyHistoryItem
import models.infoCentre.InfoCenterData
import models.photos.PhotosDataItem
import java.util.*
import kotlin.collections.ArrayList

class ContactUsAdapter(val callBack: (NotesData) -> Unit) : RecyclerView.Adapter<ContactUsAdapter.ViewHolder>(), Filterable {
    var mList = listOf<NotesData>()
    var mFilteredList = listOf<NotesData>()

    fun setContactsList(list: List<NotesData>){
        mList = list
        mFilteredList = list
        notifyDataSetChanged()
    }


    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charString = constraint?.toString() ?: ""
                mFilteredList = if (charString.isEmpty()) mList else {
                    val filteredList = ArrayList<NotesData>()
                    mList
                        .filter {
                            (it.unknownAuthor?.lowercase()?.contains(charString.lowercase()))?.or((it.unknownAuthor.contains(charString.lowercase()))) == true
                        }
                        .forEach { filteredList.add(it) }
                    filteredList
                }
                return FilterResults().apply { values = mFilteredList }
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                mFilteredList = if (results?.values == null)
                    ArrayList()
                else results.values as ArrayList<NotesData>
                notifyDataSetChanged()
            }
        }
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = ItemContactUsLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mFilteredList[position])
    }

    override fun getItemCount(): Int {
        return mFilteredList.size
    }

    inner class ViewHolder(val binding: ItemContactUsLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(contactUsDataItem: NotesData) {
            var subject=contactUsDataItem?.subject?.ifEmpty { "--" }
            var body=""
            var title=""

            title = if(contactUsDataItem?.author?.fullName==null || contactUsDataItem?.author.fullName==""){
                "--"
            }else{
                contactUsDataItem?.author?.fullName
            }
            /* var username="--"
             if(AppController.controller?.userProfile!!.userDetails[0]!=null) {
                 username = AppController.controller?.userProfile!!.userDetails[0].fullName
             }
 */
            binding.tvSubject.text = contactUsDataItem?.subject?.ifEmpty { "--" } +" ("+contactUsDataItem?.noteId+")"
            var username="--"
            if(AppController.controller?.userProfile!!.userDetails[0]!=null) {
                username = AppController.controller?.userProfile!!.userDetails[0].fullName
            }
            if(contactUsDataItem?.createdInMyHome){
//                binding.tvTitle.text = username+" - "+contactUsDataItem.noteId
                binding.tvTitle.text = username

            }else {
//                binding.tvTitle.text = title+" - "+contactUsDataItem.noteId+" - "+binding.root.context.getString(R.string.burbank_homes)
                binding.tvTitle.text = title+" - "+binding.root.context.getString(R.string.burbank_homes)

            }

            binding.tvSubject.text = subject
            if(contactUsDataItem?.subject==null || contactUsDataItem?.subject==""){
                binding.tvSubject.text =  "--"
            }
            body = if(contactUsDataItem?.author?.fullName==null){
                ""+contactUsDataItem?.body
            }else {
                contactUsDataItem?.body + ""
            }

            binding.tvDescription.text=body
            val mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(binding.view.context)
            val isitemViewed=mSharedPreferences.getBoolean(""+contactUsDataItem?.noteId+"_"+contactUsDataItem?.subject,false)
//            println("userProfile::  "+Gson().toJson(AppController.controller?.userProfile!!.userDetails[0].fullName))
            /*  binding.tvTitle.text = username.replaceFirstChar {
                  if (it.isLowerCase()) it.titlecase(
                      Locale.getDefault()
                  ) else it.toString()
              }*/
            if(isitemViewed){
                binding.tvDot.visibility=View.GONE
            }
            if(contactUsDataItem.activityDate==null || contactUsDataItem?.activityDate==""){
                binding.tvDate.text =  "--"

            }else {
                if(contactUsDataItem.replies?.isEmpty() == true) {
                    binding.tvDate.text ="Created on "+
                            contactUsDataItem.activityDate!!.convertDateFormat(ACTUAL_DATE_FORMAT)
                }else{
                    binding.tvDate.text ="Replied on "+
                            contactUsDataItem.replies?.get(0)?.activityDate?.convertDateFormat(ACTUAL_DATE_FORMAT)
                }
                if(contactUsDataItem.replies?.size!!>0){
                    binding.tvDate.text ="Replied on "+ contactUsDataItem.replies!![0].activityDate?.convertDateFormat(ACTUAL_DATE_FORMAT)
                }else {
                    binding.tvDate.text =
                        "Created on "+contactUsDataItem.activityDate?.convertDateFormat(ACTUAL_DATE_FORMAT)
                }
            }
            binding.root.setOnClickListener {
                callBack.invoke(contactUsDataItem)
                AppConstants.selectedTitlte=binding.tvTitle.text.toString()
            }
        }
    }
    fun filterList(filterlist: List<NotesData>) {
        // below line is to add our filtered
        // list in our course array list.
        mFilteredList = filterlist
        // below line is to notify our adapter
        // as change in recycler view data.
        notifyDataSetChanged()
    }
}