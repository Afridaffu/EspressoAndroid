package com.coyni.mapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.coyni.mapp.R;
import com.coyni.mapp.model.Item;
import com.coyni.mapp.utils.Utils;

import java.util.List;
import java.util.Locale;

public class AgreeListAdapter extends RecyclerView.Adapter<AgreeListAdapter.MyViewHolder> {

    private Context context;
    private List<Item> items;
    private RecyclerClickListener recyclerClickListener;

    public AgreeListAdapter(Context context, List<Item> agreementsList, RecyclerClickListener listener) {
        this.context = context;
        this.items = agreementsList;
        this.recyclerClickListener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_items, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int pos) {
        if (items.get(pos).getSignatureType() == Utils.cPP) {
            holder.agreementTV.setText(context.getResources().getString(R.string.privacy_policy) + " ");
        } else if (items.get(pos).getSignatureType() == Utils.cTOS) {
            holder.agreementTV.setText(context.getResources().getString(R.string.tos) + " ");
        }
//        else if (items.get(pos).getSignatureType() == Utils.mAgmt) {
//            holder.agreementTV.setText(context.getResources().getString(R.string.merchant_s_agreement) + " ");
//        }
        if (items.get(pos).getDocumentVersion() != null && !items.get(pos).getDocumentVersion().equals("")) {
            holder.listDocsTV.setText(items.get(pos).getDocumentVersion().toLowerCase(Locale.ROOT).replace(" ", ""));
        }

        holder.itemView.setTag(items.get(pos));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Item doc = (Item) view.getTag();
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
        void click(View view, Item doc);
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
