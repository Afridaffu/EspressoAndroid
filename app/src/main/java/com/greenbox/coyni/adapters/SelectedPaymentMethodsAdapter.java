package com.greenbox.coyni.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class SelectedPaymentMethodsAdapter extends RecyclerView.Adapter<SelectedPaymentMethodsAdapter.MyViewHolder> {
    List<PaymentsList> listPayments;
    Context mContext;
    MyApplication objMyApplication;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvPayHead, tvNumber, tvError;
        public ImageView imgPayMethod;
        public LinearLayout layoutError;

        public MyViewHolder(View view) {
            super(view);
            imgPayMethod = view.findViewById(R.id.imgPayMethod);
            tvPayHead = view.findViewById(R.id.tvPayHead);
            tvNumber = view.findViewById(R.id.tvNumber);
            tvError = view.findViewById(R.id.tvError);
            layoutError = view.findViewById(R.id.layoutError);
        }
    }


    public SelectedPaymentMethodsAdapter(List<PaymentsList> list, Context context) {
        this.mContext = context;
        this.listPayments = list;
        this.objMyApplication = (MyApplication) context.getApplicationContext();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.selectedpaymentmethodlistitem, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        try {
            PaymentsList objData = listPayments.get(position);
            if (objData.getPaymentMethod().toLowerCase().equals("bank")) {
                holder.imgPayMethod.setImageResource(R.drawable.ic_bankactive);
                if (!objData.getRelink()) {
                    holder.layoutError.setVisibility(View.GONE);
                } else {
                    holder.layoutError.setVisibility(View.VISIBLE);
                    holder.tvError.setText("There’s a issue with your payment ");
                }
                holder.tvPayHead.setText(objData.getBankName());
                if (objData.getAccountNumber() != null && objData.getAccountNumber().length() > 4) {
                    holder.tvNumber.setText("**** " + objData.getAccountNumber().substring(objData.getAccountNumber().length() - 4));
                } else {
                    holder.tvNumber.setText(objData.getAccountNumber());
                }
            } else {
                holder.tvNumber.setText("****" + objData.getLastFour());
                if (!objData.getExpired()) {
                    holder.layoutError.setVisibility(View.GONE);
                    switch (objData.getCardBrand().toUpperCase().replace(" ", "")) {
                        case "VISA":
                            holder.tvPayHead.setText(Utils.capitalize(objData.getCardBrand() + " " + objData.getCardType() + " Card"));
                            holder.imgPayMethod.setImageResource(R.drawable.ic_visaactive);
                            break;
                        case "MASTERCARD":
                            holder.tvPayHead.setText(Utils.capitalize(objData.getCardBrand() + " " + objData.getCardType() + " Card"));
                            holder.imgPayMethod.setImageResource(R.drawable.ic_masteractive);
                            break;
                        case "AMERICANEXPRESS":
                            holder.tvPayHead.setText("American Express Card");
                            holder.imgPayMethod.setImageResource(R.drawable.ic_amexactive);
                            break;
                        case "DISCOVER":
                            holder.tvPayHead.setText("Discover Card");
                            holder.imgPayMethod.setImageResource(R.drawable.ic_discoveractive);
                            break;
                    }
                } else {
                    holder.layoutError.setVisibility(View.VISIBLE);
                    holder.tvError.setText("Expired");
                }
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (objData.getPaymentMethod().toLowerCase().equals("bank")) {

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
