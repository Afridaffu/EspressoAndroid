package com.greenbox.coyni.adapters;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.greenbox.coyni.R;
import com.greenbox.coyni.model.users.TimeZoneModel;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.view.business.DBAInfoAcivity;

import java.util.List;

public class CustomerTimeZonesAdapter extends RecyclerView.Adapter<CustomerTimeZonesAdapter.MyViewHolder> {
    List<TimeZoneModel> listCountries;
    Context mContext;
    MyApplication objMyApplication;
    String from;
    Dialog dialog;
    EditText editText;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        //        public TextInputEditText etCurrencyPST;
        TextView tvPreference;
        ImageView tickIcon;
        View viewLine;

        public MyViewHolder(View view) {
            super(view);
//            etCurrencyPST = (TextInputEditText) view.findViewById(R.id.etCurrencyPST);
            tvPreference = (TextView) view.findViewById(R.id.tvPreference);
            tickIcon = (ImageView) view.findViewById(R.id.tickIcon);
            viewLine = (View) view.findViewById(R.id.viewLine);
        }
    }


    public CustomerTimeZonesAdapter(List<TimeZoneModel> list, Context context, String from, Dialog dialog, EditText editText) {
        this.mContext = context;
        this.listCountries = list;
        this.from = from;
        this.dialog = dialog;
        this.editText = editText;
        this.objMyApplication = (MyApplication) context.getApplicationContext();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.timezones_listitem, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        try {
            holder.tvPreference.setText(listCountries.get(position).getTimezone());
            if (listCountries.get(position).isSelected()) {
                holder.tickIcon.setVisibility(View.VISIBLE);
            } else {
                holder.tickIcon.setVisibility(View.GONE);
            }
            holder.tvPreference.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("text", "" + position);
                    objMyApplication.setTempTimezoneID(listCountries.get(position).getTimezoneID());
                    objMyApplication.setTempTimezone(listCountries.get(position).getTimezone());

                    if (from.equals("DBA_INFO")) {
                        objMyApplication.setTimezone(objMyApplication.getTempTimezone());
                        objMyApplication.setTimezoneID(objMyApplication.getTempTimezoneID());
                        editText.setText(objMyApplication.getTimezone());
                        DBAInfoAcivity dbaInfoAcivity = (DBAInfoAcivity) mContext;
                        dbaInfoAcivity.isTimeZone = true;
                        dbaInfoAcivity.enableOrDisableNext();
                        dialog.dismiss();
                    } else {
                        for (int i = 0; i < listCountries.size(); i++) {
                            Log.e("Pos, i", position + " " + i);
                            listCountries.get(i).setSelected(position == i);
                        }

                        notifyDataSetChanged();
                    }
                }
            });

            if (position == listCountries.size() - 1) {
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
        return listCountries.size();
    }

    public void updateList(List<TimeZoneModel> list) {
        listCountries = list;
        notifyDataSetChanged();
    }

}
