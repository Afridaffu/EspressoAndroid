package com.greenbox.coyni.view.business;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.greenbox.coyni.R;
import com.greenbox.coyni.adapters.BusinessProfileRecyclerAdapter;
import com.greenbox.coyni.model.CompanyInfo.CompanyInfoRequest;
import com.greenbox.coyni.model.bank.BankDeleteResponseData;
import com.greenbox.coyni.model.register.PhNoWithCountryCode;
import com.greenbox.coyni.model.team.TeamDeleteModel;
import com.greenbox.coyni.model.team.TeamRequest;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.view.BaseActivity;
import com.greenbox.coyni.view.BusinessReceivePaymentActivity;
import com.greenbox.coyni.view.PaymentMethodsActivity;
import com.greenbox.coyni.view.UserDetailsActivity;
import com.greenbox.coyni.viewmodel.BusinessIdentityVerificationViewModel;
import com.greenbox.coyni.viewmodel.TeamViewModel;

public class TeamMember extends BaseActivity {
    private TextView txName, txRole, txStatus, txImageName,txEmailAddress,txPhoneNumber;
    private String firstName="",lastName="",role="",status="",emailAddress="",phoneNumber="",imageName="";
    private int teamMemberId=0,statusValue=1;
    private CardView mEditCv,mCancelCV,mRemoveCv,mResendInvitation;
    private ImageView mStatusIcon;
    private TeamViewModel teamViewModel;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_member);
        Bundle bundle=getIntent().getExtras();
        firstName= bundle.getString("TeamMemberFirstName",firstName);
        lastName=bundle.getString("TeamMemberLastName",lastName);
        role=bundle.getString("Role",role);
        status=bundle.getString("Status",status);
        emailAddress=bundle.getString("EmailAddress",emailAddress);
        phoneNumber=bundle.getString("PhoneNumber",phoneNumber);
        imageName=bundle.getString("ImageName",imageName);
        teamMemberId=bundle.getInt("RoleId",teamMemberId);

        initFields();
        initObservers();
    }

    private void initObservers() {
        teamViewModel.getTeamDelMutableLiveData().observe(this, new Observer<TeamDeleteModel>() {
            @Override
            public void onChanged(TeamDeleteModel teamDeleteModel) {
                if (teamDeleteModel.getStatus().toLowerCase().equals("success")) {
                    Utils.showCustomToast(TeamMember.this, "Removed Successfully.", R.drawable.ic_custom_tick, "");
                }
            }
        });
    }

    private void initFields() {
        txName=findViewById(R.id.name);
        txRole=findViewById(R.id.role);
        txStatus=findViewById(R.id.status);
        txImageName=findViewById(R.id.imageTextTV);
        txEmailAddress=findViewById(R.id.emailAddress);
        txPhoneNumber=findViewById(R.id.phoneNumber);
        mEditCv=findViewById(R.id.nextCV);
        mCancelCV=findViewById(R.id.cancelCv);
        mRemoveCv=findViewById(R.id.removeCv);
        mResendInvitation=findViewById(R.id.resendInvitationCV);
        mStatusIcon=findViewById(R.id.editStatusIV);
        teamViewModel = new ViewModelProvider(this).get(TeamViewModel.class);
        teamViewModel.deleteTeam(teamMemberId);
        txName.setText(firstName+""+lastName);
        txRole.setText(role);
        txStatus.setText(status);
        txImageName.setText(imageName);
        txEmailAddress.setText(emailAddress);
        txPhoneNumber.setText(phoneNumber);
        if(statusValue==1){
            mCancelCV.setVisibility(View.GONE);
            mEditCv.setVisibility(View.GONE);
            mResendInvitation.setVisibility(View.VISIBLE);
            mRemoveCv.setVisibility(View.VISIBLE);
            txStatus.setTextColor(getResources().getColor(R.color.active_green));
            mStatusIcon.setBackgroundResource(R.drawable.active_dot);
            txStatus.setBackgroundResource(R.drawable.txn_active_bg);
        }
        else if(statusValue==2){
            mCancelCV.setVisibility(View.GONE);
            mEditCv.setVisibility(View.GONE);
            mResendInvitation.setVisibility(View.GONE);
            mRemoveCv.setVisibility(View.VISIBLE);
            txStatus.setTextColor(getResources().getColor(R.color.error_red));
            mStatusIcon.setBackgroundResource(R.drawable.resend_invitation_bg);
            txStatus.setBackgroundResource(R.drawable.txn_resend_invitation_bg);
        }
        else{
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

                Intent intent=new Intent(TeamMember.this,EditTeamMember.class);
                intent.putExtra("TeamMemberFirstName",firstName);
                intent.putExtra("TeamMemberLastName",lastName);
                intent.putExtra("ImageName", imageName);
                intent.putExtra("Role",role);
                intent.putExtra("Status",status);
                intent.putExtra("EmailAddress",emailAddress);
                intent.putExtra("PhoneNumber",phoneNumber);
                intent.putExtra("TeamMemberId",teamMemberId);
                startActivity(intent);
            }
        });
        mResendInvitation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(TeamMember.this,EditTeamMember.class);
                intent.putExtra("TeamMemberFirstName",firstName);
                intent.putExtra("TeamMemberLastName",lastName);
                intent.putExtra("ImageName", imageName);
                intent.putExtra("Role",role);
                intent.putExtra("Status",status);
                intent.putExtra("EmailAddress",emailAddress);
                intent.putExtra("PhoneNumber",phoneNumber);
                intent.putExtra("TeamMemberId",teamMemberId);
                startActivity(intent);
            }
        });
        mCancelCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.showCustomToast(TeamMember.this, "Invitation Canceled.", R.drawable.ic_custom_tick, "");
                finish();
            }
        });
        mRemoveCv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    final Dialog dialog = new Dialog(TeamMember.this);
                    dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.dialog_remove_team_members);
                    dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                    DisplayMetrics mertics = getApplicationContext().getResources().getDisplayMetrics();
                    TextView yesTx=(TextView)dialog.findViewById(R.id.tvYes);
                    TextView noTx=(TextView)dialog.findViewById(R.id.tvNo);

                    yesTx.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            initObservers();
                            Utils.showCustomToast(TeamMember.this, "Removed Successfully.", R.drawable.ic_custom_tick, "");
                            finish();
                        }
                    });
                    noTx.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                         dialog.dismiss();
                        }
                    });

                    Window window = dialog.getWindow();
                    window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, (int) (mertics.heightPixels * 0.75));

                    WindowManager.LayoutParams wlp = window.getAttributes();

                    wlp.gravity = Gravity.BOTTOM;
                    wlp.flags &= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
                    window.setAttributes(wlp);

                    dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

                    dialog.setCanceledOnTouchOutside(true);
                    dialog.show();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }
}