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
import com.greenbox.coyni.model.paymentmethods.PaymentsList;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.view.BuyTokenActivity;
import com.greenbox.coyni.view.BuyTokenPaymentMethodsActivity;
import com.greenbox.coyni.view.PaymentMethodsActivity;

import java.util.List;

public class SelectedPaymentMethodsAdapter extends RecyclerView.Adapter<SelectedPaymentMethodsAdapter.MyViewHolder> {
    List<PaymentsList> listPayments;
    Context mContext;
    MyApplication objMyApplication;
    Long mLastClickTime = 0L;
    String strScreen = "";

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvPayHead, tvNumber, tvError;
        public ImageView imgPayMethod, imgTick;
        public LinearLayout layoutError;

        public MyViewHolder(View view) {
            super(view);
            imgPayMethod = view.findViewById(R.id.imgPayMethod);
            imgTick = view.findViewById(R.id.imgTick);
            tvPayHead = view.findViewById(R.id.tvPayHead);
            tvNumber = view.findViewById(R.id.tvNumber);
            tvError = view.findViewById(R.id.tvError);
            layoutError = view.findViewById(R.id.layoutError);
        }
    }


    public SelectedPaymentMethodsAdapter(List<PaymentsList> list, Context context, String screen) {
        this.mContext = context;
        this.listPayments = list;
        this.strScreen = screen;
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
            if (!strScreen.equals("selectpay")) {
                if (objData.getId() == objMyApplication.getSelectedCard().getId()) {
                    holder.imgTick.setVisibility(View.VISIBLE);
                } else {
                    holder.imgTick.setVisibility(View.GONE);
                }
            } else {
                holder.imgTick.setVisibility(View.GONE);
            }
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
                } else {
                    holder.layoutError.setVisibility(View.VISIBLE);
                    holder.tvError.setText("Expired");
                }
                switch (objData.getCardBrand().toUpperCase().replace(" ", "")) {
                    case "VISA":
                        holder.tvPayHead.setText(Utils.capitalize(objData.getCardBrand() + " " + objData.getCardType()));
                        holder.imgPayMethod.setImageResource(R.drawable.ic_visaactive);
                        break;
                    case "MASTERCARD":
                        holder.tvPayHead.setText(Utils.capitalize(objData.getCardBrand() + " " + objData.getCardType()));
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
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();
                        objMyApplication.setSelectedCard(objData);
                        if (strScreen.equals("selectpay")) {
                            if (objData.getPaymentMethod().toLowerCase().equals("bank")) {
                                if (!objData.getRelink()) {

                                } else {
                                    ((BuyTokenPaymentMethodsActivity) mContext).expiry();
                                }
                            } else if (!objData.getExpired()) {
                                ((BuyTokenPaymentMethodsActivity) mContext).displayCVV();
                            } else {
                                ((BuyTokenPaymentMethodsActivity) mContext).expiry();
                            }
                        } else {
                            objMyApplication.setSelectedCard(objData);
                            notifyDataSetChanged();
                            ((BuyTokenActivity) mContext).bindPayMethod(objData);
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
