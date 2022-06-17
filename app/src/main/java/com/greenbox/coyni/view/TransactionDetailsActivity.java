package com.greenbox.coyni.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.greenbox.coyni.R;
import com.greenbox.coyni.adapters.ActivityLogAdapter;
import com.greenbox.coyni.adapters.AddNewBusinessAccountDBAAdapter;
import com.greenbox.coyni.model.activtity_log.ActivityLogResp;
import com.greenbox.coyni.model.preferences.BaseProfile;
import com.greenbox.coyni.model.transaction.TransactionData;
import com.greenbox.coyni.model.transaction.TransactionDetails;
import com.greenbox.coyni.utils.LogUtils;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.view.business.MerchantTransactionDetailsActivity;
import com.greenbox.coyni.viewmodel.DashboardViewModel;

import org.w3c.dom.Text;

import okhttp3.internal.Util;

public class TransactionDetailsActivity extends BaseActivity {
    DashboardViewModel dashboardViewModel;
    MyApplication objMyApplication;
    String strGbxTxnIdType = "";
    int txnType;
    Integer txnSubType;
    String txnId = "";
    Dialog progressDialog;
    CardView cancelTxnCV;
    TextView successadd, purchaseTime, activity_log_tv;
    RecyclerView recyclerView;
    private String gbxID, txnTypeStr, txnSubTypeStr;

    // Control Method Types
    private static final String PAY_REQUEST = "PayRequest";
    private static final String BUY_TOKEN = "BuyTokenDebitAndCreditCard";
    private static final String BUY_BANK = "BuyTokenBank";
    private static final String BUY_SIGNET = "BuyTokenSignet";
    private static final String WITH_GIFT = "WithdrawGiftCard";
    private static final String WITH_Instant = "WithdrawInstantPay";
    private static final String WITH_BANK = "WithdrawBankAccount";
    private static final String WITH_SIGNET = "WithdrawSignet";
    private static final String BUSINESS_PAYOUT = "businessPayout";
    private static final String CANCELLED_WITH = "cancelledWithdrawBank";
    private static final String FAILED_WITH = "failedWithdrawBank";
    private static final String PAID_ORDER_TOKEN = "PaidOrderToken";
    private static final String REFUND_RECEIVED = "RefundReceived";
    private static final String REFUND_SENT = "RefundSent";
    private static final String RESERVE_RELEASE = "ReserveRelease";

    // Transaction Types
    private static final String pay_request = "pay / request";
    private static final String buy_tokens = "buy tokens";
    private static final String buy_token = "buy token";
    private static final String withdraw = "withdraw";
    private static final String business_payout = "business payout";
    private static final String canceled_bank_withdraw = "canceled bank withdraw";
    private static final String failed_bank_withdraw = "failed bank withdraw";
    private static final String paid_order = "paid order";
    private static final String refund = "refund";
    private static final String reserve_release = "reserve release";

    // Transaction SubTypes
    private static final String sent = "sent";
    private static final String received = "received";
    private static final String bank_account = "bank account";
    private static final String credit_card = "credit card";
    private static final String debit_card = "debit card";
    private static final String signet = "signet";
    private static final String gift_card = "gift card";
    private static final String instant_pay = "instant pay";
    private static final String token = "token";

