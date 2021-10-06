package com.coyni.android.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.coyni.android.adapters.CountriesListAdapter;
import com.coyni.android.adapters.StatesListAdapter;
import com.coyni.android.model.APIError;
import com.coyni.android.model.Countries;
import com.coyni.android.model.Error;
import com.coyni.android.model.States;
import com.coyni.android.model.cards.CardEditRequest;
import com.coyni.android.model.cards.CardEditResponse;
import com.coyni.android.model.cards.CardRequest;
import com.coyni.android.model.cards.CardResponse;
import com.coyni.android.model.cards.CardResponseData;
import com.coyni.android.model.cards.CardsDataItem;
import com.coyni.android.model.preauth.PreAuthData;
import com.coyni.android.model.preauth.PreAuthRequest;
import com.coyni.android.model.preauth.PreAuthResponse;
import com.coyni.android.utils.MyApplication;
import com.coyni.android.utils.Utils;
import com.coyni.android.viewmodel.BuyViewModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.coyni.android.R;

import java.util.ArrayList;
import java.util.List;

public class AddressActivity extends AppCompatActivity {
    MyApplication objMyApplication;
    TextInputEditText etAdd1, etAdd2, etCountry, etState, etCity, etZipcode;
    BuyViewModel buyViewModel;
    String strName, strNumber, strExpiry, strCVV;
    Dialog popupCountries, popupStates;
    CountriesListAdapter countriesListAdapter;
    StatesListAdapter statesListAdapter;
    CardsDataItem selectedCard;
    List<Countries> listCountries = new ArrayList<>();
    List<States> listStates = new ArrayList<>();
    BottomSheetBehavior bottomSheetBehavior;
    View viewBottomSheet, viewBack, bottom_sheet_preAuth_error;
    ProgressDialog dialog;
    TextView tvHeading, tvHead, tvPreAuthFailMessage;
    String strMessageForDescriptor;
    String publicKey;
    LinearLayout layoutOK;
    ImageView imgAuthEClose;
    CardResponseData cardResponseData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_address);
            initialization();
            initObserver();
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

    @Override
    protected void onResume() {
        super.onResume();
        try {
            objMyApplication.getHandler().removeCallbacks(objMyApplication.getMyRunnable());
            objMyApplication.userInactive(AddressActivity.this, this, false);
            objMyApplication.getAppHandler().removeCallbacks(objMyApplication.getAppRunnable());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        objMyApplication.getHandler().removeCallbacks(objMyApplication.getMyRunnable());
        objMyApplication.userInactive(AddressActivity.this, this, true);
    }

    @Override
    public void onUserInteraction() {
        // TODO Auto-generated method stub
        super.onUserInteraction();
        objMyApplication.getHandler().removeCallbacks(objMyApplication.getMyRunnable());
        objMyApplication.userInactive(AddressActivity.this, this, false);
    }

    private void initialization() {
        try {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_sent);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            Utils.statusBar(AddressActivity.this);
            objMyApplication = (MyApplication) getApplicationContext();
            buyViewModel = new ViewModelProvider(this).get(BuyViewModel.class);
            etAdd1 = (TextInputEditText) findViewById(R.id.etAdd1);
            etAdd2 = (TextInputEditText) findViewById(R.id.etAdd2);
            etCountry = (TextInputEditText) findViewById(R.id.etCountry);
            etState = (TextInputEditText) findViewById(R.id.etState);
            etCity = (TextInputEditText) findViewById(R.id.etCity);
            etZipcode = (TextInputEditText) findViewById(R.id.etZipcode);
            LinearLayout layoutConfirm = (LinearLayout) findViewById(R.id.layoutConfirm);
            LinearLayout layoutArrow = (LinearLayout) findViewById(R.id.layoutArrow);
            LinearLayout layoutStateArrow = (LinearLayout) findViewById(R.id.layoutStateArrow);
            viewBottomSheet = findViewById(R.id.bottom_sheet_preAuth);
            viewBack = (View) findViewById(R.id.viewBack);
            bottomSheetBehavior = BottomSheetBehavior.from(viewBottomSheet);
            bottomSheetBehavior.setHideable(true);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            tvHeading = (TextView) findViewById(R.id.tvHeading);
            tvHead = (TextView) findViewById(R.id.tvHead);
            tvPreAuthFailMessage = (TextView) findViewById(R.id.tvPreAuthFailMessage);
            layoutOK = (LinearLayout) findViewById(R.id.layoutOK);
            imgAuthEClose = (ImageView) findViewById(R.id.imgAuthEClose);
            bottom_sheet_preAuth_error = findViewById(R.id.bottom_sheet_preAuth_error);
            if (getIntent().getStringExtra("fromProfilePaymentMethods") != null && getIntent().getStringExtra("fromProfilePaymentMethods").equals("fromProfilePaymentMethods")) {
                tvHeading.setText("Debit / Credit Cards");
                tvHead.setText("Add Billing Address");
            }
            if (getIntent().getStringExtra("strEncryptedPublicKey") != null) {
                publicKey = getIntent().getStringExtra("strEncryptedPublicKey");
            }
            if (getIntent().getStringExtra("name") != null && !getIntent().getStringExtra("name").equals("")) {
                strName = getIntent().getStringExtra("name");
            }
            if (getIntent().getStringExtra("number") != null && !getIntent().getStringExtra("number").equals("")) {
                strNumber = getIntent().getStringExtra("number");
            }
            if (getIntent().getStringExtra("expiry") != null && !getIntent().getStringExtra("expiry").equals("")) {
                strExpiry = getIntent().getStringExtra("expiry");
            }
            if (getIntent().getStringExtra("cvv") != null && !getIntent().getStringExtra("cvv").equals("")) {
                strCVV = getIntent().getStringExtra("cvv");
            }
            if (objMyApplication.getSelectedCard() != null && getIntent().getStringExtra("from") == null) {
                selectedCard = objMyApplication.getSelectedCard();
                etAdd1.setText(selectedCard.getAddressLine1());
                etAdd2.setText(selectedCard.getAddressLine2());
                etCountry.setText(selectedCard.getCountry());
                etState.setText(selectedCard.getState());
                etCity.setText(selectedCard.getCity());
                etZipcode.setText(selectedCard.getZipCode());
            }
            layoutArrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //countriesPopup();
                }
            });
            layoutStateArrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    statesPopup();
                }
            });
            layoutConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Utils.hideKeypad(AddressActivity.this, v);
                        if (validation()) {
                            dialog = new ProgressDialog(AddressActivity.this, R.style.MyAlertDialogStyle);
                            dialog.setIndeterminate(false);
                            dialog.setMessage("Please wait...");
                            dialog.getWindow().setGravity(Gravity.CENTER);
                            dialog.show();
                            if (getIntent().getStringExtra("from") != null && !getIntent().getStringExtra("from").equals("")) {
                                CardRequest obj = new CardRequest();
                                obj.setCardNumber(strNumber);
                                obj.setName(strName);
                                obj.setCvc(strCVV);
                                obj.setExpiryDate(strExpiry);
                                obj.setAddressLine1(etAdd1.getText().toString().trim());
                                obj.setAddressLine2(etAdd2.getText().toString().trim());
//                                obj.setCountry(etCountry.getText().toString().trim());
                                obj.setCountry("US");
                                obj.setState(etState.getText().toString().trim());
                                obj.setCity(etCity.getText().toString().trim());
                                obj.setZipCode(etZipcode.getText().toString().trim());
                                obj.setDefaultForAllWithDrawals(true);
                                if (Utils.checkInternet(AddressActivity.this)) {
                                    buyViewModel.saveCards(obj);
                                } else {
//                                    Toast.makeText(AddressActivity.this, getString(R.string.internet), Toast.LENGTH_LONG).show();
                                    Utils.displayAlert(getString(R.string.internet), AddressActivity.this);
                                }
                            } else {
                                CardEditRequest obj = new CardEditRequest();
                                obj.setAddressLine1(etAdd1.getText().toString().trim());
                                obj.setAddressLine2(etAdd2.getText().toString().trim());
//                                obj.setCountry(etCountry.getText().toString().trim());
                                obj.setCountry("US");
                                obj.setState(etState.getText().toString().trim());
                                obj.setCity(etCity.getText().toString().trim());
                                obj.setZipCode(etZipcode.getText().toString().trim());
                                obj.setDefaultForAllWithDrawals(true);
                                obj.setName(strName);
                                obj.setExpiryDate(strExpiry);
                                if (Utils.checkInternet(AddressActivity.this)) {
                                    buyViewModel.editCards(obj, String.valueOf(selectedCard.getId()));
                                } else {
                                    //Toast.makeText(AddressActivity.this, getString(R.string.internet), Toast.LENGTH_LONG).show();
                                    Utils.displayAlert(getString(R.string.internet), AddressActivity.this);
                                }
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            layoutOK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                            bottomSheetBehavior.setHideable(true);
                            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                            if (viewBottomSheet.getVisibility() == View.VISIBLE) {
                                viewBottomSheet.setVisibility(View.GONE);
                            }
                            viewBack.setVisibility(View.GONE);
                        }
                        loadPreAuth(cardResponseData);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            imgAuthEClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {

                            bottomSheetBehavior.setHideable(true);
                            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                            if (viewBottomSheet.getVisibility() == View.VISIBLE) {
                                viewBottomSheet.setVisibility(View.GONE);
                            }
                            viewBack.setVisibility(View.GONE);
                        }
                        loadPreAuth(cardResponseData);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void initObserver() {
        buyViewModel.getCardResponseMutableLiveData().observe(this, new Observer<CardResponse>() {
            @Override
            public void onChanged(CardResponse cardResponse) {
                try {
                    dialog.dismiss();
                    if (cardResponse != null) {
                        cardResponseData = cardResponse.getData();
                        Error errData = cardResponse.getError();
                        if (errData == null || cardResponse.getStatus().toString().toLowerCase().equals("success")) {
                            if (cardResponseData.getStatus().toLowerCase().contains("authorize") || cardResponseData.getStatus().toLowerCase().contains("approve") || cardResponseData.getStatus().toLowerCase().equals("pending_settlement")) {
                                loadPreAuth(cardResponseData);
                            } else if (cardResponseData.getStatus().toLowerCase().equals("failed") || (cardResponseData.getResponse() != null && cardResponseData.getResponse().toLowerCase().equals("declined"))) {
                                displayAlert_InvalidCard("Card details are invalid, please try with a valid card", AddressActivity.this);
                            } else if (cardResponseData.getStatus().toLowerCase().equals("success") && cardResponseData.getAmount_authorized() == 0 && cardResponseData.getMsg() != null && !cardResponseData.getMsg().equals("")) {
                                Utils.displayAlert(cardResponseData.getMsg(), AddressActivity.this);
                            }
                        } else {
                            Utils.displayAlert(errData.getErrorDescription(), AddressActivity.this);
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        buyViewModel.getEditCardResponseMutableLiveData().observe(this, new Observer<CardEditResponse>() {
            @Override
            public void onChanged(CardEditResponse cardEditResponse) {
                try {
                    dialog.dismiss();
                    if (cardEditResponse != null) {
                        Context context = new ContextThemeWrapper(AddressActivity.this, R.style.Theme_QuickCard);
                        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);

                        builder.setTitle(R.string.app_name);
                        builder.setMessage(cardEditResponse.getData());
                        AlertDialog dialog = builder.show();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    dialog.dismiss();
                                    if (getIntent().getStringExtra("fromProfilePaymentMethods") != null && getIntent().getStringExtra("fromProfilePaymentMethods").equals("fromProfilePaymentMethods")) {
                                        Intent i = new Intent(AddressActivity.this, BuyTokenActivityProfile.class);
                                        i.putExtra("subtype", getIntent().getStringExtra("subtype"));
                                        i.putExtra("type", getIntent().getStringExtra("type"));
                                        startActivity(i);
                                        finish();
                                    } else {
                                        Intent i = new Intent(AddressActivity.this, BuyTokenActivity.class);
                                        i.putExtra("subtype", getIntent().getStringExtra("subtype"));
                                        i.putExtra("type", getIntent().getStringExtra("type"));
                                        startActivity(i);
                                        finish();
                                    }
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            }
                        }, Integer.parseInt(context.getString(R.string.closealert)));
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        buyViewModel.getApiErrorMutableLiveData().observe(this, new Observer<APIError>() {
            @Override
            public void onChanged(APIError apiError) {
                try {
                    dialog.dismiss();
                    if (apiError != null) {
                        if (!apiError.getError().getErrorDescription().equals("")) {
                            if (apiError.getError().getErrorDescription().toLowerCase().contains("token expired") || apiError.getError().getErrorDescription().toLowerCase().contains("invalid token")) {
                                objMyApplication.displayAlert(AddressActivity.this, getString(R.string.session));
                            } else {
                                Utils.displayAlert(apiError.getError().getErrorDescription(), AddressActivity.this);
                            }
                        } else {
                            Utils.displayAlert(apiError.getError().getFieldErrors().get(0), AddressActivity.this);
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        buyViewModel.getPreAuthMutableLiveData().observe(this, new Observer<PreAuthResponse>() {
            @Override
            public void onChanged(PreAuthResponse preAuthResponse) {
                dialog.dismiss();
                PreAuthData objData = preAuthResponse.getData();
                Error errData = preAuthResponse.getError();
                if (errData == null) {
                    if (objData.getStatus().toLowerCase().equals("success")) {
                        Intent i = new Intent(AddressActivity.this, CardSuccessActivity.class);
                        i.putExtra("subtype", getIntent().getStringExtra("subtype"));
                        i.putExtra("type", getIntent().getStringExtra("type"));
                        i.putExtra("fromProfilePaymentMethods", getIntent().getStringExtra("fromProfilePaymentMethods"));
                        startActivity(i);
                        finish();
                    } else {
                        bottomSheetBehavior.setHideable(true);
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                        viewBottomSheet.setVisibility(View.GONE);

                        viewBottomSheet = findViewById(R.id.bottom_sheet_preAuth_error);
                        bottomSheetBehavior = BottomSheetBehavior.from(viewBottomSheet);
                        viewBottomSheet.setVisibility(View.VISIBLE);
                        viewBack.setVisibility(View.VISIBLE);
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                        tvPreAuthFailMessage.setText(objData.getMsg().toString());
                    }
                } else if (errData.getErrorDescription().toString().toLowerCase().equals("card already registered")) {
                    bottomSheetBehavior.setHideable(true);
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                    viewBottomSheet.setVisibility(View.GONE);
                    viewBottomSheet = findViewById(R.id.bottom_sheet_preAuth_error);
                    bottomSheetBehavior = BottomSheetBehavior.from(viewBottomSheet);
                    viewBottomSheet.setVisibility(View.VISIBLE);
                    viewBack.setVisibility(View.VISIBLE);
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    tvPreAuthFailMessage.setText(errData.getErrorDescription().toString());
                } else {
                    bottomSheetBehavior.setHideable(true);
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                    viewBottomSheet.setVisibility(View.GONE);
                    viewBottomSheet = findViewById(R.id.bottom_sheet_preAuth_error);
                    bottomSheetBehavior = BottomSheetBehavior.from(viewBottomSheet);
                    viewBottomSheet.setVisibility(View.VISIBLE);
                    viewBack.setVisibility(View.VISIBLE);
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    tvPreAuthFailMessage.setText(errData.getErrorDescription().toString());
                }
            }
        });
    }

    private Boolean validation() {
        Boolean value = true;
        try {
            if (etAdd1.getText().toString().equals("")) {
                //Toast.makeText(this, "Please enter Address Line 1", Toast.LENGTH_SHORT).show();
                Utils.displayAlert("Address Line 1 required", AddressActivity.this);
                return value = false;
            } else if (etCountry.getText().toString().equals("")) {
                //Toast.makeText(this, "Please select Country", Toast.LENGTH_SHORT).show();
                Utils.displayAlert("Please select Country", AddressActivity.this);
                return value = false;
            } else if (etState.getText().toString().equals("")) {
                //Toast.makeText(this, "Please enter State / Province / Region", Toast.LENGTH_SHORT).show();
                Utils.displayAlert("State is required", AddressActivity.this);
                return value = false;
            } else if (etCity.getText().toString().equals("")) {
                //Toast.makeText(this, "Please enter City", Toast.LENGTH_SHORT).show();
                Utils.displayAlert("City is required", AddressActivity.this);
                return value = false;
            } else if (etZipcode.getText().toString().equals("")) {
                //Toast.makeText(this, "Please enter Zip Code / Postal Code", Toast.LENGTH_SHORT).show();
                Utils.displayAlert("Zip Code / Postal Code is required", AddressActivity.this);
                return value = false;
            } else if (!etZipcode.getText().toString().equals("") && etZipcode.getText().toString().length() < 5) {
                //Toast.makeText(this, "Zip Code / Postal Code must have at least 5 numbers", Toast.LENGTH_SHORT).show();
                Utils.displayAlert("Zip Code / Postal Code must have at least 5 numbers", AddressActivity.this);
                return value = false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return value;
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

    private void bindCountries() {
        RecyclerView rvCountries;
        ImageView imgBack;
        EditText etSearch;
        countriesListAdapter = new CountriesListAdapter(null, AddressActivity.this, "add");
        try {
            imgBack = popupCountries.findViewById(R.id.imgBack);
            etSearch = popupCountries.findViewById(R.id.etSearch);
            listCountries = Utils.getCountries();
            if (listCountries != null && listCountries.size() > 0) {
                rvCountries = popupCountries.findViewById(R.id.rvCountries);
                countriesListAdapter = new CountriesListAdapter(listCountries, AddressActivity.this, "add");
                LinearLayoutManager mLayoutManager = new LinearLayoutManager(AddressActivity.this);
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

    private void bindStates() {
        RecyclerView rvCountries;
        ImageView imgBack;
        EditText etSearch;
        TextView tvNoResults;
        statesListAdapter = new StatesListAdapter(null, AddressActivity.this, "add", null, null);
        try {
            imgBack = popupStates.findViewById(R.id.imgBack);
            etSearch = popupStates.findViewById(R.id.etSearch);
            tvNoResults = popupStates.findViewById(R.id.tvNoResults);
            rvCountries = popupStates.findViewById(R.id.rvCountries);
            listStates = objMyApplication.getListStates();
            if (listStates != null && listStates.size() > 0) {
                statesListAdapter = new StatesListAdapter(listStates, AddressActivity.this, "add", null, null);
                LinearLayoutManager mLayoutManager = new LinearLayoutManager(AddressActivity.this);
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

    public void populateCountry(String strCountry) {
        try {
            popupCountries.dismiss();
            etCountry.setText(strCountry);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void populateState(String strState) {
        try {
            popupStates.dismiss();
            etState.setText(strState);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void loadPreAuth(CardResponseData objData) {
        try {
            TextInputEditText etAmount = (TextInputEditText) findViewById(R.id.etAmount);
            TextView tvMessage = (TextView) findViewById(R.id.tvMessage);
            LinearLayout layoutVerify = (LinearLayout) findViewById(R.id.layoutVerify);
            ImageView imgAuthClose = (ImageView) findViewById(R.id.imgAuthClose);
            strMessageForDescriptor = "A temporary hold was placed on your card and will be removed by end of this verification process. Please check your card statement for a change from ( Descriptor Name ) and enter the amount below.";
            if (!(objData.getDescriptorName() == null)) {
                tvMessage.setText(strMessageForDescriptor.replace("( Descriptor Name )", objData.getDescriptorName()));
            }
            viewBottomSheet = findViewById(R.id.bottom_sheet_preAuth);
            bottomSheetBehavior = BottomSheetBehavior.from(viewBottomSheet);
            viewBottomSheet.setVisibility(View.VISIBLE);
            viewBack.setVisibility(View.VISIBLE);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            imgAuthClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                        bottomSheetBehavior.setHideable(true);
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                        if (viewBottomSheet.getVisibility() == View.VISIBLE) {
                            viewBottomSheet.setVisibility(View.GONE);
                        }
                        viewBack.setVisibility(View.GONE);
                    }
                }
            });
            layoutVerify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Utils.hideKeypad(AddressActivity.this, v);
                        if (!etAmount.getText().toString().trim().equals("")) {
                            PreAuthRequest objRequest = new PreAuthRequest();
                            objRequest.setAmount(etAmount.getText().toString());
                            objRequest.setTransactionId(objData.getTransactionId());

                            objRequest.setAddressLine1(etAdd1.getText().toString().trim());
                            objRequest.setAddressLine2(etAdd2.getText().toString().trim());
                            objRequest.setCity(etCity.getText().toString().trim());
                            objRequest.setCountry("US");
                            objRequest.setZipCode(etZipcode.getText().toString().trim());
                            objRequest.setName(strName);
                            objRequest.setState(etState.getText().toString().trim());

                            dialog = new ProgressDialog(AddressActivity.this, R.style.MyAlertDialogStyle);
                            dialog.setIndeterminate(false);
                            dialog.setMessage("Please wait...");
                            dialog.getWindow().setGravity(Gravity.CENTER);
                            dialog.show();

                            if (Utils.checkInternet(AddressActivity.this)) {
                                buyViewModel.preAuthCards(objRequest);
                            } else {
                                Utils.displayAlert(getString(R.string.internet), AddressActivity.this);
                            }
                        } else {
                            Utils.displayAlert("Please enter Amount", AddressActivity.this);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
        } catch (
                Exception ex) {
            ex.printStackTrace();
        }
    }

    private void displayAlert_InvalidCard(String msg, Activity activity) {
        try {
            Context context = new ContextThemeWrapper(activity, R.style.Theme_QuickCard);
            new MaterialAlertDialogBuilder(context)
                    .setTitle(R.string.app_name)
                    .setMessage(msg)
                    .setPositiveButton("OK", (dialog, which) -> {
                        dialog.dismiss();
//                        Intent i = new Intent(this, AddCardActivity.class);
//                        i.putExtra("name", strName);
//                        i.putExtra("number", getIntent().getStringExtra("number2"));
//                        i.putExtra("expiry", strExpiry);
//                        i.putExtra("cvv", strCVV);
//                        i.putExtra("from", "InvalidCard");
//                        startActivity(i);
//                        finish();

                        Intent i = new Intent();
                        i.putExtra("name", strName);
                        i.putExtra("number", getIntent().getStringExtra("number2"));
                        i.putExtra("expiry", strExpiry);
                        i.putExtra("cvv", strCVV);
                        i.putExtra("from", "InvalidCard");
                        setResult(RESULT_OK, i);
                        finish();
                    }).show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}