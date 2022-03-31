package com.greenbox.coyni.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.greenbox.coyni.R;
import com.greenbox.coyni.model.AccountsData;
import com.greenbox.coyni.model.preferences.BaseProfile;
import com.greenbox.coyni.model.preferences.ProfilesResponse;
import com.greenbox.coyni.utils.LogUtils;
import com.greenbox.coyni.utils.Utils;

import java.util.ArrayList;

public class BusinessProfileRecyclerAdapter extends BaseExpandableListAdapter {

    private Context context;
    //private ArrayList<BusinessAccountsListInfo> mainSetName;
    private OnItemClickListener listener;
    //private BusinessAccountsListInfo headerInfo;
    private AccountsData accountsData;

    public BusinessProfileRecyclerAdapter(Context context, AccountsData accountsData) {
        this.context = context;
        this.accountsData = accountsData;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        BaseProfile groupProfile = accountsData.getGroupData().get(groupPosition);
        ArrayList<ProfilesResponse.Profiles> profilesList = (ArrayList<ProfilesResponse.Profiles>) accountsData.getData().get(groupProfile.getId());
        return profilesList.get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        BaseProfile groupProfile = accountsData.getGroupData().get(groupPosition);
        ArrayList<ProfilesResponse.Profiles> profilesList = (ArrayList<ProfilesResponse.Profiles>) accountsData.getData().get(groupProfile.getId());
        return profilesList.get(childPosition).getId();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        BaseProfile groupProfile = accountsData.getGroupData().get(groupPosition);
        ArrayList<ProfilesResponse.Profiles> profilesList = (ArrayList<ProfilesResponse.Profiles>) accountsData.getData().get(groupProfile.getId());
        if (profilesList == null) {
            return 0;
        }
        return profilesList.size();
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                             View view, ViewGroup parent) {
        BaseProfile groupProfile = accountsData.getGroupData().get(groupPosition);
        ArrayList<ProfilesResponse.Profiles> profilesList = (ArrayList<ProfilesResponse.Profiles>) accountsData.getData().get(groupProfile.getId());
        ProfilesResponse.Profiles detailInfo = profilesList.get(childPosition);
        if (view == null) {
            LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = infalInflater.inflate(R.layout.business_profile_accounts_item_child, null);
        }

        TextView childItem = view.findViewById(R.id.title);
        TextView profileImageText = view.findViewById(R.id.b_imageTextTV);
        ImageView profileImage = view.findViewById(R.id.profile_img);
        ImageView imvTickIcon = view.findViewById(R.id.tickIcon);
        TextView statusTV = view.findViewById(R.id.statusTV);
        LinearLayout statusLL = view.findViewById(R.id.statusLL);
        LinearLayout childView = view.findViewById(R.id.ll_child_view);
        LinearLayout addDBA = view.findViewById(R.id.ll_add_dba);

        LogUtils.d("isLastChild", "isLastChild" + isLastChild);


        if (detailInfo.getAccountType().equals(Utils.SHARED)) {
            childItem.setText(detailInfo.getCompanyName());
        } else if (detailInfo.getAccountType().equals(Utils.PERSONAL)) {
            if (detailInfo != null && detailInfo.getFullName() != null) {
                childItem.setText(detailInfo.getFullName());
            } else {
                childItem.setText("Null Full Name");
            }
        } else if (detailInfo.getAccountType().equals(Utils.BUSINESS)) {
            if (detailInfo != null && detailInfo.getDbaName() != null) {
                childItem.setText(detailInfo.getDbaName());
            } else {
                childItem.setText("Null Company Name");
            }
        }

        if (isLastChild && detailInfo.getAccountType().equals(Utils.BUSINESS)) {
            addDBA.setVisibility(View.VISIBLE);
        } else {
            addDBA.setVisibility(View.GONE);
        }

        if (detailInfo.isSelected()) {
            imvTickIcon.setVisibility(View.VISIBLE);
            statusLL.setVisibility(View.GONE);
        } else {
            imvTickIcon.setVisibility(View.GONE);
            if (!detailInfo.getAccountStatus().equalsIgnoreCase(Utils.BUSINESS_ACCOUNT_STATUS.ACTIVE.getStatus())) {
                if (detailInfo.getAccountStatus().equalsIgnoreCase(Utils.BUSINESS_ACCOUNT_STATUS.TERMINATED.getStatus())) {
                    statusLL.setVisibility(View.VISIBLE);
                    statusLL.setBackground(context.getDrawable(R.drawable.txn_failed_bg));
                    statusTV.setText(detailInfo.getAccountStatus());
                    statusTV.setTextColor(context.getColor(R.color.default_red));

                } else if (detailInfo.getAccountStatus().equalsIgnoreCase(Utils.BUSINESS_ACCOUNT_STATUS.UNDER_REVIEW.getStatus())) {
                    statusLL.setVisibility(View.VISIBLE);
                    statusLL.setBackground(context.getDrawable(R.drawable.txn_inprogress_bg));
                    statusTV.setText(detailInfo.getAccountStatus());
                    statusTV.setTextColor(context.getColor(R.color.under_review_blue));

                } else {
                    statusLL.setVisibility(View.VISIBLE);
                    statusTV.setText(detailInfo.getAccountStatus());
                    statusTV.setTextColor(context.getColor(R.color.orange_status));
                }
            } else {
                statusLL.setVisibility(View.GONE);
            }
        }
        if (detailInfo.getImage() != null && !detailInfo.getImage().trim().equals("")) {
            profileImage.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(detailInfo.getImage())
                    .placeholder(R.drawable.acct_profile)
                    .into(profileImage);
        } else {
            profileImage.setVisibility(View.VISIBLE);
            // profileImageText.setVisibility(View.VISIBLE);

            Glide.with(context)
                    .load(detailInfo.getImage())
                    .placeholder(R.drawable.acct_profile)
                    .into(profileImage);
        }

        childView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null) {
                    listener.onChildClicked(detailInfo.getAccountType(), groupProfile.getId());
                }
            }
        });

        addDBA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null) {
                    listener.onAddDbaClicked(detailInfo.getAccountType(), groupProfile.getId());
                }
            }
        });

        return view;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return accountsData.getGroupData().get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return accountsData.getGroupData().size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return accountsData.getGroupData().get(groupPosition).getId();
    }

    @Override
    public View getGroupView(int groupPosition, boolean isLastChild, View view, ViewGroup parent) {

        BaseProfile headerInfo = (BaseProfile) getGroup(groupPosition);
        if (view == null) {
            LayoutInflater inf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inf.inflate(R.layout.business_profile_accounts_item, null);
        }

        TextView heading = view.findViewById(R.id.title);
        ImageView profileImage = view.findViewById(R.id.profile_img);
        LinearLayout groupView = view.findViewById(R.id.ll_group_view);

        if (headerInfo.getAccountType().equals(Utils.SHARED)) {
            heading.setText(R.string.shared_account);
        } else if (headerInfo.getAccountType().equals(Utils.PERSONAL)) {
            if (headerInfo != null && headerInfo.getFullName() != null) {
                heading.setText(headerInfo.getFullName());
            } else {
                heading.setText("Null Full Name");
            }
        } else if (headerInfo.getAccountType().equals(Utils.BUSINESS)) {
            if (headerInfo != null && headerInfo.getCompanyName() != null) {
                heading.setText(headerInfo.getCompanyName());
            } else {
                heading.setText("Null Company Name");
            }
        }

        groupView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null) {
                    listener.onGroupClicked(headerInfo.getAccountType(), headerInfo.getId());
                }
            }
        });

        if (headerInfo != null && headerInfo.getImage() != null && !headerInfo.getImage().trim().equals("")) {
            // profileImageText.setVisibility(View.GONE);
            profileImage.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(headerInfo.getImage())
                    .placeholder(R.drawable.ic_case)
                    .into(profileImage);
        } else {
            profileImage.setVisibility(View.VISIBLE);
            profileImage.setImageResource(R.drawable.ic_case);
        }

        return view;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public interface OnSelectListner {
        void selectedItem(int id);
    }

    public interface OnItemClickListener {
        public void onGroupClicked(String accountType, Integer id);

        public void onChildClicked(String accountType, Integer id);

        public void onAddDbaClicked(String accountType, Integer id);
    }

}


