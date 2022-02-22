package com.greenbox.coyni.adapters;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.greenbox.coyni.R;
import com.greenbox.coyni.model.BeneficialOwners.BOResp;
import com.greenbox.coyni.model.Item;
import com.greenbox.coyni.view.business.AdditionalBeneficialOwnersActivity;
import com.greenbox.coyni.viewmodel.DashboardViewModel;

import java.util.List;
import java.util.Locale;

public class BeneficialOwnersAdapter extends RecyclerView.Adapter<BeneficialOwnersAdapter.MyViewHolder> {

    Context context;
    BOResp boResp;
    AdditionalBeneficialOwnersActivity activity;
    public BeneficialOwnersAdapter(Context context, BOResp boResp) {
        this.context = context;
        this.boResp = boResp;
        this.activity =  (AdditionalBeneficialOwnersActivity) context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.beneficial_owners_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int pos) {

        try {
            if (boResp.getData().get(pos).getFirstName() != null && boResp.getData().get(pos).getLastName() != null) {
                holder.ownerNameTV.setText(boResp.getData().get(pos).getFirstName() + " " + boResp.getData().get(pos).getLastName());
            } else if (boResp.getData().get(pos).getFirstName() != null && boResp.getData().get(pos).getLastName() == null) {
                holder.ownerNameTV.setText(boResp.getData().get(pos).getFirstName());
            } else if (boResp.getData().get(pos).getFirstName() == null && boResp.getData().get(pos).getLastName() != null) {
                holder.ownerNameTV.setText(boResp.getData().get(pos).getLastName());
            } else {
                holder.ownerNameTV.setText("No name");
            }

            holder.ownershipTV.setText(boResp.getData().get(pos).getOwnershipParcentage() + "% Ownership");

            holder.itemView.setOnClickListener(view -> {
                activity.chooseEditORDelete(boResp.getData().get(pos).getId());
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        try {
            if (this.boResp != null && boResp.getData().size() > 0) {
                return boResp.getData().size();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView ownerNameTV, ownershipTV;
        ImageView moreIV;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            try {
                ownerNameTV = itemView.findViewById(R.id.ownerNameTV);
                ownershipTV = itemView.findViewById(R.id.ownershipTV);
                moreIV = itemView.findViewById(R.id.moreIV);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}
