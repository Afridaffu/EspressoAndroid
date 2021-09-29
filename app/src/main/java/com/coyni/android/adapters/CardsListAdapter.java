package com.coyni.android.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.coyni.android.R;
import com.coyni.android.model.cards.CardsDataItem;
import com.coyni.android.utils.MyApplication;
import com.coyni.android.view.BuyTokenActivity;

import java.util.List;

public class CardsListAdapter extends RecyclerView.Adapter<CardsListAdapter.MyViewHolder> {
    List<CardsDataItem> listCardsData;
    Context mContext;
    MyApplication objMyApplication;
    int row_position = 0;
    String strScreen = "";
    Boolean isFocus = false;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvCardNo, tvExpiry;
        public ImageView imgCardType, imgState, imgMore;
        public MaterialCardView cvCard;
        public RelativeLayout layoutCard;
        public EditText etCvv;
        public LinearLayout lyMore;

        public MyViewHolder(View view) {
            super(view);
            tvCardNo = (TextView) view.findViewById(R.id.tvCardNo);
            tvExpiry = (TextView) view.findViewById(R.id.tvExpiry);
            etCvv = (EditText) view.findViewById(R.id.etCvv);
            imgCardType = (ImageView) view.findViewById(R.id.imgCardType);
            imgState = (ImageView) view.findViewById(R.id.imgState);
            imgMore = (ImageView) view.findViewById(R.id.imgMore);
            cvCard = (MaterialCardView) view.findViewById(R.id.cvCard);
            layoutCard = (RelativeLayout) view.findViewById(R.id.layoutCard);
            lyMore = (LinearLayout) view.findViewById(R.id.lyMore);
        }
    }


    public CardsListAdapter(List<CardsDataItem> list, Context context, String screen) {
        this.mContext = context;
        this.listCardsData = list;
        this.objMyApplication = (MyApplication) context.getApplicationContext();
        this.strScreen = screen;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardlistitem, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        try {
            CardsDataItem objData = listCardsData.get(position);
            holder.tvCardNo.setText("**** " + objData.getLastFour());
            holder.tvExpiry.setText("Exp Date : " + objData.getExpiryDate());
            switch (objData.getCardBrand()) {
                case "MASTERCARD":
                    holder.imgCardType.setImageResource(R.drawable.ic_master);
                    break;
                case "VISA":
                    holder.imgCardType.setImageResource(R.drawable.ic_visa);
                    break;
                case "AMERICAN EXPRESS":
                    holder.imgCardType.setImageResource(R.drawable.ic_amex);
                    break;
                case "DISCOVER":
                    holder.imgCardType.setImageResource(R.drawable.ic_discover);
                    break;
            }

            //Commented as there is no deault option for V1
//            if (row_position == -1 && objData.getDefaultForAllWithDrawals()) {
//                holder.cvCard.setStrokeColor(mContext.getResources().getColor(R.color.status));
//                holder.imgState.setImageResource(R.drawable.ic_card_select);
//                ((BuyTokenActivity) mContext).cardType(objData.getCardType(), position);
//            }
            if (objData.getExpired()) {
                holder.cvCard.setStrokeColor(mContext.getResources().getColor(R.color.deleteback));
                holder.layoutCard.setBackgroundColor(Color.parseColor("#DDDDDD"));
                holder.tvExpiry.setText("Expired");
                holder.imgState.setColorFilter(Color.parseColor("#BDBDBD"));
                holder.tvExpiry.setTextColor(mContext.getResources().getColor(R.color.invalidotp));
            } else {
                holder.cvCard.setStrokeColor(mContext.getResources().getColor(R.color.white));
                holder.layoutCard.setBackgroundColor(mContext.getResources().getColor(R.color.white));
                holder.tvExpiry.setTextColor(mContext.getResources().getColor(R.color.withdrawopt));
                holder.imgState.setColorFilter(null);
            }
            if (row_position == position && !objData.getExpired()) {
                holder.cvCard.setStrokeColor(mContext.getResources().getColor(R.color.status));
                holder.imgState.setImageResource(R.drawable.ic_card_select);
                if (!strScreen.toLowerCase().equals("instant")) {
                    holder.etCvv.setVisibility(View.VISIBLE);
                    holder.etCvv.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.status));
                    holder.etCvv.setText("");
                    if (isFocus) {
                        holder.etCvv.requestFocus();
                    }
                }
                ((BuyTokenActivity) mContext).cardType(objData.getCardType(), position);
            } else {
                holder.cvCard.setStrokeColor(mContext.getResources().getColor(R.color.white));
                holder.imgState.setImageResource(R.drawable.ic_card_unselect);
                holder.etCvv.setVisibility(View.GONE);
            }

            holder.etCvv.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    ((BuyTokenActivity) mContext).cvvValue(s.toString());
                }
            });
            holder.cvCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (!objData.getExpired()) {
                            updateData(position);
                            ((BuyTokenActivity) mContext).cardType(objData.getCardType(), position);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
            holder.imgState.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (!objData.getExpired()) {
                            updateData(position);
                            ((BuyTokenActivity) mContext).cardType(objData.getCardType(), position);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            holder.lyMore.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("RestrictedApi")
                @Override
                public void onClick(View v) {
                    try {
                        MenuBuilder menuBuilder = new MenuBuilder(mContext);
                        MenuInflater inflater = new MenuInflater(mContext);
                        inflater.inflate(R.menu.poupup_menu, menuBuilder);
                        MenuPopupHelper optionsMenu = new MenuPopupHelper(mContext, menuBuilder, holder.imgMore);
                        optionsMenu.setForceShowIcon(true);
                        menuBuilder.setCallback(new MenuBuilder.Callback() {
                            @Override
                            public boolean onMenuItemSelected(MenuBuilder menu, MenuItem item) {
                                switch (item.getItemId()) {
                                    case R.id.menuEdit: // Handle option1 Click
                                        ((BuyTokenActivity) mContext).editCard(objData);
                                        return true;
                                    case R.id.menuRemove: // Handle option2 Click
                                        ((BuyTokenActivity) mContext).showCardDeletePopup(objData);
                                        return true;
                                    default:
                                        return false;
                                }
                            }

                            @Override
                            public void onMenuModeChange(MenuBuilder menu) {
                            }
                        });
                        optionsMenu.show();
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
        return listCardsData.size();
    }

    private void updateData(int position) {
        try {
            isFocus = true;
            row_position = position;
            notifyDataSetChanged();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
