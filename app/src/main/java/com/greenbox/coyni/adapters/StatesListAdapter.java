package com.greenbox.coyni.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.greenbox.coyni.R;
import com.greenbox.coyni.model.States;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.view.EditAddressActivity;

import java.util.List;

public class StatesListAdapter extends RecyclerView.Adapter<StatesListAdapter.MyViewHolder> {
    List<States> listStates;
    Context mContext;
    MyApplication objMyApplication;
    String strScreen = "";

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvState, tvCode;
        ImageView tickIcon;
        View viewLine;

        public MyViewHolder(View view) {
            super(view);
            tvState = (TextView) view.findViewById(R.id.tvState);
            tickIcon = (ImageView) view.findViewById(R.id.tickIcon);
            viewLine = (View) view.findViewById(R.id.viewLine);
        }
    }


    public StatesListAdapter(List<States> list, Context context, String screen) {
        this.mContext = context;
        this.listStates = list;
        this.objMyApplication = (MyApplication) context.getApplicationContext();
        this.strScreen = screen;
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
            States objData = listStates.get(position);
            holder.tvState.setText(objData.getIsocode());
            if (listStates.get(position).isSelected()) {
                holder.tickIcon.setVisibility(View.VISIBLE);
            } else {
                holder.tickIcon.setVisibility(View.GONE);
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Utils.tempState = objData.getIsocode();
                        for (int i = 0; i < listStates.size(); i++) {
                            if (position == i) {
                                listStates.get(i).setSelected(true);
                            } else {
                                listStates.get(i).setSelected(false);
                            }
                        }
                        notifyDataSetChanged();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            if (position == listStates.size() - 1) {
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
        return listStates.size();
    }

    public void updateList(List<States> list) {
        listStates = list;
        notifyDataSetChanged();
    }

}

