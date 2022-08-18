package com.coyni.mapp.adapters;

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

import com.coyni.mapp.R;
import com.coyni.mapp.model.paymentmethods.PaymentsList;
import com.coyni.mapp.utils.MyApplication;
import com.coyni.mapp.utils.Utils;
import com.coyni.mapp.view.PaymentMethodsActivity;
import com.coyni.mapp.view.business.BusinessPaymentMethodsActivity;

import java.util.List;

public class PaymentMethodsAdapter extends RecyclerView.Adapter<PaymentMethodsAdapter.MyViewHolder> {
    List<PaymentsList> listPayments;
    Context mContext;
    MyApplication objMyApplication;
    Long mLastClickTime = 0L;
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
        this.strScreen = screen;
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
            if (objData.getPaymentMethod() != null && objData.getPaymentMethod().toLowerCase().equals("bank")) {
                holder.layoutBank.setVisibility(View.VISIBLE);
                holder.tvCardNumber.setVisibility(View.GONE);
//                holder.imgBankIcon.setImageResource(R.drawable.ic_bankactive);
                if (!objData.getRelink()) {
                    holder.tvBankExpire.setVisibility(View.GONE);
                    holder.layoutBack.setBackgroundResource(R.drawable.ic_activebank);
                    holder.imgBankIcon.setImageResource(R.drawable.ic_bankactive);
                    holder.imgBankIcon.setVisibility(View.VISIBLE);
                } else {
                    holder.tvBankExpire.setVisibility(View.VISIBLE);
                    holder.layoutBack.setBackgroundResource(R.drawable.ic_issuebank);
                    holder.imgBankIcon.setImageResource(R.drawable.ic_bank_account_inactive);
                    holder.imgBankIcon.setVisibility(View.VISIBLE);
                }
                holder.tvBankName.setText(objData.getBankName());
                if (objData.getAccountNumber() != null && objData.getAccountNumber().length() > 4) {
                    holder.tvAccNumber.setText("**** " + objData.getAccountNumber().substring(objData.getAccountNumber().length() - 4));
                } else {
                    holder.tvAccNumber.setText(objData.getAccountNumber());
                }
            } else if (objData.getPaymentMethod() != null && objData.getPaymentMethod().toLowerCase().equals("signet")) {
                holder.tvBankHead.setText("Signet Account");
                holder.layoutBank.setVisibility(View.VISIBLE);
                holder.tvCardNumber.setVisibility(View.GONE);
//                holder.imgBankIcon.setImageResource(R.drawable.ic_signetactive);
                if (!objData.getRelink()) {
                    holder.tvBankExpire.setVisibility(View.GONE);
                    holder.layoutBack.setBackgroundResource(R.drawable.ic_activesignet);
                    holder.imgBankIcon.setImageResource(R.drawable.ic_signetactive);
                    holder.imgBankIcon.setVisibility(View.VISIBLE);
                } else {
                    holder.tvBankExpire.setVisibility(View.VISIBLE);
                    holder.layoutBack.setBackgroundResource(R.drawable.ic_issuesignet);
                    holder.imgBankIcon.setImageResource(R.drawable.ic_signetinactive);
                    holder.imgBankIcon.setVisibility(View.VISIBLE);
                }
                holder.tvAccNumber.setVisibility(View.GONE);
                if (objData.getAccountNumber() != null && objData.getAccountNumber().length() > 14) {
                    holder.tvBankName.setText(objData.getAccountNumber().substring(0, 10) + "**** " + objData.getAccountNumber().substring(objData.getAccountNumber().length() - 4));
                } else {
                    holder.tvBankName.setText(objData.getAccountNumber());
                }
            } else {
                holder.layoutBank.setVisibility(View.GONE);
                holder.tvCardNumber.setVisibility(View.VISIBLE);
                holder.tvCardNumber.setText(objData.getFirstSix().replace(" ", "").replaceAll("(.{4})", "$1 ").trim() + " ****" + objData.getLastFour());
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
                            holder.tvBankHead.setText(Utils.capitalize(objData.getCardBrand() + " " + objData.getCardType() + " Card"));
                            holder.imgBankIcon.setImageResource(R.drawable.ic_visaexpire);
                            holder.layoutBack.setBackgroundResource(R.drawable.ic_expiredvisa);
                            break;
                        case "MASTERCARD":
                            holder.tvBankHead.setText(Utils.capitalize(objData.getCardBrand() + " " + objData.getCardType() + " Card"));
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
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();
                        if (strScreen.equals("customer")) {
                            if (objData.getPaymentMethod().toLowerCase().equals("bank")) {
                                if (objMyApplication.getFeatureControlGlobal().getPayBank() != null && objMyApplication.getFeatureControlByUser() != null
                                        && objMyApplication.getFeatureControlGlobal().getPayBank() && objMyApplication.getFeatureControlByUser().getPayBank()) {
                                    if (!objData.getRelink()) {
                                        ((PaymentMethodsActivity) mContext).deleteBank(mContext, objData);
                                    } else {
                                        ((PaymentMethodsActivity) mContext).expiry(mContext, objData);
                                    }
                                } else {
                                    Utils.displayAlert(mContext.getString(R.string.errormsg), ((PaymentMethodsActivity) mContext), "", "");
                                }
                            } else {
                                if (objData.getCardType().toLowerCase().equals("debit")) {
                                    if (objMyApplication.getFeatureControlGlobal().getPayDebit() != null && objMyApplication.getFeatureControlByUser() != null
                                            && objMyApplication.getFeatureControlGlobal().getPayDebit() && objMyApplication.getFeatureControlByUser().getPayDebit()) {
                                        if (!objData.getExpired()) {
                                            objMyApplication.setSelectedCard(objData);
                                            ((PaymentMethodsActivity) mContext).editCard();
                                        } else {
                                            objMyApplication.setSelectedCard(objData);
                                            ((PaymentMethodsActivity) mContext).expiry(mContext, objData);
                                        }
                                    } else {
                                        Utils.displayAlert(mContext.getString(R.string.errormsg), ((PaymentMethodsActivity) mContext), "", "");
                                    }
                                } else {
                                    if (objMyApplication.getFeatureControlGlobal().getPayCredit() != null && objMyApplication.getFeatureControlByUser() != null
                                            && objMyApplication.getFeatureControlGlobal().getPayCredit() && objMyApplication.getFeatureControlByUser().getPayCredit()) {
                                        if (!objData.getExpired()) {
                                            objMyApplication.setSelectedCard(objData);
                                            ((PaymentMethodsActivity) mContext).editCard();
                                        } else {
                                            objMyApplication.setSelectedCard(objData);
                                            ((PaymentMethodsActivity) mContext).expiry(mContext, objData);
                                        }
                                    } else {
                                        Utils.displayAlert(mContext.getString(R.string.errormsg), ((PaymentMethodsActivity) mContext), "", "");
                                    }
                                }
                            }
                        } else {
//                            if (objData.getPaymentMethod().toLowerCase().equals("bank") || objData.getPaymentMethod().toLowerCase().equals("signet")) {
                            if (objData.getPaymentMethod().toLowerCase().equals("bank")) {
                                if (objMyApplication.getFeatureControlGlobal().getPayBank() != null && objMyApplication.getFeatureControlByUser() != null
                                        && objMyApplication.getFeatureControlGlobal().getPayBank() && objMyApplication.getFeatureControlByUser().getPayBank()) {
                                    if (!objData.getRelink()) {
                                        ((BusinessPaymentMethodsActivity) mContext).deleteBank(objData);
                                    } else {
                                        ((BusinessPaymentMethodsActivity) mContext).expiry(objData);
                                    }
                                } else {
                                    Utils.displayAlert(mContext.getString(R.string.errormsg), ((BusinessPaymentMethodsActivity) mContext), "", "");
                                }
                            } else if (objData.getPaymentMethod().toLowerCase().equals("signet")) {
                                if (objMyApplication.getFeatureControlGlobal().getPaySignet() != null && objMyApplication.getFeatureControlByUser() != null
                                        && objMyApplication.getFeatureControlGlobal().getPaySignet() && objMyApplication.getFeatureControlByUser().getPaySignet()) {
                                    ((BusinessPaymentMethodsActivity) mContext).deleteBank(objData);
                                } else {
                                    Utils.displayAlert(mContext.getString(R.string.errormsg), ((BusinessPaymentMethodsActivity) mContext), "", "");
                                }
                            } else {
                                if (objData.getCardType().toLowerCase().equals("debit")) {
                                    if (objMyApplication.getFeatureControlGlobal().getPayDebit() != null && objMyApplication.getFeatureControlByUser() != null
                                            && objMyApplication.getFeatureControlGlobal().getPayDebit() && objMyApplication.getFeatureControlByUser().getPayDebit()) {
                                        if (!objData.getExpired()) {
                                            objMyApplication.setSelectedCard(objData);
                                            ((BusinessPaymentMethodsActivity) mContext).editCard();
                                        } else {
                                            objMyApplication.setSelectedCard(objData);
                                            ((BusinessPaymentMethodsActivity) mContext).expiry(objData);
                                        }
                                    } else {
                                        Utils.displayAlert(mContext.getString(R.string.errormsg), ((BusinessPaymentMethodsActivity) mContext), "", "");
                                    }
                                } else {
                                    if (objMyApplication.getFeatureControlGlobal().getPayCredit() != null && objMyApplication.getFeatureControlByUser() != null
                                            && objMyApplication.getFeatureControlGlobal().getPayCredit() && objMyApplication.getFeatureControlByUser().getPayCredit()) {
                                        if (!objData.getExpired()) {
                                            objMyApplication.setSelectedCard(objData);
                                            ((BusinessPaymentMethodsActivity) mContext).editCard();
                                        } else {
                                            objMyApplication.setSelectedCard(objData);
                                            ((BusinessPaymentMethodsActivity) mContext).expiry(objData);
                                        }
                                    } else {
                                        Utils.displayAlert(mContext.getString(R.string.errormsg), ((BusinessPaymentMethodsActivity) mContext), "", "");
                                    }
                                }
                            }
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

