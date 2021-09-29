package com.coyni.android.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.TextView;

import com.coyni.android.R;
import com.coyni.android.model.sendtransfer.TransferSendRequest;
import com.coyni.android.model.sendtransfer.TransferSendResponse;
import com.coyni.android.model.transferfee.TransferFeeResponse;
import com.coyni.android.utils.MyApplication;
import com.coyni.android.utils.Utils;
import com.coyni.android.viewmodel.PayViewModel;
import com.coyni.android.viewmodel.SendViewModel;

import in.shadowfax.proswipebutton.ProSwipeButton;

public class PayRequestPreviewActivity extends AppCompatActivity {
    MyApplication objMyApplication;
    PayViewModel payViewModel;
    SendViewModel sendViewModel;
    TextView tvAmount, tvTitle, tvWalletID, tvProcessingFee, tvTotal, tvBalance;
    TransferFeeResponse transferFeeResponse;
    ProSwipeButton proSwipeBtn;
    String strTransId = "", strWalleId = "", strRemarks = "";
    ProgressDialog dialog;
    Double pfee = 0.0, bal = 0.0, amt = 0.0, total = 0.0;
    private static int CODE_AUTHENTICATION_VERIFICATION = 241;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_pay_request_preview);
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
            objMyApplication.userInactive(PayRequestPreviewActivity.this, this, false);
            objMyApplication.getAppHandler().removeCallbacks(objMyApplication.getAppRunnable());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        objMyApplication.getHandler().removeCallbacks(objMyApplication.getMyRunnable());
        objMyApplication.userInactive(PayRequestPreviewActivity.this, this, true);
    }

    @Override
    public void onUserInteraction() {
        // TODO Auto-generated method stub
        super.onUserInteraction();
        objMyApplication.getHandler().removeCallbacks(objMyApplication.getMyRunnable());
        objMyApplication.userInactive(PayRequestPreviewActivity.this, this, false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == CODE_AUTHENTICATION_VERIFICATION) {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    if (Utils.checkInternet(PayRequestPreviewActivity.this)) {
                        dialog = new ProgressDialog(PayRequestPreviewActivity.this, R.style.MyAlertDialogStyle);
                        dialog.setIndeterminate(false);
                        dialog.setMessage("Please wait...");
                        dialog.getWindow().setGravity(Gravity.CENTER);
                        dialog.show();
                        payTransaction();
                    } else {
                        Utils.displayAlert(getString(R.string.internet), PayRequestPreviewActivity.this);
                    }
                }
            });
        } else {
            Utils.displayAlert("Failure: Unable to verify user's identity", PayRequestPreviewActivity.this);
        }
    }

    private void initialization() {
        try {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_sent);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            Utils.statusBar(PayRequestPreviewActivity.this);
            payViewModel = new ViewModelProvider(this).get(PayViewModel.class);
            sendViewModel = new ViewModelProvider(this).get(SendViewModel.class);
            objMyApplication = (MyApplication) getApplicationContext();
            tvTitle = (TextView) toolbar.findViewById(R.id.tvTitle);
            tvAmount = (TextView) findViewById(R.id.tvAmount);
            tvWalletID = (TextView) findViewById(R.id.tvWalletID);
            tvProcessingFee = (TextView) findViewById(R.id.tvProcessingFee);
            tvTotal = (TextView) findViewById(R.id.tvTotal);
            tvBalance = (TextView) findViewById(R.id.tvBalance);
            proSwipeBtn = (ProSwipeButton) findViewById(R.id.btnConfirm);
            proSwipeBtn.setSwipeDistance(0.6f);
            proSwipeBtn.setOnSwipeListener(new ProSwipeButton.OnSwipeListener() {
                @Override
                public void onSwipeConfirm() {
                    // user has swiped the btn. Perform your async operation now
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            proSwipeBtn.showResultIcon(true, true);
                            if (Utils.checkInternet(PayRequestPreviewActivity.this)) {
                                if (objMyApplication.getCoyniPin()) {
                                    Utils.checkAuthentication(PayRequestPreviewActivity.this, CODE_AUTHENTICATION_VERIFICATION);
                                } else {
                                    dialog = new ProgressDialog(PayRequestPreviewActivity.this, R.style.MyAlertDialogStyle);
                                    dialog.setIndeterminate(false);
                                    dialog.setMessage("Please wait...");
                                    dialog.getWindow().setGravity(Gravity.CENTER);
                                    dialog.show();
                                    payTransaction();
                                }
                            } else {
                                Utils.displayAlert(getString(R.string.internet), PayRequestPreviewActivity.this);
                            }
                        }
                    }, 500);

                }
            });

            tvTitle.setText("Pay " + getString(R.string.currency));
            if (getIntent().getStringExtra("walletId") != null && !getIntent().getStringExtra("walletId").equals("")) {
                tvWalletID.setText(getIntent().getStringExtra("walletId").substring(0, 12) + "...");
                strWalleId = getIntent().getStringExtra("walletId");
            }
            if (getIntent().getStringExtra("msg") != null && !getIntent().getStringExtra("msg").equals("")) {
                strRemarks = getIntent().getStringExtra("msg");
            }
            if (getIntent().getStringExtra("pay") != null && !getIntent().getStringExtra("pay").equals("")) {
                tvAmount.setText(Utils.USNumberFormat(Double.parseDouble(getIntent().getStringExtra("pay").replace(",", ""))) + " " + getString(R.string.currency));
                amt = Double.parseDouble(getIntent().getStringExtra("pay").replace(",", ""));
            }

            transferFeeResponse = objMyApplication.getTransferFeeResponse();
            if (transferFeeResponse != null) {
                pfee = transferFeeResponse.getData().getFee();
//                tvProcessingFee.setText(Utils.convertBigDecimalUSDC(String.valueOf(pfee)) + " " + getString(R.string.currency));
                tvProcessingFee.setText(Utils.USNumberFormat(pfee) + " " + getString(R.string.currency));
                total = amt + pfee;
                tvTotal.setText(Utils.USNumberFormat(total) + " " + getString(R.string.currency));
            } else {
                pfee = 2.0;
            }
            bal = objMyApplication.getGBTBalance() - total;
            String strBal = Utils.convertBigDecimalUSDC(String.valueOf(bal));
            tvBalance.setText(Utils.USNumberFormat(Double.parseDouble(strBal)) + " " + getString(R.string.currency));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void initObserver() {
        sendViewModel.getSendTokenMutableLiveData().observe(this, new Observer<TransferSendResponse>() {
            @Override
            public void onChanged(TransferSendResponse transferSendResponse) {
                dialog.dismiss();
                if (transferSendResponse != null) {
                    strTransId = transferSendResponse.getData().getGbxTransactionId();
                    Intent i = new Intent(PayRequestPreviewActivity.this, PayRequestSuccessActivity.class);
                    i.putExtra("amount", String.valueOf(total));
                    i.putExtra("walletId", strWalleId);
                    i.putExtra("balance", Utils.convertBigDecimalUSDC(String.valueOf(bal)));
                    i.putExtra("transId", strTransId);
                    startActivity(i);
                }
            }
        });

    }

    private void payTransaction() {
        try {
            TransferSendRequest request = new TransferSendRequest();
            request.setTokens(String.valueOf(amt));
            request.setRemarks(strRemarks);
            request.setRecipientWalletId(strWalleId);
            if (Utils.checkInternet(PayRequestPreviewActivity.this)) {
                sendViewModel.sendTokens(request);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}