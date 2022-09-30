package com.coyni.mapp.utils.outline_et;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.coyni.mapp.R;
import com.coyni.mapp.utils.MaskEditText.widget.MaskEditText;
import com.coyni.mapp.utils.PasswordCustomTransformationMethod;
import com.coyni.mapp.utils.Utils;
import com.coyni.mapp.view.business.CompanyInformationActivity;

public class SSNOutlineBoxNumberEditText extends ConstraintLayout {
    public static TextView ssnType;
    private LinearLayout hintHolder, ssnDDLL, weightRL;
    private MaskEditText ssnET, einET;
    private Context mContext;
    public String FROM = "";
    private ImageView dropdownIV, viewSSNIV;
    private View anchorView;
    private Boolean isCPwdEye = true;
    private Long mLastClickTime = 0L;
    private PasswordCustomTransformationMethod passwordCustomTransformationMethod;

    public SSNOutlineBoxNumberEditText(Context context) {
        this(context, null, 0);
    }

    public SSNOutlineBoxNumberEditText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SSNOutlineBoxNumberEditText(Context context, AttributeSet attrs, int defStyleattr) {
        super(context, attrs, defStyleattr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attributeSet) {
        LayoutInflater.from(context).inflate(R.layout.activity_ssnoutline_box_number_edit_text, this, true);
        hintHolder = findViewById(R.id.ssnhintdHolderLL);
        ssnType = findViewById(R.id.ssnType);
        ssnET = findViewById(R.id.ssnET);
        einET = findViewById(R.id.einET);
        dropdownIV = findViewById(R.id.dropdownIV);
        ssnDDLL = findViewById(R.id.ssnDDLL);
        anchorView = findViewById(R.id.anchorView);
        viewSSNIV = findViewById(R.id.viewSSNIV);
        weightRL = findViewById(R.id.weightRL);

        passwordCustomTransformationMethod = new PasswordCustomTransformationMethod();
        ssnET.setTransformationMethod(passwordCustomTransformationMethod);
        einET.setTransformationMethod(passwordCustomTransformationMethod);

        ssnET.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                try {
                    CompanyInformationActivity comp = (CompanyInformationActivity) mContext;
                    if (b) {
                        if (FROM.equals("CompanyInfo")) {
                            comp.ssnErrorLL.setVisibility(GONE);
//                            comp.basicInfoSL.scrollTo(comp.basicNextCV.getLeft(), comp.basicNextCV.getBottom());
                        }
                        hintHolder.setBackground(getResources().getDrawable(R.drawable.outline_box_focused));
                    } else {
                        hintHolder.setBackground(getResources().getDrawable(R.drawable.outline_box_unfocused));
                        if ((ssnET.getText().length() > 0 && ssnET.getText().length() < 11)) {
                            hintHolder.setBackground(getResources().getDrawable(R.drawable.outline_box_error));
                            comp.ssnErrorLL.setVisibility(VISIBLE);
                            comp.ssnErrorTV.setText("Please enter a valid " + getSSNTypeText());
                        } else if ((ssnET.getText().length() == 0)) {
                            hintHolder.setBackground(getResources().getDrawable(R.drawable.outline_box_error));
                            comp.ssnErrorLL.setVisibility(VISIBLE);
                            comp.ssnErrorTV.setText("Field Required");
                        } else {
                            hintHolder.setBackground(getResources().getDrawable(R.drawable.outline_box_unfocused));
                            comp.ssnErrorLL.setVisibility(GONE);
                        }


                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        ssnET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                CompanyInformationActivity comAct = (CompanyInformationActivity) mContext;
                if (charSequence.length() == 11) {
                    comAct.ssnErrorLL.setVisibility(GONE);
                    comAct.isSSN = true;
                } else if (charSequence.length() > 0 && charSequence.length() < 11) {
                    comAct.isSSN = false;
                } else if ((ssnET.getText().length() == 0)) {
                    comAct.isSSN = false;
//                    comAct.ssnErrorLL.setVisibility(VISIBLE);
//                    comAct.ssnErrorTV.setText("Field Required");
                }
                comAct.enableOrDisableNext();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        einET.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                try {
                    CompanyInformationActivity comp = (CompanyInformationActivity) mContext;
                    if (b) {
                        if (FROM.equals("CompanyInfo")) {
                            comp.ssnErrorLL.setVisibility(GONE);
//                            comp.basicInfoSL.scrollTo(comp.basicNextCV.getLeft(), comp.basicNextCV.getBottom());
                        }
                        hintHolder.setBackground(getResources().getDrawable(R.drawable.outline_box_focused));
                    } else {
                        hintHolder.setBackground(getResources().getDrawable(R.drawable.outline_box_unfocused));
                        if ((einET.getText().length() > 0 && einET.getText().length() < 10)) {
                            hintHolder.setBackground(getResources().getDrawable(R.drawable.outline_box_error));
                            comp.ssnErrorLL.setVisibility(VISIBLE);
                            comp.ssnErrorTV.setText("Please enter a valid " + getSSNTypeText());
                        } else if ((einET.getText().length() == 0)) {
                            hintHolder.setBackground(getResources().getDrawable(R.drawable.outline_box_error));
                            comp.ssnErrorLL.setVisibility(VISIBLE);
                            comp.ssnErrorTV.setText("Field Required");
                        } else {
                            hintHolder.setBackground(getResources().getDrawable(R.drawable.outline_box_unfocused));
                            comp.ssnErrorLL.setVisibility(GONE);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        einET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                CompanyInformationActivity comAct = (CompanyInformationActivity) mContext;
                if (charSequence.length() == 10) {
                    comAct.ssnErrorLL.setVisibility(GONE);
                    comAct.isSSN = true;
                } else if (charSequence.length() > 0 && charSequence.length() < 10) {
                    comAct.isSSN = false;
                } else if ((ssnET.getText().length() == 0)) {
                    comAct.isSSN = false;
                }
                comAct.enableOrDisableNext();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        ssnDDLL.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                CompanyInformationActivity comAct = (CompanyInformationActivity) mContext;

                LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View customView = layoutInflater.inflate(R.layout.ssn_popup, null);

                //instantiate popup window
                PopupWindow popupWindow = new PopupWindow(customView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

                popupWindow.setOutsideTouchable(true);
                popupWindow.setBackgroundDrawable(new ColorDrawable());
                popupWindow.setTouchInterceptor(new OnTouchListener() {

                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                            popupWindow.dismiss();
                            return true;
                        }
                        return false;
                    }

                });

                //display the popup window
                popupWindow.showAsDropDown(anchorView);

                TextView ssnTV = customView.findViewById(R.id.ssnTV);
                TextView einTV = customView.findViewById(R.id.einTV);
                TextView typeTV = customView.findViewById(R.id.typeTV);
                LinearLayout einLL = customView.findViewById(R.id.einLL);
                LinearLayout ssnLL = customView.findViewById(R.id.ssnLL);
                LinearLayout popupLL = customView.findViewById(R.id.popupLL);
                typeTV.setText(ssnType.getText().toString());

//                LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
//                        LayoutParams.MATCH_PARENT,
//                        LayoutParams.MATCH_PARENT,
//                        (float) 1.9
//                );
//                popupLL.setLayoutParams(param);

                if (ssnType.getText().toString().equals("SSN")) {
                    ssnLL.setVisibility(GONE);
                    einLL.setVisibility(VISIBLE);
                } else {
                    ssnLL.setVisibility(VISIBLE);
                    einLL.setVisibility(GONE);
                }

                ssnTV.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        popupWindow.dismiss();
                        if (!ssnType.getText().toString().equals("SSN")) {
                            ssnType.setText(ssnTV.getText().toString());
                            ssnET.setVisibility(VISIBLE);
//                            ssnET.requestFocus();
                            showKeypad(ssnET);
                            einET.clearFocus();
                            einET.setVisibility(GONE);
                            ssnET.setText("");
                            einET.setText("");
                            comAct.ssnErrorLL.setVisibility(GONE);
                            comAct.isSSN = false;
                            hintHolder.setBackground(getResources().getDrawable(R.drawable.outline_box_unfocused));
                            comAct.enableOrDisableNext();
//                            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
//                                    LayoutParams.MATCH_PARENT,
//                                    LayoutParams.MATCH_PARENT,
//                                    (float) 1.9
//                            );
//                            weightRL.setLayoutParams(param);
                            comAct.setUI_IdentificationType();
//                            if (!Utils.isKeyboardVisible)
//                                Utils.shwForcedKeypad(mContext);
                        }
                    }
                });

                einTV.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        popupWindow.dismiss();
                        if (!ssnType.getText().toString().equals("EIN/TIN")) {
                            ssnType.setText(einTV.getText().toString());
                            ssnET.setVisibility(GONE);
                            einET.setVisibility(VISIBLE);
                            ssnET.clearFocus();
                            einET.requestFocus();
                            showKeypad(einET);
                            einET.setText("");
                            ssnET.setText("");
                            comAct.ssnErrorLL.setVisibility(GONE);
                            comAct.isSSN = false;
                            hintHolder.setBackground(getResources().getDrawable(R.drawable.outline_box_unfocused));
                            comAct.enableOrDisableNext();
//                            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
//                                    LayoutParams.MATCH_PARENT,
//                                    LayoutParams.MATCH_PARENT,
//                                    (float) 1.9
//                            );
//                            weightRL.setLayoutParams(param);
                            comAct.setUI_IdentificationType();
//                            if (!Utils.isKeyboardVisible)
//                                Utils.shwForcedKeypad(mContext);
                        }
                    }
                });
            }
        });

        viewSSNIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
