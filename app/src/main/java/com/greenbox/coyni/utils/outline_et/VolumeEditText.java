package com.greenbox.coyni.utils.outline_et;

import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.greenbox.coyni.R;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.view.TransactionListActivity;
import com.greenbox.coyni.view.business.DBAInfoAcivity;

public class VolumeEditText extends ConstraintLayout {

    private TextView hintName, volumeErrorTV;
    private LinearLayout hintHolder, volumeErrorLL;
    private EditText volumeET;
    public String FROM = "", hintString = "", mType = "";
    private Context mContext;


    public VolumeEditText(Context context) {
        this(context, null, 0);
    }

    public VolumeEditText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VolumeEditText(Context context, AttributeSet attrs, int defStyleattr) {
        super(context, attrs, defStyleattr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attributeSet) {
        LayoutInflater.from(context).inflate(R.layout.monthly_processing_volume_layout, this, true);
        hintHolder = findViewById(R.id.volhintdHolderLL);
        volumeET = findViewById(R.id.volET);
        hintName = findViewById(R.id.volumehintTV);
        volumeErrorLL = findViewById(R.id.volumeErrorLL);
        volumeErrorTV = findViewById(R.id.volumeErrorTV);

        volumeET.setOnFocusChangeListener((view, b) -> {
            try {
                if (b) {
                    volumeET.setFilters(new InputFilter[]{new InputFilter.LengthFilter(Integer.parseInt(mContext.getString(R.string.maxlength)))});
                    volumeET.setHint(getResources().getString(R.string.enter_the_amount));
                    hintName.setVisibility(VISIBLE);
                    hintName.setTextColor(getResources().getColor(R.color.primary_color));
                    hintHolder.setBackground(getResources().getDrawable(R.drawable.outline_box_focused));
                    volumeErrorLL.setVisibility(GONE);
                } else {
                    volumeET.setFilters(new InputFilter[]{new InputFilter.LengthFilter(Integer.parseInt(mContext.getString(R.string.maxlendecimal)))});
                    USFormat(volumeET, "");

                    volumeET.setHint(hintString);

                    if (volumeET.getText().toString().length() > 0)
                        hintName.setVisibility(VISIBLE);
                    else
                        hintName.setVisibility(GONE);

//                    if (volumeET.getText().toString().length() > 0 && volumeET.getText().toString().equals("0.00")) {
//                        hintName.setTextColor(getResources().getColor(R.color.error_red));
//                        hintHolder.setBackground(getResources().getDrawable(R.drawable.outline_box_error));
//                        volumeErrorLL.setVisibility(VISIBLE);
//                        volumeErrorTV.setText(hintString + " must be greater than 0.");
//                    } else
                    if ((volumeET.getText().length() == 0)) {
                        hintName.setTextColor(getResources().getColor(R.color.error_red));
                        hintHolder.setBackground(getResources().getDrawable(R.drawable.outline_box_error));
                        volumeErrorLL.setVisibility(VISIBLE);
                        volumeErrorTV.setText("Field Required");
                    } else {
                        hintName.setTextColor(getResources().getColor(R.color.primary_black));
                        hintHolder.setBackground(getResources().getDrawable(R.drawable.outline_box_unfocused));
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        volumeET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (charSequence.toString().length() > 0)
                    hintName.setVisibility(VISIBLE);

                if (FROM.equals("DBA_INFO")) {
                    DBAInfoAcivity dia = (DBAInfoAcivity) mContext;
//                    if (!charSequence.toString().equals("") && !volumeET.getText().toString().equals("0.00")) {
                    if (!charSequence.toString().equals("")) {
                        if (mType.equals("MPV")) {
                            dia.isMPV = true;
                        } else if (mType.equals("HT")) {
                            dia.isHighTkt = true;
                        } else if (mType.equals("AT")) {
                            dia.isAvgTkt = true;
                        }
                        volumeErrorLL.setVisibility(GONE);
                    } else {
                        if (mType.equals("MPV")) {
                            dia.isMPV = false;
                        } else if (mType.equals("HT")) {
                            dia.isHighTkt = false;
                        } else if (mType.equals("AT")) {
                            dia.isAvgTkt = false;
                        }
                    }
                    dia.enableOrDisableNext();
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().equals(".")) {
                    volumeET.setText("");
                }
            }

        });

//        volumeET.setOnTouchListener((view, motionEvent) -> {
//            if (FROM.equals("DBA_INFO") && mType.equals("MPV")) {
//                DBAInfoAcivity dia = (DBAInfoAcivity) mContext;
////                dia.pageOneView.setVisibility(VISIBLE);
//                dia.dbaBasicSL.scrollTo(dia.highTicketOET.getLeft(), dia.highTicketOET.getBottom());
//                volumeET.requestFocus();
//                volumeET.setSelection(volumeET.getText().toString().length());
//            } else if (FROM.equals("DBA_INFO") && mType.equals("HT")) {
//                DBAInfoAcivity dia = (DBAInfoAcivity) mContext;
////                dia.pageOneView.setVisibility(VISIBLE);
//                dia.dbaBasicSL.scrollTo(dia.avgTicketOET.getLeft(), dia.avgTicketOET.getBottom());
//                volumeET.requestFocus();
//                volumeET.setSelection(volumeET.getText().toString().length());
//             } else if (FROM.equals("DBA_INFO") && mType.equals("AT")) {
//                DBAInfoAcivity dia = (DBAInfoAcivity) mContext;
////                dia.pageOneView.setVisibility(VISIBLE);
//                dia.dbaBasicSL.scrollTo(dia.timezoneTIL.getLeft(), dia.timezoneTIL.getBottom());
//                volumeET.requestFocus();
//                volumeET.setSelection(volumeET.getText().toString().length());
//            }
//            return false;
//        });
    }

    public void setFrom(String fromm, Context context, String type) {
        FROM = fromm;
        mContext = context;
        mType = type;

        if (mType.equals("MPV")) {
            volumeET.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        } else if (mType.equals("HT")) {
            volumeET.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        } else if (mType.equals("AT")) {
            volumeET.setImeOptions(EditorInfo.IME_ACTION_DONE);
        }
    }


    public void setText(String text) {
        volumeET.setFilters(new InputFilter[]{new InputFilter.LengthFilter(Integer.parseInt(mContext.getString(R.string.maxlendecimal)))});
        volumeET.setText(text);
        volumeET.setFilters(new InputFilter[]{new InputFilter.LengthFilter(Integer.parseInt(mContext.getString(R.string.maxlength)))});
    }

    public void setHint(String text) {
        hintString = text;
        hintName.setText(text);
        volumeET.setHint(text);
    }

    public String getText() {
        return volumeET.getText().toString().trim();
    }

    public int getETID() {
        return volumeET.getId();
    }

    public void requestETFocus() {
        volumeET.requestFocus();
    }

    public void setSelection() {
        volumeET.setSelection(volumeET.getText().toString().length());
    }

    private void USFormat(EditText etAmount, String mode) {
        try {
            String strAmount = "";
            strAmount = Utils.convertBigDecimalUSDC(etAmount.getText().toString().trim().replace(",", ""));
            etAmount.setText(Utils.USNumberFormat(Utils.doubleParsing(strAmount)));
            etAmount.setSelection(etAmount.getText().length());

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}