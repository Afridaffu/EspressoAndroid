package com.greenbox.coyni.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.greenbox.coyni.R;
import com.greenbox.coyni.model.summary.Item;
import com.greenbox.coyni.utils.MyApplication;

import java.util.List;

public class BankAccountsRecyclerAdapter extends RecyclerView.Adapter<BankAccountsRecyclerAdapter.MyViewHolder> {
    List<Item> banks;
    Context mContext;
    MyApplication objMyApplication;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView bankNameTx, accountNumberTx;

        public MyViewHolder(View view) {
            super(view);
            bankNameTx = (TextView) view.findViewById(R.id.bankName);
            accountNumberTx = (TextView) view.findViewById(R.id.accountNumber);
        }
    }


    public BankAccountsRecyclerAdapter(Context context, List<Item> list) {
        this.mContext = context;
        this.banks = list;
        this.objMyApplication = (MyApplication) context.getApplicationContext();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bankaccounts_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        try {
            Item objData = banks.get(position);
            holder.bankNameTx.setText(objData.getBankName());

            holder.accountNumberTx.setText(objData.getAccountNumber());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return banks.size();
    }


}

