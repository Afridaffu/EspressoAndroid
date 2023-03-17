package com.coyni.pos.app.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.coyni.pos.app.R
import com.coyni.pos.app.baseclass.BaseRecyclerViewAdapter
import com.coyni.pos.app.baseclass.OnItemClickListener
import com.coyni.pos.app.databinding.ActivityLogItemsBinding
import com.coyni.pos.app.model.ActivityLogs.ActivityLogsResponseData
import com.coyni.pos.app.utils.MyApplication
import com.coyni.pos.app.utils.Utils

class ActivityLogAdapter(val context: Context, var respList: List<ActivityLogsResponseData>) :
    BaseRecyclerViewAdapter<ActivityLogAdapter.MyViewHolder>() {
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
        myApplication = context.applicationContext as MyApplication?
//        respList = myApplication?.mCurrentUserData?.activityLogsResponseData!!

        if (respList.get(position)?.customProperties?.status != null) {

            holder.binding.statusTV.setText(
                respList?.get(position)?.customProperties?.status
            )
            when (respList?.get(position)?.customProperties?.status!!
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
                Utils.refunded,
                Utils.PARTIAL_REFUND,
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

        if (respList?.get(position)?.createdAt.equals("")) {
            val date: String = respList?.get(position)?.createdAt!!
            if (date.contains(".")) {
                val resDate = date.substring(0, date.lastIndexOf("."))
                holder.binding.date.text =
                    myApplication?.mCurrentUserData?.convertZoneDateTime(
                        resDate,
                        dateAndTime,
                        requiredFormat
                    )?.toLowerCase()
            } else {
            }
        }

        if (respList?.get(position)!!.message != null) {
            holder.binding.messageTv.setText(respList?.get(position)!!.message)
        }
    }

    override fun getItemCount(): Int {
        return if (respList == null || respList?.size == null) {
            0
        } else respList?.size!!
    }


    class MyViewHolder(val binding: ActivityLogItemsBinding) :
        RecyclerView.ViewHolder(binding.root)
}
