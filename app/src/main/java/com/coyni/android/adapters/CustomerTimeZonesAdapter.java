package com.coyni.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.coyni.android.R;
import com.coyni.android.fragments.ProfileFragment;
import com.coyni.android.utils.MyApplication;

import java.util.List;

public class CustomerTimeZonesAdapter extends RecyclerView.Adapter<CustomerTimeZonesAdapter.MyViewHolder> {
    List<String> listCountries;
    Context mContext;
    MyApplication objMyApplication;
    ProfileFragment profileFragment = new ProfileFragment();

    public class MyViewHolder extends RecyclerView.ViewHolder {
        //        public TextInputEditText etCurrencyPST;
        TextView tvPreference;

        public MyViewHolder(View view) {
            super(view);
//            etCurrencyPST = (TextInputEditText) view.findViewById(R.id.etCurrencyPST);
            tvPreference = (TextView)view.findViewById(R.id.tvPreference);
        }
    }


    public CustomerTimeZonesAdapter(List<String> list, Context context) {
        this.mContext = context;
        this.listCountries = list;
        this.objMyApplication = (MyApplication) context.getApplicationContext();
    }

    @Override
    public CustomerTimeZonesAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.timezones_listitem, parent, false);
        return new CustomerTimeZonesAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CustomerTimeZonesAdapter.MyViewHolder holder, int position) {
        try {
            String objData = listCountries.get(position);
            holder.tvPreference.setText(objData);
            holder.tvPreference.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    ((ProfileFragment)profileFragment).populatePreferenceTimeZone(objData);
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

    public void updateList(List<String> list){
        listCountries = list;
        notifyDataSetChanged();
    }

}
