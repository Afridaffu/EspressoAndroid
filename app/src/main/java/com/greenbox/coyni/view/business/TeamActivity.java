package com.greenbox.coyni.view.business;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.greenbox.coyni.R;
import com.greenbox.coyni.adapters.TeamAdapter;
import com.greenbox.coyni.model.team.TeamData;
import com.greenbox.coyni.model.team.TeamListResponse;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.view.BaseActivity;
import com.greenbox.coyni.viewmodel.TeamViewModel;

import java.util.ArrayList;
import java.util.List;

public class TeamActivity extends BaseActivity {

    private LinearLayout bpBackBtn, addTeamMemberL, clearTextLL;
    private TeamViewModel teamViewModel;
    private RecyclerView recyclerViewTeam;
    private TeamAdapter teamAdapter;
    private List<TeamData> datumList = new ArrayList<>();
    private EditText searchET;
    private TeamAdapter.TeamMemberClickListener memberClickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_team);
        initFields();
        initObservers();
        setOnClickListener();
    }

    private void initFields() {
        bpBackBtn = findViewById(R.id.bpbackBtn);
        addTeamMemberL = findViewById(R.id.addTeamMemberL);
        searchET = findViewById(R.id.searchET);
        clearTextLL = findViewById(R.id.clearTextLL);
        bpBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        recyclerViewTeam = findViewById(R.id.rvTeam);

        addTeamMemberL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TeamActivity.this, AddNewTeamMemberActivity.class));
            }
        });

        searchET.setFilters(new InputFilter[]{new InputFilter.LengthFilter(60)});
        teamViewModel = new ViewModelProvider(this).get(TeamViewModel.class);


        searchET.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().length() > 0) {
                    clearTextLL.setVisibility(View.VISIBLE);
                } else {
                    clearTextLL.setVisibility(View.GONE);
                }
                String search_key = charSequence.toString();
                List<TeamData> filterList = new ArrayList<>();
                if (datumList.size() > 0) {
                    for (int iteration = 0; iteration < datumList.size(); iteration++) {

                        if (datumList.get(iteration).getFirstName().toLowerCase().contains(search_key.toLowerCase()) || datumList.get(iteration).getLastName().toLowerCase().contains(search_key.toLowerCase())) {
                            filterList.add(datumList.get(iteration));
                        }
                    }
                }

                if (filterList.size() > 0) {
                    LinearLayoutManager layoutManager = new LinearLayoutManager(TeamActivity.this);
                    teamAdapter = new TeamAdapter(TeamActivity.this, filterList, memberClickListener);
                    recyclerViewTeam.setLayoutManager(layoutManager);
                    recyclerViewTeam.setAdapter(teamAdapter);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        clearTextLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchET.setText("");
                searchET.clearFocus();
                // addTeamMemberL.setVisibility(View.VISIBLE);
                Utils.hideKeypad(TeamActivity.this);
            }
        });
    }

    private void setOnClickListener() {
        try {

            memberClickListener = (view, position) -> {
                Intent intent = new Intent(this, TeamMemberActivity.class);
                intent.putExtra(Utils.teamMemberId, datumList.get(position).getId());
                intent.putExtra(Utils.teamStatus, datumList.get(position).getStatus());
                startActivity(intent);
            };
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        showProgressDialog();
        teamViewModel.retrieveTeamInfo();
    }

    private void initObservers() {
        try {
            teamViewModel.getTeamRetrieveMutableLiveData().observe(this, new Observer<TeamListResponse>() {
                @Override
                public void onChanged(TeamListResponse teamResponseModel) {
                    dismissDialog();
                    if (teamResponseModel != null && teamResponseModel.getStatus() != null
                            && teamResponseModel.getStatus().equalsIgnoreCase(Utils.SUCCESS)) {
                        if (teamResponseModel.getData() != null
                                && teamResponseModel.getData().getItems() != null
                                && teamResponseModel.getData().getItems().size() > 0) {
                            datumList = teamResponseModel.getData().getItems();
                            recyclerViewTeam.setVisibility(View.VISIBLE);
                            LinearLayoutManager layoutManager = new LinearLayoutManager(TeamActivity.this);
                            recyclerViewTeam.setLayoutManager(layoutManager);
                            teamAdapter = new TeamAdapter(TeamActivity.this, datumList, memberClickListener);
                            recyclerViewTeam.setAdapter(teamAdapter);
                        } else {
                            recyclerViewTeam.setVisibility(View.GONE);
                        }
                    } else {
                        Utils.displayAlert(teamResponseModel.getError().getErrorDescription(), TeamActivity.this, "", teamResponseModel.getError().getFieldErrors().get(0));
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}