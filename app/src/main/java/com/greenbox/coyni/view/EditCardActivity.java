package com.greenbox.coyni.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.greenbox.coyni.R;
import com.greenbox.coyni.model.APIError;
import com.greenbox.coyni.model.cards.CardEditRequest;
import com.greenbox.coyni.model.cards.CardEditResponse;
import com.greenbox.coyni.model.paymentmethods.PaymentsList;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.utils.outline_et.CardNumberEditText;
import com.greenbox.coyni.viewmodel.PaymentMethodsViewModel;
import com.santalu.maskara.widget.MaskEditText;

public class EditCardActivity extends AppCompatActivity {
    PaymentsList selectedCard;
    MyApplication objMyApplication;
    TextInputEditText etName, etAddress1, etAddress2, etCity, etState, etZipcode, etCountry;
    CardNumberEditText etlCard;
    MaskEditText etExpiry;
    PaymentMethodsViewModel paymentMethodsViewModel;
    CardView cvSave, cvRemove;
    ProgressDialog dialog;
    TextView tvCard;
    Boolean isAddress1 = false, isCity = false, isState = false, isZipcode = false, isAddEnabled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_edit_card);
            initialization();
            initObserver();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void initialization() {
        try {
            objMyApplication = (MyApplication) getApplicationContext();
            selectedCard = objMyApplication.getSelectedCard();
            etName = findViewById(R.id.etName);
            etlCard = findViewById(R.id.etlCard);
            etExpiry = findViewById(R.id.etExpiry);
            etAddress1 = findViewById(R.id.etAddress1);
            etAddress2 = findViewById(R.id.etAddress2);
            etCity = findViewById(R.id.etCity);
            etState = findViewById(R.id.etState);
            etZipcode = findViewById(R.id.etZipcode);
            etCountry = findViewById(R.id.etCountry);
            tvCard = findViewById(R.id.tvCard);
            cvSave = findViewById(R.id.cvSave);
            cvRemove = findViewById(R.id.cvRemove);
            paymentMethodsViewModel = new ViewModelProvider(this).get(PaymentMethodsViewModel.class);
            etName.setEnabled(false);
            etExpiry.setEnabled(false);
            etlCard.disableEditText();
            if (selectedCard != null) {
                etName.setText(Utils.capitalize(selectedCard.getName()));
                etlCard.setText(selectedCard.getFirstSix() + " ****" + selectedCard.getLastFour());
                etExpiry.setText(selectedCard.getExpiryDate());
                etAddress1.setText(selectedCard.getAddressLine1());
                etAddress2.setText(selectedCard.getAddressLine2());
                etCity.setText(selectedCard.getCity());
                etState.setText(selectedCard.getState());
                etZipcode.setText(selectedCard.getZipCode());
                etlCard.setImage(selectedCard.getCardBrand());
                etlCard.hideCamera();
                switch (selectedCard.getCardBrand().toUpperCase().replace(" ", "")) {
                    case "VISA":
                        tvCard.setText(Utils.capitalize(selectedCard.getCardBrand() + " " + selectedCard.getCardType() + " ****" + selectedCard.getLastFour()));
                        break;
                    case "MASTERCARD":
                        tvCard.setText(Utils.capitalize(selectedCard.getCardBrand() + " " + selectedCard.getCardType() + " ****" + selectedCard.getLastFour()));
                        break;
                    case "AMERICANEXPRESS":
                        tvCard.setText(Utils.capitalize("American Express Card ****" + selectedCard.getLastFour()));
                        break;
                    case "DISCOVER":
                        tvCard.setText(Utils.capitalize("Discover Card ****" + selectedCard.getLastFour()));
                        break;
                }
            }

            cvRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(EditCardActivity.this, PaymentMethodsActivity.class);
                    i.putExtra("screen", "editcard");
                    i.putExtra("action", "remove");
                    startActivity(i);
                    finish();
                }
            });

            cvSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        CardEditRequest request = new CardEditRequest();
                        request.setAddressLine1(etAddress1.getText().toString().trim());
                        request.setAddressLine2(etAddress2.getText().toString().trim());
                        request.setName(etName.getText().toString().trim());
                        request.setExpiryDate(etExpiry.getText().toString().trim());
                        request.setCity(etCity.getText().toString().trim());
                        request.setState(etState.getText().toString().trim());
                        if (etCountry.getText().toString().trim().equals("United States")) {
                            request.setCountry("US");
                        } else {
                            request.setCountry(Utils.getStrCCode());
                        }
                        dialog = Utils.showProgressDialog(EditCardActivity.this);
                        paymentMethodsViewModel.editCards(request, selectedCard.getId());
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
        paymentMethodsViewModel.getCardEditResponseMutableLiveData().observe(this, new Observer<CardEditResponse>() {
            @Override
            public void onChanged(CardEditResponse cardEditResponse) {
                dialog.dismiss();
                if (cardEditResponse != null) {
                    if (cardEditResponse.getStatus().toLowerCase().equals("success")) {
                        displayAlertNew(cardEditResponse.getData(), EditCardActivity.this, "");
                    }
                }
            }
        });

        paymentMethodsViewModel.getApiErrorMutableLiveData().observe(this, new Observer<APIError>() {
            @Override
            public void onChanged(APIError apiError) {
                try {
                    dialog.dismiss();
                    if (apiError != null) {
                        if (!apiError.getError().getErrorDescription().equals("")) {
                            Utils.displayAlert(apiError.getError().getErrorDescription(), EditCardActivity.this, "");
                        } else {
                            Utils.displayAlert(apiError.getError().getFieldErrors().get(0), EditCardActivity.this, "");
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    private void displayAlertNew(String msg, final Context context, String headerText) {
        try {
            // custom dialog
            final Dialog dialog = new Dialog(context);
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.bottom_sheet_alert_dialog);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            DisplayMetrics mertics = context.getResources().getDisplayMetrics();
            int width = mertics.widthPixels;

            TextView header = dialog.findViewById(R.id.tvHead);
            TextView message = dialog.findViewById(R.id.tvMessage);
            CardView actionCV = dialog.findViewById(R.id.cvAction);
            TextView actionText = dialog.findViewById(R.id.tvAction);

            if (!headerText.equals("")) {
                header.setVisibility(View.VISIBLE);
                header.setText(headerText);
            }

            actionCV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    objMyApplication.setSelectedCard(null);
                    onBackPressed();
                    finish();
                }
            });

            message.setText(msg);
            Window window = dialog.getWindow();
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

            WindowManager.LayoutParams wlp = window.getAttributes();

            wlp.gravity = Gravity.BOTTOM;
            wlp.flags &= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            window.setAttributes(wlp);

            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

            dialog.setCanceledOnTouchOutside(true);
            dialog.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}