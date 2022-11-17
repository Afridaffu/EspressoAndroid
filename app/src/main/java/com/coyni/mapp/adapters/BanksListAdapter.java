package com.coyni.mapp.adapters;

import android.content.Context;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.coyni.mapp.R;
import com.coyni.mapp.model.bank.BankItem;
import com.coyni.mapp.utils.MyApplication;
import com.coyni.mapp.utils.swipelayout.RecyclerSwipeAdapter;
import com.coyni.mapp.utils.swipelayout.SwipeLayout;
import com.coyni.mapp.view.business.AddBankAccount;

import java.util.List;

public class BanksListAdapter extends RecyclerSwipeAdapter<BanksListAdapter.MyViewHolder> {
    List<BankItem> listBanks;
    Context mContext;
    MyApplication objMyApplication;
    Long mLastClickTime = 0L;
    SwipeLayout swipeLayout;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvBankName, tvAccount;
        public LinearLayout deleteLL;
        SwipeLayout swipeLayout;

        public MyViewHolder(View view) {
            super(view);
            tvBankName = view.findViewById(R.id.tvBankName);
            tvAccount = view.findViewById(R.id.tvAccount);
            deleteLL = view.findViewById(R.id.deleteLL);
            swipeLayout = itemView.findViewById(R.id.swipeLayout);
        }
    }


    public BanksListAdapter(List<BankItem> list, Context context) {
        this.mContext = context;
        this.listBanks = list;
        this.objMyApplication = (MyApplication) context.getApplicationContext();
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
            BankItem objData = listBanks.get(position);
            holder.tvBankName.setText(objData.getBankName());
            if (objData.getAccountNumber() != null && objData.getAccountNumber().length() > 4) {
                holder.tvAccount.setText(mContext.getString(R.string.dot) + objData.getAccountNumber().substring(objData.getAccountNumber().length() - 4));
            } else {
                holder.tvAccount.setText(objData.getAccountNumber());
            }

            holder.deleteLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();

                        mItemManger.closeAllItems();

                        AddBankAccount addBankAccount = (AddBankAccount) mContext;
                        if (listBanks.size() > 1) {
//                            addBankAccount.deleteBankAPICall(objData.getId());
                            addBankAccount.deleteBankAPICall(objData.getBankId());
                        } else if (listBanks.size() == 1) {
                            addBankAccount.showPopup();
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            holder.swipeLayout.addSwipeListener(new SwipeLayout.SwipeListener() {
                @Override
                public void onStartOpen(SwipeLayout layout) {
                    mItemManger.closeAllExcept(layout);
                }

                @Override
                public void onOpen(SwipeLayout layout) {

                }

                @Override
                public void onStartClose(SwipeLayout layout) {
                }

                @Override
                public void onClose(SwipeLayout layout) {
                }

                @Override
                public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {
                }

                @Override
                public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {
                }
            });

            mItemManger.bind(holder.itemView, position);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return listBanks.size();
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipeLayout;
    }
}

