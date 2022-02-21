package com.greenbox.coyni.view.business;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.greenbox.coyni.R;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.view.UserDetailsActivity;
import com.greenbox.coyni.viewmodel.AccountLimitsViewModel;
import com.greenbox.coyni.viewmodel.BusinessIdentityVerificationViewModel;
import com.greenbox.coyni.viewmodel.DashboardViewModel;

public class DBAInfoDetails extends AppCompatActivity {

    private TextView editEmailTV, editPhonenumberTV, editAddressTV, businesstypeTV, companyemailTV, editProfileTV;
    private LinearLayout companyEmailLL, editEmailLL, editPhoneNumLL, editAddressLL, editCompanyemailLL, closeLL;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_dbainfo_details);

        closeLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initFields();
//            getStates();
        initObservers();
    }

    private void initObservers() {
    }

    private void initFields() {
        closeLL = findViewById(R.id.closeLL);
        companyemailTV = findViewById(R.id.companyEmailTV);
        editProfileTV = findViewById(R.id.editProfileTV);
        businesstypeTV = findViewById(R.id.BusinesstypeTV);
        editEmailTV = findViewById(R.id.editcompanyemailTV);
        editPhonenumberTV = findViewById(R.id.editPhonenumTV);
        editAddressTV = findViewById(R.id.editAddressTV);
        companyEmailLL = findViewById(R.id.editcompanyemailLL);
        editEmailLL = findViewById(R.id.editemailLL);
        editPhoneNumLL = findViewById(R.id.editphonenumLL);
        editAddressLL = findViewById(R.id.editaddressLL);



        }

    }
