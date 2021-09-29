package com.coyni.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.coyni.android.R;
import com.coyni.android.fragments.SignetAccountFragment;
import com.coyni.android.fragments.UserAddressFragment;
import com.coyni.android.model.States;
import com.coyni.android.utils.MyApplication;
import com.coyni.android.view.AddEditCardDetailsActivity;
import com.coyni.android.view.AddressActivity;

import java.util.List;

public class StatesListAdapter extends RecyclerView.Adapter<StatesListAdapter.MyViewHolder> {
    List<States> listStates;
    Context mContext;
    MyApplication objMyApplication;
    String strScreen = "";
    SignetAccountFragment fragment;
    UserAddressFragment addressFragment;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvCountry, tvCode;

        public MyViewHolder(View view) {
            super(view);
            tvCountry = (TextView) view.findViewById(R.id.tvCountry);
            tvCode = (TextView) view.findViewById(R.id.tvCode);
        }
    }


    public StatesListAdapter(List<States> list, Context context, String screen, SignetAccountFragment frag, UserAddressFragment fragment) {
        this.mContext = context;
        this.listStates = list;
        this.objMyApplication = (MyApplication) context.getApplicationContext();
        this.strScreen = screen;
        this.fragment = frag;
        this.addressFragment = fragment;
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
                        if (strScreen.equals("add")) {
                            ((AddressActivity) mContext).populateState(objData.getName());
                        } else if (strScreen.equals("signet")) {
                            fragment.populateState(objData.getName());
                        } else if (strScreen.equals("edit")) {
                            ((AddEditCardDetailsActivity) mContext).populateState(objData.getName());
                        } else {
                            addressFragment.populateState(objData.getName());
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


