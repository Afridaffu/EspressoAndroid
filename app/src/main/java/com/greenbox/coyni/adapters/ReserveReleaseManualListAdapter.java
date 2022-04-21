package com.greenbox.coyni.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.greenbox.coyni.R;
import com.greenbox.coyni.model.reservemanual.ManualItem;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;

import java.util.List;

public class ReserveReleaseManualListAdapter extends BaseRecyclerViewAdapter<ReserveReleaseManualListAdapter.MyViewHolder> {

    private Context context;
    private OnItemClickListener listener;
    private List<ManualItem> dataList;
    private String timeDate = "";
    private MyApplication myApplication;

    public ReserveReleaseManualListAdapter(Context context, List<ManualItem> dataList) {
        this.context = context;
        this.dataList = dataList;
        myApplication = (MyApplication) context.getApplicationContext();
    }

    @Override
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View manualList = inflater.inflate(R.layout.manual_list, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(manualList);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ManualItem objData = dataList.get(position);

        if (objData.getReason() != null && !objData.getReason().equals("")) {
            holder.reason.setText(objData.getReason());
        }
        if (String.valueOf(objData.getAmount()) != null && !String.valueOf(objData.getAmount()).equals("")) {
            holder.amount.setText(Utils.convertTwoDecimal(String.valueOf(objData.getAmount())));
        }
        if (objData.getSentTo() != null && !objData.getSentTo().equals("")) {
            holder.tokenAc.setText(objData.getSentTo());
        }
        if (objData.getDate() != null && !objData.getDate().equals("")) {
            timeDate = objData.getDate();
            if (timeDate.contains(".")) {
                timeDate = timeDate.substring(0, timeDate.lastIndexOf("."));
            }
            timeDate = myApplication.convertZoneDateTime(timeDate, "yyyy-MM-dd hh:mm:ss", "dd/MM/yyyy @ hh:mma");
            holder.dateTime.setText(timeDate);
        }
    }

    @Override
    public int getItemCount() {
        if (dataList == null) {
            return 0;
        }
        return dataList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public RelativeLayout rlBase;
        public TextView reason, amount, tokenAc, dateTime;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            reason = itemView.findViewById(R.id.reasonTV);
            amount = itemView.findViewById(R.id.ammont_TV);
            tokenAc = itemView.findViewById(R.id.tokenAcTV);
            dateTime = itemView.findViewById(R.id.dateTime_TV);
            rlBase = itemView.findViewById(R.id.rl_base);

        }
    }
}
