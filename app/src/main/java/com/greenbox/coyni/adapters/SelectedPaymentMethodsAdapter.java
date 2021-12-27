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
import com.greenbox.coyni.view.WithdrawPaymentMethodsActivity;

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
            if (objData.getPaymentMethod().toLowerCase().equals("bank")) {
                holder.layoutBank.setVisibility(View.VISIBLE);
                holder.layoutCard.setVisibility(View.GONE);
                holder.imgPayMethod.setImageResource(R.drawable.ic_bankactive);
                if (strScreen.equals("withdraw")) {
                    holder.imgBankArrow.setVisibility(View.VISIBLE);
                } else {
                    holder.imgBankArrow.setVisibility(View.GONE);
                }
//                if (!strScreen.equals("selectpay")) {
                if (strScreen.equals("buytoken")) {
                    if (objData.getId() == objMyApplication.getSelectedCard().getId()) {
                        holder.imgBankTick.setVisibility(View.VISIBLE);
                    } else {
                        holder.imgBankTick.setVisibility(View.GONE);
                    }
                } else {
                    holder.imgBankTick.setVisibility(View.GONE);
                }
                if (!objData.getRelink()) {
                    holder.layoutError.setVisibility(View.GONE);
                } else {
                    holder.layoutError.setVisibility(View.VISIBLE);
                    holder.tvError.setText("There’s a issue with your payment ");
                }
                holder.tvBankName.setText(objData.getBankName());
                if (objData.getAccountNumber() != null && objData.getAccountNumber().length() > 4) {
                    holder.tvAccount.setText("**** " + objData.getAccountNumber().substring(objData.getAccountNumber().length() - 4));
                } else {
                    holder.tvAccount.setText(objData.getAccountNumber());
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
//                if (!strScreen.equals("selectpay") && !strScreen.equals("withdrawtoken")) {
                if (strScreen.equals("buytoken")) {
                    if (objData.getId() == objMyApplication.getSelectedCard().getId()) {
                        holder.imgCardTick.setVisibility(View.VISIBLE);
                    } else {
                        holder.imgCardTick.setVisibility(View.GONE);
                    }
                } else {
                    holder.imgCardTick.setVisibility(View.GONE);
                }
                if (!objData.getExpired()) {
                    holder.layoutError.setVisibility(View.GONE);
                } else {
                    holder.layoutError.setVisibility(View.VISIBLE);
                    holder.tvError.setText("Expired");
                }
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

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();
                        switch (strScreen) {
                            case "selectpay":
                                objMyApplication.setSelectedCard(objData);
                                if (objData.getPaymentMethod().toLowerCase().equals("bank")) {
                                    if (!objData.getRelink()) {
                                        ((BuyTokenPaymentMethodsActivity) mContext).bindSelectedBank();
                                    } else {
                                        ((BuyTokenPaymentMethodsActivity) mContext).expiry();
                                    }
                                } else if (!objData.getExpired()) {
                                    ((BuyTokenPaymentMethodsActivity) mContext).displayCVV();
                                } else {
                                    ((BuyTokenPaymentMethodsActivity) mContext).expiry();
                                }
                                break;
                            case "withdrawtoken":
                                objMyApplication.setSelectedCard(objData);
                                if (objData.getPaymentMethod().toLowerCase().equals("bank")) {
                                    if (!objData.getRelink()) {
                                        ((WithdrawPaymentMethodsActivity) mContext).bindSelectedBank("withdrawtoken");
                                    } else {
                                        ((WithdrawPaymentMethodsActivity) mContext).expiry();
                                    }
                                } else if (!objData.getExpired()) {
                                    ((WithdrawPaymentMethodsActivity) mContext).displayCVV("withdrawtoken");
                                } else {
                                    ((WithdrawPaymentMethodsActivity) mContext).expiry();
                                }
                                break;
                            case "buytoken":
                                if (objData.getId() != objMyApplication.getSelectedCard().getId()) {
                                    objMyApplication.setSelectedCard(objData);
                                    notifyDataSetChanged();
                                    if (objData.getPaymentMethod().toLowerCase().equals("bank")) {
                                        if (!objData.getRelink()) {
                                            ((BuyTokenActivity) mContext).bindSelectedBank(objData);
                                        } else {
                                            ((BuyTokenActivity) mContext).expiry();
                                        }
                                    } else {
                                        if (!objData.getExpired()) {
                                            ((BuyTokenActivity) mContext).displayCVV(objData);
                                        } else {
                                            ((BuyTokenActivity) mContext).expiry();
                                        }
                                    }
                                }
                                break;
                            case "withdraw":
                                objMyApplication.setSelectedCard(objData);
                                if (objData.getPaymentMethod().toLowerCase().equals("bank")) {
                                    if (!objData.getRelink()) {
                                        ((WithdrawPaymentMethodsActivity) mContext).bindSelectedBank("withdraw");
                                    } else {
                                        ((WithdrawPaymentMethodsActivity) mContext).expiry();
                                    }
                                } else if (!objData.getExpired()) {
                                    ((WithdrawPaymentMethodsActivity) mContext).displayCVV("withdraw");
                                } else {
                                    ((WithdrawPaymentMethodsActivity) mContext).expiry();
                                }
                                break;
                        }
//                        if (strScreen.equals("selectpay")) {
//                            objMyApplication.setSelectedCard(objData);
//                            if (objData.getPaymentMethod().toLowerCase().equals("bank")) {
//                                if (!objData.getRelink()) {
//                                    ((BuyTokenPaymentMethodsActivity) mContext).bindSelectedBank();
//                                } else {
//                                    ((BuyTokenPaymentMethodsActivity) mContext).expiry();
//                                }
//                            } else if (!objData.getExpired()) {
//                                ((BuyTokenPaymentMethodsActivity) mContext).displayCVV();
//                            } else {
//                                ((BuyTokenPaymentMethodsActivity) mContext).expiry();
//                            }
//                        } else if (strScreen.equals("withdrawtoken")) {
//                            objMyApplication.setSelectedCard(objData);
//                            if (objData.getPaymentMethod().toLowerCase().equals("bank")) {
//                                if (!objData.getRelink()) {
//                                    ((WithdrawPaymentMethodsActivity) mContext).bindSelectedBank();
//                                } else {
//                                    ((WithdrawPaymentMethodsActivity) mContext).expiry();
//                                }
//                            } else if (!objData.getExpired()) {
//                                ((WithdrawPaymentMethodsActivity) mContext).displayCVV();
//                            } else {
//                                ((WithdrawPaymentMethodsActivity) mContext).expiry();
//                            }
//                        } else {
//                            if (objData.getId() != objMyApplication.getSelectedCard().getId()) {
//                                objMyApplication.setSelectedCard(objData);
//                                notifyDataSetChanged();
//                                if (objData.getPaymentMethod().toLowerCase().equals("bank")) {
//                                    if (!objData.getRelink()) {
//                                        ((BuyTokenActivity) mContext).bindSelectedBank(objData);
//                                    } else {
//                                        ((BuyTokenActivity) mContext).expiry();
//                                    }
//                                } else {
//                                    if (!objData.getExpired()) {
//                                        ((BuyTokenActivity) mContext).displayCVV(objData);
//                                    } else {
//                                        ((BuyTokenActivity) mContext).expiry();
//                                    }
//                                }
//                            }
//                        }
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
