package com.coyni.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.coyni.android.R;
import com.coyni.android.fragments.TokenFragment;
import com.coyni.android.model.Status;
import com.coyni.android.utils.MyApplication;

import java.util.List;

public class DateSelectionAdapter extends RecyclerView.Adapter<DateSelectionAdapter.MyViewHolder> {
    private List<Status> listDates;
    Context mContext;
    MyApplication objContext;
    TokenFragment fragment;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvStatus;
        public RelativeLayout layoutItem;
        public ImageView imgRadio, imgCheck;
        public CardView cvStatus;

        public MyViewHolder(View view) {
            super(view);
            tvStatus = (TextView) view.findViewById(R.id.tvStatus);
            imgRadio = (ImageView) view.findViewById(R.id.imgRadio);
            imgCheck = (ImageView) view.findViewById(R.id.imgCheck);
            layoutItem = (RelativeLayout) view.findViewById(R.id.layoutItem);
            cvStatus = (CardView) view.findViewById(R.id.cvStatus);
        }
    }


    public DateSelectionAdapter(List<Status> list, Context context, TokenFragment fragment) {
        this.mContext = context;
        this.listDates = list;
        this.objContext = (MyApplication) context.getApplicationContext();
        this.fragment = fragment;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.statuslistitem, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        try {
            Status objData = listDates.get(position);
            holder.cvStatus.setVisibility(View.GONE);
            holder.imgCheck.setVisibility(View.GONE);
            holder.imgRadio.setVisibility(View.VISIBLE);
            holder.tvStatus.setText(objData.getStatusName());
            if (objContext.getDateId() == objData.getStatusId()) {
                holder.imgRadio.setBackgroundResource(R.drawable.ic_radio_select);
            } else {
                holder.imgRadio.setBackgroundResource(R.drawable.ic_radio_unselect);
            }
            holder.layoutItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        objContext.setDateId(listDates.get(position).getStatusId());
                        notifyDataSetChanged();
                        fragment.enableCalendar(listDates.get(position).getStatusName());
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return listDates.size();
    }
}




