package com.coyni.android.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.coyni.android.R;
import com.coyni.android.model.reguser.RegUsersResponseData;
import com.coyni.android.utils.MyApplication;
import com.coyni.android.utils.Utils;
import com.coyni.android.view.AddRecipientActivity;
import com.coyni.android.view.PayRequestActivity;

import java.util.List;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.MyViewHolder> {
    List<RegUsersResponseData> listContacts;
    Context mContext;
    MyApplication objMyApplication;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvNameHead, tvName, tvNumber;
        public CardView cvNameHead;
        public ImageView imgInvite;

        public MyViewHolder(View view) {
            super(view);
            tvNameHead = (TextView) view.findViewById(R.id.tvNameHead);
            tvName = (TextView) view.findViewById(R.id.tvName);
            tvNumber = (TextView) view.findViewById(R.id.tvNumber);
            imgInvite = (ImageView) view.findViewById(R.id.imgInvite);
            cvNameHead = (CardView) view.findViewById(R.id.cvNameHead);
        }
    }


    public ContactsAdapter(List<RegUsersResponseData> list, Context context) {
        this.mContext = context;
        this.listContacts = list;
        this.objMyApplication = (MyApplication) context.getApplicationContext();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contactlistitem, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        try {
            RegUsersResponseData objData = listContacts.get(position);
            if (objData.getUserName() != null && !objData.getUserName().equals("")) {
                if (objData.getUserName().contains(" ")) {
                    holder.tvNameHead.setText(objData.getUserName().split(" ")[0].substring(0, 1).toUpperCase() + objData.getUserName().split(" ")[1].substring(0, 1).toUpperCase());
                } else {
                    holder.tvNameHead.setText(objData.getUserName().substring(0, 1).toUpperCase());
                }
                holder.tvName.setText(Utils.capitalize(objData.getUserName()));
            }

            if (objData.getWalletAddress() != null && !objData.getWalletAddress().equals("")) {
                holder.tvNumber.setText(objData.getWalletAddress().substring(0, 23) + "...");
            } else {
                holder.tvNumber.setText("Invite them into Coyni");
            }
            if (objData.getWalletAddress() != null && !objData.getWalletAddress().equals("")) {
                holder.imgInvite.setVisibility(View.GONE);
            } else {
                holder.imgInvite.setVisibility(View.VISIBLE);
            }

            if (position % 4 == 0) {
                holder.cvNameHead.setCardBackgroundColor(Color.parseColor("#26D45858"));
                holder.tvNameHead.setTextColor(Color.parseColor("#D45858"));
            } else if (position % 4 == 1) {
                holder.cvNameHead.setCardBackgroundColor(Color.parseColor("#2635BAB6"));
                holder.tvNameHead.setTextColor(Color.parseColor("#35BAB6"));
            } else if (position % 4 == 2) {
                holder.cvNameHead.setCardBackgroundColor(Color.parseColor("#262196F3"));
                holder.tvNameHead.setTextColor(Color.parseColor("#2196F3"));
            } else {
                holder.cvNameHead.setCardBackgroundColor(Color.parseColor("#26FFA701"));
                holder.tvNameHead.setTextColor(Color.parseColor("#FFA701"));
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (objData.getWalletAddress() != null && !objData.getWalletAddress().equals("")) {
                            Intent i = new Intent(mContext, PayRequestActivity.class);
                            i.putExtra("walletId", objData.getWalletAddress());
                            mContext.startActivity(i);
                        } else {
//                            Utils.displayAlert("You can only invite this contact.", ((AddRecipientActivity) mContext));
                            Utils.displayCloseAlert("You can only invite this contact.", ((AddRecipientActivity) mContext));
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
            holder.imgInvite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent i = new Intent(Intent.ACTION_SEND);
                        i.setType("text/plain");
                        i.putExtra(Intent.EXTRA_SUBJECT, "Sharing URL");
                        i.putExtra(Intent.EXTRA_TEXT, objMyApplication.getStrInvite().replace("[body1]", objData.getUserName()));
                        mContext.startActivity(Intent.createChooser(i, "Share URL"));
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
        return listContacts.size();
    }

    public void updateList(List<RegUsersResponseData> list) {
        listContacts = list;
        notifyDataSetChanged();
    }
}
