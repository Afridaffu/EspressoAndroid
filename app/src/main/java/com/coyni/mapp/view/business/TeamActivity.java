package com.coyni.mapp.view.business;

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

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.coyni.mapp.R;
import com.coyni.mapp.adapters.TeamAdapter;
import com.coyni.mapp.interfaces.OnKeyboardVisibilityListener;
import com.coyni.mapp.model.SearchKeyRequest;
import com.coyni.mapp.model.team.TeamData;
import com.coyni.mapp.model.team.TeamListResponse;
import com.coyni.mapp.utils.Utils;
import com.coyni.mapp.view.BaseActivity;
import com.coyni.mapp.viewmodel.TeamViewModel;

import java.util.ArrayList;
import java.util.List;

public class TeamActivity extends BaseActivity implements OnKeyboardVisibilityListener, TextWatcher {

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
    private CardView cardView;
    private LinearLayout teamsCV;

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
        teamViewModel.retrieveTeamInfo();
        teamsCV.setVisibility(View.VISIBLE);
        noTeamMemberTV.setVisibility(View.GONE);
    }

    private void initFields() {
        bpBackBtn = findViewById(R.id.bpbackBtn);
        addTeamMemberL = findViewById(R.id.addTeamMemberL);
        searchET = findViewById(R.id.searchET);
        clearTextLL = findViewById(R.id.clearTextLL);
        noTeamMemberTV = findViewById(R.id.noTeamMemberTV);
        dividerView = findViewById(R.id.dividerView);
        cardView = findViewById(R.id.cardView);
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
                Intent intent = new Intent(TeamActivity.this, AddNewTeamMemberActivity.class);
                teamActivityResultLauncher.launch(intent);
            }
        });

        searchET.setFilters(new InputFilter[]{new InputFilter.LengthFilter(60)});
        teamViewModel = new ViewModelProvider(this).get(TeamViewModel.class);

        searchET.addTextChangedListener(this);
