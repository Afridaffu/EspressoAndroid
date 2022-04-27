package com.greenbox.coyni.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.greenbox.coyni.R;
import com.greenbox.coyni.model.preferences.ProfilesResponse;
import com.greenbox.coyni.utils.LogUtils;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;

import java.util.List;

public class AddNewBusinessAccountDBAAdapter extends RecyclerView.Adapter<AddNewBusinessAccountDBAAdapter.MyViewHolder> {
    private OnSelectListner listener;
    List<ProfilesResponse.Profiles> listCompany;
    Context mContext;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView txvCompanyName;
        private ImageView imvTickIcon,profileImage;
        private View viewLine;
        private  TextView statusTV;
        private  LinearLayout statusLL;


        public MyViewHolder(View view) {
            super(view);
            txvCompanyName = (TextView) view.findViewById(R.id.title);
            imvTickIcon = (ImageView) view.findViewById(R.id.tickIcon);
            profileImage = (ImageView) view.findViewById(R.id.profileImage);
            viewLine = (View) view.findViewById(R.id.viewLine);
             statusTV = (TextView) view.findViewById(R.id.statusTV);
             statusLL = (LinearLayout) view.findViewById(R.id.statusLL);
        }
    }


    public AddNewBusinessAccountDBAAdapter(List<ProfilesResponse.Profiles> list, Context context,OnSelectListner listener) {
        this.mContext = context;
        this.listCompany = list;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.business_comapany_listitem, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        try {
            holder.txvCompanyName.setText(listCompany.get(position).getCompanyName());
            if (position == listCompany.size() - 1) {
                holder.viewLine.setVisibility(View.GONE);
            } else {
                holder.viewLine.setVisibility(View.VISIBLE);
            }

            if (listCompany.get(position).isSelected()) {
                holder.imvTickIcon.setVisibility(View.VISIBLE);
                holder.txvCompanyName.setTextColor(mContext.getColor(R.color.primary_green));
                holder.statusLL.setVisibility(View.GONE);
            } else {
                holder.imvTickIcon.setVisibility(View.GONE);
                holder.txvCompanyName.setTextColor(mContext.getColor(R.color.primary_black));
                if(!listCompany.get(position).getAccountStatus().equalsIgnoreCase(Utils.BUSINESS_ACCOUNT_STATUS.ACTIVE.getStatus())){

                    if(listCompany.get(position).getAccountStatus().equalsIgnoreCase(Utils.BUSINESS_ACCOUNT_STATUS.TERMINATED.getStatus())){
                        holder.statusLL.setVisibility(View.VISIBLE);
                        holder.statusLL.setBackgroundColor(mContext.getColor(R.color.default_red));
                        holder.statusTV.setText(listCompany.get(position).getAccountStatus());
                    } else if(listCompany.get(position).getAccountStatus().equalsIgnoreCase(Utils.BUSINESS_ACCOUNT_STATUS.UNDER_REVIEW.getStatus())){
                        holder.statusLL.setVisibility(View.VISIBLE);
                        holder.statusLL.setBackgroundColor(mContext.getColor(R.color.under_review_blue));
                        holder.statusTV.setText(listCompany.get(position).getAccountStatus());
                    } else {
                        holder.statusLL.setVisibility(View.VISIBLE);
                        holder.statusTV.setText(listCompany.get(position).getAccountStatus());
                    }
                } else {
                    holder.statusLL.setVisibility(View.GONE);
                }

            }

            if (listCompany.get(position).getImage() != null && !listCompany.get(position).getImage().trim().equals("")) {
                // profileImageText.setVisibility(View.GONE);
                holder.profileImage.setVisibility(View.VISIBLE);
                Glide.with(mContext)
                        .load(listCompany.get(position).getImage())
                        .placeholder(R.drawable.ic_case)
                        .into(holder.profileImage);

            } else {
                holder.profileImage.setVisibility(View.VISIBLE);
                // profileImageText.setVisibility(View.VISIBLE);
                Glide.with(mContext)
                        .load(listCompany.get(position).getImage())
                        .placeholder(R.drawable.ic_case)
                        .into(holder.profileImage);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    for (int i = 0; i < listCompany.size(); i++) {
                        if (position == i) {
                            listCompany.get(i).setSelected(true);
                            listener.selectedItem(listCompany.get(i));

                        } else {
                            listCompany.get(i).setSelected(false);
                           // listener.selectedItem(null);
                        }
                    }
                    notifyDataSetChanged();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return listCompany.size();
    }

    public interface OnSelectListner{
        void selectedItem(ProfilesResponse.Profiles item);
    }


}
