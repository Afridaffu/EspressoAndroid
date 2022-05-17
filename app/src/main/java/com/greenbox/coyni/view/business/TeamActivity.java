package com.greenbox.coyni.view.business;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.greenbox.coyni.R;
import com.greenbox.coyni.adapters.TeamAdapter;
import com.greenbox.coyni.interfaces.OnKeyboardVisibilityListener;
import com.greenbox.coyni.model.team.TeamData;
import com.greenbox.coyni.model.team.TeamListResponse;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.view.BaseActivity;
import com.greenbox.coyni.viewmodel.TeamViewModel;

import java.util.ArrayList;
import java.util.List;

public class TeamActivity extends BaseActivity implements OnKeyboardVisibilityListener {

    private LinearLayout bpBackBtn, addTeamMemberL, clearTextLL;
    private TeamViewModel teamViewModel;
    private RecyclerView recyclerViewTeam;
    private TeamAdapter teamAdapter;
    private List<TeamData> originalTeamList = new ArrayList<>();
    private List<TeamData> rvTeamList = new ArrayList<>();
    private EditText searchET;
    private TeamAdapter.TeamMemberClickListener memberClickListener;
    private TextView noTeamMemberTV;
    private View dividerView;
    private CardView teamsCV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
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
        noTeamMemberTV = findViewById(R.id.noTeamMemberTV);
        dividerView = findViewById(R.id.dividerView);
        teamsCV = findViewById(R.id.teamsCV);
        setKeyboardVisibilityListener(TeamActivity.this);
        bpBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        recyclerViewTeam = findViewById(R.id.rvTeam);

        recyclerViewTeam.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (Utils.isKeyboardVisible)
                    Utils.hideKeypad(TeamActivity.this);
            }
        });
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
                rvTeamList = new ArrayList<>();
                if (originalTeamList.size() > 0) {
                    for (int iteration = 0; iteration < originalTeamList.size(); iteration++) {
                        String fullName = originalTeamList.get(iteration).getFirstName() + " " + originalTeamList.get(iteration).getLastName();
                        if (fullName.toLowerCase().contains(search_key.toLowerCase())) {
                            rvTeamList.add(originalTeamList.get(iteration));
                        }
                    }
                }

//                teamsCV.getLayoutParams().height= ViewGroup.LayoutParams.MATCH_PARENT;

                if (rvTeamList.size() > 0) {
                    LinearLayoutManager layoutManager = new LinearLayoutManager(TeamActivity.this);
                    teamAdapter = new TeamAdapter(TeamActivity.this, rvTeamList, memberClickListener);
                    recyclerViewTeam.setLayoutManager(layoutManager);
                    recyclerViewTeam.setAdapter(teamAdapter);
                    recyclerViewTeam.setVisibility(View.VISIBLE);
                    dividerView.setVisibility(View.VISIBLE);
                    noTeamMemberTV.setVisibility(View.GONE);
                    teamsCV.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;

                }
//                else if (originalTeamList.size() > 0) {
//                    rvTeamList = originalTeamList;
//                    LinearLayoutManager layoutManager = new LinearLayoutManager(TeamActivity.this);
//                    teamAdapter = new TeamAdapter(TeamActivity.this, rvTeamList, memberClickListener);
//                    recyclerViewTeam.setLayoutManager(layoutManager);
//                    recyclerViewTeam.setAdapter(teamAdapter);
//                    recyclerViewTeam.setVisibility(View.VISIBLE);
//                    dividerView.setVisibility(View.VISIBLE);
//                    noTeamMemberTV.setVisibility(View.GONE);
//                }
                else {
                    LinearLayoutManager layoutManager = new LinearLayoutManager(TeamActivity.this);
                    teamAdapter = new TeamAdapter(TeamActivity.this, null, memberClickListener);
                    recyclerViewTeam.setLayoutManager(layoutManager);
                    recyclerViewTeam.setAdapter(teamAdapter);
                    recyclerViewTeam.setVisibility(View.GONE);
                    dividerView.setVisibility(View.VISIBLE);
                    noTeamMemberTV.setVisibility(View.VISIBLE);
                    teamsCV.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;

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
                if (Utils.isKeyboardVisible)
                    Utils.hideKeypad(TeamActivity.this);
            }
        });
    }

    private void setOnClickListener() {
        try {

            memberClickListener = (view, position) -> {
                Intent intent = new Intent(this, TeamMemberActivity.class);
                intent.putExtra(Utils.teamMemberId, rvTeamList.get(position).getId());
                intent.putExtra(Utils.teamStatus, rvTeamList.get(position).getStatus());
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
        searchET.setText("");
//        if (noTeamMemberTV.getVisibility() == View.VISIBLE) {
//            noTeamMemberTV.setVisibility(View.GONE);
//        }
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
                            originalTeamList = rvTeamList = teamResponseModel.getData().getItems();
                            recyclerViewTeam.setVisibility(View.VISIBLE);
                            LinearLayoutManager layoutManager = new LinearLayoutManager(TeamActivity.this);
                            recyclerViewTeam.setLayoutManager(layoutManager);
                            teamAdapter = new TeamAdapter(TeamActivity.this, rvTeamList, memberClickListener);
                            recyclerViewTeam.setAdapter(teamAdapter);
                            recyclerViewTeam.setVisibility(View.VISIBLE);
                            dividerView.setVisibility(View.VISIBLE);
                            noTeamMemberTV.setVisibility(View.GONE);
//                            teamsCV.getLayoutParams().height= ViewGroup.LayoutParams.MATCH_PARENT;
                        } else {
                            recyclerViewTeam.setVisibility(View.GONE);
                            dividerView.setVisibility(View.GONE);
                            noTeamMemberTV.setVisibility(View.GONE);
                            teamsCV.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
                        }
                    } else {
                        Utils.displayAlert(teamResponseModel.getError().getErrorDescription(), TeamActivity.this, "", teamResponseModel.getError().getFieldErrors().get(0));
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            dismissDialog();
        }

    }

    private void setKeyboardVisibilityListener(final OnKeyboardVisibilityListener onKeyboardVisibilityListener) {
        final View parentView = ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
        parentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            private boolean alreadyOpen;
            private final int defaultKeyboardHeightDP = 100;
            private final int EstimatedKeyboardDP = defaultKeyboardHeightDP + (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ? 48 : 0);
            private final Rect rect = new Rect();

            @Override
            public void onGlobalLayout() {
                int estimatedKeyboardHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, EstimatedKeyboardDP, parentView.getResources().getDisplayMetrics());
                parentView.getWindowVisibleDisplayFrame(rect);
                int heightDiff = parentView.getRootView().getHeight() - (rect.bottom - rect.top);
                boolean isShown = heightDiff >= estimatedKeyboardHeight;

                if (isShown == alreadyOpen) {
                    Log.i("Keyboard state", "Ignoring global layout change...");
                    return;
                }
                alreadyOpen = isShown;
                onKeyboardVisibilityListener.onVisibilityChanged(isShown);
            }
        });
    }

    @Override
    public void onVisibilityChanged(boolean visible) {
        Utils.isKeyboardVisible = visible;
    }
}