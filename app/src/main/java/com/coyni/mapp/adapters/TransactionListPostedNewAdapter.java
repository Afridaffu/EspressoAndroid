package com.coyni.mapp.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.coyni.mapp.R;
import com.coyni.mapp.model.transaction.TransactionListPosted;
import com.coyni.mapp.utils.MyApplication;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class TransactionListPostedNewAdapter extends RecyclerView.Adapter<TransactionListPostedNewAdapter.MyViewHolder> {
    Context mContext;
    MyApplication objMyApplication;
    List<List<TransactionListPosted>> listedData = new ArrayList<>();
    List<TransactionListPosted> transactionListItemsposted;
    ArrayList<String> dates = new ArrayList<>();


    public TransactionListPostedNewAdapter(List<TransactionListPosted> list, Context context) {
        this.transactionListItemsposted = list;
        this.mContext = context;
        this.objMyApplication = (MyApplication) context.getApplicationContext();

        for (int i = 0; i < list.size(); i++) {
            String datee = objMyApplication.convertZoneDateLastYear(list.get(i).getUpdatedAt().split("\\.")[0]);
            if (!dates.contains(datee)) {
                dates.add(datee);
            }
        }

        for (int j = 0; j < dates.size(); j++) {
            List<TransactionListPosted> individualDateData = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                String datee = objMyApplication.convertZoneDateLastYear(list.get(i).getUpdatedAt().split("\\.")[0]);
                if (dates.get(j).equals(datee)) {
                    individualDateData.add(list.get(i));
                }
            }
            listedData.add(individualDateData);
        }

    }


    @NonNull
    @Override
    public TransactionListPostedNewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.posted_item, parent, false);

        return new TransactionListPostedNewAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionListPostedNewAdapter.MyViewHolder holder, int position) {
        if (position == 0) {
            holder.cyn.setVisibility(View.VISIBLE);
        } else {
            holder.cyn.setVisibility(View.GONE);
        }
        try {
            int currentYear = Calendar.getInstance().get(Calendar.YEAR);
            SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            spf.setTimeZone(TimeZone.getTimeZone("UTC"));
            String strCurDate = spf.format(Calendar.getInstance().getTime());
            if (dates.get(position).equals(objMyApplication.convertZoneDateLastYear(strCurDate))) {
                holder.date.setText("Today");
            } else {
                if (currentYear == Integer.parseInt(dates.get(position).split(",")[1].trim())) {
                    holder.date.setText(dates.get(position).split(",")[0].trim());
                } else {
                    holder.date.setText(dates.get(position));
                }
            }
            try {
                TransactionListPostedInnerAdapter transactionListPostedInnerAdapter = new TransactionListPostedInnerAdapter(listedData.get(position), mContext);
                LinearLayoutManager nLayoutManager = new LinearLayoutManager(mContext);
                holder.innerRV.setLayoutManager(nLayoutManager);
                holder.innerRV.setItemAnimator(new DefaultItemAnimator());
                holder.innerRV.setAdapter(transactionListPostedInnerAdapter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        Log.e("size", "" + dates.size());
        return dates.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView date, cyn;
        RecyclerView innerRV;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.dateTV);
            innerRV = itemView.findViewById(R.id.postedInnerRV);
            cyn = itemView.findViewById(R.id.tv_cyn);
        }

    }

    public void updateList(List<List<TransactionListPosted>> list) {
        listedData = list;
        notifyDataSetChanged();
    }

}
