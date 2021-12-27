package com.greenbox.coyni.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.greenbox.coyni.R;
import com.greenbox.coyni.model.GlideApp;
import com.greenbox.coyni.model.giftcard.Brand;

import java.util.List;
import java.util.logging.Handler;

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

                Glide.with(getContext())
                        .asBitmap()
                        .load("https://profileimageszcart.s3.amazonaws.com/profilePic.jpg")
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(new CustomTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                imgBrand.setImageBitmap(resource);
                                imgBrand.buildDrawingCache();
                            }
                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) { }
                        });
            }

            brandNameTV.setText(objData.getBrandName());
            listitemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (objData != null) {
//                            Intent i = new Intent(mContext, GiftCardDetailsActivity.class);
//                            i.putExtra("brand", objData);
//                            mContext.startActivity(i);
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
