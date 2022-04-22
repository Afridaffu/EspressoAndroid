package com.greenbox.coyni.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.greenbox.coyni.R;
import com.greenbox.coyni.model.DateItem;
import com.greenbox.coyni.model.ListItem;
import com.greenbox.coyni.model.reservemanual.ManualItem;
import com.greenbox.coyni.model.transaction.TransactionListPosted;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ReserveDetailsListAdapter extends BaseRecyclerViewAdapter<ReserveDetailsListAdapter.MyViewHolder>{

    private Context context;
    private OnItemClickListener listener;
    private MyApplication objMyApplication;
    private List<TransactionListPosted> transactionListItemsPosted;

    public  ReserveDetailsListAdapter(List<TransactionListPosted> list, Context context) {
        this.transactionListItemsPosted = list;
        this.context = context;
        this.objMyApplication = (MyApplication) context.getApplicationContext();
    }

    @Override
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View detailsList = inflater.inflate(R.layout.reserve_recent_transactions, parent, false);
        MyViewHolder myViewHolder =new MyViewHolder(detailsList);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        TransactionListPosted posted = transactionListItemsPosted.get(position);
        if (posted.getTxnTypeDn()!= null && !posted.getTxnTypeDn().equals("")) {
            holder.name.setText(posted.getTxnTypeDn() + " - ");
        }
        if (posted.getAmount()!= null && !posted.getAmount().equals("")) {
            String amt="";
            amt = posted.getAmount().trim();
            if (amt.contains(" ")) {
                amt = amt.substring(0, amt.lastIndexOf(" "));
            }
            holder.amount.setText("-"+Utils.convertTwoDecimal(amt));
        }
        if (posted.getWalletBalance()!= null && !posted.getWalletBalance().equals("")) {
            holder.amountTV.setText("Amount: "+Utils.convertTwoDecimal(String.valueOf(posted.getWalletBalance())));
        }
        if (posted.getUpdatedAt()!=null && !posted.getUpdatedAt().equals("")) {

            String timeDate = "";
            timeDate = posted.getUpdatedAt();
            if (timeDate.contains(".")) {
                timeDate = timeDate.substring(0, timeDate.lastIndexOf("."));
            }
            timeDate = objMyApplication.convertZoneDateTime(timeDate, "yyyy-MM-dd HH:mm:ss", "MM/dd/yyyy hh:mma");
//            holder.dateTime.setText(Utils.convertPayoutDate(timeDate));
            holder.dateTime.setText(timeDate);
        }
    }

    @Override
    public int getItemCount() {
        return transactionListItemsPosted.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public RelativeLayout rlBase;
        public TextView amount, amountTV, dateTime, name;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.nameTv);
            amount = itemView.findViewById(R.id.ammont_TV);
            amountTV = itemView.findViewById(R.id.amountTV);
            dateTime = itemView.findViewById(R.id.dateTime_TV);
            rlBase = itemView.findViewById(R.id.rl_base);
        }
    }
}
