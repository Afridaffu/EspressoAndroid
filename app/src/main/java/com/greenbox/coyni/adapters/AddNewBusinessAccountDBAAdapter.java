package com.greenbox.coyni.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.greenbox.coyni.R;
import com.greenbox.coyni.utils.MyApplication;

import java.util.List;

public class AddNewBusinessAccountDBAAdapter extends RecyclerView.Adapter<AddNewBusinessAccountDBAAdapter.MyViewHolder> {
    List<String> listCompany;
    Context mContext;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txvCompanyName;
        View viewLine;

        public MyViewHolder(View view) {
            super(view);
            txvCompanyName = (TextView) view.findViewById(R.id.txv_comapny_name);
            viewLine = (View) view.findViewById(R.id.viewLine);
        }
    }


    public AddNewBusinessAccountDBAAdapter(List<String> list, Context context) {
        this.mContext = context;
        this.listCompany = list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.business_comapany_listitem, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        try {
            holder.txvCompanyName.setText(listCompany.get(position));
            if (position == listCompany.size() - 1) {
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
        return listCompany.size();
    }

    public void updateList(List<String> list) {
        listCompany = list;
        notifyDataSetChanged();
    }

}
