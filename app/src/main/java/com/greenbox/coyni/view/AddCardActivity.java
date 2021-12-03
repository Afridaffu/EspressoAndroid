package com.greenbox.coyni.view;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.greenbox.coyni.R;
import com.greenbox.coyni.model.APIError;
import com.greenbox.coyni.model.publickey.PublicKeyResponse;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.viewmodel.PaymentMethodsViewModel;

public class AddCardActivity extends AppCompatActivity {
    String strPublicKey = "";
    PaymentMethodsViewModel paymentMethodsViewModel;
    MyApplication objMyApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_addnewdebit_card);
            initialization();
            initObserver();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void initialization() {
        try {
            objMyApplication = (MyApplication) getApplicationContext();
            paymentMethodsViewModel = new ViewModelProvider(this).get(PaymentMethodsViewModel.class);
            paymentMethodsViewModel.getPublicKey(objMyApplication.getUserId());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void initObserver() {
        paymentMethodsViewModel.getPublicKeyResponseMutableLiveData().observe(this, new Observer<PublicKeyResponse>() {
            @Override
            public void onChanged(PublicKeyResponse publicKeyResponse) {
                if (publicKeyResponse != null) {
                    strPublicKey = publicKeyResponse.getData().getPublicKey();
                }
            }
        });

        paymentMethodsViewModel.getApiErrorMutableLiveData().observe(AddCardActivity.this, new Observer<APIError>() {
            @Override
            public void onChanged(APIError apiError) {
                try {
                    if (apiError != null) {
                        if (!apiError.getError().getErrorDescription().equals("")) {
                            Utils.displayAlert(apiError.getError().getErrorDescription(), AddCardActivity.this, "");
                        } else {
                            Utils.displayAlert(apiError.getError().getFieldErrors().get(0), AddCardActivity.this, "");
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }
}