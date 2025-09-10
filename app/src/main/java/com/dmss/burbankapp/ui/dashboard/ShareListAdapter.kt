package com.dmss.burbankapp.ui.dashboard

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.dmss.burbankapp.R
import com.dmss.burbankapp.data.model.ShareListModel

class ShareListAdapter(
    var context: Context,
    var dataList: ArrayList<ShareListModel>,
    var shareAccountItemClick: ShareAccountItemClick
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ShareViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.share_account_list_item, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ShareViewHolder -> {
                holder.bind(dataList[position], context, shareAccountItemClick)
            }

        }
    }


    class ShareViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var email: TextView = itemView.findViewById(R.id.tv_email)
        var iv_pending: ImageView = itemView.findViewById(R.id.iv_pending)
        var iv_delete: ImageView = itemView.findViewById(R.id.iv_delete)
        var status: TextView = itemView.findViewById(R.id.status)
        var userName: TextView = itemView.findViewById(R.id.tvUsername)

        fun bind(
            shareListModel: ShareListModel,
            context: Context,
            shareAccountItemClick: ShareAccountItemClick
        ) {
            iv_pending.setOnClickListener {
                shareAccountItemClick.selectedFavoriteItem(shareListModel)
            }
            iv_delete.setOnClickListener {
                shareAccountItemClick.selectedDeleteItem(shareListModel)
            }
            if (shareListModel.fullName.isNotEmpty()) {
                userName.text = shareListModel.fullName
            }
            email.text = shareListModel.email

            when (shareListModel.acceptStatus) {
                0 -> {

                }
                1 -> {
                    if (shareListModel.favouriteAdded) {
                        status.text = "(Pending)"
                        iv_pending.setImageDrawable(
                            ContextCompat.getDrawable(
                                context,
                                R.drawable.pending_share
                            )
                        )
                    }

                }
                2 -> {
                    if (shareListModel.favouriteAdded) {
                        status.text = "(Accepted)"
                        iv_pending.setImageDrawable(
                            ContextCompat.getDrawable(
                                context,
                                R.drawable.share_favorite
                            )
                        )
                    } else {
                        iv_pending.setImageDrawable(
                            ContextCompat.getDrawable(
                                context,
                                R.drawable.unfavorite_share
                            )
                        )
                        status.text = "(Accepted)"
                    }
                }
            }
        }
    }

    fun setData(shareList: ArrayList<ShareListModel>) {
        if (shareList.size > 0) {
            dataList = shareList
            notifyDataSetChanged()
        }

    }


    interface ShareAccountItemClick {
        fun selectedFavoriteItem(shareListModel: ShareListModel)
        fun selectedDeleteItem(shareListModel: ShareListModel)
    }


}