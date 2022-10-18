package com.coyni.mapp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.coyni.mapp.R;
import com.coyni.mapp.model.preferences.BaseProfile;
import com.coyni.mapp.utils.DisplayImageUtility;
import com.coyni.mapp.utils.Utils;

import java.util.List;

public class AddNewBusinessAccountDBAAdapter extends RecyclerView.Adapter<AddNewBusinessAccountDBAAdapter.MyViewHolder> {
    private OnSelectListner listener;
    List<BaseProfile> listCompany;
    Context mContext;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView txvCompanyName, tvDBACount;
        private ImageView imvTickIcon, profileImage;
        private View viewLine;
//        private TextView statusTV;
//        private LinearLayout statusLL;


        public MyViewHolder(View view) {
            super(view);
            txvCompanyName = (TextView) view.findViewById(R.id.title);
            tvDBACount = (TextView) view.findViewById(R.id.tvDBACount);
            imvTickIcon = (ImageView) view.findViewById(R.id.tickIcon);
            profileImage = (ImageView) view.findViewById(R.id.profileImage);
            viewLine = (View) view.findViewById(R.id.viewLine);
//            statusTV = (TextView) view.findViewById(R.id.statusTV);
//            statusLL = (LinearLayout) view.findViewById(R.id.statusLL);
        }
    }


    public AddNewBusinessAccountDBAAdapter(List<BaseProfile> list, Context context, OnSelectListner listener) {
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

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        try {
            if (listCompany.get(position).getCompanyName() == null) {
                holder.txvCompanyName.setText("[Company Name]");
            } else {
                holder.txvCompanyName.setText(listCompany.get(position).getCompanyName());
                if (position == listCompany.size() - 1) {
                    holder.viewLine.setVisibility(View.GONE);
                } else {
                    holder.viewLine.setVisibility(View.VISIBLE);
                }
            }
            holder.tvDBACount.setText("Total DBAs : " + listCompany.get(position).getDbaCount());

            if (listCompany.get(position).isSelected()) {
                holder.imvTickIcon.setVisibility(View.VISIBLE);
                holder.txvCompanyName.setTextColor(mContext.getColor(R.color.primary_green));
            } else if (listCompany.get(position).getAccountStatus().equalsIgnoreCase(Utils.BUSINESS_ACCOUNT_STATUS.ACTIVE.getStatus())) {
                holder.txvCompanyName.setTextColor(mContext.getColor(R.color.primary_black));
                holder.imvTickIcon.setVisibility(View.GONE);
            } else {
                holder.txvCompanyName.setTextColor(mContext.getColor(R.color.light_gray));
                holder.imvTickIcon.setVisibility(View.GONE);
            }

//                holder.txvCompanyName.setTextColor(mContext.getColor(R.color.primary_black));
//                if (!listCompany.get(position).getAccountStatus().equalsIgnoreCase(Utils.BUSINESS_ACCOUNT_STATUS.ACTIVE.getStatus())) {
//
//                    if (listCompany.get(position).getAccountStatus().equalsIgnoreCase(Utils.BUSINESS_ACCOUNT_STATUS.TERMINATED.getStatus())) {
//                        holder.statusLL.setVisibility(View.VISIBLE);
//                        holder.statusLL.setBackgroundColor(mContext.getColor(R.color.default_red));
//                        holder.statusTV.setText(listCompany.get(position).getAccountStatus());
//                    } else if (listCompany.get(position).getAccountStatus().equalsIgnoreCase(Utils.BUSINESS_ACCOUNT_STATUS.UNDER_REVIEW.getStatus())) {
//                        holder.statusLL.setVisibility(View.VISIBLE);
//                        holder.statusLL.setBackgroundColor(mContext.getColor(R.color.under_review_blue));
//                        holder.statusTV.setText(listCompany.get(position).getAccountStatus());
//                    } else {
//                        holder.statusLL.setVisibility(View.VISIBLE);
//                        holder.statusTV.setText(listCompany.get(position).getAccountStatus());
//                    }
//                } else {
//                    holder.statusLL.setVisibility(View.GONE);
//                }

            if (listCompany.get(position).getAccountStatus().equalsIgnoreCase(Utils.BUSINESS_ACCOUNT_STATUS.UNDER_REVIEW.getStatus()) ||
                    listCompany.get(position).getAccountStatus().equalsIgnoreCase(Utils.BUSINESS_ACCOUNT_STATUS.UNVERIFIED.getStatus()) ||
                    listCompany.get(position).getAccountStatus().equalsIgnoreCase(Utils.BUSINESS_ACCOUNT_STATUS.ACTION_REQUIRED.getStatus())) {
                holder.itemView.setEnabled(false);
                holder.txvCompanyName.setEnabled(false);
            } else {
                holder.itemView.setEnabled(true);
                holder.txvCompanyName.setEnabled(true);
            }

//            if (listCompany.get(position).getImage() != null
//                    && !listCompany.get(position).getImage().trim().equals("")) {
//                // profileImageText.setVisibility(View.GONE);
//                holder.profileImage.setVisibility(View.VISIBLE);
//                DisplayImageUtility utility = DisplayImageUtility.getInstance(mContext);
//                utility.addImage(listCompany.get(position).getImage(), holder.profileImage, R.drawable.ic_case);
//            } else {
                holder.profileImage.setVisibility(View.VISIBLE);
                holder.profileImage.setImageResource(R.drawable.ic_case);
//            }
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
//                             listener.selectedItem(null);
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
        if (listCompany == null) {
            return 0;
        }
        return listCompany.size();
    }

    public interface OnSelectListner {
        void selectedItem(BaseProfile item);
    }

}
