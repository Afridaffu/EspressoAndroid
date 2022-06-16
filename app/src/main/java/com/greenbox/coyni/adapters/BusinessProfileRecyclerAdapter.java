package com.greenbox.coyni.adapters;

import static android.service.controls.ControlsProviderService.TAG;

import android.content.Context;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.greenbox.coyni.R;
import com.greenbox.coyni.model.AccountsData;
import com.greenbox.coyni.model.preferences.BaseProfile;
import com.greenbox.coyni.model.preferences.ProfilesResponse;
import com.greenbox.coyni.utils.DisplayImageUtility;
import com.greenbox.coyni.utils.LogUtils;
import com.greenbox.coyni.utils.Utils;

import java.util.ArrayList;

public class BusinessProfileRecyclerAdapter extends BaseExpandableListAdapter {

    private Context context;
    private OnItemClickListener listener;
    private AccountsData accountsData;
    private int selectedID;
    private boolean showdba;
    private long mLastClickTime = 0L;

    public BusinessProfileRecyclerAdapter(Context context, AccountsData accountsData, int selectedID, boolean showdba) {
        this.context = context;
        this.accountsData = accountsData;
        this.selectedID = selectedID;
        this.showdba = showdba;
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
        LinearLayout childView = view.findViewById(R.id.ll_child_view);
        LinearLayout addDBA = view.findViewById(R.id.ll_add_dba);
        TextView addDbaText = view.findViewById(R.id.addDbaText);

        LogUtils.d("isLastChild", "isLastChild" + isLastChild);
        if (profilesList.size() == 1 && !profilesList.get(0).getAccountStatus().equalsIgnoreCase(Utils.BUSINESS_ACCOUNT_STATUS.ACTIVE.getStatus())) {
            addDBA.setEnabled(false);
            addDbaText.setEnabled(false);
        } else {
            for (int position = 0; position < profilesList.size(); position++) {
                if (profilesList.get(position).getAccountStatus().equalsIgnoreCase(Utils.BUSINESS_ACCOUNT_STATUS.UNDER_REVIEW.getStatus()) ||
                        profilesList.get(position).getAccountStatus().equalsIgnoreCase(Utils.BUSINESS_ACCOUNT_STATUS.UNVERIFIED.getStatus()) ||
                        profilesList.get(position).getAccountStatus().equalsIgnoreCase(Utils.BUSINESS_ACCOUNT_STATUS.ACTION_REQUIRED.getStatus())) {
                    addDBA.setEnabled(false);
                    addDbaText.setEnabled(false);
                    break;
                } else {
                    addDBA.setEnabled(true);
                    addDbaText.setEnabled(true);
                }
            }
        }

        if (detailInfo.getAccountType().equals(Utils.SHARED)) {
//            if (detailInfo.getCompanyName() != null) {
////                childItem.setText(detailInfo.getCompanyName());
////            } else {
////                childItem.setText("");
////            }
            if (detailInfo.getDbaName() != null) {
                if (detailInfo.getDbaName().length() > 20) {
                    childItem.setText(detailInfo.getDbaName().substring(0, 20));
                } else {
                    childItem.setText(detailInfo.getDbaName());
                }
            } else {
                childItem.setText("[Dba Name]");
            }
            if (detailInfo.getImage() != null && !detailInfo.getImage().trim().equals("")) {
                profileImage.setVisibility(View.VISIBLE);
                DisplayImageUtility utility = DisplayImageUtility.getInstance(context);
                utility.addImage(detailInfo.getImage(), profileImage, R.drawable.ic_case);
            } else {
                profileImage.setVisibility(View.VISIBLE);
            }
        } else if (detailInfo.getAccountType().equals(Utils.PERSONAL)) {
            if (detailInfo.getFullName() != null) {
                childItem.setText(detailInfo.getFullName());
            } else {
                childItem.setText("");
            }
        } else if (detailInfo.getAccountType().equals(Utils.BUSINESS)) {
            if (detailInfo.getDbaName() != null) {
                if (detailInfo.getDbaName().length() > 20) {
                    childItem.setText(detailInfo.getDbaName().substring(0, 20));
                } else {
                    childItem.setText(detailInfo.getDbaName());
                }
            } else {
                childItem.setText("[Dba Name]");
            }
            if (detailInfo.getImage() != null && !detailInfo.getImage().trim().equals("")) {
                profileImage.setVisibility(View.VISIBLE);

                DisplayImageUtility utility = DisplayImageUtility.getInstance(context);
                utility.addImage(detailInfo.getImage(), profileImage, R.drawable.acct_profile);
            } else {
                profileImage.setVisibility(View.VISIBLE);
                profileImageText.setVisibility(View.GONE);

//                Glide.with(context)
//                        .load(detailInfo.getImage())
//                        .placeholder(R.drawable.acct_profile)
//                        .into(profileImage);
            }
        }
        if (showdba && (childPosition == profilesList.size() - 1) && detailInfo.getAccountType().equals(Utils.BUSINESS)) {
            addDBA.setVisibility(View.VISIBLE);
        } else {
            addDBA.setVisibility(View.GONE);
        }
        LogUtils.v(TAG, detailInfo.getId() + " selected id " + selectedID);

        if (selectedID == detailInfo.getId()) {
            imvTickIcon.setVisibility(View.VISIBLE);
            childItem.setTextColor(context.getColor(R.color.primary_color));

            if (!detailInfo.getAccountStatus().equalsIgnoreCase(Utils.BUSINESS_ACCOUNT_STATUS.ACTIVE.getStatus())) {
                if (detailInfo.getAccountStatus().equalsIgnoreCase(Utils.BUSINESS_ACCOUNT_STATUS.REGISTRATION_CANCELED.getStatus()) || detailInfo.getAccountStatus().equalsIgnoreCase(Utils.BUSINESS_ACCOUNT_STATUS.DECLINED.getStatus())) {
                    statusTV.setVisibility(View.VISIBLE);
                    statusTV.setBackground(context.getDrawable(R.drawable.txn_failed_bg));
                    statusTV.setText(detailInfo.getAccountStatus());
                    statusTV.setTextColor(context.getColor(R.color.default_red));
                } else if (detailInfo.getAccountStatus().equalsIgnoreCase(Utils.BUSINESS_ACCOUNT_STATUS.ACTIVE.getStatus())) {
                    statusTV.setVisibility(View.VISIBLE);
                    statusTV.setBackground(context.getDrawable(R.drawable.txn_completed_bg));
                    statusTV.setText(detailInfo.getAccountStatus());
                    statusTV.setTextColor(context.getColor(R.color.active_green));
                } else if (detailInfo.getAccountStatus().equalsIgnoreCase(Utils.BUSINESS_ACCOUNT_STATUS.UNDER_REVIEW.getStatus())) {
                    statusTV.setVisibility(View.VISIBLE);
                    statusTV.setBackground(context.getDrawable(R.drawable.txn_inprogress_bg));
                    statusTV.setText(detailInfo.getAccountStatus());
                    statusTV.setTextColor(context.getColor(R.color.under_review_blue));
                } else if (detailInfo.getAccountStatus().equalsIgnoreCase(Utils.BUSINESS_ACCOUNT_STATUS.UNVERIFIED.getStatus()) || detailInfo.getAccountStatus().equalsIgnoreCase(Utils.BUSINESS_ACCOUNT_STATUS.ACTION_REQUIRED.getStatus())) {
                    statusTV.setVisibility(View.VISIBLE);
                    statusTV.setText(detailInfo.getAccountStatus());
                    statusTV.setBackground(context.getDrawable(R.drawable.txn_pending_bg));
                    statusTV.setTextColor(context.getColor(R.color.orange_status));
                }else if (detailInfo.getAccountStatus().equalsIgnoreCase(Utils.BUSINESS_ACCOUNT_STATUS.DEACTIVE.getStatus())) {
                    statusTV.setVisibility(View.VISIBLE);
                    statusTV.setText(detailInfo.getAccountStatus());
                    statusTV.setBackground(context.getDrawable(R.drawable.txn_deactive_bg));
                    statusTV.setTextColor(context.getColor(R.color.xdark_gray));
                }
            } else {
                statusTV.setVisibility(View.GONE);
            }
        } else {
            imvTickIcon.setVisibility(View.GONE);
            childItem.setTextColor(context.getColor(R.color.primary_black));
            if (!detailInfo.getAccountStatus().equalsIgnoreCase(Utils.BUSINESS_ACCOUNT_STATUS.ACTIVE.getStatus())) {
                if (detailInfo.getAccountStatus().equalsIgnoreCase(Utils.BUSINESS_ACCOUNT_STATUS.REGISTRATION_CANCELED.getStatus()) || detailInfo.getAccountStatus().equalsIgnoreCase(Utils.BUSINESS_ACCOUNT_STATUS.DECLINED.getStatus())) {
                    statusTV.setVisibility(View.VISIBLE);
                    statusTV.setBackground(context.getDrawable(R.drawable.txn_failed_bg));
                    statusTV.setText(detailInfo.getAccountStatus());
                    statusTV.setTextColor(context.getColor(R.color.default_red));
                } else if (detailInfo.getAccountStatus().equalsIgnoreCase(Utils.BUSINESS_ACCOUNT_STATUS.ACTIVE.getStatus())) {
                    statusTV.setVisibility(View.VISIBLE);
                    statusTV.setBackground(context.getDrawable(R.drawable.txn_completed_bg));
                    statusTV.setText(detailInfo.getAccountStatus());
                    statusTV.setTextColor(context.getColor(R.color.active_green));
                } else if (detailInfo.getAccountStatus().equalsIgnoreCase(Utils.BUSINESS_ACCOUNT_STATUS.UNDER_REVIEW.getStatus())) {
                    statusTV.setVisibility(View.VISIBLE);
                    statusTV.setBackground(context.getDrawable(R.drawable.txn_inprogress_bg));
                    statusTV.setText(detailInfo.getAccountStatus());
                    statusTV.setTextColor(context.getColor(R.color.under_review_blue));
                } else if (detailInfo.getAccountStatus().equalsIgnoreCase(Utils.BUSINESS_ACCOUNT_STATUS.UNVERIFIED.getStatus()) || detailInfo.getAccountStatus().equalsIgnoreCase(Utils.BUSINESS_ACCOUNT_STATUS.ACTION_REQUIRED.getStatus())) {
                    statusTV.setVisibility(View.VISIBLE);
                    statusTV.setText(detailInfo.getAccountStatus());
                    statusTV.setBackground(context.getDrawable(R.drawable.txn_pending_bg));
                    statusTV.setTextColor(context.getColor(R.color.orange_status));
                }else if (detailInfo.getAccountStatus().equalsIgnoreCase(Utils.BUSINESS_ACCOUNT_STATUS.DEACTIVE.getStatus())) {
                    statusTV.setVisibility(View.VISIBLE);
                    statusTV.setText(detailInfo.getAccountStatus());
                    statusTV.setBackground(context.getDrawable(R.drawable.txn_deactive_bg));
                    statusTV.setTextColor(context.getColor(R.color.xdark_gray));
                }
            } else {
                statusTV.setVisibility(View.GONE);
            }
        }

        if (detailInfo.getAccountType().equals(Utils.SHARED)) {
            if(detailInfo.getAccountStatus().equalsIgnoreCase(Utils.BUSINESS_ACCOUNT_STATUS.DEACTIVE.getStatus())) {
                childView.setAlpha(0.5f);
                childView.setEnabled(false);
            }else {
                childView.setAlpha(1.0f);
                childView.setEnabled(true);
                childView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (listener != null) {
                            selectedID = detailInfo.getId();
                            listener.onGroupClicked(groupPosition, detailInfo.getAccountType(), detailInfo.getId(), detailInfo.getFullName());
                            notifyDataSetChanged();
                        }
                    }
                });
            }

        }

        childView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                if (listener != null) {
                    selectedID = detailInfo.getId();
                    listener.onChildClicked(detailInfo);
                    notifyDataSetChanged();
                }
            }
        });

        addDBA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                if (listener != null) {
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
    public View getGroupView(int groupPosition, boolean isLastChild, View view, ViewGroup
            parent) {

        BaseProfile headerInfo = (BaseProfile) getGroup(groupPosition);
        if (view == null) {
            LayoutInflater inf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inf.inflate(R.layout.business_profile_accounts_item, null);
        }

        TextView heading = view.findViewById(R.id.title);
        TextView personalText = view.findViewById(R.id.b_imageTextTV);
        ImageView profileImage = view.findViewById(R.id.profile_img);
        ImageView arrowImg = view.findViewById(R.id.arrowImg);
        ImageView tickIcon = view.findViewById(R.id.tickIcon);
        LinearLayout groupView = view.findViewById(R.id.ll_group_view);

        LogUtils.v(TAG, "isselectedId" + selectedID);

        if (headerInfo.getAccountType().equals(Utils.SHARED)) {
            arrowImg.setVisibility(View.VISIBLE);
            heading.setText(R.string.shared_account);
            profileImage.setVisibility(View.VISIBLE);
            profileImage.setImageResource(R.drawable.shared_icon);

        } else if (headerInfo.getAccountType().equals(Utils.PERSONAL)) {
            if (selectedID == headerInfo.getId()) {
                arrowImg.setVisibility(View.GONE);
                tickIcon.setVisibility(View.VISIBLE);
                heading.setTextColor(context.getColor(R.color.primary_color));
            } else {
                arrowImg.setVisibility(View.GONE);
                tickIcon.setVisibility(View.GONE);
                heading.setTextColor(context.getColor(R.color.primary_black));

            }
            if (headerInfo.getFullName() != null) {
                if (headerInfo.getFullName().length() > 21) {
                    heading.setText(headerInfo.getFullName().substring(0, 20));
                } else {
                    heading.setText(headerInfo.getFullName());
                }
            } else {
                heading.setText("[Personal]");
            }
            if (headerInfo.getImage() != null && !headerInfo.getImage().trim().equals("")) {
                personalText.setVisibility(View.GONE);
                profileImage.setVisibility(View.VISIBLE);

                DisplayImageUtility utility = DisplayImageUtility.getInstance(context);
                utility.addImage(headerInfo.getImage(), profileImage, R.drawable.ic_case);
            } else {
                personalText.setVisibility(View.VISIBLE);
                profileImage.setVisibility(View.GONE);
                String userName = headerInfo.getFullName().substring(0, 1).toUpperCase();
                personalText.setText(userName);
            }
        } else if (headerInfo.getAccountType().equals(Utils.BUSINESS)) {
            arrowImg.setVisibility(View.VISIBLE);
            if (headerInfo.getCompanyName() != null) {
                if (headerInfo.getCompanyName().length() > 21) {
                    heading.setText(headerInfo.getCompanyName().substring(0, 20));
                } else {
                    heading.setText(headerInfo.getCompanyName());
                }
            } else {
                heading.setText("[Company Name]");
            }

            if (headerInfo.getImage() != null) {
                personalText.setVisibility(View.GONE);
                profileImage.setVisibility(View.VISIBLE);

                DisplayImageUtility utility = DisplayImageUtility.getInstance(context);
                utility.addImage(headerInfo.getImage(), profileImage, R.drawable.ic_case);
            } else {
                personalText.setVisibility(View.GONE);
                profileImage.setVisibility(View.VISIBLE);
                profileImage.setImageResource(R.drawable.ic_case);
            }

        }
        groupView.setEnabled(true);
        if (headerInfo.getAccountType().equals(Utils.PERSONAL)) {
            if (headerInfo.getAccountStatus().equalsIgnoreCase(Utils.BUSINESS_ACCOUNT_STATUS.DEACTIVE.getStatus())
                    || headerInfo.getAccountStatus().equalsIgnoreCase(Utils.BUSINESS_ACCOUNT_STATUS.DECLINED.getStatus())) {
                groupView.setAlpha(0.5f);
                groupView.setEnabled(false);
            } else {
                groupView.setAlpha(1.0f);
                groupView.setEnabled(true);
                groupView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (listener != null) {
                            selectedID = headerInfo.getId();
                            listener.onGroupClicked(groupPosition, headerInfo.getAccountType(), headerInfo.getId(), headerInfo.getFullName());
                            notifyDataSetChanged();
                        }
                    }
                });
            }
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

        void onGroupClicked(int position, String accountType, Integer id, String fullName);

        void onChildClicked(ProfilesResponse.Profiles detailInfo);

        void onAddDbaClicked(String accountType, Integer id);
    }


}


