package com.coyni.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.coyni.android.R;
import com.coyni.android.model.Countries;
import com.coyni.android.utils.MyApplication;
import com.coyni.android.view.AddressActivity;
import com.coyni.android.view.CreateAccountActivity;

import java.util.List;

public class CountriesListAdapter extends RecyclerView.Adapter<CountriesListAdapter.MyViewHolder> {
    List<Countries> listCountries;
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


    public CountriesListAdapter(List<Countries> list, Context context, String screen) {
        this.mContext = context;
        this.listCountries = list;
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
            Countries objData = listCountries.get(position);
            holder.tvCountry.setText(objData.getName());
            holder.tvCode.setText("(" + objData.getIsocode() + ")");
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (strScreen.equals("add")) {
                        ((AddressActivity) mContext).populateCountry(objData.getName());
                    } else {
                        ((CreateAccountActivity) mContext).populateCountry(objData.getIsocode());
                    }
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return listCountries.size();
    }

    public void updateList(List<Countries> list) {
        listCountries = list;
        notifyDataSetChanged();
    }

}

