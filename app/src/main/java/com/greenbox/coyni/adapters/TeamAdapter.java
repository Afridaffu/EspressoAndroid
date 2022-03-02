package com.greenbox.coyni.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.greenbox.coyni.R;
import com.greenbox.coyni.model.giftcard.Brand;
import com.greenbox.coyni.model.paymentmethods.PaymentsList;
import com.greenbox.coyni.model.team.TeamData;
import com.greenbox.coyni.model.team.TeamResponseModel;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.view.BuyTokenActivity;
import com.greenbox.coyni.view.BuyTokenPaymentMethodsActivity;
import com.greenbox.coyni.view.WithdrawPaymentMethodsActivity;
import com.greenbox.coyni.view.WithdrawTokenActivity;
import com.greenbox.coyni.view.business.TeamMember;

import java.util.List;

public class TeamAdapter extends RecyclerView.Adapter<TeamAdapter.MyViewHolder> {
    List<TeamData> listTeam;
    Context mContext;
    MyApplication objMyApplication;
    String memberName="";
    private TeamAdapter.TeamMemberClickListener memberClickListener;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView txName, txRole, txStatus, txImageName;
        public LinearLayout teamLL;

        public MyViewHolder(View view) {
            super(view);
            txName=view.findViewById(R.id.name);
            txRole=view.findViewById(R.id.role);
            txStatus=view.findViewById(R.id.status);
            txImageName=view.findViewById(R.id.imageTextTV);

        }
    }


    public TeamAdapter(Context context,List<TeamData> listTeam,TeamAdapter.TeamMemberClickListener memberClickListener) {
        this.mContext = context;
        this.objMyApplication = (MyApplication) context.getApplicationContext();
        this.listTeam= listTeam;
        this.memberClickListener=memberClickListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_team, parent, false);
        return new MyViewHolder(itemView);
    }
    public interface TeamMemberClickListener {
        void onItemClick(View view, int position);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        try {
            TeamData objData = listTeam.get(position);
            String firstName="",lastName="";
            if(objData.getFirstName()!=null&&!objData.getFirstName().equals("")){
                 firstName=objData.getFirstName();
            }
            if(objData.getLastName()!=null&&!objData.getLastName().equals("")){
                lastName=objData.getLastName();
            }
            memberName=firstName.substring(0)+lastName.substring(0);
            holder.txImageName.setText(memberName);
            holder.txName.setText(firstName+""+lastName);
            if(objData.getRoleName()!=null&&!objData.getRoleName().equals("")){
                holder.txRole.setText(objData.getRoleName());
            }
            if(objData.getStatus()!=null&&!objData.getStatus().equals("")){
                holder.txStatus.setText(objData.getStatus().toString());
            }



        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return listTeam.size();
    }

}
