package com.greenbox.coyni.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.greenbox.coyni.R;
import com.greenbox.coyni.model.summary.Item;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.swipelayout.RecyclerSwipeAdapter;
import com.greenbox.coyni.view.business.AddBankAccount;
import com.greenbox.coyni.view.business.ReviewApplicationActivity;

import java.util.List;

public class BankAccountsRecyclerAdapter extends RecyclerSwipeAdapter<BankAccountsRecyclerAdapter.MyViewHolder> {
    List<Item> banks;
    Context mContext;
    MyApplication objMyApplication;
    private OnSelectListner listener;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView bankNameTx, accountNumberTx;
        public LinearLayout deleteLL;


        public MyViewHolder(View view) {
            super(view);
            bankNameTx = (TextView) view.findViewById(R.id.bankName);
            accountNumberTx = (TextView) view.findViewById(R.id.accountNumber);
            deleteLL = view.findViewById(R.id.deleteLL);

        }
    }


    public BankAccountsRecyclerAdapter(Context context, List<Item> list , OnSelectListner listener) {
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
            Item objData = banks.get(position);
            holder.bankNameTx.setText(objData.getBankName());

            holder.accountNumberTx.setText(objData.getAccountNumber());

            holder.deleteLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
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
        } catch (Exception ex) {
            ex.printStackTrace();
        }
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

