package com.coyni.mapp.adapters;

import androidx.recyclerview.widget.RecyclerView;

public abstract class BaseRecyclerViewAdapter<T extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<T> {

    public abstract void setOnItemClickListener(OnItemClickListener listener);
}
