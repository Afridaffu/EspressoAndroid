package com.greenbox.coyni.view.business;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.greenbox.coyni.R;
import com.greenbox.coyni.model.APIError;
import com.greenbox.coyni.model.bank.SignOn;
import com.greenbox.coyni.model.bank.SignOnData;
import com.greenbox.coyni.model.bank.SyncAccount;
import com.greenbox.coyni.model.paymentmethods.PaymentMethodsResponse;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.view.BaseActivity;
import com.greenbox.coyni.view.BuyTokenPaymentMethodsActivity;
import com.greenbox.coyni.view.WebViewActivity;
import com.greenbox.coyni.viewmodel.CustomerProfileViewModel;

public class AddBankAccount extends BaseActivity {
    String strScreen = "", strSignOn = "";
    CustomerProfileViewModel customerProfileViewModel;
    MyApplication objMyApplication;
    SignOnData signOnData;
    Boolean isBank = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_add_bank);
            initialization();
            initObserver();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        try {
            if (requestCode == 1 && data == null) {
                if (objMyApplication.getStrFiservError() != null && objMyApplication.getStrFiservError().toLowerCase().equals("cancel")) {
                    Utils.displayAlert("Bank integration has been cancelled", AddBankAccount.this, "", "");
                } else {
                    showProgressDialog();
                    customerProfileViewModel.meSyncAccount();
                }
            } else {
                super.onActivityResult(requestCode, resultCode, data);
            }
        } catch (Exception ex) {
            super.onActivityResult(requestCode, resultCode, data);
            ex.printStackTrace();
        }
    }

    private void initialization() {
        try {
            objMyApplication = (MyApplication) getApplicationContext();
            customerProfileViewModel = new ViewModelProvider(this).get(CustomerProfileViewModel.class);
            if (Utils.checkInternet(AddBankAccount.this)) {
                if (objMyApplication.getSignOnData() == null || objMyApplication.getSignOnData().getUrl() == null) {
                    showProgressDialog();
                    customerProfileViewModel.meSignOn();
                } else {
                    strSignOn = objMyApplication.getStrSignOnError();
                    signOnData = objMyApplication.getSignOnData();
                }
            } else {
                Utils.displayAlert(getString(R.string.internet), AddBankAccount.this, "", "");
            }

            ControlMethod("externalBank");
            strScreen = "externalBank";
            externalBank();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void initObserver() {
        customerProfileViewModel.getSignOnMutableLiveData().observe(this, new Observer<SignOn>() {
            @Override
            public void onChanged(SignOn signOn) {
                try {
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    if (signOn != null) {
                        if (signOn.getStatus().toUpperCase().equals("SUCCESS")) {
                            objMyApplication.setSignOnData(signOn.getData());
                            signOnData = signOn.getData();
                            objMyApplication.setStrSignOnError("");
                            strSignOn = "";
                            if (objMyApplication.getResolveUrl()) {
                                objMyApplication.callResolveFlow(AddBankAccount.this, strSignOn, signOnData);
                            }
                        } else {
                            if (signOn.getError().getErrorCode().equals(getString(R.string.error_code)) && !objMyApplication.getResolveUrl()) {
                                objMyApplication.setResolveUrl(true);
                                customerProfileViewModel.meSignOn();
                            } else {
                                objMyApplication.setSignOnData(null);
                                signOnData = null;
                                objMyApplication.setStrSignOnError(signOn.getError().getErrorDescription());
                                strSignOn = signOn.getError().getErrorDescription();
                            }
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        customerProfileViewModel.getApiErrorMutableLiveData().observe(AddBankAccount.this, new Observer<APIError>() {
            @Override
            public void onChanged(APIError apiError) {
                try {
                    dialog.dismiss();
                    if (apiError != null) {
                        if (apiError.getError().getErrorCode().equals(getString(R.string.error_code)) && !objMyApplication.getResolveUrl()) {
                            objMyApplication.setResolveUrl(true);
                            customerProfileViewModel.meSignOn();
                        } else if (!isBank) {
                            if (!apiError.getError().getErrorDescription().equals("")) {
                                Utils.displayAlert(apiError.getError().getErrorDescription(), AddBankAccount.this, "", apiError.getError().getFieldErrors().get(0));
                            } else {
                                Utils.displayAlert(apiError.getError().getFieldErrors().get(0), AddBankAccount.this, "", apiError.getError().getFieldErrors().get(0));
                            }
                        } else {
                            isBank = false;
                            if (apiError.getError().getErrorCode().equals(getString(R.string.bank_error_code)) && apiError.getError().getErrorDescription().toLowerCase().contains("this payment method has already")) {
                                Utils.displayAlert(apiError.getError().getErrorDescription(), AddBankAccount.this, "Error", apiError.getError().getFieldErrors().get(0));
                            } else {
                                //displayError();
                            }
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        customerProfileViewModel.getSyncAccountMutableLiveData().observe(AddBankAccount.this, new Observer<SyncAccount>() {
            @Override
            public void onChanged(SyncAccount syncAccount) {
                try {
                    if (syncAccount != null) {
                        if (syncAccount.getStatus().toLowerCase().equals("success")) {
                            customerProfileViewModel.meBanks();
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        customerProfileViewModel.getPaymentMethodsResponseMutableLiveData().observe(this, new Observer<PaymentMethodsResponse>() {
            @Override
            public void onChanged(PaymentMethodsResponse paymentMethodsResponse) {
                if (dialog != null) {
                    dialog.dismiss();
                }
                if (paymentMethodsResponse != null) {
                    if (paymentMethodsResponse.getStatus().toLowerCase().equals("success")) {
                        //displaySuccess();
                    } else {
                        if (!paymentMethodsResponse.getError().getErrorDescription().equals("")) {
                            Utils.displayAlert(paymentMethodsResponse.getError().getErrorDescription(), AddBankAccount.this, "", paymentMethodsResponse.getError().getFieldErrors().get(0));
                        } else {
                            Utils.displayAlert(paymentMethodsResponse.getError().getFieldErrors().get(0), AddBankAccount.this, "", paymentMethodsResponse.getError().getFieldErrors().get(0));
                        }
                    }
                }
            }
        });
    }

    private void externalBank() {
        try {
            TextView tvLearnMore, tvHead;
            CardView cvNext;
            tvHead = findViewById(R.id.tvHead);
            tvLearnMore = findViewById(R.id.tvLearnMore);
            cvNext = findViewById(R.id.cvNext);
            tvHead.setText("I’m Ready");
            cvNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (strSignOn.equals("") && signOnData != null && signOnData.getUrl() != null) {
                            isBank = true;
                            Intent i = new Intent(AddBankAccount.this, WebViewActivity.class);
                            i.putExtra("signon", signOnData);
                            startActivityForResult(i, 1);
                        } else {
                            Utils.displayAlert(strSignOn, AddBankAccount.this, "", "");
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void ControlMethod(String methodToShow) {
        try {
            switch (methodToShow) {
                case "externalBank": {
                    findViewById(R.id.externalBank).setVisibility(View.VISIBLE);
                    findViewById(R.id.firstError).setVisibility(View.GONE);
                    findViewById(R.id.banksuccess).setVisibility(View.GONE);
                }
                break;
                case "firstError": {
                    findViewById(R.id.externalBank).setVisibility(View.GONE);
                    findViewById(R.id.banksuccess).setVisibility(View.GONE);
                    findViewById(R.id.firstError).setVisibility(View.VISIBLE);
                }
                break;
                case "banksuccess": {
                    findViewById(R.id.externalBank).setVisibility(View.GONE);
                    findViewById(R.id.firstError).setVisibility(View.GONE);
                    findViewById(R.id.banksuccess).setVisibility(View.VISIBLE);
                }
                break;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}