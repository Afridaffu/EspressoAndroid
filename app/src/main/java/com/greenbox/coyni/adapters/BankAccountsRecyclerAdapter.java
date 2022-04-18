package com.greenbox.coyni.adapters;

import android.content.Context;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.greenbox.coyni.R;
import com.greenbox.coyni.model.summary.BankAccount;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.swipelayout.RecyclerSwipeAdapter;
import com.greenbox.coyni.utils.swipelayout.SwipeLayout;

import java.util.List;

public class BankAccountsRecyclerAdapter extends RecyclerSwipeAdapter<BankAccountsRecyclerAdapter.MyViewHolder> {
    List<BankAccount> banks;
    Context mContext;
    MyApplication objMyApplication;
    private OnSelectListner listener;
    Long mLastClickTime = 0L;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView bankNameTx, accountNumberTx;
        public LinearLayout deleteLL;
        SwipeLayout swipeLayout;


        public MyViewHolder(View view) {
            super(view);
            bankNameTx = (TextView) view.findViewById(R.id.bankName);
            accountNumberTx = (TextView) view.findViewById(R.id.accountNumber);
            deleteLL = view.findViewById(R.id.deleteLL);
            swipeLayout = itemView.findViewById(R.id.swipeLayout);

        }
    }


    public BankAccountsRecyclerAdapter(Context context, List<BankAccount> list , OnSelectListner listener) {
        this.mContext = context;
        this.banks = list;
        this.objMyApplication = (MyApplication) context.getApplicationContext();
        this.listener = listener;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bankaccounts_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        try {
            BankAccount objData = banks.get(position);
            holder.bankNameTx.setText(objData.getBankName());

            holder.accountNumberTx.setText(objData.getAccountNumber());

            holder.deleteLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();

                    mItemManger.closeAllItems();
                    try {
                        if (banks.size() > 1) {
                            listener.selectedBankItem(objData.getId());
                        } else if (banks.size() == 1) {
                            listener.selectedBankItem(0);
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

        mItemManger.bind(holder.itemView, position);
    }

    @Override
    public int getItemCount() {
        return banks.size();
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipeLayout;
    }

    public interface OnSelectListner{
        void selectedBankItem(int id );
    }

}

