package com.coyni.pos.app.baseclass

import androidx.recyclerview.widget.RecyclerView

abstract class BaseRecyclerViewAdapter<T : RecyclerView.ViewHolder> :
    RecyclerView.Adapter<T>() {
    abstract fun setOnItemClickListener(onItemClickListener: OnItemClickListener)
}