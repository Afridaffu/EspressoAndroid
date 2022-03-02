package com.greenbox.coyni.view.business;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.greenbox.coyni.R;
import com.greenbox.coyni.adapters.TeamAdapter;
import com.greenbox.coyni.model.team.TeamData;
import com.greenbox.coyni.model.team.TeamRequest;
import com.greenbox.coyni.model.team.TeamResponseModel;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.view.BaseActivity;
import com.greenbox.coyni.viewmodel.TeamViewModel;

import java.util.ArrayList;
import java.util.List;

public class TeamActivity extends BaseActivity implements TeamAdapter.TeamMemberClickListener {

    private LinearLayout bpbackBtn, addTeamMemberL,clearTextLL;
    private TeamViewModel teamViewModel;
    private RecyclerView recyclerViewTeam;
    private TeamAdapter teamAdapter;
    private List<TeamData> datumList = new ArrayList<>();
    private TextView noBrandsTV;
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

    }

    private void initFields() {
        bpbackBtn = findViewById(R.id.bpbackBtn);
        addTeamMemberL = findViewById(R.id.addTeamMemberL);
        searchET = findViewById(R.id.searchET);
        clearTextLL = findViewById(R.id.clearTextLL);
        noBrandsTV = findViewById(R.id.noBrandsTV);
        bpbackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        addTeamMemberL.setVisibility(View.VISIBLE);

        addTeamMemberL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TeamActivity.this, AddNewTeamMemberActivity.class));
            }
        });
        TeamRequest request = new TeamRequest();
        searchET.setFilters(new InputFilter[]{new InputFilter.LengthFilter(60)});
        teamViewModel = new ViewModelProvider(this).get(TeamViewModel.class);
        teamViewModel.getTeamInfo(request);

        searchET.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                addTeamMemberL.setVisibility(View.GONE);
                if (charSequence.toString().length() > 0) {
                    clearTextLL.setVisibility(View.VISIBLE);
                } else {
                    clearTextLL.setVisibility(View.GONE);
                }
                String search_key = charSequence.toString();
                List<TeamData> filterList = new ArrayList<>();
                int pindex = 0, poindex = 0;
                if (datumList.size() > 0) {
                    for (int iteration = 0; iteration < datumList.size(); iteration++) {
                        pindex = datumList.get(iteration).getFirstName().toLowerCase().indexOf(search_key.toLowerCase());
                        if (pindex == 0) {
                            filterList.add(datumList.get(iteration));
                        }
                    }
                }

                if (filterList.size() > 0) {
                    noBrandsTV.setVisibility(View.GONE);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(TeamActivity.this);
                    teamAdapter = new TeamAdapter(TeamActivity.this, filterList,memberClickListener);
                    recyclerViewTeam.setLayoutManager(layoutManager);
                    recyclerViewTeam.setAdapter(teamAdapter);

                } else {
                    noBrandsTV.setVisibility(View.VISIBLE);
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
                noBrandsTV.setVisibility(View.GONE);
                addTeamMemberL.setVisibility(View.VISIBLE);
                Utils.hideKeypad(TeamActivity.this);
            }
        });
    }

    @Override
    public void onItemClick(View view, int position) {
        Context context=view.getContext();
        Intent intent=new Intent();
        switch (position){
            default:
                String firstName="",lastName="";
                if(datumList.get(position).getFirstName()!=null&&!datumList.get(position).getFirstName().equals("")){
                    firstName=datumList.get(position).getFirstName();
                }
                if(datumList.get(position).getLastName()!=null&&!datumList.get(position).getLastName().equals("")){
                    lastName=datumList.get(position).getLastName();
                }
               String memberName=firstName.substring(0)+lastName.substring(0);
                intent =  new Intent(context, TeamMember.class);
                intent.putExtra(Utils.teamFirstName,datumList.get(position).getFirstName());
                intent.putExtra(Utils.teamLastName,datumList.get(position).getLastName());
                intent.putExtra(Utils.teamImageName, memberName);
                intent.putExtra(Utils.teamRoleName,datumList.get(position).getRoleName());
                intent.putExtra(Utils.teamStatus, (Parcelable) datumList.get(position).getStatus());
                intent.putExtra(Utils.teamEmailAddress,datumList.get(position).getEmailAddress());
                intent.putExtra(Utils.teamPhoneNumber, (Parcelable) datumList.get(position).getPhoneNumber());
                intent.putExtra(Utils.teamMemberId,datumList.get(position).getId());
                context.startActivity(intent);
                break;

        }
    }
    private void initObservers() {

        try {
            teamViewModel.getTeamMutableLiveData().observe(this, new Observer<TeamResponseModel>() {
                @Override
                public void onChanged(TeamResponseModel teamResponseModel) {
                    if (teamResponseModel != null) {
                        if (teamResponseModel.getStatus().equalsIgnoreCase("SUCCESS")) {
                            if (teamResponseModel.getData().size() > 0) {
                                datumList=teamResponseModel.getData();

                                LinearLayoutManager layoutManager = new LinearLayoutManager(TeamActivity.this);
                                recyclerViewTeam.setLayoutManager(layoutManager);
                                teamAdapter = new TeamAdapter(TeamActivity.this, datumList,memberClickListener);
                                recyclerViewTeam.setAdapter(teamAdapter);

                            } else {
                            }
                        } else {
                           // Utils.displayAlert(teamResponseModel.getError().getErrorDescription(), GiftCardActivity.this, "", brandsResponse.getError().getFieldErrors().get(0));
                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        }

}