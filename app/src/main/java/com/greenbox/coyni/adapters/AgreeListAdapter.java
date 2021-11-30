package com.greenbox.coyni.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.greenbox.coyni.R;
import com.greenbox.coyni.model.Agreements;
import com.greenbox.coyni.viewmodel.DashboardViewModel;

import java.util.Locale;

public class AgreeListAdapter extends RecyclerView.Adapter<AgreeListAdapter.MyViewHolder> {

    Context context;
    Agreements agreements;
    DashboardViewModel dashboardViewModel;
   private RecyclerClickListener recyclerClickListener;
    public AgreeListAdapter(Context context, Agreements agreementsList, DashboardViewModel dashboardViewModel,RecyclerClickListener listener) {
        this.context = context;
        this.agreements = agreementsList;
        this.dashboardViewModel = dashboardViewModel;
        this.recyclerClickListener=listener;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public Agreements getAgreementsList() {
        return agreements;
    }

    public void setAgreementsList(Agreements agreementsList) {
        this.agreements = agreementsList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view= LayoutInflater.from(context).inflate(R.layout.list_items,parent,false);
       return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int pos) {
            if (pos==0){
                holder.agreementTV.setText("Privacy Policy "+agreements.getData().getItems().get(pos+1).getDocumentVersion().toLowerCase(Locale.ROOT).replace(" ",""));
            }
            else if (pos==1){
                holder.agreementTV.setText("Terms of Service "+agreements.getData().getItems().get(pos-1).getDocumentVersion().toLowerCase(Locale.ROOT).replace(" ",""));
            }
    }

    @Override
    public int getItemCount() {
       if (this.agreements!=null){
            return agreements.getData().getItems().size();
        }
        return 0;
    }
    public interface RecyclerClickListener{
        void click(View view,int position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView agreementTV;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            agreementTV=itemView.findViewById(R.id.listagreementsTV);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            recyclerClickListener.click(view,getAdapterPosition());
        }
    }
}
