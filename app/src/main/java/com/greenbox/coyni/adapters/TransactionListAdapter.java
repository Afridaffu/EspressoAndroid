package com.greenbox.coyni.adapters;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.greenbox.coyni.R;
import com.greenbox.coyni.model.transaction.TransactionListItems;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;

import java.util.List;

public class TransactionListAdapter extends RecyclerView.Adapter<TransactionListAdapter.MyViewHolder> {
    List<TransactionListItems> transactionListItems;
    Context mecontext;
    MyApplication myApplication;

public TransactionListAdapter(List<TransactionListItems> list,Context context){
    this.transactionListItems=list;
    this.mecontext=context;
    this.myApplication = (MyApplication) context.getApplicationContext();
}

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.postedlistitem, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionListAdapter.MyViewHolder holder, int position) {
    TransactionListItems objData=transactionListItems.get(position);




    }

    @Override
    public int getItemCount() {
        return transactionListItems.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
    TextView date,txnDescrip,amount,txnStatus,walletBal;



        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            date=itemView.findViewById(R.id.dateTV);
            txnDescrip=itemView.findViewById(R.id.messageTV);
            amount=itemView.findViewById(R.id.amountTV);
            txnStatus=itemView.findViewById(R.id.statusTV);
            walletBal=itemView.findViewById(R.id.balanceTV);
        }
    }
    private String convertTwoDecimal(String strAmount) {
        String strValue = "", strAmt = "";
        try {
            if (strAmount.contains(" ")) {
                strAmt = Utils.convertBigDecimalUSDC(strAmount.split(" ")[0]);
                strValue = Utils.USNumberFormat(Double.parseDouble(strAmt)) + " " + strAmount.split(" ")[1];
            } else {
                strAmt = Utils.convertBigDecimalUSDC(strAmount);
                strValue = Utils.USNumberFormat(Double.parseDouble(strAmt)) + " " + "CYN";
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return strValue;
    }

    public void addLoadingView() {
        //add loading item
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                transactionListItems.add(null);
                notifyItemInserted(transactionListItems.size() - 1);
            }
        });
    }

    public void removeLoadingView() {
        //Remove loading item
        transactionListItems.remove(transactionListItems.size() - 1);
        notifyItemRemoved(transactionListItems.size());
    }

    public void addData(List<TransactionListItems> listItems) {
        this.transactionListItems.addAll(listItems);
        notifyDataSetChanged();
    }

}
