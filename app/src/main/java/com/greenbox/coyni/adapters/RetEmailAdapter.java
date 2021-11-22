package com.greenbox.coyni.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.greenbox.coyni.R;
import com.greenbox.coyni.model.States;
import com.greenbox.coyni.model.retrieveemail.RetUserResData;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.view.BindingLayoutActivity;
import com.greenbox.coyni.view.EditAddressActivity;
import com.greenbox.coyni.view.LoginActivity;

import java.util.List;

public class RetEmailAdapter extends RecyclerView.Adapter<RetEmailAdapter.MyViewHolder> {
    List<RetUserResData> listEmails;
    Context mContext;
    MyApplication objMyApplication;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvEmail;

        public MyViewHolder(View view) {
            super(view);
            tvEmail = (TextView) view.findViewById(R.id.tvEmail);
        }
    }


    public RetEmailAdapter(List<RetUserResData> list, Context context) {
        this.mContext = context;
        this.listEmails = list;
        this.objMyApplication = (MyApplication) context.getApplicationContext();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ret_email_list_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        try {
            RetUserResData objData = listEmails.get(position);
            holder.tvEmail.setText(objData.getEmail().replaceAll("(?<=.{4}).(?=.*@)", "*"));

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        objMyApplication.setStrRetrEmail(objData.getEmail());
                        Intent i = new Intent(mContext, LoginActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        mContext.startActivity(i);
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
        return listEmails.size();
    }


}

