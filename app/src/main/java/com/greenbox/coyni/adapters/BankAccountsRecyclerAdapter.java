package com.greenbox.coyni.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.greenbox.coyni.R;
import com.greenbox.coyni.model.bank.BankItems;
import com.greenbox.coyni.model.giftcard.Brand;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.view.GiftCardDetails;

import java.util.List;

public class BankAccountsRecyclerAdapter extends RecyclerView.Adapter<BankAccountsRecyclerAdapter.MyViewHolder> {
    List<BankItems> banks;
    Context mContext;
    MyApplication objMyApplication;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView bankNameTx,accountNumberTx;

        public MyViewHolder(View view) {
            super(view);
            bankNameTx = (TextView) view.findViewById(R.id.bankName);
            accountNumberTx=(TextView) view.findViewById(R.id.accountNumber);
        }
    }


    public BankAccountsRecyclerAdapter(Context context, List<BankItems> list) {
        this.mContext = context;
        this.banks = list;
        this.objMyApplication = (MyApplication) context.getApplicationContext();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bankaccounts_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        try {
            BankItems objData = banks.get(position);
            holder.bankNameTx.setText(objData.getBankName());
//            if (objData.getImageUrls().get_1200w326ppi() != null && !objData.getImageUrls().get_1200w326ppi().equals("")) {
//                Glide.with(mContext).load(objData.getImageUrls().get_1200w326ppi().trim()).into(holder.imgBank);
//            }
//            holder.itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    try {
//                        if (objData != null) {
//                            Intent i = new Intent(mContext, GiftCardDetails.class);
//                            i.putExtra("BRAND_KEY", objData.getBrandKey());
//                            mContext.startActivity(i);
//                        }
//                    } catch (Exception ex) {
//                        ex.printStackTrace();
//                    }
//                }
//            });
            holder.accountNumberTx.setText(objData.getAccountNumber());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return banks.size();
    }


}

