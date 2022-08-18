package com.coyni.mapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.coyni.mapp.R;
import com.coyni.mapp.model.recentusers.RecentUsersData;
import com.coyni.mapp.utils.DisplayImageUtility;
import com.coyni.mapp.utils.MyApplication;
import com.coyni.mapp.utils.Utils;
import com.coyni.mapp.view.PayRequestActivity;

import java.util.List;

public class RecentUsersAdapter extends RecyclerView.Adapter<RecentUsersAdapter.MyViewHolder> {
    List<RecentUsersData> listUsers;
    Context mContext;
    MyApplication objMyApplication;
    Long mLastClickTime = 0L;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvNameHead, tvUserName, tvWalletAddress;
        public ImageView imgUser;

        public MyViewHolder(View view) {
            super(view);
            tvNameHead = view.findViewById(R.id.tvNameHead);
            tvUserName = view.findViewById(R.id.tvUserName);
            tvWalletAddress = view.findViewById(R.id.tvWalletAddress);
            imgUser = view.findViewById(R.id.imgUser);
        }
    }


    public RecentUsersAdapter(List<RecentUsersData> list, Context context) {
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
            RecentUsersData objData = listUsers.get(position);

            String strPhContact = "", strEcoSysName = "", strName = "", strImagePath = "";
            if (objData.getUserName() != null && !objData.getUserName().equals("")) {
                if (objData.getUserName().length() > 24) {
                    strEcoSysName = objData.getUserName().substring(0, 24) + "...";
                } else {
                    strEcoSysName = objData.getUserName();
                }
            } else {
                strEcoSysName = "";
            }
            if (objMyApplication.getObjPhContacts().containsKey(objData.getPhoneNumber().replace("(1)", ""))) {
                strName = objMyApplication.getObjPhContacts().get(objData.getPhoneNumber().replace("(1)", "")).getFirstName() + " " + objMyApplication.getObjPhContacts().get(objData.getPhoneNumber().replace("(1)", "")).getLastName();
                if (strName.length() > 24) {
                    strPhContact = strName.substring(0, 24) + "...";
                } else {
                    strPhContact = strName;
                }
                strImagePath = objMyApplication.getObjPhContacts().get(objData.getPhoneNumber().replace("(1)", "")).getImagePath();
            } else {
                strPhContact = "";
                strImagePath = "";
            }
            if (!strPhContact.equals("") && !strEcoSysName.equals("")) {
                holder.tvUserName.setText(Utils.capitalize(strPhContact));
                holder.tvWalletAddress.setText("@" + Utils.capitalize(strEcoSysName));
                holder.tvWalletAddress.setVisibility(View.VISIBLE);
                holder.tvNameHead.setText(objMyApplication.setNameHead(strPhContact));
            } else if (strPhContact.equals("") && !strEcoSysName.equals("")) {
                holder.tvUserName.setText(Utils.capitalize(strEcoSysName));
                holder.tvWalletAddress.setVisibility(View.GONE);
                holder.tvNameHead.setText(objMyApplication.setNameHead(strEcoSysName));
            } else if (!strPhContact.equals("") && strEcoSysName.equals("")) {
                holder.tvUserName.setText(Utils.capitalize(strPhContact));
                holder.tvWalletAddress.setVisibility(View.GONE);
                holder.tvNameHead.setText(objMyApplication.setNameHead(strPhContact));
            }

            if (objData.getImage() != null && !objData.getImage().trim().equals("")) {
                holder.imgUser.setVisibility(View.VISIBLE);
                holder.tvNameHead.setVisibility(View.GONE);
                DisplayImageUtility utility = DisplayImageUtility.getInstance(mContext);
                utility.addImage(objData.getImage(), holder.imgUser, R.drawable.ic_profilelogo);
            } else if (strImagePath != null && !strImagePath.equals("")) {
                if (strImagePath.startsWith("content:")) {
                    holder.imgUser.setVisibility(View.VISIBLE);
                    holder.tvNameHead.setVisibility(View.GONE);
                    holder.imgUser.setImageBitmap(objMyApplication.convertImageURIToBitMap(strImagePath.trim()));
                } else {
                    holder.imgUser.setVisibility(View.GONE);
                    holder.tvNameHead.setVisibility(View.VISIBLE);
                }
            } else {
                holder.imgUser.setVisibility(View.GONE);
                holder.tvNameHead.setVisibility(View.VISIBLE);
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();
                        String imgPath = "";
                        if (objMyApplication.getObjPhContacts().containsKey(objData.getPhoneNumber().replace("(1)", ""))) {
                            imgPath = objMyApplication.getObjPhContacts().get(objData.getPhoneNumber().replace("(1)", "")).getImagePath();
                        }
                        Intent i = new Intent(mContext, PayRequestActivity.class);
                        i.putExtra("walletId", objData.getWalletAddress());
                        i.putExtra("name", objData.getUserName());
                        i.putExtra("phone", objData.getPhoneNumber());
                        if (objData.getImage() != null && !objData.getImage().trim().equals("")) {
                            i.putExtra("image", objData.getImage());
                        } else if (imgPath != null && !imgPath.equals("")) {
                            i.putExtra("image", imgPath);
                        } else {
                            i.putExtra("image", "");
                        }
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
        return listUsers.size();
    }

    public void updateList(List<RecentUsersData> list) {
        listUsers = list;
        notifyDataSetChanged();
    }

}


