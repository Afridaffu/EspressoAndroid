package com.greenbox.coyni.view.business;

import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.TypefaceSpan;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.greenbox.coyni.R;
import com.greenbox.coyni.adapters.ReserveDetailsListAdapter;
import com.greenbox.coyni.model.BusinessBatchPayout.BatchPayoutListItems;
import com.greenbox.coyni.model.reserveIdDetails.DetailsRequest;
import com.greenbox.coyni.model.reserveIdDetails.DetailsResponse;
import com.greenbox.coyni.model.reserverule.RollingRuleResponse;
import com.greenbox.coyni.model.transaction.TransactionList;
import com.greenbox.coyni.model.transaction.TransactionListPosted;
import com.greenbox.coyni.model.transaction.TransactionListRequest;
import com.greenbox.coyni.utils.CustomTypefaceSpan;
import com.greenbox.coyni.utils.LogUtils;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.view.BaseActivity;
import com.greenbox.coyni.viewmodel.BusinessDashboardViewModel;
import com.greenbox.coyni.viewmodel.DashboardViewModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ReserveDetailsActivity extends BaseActivity {

    private LinearLayout backButton, lycopy, releasedIDcopy;
    private TextView reserveID, amount, statusTV, releaseDateTime, reserveRegular, cancel, reserveIDTV;
    private TextView releasedAMT, releasedDateTime, transID, tvDBAName, onHoldAmt;
    private TextView noTransactions, noMoreTransactions;
    private RecyclerView recyclerViewRv;
    private CardView details, onHoldCv;
    private CardView released;
    private MyApplication myApplication;
    private String time = "", amt = "";

    private String status = "", reserveAmount = "", batchId = "", timeDate = "", timeDateTemp = "", reserveRules = "", releaseDate = "";

    private DashboardViewModel dashboardViewModel;
    private BusinessDashboardViewModel businessDashboardViewModel;
    private ReserveDetailsListAdapter reserveDetailsListAdapter;
    private List<TransactionListPosted> globalPosted = new ArrayList<>();
    private int totalItemCount, currentPage = 1, total = 0;
    private BatchPayoutListItems selected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserve_details);

        selected = (BatchPayoutListItems) getIntent().getSerializableExtra(Utils.DATA);
//        showProgressDialog();
        initFields();
        initObserver();
        displayingDetails();

    }

    private void initFields() {

        backButton = findViewById(R.id.lyRDClose);
        reserveID = findViewById(R.id.tvReserveID);
        lycopy = findViewById(R.id.lycopy);
        myApplication = (MyApplication) getApplicationContext();
        reserveIDTV = findViewById(R.id.reserveIDTV);
        amount = findViewById(R.id.tvamount);
        statusTV = findViewById(R.id.reserveStatusTV);
        releaseDateTime = findViewById(R.id.releasedateTimeTV);
        cancel = findViewById(R.id.canceledTV);
        reserveRegular = findViewById(R.id.reserveRegularTV);

        releasedAMT = findViewById(R.id.tvreleasedAMT);
        releasedDateTime = findViewById(R.id.ReleasedDateTimeTv);
        transID = findViewById(R.id.tvtransID);
        releasedIDcopy = findViewById(R.id.releasedIDcopyLL);
        tvDBAName = findViewById(R.id.tvDBAName);

        noTransactions = findViewById(R.id.noTransactionsTV);
        noMoreTransactions = findViewById(R.id.noMoreTransactionsTV);

        details = findViewById(R.id.detailsCV);
        onHoldCv = findViewById(R.id.onHoldCV);
        onHoldAmt = findViewById(R.id.onHoldAmtTV);
        released = findViewById(R.id.releasedCV);

        recyclerViewRv = findViewById(R.id.recyclerviewRV);

        backButton.setOnClickListener(v -> finish());

        businessDashboardViewModel = new ViewModelProvider(this).get(BusinessDashboardViewModel.class);
        businessDashboardViewModel.getRollingRuleDetails();

        dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);

        TransactionListRequest transactionListRequest = new TransactionListRequest();
        transactionListRequest.setTransactionType(getDefaultTransactionTypes());
