package com.greenbox.coyni.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.greenbox.coyni.R;
import com.greenbox.coyni.model.BeneficialOwners.BOResp;
import com.greenbox.coyni.utils.MyApplication;

import java.util.List;

public class BenificialOwnersRecyclerAdapter extends RecyclerView.Adapter<BenificialOwnersRecyclerAdapter.MyViewHolder> {
    List<BOResp.BeneficialOwner> beneficialOwnerList;
    Context mContext;
    MyApplication objMyApplication;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView firstNameTx,lastnameTx,ssnTx,dobTx,ownershipTx,dateTx,numberTx;

        public MyViewHolder(View view) {
            super(view);
            numberTx=(TextView)view.findViewById(R.id.number_bo);
            firstNameTx = (TextView) view.findViewById(R.id.firstname_bo);
            lastnameTx=(TextView) view.findViewById(R.id.lastname_bo);
            ssnTx=(TextView) view.findViewById(R.id.ssn_bo);
            dobTx=(TextView) view.findViewById(R.id.dob_bo);
            ownershipTx=(TextView) view.findViewById(R.id.ownership_bo);
            dateTx=(TextView) view.findViewById(R.id.bo_uploaded_date);
        }
    }


    public BenificialOwnersRecyclerAdapter(Context context, List<BOResp.BeneficialOwner> beneficialOwnerList) {
        this.mContext = context;
        this.beneficialOwnerList = beneficialOwnerList;
        this.objMyApplication = (MyApplication) context.getApplicationContext();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.review_application_beneficial_owners_list_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        try {
            BOResp.BeneficialOwner objData = beneficialOwnerList.get(position);
            holder.numberTx.setText("Beneficial Owner "+position);
            if(objData.getFirstName()!=null&&!objData.getFirstName().equals("")) {
                holder.firstNameTx.setText(objData.getFirstName());
            }
            if(objData.getLastName()!=null&&!objData.getLastName().equals("")) {
                holder.lastnameTx.setText(objData.getLastName());
            }
            if(objData.getSsn()!=null&&!objData.getSsn().equals("")) {
                holder.ssnTx.setText(objData.getSsn());
            }
            if(objData.getDob()!=null&&!objData.getDob().equals("")) {
                holder.dobTx.setText(objData.getDob());
            }
            if(objData.getOwnershipParcentage()!=0) {
                holder.ownershipTx.setText(objData.getOwnershipParcentage());
            }
            if(objData.getRequiredDocuments().get(position).getUpdatedAt()!=null&&!objData.getRequiredDocuments().get(position).getUpdatedAt().equals("")) {
                holder.dateTx.setText("Uploaded on"+""+objData.getRequiredDocuments().get(position).getUpdatedAt());
            }


        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return beneficialOwnerList.size();
    }


}

