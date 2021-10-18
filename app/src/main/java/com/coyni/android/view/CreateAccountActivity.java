package com.coyni.android.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.coyni.android.adapters.CountriesListAdapter;
import com.coyni.android.model.Countries;
import com.coyni.android.model.register.CustRegisRequest;
import com.coyni.android.model.register.CustRegisterResponse;
import com.coyni.android.model.register.PhNoWithCountryCode;
import com.coyni.android.utils.MyApplication;
import com.coyni.android.utils.Utils;
import com.coyni.android.viewmodel.LoginViewModel;
import com.google.android.material.textfield.TextInputEditText;
import com.coyni.android.R;
import com.santalu.maskara.widget.MaskEditText;

import java.util.ArrayList;
import java.util.List;

public class CreateAccountActivity extends AppCompatActivity {
    TextView tvTerms, tvPrivacy;
    CardView cvNext;
    MyApplication objContext;
    TextInputEditText etFirstName, etLastName, etEmail;
    ImageView imgBack;
    MaskEditText etPhone;
    ProgressDialog dialog;
    LoginViewModel loginViewModel;
    Dialog popupCountries;
    CountriesListAdapter countriesListAdapter;
    List<Countries> listCountries = new ArrayList<>();
    String strCode = "US";
    String phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_create_account);
            initialization();
            intiObserver();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void initialization() {
        try {
            loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
            imgBack = (ImageView) findViewById(R.id.imgBack);
            tvTerms = (TextView) findViewById(R.id.tvTerms);
            tvPrivacy = (TextView) findViewById(R.id.tvPrivacy);
            cvNext = (CardView) findViewById(R.id.cvNext);
            objContext = (MyApplication) getApplicationContext();
            etFirstName = (TextInputEditText) findViewById(R.id.etFirstName);
            etLastName = (TextInputEditText) findViewById(R.id.etLastName);
            etPhone = (MaskEditText) findViewById(R.id.etPhone);
            etEmail = (TextInputEditText) findViewById(R.id.etEmail);
            Utils.statusBar(CreateAccountActivity.this);

            imgBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });

            cvNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (validation()) {
                            phoneNumber = etPhone.getText().toString().substring(1, 4) + etPhone.getText().toString().substring(6, 9) + etPhone.getText().toString().substring(10, etPhone.getText().toString().length());
                            dialog = new ProgressDialog(CreateAccountActivity.this, R.style.MyAlertDialogStyle);
                            dialog.setIndeterminate(false);
                            dialog.setMessage("Please wait...");
                            dialog.show();
                            save();
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
            tvPrivacy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(CreateAccountActivity.this, SignUpPrivacyWebViewActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                }
            });
            tvTerms.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(CreateAccountActivity.this, SignUpTOSWebViewActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void intiObserver() {
        loginViewModel.getCustRegisResponseMutableLiveData().observe(this, new Observer<CustRegisterResponse>() {
            @Override
            public void onChanged(CustRegisterResponse custRegisterResponse) {
                dialog.dismiss();
                if (custRegisterResponse != null) {
                    try {
                        objContext.setStrEmail(etEmail.getText().toString().trim());
                        Intent i = new Intent(CreateAccountActivity.this, SmsOtpActivity.class);
                        i.putExtra("countryCode", strCode);
                        i.putExtra("phone", phoneNumber);
                        startActivity(i);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        loginViewModel.getErrorMessage().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (s.toString().toLowerCase().contains("entered email already exist in the system")) {
                    etEmail.setText("");
                }
                if (s.toString().toLowerCase().contains("entered phone number already exist in the system")) {
                    etPhone.setText("");
                }
                if (s.toString().toLowerCase().contains("entered phone number and email already exist in the system")) {
                    etPhone.setText("");
                    etEmail.setText("");
                }

                Utils.displayAlert(s, CreateAccountActivity.this);
            }
        });
    }

    private void save() {
        try {
            CustRegisRequest regisRequest = new CustRegisRequest();
            PhNoWithCountryCode phone = new PhNoWithCountryCode();
            regisRequest.setEmail(etEmail.getText().toString().trim());
            regisRequest.setFirstName(etFirstName.getText().toString().trim());
            regisRequest.setLastName(etLastName.getText().toString().trim());
            phone.setCountryCode(strCode);
//            phone.setPhoneNumber(etPhone.getText().toString().trim());
            phone.setPhoneNumber(phoneNumber);
            regisRequest.setPhoneNumberWithCountryCode(phone);
            loginViewModel.customerRegistration(regisRequest);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private Boolean validation() {
        Boolean value = true;
        try {
            if (etFirstName.getText().toString().equals("")) {
                Utils.displayAlert("Please enter your First Name", CreateAccountActivity.this);
                return value = false;
            } else if (etLastName.getText().toString().equals("")) {
                Utils.displayAlert("Please enter your Last Name", CreateAccountActivity.this);
                return value = false;
            } else if (etPhone.getText().toString().equals("")) {
                Utils.displayAlert("Please enter your Phone Number", CreateAccountActivity.this);
                return value = false;
            } else if (!etPhone.getText().toString().equals("") && etPhone.getText().toString().length() < 14) {
                Utils.displayAlert("Please enter valid Phone Number", CreateAccountActivity.this);
                return value = false;
            } else if (etEmail.getText().toString().equals("")) {
                Utils.displayAlert("Please enter your Email", CreateAccountActivity.this);
                return value = false;
            } else if (!isEmailValid(etEmail.getText().toString().trim())) {
                Utils.displayAlert("Please enter valid Email", CreateAccountActivity.this);
                return value = false;
            } else if (!etEmail.getText().toString().equals("")) {
                String strTail = etEmail.getText().toString().split("@")[1];
                String strTail2 = strTail.substring(strTail.indexOf("."));
                if (strTail2 != "") {
                    if (strTail2.length() <= 2) {
                        Utils.displayAlert("Please enter valid Email", CreateAccountActivity.this);
                        return value = false;
                    }
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return value;
    }

    boolean isEmailValid(CharSequence email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void countriesPopup() {
        try {
            popupCountries = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
            popupCountries.requestWindowFeature(Window.FEATURE_NO_TITLE);
            popupCountries.setContentView(R.layout.countrieslist);
            popupCountries.setCanceledOnTouchOutside(false);
            popupCountries.setCancelable(false);
            popupCountries.show();
            bindCountries();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void bindCountries() {
        RecyclerView rvCountries;
        ImageView imgBack;
        EditText etSearch;
        countriesListAdapter = new CountriesListAdapter(null, CreateAccountActivity.this, "create");
        try {
            imgBack = popupCountries.findViewById(R.id.imgBack);
            etSearch = popupCountries.findViewById(R.id.etSearch);
            listCountries = Utils.getCountries();
            if (listCountries != null && listCountries.size() > 0) {
                rvCountries = popupCountries.findViewById(R.id.rvCountries);
                countriesListAdapter = new CountriesListAdapter(listCountries, CreateAccountActivity.this, "create");
                LinearLayoutManager mLayoutManager = new LinearLayoutManager(CreateAccountActivity.this);
                rvCountries.setLayoutManager(mLayoutManager);
                rvCountries.setItemAnimator(new DefaultItemAnimator());
                rvCountries.setAdapter(countriesListAdapter);
            }
            imgBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupCountries.dismiss();
                }
            });
            etSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    try {
                        String search_key = s.toString();
                        List<Countries> filterList = new ArrayList<>();
                        int sIndex = 0;
                        if (listCountries != null && listCountries.size() > 0) {
                            for (int i = 0; i < listCountries.size(); i++) {
                                sIndex = listCountries.get(i).getName().toLowerCase().indexOf(search_key.toLowerCase());
                                if (sIndex == 0) {
                                    filterList.add(listCountries.get(i));
                                }
                            }
                            if (filterList != null && filterList.size() > 0) {
                                countriesListAdapter.updateList(filterList);
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void populateCountry(String code) {
        try {
            popupCountries.dismiss();
            strCode = code;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}