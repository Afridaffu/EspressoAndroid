package com.coyni.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.coyni.android.R;
import com.coyni.android.fragments.WalletDetailsFragment;
import com.coyni.android.model.wallet.WalletInfo;
import com.coyni.android.utils.MyApplication;
import com.coyni.android.utils.Utils;
import com.coyni.android.view.MainActivity;

import java.util.List;

public class CustomerWalletsAdapter extends RecyclerView.Adapter<CustomerWalletsAdapter.MyViewHolder> {
    List<WalletInfo> listWalletInfo;
    Context mContext;
    MyApplication objMyApplication;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvWalletName, tvBalValue, tvExchVal, tvExchAmount;
        public ImageView imgCurrency;
        public CardView cvWallet;

        public MyViewHolder(View view) {
            super(view);
            tvWalletName = (TextView) view.findViewById(R.id.tvWalletName);
            tvBalValue = (TextView) view.findViewById(R.id.tvBalValue);
            tvExchVal = (TextView) view.findViewById(R.id.tvExchVal);
            tvExchAmount = (TextView) view.findViewById(R.id.tvExchAmount);
            imgCurrency = (ImageView) view.findViewById(R.id.imgCurrency);
            cvWallet = (CardView) view.findViewById(R.id.cvWallet);
        }
    }


    public CustomerWalletsAdapter(List<WalletInfo> list, Context context) {
        this.mContext = context;
        this.listWalletInfo = list;
        this.objMyApplication = (MyApplication) context.getApplicationContext();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custwalletlistitem, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        try {
            WalletInfo objData = listWalletInfo.get(position);
            String strExchangeVal = "";
            if (objData.getWalletName() != null)
                holder.tvWalletName.setText(objData.getWalletName());
            else
                holder.tvWalletName.setText("");
            holder.tvBalValue.setText(Utils.convertBigDecimal(String.valueOf(objData.getAvailabilityToUse())) + " " + objData.getWalletType());
            strExchangeVal = "1 " + objData.getWalletType() + " = $" + Utils.convertBigDecimal(String.valueOf(objData.getExchangeRate()));
            holder.tvExchVal.setText(strExchangeVal);
            holder.tvExchAmount.setText("$ " + Utils.convertBigDecimal(String.valueOf(objData.getExchangeAmount())));
            switch (objData.getWalletType()) {
                case "USDC":
                    holder.imgCurrency.setImageResource(R.drawable.ic_usd_coin);
                    break;
                case "BTC":
                    holder.imgCurrency.setImageResource(R.drawable.ic_bitcoin);
                    break;
                case "ETH":
                    holder.imgCurrency.setImageResource(R.drawable.ic_etherum);
                    break;
                case "CYN":
                    //holder.imgCurrency.setImageResource(R.drawable.ic_gbt_icon);
                    break;
            }
            holder.cvWallet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        objMyApplication.setAssetInfo(objData);
                        if (mContext instanceof MainActivity) {
                            ((MainActivity) mContext).walletDetails(objData.getWalletName(), WalletDetailsFragment.newInstance(mContext), "details");
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
        return listWalletInfo.size();
    }

}