    private String message, createdAt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            setContentView(R.layout.activity_transaction_details);
            initialization();
            initObserver();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void initialization() {
        try {
            dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
            recyclerView = findViewById(R.id.recycler_view);
            objMyApplication = (MyApplication) getApplicationContext();
            if (getIntent().getStringExtra(Utils.gbxTxnIdType) != null && !getIntent().getStringExtra(Utils.gbxTxnIdType).equals("")) {
                strGbxTxnIdType = getIntent().getStringExtra(Utils.gbxTxnIdType);
            }
            if (getIntent().getStringExtra(Utils.txnType) != null && !getIntent().getStringExtra(Utils.txnType).equals("")) {
                //txnType = Integer.parseInt(getIntent().getStringExtra("txnType"));
                switch (getIntent().getStringExtra(Utils.txnType).toLowerCase()) {
                    case pay_request:
                        txnType = Integer.parseInt(Utils.payType);
                        break;
                    case buy_token:
                    case buy_tokens: {
                        txnType = Integer.parseInt(Utils.addType);
                        break;
                    }
                    case withdraw:
                        txnType = Integer.parseInt(Utils.withdrawType);
                        break;
//                    case Utils.businessPayouttxntype:
//                        txnType = Utils.businessPayout;
//                        break;
                    case Utils.merchantPayouttxntype:
                        txnType = Utils.merchantPayout;
                        break;
                    case canceled_bank_withdraw:
                        txnType = Utils.cancelledWithdraw;
                        break;
                    case failed_bank_withdraw:
                        txnType = Utils.failedWithdraw;
                        break;
                    case paid_order:
                        txnType = Utils.paidInvoice;
                        break;
                    case refund:
                        txnType = Utils.refund;
                        break;
                    case reserve_release:
                        txnType = Utils.reserveRelease;
                        break;
                }
            }
            if (getIntent().getStringExtra(Utils.txnSubType) != null && !getIntent().getStringExtra(Utils.txnSubType).equals("")) {
                //txnSubType = Integer.parseInt(getIntent().getStringExtra("txnSubType"));
                switch (getIntent().getStringExtra(Utils.txnSubType).toLowerCase()) {
                    case sent:
                        txnSubType = Integer.parseInt(Utils.paySubType);
                        break;
                    case received:
                        txnSubType = Integer.parseInt(Utils.requestSubType);
                        break;
                    case bank_account:
                        txnSubType = Integer.parseInt(Utils.bankType);
                        break;
                    case credit_card:
                        txnSubType = Integer.parseInt(Utils.creditType);
                        break;
                    case debit_card:
                        txnSubType = Integer.parseInt(Utils.debitType);
                        break;
                    case gift_card:
                        txnSubType = Integer.parseInt(Utils.giftcardType);
                        break;
                    case instant_pay:
                        txnSubType = Integer.parseInt(Utils.instantType);
                        break;
                    case signet:
                        txnSubType = Integer.parseInt(Utils.signetType);
                        break;
                    case token:
                        txnSubType = Integer.parseInt(Utils.tokenType);
                        break;
                    case Utils.transfersub:
                        txnSubType = Utils.transfer;
                        break;
                    default:
                        txnSubType = null;
                        break;
                }
            }

            if (getIntent().getStringExtra("txnId") != null && !getIntent().getStringExtra("txnId").equals("")) {
                txnId = getIntent().getStringExtra("txnId");
            }

            if (Utils.checkInternet(TransactionDetailsActivity.this)) {
//                progressDialog = Utils.showProgressDialog(TransactionDetailsActivity.this);
                showProgressDialog();
                dashboardViewModel.getTransactionDetails(strGbxTxnIdType, txnType, txnSubType);
            } else {
                Utils.displayAlert(getString(R.string.internet), TransactionDetailsActivity.this, "", "");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void initObserver() {
        dashboardViewModel.getTransactionDetailsMutableLiveData().observe(this, transactionDetails -> {
//            if (progressDialog != null) {
//                progressDialog.dismiss();
//            }
            dismissDialog();
            if (transactionDetails != null && transactionDetails.getStatus().equalsIgnoreCase("Success")) {
                switch (transactionDetails.getData().getTransactionType().toLowerCase()) {
                    case pay_request:
                        ControlMethod(PAY_REQUEST);
                        payRequest(transactionDetails.getData());
                        break;
                    case buy_token:
                    case buy_tokens:
                        switch (transactionDetails.getData().getTransactionSubtype().toLowerCase()) {
                            case credit_card:
                            case debit_card:
                                ControlMethod(BUY_TOKEN);
                                buyTokenCreditDebit(transactionDetails.getData());
                                break;
                            case bank_account:
                                ControlMethod(BUY_BANK);
                                buyTokenBankAccount(transactionDetails.getData());
                                break;
                            case signet:
                                ControlMethod(BUY_SIGNET);
                                buyTokenSignet(transactionDetails.getData());
                                break;
                        }
                        break;
                    case withdraw:
                        switch (transactionDetails.getData().getTransactionSubtype().toLowerCase()) {
                            case gift_card:
                                ControlMethod(WITH_GIFT);
                                withdrawGiftCard(transactionDetails.getData());
                                break;
                            case instant_pay:
                                ControlMethod(WITH_Instant);
                                withdrawInstant(transactionDetails.getData());
                                break;
                            case bank_account:
                                ControlMethod(WITH_BANK);
                                withdrawBank(transactionDetails.getData());
                                break;
                            case signet:
                                ControlMethod(WITH_SIGNET);
                                withdrawSignet(transactionDetails.getData());
                                break;
                        }
                    case canceled_bank_withdraw: {
                        ControlMethod(CANCELLED_WITH);
                        cancelledWithdraw(transactionDetails.getData());
                    }
                    break;
                    case failed_bank_withdraw: {
                        ControlMethod(FAILED_WITH);
                        failedWithdraw(transactionDetails.getData());
                    }
                    break;
                    case paid_order:
                        if (token.equals(transactionDetails.getData().getTransactionSubtype().toLowerCase())) {
                            ControlMethod(PAID_ORDER_TOKEN);
                            paidOrderToken(transactionDetails.getData());
                        }
                        break;
                    case refund: {
                        if (received.equals(transactionDetails.getData().getTransactionSubtype().toLowerCase())) {
                            ControlMethod(REFUND_RECEIVED);
                            paidOrderToken(transactionDetails.getData());
                        } else if (sent.equals(transactionDetails.getData().getTransactionSubtype().toLowerCase())) {
                            ControlMethod(REFUND_SENT);
                            refundtoken(transactionDetails.getData());
                        }
                    }
                    break;
                    case reserve_release: {
                        ControlMethod(RESERVE_RELEASE);
                        reserveRelease(transactionDetails.getData());
                    }
                    break;
//                    case business_payout: {
//                        ControlMethod(BUSINESS_PAYOUT);
//                        businessPayout(transactionDetails.getData());
//                    }
//                    break;
                    case Utils.merchantPayouttxntype: {
                        ControlMethod(BUSINESS_PAYOUT);
                        businessPayout(transactionDetails.getData());
                    }
                    break;

                }
            } else {
                if (transactionDetails.getError().getErrorDescription() != null && !transactionDetails.getError().getErrorDescription().equals("")) {
                    Utils.displayAlert(transactionDetails.getError().getErrorDescription(), TransactionDetailsActivity.this, "", transactionDetails.getError().getFieldErrors().get(0));
                } else {
                    Utils.displayAlert(transactionDetails.getError().getFieldErrors().get(0), TransactionDetailsActivity.this, "", "");
                }
            }
        });

        dashboardViewModel.getCancelBuyTokenResponseMutableLiveData().observe(this, cancelBuyTokenResponse -> {
            try {
//                progressDialog.dismiss();
                dismissDialog();
                if (cancelBuyTokenResponse != null && cancelBuyTokenResponse.getStatus().equalsIgnoreCase(Utils.SUCCESS)) {
                    Utils.showCustomToast(TransactionDetailsActivity.this, "Transaction cancelled successfully.", R.drawable.ic_custom_tick, "");
                    //progressDialog = Utils.showProgressDialog(TransactionDetailsActivity.this);
                    dashboardViewModel.getTransactionDetails(strGbxTxnIdType, txnType, txnSubType);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        dashboardViewModel.getActivityLogRespMutableLiveData().observe(this, new Observer<ActivityLogResp>() {
            @Override
            public void onChanged(ActivityLogResp activityLogResp) {
                if (activityLogResp != null) {
                    if (activityLogResp.getStatus().equalsIgnoreCase(Utils.SUCCESS)) {
                        if (activityLogResp.getData().size() > 0) {
                            ActivityLogAdapter activityListAdater = new ActivityLogAdapter(activityLogResp, TransactionDetailsActivity.this);
                            LinearLayoutManager mLayoutManager = new LinearLayoutManager(TransactionDetailsActivity.this);
                            recyclerView.setLayoutManager(mLayoutManager);
                            recyclerView.setItemAnimator(new DefaultItemAnimator());
                            recyclerView.setAdapter(activityListAdater);
                            activity_log_tv.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        });
    }

    private void refundReceived(TransactionData data) {

    }

    private void reserveRelease(TransactionData reserveData) {
        TextView type, reserveAmount, status, date, referenceId, reserveRules, depositTo, tokenType;
        TextView reserveHeld, reservedOn, reserveId;

        LinearLayout reserveIDCopy, mCloseButton;

        type = findViewById(R.id.reserve_type);
        reserveAmount = findViewById(R.id.reserve_amount);
        status = findViewById(R.id.reserve_status);
        date = findViewById(R.id.released_on);
        referenceId = findViewById(R.id.reference_id);
        reserveRules = findViewById(R.id.reserve_rules);
        depositTo = findViewById(R.id.deposit_to);
        tokenType = findViewById(R.id.token_account_type);

        reserveHeld = findViewById(R.id.reserve_held_amount);
        reservedOn = findViewById(R.id.reserved_on_date);
        reserveId = findViewById(R.id.reserve_id);

        reserveIDCopy = findViewById(R.id.reserve_id_copy);
        mCloseButton = findViewById(R.id.reserve_close_button);


        if (reserveData.getTransactionType() != null) {
            type.setText(reserveData.getTransactionType());
        }
        if (reserveData.getStatus() != null) {
            status.setText(reserveData.getStatus());

            switch (reserveData.getStatus().toLowerCase()) {
                case Utils.transCompleted:
                    status.setTextColor(getResources().getColor(R.color.completed_status));
                    status.setBackgroundResource(R.drawable.txn_completed_bg);
                    break;
                case Utils.transinprogress:
                    status.setTextColor(getResources().getColor(R.color.inprogress_status));
                    status.setBackgroundResource(R.drawable.txn_inprogress_bg);
                    break;
                case Utils.transPending:
                    status.setTextColor(getResources().getColor(R.color.pending_status));
                    status.setBackgroundResource(R.drawable.txn_pending_bg);
                    break;
                case Utils.transFailed:
                    status.setTextColor(getResources().getColor(R.color.failed_status));
                    status.setBackgroundResource(R.drawable.txn_failed_bg);
                    break;
            }
        }

        if (reserveData.getReleasedDate() != null) {
            date.setText(objMyApplication.convertZoneLatestTxn(reserveData.getReleasedDate()));
        }


        if (reserveData.getReferenceId() != null) {
            if (reserveData.getReferenceId().length() > 10) {
                referenceId.setText(reserveData.getReferenceId().substring(0, 10) + "...");
            } else {
                referenceId.setText(reserveData.getReferenceId());
            }
        }

        if (reserveData.getReserveRule() != null) {
            reserveRules.setText(reserveData.getReserveRule());
        }

//        String depositTO;
//        if (reserveData.getDepositTo() != null) {
//            if (reserveData.getDepositTo().toLowerCase().contains("token account")) {
//                tokenType.setText(reserveData.getDepositTo().split("Token")[0] + "Token Account");
//            }
//            depositTO = reserveData.getDepositTo().split("Account")[1].trim();
//            if (depositTO.length() > 10)
//                depositTo.setText(Html.fromHtml("<u>" + depositTO.substring(0, 10) + "..." + "</u>"));
//            else
//                depositTo.setText(Html.fromHtml("<u>" + depositTO + "</u>"));
//        }
        if (reserveData.getDepositTo() != null) {
            if (reserveData.getDepositTo().length() > 10) {
                depositTo.setText(reserveData.getDepositTo().substring(0, 10) + "...");
                depositTo.setPaintFlags(depositTo.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

            } else {
                depositTo.setText(reserveData.getDepositTo());
                depositTo.setPaintFlags(depositTo.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            }
        }

        if (reserveData.getTotalAmount() != null) {
            if (reserveData.getTotalAmount().contains("CYN"))
                reserveAmount.setText(Utils.convertTwoDecimal(reserveData.getTotalAmount().replace("CYN", "").trim()));
            else
                reserveAmount.setText(Utils.convertTwoDecimal(reserveData.getTotalAmount().replace("USD", "").trim()));
        }

        if (reserveData.getAmountReleased() != null) {
            if (reserveData.getAmountReleased().contains("CYN"))
                reserveHeld.setText(Utils.convertTwoDecimal(reserveData.getAmountReleased().replace("CYN", "").trim()) + " CYN");
            else
                reserveHeld.setText(Utils.convertTwoDecimal(reserveData.getAmountReleased().replace("USD", "").trim()) + " CYN");
        }

        if (reserveData.getReservedOn() != null) {
            reservedOn.setText(objMyApplication.convertZoneLatestTxn(reserveData.getReservedOn()));
        }

        String reserveID = "";
        if (reserveData.getReserveId() != null) {
            if (reserveData.getReserveId().length() > 10) {
                reserveID = reserveData.getReserveId().substring(0, 10) + "...";
                reserveId.setText(Html.fromHtml("<u>" + reserveID + "</u>"));
            } else {
                reserveID = reserveData.getReserveId();
                reserveId.setText(Html.fromHtml("<u>" + reserveID + "</u>"));
            }
        }

        reserveIDCopy.setOnClickListener(view -> Utils.copyText(reserveData.getReferenceId(), TransactionDetailsActivity.this));

        mCloseButton.setOnClickListener(view -> finish());


    }

    private void paidOrderToken(TransactionData paidOrderData) {
        TextView mTransactionType, mPaidStatus, mPaidAmount, mPaidDateAndTime, mAccountBalance, mReferenceID, mMerchantAccountID, mDbaName, mCustomerServiceEmail, mCustomerServicePhone, mDescription;
        LinearLayout mReferenceCopy, mMerchantAccountCopy, mBackButton;
        TextView mAmountPaid, mDateAndTime, mPaidReferenceID;

        mTransactionType = findViewById(R.id.transaction_types);
        mPaidAmount = findViewById(R.id.paid_amount);
        mPaidStatus = findViewById(R.id.paid_status);
        mPaidDateAndTime = findViewById(R.id.paid_date_time);
        mAccountBalance = findViewById(R.id.account_balance);
        mReferenceID = findViewById(R.id.paid_reference_id);
        mMerchantAccountID = findViewById(R.id.merchant_account_id);
        mDbaName = findViewById(R.id.dba_name);
        mCustomerServiceEmail = findViewById(R.id.customer_service_email);
        mCustomerServicePhone = findViewById(R.id.customer_service_phone);
        mReferenceCopy = findViewById(R.id.copy_ref_ll);
        mMerchantAccountCopy = findViewById(R.id.copy_merchant_id);
        mDescription = findViewById(R.id.description);
        mBackButton = findViewById(R.id.back_button);

        mAmountPaid = findViewById(R.id.amount_paid);
        mDateAndTime = findViewById(R.id.date_and_time);
        mPaidReferenceID = findViewById(R.id.reference_id);


        if (paidOrderData.getTransactionType() != null && paidOrderData.getTransactionSubtype() != null) {
            mTransactionType.setText(paidOrderData.getTransactionType() + " - " + paidOrderData.getTransactionSubtype());
        }

        if (paidOrderData.getPaidAmount() != null) {
            mPaidAmount.setText(Utils.convertTwoDecimal(paidOrderData.getPaidAmount().replace("CYN", "").trim()));
            findViewById(R.id.card_view_refund).setVisibility(View.GONE);
            findViewById(R.id.original_transaction).setVisibility(View.GONE);
            findViewById(R.id.description).setVisibility(View.VISIBLE);
        }

        if (paidOrderData.getRefundAmount() != null) {
            mPaidAmount.setText(Utils.convertTwoDecimal(paidOrderData.getRefundAmount().replace("CYN", "").trim()));
            findViewById(R.id.card_view_refund).setVisibility(View.VISIBLE);
            findViewById(R.id.original_transaction).setVisibility(View.VISIBLE);
            findViewById(R.id.description).setVisibility(View.GONE);
        }

        if (paidOrderData.getStatus() != null) {
            mPaidStatus.setText(paidOrderData.getStatus());
            switch (paidOrderData.getStatus().toLowerCase()) {
                case Utils.transCompleted:
                    mPaidStatus.setTextColor(getResources().getColor(R.color.completed_status));
                    mPaidStatus.setBackgroundResource(R.drawable.txn_completed_bg);
                    break;
                case Utils.transinprogress:
                    mPaidStatus.setTextColor(getResources().getColor(R.color.inprogress_status));
                    mPaidStatus.setBackgroundResource(R.drawable.txn_inprogress_bg);
                    break;
                case Utils.refundd:
                case Utils.partialrefund:
                case Utils.transPending:
                    mPaidStatus.setTextColor(getResources().getColor(R.color.pending_status));
                    mPaidStatus.setBackgroundResource(R.drawable.txn_pending_bg);
                    break;
                case Utils.transCancelled:
                case Utils.transFailed:
                    mPaidStatus.setTextColor(getResources().getColor(R.color.failed_status));
                    mPaidStatus.setBackgroundResource(R.drawable.txn_failed_bg);
                    break;
            }
        }

        if (paidOrderData.getCreatedDate() != null) {
            mPaidDateAndTime.setText(objMyApplication.convertZoneLatestTxn(paidOrderData.getCreatedDate()));
        }

        if (paidOrderData.getAccountBalance() != null) {
            mAccountBalance.setText(Utils.convertTwoDecimal(paidOrderData.getAccountBalance().replace("CYN", "").trim()) + " CYN");
        }

        if (paidOrderData.getReferenceId() != null) {
            if (paidOrderData.getReferenceId().length() > 10)
                mReferenceID.setText(paidOrderData.getReferenceId().substring(0, 10) + "...");
            else
                mReferenceID.setText(paidOrderData.getReferenceId());

            mReferenceCopy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Utils.copyText(paidOrderData.getReferenceId(), TransactionDetailsActivity.this);
                }
            });
        }

        if (paidOrderData.getMerchantId() != null) {
            mMerchantAccountID.setText(paidOrderData.getMerchantId());
        }

        if (paidOrderData.getDbaName() != null) {
            if (paidOrderData.getDbaName().length() > 20)
                mDbaName.setText(paidOrderData.getDbaName().substring(0, 20) + "...");
            else
                mDbaName.setText(paidOrderData.getDbaName());
        }

        if (paidOrderData.getCustomerServiceMail() != null) {
            if (paidOrderData.getCustomerServiceMail().length() > 20) {
                mCustomerServiceEmail.setText(paidOrderData.getCustomerServiceMail().substring(0, 20) + "...");
            } else {
                mCustomerServiceEmail.setText(paidOrderData.getCustomerServiceMail());
            }
        }
        if (paidOrderData.getCustomerServicePhoneNo() != null) {
            String phone_number = "(" + paidOrderData.getCustomerServicePhoneNo().substring(0, 3) + ")" + " " + paidOrderData.getCustomerServicePhoneNo().substring(3, 6) + "-" + paidOrderData.getCustomerServicePhoneNo().substring(6, 10);
            mCustomerServicePhone.setText(phone_number);
        }

        String mVar = getString(R.string.description) + " ";
        SpannableString spannableString = new SpannableString(mVar);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                try {
                    startActivity(new Intent(TransactionDetailsActivity.this, GetHelpWebViewActivity.class));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(true);
                ds.setColor(getColor(R.color.primary_color));
            }
        };

        spannableString.setSpan(clickableSpan, mVar.length() - 9, mVar.length() - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mDescription.setText(spannableString);
        mDescription.setMovementMethod(LinkMovementMethod.getInstance());
        mDescription.setHighlightColor(Color.TRANSPARENT);

        if (paidOrderData.getSaleOrderPaidAmount() != null) {
            mAmountPaid.setText(Utils.convertTwoDecimal(paidOrderData.getSaleOrderPaidAmount().replace("CYN", "").trim()) + " CYN");
        }
        if (paidOrderData.getSaleOrderDateAndTime() != null) {
            mDateAndTime.setText(objMyApplication.convertZoneLatestTxn(paidOrderData.getSaleOrderDateAndTime()));
        }

        if (paidOrderData.getSaleOrderReferenceId() != null) {
            if (paidOrderData.getSaleOrderReferenceId().length() > 10) {
                String refId = paidOrderData.getSaleOrderReferenceId().substring(0, 10) + "...";
                mPaidReferenceID.setText(Html.fromHtml("<u>" + refId + "</u>"));
            } else
                mPaidReferenceID.setText(Html.fromHtml("<u>" + paidOrderData.getSaleOrderReferenceId() + "</u>"));
        }
        String paidOrderId = paidOrderData.getSaleOrderReferenceId();
        mPaidReferenceID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TransactionDetailsActivity.this, TransactionDetailsActivity.class)
                        .putExtra(Utils.txnType, paid_order)
                        .putExtra(Utils.txnSubType, token)
                        .putExtra(Utils.gbxTxnIdType, paidOrderId));
            }
        });

        mBackButton.setOnClickListener(view -> finish());


    }

    private void refundtoken(TransactionData refundsentdata) {
        TextView mTransactionType, mPaidStatus, mPaidAmount, mPaidDateAndTime, mAccountBalance, mReferenceID, mMerchantAccountID, mDbaName, mCustomerServiceEmail, mCustomerServicePhone, mDescription;
        LinearLayout mReferenceCopy, mMerchantAccountCopy, mBackButton;
        TextView mAmountPaid, mDateAndTime, mSaleReferenceID;

        mTransactionType = findViewById(R.id.transaction_types);
        mPaidAmount = findViewById(R.id.paid_amount);
        mPaidStatus = findViewById(R.id.paid_status);
        mPaidDateAndTime = findViewById(R.id.paid_date_time);
        mAccountBalance = findViewById(R.id.account_balance);
        mReferenceID = findViewById(R.id.paid_reference_id);
        mMerchantAccountID = findViewById(R.id.merchant_account_id);
        mDbaName = findViewById(R.id.dba_name);
        mCustomerServiceEmail = findViewById(R.id.customer_service_email);
        mCustomerServicePhone = findViewById(R.id.customer_service_phone);
        mReferenceCopy = findViewById(R.id.copy_ref_ll);
        mMerchantAccountCopy = findViewById(R.id.copy_merchant_id);
        mDescription = findViewById(R.id.description);
        mBackButton = findViewById(R.id.back_button);

        mAmountPaid = findViewById(R.id.amount_paid);
        mDateAndTime = findViewById(R.id.date_and_time);
        mSaleReferenceID = findViewById(R.id.reference_id);


        if (refundsentdata.getTransactionType() != null && refundsentdata.getTransactionSubtype() != null) {
            mTransactionType.setText(refundsentdata.getTransactionType() + " - " + refundsentdata.getTransactionSubtype());
        }

        if (refundsentdata.getPaidAmount() != null) {
            mPaidAmount.setText(Utils.convertTwoDecimal(refundsentdata.getPaidAmount().replace("CYN", "").trim()));
            findViewById(R.id.card_view_refund).setVisibility(View.GONE);
            findViewById(R.id.original_transaction).setVisibility(View.GONE);
            findViewById(R.id.description).setVisibility(View.VISIBLE);
        }

        if (refundsentdata.getRefundAmount() != null) {
            mPaidAmount.setText(Utils.convertTwoDecimal(refundsentdata.getRefundAmount().replace("CYN", "").trim()));
            findViewById(R.id.card_view_refund).setVisibility(View.VISIBLE);
            findViewById(R.id.original_transaction).setVisibility(View.VISIBLE);
            findViewById(R.id.description).setVisibility(View.GONE);
        }

        if (refundsentdata.getStatus() != null) {
            mPaidStatus.setText(refundsentdata.getStatus());
            switch (refundsentdata.getStatus().toLowerCase()) {
                case Utils.transCompleted:
                    mPaidStatus.setTextColor(getResources().getColor(R.color.completed_status));
                    mPaidStatus.setBackgroundResource(R.drawable.txn_completed_bg);
                    break;
                case Utils.transinprogress:
                    mPaidStatus.setTextColor(getResources().getColor(R.color.inprogress_status));
                    mPaidStatus.setBackgroundResource(R.drawable.txn_inprogress_bg);
                    break;
                case Utils.refundd:
                case Utils.partialrefund:
                case Utils.transPending:
                    mPaidStatus.setTextColor(getResources().getColor(R.color.pending_status));
                    mPaidStatus.setBackgroundResource(R.drawable.txn_pending_bg);
                    break;
                case Utils.transCancelled:
                case Utils.transFailed:
                    mPaidStatus.setTextColor(getResources().getColor(R.color.failed_status));
                    mPaidStatus.setBackgroundResource(R.drawable.txn_failed_bg);
                    break;
            }
        }

        if (refundsentdata.getCreatedDate() != null) {
            mPaidDateAndTime.setText(objMyApplication.convertZoneLatestTxn(refundsentdata.getCreatedDate()));
        }

        if (refundsentdata.getAccountBalance() != null) {
            mAccountBalance.setText(Utils.convertTwoDecimal(refundsentdata.getAccountBalance().replace("CYN", "").trim()) + " CYN");
        }

        if (refundsentdata.getReferenceId() != null) {
            if (refundsentdata.getReferenceId().length() > 10)
                mReferenceID.setText(refundsentdata.getReferenceId().substring(0, 10) + "...");
            else
                mReferenceID.setText(refundsentdata.getReferenceId());

            mReferenceCopy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Utils.copyText(refundsentdata.getReferenceId(), TransactionDetailsActivity.this);
                }
            });
        }

        if (refundsentdata.getMerchantId() != null) {
            mMerchantAccountID.setText(refundsentdata.getMerchantId());
        }

        if (refundsentdata.getDbaName() != null) {
            if (refundsentdata.getDbaName().length() > 20)
                mDbaName.setText(refundsentdata.getDbaName().substring(0, 20) + "...");
            else
                mDbaName.setText(refundsentdata.getDbaName());
        }

        if (refundsentdata.getCustomerServiceMail() != null) {
            if (refundsentdata.getCustomerServiceMail().length() > 20) {
                mCustomerServiceEmail.setText(refundsentdata.getCustomerServiceMail().substring(0, 20) + "...");
            } else {
                mCustomerServiceEmail.setText(refundsentdata.getCustomerServiceMail());
            }
        }
        if (refundsentdata.getCustomerServicePhoneNo() != null) {
            String phone_number = "(" + refundsentdata.getCustomerServicePhoneNo().substring(0, 3) + ")" + " " + refundsentdata.getCustomerServicePhoneNo().substring(3, 6) + "-" + refundsentdata.getCustomerServicePhoneNo().substring(6, 10);
            mCustomerServicePhone.setText(phone_number);
        }

//        String mVar = getString(R.string.description);
//        SpannableString spannableString = new SpannableString(mVar);
//        ClickableSpan clickableSpan = new ClickableSpan() {
//            @Override
//            public void onClick(@NonNull View view) {
//                try {
//                    startActivity(new Intent(TransactionDetailsActivity.this, GetHelpWebViewActivity.class));
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void updateDrawState(@NonNull TextPaint ds) {
//                super.updateDrawState(ds);
//                ds.setUnderlineText(true);
//                ds.setColor(getColor(R.color.primary_color));
//            }
//        };
//
//        spannableString.setSpan(clickableSpan, mVar.length() - 8, mVar.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        mDescription.setText(spannableString);
//        mDescription.setMovementMethod(LinkMovementMethod.getInstance());
//        mDescription.setHighlightColor(Color.TRANSPARENT);

        if (refundsentdata.getSaleOrderNetAmount() != null) {
            mAmountPaid.setText(Utils.convertTwoDecimal(refundsentdata.getSaleOrderNetAmount().replace("CYN", "").trim()) + " CYN");
        }
        if (refundsentdata.getSaleOrderDateAndTime() != null) {
            mDateAndTime.setText(objMyApplication.convertZoneLatestTxn(refundsentdata.getSaleOrderDateAndTime()));
        }

        if (refundsentdata.getSaleOrderReferenceId() != null) {
            if (refundsentdata.getSaleOrderReferenceId().length() > 10) {
                String refId = refundsentdata.getSaleOrderReferenceId().substring(0, 10) + "...";
                mSaleReferenceID.setText(Html.fromHtml("<u>" + refId + "</u>"));
            } else
                mSaleReferenceID.setText(Html.fromHtml("<u>" + refundsentdata.getSaleOrderReferenceId() + "</u>"));
        }
        String saleOrderId = refundsentdata.getSaleOrderReferenceId();
        mSaleReferenceID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TransactionDetailsActivity.this, MerchantTransactionDetailsActivity.class)
                        .putExtra(Utils.SELECTED_MERCHANT_TRANSACTION_TXN_TYPE, Utils.saleOrdertxntype)
                        .putExtra(Utils.SELECTED_MERCHANT_TRANSACTION_TXN_SUB_TYPE, Utils.tokensub)
                        .putExtra(Utils.SELECTED_MERCHANT_TRANSACTION_GBX_ID, saleOrderId));
            }
        });
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
//        mBackButton.setOnClickListener(view -> finish());


    }

    private void payRequest(TransactionData objData) {
        try {
            TextView headerTV, amount, descrptn, completed, datetime, fee, total, balance;
            TextView refid, name, accountadress;
            LinearLayout lyPRClose, lyRefId, lyAccAdd;
            headerTV = findViewById(R.id.headerTV);
            amount = findViewById(R.id.amountTV);
            descrptn = findViewById(R.id.descrptnTV);
            completed = findViewById(R.id.payreqStatusTV);
            datetime = findViewById(R.id.dateTimeTV);
            fee = findViewById(R.id.feeTV);
            total = findViewById(R.id.totalTV);
            balance = findViewById(R.id.balanceTV);
            refid = findViewById(R.id.refidTV);
            name = findViewById(R.id.nameTV);
            accountadress = findViewById(R.id.accAddrsTV);
            lyPRClose = findViewById(R.id.lyPRClose);
            lyRefId = findViewById(R.id.lyRefId);
            lyAccAdd = findViewById(R.id.lyAccAdd);


            if (objData.getTransactionType() != null && objData.getTransactionSubtype() != null) {
                headerTV.setText(objData.getTransactionType() + " - " + objData.getTransactionSubtype().replace("Tokens", ""));
            }

            if (objData.getTransactionSubtype() != null) {
                if (objData.getTransactionSubtype().equals("Sent")) {
                    amount.setText(Utils.convertTwoDecimal(objData.getAmount().replace("CYN", "").trim()));
                    //                amount.setText(objData.getAmount().replace("CYN", "").trim());
                    name.setText(objData.getRecipientName());
                    accountadress.setText((objData.getRecipientWalletAddress().substring(0, 10)) + "...");
                    fee.setText(Utils.convertTwoDecimal(objData.getProcessingFee().replace("CYN", "").trim()) + " CYN");
                    total.setText(Utils.convertTwoDecimal(objData.getTotalAmount().replace("CYN", "").trim()) + " CYN");
                    lyAccAdd.setOnClickListener(view -> Utils.copyText(objData.getRecipientWalletAddress(), TransactionDetailsActivity.this));
                } else {
                    amount.setText(Utils.convertTwoDecimal(objData.getAmountReceived().replace("CYN", "").trim()));
                    findViewById(R.id.payreqTAmountLL).setVisibility(View.GONE);
                    findViewById(R.id.payreqPfLL).setVisibility(View.GONE);
                    name.setText(objData.getSenderName());
                    accountadress.setText((objData.getSenderWalletAddress().substring(0, 10) + "..."));
                    lyAccAdd.setOnClickListener(view -> Utils.copyText(objData.getSenderWalletAddress(), TransactionDetailsActivity.this));
                }
            }


            if (objData.getSenderName() != null && !objData.getSenderMessage().equals("")) {
                descrptn.setText("\"" + objData.getSenderMessage() + "\"");
            } else {
                descrptn.setVisibility(View.GONE);
            }


            if (objData.getCreatedDate() != null) {
                datetime.setText(objMyApplication.convertZoneLatestTxn(objData.getCreatedDate()));
            }

            if (objData.getAccountBalance() != null) {
                balance.setText(Utils.convertTwoDecimal(objData.getAccountBalance().replace("CYN", "").trim()) + " CYN");
            }

            if (objData.getReferenceId() != null) {

                if (objData.getReferenceId().length() > 10) {
                    refid.setText(objData.getReferenceId().substring(0, 10) + "...");
                } else {
                    refid.setText(objData.getReferenceId());
                }

                lyRefId.setOnClickListener(view -> Utils.copyText(objData.getReferenceId(), TransactionDetailsActivity.this));

            }

            if (objData.getStatus() != null) {
                completed.setText(objData.getStatus());
                switch (objData.getStatus().toLowerCase()) {
                    case Utils.transCompleted:
                        completed.setTextColor(getResources().getColor(R.color.completed_status));
                        completed.setBackgroundResource(R.drawable.txn_completed_bg);
                        break;
                    case Utils.transinprogress:
                        completed.setTextColor(getResources().getColor(R.color.inprogress_status));
                        completed.setBackgroundResource(R.drawable.txn_inprogress_bg);
                        break;
                    case Utils.transPending:
                        completed.setTextColor(getResources().getColor(R.color.pending_status));
                        completed.setBackgroundResource(R.drawable.txn_pending_bg);
                        break;
                    case Utils.transFailed:
                        completed.setTextColor(getResources().getColor(R.color.failed_status));
                        completed.setBackgroundResource(R.drawable.txn_failed_bg);
                        break;
                }
            }

            lyPRClose.setOnClickListener(view -> onBackPressed());

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void buyTokenCreditDebit(TransactionData objData) {
        TextView headerTV, amount, status, datetime, fee, total, balance, purchaseAmountTV;
        TextView refid, name, descriptorName, cardNumber, expiryDate, depositIDTV;
        LinearLayout lyPRClose;
        ImageView refIdIV, depositIDIV, cardBrandIV;
        LinearLayout depositID, referenceID;
        CardView card_view_activity_log;
//        TextView activity_log_tv;

        headerTV = findViewById(R.id.headTV);
        amount = findViewById(R.id.tvAmount);
        status = findViewById(R.id.statusTV);
        datetime = findViewById(R.id.datetimeTV);
        purchaseAmountTV = findViewById(R.id.purchaseTV);
        fee = findViewById(R.id.processingFeeTV);
        total = findViewById(R.id.totalAmountTV);
        balance = findViewById(R.id.accBalTV);
        depositID = findViewById(R.id.depositIDLL);
        referenceID = findViewById(R.id.referenceIDLL);
        refid = findViewById(R.id.referenceIDTV);
        descriptorName = findViewById(R.id.descriNameTV);
        name = findViewById(R.id.cardHoldernameTV);
        purchaseTime = findViewById(R.id.purchaseTime);
        cardNumber = findViewById(R.id.cardnumTV);
        card_view_activity_log = findViewById(R.id.cv_activity_log);
        activity_log_tv = findViewById(R.id.activity_log_tv);
        expiryDate = findViewById(R.id.expdateTV);
        successadd = findViewById(R.id.message_tv);
        depositIDTV = findViewById(R.id.depositid);
        lyPRClose = findViewById(R.id.previous);
        cardBrandIV = findViewById(R.id.cardBrandIV);


        if (objData.getTransactionType() != null && objData.getTransactionSubtype() != null) {
            headerTV.setText(objData.getTransactionType() + " - " + objData.getTransactionSubtype());
        }

        if (objData.getYouGet() != null) {
            amount.setText(Utils.convertTwoDecimal(objData.getYouGet().replace("CYN", "").trim()));
        }

        if (objData.getStatus() != null) {
            status.setText(objData.getStatus());

            switch (objData.getStatus().toLowerCase()) {
                case Utils.transCompleted:
                    status.setTextColor(getResources().getColor(R.color.completed_status));
                    status.setBackgroundResource(R.drawable.txn_completed_bg);
                    card_view_activity_log.setVisibility(View.VISIBLE);
                    getActivityLogAPICall();
                    break;
                case Utils.transinprogress:
                    status.setTextColor(getResources().getColor(R.color.inprogress_status));
                    status.setBackgroundResource(R.drawable.txn_inprogress_bg);
                    break;
                case Utils.transPending:
                    status.setTextColor(getResources().getColor(R.color.pending_status));
                    status.setBackgroundResource(R.drawable.txn_pending_bg);
                    break;
                case Utils.transFailed:
                    status.setTextColor(getResources().getColor(R.color.failed_status));
                    status.setBackgroundResource(R.drawable.txn_failed_bg);
                    break;
            }
        }

        if (objData.getCreatedDate() != null) {
            datetime.setText(objMyApplication.convertZoneLatestTxn(objData.getCreatedDate()));
        }

        if (objData.getYouPay() != null) {
            purchaseAmountTV.setText(objData.getYouPay());
        }

        if (objData.getCardBrand() != null) {
            switch (objData.getCardBrand()) {
                case Utils.MASTERCARD:
                    cardBrandIV.setImageResource(R.drawable.ic_master);
                    break;
                case Utils.VISA:
                    cardBrandIV.setImageResource(R.drawable.ic_visa);
                    break;
                case Utils.AMERICANEXPRESS:
                    cardBrandIV.setImageResource(R.drawable.ic_amex);
                    break;
                case Utils.DISCOVER:
                    cardBrandIV.setImageResource(R.drawable.ic_discover);
                    break;
            }
        }

        if (objData.getCreatedDate() != null) {
            datetime.setText(objMyApplication.convertZoneLatestTxn(objData.getCreatedDate()));
        }


        if (objData.getYouGet() != null && objData.getProcessingFee() != null) {
            Double purchaseAmount = Double.parseDouble(objData.getYouGet().replace("CYN", "").trim());
            Double processingFee = Double.parseDouble(objData.getProcessingFee().replace("USD", "").trim());
            purchaseAmountTV.setText("$" + Utils.convertTwoDecimal(objData.getYouGet().replace("CYN", "").trim()));
            fee.setText("$" + Utils.convertTwoDecimal((objData.getProcessingFee().replace("USD", "").trim())));
            total.setText("$" + Utils.convertTwoDecimal(String.valueOf(purchaseAmount + processingFee)));
        }

        if (objData.getAccountBalance() != null) {
            balance.setText(Utils.convertTwoDecimal((objData.getAccountBalance().replace("CYN", "").trim())) + " CYN");
        }

        if (objData.getReferenceId() != null) {
            if (objData.getReferenceId().length() > 10) {
                refid.setText(objData.getReferenceId().substring(0, 10) + "...");
            } else {
                refid.setText(objData.getReferenceId());
            }

            referenceID.setOnClickListener(view -> Utils.copyText(objData.getReferenceId(), TransactionDetailsActivity.this));

        }

        if (objData.getDescriptorName() != null) {
            descriptorName.setText(objData.getDescriptorName());
        }

        if (objData.getCardHolderName() != null) {
            name.setText(objData.getCardHolderName());
        }

        if (objData.getCardNumber() != null) {
            cardNumber.setText("\u2022\u2022\u2022\u2022" + objData.getCardNumber().substring(objData.getCardNumber().length() - 4));
        }

        if (objData.getCardExpiryDate() != null) {
            expiryDate.setText(objData.getCardExpiryDate());
        }

        if (objData.getDepositId() != null) {
            if (objData.getDepositId().length() > 10) {
                depositIDTV.setText(objData.getDepositId().substring(0, 10) + "...");
            } else {
                depositIDTV.setText(objData.getDepositId());
            }

            depositID.setOnClickListener(view -> Utils.copyText(objData.getDepositId(), TransactionDetailsActivity.this));
        }

        lyPRClose.setOnClickListener(view -> onBackPressed());


    }

    private void buyTokenBankAccount(TransactionData objData) {
        TextView headerTV, amount, status, datetime, fee, total, balance, purchaseAmount, messageTv, chargeback, heading_activity;
        TextView refId, name, accountAddress, descriptorName, depositIDTV, bankAccNumTV, bankNameTV, nameOnAccTV;
        LinearLayout lyPRClose, btBankReference, btBankDepositID, descriptorLL;
        ImageView previousBtn, depositIDIV;
        CardView activityLog;

        headerTV = findViewById(R.id.btbankheaderTV);
        amount = findViewById(R.id.btbankamountTV);
        status = findViewById(R.id.btbankStatusTV);
        datetime = findViewById(R.id.btbankDatetimeTV);
        purchaseAmount = findViewById(R.id.btBankpurchaseamntTV);
        fee = findViewById(R.id.btBankprocessingfeeTV);
        total = findViewById(R.id.btbankTotalTV);
        refId = findViewById(R.id.btbankRefidTV);
        btBankReference = findViewById(R.id.btbankReferenceLL);
        btBankDepositID = findViewById(R.id.btbankDepositIDLL);
        descriptorName = findViewById(R.id.btbankDescrptorTV);
        depositIDTV = findViewById(R.id.btbankDepositIDTV);
        lyPRClose = findViewById(R.id.btbankprevious);
        bankAccNumTV = findViewById(R.id.btbankaccountTV);
        bankNameTV = findViewById(R.id.btbanknameTV);
        nameOnAccTV = findViewById(R.id.btbanknameACTV);
        cancelTxnCV = findViewById(R.id.cancelTxnCV);
        purchaseTime = findViewById(R.id.purchaseTime);
        descriptorLL = findViewById(R.id.descriptorLL);

        successadd = findViewById(R.id.message_tv);
        activityLog = findViewById(R.id.cv_activity_log);
        heading_activity = findViewById(R.id.tv_activity_log);


        if (objData.getTransactionType() != null && objData.getTransactionSubtype() != null) {
            headerTV.setText(objData.getTransactionType() + " - " + objData.getTransactionSubtype());
        }

        if (objData.getYouGet() != null) {
            amount.setText(objData.getYouGet().replace("CYN", "").trim());
        }
        cancelTxnCV.setVisibility(View.GONE);

        if (objData.getStatus() != null) {
            status.setText(objData.getStatus());
            switch (objData.getStatus().toLowerCase()) {
                case Utils.transCompleted:
                    status.setTextColor(getResources().getColor(R.color.completed_status));
                    status.setBackgroundResource(R.drawable.txn_completed_bg);
                    heading_activity.setVisibility(View.VISIBLE);
                    activityLog.setVisibility(View.VISIBLE);
                    getActivityLogAPICall();
                    break;
                case Utils.transinprogress:
                    status.setTextColor(getResources().getColor(R.color.inprogress_status));
                    status.setBackgroundResource(R.drawable.txn_inprogress_bg);
                    cancelTxnCV.setVisibility(View.VISIBLE);
                    break;
                case Utils.transPending:
                    status.setTextColor(getResources().getColor(R.color.pending_status));
                    status.setBackgroundResource(R.drawable.txn_pending_bg);
                    break;
                case Utils.transFailed:
                    status.setTextColor(getResources().getColor(R.color.failed_status));
                    status.setBackgroundResource(R.drawable.txn_failed_bg);
                    break;
                case Utils.transCancelled: {
                    status.setTextColor(getResources().getColor(R.color.failed_status));
                    status.setBackgroundResource(R.drawable.txn_failed_bg);
                    if (descriptorLL.getVisibility() == View.VISIBLE) {
                        descriptorLL.setVisibility(View.GONE);
                    }
                }
                break;
            }
        }

        if (objData.getCreatedDate() != null) {
            datetime.setText(objMyApplication.convertZoneLatestTxn(objData.getCreatedDate()));
        }

        if (objData.getYouGet() != null) {
            purchaseAmount.setText("$" + Utils.convertTwoDecimal(objData.getYouGet().replace("CYN", "").trim()));
        }

        if (objData.getProcessingFee() != null) {
            fee.setText("$" + Utils.convertTwoDecimal(objData.getProcessingFee().replace("USD", "").trim()));
        }

        if (objData.getYouPay() != null) {
            total.setText("$" + Utils.convertTwoDecimal(objData.getYouPay().replace("USD", "").trim()));
        }

        if (objData.getBankAccountNumber() != null && objData.getBankAccountNumber().length() > 4) {
            bankAccNumTV.setText("\u2022\u2022\u2022\u2022" + objData.getBankAccountNumber().substring(objData.getBankAccountNumber().length() - 4));
        }

        if (objData.getReferenceId() != null) {
            if (objData.getReferenceId().length() > 10) {
                refId.setText(objData.getReferenceId().substring(0, 10) + "...");
            } else {
                refId.setText(objData.getReferenceId());
            }

            btBankReference.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Utils.copyText(objData.getReferenceId(), TransactionDetailsActivity.this);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        if (objData.getDescriptorName() != null) {
            descriptorName.setText(objData.getDescriptorName());
        }

        if (objData.getDepositId() != null) {
            if (objData.getDepositId().length() > 10) {
                depositIDTV.setText(objData.getDepositId().substring(0, 10) + "...");
            } else {
                depositIDTV.setText(objData.getDepositId());
            }
            btBankDepositID.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Utils.copyText(objData.getDepositId(), TransactionDetailsActivity.this);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        if (objData.getBankName() != null) {
            bankNameTV.setText(objData.getBankName());
        }

        if (objData.getNameOnBankAccount() != null) {
            nameOnAccTV.setText(objData.getNameOnBankAccount());
        }

        cancelTxnCV.setOnClickListener(view -> {
            cancelPopup();
        });

        lyPRClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    private void buyTokenSignet(TransactionData objData) {
        TextView headerText, amount, status, date, purchaseAmount, processingFee, totalAmount, depositID, referenceID, descriptorName, nameOnAccount, walletID;
        LinearLayout copyRefID, copyDepositId;
        headerText = findViewById(R.id.buySignetHeaderTV);
        amount = findViewById(R.id.signetAmountTV);
        status = findViewById(R.id.signetStatusTV);
        date = findViewById(R.id.signetdateTimeTV);
        purchaseAmount = findViewById(R.id.signetPurchaseTV);
        processingFee = findViewById(R.id.signetProcessingFeeTV);
        totalAmount = findViewById(R.id.signetTotalAmountTV);
        depositID = findViewById(R.id.signetDepositIDTV);
        referenceID = findViewById(R.id.signetReferIDTV);
        descriptorName = findViewById(R.id.signetDescNameTV);
        nameOnAccount = findViewById(R.id.signetNameOnAccountTV);
        walletID = findViewById(R.id.signetWalletIdTV);
        copyRefID = findViewById(R.id.copyRefID);
        copyDepositId = findViewById(R.id.copyDepositID);


    }

    private void withdrawGiftCard(TransactionData objData) {
        TextView headerMsdTV, amountTV;
        TextView status, dateAndTime, withGiftCardName, subtotal, fee, grandTotal, refId, withId, recipientName, email;
        LinearLayout previous, giftCardWithdrawID, giftCardReferenceID;

        headerMsdTV = findViewById(R.id.withGiftheadTV);
        amountTV = findViewById(R.id.withdrawGiftamount);
        status = findViewById(R.id.withdrawGiftStatusTV);
        dateAndTime = findViewById(R.id.withGiftdateTimeTV);
        withGiftCardName = findViewById(R.id.withdrawGiftcardnameTV);
        subtotal = findViewById(R.id.withdrawGiftsubtotatlTV);
        fee = findViewById(R.id.withdrawGiftprofeeTV);
        grandTotal = findViewById(R.id.withdrawGiftgrandtotalTV);
        refId = findViewById(R.id.withdrawGiftrefidTV);
        withId = findViewById(R.id.withgiftid);
        recipientName = findViewById(R.id.withGiftRecipientNameTV);
        email = findViewById(R.id.withGiftReciEmailTV);
        giftCardWithdrawID = findViewById(R.id.giftCardwithdrawIDLL);
        giftCardReferenceID = findViewById(R.id.giftcardReferenceIDLL);
        previous = findViewById(R.id.withGiftprevious);

        if (objData.getTransactionType() != null && objData.getTransactionSubtype() != null) {
            headerMsdTV.setText(objData.getTransactionType() + " - " + objData.getTransactionSubtype());
        }

        if (objData.getGiftCardAmount() != null) {
            amountTV.setText(Utils.convertTwoDecimal(objData.getGiftCardAmount().toUpperCase().replace("USD", "").trim()));
        }

        if (objData.getStatus() != null) {
            status.setText(objData.getStatus());
            switch (objData.getStatus().toLowerCase()) {
                case Utils.transCompleted:
                    status.setTextColor(getResources().getColor(R.color.completed_status));
                    status.setBackgroundResource(R.drawable.txn_completed_bg);
                    break;
                case Utils.transinprogress:
                    status.setTextColor(getResources().getColor(R.color.inprogress_status));
                    status.setBackgroundResource(R.drawable.txn_inprogress_bg);
                    break;
                case Utils.transPending:
                    status.setTextColor(getResources().getColor(R.color.pending_status));
                    status.setBackgroundResource(R.drawable.txn_pending_bg);
                    break;
                case Utils.transFailed:
                case Utils.transCancelled:
                    status.setTextColor(getResources().getColor(R.color.failed_status));
                    status.setBackgroundResource(R.drawable.txn_failed_bg);
                    break;
            }
        }

        if (objData.getGiftCardName() != null) {
            withGiftCardName.setText(objData.getGiftCardName());
        }

        if (objData.getReferenceId() != null) {
            if (objData.getReferenceId().length() > 10) {
                refId.setText(objData.getReferenceId().substring(0, 10) + "...");
            } else {
                refId.setText(objData.getReferenceId());
            }

            giftCardReferenceID.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Utils.copyText(objData.getReferenceId(), TransactionDetailsActivity.this);
                }
            });
        }

        if (objData.getRecipientName() != null) {
            recipientName.setText(Utils.capitalize(objData.getRecipientName()));
        }

        if (objData.getRecipientEmail() != null) {
            email.setText(objData.getRecipientEmail());
        }

        Double subtotall = null;
        if (objData.getTotalPaidAmount() != null) {
            subtotall = Double.parseDouble(objData.getTotalPaidAmount().replace("CYN", "").trim());
            grandTotal.setText("" + Utils.convertTwoDecimal(String.valueOf(subtotall)));
        }

        if (objData.getGiftCardAmount() != null) {
            subtotal.setText("" + Utils.convertTwoDecimal(objData.getGiftCardAmount().toUpperCase().replace("USD", "").trim()));
        }

        if (objData.getGiftCardFee() != null) {
            fee.setText("" + Utils.convertTwoDecimal(objData.getGiftCardFee().replace(" CYN", "").trim()));
        }

        if (objData.getCreatedDate() != null) {
            dateAndTime.setText(objMyApplication.convertZoneLatestTxn(objData.getCreatedDate()));
        }

        if (objData.getWithdrawId() != null) {
            if (objData.getWithdrawId().length() > 10) {
                withId.setText(objData.getWithdrawId().substring(0, 10) + "...");
            } else {
                withId.setText(objData.getWithdrawId());
            }
            giftCardWithdrawID.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Utils.copyText(objData.getWithdrawId(), TransactionDetailsActivity.this);
                }
            });
        }
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


    }

    private void withdrawInstant(TransactionData objData) {
        LinearLayout previous, withdrawID, referenceID;
        TextView withHeader, withAmount, withDescription, withStatus, withDateTime, withWithdrawalAmount, withProcessFee, withTotal, withAccountBal, withWithdrawalId, withRefId;
        TextView withCardHolderName, withCardNumber, withExpiryDate;
        ImageView withCardBrand;

        previous = findViewById(R.id.withInstantprevious);
        withHeader = findViewById(R.id.withinheaderTV);
        withAmount = findViewById(R.id.withinamount);
        withDescription = findViewById(R.id.withindescrptnTV);
        withStatus = findViewById(R.id.withinstatus);
        withDateTime = findViewById(R.id.withindateTimeTV);
        withWithdrawalAmount = findViewById(R.id.withinwithdrawamount);
        withProcessFee = findViewById(R.id.withinprocessingfee);
        withTotal = findViewById(R.id.withintotalamount);
        withAccountBal = findViewById(R.id.withinaccountbal);
        withWithdrawalId = findViewById(R.id.withinwithdrawidTV);
        withRefId = findViewById(R.id.withinrefid);
        withdrawID = findViewById(R.id.withiwithdrawalLL);
        referenceID = findViewById(R.id.withiReferenceIDLL);
        withCardHolderName = findViewById(R.id.withincardholdername);
        withCardBrand = findViewById(R.id.withincardbrandIV);
        withCardNumber = findViewById(R.id.withincardnumTV);
        withExpiryDate = findViewById(R.id.withinexpdateTV);


        if (objData.getTransactionType() != null && objData.getTransactionSubtype() != null) {
            withHeader.setText(objData.getTransactionType() + " - " + objData.getTransactionSubtype());
        }

        if (objData.getReceivedAmount() != null) {
            withAmount.setText(Utils.convertTwoDecimal(objData.getReceivedAmount().replace("USD", "").trim()));
        }

        if (objData.getRemarks() != null && !objData.getRemarks().equals("")) {
            withDescription.setText("\"" + objData.getRemarks() + "\"");
        } else {
            withDescription.setVisibility(View.GONE);
        }

        if (objData.getStatus() != null) {
            withStatus.setText(objData.getStatus());
            switch (objData.getStatus().toLowerCase()) {
                case Utils.transCompleted:
                    withStatus.setTextColor(getResources().getColor(R.color.completed_status));
                    withStatus.setBackgroundResource(R.drawable.txn_completed_bg);
                    break;
                case Utils.transinprogress:
                case Utils.transInProgress:
                    withStatus.setTextColor(getResources().getColor(R.color.inprogress_status));
                    withStatus.setBackgroundResource(R.drawable.txn_inprogress_bg);
                    break;
                case Utils.transPending:
                    withStatus.setTextColor(getResources().getColor(R.color.pending_status));
                    withStatus.setBackgroundResource(R.drawable.txn_pending_bg);
                    break;
                case Utils.transFailed:
                    withStatus.setTextColor(getResources().getColor(R.color.failed_status));
                    withStatus.setBackgroundResource(R.drawable.txn_failed_bg);
                    break;
            }
        }

        if (objData.getCreatedDate() != null) {
            withDateTime.setText(objMyApplication.convertZoneLatestTxn(objData.getCreatedDate()));
        }

        if (objData.getWithdrawAmount() != null) {
            withWithdrawalAmount.setText(Utils.convertTwoDecimal(objData.getWithdrawAmount().replace("CYN", "").trim()) + " CYN");
        }

        if (objData.getProcessingFee() != null) {
            withProcessFee.setText(Utils.convertTwoDecimal(objData.getProcessingFee().replace("CYN", "").trim()) + " CYN");
        }

        if (objData.getTotalAmount() != null) {
            withTotal.setText(Utils.convertTwoDecimal(objData.getTotalAmount().replace("CYN", "").trim()) + " CYN");
        }


        if (objMyApplication.getAccountType() == Utils.PERSONAL_ACCOUNT) {
            if (objData.getAccountBalance() != null) {
                withAccountBal.setText(Utils.convertTwoDecimal(objData.getAccountBalance().replace("CYN", "").trim()) + " CYN");
            }
        } else {
            findViewById(R.id.account_balance_ll).setVisibility(View.GONE);
        }

        if (objData.getWithdrawalId() != null) {
            if (objData.getWithdrawalId().length() > 10) {
                withWithdrawalId.setText(objData.getWithdrawalId().substring(0, 10) + "...");
            } else {
                withWithdrawalId.setText(objData.getWithdrawalId());
            }

            withdrawID.setOnClickListener(view -> Utils.copyText(objData.getWithdrawalId(), TransactionDetailsActivity.this));
        }

        if (objData.getReferenceId() != null) {
            if (objData.getReferenceId().length() > 10) {
                withRefId.setText(objData.getReferenceId().substring(0, 10) + "...");
            } else {
                withRefId.setText(objData.getReferenceId());
            }

            referenceID.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Utils.copyText(objData.getReferenceId(), TransactionDetailsActivity.this);
                }
            });
        }

        if (objData.getCardHolderName() != null) {
            withCardHolderName.setText(objData.getCardHolderName());
        }

        if (objData.getCardNumber() != null) {
            withCardNumber.setText("\u2022\u2022\u2022\u2022" + objData.getCardNumber().substring(objData.getCardNumber().length() - 4));
        }

        if (objData.getCardBrand() != null) {
            switch (objData.getCardBrand()) {
                case Utils.MASTERCARD:
                    withCardBrand.setImageResource(R.drawable.ic_master);
                    break;
                case Utils.VISA:
                    withCardBrand.setImageResource(R.drawable.ic_visa);
                    break;
                case Utils.AMERICANEXPRESS:
                    withCardBrand.setImageResource(R.drawable.ic_amex);
                    break;
                case Utils.DISCOVER:
                    withCardBrand.setImageResource(R.drawable.ic_discover);
                    break;
            }
        }

        if (objData.getCardExpiryDate() != null) {
            withExpiryDate.setText(objData.getCardExpiryDate());
        }

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    private void withdrawBank(TransactionData objData) {
        TextView withBankHeader, withBankAmount, withBankDescription, withBankStatus, withBankDateTime, withBankWithdrawalAmount, withBankProcessingFee, withBankTotal, withBankAccountBal, withBankWithdrawalId, withBankRefId;
        TextView withBankNameOnAccount, withBankName, withBankAccount;
        LinearLayout withBankCloseLL, withBankWithdrawalID, withBankReference;
        CardView cvCancelWB;

        withBankHeader = findViewById(R.id.withBankHeaderTV);
        withBankAmount = findViewById(R.id.withBankAmount);
        withBankDescription = findViewById(R.id.withBankDescrptnTV);
        withBankStatus = findViewById(R.id.withBankStatusTV);
        withBankDateTime = findViewById(R.id.withbankDatetimeTV);
        withBankWithdrawalAmount = findViewById(R.id.withBankWithdrawAmount);
        withBankProcessingFee = findViewById(R.id.withBankProcessFee);
        withBankTotal = findViewById(R.id.withBankTotalTV);
        withBankAccountBal = findViewById(R.id.withBankAccBalanceTV);
        withBankWithdrawalId = findViewById(R.id.withBankWithdrawidTV);
        withBankRefId = findViewById(R.id.withBankReferenceIDTV);
        withBankWithdrawalID = findViewById(R.id.withBankWithdrawalIDLL);
        withBankReference = findViewById(R.id.withBankReferenceIDLL);
        withBankNameOnAccount = findViewById(R.id.withBankNameOnAccountTV);
        withBankName = findViewById(R.id.withBankBanknameTV);
        withBankAccount = findViewById(R.id.withBankbankaccountTV);

        withBankCloseLL = findViewById(R.id.withbankCloseLL);
        cvCancelWB = findViewById(R.id.cvCancelWB);


        if (objData.getTransactionType() != null && objData.getTransactionSubtype() != null) {
            withBankHeader.setText(objData.getTransactionType() + " - " + objData.getTransactionSubtype());
        }

        if (objData.getWithdrawAmount() != null) {
            if (objData.getWithdrawAmount().toLowerCase().contains("cyn")) {
                withBankAmount.setText(Utils.convertTwoDecimal(objData.getWithdrawAmount().replace("CYN", "").trim()));
            } else {
                withBankAmount.setText(Utils.convertTwoDecimal(objData.getWithdrawAmount().replace("USD", "").trim()));

            }
        }

        if (objData.getRemarks() != null && !objData.getRemarks().equals("")) {
            withBankDescription.setText("\"" + objData.getRemarks() + "\"");
        } else {
            withBankDescription.setVisibility(View.GONE);

        }
        cvCancelWB.setVisibility(View.GONE);
        if (objData.getStatus() != null) {
            withBankStatus.setText(objData.getStatus());
            switch (objData.getStatus().toLowerCase()) {
                case Utils.transCompleted:
                    withBankStatus.setTextColor(getResources().getColor(R.color.completed_status));
                    withBankStatus.setBackgroundResource(R.drawable.txn_completed_bg);
                    getActivityLogAPICall();
                    break;
                case Utils.transinprogress:
                case Utils.transInProgress:
                    withBankStatus.setTextColor(getResources().getColor(R.color.inprogress_status));
                    withBankStatus.setBackgroundResource(R.drawable.txn_inprogress_bg);
                    cvCancelWB.setVisibility(View.VISIBLE);
                    break;
                case Utils.transPending:
                    withBankStatus.setTextColor(getResources().getColor(R.color.pending_status));
                    withBankStatus.setBackgroundResource(R.drawable.txn_pending_bg);
                    break;
                case Utils.transFailed:
                    withBankStatus.setTextColor(getResources().getColor(R.color.failed_status));
                    withBankStatus.setBackgroundResource(R.drawable.txn_failed_bg);
                    break;
                case Utils.transCancelled: {
                    withBankStatus.setTextColor(getResources().getColor(R.color.failed_status));
                    withBankStatus.setBackgroundResource(R.drawable.txn_failed_bg);
                }
            }
        }

        if (objData.getCreatedDate() != null) {
            withBankDateTime.setText(objMyApplication.convertZoneLatestTxn(objData.getCreatedDate()));
        }
//
//        Double subtotal = Double.parseDouble(objData.getWithdrawAmount().replace("USD", "").trim());
//        Double procesFee = Double.parseDouble(objData.getProcessingFee().replace("CYN", "").trim());

//        withBankWithdrawalAmount.setText("" + Utils.convertTwoDecimal(objData.getWithdrawAmount().replace("USD", "").trim()));
        if (objData.getWithdrawAmount() != null) {
            if (objData.getWithdrawAmount().toLowerCase().contains("cyn")) {
                withBankWithdrawalAmount.setText(Utils.convertTwoDecimal(objData.getWithdrawAmount().replace("CYN", "").trim()));
            } else {
                withBankWithdrawalAmount.setText(Utils.convertTwoDecimal(objData.getWithdrawAmount().replace("USD", "").trim()));

            }
        }

        if (objData.getProcessingFee() != null) {
            withBankProcessingFee.setText("" + Utils.convertTwoDecimal(objData.getProcessingFee().replace("CYN", "").trim()));
        }

        if (objData.getTotalAmount() != null) {
            withBankTotal.setText("" + Utils.convertTwoDecimal(objData.getTotalAmount().replace("CYN", "").trim()));
        }

        if (objData.getAccountBalance() != null)
            withBankAccountBal.setText(Utils.convertTwoDecimal(objData.getAccountBalance().replace("CYN", "").trim()) + " CYN");

        if (objData.getWithdrawalId() != null) {
            if (objData.getWithdrawalId().length() > 10) {
                withBankWithdrawalId.setText(objData.getWithdrawalId().substring(0, 10) + "...");
            } else {
                withBankWithdrawalId.setText(objData.getWithdrawalId());
            }

            withBankWithdrawalID.setOnClickListener(view -> Utils.copyText(objData.getWithdrawalId(), TransactionDetailsActivity.this));

        }

        if (objData.getReferenceId() != null) {
            if (objData.getReferenceId().length() > 10) {
                withBankRefId.setText(objData.getReferenceId().substring(0, 10) + "...");
            } else {
                withBankRefId.setText(objData.getReferenceId());
            }

            withBankReference.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Utils.copyText(objData.getReferenceId(), TransactionDetailsActivity.this);
                }
            });
        }

        if (objData.getNameOnBankAccount() != null) {
            withBankNameOnAccount.setText(objData.getNameOnBankAccount());
        }

        if (objData.getBankName() != null) {
            withBankName.setText(objData.getBankName());
        }


        if (objData.getBankAccountNumber() != null && objData.getBankAccountNumber().length() > 4) {
            withBankAccount.setText("\u2022\u2022\u2022\u2022" + objData.getBankAccountNumber().substring(objData.getBankAccountNumber().length() - 4));
        }

        cvCancelWB.setOnClickListener(view -> {
            cancelPopup();
        });

        withBankCloseLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void withdrawSignet(TransactionData objData) {

        TextView withBankHeader, withBankAmount, withBankDescription, withBankStatus, withBankDateTime, withBankWithdrawalAmount, withBankProcessingFee, withBankTotal, withBankAccountBal, withBankWithdrawalId, withBankRefId;
        TextView withBankNameOnAccount, withBankName, withBankAccount, signetTextTV;
//        ImageView withbankwithdrawalid, withbankrefIDIV;
        LinearLayout withBankCloseLL, withBankWithdrawalID, withBankReference;

        withBankHeader = findViewById(R.id.withBankHeaderTV);
        withBankAmount = findViewById(R.id.withBankAmount);
        withBankDescription = findViewById(R.id.withBankDescrptnTV);
        withBankStatus = findViewById(R.id.withBankStatusTV);
        withBankDateTime = findViewById(R.id.withbankDatetimeTV);
        withBankWithdrawalAmount = findViewById(R.id.withBankWithdrawAmount);
        withBankProcessingFee = findViewById(R.id.withBankProcessFee);
        withBankTotal = findViewById(R.id.withBankTotalTV);
        withBankAccountBal = findViewById(R.id.withBankAccBalanceTV);
        withBankWithdrawalId = findViewById(R.id.withBankWithdrawidTV);
        withBankRefId = findViewById(R.id.withBankReferenceIDTV);
        withBankWithdrawalID = findViewById(R.id.withBankWithdrawalIDLL);
        withBankReference = findViewById(R.id.withBankReferenceIDLL);
        withBankNameOnAccount = findViewById(R.id.withBankNameOnAccountTV);
        withBankName = findViewById(R.id.withBankBanknameTV);
        withBankAccount = findViewById(R.id.withBankbankaccountTV);
        signetTextTV = findViewById(R.id.bankNameORSignetTV);
        withBankCloseLL = findViewById(R.id.withbankCloseLL);


        if (objData.getTransactionType() != null && objData.getTransactionSubtype() != null) {
            withBankHeader.setText(objData.getTransactionType() + " - " + objData.getTransactionSubtype());
        }

        if (objData.getWithdrawAmount() != null) {
            withBankAmount.setText(Utils.convertTwoDecimal(objData.getWithdrawAmount().replace("CYN", "").trim()));
        }

        if (objData.getStatus() != null) {
            withBankStatus.setText(objData.getStatus());
            switch (objData.getStatus().toLowerCase()) {
                case Utils.transCompleted:
                    withBankStatus.setTextColor(getResources().getColor(R.color.completed_status));
                    withBankStatus.setBackgroundResource(R.drawable.txn_completed_bg);
                    break;
                case Utils.transinprogress:
                case Utils.transInProgress:
                    withBankStatus.setTextColor(getResources().getColor(R.color.inprogress_status));
                    withBankStatus.setBackgroundResource(R.drawable.txn_inprogress_bg);
                    break;
                case Utils.transPending:
                    withBankStatus.setTextColor(getResources().getColor(R.color.pending_status));
                    withBankStatus.setBackgroundResource(R.drawable.txn_pending_bg);
                    break;
                case Utils.transFailed:
                case Utils.transCancelled:
                    withBankStatus.setTextColor(getResources().getColor(R.color.failed_status));
                    withBankStatus.setBackgroundResource(R.drawable.txn_failed_bg);
                    break;
            }
        }

        if (objData.getCreatedDate() != null) {
            withBankDateTime.setText(objMyApplication.convertZoneLatestTxn(objData.getCreatedDate()));
        }


        if (objData.getWithdrawAmount() != null) {
            withBankWithdrawalAmount.setText("" + Utils.convertTwoDecimal(objData.getWithdrawAmount().replace("CYN", "").trim()));
        }

        if (objData.getProcessingFee() != null) {
            withBankProcessingFee.setText("" + Utils.convertTwoDecimal(objData.getProcessingFee().replace("CYN", "").trim()));
        }

        if (objData.getTotalAmount() != null) {
            withBankTotal.setText("" + Utils.convertTwoDecimal(objData.getTotalAmount().replace("CYN", "").trim()));
        }

        if (objData.getAccountBalance() != null) {
            withBankAccountBal.setText(Utils.convertTwoDecimal(objData.getAccountBalance().replace("CYN", "").trim()) + " CYN");
        }

        if (objData.getWithdrawalId() != null) {
            if (objData.getWithdrawalId().length() > 10) {
                withBankWithdrawalId.setText(objData.getWithdrawalId().substring(0, 10) + "...");
            } else {
                withBankWithdrawalId.setText(objData.getWithdrawalId());
            }

            withBankWithdrawalID.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Utils.copyText(objData.getWithdrawalId(), TransactionDetailsActivity.this);
                }
            });
        }


        if (objData.getReferenceId() != null) {
            if (objData.getReferenceId().length() > 10)
                withBankRefId.setText(objData.getReferenceId().substring(0, 10) + "...");
            else {
                withBankRefId.setText(objData.getReferenceId());
            }

            withBankReference.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Utils.copyText(objData.getReferenceId(), TransactionDetailsActivity.this);
                }
            });
        }

        if (objData.getNameOnBank() != null) {
            withBankNameOnAccount.setText(objData.getNameOnBank());
        }

        signetTextTV.setText("Signet Wallet ID");
