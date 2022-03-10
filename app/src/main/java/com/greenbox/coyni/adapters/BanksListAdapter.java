package com.greenbox.coyni.adapters;

import android.content.Context;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.greenbox.coyni.R;
import com.greenbox.coyni.model.bank.BankItem;
import com.greenbox.coyni.model.paymentmethods.PaymentsList;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.utils.swipelayout.RecyclerSwipeAdapter;
import com.greenbox.coyni.utils.swipelayout.SwipeLayout;
import com.greenbox.coyni.view.BuyTokenActivity;
import com.greenbox.coyni.view.BuyTokenPaymentMethodsActivity;
import com.greenbox.coyni.view.WithdrawPaymentMethodsActivity;
import com.greenbox.coyni.view.WithdrawTokenActivity;
import com.greenbox.coyni.view.business.AddBankAccount;
import com.greenbox.coyni.view.business.SelectPaymentMethodActivity;

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
                holder.tvAccount.setText("**** " + objData.getAccountNumber().substring(objData.getAccountNumber().length() - 4));
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
                            addBankAccount.deleteBankAPICall(objData.getId());
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

