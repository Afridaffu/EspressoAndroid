package com.greenbox.coyni.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

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
        private ImageView imvTickIcon;
        private View viewLine;

        public MyViewHolder(View view) {
            super(view);
            txvCompanyName = (TextView) view.findViewById(R.id.txv_comapny_name);
            imvTickIcon = (ImageView) view.findViewById(R.id.tickIcon);
            viewLine = (View) view.findViewById(R.id.viewLine);
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
            } else {
                holder.imvTickIcon.setVisibility(View.GONE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    LogUtils.d("position","postion"+position+listCompany.size());
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
