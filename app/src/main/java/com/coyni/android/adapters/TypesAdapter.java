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
import com.coyni.android.model.Status;
import com.coyni.android.utils.MyApplication;

import java.util.List;

public class TypesAdapter extends RecyclerView.Adapter<TypesAdapter.MyViewHolder> {
    private List<Status> listTypes;
    Context mContext;
    MyApplication objContext;

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


    public TypesAdapter(List<Status> list, Context context) {
        this.mContext = context;
        this.listTypes = list;
        this.objContext = (MyApplication) context.getApplicationContext();
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
            Status objData = listTypes.get(position);
            holder.tvStatus.setText(objData.getStatusName());
            holder.cvStatus.setVisibility(View.GONE);
            if (objContext.getTypeId() == objData.getStatusId()) {
                holder.imgRadio.setBackgroundResource(R.drawable.ic_radio_select);
            } else {
                holder.imgRadio.setBackgroundResource(R.drawable.ic_radio_unselect);
            }
            holder.layoutItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (objContext.getTypeId() != listTypes.get(position).getStatusId()) {
                        objContext.setTypeId(listTypes.get(position).getStatusId());
                    } else {
                        objContext.setTypeId(-1);
                    }
                    notifyDataSetChanged();
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return listTypes.size();
    }
}




