package com.coyni.mapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.coyni.mapp.R;
import com.coyni.mapp.model.team.TeamData;
import com.coyni.mapp.utils.Utils;

import java.util.List;

public class TeamAdapter extends RecyclerView.Adapter<TeamAdapter.MyViewHolder> {

    List<TeamData> listTeam;
    Context mContext;
    private String fullname = "";
    private TeamAdapter.TeamMemberClickListener memberClickListener;

    public TeamAdapter(Context context, List<TeamData> listTeam, TeamAdapter.TeamMemberClickListener memberClickListener) {
        this.mContext = context;
        this.listTeam = listTeam;
        this.memberClickListener = memberClickListener;
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
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_team, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int pos) {
        try {
            TeamData objData = listTeam.get(pos);
            String firstName = "", lastName = "";
            if (objData.getFirstName() != null && !objData.getFirstName().equals("")) {
                firstName = objData.getFirstName();
            }
            if (objData.getLastName() != null && !objData.getLastName().equals("")) {
                lastName = objData.getLastName();
            }
            char first = firstName.charAt(0);
            char last = lastName.charAt(0);
//            char last = lastName.charAt(0);


            String imageName = String.valueOf(first).toUpperCase() + String.valueOf(last).toUpperCase();
            holder.txImageName.setText(imageName);
            String name = Utils.capitalize(firstName) + " " + Utils.capitalize(lastName);
            holder.txName.setText(name);

            if (name.length() > 22) {
                holder.txName.setText(name.substring(0, 22) + "...");
            } else {
                holder.txName.setText(name);
            }

            if (objData.getRoleName() != null && !objData.getRoleName().equals("")) {
                holder.txRole.setText(objData.getRoleName());
            }


            if (objData.getStatus() != null && !objData.getStatus().equals("")) {
                holder.txStatus.setText(objData.getStatus().trim().replace(" ", "\n"));
                if (objData.getStatus().equalsIgnoreCase(Utils.teammemberpending)) {
                    holder.txStatus.setTextColor(getContext().getColor(R.color.pending_color));
                    holder.txStatus.setBackgroundResource(R.drawable.txn_pending_bg);
                } else if (objData.getStatus().equalsIgnoreCase(Utils.actionRequired)) {
                    holder.txStatus.setTextColor(getContext().getColor(R.color.xdark_gray));
                    holder.txStatus.setBackgroundResource(R.drawable.txn_deactive_bg);
                }else if (objData.getStatus().equalsIgnoreCase(Utils.unVerified)) {
                    holder.txStatus.setTextColor(getContext().getColor(R.color.xdark_gray));
                    holder.txStatus.setBackgroundResource(R.drawable.txn_deactive_bg);
                }else if (objData.getStatus().equalsIgnoreCase(Utils.active)) {
                    holder.txStatus.setTextColor(getContext().getColor(R.color.active_green));
                    holder.txStatus.setBackgroundResource(R.drawable.txn_active_bg);
                } else if (objData.getStatus().equalsIgnoreCase(Utils.underReview)) {
                    holder.txStatus.setTextColor(getContext().getColor(R.color.xdark_gray));
                    holder.txStatus.setBackgroundResource(R.drawable.txn_deactive_bg);
                } else if (objData.getStatus().equalsIgnoreCase(Utils.canceled)) {
                    holder.txStatus.setTextColor(getContext().getColor(R.color.error_red));
                    holder.txStatus.setBackgroundResource(R.drawable.txn_failed_bg);
                } else if (objData.getStatus().equalsIgnoreCase(Utils.expired)) {
                    holder.txStatus.setText(Utils.resendInvitation);
                    holder.txStatus.setTextColor(getContext().getColor(R.color.error_red));
                    holder.txStatus.setBackground(null);
                } else if (objData.getStatus().equalsIgnoreCase(Utils.resendInvitation)) {
                    holder.txStatus.setTextColor(getContext().getColor(R.color.error_red));
                    holder.txStatus.setBackground(null);
                }
//                else {
//                    holder.txStatus.setTextColor(getContext().getColor(R.color.xdark_gray));
//                    holder.txStatus.setBackgroundResource(R.drawable.txn_in_active_bg);
//                }
            }
            if (!objData.getStatus().equals(Utils.underReview) && !objData.getStatus().equals(Utils.actionRequired)
                    && !objData.getStatus().equals(Utils.unVerified)) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        memberClickListener.click(view, pos);
                    }
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        if (this.listTeam != null) {
            return listTeam.size();
        }
        return 0;
    }

    public interface TeamMemberClickListener {
        void click(View view, int position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView txName, txRole, txStatus, txImageName;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txName = itemView.findViewById(R.id.name);
            txRole = itemView.findViewById(R.id.role);
            txStatus = itemView.findViewById(R.id.status);
            txImageName = itemView.findViewById(R.id.imageTextTV);
//            itemView.setOnClickListener(this);
        }

//        @Override
//        public void onClick(View view) {
//            memberClickListener.click(view, getAdapterPosition());
//        }
    }
}