//        searchET.addTextChangedListener(new TextWatcher() {
//
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                if (charSequence.toString().length() > 0) {
//                    clearTextLL.setVisibility(View.VISIBLE);
//                } else if (charSequence.toString().length() == 0) {
//                    clearTextLL.setVisibility(View.GONE);
//                }
//
//                String search_key = charSequence.toString();
//                rvTeamList = new ArrayList<>();
//                if (originalTeamList.size() > 0) {
//                    for (int iteration = 0; iteration < originalTeamList.size(); iteration++) {
//                        String fullName = originalTeamList.get(iteration).getFirstName() + " " + originalTeamList.get(iteration).getLastName();
//                        if (fullName.toLowerCase().contains(search_key.toLowerCase())) {
//                            rvTeamList.add(originalTeamList.get(iteration));
//                        }
//                    }
//                }
//
////                teamsCV.getLayoutParams().height= ViewGroup.LayoutParams.MATCH_PARENT;
//
//                if (rvTeamList.size() > 0) {
//                    LinearLayoutManager layoutManager = new LinearLayoutManager(TeamActivity.this);
//                    teamAdapter = new TeamAdapter(TeamActivity.this, rvTeamList, memberClickListener);
//                    recyclerViewTeam.setLayoutManager(layoutManager);
//                    recyclerViewTeam.setAdapter(teamAdapter);
//                    recyclerViewTeam.setVisibility(View.VISIBLE);
//                    dividerView.setVisibility(View.VISIBLE);
//                    noTeamMemberTV.setVisibility(View.GONE);
//                    teamsCV.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
//
//                }
////                else if (originalTeamList.size() > 0) {
////                    rvTeamList = originalTeamList;
////                    LinearLayoutManager layoutManager = new LinearLayoutManager(TeamActivity.this);
////                    teamAdapter = new TeamAdapter(TeamActivity.this, rvTeamList, memberClickListener);
////                    recyclerViewTeam.setLayoutManager(layoutManager);
////                    recyclerViewTeam.setAdapter(teamAdapter);
////                    recyclerViewTeam.setVisibility(View.VISIBLE);
////                    dividerView.setVisibility(View.VISIBLE);
////                    noTeamMemberTV.setVisibility(View.GONE);
////                }
//                else {
//                    LinearLayoutManager layoutManager = new LinearLayoutManager(TeamActivity.this);
//                    teamAdapter = new TeamAdapter(TeamActivity.this, null, memberClickListener);
//                    recyclerViewTeam.setLayoutManager(layoutManager);
//                    recyclerViewTeam.setAdapter(teamAdapter);
//                    recyclerViewTeam.setVisibility(View.GONE);
//                    dividerView.setVisibility(View.VISIBLE);
//                    noTeamMemberTV.setVisibility(View.VISIBLE);
//                    if (charSequence.toString().length() > 0) {
//                        teamsCV.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
//                    } else if (charSequence.toString().length() == 0) {
//                        teamsCV.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
//                        noTeamMemberTV.setVisibility(View.GONE);
//                        dividerView.setVisibility(View.GONE);
//                    }
//
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//
//            }
//        });

        clearTextLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchET.setText("");
                searchET.clearFocus();
                // addTeamMemberL.setVisibility(View.VISIBLE);
                if (Utils.isKeyboardVisible)
                    Utils.hideKeypad(TeamActivity.this);
                noTeamMemberTV.setVisibility(View.GONE);
                teamsCV.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
            }
        });
    }

    private void setOnClickListener() {
        try {

            memberClickListener = (view, position) -> {
                Intent intent = new Intent(this, TeamMemberActivity.class);
                intent.putExtra(Utils.teamMemberId, rvTeamList.get(position).getId());
                intent.putExtra(Utils.teamStatus, rvTeamList.get(position).getStatus());
                teamActivityResultLauncher.launch(intent);
                //startActivity(intent);
            };
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void searchAPI(String search) {
        SearchKeyRequest keyRequest = new SearchKeyRequest();
        keyRequest.setSearchKey(search);
        teamViewModel.searchTeamMember(keyRequest);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if (charSequence.length() > 2) {
            searchAPI(charSequence.toString());
            clearTextLL.setVisibility(View.VISIBLE);
        } else if (charSequence.toString().trim().length() == 0) {
            clearTextLL.setVisibility(View.GONE);
            teamViewModel.retrieveTeamInfo();
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    ActivityResultLauncher<Intent> teamActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_CANCELED) {
                    showProgressDialog();
                    searchET.setText("");
                    recyclerViewTeam.setVisibility(View.GONE);
                    dividerView.setVisibility(View.GONE);
                    noTeamMemberTV.setVisibility(View.GONE);
                    teamsCV.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    //teamViewModel.retrieveTeamInfo();
                }
            });

    private void initObservers() {
        try {
            teamViewModel.getTeamRetrieveMutableLiveData().observe(this, new Observer<TeamListResponse>() {
                @Override
                public void onChanged(TeamListResponse teamResponseModel) {
                    dismissDialog();
                    if (teamResponseModel != null && teamResponseModel.getStatus() != null
                            && teamResponseModel.getStatus().equalsIgnoreCase(Utils.SUCCESS)) {
                        if (teamResponseModel.getData() != null
                                && teamResponseModel.getData().getItems() != null) {
                            rvTeamList = teamResponseModel.getData().getItems();
                        }
                        teamList(true);
                    } else {
                        Utils.displayAlert(teamResponseModel.getError().getErrorDescription(), TeamActivity.this, "", teamResponseModel.getError().getFieldErrors().get(0));
                    }
                }
            });

            teamViewModel.getSearchTeamMutableLiveData().observe(this, new Observer<TeamListResponse>() {
                @Override
                public void onChanged(TeamListResponse teamListResponse) {
                    if (teamListResponse != null && teamListResponse.getStatus() != null
                            && teamListResponse.getStatus().equalsIgnoreCase(Utils.SUCCESS)) {
                        if (teamListResponse.getData() != null
                                && teamListResponse.getData().getItems() != null) {
                            rvTeamList = teamListResponse.getData().getItems();
                        }
                        teamList(false);
                    } else {
                        Utils.displayAlert(teamListResponse.getError().getErrorDescription(), TeamActivity.this, "", teamListResponse.getError().getFieldErrors().get(0));
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

    private void teamList(boolean isShowAdd) {
        if (rvTeamList != null && rvTeamList.size() > 0) {
            LinearLayoutManager layoutManager = new LinearLayoutManager(TeamActivity.this);
            recyclerViewTeam.setLayoutManager(layoutManager);
            teamAdapter = new TeamAdapter(TeamActivity.this, rvTeamList, memberClickListener);
            recyclerViewTeam.setAdapter(teamAdapter);
            recyclerViewTeam.setVisibility(View.VISIBLE);
            dividerView.setVisibility(View.VISIBLE);
            noTeamMemberTV.setVisibility(View.GONE);
            cardView.setVisibility(View.VISIBLE);

        } else {
            recyclerViewTeam.setVisibility(View.GONE);
//            dividerView.setVisibility(View.GONE);
            noTeamMemberTV.setVisibility(isShowAdd ? View.GONE : View.VISIBLE);
            cardView.setVisibility(isShowAdd ? View.VISIBLE : View.GONE);
            teamsCV.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;

        }
        teamsCV.setVisibility(isShowAdd ? View.VISIBLE : View.GONE);
    }
}