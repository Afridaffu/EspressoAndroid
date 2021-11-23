package com.greenbox.coyni.view;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.greenbox.coyni.R;
import com.greenbox.coyni.adapters.StatesListAdapter;
import com.greenbox.coyni.model.States;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class EditAddressActivity extends AppCompatActivity {

    TextInputEditText address1ET, address2ET,cityET,stateET,zipcodeET;
    TextInputLayout address1TIL, address2TIL,cityTIL,stateTIL,zipcodeTIL;
    ConstraintLayout stateCL;
    MyApplication myApplicationObj;
    CardView editAddressSaveCV;
    boolean isAddress1, isAddress2,isCity,isState,isZipcode,isSaveEnabled;
    Dialog popupStates;
    StatesListAdapter statesListAdapter;
    List<States> listStates = new ArrayList<>();
    LinearLayout backIV,address1ErrorLL,address2ErrorLL,cityErrorLL,zipcodeErrorLL;
    TextView address1ErrorTV,address2ErrorTV,cityErrorTV,zipcodeErrorTV;
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initfields(){

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

            editAddressSaveCV = findViewById(R.id.editAddressSaveCV);

            editAddressSaveCV.setOnClickListener(view -> {
                if(isSaveEnabled){

                }
            });

            stateCL.setOnClickListener(view -> statesPopup());

            stateTIL.setOnClickListener(view -> statesPopup());

            backIV.setOnClickListener(view -> finish());

            if(myApplicationObj.getMyProfile().getData().getFirstName()!=null){
                if(myApplicationObj.getMyProfile().getData().getAddressLine1()!=null
                        && !myApplicationObj.getMyProfile().getData().getAddressLine1().equals("")){
                    address1ET.setText( myApplicationObj.getMyProfile().getData().getAddressLine1());
                }
                if(myApplicationObj.getMyProfile().getData().getAddressLine2()!=null
                        && !myApplicationObj.getMyProfile().getData().getAddressLine2().equals("")){
                    address2ET.setText( myApplicationObj.getMyProfile().getData().getAddressLine2());
                }
                if(myApplicationObj.getMyProfile().getData().getCity()!=null
                        && !myApplicationObj.getMyProfile().getData().getCity().equals("")){
                    cityET.setText( myApplicationObj.getMyProfile().getData().getCity());
                }
                if(myApplicationObj.getMyProfile().getData().getState()!=null
                        && !myApplicationObj.getMyProfile().getData().getState().equals("")){
                    stateET.setText( myApplicationObj.getMyProfile().getData().getState());
                }
                if(myApplicationObj.getMyProfile().getData().getZipCode()!=null
                        && !myApplicationObj.getMyProfile().getData().getZipCode().equals("")){
                    zipcodeET.setText( myApplicationObj.getMyProfile().getData().getZipCode());
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void textWatchers(){
        address1ET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length() > 0){
                    isAddress1 = true;
                    address1ErrorLL.setVisibility(GONE);
                    address1TIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                    Utils.setUpperHintColor(address1TIL,getResources().getColor(R.color.primary_green));
                }else{
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
                    if (str.length() > 0 && str.substring(0).equals(" ") || (str.length() > 0 && str.contains(" "))) {
                        address1ET.setText(address1ET.getText().toString().replaceAll(" ", ""));
                        address1ET.setSelection(address1ET.getText().length());
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
                if(charSequence.length() > 0){
                    isAddress2 = true;
                    address2ErrorLL.setVisibility(GONE);
                    address2TIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                    Utils.setUpperHintColor(address2TIL,getResources().getColor(R.color.primary_green));
                }else{
                    address2ErrorLL.setVisibility(VISIBLE);
                    address2ErrorTV.setText("Field Required");
                    isAddress2 = false;
                }
                enableOrDisableSave();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        cityET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length() > 0){
                    isCity = true;
                    cityErrorLL.setVisibility(GONE);
                    cityTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                    Utils.setUpperHintColor(cityTIL,getResources().getColor(R.color.primary_green));
                }else{
                    cityErrorLL.setVisibility(VISIBLE);
                    cityErrorTV.setText("Field Required");
                    isCity = false;
                }
                enableOrDisableSave();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        stateET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length() > 0){
                    isState = true;
                }else{
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
                if(charSequence.length() > 0){
                    isZipcode = true;
                    zipcodeErrorLL.setVisibility(GONE);
                    zipcodeTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                    Utils.setUpperHintColor(zipcodeTIL,getResources().getColor(R.color.primary_green));
                }else{
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

    public void enableOrDisableSave() {
        try {
            if (isAddress1 && isAddress2 && isCity && isState && isZipcode) {
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
}