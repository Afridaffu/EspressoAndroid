package com.coyni.pos.app.baseclass

import com.coyni.pos.app.adapter.TransactionFilterAdapter

interface OnItemClickListener : TransactionFilterAdapter.OnItemClickListener {
    fun onItemClick(position: Int?, value: Any?)
    override fun onGroupClicked(position: Int, childItems: String?) {
        TODO("Not yet implemented")
    }
}