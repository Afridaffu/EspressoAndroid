package com.coyni.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.coyni.android.R;
import com.coyni.android.model.cards.CardsDataItem;
import com.coyni.android.utils.MyApplication;
import com.coyni.android.view.BuyTokenActivityProfile;

import java.util.List;

public class CardsListAdapterProfile extends RecyclerView.Adapter<CardsListAdapterProfile.MyViewHolder> {
    List<CardsDataItem> listCardsData;
    Context mContext;
    MyApplication objMyApplication;
    int row_position = 0;
    String strScreen = "";

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvCardNo, tvExpiry,tvName,tvCardType;
        public ImageView imgCardType, imgState;
        public RelativeLayout rlCard;

        public MyViewHolder(View view) {
            super(view);
            tvCardNo = (TextView) view.findViewById(R.id.tvCardNo);
            tvExpiry = (TextView) view.findViewById(R.id.tvExpiry);
            tvName = (TextView) view.findViewById(R.id.tvName);
            tvCardType = (TextView) view.findViewById(R.id.tvCardType);
            imgCardType = (ImageView) view.findViewById(R.id.imgCardType);
            imgState = (ImageView) view.findViewById(R.id.imgState);
            rlCard = (RelativeLayout) view.findViewById(R.id.rlCard);
        }
    }


    public CardsListAdapterProfile(List<CardsDataItem> list, Context context, String screen) {
        this.mContext = context;
        this.listCardsData = list;
        this.objMyApplication = (MyApplication) context.getApplicationContext();
        this.strScreen = screen;
    }

    @Override
    public CardsListAdapterProfile.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardlistitem_profile, parent, false);
        return new CardsListAdapterProfile.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CardsListAdapterProfile.MyViewHolder holder, int position) {
        try {
//            CardsDataItem objData = listCardsData.get(position);

            //    int positionInList = position % listCardsData.size();
            CardsDataItem objData = listCardsData.get(position);

            holder.tvCardNo.setText(""+ objData.getLastFour());
            holder.tvExpiry.setText("" + objData.getExpiryDate());
            holder.tvName.setText("" + objData.getName());
            holder.tvCardType.setText("" + objData.getCardType());
            switch (objData.getCardBrand()) {
                case "MASTERCARD":
                    holder.imgCardType.setImageResource(R.drawable.ic_mastercard);
                    holder.rlCard.setBackgroundResource(R.drawable.ic_master_card);
                    break;
                case "VISA":
                    holder.imgCardType.setImageResource(R.drawable.ic_visa_card_logo);
                    holder.rlCard.setBackgroundResource(R.drawable.ic_visa_card);
                    break;
                case "AMERICAN EXPRESS":
                    holder.imgCardType.setImageResource(R.drawable.ic_amex_logo);
                    holder.rlCard.setBackgroundResource(R.drawable.ic_american_express_card);
                    break;
                case "DISCOVER":
                    holder.imgCardType.setImageResource(R.drawable.ic_discover_logo);
                    holder.rlCard.setBackgroundResource(R.drawable.ic_discover_card);
                    break;
            }

            holder.rlCard.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    try {
                        if (!objData.getExpired()) {

                            updateData(position);
                            ((BuyTokenActivityProfile) mContext).cardType(objData.getCardType(), position);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    return false;
                }
            });

        } catch (Exception ex) {
            ex.printStackTrace();
        }


    }

    @Override
    public int getItemCount() {
        return listCardsData.size();
    }

    private void updateData(int position) {
        try {
            row_position = position;
            notifyDataSetChanged();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}

