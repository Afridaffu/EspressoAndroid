package com.coyni.android.view;

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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.coyni.android.adapters.StatesListAdapter;
import com.coyni.android.model.States;
import com.coyni.android.model.cards.CardEditRequest;
import com.coyni.android.model.cards.CardEditResponse;
import com.coyni.android.model.cards.CardResponse;
import com.coyni.android.model.cards.CardResponseData;
import com.coyni.android.model.cards.CardType;
import com.coyni.android.model.cards.CardTypeRequest;
import com.coyni.android.model.cards.CardsDataItem;
import com.coyni.android.model.preauth.PreAuthRequest;
import com.coyni.android.model.preauth.PreAuthResponse;
import com.coyni.android.utils.MyApplication;
import com.coyni.android.utils.Utils;
import com.coyni.android.viewmodel.BuyViewModel;
import com.coyni.android.viewmodel.DashboardViewModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.coyni.android.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AddEditCardDetailsActivity extends AppCompatActivity {
    MyApplication objMyApplication;
    TextInputEditText etName, etNumber, etExpiry, etCVV;
    TextInputEditText etAdd1, etAdd2, etCountry, etState, etCity, etZipcode;
    CardsDataItem selectedCard;
    ImageView imgCardType;
    BuyViewModel buyViewModel;
    String strTime = "";
    CardType objCard;
    DashboardViewModel dashboardViewModel;
    BottomSheetBehavior bottomSheetBehavior;
    View viewBottomSheet, viewBack;
    ProgressDialog dialog;
    Dialog popupStates;
    StatesListAdapter statesListAdapter;
    List<States> listStates = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_add_edit_card);
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
            objMyApplication.userInactive(AddEditCardDetailsActivity.this, this, false);
            objMyApplication.getAppHandler().removeCallbacks(objMyApplication.getAppRunnable());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        objMyApplication.getHandler().removeCallbacks(objMyApplication.getMyRunnable());
        objMyApplication.userInactive(AddEditCardDetailsActivity.this, this, true);
    }

    @Override
    public void onUserInteraction() {
        // TODO Auto-generated method stub
        super.onUserInteraction();
        objMyApplication.getHandler().removeCallbacks(objMyApplication.getMyRunnable());
        objMyApplication.userInactive(AddEditCardDetailsActivity.this, this, false);
    }

    private void initialization() {
        try {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_sent);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            Utils.statusBar(AddEditCardDetailsActivity.this);
            objMyApplication = (MyApplication) getApplicationContext();
            buyViewModel = new ViewModelProvider(this).get(BuyViewModel.class);
            dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
            etName = (TextInputEditText) findViewById(R.id.etName);
            etNumber = (TextInputEditText) findViewById(R.id.etNumber);
            etExpiry = (TextInputEditText) findViewById(R.id.etExpiry);
            etCVV = (TextInputEditText) findViewById(R.id.etCVV);
            etAdd1 = (TextInputEditText) findViewById(R.id.etAdd1);
            etAdd2 = (TextInputEditText) findViewById(R.id.etAdd2);
            etCountry = (TextInputEditText) findViewById(R.id.etCountry);
            etState = (TextInputEditText) findViewById(R.id.etState);
            etCity = (TextInputEditText) findViewById(R.id.etCity);
            etZipcode = (TextInputEditText) findViewById(R.id.etZipcode);
            imgCardType = (ImageView) findViewById(R.id.imgCardType);
            //cvScan = (CardView) findViewById(R.id.cvScan);
            LinearLayout layoutProceed = (LinearLayout) findViewById(R.id.layoutProceed);
            LinearLayout layoutStateArrow = (LinearLayout) findViewById(R.id.layoutStateArrow);
            etName.setText(Utils.capitalize(objMyApplication.getStrUser()));
            etName.setEnabled(false);
            if (objMyApplication.getSelectedCard() != null && getIntent().getStringExtra("from") == null) {
                selectedCard = objMyApplication.getSelectedCard();
//                etName.setText(Utils.capitalize(selectedCard.getName()));
                etNumber.setText("**** **** **** " + selectedCard.getLastFour());
                etExpiry.setText(selectedCard.getExpiryDate());
                imgCardType.setVisibility(View.VISIBLE);
                etAdd1.setText(selectedCard.getAddressLine1());
                etAdd2.setText(selectedCard.getAddressLine2());
                // etCountry.setText(selectedCard.getCountry());
                etState.setText(selectedCard.getState());
                etCity.setText(selectedCard.getCity());
                etZipcode.setText(selectedCard.getZipCode());

                if (selectedCard.getCardBrand().toLowerCase().equals("visa")) {
                    imgCardType.setImageResource(R.drawable.ic_visa);
                } else if (selectedCard.getCardBrand().toLowerCase().contains("master")) {
                    imgCardType.setImageResource(R.drawable.ic_master);
                } else if (selectedCard.getCardBrand().toLowerCase().contains("american")) {
                    imgCardType.setImageResource(R.drawable.ic_amex);
                } else if (selectedCard.getCardBrand().toLowerCase().contains("discover")) {
                    imgCardType.setImageResource(R.drawable.ic_discover);
                }
                etName.setEnabled(false);
                etNumber.setEnabled(false);
                etCVV.setVisibility(View.GONE);
                //cvScan.setVisibility(View.GONE);
            }
            etExpiry.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        etExpiry.setHint("MM/YY");
                    } else {
                        etExpiry.setHint("");
                    }
                }
            });

            etExpiry.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() == 2) {
                        if (start == 2 && before == 1 && !s.toString().contains("/")) {
                            etExpiry.setText("" + s.toString().charAt(0));
                            etExpiry.setSelection(1);
                        } else {
                            etExpiry.setText(s + "/");
                            etExpiry.setSelection(3);
                        }
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            etNumber.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if ((s.length() == 4 && start == 3) || (s.length() == 9 && start == 8) || (s.length() == 14 && start == 13)) {
                        etNumber.setText(s + " ");
                        etNumber.setSelection(s.length() + 1);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                    try {
                        if (s.length() == 7) {
                            CardTypeRequest request = new CardTypeRequest();
                            request.setCardNumber(s.toString().replace(" ", ""));
                            dashboardViewModel.cardType(request);
                        } else if (s.length() == 0) {
                            imgCardType.setVisibility(View.GONE);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            layoutProceed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (validation()) {
                        dialog = new ProgressDialog(AddEditCardDetailsActivity.this, R.style.MyAlertDialogStyle);
                        dialog.setIndeterminate(false);
                        dialog.setMessage("Please wait...");
                        dialog.getWindow().setGravity(Gravity.CENTER);
                        dialog.show();
                        CardEditRequest obj = new CardEditRequest();
                        obj.setAddressLine1(etAdd1.getText().toString().trim());
                        obj.setAddressLine2(etAdd2.getText().toString().trim());
//                        obj.setCountry(etCountry.getText().toString().trim());
                        obj.setCountry("US");
                        obj.setState(etState.getText().toString().trim());
                        obj.setCity(etCity.getText().toString().trim());
                        obj.setZipCode(etZipcode.getText().toString().trim());
                        obj.setDefaultForAllWithDrawals(true);
                        obj.setName(etName.getText().toString());
                        obj.setExpiryDate(etExpiry.getText().toString());
                        if (Utils.checkInternet(AddEditCardDetailsActivity.this)) {
                            buyViewModel.editCards(obj, String.valueOf(selectedCard.getId()));
                        } else {
                            Utils.displayAlert(getString(R.string.internet), AddEditCardDetailsActivity.this);
                        }
                    }
                }
            });

            layoutStateArrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    statesPopup();
                }
            });

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void initObserver() {
        dashboardViewModel.getCardTypeMutableLiveData().observe(this, new Observer<CardType>() {
            @Override
            public void onChanged(CardType cardType) {
                if (cardType != null) {
                    objCard = cardType;
                    if (!cardType.getData().getCardBrand().equals("")) {
                        if (cardType.getData().getCardBrand().toLowerCase().equals("visa")) {
                            imgCardType.setVisibility(View.VISIBLE);
                            imgCardType.setImageResource(R.drawable.ic_visa);
                        } else if (cardType.getData().getCardBrand().toLowerCase().contains("master")) {
                            imgCardType.setVisibility(View.VISIBLE);
                            imgCardType.setImageResource(R.drawable.ic_master);
                        } else if (cardType.getData().getCardBrand().toLowerCase().contains("american")) {
                            imgCardType.setVisibility(View.VISIBLE);
                            imgCardType.setImageResource(R.drawable.ic_amex);
                        } else if (cardType.getData().getCardBrand().toLowerCase().contains("discover")) {
                            imgCardType.setVisibility(View.VISIBLE);
                            imgCardType.setImageResource(R.drawable.ic_discover);
                        }
                    }
                }
            }
        });

        buyViewModel.getCardResponseMutableLiveData().observe(this, new Observer<CardResponse>() {
            @Override
            public void onChanged(CardResponse cardResponse) {
                dialog.dismiss();
                if (cardResponse != null) {
//                    Intent i = new Intent(AddressActivity.this, BuyTokenActivity.class);
//                    i.putExtra("subtype", getIntent().getStringExtra("subtype"));
//                    i.putExtra("type", getIntent().getStringExtra("type"));
//                    finish();
//                    startActivity(i);
                    CardResponseData objData = cardResponse.getData();
                    if (objData.getStatus().toLowerCase().equals("authorized") && objData.getResponse().toLowerCase().contains("approved")) {
                        loadPreAuth(objData);
                    } else {
                        Utils.displayAlert("Test card added successfully, PreAuth is not required for this test card.", AddEditCardDetailsActivity.this);
                    }
                }
            }
        });

        buyViewModel.getEditCardResponseMutableLiveData().observe(this, new Observer<CardEditResponse>() {
            @Override
            public void onChanged(CardEditResponse cardEditResponse) {
                dialog.dismiss();
                if (cardEditResponse != null) {
//                    Utils.displayAlert(cardEditResponse.getData(), AddEditCardDetailsActivity.this);
//                    if (getIntent().getStringExtra("fromProfilePaymentMethods") != null && getIntent().getStringExtra("fromProfilePaymentMethods").equals("fromProfilePaymentMethods")) {
//                        Intent i = new Intent(AddEditCardDetailsActivity.this, BuyTokenActivityProfile.class);
//                        i.putExtra("subtype", getIntent().getStringExtra("subtype"));
//                        i.putExtra("type", getIntent().getStringExtra("type"));
//                        finish();
//                        startActivity(i);
//                    } else {
//                        Intent i = new Intent(AddEditCardDetailsActivity.this, BuyTokenActivity.class);
//                        i.putExtra("subtype", getIntent().getStringExtra("subtype"));
//                        i.putExtra("type", getIntent().getStringExtra("type"));
//                        finish();
//                        startActivity(i);
//                    }

                    Context context = new ContextThemeWrapper(AddEditCardDetailsActivity.this, R.style.Theme_QuickCard);
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
                                    Intent i = new Intent(AddEditCardDetailsActivity.this, BuyTokenActivityProfile.class);
                                    i.putExtra("subtype", getIntent().getStringExtra("subtype"));
                                    i.putExtra("type", getIntent().getStringExtra("type"));
                                    finish();
                                    startActivity(i);
                                } else {
                                    Intent i = new Intent(AddEditCardDetailsActivity.this, BuyTokenActivity.class);
                                    i.putExtra("subtype", getIntent().getStringExtra("subtype"));
                                    i.putExtra("type", getIntent().getStringExtra("type"));
                                    finish();
                                    startActivity(i);
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    }, Integer.parseInt(context.getString(R.string.closealert)));

                }
            }
        });
    }

    private Boolean validation() {
        Boolean value = true;
        try {
            if (etName.getText().toString().equals("")) {
                Utils.displayAlert("Card holder Name is required", AddEditCardDetailsActivity.this);
                return value = false;
            } else if (etNumber.getText().toString().equals("")) {
                etNumber.requestFocus();
                Utils.displayAlert("Card Number is required", AddEditCardDetailsActivity.this);
                return value = false;
            } else if (!selectedCard.getCardBrand().contains("american") && getIntent().getStringExtra("from") != null && !etNumber.getText().toString().equals("") && etNumber.getText().toString().length() < 19) {
                etNumber.requestFocus();
                Utils.displayAlert("Card Number should be 16 digits", AddEditCardDetailsActivity.this);
                return value = false;
            } else if (selectedCard.getCardBrand().toLowerCase().contains("american") && getIntent().getStringExtra("from") != null && !etNumber.getText().toString().equals("") && etNumber.getText().toString().length() != 18) {
                etNumber.requestFocus();
                Utils.displayAlert("Card Number should be 15 digits", AddEditCardDetailsActivity.this);
                return value = false;
            } else if (etExpiry.getText().toString().equals("")) {
                etExpiry.requestFocus();
                Utils.displayAlert("Expiry Date is required", AddEditCardDetailsActivity.this);
                return value = false;
            } else if (!etExpiry.getText().toString().matches("(?:0[1-9]|1[0-2])/[0-9]{2}")) {
                etExpiry.requestFocus();
                Utils.displayAlert("Please enter valid Expiry Date", AddEditCardDetailsActivity.this);
                return value = false;
            } else if (!validateExpiry()) {
                etExpiry.requestFocus();
                Utils.displayAlert("Please enter valid Expiry Date", AddEditCardDetailsActivity.this);
                return value = false;
            } else if (!selectedCard.getCardBrand().toLowerCase().equals("visa") && !selectedCard.getCardBrand().toLowerCase().contains("master") && !selectedCard.getCardBrand().toLowerCase().contains("american") && !selectedCard.getCardBrand().toLowerCase().contains("discover")) {
                Utils.displayAlert("GreenBox system supports only MASTERCARD, VISA, AMERICAN EXPRESS and DISCOVER", AddEditCardDetailsActivity.this);
                return value = false;
            } else if (getIntent().getStringExtra("from") != null && !etCVV.getText().toString().equals("") && etCVV.getText().toString().length() < 4) {
                etCVV.requestFocus();
                Utils.displayAlert("Please enter valid CVV/CVC.", AddEditCardDetailsActivity.this);
                return value = false;
            } else if (etAdd1.getText().toString().equals("")) {
                Utils.displayAlert("Address Line 1 required", AddEditCardDetailsActivity.this);
                return value = false;
            } else if (etCountry.getText().toString().equals("")) {
                Utils.displayAlert("Please select Country", AddEditCardDetailsActivity.this);
                return value = false;
            } else if (etState.getText().toString().equals("")) {
                Utils.displayAlert("State is required", AddEditCardDetailsActivity.this);
                return value = false;
            } else if (etCity.getText().toString().equals("")) {
                Utils.displayAlert("City is required", AddEditCardDetailsActivity.this);
                return value = false;
            } else if (etZipcode.getText().toString().equals("")) {
                Utils.displayAlert("Zip Code / Postal Code is required", AddEditCardDetailsActivity.this);
                return value = false;
            } else if (!etZipcode.getText().toString().equals("") && etZipcode.getText().toString().length() < 5) {
                Utils.displayAlert("Zip Code / Postal Code must have at least 5 numbers", AddEditCardDetailsActivity.this);
                return value = false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return value;
    }

    private void loadPreAuth(CardResponseData objData) {
        try {
            TextInputEditText etAmount = (TextInputEditText) findViewById(R.id.etAmount);
            LinearLayout layoutVerify = (LinearLayout) findViewById(R.id.layoutVerify);
            ImageView imgAuthClose = (ImageView) findViewById(R.id.imgAuthClose);
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
                    if (!etAmount.getText().toString().trim().equals("")) {
                        PreAuthRequest objRequest = new PreAuthRequest();
                        objRequest.setAmount(etAmount.getText().toString());
                        objRequest.setCardNumber(objData.getCardNumber());
                        objRequest.setTransactionId(objData.getTransactionId());

                        dialog = new ProgressDialog(AddEditCardDetailsActivity.this, R.style.MyAlertDialogStyle);
                        dialog.setIndeterminate(false);
                        dialog.setMessage("Please wait...");
                        dialog.getWindow().setGravity(Gravity.CENTER);
                        dialog.show();

                        if (Utils.checkInternet(AddEditCardDetailsActivity.this)) {
                            buyViewModel.preAuthCards(objRequest);
                            preAuthVerification();
                        } else {
//                            Toast.makeText(AddEditCardDetailsActivity.this, getString(R.string.internet), Toast.LENGTH_LONG).show();
                            Utils.displayAlert(getString(R.string.internet), AddEditCardDetailsActivity.this);
                        }
                    } else {
//                        Toast.makeText(AddEditCardDetailsActivity.this, "Please enter Amount", Toast.LENGTH_SHORT).show();
                        Utils.displayAlert("Please enter Amount", AddEditCardDetailsActivity.this);
                    }
                }
            });
        } catch (
                Exception ex) {
            ex.printStackTrace();
        }
    }

    private Boolean validateExpiry() {
        Boolean value = true;
        try {
            Calendar cal = Calendar.getInstance();
            int month = cal.get(Calendar.MONTH) + 1;
            String year = "";
            SimpleDateFormat ydf = new SimpleDateFormat("yy");
            year = ydf.format(Calendar.getInstance().getTime());
            if (Integer.parseInt(etExpiry.getText().toString().split("/")[1]) < Integer.parseInt(year)) {
                value = false;
            } else if (Integer.parseInt(etExpiry.getText().toString().split("/")[1]) <= Integer.parseInt(year) && Integer.parseInt(etExpiry.getText().toString().split("/")[0]) < month) {
                value = false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return value;
    }

    private void preAuthVerification() {
        buyViewModel.getPreAuthMutableLiveData().observe(this, new Observer<PreAuthResponse>() {
            @Override
            public void onChanged(PreAuthResponse preAuthResponse) {
                dialog.dismiss();
                if (preAuthResponse != null) {

                    if (preAuthResponse.getData().getStatus().equals("success")) {
                        Intent i = new Intent(AddEditCardDetailsActivity.this, CardSuccessActivity.class);
                        finish();
                        startActivity(i);
                    }

                    /*Intent i = new Intent(AddressActivity.this, BuyTokenActivity.class);
                    i.putExtra("subtype", getIntent().getStringExtra("subtype"));
                    i.putExtra("type", getIntent().getStringExtra("type"));
                    finish();
                    startActivity(i);*/
                }
            }
        });
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
        statesListAdapter = new StatesListAdapter(null, AddEditCardDetailsActivity.this, "edit", null, null);
        try {
            imgBack = popupStates.findViewById(R.id.imgBack);
            etSearch = popupStates.findViewById(R.id.etSearch);
            tvNoResults = popupStates.findViewById(R.id.tvNoResults);
            rvCountries = popupStates.findViewById(R.id.rvCountries);
            listStates = objMyApplication.getListStates();
            if (listStates != null && listStates.size() > 0) {
                statesListAdapter = new StatesListAdapter(listStates, AddEditCardDetailsActivity.this, "edit", null, null);
                LinearLayoutManager mLayoutManager = new LinearLayoutManager(AddEditCardDetailsActivity.this);
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
            etState.setText(strState);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}