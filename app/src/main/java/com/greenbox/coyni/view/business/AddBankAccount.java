package com.greenbox.coyni.view.business;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.greenbox.coyni.R;
import com.greenbox.coyni.adapters.BanksListAdapter;
import com.greenbox.coyni.adapters.PaymentMethodsAdapter;
import com.greenbox.coyni.model.APIError;
import com.greenbox.coyni.model.bank.BankDeleteResponseData;
import com.greenbox.coyni.model.bank.BankItem;
import com.greenbox.coyni.model.bank.BankResponse;
import com.greenbox.coyni.model.bank.SignOn;
import com.greenbox.coyni.model.bank.SignOnData;
import com.greenbox.coyni.model.bank.SyncAccount;
import com.greenbox.coyni.model.paymentmethods.PaymentMethodsResponse;
import com.greenbox.coyni.model.paymentmethods.PaymentsList;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.view.BaseActivity;
import com.greenbox.coyni.view.BuyTokenPaymentMethodsActivity;
import com.greenbox.coyni.view.PaymentMethodsActivity;
import com.greenbox.coyni.view.WebViewActivity;
import com.greenbox.coyni.view.WithdrawPaymentMethodsActivity;
import com.greenbox.coyni.viewmodel.CustomerProfileViewModel;
import com.greenbox.coyni.viewmodel.PaymentMethodsViewModel;

import java.util.List;

public class AddBankAccount extends BaseActivity {
    String strScreen = "", strSignOn = "";
    CustomerProfileViewModel customerProfileViewModel;
    MyApplication objMyApplication;
    SignOnData signOnData;
    Boolean isBank = false;
    Long mLastClickTime = 0L;
    PaymentMethodsViewModel paymentMethodsViewModel;

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
    public void onBackPressed() {
        if (!strScreen.equals("firstError")) {
            super.onBackPressed();
            finish();
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
            paymentMethodsViewModel = new ViewModelProvider(this).get(PaymentMethodsViewModel.class);
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
        try {
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
                                    displayError();
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

            customerProfileViewModel.getBankResponseMutableLiveData().observe(this, new Observer<BankResponse>() {
                @Override
                public void onChanged(BankResponse bankResponse) {
                    dismissDialog();
                    if (bankResponse != null) {
                        if (bankResponse.getStatus().toLowerCase().equals("success")) {
                            ControlMethod("banksuccess");
                            strScreen = "banksuccess";
                            bankSuccess(bankResponse.getData().getItems());
                        } else {
                            if (!bankResponse.getError().getErrorDescription().equals("")) {
                                Utils.displayAlert(bankResponse.getError().getErrorDescription(), AddBankAccount.this, "", bankResponse.getError().getFieldErrors().get(0));
                            } else {
                                Utils.displayAlert(bankResponse.getError().getFieldErrors().get(0), AddBankAccount.this, "", bankResponse.getError().getFieldErrors().get(0));
                            }
                        }
                    }
                }
            });

            paymentMethodsViewModel.getDelBankResponseMutableLiveData().observe(this, new Observer<BankDeleteResponseData>() {
                @Override
                public void onChanged(BankDeleteResponseData bankDeleteResponseData) {
                    dismissDialog();
                    if (bankDeleteResponseData.getStatus().toLowerCase().equals("success")) {
                        Utils.showCustomToast(AddBankAccount.this, "Bank has been removed.", R.drawable.ic_custom_tick, "");
                        customerProfileViewModel.meBanks();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void externalBank() {
        try {
            TextView tvLearnMore, tvHead, tvBankHead;
            CardView cvNext;
            LinearLayout lyExternalClose;
            tvHead = findViewById(R.id.tvHead);
            tvLearnMore = findViewById(R.id.tvLearnMore);
            tvBankHead = findViewById(R.id.tvBankHead);
            cvNext = findViewById(R.id.cvNext);
            lyExternalClose = findViewById(R.id.lyExternalClose);
            tvHead.setText("I’m Ready");
            tvBankHead.setText("Add Bank Account");
            tvLearnMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();
                        Utils.populateLearnMore(AddBankAccount.this);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
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

            lyExternalClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void bankSuccess(List<BankItem> listBanks) {
        try {
            BanksListAdapter banksListAdapter;
            CardView cvDone = findViewById(R.id.cvDone);
            RecyclerView rvBanks = findViewById(R.id.rvBanks);
            LinearLayout lySuccesslClose = findViewById(R.id.lySuccesslClose);
            cvDone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });
            if (listBanks != null && listBanks.size() > 0) {
                banksListAdapter = new BanksListAdapter(listBanks, AddBankAccount.this);
                LinearLayoutManager mLayoutManager = new LinearLayoutManager(AddBankAccount.this);
                rvBanks.setLayoutManager(mLayoutManager);
                rvBanks.setItemAnimator(new DefaultItemAnimator());
                rvBanks.setAdapter(banksListAdapter);
            } else {
                rvBanks.setVisibility(View.INVISIBLE);
            }

            lySuccesslClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ControlMethod("externalBank");
                    strScreen = "externalBank";
                    externalBank();
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

    private void displayError() {
        try {
            ControlMethod("firstError");
            TextView tvErrorHead, tvErrorMessage;
            CardView cvTryAgain;
            tvErrorHead = findViewById(R.id.tvErrorHead);
            tvErrorMessage = findViewById(R.id.tvErrorMessage);
            cvTryAgain = findViewById(R.id.cvTryAgain);
            tvErrorHead.setText(getString(R.string.bank_exhausthead));
            strScreen = "firstError";
            tvErrorMessage.setText("There is an account limit of 4 total bank accounts, and it looks like you surpassed that number via the Fiserv bank account verification process. Please try again or remove one or more of your current bank account payment methods.");
            cvTryAgain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ControlMethod("externalBank");
                    strScreen = "externalBank";
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void deleteBankAPICall(int id) {
        try {
            showProgressDialog();
            paymentMethodsViewModel.deleteBanks(id);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void showPopup() {
        try {
            final Dialog dialog = new Dialog(AddBankAccount.this);
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.bankdeleteconfirmation);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            DisplayMetrics mertics = getResources().getDisplayMetrics();
            int width = mertics.widthPixels;

            TextView tvRelink = dialog.findViewById(R.id.tvRelink);
            TextView tvKeep = dialog.findViewById(R.id.tvKeep);

            tvRelink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        dialog.dismiss();
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();

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
            tvKeep.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
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