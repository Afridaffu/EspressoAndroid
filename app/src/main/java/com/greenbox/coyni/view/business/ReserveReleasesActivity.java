package com.greenbox.coyni.view.business;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.greenbox.coyni.R;
import com.greenbox.coyni.adapters.OnItemClickListener;
import com.greenbox.coyni.adapters.ReserveReleaseManualListAdapter;
import com.greenbox.coyni.adapters.ReserveReleasesRollingAdapter;
import com.greenbox.coyni.dialogs.OnDialogClickListener;
import com.greenbox.coyni.dialogs.ReserveReleaseDialog;
import com.greenbox.coyni.dialogs.ReserveReleasesFilterDialog;
import com.greenbox.coyni.model.BusinessBatchPayout.BatchPayoutListItems;
import com.greenbox.coyni.model.BusinessBatchPayout.BatchPayoutListResponse;
import com.greenbox.coyni.model.BusinessBatchPayout.RollingListRequest;
import com.greenbox.coyni.model.reservemanual.ManualListResponse;
import com.greenbox.coyni.model.reservemanual.ReserveFilter;
import com.greenbox.coyni.utils.LogUtils;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.view.BaseActivity;
import com.greenbox.coyni.viewmodel.BusinessDashboardViewModel;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ReserveReleasesActivity extends BaseActivity implements TextWatcher {

    private ImageView ivFilterIcon;
    private LinearLayout closeBtnIV, rollingLL;
    private RecyclerView recyclerViewRolling, rvManualList;
    private SwipeRefreshLayout refreshLayout;
    private EditText searchET;
    private TextView changeName;
    private BusinessDashboardViewModel businessDashboardViewModel;
    private ReserveReleasesRollingAdapter reserveReleasesRollingAdapter;
    private List<BatchPayoutListItems> payoutList = new ArrayList<>();
    private View view;
    private ReserveFilter reserveFilter = new ReserveFilter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserve_releases);

        initFields();
        initObserver();

        showProgressDialog();
        getReserveReleaseData();

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setRefreshing(false);
                ivFilterIcon.setImageDrawable(getDrawable(R.drawable.ic_filtericon));
                searchET.setText("");
                searchET.clearFocus();
                getReserveReleaseData();
            }
        });

        String resp = "{\"status\":\"SUCCESS\",\"timestamp\":\"2022-02-28T10:49:06.398+00:00\",\"data\":{\"items\":[{\"date\":\"2022-02-26 19:26:09.72\",\"referenceId\":\"qwe23423wefdsf922\",\"sentTo\":\"dba 104 Business Token Account\",\"amount\":100,\"reason\":\"manual reserve release reason\"},{\"date\":\"2022-02-26 18:26:09.72\",\"referenceId\":\"gwe23423wefdsf922\",\"sentTo\":\"dba 104 Business Token Account\",\"amount\":108,\"reason\":\"manual reserve release reason\"},{\"date\":\"2022-02-26 15:26:09.72\",\"referenceId\":\"bwe23423wefdsf922\",\"sentTo\":\"dba 104 Business Token Account\",\"amount\":200,\"reason\":\"manual reserve release reason\"},{\"date\":\"2022-02-26 10:26:09.72\",\"referenceId\":\"dwe23423wefdsf922\",\"sentTo\":\"dba 104 Business Token Account\",\"amount\":104,\"reason\":\"manual reserve release reason\"},{\"date\":\"2022-02-26 08:26:09.72\",\"referenceId\":\"awe23423wefdsf922\",\"sentTo\":\"dba 104 Business Token Account\",\"amount\":110,\"reason\":\"manual reserve release reason\"}],\"currentPageNo\":0,\"pageSize\":100,\"totalItems\":5,\"totalPages\":1},\"error\":null}";
        Type type = new TypeToken<ManualListResponse>() {
        }.getType();
        Gson gson = new Gson();
        ManualListResponse respose = gson.fromJson(resp, type);
        LogUtils.v(TAG, "Responce" + respose);

        ReserveReleaseManualListAdapter manualListAdapter = new ReserveReleaseManualListAdapter(this, respose.getData().getItems());
        rvManualList.setAdapter(manualListAdapter);
        rvManualList.setLayoutManager(new LinearLayoutManager(this));


        closeBtnIV.setOnClickListener(view -> onBackPressed());
        ivFilterIcon.setOnClickListener(v -> showFiltersPopup());

        ReserveReleaseDialog dialog = new ReserveReleaseDialog(ReserveReleasesActivity.this);
        rollingLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });

        dialog.setOnDialogClickListener(new OnDialogClickListener() {
            @Override
            public void onDialogClicked(String action, Object value) {
                if (action.equalsIgnoreCase("Rolling")) {
                    ivFilterIcon.setImageDrawable(getDrawable(R.drawable.ic_filtericon));
                    ivFilterIcon.setVisibility(View.VISIBLE);
                    view.setVisibility(View.VISIBLE);
                    changeName.setText(R.string.rolling_releases);
                    recyclerViewRolling.setVisibility(View.VISIBLE);
                    rvManualList.setVisibility(View.GONE);
                    searchET.setText("");
                    searchET.clearFocus();
                    dialog.dismiss();
                } else {
                    ivFilterIcon.setVisibility(View.GONE);
                    recyclerViewRolling.setVisibility(View.GONE);
                    rvManualList.setVisibility(View.VISIBLE);
                    view.setVisibility(View.GONE);
                    changeName.setText(R.string.manual_releases);
                    searchET.setText("");
                    searchET.clearFocus();
                    dialog.dismiss();
                }
            }
        });

    }

    private void getReserveReleaseData() {
        RollingListRequest listRequest = new RollingListRequest();
        ArrayList<Integer> status = new ArrayList<>();
        if (reserveFilter != null && reserveFilter.isFilterApplied) {
            if (reserveFilter.isOpen()) {
                status.add(Utils.ROLLING_LIST_STATUS.OPEN.getStatusType());
            }
            if (reserveFilter.isOnHold()) {
                status.add(Utils.ROLLING_LIST_STATUS.ON_HOLD.getStatusType());
            }
            if (reserveFilter.isReleased()) {
                status.add(Utils.ROLLING_LIST_STATUS.RELEASED.getStatusType());
            }
            if (reserveFilter.isCancelled()) {
                status.add(Utils.ROLLING_LIST_STATUS.CANCELLED.getStatusType());
            }

            //Add dates condition here
        } else {
            status.add(Utils.ROLLING_LIST_STATUS.OPEN.getStatusType());

            status.add(Utils.ROLLING_LIST_STATUS.ON_HOLD.getStatusType());

            status.add(Utils.ROLLING_LIST_STATUS.RELEASED.getStatusType());

            status.add(Utils.ROLLING_LIST_STATUS.CANCELLED.getStatusType());

        }
        listRequest.setRollingStatus(status);
        businessDashboardViewModel.getPayoutListData(listRequest);
    }

    private void initFields() {
        closeBtnIV = findViewById(R.id.closeBtnIV);
        searchET = findViewById(R.id.searchET);
        ivFilterIcon = findViewById(R.id.ivFilterIcon);
        rollingLL = findViewById(R.id.rollingLL);
        recyclerViewRolling = findViewById(R.id.rvRollingList);
        recyclerViewRolling.setLayoutManager(new LinearLayoutManager(ReserveReleasesActivity.this));
        refreshLayout = findViewById(R.id.refreshLayoutRL);
        rvManualList = findViewById(R.id.rvManualList);
        view = findViewById(R.id.viewV);
        changeName = findViewById(R.id.tvChangeName);
        businessDashboardViewModel = new ViewModelProvider(this).get(BusinessDashboardViewModel.class);

    }

    private void initObserver() {
        try {

            businessDashboardViewModel.getBatchPayoutListMutableLiveData().observe(this, new Observer<BatchPayoutListResponse>() {
                @Override
                public void onChanged(BatchPayoutListResponse batchPayoutList) {
                    dismissDialog();
                    if (batchPayoutList != null) {
                        if (batchPayoutList.getStatus().equalsIgnoreCase("SUCCESS")) {
                            if (batchPayoutList.getData().getItems() != null) {
                                payoutList = batchPayoutList.getData().getItems();
                                reserveReleasesRollingAdapter = new ReserveReleasesRollingAdapter(ReserveReleasesActivity.this, payoutList);
                                reserveReleasesRollingAdapter.setOnItemClickListener(new OnItemClickListener() {
                                    @Override
                                    public void onItemClick(int position, Object obj) {
//                                        LogUtils.d(TAG, "onitemclick" + position + obj.toString());
//
//                                        BatchPayoutListItems batchPayoutListItem = (BatchPayoutListItems) obj;


                                    }

                                });
                                recyclerViewRolling.setAdapter(reserveReleasesRollingAdapter);
                            }
                        } else {
                            Utils.displayAlert(getString(R.string.something_went_wrong), ReserveReleasesActivity.this, "", batchPayoutList.getError().getFieldErrors().get(0));

                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void showFiltersPopup() {
        ReserveReleasesFilterDialog showReserveReleaseDialog = new ReserveReleasesFilterDialog(ReserveReleasesActivity.this, reserveFilter);
        showReserveReleaseDialog.setOnDialogClickListener(new OnDialogClickListener() {
            @Override
            public void onDialogClicked(String action, Object value) {
                LogUtils.d(TAG, "onclickkk" + action + value);
                if (action.equalsIgnoreCase("ApplyFilter")) {
                    reserveFilter = (ReserveFilter) value;
                    ivFilterIcon.setImageDrawable(getDrawable(R.drawable.ic_filter_enabled));
                    getReserveReleaseData();
                } else if(action.equalsIgnoreCase("ResetFilter")) {
                    reserveFilter = (ReserveFilter) value;
                    ivFilterIcon.setImageDrawable(getDrawable(R.drawable.ic_filtericon));
                    getReserveReleaseData();
                }
            }
        });
        showReserveReleaseDialog.show();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}