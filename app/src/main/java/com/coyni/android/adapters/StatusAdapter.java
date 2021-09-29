package com.coyni.android.adapters;

import android.content.Context;
import android.graphics.Color;
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
import com.coyni.android.utils.Utils;

import java.util.List;

public class StatusAdapter extends RecyclerView.Adapter<StatusAdapter.MyViewHolder> {
    private List<Status> listStatus;
    Context mContext;
    MyApplication objContext;
    TokenFragment fragment;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvStatus;
        public CardView cvStatus;
        public RelativeLayout layoutItem;
        public ImageView imgRadio;

        public MyViewHolder(View view) {
            super(view);
            tvStatus = (TextView) view.findViewById(R.id.tvStatus);
            cvStatus = (CardView) view.findViewById(R.id.cvStatus);
            imgRadio = (ImageView) view.findViewById(R.id.imgRadio);
            layoutItem = (RelativeLayout) view.findViewById(R.id.layoutItem);
        }
    }


    public StatusAdapter(List<Status> list, Context context, TokenFragment fragment) {
        this.mContext = context;
        this.listStatus = list;
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
            Status objData = listStatus.get(position);
            holder.tvStatus.setText(objData.getStatusName());
            if (objContext.getStatusId() == objData.getStatusId()) {
                holder.imgRadio.setBackgroundResource(R.drawable.ic_radio_select);
            } else {
                holder.imgRadio.setBackgroundResource(R.drawable.ic_radio_unselect);
            }
            switch (objData.getStatusName().toLowerCase().replace(" ", "")) {
                case Utils.transInProgress:
                    holder.cvStatus.setCardBackgroundColor(Color.parseColor("#2196F3"));
                    break;
                case Utils.transPending:
                    holder.cvStatus.setCardBackgroundColor(Color.parseColor("#A5A5A5"));
                    break;
                case Utils.transCompleted:
                    holder.cvStatus.setCardBackgroundColor(Color.parseColor("#00CC6E"));
                    break;
                case Utils.transFailed:
                    holder.cvStatus.setCardBackgroundColor(Color.parseColor("#D45858"));
                    break;
            }
            holder.layoutItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (objContext.getStatusId() != listStatus.get(position).getStatusId()) {
                        objContext.setStatusId(listStatus.get(position).getStatusId());
                    } else {
                        objContext.setStatusId(-1);
                    }
                    ((TokenFragment) fragment).removeFocus();
                    notifyDataSetChanged();
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return listStatus.size();
    }
}



