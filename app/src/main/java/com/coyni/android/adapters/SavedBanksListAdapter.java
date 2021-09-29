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

import com.coyni.android.R;
import com.coyni.android.fragments.SavedBanksFragment;
import com.coyni.android.model.bank.BanksDataItem;
import com.coyni.android.utils.MyApplication;
import com.coyni.android.utils.Utils;

import java.util.List;

public class SavedBanksListAdapter extends RecyclerView.Adapter<SavedBanksListAdapter.MyViewHolder> {
    List<BanksDataItem> listBanks;
    Context mContext;
    MyApplication objMyApplication;
    int row_position = 0;
    String strScreen = "";
    SavedBanksFragment fragment;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvBankHead, tvBankId, tvName, tvAddress;
        public LinearLayout lyMore;
        public ImageView imgMore, imgBank;

        public MyViewHolder(View view) {
            super(view);
            tvBankHead = (TextView) view.findViewById(R.id.tvBankHead);
            tvBankId = (TextView) view.findViewById(R.id.tvBankId);
            tvName = (TextView) view.findViewById(R.id.tvName);
            tvAddress = (TextView) view.findViewById(R.id.tvAddress);
            lyMore = (LinearLayout) view.findViewById(R.id.lyMore);
            imgMore = (ImageView) view.findViewById(R.id.imgMore);
            imgBank = (ImageView) view.findViewById(R.id.imgBank);
        }
    }


    public SavedBanksListAdapter(List<BanksDataItem> list, Context context, String screen, SavedBanksFragment frag) {
        this.mContext = context;
        this.listBanks = list;
        this.objMyApplication = (MyApplication) context.getApplicationContext();
        this.strScreen = screen;
        this.fragment = frag;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.savebanklistitem, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        try {
            BanksDataItem objData = listBanks.get(position);
            String strAddress = "";
            if (strScreen.equals("signet")) {
                holder.tvBankHead.setText("Signet Wallet ID");
                holder.tvName.setText(objData.getAccountName());
                holder.imgBank.setImageResource(R.drawable.ic_signet_account);
                if (objData.getAccoutNumber() != null && objData.getAccoutNumber().length() > 4) {
                    holder.tvBankId.setText("**** " + objData.getAccoutNumber().substring(objData.getAccoutNumber().length() - 4));
                } else {
                    holder.tvBankId.setText(objData.getAccoutNumber());
                }
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
                holder.tvBankHead.setText("Account Number");
                holder.tvName.setText(objData.getBankName());
                holder.imgBank.setImageResource(R.drawable.ic_bank);
                if (objData.getAccoutNumber() != null && objData.getAccoutNumber().length() > 4) {
                    holder.tvBankId.setText("**** " + objData.getAccoutNumber().substring(objData.getAccoutNumber().length() - 4));
                } else {
                    holder.tvBankId.setText(objData.getAccoutNumber());
                }

                if (objData.getAddressLine1() != null && !objData.getAddressLine1().equals("")) {
                    holder.tvAddress.setText(objData.getAddressLine1().trim());
                } else {
                    holder.tvAddress.setText("");
                }
            }

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
                                            fragment.editSignet(objData);
                                        } else {
                                            //Toast.makeText(mContext, "Edit not applicable", Toast.LENGTH_LONG).show();
                                            Utils.displayAlert("Edit not applicable", fragment.getActivity());
                                        }
                                        return true;
                                    case R.id.menuRemove: // Handle option2 Click
                                        fragment.showBankDeletePopup(objData);
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

