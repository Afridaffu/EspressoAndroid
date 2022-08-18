package com.coyni.mapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.coyni.mapp.R;
import com.coyni.mapp.model.Item;
import com.coyni.mapp.utils.Utils;

import java.util.List;
import java.util.Locale;

public class PastAgreeListAdapter extends RecyclerView.Adapter<PastAgreeListAdapter.MyViewHolder> {
    private List<Item> items;
    private Context mContext;
    private String privacyURL = "https://crypto-resources.s3.amazonaws.com/Greenbox+POS+GDPR+Privacy+Policy.pdf";
    private String tosURL = "https://crypto-resources.s3.amazonaws.com/Gen+3+V1+TOS+v6.pdf";
    //MyApplication objMyApplication;
    private AgreementClickListener listener;

    public interface AgreementClickListener {
        void click(View view, Item doc);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView listagreementsTV;

        public MyViewHolder(View view) {
            super(view);
            listagreementsTV = (TextView) view.findViewById(R.id.listagreementsTV);
        }
    }

    public PastAgreeListAdapter(List<Item> list, Context context) {
        this.mContext = context;
        this.items = list;
    }

    public void setOnAgreementClickListener(AgreementClickListener listener) {
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_items, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        try {
            Item objData = items.get(position);
            if (objData.getSignatureType() == Utils.cPP) {
                holder.listagreementsTV.setText("Privacy Policy " + objData.getDocumentVersion().toLowerCase(Locale.ROOT).replace(" ", ""));
            } else if (objData.getSignatureType() == Utils.cTOS) {
                holder.listagreementsTV.setText("Terms of Service " + objData.getDocumentVersion().toLowerCase(Locale.ROOT).replace(" ", ""));
            } else if (objData.getSignatureType() == Utils.mAgmt) {
                holder.listagreementsTV.setText("Merchant’s Agreement " + objData.getDocumentVersion().toLowerCase(Locale.ROOT).replace(" ", ""));
            }
            holder.itemView.setTag(objData);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Item data = (Item) v.getTag();
                    if (listener != null) {
                        listener.click(v, data);
                    }
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}

