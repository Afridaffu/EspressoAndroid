package com.greenbox.coyni.adapters;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.greenbox.coyni.R;
import com.greenbox.coyni.model.DBAInfo.BusinessType;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.view.business.DBAInfoAcivity;

import java.util.List;

public class BusinessTypeListAdapter extends RecyclerView.Adapter<BusinessTypeListAdapter.MyViewHolder> {
    List<BusinessType> listBT;
    Context mContext;
    MyApplication objMyApplication;
    EditText mET;
    Dialog dialog;
    DBAInfoAcivity dbaInfoAcivity;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvBT, tvCode;
        ImageView tickIcon;
        View viewLine;

        public MyViewHolder(View view) {
            super(view);
            tvBT = (TextView) view.findViewById(R.id.tvState);
            tickIcon = (ImageView) view.findViewById(R.id.tickIcon);
            viewLine = (View) view.findViewById(R.id.viewLine);
        }
    }


    public BusinessTypeListAdapter(List<BusinessType> list, Context context, EditText editText, Dialog dialog) {
        this.mContext = context;
        this.listBT = list;
        this.mET = editText;
        this.dialog = dialog;
        this.dbaInfoAcivity = (DBAInfoAcivity) context;
        this.objMyApplication = (MyApplication) context.getApplicationContext();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.countrieslistitem, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        try {
            BusinessType objData = listBT.get(position);
            holder.tvBT.setText(objData.getKey());
            if (listBT.get(position).isSelected()) {
                holder.tickIcon.setVisibility(View.VISIBLE);
            } else {
                holder.tickIcon.setVisibility(View.GONE);
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
//                        for (int i = 0; i < listBT.size(); i++) {
//                            if (position == i) {
//                                listBT.get(i).setSelected(true);
//                            } else {
//                                listBT.get(i).setSelected(false);
//                            }
//                        }
//                        notifyDataSetChanged();

                        mET.setText(objData.getKey());
                        dialog.dismiss();

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            if (position == listBT.size() - 1) {
                holder.viewLine.setVisibility(View.GONE);
            } else {
                holder.viewLine.setVisibility(View.VISIBLE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return listBT.size();
    }

    public void updateList(List<BusinessType> list) {
        listBT = list;
        notifyDataSetChanged();
    }

}

