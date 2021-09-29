package com.coyni.android.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.coyni.android.R;
import com.coyni.android.model.register.AgreementsByType;
import com.coyni.android.utils.MyApplication;
import com.coyni.android.utils.Utils;
import com.coyni.android.viewmodel.DashboardViewModel;

public class SignUpToTOSActivity extends AppCompatActivity {
    TextView tvAgreementsContent,tvLastUpdatedDate;
    MyApplication objMyApplication;
    ProgressDialog dialog;
    DashboardViewModel dashboardViewModel;
    ImageView imgBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_signup_tos);
            initialization();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void initialization() {
        try {
            objMyApplication = (MyApplication) getApplicationContext();
            dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
            tvAgreementsContent = (TextView) findViewById(R.id.tvAgreementsContent);
            tvLastUpdatedDate = (TextView) findViewById(R.id.tvLastUpdatedDate);
            imgBack = (ImageView) findViewById(R.id.imgBack);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                        WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            }
            if (Utils.checkInternet(SignUpToTOSActivity.this)) {
                dialog = new ProgressDialog(SignUpToTOSActivity.this, R.style.MyAlertDialogStyle);
                dialog.setIndeterminate(false);
                dialog.setMessage("Please wait...");
                dialog.getWindow().setGravity(Gravity.CENTER);
                dialog.show();
                dashboardViewModel.meAgreementsByType(0);

            } else {
                // Toast.makeText(context, getString(R.string.internet), Toast.LENGTH_LONG).show();
            }
            initObservables();

            imgBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(SignUpToTOSActivity.this, CreateAccountActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    private void initObservables() {
        dashboardViewModel.getUserAgreementsByTypeMutableLiveData().observe(SignUpToTOSActivity.this, new Observer<AgreementsByType>() {
            @Override
            public void onChanged(AgreementsByType user) {
                dialog.dismiss();
                if (user != null && user.getError() == null) {
                    tvAgreementsContent.setText(Html.fromHtml(user.getData().getContent()));
                    tvLastUpdatedDate.setText(Utils.OnlyDate(user.getData().getStartDate()));
                }
                else
                {
                    Utils.displayAlert(dashboardViewModel.getErrorMessage(), SignUpToTOSActivity.this);
                }
            }
        });
    }
    @Override
    public void onBackPressed()
    {
        // code here to show dialog
        super.onBackPressed();  // optional depending on your needs
        Intent i = new Intent(SignUpToTOSActivity.this, CreateAccountActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }
}
