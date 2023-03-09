package com.coyni.pos.app.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.coyni.pos.app.baseclass.BaseRecyclerViewAdapter
import com.coyni.pos.app.baseclass.OnItemClickListener
import com.coyni.pos.app.databinding.RecentTxnsListBinding
import com.coyni.pos.app.model.TransactionFilter.TransactionItems
import com.coyni.pos.app.utils.MyApplication

class RecentTransactionsListAdapter (): BaseRecyclerViewAdapter<RecentTransactionsListAdapter.MyViewHolder>() {

    private var listener: OnItemClickListener?= null
    var recentTxns: List<TransactionItems>? = null
    private lateinit var mContext: Context
    private lateinit var objMyApplication : MyApplication


    override fun setOnItemClickListener(listener: OnItemClickListener) {
       this.listener = listener
    }

    constructor(context: Context, list: List<TransactionItems>?) : this() {
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

        val itemViewHolder: MyViewHolder = holder

        var objData: TransactionItems = recentTxns!!.get(position)

        setItemViewData(objData, itemViewHolder, position)

            holder.binding.llClick.setOnClickListener{
            listener?.onItemClick(position, "abc")
        }
    }

    private fun setItemViewData(objData: TransactionItems, holder: MyViewHolder, position: Int) {

        if(objData!=null){
//            holder.binding.dateTV.setText(objData?.createdAt)

//            holder.binding.descriptionTV.setText(objData?.txnTypeDn + " - " + objData?.txnSubTypeDn)
//
//            holder.binding.amountTV.setText(objData?.amount)
        }



    }

    override fun getItemCount(): Int {

        return 10
    }

    class MyViewHolder(val binding: RecentTxnsListBinding) :
        RecyclerView.ViewHolder(binding.root)
}