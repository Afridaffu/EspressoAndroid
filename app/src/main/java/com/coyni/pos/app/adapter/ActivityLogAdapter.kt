package com.coyni.pos.app.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.coyni.pos.app.R
import com.coyni.pos.app.baseclass.BaseRecyclerViewAdapter
import com.coyni.pos.app.baseclass.OnItemClickListener
import com.coyni.pos.app.databinding.ActivityLogItemsBinding
import com.coyni.pos.app.model.ActivityLogs.ActivityLogsResponse
import com.coyni.pos.app.utils.MyApplication
import com.coyni.pos.app.utils.Utils

class ActivityLogAdapter(val context: Context) :
    BaseRecyclerViewAdapter<ActivityLogAdapter.MyViewHolder>() {
    var respList: ActivityLogsResponse? = null
    var myApplication: MyApplication? = null
    private var listener: OnItemClickListener? = null
    private val dateAndTime = "yyyy-MM-dd HH:mm:ss"
    private val requiredFormat = "MM/dd/yyyy h:mma"

    override fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.listener = onItemClickListener
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val binding: ActivityLogItemsBinding =
            ActivityLogItemsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: MyViewHolder,
        position: Int
    ) {
        myApplication = context?.applicationContext as MyApplication?

        if (respList?.data?.get(position)?.propetyData?.status != null) {

            holder.binding.statusTV.setText(
                respList?.data?.get(position)?.propetyData?.status
            )
            when (respList?.data?.get(position)?.propetyData?.status!!
                .toLowerCase()) {
                Utils.transCompleted, Utils.transSuccessful -> {
                    holder.binding.statusTV.setTextColor(
                        context.getResources().getColor(R.color.true_green)
                    )
                    holder.binding.statusTV.setBackgroundResource(R.drawable.txn_completed_bg)
                }
                Utils.transInprogress -> {
                    holder.binding.statusTV.setTextColor(
                        context.getResources().getColor(R.color.inprogress_status)
                    )
                    holder.binding.statusTV.setBackgroundResource(R.drawable.txn_inprogress_bg)
                }
                Utils.transPending -> {
                    holder.binding.statusTV.setTextColor(
                        context.getResources().getColor(R.color.pending_status)
                    )
                    holder.binding.statusTV.setBackgroundResource(R.drawable.txn_pending_bg)
                }
                Utils.transFailed -> {
                    holder.binding.statusTV.setTextColor(
                        context.getResources().getColor(R.color.failed_status)
                    )
                    holder.binding.statusTV.setBackgroundResource(R.drawable.txn_failed_bg)
                }
            }
        }


        if (respList?.data?.get(position)?.createdAt != null) {
            holder.binding.date.setText(
                myApplication?.mCurrentUserData?.convertZoneDateTime(
                    respList?.data?.get(position)!!.createdAt, dateAndTime, requiredFormat
                )
                    ?.toLowerCase()
            )
        }
        if (respList?.data?.get(position)!!.message != null) {
            holder.binding.messageTv.setText(respList?.data?.get(position)!!.message)
        }
    }

    override fun getItemCount(): Int {
        return if (respList == null || respList?.data?.size == null) {
            0
        } else respList?.data?.size!!
    }


    class MyViewHolder(val binding: ActivityLogItemsBinding) :
        RecyclerView.ViewHolder(binding.root)
}
