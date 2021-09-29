package com.coyni.android.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.coyni.android.R;
import com.coyni.android.model.GlideApp;
import com.coyni.android.model.giftcard.Brand;
import com.coyni.android.view.GiftCardDetailsActivity;

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
                listitemView = LayoutInflater.from(getContext()).inflate(R.layout.giftcarditem, parent, false);
            }
            Brand objData = getItem(position);

            ImageView imgBrand = listitemView.findViewById(R.id.imgBrand);
            if (objData.getImageUrls().get_1200w326ppi() != null && !objData.getImageUrls().get_1200w326ppi().equals("")) {
                GlideApp.with(mContext)
                        .load(objData.getImageUrls().get_1200w326ppi())
                        .into(imgBrand);
            }
            listitemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (objData != null) {
                            Intent i = new Intent(mContext, GiftCardDetailsActivity.class);
                            i.putExtra("brand", objData);
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
