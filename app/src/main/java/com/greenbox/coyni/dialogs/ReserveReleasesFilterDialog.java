package com.greenbox.coyni.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.google.android.material.chip.Chip;
import com.greenbox.coyni.R;
import com.greenbox.coyni.utils.LogUtils;

import java.util.ArrayList;

public class ReserveReleasesFilterDialog extends BaseDialog {

    Context context;
    private ArrayList<Integer> transactionType = new ArrayList<Integer>();
    private boolean isFilters = false;


    public ReserveReleasesFilterDialog(@NonNull Context context) {
        super(context);
    }

    public ReserveReleasesFilterDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_reserve_filter);
        initFields();
    }

    private void initFields() {

        Chip OpenC = findViewById(R.id.OpenC);
        Chip releasedC = findViewById(R.id.releasedC);
        Chip onHoldC = findViewById(R.id.onHoldC);
        Chip canceledC = findViewById(R.id.canceledC);
        CardView applyFilter = findViewById(R.id.applyFilterBtnCV);
        TextView resetFilter = findViewById(R.id.resetFiltersTV);

//        if (isFilters) {
//            if (transactionType.size() > 0) {
//                for (int i = 0; i < transactionType.size(); i++) {
//                    switch (transactionType.get(i)) {
//                        case 1:
//                            OpenC.setChecked(true);
//                            break;
//
//                        case 2:
//                            releasedC.setChecked(true);
//                            break;
//
//                        case 3:
//                            onHoldC.setChecked(true);
//                            break;
//
//                        case 4:
//                            canceledC.setChecked(true);
//                            break;
//
//                    }
//                }
//            }
//
//        }

        OpenC.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    transactionType.add(1);
                } else {
                    transactionType.remove(Integer.valueOf(1));
                }
            }
        });
//            OpenC.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    LogUtils.d("Dialogg", "dialog" + OpenC.isCheckable());
//                    transactionType.add(1);
//                }
//            });
        releasedC.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    transactionType.add(2);
                } else {
                    transactionType.remove(Integer.valueOf(2));
                }
            }
        });
        onHoldC.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    transactionType.add(3);
                } else {
                    transactionType.remove(Integer.valueOf(3));
                }
            }
        });
        canceledC.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    transactionType.add(4);
                } else {
                    transactionType.remove(Integer.valueOf(4));
                }
            }
        });

        applyFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isFilters = false;

                if (transactionType.size() > 0) {
                    isFilters = true;

                    // transactionListRequest.setTransactionType(transactionType);
                }

                if (getOnDialogClickListener() != null) {
                    getOnDialogClickListener().onDialogClicked("ApplyFilter", "");
                }
                dismiss();
            }
        });

        resetFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtils.d("Dialogg", "resetFilter" + transactionType.size());
                //transactionType.clear();
                isFilters = false;
                OpenC.setChecked(false);
                onHoldC.setChecked(false);
                canceledC.setChecked(false);
                releasedC.setChecked(false);
            }
        });

    }


}
