package com.greenbox.coyni.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.greenbox.coyni.R;
import com.greenbox.coyni.model.reguser.RegUsersResponseData;
import com.greenbox.coyni.utils.DisplayImageUtility;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.view.AddRecipientActivity;
import com.greenbox.coyni.view.PayRequestActivity;

import java.util.List;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.MyViewHolder> {
    List<RegUsersResponseData> listUsers;
    Context mContext;
    MyApplication objMyApplication;
    Long mLastClickTime = 0L;
    Long mLastClickTimeInvite = 0L;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvNameHead, tvUserName, tvWalletAddress;
        public ImageView imgInvite, imgUser;
        public RelativeLayout rlUserDetailS;

        public MyViewHolder(View view) {
            super(view);
            tvNameHead = view.findViewById(R.id.tvNameHead);
            tvUserName = view.findViewById(R.id.tvUserName);
            tvWalletAddress = view.findViewById(R.id.tvWalletAddress);
            imgInvite = view.findViewById(R.id.imgInvite);
            imgUser = view.findViewById(R.id.imgUser);
            rlUserDetailS = view.findViewById(R.id.lldetails);
        }
    }


    public ContactsAdapter(List<RegUsersResponseData> list, Context context) {
        this.mContext = context;
        this.listUsers = list;
        this.objMyApplication = (MyApplication) context.getApplicationContext();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recentuserlistitem, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        try {
            RegUsersResponseData objData = listUsers.get(position);
            String strPhContact = "", strEcoSysName = "";
            if (objData.getUserName() != null && !objData.getUserName().equals("")) {
                if (objData.getUserName().length() > 24) {
                    strPhContact = objData.getUserName().substring(0, 24) + "...";
                } else {
                    strPhContact = objData.getUserName();
                }
            } else {
                strPhContact = "";
            }
            if (objData.getFullName() != null && !objData.getFullName().equals("")) {
                if (objData.getFullName().length() > 24) {
                    strEcoSysName = objData.getFullName().substring(0, 24) + "...";
                } else {
                    strEcoSysName = objData.getFullName();
                }
            } else {
                strEcoSysName = "";
            }
            if (!strPhContact.equals("") && !strEcoSysName.equals("")) {
                holder.tvUserName.setText(Utils.capitalize(strPhContact));
                holder.tvWalletAddress.setText("@" + Utils.capitalize(strEcoSysName));
                holder.tvWalletAddress.setVisibility(View.VISIBLE);
                holder.imgInvite.setVisibility(View.GONE);
                holder.tvNameHead.setText(objMyApplication.setNameHead(strEcoSysName));
            } else if (strPhContact.equals("") && !strEcoSysName.equals("")) {
                holder.tvUserName.setText(Utils.capitalize(strEcoSysName));
                holder.tvWalletAddress.setVisibility(View.GONE);
                holder.imgInvite.setVisibility(View.GONE);
                holder.tvNameHead.setText(objMyApplication.setNameHead(strEcoSysName));
            } else if (!strPhContact.equals("") && strEcoSysName.equals("")) {
                holder.tvUserName.setText(Utils.capitalize(strPhContact.replace("null","")));
                holder.tvWalletAddress.setVisibility(View.GONE);
                holder.imgInvite.setVisibility(View.VISIBLE);
                holder.tvNameHead.setText(objMyApplication.setNameHead(strPhContact.replace("null","")));
            }

            if (objData.getImage() != null && !objData.getImage().trim().equals("")) {
                holder.imgUser.setVisibility(View.VISIBLE);
                holder.tvNameHead.setVisibility(View.GONE);
                if (objData.getImage().startsWith("content:")) {
                    holder.imgUser.setImageBitmap(objMyApplication.convertImageURIToBitMap(objData.getImage().trim()));
                } else {
                    DisplayImageUtility utility = DisplayImageUtility.getInstance(mContext);
                    utility.addImage(objData.getImage(), holder.imgUser, R.drawable.ic_profilelogo);
                }
            } else {
                holder.imgUser.setVisibility(View.GONE);
                holder.tvNameHead.setVisibility(View.VISIBLE);
            }

            holder.imgInvite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (SystemClock.elapsedRealtime() - mLastClickTimeInvite < 2000) {
                            return;
                        }
                        mLastClickTimeInvite = SystemClock.elapsedRealtime();
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


            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();
                        if (objData.getWalletAddress() != null && !objData.getWalletAddress().equals("")) {
                            Intent i = new Intent(mContext, PayRequestActivity.class);
                            i.putExtra("walletId", objData.getWalletAddress());
                            i.putExtra("name", objData.getUserName());
                            i.putExtra("phone", objData.getPhoneNumber());
                            i.putExtra("image", objData.getImage());
                            mContext.startActivity(i);
                        } else {
//                            Utils.displayAlert("You can only invite this contact.", ((AddRecipientActivity) mContext), "", "");
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
        return listUsers.size();
    }

    public void updateList(List<RegUsersResponseData> list) {
        listUsers = list;
        notifyDataSetChanged();
    }
}


