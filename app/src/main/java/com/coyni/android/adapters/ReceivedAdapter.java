package com.coyni.android.adapters;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.coyni.android.fragments.RequestsFragment;
import com.coyni.android.model.receiverequests.ReceiveRequestsItems;
import com.coyni.android.model.transferfee.TransferFeeResponse;
import com.coyni.android.utils.MyApplication;
import com.coyni.android.utils.Utils;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.coyni.android.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class ReceivedAdapter extends RecyclerView.Adapter<ReceivedAdapter.MyViewHolder> {
    private List<ReceiveRequestsItems> listItems;
    Context mContext;
    MyApplication objContext;
    int row_decline_pos = -1, row_pay_pos = -1;
    RequestsFragment fragment;
    TransferFeeResponse transferFeeResponse;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvDate, tvNameHead, tvMessage, tvDescription, tvBalance, tvCharCount, tvPFee, tvStatus;
        public LinearLayout layoutOptions, layoutDecline;
        public CardView cvPayOpt, cvDecline, cvPay, cvInfo;
        public TextInputEditText etMessage;
        public RelativeLayout layoutPay;
        public MaterialCardView cvDeclineOpt;
        public ImageView imgInfo;
        public TextView tvPay;

        public MyViewHolder(View view) {
            super(view);
            tvDate = (TextView) view.findViewById(R.id.tvDate);
            tvNameHead = (TextView) view.findViewById(R.id.tvNameHead);
            tvMessage = (TextView) view.findViewById(R.id.tvMessage);
            tvDescription = (TextView) view.findViewById(R.id.tvDescription);
            tvBalance = (TextView) view.findViewById(R.id.tvBalance);
            tvCharCount = (TextView) view.findViewById(R.id.tvCharCount);
            tvPFee = (TextView) view.findViewById(R.id.tvPFee);
            tvStatus = (TextView) view.findViewById(R.id.tvStatus);
            layoutOptions = (LinearLayout) view.findViewById(R.id.layoutOptions);
            layoutDecline = (LinearLayout) view.findViewById(R.id.layoutDecline);
            layoutPay = (RelativeLayout) view.findViewById(R.id.layoutPay);
            cvPayOpt = (CardView) view.findViewById(R.id.cvPayOpt);
            cvDecline = (CardView) view.findViewById(R.id.cvDecline);
            cvPay = (CardView) view.findViewById(R.id.cvPay);
            cvInfo = (CardView) view.findViewById(R.id.cvInfo);
            etMessage = (TextInputEditText) view.findViewById(R.id.etMessage);
            cvDeclineOpt = (MaterialCardView) view.findViewById(R.id.cvDeclineOpt);
            imgInfo = (ImageView) view.findViewById(R.id.imgInfo);
            tvPay = (TextView) view.findViewById(R.id.tvPay);
        }
    }


    public ReceivedAdapter(List<ReceiveRequestsItems> list, Context context, RequestsFragment fragment) {
        this.mContext = context;
        this.listItems = list;
        this.objContext = (MyApplication) context.getApplicationContext();
        this.fragment = fragment;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.receivedlistitem, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        try {
            ReceiveRequestsItems objData = listItems.get(position);
            String strPrev = "", strCurr = "", strCurDate = "";
            holder.tvDate.setText(Utils.convertDate(objData.getRequestedDate()));
            if (position != 0) {
                strPrev = Utils.convertDate(listItems.get(position - 1).getRequestedDate());
                strCurr = Utils.convertDate(objData.getRequestedDate());
                if (strPrev.equals(strCurr)) {
                    holder.tvDate.setVisibility(View.GONE);
                } else {
                    holder.tvDate.setVisibility(View.VISIBLE);
                }
            } else if (position == 0) {
                holder.tvDate.setVisibility(View.VISIBLE);
                SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                strCurDate = spf.format(Calendar.getInstance().getTime());
                if (Utils.convertDate(objData.getRequestedDate()).equals(Utils.convertDate(strCurDate))) {
                    holder.tvDate.setText("Today's");
                }
            }
            if (objData.getToUser().contains(" ")) {
                holder.tvNameHead.setText(objData.getToUser().split(" ")[0].substring(0, 1).toUpperCase() + objData.getToUser().split(" ")[1].substring(0, 1).toUpperCase());
            } else {
                holder.tvNameHead.setText(objData.getToUser().substring(0, 1).toUpperCase());
            }
            holder.tvMessage.setText(objData.getContent());
            holder.tvDescription.setText(objData.getRemarks());
            holder.tvStatus.setText(objData.getStatus());
            if (Utils.capitalize(objData.getStatus().replace(" ", "")).equals(Utils.requested)) {
                holder.tvStatus.setVisibility(View.GONE);
            } else {
                holder.tvStatus.setVisibility(View.VISIBLE);
                switch (Utils.capitalize(objData.getStatus().replace(" ", ""))) {
                    case Utils.cancel:
                        holder.tvStatus.setTextColor(Color.parseColor("#858585"));
                        break;
                    case Utils.complete:
                        holder.tvStatus.setTextColor(Color.parseColor("#00CC6E"));
                        break;
                    case Utils.decline:
                        holder.tvStatus.setTextColor(Color.parseColor("#FF6464"));
                        break;
                    case Utils.remind:
                        holder.tvStatus.setTextColor(Color.parseColor("#009D98"));
                        break;
                }
            }

            if (row_decline_pos == position) {
                holder.layoutOptions.setVisibility(View.GONE);
                holder.layoutPay.setVisibility(View.GONE);
                holder.layoutDecline.setVisibility(View.VISIBLE);
            } else if (row_pay_pos == position) {
                holder.layoutOptions.setVisibility(View.GONE);
                holder.layoutDecline.setVisibility(View.GONE);
                holder.layoutPay.setVisibility(View.VISIBLE);
                String strAmount = "";
                Double balance = objContext.getGBTBalance();
//                holder.cvPay.setEnabled(true);
//                holder.cvPay.setCardBackgroundColor(mContext.getResources().getColor(R.color.btnback));
//                holder.tvPay.setTextColor(mContext.getResources().getColor(R.color.headcolor));
//                holder.cvDecline.setEnabled(true);
//                holder.cvDecline.setCardBackgroundColor(mContext.getResources().getColor(R.color.deleteback));
                if (balance != null) {
                    strAmount = Utils.convertBigDecimalUSDC(String.valueOf(balance));
                    holder.tvBalance.setText(Utils.USNumberFormat(Double.parseDouble(strAmount)));
                }
                if (transferFeeResponse != null) {
                    String strMsg = Utils.feeCalculation(transferFeeResponse);
                    holder.tvPFee.setText(strMsg);
                    if (!strMsg.equals("")) {
                        holder.cvInfo.setVisibility(View.VISIBLE);
                    } else {
                        holder.cvInfo.setVisibility(View.GONE);
                    }
                }
            } else {
                holder.layoutOptions.setVisibility(View.VISIBLE);
                holder.layoutDecline.setVisibility(View.GONE);
                holder.layoutPay.setVisibility(View.GONE);
                if (Utils.capitalize(objData.getStatus().replace(" ", "")).equals(Utils.cancel)) {
                    holder.layoutOptions.setVisibility(View.GONE);
                } else {
                    holder.layoutOptions.setVisibility(View.VISIBLE);
                }
            }

            holder.cvDeclineOpt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateDeclineData(position);
                }
            });

            holder.cvPayOpt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        String strAmount = "";
                        strAmount = Utils.convertBigDecimalUSDC(String.valueOf(objData.getAmount()));
                        strAmount = Utils.USNumberFormat(Double.parseDouble(strAmount));
                        ((RequestsFragment) fragment).calculateFee(strAmount);
                        updatePayData(position);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            holder.cvDecline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        holder.cvDecline.setEnabled(false);
                        holder.cvDecline.setCardBackgroundColor(Color.parseColor("#9B9B9B"));
                        ((RequestsFragment) fragment).initiateRemind(objData.getId(), Utils.decline, holder.etMessage.getText().toString().trim());
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            holder.cvPay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (objData.getAmount() < objContext.getGBTBalance()) {
                            holder.cvPay.setEnabled(false);
                            holder.cvPay.setCardBackgroundColor(Color.parseColor("#9B9B9B"));
                            holder.tvPay.setTextColor(mContext.getResources().getColor(R.color.white));
                            String strAmount = "";
                            strAmount = Utils.convertBigDecimalUSDC(String.valueOf(objData.getAmount()));
                            strAmount = Utils.USNumberFormat(Double.parseDouble(strAmount));
                            ((RequestsFragment) fragment).initiatePayment(strAmount, objData.getRequesterWalletId(), objData.getId());
                        } else {
                            Utils.displayAlert("Your balance is low in order to Pay.", fragment.getActivity());
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            holder.imgInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (transferFeeResponse != null) {
                            String strMsg = Utils.feeCalculation(transferFeeResponse);
                            holder.tvPFee.setText(strMsg);
                        }
                        holder.cvInfo.setVisibility(View.VISIBLE);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                holder.cvInfo.setVisibility(View.GONE);
                            }
                        }, Integer.parseInt(mContext.getString(R.string.time)));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            holder.etMessage.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() > 0) {
                        holder.tvCharCount.setText(s.length() + "/160");
                    } else {
                        holder.tvCharCount.setText("0/160");
                    }
                }
            });

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public void updateDeclineData(int position) {
        try {
            row_decline_pos = position;
            row_pay_pos = -1;
            notifyDataSetChanged();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void updatePayData(int position) {
        try {
            row_pay_pos = position;
            row_decline_pos = -1;
            notifyDataSetChanged();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void setTransferFeeResponse(TransferFeeResponse feeResponse) {
        transferFeeResponse = feeResponse;
    }
}

