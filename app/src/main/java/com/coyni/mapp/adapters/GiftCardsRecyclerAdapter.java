package com.coyni.mapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.coyni.mapp.R;
import com.coyni.mapp.model.giftcard.Brand;
import com.coyni.mapp.utils.MyApplication;
import com.coyni.mapp.view.GiftCardActivity;
import com.coyni.mapp.view.GiftCardDetails;

import java.util.List;

public class GiftCardsRecyclerAdapter extends RecyclerView.Adapter<GiftCardsRecyclerAdapter.MyViewHolder> {
    List<Brand> brands;
    Context mContext;
    MyApplication objMyApplication;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imgBrand;
        TextView brandNameTV;

        public MyViewHolder(View view) {
            super(view);
            imgBrand = (ImageView) view.findViewById(R.id.imgBrand);
            brandNameTV = (TextView) view.findViewById(R.id.brandNameTV);
        }
    }


    public GiftCardsRecyclerAdapter(Context context, List<Brand> list) {
        this.mContext = context;
        this.brands = list;
        this.objMyApplication = (MyApplication) context.getApplicationContext();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.giftcard_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        try {
            Brand objData = brands.get(position);
            holder.brandNameTV.setText(objData.getBrandName());
            if (objData.getImageUrls().get_1200w326ppi() != null && !objData.getImageUrls().get_1200w326ppi().equals("")) {
                Glide.with(mContext).load(objData.getImageUrls().get_1200w326ppi().trim()).into(holder.imgBrand);
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
//                        if(Utils.isKeyboardVisible)
//                            Utils.hideKeypad(mContext);
                        if (objData != null) {
                            GiftCardActivity giftCardActivity = (GiftCardActivity) mContext;
                            Intent i = new Intent(mContext, GiftCardDetails.class);
                            i.putExtra("BRAND_KEY", objData.getBrandKey());
                            mContext.startActivity(i);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return brands.size();
    }


}
