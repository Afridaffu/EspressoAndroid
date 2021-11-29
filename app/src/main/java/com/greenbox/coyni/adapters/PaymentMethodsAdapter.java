package com.greenbox.coyni.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.greenbox.coyni.R;
import com.greenbox.coyni.model.paymentmethods.PaymentsList;
import com.greenbox.coyni.utils.MyApplication;

import java.util.List;
import java.util.Locale;

public class PaymentMethodsAdapter extends RecyclerView.Adapter<PaymentMethodsAdapter.MyViewHolder> {
    List<PaymentsList> listPayments;
    Context mContext;
    MyApplication objMyApplication;
    String strScreen = "";

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvBankHead, tvBankExpire, tvCardNumber, tvBankName, tvAccNumber;
        public RelativeLayout layoutBack;
        public ImageView imgBankIcon;
        public LinearLayout layoutBank;

        public MyViewHolder(View view) {
            super(view);
            layoutBack = view.findViewById(R.id.layoutBack);
            imgBankIcon = view.findViewById(R.id.imgBankIcon);
            tvBankHead = view.findViewById(R.id.tvBankHead);
            tvBankExpire = view.findViewById(R.id.tvBankExpire);
            tvCardNumber = view.findViewById(R.id.tvCardNumber);
            layoutBank = view.findViewById(R.id.layoutBank);
            tvBankName = view.findViewById(R.id.tvBankName);
            tvAccNumber = view.findViewById(R.id.tvAccNumber);
        }
    }


    public PaymentMethodsAdapter(List<PaymentsList> list, Context context, String screen) {
        this.mContext = context;
        this.listPayments = list;
        this.objMyApplication = (MyApplication) context.getApplicationContext();
        this.strScreen = screen;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.paymentmethodlistitem, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        try {
            PaymentsList objData = listPayments.get(position);
            if (objData.getPaymentMethod().toLowerCase().equals("bank")) {
                holder.layoutBank.setVisibility(View.VISIBLE);
                holder.tvCardNumber.setVisibility(View.GONE);
                holder.imgBankIcon.setImageResource(R.drawable.ic_bankactive);
                if (!objData.getRelink()) {
                    holder.tvBankExpire.setVisibility(View.GONE);
                    holder.layoutBack.setBackgroundResource(R.drawable.ic_activebank);
                } else {
                    holder.tvBankExpire.setVisibility(View.VISIBLE);
                    holder.layoutBack.setBackgroundResource(R.drawable.ic_issuebank);
                }
                holder.tvBankName.setText(objData.getBankName());
                if (objData.getAccountNumber() != null && objData.getAccountNumber().length() > 4) {
                    holder.tvAccNumber.setText("**** " + objData.getAccountNumber().substring(objData.getAccountNumber().length() - 4));
                } else {
                    holder.tvAccNumber.setText(objData.getAccountNumber());
                }
            } else {
                holder.layoutBank.setVisibility(View.GONE);
                holder.tvCardNumber.setVisibility(View.VISIBLE);
                holder.tvCardNumber.setText(objData.getFirstSix()+"****"+objData.getLastFour());
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return listPayments.size();
    }

}

