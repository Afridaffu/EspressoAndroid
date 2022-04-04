package com.greenbox.coyni.view.business;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.greenbox.coyni.R;
import com.greenbox.coyni.dialogs.CustomConfirmationDialog;
import com.greenbox.coyni.dialogs.OnDialogClickListener;
import com.greenbox.coyni.model.DialogAttributes;
import com.greenbox.coyni.model.team.Data;
import com.greenbox.coyni.model.team.TeamData;
import com.greenbox.coyni.model.team.TeamGetDataModel;
import com.greenbox.coyni.model.team.TeamInfoAddModel;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.view.BaseActivity;
import com.greenbox.coyni.viewmodel.TeamViewModel;

public class TeamMemberActivity extends BaseActivity {
    private TextView txName, txRole, txStatus, txImageName, txEmailAddress, txPhoneNumber;
    private String firstName = "", lastName = "", role = "", status = "", emailAddress = "", phoneNumber = "", imageName = "", teamStatus = "";
    private int teamMemberId = 0;
    private CardView mEditCv, mCancelCV, mRemoveCv, mResendInvitation;
    private ImageView mStatusIcon;
    private TeamViewModel teamViewModel;
    private ProgressDialog progressDialog;
    private LinearLayout bpBackBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_member);
        Bundle bundle = getIntent().getExtras();
        teamMemberId = bundle.getInt(Utils.teamMemberId, teamMemberId);
        status = bundle.getString(Utils.teamStatus, status);

        initFields();
        initObservers();
    }

    private void initObservers() {
        try {
            teamViewModel.getTeamGetMutableLiveData().observe(TeamMemberActivity.this, new Observer<TeamGetDataModel>() {
                @Override
                public void onChanged(TeamGetDataModel teamGetDataModel) {
                    dismissDialog();
                    try {
                        if (teamGetDataModel != null) {
                            if (teamGetDataModel.getStatus().equalsIgnoreCase("SUCCESS")) {
                                TeamData data = teamGetDataModel.getData();
                                if (data.getFirstName() != null && !data.getFirstName().equals("")) {
                                    firstName = data.getFirstName();
                                }
                                if (data.getLastName() != null && !data.getLastName().equals("")) {
                                    lastName = data.getLastName();
                                }
                                txName.setText(firstName + " " + lastName);
                                char first = firstName.charAt(0);
//                                char lastname = lastName.charAt(0);
                                String imageName = String.valueOf(first);
                                txImageName.setText(imageName);
                                if (data.getRoleName() != null && !data.getRoleName().equals("")) {
                                    txRole.setText(data.getRoleName());
                                }
                                if (data.getStatus() != null && !data.getStatus().equals("")) {
                                    teamStatus = data.getStatus();
                                    if (data.getStatus().equalsIgnoreCase(Utils.canceled)) {
                                        txStatus.setText(Utils.expired);
                                    }
                                    else if(data.getStatus().equalsIgnoreCase(Utils.teammemberpending)){
                                        mResendInvitation.setVisibility(View.GONE);
                                        mCancelCV.setVisibility(View.VISIBLE);
                                        mEditCv.setVisibility(View.VISIBLE);
                                        mRemoveCv.setVisibility(View.GONE);
                                        txStatus.setTextColor(getResources().getColor(R.color.pending_color));
                                        mStatusIcon.setBackgroundResource(R.drawable.pending_dot);
                                        txStatus.setBackgroundResource(R.drawable.txn_pending_bg);
                                        txStatus.setText(data.getStatus());
                                    }
                                    else {
                                        txStatus.setText(data.getStatus());
                                    }
                                }
                                if (data.getEmailAddress() != null && !data.getEmailAddress().equals("")) {
                                    emailAddress = data.getEmailAddress();
                                    txEmailAddress.setText(data.getEmailAddress());
                                }
                                if (data.getPhoneNumber() != null && !data.getPhoneNumber().equals("")) {
                                    phoneNumber = data.getPhoneNumber();
                                    txPhoneNumber.setText("(" + phoneNumber.substring(0, 3) + ") " + phoneNumber.substring(3, 6) + "-" + phoneNumber.substring(6, 10));
                                }
                            } else {
                                Utils.displayAlert(teamGetDataModel.getError().getErrorDescription(), TeamMemberActivity.this, "", teamGetDataModel.getError().getFieldErrors().get(0));
                            }
                        } else {
                            Toast.makeText(TeamMemberActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            teamViewModel.getTeamCancelMutableLiveData().observe(TeamMemberActivity.this, new Observer<TeamInfoAddModel>() {
                @Override
                public void onChanged(TeamInfoAddModel teamInfoAddModel) {
                    dismissDialog();
                    try {
                        if (teamInfoAddModel != null) {
                            if (teamInfoAddModel.getStatus().equalsIgnoreCase("SUCCESS")) {
                                Utils.showCustomToast(TeamMemberActivity.this, getResources().getString(R.string.invitation_canceled), R.drawable.ic_custom_tick, "");
                                new Handler().postDelayed(() -> {
                                    try {
                                        finish();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                }, 2000);
                            } else {
                                Utils.displayAlert(teamInfoAddModel.getError().getErrorDescription(), TeamMemberActivity.this, "", teamInfoAddModel.getError().getFieldErrors().get(0));
                            }
                        } else {
                            Toast.makeText(TeamMemberActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            teamViewModel.getTeamDelMutableLiveData().observe(this, new Observer<TeamInfoAddModel>() {
                @Override
                public void onChanged(TeamInfoAddModel teamInfoAddModel) {
                    dismissDialog();
                    try {
                        if (teamInfoAddModel != null) {
                            if (teamInfoAddModel.getStatus().equalsIgnoreCase("SUCCESS")) {
                                Utils.showCustomToast(TeamMemberActivity.this, getResources().getString(R.string.Removed_success), R.drawable.ic_custom_tick, "");
                                new Handler().postDelayed(() -> {
                                    try {
                                        finish();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                }, 2000);
                            } else {
                                Utils.displayAlert(teamInfoAddModel.getError().getErrorDescription(), TeamMemberActivity.this, "", teamInfoAddModel.getError().getFieldErrors().get(0));
                            }
                        } else {
                            Toast.makeText(TeamMemberActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initFields() {
        txName = findViewById(R.id.name);
        txRole = findViewById(R.id.role);
        txStatus = findViewById(R.id.status);
        txImageName = findViewById(R.id.imageTextTV);
        txEmailAddress = findViewById(R.id.emailAddress);
        txPhoneNumber = findViewById(R.id.phoneNumber);
        mEditCv = findViewById(R.id.nextCV);
        mCancelCV = findViewById(R.id.cancelCv);
        mRemoveCv = findViewById(R.id.removeCv);
        mResendInvitation = findViewById(R.id.resendInvitationCV);
        mStatusIcon = findViewById(R.id.editStatusIV);
        bpBackBtn = findViewById(R.id.bpbackBtn);
        bpBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        teamViewModel = new ViewModelProvider(this).get(TeamViewModel.class);

        if (status.equalsIgnoreCase(Utils.active)) {
            mCancelCV.setVisibility(View.GONE);
            mEditCv.setVisibility(View.GONE);
            mResendInvitation.setVisibility(View.GONE);
            mRemoveCv.setVisibility(View.VISIBLE);
            txStatus.setTextColor(getResources().getColor(R.color.active_green));
            mStatusIcon.setBackgroundResource(R.drawable.active_dot);
            txStatus.setBackgroundResource(R.drawable.txn_active_bg);
        } else if (status.equalsIgnoreCase(Utils.canceled)) {
            mCancelCV.setVisibility(View.GONE);
            mEditCv.setVisibility(View.GONE);
            mResendInvitation.setVisibility(View.VISIBLE);
            mRemoveCv.setVisibility(View.VISIBLE);
            txStatus.setTextColor(getResources().getColor(R.color.error_red));
            mStatusIcon.setBackgroundResource(R.drawable.resend_invitation_bg);
            txStatus.setBackgroundResource(R.drawable.txn_resend_invitation_bg);
        } else if (status.equalsIgnoreCase(Utils.inActive)) {
            mCancelCV.setVisibility(View.GONE);
            mEditCv.setVisibility(View.GONE);
            mResendInvitation.setVisibility(View.GONE);
            mRemoveCv.setVisibility(View.VISIBLE);
            txStatus.setTextColor(getResources().getColor(R.color.xdark_gray));
            mStatusIcon.setBackgroundResource(R.drawable.inactive_bg);
            txStatus.setBackgroundResource(R.drawable.txn_in_active_bg);
        } else {
            mResendInvitation.setVisibility(View.GONE);
            mCancelCV.setVisibility(View.VISIBLE);
            mEditCv.setVisibility(View.VISIBLE);
            mRemoveCv.setVisibility(View.GONE);
            txStatus.setTextColor(getResources().getColor(R.color.pending_color));
            mStatusIcon.setBackgroundResource(R.drawable.pending_dot);
            txStatus.setBackgroundResource(R.drawable.txn_pending_bg);
        }
        mEditCv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TeamMemberActivity.this, EditTeamMember.class);
                intent.putExtra(Utils.teamFirstName, firstName);
                intent.putExtra(Utils.teamLastName, lastName);
                intent.putExtra(Utils.teamEmailAddress, emailAddress);
                intent.putExtra(Utils.teamPhoneNumber, phoneNumber);
                intent.putExtra(Utils.teamMemberId, teamMemberId);
                startActivity(intent);
            }
        });
        mResendInvitation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TeamMemberActivity.this, EditTeamMember.class);
                intent.putExtra(Utils.teamFirstName, firstName);
                intent.putExtra(Utils.teamLastName, lastName);
                intent.putExtra(Utils.teamEmailAddress, emailAddress);
                intent.putExtra(Utils.teamPhoneNumber, phoneNumber);
                intent.putExtra(Utils.teamMemberId, teamMemberId);
                startActivity(intent);
            }
        });
        mCancelCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    showProgressDialog();
                    teamViewModel.cancelTeamMember(teamMemberId);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        mRemoveCv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    showRemoveMemberDialog();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        showProgressDialog();
        teamViewModel.getTeamMember(teamMemberId);
    }


    private void showRemoveMemberDialog() {
        DialogAttributes dialogAttributes = new DialogAttributes(getResources().getString(R.string.remove_team_members), getString(R.string.account_permissions, firstName + " " + lastName), getString(R.string.yes), getString(R.string.no));
        CustomConfirmationDialog customConfirmationDialog = new CustomConfirmationDialog
                (TeamMemberActivity.this, dialogAttributes);

        customConfirmationDialog.setOnDialogClickListener(new OnDialogClickListener() {
            @Override
            public void onDialogClicked(String action, Object value) {
                if (action.equalsIgnoreCase(getString(R.string.yes))) {
                    customConfirmationDialog.dismiss();
                    showProgressDialog();
                    teamViewModel.deleteTeamMember(teamMemberId);
                }
            }
        });

        customConfirmationDialog.show();
    }
}