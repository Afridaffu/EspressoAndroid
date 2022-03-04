package com.greenbox.coyni.adapters;

import static com.greenbox.coyni.utils.Utils.convertDate;

import android.content.Context;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.greenbox.coyni.R;
import com.greenbox.coyni.model.BeneficialOwners.BOResp;
import com.greenbox.coyni.model.preferences.ProfilesResponse;
import com.greenbox.coyni.model.summary.BeneficialOwnerInfo;
import com.greenbox.coyni.utils.LogUtils;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.view.business.ReviewApplicationActivity;

import java.util.List;

public class BenificialOwnersRecyclerAdapter extends
        RecyclerView.Adapter<BenificialOwnersRecyclerAdapter.MyViewHolder> {
    private List<BeneficialOwnerInfo> beneficialOwnerList;
    private Context mContext;
    private MyApplication objMyApplication;
    private int count=0;
    private OnSelectListner listener;
    Boolean isCPwdEye = false;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView firstNameTx,lastnameTx,ssnTx,dobTx,ownershipTx,dateTx,numberTx,uploadeMethodTx;
        LinearLayout llUploadDocument;
        ImageView llEin;

        public MyViewHolder(View view) {
            super(view);
            numberTx=(TextView)view.findViewById(R.id.number_bo);
            firstNameTx = (TextView) view.findViewById(R.id.firstname_bo);
            lastnameTx=(TextView) view.findViewById(R.id.lastname_bo);
            ssnTx=(TextView) view.findViewById(R.id.ssn_bo);
            dobTx=(TextView) view.findViewById(R.id.dob_bo);
            ownershipTx=(TextView) view.findViewById(R.id.ownership_bo);
            dateTx=(TextView) view.findViewById(R.id.bo_uploaded_date);
            llUploadDocument=(LinearLayout) view.findViewById(R.id.llUploadDocument);
            uploadeMethodTx=(TextView) view.findViewById(R.id.bo_uploaded_method);
            llEin = (ImageView)view.findViewById(R.id.llEIN);

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
            count=position+1;
            holder.numberTx.setText("Beneficial Owner "+count);
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
                holder.dobTx.setText(Utils.convertTxnDatebusiness(objData.getDob()));
            }
            if(objData.getOwnershipParcentage()!=0) {
                holder.ownershipTx.setText(objData.getOwnershipParcentage().toString()+""+mContext.getString(R.string.percentageSymbol));
            }
            if (objData.getRequiredDocuments() != null && objData.getRequiredDocuments().size() > 0) {
                if (objData.getRequiredDocuments().get(0).getUpdatedAt() != null
                && !objData.getRequiredDocuments().get(0).getUpdatedAt().equals("")) {
                    holder.dateTx.setText(mContext.getString(R.string.uploaded_on)+" "+ Utils.convertDocUploadedDate(objData.getRequiredDocuments().
                                  get(0).getUpdatedAt()));
                    holder.uploadeMethodTx.setText("Uploaded [ "+objData.getRequiredDocuments().
                            get(0).getImgName()+"]");

                    holder.llUploadDocument.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                listener.selectedItem(objData.getRequiredDocuments().
                                        get(0).getImgLink());
                                notifyDataSetChanged();
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
                                    holder.llEin.setBackgroundResource(R.drawable.ic_eyeopen);
                                    holder.ssnTx.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                                } else {
                                    isCPwdEye = false;
                                    holder.llEin.setBackgroundResource(R.drawable.ic_eyeclose);
                                    holder.ssnTx.setTransformationMethod(PasswordTransformationMethod.getInstance());
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

    public interface OnSelectListner{
        void selectedItem(String file);
    }

}

