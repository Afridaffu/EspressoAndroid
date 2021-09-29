package com.coyni.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.coyni.android.R;
import com.coyni.android.model.cards.CardsDataItem;
import com.coyni.android.utils.MyApplication;

import java.util.List;
import java.util.Objects;

public class ViewPagerCardsAdapter extends PagerAdapter {
    List<CardsDataItem> listCardsData;
    Context mContext;
    MyApplication objMyApplication;
    int row_position = 0;
    String strScreen = "";
    LayoutInflater mLayoutInflater;

    public TextView tvCardNo, tvExpiry, tvName, tvCardType;
    public ImageView imgCardType, imgState;
    public RelativeLayout rlCard;

    public ViewPagerCardsAdapter(List<CardsDataItem> list, Context context, String screen) {
        this.mContext = context;
        this.listCardsData = list;
        this.objMyApplication = (MyApplication) context.getApplicationContext();
        this.strScreen = screen;

        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // return the number of images
        return listCardsData.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == ((RelativeLayout) object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        // inflating the item.xml
        View view = mLayoutInflater.inflate(R.layout.cardlistitem_profile, container, false);

        try {

            tvCardNo = (TextView) view.findViewById(R.id.tvCardNo);
            tvExpiry = (TextView) view.findViewById(R.id.tvExpiry);
            tvName = (TextView) view.findViewById(R.id.tvName);
            tvCardType = (TextView) view.findViewById(R.id.tvCardType);
            imgCardType = (ImageView) view.findViewById(R.id.imgCardType);
            imgState = (ImageView) view.findViewById(R.id.imgState);
            rlCard = (RelativeLayout) view.findViewById(R.id.rlCard);
            // referencing the image view from the item.xml file

            // Adding the View
            Objects.requireNonNull(container).addView(view);

            CardsDataItem objData = listCardsData.get(position);

            tvCardNo.setText("" + objData.getLastFour());
            tvExpiry.setText("" + objData.getExpiryDate());
            if (objData.getName().contains(" ")) {
                String[] names = objData.getName().split(" ");
                if (names.length > 0) {
                    tvName.setText("" + names[0] + " " + names[names.length - 1]);
                } else {
                    tvName.setText("" + objData.getName());
                }
            } else {
                tvName.setText("" + objData.getName());
            }
            tvCardType.setText("" + objData.getCardType());
            switch (objData.getCardBrand()) {
                case "MASTERCARD":
                    imgCardType.setImageResource(R.drawable.ic_mastercard);
                    rlCard.setBackgroundResource(R.drawable.ic_master_card);
                    break;
                case "VISA":
                    imgCardType.setImageResource(R.drawable.ic_visa_card_logo);
                    rlCard.setBackgroundResource(R.drawable.ic_visa_card);
                    break;
                case "AMERICAN EXPRESS":
                    imgCardType.setImageResource(R.drawable.ic_amex_logo);
                    rlCard.setBackgroundResource(R.drawable.ic_american_express_card);
                    break;
                case "DISCOVER":
                    imgCardType.setImageResource(R.drawable.ic_discover_logo);
                    rlCard.setBackgroundResource(R.drawable.ic_discover_card);
                    break;
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

        container.removeView((RelativeLayout) object);
    }
}
