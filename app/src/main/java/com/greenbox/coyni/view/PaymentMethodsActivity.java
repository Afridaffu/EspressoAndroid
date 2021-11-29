package com.greenbox.coyni.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.greenbox.coyni.R;
import com.greenbox.coyni.model.paymentmethods.PaymentMethodsResponse;
import com.greenbox.coyni.utils.MyApplication;

public class PaymentMethodsActivity extends AppCompatActivity {
    MyApplication objMyApplication;
    PaymentMethodsResponse paymentMethodsResponse;
    LinearLayout lyAPayClose, lyExternal, lyExternalClose;
    CardView cvNext;
    TextView tvBankError, tvDCardError, tvCCardError;
    String strCurrent = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_payment_methods);
            initialization();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void initialization() {
        try {
            objMyApplication = (MyApplication) getApplicationContext();
            paymentMethodsResponse = objMyApplication.getPaymentMethodsResponse();
            if (paymentMethodsResponse.getData().getData() != null && paymentMethodsResponse.getData().getData().size() > 0) {
                ControlMethod("paymentMethods");
                strCurrent = "paymentMethods";
            } else {
                ControlMethod("addpayment");
                strCurrent = "addpayment";
            }
            addPayment();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void addPayment() {
        try {
            lyAPayClose = findViewById(R.id.lyAPayClose);
            tvBankError = findViewById(R.id.tvBankError);
            tvDCardError = findViewById(R.id.tvDCardError);
            tvCCardError = findViewById(R.id.tvCCardError);
            lyExternal = findViewById(R.id.lyExternal);
            lyExternalClose = findViewById(R.id.lyExternalClose);
            cvNext = findViewById(R.id.cvNext);
            lyAPayClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
            lyExternal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ControlMethod("externalBank");
                    strCurrent = "externalBank";
                }
            });
            lyExternalClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ControlMethod("addpayment");
                    strCurrent = "addpayment";
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void ControlMethod(String methodToShow) {
        try {
            switch (methodToShow) {
                case "addpayment": {
                    findViewById(R.id.addpayment).setVisibility(View.VISIBLE);
                    findViewById(R.id.paymentMethods).setVisibility(View.GONE);
                    findViewById(R.id.externalBank).setVisibility(View.GONE);
                }
                break;
                case "paymentMethods": {
                    findViewById(R.id.addpayment).setVisibility(View.GONE);
                    findViewById(R.id.externalBank).setVisibility(View.GONE);
                    findViewById(R.id.paymentMethods).setVisibility(View.VISIBLE);
                }
                break;
                case "externalBank": {
                    findViewById(R.id.addpayment).setVisibility(View.GONE);
                    findViewById(R.id.paymentMethods).setVisibility(View.GONE);
                    findViewById(R.id.externalBank).setVisibility(View.VISIBLE);
                }
                break;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}