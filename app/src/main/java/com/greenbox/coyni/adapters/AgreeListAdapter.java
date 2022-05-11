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
import com.greenbox.coyni.utils.Utils;
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
        int docId = 0;
        if ((items.get(pos).getSignatureType() == Utils.mPP || items.get(pos).getSignatureType() == Utils.cPP)
                && items.get(pos).getDocumentVersion() != null) {
            docId = Utils.cPP;
            holder.agreementTV.setText(context.getResources().getString(R.string.privay_policy) + " ");
            holder.listDocsTV.setText(items.get(pos).getDocumentVersion().toLowerCase(Locale.ROOT).replace(" ", ""));
        } else if ((items.get(pos).getSignatureType() == Utils.mTOS || items.get(pos).getSignatureType() == Utils.cTOS)
                && items.get(pos).getDocumentVersion() != null) {
            docId = Utils.cTOS;
            holder.agreementTV.setText(context.getResources().getString(R.string.tos) + " ");
            holder.listDocsTV.setText(items.get(pos).getDocumentVersion().toLowerCase(Locale.ROOT).replace(" ", ""));
        } else if (items.get(pos).getSignatureType() == Utils.mAgmt && items.get(pos).getDocumentVersion() != null) {
            docId = Utils.mAgmt;
            holder.agreementTV.setText(context.getResources().getString(R.string.merchant_s_agreement) + " ");
            holder.listDocsTV.setText(items.get(pos).getDocumentVersion().toLowerCase(Locale.ROOT).replace(" ", ""));
        }
        holder.itemView.setTag(docId);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int doc = (int) view.getTag();
                recyclerClickListener.click(view, doc);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (this.items != null) {
            return items.size();
        }
        return 0;
    }

    public interface RecyclerClickListener {
        void click(View view, int docId);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        View itemView;
        TextView agreementTV, listDocsTV;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            agreementTV = itemView.findViewById(R.id.listagreementsTV);
            listDocsTV = itemView.findViewById(R.id.listDocsTV);
        }
    }
}
