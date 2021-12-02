package com.greenbox.coyni.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.greenbox.coyni.R;
import com.greenbox.coyni.model.Item;
import com.greenbox.coyni.model.States;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.viewmodel.DashboardViewModel;

import java.util.List;
import java.util.Locale;

public class PastAgreeListAdapter extends RecyclerView.Adapter<PastAgreeListAdapter.MyViewHolder> {
    List<Item> items;
    Context mContext;
    String privacyURL = "https://crypto-resources.s3.amazonaws.com/Greenbox+POS+GDPR+Privacy+Policy.pdf";
    String tosURL = "https://crypto-resources.s3.amazonaws.com/Gen+3+V1+TOS+v6.pdf";

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
            if (objData.getSignatureType() == 1) {
                holder.listagreementsTV.setText("Privacy Policy " + objData.getDocumentVersion().toLowerCase(Locale.ROOT).replace(" ", ""));
            } else {
                holder.listagreementsTV.setText("Terms of Service " + objData.getDocumentVersion().toLowerCase(Locale.ROOT).replace(" ", ""));
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (objData.getSignatureType() == 0) {
                        Intent inte = new Intent(Intent.ACTION_VIEW);
                        inte.setDataAndType(
                                Uri.parse(tosURL),
                                "application/pdf");
                        mContext.startActivity(inte);
                    } else {
                        Intent inte = new Intent(Intent.ACTION_VIEW);
                        inte.setDataAndType(
                                Uri.parse(privacyURL),
                                "application/pdf");
                        mContext.startActivity(inte);
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

