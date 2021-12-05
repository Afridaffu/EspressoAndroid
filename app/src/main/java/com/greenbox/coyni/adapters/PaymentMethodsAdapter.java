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
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.view.PaymentMethodsActivity;

import java.util.List;

public class PaymentMethodsAdapter extends RecyclerView.Adapter<PaymentMethodsAdapter.MyViewHolder> {
    List<PaymentsList> listPayments;
    Context mContext;
    MyApplication objMyApplication;

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


    public PaymentMethodsAdapter(List<PaymentsList> list, Context context) {
        this.mContext = context;
        this.listPayments = list;
        this.objMyApplication = (MyApplication) context.getApplicationContext();
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
                holder.tvCardNumber.setText(objData.getFirstSix() + "****" + objData.getLastFour());
                if (!objData.getExpired()) {
                    holder.tvBankExpire.setVisibility(View.GONE);
                    switch (objData.getCardBrand().toUpperCase().replace(" ", "")) {
                        case "VISA":
                            holder.tvBankHead.setText(Utils.capitalize(objData.getCardBrand() + " " + objData.getCardType() + " Card"));
                            holder.imgBankIcon.setImageResource(R.drawable.ic_visaactive);
                            holder.layoutBack.setBackgroundResource(R.drawable.ic_activevisa);
                            break;
                        case "MASTERCARD":
                            holder.tvBankHead.setText(Utils.capitalize(objData.getCardBrand() + " " + objData.getCardType() + " Card"));
                            holder.imgBankIcon.setImageResource(R.drawable.ic_masteractive);
                            holder.layoutBack.setBackgroundResource(R.drawable.ic_activemaster);
                            break;
                        case "AMERICANEXPRESS":
                            holder.tvBankHead.setText("American Express Card");
                            holder.imgBankIcon.setImageResource(R.drawable.ic_amexactive);
                            holder.layoutBack.setBackgroundResource(R.drawable.ic_activeamex);
                            break;
                        case "DISCOVER":
                            holder.tvBankHead.setText("Discover Card");
                            holder.imgBankIcon.setImageResource(R.drawable.ic_discoveractive);
                            holder.layoutBack.setBackgroundResource(R.drawable.ic_activediscover);
                            break;
                    }
                } else {
                    holder.tvBankExpire.setVisibility(View.VISIBLE);
                    holder.tvBankExpire.setText("Expired");
                    switch (objData.getCardBrand().toUpperCase()) {
                        case "VISA":
                            holder.tvBankHead.setText(objData.getCardBrand() + " " + objData.getCardType() + " Card");
                            holder.imgBankIcon.setImageResource(R.drawable.ic_visaexpire);
                            holder.layoutBack.setBackgroundResource(R.drawable.ic_expiredvisa);
                            break;
                        case "MASTERCARD":
                            holder.tvBankHead.setText(objData.getCardBrand() + " " + objData.getCardType() + " Card");
                            holder.imgBankIcon.setImageResource(R.drawable.ic_masterexpire);
                            holder.layoutBack.setBackgroundResource(R.drawable.ic_expiredmaster);
                            break;
                        case "AMERICAN EXPRESS":
                            holder.tvBankHead.setText("American Express Card");
                            holder.imgBankIcon.setImageResource(R.drawable.ic_amexexpire);
                            holder.layoutBack.setBackgroundResource(R.drawable.ic_expiredamex);
                            break;
                        case "DISCOVER":
                            holder.tvBankHead.setText("Discover Card");
                            holder.imgBankIcon.setImageResource(R.drawable.ic_discoverexpire);
                            holder.layoutBack.setBackgroundResource(R.drawable.ic_expireddiscover);
                            break;
                    }
                }
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (objData.getPaymentMethod().toLowerCase().equals("bank")) {
                            ((PaymentMethodsActivity) mContext).deleteBank(mContext, objData);
                        }
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
        return listPayments.size();
    }

}

