package com.greenbox.coyni.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.greenbox.coyni.R;
import com.greenbox.coyni.model.preferences.ProfilesResponse;
import com.greenbox.coyni.utils.LogUtils;

import java.util.HashMap;
import java.util.List;

public class BusinessProfileRecyclerAdapter extends BaseExpandableListAdapter {

    private Context _context;

    private List<ProfilesResponse.Profiles> _listDataHeader; // header titles

    public BusinessProfileRecyclerAdapter(Context context, List<ProfilesResponse.Profiles> listDataHeader) {
        this._context = context;
        this._listDataHeader = listDataHeader;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataHeader.get(groupPosition).getDbaName();//npt array
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        final String childText = _listDataHeader.get(groupPosition).getDbaName();

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.business_profile_accounts_item_child, null);

        }

        TextView txtListChild = (TextView) convertView.findViewById(R.id.title);
        ImageView mIvUserIcon = (ImageView) convertView.findViewById(R.id.profile_img);
        TextView mTvUserIconText = (TextView) convertView.findViewById(R.id.b_imageTextTV);

        txtListChild.setText(childText);

        String iconText = "";

        if ( _listDataHeader.get(groupPosition) != null
                && _listDataHeader.get(groupPosition).getDbaName() != null) {
            String firstName = _listDataHeader.get(groupPosition).getDbaName();
            iconText = firstName.substring(0, 1).toUpperCase();
            String username = firstName.substring(0, 1).toUpperCase() + firstName.substring(1).toLowerCase();

        }
        if (_listDataHeader.get(groupPosition) != null
                && _listDataHeader.get(groupPosition).getImage() != null) {
            mTvUserIconText.setVisibility(View.GONE);
            mIvUserIcon.setVisibility(View.VISIBLE);
            Glide.with(_context)
                    .load(_listDataHeader.get(groupPosition).getImage())
                    .placeholder(R.drawable.ic_profile_male_user)
                    .into(mIvUserIcon);
        } else {
            mTvUserIconText.setVisibility(View.VISIBLE);
            mIvUserIcon.setVisibility(View.GONE);
            mTvUserIconText.setText(iconText);
        }
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String headerTitle = _listDataHeader.get(groupPosition).getCompanyName();
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.business_profile_accounts_item, null);
        }

        TextView lblListHeader = (TextView) convertView.findViewById(R.id.title);
        ImageView mIvUserIcon = (ImageView) convertView.findViewById(R.id.profile_img);
        TextView mTvUserIconText = (TextView) convertView.findViewById(R.id.b_imageTextTV);

        String iconText = "";
        LogUtils.d("adapterr","adpter"+_listDataHeader.get(groupPosition).getFullName());
        LogUtils.d("adapterr123","adpter1"+_listDataHeader.get(groupPosition).getCompanyName());
        if ( _listDataHeader.get(groupPosition) != null
                && _listDataHeader.get(groupPosition).getCompanyName() != null) {
            String firstName = _listDataHeader.get(groupPosition).getCompanyName();
            iconText = firstName.substring(0, 1).toUpperCase();
            String username = firstName.substring(0, 1).toUpperCase() + firstName.substring(1).toLowerCase();

        }
        if (_listDataHeader.get(groupPosition) != null
                && _listDataHeader.get(groupPosition).getImage() != null) {
            mTvUserIconText.setVisibility(View.GONE);
            mIvUserIcon.setVisibility(View.VISIBLE);
            Glide.with(_context)
                    .load(_listDataHeader.get(groupPosition).getImage())
                    .placeholder(R.drawable.ic_profile_male_user)
                    .into(mIvUserIcon);
        } else {
            mTvUserIconText.setVisibility(View.VISIBLE);
            mIvUserIcon.setVisibility(View.GONE);
            mTvUserIconText.setText(iconText);
        }

        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);


        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

}


