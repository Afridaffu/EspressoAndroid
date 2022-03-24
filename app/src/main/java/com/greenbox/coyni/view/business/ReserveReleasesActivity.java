package com.greenbox.coyni.view.business;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.greenbox.coyni.R;
import com.greenbox.coyni.adapters.OnItemClickListener;
import com.greenbox.coyni.adapters.ReserveReleasesRollingAdapter;
import com.greenbox.coyni.dialogs.ManualDialog;
import com.greenbox.coyni.dialogs.OnDialogClickListener;
import com.greenbox.coyni.dialogs.ReserveReleasesFilterDialog;
import com.greenbox.coyni.utils.LogUtils;
import com.greenbox.coyni.view.BaseActivity;
import com.greenbox.coyni.view.TransactionListActivity;

public class ReserveReleasesActivity extends BaseActivity implements TextWatcher {

    private ImageView ivFilterIcon;
    private LinearLayout closeBtnIV, rollingLL;
    private RecyclerView rrTransListRV;
    private SwipeRefreshLayout refreshLayout;
    private EditText searchET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserve_releases);

        closeBtnIV = findViewById(R.id.closeBtnIV);
        searchET = findViewById(R.id.searchET);
        ivFilterIcon = findViewById(R.id.ivFilterIcon);
        rollingLL = findViewById(R.id.rollingLL);
        rrTransListRV = findViewById(R.id.rrTransListRV);
        refreshLayout = findViewById(R.id.refreshLayoutRL);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setRefreshing(false);
                ivFilterIcon.setImageDrawable(getDrawable(R.drawable.ic_filtericon));
                searchET.setText("");
                searchET.clearFocus();
            }
        });

        ReserveReleasesRollingAdapter adapter = new ReserveReleasesRollingAdapter(this);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position, Object obj) {
                //LogUtils.v(TAG, "position clicked " + position);
                startActivity(new Intent(getApplicationContext(), ReserveDetailsActivity.class));
            }
        });

        rrTransListRV.setAdapter(adapter);
        rrTransListRV.setLayoutManager(new LinearLayoutManager(this));
        closeBtnIV.setOnClickListener(view -> onBackPressed());


        ivFilterIcon.setOnClickListener(v -> showFiltersPopup());
        rollingLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ManualDialog dialog = new ManualDialog(ReserveReleasesActivity.this);
                dialog.show();

                LinearLayout llRolling = dialog.findViewById(R.id.llRolling);
                LinearLayout llManualLL = dialog.findViewById(R.id.llManual);

                llRolling.setOnClickListener(view ->
                        dialog.dismiss()
                );
                llManualLL.setOnClickListener(view ->
                        startActivity(new Intent(ReserveReleasesActivity.this, ManualReleasesActivity.class))
                );
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