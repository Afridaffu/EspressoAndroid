package com.greenbox.coyni.view.business;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.greenbox.coyni.R;
import com.greenbox.coyni.model.DBAInfo.DBAInfoResp;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.view.UserDetailsActivity;
import com.greenbox.coyni.viewmodel.AccountLimitsViewModel;
import com.greenbox.coyni.viewmodel.BusinessIdentityVerificationViewModel;
import com.greenbox.coyni.viewmodel.DashboardViewModel;

public class DBAInfoDetails extends AppCompatActivity {

  private TextView companyemailTV, companynameTV, companybusinesstypeTV, companywebTV, companyPhonenumberTV, companyAddressTV;
  private LinearLayout companyweblLL, companyeditEmailLL, companyPhoneNumLL, companyAddressLL, closeLL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_dbainfo_details);

        initFields();
    }
    private void initFields() {
        closeLL = findViewById(R.id.closeLL);
        companyemailTV = findViewById(R.id.companyEmailTV);
        companynameTV = findViewById(R.id.companynameTV);
        companybusinesstypeTV = findViewById(R.id.companyBusinesstypeTV);
        companywebTV = findViewById(R.id.editcompanyweblTV);
        companyPhonenumberTV = findViewById(R.id.editPhonenumTV);
        companyAddressTV = findViewById(R.id.editAddressTV);
        companyweblLL = findViewById(R.id.companyweblLL);
        companyeditEmailLL = findViewById(R.id.editemailLL);
        companyPhoneNumLL = findViewById(R.id.editphonenumLL);
        companyAddressLL = findViewById(R.id.editaddressLL);

    }
}