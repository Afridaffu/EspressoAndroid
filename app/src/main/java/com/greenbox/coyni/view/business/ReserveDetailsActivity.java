package com.greenbox.coyni.view.business;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.greenbox.coyni.R;
import com.greenbox.coyni.adapters.ReserveDetailsListAdapter;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.view.BaseActivity;

public class ReserveDetailsActivity extends BaseActivity {

    private LinearLayout backButton, lycopy, releasedIDcopy ;
    private TextView reserveID, amount, status, releasedateTime, reserveRegular, cancel, reserveIDTV;
    private TextView releasedAMT, releasedDateTime, transID, tvDBAName;
    private RecyclerView recyclerViewRv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserve_details);
        initFields();

    }

    private void initFields() {

        backButton = findViewById(R.id.lyRDClose);
        reserveID = findViewById(R.id.tvreserveID);
        lycopy = findViewById(R.id.lycopy);
        reserveIDTV = findViewById(R.id.reserveIDTV);
        amount = findViewById(R.id.tvamount);
        status = findViewById(R.id.reserveStatusTV);
        releasedateTime = findViewById(R.id.releasedateTimeTV);
        cancel = findViewById(R.id.canceledTV);
        reserveRegular = findViewById(R.id.reserveRegularTV);

        releasedAMT = findViewById(R.id.tvreleasedAMT);
        releasedDateTime = findViewById(R.id.ReleasedDateTimeTv);
        transID = findViewById(R.id.tvtransID);
        releasedIDcopy = findViewById(R.id.releasedIDcopyLL);
        tvDBAName = findViewById(R.id.tvDBAName);


        recyclerViewRv = findViewById(R.id.recyclerviewRV);

        cancel.setText(getResources().getString(R.string.canceled_description,"12/24/2020","6435.21"));

        backButton.setOnClickListener(v -> finish());
        lycopy.setOnClickListener(v -> Utils.copyText(reserveIDTV.getText().toString(), ReserveDetailsActivity.this));
        releasedIDcopy.setOnClickListener(v -> Utils.copyText(transID.getText().toString(), ReserveDetailsActivity.this));

        ReserveDetailsListAdapter detailsListAdapter = new ReserveDetailsListAdapter(this);
        recyclerViewRv.setAdapter(detailsListAdapter);
        recyclerViewRv.setLayoutManager(new LinearLayoutManager(this));
    }
}