//        transactionListRequest.setTxnStatus(getDefaultStatus());

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        String createdDate = selected.getScheduledRelease();
        String createdDate = selected.getCreatedAt();

        if (createdDate != null) {
            Date date = null;
            try {
                date = dateFormat.parse(createdDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Calendar fromDate = Calendar.getInstance();
            fromDate.setTime(date);
            fromDate.set(Calendar.HOUR_OF_DAY, 0);
            fromDate.set(Calendar.MINUTE, 0);
            fromDate.set(Calendar.SECOND, 0);

            Calendar toDate = Calendar.getInstance();
            toDate.setTime(date);
            toDate.set(Calendar.HOUR_OF_DAY, 23);
            toDate.set(Calendar.MINUTE, 59);
            toDate.set(Calendar.SECOND, 59);

            String reqFromDate = "";
            String reqToDate = "";
            try {
                reqFromDate = dateFormat.format(fromDate.getTime());
                reqToDate = dateFormat.format(toDate.getTime());
                transactionListRequest.setUpdatedFromDate(reqFromDate);
                transactionListRequest.setUpdatedFromDateOperator(">=");
                transactionListRequest.setUpdatedToDate(reqToDate);
                transactionListRequest.setUpdatedToDateOperator("<=");
                transactionListRequest.setMerchantTransactions(true);
                Log.e("transactionListRequest", new Gson().toJson(transactionListRequest));
                dashboardViewModel.meTransactionList(transactionListRequest);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void getReserveDetails(String payoutId) {
        DetailsRequest detailsRequest = new DetailsRequest();
        detailsRequest.setPayoutId(payoutId);
        detailsRequest.setTxnType(Utils.reserveRelease);
        businessDashboardViewModel.getReserveIdDetails(detailsRequest);
    }

    private void initObserver() {
        try {
            businessDashboardViewModel.getRollingRuleResponseMutableLiveData().observe(this, new Observer<RollingRuleResponse>() {
                @Override
                public void onChanged(RollingRuleResponse ruleResponse) {
                    if (ruleResponse != null) {
                        if (ruleResponse.getStatus().equalsIgnoreCase(Utils.SUCCESS)) {
                            if (ruleResponse.getData() != null) {
                                reserveRules = ruleResponse.getData().getReserveAmount().split("\\.")[0] + "% / " + ruleResponse.getData().getReservePeriod() + "days " + ruleResponse.getData().getReserveType();

                                if (!reserveRules.equals("") && reserveRules != null) {

                                    reserveRegular.setText(reserveRules);
                                }

                            }
                        } else {
//                            Utils.displayAlert(getString(R.string.something_went_wrong), ReserveDetailsActivity.this, "", ruleResponse.getError().getFieldErrors().get(0));
                        }
                    }

                }
            });

            dashboardViewModel.getTransactionListMutableLiveData().observe(this, new Observer<TransactionList>() {
                @Override
                public void onChanged(TransactionList transactionList) {
                    dismissDialog();
                    try {
                        if (transactionList != null) {
                            if (transactionList.getStatus().equalsIgnoreCase(Utils.SUCCESS)) {
                                LogUtils.d(TAG, "list" + transactionList.getData().getItems().getPostedTransactions());
                                try {
                                    LinearLayoutManager nLayoutManager = new LinearLayoutManager(ReserveDetailsActivity.this);

                                    if (transactionList.getData() != null) {
                                        if (transactionList.getData().getItems() != null) {
                                            if (transactionList.getData().getItems().getPostedTransactions() != null) {
                                                globalPosted.addAll(transactionList.getData().getItems().getPostedTransactions());
                                                LogUtils.v(TAG, " Get Posted Transactions");
                                            }
                                        }
                                    }
                                    if (globalPosted.size() > 0) {
                                        reserveDetailsListAdapter = new ReserveDetailsListAdapter(globalPosted, ReserveDetailsActivity.this);
                                        recyclerViewRv.setLayoutManager(nLayoutManager);
                                        recyclerViewRv.setItemAnimator(new DefaultItemAnimator());
                                        recyclerViewRv.setAdapter(reserveDetailsListAdapter);
                                        noMoreTransactions.setVisibility(View.VISIBLE);
                                        noTransactions.setVisibility(View.GONE);
                                        if (currentPage > 0) {
                                            int myPos = globalPosted.size() - transactionList.getData().getItems().getPostedTransactions().size();
                                            recyclerViewRv.scrollToPosition(myPos);
                                        } else {
                                            recyclerViewRv.scrollToPosition(0);
                                        }
                                    } else {
                                        noMoreTransactions.setVisibility(View.GONE);
                                        noTransactions.setVisibility(View.VISIBLE);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
//                                Utils.displayAlert(transactionList.getError().getErrorDescription(), ReserveDetailsActivity.this, "", transactionList.getError().getFieldErrors().get(0));
                            }
                        } else {
//                            Utils.displayAlert(getString(R.string.something_went_wrong), ReserveDetailsActivity.this, "", transactionList.getError().getFieldErrors().get(0));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            businessDashboardViewModel.getDetailsResponseMutableLiveData().observe(this, new Observer<DetailsResponse>() {
                @Override
                public void onChanged(DetailsResponse detailsResponse) {
                    if (detailsResponse != null) {
                        if (detailsResponse.getStatus().equalsIgnoreCase(Utils.SUCCESS)) {
                            if (detailsResponse.getData() != null) {
                                addData(detailsResponse);
                            }
                        } else {
                            Utils.displayAlert(getString(R.string.something_went_wrong), ReserveDetailsActivity.this, "", detailsResponse.getError().getFieldErrors().get(0));

                        }
                    }

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addData(DetailsResponse detailsResponse) {
        if (detailsResponse.getData().getPayoutReferenceId() != null && !detailsResponse.getData().getPayoutReferenceId().equals("")) {
            if (detailsResponse.getData().getPayoutReferenceId().length() > 14) {
                transID.setText(Html.fromHtml("<u>"+detailsResponse.getData().getPayoutReferenceId().substring(0, 14) + "..."+"</u>"));
            } else {
                transID.setText(Html.fromHtml("<u>"+detailsResponse.getData().getPayoutReferenceId()+"</u>"));
            }
            releasedIDcopy.setOnClickListener(v -> Utils.copyText(detailsResponse.getData().getPayoutReferenceId(), ReserveDetailsActivity.this));

        }
    }

    private ArrayList<Integer> getDefaultTransactionTypes() {
        ArrayList<Integer> transactionType = new ArrayList<>();
        transactionType.add(Utils.saleOrder);
        return transactionType;
    }

    private ArrayList<Integer> getDefaultStatus() {

        ArrayList<Integer> transactionStatus = new ArrayList<>();
        transactionStatus.add(Utils.completed);
        return transactionStatus;
    }

    private void displayingDetails() {

        if (selected.getStatus().equalsIgnoreCase(Utils.RELEASED)) {
            getReserveDetails(selected.getBatchId());
        }

//        if (myApplication.getCurrentUserData().getReserveGBTBalance() != null) {
//            reserveAmount = Utils.convertTwoDecimal(String.valueOf(myApplication.getCurrentUserData().getReserveGBTBalance()));
//            amount.setText(reserveAmount + " CYN");
//        }
        if (selected != null && selected.getCreatedAt() != null) {
            timeDate = selected.getCreatedAt();

            if (timeDate.contains(".")) {
                timeDate = timeDate.substring(0, timeDate.lastIndexOf("."));
            }
            timeDate = myApplication.convertZoneDateTime(timeDate, "yyyy-MM-dd HH:mm:ss", "MM/dd/yyyy @ hh:mm a").toLowerCase();
        }
        releaseDateTime.setText(timeDate);

        if (selected != null && selected.getBatchId() != null) {
            batchId = selected.getBatchId();
            reserveID.setText(batchId);
            lycopy.setOnClickListener(v -> Utils.copyText(batchId, ReserveDetailsActivity.this));

            if (batchId.length() > 14) {
                reserveIDTV.setText(Html.fromHtml("<u>"+batchId.substring(0, 14) + "..."+"</u>"));
            } else {
                reserveIDTV.setText(Html.fromHtml("<u>"+batchId+"</u>"));
            }
        }
        getDateDescription();

        if (selected != null && selected.getStatus() != null) {
            status = selected.getStatus();
            if (!status.equals("")) {
                if (status.equalsIgnoreCase(Utils.RELEASED)) {
                    released.setVisibility(View.VISIBLE);
                    details.setVisibility(View.GONE);
                    onHoldCv.setVisibility(View.GONE);
                    statusTV.setText(Utils.complete);
                    statusTV.setTextColor(getColor(R.color.completed_status));
                    statusTV.setBackgroundResource(R.drawable.txn_completed_bg);
                } else if (status.equalsIgnoreCase(Utils.ONHOLD)) {
                    released.setVisibility(View.GONE);
                    details.setVisibility(View.GONE);
                    onHoldCv.setVisibility(View.VISIBLE);
//                    onHoldAmt.setText(Html.fromHtml(getResources().getString(R.string.release_scheduled_for, "<font color='#151515'><b>" + time + "</b></font>", "<font color='#151515'><b>" + amt + "</b></font>")));
                    String onHold_desc = getResources().getString(R.string.release_scheduled_for, time, amt);

                    //22  - 22+arg1.length;    length - (42 + arg2.length) length - 42
                    SpannableString spannableString = new SpannableString(onHold_desc);
                    Typeface font = Typeface.createFromAsset(getAssets(), "font/opensans_bold.ttf");

                    spannableString.setSpan(new ForegroundColorSpan(getColor(R.color.primary_black)), 22, 22 + time.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                    spannableString.setSpan(new CustomTypefaceSpan("",font), 22, 22 + time.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                    spannableString.setSpan(new ForegroundColorSpan(getColor(R.color.primary_black)), onHold_desc.length() - (1 + amt.length()), onHold_desc.length() - 1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                    spannableString.setSpan(new CustomTypefaceSpan("",font), onHold_desc.length() - (1 + amt.length()), onHold_desc.length() - 1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                    onHoldAmt.setText(spannableString);
                    statusTV.setText(status);
                    statusTV.setTextColor(getColor(R.color.pending_color));
                    statusTV.setBackgroundResource(R.drawable.txn_pending_bg);
                } else {
                    released.setVisibility(View.GONE);
                    details.setVisibility(View.VISIBLE);
                    onHoldCv.setVisibility(View.GONE);
                    String cancel_desc = getResources().getString(R.string.reserve_canceled_description, time, amt);

                    //22  - 22+arg1.length;    length - (64 + arg2.length) length - 64
                    SpannableString spannableString = new SpannableString(cancel_desc);
                    Typeface font = Typeface.createFromAsset(getAssets(), "font/opensans_bold.ttf");

                    spannableString.setSpan(new ForegroundColorSpan(getColor(R.color.primary_black)), 22, 22 + time.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                    spannableString.setSpan(new CustomTypefaceSpan("",font), 22, 22 + time.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                    spannableString.setSpan(new ForegroundColorSpan(getColor(R.color.primary_black)), cancel_desc.length() - (63 + amt.length()), cancel_desc.length() - 63, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                    spannableString.setSpan(new CustomTypefaceSpan("",font), cancel_desc.length() - (63 + amt.length()), cancel_desc.length() - 63, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

                    cancel.setText(spannableString);
                    statusTV.setText(status);
                    statusTV.setTextColor(getColor(R.color.failed_status));
                    statusTV.setBackgroundResource(R.drawable.txn_failed_bg);
                }
            }
        }

        //Released Details
        if (selected != null && selected.getReserveAmount() != null) {
            releasedAMT.setText(selected.getReserveAmount() + " CYN");
            amount.setText(selected.getReserveAmount() + " CYN");
        } else {
            releasedAMT.setText("0.00 CYN");
            amount.setText("0.00 CYN");
        }
        if (selected.getScheduledRelease() != null && !selected.getScheduledRelease().equals("")) {
            releaseDate = selected.getScheduledRelease();

            if (releaseDate.contains(".")) {
                releaseDate = releaseDate.substring(0, releaseDate.lastIndexOf("."));
            }
            releaseDate = myApplication.convertZoneDateTime(releaseDate, "yyyy-MM-dd HH:mm:ss", "MM/dd/yyyy @ hh:mm a").toLowerCase();
            releasedDateTime.setText(releaseDate);
        }
        tvDBAName.setText(myApplication.getMyProfile().getData().getDbaName());

    }

    private void getDateDescription() {
        if (selected != null && selected.getScheduledRelease() != null) {
            timeDateTemp = selected.getScheduledRelease();

            if (timeDateTemp.contains(".")) {
                timeDateTemp = timeDateTemp.substring(0, timeDateTemp.lastIndexOf("."));
            }

            time = myApplication.convertZoneDateTime(timeDateTemp, "yyyy-MM-dd HH:mm:ss", "MM/dd/yyyy");
            amt = Utils.convertTwoDecimal(selected.getReserveAmount()) + " CYN";
        }
    }

}