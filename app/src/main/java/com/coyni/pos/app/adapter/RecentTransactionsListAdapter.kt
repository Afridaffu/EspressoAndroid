package com.coyni.pos.app.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.coyni.pos.app.baseclass.BaseRecyclerViewAdapter
import com.coyni.pos.app.baseclass.OnItemClickListener
import com.coyni.pos.app.databinding.RecentTxnsListBinding

class RecentTransactionsListAdapter (val context: Context): BaseRecyclerViewAdapter<RecentTransactionsListAdapter.MyViewHolder>() {

    private var listener: OnItemClickListener?= null

    override fun setOnItemClickListener(listener: OnItemClickListener) {
       this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val binding: RecentTxnsListBinding =
            RecentTxnsListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.binding.llClick.setOnClickListener{
            listener?.onItemClick(position, "abc")
        }
    }

    override fun getItemCount(): Int {
        return 10
    }

    class MyViewHolder(val binding: RecentTxnsListBinding) :
        RecyclerView.ViewHolder(binding.root)
}