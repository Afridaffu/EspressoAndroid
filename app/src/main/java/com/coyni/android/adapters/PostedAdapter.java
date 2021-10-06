package com.coyni.android.adapters;

import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.coyni.android.fragments.RequestsFragment;
import com.coyni.android.model.sentrequests.SentRequestsItems;
import com.coyni.android.utils.MyApplication;
import com.coyni.android.utils.Utils;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.coyni.android.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class PostedAdapter extends RecyclerView.Adapter<PostedAdapter.MyViewHolder> {
    private List<SentRequestsItems> listItems;
    Context mContext;
    MyApplication objContext;
    int row_position = -1;
    RequestsFragment fragment;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvDate, tvNameHead, tvMessage, tvDescription, tvCharCount, tvStatus, tvRemind, btnRemind, tvCancel;
        public LinearLayout layoutOptions, layoutRemind;
        public CardView cvRemindOpt, cvRemind;
        public TextInputEditText etMessage;
        public MaterialCardView cvCancel;

        public MyViewHolder(View view) {
            super(view);
            tvDate = (TextView) view.findViewById(R.id.tvDate);
            tvNameHead = (TextView) view.findViewById(R.id.tvNameHead);
            tvMessage = (TextView) view.findViewById(R.id.tvMessage);
            tvDescription = (TextView) view.findViewById(R.id.tvDescription);
            tvCharCount = (TextView) view.findViewById(R.id.tvCharCount);
            tvStatus = (TextView) view.findViewById(R.id.tvStatus);
            tvRemind = (TextView) view.findViewById(R.id.tvRemind);
            btnRemind = (TextView) view.findViewById(R.id.btnRemind);
            tvCancel = (TextView) view.findViewById(R.id.tvCancel);
            layoutOptions = (LinearLayout) view.findViewById(R.id.layoutOptions);
            layoutRemind = (LinearLayout) view.findViewById(R.id.layoutRemind);
            cvRemindOpt = (CardView) view.findViewById(R.id.cvRemindOpt);
            cvRemind = (CardView) view.findViewById(R.id.cvRemind);
            etMessage = (TextInputEditText) view.findViewById(R.id.etMessage);
            cvCancel = (MaterialCardView) view.findViewById(R.id.cvCancel);
        }
    }


    public PostedAdapter(List<SentRequestsItems> list, Context context, RequestsFragment fragment) {
        this.mContext = context;
        this.listItems = list;
        this.objContext = (MyApplication) context.getApplicationContext();
        this.fragment = fragment;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.postedlistitem, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        try {
            SentRequestsItems objData = listItems.get(position);
            String strPrev = "", strCurr = "", strCurDate = "", strName = "";
            holder.tvDate.setText(Utils.convertDate(objData.getRequestedDate()));
            if (position != 0) {
                strPrev = Utils.convertDate(listItems.get(position - 1).getRequestedDate());
                strCurr = Utils.convertDate(objData.getRequestedDate());
                if (strPrev.equals(strCurr)) {
                    holder.tvDate.setVisibility(View.GONE);
                } else {
                    holder.tvDate.setVisibility(View.VISIBLE);
                }
            } else if (position == 0) {
                holder.tvDate.setVisibility(View.VISIBLE);
                SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                strCurDate = spf.format(Calendar.getInstance().getTime());
                if (Utils.convertDate(objData.getRequestedDate()).equals(Utils.convertDate(strCurDate))) {
                    holder.tvDate.setText("Today's");
                }
            }
            if (objData.getToUser().contains(" ")) {
                strName = objData.getToUser().trim().replaceAll("\\s{2,}"," ");
//                holder.tvNameHead.setText(objData.getToUser().split(" ")[0].substring(0, 1).toUpperCase() + objData.getToUser().split(" ")[1].substring(0, 1).toUpperCase());
                holder.tvNameHead.setText(strName.split(" ")[0].substring(0, 1).toUpperCase() + strName.split(" ")[1].substring(0, 1).toUpperCase());
            } else {
                holder.tvNameHead.setText(objData.getToUser().substring(0, 1).toUpperCase());
            }
            holder.tvMessage.setText(objData.getContent());
            holder.tvDescription.setText(objData.getRemarks());
            holder.tvStatus.setText(objData.getStatus());
            if (row_position == position) {
                holder.layoutOptions.setVisibility(View.GONE);
                holder.layoutRemind.setVisibility(View.VISIBLE);
            } else {
                holder.layoutOptions.setVisibility(View.VISIBLE);
                holder.layoutRemind.setVisibility(View.GONE);
            }
            holder.cvRemindOpt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.cvRemindOpt.setEnabled(false);
                    holder.cvRemindOpt.setCardBackgroundColor(Color.parseColor("#9B9B9B"));
                    holder.tvRemind.setTextColor(mContext.getResources().getColor(R.color.white));
                    ((RequestsFragment) fragment).initiateRemind(objData.getId(), Utils.remind, "");
                }
            });

            holder.cvRemind.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        holder.cvRemind.setEnabled(false);
                        holder.cvRemind.setCardBackgroundColor(Color.parseColor("#9B9B9B"));
                        holder.btnRemind.setTextColor(mContext.getResources().getColor(R.color.white));
                        ((RequestsFragment) fragment).initiateRemind(objData.getId(), Utils.remind, holder.etMessage.getText().toString().trim());
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            holder.cvCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.cvCancel.setEnabled(false);
                    holder.cvCancel.setCardBackgroundColor(Color.parseColor("#9B9B9B"));
                    holder.tvCancel.setTextColor(mContext.getResources().getColor(R.color.white));
                    ((RequestsFragment) fragment).initiateRemind(objData.getId(), Utils.cancel, "");
                }
            });

            holder.etMessage.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() > 0) {
                        holder.tvCharCount.setText(s.length() + "/160");
                    } else {
                        holder.tvCharCount.setText("0/160");
                    }
                }
            });

            switch (Utils.capitalize(objData.getStatus().replace(" ", ""))) {
                case Utils.requested:
                    if (row_position == position) {
                        holder.layoutOptions.setVisibility(View.GONE);
                        holder.layoutRemind.setVisibility(View.VISIBLE);
                    } else {
                        holder.layoutOptions.setVisibility(View.VISIBLE);
                    }
                    holder.tvStatus.setVisibility(View.GONE);
                    holder.cvRemindOpt.setEnabled(true);
                    break;
                case Utils.cancel:
                    holder.tvStatus.setTextColor(Color.parseColor("#858585"));
                    holder.layoutOptions.setVisibility(View.GONE);
                    holder.tvStatus.setVisibility(View.VISIBLE);
                    break;
                case Utils.complete:
                    holder.tvStatus.setTextColor(Color.parseColor("#00CC6E"));
                    holder.layoutOptions.setVisibility(View.GONE);
                    holder.tvStatus.setVisibility(View.VISIBLE);
                    break;
                case Utils.decline:
                    holder.tvStatus.setTextColor(Color.parseColor("#FF6464"));
                    holder.layoutOptions.setVisibility(View.GONE);
                    holder.tvStatus.setVisibility(View.VISIBLE);
                    break;
                case Utils.remind:
                    holder.tvStatus.setTextColor(Color.parseColor("#009D98"));
                    holder.tvStatus.setVisibility(View.GONE);
                    holder.cvRemindOpt.setEnabled(false);
                    holder.cvRemindOpt.setCardBackgroundColor(Color.parseColor("#9B9B9B"));
                    holder.tvRemind.setText("Reminded");
                    holder.tvRemind.setTextColor(mContext.getResources().getColor(R.color.white));
                    break;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public void updateData(int position) {
        try {
            row_position = position;
            notifyDataSetChanged();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}





