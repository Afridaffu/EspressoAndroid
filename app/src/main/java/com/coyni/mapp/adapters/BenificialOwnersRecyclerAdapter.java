package com.coyni.mapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.coyni.mapp.R;
import com.coyni.mapp.model.summary.BeneficialOwnerInfo;
import com.coyni.mapp.utils.MyApplication;
import com.coyni.mapp.utils.Utils;

import java.util.List;

public class BenificialOwnersRecyclerAdapter extends
        RecyclerView.Adapter<BenificialOwnersRecyclerAdapter.MyViewHolder> {
    private List<BeneficialOwnerInfo> beneficialOwnerList;
    private Context mContext;
    private MyApplication objMyApplication;
    private int count = 0;
    private OnSelectListner listener;
    Boolean isCPwdEye = false;
    private  String convert;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView firstNameTx, lastnameTx, ssnTx, dobTx, ownershipTx, dateTx, numberTx, uploadeMethodTx;
        LinearLayout llUploadDocument;
        ImageView llEin;

        public MyViewHolder(View view) {
            super(view);
            numberTx = (TextView) view.findViewById(R.id.number_bo);
            firstNameTx = (TextView) view.findViewById(R.id.firstname_bo);
            lastnameTx = (TextView) view.findViewById(R.id.lastname_bo);
            ssnTx = (TextView) view.findViewById(R.id.ssn_bo);
            dobTx = (TextView) view.findViewById(R.id.dob_bo);
            ownershipTx = (TextView) view.findViewById(R.id.ownership_bo);
            dateTx = (TextView) view.findViewById(R.id.bo_uploaded_date);
            llUploadDocument = (LinearLayout) view.findViewById(R.id.llUploadDocument);
            uploadeMethodTx = (TextView) view.findViewById(R.id.bo_uploaded_method);
            llEin = (ImageView) view.findViewById(R.id.llEIN);

        }
    }


    public BenificialOwnersRecyclerAdapter(Context context, List<BeneficialOwnerInfo> beneficialOwnerList, OnSelectListner listener) {
        this.mContext = context;
        this.beneficialOwnerList = beneficialOwnerList;
        this.objMyApplication = (MyApplication) context.getApplicationContext();
        this.listener = listener;

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
            BeneficialOwnerInfo objData = beneficialOwnerList.get(position);
            count = position + 1;
            holder.numberTx.setText("Beneficial Owner " + count);
            if (objData.getFirstName() != null && !objData.getFirstName().equals("")) {
                holder.firstNameTx.setText(objData.getFirstName());
            }
            if (objData.getLastName() != null && !objData.getLastName().equals("")) {
                holder.lastnameTx.setText(objData.getLastName());
            }
            if (objData.getSsn() != null && !objData.getSsn().equals("")) {
                isCPwdEye = true;
                convert = objData.getSsn().replaceAll("\\-","");
                String converted = convert.replaceAll("\\w(?=\\w{2})", "•");
                String hifened = converted.substring(0, 3) + " - " + converted.substring(3, 5) + " - " + converted.substring(5);
                //String mEintext = cir.getSsnOrEin().substring(0,2).replaceAll("\\w(?=\\w{2})", ".")+ "-"+ cir.getSsnOrEin().substring(2).replaceAll("\\w(?=\\w{2})", ".");
                holder.ssnTx.setText(hifened);
            }
            if (objData.getDob() != null && !objData.getDob().equals("")) {
                holder.dobTx.setText(Utils.convertTxnDatebusiness(objData.getDob()));
            }
            if (objData.getOwnershipParcentage() != 0) {
                holder.ownershipTx.setText(objData.getOwnershipParcentage().toString() + "" + mContext.getString(R.string.percentageSymbol));
            }
            if (objData.getRequiredDocuments() != null && objData.getRequiredDocuments().size() > 0) {
                if (objData.getRequiredDocuments().get(0).getUpdatedAt() != null
                        && !objData.getRequiredDocuments().get(0).getUpdatedAt().equals("")) {
                    holder.dateTx.setText(mContext.getString(R.string.uploaded_on) + " " + Utils.convertDocUploadedDate(objData.getRequiredDocuments().
                            get(0).getUpdatedAt()));
                    holder.uploadeMethodTx.setText("Uploaded " + objData.getRequiredDocuments().
                            get(0).getImgName());

                    holder.llUploadDocument.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                listener.selectedItem(objData.getRequiredDocuments().
                                        get(0).getImgLink());
//                                notifyDataSetChanged();
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    });

                    holder.llEin.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                if (!isCPwdEye) {
                                    isCPwdEye = true;
                                    holder.llEin.setBackgroundResource(R.drawable.ic_eyeclose);
                                    String converted = convert.replaceAll("\\w(?=\\w{2})", "•");
                                    String hifened = converted.substring(0, 3) + " - " + converted.substring(3, 5) + " - " + converted.substring(5,converted.length());
                                    //String mEintext = cir.getSsnOrEin().substring(0,2).replaceAll("\\w(?=\\w{2})", ".")+ "-"+ cir.getSsnOrEin().substring(2).replaceAll("\\w(?=\\w{2})", ".");
                                    holder.ssnTx.setText(hifened);
                                } else {
                                    isCPwdEye = false;
                                    holder.llEin.setBackgroundResource(R.drawable.ic_eyeopen);
                                    String hifened = convert.substring(0, 3) + " - " + convert.substring(3, 5) + " - " + convert.substring(5,convert.length());
                                    holder.ssnTx.setText(hifened);
                                   // holder.ssnTx.setText(objData.getSsn().substring(0, 2) + "-" + objData.getSsn().substring(2));
                                }

                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    });

                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return beneficialOwnerList.size();
    }

    public interface OnSelectListner {
        void selectedItem(String file);
    }

}

