package com.greenbox.coyni.adapters;

import android.content.Context;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.greenbox.coyni.R;
import com.greenbox.coyni.model.bank.BankItem;
import com.greenbox.coyni.model.paymentmethods.PaymentsList;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.view.BuyTokenActivity;
import com.greenbox.coyni.view.BuyTokenPaymentMethodsActivity;
import com.greenbox.coyni.view.WithdrawPaymentMethodsActivity;
import com.greenbox.coyni.view.WithdrawTokenActivity;
import com.greenbox.coyni.view.business.SelectPaymentMethodActivity;

import java.util.List;

public class BanksListAdapter extends RecyclerView.Adapter<BanksListAdapter.MyViewHolder> {
    List<BankItem> listBanks;
    Context mContext;
    MyApplication objMyApplication;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvBankName, tvAccount;

        public MyViewHolder(View view) {
            super(view);
            tvBankName = view.findViewById(R.id.tvBankName);
            tvAccount = view.findViewById(R.id.tvAccount);
        }
    }


    public BanksListAdapter(List<BankItem> list, Context context) {
        this.mContext = context;
        this.listBanks = list;
        this.objMyApplication = (MyApplication) context.getApplicationContext();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.banklistitem, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        try {
            BankItem objData = listBanks.get(position);
            holder.tvBankName.setText(objData.getBankName());
            if (objData.getAccountNumber() != null && objData.getAccountNumber().length() > 4) {
                holder.tvAccount.setText("**** " + objData.getAccountNumber().substring(objData.getAccountNumber().length() - 4));
            } else {
                holder.tvAccount.setText(objData.getAccountNumber());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return listBanks.size();
    }

}

