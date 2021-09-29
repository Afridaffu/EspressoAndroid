package com.coyni.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.coyni.android.R;
import com.coyni.android.model.export.ExportColumnsData;
import com.coyni.android.utils.MyApplication;

import java.util.List;

public class ColumnSelectionAdapter extends RecyclerView.Adapter<ColumnSelectionAdapter.MyViewHolder> {
    private List<ExportColumnsData> listColumns;
    Context mContext;
    MyApplication objContext;
    String strHead = "";

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvStatus;
        public RelativeLayout layoutItem;
        public ImageView imgRadio, imgCheck;
        public CardView cvStatus;

        public MyViewHolder(View view) {
            super(view);
            tvStatus = (TextView) view.findViewById(R.id.tvStatus);
            imgRadio = (ImageView) view.findViewById(R.id.imgRadio);
            imgCheck = (ImageView) view.findViewById(R.id.imgCheck);
            layoutItem = (RelativeLayout) view.findViewById(R.id.layoutItem);
            cvStatus = (CardView) view.findViewById(R.id.cvStatus);
        }
    }


    public ColumnSelectionAdapter(List<ExportColumnsData> list, Context context) {
        this.mContext = context;
        this.listColumns = list;
        this.objContext = (MyApplication) context.getApplicationContext();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.statuslistitem, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        try {
            ExportColumnsData objData = listColumns.get(position);
//            if (objData.getExportUIName().equals("ReferenceId")) {
//                holder.tvStatus.setText("Reference ID");
//            } else if (objData.getExportUIName().equals("TransactionType")) {
//                holder.tvStatus.setText("Transaction Type");
//            } else if (objData.getExportUIName().equals("Date/time")) {
//                holder.tvStatus.setText("Create Date");
//            } else {
//                holder.tvStatus.setText(objData.getExportUIName());
//            }
            holder.tvStatus.setText(objData.getExportUIName());
            holder.imgRadio.setVisibility(View.GONE);
            holder.cvStatus.setVisibility(View.GONE);
            if (!strHead.equals("") && strHead.toLowerCase().equals("default")) {
                holder.imgCheck.setVisibility(View.GONE);
            } else {
                holder.imgCheck.setVisibility(View.VISIBLE);
            }
            if (objContext.getSelectedListColumns().contains(objData.getExportColumnName())) {
//            if (objContext.getSelectedListColumns().contains(objData.getExportUIName())) {
                holder.imgCheck.setBackgroundResource(R.drawable.ic_checkbox_select);
            } else {
                holder.imgCheck.setBackgroundResource(R.drawable.ic_checkbox_unselect);
            }

            holder.layoutItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (!strHead.equals("") && strHead.toLowerCase().equals("custom")) {
//                            if (objContext.getSelectedListColumns().contains(objData.getExportUIName())) {
//                                objContext.getSelectedListColumns().remove(objData.getExportUIName());
//                            } else {
//                                objContext.getSelectedListColumns().add(objData.getExportUIName());
//                            }
                            if (objContext.getSelectedListColumns().contains(objData.getExportColumnName())) {
                                objContext.getSelectedListColumns().remove(objData.getExportColumnName());
                            } else {
                                objContext.getSelectedListColumns().add(objData.getExportColumnName());
                            }
                            notifyDataSetChanged();
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
        return listColumns.size();
    }

    public void updateData(String head) {
        try {
            strHead = head;
            notifyDataSetChanged();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}





