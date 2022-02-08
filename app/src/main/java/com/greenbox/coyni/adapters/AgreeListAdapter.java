package com.greenbox.coyni.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.greenbox.coyni.R;
import com.greenbox.coyni.model.Item;
import com.greenbox.coyni.viewmodel.DashboardViewModel;

import java.util.List;
import java.util.Locale;

public class AgreeListAdapter extends RecyclerView.Adapter<AgreeListAdapter.MyViewHolder> {

    Context context;
    List<Item> items;
    DashboardViewModel dashboardViewModel;
    private RecyclerClickListener recyclerClickListener;

    public AgreeListAdapter(Context context, List<Item> agreementsList, DashboardViewModel dashboardViewModel, RecyclerClickListener listener) {
        this.context = context;
        this.items = agreementsList;
        this.dashboardViewModel = dashboardViewModel;
        this.recyclerClickListener = listener;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_items, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int pos) {
        if (items.get(pos).getSignatureType() == 1) {
            holder.agreementTV.setText("Privacy Policy " + items.get(pos).getDocumentVersion().toLowerCase(Locale.ROOT).replace(" ", ""));
        } else if (items.get(pos).getSignatureType() == 0) {
            holder.agreementTV.setText("Terms of Service " + items.get(pos).getDocumentVersion().toLowerCase(Locale.ROOT).replace(" ", ""));
        }
        if(items.get(pos).getSignatureType() == 5){
            holder.agreementTV.setText("Merchant’s Agreement " + items.get(pos).getDocumentVersion().toLowerCase(Locale.ROOT).replace(" ", ""));
        }
    }

    @Override
    public int getItemCount() {
        if (this.items != null) {
            return items.size();
        }
        return 0;
    }

    public interface RecyclerClickListener {
        void click(View view, int position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView agreementTV;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            agreementTV = itemView.findViewById(R.id.listagreementsTV);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            recyclerClickListener.click(view, getAdapterPosition());
        }
    }
}
