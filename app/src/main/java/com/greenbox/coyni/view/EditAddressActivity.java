package com.greenbox.coyni.view;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.greenbox.coyni.R;
import com.greenbox.coyni.adapters.StatesListAdapter;
import com.greenbox.coyni.model.States;
import com.greenbox.coyni.model.users.User;
import com.greenbox.coyni.model.users.UserData;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.viewmodel.CustomerProfileViewModel;

import java.util.ArrayList;
import java.util.List;

public class EditAddressActivity extends AppCompatActivity {

    TextInputEditText address1ET, address2ET, cityET, stateET, zipcodeET;
    TextInputLayout address1TIL, address2TIL, cityTIL, stateTIL, zipcodeTIL, countryTIL;
    ConstraintLayout stateCL;
    MyApplication myApplicationObj;
    CardView editAddressSaveCV;
    boolean isAddress1, isAddress2, isCity, isState, isZipcode, isSaveEnabled;
    Dialog popupStates;
    StatesListAdapter statesListAdapter;
    List<States> listStates = new ArrayList<>();
    LinearLayout backIV, address1ErrorLL, address2ErrorLL, cityErrorLL, zipcodeErrorLL;
    TextView address1ErrorTV, address2ErrorTV, cityErrorTV, zipcodeErrorTV;
    Long mLastClickTime = 0L;
    ProgressDialog dialog;
    CustomerProfileViewModel customerProfileViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_edit_address);
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(Color.TRANSPARENT);
            initfields();
            textWatchers();
            focusWatchers();
            initObservers();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        Utils.tempStateName = "";
        super.onBackPressed();
    }

    public void initfields() {

        try {

            myApplicationObj = (MyApplication) getApplicationContext();
            address1TIL = findViewById(R.id.addressLineOneTIL);
            address2TIL = findViewById(R.id.addressLineTwoTIL);
            cityTIL = findViewById(R.id.cityTIL);
            stateTIL = findViewById(R.id.stateTIL);
            stateCL = findViewById(R.id.stateCL);
            zipcodeTIL = findViewById(R.id.zipcodeTIL);

            address1ET = findViewById(R.id.addressLineOneET);
            address2ET = findViewById(R.id.addressLineTwoET);
            cityET = findViewById(R.id.cityET);
            stateET = findViewById(R.id.stateET);
            zipcodeET = findViewById(R.id.zipcodeET);
            backIV = findViewById(R.id.backIV);

            address1ErrorLL = findViewById(R.id.address1ErrorLL);
            address1ErrorTV = findViewById(R.id.address1ErrorTV);

            address2ErrorLL = findViewById(R.id.address2ErrorLL);
            address2ErrorTV = findViewById(R.id.address2ErrorTV);

            cityErrorLL = findViewById(R.id.cityErrorLL);
            cityErrorTV = findViewById(R.id.cityErrorTV);

            zipcodeErrorLL = findViewById(R.id.zipcodeErrorLL);
            zipcodeErrorTV = findViewById(R.id.zipcodeErrorTV);
            countryTIL = findViewById(R.id.countryTIL);

            editAddressSaveCV = findViewById(R.id.editAddressSaveCV);

            customerProfileViewModel = new ViewModelProvider(this).get(CustomerProfileViewModel.class);

            address1ET.setFilters(new InputFilter[]{new InputFilter.LengthFilter(100)});
            address2ET.setFilters(new InputFilter[]{new InputFilter.LengthFilter(100)});
            cityET.setFilters(new InputFilter[]{new InputFilter.LengthFilter(50)});
            zipcodeET.setFilters(new InputFilter[]{new InputFilter.LengthFilter(5)});


            address1TIL.setBoxStrokeColorStateList(Utils.getNormalColorState());
            address2TIL.setBoxStrokeColorStateList(Utils.getNormalColorState());
            cityTIL.setBoxStrokeColorStateList(Utils.getNormalColorState());
            stateTIL.setBoxStrokeColorStateList(Utils.getNormalColorState());
            zipcodeTIL.setBoxStrokeColorStateList(Utils.getNormalColorState());
            countryTIL.setBoxStrokeColorStateList(Utils.getNormalColorState());

            editAddressSaveCV.setOnClickListener(view -> {
                if (isSaveEnabled) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    dialog = new ProgressDialog(EditAddressActivity.this, R.style.MyAlertDialogStyle);
                    dialog.setIndeterminate(false);
                    dialog.setMessage("Please wait...");
                    dialog.show();
                    Utils.tempStateName = "";
                    updateAddress();
                }
            });

            stateCL.setOnClickListener(view -> {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                Utils.populateStates(this, stateET, myApplicationObj);
            });

            stateTIL.setOnClickListener(view -> {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                Utils.populateStates(this, stateET, myApplicationObj);
            });

            stateET.setOnClickListener(view -> {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                Utils.populateStates(this, stateET, myApplicationObj);
            });

            backIV.setOnClickListener(view -> finish());

            if (myApplicationObj.getMyProfile().getData().getFirstName() != null) {
                if (myApplicationObj.getMyProfile().getData().getAddressLine1() != null
                        && !myApplicationObj.getMyProfile().getData().getAddressLine1().equals("")) {
                    address1ET.setText(myApplicationObj.getMyProfile().getData().getAddressLine1());
                    isAddress1 = true;
                } else {
                    isAddress1 = false;
                }
                if (myApplicationObj.getMyProfile().getData().getAddressLine2() != null
                        && !myApplicationObj.getMyProfile().getData().getAddressLine2().equals("")) {
                    address2ET.setText(myApplicationObj.getMyProfile().getData().getAddressLine2());
                    isAddress2 = true;
                } else {
                    isAddress2 = false;
                }
                if (myApplicationObj.getMyProfile().getData().getCity() != null
                        && !myApplicationObj.getMyProfile().getData().getCity().equals("")) {
                    cityET.setText(myApplicationObj.getMyProfile().getData().getCity());
                    isCity = true;
                } else {
                    isCity = false;
                }
                if (myApplicationObj.getMyProfile().getData().getState() != null
                        && !myApplicationObj.getMyProfile().getData().getState().equals("")) {
                    stateET.setText(myApplicationObj.getMyProfile().getData().getState());
                    Utils.tempStateName = myApplicationObj.getMyProfile().getData().getState();
                    isState = true;
                } else {
                    isState = false;
                }
                if (myApplicationObj.getMyProfile().getData().getZipCode() != null
                        && !myApplicationObj.getMyProfile().getData().getZipCode().equals("")) {
                    zipcodeET.setText(myApplicationObj.getMyProfile().getData().getZipCode());
                    isZipcode = true;
                } else {
                    isZipcode = false;
                }

                enableOrDisableSave();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void textWatchers() {
        address1ET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0) {
                    isAddress1 = true;
                    address1ErrorLL.setVisibility(GONE);
                    address1TIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                    Utils.setUpperHintColor(address1TIL, getResources().getColor(R.color.primary_green));
                } else {
                    address1ErrorLL.setVisibility(VISIBLE);
                    address1ErrorTV.setText("Field Required");
                    isAddress1 = false;
                }
                enableOrDisableSave();

            }

            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    String str = address1ET.getText().toString();
                    if (str.length() > 0 && str.substring(0, 1).equals(" ")) {
                        address1ET.setText(str.substring(1));
//                        address1ET.setSelection(address1ET.getText().length());
                        address1ErrorLL.setVisibility(GONE);
                    } else if (str.substring(0).equals(" ")) {
                        address1ET.setText("");
                        address1ET.setSelection(address1ET.getText().length());
                        address1ErrorLL.setVisibility(GONE);
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
        });

        address2ET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                if(charSequence.length() > 0){
//                    isAddress2 = true;
//                    address2ErrorLL.setVisibility(GONE);
//                    address2TIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
//                    Utils.setUpperHintColor(address2TIL,getResources().getColor(R.color.primary_green));
//                }else{
//                    address2ErrorLL.setVisibility(VISIBLE);
//                    address2ErrorTV.setText("Field Required");
//                    isAddress2 = false;
//                }
                enableOrDisableSave();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    String str = address2ET.getText().toString();
                    if (str.length() > 0 && str.substring(0, 1).equals(" ")) {
                        address2ET.setText(str.substring(1));
                        address2ErrorLL.setVisibility(GONE);
                    } else if (str.substring(0).equals(" ")) {
                        address2ET.setText("");
                        address2ET.setSelection(address2ET.getText().length());
                        address2ErrorLL.setVisibility(GONE);
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        cityET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0) {
                    isCity = true;
                    cityErrorLL.setVisibility(GONE);
                    cityTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                    Utils.setUpperHintColor(cityTIL, getResources().getColor(R.color.primary_green));
                } else {
                    cityErrorLL.setVisibility(VISIBLE);
                    cityErrorTV.setText("Field Required");
                    isCity = false;
                }
                enableOrDisableSave();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    String str = cityET.getText().toString();
                    if (str.length() > 0 && str.substring(0, 1).equals(" ")) {
                        cityET.setText(str.substring(1));
                        cityErrorLL.setVisibility(GONE);
                    } else if (str.substring(0).equals(" ")) {
                        cityET.setText("");
                        cityET.setSelection(cityET.getText().length());
                        cityErrorLL.setVisibility(GONE);
                    } else if (str.length() > 0 && str.substring(str.length() - 1).equals(".")) {
                        cityET.setText(cityET.getText().toString().replaceAll(".", ""));
                        cityET.setSelection(cityET.getText().length());
                        cityErrorLL.setVisibility(GONE);
                    }


                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        stateET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0) {
                    isState = true;
                } else {
                    isState = false;
                }
                enableOrDisableSave();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        zipcodeET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 5) {
                    isZipcode = true;
                    zipcodeErrorLL.setVisibility(GONE);
                    zipcodeTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                    Utils.setUpperHintColor(zipcodeTIL, getResources().getColor(R.color.primary_green));
                } else if (charSequence.length() < 5) {
                    isZipcode = false;
                    zipcodeErrorLL.setVisibility(GONE);
                    zipcodeTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                    Utils.setUpperHintColor(zipcodeTIL, getResources().getColor(R.color.primary_green));
                    zipcodeErrorTV.setText("Minimum 5 Characters Required");
                } else if (charSequence.length() == 0) {
                    isZipcode = false;
                    zipcodeErrorLL.setVisibility(VISIBLE);
                    zipcodeErrorTV.setText("Field Required");
                }
                enableOrDisableSave();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    public void focusWatchers() {

        address1ET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    if (address1ET.getText().toString().trim().length() > 0) {
                        address1ErrorLL.setVisibility(GONE);
                        address1TIL.setBoxStrokeColorStateList(Utils.getNormalColorState());
                        Utils.setUpperHintColor(address1TIL, getColor(R.color.primary_black));

                    } else {
                        address1TIL.setBoxStrokeColorStateList(Utils.getErrorColorState());
                        Utils.setUpperHintColor(address1TIL, getColor(R.color.error_red));
                        address1ErrorLL.setVisibility(VISIBLE);
                        address1ErrorTV.setText("Field Required");
                    }
                } else {
                    address1TIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                    Utils.setUpperHintColor(address1TIL, getColor(R.color.primary_green));
                }
            }
        });

        address2ET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    address2TIL.setBoxStrokeColorStateList(Utils.getNormalColorState());
                    Utils.setUpperHintColor(address2TIL, getColor(R.color.primary_black));
                } else {
                    address2TIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                    Utils.setUpperHintColor(address2TIL, getColor(R.color.primary_green));
                }
            }
        });


        cityET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    if (cityET.getText().toString().trim().length() > 0) {
                        cityErrorLL.setVisibility(GONE);
                        cityTIL.setBoxStrokeColorStateList(Utils.getNormalColorState());
                        Utils.setUpperHintColor(cityTIL, getColor(R.color.primary_black));

                    } else {
                        cityTIL.setBoxStrokeColorStateList(Utils.getErrorColorState());
                        Utils.setUpperHintColor(cityTIL, getColor(R.color.error_red));
                        cityErrorLL.setVisibility(VISIBLE);
                        cityErrorTV.setText("Field Required");
                    }
                } else {
                    cityTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                    Utils.setUpperHintColor(cityTIL, getColor(R.color.primary_green));
                }
            }
        });

        zipcodeET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    if (zipcodeET.getText().toString().trim().length() == 5) {
                        zipcodeErrorLL.setVisibility(GONE);
                        zipcodeTIL.setBoxStrokeColorStateList(Utils.getNormalColorState());
                        Utils.setUpperHintColor(zipcodeTIL, getColor(R.color.primary_black));

                    } else if (zipcodeET.getText().toString().trim().length() < 5 && zipcodeET.getText().toString().trim().length() > 0) {
                        zipcodeTIL.setBoxStrokeColorStateList(Utils.getErrorColorState());
                        Utils.setUpperHintColor(zipcodeTIL, getColor(R.color.error_red));
                        zipcodeErrorLL.setVisibility(VISIBLE);
                        zipcodeErrorTV.setText("Minimum 5 Characters Required");

                    } else {
                        zipcodeTIL.setBoxStrokeColorStateList(Utils.getErrorColorState());
                        Utils.setUpperHintColor(zipcodeTIL, getColor(R.color.error_red));
                        zipcodeErrorLL.setVisibility(VISIBLE);
                        zipcodeErrorTV.setText("Field Required");
                    }
                } else {
                    zipcodeTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                    Utils.setUpperHintColor(zipcodeTIL, getColor(R.color.primary_green));
                }
            }
        });

    }

    public void enableOrDisableSave() {
        try {
            if (isAddress1 && isCity && isState && isZipcode) {
                isSaveEnabled = true;
                editAddressSaveCV.setCardBackgroundColor(getResources().getColor(R.color.primary_color));
            } else {
                isSaveEnabled = false;
                editAddressSaveCV.setCardBackgroundColor(getResources().getColor(R.color.inactive_color));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void statesPopup() {
        try {
            popupStates = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
            popupStates.requestWindowFeature(Window.FEATURE_NO_TITLE);
            popupStates.setContentView(R.layout.countrieslist);
            popupStates.setCanceledOnTouchOutside(false);
            popupStates.setCancelable(false);
            popupStates.show();
            bindStates();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void bindStates() {
        RecyclerView rvCountries;
        ImageView imgBack;
        EditText etSearch;
        TextView tvNoResults;
        statesListAdapter = new StatesListAdapter(null, this, "EditAddress");
        try {
            imgBack = popupStates.findViewById(R.id.imgBack);
            etSearch = popupStates.findViewById(R.id.etSearch);
            tvNoResults = popupStates.findViewById(R.id.tvNoResults);
            rvCountries = popupStates.findViewById(R.id.rvCountries);
            listStates = myApplicationObj.getListStates();
            if (listStates != null && listStates.size() > 0) {
                statesListAdapter = new StatesListAdapter(listStates, this, "EditAddress");
                LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
                rvCountries.setLayoutManager(mLayoutManager);
                rvCountries.setItemAnimator(new DefaultItemAnimator());
                rvCountries.setAdapter(statesListAdapter);
            }
            imgBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupStates.dismiss();
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
                        List<States> filterList = new ArrayList<>();
                        int sIndex = 0;
                        if (listStates != null && listStates.size() > 0) {
                            for (int i = 0; i < listStates.size(); i++) {
                                sIndex = listStates.get(i).getName().toLowerCase().indexOf(search_key.toLowerCase());
                                if (sIndex == 0) {
                                    filterList.add(listStates.get(i));
                                }
                            }
                            if (filterList != null && filterList.size() > 0) {
                                statesListAdapter.updateList(filterList);
                                rvCountries.setVisibility(View.VISIBLE);
                                tvNoResults.setVisibility(View.GONE);
                            } else {
                                rvCountries.setVisibility(View.GONE);
                                tvNoResults.setVisibility(View.VISIBLE);
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

    public void populateState(String strState) {
        try {
            popupStates.dismiss();
            stateET.setText(strState);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void updateAddress() {
        try {

            UserData userData = new UserData();
            userData.setAddressLine1(address1ET.getText().toString().trim());
            userData.setAddressLine2(address2ET.getText().toString().trim());
            userData.setCity(cityET.getText().toString().trim());
            userData.setState(stateET.getText().toString().trim());
            userData.setZipCode(zipcodeET.getText().toString().trim());
            userData.setCountry("United States");

            customerProfileViewModel.meUpdateAddress(userData);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void initObservers() {

        customerProfileViewModel.getUserMutableLiveData().observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                try {
                    dialog.dismiss();
                    if (user != null) {
                        if (user.getStatus().toString().toLowerCase().equals("success")) {
                            Utils.showCustomToast(EditAddressActivity.this, "Address has been updated", R.drawable.ic_location, "EditAddress");
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        finish();
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }
                                }
                            }, 2000);
                        } else {
                            Utils.displayAlert(user.getError().getErrorDescription(), EditAddressActivity.this, "", user.getError().getFieldErrors().get(0));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}