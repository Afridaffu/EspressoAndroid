package com.greenbox.coyni.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.greenbox.coyni.R;
import com.greenbox.coyni.model.BeneficialOwners.BOResp;
import com.greenbox.coyni.utils.MyApplication;

import java.util.List;

public class MerchantSettingsBeneficialOwnersAdapter extends RecyclerView.Adapter<MerchantSettingsBeneficialOwnersAdapter.MyViewHolder> {

    private List<BOResp.BeneficialOwner> beneficialOwnerList;
    Context mContext;
    private MyApplication objMyApplication;
    private MerchantSettingsBeneficialOwnersAdapter.RecyclerClickListener beneficialClickListener;


    public MerchantSettingsBeneficialOwnersAdapter(Context context, List<BOResp.BeneficialOwner> beneficialOwnerList, MerchantSettingsBeneficialOwnersAdapter.RecyclerClickListener beneficialClickListener) {
        this.mContext = context;
        this.objMyApplication = (MyApplication) context.getApplicationContext();
        this.beneficialOwnerList = beneficialOwnerList;
        this.beneficialClickListener = beneficialClickListener;
    }

    public Context getContext() {
        return mContext;
    }

    public void setContext(Context context) {
        this.mContext = context;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_merchant_settings_beneficial_owners, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int pos) {
        BOResp.BeneficialOwner objData = beneficialOwnerList.get(pos);
        if(pos==0){
            holder.primaryIcon.setVisibility(View.VISIBLE);
        }
        String firstName = "", lastName = "";
        if (objData.getFirstName() != null && !objData.getFirstName().equals("")) {
            firstName = objData.getFirstName();
        }
        if (objData.getLastName() != null && !objData.getLastName().equals("")) {
            lastName = objData.getLastName();
        }
        holder.txName.setText(firstName + "" + lastName+"");
        if (objData.getOwnershipParcentage() != 0) {
            holder.ownershipTx.setText(objData.getOwnershipParcentage() + "% Ownership");
        }
    }

    @Override
    public int getItemCount() {
        if (this.beneficialOwnerList != null) {
            return beneficialOwnerList.size();
        }
        return 0;
    }

    public interface RecyclerClickListener {
        void click(View view, int position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView txName, ownershipTx;
        public RelativeLayout boRL;
        public ImageView primaryIcon;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txName = itemView.findViewById(R.id.nameTx);
            ownershipTx = itemView.findViewById(R.id.ownershipTx);
            boRL = itemView.findViewById(R.id.bowLL);
            primaryIcon=itemView.findViewById(R.id.primary_icon);
            primaryIcon.setVisibility(View.GONE);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            beneficialClickListener.click(view, getAdapterPosition());
        }
    }
}
