package com.coyni.pos.app.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.coyni.pos.app.R
import com.coyni.pos.app.baseclass.BaseRecyclerViewAdapter
import com.coyni.pos.app.baseclass.OnItemClickListener
import com.coyni.pos.app.databinding.TxnStepperItemBinding
import com.coyni.pos.app.model.web_socket.StepperItem
import com.coyni.pos.app.utils.Utils

class TxnStepperAdapter(val context: Context, var stepperData: List<StepperItem>) :
    BaseRecyclerViewAdapter<TxnStepperAdapter.MyViewHolder>() {
    private var listener: OnItemClickListener? = null

    override fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val binding: TxnStepperItemBinding =
            TxnStepperItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: MyViewHolder,
        position: Int
    ) {
        when (stepperData.get(position).status) {
            Utils.transCompleted -> {
                holder.binding.loaderView.setAnimation(R.raw.stepper_completed)
                holder.binding.loaderView.loop(!stepperData.get(position).isCompleted)
            }
            Utils.transFailed -> {
                holder.binding.loaderView.setAnimation(R.raw.stepper_failed)
                holder.binding.loaderView.loop(!stepperData.get(position).isCompleted)
            }
            else -> holder.binding.loaderView.setAnimation(R.raw.stepper_loader)
        }

        //Divider code
        if (position == stepperData.size - 1) {
            holder.binding.dividerCV.visibility = View.GONE
            holder.binding.loaderView.loop(true)
        } else {
            holder.binding.dividerCV.visibility = View.VISIBLE
        }
        if (stepperData.get(position).header != "")
            holder.binding.headerTV.setText(stepperData.get(position).header)
        if (stepperData.get(position).content != "") {
            holder.binding.contentTV.visibility = View.VISIBLE
            holder.binding.contentTV.setText(stepperData.get(position).content)
        } else holder.binding.contentTV.visibility = View.GONE
    }


    override fun getItemCount(): Int {
        return this.stepperData.size
    }

    fun updateList(stepperData: List<StepperItem>) {
        this.stepperData = stepperData

        notifyDataSetChanged()
    }

    class MyViewHolder(val binding: TxnStepperItemBinding) :
        RecyclerView.ViewHolder(binding.root)
}