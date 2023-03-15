package com.coyni.pos.app.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.coyni.pos.app.R
import com.coyni.pos.app.baseclass.BaseRecyclerViewAdapter
import com.coyni.pos.app.baseclass.OnItemClickListener
import com.coyni.pos.app.databinding.RecentTxnsListBinding
import com.coyni.pos.app.model.TransactionFilter.TransactionItem
import com.coyni.pos.app.utils.MyApplication
import com.coyni.pos.app.utils.Utils

class RecentTransactionsListAdapter() :
    BaseRecyclerViewAdapter<RecentTransactionsListAdapter.MyViewHolder>() {

    private var listener: OnItemClickListener? = null
    private lateinit var mContext: Context
    private lateinit var objMyApplication: MyApplication
    private var recentTxns: List<TransactionItem>? = null

    override fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    constructor(context: Context, list: List<TransactionItem>?) : this() {
        this.mContext = context
        this.recentTxns = list
        this.objMyApplication = context.applicationContext as MyApplication
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val binding: RecentTxnsListBinding =
            RecentTxnsListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        var item: TransactionItem? = recentTxns?.get(position)
        val itemViewHolder: MyViewHolder = holder

        objMyApplication.mCurrentUserData.UserType = item?.userType

        if (item != null) {
            holder.binding.descriptionTV.text = item?.txnTypeDn + " - " + item?.txnSubTypeDn

            if (item?.txnTypeDn.equals(Utils.Refund, ignoreCase = true)) {
                holder.binding.amountTV.text =
                    "-" + Utils.convertTwoDecimal(item?.amount!!).split(" ")[0]
                holder.binding.amountTV.setTextColor(mContext.resources.getColor(R.color.black))
            } else if (item?.txnTypeDn.equals(Utils.SALE_ORDER, ignoreCase = true)) {
                holder.binding.amountTV.text =
                    "+" + Utils.convertTwoDecimal(item?.amount!!).split(" ")[0]
                holder.binding.amountTV.setTextColor(mContext.resources.getColor(R.color.true_green))
            }
//                holder.binding.amountTV.text = item?.amount

            if (item?.createdAt != null && !item?.createdAt.equals("")) {
                val date: String = item?.createdAt!!
                if (date.contains(".")) {
                    val resDate = date.substring(0, date.lastIndexOf("."))
                    holder.binding.dateTV.setText(
                        objMyApplication?.mCurrentUserData?.convertZoneDateTime(
                            resDate,
                            "yyyy-MM-dd HH:mm:ss",
                            "MM/dd/yyyy hh:mma"
                        )?.toLowerCase()
                    )
                } else {
                }
            }

            holder.binding.llClick.setOnClickListener {
                listener?.onItemClick(position, "abc")
            }
        }
    }

    override fun getItemCount(): Int {
        if (objMyApplication.mCurrentUserData.validateResponseData?.empRole == Utils.EMPROLE) {
            return if (recentTxns!!.size > 10) 10
            else recentTxns!!.size
        } else {
           return recentTxns!!.size
        }
    }

    class MyViewHolder(val binding: RecentTxnsListBinding) :
        RecyclerView.ViewHolder(binding.root)
}