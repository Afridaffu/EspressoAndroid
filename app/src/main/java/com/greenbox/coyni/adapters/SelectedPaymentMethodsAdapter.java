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
import com.greenbox.coyni.view.WithdrawPaymentMethodsActivity;
import com.greenbox.coyni.view.WithdrawTokenActivity;
import com.greenbox.coyni.view.business.SelectPaymentMethodActivity;

import java.util.List;

public class SelectedPaymentMethodsAdapter extends RecyclerView.Adapter<SelectedPaymentMethodsAdapter.MyViewHolder> {
    List<PaymentsList> listPayments;
    Context mContext;
    MyApplication objMyApplication;
    Long mLastClickTime = 0L;
    String strScreen = "";

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvBankName, tvAccount, tvError, tvCardName, tvCardNumber;
        public ImageView imgPayMethod, imgBankTick, imgCardTick, imgBankArrow, imgCardArrow;
        public LinearLayout layoutError, layoutBank;
        public RelativeLayout layoutCard;

        public MyViewHolder(View view) {
            super(view);
            imgPayMethod = view.findViewById(R.id.imgPayMethod);
            imgBankTick = view.findViewById(R.id.imgBankTick);
            imgCardTick = view.findViewById(R.id.imgCardTick);
            imgBankArrow = view.findViewById(R.id.imgBankArrow);
            imgCardArrow = view.findViewById(R.id.imgCardArrow);
            tvBankName = view.findViewById(R.id.tvBankName);
            tvCardName = view.findViewById(R.id.tvCardName);
            tvAccount = view.findViewById(R.id.tvAccount);
            tvCardNumber = view.findViewById(R.id.tvCardNumber);
            tvError = view.findViewById(R.id.tvError);
            layoutError = view.findViewById(R.id.layoutError);
            layoutBank = view.findViewById(R.id.layoutBank);
            layoutCard = view.findViewById(R.id.layoutCard);
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
            if (objData.getPaymentMethod() != null && objData.getPaymentMethod().toLowerCase().equals("bank")) {
                holder.layoutBank.setVisibility(View.VISIBLE);
                holder.layoutCard.setVisibility(View.GONE);
                holder.imgPayMethod.setImageResource(R.drawable.ic_bankactive);
                if (strScreen.equals("withdraw")) {
                    holder.imgBankArrow.setVisibility(View.VISIBLE);
                } else {
                    holder.imgBankArrow.setVisibility(View.GONE);
                }
                if (strScreen.equals("buytoken") || strScreen.equals("wdrawtoken")) {
                    if (objData.getId() == objMyApplication.getSelectedCard().getId()) {
                        holder.imgBankTick.setVisibility(View.VISIBLE);
                    } else {
                        holder.imgBankTick.setVisibility(View.GONE);
                    }
                } else {
                    holder.imgBankTick.setVisibility(View.GONE);
                }

                switch (strScreen) {
                    case "selectpay":
                    case "buytoken": {
                        if (objMyApplication.getFeatureControlGlobal().getBuyBank() != null && objMyApplication.getFeatureControlByUser() != null
                                && (!objMyApplication.getFeatureControlGlobal().getBuyBank() || !objMyApplication.getFeatureControlByUser().getBuyBank())) {
                            holder.layoutError.setVisibility(View.VISIBLE);
                            holder.imgPayMethod.setAlpha(0.5f);
                            holder.tvError.setText(mContext.getString(R.string.noFeature));
                            holder.tvBankName.setAlpha(0.5f);
                            holder.tvAccount.setAlpha(0.5f);
                        } else {
                            if (!objData.getRelink()) {
                                holder.layoutError.setVisibility(View.GONE);
                            } else {
                                holder.layoutError.setVisibility(View.VISIBLE);
                                holder.tvError.setText("There’s a issue with your payment ");
                            }
                        }
                    }
                    break;
                    case "withdrawtoken":
                    case "wdrawtoken":
                    case "withdraw": {
                        if (objMyApplication.getFeatureControlGlobal().getWithBank() != null && objMyApplication.getFeatureControlByUser() != null
                                && (!objMyApplication.getFeatureControlGlobal().getWithBank() || !objMyApplication.getFeatureControlByUser().getWithBank())) {
                            holder.layoutError.setVisibility(View.VISIBLE);
                            holder.imgPayMethod.setAlpha(0.5f);
                            holder.tvError.setText(mContext.getString(R.string.noFeature));
                            holder.tvBankName.setAlpha(0.5f);
                            holder.tvAccount.setAlpha(0.5f);
                        } else {
                            if (!objData.getRelink()) {
                                holder.layoutError.setVisibility(View.GONE);
                            } else {
                                holder.layoutError.setVisibility(View.VISIBLE);
                                holder.tvError.setText("There’s a issue with your payment ");
                            }
                        }
                    }
                    break;
                }
//                if (!objData.getRelink()) {
//                    holder.layoutError.setVisibility(View.GONE);
//                } else {
//                    holder.layoutError.setVisibility(View.VISIBLE);
//                    holder.tvError.setText("There’s a issue with your payment ");
//                }

                holder.tvBankName.setText(objData.getBankName());
                if (objData.getAccountNumber() != null && objData.getAccountNumber().length() > 4) {
                    holder.tvAccount.setText("**** " + objData.getAccountNumber().substring(objData.getAccountNumber().length() - 4));
                } else {
                    holder.tvAccount.setText(objData.getAccountNumber());
                }
            } else if (objData.getPaymentMethod() != null && objData.getPaymentMethod().toLowerCase().equals("signet")) {
                holder.layoutBank.setVisibility(View.VISIBLE);
                holder.layoutCard.setVisibility(View.GONE);
                holder.imgPayMethod.setImageResource(R.drawable.ic_signet_ac_logo);
                if (strScreen.equals("withdraw")) {
                    holder.imgBankArrow.setVisibility(View.VISIBLE);
                } else {
                    holder.imgBankArrow.setVisibility(View.GONE);
                }
                if (strScreen.equals("buytoken") || strScreen.equals("wdrawtoken")) {
                    if (objData.getId() == objMyApplication.getSelectedCard().getId()) {
                        holder.imgBankTick.setVisibility(View.VISIBLE);
                    } else {
                        holder.imgBankTick.setVisibility(View.GONE);
                    }
                } else {
                    holder.imgBankTick.setVisibility(View.GONE);
                }

                switch (strScreen) {
                    case "selectpay":
                    case "buytoken": {
                        if (objMyApplication.getFeatureControlGlobal().getBuySignet() != null && objMyApplication.getFeatureControlByUser() != null
                                && (!objMyApplication.getFeatureControlGlobal().getBuySignet() || !objMyApplication.getFeatureControlByUser().getBuySignet())) {
                            holder.layoutError.setVisibility(View.VISIBLE);
                            holder.imgPayMethod.setAlpha(0.5f);
                            holder.tvError.setText(mContext.getString(R.string.noFeature));
                            holder.tvBankName.setAlpha(0.5f);
                            holder.tvAccount.setAlpha(0.5f);
                        } else {
                            if (!objData.getRelink()) {
                                holder.layoutError.setVisibility(View.GONE);
                            } else {
                                holder.layoutError.setVisibility(View.VISIBLE);
                                holder.tvError.setText("There’s a issue with your payment ");
                            }
                        }
                    }
                    break;
                    case "withdrawtoken":
                    case "wdrawtoken":
                    case "withdraw": {
                        if (objMyApplication.getFeatureControlGlobal().getWithSignet() != null && objMyApplication.getFeatureControlByUser() != null
                                && (!objMyApplication.getFeatureControlGlobal().getWithSignet() || !objMyApplication.getFeatureControlByUser().getWithSignet())) {
                            holder.layoutError.setVisibility(View.VISIBLE);
                            holder.imgPayMethod.setAlpha(0.5f);
                            holder.tvError.setText(mContext.getString(R.string.noFeature));
                            holder.tvBankName.setAlpha(0.5f);
                            holder.tvAccount.setAlpha(0.5f);
                        } else {
                            if (!objData.getRelink()) {
                                holder.layoutError.setVisibility(View.GONE);
                            } else {
                                holder.layoutError.setVisibility(View.VISIBLE);
                                holder.tvError.setText("There’s a issue with your payment ");
                            }
                        }
                    }
                    break;
                }
//                if (!objData.getRelink()) {
//                    holder.layoutError.setVisibility(View.GONE);
//                } else {
//                    holder.layoutError.setVisibility(View.VISIBLE);
//                    holder.tvError.setText("There’s a issue with your payment ");
//                }
                holder.tvBankName.setVisibility(View.GONE);
                if (objData.getAccountNumber() != null && objData.getAccountNumber().length() > 14) {
                    holder.tvAccount.setText("Signet Account " + objData.getAccountNumber().substring(0, 5) + "**** " + objData.getAccountNumber().substring(objData.getAccountNumber().length() - 4));
                } else {
                    holder.tvAccount.setText("Signet Account " + objData.getAccountNumber());
                }
            } else {
                holder.layoutBank.setVisibility(View.GONE);
                holder.layoutCard.setVisibility(View.VISIBLE);
                holder.tvCardNumber.setText("****" + objData.getLastFour());
                if (strScreen.equals("withdraw")) {
                    holder.imgCardArrow.setVisibility(View.VISIBLE);
                } else {
                    holder.imgCardArrow.setVisibility(View.GONE);
                }
                if (strScreen.equals("buytoken") || strScreen.equals("wdrawtoken")) {
                    if (objMyApplication.getSelectedCard() != null && objData.getId() == objMyApplication.getSelectedCard().getId()) {
                        holder.imgCardTick.setVisibility(View.VISIBLE);
                    } else {
                        holder.imgCardTick.setVisibility(View.GONE);
                    }
                } else {
                    holder.imgCardTick.setVisibility(View.GONE);
                }
                switch (strScreen) {
                    case "selectpay":
                    case "buytoken": {
                        if (objData.getCardType().toLowerCase().equals("debit")) {
                            if (objMyApplication.getFeatureControlGlobal().getBuyDebit() != null && objMyApplication.getFeatureControlByUser() != null
                                    && (!objMyApplication.getFeatureControlGlobal().getBuyDebit() || !objMyApplication.getFeatureControlByUser().getBuyDebit())) {
                                holder.layoutError.setVisibility(View.VISIBLE);
                                holder.imgPayMethod.setAlpha(0.5f);
                                holder.tvError.setText(mContext.getString(R.string.noFeature));
                                holder.tvCardName.setAlpha(0.5f);
                                holder.tvCardNumber.setAlpha(0.5f);
                            } else {
                                if (!objData.getExpired()) {
                                    holder.layoutError.setVisibility(View.GONE);
                                } else {
                                    holder.layoutError.setVisibility(View.VISIBLE);
                                    holder.tvError.setText("Expired");
                                }
                            }
                        } else {
                            if (objMyApplication.getFeatureControlGlobal().getBuyCredit() != null && objMyApplication.getFeatureControlByUser() != null
                                    && (!objMyApplication.getFeatureControlGlobal().getBuyCredit() || !objMyApplication.getFeatureControlByUser().getBuyCredit())) {
                                holder.layoutError.setVisibility(View.VISIBLE);
                                holder.imgPayMethod.setAlpha(0.5f);
                                holder.tvError.setText(mContext.getString(R.string.noFeature));
                                holder.tvCardName.setAlpha(0.5f);
                                holder.tvCardNumber.setAlpha(0.5f);
                            } else {
                                if (!objData.getExpired()) {
                                    holder.layoutError.setVisibility(View.GONE);
                                } else {
                                    holder.layoutError.setVisibility(View.VISIBLE);
                                    holder.tvError.setText("Expired");
                                }
                            }
                        }

                    }
                    break;
                    case "withdrawtoken":
                    case "wdrawtoken":
                    case "withdraw": {
                        if (objMyApplication.getFeatureControlGlobal().getWithSignet() != null && objMyApplication.getFeatureControlByUser() != null
                                && (!objMyApplication.getFeatureControlGlobal().getWithSignet() || !objMyApplication.getFeatureControlByUser().getWithSignet())) {
                            holder.layoutError.setVisibility(View.VISIBLE);
                            holder.imgPayMethod.setImageResource(R.drawable.ic_bank_account_inactive);
                            holder.tvError.setText(mContext.getString(R.string.noFeature));
                            holder.tvCardName.setAlpha(0.5f);
                            holder.tvCardNumber.setAlpha(0.5f);
                        } else {
                            if (!objData.getExpired()) {
                                holder.layoutError.setVisibility(View.GONE);
                            } else {
                                holder.layoutError.setVisibility(View.VISIBLE);
                                holder.tvError.setText("Expired");
                            }
                        }
                    }
                    break;
                }
//                if (!objData.getExpired()) {
//                    holder.layoutError.setVisibility(View.GONE);
//                } else {
//                    holder.layoutError.setVisibility(View.VISIBLE);
//                    holder.tvError.setText("Expired");
//                }
                if (objData.getCardBrand() != null) {
                    switch (objData.getCardBrand().toUpperCase().replace(" ", "")) {
                        case "VISA":
                            holder.tvCardName.setText(Utils.capitalize(objData.getCardBrand() + " " + objData.getCardType()));
                            holder.imgPayMethod.setImageResource(R.drawable.ic_visaactive);
                            break;
                        case "MASTERCARD":
                            holder.tvCardName.setText(Utils.capitalize(objData.getCardBrand() + " " + objData.getCardType()));
                            holder.imgPayMethod.setImageResource(R.drawable.ic_masteractive);
                            break;
                        case "AMERICANEXPRESS":
                            holder.tvCardName.setText("American Express Card");
                            holder.imgPayMethod.setImageResource(R.drawable.ic_amexactive);
                            break;
                        case "DISCOVER":
                            holder.tvCardName.setText("Discover Card");
                            holder.imgPayMethod.setImageResource(R.drawable.ic_discoveractive);
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
                        objMyApplication.setSelectedButTokenType(objData.getPaymentMethod().toLowerCase());
                        switch (strScreen) {
                            case "selectpay":
                                objMyApplication.setPrevSelectedCard(objMyApplication.getSelectedCard());
                                objMyApplication.setSelectedCard(objData);
//                                if (objData.getPaymentMethod().toLowerCase().equals("bank") || objData.getPaymentMethod().toLowerCase().equals("signet")) {
                                if (objData.getPaymentMethod().toLowerCase().equals("bank")) {
                                    if (objMyApplication.getFeatureControlGlobal().getBuyBank() != null && objMyApplication.getFeatureControlByUser() != null
                                            && objMyApplication.getFeatureControlGlobal().getBuyBank() && objMyApplication.getFeatureControlByUser().getBuyBank()) {
                                        if (!objData.getRelink()) {
                                            if (objMyApplication.getAccountType() == Utils.PERSONAL_ACCOUNT) {
                                                ((BuyTokenPaymentMethodsActivity) mContext).bindSelectedBank();
                                            } else {
                                                ((SelectPaymentMethodActivity) mContext).bindSelectedBank();
                                            }
                                        } else {
                                            if (objMyApplication.getAccountType() == Utils.PERSONAL_ACCOUNT) {
                                                ((BuyTokenPaymentMethodsActivity) mContext).expiry();
                                            } else {
                                                ((SelectPaymentMethodActivity) mContext).expiry();
                                            }
                                        }
                                    }
                                } else if (objData.getPaymentMethod().toLowerCase().equals("signet")) {
                                    if (objMyApplication.getFeatureControlGlobal().getBuySignet() != null && objMyApplication.getFeatureControlByUser() != null
                                            && objMyApplication.getFeatureControlGlobal().getBuySignet() && objMyApplication.getFeatureControlByUser().getBuySignet()) {
                                        if (!objData.getRelink()) {
                                            ((SelectPaymentMethodActivity) mContext).bindSelectedBank();
                                        } else {
                                            ((SelectPaymentMethodActivity) mContext).deleteBank(objData);
                                        }
                                    }
                                } else {
                                    if (objData.getCardType().toLowerCase().equals("debit")) {
                                        if (objMyApplication.getFeatureControlGlobal().getBuyDebit() != null && objMyApplication.getFeatureControlByUser() != null
                                                && objMyApplication.getFeatureControlGlobal().getBuyDebit() && objMyApplication.getFeatureControlByUser().getBuyDebit()) {
                                            if (!objData.getExpired()) {
                                                ((BuyTokenPaymentMethodsActivity) mContext).displayCVV();
                                            } else {
                                                ((BuyTokenPaymentMethodsActivity) mContext).expiry();
                                            }
                                        }
                                    } else {
                                        if (objMyApplication.getFeatureControlGlobal().getBuyCredit() != null && objMyApplication.getFeatureControlByUser() != null
                                                && objMyApplication.getFeatureControlGlobal().getBuyCredit() && objMyApplication.getFeatureControlByUser().getBuyCredit()) {
                                            if (!objData.getExpired()) {
                                                ((BuyTokenPaymentMethodsActivity) mContext).displayCVV();
                                            } else {
                                                ((BuyTokenPaymentMethodsActivity) mContext).expiry();
                                            }
                                        }
                                    }
                                }
//                                else if (!objData.getExpired()) {
//                                    ((BuyTokenPaymentMethodsActivity) mContext).displayCVV();
//                                } else {
//                                    ((BuyTokenPaymentMethodsActivity) mContext).expiry();
//                                }
                                break;
                            case "withdrawtoken":
                                objMyApplication.setPrevSelectedCard(objMyApplication.getSelectedCard());
                                objMyApplication.setSelectedCard(objData);
                                if (objData.getPaymentMethod().toLowerCase().equals("bank")) {
                                    if (objMyApplication.getFeatureControlGlobal().getWithBank() != null && objMyApplication.getFeatureControlByUser() != null
                                            && objMyApplication.getFeatureControlGlobal().getWithBank() && objMyApplication.getFeatureControlByUser().getWithBank()) {
                                        if (!objData.getRelink()) {
                                            ((WithdrawPaymentMethodsActivity) mContext).bindSelectedBank("withdrawtoken");
                                        } else {
                                            ((WithdrawPaymentMethodsActivity) mContext).expiry();
                                        }
                                    }
                                } else {
                                    if (objMyApplication.getFeatureControlGlobal().getWithInstant() != null && objMyApplication.getFeatureControlByUser() != null
                                            && objMyApplication.getFeatureControlGlobal().getWithInstant() && objMyApplication.getFeatureControlByUser().getWithInstant()) {
                                        if (!objData.getExpired()) {
                                            ((WithdrawPaymentMethodsActivity) mContext).bindSelectedCard("withdrawtoken");
                                        } else {
                                            ((WithdrawPaymentMethodsActivity) mContext).expiry();
                                        }
                                    }
                                }
                                break;
                            case "buytoken":
                                if (objData.getId() != objMyApplication.getSelectedCard().getId()) {
                                    ((BuyTokenActivity) mContext).strType = objData.getPaymentMethod().toLowerCase();
//                                    objMyApplication.setPrevSelectedCard(objMyApplication.getSelectedCard());
//                                    objMyApplication.setSelectedCard(objData);
//                                    notifyDataSetChanged();
                                    if (objData.getPaymentMethod().toLowerCase().equals("bank")) {
                                        if (objMyApplication.getFeatureControlGlobal().getBuyBank() != null && objMyApplication.getFeatureControlByUser() != null
                                                && objMyApplication.getFeatureControlGlobal().getBuyBank() && objMyApplication.getFeatureControlByUser().getBuyBank()) {
                                            objMyApplication.setPrevSelectedCard(objMyApplication.getSelectedCard());
                                            objMyApplication.setSelectedCard(objData);
                                            notifyDataSetChanged();
                                            if (!objData.getRelink()) {
                                                ((BuyTokenActivity) mContext).bindSelectedBank(objData);
                                            } else {
                                                ((BuyTokenActivity) mContext).expiry();
                                            }
                                        }
                                    } else if (objData.getPaymentMethod().toLowerCase().equals("signet")) {
                                        if (objMyApplication.getFeatureControlGlobal().getBuySignet() != null && objMyApplication.getFeatureControlByUser() != null
                                                && objMyApplication.getFeatureControlGlobal().getBuySignet() && objMyApplication.getFeatureControlByUser().getBuySignet()) {
                                            objMyApplication.setPrevSelectedCard(objMyApplication.getSelectedCard());
                                            objMyApplication.setSelectedCard(objData);
                                            notifyDataSetChanged();
                                            if (!objData.getRelink()) {
                                                ((BuyTokenActivity) mContext).bindSelectedBank(objData);
                                            } else {
                                                ((BuyTokenActivity) mContext).deleteBank(objData);
                                            }
                                        }
                                    } else {
                                        if (objData.getCardType().toLowerCase().equals("debit")) {
                                            if (objMyApplication.getFeatureControlGlobal().getBuyDebit() != null && objMyApplication.getFeatureControlByUser() != null
                                                    && objMyApplication.getFeatureControlGlobal().getBuyDebit() && objMyApplication.getFeatureControlByUser().getBuyDebit()) {
                                                objMyApplication.setPrevSelectedCard(objMyApplication.getSelectedCard());
                                                objMyApplication.setSelectedCard(objData);
                                                notifyDataSetChanged();
                                                if (!objData.getExpired()) {
                                                    ((BuyTokenActivity) mContext).displayCVV(objData);
                                                } else {
                                                    ((BuyTokenActivity) mContext).expiry();
                                                }
                                            }
                                        } else {
                                            if (objMyApplication.getFeatureControlGlobal().getBuyCredit() != null && objMyApplication.getFeatureControlByUser() != null
                                                    && objMyApplication.getFeatureControlGlobal().getBuyCredit() && objMyApplication.getFeatureControlByUser().getBuyCredit()) {
                                                objMyApplication.setPrevSelectedCard(objMyApplication.getSelectedCard());
                                                objMyApplication.setSelectedCard(objData);
                                                notifyDataSetChanged();
                                                if (!objData.getExpired()) {
                                                    ((BuyTokenActivity) mContext).displayCVV(objData);
                                                } else {
                                                    ((BuyTokenActivity) mContext).expiry();
                                                }
                                            }
                                        }

//                                        if (!objData.getExpired()) {
//                                            ((BuyTokenActivity) mContext).displayCVV(objData);
//                                        } else {
//                                            ((BuyTokenActivity) mContext).expiry();
//                                        }
                                    }

                                    ((BuyTokenActivity) mContext).calculateFee("10");
                                }
                                break;
                            case "withdraw":
                                objMyApplication.setPrevSelectedCard(objMyApplication.getSelectedCard());
                                objMyApplication.setSelectedCard(objData);
                                if (objData.getPaymentMethod().toLowerCase().equals("bank")) {
                                    if (objMyApplication.getFeatureControlGlobal().getWithBank() != null && objMyApplication.getFeatureControlByUser() != null
                                            && objMyApplication.getFeatureControlGlobal().getWithBank() && objMyApplication.getFeatureControlByUser().getWithBank()) {
                                        if (!objData.getRelink()) {
                                            ((WithdrawPaymentMethodsActivity) mContext).bindSelectedBank("withdraw");
                                        } else {
                                            ((WithdrawPaymentMethodsActivity) mContext).expiry();
                                        }
                                    }
                                } else {
                                    if (objMyApplication.getFeatureControlGlobal().getWithInstant() != null && objMyApplication.getFeatureControlByUser() != null
                                            && objMyApplication.getFeatureControlGlobal().getWithInstant() && objMyApplication.getFeatureControlByUser().getWithInstant()) {
                                        if (!objData.getExpired()) {
                                            ((WithdrawPaymentMethodsActivity) mContext).bindSelectedCard("withdraw");
                                        } else {
                                            ((WithdrawPaymentMethodsActivity) mContext).expiry();
                                        }
                                    }
                                }
                                break;
                            case "wdrawtoken":
                                if (objData.getId() != objMyApplication.getSelectedCard().getId()) {
//                                    objMyApplication.setPrevSelectedCard(objMyApplication.getSelectedCard());
//                                    objMyApplication.setSelectedCard(objData);
//                                    notifyDataSetChanged();
//                                    if (objData.getPaymentMethod().toLowerCase().equals("bank") || objData.getPaymentMethod().toLowerCase().equals("signet")) {
                                    if (objData.getPaymentMethod().toLowerCase().equals("bank")) {
                                        if (objMyApplication.getFeatureControlGlobal().getWithBank() != null && objMyApplication.getFeatureControlByUser() != null
                                                && objMyApplication.getFeatureControlGlobal().getWithBank() && objMyApplication.getFeatureControlByUser().getWithBank()) {
                                            objMyApplication.setPrevSelectedCard(objMyApplication.getSelectedCard());
                                            objMyApplication.setSelectedCard(objData);
                                            notifyDataSetChanged();
                                            if (!objData.getRelink()) {
                                                ((WithdrawTokenActivity) mContext).bindSelectedBank(objData);
                                            } else {
                                                ((WithdrawTokenActivity) mContext).expiry();
                                            }
                                        }
                                    } else if (objData.getPaymentMethod().toLowerCase().equals("signet")) {
                                        if (objMyApplication.getFeatureControlGlobal().getWithSignet() != null && objMyApplication.getFeatureControlByUser() != null
                                                && objMyApplication.getFeatureControlGlobal().getWithSignet() && objMyApplication.getFeatureControlByUser().getWithSignet()) {
                                            objMyApplication.setPrevSelectedCard(objMyApplication.getSelectedCard());
                                            objMyApplication.setSelectedCard(objData);
                                            notifyDataSetChanged();
                                            if (!objData.getRelink()) {
                                                ((WithdrawTokenActivity) mContext).bindSelectedBank(objData);
                                            } else {
                                                ((WithdrawTokenActivity) mContext).expiry();
                                            }
                                        }
                                    } else {
                                        if (objMyApplication.getFeatureControlGlobal().getWithInstant() != null && objMyApplication.getFeatureControlByUser() != null
                                                && objMyApplication.getFeatureControlGlobal().getWithInstant() && objMyApplication.getFeatureControlByUser().getWithInstant()) {
                                            objMyApplication.setPrevSelectedCard(objMyApplication.getSelectedCard());
                                            objMyApplication.setSelectedCard(objData);
                                            notifyDataSetChanged();
                                            if (!objData.getExpired()) {
                                                ((WithdrawTokenActivity) mContext).bindSelectedCard(objData);
                                            } else {
                                                ((WithdrawTokenActivity) mContext).expiry();
                                            }
                                        }
                                    }
                                }
                                break;
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
