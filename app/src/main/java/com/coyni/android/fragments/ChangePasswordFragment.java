package com.coyni.android.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.coyni.android.view.MainActivity;
import com.coyni.android.model.login.ChangePasswordRequest;
import com.coyni.android.model.login.ChangePasswordResponse;
import com.coyni.android.model.usertracker.UserTracker;
import com.coyni.android.model.usertracker.UserTrackerData;
import com.coyni.android.utils.MyApplication;
import com.coyni.android.utils.Utils;
import com.coyni.android.view.LoginActivity;
import com.coyni.android.viewmodel.DashboardViewModel;
import com.coyni.android.viewmodel.LoginViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.coyni.android.R;
import com.google.android.material.textfield.TextInputLayout;

import java.util.regex.Pattern;

public class ChangePasswordFragment extends Fragment {
    View view;
    static Context context;
    ImageView imgBack;
    MyApplication objMyApplication;
    TextInputEditText etOldPassword, etNewPassword, etConfirmPassword;
    DashboardViewModel dashboardViewModel;
    ProgressDialog dialog;
    CardView cvUpdatePassword, cvPwdCriteria;
    LoginViewModel loginViewModel;
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[a-z])(?=."
                    + "*[A-Z])(?=.*\\d)"
                    + "(?=.*[-+_!@#$%^&*., ?]).+$");

    private static final Pattern UPPERCASE =
            Pattern.compile("^(?=.*[A-Z]).+$");
    private static final Pattern LOWERCASE =
            Pattern.compile("^(?=.*[a-z]).+$");
    private static final Pattern NUMBER =
            Pattern.compile("^(?=.*\\d).+$");
    private static final Pattern SPECIAL =
            Pattern.compile("^(?=.*[-+_!@#$%^&*., ?]).+$");
    ImageView imgUpper, imgNumber, imgLower, imgSpecial, imgNoOfCharacters;
    TextView tvUpper, tvNumber, tvLower, tvSpecial, tvNoOfCharacters;
    SQLiteDatabase mydatabase;
    Cursor dsUserDetails;
    String strNewPwd = "";

    public ChangePasswordFragment() {
    }

    public static ChangePasswordFragment newInstance(Context cxt) {
        ChangePasswordFragment fragment = new ChangePasswordFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        context = cxt;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_change_password, container, false);
        try {
            dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
            objMyApplication = (MyApplication) context.getApplicationContext();
            loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
            if (Build.VERSION.SDK_INT >= 23) {
                //getActivity().getWindow().setStatusBarColor(getActivity().getColor(R.color.statusbar));
                getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                        WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            }
            getActivity().findViewById(R.id.layoutMenu).setVisibility(View.GONE);

            etOldPassword = (TextInputEditText) view.findViewById(R.id.etOldPassword);
            etNewPassword = (TextInputEditText) view.findViewById(R.id.etNewPassword);
            etConfirmPassword = (TextInputEditText) view.findViewById(R.id.etConfirmPassword);
            cvUpdatePassword = (CardView) view.findViewById(R.id.cvUpdatePassword);
            cvPwdCriteria = (CardView) view.findViewById(R.id.cvPwdCriteria);
            imgUpper = (ImageView) view.findViewById(R.id.imgUpper);
            imgNumber = (ImageView) view.findViewById(R.id.imgNumber);
            imgLower = (ImageView) view.findViewById(R.id.imgLower);
            imgSpecial = (ImageView) view.findViewById(R.id.imgSpecial);
            imgNoOfCharacters = (ImageView) view.findViewById(R.id.imgNoOfCharacters);
            tvUpper = (TextView) view.findViewById(R.id.tvUpper);
            tvNumber = (TextView) view.findViewById(R.id.tvNumber);
            tvLower = (TextView) view.findViewById(R.id.tvLower);
            tvSpecial = (TextView) view.findViewById(R.id.tvSpecial);
            tvNoOfCharacters = (TextView) view.findViewById(R.id.tvNoOfCharacters);

            imgBack = (ImageView) view.findViewById(R.id.imgBack);

            etNewPassword.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() > 0) {
                        cvPwdCriteria.setVisibility(View.VISIBLE);
                        passwordCreteria(s.toString());
                    } else {
                        cvPwdCriteria.setVisibility(View.GONE);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            SetDB();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        cvUpdatePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (validation()) {
                        Utils.hideKeypad(getActivity(), v);
                        dialog = new ProgressDialog(getActivity(), R.style.MyAlertDialogStyle);
                        dialog.setIndeterminate(false);
                        dialog.setMessage("Please wait...");
                        dialog.getWindow().setGravity(Gravity.CENTER);
                        dialog.show();
                        strNewPwd = etNewPassword.getText().toString().trim();
                        ChangePasswordRequest changePwdRequest = new ChangePasswordRequest();
                        changePwdRequest.setOldPassword(etOldPassword.getText().toString().trim());
                        changePwdRequest.setNewPassword(etNewPassword.getText().toString().trim());
                        loginViewModel.changePassword(changePwdRequest, objMyApplication.getIntUserId());
//                        initObserver();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) context).loadProfile();
            }
        });
        initObserver();
        return view;
    }

    private Boolean validation() {
        Boolean value = true;
        try {
            if (etOldPassword.getText().toString().equals("")) {
                Utils.displayAlert("Please enter your current Password", getActivity());
                return value = false;
            } else if (etNewPassword.getText().toString().equals("")) {
                Utils.displayAlert("Password is required", getActivity());
                return value = false;
            } else if (!PASSWORD_PATTERN.matcher(etNewPassword.getText().toString()).matches()) {
                Utils.displayAlert("Password should be 1 Uppercase,1 Special Character,1 Number,1 Lowercase and length should be minimum 8", getActivity());
                return value = false;
            } else if (etNewPassword.getText().toString().equals(etOldPassword.getText().toString())) {
                Utils.displayAlert("New password must not match with Old password", getActivity());
                return value = false;
            } else if (etConfirmPassword.getText().toString().equals("")) {
                Utils.displayAlert("Confirm password is required", getActivity());
                return value = false;
            } else if (!etConfirmPassword.getText().toString().equals(etNewPassword.getText().toString())) {
                Utils.displayAlert("New Password and Confirm password should be same", getActivity());
                return value = false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return value;
    }

    private void initObserver() {
        loginViewModel.getChangePasswordLiveData().observe(this, new Observer<ChangePasswordResponse>() {
            @Override
            public void onChanged(ChangePasswordResponse changePwdResponse) {
                dialog.dismiss();
                if (changePwdResponse != null && changePwdResponse.getError() == null) {
                    displayAlert_ChangePwd("Password changed Successfully", getActivity());
                    try {
                        dsUserDetails = mydatabase.rawQuery("Select * from tblUserDetails", null);
                        dsUserDetails.moveToFirst();
                        if (dsUserDetails.getCount() > 0) {
                            mydatabase.execSQL("Delete from tblUserDetails");
                            mydatabase.execSQL("INSERT INTO tblUserDetails(id,username,password) VALUES(null,'" + objMyApplication.getStrLEmail() + "','" + strNewPwd + "')");
                        }
                    } catch (Exception ex) {
                        if (ex.getMessage().toString().contains("no such table")) {
                            mydatabase.execSQL("DROP TABLE IF EXISTS tblUserDetails;");
                            mydatabase.execSQL("CREATE TABLE IF NOT EXISTS tblUserDetails(id INTEGER PRIMARY KEY AUTOINCREMENT DEFAULT 1, username TEXT, password TEXT);");
                        }
                    }
                } else {
                    Utils.displayAlert(loginViewModel.getErrorMessage1(), getActivity());
                }
            }
        });
    }

    private void passwordCreteria(String password) {
        try {
            Boolean isUpper = false, isLower = false, isNum = false, isSpec = false, isNumCharacters = false;
            if (UPPERCASE.matcher(password).matches()) {
                imgUpper.setVisibility(View.VISIBLE);
                imgUpper.setImageResource(R.drawable.ic_tick);
                tvUpper.setTextColor(Color.GREEN);
                isUpper = true;
            } else {
                imgUpper.setVisibility(View.VISIBLE);
                imgUpper.setImageResource(R.drawable.ic_close);
                tvUpper.setTextColor(Color.RED);
                isUpper = false;
            }
            if (LOWERCASE.matcher(password).matches()) {
                imgLower.setVisibility(View.VISIBLE);
                imgLower.setImageResource(R.drawable.ic_tick);
                tvLower.setTextColor(Color.GREEN);
                isLower = true;
            } else {
                imgLower.setVisibility(View.VISIBLE);
                imgLower.setImageResource(R.drawable.ic_close);
                tvLower.setTextColor(Color.RED);
                isLower = false;
            }
            if (NUMBER.matcher(password).matches()) {
                imgNumber.setVisibility(View.VISIBLE);
                imgNumber.setImageResource(R.drawable.ic_tick);
                tvNumber.setTextColor(Color.GREEN);
                isNum = true;
            } else {
                imgNumber.setVisibility(View.VISIBLE);
                imgNumber.setImageResource(R.drawable.ic_close);
                tvNumber.setTextColor(Color.RED);
                isNum = false;
            }
            if (SPECIAL.matcher(password).matches()) {
                imgSpecial.setVisibility(View.VISIBLE);
                imgSpecial.setImageResource(R.drawable.ic_tick);
                tvSpecial.setTextColor(Color.GREEN);
                isSpec = true;
            } else {
                imgSpecial.setVisibility(View.VISIBLE);
                imgSpecial.setImageResource(R.drawable.ic_close);
                tvSpecial.setTextColor(Color.RED);
                isSpec = false;
            }
            if (password.length() >= 8 && password.length() <= 20) {
                imgNoOfCharacters.setVisibility(View.VISIBLE);
                imgNoOfCharacters.setImageResource(R.drawable.ic_tick);
                tvNoOfCharacters.setTextColor(Color.GREEN);
                isNumCharacters = true;
            } else {
                imgNoOfCharacters.setVisibility(View.VISIBLE);
                imgNoOfCharacters.setImageResource(R.drawable.ic_close);
                tvNoOfCharacters.setTextColor(Color.RED);
                isNumCharacters = false;
            }

            if (isUpper && isLower && isNum && isSpec && isNumCharacters) {
                cvPwdCriteria.setVisibility(View.GONE);
            } else {
                cvPwdCriteria.setVisibility(View.VISIBLE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void displayAlert_ChangePwd(String msg, Activity activity) {
        try {
//        Context context = new ContextThemeWrapper(activity, R.style.Theme_QuickCard);
//        new MaterialAlertDialogBuilder(context)
//                .setTitle(R.string.app_name)
//                .setMessage(msg)
//                .setCancelable(false)
//                .setPositiveButton("OK", (dialog, which) -> {
//                    dialog.dismiss();
//                    Intent i = new Intent(getActivity(), LoginActivity.class);
//                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(i);
//                    getActivity().finish();
//                }).show();

            Context context = new ContextThemeWrapper(activity, R.style.Theme_QuickCard);
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);

            builder.setTitle(R.string.app_name);
            builder.setMessage(msg);
            AlertDialog dialog = builder.show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    dialog.dismiss();
                    Intent i = new Intent(getActivity(), LoginActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                    getActivity().finish();
                }
            }, Integer.parseInt(context.getString(R.string.closealert)));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void SetDB() {
        try {
            mydatabase = getActivity().openOrCreateDatabase("Coyni", getActivity().MODE_PRIVATE, null);
            dsUserDetails = mydatabase.rawQuery("Select * from tblUserDetails", null);
            dsUserDetails.moveToFirst();
        } catch (Exception ex) {
            if (ex.getMessage().toString().contains("no such table")) {
                mydatabase.execSQL("DROP TABLE IF EXISTS tblUserDetails;");
                mydatabase.execSQL("CREATE TABLE IF NOT EXISTS tblUserDetails(id INTEGER PRIMARY KEY AUTOINCREMENT DEFAULT 1, username TEXT, password TEXT);");
            }
        }
    }
}

