package com.greenbox.coyni.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.greenbox.coyni.R;
import com.greenbox.coyni.model.States;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.view.EditAddressActivity;

import java.util.List;

public class StatesListAdapter extends RecyclerView.Adapter<StatesListAdapter.MyViewHolder> {
    List<States> listStates;
    Context mContext;
    MyApplication objMyApplication;
    String strScreen = "";

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvCountry, tvCode;

        public MyViewHolder(View view) {
            super(view);
            tvCountry = (TextView) view.findViewById(R.id.tvCountry);
            tvCode = (TextView) view.findViewById(R.id.tvCode);
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
            holder.tvCountry.setText(objData.getName());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (strScreen.equals("EditAddress")) {
                            ((EditAddressActivity) mContext).populateState(objData.getName());
                        }
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
        return listStates.size();
    }

    public void updateList(List<States> list) {
        listStates = list;
        notifyDataSetChanged();
    }

}

