package com.coyni.android.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.coyni.android.R;
import com.coyni.android.model.transactions.TokenTransactionsItem;
import com.coyni.android.utils.MyApplication;
import com.coyni.android.utils.Utils;
import com.coyni.android.view.TransactionDetailsActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class TokenTransactionsAdapter extends RecyclerView.Adapter<TokenTransactionsAdapter.MyViewHolder> {
    List<TokenTransactionsItem> listTransactionsItem;
    Context mContext;
    MyApplication objMyApplication;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvDate, tvDescription, tvBalance, tvAmount, tvStatus;
        public ImageView imgIcon;
        public CardView cvType;

        public MyViewHolder(View view) {
            super(view);
            tvDate = (TextView) view.findViewById(R.id.tvDate);
            tvDescription = (TextView) view.findViewById(R.id.tvDescription);
            tvBalance = (TextView) view.findViewById(R.id.tvBalance);
            tvAmount = (TextView) view.findViewById(R.id.tvAmount);
            tvStatus = (TextView) view.findViewById(R.id.tvStatus);
            imgIcon = (ImageView) view.findViewById(R.id.imgIcon);
            cvType = (CardView) view.findViewById(R.id.cvType);
        }
    }


    public TokenTransactionsAdapter(List<TokenTransactionsItem> list, Context context) {
        this.mContext = context;
        this.listTransactionsItem = list;
        this.objMyApplication = (MyApplication) context.getApplicationContext();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tokentransactionlistitem, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        try {
            TokenTransactionsItem objData = listTransactionsItem.get(position);
            String strType = "", strPrev = "", strCurr = "", strCurDate = "";
//            holder.tvDate.setText(Utils.convertDate(objData.getCreatedAt()));
            holder.tvDate.setText(objMyApplication.convertZoneDate(objData.getUpdatedAt()));
            if (position != 0) {
//                strPrev = Utils.convertDate(listTransactionsItem.get(position - 1).getCreatedAt());
//                strCurr = Utils.convertDate(objData.getCreatedAt());
                strPrev = objMyApplication.convertZoneDate(listTransactionsItem.get(position - 1).getUpdatedAt());
                strCurr = objMyApplication.convertZoneDate(objData.getUpdatedAt());
                if (strPrev.equals(strCurr)) {
                    holder.tvDate.setVisibility(View.GONE);
                } else {
                    holder.tvDate.setVisibility(View.VISIBLE);
                }
            } else if (position == 0) {
                holder.tvDate.setVisibility(View.VISIBLE);
                SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                strCurDate = spf.format(Calendar.getInstance().getTime());
//                if (Utils.convertDate(objData.getCreatedAt()).equals(Utils.convertDate(strCurDate))) {
                if (Utils.convertDate(objData.getUpdatedAt()).equals(Utils.convertDate(strCurDate))) {
                    holder.tvDate.setText("Today");
                }
            }
            holder.tvDescription.setText(objData.getTxnDescription());
            holder.tvBalance.setText(convertTwoDecimal(objData.getWalletBalance()));
            if (objData.getTxnType().toLowerCase().contains("withdraw")) {
                strType = "withdraw";
            } else if (objData.getTxnType().toLowerCase().contains("pay") || objData.getTxnType().toLowerCase().contains("request")) {
                if (objData.getTxnSubType().toLowerCase().contains("send") || objData.getTxnSubType().toLowerCase().contains("sent")) {
                    strType = "pay";
                } else {
                    strType = "receive";
                }
            } else if (objData.getTxnType().toLowerCase().contains("buy")) {
                strType = "buy";
            } else {
                strType = objData.getTxnType().toLowerCase();
            }

            if (strType.contains("pay") || strType.equals("withdraw")) {
                holder.tvAmount.setText("-" + convertTwoDecimal(objData.getAmount()));
            } else {
                holder.tvAmount.setText(convertTwoDecimal(objData.getAmount()));
            }
            switch (strType) {
                case "pay":
                    holder.cvType.setCardBackgroundColor(mContext.getResources().getColor(R.color.tokensend));
                    holder.imgIcon.setImageResource(R.drawable.ic_token_send);
                    break;
                case "receive":
                    holder.cvType.setCardBackgroundColor(mContext.getResources().getColor(R.color.tokenrece));
                    holder.imgIcon.setImageResource(R.drawable.ic_token_receive);
                    break;
                case "withdraw":
                    holder.cvType.setCardBackgroundColor(mContext.getResources().getColor(R.color.tokenwith));
                    holder.imgIcon.setImageResource(R.drawable.ic_token_withdraw);
                    break;
                case "buy":
                    holder.cvType.setCardBackgroundColor(mContext.getResources().getColor(R.color.tokenpaid));
                    holder.imgIcon.setImageResource(R.drawable.ic_token_paid);
                    break;
                case "deposit":
                    holder.cvType.setCardBackgroundColor(mContext.getResources().getColor(R.color.tokenadd));
                    holder.imgIcon.setImageResource(R.drawable.ic_buy_token);
                    break;
            }
            holder.tvStatus.setText(objData.getTxnStatus());
            switch (objData.getTxnStatus().replace(" ", "").toLowerCase()) {
                case Utils.transInProgress:
                    holder.tvStatus.setTextColor(Color.parseColor("#2196F3"));
                    break;
                case Utils.transPending:
                    holder.tvStatus.setTextColor(Color.parseColor("#A5A5A5"));
                    break;
                case Utils.transCompleted:
                    holder.tvStatus.setTextColor(Color.parseColor("#00CC6E"));
                    break;
                case Utils.transFailed:
                    holder.tvStatus.setTextColor(Color.parseColor("#D45858"));
                    break;
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent i = new Intent(mContext, TransactionDetailsActivity.class);
                        i.putExtra("gbxTxnId", objData.getGbxTransactionId());
                        i.putExtra("txnType", objData.getTxnType());
                        i.putExtra("txnSubType", objData.getTxnSubType());
                        mContext.startActivity(i);
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
        return listTransactionsItem.size();
    }

    private String convertTwoDecimal(String strAmount) {
        String strValue = "", strAmt = "";
        try {
            if (strAmount.contains(" ")) {
                strAmt = Utils.convertBigDecimalUSDC(strAmount.split(" ")[0]);
                strValue = Utils.USNumberFormat(Double.parseDouble(strAmt)) + " " + strAmount.split(" ")[1];
            } else {
                strAmt = Utils.convertBigDecimalUSDC(strAmount);
                strValue = Utils.USNumberFormat(Double.parseDouble(strAmt)) + " " + mContext.getString(R.string.currency);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return strValue;
    }

    public void addLoadingView() {
        //add loading item
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                listTransactionsItem.add(null);
                notifyItemInserted(listTransactionsItem.size() - 1);
            }
        });
    }

    public void removeLoadingView() {
        //Remove loading item
        listTransactionsItem.remove(listTransactionsItem.size() - 1);
        notifyItemRemoved(listTransactionsItem.size());
    }

    public void addData(List<TokenTransactionsItem> listItems) {
        this.listTransactionsItem.addAll(listItems);
        notifyDataSetChanged();
    }

}




