package com.coyni.pos.app.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.SystemClock
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
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
    var lastClickTime = 0L

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
                respList?.get(position)?.customProperties?.status + ":"
            )
            when (respList?.get(position)?.customProperties?.status!!) {
                Utils.Completed, Utils.transSuccessful -> {
//                    holder.binding.statusTV.setTextColor(
//                        context.getResources().getColor(R.color.true_green)
//                    )
//                    holder.binding.statusTV.setBackgroundResource(R.drawable.txn_completed_bg)
                }
                Utils.transInprogress -> {
//                    holder.binding.statusTV.setTextColor(
//                        context.getResources().getColor(R.color.inprogress_status)
//                    )
//                    holder.binding.statusTV.setBackgroundResource(R.drawable.txn_inprogress_bg)
                }
                Utils.refunded,
                Utils.PARTIAL_REFUND,
                Utils.transPending -> {
//                    holder.binding.statusTV.setTextColor(
//                        context.getResources().getColor(R.color.pending_status)
//                    )
//                    holder.binding.statusTV.setBackgroundResource(R.drawable.txn_pending_bg)
                }
                Utils.transFailed -> {
//                    holder.binding.statusTV.setTextColor(
//                        context.getResources().getColor(R.color.failed_status)
//                    )
//                    holder.binding.statusTV.setBackgroundResource(R.drawable.txn_failed_bg)
                }
            }
        }

        if (respList?.get(position)?.createdAt != "") {
            val date: String = respList?.get(position)?.createdAt!!
//            if (date.contains(".")) {
//                val resDate = date.substring(0, date.lastIndexOf("."))
            holder.binding.date.text =
                myApplication?.mCurrentUserData?.convertZoneDateTime(
                    date,
                    dateAndTime,
                    requiredFormat
                )?.toLowerCase()
//            } else {
//            }
        }

        if (respList?.get(position)!!.message != null) {
            val ss =
                SpannableString(respList?.get(position)!!.message + " View Refund Transaction")

            val clickableSpan: ClickableSpan = object : ClickableSpan() {
                override fun onClick(view: View) {
                    try {
                        if (SystemClock.elapsedRealtime() - lastClickTime < 2000) {
                            return
                        }
                        lastClickTime = SystemClock.elapsedRealtime()
                        listener?.onItemClick(position, "")
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                    }
                }
                override fun updateDrawState(ss: TextPaint) {
                    super.updateDrawState(ss)
                    ss.isUnderlineText = false
                }
            }
            ss.setSpan(
                ForegroundColorSpan(Color.parseColor("#00A6A2")),
                ss.indexOf("View Refund Transaction"),
                ss.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            ss.setSpan("",
                ss.indexOf("View Refund Transaction"),
                ss.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            ss.setSpan(
                StyleSpan(Typeface.BOLD),
                ss.indexOf("View Refund Transaction"),
                ss.length,
                Spannable.SPAN_EXCLUSIVE_INCLUSIVE
            )
            ss.setSpan(clickableSpan, ss.indexOf("View Refund Transaction"),
                ss.length, 0)

            holder.binding.messageTv.setText(ss)
            holder.binding.messageTv.setMovementMethod(LinkMovementMethod.getInstance())

//            holder.binding.messageTv.setMovementMethod(LinkMovementMethod.getInstance())
//            holder.binding.messageTv.setHighlightColor(Color.TRANSPARENT)
        }

//        holder.binding.messageTv.setOnClickListener {
//            listener?.onItemClick(position, "")
//        }
    }

    override fun getItemCount(): Int {
        return if (respList == null || respList?.size == null) {
            0
        } else respList?.size!!
    }


    class MyViewHolder(val binding: ActivityLogItemsBinding) :
        RecyclerView.ViewHolder(binding.root)
}