//        signetTextTV.setPadding(0, 0, 0, 20);
//        findViewById(R.id.nameOnAccount).setPadding(0, 10, 0, 0);
//        findViewById(R.id.withdrawIDTV).setPadding(30, 30, 0, 0);
        if (objData.getWalletId() != null) {
            if (objData.getWalletId().length() > 20) {
                withBankName.setText(objData.getWalletId().substring(0, 20) + "...");
            } else {
                withBankName.setText(objData.getWalletId());
            }
        }

        findViewById(R.id.bankAccountLL).setVisibility(View.GONE);


        withBankCloseLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        if (withBankDescription.getVisibility() == View.VISIBLE) {
            withBankDescription.setVisibility(View.GONE);
        }

    }

    private void businessPayout(TransactionData businessPayoutData) {
        try {
            TextView headerNameTV, amountTV, statusTV, dateAndTime, payoutID, refID, payoutDate, totalAmount, totalTransactions, depositID;
            LinearLayout copyPayoutID, copyReferenceIDLL;
            String payoutId = "";

            headerNameTV = findViewById(R.id.mPayoutheaderTV);
            amountTV = findViewById(R.id.merchantamountTV);
            statusTV = findViewById(R.id.merchantstatusTV);
            dateAndTime = findViewById(R.id.merchantdateTv);
            payoutID = findViewById(R.id.mPayoutIdTV);
            copyPayoutID = findViewById(R.id.copyPayoutIDLL);
            copyReferenceIDLL = findViewById(R.id.copuRefIDLL);
            refID = findViewById(R.id.mreferenceIdTV);
            payoutDate = findViewById(R.id.merchantPIdateTV);
            totalAmount = findViewById(R.id.mPItotalamountTV);
            totalTransactions = findViewById(R.id.mPItotaltransactionsTV);
            depositID = findViewById(R.id.mPIdeposittoTV);

            if (businessPayoutData.getTransactionType() != null) {
                headerNameTV.setText(businessPayoutData.getTransactionType());
            }

            if (businessPayoutData.getTotalAmount() != null) {
                amountTV.setText(Utils.convertTwoDecimal(businessPayoutData.getTotalAmount().replace("CYN", "").trim()));
            }

            if (businessPayoutData.getStatus() != null) {
                statusTV.setText(businessPayoutData.getStatus());
                switch (businessPayoutData.getStatus().toLowerCase()) {
                    case Utils.transCompleted:
                        statusTV.setTextColor(getResources().getColor(R.color.completed_status));
                        statusTV.setBackgroundResource(R.drawable.txn_completed_bg);
                        break;
                    case Utils.transinprogress:
                    case Utils.transInProgress:
                        statusTV.setTextColor(getResources().getColor(R.color.inprogress_status));
                        statusTV.setBackgroundResource(R.drawable.txn_inprogress_bg);
                        break;
                    case Utils.transPending:
                        statusTV.setTextColor(getResources().getColor(R.color.pending_status));
                        statusTV.setBackgroundResource(R.drawable.txn_pending_bg);
                        break;
                    case Utils.transFailed:
                    case Utils.transCancelled:
                        statusTV.setTextColor(getResources().getColor(R.color.failed_status));
                        statusTV.setBackgroundResource(R.drawable.txn_failed_bg);
                        break;
                }
            }

            if (businessPayoutData.getCreatedDate() != null) {
                dateAndTime.setText(objMyApplication.convertZoneLatestTxn(businessPayoutData.getCreatedDate()));
            }

            if (businessPayoutData.getPayoutId() != null) {
                if (businessPayoutData.getPayoutId().length() > 10) {
                    payoutId = businessPayoutData.getPayoutId().substring(0, 10) + "...";
                    payoutID.setText(Html.fromHtml("<u>" + payoutId + "</u>"));
                } else {
                    //                payoutID.setText(businessPayoutData.getPayoutId());
                    payoutId = businessPayoutData.getPayoutId();
                    payoutID.setText(Html.fromHtml("<u>" + payoutId + "</u>"));
                }

                copyPayoutID.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            Utils.copyText(businessPayoutData.getPayoutId(), TransactionDetailsActivity.this);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            if (businessPayoutData.getReferenceId() != null) {
                if (businessPayoutData.getReferenceId().length() > 10) {
                    refID.setText(businessPayoutData.getReferenceId().substring(0, 10) + "...");
                } else {
                    refID.setText(businessPayoutData.getReferenceId());
                }

                copyReferenceIDLL.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            Utils.copyText(businessPayoutData.getReferenceId(), TransactionDetailsActivity.this);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }


//            if (businessPayoutData.getPayoutCreatedDate() != null && businessPayoutData.getPayoutUpdatedDate() != null) {
//                String startDate = objMyApplication.convertPayoutDateTimeZone(businessPayoutData.getPayoutCreatedDate());
//                String endDate = objMyApplication.convertPayoutDateTimeZone(businessPayoutData.getPayoutUpdatedDate());
//
//                payoutDate.setText(startDate + " to " + endDate);
//            }
            if (businessPayoutData.getPayoutCreatedDate() != null && businessPayoutData.getPayoutUpdatedDate() != null) {
                String startDate = objMyApplication.convertZoneLatestTxndate(businessPayoutData.getPayoutCreatedDate()).toUpperCase();
                String endDate = objMyApplication.convertZoneLatestTxndate(businessPayoutData.getPayoutUpdatedDate()).toUpperCase();

                payoutDate.setText(startDate + " to " + endDate);
            }

            if (businessPayoutData.getTotalAmount() != null) {
                totalAmount.setText(Utils.convertTwoDecimal(businessPayoutData.getTotalAmount().replace("CYN", "").trim()) + " ");
            }

            if (businessPayoutData.getTotalTransactions() != null) {
                totalTransactions.setText(businessPayoutData.getTotalTransactions());
            }


            if (businessPayoutData.getDepositTo() != null) {
                if (businessPayoutData.getDepositTo().length() > 10) {
                    depositID.setText(businessPayoutData.getDepositTo().substring(0, 10) + "...");
                    depositID.setPaintFlags(depositID.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

                } else {
                    depositID.setText(businessPayoutData.getDepositTo());
                    depositID.setPaintFlags(depositID.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

                }
            }

            findViewById(R.id.Mpayoutll).setOnClickListener(view -> finish());


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void failedWithdraw(TransactionData failedData) {
        try {
            TextView transactionTypeTV, amountTV, statusTV, dateAndTime, refID, reasonFailed, achWithTV, achRefID, withdrawAmountTV, processingFeeTV, totalAmountTV;
            LinearLayout copyReferenceIDLL, copyACHWithID;

            transactionTypeTV = findViewById(R.id.headerTxtTV);
            amountTV = findViewById(R.id.amountTV);
            statusTV = findViewById(R.id.statusTV);
            dateAndTime = findViewById(R.id.dateAndTimeTV);
            refID = findViewById(R.id.referenceIDTV);
            copyReferenceIDLL = findViewById(R.id.copyRefIDLL);
            reasonFailed = findViewById(R.id.reasonForFailed);
            achWithTV = findViewById(R.id.ACHWithdrawalIDTV);
            copyACHWithID = findViewById(R.id.copyACHWithdrawalIDTV);
            achRefID = findViewById(R.id.achReferenceIDTV);
            withdrawAmountTV = findViewById(R.id.withdrawAmountTV);
            processingFeeTV = findViewById(R.id.withBankProcessFee);
            totalAmountTV = findViewById(R.id.totalAmountTV);

            if (failedData.getTransactionType() != null) {
                transactionTypeTV.setText(failedData.getTransactionType());
            }


            amountTV.setText(Utils.convertTwoDecimal(failedData.getWithdrawAmount().replace("CYN", "").trim()));


            if (failedData.getStatus() != null) {
                statusTV.setText(failedData.getStatus());
                switch (failedData.getStatus().toLowerCase()) {
                    case Utils.transCompleted:
                        statusTV.setTextColor(getResources().getColor(R.color.completed_status));
                        statusTV.setBackgroundResource(R.drawable.txn_completed_bg);
                        break;
                    case Utils.transinprogress:
                    case Utils.transInProgress:
                        statusTV.setTextColor(getResources().getColor(R.color.inprogress_status));
                        statusTV.setBackgroundResource(R.drawable.txn_inprogress_bg);
                        break;
                    case Utils.transPending:
                        statusTV.setTextColor(getResources().getColor(R.color.pending_status));
                        statusTV.setBackgroundResource(R.drawable.txn_pending_bg);
                        break;
                    case Utils.transFailed:
                    case Utils.transCancelled:
                        statusTV.setTextColor(getResources().getColor(R.color.failed_status));
                        statusTV.setBackgroundResource(R.drawable.txn_failed_bg);
                        break;
                }
            }

            if (failedData.getCreatedDate() != null) {
                dateAndTime.setText(objMyApplication.convertZoneLatestTxn(failedData.getCreatedDate()));
            }

            if (failedData.getReferenceId() != null) {
                if (failedData.getReferenceId().length() > 10) {
                    refID.setText(failedData.getReferenceId().substring(0, 10) + "...");
                } else {
                    refID.setText(failedData.getReferenceId());
                }

                copyReferenceIDLL.setOnClickListener(view -> {
                    try {
                        Utils.copyText(failedData.getReferenceId(), TransactionDetailsActivity.this);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }


            if (failedData.getFailedReason() != null) {
                reasonFailed.setText(failedData.getFailedReason());
            }

            if (failedData.getAchReferenceId() != null) {
                if (failedData.getWithdrawId().length() > 10) {
                    achWithTV.setText(failedData.getWithdrawId().substring(0, 10) + "...");
                } else {
                    achWithTV.setText(failedData.getWithdrawId());
                }

                copyACHWithID.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            Utils.copyText(failedData.getWithdrawId(), TransactionDetailsActivity.this);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            String achRefId = "";
            if (failedData.getAchReferenceId() != null) {
                if (failedData.getAchReferenceId().length() > 10) {
                    achRefId = failedData.getAchReferenceId().substring(0, 10) + "...";
                    achRefID.setText(Html.fromHtml("<u>" + achRefId + "</u>"));
                } else {
                    achRefId = failedData.getAchReferenceId();
                    achRefID.setText(Html.fromHtml("<u>" + achRefId + "</u>"));
                }
            }

            if (failedData.getWithdrawAmount() != null) {
                withdrawAmountTV.setText(Utils.convertTwoDecimal(failedData.getWithdrawAmount().replace("CYN", "").trim()));
            }

            if (failedData.getProcessingFee() != null) {
                processingFeeTV.setText(Utils.convertTwoDecimal(failedData.getProcessingFee().replace("CYN", "").trim()));
            }

            if (failedData.getTotalAmount() != null) {
                totalAmountTV.setText(Utils.convertTwoDecimal(failedData.getTotalAmount().replace("CYN", "").trim()));
            }
        } catch (Resources.NotFoundException | NumberFormatException e) {
            e.printStackTrace();
        }


    }

    private void cancelledWithdraw(TransactionData cancelledData) {
        try {
            TextView transactionTypeTV, amountTV, statusTV, dateAndTime, refID, reasonFailed, achWithTV, achRefID, withdrawAmountTV, processingFeeTV, totalAmountTV;
            LinearLayout copyReferenceIDLL, copyACHWithID;

            transactionTypeTV = findViewById(R.id.headerTxtTV);
            amountTV = findViewById(R.id.amountTV);
            statusTV = findViewById(R.id.statusTV);
            dateAndTime = findViewById(R.id.dateAndTimeTV);
            refID = findViewById(R.id.referenceIDTV);
            copyReferenceIDLL = findViewById(R.id.copyRefIDLL);
            reasonFailed = findViewById(R.id.reasonForFailed);
            achWithTV = findViewById(R.id.ACHWithdrawalIDTV);
            copyACHWithID = findViewById(R.id.copyACHWithdrawalIDTV);
            achRefID = findViewById(R.id.achReferenceIDTV);
            withdrawAmountTV = findViewById(R.id.withdrawAmountTV);
            processingFeeTV = findViewById(R.id.withBankProcessFee);
            totalAmountTV = findViewById(R.id.totalAmountTV);

            if (cancelledData.getTransactionType() != null) {
                transactionTypeTV.setText(cancelledData.getTransactionType());
            }


            amountTV.setText(Utils.convertTwoDecimal(cancelledData.getWithdrawAmount().replace("CYN", "").trim()));


            if (cancelledData.getStatus() != null) {
                statusTV.setText(cancelledData.getStatus());
                switch (cancelledData.getStatus().toLowerCase()) {
                    case Utils.transCompleted:
                        statusTV.setTextColor(getResources().getColor(R.color.completed_status));
                        statusTV.setBackgroundResource(R.drawable.txn_completed_bg);
                        break;
                    case Utils.transinprogress:
                    case Utils.transInProgress:
                        statusTV.setTextColor(getResources().getColor(R.color.inprogress_status));
                        statusTV.setBackgroundResource(R.drawable.txn_inprogress_bg);
                        break;
                    case Utils.transPending:
                        statusTV.setTextColor(getResources().getColor(R.color.pending_status));
                        statusTV.setBackgroundResource(R.drawable.txn_pending_bg);
                        break;
                    case Utils.transFailed:
                    case Utils.transCancelled:
                        statusTV.setTextColor(getResources().getColor(R.color.failed_status));
                        statusTV.setBackgroundResource(R.drawable.txn_failed_bg);
                        break;
                }
            }


            if (cancelledData.getCreatedDate() != null) {
                dateAndTime.setText(objMyApplication.convertZoneLatestTxn(cancelledData.getCreatedDate()));
            }

            if (cancelledData.getReferenceId() != null) {
                if (cancelledData.getReferenceId().length() > 10)
                    refID.setText(cancelledData.getReferenceId().substring(0, 10) + "...");
                else {
                    refID.setText(cancelledData.getReferenceId());
                }

                copyReferenceIDLL.setOnClickListener(view -> {
                    try {
                        Utils.copyText(cancelledData.getReferenceId(), TransactionDetailsActivity.this);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }

            findViewById(R.id.failedReTV).setVisibility(View.GONE);
            reasonFailed.setVisibility(View.GONE);

            if (cancelledData.getAchReferenceId() != null) {
                if (cancelledData.getWithdrawId().length() > 10) {
                    achWithTV.setText(cancelledData.getWithdrawId().substring(0, 10) + "...");
                } else {
                    achWithTV.setText(cancelledData.getWithdrawId());
                }

                copyACHWithID.setOnClickListener(view -> {
                    try {
                        Utils.copyText(cancelledData.getWithdrawId(), TransactionDetailsActivity.this);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }


            String achRefId;
            if (cancelledData.getAchReferenceId() != null) {
                if (cancelledData.getAchReferenceId().length() > 10) {
                    achRefId = cancelledData.getAchReferenceId().substring(0, 10) + "...";
                    achRefID.setText(Html.fromHtml("<u>" + achRefId + "</u>"));
                } else {
                    achRefId = cancelledData.getAchReferenceId();
                    achRefID.setText(Html.fromHtml("<u>" + achRefId + "</u>"));
                }
            }

            if (cancelledData.getWithdrawAmount() != null) {
                withdrawAmountTV.setText(Utils.convertTwoDecimal(cancelledData.getWithdrawAmount().replace("CYN", "").trim()));
            }

            if (cancelledData.getProcessingFee() != null) {
                processingFeeTV.setText(Utils.convertTwoDecimal(cancelledData.getProcessingFee().replace("CYN", "").trim()));
            }

            if (cancelledData.getTotalAmount() != null) {
                totalAmountTV.setText(Utils.convertTwoDecimal(cancelledData.getTotalAmount().replace("CYN", "").trim()));
            }

        } catch (Resources.NotFoundException | NumberFormatException e) {
            e.printStackTrace();
        }

    }

    private void ControlMethod(String methodToShow) {
        try {
            switch (methodToShow) {
                case PAY_REQUEST: {
                    findViewById(R.id.payrequest).setVisibility(View.VISIBLE);
                    findViewById(R.id.buytokenCD).setVisibility(View.GONE);
                    findViewById(R.id.buytokenBank).setVisibility(View.GONE);
                    findViewById(R.id.withdrawGift).setVisibility(View.GONE);
                    findViewById(R.id.withdrawInstant).setVisibility(View.GONE);
                    findViewById(R.id.withdrawBank).setVisibility(View.GONE);
                    findViewById(R.id.buyTokenSignet).setVisibility(View.GONE);
                    findViewById(R.id.businessPayout).setVisibility(View.GONE);
                    findViewById(R.id.failedWithdrawBankAcc).setVisibility(View.GONE);
                    findViewById(R.id.paidOrderToken).setVisibility(View.GONE);
                    findViewById(R.id.reserve_release_details).setVisibility(View.GONE);
                }
                break;
                case BUY_TOKEN: {
                    findViewById(R.id.payrequest).setVisibility(View.GONE);
                    findViewById(R.id.buytokenCD).setVisibility(View.VISIBLE);
                    findViewById(R.id.buytokenBank).setVisibility(View.GONE);
                    findViewById(R.id.withdrawGift).setVisibility(View.GONE);
                    findViewById(R.id.withdrawInstant).setVisibility(View.GONE);
                    findViewById(R.id.withdrawBank).setVisibility(View.GONE);
                    findViewById(R.id.buyTokenSignet).setVisibility(View.GONE);
                    findViewById(R.id.businessPayout).setVisibility(View.GONE);
                    findViewById(R.id.failedWithdrawBankAcc).setVisibility(View.GONE);
                    findViewById(R.id.paidOrderToken).setVisibility(View.GONE);
                    findViewById(R.id.reserve_release_details).setVisibility(View.GONE);
                }
                break;
                case BUY_BANK: {
                    findViewById(R.id.payrequest).setVisibility(View.GONE);
                    findViewById(R.id.buytokenCD).setVisibility(View.GONE);
                    findViewById(R.id.buytokenBank).setVisibility(View.VISIBLE);
                    findViewById(R.id.withdrawGift).setVisibility(View.GONE);
                    findViewById(R.id.withdrawInstant).setVisibility(View.GONE);
                    findViewById(R.id.withdrawBank).setVisibility(View.GONE);
                    findViewById(R.id.buyTokenSignet).setVisibility(View.GONE);
                    findViewById(R.id.businessPayout).setVisibility(View.GONE);
                    findViewById(R.id.failedWithdrawBankAcc).setVisibility(View.GONE);
                    findViewById(R.id.paidOrderToken).setVisibility(View.GONE);
                    findViewById(R.id.reserve_release_details).setVisibility(View.GONE);
                }
                break;
                case BUY_SIGNET: {
                    findViewById(R.id.payrequest).setVisibility(View.GONE);
                    findViewById(R.id.buytokenCD).setVisibility(View.GONE);
                    findViewById(R.id.buytokenBank).setVisibility(View.GONE);
                    findViewById(R.id.withdrawGift).setVisibility(View.GONE);
                    findViewById(R.id.withdrawInstant).setVisibility(View.GONE);
                    findViewById(R.id.withdrawBank).setVisibility(View.GONE);
                    findViewById(R.id.buyTokenSignet).setVisibility(View.VISIBLE);
                    findViewById(R.id.businessPayout).setVisibility(View.GONE);
                    findViewById(R.id.failedWithdrawBankAcc).setVisibility(View.GONE);
                    findViewById(R.id.paidOrderToken).setVisibility(View.GONE);
                    findViewById(R.id.reserve_release_details).setVisibility(View.GONE);
                }
                break;
                case WITH_GIFT: {
                    findViewById(R.id.payrequest).setVisibility(View.GONE);
                    findViewById(R.id.buytokenCD).setVisibility(View.GONE);
                    findViewById(R.id.buytokenBank).setVisibility(View.GONE);
                    findViewById(R.id.withdrawGift).setVisibility(View.VISIBLE);
                    findViewById(R.id.withdrawInstant).setVisibility(View.GONE);
                    findViewById(R.id.withdrawBank).setVisibility(View.GONE);
                    findViewById(R.id.buyTokenSignet).setVisibility(View.GONE);
                    findViewById(R.id.businessPayout).setVisibility(View.GONE);
                    findViewById(R.id.failedWithdrawBankAcc).setVisibility(View.GONE);
                    findViewById(R.id.paidOrderToken).setVisibility(View.GONE);
                    findViewById(R.id.reserve_release_details).setVisibility(View.GONE);
                }
                break;
                case WITH_Instant: {
                    findViewById(R.id.payrequest).setVisibility(View.GONE);
                    findViewById(R.id.buytokenCD).setVisibility(View.GONE);
                    findViewById(R.id.buytokenBank).setVisibility(View.GONE);
                    findViewById(R.id.withdrawGift).setVisibility(View.GONE);
                    findViewById(R.id.withdrawInstant).setVisibility(View.VISIBLE);
                    findViewById(R.id.withdrawBank).setVisibility(View.GONE);
                    findViewById(R.id.buyTokenSignet).setVisibility(View.GONE);
                    findViewById(R.id.businessPayout).setVisibility(View.GONE);
                    findViewById(R.id.failedWithdrawBankAcc).setVisibility(View.GONE);
                    findViewById(R.id.paidOrderToken).setVisibility(View.GONE);
                    findViewById(R.id.reserve_release_details).setVisibility(View.GONE);
                }
                break;
                case WITH_BANK:
                case WITH_SIGNET: {
                    findViewById(R.id.payrequest).setVisibility(View.GONE);
                    findViewById(R.id.buytokenCD).setVisibility(View.GONE);
                    findViewById(R.id.buytokenBank).setVisibility(View.GONE);
                    findViewById(R.id.withdrawGift).setVisibility(View.GONE);
                    findViewById(R.id.withdrawInstant).setVisibility(View.GONE);
                    findViewById(R.id.withdrawBank).setVisibility(View.VISIBLE);
                    findViewById(R.id.buyTokenSignet).setVisibility(View.GONE);
                    findViewById(R.id.businessPayout).setVisibility(View.GONE);
                    findViewById(R.id.failedWithdrawBankAcc).setVisibility(View.GONE);
                    findViewById(R.id.paidOrderToken).setVisibility(View.GONE);
                    findViewById(R.id.reserve_release_details).setVisibility(View.GONE);
                }
                break;
                case BUSINESS_PAYOUT: {
                    findViewById(R.id.payrequest).setVisibility(View.GONE);
                    findViewById(R.id.buytokenCD).setVisibility(View.GONE);
                    findViewById(R.id.buytokenBank).setVisibility(View.GONE);
                    findViewById(R.id.withdrawGift).setVisibility(View.GONE);
                    findViewById(R.id.withdrawInstant).setVisibility(View.GONE);
                    findViewById(R.id.withdrawBank).setVisibility(View.GONE);
                    findViewById(R.id.buyTokenSignet).setVisibility(View.GONE);
                    findViewById(R.id.businessPayout).setVisibility(View.VISIBLE);
                    findViewById(R.id.failedWithdrawBankAcc).setVisibility(View.GONE);
                    findViewById(R.id.paidOrderToken).setVisibility(View.GONE);
                    findViewById(R.id.reserve_release_details).setVisibility(View.GONE);
                }
                break;
                case CANCELLED_WITH:
                case FAILED_WITH: {
                    findViewById(R.id.payrequest).setVisibility(View.GONE);
                    findViewById(R.id.buytokenCD).setVisibility(View.GONE);
                    findViewById(R.id.buytokenBank).setVisibility(View.GONE);
                    findViewById(R.id.withdrawGift).setVisibility(View.GONE);
                    findViewById(R.id.withdrawInstant).setVisibility(View.GONE);
                    findViewById(R.id.withdrawBank).setVisibility(View.GONE);
                    findViewById(R.id.buyTokenSignet).setVisibility(View.GONE);
                    findViewById(R.id.businessPayout).setVisibility(View.GONE);
                    findViewById(R.id.failedWithdrawBankAcc).setVisibility(View.VISIBLE);
                    findViewById(R.id.paidOrderToken).setVisibility(View.GONE);
                    findViewById(R.id.reserve_release_details).setVisibility(View.GONE);
                }
                break;
                case REFUND_SENT:
                case PAID_ORDER_TOKEN:
                case REFUND_RECEIVED: {
                    findViewById(R.id.payrequest).setVisibility(View.GONE);
                    findViewById(R.id.buytokenCD).setVisibility(View.GONE);
                    findViewById(R.id.buytokenBank).setVisibility(View.GONE);
                    findViewById(R.id.withdrawGift).setVisibility(View.GONE);
                    findViewById(R.id.withdrawInstant).setVisibility(View.GONE);
                    findViewById(R.id.withdrawBank).setVisibility(View.GONE);
                    findViewById(R.id.buyTokenSignet).setVisibility(View.GONE);
                    findViewById(R.id.businessPayout).setVisibility(View.GONE);
                    findViewById(R.id.failedWithdrawBankAcc).setVisibility(View.GONE);
                    findViewById(R.id.paidOrderToken).setVisibility(View.VISIBLE);
                    findViewById(R.id.reserve_release_details).setVisibility(View.GONE);
                }
                break;
                case RESERVE_RELEASE: {
                    findViewById(R.id.payrequest).setVisibility(View.GONE);
                    findViewById(R.id.buytokenCD).setVisibility(View.GONE);
                    findViewById(R.id.buytokenBank).setVisibility(View.GONE);
                    findViewById(R.id.withdrawGift).setVisibility(View.GONE);
                    findViewById(R.id.withdrawInstant).setVisibility(View.GONE);
                    findViewById(R.id.withdrawBank).setVisibility(View.GONE);
                    findViewById(R.id.buyTokenSignet).setVisibility(View.GONE);
                    findViewById(R.id.businessPayout).setVisibility(View.GONE);
                    findViewById(R.id.failedWithdrawBankAcc).setVisibility(View.GONE);
                    findViewById(R.id.paidOrderToken).setVisibility(View.GONE);
                    findViewById(R.id.reserve_release_details).setVisibility(View.VISIBLE);
                }
                break;

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void cancelPopup() {
        try {
            final Dialog dialog = new Dialog(TransactionDetailsActivity.this);
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.canceltransaction);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            DisplayMetrics mertics = getResources().getDisplayMetrics();
            int width = mertics.widthPixels;

            TextView tvNo = dialog.findViewById(R.id.tvNo);
            TextView tvYes = dialog.findViewById(R.id.tvYes);

            tvNo.setOnClickListener(v -> dialog.dismiss());
            tvYes.setOnClickListener(v -> {
                try {
                    dialog.dismiss();
//                    progressDialog = Utils.showProgressDialog(TransactionDetailsActivity.this);
                    showProgressDialog();
                    if (txnType == 2)
                        dashboardViewModel.cancelBuyToken(strGbxTxnIdType);
                    else
                        dashboardViewModel.cancelWithdrawToken(strGbxTxnIdType);
                } catch (Exception ex) {
                    ex.printStackTrace();
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

    private void getActivityLogAPICall() {

        if (Utils.checkInternet(TransactionDetailsActivity.this)) {
            if (txnId != null && !txnId.equals("")) {
                if (objMyApplication.getAccountType() == Utils.PERSONAL_ACCOUNT) {
                    dashboardViewModel.getActivityLog(txnId, "c");
                }
            }
        }
    }

}