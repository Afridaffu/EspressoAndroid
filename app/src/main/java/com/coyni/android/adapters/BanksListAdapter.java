package com.coyni.android.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.coyni.android.R;
import com.coyni.android.model.bank.BanksDataItem;
import com.coyni.android.utils.MyApplication;
import com.coyni.android.utils.Utils;
import com.coyni.android.view.BuyTokenActivity;
import com.coyni.android.view.WithdrawTokenActivity;

import java.util.List;

public class BanksListAdapter extends RecyclerView.Adapter<BanksListAdapter.MyViewHolder> {
    List<BanksDataItem> listBanks;
    Context mContext;
    MyApplication objMyApplication;
    int row_position = 0;
    String strScreen = "";

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvBankName, tvCardNo, tvAccName, tvAddress;
        public ImageView imgBank, imgState, imgMore;
        public MaterialCardView cvCard;
        public LinearLayout lyMore;

        public MyViewHolder(View view) {
            super(view);
            tvBankName = (TextView) view.findViewById(R.id.tvBankName);
            tvCardNo = (TextView) view.findViewById(R.id.tvCardNo);
            tvAccName = (TextView) view.findViewById(R.id.tvAccName);
            tvAddress = (TextView) view.findViewById(R.id.tvAddress);
            imgBank = (ImageView) view.findViewById(R.id.imgBank);
            imgState = (ImageView) view.findViewById(R.id.imgState);
            imgMore = (ImageView) view.findViewById(R.id.imgMore);
            cvCard = (MaterialCardView) view.findViewById(R.id.cvCard);
            lyMore = (LinearLayout) view.findViewById(R.id.lyMore);
        }
    }


    public BanksListAdapter(List<BanksDataItem> list, Context context, String screen) {
        this.mContext = context;
        this.listBanks = list;
        this.objMyApplication = (MyApplication) context.getApplicationContext();
        this.strScreen = screen;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.banklistitem, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        try {
            BanksDataItem objData = listBanks.get(position);
            String strAddress = "";
            if (objData.getAccountCategory().toLowerCase().equals("signet")) {
                holder.tvBankName.setText("Signet Wallet ID");
                if (objData.getAccoutNumber() != null && objData.getAccoutNumber().length() > 4) {
                    holder.tvCardNo.setText("**** " + objData.getAccoutNumber().substring(objData.getAccoutNumber().length() - 4));
                } else {
                    holder.tvCardNo.setText(objData.getAccoutNumber());
                }
//                if (objData.getAccoutNumber() != null && objData.getAccoutNumber().length() > 8) {
//                    holder.tvCardNo.setText(objData.getAccoutNumber().substring(0, 8) + "...");
//                } else {
//                    holder.tvCardNo.setText(objData.getAccoutNumber());
//                }
                holder.imgBank.setImageResource(R.drawable.ic_signet_account);
                if (objData.getAddressLine1() != null && !objData.getAddressLine1().equals("")) {
                    strAddress = objData.getAddressLine1() + ", ";
                }
                if (objData.getAddressLine2() != null && !objData.getAddressLine2().equals("")) {
                    strAddress = strAddress + objData.getAddressLine2() + ", ";
                }
                if (objData.getCity() != null && !objData.getCity().equals("")) {
                    strAddress = strAddress + objData.getCity() + ", ";
                }
                if (objData.getState() != null && !objData.getState().equals("")) {
                    strAddress = strAddress + objData.getState() + ", ";
                }
                if (objData.getCountry() != null && !objData.getCountry().equals("")) {
                    strAddress = strAddress + objData.getCountry() + ", ";
                }
                holder.tvAddress.setText(strAddress.trim().substring(0, (strAddress.trim().length() - 1)));
            } else {
                holder.tvBankName.setText(objData.getBankName());
                if (objData.getAccoutNumber() != null && objData.getAccoutNumber().length() > 4) {
                    holder.tvCardNo.setText("**** " + objData.getAccoutNumber().substring(objData.getAccoutNumber().length() - 4));
                } else {
                    holder.tvCardNo.setText("**** " + objData.getAccoutNumber());
                }
                holder.imgBank.setImageResource(R.drawable.ic_bank);
                if (objData.getAddressLine1() != null && !objData.getAddressLine1().equals("")) {
                    holder.tvAddress.setText(objData.getAddressLine1().trim());
                } else {
                    holder.tvAddress.setText("");
                }
            }
            holder.tvAccName.setText(objData.getAccountName());


            if (row_position == position) {
                holder.cvCard.setStrokeColor(mContext.getResources().getColor(R.color.status));
                holder.imgState.setImageResource(R.drawable.ic_card_select);
                objMyApplication.setSelectedBank(objData);
                if (strScreen.equals("buy")) {
                    ((BuyTokenActivity) mContext).selectedBank();
                } else {
                    ((WithdrawTokenActivity) mContext).bankSelected(true);
                }
            } else {
                holder.cvCard.setStrokeColor(mContext.getResources().getColor(R.color.white));
                holder.imgState.setImageResource(R.drawable.ic_card_unselect);
            }
            if ((row_position == -1 && objData.getDefault() != null && objData.getDefault()) || (listBanks.size() == 1 && position == 0)) {
                holder.cvCard.setStrokeColor(mContext.getResources().getColor(R.color.status));
                holder.imgState.setImageResource(R.drawable.ic_card_select);
                objMyApplication.setSelectedBank(objData);
                if (strScreen.equals("buy")) {
                    ((BuyTokenActivity) mContext).selectedBank();
                } else {
                    ((WithdrawTokenActivity) mContext).bankSelected(true);
                }
            }
            holder.cvCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        updateData(position);
                        objMyApplication.setSelectedBank(objData);
                        if (strScreen.equals("buy")) {
                            ((BuyTokenActivity) mContext).selectedBank();
                        } else {
                            ((WithdrawTokenActivity) mContext).bankSelected(true);
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
                        updateData(position);
                        objMyApplication.setSelectedBank(objData);
                        if (strScreen.equals("buy")) {
                            ((BuyTokenActivity) mContext).selectedBank();
                        } else {
                            ((WithdrawTokenActivity) mContext).bankSelected(true);
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
                        if (objData.getAccountCategory().toLowerCase().equals("signet")) {
                            inflater.inflate(R.menu.poupup_menu, menuBuilder);
                        } else {
                            inflater.inflate(R.menu.poupup_bank_menu, menuBuilder);
                        }
                        MenuPopupHelper optionsMenu = new MenuPopupHelper(mContext, menuBuilder, holder.imgMore);
                        optionsMenu.setForceShowIcon(true);
                        menuBuilder.setCallback(new MenuBuilder.Callback() {
                            @Override
                            public boolean onMenuItemSelected(MenuBuilder menu, MenuItem item) {
                                switch (item.getItemId()) {
                                    case R.id.menuEdit: // Handle option1 Click
                                        if (objData.getAccountCategory().toLowerCase().equals("signet")) {
                                            ((WithdrawTokenActivity) mContext).editBank(objData);
                                        } else {
                                            //Toast.makeText(mContext, "Edit not applicable", Toast.LENGTH_LONG).show();
                                            Utils.displayAlert("Edit not applicable", ((WithdrawTokenActivity) mContext));
                                        }
                                        return true;
                                    case R.id.menuRemove: // Handle option2 Click
                                        if (strScreen.equals("buy")) {
                                            ((BuyTokenActivity) mContext).showBankDeletePopup(objData);
                                        } else {
                                            ((WithdrawTokenActivity) mContext).showBankDeletePopup(objData);
                                        }
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
        return listBanks.size();
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
