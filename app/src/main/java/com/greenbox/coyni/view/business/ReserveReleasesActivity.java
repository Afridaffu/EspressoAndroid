package com.greenbox.coyni.view.business;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.greenbox.coyni.R;
import com.greenbox.coyni.adapters.OnItemClickListener;
import com.greenbox.coyni.adapters.ReserveReleaseManualListAdapter;
import com.greenbox.coyni.adapters.ReserveReleasesRollingAdapter;
import com.greenbox.coyni.dialogs.ReserveReleaseDialog;
import com.greenbox.coyni.dialogs.OnDialogClickListener;
import com.greenbox.coyni.dialogs.ReserveReleasesFilterDialog;
import com.greenbox.coyni.model.reservemanual.ManualListResponse;
import com.greenbox.coyni.model.reserverolling.RollingListResponse;
import com.greenbox.coyni.utils.LogUtils;
import com.greenbox.coyni.view.BaseActivity;

import java.lang.reflect.Type;

public class ReserveReleasesActivity extends BaseActivity implements TextWatcher {

    private ImageView ivFilterIcon;
    private LinearLayout closeBtnIV, rollingLL;
    private RecyclerView rvRollingList, rvManualList;
    private SwipeRefreshLayout refreshLayout;
    private EditText searchET;
    private TextView changeName;
    private View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserve_releases);

        closeBtnIV = findViewById(R.id.closeBtnIV);
        searchET = findViewById(R.id.searchET);
        ivFilterIcon = findViewById(R.id.ivFilterIcon);
        rollingLL = findViewById(R.id.rollingLL);
        rvRollingList = findViewById(R.id.rvRollingList);
        refreshLayout = findViewById(R.id.refreshLayoutRL);
        rvManualList = findViewById(R.id.rvManualList);
        view = findViewById(R.id.viewV);
        changeName = findViewById(R.id.tvChangeName);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setRefreshing(false);
                ivFilterIcon.setImageDrawable(getDrawable(R.drawable.ic_filtericon));
                searchET.setText("");
                searchET.clearFocus();
            }
        });

        String resList = "{\"status\":\"SUCCESS\",\"timestamp\":\"2022-03-25 07:00:00.0\",\"data\":{\"items\":[{\"batchId\":\"20220325\",\"status\":\"Open\",\"createdAt\":\"2022-03-25 07:00:00.0\",\"totalTransactionsCount\":\"0\",\"totalFee\":\"0.000000\",\"totalAmount\":\"0.000000\",\"sentTo\":\"Coyni Token Account\",\"reserve\":\"0\",\"reserveId\":\"RF439220840700\",\"scheduledRelease\":\"2022-03-29 07:00:00.0\",\"reserveAmount\":10},{\"batchId\":\"20220325\",\"status\":\"On Hold\",\"createdAt\":\"2022-03-25 07:00:00.0\",\"totalTransactionsCount\":\"0\",\"totalFee\":\"0.000000\",\"totalAmount\":\"0.000000\",\"sentTo\":\"Coyni Token Account\",\"reserve\":\"0\",\"reserveId\":\"RF439220840700\",\"scheduledRelease\":\"2022-03-29 07:00:00.0\",\"reserveAmount\":10},{\"batchId\":\"20220325\",\"status\":\"Released\",\"createdAt\":\"2022-03-25 07:00:00.0\",\"totalTransactionsCount\":\"0\",\"totalFee\":\"0.000000\",\"totalAmount\":\"0.000000\",\"sentTo\":\"Coyni Token Account\",\"reserve\":\"0\",\"reserveId\":\"RF439220840700\",\"scheduledRelease\":\"2022-03-29 07:00:00.0\",\"reserveAmount\":10},{\"batchId\":\"20220325\",\"status\":\"Released\",\"createdAt\":\"2022-03-25 07:00:00.0\",\"totalTransactionsCount\":\"0\",\"totalFee\":\"0.000000\",\"totalAmount\":\"0.000000\",\"sentTo\":\"Coyni Token Account\",\"reserve\":\"0\",\"reserveId\":\"RF439220840700\",\"scheduledRelease\":\"2022-03-29 07:00:00.0\",\"reserveAmount\":10},{\"batchId\":\"20220325\",\"status\":\"Canceled\",\"createdAt\":\"2022-03-25 07:00:00.0\",\"totalTransactionsCount\":\"0\",\"totalFee\":\"0.000000\",\"totalAmount\":\"0.000000\",\"sentTo\":\"Coyni Token Account\",\"reserve\":\"0\",\"reserveId\":\"RF439220840700\",\"scheduledRelease\":\"2022-03-29 07:00:00.0\",\"reserveAmount\":10}],\"currentPageNo\":0,\"pageSize\":100,\"totalItems\":5,\"totalPages\":2},\"error\":null}";
        Type rolling = new TypeToken<RollingListResponse>() {}.getType();
        Gson gson = new Gson();
        RollingListResponse listResponse = gson.fromJson(resList, rolling);

        ReserveReleasesRollingAdapter adapter = new ReserveReleasesRollingAdapter(this, listResponse.getData().getItems());
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position, Object obj) {
                //LogUtils.v(TAG, "position clicked " + position);
                startActivity(new Intent(getApplicationContext(), ReserveDetailsActivity.class));
            }
        });

        rvRollingList.setAdapter(adapter);
        rvRollingList.setLayoutManager(new LinearLayoutManager(this));

        String resp = "{\"status\":\"SUCCESS\",\"timestamp\":\"2022-02-28T10:49:06.398+00:00\",\"data\":{\"items\":[{\"date\":\"2022-02-26 19:26:09.72\",\"referenceId\":\"qwe23423wefdsf922\",\"sentTo\":\"dba 104 Business Token Account\",\"amount\":100,\"reason\":\"manual reserve release reason\"},{\"date\":\"2022-02-26 18:26:09.72\",\"referenceId\":\"gwe23423wefdsf922\",\"sentTo\":\"dba 104 Business Token Account\",\"amount\":108,\"reason\":\"manual reserve release reason\"},{\"date\":\"2022-02-26 15:26:09.72\",\"referenceId\":\"bwe23423wefdsf922\",\"sentTo\":\"dba 104 Business Token Account\",\"amount\":200,\"reason\":\"manual reserve release reason\"},{\"date\":\"2022-02-26 10:26:09.72\",\"referenceId\":\"dwe23423wefdsf922\",\"sentTo\":\"dba 104 Business Token Account\",\"amount\":104,\"reason\":\"manual reserve release reason\"},{\"date\":\"2022-02-26 08:26:09.72\",\"referenceId\":\"awe23423wefdsf922\",\"sentTo\":\"dba 104 Business Token Account\",\"amount\":110,\"reason\":\"manual reserve release reason\"}],\"currentPageNo\":0,\"pageSize\":100,\"totalItems\":5,\"totalPages\":1},\"error\":null}";
        Type type = new TypeToken<ManualListResponse>(){}.getType();
        ManualListResponse respose = gson.fromJson(resp, type);
        LogUtils.v(TAG, "Responce" +respose);

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
                    ivFilterIcon.setVisibility(View.VISIBLE);
                    view.setVisibility(View.VISIBLE);
                    changeName.setText(R.string.rolling_releases);
                    rvRollingList.setVisibility(View.VISIBLE);
                    rvManualList.setVisibility(View.GONE);
                    searchET.setText("");
                    searchET.clearFocus();
                    dialog.dismiss();
                } else {
                    ivFilterIcon.setVisibility(View.GONE);
                    rvRollingList.setVisibility(View.GONE);
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

    private void showFiltersPopup() {

        ReserveReleasesFilterDialog showReserveReleaseDialog = new ReserveReleasesFilterDialog(ReserveReleasesActivity.this);
        showReserveReleaseDialog.setOnDialogClickListener(new OnDialogClickListener() {
            @Override
            public void onDialogClicked(String action, Object value) {
                LogUtils.d(TAG, "onclickkk" + action + value);
                if (action.equalsIgnoreCase("ApplyFilter")) {

                    ivFilterIcon.setImageDrawable(getDrawable(R.drawable.ic_filter_enabled));

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