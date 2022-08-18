package com.coyni.mapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.coyni.mapp.R;
import com.coyni.mapp.model.BeneficialOwners.BOResp;
import com.coyni.mapp.view.business.AdditionalBeneficialOwnersActivity;

public class BeneficialOwnersAdapter extends RecyclerView.Adapter<BeneficialOwnersAdapter.MyViewHolder> {

    Context context;
    BOResp boResp;
    AdditionalBeneficialOwnersActivity activity;

    public BeneficialOwnersAdapter(Context context, BOResp boResp) {
        this.context = context;
        this.boResp = boResp;
        this.activity = (AdditionalBeneficialOwnersActivity) context;
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

            if (boResp.getData().get(pos).isDraft())
                holder.draftTV.setVisibility(View.GONE);
            else
                holder.draftTV.setVisibility(View.GONE);

            try {
                if (!boResp.getData().get(pos).getFirstName().equals("")
                        && !boResp.getData().get(pos).getLastName().equals("")) {
                    holder.ownerNameTV.setText(boResp.getData().get(pos).getFirstName() + " " + boResp.getData().get(pos).getLastName());
                } else if (!boResp.getData().get(pos).getFirstName().equals("")
                        && boResp.getData().get(pos).getLastName().equals("")) {
                    holder.ownerNameTV.setText(boResp.getData().get(pos).getFirstName());
                } else if (boResp.getData().get(pos).getFirstName().equals("")
                        && !boResp.getData().get(pos).getLastName().equals("")) {
                    holder.ownerNameTV.setText(boResp.getData().get(pos).getLastName());
                } else {
                    holder.ownerNameTV.setText("No name");
                }
            } catch (Exception e) {
                e.printStackTrace();
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

        TextView ownerNameTV, ownershipTV, draftTV;
        ImageView moreIV;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            try {
                ownerNameTV = itemView.findViewById(R.id.ownerNameTV);
                ownershipTV = itemView.findViewById(R.id.ownershipTV);
                moreIV = itemView.findViewById(R.id.moreIV);
                draftTV = itemView.findViewById(R.id.draftTV);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}
