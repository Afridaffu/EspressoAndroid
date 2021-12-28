package com.greenbox.coyni.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.greenbox.coyni.R;
import com.greenbox.coyni.model.giftcard.Brand;
import com.greenbox.coyni.view.GiftCardDetails;

import java.util.List;

public class GiftCardsAdapter extends ArrayAdapter<Brand> {
    Context mContext;

    public GiftCardsAdapter(@NonNull Context context, List<Brand> courseModelArrayList) {
        super(context, 0, courseModelArrayList);
        mContext = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listitemView = convertView;
        try {
            if (listitemView == null) {
                listitemView = LayoutInflater.from(getContext()).inflate(R.layout.giftcard_item, parent, false);
            }
            Brand objData = getItem(position);
            ImageView imgBrand = listitemView.findViewById(R.id.imgBrand);
            TextView brandNameTV = listitemView.findViewById(R.id.brandNameTV);
            if (objData.getImageUrls().get_1200w326ppi() != null && !objData.getImageUrls().get_1200w326ppi().equals("")) {
                Glide.with(mContext).load(objData.getImageUrls().get_1200w326ppi().trim()).into(imgBrand);
            }

            brandNameTV.setText(objData.getBrandName());
            listitemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (objData != null) {
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
        return listitemView;
    }

}