//                    ssnET.clearFocus();
                    if (!isCPwdEye) {
                        isCPwdEye = true;
                        viewSSNIV.setBackgroundResource(R.drawable.ic_eyeclose);
                        ssnET.setTransformationMethod(passwordCustomTransformationMethod);
                        einET.setTransformationMethod(passwordCustomTransformationMethod);
                    } else {
                        isCPwdEye = false;
                        viewSSNIV.setBackgroundResource(R.drawable.ic_eyeopen);
                        ssnET.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        einET.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    }

                    if (ssnET.getText().length() > 0) {
                        ssnET.setSelection(ssnET.getText().toString().length());
                    }
                    if (einET.getText().length() > 0) {
                        einET.setSelection(einET.getText().toString().length());
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });


    }

    public void setSelection() {
        ssnET.setSelection(ssnET.getText().toString().length());
    }

    public void setFrom(String fromm, Context context) {
        FROM = fromm;
        mContext = context;
    }

    public void setText(String text) {
        if (ssnType.getText().toString().equals("SSN")) {
            ssnET.setVisibility(VISIBLE);
            einET.setVisibility(GONE);
            ssnET.setText(text);
        } else {
            ssnET.setVisibility(GONE);
            einET.setVisibility(VISIBLE);
            einET.setText(text);
        }
    }

    public String getText() {
        if (ssnType.getText().toString().equals("SSN")) {
            return ssnET.getText().toString().trim();
        } else {
            return einET.getText().toString().trim();
        }
    }

    public int getETID() {
        return ssnET.getId();
    }

    public void requestETFocus(String type) {
        if (type.equals("SSN")) {
//            ssnET.requestFocus();
            showKeypad(ssnET);
        } else
//            einET.requestFocus();
            showKeypad(einET);
//        if (!Utils.isKeyboardVisible)
//            Utils.shwForcedKeypad(mContext);
    }

    public void setSSNTypeText(String text) {
        if (text.equals("SSN")) {
            ssnET.setVisibility(VISIBLE);
            einET.setVisibility(GONE);
//            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
//                    LayoutParams.MATCH_PARENT,
//                    LayoutParams.MATCH_PARENT,
//                    (float) 2);
//            weightRL.setLayoutParams(param);
        } else {
            ssnET.setVisibility(GONE);
            einET.setVisibility(VISIBLE);
//            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
//                    LayoutParams.MATCH_PARENT,
//                    LayoutParams.MATCH_PARENT,
//                    (float) 1.4);
//            weightRL.setLayoutParams(param);

        }

        ssnType.setText(text);
    }

    public String getSSNTypeText() {
        return ssnType.getText().toString();
    }

    public String getUnmaskedText() {
        if (ssnType.getText().toString().equals("SSN")) {
            return ssnET.getUnMasked();
        } else {
            return einET.getUnMasked();
        }
    }

    public void disableDropDown() {
        ssnDDLL.setClickable(false);
        dropdownIV.setVisibility(GONE);
    }

    public void enableDropDown() {
        ssnDDLL.setClickable(true);
        dropdownIV.setVisibility(VISIBLE);
    }

    public void showKeypad(MaskEditText editText) {
        if (((EditText) editText).requestFocus()) {
            InputMethodManager imm = (InputMethodManager)
                    mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput((EditText) editText, InputMethodManager.SHOW_IMPLICIT);
            Log.e("open", "keyboard");
        }
    }
}