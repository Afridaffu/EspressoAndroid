package com.greenbox.coyni.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.greenbox.coyni.R;
import com.greenbox.coyni.model.preferences.ProfilesResponse;
import com.greenbox.coyni.model.profile.BusinessAccountDbaInfo;
import com.greenbox.coyni.model.profile.BusinessAccountsListInfo;
import com.greenbox.coyni.utils.LogUtils;
import com.greenbox.coyni.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BusinessProfileRecyclerAdapter extends BaseExpandableListAdapter {

    private Context context;
    private ArrayList<BusinessAccountsListInfo> mainSetName;
    private List<ProfilesResponse.Profiles> businessAccountList;
    private OnSelectListner listener;


    public BusinessProfileRecyclerAdapter(Context context, ArrayList<BusinessAccountsListInfo> deptList,OnSelectListner listener) {
        this.context = context;
        this.mainSetName = deptList;
        this.listener = listener;
        this.businessAccountList = businessAccountList;
        LogUtils.d("listener","listener"+listener);

    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        ArrayList<BusinessAccountDbaInfo> productList = mainSetName.get(groupPosition).getSubsetName();
        return productList.get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                             View view, ViewGroup parent) {

        BusinessAccountDbaInfo detailInfo = (BusinessAccountDbaInfo) getChild(groupPosition, childPosition);
        if (view == null) {
            LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = infalInflater.inflate(R.layout.business_profile_accounts_item_child, null);
        }
        TextView childItem = (TextView) view.findViewById(R.id.title);
        TextView profileImageText = (TextView) view.findViewById(R.id.b_imageTextTV);
        ImageView profileImage = (ImageView) view.findViewById(R.id.profile_img);
        ImageView imvTickIcon = (ImageView) view.findViewById(R.id.tickIcon);
        TextView statusTV = (TextView) view.findViewById(R.id.statusTV);
        LinearLayout statusLL = (LinearLayout) view.findViewById(R.id.statusLL);
        LinearLayout addDBA = (LinearLayout) view.findViewById(R.id.ll_add_dba);

        LogUtils.d("isLastChild","isLastChild"+isLastChild);

        if(isLastChild){
            addDBA.setVisibility(View.VISIBLE);
        } else {
            addDBA.setVisibility(View.GONE);
        }

        if(detailInfo.getName()!=null) {
            childItem.setText(detailInfo.getName().trim());
        } else {
            childItem.setText("");
        }

        if (detailInfo.getIsSelected()) {
           imvTickIcon.setVisibility(View.VISIBLE);
            statusLL.setVisibility(View.GONE);
        } else {
           imvTickIcon.setVisibility(View.GONE);
            if(!detailInfo.getAccountSttaus().equalsIgnoreCase(Utils.BUSINESS_ACCOUNT_STATUS.ACTIVE.getStatus())){
                if(detailInfo.getAccountSttaus().equalsIgnoreCase(Utils.BUSINESS_ACCOUNT_STATUS.TERMINATED.getStatus())){
                    statusLL.setVisibility(View.VISIBLE);
                    statusLL.setBackground(context.getDrawable(R.drawable.txn_failed_bg));
                    statusTV.setText(detailInfo.getAccountSttaus());
                    statusTV.setTextColor(context.getColor(R.color.default_red));

                } else if(detailInfo.getAccountSttaus().equalsIgnoreCase(Utils.BUSINESS_ACCOUNT_STATUS.UNDER_REVIEW.getStatus())){
                    statusLL.setVisibility(View.VISIBLE);
                    statusLL.setBackground(context.getDrawable(R.drawable.txn_inprogress_bg));
                    statusTV.setText(detailInfo.getAccountSttaus());
                    statusTV.setTextColor(context.getColor(R.color.under_review_blue));

                } else {
                    statusLL.setVisibility(View.VISIBLE);
                    statusTV.setText(detailInfo.getAccountSttaus());
                    statusTV.setTextColor(context.getColor(R.color.orange_status));

                }
            } else {
                statusLL.setVisibility(View.GONE);
            }

        }

        if (detailInfo.getDbaImage() != null && !detailInfo.getDbaImage().trim().equals("")) {
           // profileImageText.setVisibility(View.GONE);
            profileImage.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(detailInfo.getDbaImage())
                    .placeholder(R.drawable.acct_profile)
                    .into(profileImage);

        } else {
            profileImage.setVisibility(View.VISIBLE);
           // profileImageText.setVisibility(View.VISIBLE);

            Glide.with(context)
                    .load(detailInfo.getDbaImage())
                    .placeholder(R.drawable.acct_profile)
                    .into(profileImage);
        }

        addDBA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    int companyID=0;
//                    for (ProfilesResponse.Profiles c : businessAccountList) {
//                        if(c.getCompanyName().equalsIgnoreCase(mainSetName.get(groupPosition).getName()) && c.getDbaOwner()==null){
//                            companyID = c.getId();
//                        }
//                    }
                    listener.selectedItem(mainSetName.get(groupPosition).getId());
                } catch (Exception ex){
                    ex.printStackTrace();
                }
            }

        });

        return view;
    }

    @Override
    public int getChildrenCount(int groupPosition) {

        ArrayList<BusinessAccountDbaInfo> productList = mainSetName.get(groupPosition).getSubsetName();
        return productList.size();

    }

    @Override
    public Object getGroup(int groupPosition) {
        return mainSetName.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return mainSetName.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isLastChild, View view,
                             ViewGroup parent) {

        BusinessAccountsListInfo headerInfo = (BusinessAccountsListInfo) getGroup(groupPosition);
        if (view == null) {
            LayoutInflater inf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inf.inflate(R.layout.business_profile_accounts_item, null);
        }

        TextView heading = (TextView) view.findViewById(R.id.title);
       // TextView profileImageText = (TextView) view.findViewById(R.id.b_imageTextTV);
        ImageView profileImage = (ImageView) view.findViewById(R.id.profile_img);

        if(headerInfo.getName()!=null) {
            heading.setText(headerInfo.getName());
        } else {
            heading.setText("");
        }

        if (headerInfo.getMainImage() != null && !headerInfo.getMainImage().trim().equals("")) {
           // profileImageText.setVisibility(View.GONE);
            profileImage.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(headerInfo.getMainImage())
                    .placeholder(R.drawable.ic_case)
                    .into(profileImage);
        } else {
            profileImage.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(headerInfo.getMainImage())
                    .placeholder(R.drawable.ic_case)
                    .into(profileImage);

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

    public interface OnSelectListner{
        void selectedItem(int id);
    }

}


