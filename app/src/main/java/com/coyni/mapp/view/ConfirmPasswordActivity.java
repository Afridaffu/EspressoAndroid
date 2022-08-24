package com.coyni.mapp.view;

import static android.view.View.GONE;

import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.TypedValue;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.coyni.mapp.R;
import com.coyni.mapp.interfaces.OnKeyboardVisibilityListener;
import com.coyni.mapp.model.login.LoginResponse;
import com.coyni.mapp.model.login.PasswordRequest;
import com.coyni.mapp.utils.MyApplication;
import com.coyni.mapp.utils.Utils;
import com.coyni.mapp.viewmodel.LoginViewModel;

import java.util.regex.Pattern;

public class ConfirmPasswordActivity extends BaseActivity implements OnKeyboardVisibilityListener {
    TextInputEditText currentPassET;
    TextInputLayout currentTIL;
    CardView saveBtn;
    boolean btnEnabled = false;
    ImageView backBtn;
    String oldPassword;
    Boolean isCPwdEye = false;
    private Pattern strong;
    private static final String STRONG_PATTERN =
            "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%*!?]).{8,})";
    LoginViewModel loginViewModel;
    Dialog dialog;
    MyApplication objMyApplication;
    LinearLayout layoutPwdError;
    TextView tvPwdError;
    private Long mLastClickTime = 0L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_confirm_password);

        try {
            loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
            objMyApplication = (MyApplication) getApplicationContext();
            currentPassET = findViewById(R.id.currentPassET);
            currentTIL = findViewById(R.id.currentPassTIL);
            saveBtn = findViewById(R.id.saveBtnCV);
            backBtn = findViewById(R.id.cpConfirmBackIV);
            layoutPwdError = findViewById(R.id.layoutPwdError);
            tvPwdError = findViewById(R.id.tvPwdError);
            strong = Pattern.compile(STRONG_PATTERN);

            setKeyboardVisibilityListener(this);

            currentTIL.setEndIconOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (!isCPwdEye) {
                            isCPwdEye = true;
                            currentTIL.setEndIconDrawable(R.drawable.ic_eyeopen);
                            currentPassET.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        } else {
                            isCPwdEye = false;
                            currentTIL.setEndIconDrawable(R.drawable.ic_eyeclose);
                            currentPassET.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        }
                        if (currentPassET.getText().length() > 0) {
                            currentPassET.setSelection(currentPassET.getText().length());
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });


            backBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(Utils.isKeyboardVisible)
                        Utils.hideKeypad(ConfirmPasswordActivity.this);
                    finish();
                }
            });

            currentPassET.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (i2 - i1 > 1) {
                        currentPassET.setText(charSequence);
                        currentPassET.setSelection(charSequence.toString().length());
                    }
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (charSequence.length() >= 8 && strong.matcher(currentPassET.getText().toString().trim()).matches()) {
                        btnEnabled = true;
                        oldPassword = currentPassET.getText().toString().trim();
                        saveBtn.setCardBackgroundColor(getResources().getColor(R.color.primary_color));
                    } else {
                        btnEnabled = false;
                        saveBtn.setCardBackgroundColor(getResources().getColor(R.color.inactive_color));
                        layoutPwdError.setVisibility(View.GONE);
                    }

                }

                @Override
                public void afterTextChanged(Editable s) {
                    try {
                        if (s.length() > 0 && s.toString().trim().length() == 0) {
                            currentPassET.setText("");
                        } else if (s.length() > 0 && s.toString().contains(" ")) {
                            currentPassET.setText(s.toString().trim());
                            currentPassET.setSelection(s.toString().trim().length());
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

//            currentPassET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//                @Override
//                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                    if (actionId == EditorInfo.IME_ACTION_DONE) {
//                        Utils.hideKeypad(ConfirmPasswordActivity.this);
//                    }
//                    return false;
//                }
//            });

            currentPassET.setCustomSelectionActionModeCallback(new ActionMode.Callback() {

                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

                public void onDestroyActionMode(ActionMode mode) {
                }

                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    return false;
                }
            });

            saveBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();
                        if (btnEnabled) {
                            dialog = Utils.showProgressDialog(ConfirmPasswordActivity.this);
                            PasswordRequest passwordRequest = new PasswordRequest();
                            passwordRequest.setUsername(objMyApplication.getStrEmail());
                            passwordRequest.setPassword(currentPassET.getText().toString().trim());
                            loginViewModel.authenticatePassword(passwordRequest);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });

            loginViewModel.getAuthenticatePasswordResponse().observe(this, new Observer<LoginResponse>() {
                @Override
                public void onChanged(LoginResponse loginResponse) {
                    dialog.dismiss();
                    if (loginResponse != null) {
                        if (loginResponse.getStatus().trim().toLowerCase().equals("success")) {
                            layoutPwdError.setVisibility(View.GONE);
                            startActivity(new Intent(ConfirmPasswordActivity.this, CreatePasswordActivity.class)
                                    .putExtra("screen", "ConfirmPassword")
                                    .putExtra("oldpassword", oldPassword)
                            );
                            clearField();

                        } else {
                            if (loginResponse.getError().getErrorDescription().contains("Invalid credentials")) {
                                layoutPwdError.setVisibility(View.VISIBLE);
                                tvPwdError.setText("Password Not Matched");
                                currentPassET.clearFocus();
                                Utils.setUpperHintColor(currentTIL, getColor(R.color.error_red));
                                currentTIL.setBoxStrokeColorStateList(Utils.getErrorColorState(ConfirmPasswordActivity.this));
                            } else {
                                layoutPwdError.setVisibility(View.GONE);
                                Utils.displayAlert(loginResponse.getError().getErrorDescription(), ConfirmPasswordActivity.this, "", loginResponse.getError().getFieldErrors().get(0));
                            }
                        }
                    }
                }
            });

            currentPassET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (!b) {

                    } else {
                        Utils.setUpperHintColor(currentTIL, getColor(R.color.primary_green));
                        currentTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                        layoutPwdError.setVisibility(GONE);
                    }
                }
            });

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            currentPassET.requestFocus();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clearField() {
        currentPassET.setText("");
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void setKeyboardVisibilityListener(final OnKeyboardVisibilityListener onKeyboardVisibilityListener) {
        final View parentView = ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
        parentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            private boolean alreadyOpen;
            private final int defaultKeyboardHeightDP = 100;
            private final int EstimatedKeyboardDP = defaultKeyboardHeightDP + (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ? 48 : 0);
            private final Rect rect = new Rect();

            @Override
            public void onGlobalLayout() {
                int estimatedKeyboardHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, EstimatedKeyboardDP, parentView.getResources().getDisplayMetrics());
                parentView.getWindowVisibleDisplayFrame(rect);
                int heightDiff = parentView.getRootView().getHeight() - (rect.bottom - rect.top);
                boolean isShown = heightDiff >= estimatedKeyboardHeight;

                if (isShown == alreadyOpen) {
                    Log.i("Keyboard state", "Ignoring global layout change...");
                    return;
                }
                alreadyOpen = isShown;
                onKeyboardVisibilityListener.onVisibilityChanged(isShown);
            }
        });
    }

    @Override
    public void onVisibilityChanged(boolean visible) {
        Utils.isKeyboardVisible = visible;
    }
}